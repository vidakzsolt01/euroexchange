/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package hu.gov.allamkincstar.exercises.euroexchange.data;

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
public class OnlineFileTest {
    
    public OnlineFileTest() {
    }
    
    private SimpleDateFormat sdf = new SimpleDateFormat("hhMMdd_HHmmss");
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getFilename method, of class OnlineFile.
     */
    @Test
    public void testGetFilename() throws Exception {
        System.out.println("getFilename");
        OnlineFile instance = new OnlineFile();
        String currentDateFlow = sdf.format(new Date());
        if ("20220210_110201".compareTo(currentDateFlow) > 0){
            String expResult = "arfolyam_20220210_100209.xml";
            String result = instance.getFilename();
            assertEquals(expResult, result);
        } else {
            String expResult = "arfolyam_" +sdf.format(new Date()) + ".xml";
            String result = instance.getFilename();
            assertEquals(expResult, result);
        }
    }

    /**
     * Test of getLastDownload method, of class OnlineFile.
     */
    @Test
    public void testGetLastDownload() {
//        System.out.println("getLastDownload");
//        OnlineFile instance = new OnlineFile();
//        Date expResult = null;
//        Date result = instance.getLastDownload();
//        assertEquals(expResult, result);
    }

    /**
     * Test of isEcbAvailable method, of class OnlineFile.
     */
    @Test
    public void testIsEcbAvailable() {
        System.out.println("isEcbAvailable");
//        OnlineFile instance = new OnlineFile();
//        Boolean expResult = null;
//        Boolean result = instance.isEcbAvailable();
//        assertEquals(expResult, result);
    }
    
}
