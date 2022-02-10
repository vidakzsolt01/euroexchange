package hu.gov.allamkincstar.exercises.euroexchange.data;

import hu.gov.allamkincstar.exercises.euroexchange.EuroXchange;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.text.ParseException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Az ECB-től letöltött file modellje<br>
 * Célja magával a letöltéssel és a file-névkezeléssel összefüggő sajátos feladatok 
 * ellátása.
 * 
 * @author vidakzs
 */
public class OnlineFile {
    
    /**
     * A letöltött file speciális kezeléséhezz tartalmazza a file nevét, és a letöltésének
     * dátumát
     */
    private RateXmlFile rateXmlFile;
    /**
     * jelző arra, hogy az ECB elérhető és letölthető (volt) az árfolym XML
     */
    private Boolean ecbAvailable;

    /**
     * A előbb meghatározza, hogy a korábban már letöltött file-ok közt van-e olyan, 
     * amely az aktuális gépi dátum szrinti órában került letöltésre, s csak akkor 
     * tölt le friss file-t, ha nincs ilyen.<br>
     * Ez utóbbi esetben meghatároz egy új file-t, és a letöltés sikerességétől 
     * függően beállítja az online elérhetőség jelzőjét.<br>
     * 
     * @throws ParseException 
     */
    public OnlineFile() throws ParseException {
        String lastFilename = downloadedFilenameInOneHour();
        if (lastFilename == null){
            establishRateXmlFile();
        } else {
            rateXmlFile = new RateXmlFile(lastFilename);
        }
        ecbAvailable = (rateXmlFile.getDownloadDate() != null);
    }
    
    /**
     * Meghatározza az új online file-t. Ennek során letölti file-t a helyi meghajtóra, 
     * példányosít egy RateXmlFile objektumot,és beállítja az "online elérhető"-jelzőt<br>
     * @throws ParseException 
     */
    private void establishRateXmlFile() throws ParseException{
        String filename = rateXmldownload();
        rateXmlFile = new RateXmlFile(filename);
        if (filename != null) {
            ecbAvailable = true;
        } else {
            ecbAvailable = false;
        }
    }
    
    /**
     * Megállapítja, hogy van-e egy órán belül letöltött korábbi XML, s ha igen, akkor 
     * visszaadja ennek a nevét. (Pontosabban: nem az egy órán belül letöltött file-t 
     * keresi, hanem azt, amelyik az aktuális gépidő szerinti órában került letööltésre)<br>
     * @return 
     */
    private String downloadedFilenameInOneHour(){
        String dateToHour = EuroXchange.SDF_4FILENAME.format(new Date()).substring(0, 11);
        File xmldir = new File(EuroXchange.PATH_RATEXML);
        FileFilter fileFilter = 
                file -> file.getName().startsWith(EuroXchange.FILENAME_PREFIX) && 
                        file.getName().contains(dateToHour) && 
                        file.getName().endsWith(".xml");

        File[] fileList = xmldir.listFiles(fileFilter);
        if (fileList != null && fileList.length > 0){
            return fileList[0].getName();
        }
        return null;
    }
    
    /**
     * Letölti az ECB oldaláról az árfolyamtáblázatot tartalmazó XML-t a helyi 
     * meghajtóra az arfolyam_ÉÉHHNN_ÓÓPPMM.xml névmintát követve.<br>
     * 
     * @return 
     */
    private String rateXmldownload(){
        URL url;
        String filename = EuroXchange.FILENAME_PREFIX + "_" + EuroXchange.SDF_4FILENAME.format(new Date()) + ".xml";
        String fullPath = EuroXchange.PATH_RATEXML + filename;
        try {
            url = new URL(EuroXchange.URL_ECB);
            certificateValidator();
            try(ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream())){
                try (FileOutputStream fileOutputStream = new FileOutputStream(fullPath)) {
                    FileChannel fileChannel = fileOutputStream.getChannel();
                    fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                }
            }
            return filename;
        } catch (MalformedURLException ex) {
            Logger.getLogger(OnlineFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(OnlineFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    /**
     * A tüzfal miatt az ECB honlapja nem elérhető a Java futtató számára, mert nem
     * talál tanúsítványt hozzá. Ezt a problémát küszöböli ki ez a metódus
     */
    private void certificateValidator() {

        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                }
            }
        };

        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
        }
    }
    
    /**
     * Megállapítja, hogy a legutolsó letöltés "elég régen" volt-e. Ehhez a letöltött 
     * file letöltési dátumát veti össze az aktuális gépidővel, s ha a különbség 1 
     * óránál nagyobb, akkor van "elég rég".<br>
     * 
     * @return 
     */
    private boolean isOldLastFile(){
        if (rateXmlFile == null || rateXmlFile.getDownloadDate() == null)
            return true;
        Date now = new Date();
        long diff = now.getTime() - rateXmlFile.getDownloadDate().getTime();
        long hours = diff / (60 * 60 * 1000);
        return (hours > 1L) ;
    }
    
    /**
     * A filename getter kicsit specializált abból a szempontból, hogy mielőtt 
     * visszaadja a file-nevet, előbb ellenőrzi, hogy még aktuális-e a mostani file, 
     * és ha nem (egy óránál öregebb), akkor előbb eltölt egy újat, s ezt adja vissza.
     * (Azért van erre szükség, mert a fő form-on induláskor a program letölt egy 
     * árfolyam XML-t, és futás küzben a felhasználó választhat az online 
     * és az archív árfolyamforrás igénybe vétele között. Az online-t választva 
     * egyébként nem töltünk le automatikusan egy új file-t, csak akkor, ha közben 
     * eltelt egy óra, tehát lehet frissíteni. Ezt itt teszi a program)<br>
     * 
     * @return
     * @throws ParseException 
     */
    public String getFilename() throws ParseException {
        if (isOldLastFile()){
            String filename = rateXmldownload();
            rateXmlFile = new RateXmlFile(filename);
        }
        if (rateXmlFile == null) return null;
        return rateXmlFile.getFilename();
    }

    public Date getLastDownload() {
        if (rateXmlFile == null) return null;
        return rateXmlFile.getDownloadDate();
    }

    public Boolean isEcbAvailable() {
        return ecbAvailable;
    }

    
}
