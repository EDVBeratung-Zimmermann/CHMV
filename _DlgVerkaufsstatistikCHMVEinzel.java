/*
 *
 * Das JAVA-Programm Miles-Verlag Verlagsverwaltung stellt alle notwendigen
 * Funktionen für die Verwaltung des Carola Hartman Miles-Verlags bereit.
 *
 * Copyright (C) 2017 EDV-Beratung und Betrieb, Entwicklung von SOftware
 *                    Dipl.Inform Thomas Zimmermann
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package milesVerlagMain;

import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.*;
import javax.swing.GroupLayout;
import com.toedter.calendar.*;
import net.miginfocom.swing.*;

/**
 *
 * @author Thomas Zimmermann
 */
public class _DlgVerkaufsstatistikCHMVEinzel extends javax.swing.JDialog {

    /**
     * Creates new form _DlgVerkaufsstatistikCHMV
     *
     * @param parent
     * @param modal
     */
    public _DlgVerkaufsstatistikCHMVEinzel(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        Ausgabeformat.add(PDF);
        Ausgabeformat.add(XLS);
        Ausgabeformat.add(DOC);

        conn = null;

        try { // Datenbank-Treiber laden
            Class.forName(Modulhelferlein.dbDriver);
        } catch (ClassNotFoundException exept) {
            Modulhelferlein.Fehlermeldung("DB-Bestellung", "Treiber nicht gefunden: ", exept.getMessage());
            System.exit(1);
        } // Datenbank-Treiber laden

        try { // Verbindung zur Datenbank über die JDBC-Brücke
            conn = DriverManager.getConnection(Modulhelferlein.dbUrl, Modulhelferlein.dbUser, Modulhelferlein.dbPassword);
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("DB-Bestellung", "Verbindung nicht moeglich: ", exept.getMessage());
            System.exit(1);
        } // try Verbindung zur Datenbank über die JDBC-Brücke

        if (conn != null) {

            SQLAnfrageB = null; // Anfrage erzeugen für result => Bestellungen
            SQLAnfrageBD = null; // Anfrage erzeugen für resultB => Bestellungen Details
            SQLAnfrageK = null; // Anfrage erzeugen für resultK => Aufbau Kundenliste
            SQLAnfrageBNr = null; // Anfrage erzeugen für resultBNr => Bestellnummer
            SQLAnfrageBuch = null; // Anfrage erzeugen für resultBuch => Aufbau Bücherliste

            try { // SQL-Anfragen an die Datenbank
                SQLAnfrageB = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                SQLAnfrageBD = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                SQLAnfrageK = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                SQLAnfrageBNr = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                SQLAnfrageBuch = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

                eintrag = "";
                resultBuch = SQLAnfrageBuch.executeQuery("SELECT * FROM TBL_BUCH ORDER BY BUCH_ISBN");
                while (resultBuch.next()) {
                    eintrag = "";
                    resultK = SQLAnfrageK.executeQuery("SELECT * FROM TBL_ADRESSE "
                            + " WHERE ADRESSEN_ID = '"
                            + resultBuch.getString("BUCH_AUTOR") + "'");
                    resultK.next();
                    int i = resultBuch.getInt("BUCH_ID");
                    if (i < 10) {
                        eintrag = "00" + Integer.toString(i);
                    } else {
                        if (i < 100) {
                            eintrag = "0" + Integer.toString(i);
                        } else {
                            eintrag = "" + Integer.toString(i);
                        }
                    }
                    eintrag = eintrag + ", "
                            + resultBuch.getString("BUCH_ISBN") + ", "
                            + resultK.getString("ADRESSEN_Name") + ", ";
                    switch (resultBuch.getInt("BUCH_HC")) {
                        case 0:
                            eintrag = eintrag + "PB, " + resultBuch.getString("BUCH_TITEL");
                            break;
                        case 1:
                            eintrag = eintrag + "HC, " + resultBuch.getString("BUCH_TITEL");
                            break;
                        case 2:
                            eintrag = eintrag + "eB, " + resultBuch.getString("BUCH_TITEL");
                            break;
                    }
                    jComboBoxBuch.addItem(eintrag);
                } // while
            } catch (SQLException ex) {
                Modulhelferlein.Fehlermeldung("Details Verkaufsstatistik", "SQL-Exception", ex.getMessage());
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        panel1 = new JPanel();
        jLabel1 = new JLabel();
        jLabel3 = new JLabel();
        jLabel4 = new JLabel();
        jLabel6 = new JLabel();
        field_von = new JDateChooser();
        field_bis = new JDateChooser();
        PDF = new JRadioButton();
        XLS = new JRadioButton();
        DOC = new JRadioButton();
        jLabel2 = new JLabel();
        jComboBoxBuch = new JComboBox<>();
        Drucken = new JButton();
        Schliessen = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Carola Hartmann Miles Verlag");
        setResizable(false);
        Container contentPane = getContentPane();

        //======== panel1 ========
        {
            panel1.setLayout(new MigLayout(
                "insets 0,hidemode 3,gap 5 5",
                // columns
                "[fill]" +
                "[fill]" +
                "[fill]" +
                "[fill]" +
                "[fill]" +
                "[fill]" +
                "[fill]",
                // rows
                "[fill]" +
                "[fill]" +
                "[fill]" +
                "[fill]" +
                "[fill]" +
                "[fill]" +
                "[fill]" +
                "[fill]" +
                "[]"));

            //---- jLabel1 ----
            jLabel1.setFont(new Font("Tahoma", Font.BOLD, 12));
            jLabel1.setText("Verkaufsstatistik ");
            panel1.add(jLabel1, "cell 0 0 2 1");

            //---- jLabel3 ----
            jLabel3.setText("von");
            panel1.add(jLabel3, "cell 0 2");

            //---- jLabel4 ----
            jLabel4.setText("bis");
            panel1.add(jLabel4, "cell 2 2");

            //---- jLabel6 ----
            jLabel6.setText("Ausgabeformat");
            jLabel6.setFont(jLabel6.getFont().deriveFont(jLabel6.getFont().getStyle() | Font.BOLD));
            panel1.add(jLabel6, "cell 5 2 2 1");
            panel1.add(field_von, "cell 0 3 2 1");
            panel1.add(field_bis, "cell 2 3");

            //---- PDF ----
            PDF.setText("PDF");
            panel1.add(PDF, "cell 5 3");

            //---- XLS ----
            XLS.setSelected(true);
            XLS.setText("XLS");
            panel1.add(XLS, "cell 5 4");

            //---- DOC ----
            DOC.setText("DOC");
            panel1.add(DOC, "cell 5 5");

            //---- jLabel2 ----
            jLabel2.setText("Buch");
            panel1.add(jLabel2, "cell 0 6");

            //---- jComboBoxBuch ----
            jComboBoxBuch.setModel(new DefaultComboBoxModel<>(new String[] {

            }));
            panel1.add(jComboBoxBuch, "cell 1 6 6 1");

            //---- Drucken ----
            Drucken.setText("Drucken");
            Drucken.addActionListener(e -> DruckenActionPerformed(e));
            panel1.add(Drucken, "cell 4 8 2 1");

            //---- Schliessen ----
            Schliessen.setText("Abbrechen");
            Schliessen.addActionListener(e -> SchliessenActionPerformed(e));
            panel1.add(Schliessen, "cell 6 8");
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(panel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(12, Short.MAX_VALUE))
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(panel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        setSize(415, 275);
        setLocationRelativeTo(getOwner());
    }// </editor-fold>//GEN-END:initComponents

    private void DruckenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DruckenActionPerformed
        // TODO add your handling code here:
        java.util.Date vonDate = null;
        vonDate = field_von.getDate();
        java.util.Date bisDate = null;
        bisDate = field_bis.getDate();
        String strVon = "1970-01-01";
        String strBis = "1970-01-01";
        if (vonDate != null) {
            strVon = Modulhelferlein.printDateFormat("yyyy-MM-dd", vonDate);
        }
        if (bisDate != null) {
            strBis = Modulhelferlein.printDateFormat("yyyy-MM-dd", bisDate);
        }

        String buch[] = jComboBoxBuch.getItemAt(jComboBoxBuch.getSelectedIndex()).split(",");
        String buchISBN = buch[1];

        System.out.println("Bericht für " + buchISBN + " von " + strVon + " bis " + strBis);

        if (PDF.isSelected()) {
            Modulhelferlein.Infomeldung("PDF-Ausgabe ist noch nicht implementiert");
        } else if (DOC.isSelected()) {
            Modulhelferlein.Infomeldung("DOC-Ausgabe ist noch nicht implementiert");
        } else {
            berVerkaufEinzel.bericht(buchISBN, "XLS", strVon, strBis);
        }

        this.dispose();
    }//GEN-LAST:event_DruckenActionPerformed

    private void SchliessenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SchliessenActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_SchliessenActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        /**
         * try { for (javax.swing.UIManager.LookAndFeelInfo info :
         * javax.swing.UIManager.getInstalledLookAndFeels()) { if
         * ("Nimbus".equals(info.getName())) {
         * javax.swing.UIManager.setLookAndFeel(info.getClassName()); break; } }
         * } catch (ClassNotFoundException | InstantiationException |
         * IllegalAccessException | javax.swing.UnsupportedLookAndFeelException
         * ex) {
         * java.util.logging.Logger.getLogger(CarolaHartmannMilesVerlag.class.getName()).log(java.util.logging.Level.SEVERE,
         * null, ex); }
         */
        //</editor-fold>

        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(() -> {
            _DlgVerkaufsstatistikCHMVEinzel dialog = new _DlgVerkaufsstatistikCHMVEinzel(new javax.swing.JFrame(), true);
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    dialog.dispose();
                }
            });
            dialog.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JPanel panel1;
    private JLabel jLabel1;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JLabel jLabel6;
    private JDateChooser field_von;
    private JDateChooser field_bis;
    private JRadioButton PDF;
    private JRadioButton XLS;
    private JRadioButton DOC;
    private JLabel jLabel2;
    private JComboBox<String> jComboBoxBuch;
    private JButton Drucken;
    private JButton Schliessen;
    // End of variables declaration//GEN-END:variables
    private Connection conn;
    private Statement SQLAnfrageB;
    private Statement SQLAnfrageBD;
    private Statement SQLAnfrageK;
    private Statement SQLAnfrageBNr;
    private Statement SQLAnfrageBuch;
    private ResultSet resultB;
    private ResultSet resultBD;
    private ResultSet resultK;
    private ResultSet resultBNr;
    private ResultSet resultBuch;
    private String eintrag;

    private ButtonGroup Ausgabeformat = new ButtonGroup();
}
