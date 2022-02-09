package hu.gov.allamkincstar.exercises.euroexchange.handler;

import hu.gov.allamkincstar.exercises.euroexchange.EuroXchange;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 *
 * @author vidakzs
 */
public class XchangeHelper {

    private XchangeHelper() {
    }

    
    public static String rateXmldownload(){
        URL url;
        String filename = EuroXchange.FILENAME_DEFAULT.replace(".xml", "_" + EuroXchange.SDF_4FILENAME.format(new Date()) + ".xml");
        certificateValidator();
        try {
            url = new URL(EuroXchange.URL_ECB);
            try(ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream())){
                try (FileOutputStream fileOutputStream = new FileOutputStream(filename)) {
                    FileChannel fileChannel = fileOutputStream.getChannel();
                    fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                }
            }
            return filename;
        } catch (MalformedURLException ex) {
            Logger.getLogger(XchangeHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(XchangeHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    private static void certificateValidator() {

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
}
