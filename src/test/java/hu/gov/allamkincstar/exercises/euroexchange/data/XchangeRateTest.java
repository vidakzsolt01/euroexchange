package hu.gov.allamkincstar.exercises.euroexchange.data;

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
public class XchangeRateTest {
    
    private static final String DEVIZA = "HUF";
    private static final Float RATE = 352.3F;
    
    public XchangeRateTest() {
    }
    
    private XchangeRate rate;
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        rate = new XchangeRate(DEVIZA, String.valueOf(RATE));
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getCurrency method, of class XchangeRate.
     */
    @Test
    public void testGetCurrency() {
        System.out.println("getCurrency");
        XchangeRate instance = rate;
        String expResult = DEVIZA;
        String result = instance.getCurrency();
        assertEquals(expResult, result);
    }

    /**
     * Test of getRate method, of class XchangeRate.
     */
    @Test
    public void testGetRate() {
        System.out.println("getRate");
        XchangeRate instance = rate;
        float expResult = RATE;
        float result = instance.getRate();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of toString method, of class XchangeRate.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        XchangeRate instance = rate;
        String expResult = DEVIZA;
        String result = instance.toString();
        assertEquals(expResult, result);
    }

}
