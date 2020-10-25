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

/**
 *
 * @author Thomas Zimmermann
 */
public class VerwaltenDatenbankKonfigurationWaehrung extends javax.swing.JDialog {

    /**
     * Creates new form VerwaltenDatenbankKonfigurationWaehrung
     *
     * @param parent
     * @param modal
     */
    public VerwaltenDatenbankKonfigurationWaehrung(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.chooser = new JFileChooser(new File(System.getProperty("user.dir")));
        this.chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        initComponents();

        conn = null;

        // Datenbank-Treiber laden
        try {
            Class.forName(ModulHelferlein.dbDriver);
        } catch (ClassNotFoundException exept) {
            ModulHelferlein.Fehlermeldung("Treiber nicht gefunden.");
        }

        // Verbindung zur Datenbank �ber die JDBC-Br�cke
        try {
            conn = DriverManager.getConnection(ModulHelferlein.dbUrl, ModulHelferlein.dbUser, ModulHelferlein.dbPassword);
        } catch (SQLException exept) {
            ModulHelferlein.Fehlermeldung("Verbindung zur Datenbank nicht moeglich.");
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

                // gehe zum zweiten Datensatz - wenn nicht leer
                if (result.next()) {
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
                ModulHelferlein.Fehlermeldung(
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
        jLabel2 = new JLabel();
        jLabel3 = new JLabel();
        jLabel4 = new JLabel();
        jLabel5 = new JLabel();
        field_Stammdaten = new JTextField();
        field_Einnahmen = new JTextField();
        field_Ausgaben = new JTextField();
        field_Umsaetze = new JTextField();
        field_Sicherung = new JTextField();
        field_Mahnungen = new JTextField();
        field_Rechnungen = new JTextField();
        field_Termine = new JTextField();
        field_Schriftverkehr = new JTextField();
        field_Steuer = new JTextField();
        btnStammdaten = new JButton();
        jButtonUpdate = new JButton();
        jButtonSchliessen = new JButton();
        jLabel6 = new JLabel();
        jLabel7 = new JLabel();
        jLabel8 = new JLabel();
        jLabel9 = new JLabel();
        jLabel10 = new JLabel();
        jLabel11 = new JLabel();
        btnSteuer = new JButton();
        btnAusgaben = new JButton();
        btnEinnahmen = new JButton();
        btnUmsaetze = new JButton();
        btnSicherungen = new JButton();
        btnMahnungen = new JButton();
        btnRechnungen = new JButton();
        btnTermine = new JButton();
        btnSchriftverkehr = new JButton();
        field_ID = new JTextField();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Carola Hartmann Miles Verlag");
        setResizable(false);
        Container contentPane = getContentPane();
        contentPane.setLayout(null);

        //======== panel1 ========
        {

            //---- jLabel1 ----
            jLabel1.setFont(new Font("Tahoma", Font.BOLD, 12));
            jLabel1.setText("Verwalten der Konfiguration - W\u00e4hrungen");

            //---- jLabel2 ----
            jLabel2.setText("USD");

            //---- jLabel3 ----
            jLabel3.setText("GBP");

            //---- jLabel4 ----
            jLabel4.setText("CHF");

            //---- jLabel5 ----
            jLabel5.setText("NOK");

            //---- field_Stammdaten ----
            field_Stammdaten.setFocusCycleRoot(true);
            field_Stammdaten.setName("");

            //---- field_Einnahmen ----
            field_Einnahmen.setName("");

            //---- field_Ausgaben ----
            field_Ausgaben.setName("");

            //---- field_Umsaetze ----
            field_Umsaetze.setName("");

            //---- field_Sicherung ----
            field_Sicherung.setName("");

            //---- field_Mahnungen ----
            field_Mahnungen.setName("");

            //---- field_Rechnungen ----
            field_Rechnungen.setName("");

            //---- field_Termine ----
            field_Termine.setName("");

            //---- field_Schriftverkehr ----
            field_Schriftverkehr.setName("");

            //---- field_Steuer ----
            field_Steuer.setName("");

            //---- btnStammdaten ----
            btnStammdaten.setText("...");
            btnStammdaten.setEnabled(false);
            btnStammdaten.setFocusable(false);
            btnStammdaten.setName("");
            btnStammdaten.addActionListener(e -> btnStammdatenActionPerformed(e));

            //---- jButtonUpdate ----
            jButtonUpdate.setText("Update");
            jButtonUpdate.setToolTipText("Aktualisiert die Konfiguartionsdaten");
            jButtonUpdate.setName("");
            jButtonUpdate.addActionListener(e -> jButtonUpdateActionPerformed(e));

            //---- jButtonSchliessen ----
            jButtonSchliessen.setText("Schlie\u00dfen");
            jButtonSchliessen.setToolTipText("Schlie\u00dft den Dialog");
            jButtonSchliessen.setName("");
            jButtonSchliessen.addActionListener(e -> jButtonSchliessenActionPerformed(e));

            //---- jLabel6 ----
            jLabel6.setText("ILS");

            //---- jLabel7 ----
            jLabel7.setText("DKK");

            //---- jLabel8 ----
            jLabel8.setText("CAD");

            //---- jLabel9 ----
            jLabel9.setText("-");

            //---- jLabel10 ----
            jLabel10.setText("-");

            //---- jLabel11 ----
            jLabel11.setText("-");
            jLabel11.setToolTipText("");

            //---- btnSteuer ----
            btnSteuer.setText("...");
            btnSteuer.setEnabled(false);
            btnSteuer.setFocusable(false);
            btnSteuer.setName("");
            btnSteuer.addActionListener(e -> btnSteuerActionPerformed(e));

            //---- btnAusgaben ----
            btnAusgaben.setText("...");
            btnAusgaben.setEnabled(false);
            btnAusgaben.setFocusable(false);
            btnAusgaben.setName("");
            btnAusgaben.addActionListener(e -> btnAusgabenActionPerformed(e));

            //---- btnEinnahmen ----
            btnEinnahmen.setText("...");
            btnEinnahmen.setEnabled(false);
            btnEinnahmen.setFocusable(false);
            btnEinnahmen.setName("");
            btnEinnahmen.addActionListener(e -> btnEinnahmenActionPerformed(e));

            //---- btnUmsaetze ----
            btnUmsaetze.setText("...");
            btnUmsaetze.setEnabled(false);
            btnUmsaetze.setFocusable(false);
            btnUmsaetze.setName("");
            btnUmsaetze.addActionListener(e -> btnUmsaetzeActionPerformed(e));

            //---- btnSicherungen ----
            btnSicherungen.setText("...");
            btnSicherungen.setEnabled(false);
            btnSicherungen.setFocusable(false);
            btnSicherungen.setName("");
            btnSicherungen.addActionListener(e -> btnSicherungenActionPerformed(e));

            //---- btnMahnungen ----
            btnMahnungen.setText("...");
            btnMahnungen.setEnabled(false);
            btnMahnungen.setFocusable(false);
            btnMahnungen.setName("");
            btnMahnungen.addActionListener(e -> btnMahnungenActionPerformed(e));

            //---- btnRechnungen ----
            btnRechnungen.setText("...");
            btnRechnungen.setEnabled(false);
            btnRechnungen.setFocusable(false);
            btnRechnungen.setName("");
            btnRechnungen.addActionListener(e -> btnRechnungenActionPerformed(e));

            //---- btnTermine ----
            btnTermine.setText("...");
            btnTermine.setEnabled(false);
            btnTermine.setFocusable(false);
            btnTermine.setName("");
            btnTermine.addActionListener(e -> btnTermineActionPerformed(e));

            //---- btnSchriftverkehr ----
            btnSchriftverkehr.setText("...");
            btnSchriftverkehr.setEnabled(false);
            btnSchriftverkehr.setFocusable(false);
            btnSchriftverkehr.setName("");
            btnSchriftverkehr.addActionListener(e -> btnSchriftverkehrActionPerformed(e));

            //---- field_ID ----
            field_ID.setEditable(false);
            field_ID.setEnabled(false);
            field_ID.setFocusable(false);

            GroupLayout panel1Layout = new GroupLayout(panel1);
            panel1.setLayout(panel1Layout);
            panel1Layout.setHorizontalGroup(
                panel1Layout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                        .addContainerGap(12, Short.MAX_VALUE)
                        .addGroup(panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(96, 96, 96)
                                .addComponent(field_ID, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(11, 11, 11)
                                .addComponent(field_Stammdaten, GroupLayout.PREFERRED_SIZE, 318, GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)
                                .addComponent(btnStammdaten, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(12, 12, 12)
                                .addComponent(field_Einnahmen, GroupLayout.PREFERRED_SIZE, 318, GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)
                                .addComponent(btnEinnahmen, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addGap(11, 11, 11)
                                .addComponent(field_Ausgaben, GroupLayout.PREFERRED_SIZE, 318, GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)
                                .addComponent(btnAusgaben, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addGap(10, 10, 10)
                                .addComponent(field_Umsaetze, GroupLayout.PREFERRED_SIZE, 318, GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)
                                .addComponent(btnUmsaetze, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addGap(16, 16, 16)
                                .addComponent(field_Sicherung, GroupLayout.PREFERRED_SIZE, 318, GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)
                                .addComponent(btnSicherungen, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addGap(12, 12, 12)
                                .addComponent(field_Mahnungen, GroupLayout.PREFERRED_SIZE, 318, GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)
                                .addComponent(btnMahnungen, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addGap(10, 10, 10)
                                .addComponent(field_Rechnungen, GroupLayout.PREFERRED_SIZE, 318, GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)
                                .addComponent(btnRechnungen, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addGap(27, 27, 27)
                                .addComponent(field_Termine, GroupLayout.PREFERRED_SIZE, 318, GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)
                                .addComponent(btnTermine, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addGap(27, 27, 27)
                                .addComponent(field_Schriftverkehr, GroupLayout.PREFERRED_SIZE, 318, GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)
                                .addComponent(btnSchriftverkehr, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addGap(27, 27, 27)
                                .addComponent(field_Steuer, GroupLayout.PREFERRED_SIZE, 318, GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)
                                .addComponent(btnSteuer, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(jButtonUpdate)
                                .addGap(10, 10, 10)
                                .addComponent(jButtonSchliessen)))
                        .addContainerGap())
            );
            panel1Layout.setVerticalGroup(
                panel1Layout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addComponent(jLabel1))
                            .addComponent(field_ID, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addComponent(jLabel2))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addComponent(field_Stammdaten, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addComponent(btnStammdaten))
                        .addGap(6, 6, 6)
                        .addGroup(panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addComponent(jLabel3))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addComponent(field_Einnahmen, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addComponent(btnEinnahmen))
                        .addGap(6, 6, 6)
                        .addGroup(panel1Layout.createParallelGroup()
                            .addComponent(jLabel4)
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addComponent(field_Ausgaben, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addComponent(btnAusgaben))
                        .addGap(6, 6, 6)
                        .addGroup(panel1Layout.createParallelGroup()
                            .addComponent(jLabel5)
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addComponent(field_Umsaetze, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addComponent(btnUmsaetze))
                        .addGap(6, 6, 6)
                        .addGroup(panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addComponent(jLabel6))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addComponent(field_Sicherung, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addComponent(btnSicherungen))
                        .addGap(6, 6, 6)
                        .addGroup(panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addComponent(jLabel7))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addComponent(field_Mahnungen, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addComponent(btnMahnungen))
                        .addGap(6, 6, 6)
                        .addGroup(panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addComponent(jLabel8))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addComponent(field_Rechnungen, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addComponent(btnRechnungen))
                        .addGap(6, 6, 6)
                        .addGroup(panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addComponent(jLabel9))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addComponent(field_Termine, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addComponent(btnTermine))
                        .addGap(6, 6, 6)
                        .addGroup(panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addComponent(jLabel10))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addComponent(field_Schriftverkehr, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addComponent(btnSchriftverkehr))
                        .addGap(6, 6, 6)
                        .addGroup(panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addComponent(jLabel11))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addComponent(field_Steuer, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addComponent(btnSteuer))
                        .addGap(6, 6, 6)
                        .addGroup(panel1Layout.createParallelGroup()
                            .addComponent(jButtonUpdate)
                            .addComponent(jButtonSchliessen))
                        .addContainerGap())
            );
        }
        contentPane.add(panel1);
        panel1.setBounds(0, 0, 400, 370);

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
        setSize(400, 400);
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
            result.updateString("Konfiguration_Ausgaben", field_Ausgaben.getText());
            result.updateString("Konfiguration_Umsaetze", field_Umsaetze.getText());
            result.updateString("Konfiguration_Rechnungen", field_Rechnungen.getText());
            result.updateString("Konfiguration_Sicherung", field_Sicherung.getText());
            result.updateString("Konfiguration_Mahnungen", field_Mahnungen.getText());
            result.updateString("Konfiguration_Termine", field_Termine.getText());
            result.updateString("Konfiguration_Schriftverkehr", field_Schriftverkehr.getText());
            result.updateString("Konfiguration_Steuer", field_Steuer.getText());
            if (resultIsEmpty) {
                result.insertRow();
                resultIsEmpty = false;
            } else {
                result.updateRow();
            }
            ModulHelferlein.USD = Float.parseFloat(field_Stammdaten.getText());
            ModulHelferlein.GBP = Float.parseFloat(field_Einnahmen.getText());
            ModulHelferlein.CHF = Float.parseFloat(field_Ausgaben.getText());
            ModulHelferlein.NOK = Float.parseFloat(field_Umsaetze.getText());
            ModulHelferlein.ILS = Float.parseFloat(field_Rechnungen.getText());
            ModulHelferlein.DKK = Float.parseFloat(field_Sicherung.getText());
            ModulHelferlein.CAD = Float.parseFloat(field_Mahnungen.getText());
            /*
            ModulHelferlein.pathKonfiguration = field_Termine.getText();
            ModulHelferlein.pathBuchprojekte = field_Schriftverkehr.getText();
            ModulHelferlein.pathSteuer = field_Steuer.getText();
            */
        } catch (SQLException exept) {
            ModulHelferlein.Fehlermeldung("SQL-Exception: " + exept.getMessage());
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
            ModulHelferlein.Fehlermeldung("SQL-Exception: " + exept.getMessage());
        }
        this.dispose();
    }//GEN-LAST:event_jButtonSchliessenActionPerformed

    private void btnSteuerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSteuerActionPerformed
        // TODO add your handling code here:
        if (chooser.showDialog(null, "Verzeichnis w�hlen") == JFileChooser.APPROVE_OPTION) {
            field_Steuer.setText(chooser.getSelectedFile().getPath());
        }
    }//GEN-LAST:event_btnSteuerActionPerformed

    private void btnSchriftverkehrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSchriftverkehrActionPerformed
        // TODO add your handling code here:
        if (chooser.showDialog(null, "Verzeichnis w�hlen") == JFileChooser.APPROVE_OPTION) {
            field_Schriftverkehr.setText(chooser.getSelectedFile().getPath());
        }
    }//GEN-LAST:event_btnSchriftverkehrActionPerformed

    private void btnTermineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTermineActionPerformed
        // TODO add your handling code here:
        if (chooser.showDialog(null, "Verzeichnis w�hlen") == JFileChooser.APPROVE_OPTION) {
            field_Termine.setText(chooser.getSelectedFile().getPath());
        }
    }//GEN-LAST:event_btnTermineActionPerformed

    private void btnRechnungenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRechnungenActionPerformed
        // TODO add your handling code here:
        if (chooser.showDialog(null, "Verzeichnis w�hlen") == JFileChooser.APPROVE_OPTION) {
            field_Rechnungen.setText(chooser.getSelectedFile().getPath());
        }
    }//GEN-LAST:event_btnRechnungenActionPerformed

    private void btnMahnungenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMahnungenActionPerformed
        // TODO add your handling code here:
        if (chooser.showDialog(null, "Verzeichnis w�hlen") == JFileChooser.APPROVE_OPTION) {
            field_Mahnungen.setText(chooser.getSelectedFile().getPath());
        }
    }//GEN-LAST:event_btnMahnungenActionPerformed

    private void btnSicherungenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSicherungenActionPerformed
        // TODO add your handling code here:
        if (chooser.showDialog(null, "Verzeichnis w�hlen") == JFileChooser.APPROVE_OPTION) {
            field_Sicherung.setText(chooser.getSelectedFile().getPath());
        }
    }//GEN-LAST:event_btnSicherungenActionPerformed

    private void btnUmsaetzeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUmsaetzeActionPerformed
        // TODO add your handling code here:
        if (chooser.showDialog(null, "Verzeichnis w�hlen") == JFileChooser.APPROVE_OPTION) {
            field_Umsaetze.setText(chooser.getSelectedFile().getPath());
        }
    }//GEN-LAST:event_btnUmsaetzeActionPerformed

    private void btnAusgabenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAusgabenActionPerformed
        // TODO add your handling code here:
        if (chooser.showDialog(null, "Verzeichnis w�hlen") == JFileChooser.APPROVE_OPTION) {
            field_Ausgaben.setText(chooser.getSelectedFile().getPath());
        }
    }//GEN-LAST:event_btnAusgabenActionPerformed

    private void btnEinnahmenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEinnahmenActionPerformed
        // TODO add your handling code here:
        if (chooser.showDialog(null, "Verzeichnis w�hlen") == JFileChooser.APPROVE_OPTION) {
            field_Einnahmen.setText(chooser.getSelectedFile().getPath());
        }
    }//GEN-LAST:event_btnEinnahmenActionPerformed

    private void btnStammdatenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStammdatenActionPerformed
        // TODO add your handling code here:
        if (chooser.showDialog(null, "Verzeichnis w�hlen") == JFileChooser.APPROVE_OPTION) {
            field_Stammdaten.setText(chooser.getSelectedFile().getPath());
        }
    }//GEN-LAST:event_btnStammdatenActionPerformed

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
            VerwaltenDatenbankKonfigurationWaehrung dialog = new VerwaltenDatenbankKonfigurationWaehrung(new javax.swing.JFrame(), true);
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
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JLabel jLabel5;
    private JTextField field_Stammdaten;
    private JTextField field_Einnahmen;
    private JTextField field_Ausgaben;
    private JTextField field_Umsaetze;
    private JTextField field_Sicherung;
    private JTextField field_Mahnungen;
    private JTextField field_Rechnungen;
    private JTextField field_Termine;
    private JTextField field_Schriftverkehr;
    private JTextField field_Steuer;
    private JButton btnStammdaten;
    private JButton jButtonUpdate;
    private JButton jButtonSchliessen;
    private JLabel jLabel6;
    private JLabel jLabel7;
    private JLabel jLabel8;
    private JLabel jLabel9;
    private JLabel jLabel10;
    private JLabel jLabel11;
    private JButton btnSteuer;
    private JButton btnAusgaben;
    private JButton btnEinnahmen;
    private JButton btnUmsaetze;
    private JButton btnSicherungen;
    private JButton btnMahnungen;
    private JButton btnRechnungen;
    private JButton btnTermine;
    private JButton btnSchriftverkehr;
    private JTextField field_ID;
    // End of variables declaration//GEN-END:variables

    private Connection conn;
    private Statement SQLAnfrage;
    private ResultSet result;
    private JFileChooser chooser;
    private boolean resultIsEmpty = true;

}
