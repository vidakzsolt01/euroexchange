package hu.gov.allamkincstar.exercises.euroexchange.data;

import hu.gov.allamkincstar.exercises.euroexchange.EuroXchange;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Az árfolyam XML modellje. Feladata, hogy a konstruktor-paraméterben átvett 
 * file-t betöltse, elemezze, és feltöltse az árfolyamokat tartalmazó listát, amelyet
 * majd a form-on meg kell jeleníteni egy combobox-ban.<br>
 * Kétféle konstruktorral példányosítható:
 * - String - ez egy filenév kell, legyen, ezt file-t fogja betölteni
 * - URL - ez egy WEB-cím, ami egy árfolym XML-re mutat
 * Az osztály tagváltozói (lényegében az XML-ből feltöltött adatok) nem módosíthatóak, 
 * ezért e betöltéssel járól összetett folyamatok egy belső, lokális osztályban futnak, 
 * így az osztály konstruktorában egy lépésben intézhetők a tagváltozó-értékadások.
 * 
 * 
 * @author vidakzs
 */
public class Xml {
    
    /**
     * a betöltés eredményének kódja: 0 - sikeres
     */
    public static final int RESULTCODE_OK = 0;
    /**
     * a betöltés eredményének kódja: -1 - nem sikerült az XML-tartalmat elemezni
     */
    public static final int RESULTCODE_ERROR_PARSING = -1;
    /**
     * a betöltés eredményének kódja: -2 - nem ECB-árfolym formátumú XML-t kaptunk
     */
    public static final int RESULTCODE_ERROR_NO_RATE_XML = -2;
    
    /**
     * a betöltés eredményének szöveges tartalma
     */
    private static final String RESULT_OK = "Az XML betöltése sikeresen lezajlott";
    private static final String RESULT_ERROR_NO_RATE_XML = "A megadott file nem árfolyam-XML: ";
    private static final String RESULT_ERROR_PARSING = "Az XML elemzése/betöltése nem sikerült";

    /**
     * devizaárfolyamok listája - ez fog megjelennit a fomr-on a comboboxban
     */
    private final List<XchangeRate> rates;
    /**
     * az XML-ben az árfolyamtáblázat dátuma
     */
    private final Date rateDate;
    /**
     * a file neve, melyből feltötltöttük a táblázatot
     */
    private final String filename;
    
    /**
     * a feltöltés eredményének kódja
     */
    private final int resultCode;
    /**
     * a feltöltés eredményének szöveges értelmezése
     */
    private final String resultMessage;

    /**
     * A "file-neves" konstruktorban fut a belső osztály, s ebből ekrülnek beállításra a tagváltozók
     * @param xmlFilename
     * @throws JAXBException
     * @throws FileNotFoundException
     * @throws ParseException 
     */
    public Xml(String path, String xmlFilename) throws JAXBException, FileNotFoundException, ParseException {
        this.filename = xmlFilename;
        XmlLoader dp = new XmlLoader(path, xmlFilename);
        this.rates = dp.rateList;
        this.rateDate = dp.rateDate;
        this.resultCode = dp.resultCode;
        this.resultMessage = dp.resultMessage;
    }
    
    
    /**
     * Az "URL-es" konstruktorban fut a belső osztály, s ebből ekrülnek beállításra a tagváltozók
     * @param xmlUrl
     * @throws JAXBException
     * @throws ParseException 
     */
    public Xml(URL xmlUrl) throws JAXBException, ParseException{
        this.filename = "(online)";
        XmlLoader dp = new XmlLoader(xmlUrl);
        this.rates = dp.rateList;
        this.rateDate = dp.rateDate;
        this.resultCode = dp.resultCode;
        this.resultMessage = dp.resultMessage;
    }
    
    public Date getRateDate() {
        return rateDate;
    }

    public String getFilename() {
        return filename;
    }

    public List<XchangeRate> getRates() {
        return rates;
    }

    public int getResultCode() {
        return resultCode;
    }

    public String getResultMessage() {
        return resultMessage;
    }
    
    /**
     * A lokális, belső osztály, mely a tényleges betöltési munkálatokat végzi. 
     * Igazodvs a befoglaló osztályhoz ennek is kétféle konstruktora kell, legyen, 
     * egy "file-neves" és ag "URL-es"
     */
    private class XmlLoader{
        
        /**
         * XML tag-ek, attribútumok nevei
         */
        private static final String XMLTAG_SENDER = "gesmes:Sender";
        private static final String XMLTAG_NAME = "gesmes:name";
        private static final String XMLTAG_CUBE = "Cube";
        private static final String XMLATTRIBUTE_CURRENCY = "currency";
        private static final String XMLATTRIBUTE_RATE = "rate";
        private static final String XMLATTRIBUTE_TIME = "time";
        private static final String XMLVALUE_NAME = "European Central Bank";

        List<String> currencies = new ArrayList<>();
        Map<String, XchangeRate> rateMap = new HashMap<>();
        List<XchangeRate> rateList = new ArrayList<>();
        Date rateDate;
        int resultCode = RESULTCODE_OK;
        String resultMessage = RESULT_OK;
        String path;

        /**
         * Minden a konstruktorban intéződik
         * - az XML parszolása, s ha ez sikeres, akkor
         * - az XML-tartalom (Document) feldolgozása
         * Közben beállításra kerülnek az eredményváltozók is.
         * @param filename 
         */
        public XmlLoader(String path, String filename) {
            this.path = path; 
            Document document = parseXmlToDOM(this.path, filename);
            if (document != null && resultCode == RESULTCODE_OK){
                try {
                    processDocument(document);
                } catch (ParseException ex) {
                    setResultContext(RESULTCODE_ERROR_PARSING);
                }
            }
        }

        /**
         * Minden a konstruktorban intéződik
         * - az XML parszolása, s ha ez sikeres, akkor
         * - az XML-tartalom (Document) feldolgozása
         * Közben beállításra kerülnek az eredményváltozók is.
         * @param filename 
         */
        public XmlLoader(URL xmlUrl)  {
            Document document = parseXmlToDOM(xmlUrl);
            if (document != null && resultCode == RESULTCODE_OK){
                try {
                    processDocument(document);
                } catch (ParseException ex) {
                    setResultContext(RESULTCODE_ERROR_PARSING);
                }
            }
        }
        
        /**
         * az "eredmény -környezet" beállítása: itt egy helyen, egyszerre, együtt 
         * kerül beállításra az eredmény kódja és a hozzá illő szövege
         * @param code 
         */
        private void setResultContext(int code){
            switch (code) {
                case RESULTCODE_ERROR_PARSING:
                    resultMessage = RESULT_ERROR_PARSING;
                    break;
                case RESULTCODE_ERROR_NO_RATE_XML:
                    resultMessage = RESULT_ERROR_NO_RATE_XML + "\n\n" + path + filename;
                    break;
                default:
                    throw new AssertionError();
            }
            resultCode = code;
        }

        /**
         * Az file-ból történő XML-parszolást végző metódus. A felmerülő 
         * exception-öket helyben kezeli.
         * @param filename
         * @return 
         */
        private Document parseXmlToDOM(String path, String filename) {
            
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            File xmlFile = new File(path + filename);
            try {
                dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document document = db.parse(xmlFile);
                if (document != null) document.getDocumentElement().normalize();
                return document;
            } catch (ParserConfigurationException | SAXException | IOException ex) {
                setResultContext(RESULTCODE_ERROR_PARSING);
            }
            
            return null;
        }
        
        /**
         * Az URL alapján történő XML-parszolást végző metódus. A felmerülő
         * exception-öket helyben kezeli.
         *
         * @param filename
         * @return
         */
        private Document parseXmlToDOM(URL xmlUrl) {
            
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

            try {
                dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document document = db.parse(xmlUrl.openStream());
                if (document != null) document.getDocumentElement().normalize();
                return document;
            } catch (ParserConfigurationException | SAXException | IOException ex) {
                setResultContext(RESULTCODE_ERROR_PARSING);
            }
            
            return null;
        }
        
        /**
         * Az XML DOM-ba töltött XML-tartalom feldolgozását végzi.
         * Itt tölti fel a majdan a tagváltozókba kerülő adatokat
         * @param document
         * @throws ParseException 
         */
        private void processDocument(Document document) throws ParseException{
            if (!checkXmlContent(document)) return;
            NodeList cubeList = document.getElementsByTagName(XMLTAG_CUBE);
            List<XchangeRate> rateListTemp = new ArrayList<>();
            for (int i = 0; i < cubeList.getLength(); i++) {
                Node node = cubeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    if (node.getAttributes().getNamedItem(XMLATTRIBUTE_CURRENCY) != null) {
                        String currency = node.getAttributes().getNamedItem(XMLATTRIBUTE_CURRENCY).getTextContent();
                        String rate = node.getAttributes().getNamedItem(XMLATTRIBUTE_RATE).getTextContent();
                        currencies.add(currency);
                        XchangeRate xchangeRate = new XchangeRate(currency, rate);
                        rateMap.put(currency, xchangeRate);
                        rateListTemp.add(xchangeRate);
                    } else if (node.getAttributes().getNamedItem(XMLATTRIBUTE_TIME) != null) {
                        rateDate = EuroXchange.SDF_HYPHENED.parse(node.getAttributes().getNamedItem(XMLATTRIBUTE_TIME).getTextContent());
                    }
                }
            }
            if (!rateListTemp.isEmpty()) {
                rateList = new ArrayList<>();
                rateList.addAll(rateListTemp.stream().sorted().collect(Collectors.toList()));
            }
        }
        
        /**
         * Ellenőrzi az XML-t, hogy ECB-s árfolyam XML-e.
         * Az ellenőrzés egyetlen kitétele, hogy létezzen a 
         * 
         * Sernder/name XML-tútvonalon adat és ennek értéke "European Central Bank" legyen.
         * 
         * @param document
         * @return 
         */
        private boolean checkXmlContent(Document document){
            NodeList senderList = document.getElementsByTagName(XMLTAG_SENDER);
            if (senderList.getLength() == 0) {
                setResultContext(RESULTCODE_ERROR_NO_RATE_XML);
                return false;
            }
            NodeList nameList = ((Element) senderList.item(0)).getElementsByTagName(XMLTAG_NAME);
            if (nameList.getLength() == 0) {
                setResultContext(RESULTCODE_ERROR_NO_RATE_XML);
                return false;
            }
            String name = nameList.item(0).getTextContent();
            if (!XMLVALUE_NAME.equals(name)) {
                setResultContext(RESULTCODE_ERROR_NO_RATE_XML);
                return false;
            }
            return true;
        }
    }

}
