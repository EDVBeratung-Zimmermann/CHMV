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
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.plaf.ActionMapUIResource;
import net.miginfocom.swing.*;

/**
 *
 * @author Thomas Zimmermann
 */
public class VerwaltenDatenbankRezensionen extends javax.swing.JDialog {

    /**
     * Creates new form VerwaltenDatenbankRezensionen
     *
     * @param parent
     * @param modal
     */
    public VerwaltenDatenbankRezensionen(java.awt.Frame parent, boolean modal) {
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
        
        conn = null;

        try { // Datenbank-Treiber laden
            Class.forName(Modulhelferlein.dbDriver);
        } catch (ClassNotFoundException exept) {
            Modulhelferlein.Fehlermeldung(
                    "ClassNotFoundException: Treiber nicht gefunden: "
                    + exept.getMessage());
        } // try Datenbank-Treiber laden

        try { // Verbindung zur Datenbank über die JDBC-Brücke
            conn = DriverManager.getConnection(Modulhelferlein.dbUrl, Modulhelferlein.dbUser, Modulhelferlein.dbPassword);
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung(
                    "SQL-Excetion: Verbindung nicht moeglich: "
                    + exept.getMessage());
        } // Verbindung zur Datenbank über die JDBC-Brücke

        if (conn != null) {
            // Anfrage zur Erstellung der Zeitschriftenliste erzeugen        	
            SQLAnfrageZeitschrift = null;
            try {
                SQLAnfrageZeitschrift = conn.createStatement(
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE); // Anfrage der DB conn2 zuordnen
                resultZeitschrift = SQLAnfrageZeitschrift.executeQuery("SELECT * FROM TBL_ADRESSE WHERE ADRESSEN_TYP = 'Zeitschrift' ORDER BY ADRESSEN_NAME");

                // gehe zum ersten Datensatz - wenn nicht leer
                while (resultZeitschrift.next()) {
                    field_Zeitschrift.addItem(Integer.toString(resultZeitschrift.getInt("ADRESSEN_ID"))
                            + ", " + resultZeitschrift.getString("ADRESSEN_ZEITSCHRIFT"));
                } // while
            } catch (SQLException exept) {
                Modulhelferlein.Fehlermeldung("SQL-Exception: SQL-Anfrage nicht moeglich: " + exept.getMessage());
            } // try

            // Anfrage zur Erstellung der Rezensentenliste erzeugen        	
            SQLAnfrageRezensent = null;
            try {
                SQLAnfrageRezensent = conn.createStatement(
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE); // Anfrage der DB conn2 zuordnen
                resultRezensent = SQLAnfrageRezensent.executeQuery("SELECT * FROM TBL_ADRESSE WHERE ADRESSEN_TYP = 'Rezensent' ORDER BY ADRESSEN_NAME");

                // gehe zum ersten Datensatz - wenn nicht leer
                while (resultRezensent.next()) {
                    field_Rezensent.addItem(Integer.toString(resultRezensent.getInt("ADRESSEN_ID"))
                            + ", " + resultRezensent.getString("ADRESSEN_NAME")
                            + ", " + resultRezensent.getString("ADRESSEN_VORNAME"));
                } // while
            } catch (SQLException exept) {
                Modulhelferlein.Fehlermeldung("SQL-Exception: SQL-Anfrage nicht moeglich: " + exept.getMessage());
            } // try

            // Anfrage zur Erstellung einer Auswahlliste der Bücher erzeugen            
            SQLAnfrageBuch = null;
            try {
                SQLAnfrageBuch = conn.createStatement(
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE); // Anfrage der DB conn2 zuordnen
                resultBuch = SQLAnfrageBuch.executeQuery("SELECT * FROM TBL_BUCH ORDER BY BUCH_ISBN");

                // gehe zum ersten Datensatz - wenn nicht leer
                while (resultBuch.next()) {
                    field_Buch.addItem(Integer.toString(resultBuch.getInt("BUCH_ID"))
                            + ", " + resultBuch.getString("BUCH_ISBN")
                            + ", " + resultBuch.getString("BUCH_TITEL"));
                } // while
            } catch (SQLException exept) {
                Modulhelferlein.Fehlermeldung("SQL-Exception: SQL-Anfrage nicht moeglich: " + exept.getMessage());
            } // try

            SQLAnfrage = null;

            try { // Anfrage erzeugen
                SQLAnfrage = conn.createStatement(
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE); // Anfrage der DB conn2 zuordnen
                result = SQLAnfrage.executeQuery("SELECT * FROM TBL_REZENSION ORDER BY REZENSION_ID DESC"); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 
                if (result.next()) {
                    maxID = result.getInt("REZENSION_ID");
                } else {
                    maxID = 0;
                }
                result = SQLAnfrage.executeQuery("SELECT * FROM TBL_REZENSION"); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 
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
                    resultIsEmpty = false;
                    count = 1;
                    field_count.setText(Integer.toString(count));
                    field_ID.setText(Integer.toString(result.getInt("REZENSION_ID")));
                    field_Datei.setText(result.getString("REZENSION_DATEI"));
                    field_Anmerkung.setText(result.getString("REZENSION_ANMERKUNG"));
                    BuchRezZeit = result.getInt("REZENSION_BUCH");
                    for (int i = 0; i < field_Buch.getItemCount(); i++) {
                        String s = (String) field_Buch.getItemAt(i);
                        String sSplit[] = s.split(",");
                        if (Integer.parseInt(sSplit[0]) == BuchRezZeit) {
                            field_Buch.setSelectedIndex(i);
                        } // if
                    } // for
                    BuchRezZeit = result.getInt("REZENSION_ZEITSCHRIFT");
                    for (int i = 0; i < field_Zeitschrift.getItemCount(); i++) {
                        String s = (String) field_Zeitschrift.getItemAt(i);
                        String sSplit[] = s.split(",");
                        if (Integer.parseInt(sSplit[0]) == BuchRezZeit) {
                            field_Zeitschrift.setSelectedIndex(i);
                        } // if
                    } // for
                    BuchRezZeit = result.getInt("REZENSION_REZENSENT");
                    for (int i = 0; i < field_Rezensent.getItemCount(); i++) {
                        String s = (String) field_Rezensent.getItemAt(i);
                        String sSplit[] = s.split(",");
                        if (Integer.parseInt(sSplit[0]) == BuchRezZeit) {
                            field_Rezensent.setSelectedIndex(i);
                        } // if
                    } // for

                    // Schalterzustände setzen   
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
                } // if
            } catch (SQLException exept) {
                Modulhelferlein.Fehlermeldung("SQL-Exception: SQL-Anfrage nicht moeglich: "
                        + exept.getMessage());
            } // try // Anfrage erzeugen
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
        jLabelID = new JLabel();
        field_ID = new JTextField();
        jLabel7 = new JLabel();
        field_count = new JTextField();
        jLabel8 = new JLabel();
        field_countMax = new JTextField();
        field_Zeitschrift = new JComboBox<>();
        jLabel4 = new JLabel();
        field_Buch = new JComboBox<>();
        jLabel5 = new JLabel();
        RezensionAdd = new JButton();
        field_Datei = new JTextField();
        jLabel6 = new JLabel();
        field_Anmerkung = new JTextField();
        jLabel2 = new JLabel();
        jLabel3 = new JLabel();
        field_Rezensent = new JComboBox<>();
        Anfang = new JButton();
        Zurueck = new JButton();
        Vor = new JButton();
        Ende = new JButton();
        Update = new JButton();
        Einfuegen = new JButton();
        Loeschen = new JButton();
        Suchen = new JButton();
        WSuchen = new JButton();
        Drucken = new JButton();
        Schliessen = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Carola Hartmann Miles Verlag");
        setResizable(false);
        setFont(new Font(Font.DIALOG, Font.BOLD, 12));
        setMinimumSize(new Dimension(630, 345));
        var contentPane = getContentPane();

        //======== panel1 ========
        {
            panel1.setLayout(null);

            //---- jLabel1 ----
            jLabel1.setFont(new Font("Tahoma", Font.BOLD, 12));
            jLabel1.setText("Erfassen der Rezensionen");
            panel1.add(jLabel1);
            jLabel1.setBounds(0, 0, 600, jLabel1.getPreferredSize().height);

            //---- jLabelID ----
            jLabelID.setText("ID");
            jLabelID.setHorizontalAlignment(SwingConstants.RIGHT);
            panel1.add(jLabelID);
            jLabelID.setBounds(220, 23, 50, jLabelID.getPreferredSize().height);

            //---- field_ID ----
            field_ID.setEditable(false);
            field_ID.setText("0000");
            field_ID.setEnabled(false);
            field_ID.setFocusable(false);
            field_ID.setMaximumSize(new Dimension(30, 20));
            field_ID.setMinimumSize(new Dimension(30, 20));
            field_ID.setPreferredSize(null);
            panel1.add(field_ID);
            field_ID.setBounds(new Rectangle(new Point(275, 20), field_ID.getPreferredSize()));

            //---- jLabel7 ----
            jLabel7.setText("Datensatz Nr.");
            jLabel7.setHorizontalAlignment(SwingConstants.RIGHT);
            panel1.add(jLabel7);
            jLabel7.setBounds(330, 23, 105, jLabel7.getPreferredSize().height);

            //---- field_count ----
            field_count.setEditable(false);
            field_count.setText("000");
            field_count.setEnabled(false);
            field_count.setFocusable(false);
            field_count.setMaximumSize(new Dimension(30, 20));
            field_count.setMinimumSize(new Dimension(30, 20));
            field_count.setPreferredSize(null);
            panel1.add(field_count);
            field_count.setBounds(440, 20, 30, field_count.getPreferredSize().height);

            //---- jLabel8 ----
            jLabel8.setText("von");
            jLabel8.setHorizontalAlignment(SwingConstants.CENTER);
            panel1.add(jLabel8);
            jLabel8.setBounds(495, 23, 50, jLabel8.getPreferredSize().height);

            //---- field_countMax ----
            field_countMax.setEditable(false);
            field_countMax.setText("000");
            field_countMax.setEnabled(false);
            field_countMax.setFocusable(false);
            field_countMax.setMaximumSize(new Dimension(30, 20));
            field_countMax.setMinimumSize(new Dimension(30, 20));
            field_countMax.setPreferredSize(null);
            panel1.add(field_countMax);
            field_countMax.setBounds(550, 20, 30, field_countMax.getPreferredSize().height);

            //---- field_Zeitschrift ----
            field_Zeitschrift.setModel(new DefaultComboBoxModel<>(new String[] {

            }));
            panel1.add(field_Zeitschrift);
            field_Zeitschrift.setBounds(0, 89, 325, field_Zeitschrift.getPreferredSize().height);

            //---- jLabel4 ----
            jLabel4.setText("Buch");
            panel1.add(jLabel4);
            jLabel4.setBounds(0, 114, 600, jLabel4.getPreferredSize().height);

            //---- field_Buch ----
            field_Buch.setModel(new DefaultComboBoxModel<>(new String[] {

            }));
            panel1.add(field_Buch);
            field_Buch.setBounds(0, 133, 600, field_Buch.getPreferredSize().height);

            //---- jLabel5 ----
            jLabel5.setText("Rezension");
            panel1.add(jLabel5);
            jLabel5.setBounds(0, 158, 600, jLabel5.getPreferredSize().height);

            //---- RezensionAdd ----
            RezensionAdd.setText("...");
            RezensionAdd.addActionListener(e -> RezensionAddActionPerformed(e));
            panel1.add(RezensionAdd);
            RezensionAdd.setBounds(0, 177, 50, RezensionAdd.getPreferredSize().height);
            panel1.add(field_Datei);
            field_Datei.setBounds(55, 177, 545, 23);

            //---- jLabel6 ----
            jLabel6.setText("Anmerkung");
            panel1.add(jLabel6);
            jLabel6.setBounds(0, 205, 600, jLabel6.getPreferredSize().height);
            panel1.add(field_Anmerkung);
            field_Anmerkung.setBounds(0, 224, 600, field_Anmerkung.getPreferredSize().height);

            //---- jLabel2 ----
            jLabel2.setText("Zeitschrift");
            panel1.add(jLabel2);
            jLabel2.setBounds(0, 70, 325, jLabel2.getPreferredSize().height);

            //---- jLabel3 ----
            jLabel3.setText("Rezensent");
            panel1.add(jLabel3);
            jLabel3.setBounds(330, 70, 270, jLabel3.getPreferredSize().height);

            //---- field_Rezensent ----
            field_Rezensent.setModel(new DefaultComboBoxModel<>(new String[] {

            }));
            panel1.add(field_Rezensent);
            field_Rezensent.setBounds(330, 89, 270, field_Rezensent.getPreferredSize().height);

            //---- Anfang ----
            Anfang.setText("<<");
            Anfang.setToolTipText("Gehe zum Anfang - erster Datensatz");
            Anfang.setMaximumSize(new Dimension(50, 25));
            Anfang.setMinimumSize(new Dimension(50, 25));
            Anfang.setPreferredSize(new Dimension(50, 25));
            Anfang.addActionListener(e -> AnfangActionPerformed(e));
            panel1.add(Anfang);
            Anfang.setBounds(new Rectangle(new Point(0, 264), Anfang.getPreferredSize()));

            //---- Zurueck ----
            Zurueck.setText("<");
            Zurueck.setToolTipText("Gehe zum vorherigen Datensatz");
            Zurueck.setMaximumSize(new Dimension(50, 25));
            Zurueck.setMinimumSize(new Dimension(50, 25));
            Zurueck.setPreferredSize(new Dimension(50, 25));
            Zurueck.addActionListener(e -> ZurueckActionPerformed(e));
            panel1.add(Zurueck);
            Zurueck.setBounds(55, 264, 50, Zurueck.getPreferredSize().height);

            //---- Vor ----
            Vor.setText(">");
            Vor.setToolTipText("Gehe zum n\u00e4chsten Datensatz");
            Vor.setMaximumSize(new Dimension(50, 25));
            Vor.setMinimumSize(new Dimension(50, 25));
            Vor.setPreferredSize(new Dimension(50, 25));
            Vor.addActionListener(e -> VorActionPerformed(e));
            panel1.add(Vor);
            Vor.setBounds(110, 264, 50, Vor.getPreferredSize().height);

            //---- Ende ----
            Ende.setText(">>");
            Ende.setMaximumSize(new Dimension(50, 25));
            Ende.setMinimumSize(new Dimension(50, 25));
            Ende.setPreferredSize(new Dimension(50, 25));
            Ende.addActionListener(e -> EndeActionPerformed(e));
            panel1.add(Ende);
            Ende.setBounds(165, 264, 50, Ende.getPreferredSize().height);

            //---- Update ----
            Update.setText("!");
            Update.setToolTipText("Datensatz aktualisieren");
            Update.setMaximumSize(new Dimension(50, 25));
            Update.setMinimumSize(new Dimension(50, 25));
            Update.setPreferredSize(new Dimension(50, 25));
            Update.addActionListener(e -> UpdateActionPerformed(e));
            panel1.add(Update);
            Update.setBounds(220, 264, 50, Update.getPreferredSize().height);

            //---- Einfuegen ----
            Einfuegen.setText("+");
            Einfuegen.setToolTipText("Datensatz einf\u00fcgen");
            Einfuegen.setMaximumSize(new Dimension(50, 25));
            Einfuegen.setMinimumSize(new Dimension(50, 25));
            Einfuegen.setPreferredSize(new Dimension(50, 25));
            Einfuegen.addActionListener(e -> EinfuegenActionPerformed(e));
            panel1.add(Einfuegen);
            Einfuegen.setBounds(275, 264, 50, Einfuegen.getPreferredSize().height);

            //---- Loeschen ----
            Loeschen.setText("-");
            Loeschen.setToolTipText("Datensatz l\u00f6schen");
            Loeschen.setMaximumSize(new Dimension(50, 25));
            Loeschen.setMinimumSize(new Dimension(50, 25));
            Loeschen.setPreferredSize(new Dimension(50, 25));
            Loeschen.addActionListener(e -> LoeschenActionPerformed(e));
            panel1.add(Loeschen);
            Loeschen.setBounds(330, 264, 50, Loeschen.getPreferredSize().height);

            //---- Suchen ----
            Suchen.setText("?");
            Suchen.setToolTipText("Datensatz suchen");
            Suchen.setMaximumSize(new Dimension(50, 25));
            Suchen.setMinimumSize(new Dimension(50, 25));
            Suchen.setPreferredSize(new Dimension(50, 25));
            Suchen.addActionListener(e -> SuchenActionPerformed(e));
            panel1.add(Suchen);
            Suchen.setBounds(385, 264, 50, Suchen.getPreferredSize().height);

            //---- WSuchen ----
            WSuchen.setText("...");
            WSuchen.setToolTipText("Weitersuchen");
            WSuchen.setMaximumSize(new Dimension(50, 25));
            WSuchen.setMinimumSize(new Dimension(50, 25));
            WSuchen.setPreferredSize(new Dimension(50, 25));
            WSuchen.addActionListener(e -> WSuchenActionPerformed(e));
            panel1.add(WSuchen);
            WSuchen.setBounds(440, 264, 50, WSuchen.getPreferredSize().height);

            //---- Drucken ----
            Drucken.setText("D");
            Drucken.setToolTipText("Liste der Einnahmen drucken");
            Drucken.setMaximumSize(new Dimension(50, 25));
            Drucken.setMinimumSize(new Dimension(50, 25));
            Drucken.setPreferredSize(new Dimension(50, 25));
            Drucken.addActionListener(e -> DruckenActionPerformed(e));
            panel1.add(Drucken);
            Drucken.setBounds(495, 264, 50, Drucken.getPreferredSize().height);

            //---- Schliessen ----
            Schliessen.setText("X");
            Schliessen.setMaximumSize(new Dimension(50, 25));
            Schliessen.setMinimumSize(new Dimension(50, 25));
            Schliessen.setPreferredSize(new Dimension(50, 25));
            Schliessen.addActionListener(e -> SchliessenActionPerformed(e));
            panel1.add(Schliessen);
            Schliessen.setBounds(550, 264, 50, Schliessen.getPreferredSize().height);

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

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(panel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(13, Short.MAX_VALUE))
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(panel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(8, Short.MAX_VALUE))
        );
        setSize(625, 340);
        setLocationRelativeTo(getOwner());
    }// </editor-fold>//GEN-END:initComponents

    private void SuchenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SuchenActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_SuchenActionPerformed

    private void SchliessenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SchliessenActionPerformed
        // TODO add your handling code here:
        try {
            result.close();
            resultBuch.close();
            resultRezensent.close();
            resultZeitschrift.close();
            SQLAnfrage.close();
            SQLAnfrageBuch.close();
            SQLAnfrageRezensent.close();
            SQLAnfrageZeitschrift.close();
            conn.close();
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: " + exept.getMessage());
        }
        this.dispose();
    }//GEN-LAST:event_SchliessenActionPerformed

    private void DruckenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DruckenActionPerformed
        // TODO add your handling code here:
        _DlgRezensionen.main(null);
    }//GEN-LAST:event_DruckenActionPerformed

    private void WSuchenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_WSuchenActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_WSuchenActionPerformed

    private void LoeschenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LoeschenActionPerformed
        // TODO add your handling code here:
        if (JOptionPane.showConfirmDialog(null, "Soll der Datensatz wirklich gelöscht werden?") == JOptionPane.YES_OPTION) {
            try {
                result.deleteRow();
                countMax = countMax - 1;
                field_countMax.setText(Integer.toString(countMax));
                if (countMax == 0) {
                    count = 0;
                } else {
                    count = 1;
                }
                field_count.setText(Integer.toString(count));

                result.first();
                if (countMax > 0) {
                    field_ID.setText(Integer.toString(result.getInt("REZENSION_ID")));
                    field_Datei.setText(result.getString("REZENSION_DATEI"));
                    field_Anmerkung.setText(result.getString("REZENSION_ANMERKUNG"));
                    BuchRezZeit = result.getInt("REZENSION_BUCH");
                    for (int i = 0; i < field_Buch.getItemCount(); i++) {
                        String s = (String) field_Buch.getItemAt(i);
                        String sSplit[] = s.split(",");
                        if (Integer.parseInt(sSplit[0]) == BuchRezZeit) {
                            field_Buch.setSelectedIndex(i);
                        } // if
                    } // for
                    BuchRezZeit = result.getInt("REZENSION_ZEITSCHRIFT");
                    for (int i = 0; i < field_Zeitschrift.getItemCount(); i++) {
                        String s = (String) field_Zeitschrift.getItemAt(i);
                        String sSplit[] = s.split(",");
                        if (Integer.parseInt(sSplit[0]) == BuchRezZeit) {
                            field_Zeitschrift.setSelectedIndex(i);
                        } // if
                    } // for
                    BuchRezZeit = result.getInt("REZENSION_REZENSENT");
                    for (int i = 0; i < field_Rezensent.getItemCount(); i++) {
                        String s = (String) field_Rezensent.getItemAt(i);
                        String sSplit[] = s.split(",");
                        if (Integer.parseInt(sSplit[0]) == BuchRezZeit) {
                            field_Rezensent.setSelectedIndex(i);
                        } // if
                    } // for
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
                    field_Datei.setText("");
                    field_Anmerkung.setText("");

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

    private void EinfuegenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EinfuegenActionPerformed
        // TODO add your handling code here:
        try {
            maxID = maxID + 1;
            result.moveToInsertRow();
            result.updateInt("REZENSION_ID", maxID);
            result.updateInt("REZENSION_BUCH", 0);
            result.updateInt("REZENSION_REZENSENT", 0);
            result.updateInt("REZENSION_ZEITSCHRIFT", 0);
            result.updateString("REZENSION_DATEI", "");
            result.updateString("REZENSION_ANMERKUNG", "");
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
            field_ID.setText(Integer.toString(result.getInt("REZENSION_ID")));
            field_Datei.setText(result.getString("REZENSION_DATEI"));
            field_Anmerkung.setText(result.getString("REZENSION_ANMERKUNG"));
            BuchRezZeit = result.getInt("REZENSION_BUCH");
            for (int i = 0; i < field_Buch.getItemCount(); i++) {
                String s = (String) field_Buch.getItemAt(i);
                String sSplit[] = s.split(",");
                if (Integer.parseInt(sSplit[0]) == BuchRezZeit) {
                    field_Buch.setSelectedIndex(i);
                } // if
            } // for
            BuchRezZeit = result.getInt("REZENSION_ZEITSCHRIFT");
            for (int i = 0; i < field_Zeitschrift.getItemCount(); i++) {
                String s = (String) field_Zeitschrift.getItemAt(i);
                String sSplit[] = s.split(",");
                if (Integer.parseInt(sSplit[0]) == BuchRezZeit) {
                    field_Zeitschrift.setSelectedIndex(i);
                } // if
            } // for
            BuchRezZeit = result.getInt("REZENSION_REZENSENT");
            for (int i = 0; i < field_Rezensent.getItemCount(); i++) {
                String s = (String) field_Rezensent.getItemAt(i);
                String sSplit[] = s.split(",");
                if (Integer.parseInt(sSplit[0]) == BuchRezZeit) {
                    field_Rezensent.setSelectedIndex(i);
                } // if
            } // for
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: " + exept.getMessage());
        }
    }//GEN-LAST:event_EinfuegenActionPerformed

    private void UpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UpdateActionPerformed
        // TODO add your handling code here:
        try {
            String s = (String) field_Buch.getItemAt(field_Buch.getSelectedIndex());
            String sBSplit[] = s.split(",");
            BuchRezZeit = Integer.parseInt(sBSplit[0]);
            result.updateInt("REZENSION_BUCH", BuchRezZeit);

            s = (String) field_Rezensent.getItemAt(field_Rezensent.getSelectedIndex());
            String sRSplit[] = s.split(",");
            BuchRezZeit = Integer.parseInt(sRSplit[0]);
            result.updateInt("REZENSION_REZENSENT", BuchRezZeit);

            s = (String) field_Zeitschrift.getItemAt(field_Zeitschrift.getSelectedIndex());
            String sZSplit[] = s.split(",");
            BuchRezZeit = Integer.parseInt(sZSplit[0]);
            result.updateInt("REZENSION_ZEITSCHRIFT", BuchRezZeit);
            result.updateString("REZENSION_ANMERKUNG", field_Anmerkung.getText());
            result.updateString("REZENSION_DATEI", field_Datei.getText());
            result.updateRow();
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: " + exept.getMessage());
        }
    }//GEN-LAST:event_UpdateActionPerformed

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
            }
            field_ID.setText(Integer.toString(result.getInt("REZENSION_ID")));
            field_Datei.setText(result.getString("REZENSION_DATEI"));
            field_Anmerkung.setText(result.getString("REZENSION_ANMERKUNG"));
            BuchRezZeit = result.getInt("REZENSION_BUCH");
            for (int i = 0; i < field_Buch.getItemCount(); i++) {
                String s = (String) field_Buch.getItemAt(i);
                String sSplit[] = s.split(",");
                if (Integer.parseInt(sSplit[0]) == BuchRezZeit) {
                    field_Buch.setSelectedIndex(i);
                } // if
            } // for
            BuchRezZeit = result.getInt("REZENSION_ZEITSCHRIFT");
            for (int i = 0; i < field_Zeitschrift.getItemCount(); i++) {
                String s = (String) field_Zeitschrift.getItemAt(i);
                String sSplit[] = s.split(",");
                if (Integer.parseInt(sSplit[0]) == BuchRezZeit) {
                    field_Zeitschrift.setSelectedIndex(i);
                } // if
            } // for
            BuchRezZeit = result.getInt("REZENSION_REZENSENT");
            for (int i = 0; i < field_Rezensent.getItemCount(); i++) {
                String s = (String) field_Rezensent.getItemAt(i);
                String sSplit[] = s.split(",");
                if (Integer.parseInt(sSplit[0]) == BuchRezZeit) {
                    field_Rezensent.setSelectedIndex(i);
                } // if
            } // for
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: " + exept.getMessage());
        }
    }//GEN-LAST:event_VorActionPerformed

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

            field_ID.setText(Integer.toString(result.getInt("REZENSION_ID")));
            field_Datei.setText(result.getString("REZENSION_DATEI"));
            field_Anmerkung.setText(result.getString("REZENSION_ANMERKUNG"));
            BuchRezZeit = result.getInt("REZENSION_BUCH");
            for (int i = 0; i < field_Buch.getItemCount(); i++) {
                String s = (String) field_Buch.getItemAt(i);
                String sSplit[] = s.split(",");
                if (Integer.parseInt(sSplit[0]) == BuchRezZeit) {
                    field_Buch.setSelectedIndex(i);
                } // if
            } // for
            BuchRezZeit = result.getInt("REZENSION_ZEITSCHRIFT");
            for (int i = 0; i < field_Zeitschrift.getItemCount(); i++) {
                String s = (String) field_Zeitschrift.getItemAt(i);
                String sSplit[] = s.split(",");
                if (Integer.parseInt(sSplit[0]) == BuchRezZeit) {
                    field_Zeitschrift.setSelectedIndex(i);
                } // if
            } // for
            BuchRezZeit = result.getInt("REZENSION_REZENSENT");
            for (int i = 0; i < field_Rezensent.getItemCount(); i++) {
                String s = (String) field_Rezensent.getItemAt(i);
                String sSplit[] = s.split(",");
                if (Integer.parseInt(sSplit[0]) == BuchRezZeit) {
                    field_Rezensent.setSelectedIndex(i);
                } // if
            } // for
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: " + exept.getMessage());
        }
    }//GEN-LAST:event_AnfangActionPerformed

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
            field_ID.setText(Integer.toString(result.getInt("REZENSION_ID")));
            field_Datei.setText(result.getString("REZENSION_DATEI"));
            field_Anmerkung.setText(result.getString("REZENSION_ANMERKUNG"));
            BuchRezZeit = result.getInt("REZENSION_BUCH");
            for (int i = 0; i < field_Buch.getItemCount(); i++) {
                String s = (String) field_Buch.getItemAt(i);
                String sSplit[] = s.split(",");
                if (Integer.parseInt(sSplit[0]) == BuchRezZeit) {
                    field_Buch.setSelectedIndex(i);
                } // if
            } // for
            BuchRezZeit = result.getInt("REZENSION_ZEITSCHRIFT");
            for (int i = 0; i < field_Zeitschrift.getItemCount(); i++) {
                String s = (String) field_Zeitschrift.getItemAt(i);
                String sSplit[] = s.split(",");
                if (Integer.parseInt(sSplit[0]) == BuchRezZeit) {
                    field_Zeitschrift.setSelectedIndex(i);
                } // if
            } // for
            BuchRezZeit = result.getInt("REZENSION_REZENSENT");
            for (int i = 0; i < field_Rezensent.getItemCount(); i++) {
                String s = (String) field_Rezensent.getItemAt(i);
                String sSplit[] = s.split(",");
                if (Integer.parseInt(sSplit[0]) == BuchRezZeit) {
                    field_Rezensent.setSelectedIndex(i);
                } // if
            } // for
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: " + exept.getMessage());
        }
    }//GEN-LAST:event_ZurueckActionPerformed

    private void RezensionAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RezensionAddActionPerformed
        // TODO add your handling code here:
        int Ergebnis = chooser.showDialog(null, "Datei mit der Rezension wählen");
        if (Ergebnis == JFileChooser.APPROVE_OPTION) {
            String sFilePathAndName = "";
            String sFileName = "";
            try {
                sFilePathAndName = chooser.getSelectedFile().getCanonicalPath();
            } catch (IOException ex) {
                Logger.getLogger(VerwaltenDatenbankRezensionen.class.getName()).log(Level.SEVERE, null, ex);
            }
            sFileName = chooser.getSelectedFile().getName();
            field_Datei.setText(sFileName);
        } // if
    }//GEN-LAST:event_RezensionAddActionPerformed

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

            field_ID.setText(Integer.toString(result.getInt("REZENSION_ID")));
            field_Datei.setText(result.getString("REZENSION_DATEI"));
            field_Anmerkung.setText(result.getString("REZENSION_ANMERKUNG"));
            BuchRezZeit = result.getInt("REZENSION_BUCH");
            for (int i = 0; i < field_Buch.getItemCount(); i++) {
                String s = (String) field_Buch.getItemAt(i);
                String sSplit[] = s.split(",");
                if (Integer.parseInt(sSplit[0]) == BuchRezZeit) {
                    field_Buch.setSelectedIndex(i);
                } // if
            } // for
            BuchRezZeit = result.getInt("REZENSION_ZEITSCHRIFT");
            for (int i = 0; i < field_Zeitschrift.getItemCount(); i++) {
                String s = (String) field_Zeitschrift.getItemAt(i);
                String sSplit[] = s.split(",");
                if (Integer.parseInt(sSplit[0]) == BuchRezZeit) {
                    field_Zeitschrift.setSelectedIndex(i);
                } // if
            } // for
            BuchRezZeit = result.getInt("REZENSION_REZENSENT");
            for (int i = 0; i < field_Rezensent.getItemCount(); i++) {
                String s = (String) field_Rezensent.getItemAt(i);
                String sSplit[] = s.split(",");
                if (Integer.parseInt(sSplit[0]) == BuchRezZeit) {
                    field_Rezensent.setSelectedIndex(i);
                } // if
            } // for
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: " + exept.getMessage());
        }
    }//GEN-LAST:event_EndeActionPerformed

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
            VerwaltenDatenbankRezensionen dialog = new VerwaltenDatenbankRezensionen(new javax.swing.JFrame(), true);
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
    private JLabel jLabelID;
    private JTextField field_ID;
    private JLabel jLabel7;
    private JTextField field_count;
    private JLabel jLabel8;
    private JTextField field_countMax;
    private JComboBox<String> field_Zeitschrift;
    private JLabel jLabel4;
    private JComboBox<String> field_Buch;
    private JLabel jLabel5;
    private JButton RezensionAdd;
    private JTextField field_Datei;
    private JLabel jLabel6;
    private JTextField field_Anmerkung;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JComboBox<String> field_Rezensent;
    private JButton Anfang;
    private JButton Zurueck;
    private JButton Vor;
    private JButton Ende;
    private JButton Update;
    private JButton Einfuegen;
    private JButton Loeschen;
    private JButton Suchen;
    private JButton WSuchen;
    private JButton Drucken;
    private JButton Schliessen;
    // End of variables declaration//GEN-END:variables

    JFileChooser chooser = new JFileChooser(new File(System.getProperty("user.dir")));

    Integer count = 0;
    Integer countMax = 0;

    Connection conn;

    Statement SQLAnfrage;
    Statement SQLAnfrageBuch;
    Statement SQLAnfrageRezensent;
    Statement SQLAnfrageZeitschrift;

    ResultSet result;
    ResultSet resultBuch;
    ResultSet resultRezensent;
    ResultSet resultZeitschrift;

    Integer BuchRezZeit = 0;
    Integer Rezensent = 0;

    Integer maxID = 0;

    boolean resultIsEmpty = true;

}
