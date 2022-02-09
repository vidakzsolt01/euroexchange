package hu.gov.allamkincstar.exercises.euroexchange;

import hu.gov.allamkincstar.exercises.euroexchange.data.XchangeRate;
import hu.gov.allamkincstar.exercises.euroexchange.data.Xml;
import hu.gov.allamkincstar.exercises.euroexchange.handler.XchangeHelper;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.bind.JAXBException;

/**
 *
 * @author vidakzs
 */
public class EuroXchange extends javax.swing.JFrame {

    public static final String URL_ECB = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";

    public static final SimpleDateFormat SDF_HYPHENED = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat SDF_4FILENAME = new SimpleDateFormat("yyyyMMdd_HHmmss");
    public static final SimpleDateFormat SDF_DOTTED = new SimpleDateFormat("yyyy. MM. dd.");
    
    public static final DecimalFormat DF = new DecimalFormat("0.00");
    
    public static final String FILENAME_DEFAULT = "arfolyam.xml";
    public static final String CURRENCY_HUF = "HUF";
    public static final String VALUE_EMPTY = "-";
    
    private ButtonGroup rgSource;
    private JFileChooser fileBrowser = null;
    
    private XchangeRate HUFRate;
    private String filename;
    private boolean ecbOnline = true;
    
    private String lastOnlineSourcefile;
    private Date lastDownload;
    
    /**
     * Creates new form EuroXchange
     */
    public EuroXchange() {
        initComponents();
        determineRateXmlFile();
        initRadios();
        txtSourcefile.setText(filename);
        txtDeviza.setText("1");
        txtDeviza.setSize(txtDeviza.getPreferredSize());
        if (filename == null){
            JOptionPane.showMessageDialog(null, 
                    "Az online árfolyam-XML nem tölthető le, és az alapértelmezett file (arfolyam.xml) \n"
                            + "sem található, használja a file-tallózó lehetőséget!");
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
    
    private void determineRateXmlFile(){
        filename = XchangeHelper.rateXmldownload();
        if (filename == null) {
            ecbOnline = false;
            filename = findDefaultXmlFile();
            return;
        }
        lastDownload = new Date();
        lastOnlineSourcefile = filename;
    }
    
    private String findDefaultXmlFile(){
        if (new File(FILENAME_DEFAULT).isFile()) return FILENAME_DEFAULT;
        return null;
    }
    
    private void initRadios(){
        rgSource = new ButtonGroup();
        rgSource.add(rbOnline);
        rgSource.add(rbLocalFile);
        rbOnline.setSelected(ecbOnline);
        rbLocalFile.setSelected(!ecbOnline);
        radioEnabling(ecbOnline);
        lblOnlineStatus.setText((ecbOnline) ? "(elérhető)" : "(NEM ELÉRHETŐ!)");
    }
    
    private void radioEnabling(boolean enabled){
        rbOnline.setEnabled(enabled);
        rbLocalFile.setEnabled(enabled);
    }
    
    private void loadXml() throws JAXBException, FileNotFoundException, ParseException{
        if (filename == null) return;
        Xml xml = new Xml(filename);
        if (xml.getResultCode() != Xml.RESULTCODE_OK){
            JOptionPane.showMessageDialog(this, xml.getResultMessage());
            return;
        }
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
            txtForint.setText("");
        }
    }
    
    private JFileChooser createFileChooser(){
        JFileChooser result = new JFileChooser();
        result.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileNameExtensionFilter extFilter = new FileNameExtensionFilter("XML file-ok", "xml");
        result.addChoosableFileFilter(extFilter);
        FileFilter nameFilter = new FileFilter() {
            @Override
            public boolean accept(File f) {
                try {
                    return  (f.isDirectory() || (Files.size(f.toPath()) > 0 && f.getName().startsWith("arfolyam")));
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
        result.setCurrentDirectory(new File("./"));
        return result;
    }
    
    private boolean isTurnableNextDownload(){
        Date now = new Date();
        long diff = now.getTime() - lastDownload.getTime();
        long hours = diff / (60 * 60 * 1000);
        return (hours > 1L) ;
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
        lblSource2 = new javax.swing.JLabel();
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

        lblSource2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblSource2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSource2.setText("Euro bázisú devizaátszámítás");

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
                    .addComponent(lblSource1)
                    .addGroup(pnlXmlLayout.createSequentialGroup()
                        .addComponent(lblSource)
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
                                .addComponent(lblEuroRateValue))))
                    .addComponent(lblSource2, javax.swing.GroupLayout.PREFERRED_SIZE, 343, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(7, Short.MAX_VALUE))
        );
        pnlXmlLayout.setVerticalGroup(
            pnlXmlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlXmlLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblSource2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlXmlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSource)
                    .addComponent(rbOnline)
                    .addComponent(rbLocalFile))
                .addGap(2, 2, 2)
                .addComponent(lblOnlineStatus)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 22, Short.MAX_VALUE)
                .addGroup(pnlXmlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblSource1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlXmlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtSourcefile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnFilebrowse)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlXmlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblRateDate)
                    .addComponent(lblRateDateValue))
                .addGap(9, 9, 9)
                .addGroup(pnlXmlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEuroRate)
                    .addComponent(lblEuroRateValue))
                .addContainerGap())
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

        txtForint.setEditable(false);
        txtForint.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtForint.setMaximumSize(new java.awt.Dimension(100, 22));
        txtForint.setMinimumSize(new java.awt.Dimension(100, 22));
        txtForint.setName("txtForint"); // NOI18N
        txtForint.setPreferredSize(new java.awt.Dimension(100, 22));

        cmbDevizas.setMaximumRowCount(8);
        cmbDevizas.setEditor(null);
        cmbDevizas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onChangeCurrency(evt);
            }
        });

        lblSource3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblSource3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSource3.setText("Átváltás");

        javax.swing.GroupLayout pnlXchangeLayout = new javax.swing.GroupLayout(pnlXchange);
        pnlXchange.setLayout(pnlXchangeLayout);
        pnlXchangeLayout.setHorizontalGroup(
            pnlXchangeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlXchangeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlXchangeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblSource3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlXchangeLayout.createSequentialGroup()
                        .addGroup(pnlXchangeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(txtDeviza, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(39, 39, 39)
                        .addGroup(pnlXchangeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addGroup(pnlXchangeLayout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addComponent(cmbDevizas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 65, Short.MAX_VALUE)
                        .addGroup(pnlXchangeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(txtForint, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        pnlXchangeLayout.setVerticalGroup(
            pnlXchangeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlXchangeLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblSource3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlXchangeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlXchangeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(jLabel2))
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlXchangeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlXchangeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtDeviza, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cmbDevizas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtForint, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(69, 69, 69))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlXchange, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlXml, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnExit)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlXml, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlXchange, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnExit)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        System.exit(0);
    }//GEN-LAST:event_btnExitActionPerformed

    private void btnFilebrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilebrowseActionPerformed
        //
        if (evt.getSource() == btnFilebrowse) {
            if (fileBrowser == null) fileBrowser = createFileChooser();
            int returnVal = fileBrowser.showOpenDialog(EuroXchange.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                filename = fileBrowser.getSelectedFile().getName();
                txtSourcefile.setText(filename);
                try {
                    loadXml();
                } catch (JAXBException | FileNotFoundException | ParseException ex) {
                    JOptionPane.showMessageDialog(this, "Nem sikerült megnyitni/betölteni a kiválasztott file-t. Hibaüzenet: " + ex.getMessage());
                }
            }
        }
    }//GEN-LAST:event_btnFilebrowseActionPerformed

    private void onChangeCurrency(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onChangeCurrency
        doExchange();
    }//GEN-LAST:event_onChangeCurrency

    private void txtDevizaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDevizaActionPerformed
        //JOptionPane.showMessageDialog(this, evt.getActionCommand());
        //JOptionPane.showMessageDialog(this, txtDeviza.getText());
        doExchange();
    }//GEN-LAST:event_txtDevizaActionPerformed

    private void rbLocalFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbLocalFileActionPerformed
        radioChanged();
    }//GEN-LAST:event_rbLocalFileActionPerformed

    private void rbOnlineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbOnlineActionPerformed
        radioChanged();
        if (isTurnableNextDownload()){
            determineRateXmlFile();
        }
    }//GEN-LAST:event_rbOnlineActionPerformed

    private void radioChanged(){
        btnFilebrowse.setEnabled(rbLocalFile.isSelected());
    }
    
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
                    new EuroXchange().setVisible(true);
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
