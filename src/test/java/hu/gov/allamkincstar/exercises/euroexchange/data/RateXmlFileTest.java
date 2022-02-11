package hu.gov.allamkincstar.exercises.euroexchange.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
public class RateXmlFileTest {
    
    private static final String DATUM = "20220211_081933";
    private static final String FILENEV = "arfolyam_" + DATUM + ".xml";
    
    public RateXmlFileTest() {
    }
    
    private RateXmlFile xmlFile;
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() throws ParseException {
        xmlFile = new RateXmlFile(FILENEV);
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getFilename method, of class RateXmlFile.
     */
    @Test
    public void testGetFilename() {
        System.out.println("getFilename");
        RateXmlFile instance = xmlFile;
        String expResult = FILENEV;
        String result = instance.getFilename();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDownloadDate method, of class RateXmlFile.
     */
    @Test
    public void testGetDownloadDate() throws ParseException {
        System.out.println("getDownloadDate");
        RateXmlFile instance = xmlFile;
        Date expResult = new SimpleDateFormat("yyyyMMdd_HHmmss").parse(DATUM);
        Date result = instance.getDownloadDate();
        assertEquals(expResult, result);
    }
    
}
