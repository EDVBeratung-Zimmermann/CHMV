/*
 *
 * Das JAVA-Programm Miles-Verlag Verlagsverwaltung stellt alle notwendigen
 * Funktionen f�r die Verwaltung des Carola Hartman Miles-Verlags bereit.
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
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.JFileChooser;
import net.miginfocom.swing.*;

/**
 *
 * @author Thomas Zimmermann
 */
public class VerwaltenDatenbankKonfigurationSicherung extends javax.swing.JDialog {

    /**
     * Creates new form VerwaltenDatenbankKonfigurationPfade
     *
     * @param parent
     * @param modal
     */
    public VerwaltenDatenbankKonfigurationSicherung(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.chooser = new JFileChooser(new File(System.getProperty("user.dir")));
        this.chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        initComponents();

        conn = null;

        // Datenbank-Treiber laden
        try {
            Class.forName(Modulhelferlein.dbDriver);
        } catch (ClassNotFoundException exept) {
            Modulhelferlein.Fehlermeldung("Treiber nicht gefunden.");
        }

        // Verbindung zur Datenbank �ber die JDBC-Br�cke
        try {
            conn = DriverManager.getConnection(Modulhelferlein.dbUrl, Modulhelferlein.dbUser, Modulhelferlein.dbPassword);
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("Verbindung zur Datenbank nicht moeglich.");
        }

        // final Connection conn2=conn;
        if (conn != null) {
            SQLAnfrage = null; // Anfrage erzeugen

            try {
                SQLAnfrage = conn.createStatement(
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE); // Anfrage der DB conn2 zuordnen
                result = SQLAnfrage.executeQuery(
                        "SELECT * FROM tbl_konfiguration"); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 

                // gehe zum ersten Datensatz - wenn nicht leer
                if (result.next()) {
                    result.next();
                    result.next();
                    result.next();
                    result.next();
                    resultIsEmpty = false;

                    field_ID.setText(Integer.toString(result.getInt("Konfiguration_ID")));
                    field_Stammdaten.setText(result.getString("Konfiguration_Stammdaten"));
                    field_Einnahmen.setText(result.getString("Konfiguration_Einnahmen"));
                    field_Ausgaben.setText(result.getString("Konfiguration_Ausgaben"));
                    field_Umsaetze.setText(result.getString("Konfiguration_Umsaetze"));
                    field_Rechnungen.setText(result.getString("Konfiguration_Rechnungen"));
                    field_Mahnungen.setText(result.getString("Konfiguration_Sicherung"));
                    field_Sicherung.setText(result.getString("Konfiguration_Mahnungen"));
                    field_Termine.setText(result.getString("Konfiguration_Termine"));
                    field_Schriftverkehr.setText(result.getString("Konfiguration_Schriftverkehr"));
                    field_Steuer.setText(result.getString("Konfiguration_Steuer"));
                } // if notempty

            } catch (SQLException exept) {
                Modulhelferlein.Fehlermeldung(
                        "SQL-Exception: SQL-Anfrage nicht moeglich. "
                        + exept.getMessage());
                System.exit(1);
            }  // try
        } // if

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
        jLabel5 = new JLabel();
        field_Einnahmen = new JTextField();
        field_Ausgaben = new JTextField();
        field_Umsaetze = new JTextField();
        field_Sicherung = new JTextField();
        field_Mahnungen = new JTextField();
        field_Rechnungen = new JTextField();
        field_Termine = new JTextField();
        field_Schriftverkehr = new JTextField();
        field_Steuer = new JTextField();
        jLabel6 = new JLabel();
        jLabel7 = new JLabel();
        jLabel8 = new JLabel();
        jLabel9 = new JLabel();
        jLabel10 = new JLabel();
        jLabel11 = new JLabel();
        field_ID = new JTextField();
        jLabel2 = new JLabel();
        field_Stammdaten = new JTextField();
        btnStammdaten = new JButton();
        btnEinnahmen = new JButton();
        btnAusgaben = new JButton();
        btnUmsaetze = new JButton();
        btnSicherungen = new JButton();
        btnMahnungen = new JButton();
        btnRechnungen = new JButton();
        btnTermine = new JButton();
        btnSchriftverkehr = new JButton();
        btnSteuer = new JButton();
        jButtonUpdate = new JButton();
        jButtonSchliessen = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Carola Hartmann Miles Verlag");
        setResizable(false);
        setMinimumSize(new Dimension(425, 430));
        var contentPane = getContentPane();
        contentPane.setLayout(null);

        //======== panel1 ========
        {
            panel1.setLayout(null);

            //---- jLabel1 ----
            jLabel1.setFont(new Font("Tahoma", Font.BOLD, 12));
            jLabel1.setText("Verwalten der Konfiguration - Sicherung");
            panel1.add(jLabel1);
            jLabel1.setBounds(0, 0, 329, 20);

            //---- jLabel3 ----
            jLabel3.setText("Ziel");
            panel1.add(jLabel3);
            jLabel3.setBounds(0, 78, 30, 23);

            //---- jLabel4 ----
            jLabel4.setText("-");
            panel1.add(jLabel4);
            jLabel4.setBounds(0, 106, 30, 23);

            //---- jLabel5 ----
            jLabel5.setText("-");
            panel1.add(jLabel5);
            jLabel5.setBounds(0, 134, 30, 23);
            panel1.add(field_Einnahmen);
            field_Einnahmen.setBounds(35, 78, 294, 23);
            panel1.add(field_Ausgaben);
            field_Ausgaben.setBounds(35, 106, 294, 23);
            panel1.add(field_Umsaetze);
            field_Umsaetze.setBounds(35, 134, 294, 23);
            panel1.add(field_Sicherung);
            field_Sicherung.setBounds(35, 162, 294, 23);
            panel1.add(field_Mahnungen);
            field_Mahnungen.setBounds(35, 190, 294, 23);
            panel1.add(field_Rechnungen);
            field_Rechnungen.setBounds(35, 218, 294, 23);
            panel1.add(field_Termine);
            field_Termine.setBounds(35, 246, 294, 23);
            panel1.add(field_Schriftverkehr);
            field_Schriftverkehr.setBounds(35, 274, 294, 23);
            panel1.add(field_Steuer);
            field_Steuer.setBounds(35, 302, 294, 23);

            //---- jLabel6 ----
            jLabel6.setText("-");
            panel1.add(jLabel6);
            jLabel6.setBounds(0, 162, 30, 23);

            //---- jLabel7 ----
            jLabel7.setText("-");
            panel1.add(jLabel7);
            jLabel7.setBounds(0, 190, 30, 23);

            //---- jLabel8 ----
            jLabel8.setText("-");
            panel1.add(jLabel8);
            jLabel8.setBounds(0, 218, 30, 23);

            //---- jLabel9 ----
            jLabel9.setText("-");
            panel1.add(jLabel9);
            jLabel9.setBounds(0, 246, 30, 23);

            //---- jLabel10 ----
            jLabel10.setText("-");
            panel1.add(jLabel10);
            jLabel10.setBounds(0, 274, 30, 23);

            //---- jLabel11 ----
            jLabel11.setText("-");
            panel1.add(jLabel11);
            jLabel11.setBounds(0, 302, 30, 23);

            //---- field_ID ----
            field_ID.setEditable(false);
            field_ID.setEnabled(false);
            field_ID.setFocusable(false);
            panel1.add(field_ID);
            field_ID.setBounds(new Rectangle(new Point(334, 0), field_ID.getPreferredSize()));

            //---- jLabel2 ----
            jLabel2.setText("Quelle");
            panel1.add(jLabel2);
            jLabel2.setBounds(new Rectangle(new Point(0, 54), jLabel2.getPreferredSize()));

            //---- field_Stammdaten ----
            field_Stammdaten.setFocusCycleRoot(true);
            panel1.add(field_Stammdaten);
            field_Stammdaten.setBounds(35, 51, 294, field_Stammdaten.getPreferredSize().height);

            //---- btnStammdaten ----
            btnStammdaten.setText("...");
            btnStammdaten.addActionListener(e -> btnStammdatenActionPerformed(e));
            panel1.add(btnStammdaten);
            btnStammdaten.setBounds(new Rectangle(new Point(345, 50), btnStammdaten.getPreferredSize()));

            //---- btnEinnahmen ----
            btnEinnahmen.setText("...");
            btnEinnahmen.addActionListener(e -> btnEinnahmenActionPerformed(e));
            panel1.add(btnEinnahmen);
            btnEinnahmen.setBounds(new Rectangle(new Point(345, 78), btnEinnahmen.getPreferredSize()));

            //---- btnAusgaben ----
            btnAusgaben.setText("...");
            btnAusgaben.setEnabled(false);
            btnAusgaben.setFocusable(false);
            btnAusgaben.addActionListener(e -> btnAusgabenActionPerformed(e));
            panel1.add(btnAusgaben);
            btnAusgaben.setBounds(new Rectangle(new Point(345, 106), btnAusgaben.getPreferredSize()));

            //---- btnUmsaetze ----
            btnUmsaetze.setText("...");
            btnUmsaetze.setEnabled(false);
            btnUmsaetze.setFocusable(false);
            btnUmsaetze.addActionListener(e -> btnUmsaetzeActionPerformed(e));
            panel1.add(btnUmsaetze);
            btnUmsaetze.setBounds(new Rectangle(new Point(345, 134), btnUmsaetze.getPreferredSize()));

            //---- btnSicherungen ----
            btnSicherungen.setText("...");
            btnSicherungen.setEnabled(false);
            btnSicherungen.setFocusable(false);
            btnSicherungen.setName("");
            btnSicherungen.addActionListener(e -> btnSicherungenActionPerformed(e));
            panel1.add(btnSicherungen);
            btnSicherungen.setBounds(new Rectangle(new Point(345, 162), btnSicherungen.getPreferredSize()));

            //---- btnMahnungen ----
            btnMahnungen.setText("...");
            btnMahnungen.setEnabled(false);
            btnMahnungen.setFocusable(false);
            btnMahnungen.addActionListener(e -> btnMahnungenActionPerformed(e));
            panel1.add(btnMahnungen);
            btnMahnungen.setBounds(new Rectangle(new Point(345, 190), btnMahnungen.getPreferredSize()));

            //---- btnRechnungen ----
            btnRechnungen.setText("...");
            btnRechnungen.setEnabled(false);
            btnRechnungen.setFocusable(false);
            btnRechnungen.addActionListener(e -> btnRechnungenActionPerformed(e));
            panel1.add(btnRechnungen);
            btnRechnungen.setBounds(new Rectangle(new Point(345, 218), btnRechnungen.getPreferredSize()));

            //---- btnTermine ----
            btnTermine.setText("...");
            btnTermine.setEnabled(false);
            btnTermine.setFocusable(false);
            btnTermine.addActionListener(e -> btnTermineActionPerformed(e));
            panel1.add(btnTermine);
            btnTermine.setBounds(new Rectangle(new Point(345, 246), btnTermine.getPreferredSize()));

            //---- btnSchriftverkehr ----
            btnSchriftverkehr.setText("...");
            btnSchriftverkehr.setEnabled(false);
            btnSchriftverkehr.setFocusable(false);
            btnSchriftverkehr.addActionListener(e -> btnSchriftverkehrActionPerformed(e));
            panel1.add(btnSchriftverkehr);
            btnSchriftverkehr.setBounds(new Rectangle(new Point(345, 274), btnSchriftverkehr.getPreferredSize()));

            //---- btnSteuer ----
            btnSteuer.setText("...");
            btnSteuer.setEnabled(false);
            btnSteuer.setFocusable(false);
            btnSteuer.addActionListener(e -> btnSteuerActionPerformed(e));
            panel1.add(btnSteuer);
            btnSteuer.setBounds(new Rectangle(new Point(345, 302), btnSteuer.getPreferredSize()));

            //---- jButtonUpdate ----
            jButtonUpdate.setText("Update");
            jButtonUpdate.setToolTipText("Aktualisiert die Konfiguartionsdaten");
            jButtonUpdate.addActionListener(e -> jButtonUpdateActionPerformed(e));
            panel1.add(jButtonUpdate);
            jButtonUpdate.setBounds(185, 340, 95, 25);

            //---- jButtonSchliessen ----
            jButtonSchliessen.setText("Schlie\u00dfen");
            jButtonSchliessen.setToolTipText("Schlie\u00dft den Dialog");
            jButtonSchliessen.addActionListener(e -> jButtonSchliessenActionPerformed(e));
            panel1.add(jButtonSchliessen);
            jButtonSchliessen.setBounds(285, 340, 104, 25);

            { // compute preferred size
                Dimension preferredSize = new Dimension();
                for(int i = 0; i < panel1.getComponentCount(); i++) {
                    Rectangle bounds = panel1.getComponent(i).getBounds();
                    preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                    preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                }
                Insets insets = panel1.getInsets();
                preferredSize.width += insets.right;
                preferredSize.height += insets.bottom;
                panel1.setMinimumSize(preferredSize);
                panel1.setPreferredSize(preferredSize);
            }
        }
        contentPane.add(panel1);
        panel1.setBounds(10, 10, 390, 365);

        { // compute preferred size
            Dimension preferredSize = new Dimension();
            for(int i = 0; i < contentPane.getComponentCount(); i++) {
                Rectangle bounds = contentPane.getComponent(i).getBounds();
                preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
            }
            Insets insets = contentPane.getInsets();
            preferredSize.width += insets.right;
            preferredSize.height += insets.bottom;
            contentPane.setMinimumSize(preferredSize);
            contentPane.setPreferredSize(preferredSize);
        }
        setSize(415, 420);
        setLocationRelativeTo(getOwner());
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUpdateActionPerformed
        // TODO add your handling code here:
        try {
            if (resultIsEmpty) {
                result.moveToInsertRow();
            }
            result.updateString("Konfiguration_Stammdaten", field_Stammdaten.getText());
            result.updateString("Konfiguration_Einnahmen", field_Einnahmen.getText());
            /**
            result.updateString("Konfiguration_Ausgaben", field_Ausgaben.getText());
            result.updateString("Konfiguration_Umsaetze", field_Umsaetze.getText());
            result.updateString("Konfiguration_Rechnungen", field_Rechnungen.getText());
            result.updateString("Konfiguration_Sicherung", field_Sicherung.getText());
            result.updateString("Konfiguration_Mahnungen", field_Mahnungen.getText());
            result.updateString("Konfiguration_Termine", field_Termine.getText());
            result.updateString("Konfiguration_Schriftverkehr", field_Schriftverkehr.getText());
            result.updateString("Konfiguration_Steuer", field_Steuer.getText());
            */
            if (resultIsEmpty) {
                result.insertRow();
                resultIsEmpty = false;
            } else {
                result.updateRow();
            }

            Modulhelferlein.pathQuelle = field_Stammdaten.getText();
            Modulhelferlein.pathZiel = field_Einnahmen.getText();
            /**
            helferlein.pathAusgaben = field_Ausgaben.getText();
            helferlein.pathUmsaetze = field_Umsaetze.getText();
            helferlein.pathRechnungen = field_Rechnungen.getText();
            helferlein.pathSicherung = field_Sicherung.getText();
            helferlein.pathMahnungen = field_Mahnungen.getText();
            helferlein.pathKonfiguration = field_Termine.getText();
            helferlein.pathBuchprojekte = field_Schriftverkehr.getText();
            helferlein.pathSteuer = field_Steuer.getText();
            */
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: " + exept.getMessage());
        }

        // Dialog schlie�en
        jButtonSchliessenActionPerformed(evt);
    }//GEN-LAST:event_jButtonUpdateActionPerformed

    private void jButtonSchliessenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSchliessenActionPerformed
        // TODO add your handling code here:
        try {
            result.close();
            SQLAnfrage.close();
            conn.close();
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: " + exept.getMessage());
        }
        this.dispose();
    }//GEN-LAST:event_jButtonSchliessenActionPerformed

    private void btnStammdatenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStammdatenActionPerformed
        // TODO add your handling code here:
        if (chooser.showDialog(null, "Verzeichnis w�hlen") == JFileChooser.APPROVE_OPTION) {
            field_Stammdaten.setText(chooser.getSelectedFile().getPath());
        }
    }//GEN-LAST:event_btnStammdatenActionPerformed

    private void btnEinnahmenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEinnahmenActionPerformed
        // TODO add your handling code here:
        if (chooser.showDialog(null, "Verzeichnis w�hlen") == JFileChooser.APPROVE_OPTION) {
            field_Einnahmen.setText(chooser.getSelectedFile().getPath());
        }
    }//GEN-LAST:event_btnEinnahmenActionPerformed

    private void btnAusgabenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAusgabenActionPerformed
        // TODO add your handling code here:
        if (chooser.showDialog(null, "Verzeichnis w�hlen") == JFileChooser.APPROVE_OPTION) {
            field_Ausgaben.setText(chooser.getSelectedFile().getPath());
        }
    }//GEN-LAST:event_btnAusgabenActionPerformed

    private void btnUmsaetzeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUmsaetzeActionPerformed
        // TODO add your handling code here:
        if (chooser.showDialog(null, "Verzeichnis w�hlen") == JFileChooser.APPROVE_OPTION) {
            field_Umsaetze.setText(chooser.getSelectedFile().getPath());
        }
    }//GEN-LAST:event_btnUmsaetzeActionPerformed

    private void btnSicherungenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSicherungenActionPerformed
        // TODO add your handling code here:
        if (chooser.showDialog(null, "Verzeichnis w�hlen") == JFileChooser.APPROVE_OPTION) {
            field_Sicherung.setText(chooser.getSelectedFile().getPath());
        }
    }//GEN-LAST:event_btnSicherungenActionPerformed

    private void btnMahnungenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMahnungenActionPerformed
        // TODO add your handling code here:
        if (chooser.showDialog(null, "Verzeichnis w�hlen") == JFileChooser.APPROVE_OPTION) {
            field_Mahnungen.setText(chooser.getSelectedFile().getPath());
        }
    }//GEN-LAST:event_btnMahnungenActionPerformed

    private void btnRechnungenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRechnungenActionPerformed
        // TODO add your handling code here:
        if (chooser.showDialog(null, "Verzeichnis w�hlen") == JFileChooser.APPROVE_OPTION) {
            field_Rechnungen.setText(chooser.getSelectedFile().getPath());
        }
    }//GEN-LAST:event_btnRechnungenActionPerformed

    private void btnTermineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTermineActionPerformed
        // TODO add your handling code here:
        if (chooser.showDialog(null, "Verzeichnis w�hlen") == JFileChooser.APPROVE_OPTION) {
            field_Termine.setText(chooser.getSelectedFile().getPath());
        }
    }//GEN-LAST:event_btnTermineActionPerformed

    private void btnSchriftverkehrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSchriftverkehrActionPerformed
        // TODO add your handling code here:
        if (chooser.showDialog(null, "Verzeichnis w�hlen") == JFileChooser.APPROVE_OPTION) {
            field_Schriftverkehr.setText(chooser.getSelectedFile().getPath());
        }
    }//GEN-LAST:event_btnSchriftverkehrActionPerformed

    private void btnSteuerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSteuerActionPerformed
        // TODO add your handling code here:
        if (chooser.showDialog(null, "Verzeichnis w�hlen") == JFileChooser.APPROVE_OPTION) {
            field_Steuer.setText(chooser.getSelectedFile().getPath());
        }
    }//GEN-LAST:event_btnSteuerActionPerformed

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
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CarolaHartmannMilesVerlag.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
*/        
        //</editor-fold>

        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(() -> {
            VerwaltenDatenbankKonfigurationSicherung dialog = new VerwaltenDatenbankKonfigurationSicherung(new javax.swing.JFrame(), true);
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    dialog.setVisible(false);
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
    private JLabel jLabel5;
    private JTextField field_Einnahmen;
    private JTextField field_Ausgaben;
    private JTextField field_Umsaetze;
    private JTextField field_Sicherung;
    private JTextField field_Mahnungen;
    private JTextField field_Rechnungen;
    private JTextField field_Termine;
    private JTextField field_Schriftverkehr;
    private JTextField field_Steuer;
    private JLabel jLabel6;
    private JLabel jLabel7;
    private JLabel jLabel8;
    private JLabel jLabel9;
    private JLabel jLabel10;
    private JLabel jLabel11;
    private JTextField field_ID;
    private JLabel jLabel2;
    private JTextField field_Stammdaten;
    private JButton btnStammdaten;
    private JButton btnEinnahmen;
    private JButton btnAusgaben;
    private JButton btnUmsaetze;
    private JButton btnSicherungen;
    private JButton btnMahnungen;
    private JButton btnRechnungen;
    private JButton btnTermine;
    private JButton btnSchriftverkehr;
    private JButton btnSteuer;
    private JButton jButtonUpdate;
    private JButton jButtonSchliessen;
    // End of variables declaration//GEN-END:variables

    private Connection conn;
    private Statement SQLAnfrage;
    private ResultSet result;
    private JFileChooser chooser;
    private boolean resultIsEmpty = true;

}
