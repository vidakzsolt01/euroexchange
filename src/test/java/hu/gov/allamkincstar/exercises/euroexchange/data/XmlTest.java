package hu.gov.allamkincstar.exercises.euroexchange.data;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.xml.bind.JAXBException;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author vidakzs
 */
public class XmlTest {
    
    private static final String FILENEV = "arfolyam_20220127_080000.xml";
    
    public XmlTest() {
    }
    
    private Xml xml;
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() throws JAXBException, FileNotFoundException, ParseException {
        xml = new Xml("./xml/", FILENEV);
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getRateDate method, of class Xml.
     */
    @Test
    public void testGetRateDate() throws ParseException {
        System.out.println("getRateDate");
        Xml instance = xml;
        Date expResult = new SimpleDateFormat("yyyy-MM-dd").parse("2022-01-27");
        Date result = instance.getRateDate();
        assertEquals(expResult, result);
    }

    /**
     * Test of getFilename method, of class Xml.
     */
    @Test
    public void testGetFilename() {
        System.out.println("getFilename");
        Xml instance = xml;
        String expResult = FILENEV;
        String result = instance.getFilename();
        assertEquals(expResult, result);
    }

    /**
     * Test of getRates method, of class Xml.
     */
    @Test
    public void testGetRates() {
        System.out.println("getRates");
        Xml instance = xml;
        List<XchangeRate> result = instance.getRates();
        assertEquals(result.size(), 32);
    }

    /**
     * Test of getResultCode method, of class Xml.
     */
    @Test
    public void testGetResultCode() {
        System.out.println("getResultCode");
        Xml instance = xml;
        int expResult = 0;
        int result = instance.getResultCode();
        assertEquals(expResult, result);
    }

    /**
     * Test of getResultMessage method, of class Xml.
     */
    @Test
    public void testGetResultMessage() {
        System.out.println("getResultMessage");
        Xml instance = xml;
        String expResult = "Az XML betöltése sikeresen lezajlott";
        String result = instance.getResultMessage();
        assertEquals(expResult, result);
    }
    
}
