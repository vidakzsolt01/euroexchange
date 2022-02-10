package hu.gov.allamkincstar.exercises.euroexchange.data;

import hu.gov.allamkincstar.exercises.euroexchange.EuroXchange;
import java.text.ParseException;
import java.util.Date;

/**
 * Az ECB-től letöltött XML-hez kapcsolódó osztály, mely a letöltött file nevét 
 * és a letöltés dátumát regisztrálja.
 * A letöltésdátumot a file nevéből képzi az "arfolyam_ÉÉHHNN_ÓÓPPMM.xml" minta 
 * alapján<br>
 * 
 * @author vidakzs
 */
public class RateXmlFile {
    /**
     * filename - útvonalat nem tartalmaz, csak file tényleges nevét
     */
    private final String filename;
    /**
     * downloadDate - a file letöltésének dátuma; ezt a file-névből képzi
     */
    private final Date downloadDate;

    /**
     * A konstruktor kap egy file-nevet, amelyből legenerálja a file letöltésének 
     * dátumát.<br>
     * null értékű file-név is érkezhet, ez nem hiba. Ebben az esetben - nyilván - a 
     * letöltésdátum képzuése is elmarad.<br>
     * 
     * @param filename
     * @throws ParseException 
     */
    public RateXmlFile(String filename) throws ParseException {
        this.filename = filename;
        if (this.filename != null){
            this.downloadDate = extractDateFromFilename(filename);
        } else {
            this.downloadDate = null;
        }
    }
    
    /**
     * meghatározza a dátumot, mely a file nevében van
     * @param filename
     * @return
     * @throws ParseException 
     */
    private Date extractDateFromFilename(String filename) throws ParseException{
        String fileDate = filename.substring(filename.indexOf("_")+1, filename.lastIndexOf(".xml"));
        return EuroXchange.SDF_4FILENAME.parse(fileDate);
    }

    public String getFilename() {
        return filename;
    }

    public Date getDownloadDate() {
        return downloadDate;
    }
    
    
}
