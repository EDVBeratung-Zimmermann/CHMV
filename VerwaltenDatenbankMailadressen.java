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
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.JOptionPane;
import javax.swing.plaf.ActionMapUIResource;

/**
 *
 * @author Thomas Zimmermann
 */
public class VerwaltenDatenbankMailadressen extends javax.swing.JDialog {

    /**
     * Creates new form VerwaltenDatenbankMailadressen
     *
     * @param parent
     * @param modal
     */
    public VerwaltenDatenbankMailadressen(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        ActionMap actionMap = new ActionMapUIResource();
        actionMap.put("action_anfang", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AnfangActionPerformed(e);
                System.out.println("anfang action performed.");
            }
        });
        actionMap.put("action_ende", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EndeActionPerformed(e);
                System.out.println("ende action performed.");
            }
        });
        actionMap.put("action_vor", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                VorActionPerformed(e);
                System.out.println("vor action performed.");
            }
        });
        actionMap.put("action_zurueck", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ZurueckActionPerformed(e);
                System.out.println("zurueck action performed.");
            }
        });
        actionMap.put("action_insert", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EinfuegenActionPerformed(e);
                System.out.println("insert action performed.");
            }
        });
        actionMap.put("action_del", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LoeschenActionPerformed(e);
                System.out.println("del action performed.");
            }
        });
        actionMap.put("action_save", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UpdateActionPerformed(e);
                System.out.println("Save action performed.");
            }
        });
        actionMap.put("action_exit", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SchliessenActionPerformed(e);
                System.out.println("Exit action performed.");
            }
        });
        actionMap.put("action_print", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DruckenActionPerformed(e);
                System.out.println("print action performed.");
            }
        });

        
        InputMap keyMap = new ComponentInputMap(panel1);
        //keyMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.Event.CTRL_MASK), "action_anfang");
        //keyMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.Event.CTRL_MASK), "action_ende");

        keyMap.put(KeyStroke.getKeyStroke("control alt A"), "action_anfang");
        keyMap.put(KeyStroke.getKeyStroke("control alt E"), "action_ende");
        keyMap.put(KeyStroke.getKeyStroke("control alt V"), "action_vor");
        keyMap.put(KeyStroke.getKeyStroke("control alt Z"), "action_zurueck");
        keyMap.put(KeyStroke.getKeyStroke("control alt I"), "action_insert");
        keyMap.put(KeyStroke.getKeyStroke("control alt D"), "action_del");
        keyMap.put(KeyStroke.getKeyStroke("control alt S"), "action_save");
        keyMap.put(KeyStroke.getKeyStroke("control alt X"), "action_exit");
        keyMap.put(KeyStroke.getKeyStroke("control alt P"), "action_print");
        SwingUtilities.replaceUIActionMap(panel1, actionMap);
        SwingUtilities.replaceUIInputMap(panel1, JComponent.WHEN_IN_FOCUSED_WINDOW,keyMap);        
        
        buttonGroupAdresse.add(rbAdressDB);
        buttonGroupAdresse.add(rbManuell);

        conn = null;

        // Datenbank-Treiber laden
        try {
            Class.forName(Modulhelferlein.dbDriver);
        } catch (ClassNotFoundException exept) {
            Modulhelferlein.Fehlermeldung(
                    "ClassNotFoundException: Treiber nicht gefunden. "
                    + exept.getMessage());
        }

        // Verbindung zur Datenbank über die JDBC-Brücke
        try {
            conn = DriverManager.getConnection(Modulhelferlein.dbUrl, Modulhelferlein.dbUser, Modulhelferlein.dbPassword);
            // final Connection conn2=conn;

            if (conn != null) {
                SQLAnfrage = null; // Anfrage erzeugen
                SQLAnfrageVerteiler = null; // Anfrage erzeugen
                SQLAnfrageAdressse = null;

                SQLAnfrageVerteiler = conn.createStatement(
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE); // Anfrage der DB conn2 zuordnen
                resultVerteiler = SQLAnfrageVerteiler.executeQuery("SELECT * FROM TBL_MAILVERTEILER"); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 
                while (resultVerteiler.next()) {
                    field_Verteiler.addItem(resultVerteiler.getString("MAILVERTEILER_NAME"));
                } // while

                SQLAnfrageAdressse = conn.createStatement(
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE); // Anfrage der DB conn2 zuordnen
                resultAdresse = SQLAnfrageAdressse.executeQuery("SELECT * FROM TBL_ADRESSE"); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 
                while (resultAdresse.next()) {
                    field_Adresse.addItem(resultAdresse.getString("ADRESSEN_NAME") + ","
                            + resultAdresse.getString("ADRESSEN_VORNAME") + ","
                            + resultAdresse.getString("ADRESSEN_EMAIL"));
                } // while

                try {
                    SQLAnfrage = conn.createStatement(
                            ResultSet.TYPE_SCROLL_SENSITIVE,
                            ResultSet.CONCUR_UPDATABLE); // Anfrage der DB conn2 zuordnen
                    result = SQLAnfrage.executeQuery("SELECT * FROM TBL_MAILADRESSE ORDER BY MAILADRESSE_ID DESC"); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 
                    if (result.next()) {
                        maxID = 1 + result.getInt("MAILADRESSE_ID");
                    } else {
                        maxID = 0;
                    }
                    result = SQLAnfrage.executeQuery("SELECT * FROM TBL_MAILADRESSE"); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 
                    // Anzahl der Datensätze ermitteln
                    countMax = 0;
                    count = 0;
                    field_count.setText(Integer.toString(count));
                    while (result.next()) {
                        ++countMax;
                    }
                    field_countMax.setText(Integer.toString(countMax));
                    // gehe zum ersten Datensatz - wenn nicht leer
                    if (result.first()) {
                        count = 1;
                        field_count.setText(Integer.toString(count));
                        resultIsEmpty = false;
                        field_ID.setText(Integer.toString(result.getInt("MAILADRESSE_ID")));
                        field_Verteiler.setSelectedIndex(result.getInt("MAILADRESSE_VERTEILER"));
                        field_Beschreibung.setText(result.getString("MAILADRESSE_ADRESSE"));
                        // Schalterzustände setzen   
                        Anfang.setEnabled(true);
                        Zurueck.setEnabled(true);
                        Vor.setEnabled(true);
                        Ende.setEnabled(true);
                        Update.setEnabled(true);
                        Einfuegen.setEnabled(true);
                        Loeschen.setEnabled(true);
                        Suchen.setEnabled(true);
                        WSuchen.setEnabled(true);
                        Drucken.setEnabled(true);
                        Schliessen.setEnabled(true);
                    } else {
                        // Schalterzustände setzen   
                        Anfang.setEnabled(false);
                        Zurueck.setEnabled(false);
                        Vor.setEnabled(false);
                        Ende.setEnabled(false);
                        Update.setEnabled(false);
                        Einfuegen.setEnabled(true);
                        Loeschen.setEnabled(false);
                        Suchen.setEnabled(false);
                        WSuchen.setEnabled(false);
                        Drucken.setEnabled(false);
                        Schliessen.setEnabled(true);
                    }
                } catch (SQLException exept) {
                    Modulhelferlein.Fehlermeldung("SQL-Exception: SQL-Anfrage nicht moeglich. " + exept.getMessage());
                }
            }
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: Verbindung nicht moeglich. " + exept.getMessage());
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
        Loeschen = new JButton();
        Suchen = new JButton();
        jLabelTitel = new JLabel();
        WSuchen = new JButton();
        jLabel1 = new JLabel();
        Drucken = new JButton();
        field_count = new JTextField();
        Schliessen = new JButton();
        jLabel2 = new JLabel();
        Zurueck = new JButton();
        field_countMax = new JTextField();
        Anfang = new JButton();
        jLabelID = new JLabel();
        Vor = new JButton();
        field_ID = new JTextField();
        Ende = new JButton();
        Update = new JButton();
        jLabel3 = new JLabel();
        Einfuegen = new JButton();
        rbAdressDB = new JRadioButton();
        rbManuell = new JRadioButton();
        field_Adresse = new JComboBox<>();
        field_Verteiler = new JComboBox<>();
        field_Beschreibung = new JTextField();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Carola Hartmann Miles Verlag");
        setResizable(false);
        setFont(new Font("Dialog", Font.BOLD, 12));
        Container contentPane = getContentPane();

        //======== panel1 ========
        {

            //---- Loeschen ----
            Loeschen.setText("-");
            Loeschen.setToolTipText("Datensatz l\u00f6schen");
            Loeschen.setMaximumSize(new Dimension(20, 23));
            Loeschen.setMinimumSize(new Dimension(20, 23));
            Loeschen.setPreferredSize(new Dimension(50, 23));
            Loeschen.addActionListener(e -> LoeschenActionPerformed(e));

            //---- Suchen ----
            Suchen.setText("?");
            Suchen.setToolTipText("Datensatz suchen");
            Suchen.setMaximumSize(new Dimension(20, 23));
            Suchen.setMinimumSize(new Dimension(20, 23));
            Suchen.setPreferredSize(new Dimension(50, 23));
            Suchen.addActionListener(e -> SuchenActionPerformed(e));

            //---- jLabelTitel ----
            jLabelTitel.setFont(new Font("Tahoma", Font.BOLD, 12));
            jLabelTitel.setText("Verwaltung der Mailadressen");

            //---- WSuchen ----
            WSuchen.setText("...");
            WSuchen.setToolTipText("Weitersuchen");
            WSuchen.setMaximumSize(new Dimension(20, 23));
            WSuchen.setMinimumSize(new Dimension(20, 23));
            WSuchen.setPreferredSize(new Dimension(50, 23));
            WSuchen.addActionListener(e -> WSuchenActionPerformed(e));

            //---- jLabel1 ----
            jLabel1.setText("Datensatz Nr.");

            //---- Drucken ----
            Drucken.setText("D");
            Drucken.setToolTipText("Liste der Einnahmen drucken");
            Drucken.setMaximumSize(new Dimension(20, 23));
            Drucken.setMinimumSize(new Dimension(20, 23));
            Drucken.setPreferredSize(new Dimension(50, 23));
            Drucken.addActionListener(e -> DruckenActionPerformed(e));

            //---- field_count ----
            field_count.setEditable(false);
            field_count.setText("000");
            field_count.setEnabled(false);
            field_count.setFocusable(false);
            field_count.setMaximumSize(new Dimension(30, 20));
            field_count.setMinimumSize(new Dimension(30, 20));
            field_count.setPreferredSize(new Dimension(30, 20));

            //---- Schliessen ----
            Schliessen.setText("X");
            Schliessen.setMaximumSize(new Dimension(20, 23));
            Schliessen.setMinimumSize(new Dimension(20, 23));
            Schliessen.setPreferredSize(new Dimension(50, 23));
            Schliessen.addActionListener(e -> SchliessenActionPerformed(e));

            //---- jLabel2 ----
            jLabel2.setText("von");

            //---- Zurueck ----
            Zurueck.setText("<");
            Zurueck.setToolTipText("Gehe zum vorherigen Datensatz");
            Zurueck.setMaximumSize(new Dimension(20, 23));
            Zurueck.setMinimumSize(new Dimension(20, 23));
            Zurueck.setPreferredSize(new Dimension(50, 23));
            Zurueck.addActionListener(e -> ZurueckActionPerformed(e));

            //---- field_countMax ----
            field_countMax.setEditable(false);
            field_countMax.setText("000");
            field_countMax.setEnabled(false);
            field_countMax.setFocusable(false);
            field_countMax.setMaximumSize(new Dimension(30, 20));
            field_countMax.setMinimumSize(new Dimension(30, 20));

            //---- Anfang ----
            Anfang.setText("<<");
            Anfang.setToolTipText("Gehe zum Anfang - erster Datensatz");
            Anfang.setMaximumSize(new Dimension(20, 23));
            Anfang.setMinimumSize(new Dimension(20, 23));
            Anfang.setPreferredSize(new Dimension(50, 23));
            Anfang.addActionListener(e -> AnfangActionPerformed(e));

            //---- jLabelID ----
            jLabelID.setText("ID");

            //---- Vor ----
            Vor.setText(">");
            Vor.setToolTipText("Gehe zum n\u00e4chsten Datensatz");
            Vor.setMaximumSize(new Dimension(20, 23));
            Vor.setMinimumSize(new Dimension(20, 23));
            Vor.setPreferredSize(new Dimension(50, 23));
            Vor.addActionListener(e -> VorActionPerformed(e));

            //---- field_ID ----
            field_ID.setEditable(false);
            field_ID.setText("0000");
            field_ID.setEnabled(false);
            field_ID.setFocusable(false);
            field_ID.setMaximumSize(new Dimension(30, 20));
            field_ID.setMinimumSize(new Dimension(30, 20));

            //---- Ende ----
            Ende.setText(">>");
            Ende.setToolTipText("Gehe zum Ende - zum letzten Datensatz");
            Ende.setMaximumSize(new Dimension(20, 23));
            Ende.setMinimumSize(new Dimension(20, 23));
            Ende.setPreferredSize(new Dimension(50, 23));
            Ende.addActionListener(e -> EndeActionPerformed(e));

            //---- Update ----
            Update.setText("!");
            Update.setToolTipText("Datensatz aktualisieren");
            Update.setMaximumSize(new Dimension(20, 23));
            Update.setMinimumSize(new Dimension(20, 23));
            Update.setPreferredSize(new Dimension(50, 23));
            Update.addActionListener(e -> UpdateActionPerformed(e));

            //---- jLabel3 ----
            jLabel3.setText("Mailverteiler");

            //---- Einfuegen ----
            Einfuegen.setText("+");
            Einfuegen.setToolTipText("Datensatz einf\u00fcgen");
            Einfuegen.setMaximumSize(new Dimension(20, 23));
            Einfuegen.setMinimumSize(new Dimension(20, 23));
            Einfuegen.setPreferredSize(new Dimension(50, 23));
            Einfuegen.addActionListener(e -> EinfuegenActionPerformed(e));

            //---- rbAdressDB ----
            rbAdressDB.setText("Adress-DB");

            //---- rbManuell ----
            rbManuell.setSelected(true);
            rbManuell.setText("manuell");

            //---- field_Adresse ----
            field_Adresse.setModel(new DefaultComboBoxModel<>(new String[] {

            }));
            field_Adresse.setPreferredSize(new Dimension(180, 25));
            field_Adresse.addActionListener(e -> field_AdresseActionPerformed(e));

            //---- field_Verteiler ----
            field_Verteiler.setModel(new DefaultComboBoxModel<>(new String[] {

            }));
            field_Verteiler.setPreferredSize(new Dimension(180, 25));

            //---- field_Beschreibung ----
            field_Beschreibung.setPreferredSize(new Dimension(263, 25));

            GroupLayout panel1Layout = new GroupLayout(panel1);
            panel1.setLayout(panel1Layout);
            panel1Layout.setHorizontalGroup(
                panel1Layout.createParallelGroup()
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(jLabelTitel)
                                .addGap(256, 256, 256)
                                .addComponent(jLabelID)
                                .addGap(4, 4, 4)
                                .addComponent(field_ID, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(26, 26, 26)
                                .addComponent(jLabel1)
                                .addGap(4, 4, 4)
                                .addComponent(field_count, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
                                .addGap(4, 4, 4)
                                .addComponent(jLabel2)
                                .addGap(4, 4, 4)
                                .addComponent(field_countMax, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(129, 129, 129)
                                .addComponent(rbAdressDB)
                                .addGap(117, 117, 117)
                                .addComponent(rbManuell))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(field_Verteiler, GroupLayout.PREFERRED_SIZE, 183, GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)
                                .addComponent(field_Adresse, GroupLayout.PREFERRED_SIZE, 183, GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addComponent(field_Beschreibung, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(Anfang, GroupLayout.PREFERRED_SIZE, 49, GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)
                                .addComponent(Zurueck, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(5, 5, 5)
                                .addComponent(Vor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(5, 5, 5)
                                .addComponent(Ende, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(20, 20, 20)
                                .addComponent(Update, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(5, 5, 5)
                                .addComponent(Einfuegen, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(5, 5, 5)
                                .addComponent(Loeschen, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(20, 20, 20)
                                .addComponent(Suchen, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(5, 5, 5)
                                .addComponent(WSuchen, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(20, 20, 20)
                                .addComponent(Drucken, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(5, 5, 5)
                                .addComponent(Schliessen, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(18, Short.MAX_VALUE))
            );
            panel1Layout.setVerticalGroup(
                panel1Layout.createParallelGroup()
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addComponent(jLabelTitel))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addComponent(jLabelID))
                            .addComponent(field_ID, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addComponent(jLabel1))
                            .addComponent(field_count, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addComponent(jLabel2))
                            .addComponent(field_countMax, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addComponent(jLabel3))
                            .addComponent(rbAdressDB)
                            .addComponent(rbManuell))
                        .addGap(2, 2, 2)
                        .addGroup(panel1Layout.createParallelGroup()
                            .addComponent(field_Beschreibung, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addGroup(panel1Layout.createParallelGroup()
                                    .addComponent(field_Verteiler, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(field_Adresse, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
                        .addGap(44, 44, 44)
                        .addGroup(panel1Layout.createParallelGroup()
                            .addComponent(Anfang, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(Zurueck, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(Vor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(Ende, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(Update, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(Einfuegen, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(Loeschen, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(Suchen, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(WSuchen, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(Drucken, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(Schliessen, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(20, Short.MAX_VALUE))
            );
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addComponent(panel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addComponent(panel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pack();
        setLocationRelativeTo(getOwner());
    }// </editor-fold>//GEN-END:initComponents

    private void LoeschenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LoeschenActionPerformed
        // TODO add your handling code here:
        if (JOptionPane.showConfirmDialog(null, "Soll der Datensatz wirklich gelöscht werden?") == JOptionPane.YES_OPTION) {
            try {
                result.deleteRow();
                countMax = countMax - 1;
                field_countMax.setText(Integer.toString(countMax));
                count = 1;
                field_count.setText(Integer.toString(count));

                result.first();
                if (countMax > 0) {
                    field_ID.setText(Integer.toString(result.getInt("MAILADRESSE_ID")));
                    field_Verteiler.setSelectedIndex(result.getInt("MAILADRESSE_VERTEILER"));
                    field_Beschreibung.setText(result.getString("MAILADRESSE_ADRESSE"));
                    // Schalterzustand anpassen
                    Anfang.setEnabled(true);
                    Zurueck.setEnabled(true);
                    Vor.setEnabled(true);
                    Ende.setEnabled(true);
                    Update.setEnabled(true);
                    Einfuegen.setEnabled(true);
                    Loeschen.setEnabled(true);
                    Suchen.setEnabled(true);
                    WSuchen.setEnabled(true);
                    Drucken.setEnabled(true);
                    Schliessen.setEnabled(true);
                } else {
                    resultIsEmpty = true;
                    field_ID.setText("");
                    field_Beschreibung.setText("");
                    // Schalterzustand anpassen
                    Anfang.setEnabled(false);
                    Zurueck.setEnabled(false);
                    Vor.setEnabled(false);
                    Ende.setEnabled(false);
                    Update.setEnabled(false);
                    Einfuegen.setEnabled(true);
                    Loeschen.setEnabled(false);
                    Suchen.setEnabled(false);
                    WSuchen.setEnabled(false);
                    Drucken.setEnabled(false);
                    Schliessen.setEnabled(true);
                }
            } catch (SQLException exept) {
                Modulhelferlein.Fehlermeldung("SQL-Exception: " + exept.getMessage());
            }
        }
    }//GEN-LAST:event_LoeschenActionPerformed

    private void SuchenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SuchenActionPerformed
        // TODO add your handling code here:
        boolean gefunden = false;
        try {
            do {
                result.first();
                count = 1;
                field_count.setText(Integer.toString(count));
                if (result.getString("MAILADRESSE_ADRESSE").contains(field_Beschreibung.getText())) {
                    gefunden = true;
                } else {
                    count = count + 1;
                }
            } while ((!gefunden) && result.next());
            if (gefunden) {
                Anfang.setEnabled(true);
                Zurueck.setEnabled(true);
                Vor.setEnabled(true);
                Ende.setEnabled(true);
                Update.setEnabled(true);
                Einfuegen.setEnabled(true);
                Loeschen.setEnabled(true);
                Suchen.setEnabled(true);
                WSuchen.setEnabled(true);
                Drucken.setEnabled(true);
                Schliessen.setEnabled(true);

                field_count.setText(Integer.toString(count));

                field_ID.setText(Integer.toString(result.getInt("MAILVERTEILER_ID")));
                field_Beschreibung.setText(result.getString("MAILVERTEILER_NAME"));
            } else {
                Modulhelferlein.Infomeldung("Datensatz wurde nicht gefunden!");
                AnfangActionPerformed(evt);
            }
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: " + exept.getMessage());
            Logger.getLogger(VerwaltenDatenbankEinnahmen.class.getName()).log(Level.SEVERE, null, exept);
        }

    }//GEN-LAST:event_SuchenActionPerformed

    private void WSuchenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_WSuchenActionPerformed
        // TODO add your handling code here:
        boolean gefunden = false;

        try {
            do {
                if (result.getString("MAILADRESSE_ADRESSE").contains(field_Beschreibung.getText())) {
                    gefunden = true;
                } else {
                    count = count + 1;
                }
            } while ((!gefunden) && result.next());
            if (gefunden) {
                Anfang.setEnabled(true);
                Zurueck.setEnabled(true);
                Vor.setEnabled(true);
                Ende.setEnabled(true);
                Update.setEnabled(true);
                Einfuegen.setEnabled(true);
                Loeschen.setEnabled(true);
                Suchen.setEnabled(true);
                WSuchen.setEnabled(true);
                Drucken.setEnabled(true);
                Schliessen.setEnabled(true);

                field_count.setText(Integer.toString(count));

                field_ID.setText(Integer.toString(result.getInt("MAILVERTEILER_ID")));
                field_Beschreibung.setText(result.getString("MAILVERTEILER_NAME"));
            } else {
                Modulhelferlein.Infomeldung("Datensatz wurde nicht gefunden!");
                AnfangActionPerformed(evt);
            }
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: " + exept.getMessage());
            Logger.getLogger(VerwaltenDatenbankEinnahmen.class.getName()).log(Level.SEVERE, null, exept);
        }
    }//GEN-LAST:event_WSuchenActionPerformed

    private void DruckenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DruckenActionPerformed
        // TODO add your handling code here:
        berMailVerteiler.bericht();
    }//GEN-LAST:event_DruckenActionPerformed

    private void SchliessenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SchliessenActionPerformed
        // TODO add your handling code here:
        try {
            result.close();
            SQLAnfrage.close();
            conn.close();
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: " + exept.getMessage());
        }
        this.dispose();
    }//GEN-LAST:event_SchliessenActionPerformed

    private void ZurueckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ZurueckActionPerformed
        // TODO add your handling code here:
        try {
            if (result.previous()) {
                count = count - 1;
                field_count.setText(Integer.toString(count));
                // Schalterzustand anpassen
                if (count > 1) {
                    Anfang.setEnabled(true);
                    Zurueck.setEnabled(true);
                } else {
                    Anfang.setEnabled(false);
                    Zurueck.setEnabled(false);
                }
                Vor.setEnabled(true);
                Ende.setEnabled(true);
                Update.setEnabled(true);
                Einfuegen.setEnabled(true);
                Loeschen.setEnabled(true);
                Suchen.setEnabled(true);
                WSuchen.setEnabled(true);
                Drucken.setEnabled(true);
                Schliessen.setEnabled(true);
            } else {
                result.next();
            }
            field_ID.setText(Integer.toString(result.getInt("MAILADRESSE_ID")));
            field_Verteiler.setSelectedIndex(result.getInt("MAILADRESSE_VERTEILER"));
            field_Beschreibung.setText(result.getString("MAILADRESSE_ADRESSE"));
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: " + exept.getMessage());
        }
    }//GEN-LAST:event_ZurueckActionPerformed

    private void AnfangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AnfangActionPerformed
        // TODO add your handling code here:
        try {
            result.first();
            count = 1;
            field_count.setText(Integer.toString(count));

            // Schalterzustand anpassen
            Anfang.setEnabled(false);
            Zurueck.setEnabled(false);
            Vor.setEnabled(true);
            Ende.setEnabled(true);
            Update.setEnabled(true);
            Einfuegen.setEnabled(true);
            Loeschen.setEnabled(true);
            Suchen.setEnabled(true);
            WSuchen.setEnabled(true);
            Drucken.setEnabled(true);
            Schliessen.setEnabled(true);

            field_ID.setText(Integer.toString(result.getInt("MAILADRESSE_ID")));
            field_Verteiler.setSelectedIndex(result.getInt("MAILADRESSE_VERTEILER"));
            field_Beschreibung.setText(result.getString("MAILADRESSE_ADRESSE"));
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: " + exept.getMessage());
        }
    }//GEN-LAST:event_AnfangActionPerformed

    private void VorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_VorActionPerformed
        // TODO add your handling code here:
        try {
            if (result.next()) {
                count = count + 1;
                field_count.setText(Integer.toString(count));
                // Schalterzustand anpassen
                if (count < countMax) {
                    Ende.setEnabled(true);
                    Vor.setEnabled(true);
                } else {
                    Ende.setEnabled(false);
                    Vor.setEnabled(false);
                }
                Anfang.setEnabled(true);
                Zurueck.setEnabled(true);
                Update.setEnabled(true);
                Einfuegen.setEnabled(true);
                Loeschen.setEnabled(true);
                Suchen.setEnabled(true);
                WSuchen.setEnabled(true);
                Drucken.setEnabled(true);
                Schliessen.setEnabled(true);
            } else {
                result.previous();
                // Schalterzustände anpassen
                Anfang.setEnabled(true);
                Zurueck.setEnabled(true);
                Vor.setEnabled(false);
                Ende.setEnabled(false);
                Update.setEnabled(true);
                Einfuegen.setEnabled(true);
                Loeschen.setEnabled(true);
                Suchen.setEnabled(true);
                WSuchen.setEnabled(true);
                Drucken.setEnabled(true);
                Schliessen.setEnabled(true);
            }
            field_ID.setText(Integer.toString(result.getInt("MAILADRESSE_ID")));
            field_Verteiler.setSelectedIndex(result.getInt("MAILADRESSE_VERTEILER"));
            field_Beschreibung.setText(result.getString("MAILADRESSE_ADRESSE"));
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: " + exept.getMessage());
        }
    }//GEN-LAST:event_VorActionPerformed

    private void EndeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EndeActionPerformed
        // TODO add your handling code here:
        try {
            result.last();
            count = countMax;
            field_count.setText(Integer.toString(count));

            // Schalterzustand anpassen
            Anfang.setEnabled(true);
            Zurueck.setEnabled(true);
            Vor.setEnabled(false);
            Ende.setEnabled(false);
            Update.setEnabled(true);
            Einfuegen.setEnabled(true);
            Loeschen.setEnabled(true);
            Suchen.setEnabled(true);
            WSuchen.setEnabled(true);
            Drucken.setEnabled(true);
            Schliessen.setEnabled(true);

            field_ID.setText(Integer.toString(result.getInt("MAILADRESSE_ID")));
            field_Verteiler.setSelectedIndex(result.getInt("MAILADRESSE_VERTEILER"));
            field_Beschreibung.setText(result.getString("MAILADRESSE_ADRESSE"));
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: " + exept.getMessage());
        }
    }//GEN-LAST:event_EndeActionPerformed

    private void UpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UpdateActionPerformed
        // TODO add your handling code here:
        try {
            if (resultIsEmpty) {
                Modulhelferlein.Fehlermeldung("Die Datenbank ist leer - bitte Datensatz einfügen!");
            } else {
                result.updateInt("MAILADRESSE_VERTEILER", field_Verteiler.getSelectedIndex());
                result.updateString("MAILADRESSE_ADRESSE", field_Beschreibung.getText());
                result.updateRow();
            }
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: " + exept.getMessage());
        }
    }//GEN-LAST:event_UpdateActionPerformed

    private void EinfuegenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EinfuegenActionPerformed
        // TODO add your handling code here:
        try {
            maxID = maxID + 1;
            result.moveToInsertRow();
            result.updateInt("MAILADRESSE_ID", maxID);
            result.updateInt("MAILADRESSE_VERTEILER", 0);
            result.updateString("MAILADRESSE_ADRESSE", "");
            result.insertRow();
            countMax = countMax + 1;
            field_countMax.setText(Integer.toString(countMax));
            count = countMax;
            field_count.setText(Integer.toString(count));

            // Schalterzustand anpassen
            Anfang.setEnabled(true);
            Zurueck.setEnabled(true);
            Vor.setEnabled(false);
            Ende.setEnabled(false);
            Update.setEnabled(true);
            Einfuegen.setEnabled(true);
            Loeschen.setEnabled(true);
            Suchen.setEnabled(true);
            WSuchen.setEnabled(true);
            Drucken.setEnabled(true);
            Schliessen.setEnabled(true);

            resultIsEmpty = false;
            result.last();

            field_ID.setText(Integer.toString(result.getInt("MAILADRESSE_ID")));
            field_Verteiler.setSelectedIndex(result.getInt("MAILADRESSE_VERTEILER"));
            field_Beschreibung.setText(result.getString("MAILADRESSE_ADRESSE"));

        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: " + exept.getMessage());
        }
    }//GEN-LAST:event_EinfuegenActionPerformed

    private void field_AdresseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_field_AdresseActionPerformed
        // TODO add your handling code here:
        String Auswahl = (String) field_Adresse.getSelectedItem();
        String[] Element = Auswahl.split(",");
        field_Beschreibung.setText(Element[2]);
    }//GEN-LAST:event_field_AdresseActionPerformed

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
            VerwaltenDatenbankMailadressen dialog = new VerwaltenDatenbankMailadressen(new javax.swing.JFrame(), true);
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
    private JButton Loeschen;
    private JButton Suchen;
    private JLabel jLabelTitel;
    private JButton WSuchen;
    private JLabel jLabel1;
    private JButton Drucken;
    private JTextField field_count;
    private JButton Schliessen;
    private JLabel jLabel2;
    private JButton Zurueck;
    private JTextField field_countMax;
    private JButton Anfang;
    private JLabel jLabelID;
    private JButton Vor;
    private JTextField field_ID;
    private JButton Ende;
    private JButton Update;
    private JLabel jLabel3;
    private JButton Einfuegen;
    private JRadioButton rbAdressDB;
    private JRadioButton rbManuell;
    private JComboBox<String> field_Adresse;
    private JComboBox<String> field_Verteiler;
    private JTextField field_Beschreibung;
    // End of variables declaration//GEN-END:variables

    Integer count = 0;
    Integer countMax = 0;
    Integer maxID = 0;

    Connection conn;
    Statement SQLAnfrage;
    Statement SQLAnfrageVerteiler;
    Statement SQLAnfrageAdressse;

    ResultSet result;
    ResultSet resultVerteiler;
    ResultSet resultAdresse;

    boolean resultIsEmpty = true;

    String Kriterium = "";

    ButtonGroup buttonGroupAdresse = new ButtonGroup();
}
