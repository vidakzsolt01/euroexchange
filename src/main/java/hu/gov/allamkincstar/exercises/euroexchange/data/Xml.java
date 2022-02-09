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
 * 
 * @author vidakzs
 */
public class Xml {
    
    public static final int RESULTCODE_OK = 0;
    public static final int RESULTCODE_ERROR_PARSING = -1;
    public static final int RESULTCODE_ERROR_NO_RATE_XML = -2;
    
    public static final String RESULT_OK = "Az XML betöltése sikeresen lezajlott";
    public static final String RESULT_ERROR_NO_RATE_XML = "A megadott file nem árfolyam-XML.";
    public static final String RESULT_ERROR_PARSING = "Az XML elemzése/betöltése nem sikerült";

    private static final String XMLTAG_SENDER = "gesmes:Sender";
    private static final String XMLTAG_NAME = "gesmes:name";
    private static final String XMLTAG_CUBE = "Cube";
    private static final String XMLATTRIBUTE_CURRENCY = "currency";
    private static final String XMLATTRIBUTE_RATE = "rate";
    private static final String XMLATTRIBUTE_TIME = "time";
    private static final String XMLVALUE_NAME = "European Central Bank";
    
    private final List<String> currencies;
    private final Map<String, XchangeRate> rateMap;
    private final List<XchangeRate> rates;
    private final Date rateDate;
    private final String filename;
    private int resultCode = RESULTCODE_OK;
    private String resultMessage = RESULT_OK;

    public Xml(String xmlFilename) throws JAXBException, FileNotFoundException, ParseException {
        this.filename = xmlFilename;
        XmlLoader dp = new XmlLoader(xmlFilename);
        this.rateMap = dp.rateMap;
        this.rates = dp.rateList;
        this.rateDate = dp.rateDate;
        this.currencies = dp.currencies;
    }
    
    
    public Xml(URL xmlUrl) throws JAXBException, ParseException{
        this.filename = "(online)";
        XmlLoader dp = new XmlLoader(xmlUrl);
        this.rateMap = dp.rateMap;
        this.rates = dp.rateList;
        this.rateDate = dp.rateDate;
        this.currencies = dp.currencies;
    }
    
    public List<String> getCurrencies() {
        return currencies;
    }

    public Date getRateDate() {
        return rateDate;
    }

    public String getFilename() {
        return filename;
    }

    public Map<String, XchangeRate> getRateMap() {
        return rateMap;
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
    
    
    private class XmlLoader{
        List<String> currencies = new ArrayList<>();
        Map<String, XchangeRate> rateMap = new HashMap<>();
        List<XchangeRate> rateList = new ArrayList<>();
        Date rateDate;

        public XmlLoader(String filename) {
            Document document = parseXmlToDOM(filename);
            if (document != null && resultCode == RESULTCODE_OK){
                try {
                    processDocument(document);
                } catch (ParseException ex) {
                    setResultContext(RESULTCODE_ERROR_PARSING);
                }
            }
        }

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
        
        private void setResultContext(int code){
            switch (code) {
                case RESULTCODE_ERROR_PARSING:
                    resultMessage = RESULT_ERROR_PARSING;
                    break;
                case RESULTCODE_ERROR_NO_RATE_XML:
                    resultMessage = RESULT_ERROR_NO_RATE_XML;
                    break;
                default:
                    throw new AssertionError();
            }
            resultCode = code;
        }

        private Document parseXmlToDOM(String filename) {
            
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

            try {
                dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document document = db.parse(new File(filename));
                if (document != null) document.getDocumentElement().normalize();
                return document;
            } catch (ParserConfigurationException | SAXException | IOException ex) {
                setResultContext(RESULTCODE_ERROR_PARSING);
            }
            
            return null;
        }
        
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
