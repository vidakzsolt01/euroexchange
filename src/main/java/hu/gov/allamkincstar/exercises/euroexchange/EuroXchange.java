package hu.gov.allamkincstar.exercises.euroexchange;

import hu.gov.allamkincstar.exercises.euroexchange.data.OnlineFile;
import hu.gov.allamkincstar.exercises.euroexchange.data.XchangeRate;
import hu.gov.allamkincstar.exercises.euroexchange.data.Xml;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.bind.JAXBException;

/**
 * Az ECB honlapjáról letöltött árfolyam-XML szerinti devizaátszámítást végez.<br><br>
 * A program alapértelmezetten a https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml 
 * URL mögött talált file alapján végzi a számításokat oly módon, hogy a file-t letölti 
 * a bázis alkönyvtár ./xml alkönyvtárába, s innen felolvasva dolgozza fel.<br>
 * Ha nem sikerül letölteni az előbbi URL-ről az XML-file-t, akkor megőróbál megnyitni 
 * egy alapértelmezettként a programmal szállított minta XML-t (arfolyam_20220127_080000.xml). 
 * Ha ez sem sikerül, akkor tájékoztató üzenet után várakozik, hogy a felhasználó 
 * indítsa a file-tallózást<br><br>
 * Fileletöltés:<br>
 * - a program indulásakor ellenőrzi, hogy van-e a korábban letöltött file-ok közt 
 * olyan, amelyiket az indítás órájában már letöltött egyszer. Ha van ilyen, akkor nem 
 * tölti le újabb file-t<br>
 * - minden további letöltés előtt újra vizsgálja, hogy az utolsó letöltés óta eltel-e 
 * már egy óra, s ameddig ez nem következik be, nem tölt le újabb file-t<br><br>
 * File-tallózás:<br>
 * - az induló alkönyvtár a bázis alkönyvtár ./xml alkönyvtára<br>
 * - választható filenév-szűrő az "Árfolyam XML-ek, amely illeszkedik az "arfolyam_ÉÉHHNN_ÓÓPPMM.xml" 
 * mintára. Miután azonban az "All files" nem kerülhető el a file-browserben, a kiválasztott 
 * filenevet a program ellenőrzi és nem megfelelés esetén - üzenet mellett - meg sem 
 * kísérli a file betöltését<br>
 * <br>
 * 
 * @author vidakzs
 */
public class EuroXchange extends javax.swing.JFrame {

    public static final String URL_ECB = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";

    public static final SimpleDateFormat SDF_HYPHENED = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat SDF_4FILENAME = new SimpleDateFormat("yyyyMMdd_HHmmss");
    public static final SimpleDateFormat SDF_DOTTED = new SimpleDateFormat("yyyy. MM. dd.");
    
    public static final DecimalFormat DF = new DecimalFormat("0.00");
    
    public static final String PATH_RATEXML = "./xml/";
    public static final String FILENAME_PREFIX = "arfolyam";
    public static final String FILENAME_DEFAULT = PATH_RATEXML + "arfolyam_20220127_080000.xml";
    public static final String CURRENCY_HUF = "HUF";
    public static final String VALUE_EMPTY = "-";
    
    private ButtonGroup rgSource = new ButtonGroup();
    private JFileChooser fileBrowser = null;
    
    private XchangeRate HUFRate;
    private String rateXmlPath;
    private String rateXmlFilename;
    
    private OnlineFile onlineFile;
    
    /**
     * A konstruktor betölt egy XML-t (online, vagy egy helyi "default" árfolyam-XML).Ha egyik sem sikerül, akkor hibaüzenetet küld.<br>
 A betöltéstől függően alaphelyzetbe állítja a vezérlőelemeket.<br>
     * 
     * @throws java.text.ParseException - a file-névben lévő dátum-szakasz dátummáalakításakor keletkező kivétel szivárog fel idáig
     * @throws java.io.IOException - az xml alkönyvtár ellenőrző/létrehozó metódusban keletkezett kivétel
     */
    public EuroXchange() throws ParseException, IOException {
        initComponents();
        checkXmlDirectory();
        rateXmlFilename = determineRateXmlFile();
        txtSourcefile.setText(rateXmlFilename);
        txtDeviza.setText("1");
        txtDeviza.setSize(txtDeviza.getPreferredSize());
        initRadios();
        if (rateXmlFilename == null){
            JOptionPane.showMessageDialog(null, 
                    "Az online árfolyam-XML nem tölthető le, és az alapértelmezett file (arfolyam_20220127_080000.xml) \n"
                            + "sem található, használja a file-tallózó lehetőséget!\n"
                            + "(a [...] gomb a 'File' szövegmező mellett jobbra)");
            lblEuroRateValue.setText(VALUE_EMPTY);
            lblRateDateValue.setText(VALUE_EMPTY);
            return;
        }
        try {
            loadXml();
        } catch (FileNotFoundException | ParseException | JAXBException ex) {
            Logger.getLogger(EuroXchange.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    /**
     * Meghatározza és betölti az árfolyam XML-t.
     * @return - a betöltött file neve (csak név), vagy null, ha nem sikerült (vagy nem volt mit) betölteni
     * @throws ParseException - a file-névben lévő dátum-szakasz dátummáalakításakor keletkező kivétel szivárog fel idáig
     */
    private String determineRateXmlFile() throws ParseException{
        onlineFile = new OnlineFile();
        String filename = onlineFile.getFilename();
        rateXmlPath = PATH_RATEXML;
        if (filename == null) {
            filename = findDefaultXmlFile();
        }
        return filename;
    }
    
    /**
     * Ellenőrzi, hogy létezik-e az xml-eket tartalmazó alkönyvtár, s ha nem, akkor létrehozza
     * @throws IOException 
     */
    private void checkXmlDirectory() throws IOException{
        if (!new File(PATH_RATEXML).isDirectory()){
            Path path = Paths.get(PATH_RATEXML);
            Files.createDirectory(path);
        }
    }
    
    /**
     * visszaadja a "default" file-nevet, ha létezik
     * @return 
     */
    private String findDefaultXmlFile(){
        if (new File(FILENAME_DEFAULT).isFile()) return FILENAME_DEFAULT;
        return null;
    }
    
    /**
     * beállítja a rádiógombok kezdeti megjelenítását - a file-betöltéstől függően
     */
    private void initRadios(){
        rgSource.add(rbOnline);
        rgSource.add(rbLocalFile);
        rbOnline.setSelected(onlineFile.isEcbAvailable());
        rbLocalFile.setSelected(!onlineFile.isEcbAvailable());
        radioEnabling(onlineFile.isEcbAvailable());
        lblOnlineStatus.setText((onlineFile.isEcbAvailable()) ? "(elérhető)" : "(NEM ELÉRHETŐ!)");
        radioChanged();
    }
    
    /**
     * rádiógombok engedélyezése/tiltása. Külön metódusban, mert mindig egyszerre, 
     * egyformán kell állítani mindkettőt
     * @param enabled 
     */
    private void radioEnabling(boolean enabled){
        rbOnline.setEnabled(enabled);
        rbLocalFile.setEnabled(enabled);
    }
    
    /**
     * Betölt egy XML-t. Voltaképpen példányosít egy Xml objektumot, a betöltés 
     * abban történik.<br>
     * A betöltés után feltölti a devizanemek combo-ját, és a további képernyővezérlkők
     * XML-ből származtatható értékeit.
     * 
     * @throws JAXBException
     * @throws FileNotFoundException
     * @throws ParseException 
     */
    private void loadXml() throws JAXBException, FileNotFoundException, ParseException{
        if (rateXmlFilename == null) return;
        Xml xml = new Xml(rateXmlPath, rateXmlFilename);
        if (xml.getResultCode() != Xml.RESULTCODE_OK){
            JOptionPane.showMessageDialog(this, xml.getResultMessage());
            return;
        }
        txtSourcefile.setText(rateXmlPath + rateXmlFilename);
        for (XchangeRate rate : xml.getRates()) {
            cmbDevizas.addItem(rate);
            if (CURRENCY_HUF.equals(rate.getCurrency())) {
                HUFRate = rate;
            }
        }
        if (cmbDevizas.getItemCount() > 0){
            cmbDevizas.setSelectedIndex(0);
            lblEuroRateValue.setText(String.valueOf(HUFRate.getRate()) + " Ft");
            lblRateDateValue.setText(SDF_DOTTED.format(xml.getRateDate()));
            doExchange();
        } else {
            lblEuroRateValue.setText(VALUE_EMPTY);
            lblRateDateValue.setText(VALUE_EMPTY);
            txtForint.setText("");
        }
    }
    
    /**
     * Létrehor egy file browser dialogbox-kezelőt.
     * Beállítja a kezdőkönyvtárat, és a listázható file-ok szűrőjét.
     * @return 
     */
    private JFileChooser createFileChooser(){
        JFileChooser result = new JFileChooser();
        result.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileNameExtensionFilter extFilter = new FileNameExtensionFilter("XML file-ok", "xml");
        result.addChoosableFileFilter(extFilter);
        FileFilter nameFilter = new FileFilter() {
            @Override
            public boolean accept(File f) {
                try {
                    return  (f.isDirectory() || (Files.size(f.toPath()) > 0 && f.getName().matches("arfolyam_[0-9]{8}_[0-9]{6}.xml")));
                } catch (IOException ex) {
                    return false;
                }
            }

            @Override
            public String getDescription() {
                return "Árfolyam XML-ek";
            }
        };
        result.addChoosableFileFilter(nameFilter);
        result.setCurrentDirectory(new File(PATH_RATEXML));
        return result;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnExit = new javax.swing.JButton();
        pnlXml = new javax.swing.JPanel();
        txtSourcefile = new javax.swing.JTextField();
        lblSource = new javax.swing.JLabel();
        rbOnline = new javax.swing.JRadioButton();
        rbLocalFile = new javax.swing.JRadioButton();
        lblSource1 = new javax.swing.JLabel();
        btnFilebrowse = new javax.swing.JButton();
        lblRateDate = new javax.swing.JLabel();
        lblRateDateValue = new javax.swing.JLabel();
        lblEuroRate = new javax.swing.JLabel();
        lblEuroRateValue = new javax.swing.JLabel();
        lblOnlineStatus = new javax.swing.JLabel();
        pnlXchange = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtDeviza = new javax.swing.JTextField();
        txtForint = new javax.swing.JTextField();
        cmbDevizas = new javax.swing.JComboBox<>();
        lblSource3 = new javax.swing.JLabel();
        lblDevizaRate = new javax.swing.JLabel();
        lblSource2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("euroXchange - Devizaszámítás");
        setSize(new java.awt.Dimension(361, 308));

        btnExit.setText("Kilépés");
        btnExit.setName("btnExit"); // NOI18N
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });

        pnlXml.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        pnlXml.setMaximumSize(new java.awt.Dimension(360, 173));
        pnlXml.setMinimumSize(new java.awt.Dimension(360, 173));
        pnlXml.setPreferredSize(new java.awt.Dimension(360, 173));

        txtSourcefile.setEditable(false);

        lblSource.setText("Forrás:");

        rbOnline.setText("Aktuális (online)");
        rbOnline.setAlignmentY(0.0F);
        rbOnline.setMargin(new java.awt.Insets(0, 2, 0, 2));
        rbOnline.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbOnlineActionPerformed(evt);
            }
        });

        rbLocalFile.setText("Archív (file-ból)");
        rbLocalFile.setAlignmentY(0.0F);
        rbLocalFile.setMargin(new java.awt.Insets(0, 2, 0, 2));
        rbLocalFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbLocalFileActionPerformed(evt);
            }
        });

        lblSource1.setLabelFor(txtSourcefile);
        lblSource1.setText("File:");

        btnFilebrowse.setLabel("...");
        btnFilebrowse.setName("btnExit"); // NOI18N
        btnFilebrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilebrowseActionPerformed(evt);
            }
        });

        lblRateDate.setText("Árfolyamdátum:");

        lblRateDateValue.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblRateDateValue.setText("Forrás:");

        lblEuroRate.setText("Euro-árfolyam:");

        lblEuroRateValue.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblEuroRateValue.setText("----");

        lblOnlineStatus.setText("(elérhető)");

        javax.swing.GroupLayout pnlXmlLayout = new javax.swing.GroupLayout(pnlXml);
        pnlXml.setLayout(pnlXmlLayout);
        pnlXmlLayout.setHorizontalGroup(
            pnlXmlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlXmlLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlXmlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblSource)
                    .addComponent(lblSource1))
                .addGap(23, 23, 23)
                .addGroup(pnlXmlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlXmlLayout.createSequentialGroup()
                        .addComponent(lblRateDate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblRateDateValue))
                    .addGroup(pnlXmlLayout.createSequentialGroup()
                        .addComponent(txtSourcefile, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnFilebrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblOnlineStatus)
                    .addGroup(pnlXmlLayout.createSequentialGroup()
                        .addComponent(rbOnline)
                        .addGap(18, 18, 18)
                        .addComponent(rbLocalFile))
                    .addGroup(pnlXmlLayout.createSequentialGroup()
                        .addComponent(lblEuroRate)
                        .addGap(18, 18, 18)
                        .addComponent(lblEuroRateValue)))
                .addContainerGap(59, Short.MAX_VALUE))
        );
        pnlXmlLayout.setVerticalGroup(
            pnlXmlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlXmlLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlXmlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSource)
                    .addComponent(rbOnline)
                    .addComponent(rbLocalFile))
                .addGap(2, 2, 2)
                .addComponent(lblOnlineStatus)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlXmlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSourcefile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFilebrowse)
                    .addComponent(lblSource1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlXmlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblRateDate)
                    .addComponent(lblRateDateValue))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlXmlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEuroRate)
                    .addComponent(lblEuroRateValue))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlXchange.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        pnlXchange.setMaximumSize(new java.awt.Dimension(360, 89));
        pnlXchange.setMinimumSize(new java.awt.Dimension(360, 89));
        pnlXchange.setPreferredSize(new java.awt.Dimension(360, 85));

        jLabel1.setText("Devizaérték:");

        jLabel2.setText("Devizák:");

        jLabel3.setText("Érték forintban:");

        txtDeviza.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDeviza.setMaximumSize(new java.awt.Dimension(65, 22));
        txtDeviza.setMinimumSize(new java.awt.Dimension(65, 22));
        txtDeviza.setName("txtDeviza"); // NOI18N
        txtDeviza.setPreferredSize(new java.awt.Dimension(65, 22));
        txtDeviza.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDevizaActionPerformed(evt);
            }
        });
        txtDeviza.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtDevizaKeyTyped(evt);
            }
        });

        txtForint.setEditable(false);
        txtForint.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtForint.setMaximumSize(new java.awt.Dimension(100, 22));
        txtForint.setMinimumSize(new java.awt.Dimension(100, 22));
        txtForint.setName("txtForint"); // NOI18N
        txtForint.setPreferredSize(new java.awt.Dimension(100, 22));

        cmbDevizas.setMaximumRowCount(8);
        cmbDevizas.setAlignmentX(0.0F);
        cmbDevizas.setEditor(null);
        cmbDevizas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onChangeCurrency(evt);
            }
        });

        lblSource3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblSource3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSource3.setText("Átváltás");

        lblDevizaRate.setText("---");

        javax.swing.GroupLayout pnlXchangeLayout = new javax.swing.GroupLayout(pnlXchange);
        pnlXchange.setLayout(pnlXchangeLayout);
        pnlXchangeLayout.setHorizontalGroup(
            pnlXchangeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlXchangeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlXchangeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblSource3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlXchangeLayout.createSequentialGroup()
                        .addGroup(pnlXchangeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(txtDeviza, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 61, Short.MAX_VALUE)
                        .addGroup(pnlXchangeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlXchangeLayout.createSequentialGroup()
                                .addGroup(pnlXchangeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cmbDevizas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2))
                                .addGap(44, 44, 44)
                                .addGroup(pnlXchangeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addComponent(txtForint, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(lblDevizaRate, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        pnlXchangeLayout.setVerticalGroup(
            pnlXchangeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlXchangeLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblSource3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlXchangeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(pnlXchangeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(jLabel2)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlXchangeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlXchangeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(txtDeviza, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtForint, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(cmbDevizas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblDevizaRate)
                .addGap(31, 31, 31))
        );

        lblSource2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblSource2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSource2.setText("Euro bázisú devizaátszámítás");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnExit))
                    .addComponent(lblSource2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pnlXml, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(pnlXchange, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblSource2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlXml, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlXchange, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnExit)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * A [Kilépés] gomb eseménykezelője
     * @param evt 
     */
    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        System.exit(0);
    }//GEN-LAST:event_btnExitActionPerformed

    /**
     * A filebrowser gomb eseménykezelője
     * @param evt 
     */
    private void btnFilebrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilebrowseActionPerformed
        //
        if (evt.getSource() == btnFilebrowse) {
            if (fileBrowser == null) fileBrowser = createFileChooser();
            int returnVal = fileBrowser.showOpenDialog(EuroXchange.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                if (!fileBrowser.getSelectedFile().getName().endsWith(".xml")){
                    JOptionPane.showMessageDialog(this, "Csak XML file-okat olvassuk be, a kiválasztott nem az: \n\n" + fileBrowser.getSelectedFile().getName());
                    return;
                }
                rateXmlFilename = fileBrowser.getSelectedFile().getName();
                rateXmlPath = fileBrowser.getCurrentDirectory() + File.separator;
                try {
                    loadXml();
                } catch (JAXBException | FileNotFoundException | ParseException ex) {
                    JOptionPane.showMessageDialog(this, "Nem sikerült megnyitni/betölteni a kiválasztott file-t. Hibaüzenet: " + ex.getMessage());
                }
            }
        }
    }//GEN-LAST:event_btnFilebrowseActionPerformed

    /**
     * A combobox-változás
     * @param evt 
     */
    private void onChangeCurrency(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onChangeCurrency
        doExchange();
        lblDevizaRate.setText("árfolyam: " + ((XchangeRate)cmbDevizas.getSelectedItem()).getRate());
    }//GEN-LAST:event_onChangeCurrency

    /**
     * A devizaérték szövegmező "megávltozott" eseménykezelője
     * @param evt 
     */
    private void txtDevizaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDevizaActionPerformed
        doExchange();
    }//GEN-LAST:event_txtDevizaActionPerformed

    /**
     * A File rádiógomb click eseménykezelője
     * @param evt 
     */
    private void rbLocalFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbLocalFileActionPerformed
        radioChanged();
    }//GEN-LAST:event_rbLocalFileActionPerformed

    /**
     * Az Online rádiógom click eseménykezelője
     * @param evt 
     */
    private void rbOnlineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbOnlineActionPerformed
        radioChanged();
        try {
            rateXmlFilename = onlineFile.getFilename();
            rateXmlPath = PATH_RATEXML;
            loadXml();
            txtSourcefile.setText(rateXmlFilename);
        } catch (ParseException | JAXBException | FileNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Nem sikerült az XML betölrtése!");
        }
    }//GEN-LAST:event_rbOnlineActionPerformed

    /**
     * A devizaérték szövegmező karaktergépelés  eseménykezelője
     * @param evt 
     */
    private void txtDevizaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDevizaKeyTyped
        char charTyped = evt.getKeyChar();
        if (charTyped < ' '){
            return;
        }
        if ((charTyped != '.' && (charTyped > '9' || charTyped < '0'))) {
            Toolkit.getDefaultToolkit().beep();
            evt.consume();  // ha nem szám, akkor ignorálja az eseményt
        }
    }//GEN-LAST:event_txtDevizaKeyTyped

    /**
     * A rádiógombok változására reagáló kód
     */
    private void radioChanged(){
        btnFilebrowse.setEnabled(rbLocalFile.isSelected());
    }
    
    /**
     * Az átváltás végrehajtása
     */
    private void doExchange(){
        
        if (HUFRate == null) return;
        if (txtDeviza.getText() == null) return;
        
        XchangeRate selectedRate = (XchangeRate) cmbDevizas.getSelectedItem();
        if (selectedRate == null) return;
        
        if (CURRENCY_HUF.equals(selectedRate.getCurrency())) {
            txtForint.setText("" + Float.valueOf(txtDeviza.getText()));
        } else {
            Float result = (Float.valueOf(txtDeviza.getText()) / selectedRate.getRate()) * HUFRate.getRate();
            txtForint.setText("" + DF.format(result));
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(EuroXchange.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(EuroXchange.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(EuroXchange.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EuroXchange.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new EuroXchange().setVisible(true);
                } catch (ParseException | IOException ex) {
                    System.err.println("Nem sikerült a program indítása...");
                    System.err.println("");
                    Logger.getLogger(EuroXchange.class.getName()).log(Level.SEVERE, null, ex);
                    System.exit(-1);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnFilebrowse;
    private javax.swing.JComboBox<XchangeRate> cmbDevizas;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel lblDevizaRate;
    private javax.swing.JLabel lblEuroRate;
    private javax.swing.JLabel lblEuroRateValue;
    private javax.swing.JLabel lblOnlineStatus;
    private javax.swing.JLabel lblRateDate;
    private javax.swing.JLabel lblRateDateValue;
    private javax.swing.JLabel lblSource;
    private javax.swing.JLabel lblSource1;
    private javax.swing.JLabel lblSource2;
    private javax.swing.JLabel lblSource3;
    private javax.swing.JPanel pnlXchange;
    private javax.swing.JPanel pnlXml;
    private javax.swing.JRadioButton rbLocalFile;
    private javax.swing.JRadioButton rbOnline;
    private javax.swing.JTextField txtDeviza;
    private javax.swing.JTextField txtForint;
    private javax.swing.JTextField txtSourcefile;
    // End of variables declaration//GEN-END:variables
}
