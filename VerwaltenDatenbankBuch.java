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
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import javax.swing.*;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.plaf.ActionMapUIResource;
import static milesVerlagMain.ModulMyOwnFocusTraversalPolicy.newPolicy;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Thomas Zimmermann
 */
public class VerwaltenDatenbankBuch extends javax.swing.JDialog {

    /**
     * Creates new form VerwaltenDatenbankBuch
     *
     * @param parent
     * @param modal
     */
    private void IconAddActionPerformed(ActionEvent event) {
        // TODO add your code here
        String Dateiname = "";
        if (chooser.showDialog(null, "Datei mit dem Icon wählen") == JFileChooser.APPROVE_OPTION) {
            try {
                Dateiname = chooser.getSelectedFile().getCanonicalPath();
                System.out.println("Cover-Datei");
                System.out.println("-> " + Modulhelferlein.pathUserDir);
                System.out.println("-> " + Modulhelferlein.pathBuchprojekte);
                System.out.println("-> " + Dateiname);

                field_Cover.setText(Dateiname);
            } catch (IOException ex) {
                Modulhelferlein.Fehlermeldung("Exception: " + ex.getMessage());
            }
        }
    }

    private void FlyerActionPerformed(ActionEvent event) {
        try {
            // TODO add your code here
            String ISBN = result.getString("BUCH_ISBN");
            briefFlyer.bericht(ISBN, "PDF");
            result = SQLAnfrage.executeQuery("SELECT * FROM tbl_buch "
                    + " WHERE BUCH_ID > '0' "
                    + " ORDER BY BUCH_ISBN");
            result.first();
            while (!ISBN.equals(result.getString("BUCH_ISBN"))) {
                result.next();
            }
            field_Flyer.setText(result.getString("BUCH_FLYER"));
        } catch (SQLException ex) {
            Modulhelferlein.Fehlermeldung("Flyer erstellen", "SQL-Exception", ex.getMessage());
        }
    }

    public VerwaltenDatenbankBuch(java.awt.Frame parent, boolean modal) {
        super(parent, modal);

        initComponents();

        buttonGroupBuchtyp.add(rbPB);
        buttonGroupBuchtyp.add(rbHC);
        buttonGroupBuchtyp.add(rbKindle);

        Vector<Component> order = new Vector<>(38);
        order.add(field_Titel);
        order.add(cbHerausgeber);
        order.add(field_ISBN);
        order.add(field_Seiten);
        order.add(field_Preis);
        order.add(field_EK);
        order.add(field_DruckNr);
        order.add(field_Auflage);
        order.add(field_Jahr);
        order.add(field_Bestand);
        order.add(cbDruckerei);
        order.add(field_Cover_gross);
        order.add(CoverAdd);
        order.add(field_Text);
        order.add(TextAdd);
        order.add(field_Flyer);
        order.add(FlyerAdd);
        order.add(field_Vertrag);
        order.add(VertragAdd);
        order.add(field_VertragBOD);
        order.add(VertragBODAdd);
        order.add(field_Honorar);
        order.add(field_Honorar_Anzahl);
        order.add(field_Honorar_Prozent);
        order.add(field_Aktiv);
        order.add(Rezension);
        order.add(field_VLB);
        order.add(field_BLB);
        order.add(btnBLB);
        order.add(field_DNB);
        order.add(btnDNB);
        order.add(rbPB);
        order.add(rbHC);
        order.add(rbKindle);
        order.add(Anfang);
        order.add(Zurueck);
        order.add(Vor);
        order.add(Ende);
        order.add(Update);
        order.add(Einfuegen);
        order.add(Loeschen);
        order.add(Suchen);
        order.add(WSuchen);
        order.add(Drucken);
        order.add(Schliessen);

        newPolicy = new ModulMyOwnFocusTraversalPolicy(order);
        setFocusTraversalPolicy(newPolicy);

        lbAutor.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

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
        SwingUtilities.replaceUIInputMap(panel1, JComponent.WHEN_IN_FOCUSED_WINDOW, keyMap);

        conn = null;

        try { // Datenbank-Treiber laden
            Class.forName(Modulhelferlein.dbDriver);
        } catch (ClassNotFoundException exept) {
            System.out.println("Treiber nicht gefunden: " + exept.getMessage());
            System.exit(1);
        } // Datenbank-Treiber laden

        try { // Verbindung zur Datenbank ?ber die JDBC-Br?cke
            conn = DriverManager.getConnection(Modulhelferlein.dbUrl, Modulhelferlein.dbUser, Modulhelferlein.dbPassword);
        } catch (SQLException exept) {
            System.out.println(
                    "Verbindung nicht moeglich: " + exept.getMessage());
            System.exit(1);
        } // try Verbindung zur Datenbank ?ber die JDBC-Br?cke

        // final Connection conn2=conn;
        if (conn != null) {
            SQLAnfrage = null; // Anfrage erzeugen
            SQLAnfrage2 = null; // Anfrage erzeugen
            SQLAnfrage_a = null; // Anfrage erzeugen

            try { // SQL-Anfragen an die Datenbank
                SQLAnfrage = conn.createStatement(
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE); // Anfrage der DB conn2 zuordnen

                SQLAnfrage2 = conn.createStatement(
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE); // Anfrage der DB conn2 zuordnen

                SQLAnfrage_a = conn.createStatement(
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE); // Anfrage der DB conn2 zuordnen

//                String eintragAnrede = "";
                String eintragAutor = "";

                // Auswahlliste f?r Autoren erstellen   
                result_a = SQLAnfrage_a.executeQuery(
                        "SELECT * FROM tbl_adresse WHERE ADRESSEN_Typ = 'Autor' ORDER BY ADRESSEN_NAME"); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert
                while (result_a.next()) {
                    eintragAutor = Integer.toString(result_a.getInt("ADRESSEN_ID")) + ", "
                            + result_a.getString("ADRESSEN_NAME") + ", "
                            + result_a.getString("ADRESSEN_VORNAME");
//                    eintragAnrede = result_a.getString("ADRESSEN_ANREDE");
                    listModel.addElement(eintragAutor);
//                    cbAnrede.addItem(eintragAnrede);
                } // while

                // Auswahlliste f?r Druckereien erstellen
                eintragAutor = "";
                result_d = SQLAnfrage.executeQuery(
                        "SELECT * FROM tbl_adresse WHERE ADRESSEN_Typ = 'Druckerei'"); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 
                while (result_d.next()) {
                    eintragAutor = Integer.toString(result_d.getInt("ADRESSEN_ID")) + ", "
                            + result_d.getString("ADRESSEN_NAME") + ", "
                            + result_d.getString("ADRESSEN_VORNAME");
                    cbDruckerei.addItem(eintragAutor);
                } // while

                result = SQLAnfrage.executeQuery("SELECT * FROM tbl_buch ORDER BY BUCH_ID DESC"); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 
                if (result.first()) {
                    maxID = result.getInt("Buch_ID");
                } else {
                    maxID = 0;
                }
                result = SQLAnfrage.executeQuery("SELECT * FROM tbl_buch "
                        + " WHERE BUCH_ID > '0' "
                        + " ORDER BY BUCH_ISBN");
                // Anzahl der Datens?tze ermitteln
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
                    field_ID.setText(Integer.toString(result.getInt("Buch_ID")));

                    //col_Autor = result.getInt("BUCH_AUTOR");
                    //result_a = SQLAnfrage2.executeQuery(
                    //        "SELECT * FROM tbl_adresse WHERE ADRESSEN_ID = " + Integer.toString(col_Autor)); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 
                    //result_a.next();
                    //field_Autor = Integer.toString(result_a.getInt("ADRESSEN_ID")) + ", "
                    //        + result_a.getString("ADRESSEN_Name") + ", "
                    //        + result_a.getString("ADRESSEN_Vorname");
                    //cbAutor.setSelectedItem(field_Autor);
                    col_Autor = result.getString("BUCH_AUTOR");
                    col_Autorliste = col_Autor.split(",");
                    int[] select;
                    select = new int[col_Autorliste.length];
                    int selcount = 0;
                    for (String strAutor : col_Autorliste) {
                        result_a = SQLAnfrage2.executeQuery(
                                "SELECT * FROM tbl_adresse WHERE ADRESSEN_ID = " + strAutor);
                        result_a.next();
                        field_Autor = Integer.toString(result_a.getInt("ADRESSEN_ID")) + ", "
                                + result_a.getString("ADRESSEN_Name") + ", "
                                + result_a.getString("ADRESSEN_Vorname");
                        lbAutor.setSelectedValue(field_Autor, true);
                        select[selcount] = lbAutor.getSelectedIndex();
                        selcount = selcount + 1;
                    }
                    lbAutor.setSelectedIndices(select);

                    LabelBild.setIcon(new ImageIcon(result.getString("BUCH_COVER")));
                    //LabelBild.setIcon(new ImageIcon(Modulhelferlein.pathUserDir + "/" + result.getString("BUCH_COVER")));
                    //Bild = new Background(result.getString("Buch_COVER"));this.add(Bild); Bild.setBounds(520, 350, 120, 160);

                    field_Titel.setText(result.getString("Buch_Titel"));
                    field_Preis.setText(Float.toString(result.getFloat("Buch_Preis")));
                    field_EK.setText(Float.toString(result.getFloat("Buch_EK")));
                    field_ISBN.setText(result.getString("Buch_ISBN"));
                    field_Seiten.setText(Integer.toString(result.getInt("BUCH_SEITEN")));
                    field_Beschreibung.setText(result.getString("BUCH_BESCHREIBUNG"));
                    field_Auflage.setText(Integer.toString(result.getInt("BUCH_AUFLAGE")));
                    col_Druckerei = result.getInt("BUCH_DRUCKEREI");
                    result_d = SQLAnfrage2.executeQuery(
                            "SELECT * FROM tbl_adresse WHERE ADRESSEN_ID = " + Integer.toString(col_Druckerei)); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 
                    result_d.next();
                    field_Druckerei = Integer.toString(result_d.getInt("ADRESSEN_ID")) + ", "
                            + result_d.getString("ADRESSEN_Name") + ", "
                            + result_d.getString("ADRESSEN_Vorname");
                    cbDruckerei.setSelectedItem(field_Druckerei);
                    cbHerausgeber.setSelected(result.getBoolean("Buch_HERAUSGEBER"));
                    field_Jahr.setText(result.getString("Buch_JAHR"));
                    field_DruckNr.setText(result.getString("Buch_Druckereinummer"));
                    field_DNB.setSelected(result.getBoolean("Buch_DEUNATBIBL"));
                    field_BLB.setSelected(result.getBoolean("Buch_BERLLBIBL"));
                    field_VLB.setSelected(result.getBoolean("Buch_VLB"));
                    field_Bestand.setText(Integer.toString(result.getInt("Buch_Bestand")));
                    field_Cover.setText(result.getString("Buch_COVER"));
                    field_Cover_gross.setText(result.getString("Buch_COVER_GROSS"));
                    field_Flyer.setText(result.getString("Buch_FLYER"));
                    field_Vertrag.setText(result.getString("Buch_VERTRAG"));
                    field_VertragBOD.setText(result.getString("Buch_BOD_VERTRAG"));
                    field_Text.setText(result.getString("Buch_TEXT"));
                    field_Honorar.setSelected(result.getBoolean("Buch_Honorar"));
                    field_Honorar_Anzahl.setText(Integer.toString(result.getInt("BUCH_HONORAR_ANZAHL")));
                    field_Honorar_Prozent.setText(Integer.toString(result.getInt("BUCH_HONORAR_PROZENT")));
                    field_Honorar_2_Anzahl.setText(Integer.toString(result.getInt("BUCH_HONORAR_2_ANZAHL")));
                    field_Honorar_2_Prozent.setText(Integer.toString(result.getInt("BUCH_HONORAR_2_PROZENT")));
                    field_Aktiv.setSelected(result.getBoolean("BUCH_AKTIV"));
                    field_Gesamtbetrachtung.setSelected(result.getBoolean("BUCH_GESAMTBETRACHTUNG"));
                    field_BoDProzent.setText(Integer.toString(result.getInt("BUCH_BODPROZENT")));
                    field_BoDFix.setText(Float.toString(result.getFloat("BUCH_BODFIX")));
                    switch (result.getInt("Buch_HC")) {
                        case 0:
                            rbPB.setSelected(true);
                            break;
                        case 1:
                            rbHC.setSelected(true);
                            break;
                        case 2:
                            rbKindle.setSelected(true);
                            break;
                    }

                    // Schalterzust?nde setzen
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
                    // Schalterzust?nde setzen   
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
                Modulhelferlein.Fehlermeldung(
                        "SQL-Exception: SQL-Anfrage nicht moeglich: "
                        + exept.getMessage());
                // System.exit(1);
            } // try SQL-Anfragen an die Datenbank

        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        panel1 = new JPanel();
        jScrollPane1 = new JScrollPane();
        field_Beschreibung = new JTextArea();
        jLabel17 = new JLabel();
        jScrollPane2 = new JScrollPane();
        lbAutor = new JList<>();
        cbHerausgeber = new JCheckBox();
        jLabel5 = new JLabel();
        jLabel1 = new JLabel();
        jLabel2 = new JLabel();
        field_count = new JTextField();
        label1 = new JLabel();
        field_countMax = new JTextField();
        jLabel4 = new JLabel();
        field_ID = new JTextField();
        field_Aktiv = new JCheckBox();
        field_Titel = new JTextField();
        jbtnBelegexemplar = new JButton();
        Rezension = new JButton();
        label2 = new JLabel();
        label3 = new JLabel();
        jLabel9 = new JLabel();
        jLabel14 = new JLabel();
        field_VLB = new JCheckBox();
        field_ISBN = new JTextField();
        field_Seiten = new JTextField();
        field_Auflage = new JTextField();
        field_Jahr = new JTextField();
        label4 = new JLabel();
        label5 = new JLabel();
        jLabel15 = new JLabel();
        jLabel24 = new JLabel();
        field_Preis = new JTextField();
        field_EK = new JTextField();
        field_Bestand = new JTextField();
        field_BLB = new JCheckBox();
        btnBLB = new JButton();
        jLabel13 = new JLabel();
        jLabel12 = new JLabel();
        field_DNB = new JCheckBox();
        btnDNB = new JButton();
        field_DruckNr = new JTextField();
        cbDruckerei = new JComboBox<>();
        label6 = new JLabel();
        rbPB = new JRadioButton();
        rbHC = new JRadioButton();
        rbKindle = new JRadioButton();
        jLabel16 = new JLabel();
        field_Cover = new JTextField();
        CoverAdd = new JButton();
        LabelBild = new JButton();
        jLabel18 = new JLabel();
        field_Text = new JTextField();
        TextAdd = new JButton();
        jLabel19 = new JLabel();
        field_Flyer = new JTextField();
        FlyerAdd = new JButton();
        jLabel20 = new JLabel();
        field_Vertrag = new JTextField();
        VertragAdd = new JButton();
        jLabel21 = new JLabel();
        field_VertragBOD = new JTextField();
        VertragBODAdd = new JButton();
        field_Honorar = new JCheckBox();
        field_Honorar_Anzahl = new JTextField();
        jLabel22 = new JLabel();
        field_Honorar_Prozent = new JTextField();
        jLabel23 = new JLabel();
        field_Honorar_2_Anzahl = new JTextField();
        label8 = new JLabel();
        field_Honorar_2_Prozent = new JTextField();
        label7 = new JLabel();
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
        label9 = new JLabel();
        field_Marge = new JTextField();
        field_Gesamtbetrachtung = new JRadioButton();
        field_BODgetrennt = new JRadioButton();
        field_BoDFix = new JTextField();
        field_BoDProzent = new JTextField();
        label10 = new JLabel();
        label11 = new JLabel();
        field_Cover_gross = new JTextField();
        Flyer = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Carola Hartmann Miles Verlag");
        setMinimumSize(new Dimension(970, 710));
        setResizable(false);
        setSize(new Dimension(750, 700));
        setFont(new Font(Font.DIALOG, Font.BOLD, 12));
        var contentPane = getContentPane();
        contentPane.setLayout(null);

        //======== panel1 ========
        {
            panel1.setPreferredSize(new Dimension(800, 658));
            panel1.setLayout(null);

            //======== jScrollPane1 ========
            {

                //---- field_Beschreibung ----
                field_Beschreibung.setColumns(20);
                field_Beschreibung.setLineWrap(true);
                field_Beschreibung.setRows(5);
                field_Beschreibung.setWrapStyleWord(true);
                jScrollPane1.setViewportView(field_Beschreibung);
            }
            panel1.add(jScrollPane1);
            jScrollPane1.setBounds(0, 282, 645, 104);

            //---- jLabel17 ----
            jLabel17.setText("Beschreibung");
            panel1.add(jLabel17);
            jLabel17.setBounds(0, 263, 141, jLabel17.getPreferredSize().height);

            //======== jScrollPane2 ========
            {

                //---- lbAutor ----
                lbAutor.setModel(listModel);
                        lbAutor.setValueIsAdjusting(true);
                jScrollPane2.setViewportView(lbAutor);
            }
            panel1.add(jScrollPane2);
            jScrollPane2.setBounds(0, 105, 325, 153);

            //---- cbHerausgeber ----
            cbHerausgeber.setText("Herausgeber");
            panel1.add(cbHerausgeber);
            cbHerausgeber.setBounds(0, 77, 195, cbHerausgeber.getPreferredSize().height);

            //---- jLabel5 ----
            jLabel5.setText("Titel");
            panel1.add(jLabel5);
            jLabel5.setBounds(0, 30, 49, jLabel5.getPreferredSize().height);

            //---- jLabel1 ----
            jLabel1.setFont(new Font("Tahoma", Font.BOLD, 12));
            jLabel1.setText("Verwalten der Buchprojekte");
            panel1.add(jLabel1);
            jLabel1.setBounds(0, 0, 367, 25);

            //---- jLabel2 ----
            jLabel2.setText("Datensatz");
            jLabel2.setHorizontalAlignment(SwingConstants.RIGHT);
            panel1.add(jLabel2);
            jLabel2.setBounds(423, 0, 57, 25);

            //---- field_count ----
            field_count.setEditable(false);
            field_count.setHorizontalAlignment(SwingConstants.CENTER);
            field_count.setText("000");
            field_count.setEnabled(false);
            field_count.setFocusable(false);
            field_count.setMinimumSize(new Dimension(50, 25));
            field_count.setPreferredSize(new Dimension(50, 25));
            panel1.add(field_count);
            field_count.setBounds(new Rectangle(new Point(485, 0), field_count.getPreferredSize()));

            //---- label1 ----
            label1.setText("von");
            label1.setHorizontalAlignment(SwingConstants.RIGHT);
            panel1.add(label1);
            label1.setBounds(540, 0, 40, 25);

            //---- field_countMax ----
            field_countMax.setEditable(false);
            field_countMax.setHorizontalAlignment(SwingConstants.CENTER);
            field_countMax.setText("000");
            field_countMax.setEnabled(false);
            field_countMax.setFocusable(false);
            field_countMax.setMinimumSize(new Dimension(50, 25));
            field_countMax.setName("");
            field_countMax.setPreferredSize(new Dimension(50, 25));
            panel1.add(field_countMax);
            field_countMax.setBounds(new Rectangle(new Point(595, 0), field_countMax.getPreferredSize()));

            //---- jLabel4 ----
            jLabel4.setText("ID");
            jLabel4.setHorizontalAlignment(SwingConstants.RIGHT);
            panel1.add(jLabel4);
            jLabel4.setBounds(655, 0, 25, 25);

            //---- field_ID ----
            field_ID.setEditable(false);
            field_ID.setText("000");
            field_ID.setEnabled(false);
            field_ID.setFocusable(false);
            panel1.add(field_ID);
            field_ID.setBounds(691, 0, 64, 25);

            //---- field_Aktiv ----
            field_Aktiv.setText("Aktiv");
            field_Aktiv.addActionListener(e -> field_AktivActionPerformed(e));
            panel1.add(field_Aktiv);
            field_Aktiv.setBounds(775, 0, 56, 25);
            panel1.add(field_Titel);
            field_Titel.setBounds(0, 49, 645, 23);

            //---- jbtnBelegexemplar ----
            jbtnBelegexemplar.setText("Belegexemplare");
            jbtnBelegexemplar.addActionListener(e -> jbtnBelegexemplarActionPerformed(e));
            panel1.add(jbtnBelegexemplar);
            jbtnBelegexemplar.setBounds(675, 49, 156, jbtnBelegexemplar.getPreferredSize().height);

            //---- Rezension ----
            Rezension.setText("Rezension veranlassen");
            Rezension.setToolTipText("Veranlasse eine Rezension f\u00fcr das aktuelle Buch");
            Rezension.addActionListener(e -> RezensionActionPerformed(e));
            panel1.add(Rezension);
            Rezension.setBounds(675, 77, 156, Rezension.getPreferredSize().height);

            //---- label2 ----
            label2.setText("ISBN");
            panel1.add(label2);
            label2.setBounds(372, 105, 46, 23);

            //---- label3 ----
            label3.setText("Seiten");
            panel1.add(label3);
            label3.setBounds(485, 105, 50, 23);

            //---- jLabel9 ----
            jLabel9.setText("Auflage");
            panel1.add(jLabel9);
            jLabel9.setBounds(540, 105, 50, 23);

            //---- jLabel14 ----
            jLabel14.setText("Jahr");
            panel1.add(jLabel14);
            jLabel14.setBounds(595, 105, 50, 23);

            //---- field_VLB ----
            field_VLB.setText("Datensatz VLB angelegt");
            panel1.add(field_VLB);
            field_VLB.setBounds(675, 105, 156, field_VLB.getPreferredSize().height);

            //---- field_ISBN ----
            field_ISBN.setText("jTextField1");
            field_ISBN.setMinimumSize(new Dimension(108, 25));
            field_ISBN.setPreferredSize(new Dimension(108, 25));
            panel1.add(field_ISBN);
            field_ISBN.setBounds(new Rectangle(new Point(372, 133), field_ISBN.getPreferredSize()));

            //---- field_Seiten ----
            field_Seiten.setText("jTextField2");
            field_Seiten.setMaximumSize(new Dimension(42, 25));
            field_Seiten.setMinimumSize(new Dimension(42, 25));
            field_Seiten.setPreferredSize(new Dimension(42, 25));
            field_Seiten.addActionListener(e -> field_SeitenActionPerformed(e));
            panel1.add(field_Seiten);
            field_Seiten.setBounds(485, 133, field_Seiten.getPreferredSize().width, 25);

            //---- field_Auflage ----
            field_Auflage.setText("jTextField3");
            field_Auflage.setMaximumSize(new Dimension(50, 20));
            field_Auflage.setMinimumSize(new Dimension(50, 20));
            field_Auflage.setPreferredSize(new Dimension(50, 25));
            field_Auflage.addActionListener(e -> field_AuflageActionPerformed(e));
            panel1.add(field_Auflage);
            field_Auflage.setBounds(540, 133, 35, 25);
            panel1.add(field_Jahr);
            field_Jahr.setBounds(595, 133, 50, 25);

            //---- label4 ----
            label4.setText("VK");
            panel1.add(label4);
            label4.setBounds(372, 165, 46, label4.getPreferredSize().height);

            //---- label5 ----
            label5.setText("EK");
            panel1.add(label5);
            label5.setBounds(423, 165, 57, label5.getPreferredSize().height);

            //---- jLabel15 ----
            jLabel15.setText("Bestand");
            panel1.add(jLabel15);
            jLabel15.setBounds(595, 165, 50, jLabel15.getPreferredSize().height);

            //---- jLabel24 ----
            jLabel24.setText("Pflichtexemplare");
            jLabel24.setFont(jLabel24.getFont().deriveFont(jLabel24.getFont().getStyle() | Font.BOLD));
            panel1.add(jLabel24);
            jLabel24.setBounds(675, 163, 156, jLabel24.getPreferredSize().height);
            panel1.add(field_Preis);
            field_Preis.setBounds(375, 185, 46, field_Preis.getPreferredSize().height);
            panel1.add(field_EK);
            field_EK.setBounds(425, 185, 57, field_EK.getPreferredSize().height);
            panel1.add(field_Bestand);
            field_Bestand.setBounds(595, 185, 50, field_Bestand.getPreferredSize().height);

            //---- field_BLB ----
            field_BLB.setText("Berl.Land.Bibl.");
            panel1.add(field_BLB);
            field_BLB.setBounds(new Rectangle(new Point(675, 182), field_BLB.getPreferredSize()));

            //---- btnBLB ----
            btnBLB.setText("D");
            btnBLB.setToolTipText("Drucke Brief an die Berliner Landesbibliothek");
            btnBLB.addActionListener(e -> btnBLBActionPerformed(e));
            panel1.add(btnBLB);
            btnBLB.setBounds(775, 182, 56, btnBLB.getPreferredSize().height);

            //---- jLabel13 ----
            jLabel13.setText("Druck.Nr.");
            jLabel13.setVerticalAlignment(SwingConstants.BOTTOM);
            panel1.add(jLabel13);
            jLabel13.setBounds(372, 210, jLabel13.getPreferredSize().width, 23);

            //---- jLabel12 ----
            jLabel12.setText("Druckerei");
            jLabel12.setVerticalAlignment(SwingConstants.BOTTOM);
            panel1.add(jLabel12);
            jLabel12.setBounds(485, 210, 50, 23);

            //---- field_DNB ----
            field_DNB.setText("Dt.Nat.Bibl.");
            panel1.add(field_DNB);
            field_DNB.setBounds(675, 210, 95, field_DNB.getPreferredSize().height);

            //---- btnDNB ----
            btnDNB.setText("D");
            btnDNB.setToolTipText("Drucke Brief an die Deutsche Nationalbibliothek");
            btnDNB.addActionListener(e -> btnDNBActionPerformed(e));
            panel1.add(btnDNB);
            btnDNB.setBounds(775, 210, 56, btnDNB.getPreferredSize().height);
            panel1.add(field_DruckNr);
            field_DruckNr.setBounds(372, 238, 108, field_DruckNr.getPreferredSize().height);

            //---- cbDruckerei ----
            cbDruckerei.setModel(new DefaultComboBoxModel<>(new String[] {
                "Item 1",
                "Item 2",
                "Item 3",
                "Item 4"
            }));
            cbDruckerei.addActionListener(e -> cbDruckereiActionPerformed(e));
            panel1.add(cbDruckerei);
            cbDruckerei.setBounds(485, 238, 160, cbDruckerei.getPreferredSize().height);

            //---- label6 ----
            label6.setText("Ausgabeart");
            label6.setFont(label6.getFont().deriveFont(label6.getFont().getStyle() | Font.BOLD));
            panel1.add(label6);
            label6.setBounds(691, 263, 79, label6.getPreferredSize().height);

            //---- rbPB ----
            rbPB.setSelected(true);
            rbPB.setText("Paperback");
            panel1.add(rbPB);
            rbPB.setBounds(691, 282, 140, rbPB.getPreferredSize().height);

            //---- rbHC ----
            rbHC.setText("Hardcover");
            panel1.add(rbHC);
            rbHC.setBounds(691, 310, 140, rbHC.getPreferredSize().height);

            //---- rbKindle ----
            rbKindle.setText("Kindle");
            panel1.add(rbKindle);
            rbKindle.setBounds(691, 338, 140, rbKindle.getPreferredSize().height);

            //---- jLabel16 ----
            jLabel16.setText("Cover");
            jLabel16.setHorizontalAlignment(SwingConstants.RIGHT);
            panel1.add(jLabel16);
            jLabel16.setBounds(100, 391, 41, 25);

            //---- field_Cover ----
            field_Cover.setText("jTextField11");
            field_Cover.setPreferredSize(new Dimension(65, 25));
            panel1.add(field_Cover);
            field_Cover.setBounds(695, 560, 115, field_Cover.getPreferredSize().height);

            //---- CoverAdd ----
            CoverAdd.setText("...");
            CoverAdd.setToolTipText("Auswahl der Bilddatei f\u00fcr das Buchcover");
            CoverAdd.addActionListener(e -> CoverAddActionPerformed(e));
            panel1.add(CoverAdd);
            CoverAdd.setBounds(595, 391, 50, 25);

            //---- LabelBild ----
            LabelBild.setFocusable(false);
            LabelBild.setHorizontalTextPosition(SwingConstants.CENTER);
            LabelBild.setIconTextGap(0);
            LabelBild.setPreferredSize(new Dimension(120, 160));
            LabelBild.setToolTipText("Icon f\u00fcr das Buchcover");
            LabelBild.setMinimumSize(new Dimension(120, 160));
            LabelBild.setMaximumSize(new Dimension(120, 160));
            LabelBild.setMargin(new Insets(0, 0, 0, 0));
            LabelBild.addActionListener(e -> IconAddActionPerformed(e));
            panel1.add(LabelBild);
            LabelBild.setBounds(691, 391, 120, 160);

            //---- jLabel18 ----
            jLabel18.setText("Text");
            jLabel18.setHorizontalAlignment(SwingConstants.RIGHT);
            panel1.add(jLabel18);
            jLabel18.setBounds(100, 421, 41, 23);
            panel1.add(field_Text);
            field_Text.setBounds(146, 421, 444, 23);

            //---- TextAdd ----
            TextAdd.setText("...");
            TextAdd.setToolTipText("Auswahl des Quelltextes f\u00fcr den Buchblock");
            TextAdd.addActionListener(e -> TextAddActionPerformed(e));
            panel1.add(TextAdd);
            TextAdd.setBounds(595, 421, 50, TextAdd.getPreferredSize().height);

            //---- jLabel19 ----
            jLabel19.setText("Flyer");
            jLabel19.setHorizontalAlignment(SwingConstants.RIGHT);
            panel1.add(jLabel19);
            jLabel19.setBounds(100, 449, 41, 23);
            panel1.add(field_Flyer);
            field_Flyer.setBounds(146, 449, 444, 23);

            //---- FlyerAdd ----
            FlyerAdd.setText("...");
            FlyerAdd.setToolTipText("Auswahl des Werbeflyers");
            FlyerAdd.addActionListener(e -> FlyerAddActionPerformed(e));
            panel1.add(FlyerAdd);
            FlyerAdd.setBounds(595, 449, 50, FlyerAdd.getPreferredSize().height);

            //---- jLabel20 ----
            jLabel20.setText("Vertrag Autor");
            jLabel20.setHorizontalAlignment(SwingConstants.RIGHT);
            panel1.add(jLabel20);
            jLabel20.setBounds(54, 477, 87, 23);
            panel1.add(field_Vertrag);
            field_Vertrag.setBounds(146, 477, 444, 23);

            //---- VertragAdd ----
            VertragAdd.setText("...");
            VertragAdd.setToolTipText("Auswahl des Vertrages mit dem Autor");
            VertragAdd.addActionListener(e -> VertragAddActionPerformed(e));
            panel1.add(VertragAdd);
            VertragAdd.setBounds(595, 477, 50, VertragAdd.getPreferredSize().height);

            //---- jLabel21 ----
            jLabel21.setText("Vertrag BOD");
            jLabel21.setHorizontalAlignment(SwingConstants.RIGHT);
            panel1.add(jLabel21);
            jLabel21.setBounds(54, 505, 87, 23);
            panel1.add(field_VertragBOD);
            field_VertragBOD.setBounds(146, 505, 444, 23);

            //---- VertragBODAdd ----
            VertragBODAdd.setText("...");
            VertragBODAdd.setToolTipText("Auswahl des Vertrages mit BoD");
            VertragBODAdd.addActionListener(e -> VertragBODAddActionPerformed(e));
            panel1.add(VertragBODAdd);
            VertragBODAdd.setBounds(595, 505, 50, VertragBODAdd.getPreferredSize().height);

            //---- field_Honorar ----
            field_Honorar.setText("Honorar auf der Basis");
            field_Honorar.setActionCommand("Honorar");
            panel1.add(field_Honorar);
            field_Honorar.setBounds(0, 540, 155, 23);

            //---- field_Honorar_Anzahl ----
            field_Honorar_Anzahl.setMaximumSize(new Dimension(33, 25));
            field_Honorar_Anzahl.setMinimumSize(new Dimension(33, 25));
            field_Honorar_Anzahl.setPreferredSize(new Dimension(33, 25));
            field_Honorar_Anzahl.addActionListener(e -> field_Honorar_AnzahlActionPerformed(e));
            panel1.add(field_Honorar_Anzahl);
            field_Honorar_Anzahl.setBounds(45, 570, field_Honorar_Anzahl.getPreferredSize().width, 20);

            //---- jLabel22 ----
            jLabel22.setText("St\u00fcck mit je");
            panel1.add(jLabel22);
            jLabel22.setBounds(90, 570, 83, 20);

            //---- field_Honorar_Prozent ----
            field_Honorar_Prozent.setMinimumSize(new Dimension(25, 30));
            field_Honorar_Prozent.setPreferredSize(new Dimension(25, 25));
            field_Honorar_Prozent.addActionListener(e -> field_Honorar_ProzentActionPerformed(e));
            panel1.add(field_Honorar_Prozent);
            field_Honorar_Prozent.setBounds(175, 570, 33, 20);

            //---- jLabel23 ----
            jLabel23.setText("% des Netto-VK pro St\u00fcck");
            panel1.add(jLabel23);
            jLabel23.setBounds(220, 570, 163, 20);
            panel1.add(field_Honorar_2_Anzahl);
            field_Honorar_2_Anzahl.setBounds(45, 600, 33, 20);

            //---- label8 ----
            label8.setText("St\u00fcck mit je");
            panel1.add(label8);
            label8.setBounds(90, 600, 83, 20);
            panel1.add(field_Honorar_2_Prozent);
            field_Honorar_2_Prozent.setBounds(175, 600, 33, 20);

            //---- label7 ----
            label7.setText("% des Netto-VK pro St\u00fcck");
            panel1.add(label7);
            label7.setBounds(220, 600, 163, 20);

            //---- Anfang ----
            Anfang.setText("<<");
            Anfang.setToolTipText("gehe zum ersten Datensatz");
            Anfang.addActionListener(e -> AnfangActionPerformed(e));
            panel1.add(Anfang);
            Anfang.setBounds(0, 635, 49, 23);

            //---- Zurueck ----
            Zurueck.setText("<");
            Zurueck.setToolTipText("gehe zum vorherigen Datensatz");
            Zurueck.addActionListener(e -> ZurueckActionPerformed(e));
            panel1.add(Zurueck);
            Zurueck.setBounds(50, 635, 49, Zurueck.getPreferredSize().height);

            //---- Vor ----
            Vor.setText(">");
            Vor.setToolTipText("gehe zum n\u00e4chsten Datensatz");
            Vor.addActionListener(e -> VorActionPerformed(e));
            panel1.add(Vor);
            Vor.setBounds(95, 635, 49, Vor.getPreferredSize().height);

            //---- Ende ----
            Ende.setText(">>");
            Ende.setToolTipText("gehe zum letzten Datensatz");
            Ende.addActionListener(e -> EndeActionPerformed(e));
            panel1.add(Ende);
            Ende.setBounds(145, 635, 49, Ende.getPreferredSize().height);

            //---- Update ----
            Update.setText("!");
            Update.setToolTipText("Datensatz aktualisieren");
            Update.addActionListener(e -> UpdateActionPerformed(e));
            panel1.add(Update);
            Update.setBounds(205, 635, 49, Update.getPreferredSize().height);

            //---- Einfuegen ----
            Einfuegen.setText("+");
            Einfuegen.setToolTipText("Datensatz einf\u00fcgen");
            Einfuegen.addActionListener(e -> EinfuegenActionPerformed(e));
            panel1.add(Einfuegen);
            Einfuegen.setBounds(255, 635, 49, Einfuegen.getPreferredSize().height);

            //---- Loeschen ----
            Loeschen.setText("-");
            Loeschen.setToolTipText("Datensatz l\u00f6schen");
            Loeschen.addActionListener(e -> LoeschenActionPerformed(e));
            panel1.add(Loeschen);
            Loeschen.setBounds(305, 635, 49, Loeschen.getPreferredSize().height);

            //---- Suchen ----
            Suchen.setText("?");
            Suchen.setToolTipText("Suche nach Autor, Titel, ISBN oder Druckereinummer");
            Suchen.addActionListener(e -> SuchenActionPerformed(e));
            panel1.add(Suchen);
            Suchen.setBounds(365, 635, 49, Suchen.getPreferredSize().height);

            //---- WSuchen ----
            WSuchen.setText("...");
            WSuchen.setToolTipText("Weitersuchen");
            WSuchen.addActionListener(e -> WSuchenActionPerformed(e));
            panel1.add(WSuchen);
            WSuchen.setBounds(415, 635, 49, WSuchen.getPreferredSize().height);

            //---- Drucken ----
            Drucken.setText("D");
            Drucken.setToolTipText("Druckt das aktuelle Buchprojekt als PDF");
            Drucken.addActionListener(e -> DruckenActionPerformed(e));
            panel1.add(Drucken);
            Drucken.setBounds(475, 635, 49, Drucken.getPreferredSize().height);

            //---- Schliessen ----
            Schliessen.setText("X");
            Schliessen.setToolTipText("Schlie\u00dft den Dialog");
            Schliessen.addActionListener(e -> SchliessenActionPerformed(e));
            panel1.add(Schliessen);
            Schliessen.setBounds(595, 635, 49, Schliessen.getPreferredSize().height);

            //---- label9 ----
            label9.setText("Marge");
            panel1.add(label9);
            label9.setBounds(new Rectangle(new Point(485, 165), label9.getPreferredSize()));
            panel1.add(field_Marge);
            field_Marge.setBounds(485, 185, 45, field_Marge.getPreferredSize().height);

            //---- field_Gesamtbetrachtung ----
            field_Gesamtbetrachtung.setText("Gesamtumfang");
            field_Gesamtbetrachtung.setSelected(true);
            panel1.add(field_Gesamtbetrachtung);
            field_Gesamtbetrachtung.setBounds(155, 540, 225, field_Gesamtbetrachtung.getPreferredSize().height);

            //---- field_BODgetrennt ----
            field_BODgetrennt.setText("BoD getrennt betrachtet");
            panel1.add(field_BODgetrennt);
            field_BODgetrennt.setBounds(500, 540, 170, field_BODgetrennt.getPreferredSize().height);
            panel1.add(field_BoDFix);
            field_BoDFix.setBounds(390, 570, 30, 20);
            panel1.add(field_BoDProzent);
            field_BoDProzent.setBounds(390, 600, 30, field_BoDProzent.getPreferredSize().height);

            //---- label10 ----
            label10.setText("Fix-Betrag BoD");
            panel1.add(label10);
            label10.setBounds(430, 570, 100, 20);

            //---- label11 ----
            label11.setText("% Marge BoD");
            panel1.add(label11);
            label11.setBounds(430, 600, 80, 20);
            panel1.add(field_Cover_gross);
            field_Cover_gross.setBounds(145, 390, 444, 23);

            //---- Flyer ----
            Flyer.setText("F");
            Flyer.setToolTipText("Werbeflyer erstellen");
            Flyer.addActionListener(e -> FlyerActionPerformed(e));
            panel1.add(Flyer);
            Flyer.setBounds(535, 635, 49, Flyer.getPreferredSize().height);

            {
                // compute preferred size
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
        panel1.setBounds(10, 10, 970, 670);

        {
            // compute preferred size
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
        setSize(860, 720);
        setLocationRelativeTo(getOwner());

        //---- buttonGroup1 ----
        var buttonGroup1 = new ButtonGroup();
        buttonGroup1.add(field_Gesamtbetrachtung);
        buttonGroup1.add(field_BODgetrennt);
    }// </editor-fold>//GEN-END:initComponents

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

            LabelBild.setIcon(new ImageIcon(result.getString("BUCH_COVER")));
            //Bild = new Background(result.getString("Buch_COVER"));this.add(Bild);Bild.setBounds(520, 350, 120, 160);

            cbHerausgeber.setSelected(result.getBoolean("Buch_HERAUSGEBER"));
            field_ID.setText(Integer.toString(result.getInt("Buch_ID")));
            field_Titel.setText(result.getString("Buch_Titel"));
            field_Preis.setText(Float.toString(result.getFloat("Buch_Preis")));
            field_EK.setText(Float.toString(result.getFloat("Buch_EK")));
            field_Marge.setText(Float.toString(result.getFloat("Buch_Marge")));
            field_ISBN.setText(result.getString("Buch_ISBN"));
            field_Seiten.setText(Integer.toString(result.getInt("BUCH_SEITEN")));
            field_Beschreibung.setText(result.getString("BUCH_BESCHREIBUNG"));
            field_Auflage.setText(Integer.toString(result.getInt("BUCH_AUFLAGE")));
            field_DruckNr.setText(result.getString("Buch_Druckereinummer"));
            field_Jahr.setText(result.getString("Buch_JAHR"));
            if (result.getBoolean("BUCH_GESAMTBETRACHTUNG")) {
                field_Gesamtbetrachtung.setSelected(true);
            } else {
                field_BODgetrennt.setSelected(true);
            }
            field_BoDProzent.setText(Integer.toString(result.getInt("BUCH_BODPROZENT")));
            field_BoDFix.setText(Float.toString(result.getFloat("BUCH_BODFIX")));
            field_DNB.setSelected(result.getBoolean("Buch_DEUNATBIBL"));
            field_BLB.setSelected(result.getBoolean("Buch_BERLLBIBL"));
            field_VLB.setSelected(result.getBoolean("Buch_VLB"));
            field_Bestand.setText(Integer.toString(result.getInt("Buch_Bestand")));
            field_Cover.setText(result.getString("Buch_COVER"));
            field_Cover_gross.setText(result.getString("Buch_COVER_GROSS"));
            field_Flyer.setText(result.getString("Buch_FLYER"));
            field_Vertrag.setText(result.getString("Buch_VERTRAG"));
            field_VertragBOD.setText(result.getString("Buch_BOD_VERTRAG"));
            field_Text.setText(result.getString("Buch_TEXT"));
            field_Aktiv.setSelected(result.getBoolean("BUCH_AKTIV"));
            switch (result.getInt("Buch_HC")) {
                case 0:
                    rbPB.setSelected(true);
                    break;
                case 1:
                    rbHC.setSelected(true);
                    break;
                case 2:
                    rbKindle.setSelected(true);
                    break;
            }
            field_Honorar.setSelected(result.getBoolean("Buch_HONORAR"));
            field_Honorar_Anzahl.setText(Integer.toString(result.getInt("BUCH_HONORAR_ANZAHL")));
            field_Honorar_Prozent.setText(Integer.toString(result.getInt("BUCH_HONORAR_PROZENT")));
            field_Honorar_2_Anzahl.setText(Integer.toString(result.getInt("BUCH_HONORAR_2_ANZAHL")));
            field_Honorar_2_Prozent.setText(Integer.toString(result.getInt("BUCH_HONORAR_2_PROZENT")));

//            col_Autor = result.getInt("BUCH_AUTOR");
//            result_a = SQLAnfrage2.executeQuery(
//                    "SELECT * FROM tbl_adresse WHERE ADRESSEN_ID = " + Integer.toString(col_Autor)); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 
//            result_a.next();
//            field_Autor = Integer.toString(result_a.getInt("ADRESSEN_ID")) + ","
//                    + result_a.getString("ADRESSEN_Name") + ","
//                    + result_a.getString("ADRESSEN_Vorname") + ","
//                    + result_a.getString("ADRESSEN_ANREDE");
//            cbAutor.setSelectedItem(field_Autor);
//            lbAutor.setSelectedValue(field_Autor, true);
            col_Autor = result.getString("BUCH_AUTOR");
            col_Autorliste = col_Autor.split(",");
            int[] select;
            select = new int[col_Autorliste.length];
            int selcount = 0;
            for (String strAutor : col_Autorliste) {
                result_a = SQLAnfrage2.executeQuery(
                        "SELECT * FROM tbl_adresse WHERE ADRESSEN_ID = " + strAutor);
                result_a.next();
                field_Autor = Integer.toString(result_a.getInt("ADRESSEN_ID")) + ", "
                        + result_a.getString("ADRESSEN_Name") + ", "
                        + result_a.getString("ADRESSEN_Vorname");
                lbAutor.setSelectedValue(field_Autor, true);
                select[selcount] = lbAutor.getSelectedIndex();
                selcount = selcount + 1;
            }
            lbAutor.setSelectedIndices(select);

            col_Druckerei = result.getInt("BUCH_DRUCKEREI");
            result_d = SQLAnfrage2.executeQuery(
                    "SELECT * FROM tbl_adresse WHERE ADRESSEN_ID = " + Integer.toString(col_Druckerei)); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 
            result_d.next();
            field_Druckerei = Integer.toString(result_d.getInt("ADRESSEN_ID")) + ", "
                    + result_d.getString("ADRESSEN_Name") + ", "
                    + result_d.getString("ADRESSEN_Vorname");
            cbDruckerei.setSelectedItem(field_Druckerei);
            System.out.println("Anfang: ID: " + field_ID.getText() + ", ISBN: " + field_ISBN.getText());
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: " + exept.getMessage());
            System.out.println("Anfang:");
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

                LabelBild.setIcon(new ImageIcon(result.getString("BUCH_COVER")));
                //Bild = new Background(result.getString("Buch_COVER"));this.add(Bild);  Bild.setBounds(520, 350, 120, 160);

                cbHerausgeber.setSelected(result.getBoolean("Buch_HERAUSGEBER"));
                field_ID.setText(Integer.toString(result.getInt("Buch_ID")));
                field_Titel.setText(result.getString("Buch_Titel"));
                field_Preis.setText(Float.toString(result.getFloat("Buch_Preis")));
                field_EK.setText(Float.toString(result.getFloat("Buch_EK")));
                field_Marge.setText(Float.toString(result.getFloat("Buch_Marge")));
                field_ISBN.setText(result.getString("Buch_ISBN"));
                field_Seiten.setText(Integer.toString(result.getInt("BUCH_SEITEN")));
                field_Beschreibung.setText(result.getString("BUCH_BESCHREIBUNG"));
                field_Auflage.setText(Integer.toString(result.getInt("BUCH_AUFLAGE")));
                field_DruckNr.setText(result.getString("Buch_Druckereinummer"));
                field_Jahr.setText(result.getString("Buch_JAHR"));
                field_DNB.setSelected(result.getBoolean("Buch_DEUNATBIBL"));
                field_BLB.setSelected(result.getBoolean("Buch_BERLLBIBL"));
                field_VLB.setSelected(result.getBoolean("Buch_VLB"));
                field_Bestand.setText(Integer.toString(result.getInt("Buch_Bestand")));
                field_Cover.setText(result.getString("Buch_COVER"));
                field_Cover_gross.setText(result.getString("Buch_COVER_GROSS"));
                field_Flyer.setText(result.getString("Buch_FLYER"));
                field_Vertrag.setText(result.getString("Buch_VERTRAG"));
                field_VertragBOD.setText(result.getString("Buch_BOD_VERTRAG"));
                field_Text.setText(result.getString("Buch_TEXT"));
                field_Honorar.setSelected(result.getBoolean("Buch_HONORAR"));
                field_Aktiv.setSelected(result.getBoolean("BUCH_AKTIV"));
                if (result.getBoolean("BUCH_GESAMTBETRACHTUNG")) {
                    field_Gesamtbetrachtung.setSelected(true);
                } else {
                    field_BODgetrennt.setSelected(true);
                }
                field_BoDProzent.setText(Integer.toString(result.getInt("BUCH_BODPROZENT")));
                field_BoDFix.setText(Float.toString(result.getFloat("BUCH_BODFIX")));
                switch (result.getInt("Buch_HC")) {
                    case 0:
                        rbPB.setSelected(true);
                        break;
                    case 1:
                        rbHC.setSelected(true);
                        break;
                    case 2:
                        rbKindle.setSelected(true);
                        break;
                }
                field_Honorar_Anzahl.setText(Integer.toString(result.getInt("BUCH_HONORAR_ANZAHL")));
                field_Honorar_Prozent.setText(Integer.toString(result.getInt("BUCH_HONORAR_PROZENT")));
                field_Honorar_2_Anzahl.setText(Integer.toString(result.getInt("BUCH_HONORAR_2_ANZAHL")));
                field_Honorar_2_Prozent.setText(Integer.toString(result.getInt("BUCH_HONORAR_2_PROZENT")));

//                col_Autor = result.getInt("BUCH_AUTOR");
//                result_a = SQLAnfrage2.executeQuery(
//                        "SELECT * FROM tbl_adresse WHERE ADRESSEN_ID = " + Integer.toString(col_Autor)); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 
//                result_a.next();
//                field_Autor = Integer.toString(result_a.getInt("ADRESSEN_ID")) + ", "
//                        + result_a.getString("ADRESSEN_Name") + ", "
//                        + result_a.getString("ADRESSEN_Vorname");
//                cbAutor.setSelectedItem(field_Autor);
//                lbAutor.setSelectedValue(field_Autor, true);
                col_Autor = result.getString("BUCH_AUTOR");
                col_Autorliste = col_Autor.split(",");
                int[] select;
                select = new int[col_Autorliste.length];
                int selcount = 0;
                for (String strAutor : col_Autorliste) {
                    result_a = SQLAnfrage2.executeQuery(
                            "SELECT * FROM tbl_adresse WHERE ADRESSEN_ID = " + strAutor);
                    result_a.next();
                    field_Autor = Integer.toString(result_a.getInt("ADRESSEN_ID")) + ", "
                            + result_a.getString("ADRESSEN_Name") + ", "
                            + result_a.getString("ADRESSEN_Vorname");
                    lbAutor.setSelectedValue(field_Autor, true);
                    select[selcount] = lbAutor.getSelectedIndex();
                    selcount = selcount + 1;
                }
                lbAutor.setSelectedIndices(select);

                col_Druckerei = result.getInt("BUCH_DRUCKEREI");
                result_d = SQLAnfrage2.executeQuery(
                        "SELECT * FROM tbl_adresse WHERE ADRESSEN_ID = " + Integer.toString(col_Druckerei)); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 
                result_d.next();
                field_Druckerei = Integer.toString(result_d.getInt("ADRESSEN_ID")) + ", "
                        + result_d.getString("ADRESSEN_Name") + ", "
                        + result_d.getString("ADRESSEN_Vorname");
                cbDruckerei.setSelectedItem(field_Druckerei);
            } else {
                result.next();
            }
            System.out.println("Zurück auf ID: " + field_ID.getText() + ", ISBN: " + field_ISBN.getText());
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: " + exept.getMessage());
            System.out.println("Zurück");
        }
    }//GEN-LAST:event_ZurueckActionPerformed

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
                // Schalterzustand anpassen
                Anfang.setEnabled(true);
                Zurueck.setEnabled(true);
                Update.setEnabled(true);
                Einfuegen.setEnabled(true);
                Loeschen.setEnabled(true);
                Suchen.setEnabled(true);
                WSuchen.setEnabled(true);
                Drucken.setEnabled(true);
                Schliessen.setEnabled(true);

                LabelBild.setIcon(new ImageIcon(result.getString("BUCH_COVER")));
                //Bild = new Background(result.getString("Buch_COVER"));this.add(Bild);Bild.setBounds(520, 350, 120, 160);

                cbHerausgeber.setSelected(result.getBoolean("Buch_HERAUSGEBER"));
                field_ID.setText(Integer.toString(result.getInt("Buch_ID")));
                field_Titel.setText(result.getString("Buch_Titel"));
                field_Preis.setText(Float.toString(result.getFloat("Buch_Preis")));
                field_EK.setText(Float.toString(result.getFloat("Buch_EK")));
                field_Marge.setText(Float.toString(result.getFloat("Buch_Marge")));
                field_ISBN.setText(result.getString("Buch_ISBN"));
                field_Seiten.setText(Integer.toString(result.getInt("BUCH_SEITEN")));
                field_Beschreibung.setText(result.getString("BUCH_BESCHREIBUNG"));
                field_Auflage.setText(Integer.toString(result.getInt("BUCH_AUFLAGE")));
                field_DruckNr.setText(result.getString("Buch_Druckereinummer"));
                field_Jahr.setText(result.getString("Buch_JAHR"));
                field_DNB.setSelected(result.getBoolean("Buch_DEUNATBIBL"));
                field_BLB.setSelected(result.getBoolean("Buch_BERLLBIBL"));
                field_VLB.setSelected(result.getBoolean("Buch_VLB"));
                field_Bestand.setText(Integer.toString(result.getInt("Buch_Bestand")));
                field_Cover.setText(result.getString("Buch_COVER"));
                field_Cover_gross.setText(result.getString("Buch_COVER_GROSS"));
                field_Flyer.setText(result.getString("Buch_FLYER"));
                field_Vertrag.setText(result.getString("Buch_VERTRAG"));
                field_VertragBOD.setText(result.getString("Buch_BOD_VERTRAG"));
                field_Text.setText(result.getString("Buch_TEXT"));
                field_Honorar.setSelected(result.getBoolean("Buch_HONORAR"));
                field_Aktiv.setSelected(result.getBoolean("BUCH_AKTIV"));
                if (result.getBoolean("BUCH_GESAMTBETRACHTUNG")) {
                    field_Gesamtbetrachtung.setSelected(true);
                } else {
                    field_BODgetrennt.setSelected(true);
                }
                field_BoDProzent.setText(Integer.toString(result.getInt("BUCH_BODPROZENT")));
                field_BoDFix.setText(Float.toString(result.getFloat("BUCH_BODFIX")));
                switch (result.getInt("Buch_HC")) {
                    case 0:
                        rbPB.setSelected(true);
                        break;
                    case 1:
                        rbHC.setSelected(true);
                        break;
                    case 2:
                        rbKindle.setSelected(true);
                        break;
                }
                field_Honorar_Anzahl.setText(Integer.toString(result.getInt("BUCH_HONORAR_ANZAHL")));
                field_Honorar_Prozent.setText(Integer.toString(result.getInt("BUCH_HONORAR_PROZENT")));
                field_Honorar_2_Anzahl.setText(Integer.toString(result.getInt("BUCH_HONORAR_2_ANZAHL")));
                field_Honorar_2_Prozent.setText(Integer.toString(result.getInt("BUCH_HONORAR_2_PROZENT")));

//                col_Autor = result.getInt("BUCH_AUTOR");
//                result_a = SQLAnfrage2.executeQuery(
//                        "SELECT * FROM tbl_adresse WHERE ADRESSEN_ID = " + Integer.toString(col_Autor)); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 
//                result_a.next();
//                field_Autor = Integer.toString(result_a.getInt("ADRESSEN_ID")) + ", "
//                        + result_a.getString("ADRESSEN_Name") + ", "
//                        + result_a.getString("ADRESSEN_Vorname");
//                cbAutor.setSelectedItem(field_Autor);
//                lbAutor.setSelectedValue(field_Autor, true);
                col_Autor = result.getString("BUCH_AUTOR");
                col_Autorliste = col_Autor.split(",");
                int[] select;
                select = new int[col_Autorliste.length];
                int selcount = 0;
                for (String strAutor : col_Autorliste) {
                    result_a = SQLAnfrage2.executeQuery(
                            "SELECT * FROM tbl_adresse WHERE ADRESSEN_ID = " + strAutor);
                    result_a.next();
                    field_Autor = Integer.toString(result_a.getInt("ADRESSEN_ID")) + ", "
                            + result_a.getString("ADRESSEN_Name") + ", "
                            + result_a.getString("ADRESSEN_Vorname");
                    lbAutor.setSelectedValue(field_Autor, true);
                    select[selcount] = lbAutor.getSelectedIndex();
                    selcount = selcount + 1;
                }
                lbAutor.setSelectedIndices(select);

                col_Druckerei = result.getInt("BUCH_DRUCKEREI");
                result_d = SQLAnfrage2.executeQuery(
                        "SELECT * FROM tbl_adresse WHERE ADRESSEN_ID = " + Integer.toString(col_Druckerei)); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 
                result_d.next();
                field_Druckerei = Integer.toString(result_d.getInt("ADRESSEN_ID")) + ", "
                        + result_d.getString("ADRESSEN_Name") + ", "
                        + result_d.getString("ADRESSEN_Vorname");
                cbDruckerei.setSelectedItem(field_Druckerei);
            } else {
                result.previous();
            }
            System.out.println("Vor auf ID: " + field_ID.getText() + ", ISBN: " + field_ISBN.getText());
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: " + exept.getMessage());
            System.out.println("Vor");
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

            LabelBild.setIcon(new ImageIcon(result.getString("BUCH_COVER")));
            //Bild = new Background(result.getString("Buch_COVER"));this.add(Bild); Bild.setBounds(520, 350, 120, 160);

            cbHerausgeber.setSelected(result.getBoolean("Buch_HERAUSGEBER"));
            field_ID.setText(Integer.toString(result.getInt("Buch_ID")));
            field_Titel.setText(result.getString("Buch_Titel"));
            field_Preis.setText(Float.toString(result.getFloat("Buch_Preis")));
            field_EK.setText(Float.toString(result.getFloat("Buch_EK")));
            field_Marge.setText(Float.toString(result.getFloat("Buch_Marge")));
            field_ISBN.setText(result.getString("Buch_ISBN"));
            field_Seiten.setText(Integer.toString(result.getInt("BUCH_SEITEN")));
            field_Beschreibung.setText(result.getString("BUCH_BESCHREIBUNG"));
            field_Auflage.setText(Integer.toString(result.getInt("BUCH_AUFLAGE")));
            field_DruckNr.setText(result.getString("Buch_Druckereinummer"));
            field_Jahr.setText(result.getString("Buch_JAHR"));
            field_DNB.setSelected(result.getBoolean("Buch_DEUNATBIBL"));
            field_BLB.setSelected(result.getBoolean("Buch_BERLLBIBL"));
            field_VLB.setSelected(result.getBoolean("Buch_VLB"));
            field_Bestand.setText(Integer.toString(result.getInt("Buch_Bestand")));
            field_Cover.setText(result.getString("Buch_COVER"));
            field_Cover_gross.setText(result.getString("Buch_COVER_GROSS"));
            field_Flyer.setText(result.getString("Buch_FLYER"));
            field_Vertrag.setText(result.getString("Buch_VERTRAG"));
            field_VertragBOD.setText(result.getString("Buch_BOD_VERTRAG"));
            field_Text.setText(result.getString("Buch_TEXT"));
            field_Honorar.setSelected(result.getBoolean("Buch_HONORAR"));
            field_Aktiv.setSelected(result.getBoolean("BUCH_AKTIV"));
            if (result.getBoolean("BUCH_GESAMTBETRACHTUNG")) {
                field_Gesamtbetrachtung.setSelected(true);
            } else {
                field_BODgetrennt.setSelected(true);
            }
            field_BoDProzent.setText(Integer.toString(result.getInt("BUCH_BODPROZENT")));
            field_BoDFix.setText(Float.toString(result.getFloat("BUCH_BODFIX")));
            switch (result.getInt("Buch_HC")) {
                case 0:
                    rbPB.setSelected(true);
                    break;
                case 1:
                    rbHC.setSelected(true);
                    break;
                case 2:
                    rbKindle.setSelected(true);
                    break;
            }
            field_Honorar_Anzahl.setText(Integer.toString(result.getInt("BUCH_HONORAR_ANZAHL")));
            field_Honorar_Prozent.setText(Integer.toString(result.getInt("BUCH_HONORAR_PROZENT")));
            field_Honorar_2_Anzahl.setText(Integer.toString(result.getInt("BUCH_HONORAR_2_ANZAHL")));
            field_Honorar_2_Prozent.setText(Integer.toString(result.getInt("BUCH_HONORAR_2_PROZENT")));

//            col_Autor = result.getInt("BUCH_AUTOR");
//            result_a = SQLAnfrage2.executeQuery(
//                    "SELECT * FROM tbl_adresse WHERE ADRESSEN_ID = " + Integer.toString(col_Autor)); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 
//            result_a.next();
//            field_Autor = Integer.toString(result_a.getInt("ADRESSEN_ID")) + ", "
//                    + result_a.getString("ADRESSEN_Name") + ", "
//                    + result_a.getString("ADRESSEN_Vorname");
//            cbAutor.setSelectedItem(field_Autor);
//            lbAutor.setSelectedValue(field_Autor, true);
            col_Autor = result.getString("BUCH_AUTOR");
            col_Autorliste = col_Autor.split(",");
            int[] select;
            select = new int[col_Autorliste.length];
            int selcount = 0;
            for (String strAutor : col_Autorliste) {
                result_a = SQLAnfrage2.executeQuery(
                        "SELECT * FROM tbl_adresse WHERE ADRESSEN_ID = " + strAutor);
                result_a.next();
                field_Autor = Integer.toString(result_a.getInt("ADRESSEN_ID")) + ", "
                        + result_a.getString("ADRESSEN_Name") + ", "
                        + result_a.getString("ADRESSEN_Vorname");
                lbAutor.setSelectedValue(field_Autor, true);
                select[selcount] = lbAutor.getSelectedIndex();
                selcount = selcount + 1;
            }
            lbAutor.setSelectedIndices(select);

            col_Druckerei = result.getInt("BUCH_DRUCKEREI");
            result_d = SQLAnfrage2.executeQuery(
                    "SELECT * FROM tbl_adresse WHERE ADRESSEN_ID = " + Integer.toString(col_Druckerei)); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 
            result_d.next();
            field_Druckerei = Integer.toString(result_d.getInt("ADRESSEN_ID")) + ", "
                    + result_d.getString("ADRESSEN_Name") + ", "
                    + result_d.getString("ADRESSEN_Vorname");
            cbDruckerei.setSelectedItem(field_Druckerei);
            System.out.println("Ende: ID: " + field_ID.getText() + ", ISBN: " + field_ISBN.getText());
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: " + exept.getMessage());
            System.out.println("Ende");
        }
    }//GEN-LAST:event_EndeActionPerformed

    private void UpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UpdateActionPerformed
        // TODO add your handling code here:
        try {
            if (Modulhelferlein.checkNumberFormatInt(field_Seiten.getText()) < 0) {
                Modulhelferlein.Infomeldung("fehlerhafte Eingabe der Anzahl der Seiten - es ist keine korrekte Ganzzahl");
            } else {
                if (Modulhelferlein.checkNumberFormatInt(field_Jahr.getText()) < 0) {
                    Modulhelferlein.Infomeldung("fehlerhafte Eingabe des Erscheinungsjahres - es ist keine korrekte Ganzzahl");
                } else {
                    if (Modulhelferlein.checkNumberFormatInt(field_Bestand.getText()) < 0) {
                        Modulhelferlein.Infomeldung("fehlerhafte Eingabe des Buchbestandes - es ist keine korrekte Ganzzahl");
                    } else {
                        if (Modulhelferlein.checkNumberFormatInt(field_Auflage.getText()) < 0) {
                            Modulhelferlein.Infomeldung("fehlerhafte Eingabe der Auflage- es ist keine korrekte Ganzzahl");
                        } else {
                            if (Modulhelferlein.checkNumberFormatFloat(field_Preis.getText()) < 0) {
                                Modulhelferlein.Infomeldung("fehlerhafte Eingabe des Preises - es ist keine korrekte Zahl");
                            } else {
                                if (Modulhelferlein.checkNumberFormatFloat(field_EK.getText()) < 0) {
                                    Modulhelferlein.Infomeldung("fehlerhafte Eingabe des EK-Preises - es ist keine korrekte Zahl");
                                } else {
                                    if (Modulhelferlein.checkNumberFormatFloat(field_Marge.getText()) < 0) {
                                        Modulhelferlein.Infomeldung("fehlerhafte Eingabe der Marge - es ist keine korrekte Zahl");
                                    } else {
                                        if (Modulhelferlein.checkNumberFormatInt(field_Honorar_Prozent.getText()) < 0) {
                                            Modulhelferlein.Infomeldung("fehlerhafte Eingabe des Prozentwertes für Honorare - es ist keine korrekte Ganzzahl");
                                        } else {
                                            if (Modulhelferlein.checkNumberFormatInt(field_Honorar_Anzahl.getText()) < 0) {
                                                Modulhelferlein.Infomeldung("fehlerhafte Eingabe der Verkeuafsanzahl für Honorare - es ist keine korrekte Ganzzahl");
                                            } else {
                                                if (Modulhelferlein.checkNumberFormatInt(field_Honorar_2_Prozent.getText()) < 0) {
                                                    Modulhelferlein.Infomeldung("fehlerhafte Eingabe des Prozentwertes für Honorare - es ist keine korrekte Ganzzahl");
                                                } else {
                                                    if (Modulhelferlein.checkNumberFormatInt(field_Honorar_2_Anzahl.getText()) < 0) {
                                                        Modulhelferlein.Infomeldung("fehlerhafte Eingabe der Verkeuafsanzahl für Honorare - es ist keine korrekte Ganzzahl");
                                                    } else {
                                                        if (Modulhelferlein.checkNumberFormatFloat(field_BoDFix.getText()) < 0) {
                                                            Modulhelferlein.Infomeldung("fehlerhafte Eingabe des Fix-Anteils der BoD-Marge - es ist keine korrekte Zahl");
                                                        } else {
                                                            if (Modulhelferlein.checkNumberFormatInt(field_Honorar_Prozent.getText()) < 0) {
                                                                Modulhelferlein.Infomeldung("fehlerhafte Eingabe des Prozentwertes der BoD-Marge - es ist keine korrekte Ganzzahl");
                                                            } else {
                                                                result.updateString("Buch_Titel", field_Titel.getText());
                                                                result.updateFloat("Buch_EK", Float.parseFloat(field_EK.getText()));
                                                                result.updateFloat("Buch_Marge", Float.parseFloat(field_Marge.getText()));
                                                                result.updateFloat("Buch_Preis", Float.parseFloat(field_Preis.getText()));
                                                                result.updateString("Buch_ISBN", field_ISBN.getText());
                                                                result.updateInt("Buch_Seiten", Integer.parseInt(field_Seiten.getText()));
                                                                result.updateString("Buch_Beschreibung", field_Beschreibung.getText());
                                                                result.updateInt("Buch_Auflage", Integer.parseInt(field_Auflage.getText()));
                                                                result.updateString("BUCH_JAHR", field_Jahr.getText());
                                                                result.updateString("Buch_Druckereinummer", field_DruckNr.getText());
                                                                result.updateBoolean("Buch_DeuNatBibl", field_DNB.isSelected());
                                                                result.updateBoolean("Buch_BerlLBibl", field_BLB.isSelected());
                                                                result.updateBoolean("Buch_VLB", field_VLB.isSelected());
                                                                if (field_Gesamtbetrachtung.isSelected()) {
                                                                    result.updateBoolean("BUCH_GESAMTBETRACHTUNG", true);
                                                                } else {
                                                                    result.updateBoolean("BUCH_GESAMTBETRACHTUNG", false);
                                                                }
                                                                result.updateFloat("Buch_BODFIX", Float.parseFloat(field_BoDFix.getText()));
                                                                result.updateInt("Buch_Bestand", Integer.parseInt(field_Bestand.getText()));
                                                                result.updateInt("Buch_BODPROZENT", Integer.parseInt(field_BoDProzent.getText()));
                                                                result.updateString("Buch_Cover", field_Cover.getText());
                                                                result.updateString("Buch_Cover_gross", field_Cover_gross.getText());
                                                                result.updateString("Buch_Flyer", field_Flyer.getText());
                                                                result.updateString("Buch_Vertrag", field_Vertrag.getText());
                                                                result.updateString("Buch_BOD_Vertrag", field_VertragBOD.getText());
                                                                result.updateString("Buch_Text", field_Text.getText());
                                                                result.updateInt("Buch_Druckerei", Integer.parseInt(field_Druckerei.split(",")[0]));
                                                                result.updateBoolean("Buch_Honorar", field_Honorar.isSelected());
                                                                result.updateInt("Buch_Honorar_Prozent", Integer.parseInt(field_Honorar_Prozent.getText()));
                                                                result.updateInt("Buch_Honorar_Anzahl", Integer.parseInt(field_Honorar_Anzahl.getText()));
                                                                result.updateInt("Buch_Honorar_2_Prozent", Integer.parseInt(field_Honorar_2_Prozent.getText()));
                                                                result.updateInt("Buch_Honorar_2_Anzahl", Integer.parseInt(field_Honorar_2_Anzahl.getText()));
                                                                result.updateBoolean("BUCH_AKTIV", field_Aktiv.isSelected());
                                                                result.updateBoolean("Buch_HERAUSGEBER", cbHerausgeber.isSelected());
                                                                if (rbPB.isSelected()) {
                                                                    result.updateInt("BUCH_HC", 0);
                                                                } else if (rbHC.isSelected()) {
                                                                    result.updateInt("BUCH_HC", 1);
                                                                } else {
                                                                    result.updateInt("BUCH_HC", 2);
                                                                }

                                                                List<String> ListeAutoren = lbAutor.getSelectedValuesList();
                                                                String EintragAutor = "";
                                                                String strAuswahl = "";
                                                                for (int i = 0, n = ListeAutoren.size(); i < n; i++) {
                                                                    strAuswahl = ListeAutoren.get(i);
                                                                    String[] splitAutor = strAuswahl.split(",");
                                                                    EintragAutor = EintragAutor + splitAutor[0] + ",";
                                                                }
                                                                EintragAutor = EintragAutor.substring(0, EintragAutor.length() - 1);
                                                                result.updateString("Buch_Autor", EintragAutor);

                                                                result.updateRow();
                                                                LabelBild.setIcon(new ImageIcon(result.getString("BUCH_COVER")));
                                                                //Bild = new Background(result.getString("Buch_COVER"));this.add(Bild); Bild.setBounds(520, 350, 120, 160);
                                                                System.out.println("Update: ID: " + field_ID.getText() + ", ISBN: " + field_ISBN.getText());
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: " + exept.getMessage());
        }
    }//GEN-LAST:event_UpdateActionPerformed

    private void EinfuegenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EinfuegenActionPerformed
        // TODO add your handling code here:
        int ID;
        String eingabeISBN = JOptionPane.showInputDialog(null, "Geben Sie die ISBN ein",
                "Neues Buchprojekt",
                JOptionPane.PLAIN_MESSAGE);
//helferlein.Infomeldung(helferlein.pathBuchprojekte + "/" + eingabeISBN + "/1/Vertrag BoD/");
        if (!"".equals(eingabeISBN)) {
            while (eingabeISBN.endsWith(" ")) {
                eingabeISBN = eingabeISBN.substring(0, eingabeISBN.length() - 1);
            }
            File dir = new File(Modulhelferlein.pathBuchprojekte + "/" + eingabeISBN + "/");
            dir.mkdir();
            dir = new File(Modulhelferlein.pathBuchprojekte + "/" + eingabeISBN + "/1/");
            dir.mkdir();
            dir = new File(Modulhelferlein.pathBuchprojekte + "/" + eingabeISBN + "/1/Schriftverkehr/");
            dir.mkdir();
            dir = new File(Modulhelferlein.pathBuchprojekte + "/" + eingabeISBN + "/1/Vertrag Autor/");
            dir.mkdir();
            dir = new File(Modulhelferlein.pathBuchprojekte + "/" + eingabeISBN + "/1/Vertrag BoD/");
            dir.mkdir();
            dir = new File(Modulhelferlein.pathBuchprojekte + "/" + eingabeISBN + "/Belegexemplare/");
            dir.mkdir();
            dir = new File(Modulhelferlein.pathBuchprojekte + "/" + eingabeISBN + "/Cover/");
            dir.mkdir();
            dir = new File(Modulhelferlein.pathBuchprojekte + "/" + eingabeISBN + "/Pflichtexemplare/");
            dir.mkdir();
            dir = new File(Modulhelferlein.pathBuchprojekte + "/" + eingabeISBN + "/Rezensionen/");
            dir.mkdir();

            try {
                ID = maxID + 1;
                maxID = maxID + 1;

                result.moveToInsertRow();
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

                result.updateInt("Buch_ID", ID);
                result.updateString("Buch_Titel", "");
                result.updateFloat("Buch_Preis", 0);
                result.updateFloat("Buch_EK", 0);
                result.updateFloat("Buch_Marge", 0);
                result.updateString("Buch_ISBN", eingabeISBN);
                result.updateInt("Buch_Seiten", 0);
                result.updateString("Buch_Beschreibung", "");
                result.updateString("BUCH_JAHR", "");
                result.updateInt("Buch_Auflage", 1);
                result.updateString("Buch_Druckereinummer", "");
                result.updateBoolean("Buch_DeuNatBibl", false);
                result.updateBoolean("Buch_DeuNatBibl", false);
                result.updateInt("Buch_Bestand", 0);
                result.updateString("Buch_Cover", "Buch.jpg");
                result.updateString("Buch_Cover_GROSS", "Buch.jpg");
                result.updateString("Buch_Flyer", "");
                result.updateString("Buch_Vertrag", "");
                result.updateString("Buch_BOD_Vertrag", "");
                result.updateString("Buch_Text", "");
                result.updateBoolean("Buch_Honorar", true);
                result.updateBoolean("Buch_Herausgeber", false);
                result.updateBoolean("Buch_VLB", false);
                result.updateBoolean("BUCH_GESAMTBETRACHTUNG", true);
                result.updateInt("Buch_Honorar_Anzahl", 0);
                result.updateInt("Buch_BODPROZENT", 0);
                result.updateFloat("Buch_BODFIX", 0);
                result.updateInt("Buch_Honorar_Prozent", 0);
                result.updateInt("Buch_Honorar_2_Anzahl", 0);
                result.updateInt("Buch_Honorar_2_Prozent", 0);
                result.updateBoolean("BUCH_HC", false);

                List<String> ListeAutoren = lbAutor.getSelectedValuesList();
                String EintragAutor = "";
                String strAuswahl = "";
                for (int i = 0, n = ListeAutoren.size(); i < n; i++) {
                    strAuswahl = ListeAutoren.get(i);
                    String[] splitAutor = strAuswahl.split(",");
                    EintragAutor = EintragAutor + splitAutor[0] + ",";
                }
                EintragAutor = EintragAutor.substring(0, EintragAutor.length() - 1);
                result.updateString("Buch_Autor", EintragAutor);

                result.updateInt("Buch_Druckerei", Integer.parseInt(((String) cbDruckerei.getSelectedItem()).split(",")[0]));
                result.updateInt("BUCH_HC", 0);
                result.updateBoolean("BUCH_AKTIV", true);
                result.updateBoolean("BUCH_HERAUSGEBER", false);

                result.insertRow();
                countMax = countMax + 1;
                field_countMax.setText(Integer.toString(countMax));
                count = countMax;
                field_count.setText(Integer.toString(count));

                resultIsEmpty = false;

                result.last();

                field_ID.setText(Integer.toString(result.getInt("Buch_ID")));
                field_Titel.setText(result.getString("Buch_Titel"));
                field_Preis.setText(Float.toString(result.getFloat("Buch_Preis")));
                field_EK.setText(Float.toString(result.getFloat("Buch_EK")));
                field_Marge.setText(Float.toString(result.getFloat("Buch_Marge")));
                field_ISBN.setText(result.getString("Buch_ISBN"));
                field_Seiten.setText(Integer.toString(result.getInt("BUCH_SEITEN")));
                field_Beschreibung.setText(result.getString("BUCH_BESCHREIBUNG"));
                field_Auflage.setText(Integer.toString(result.getInt("BUCH_AUFLAGE")));
                field_DruckNr.setText(result.getString("Buch_Druckereinummer"));
                field_Jahr.setText(result.getString("Buch_JAHR"));
                field_DNB.setSelected(result.getBoolean("Buch_DEUNATBIBL"));
                field_BLB.setSelected(result.getBoolean("Buch_BERLLBIBL"));
                field_VLB.setSelected(result.getBoolean("Buch_VLB"));
                field_Bestand.setText(Integer.toString(result.getInt("Buch_Bestand")));
                field_Cover.setText(result.getString("Buch_COVER"));
                field_Cover_gross.setText(result.getString("Buch_COVER_GROSS"));
                field_Flyer.setText(result.getString("Buch_FLYER"));
                field_Vertrag.setText(result.getString("Buch_VERTRAG"));
                field_VertragBOD.setText(result.getString("Buch_BOD_VERTRAG"));
                field_Text.setText(result.getString("Buch_TEXT"));
                field_Honorar.setSelected(result.getBoolean("Buch_HONORAR"));
                field_Honorar_Anzahl.setText(Integer.toString(result.getInt("BUCH_HONORAR_ANZAHL")));
                field_Honorar_Prozent.setText(Integer.toString(result.getInt("BUCH_HONORAR_PROZENT")));
                field_Aktiv.setSelected(result.getBoolean("BUCH_AKTIV"));
                if (result.getBoolean("BUCH_GESAMTBETRACHTUNG")) {
                    field_Gesamtbetrachtung.setSelected(true);
                } else {
                    field_BODgetrennt.setSelected(true);
                }
                field_BoDProzent.setText(Integer.toString(result.getInt("BUCH_BODPROZENT")));
                field_BoDFix.setText(Float.toString(result.getFloat("BUCH_BODFIX")));
                switch (result.getInt("Buch_HC")) {
                    case 0:
                        rbPB.setSelected(true);
                        break;
                    case 1:
                        rbHC.setSelected(true);
                        break;
                    case 2:
                        rbKindle.setSelected(true);
                        break;
                }

                LabelBild.setIcon(new ImageIcon(result.getString("BUCH_COVER")));
                //Bild = new Background("Buch.jpg");this.add(Bild); Bild.setBounds(520, 350, 120, 160);

                cbHerausgeber.setSelected(result.getBoolean("BUCH_HERAUSGEBER"));
//            col_Autor = result.getInt("BUCH_AUTOR");
//            result_a = SQLAnfrage2.executeQuery(
//                    "SELECT * FROM tbl_adresse WHERE ADRESSEN_ID = " + Integer.toString(col_Autor)); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 
//            result_a.next();
//            field_Autor = Integer.toString(result_a.getInt("ADRESSEN_ID")) + ", "
//                    + result_a.getString("ADRESSEN_Name") + ", "
//                    + result_a.getString("ADRESSEN_Vorname");
//            cbAutor.setSelectedItem(field_Autor);
//            lbAutor.setSelectedValue(field_Autor, true);
                col_Autor = result.getString("BUCH_AUTOR");
                col_Autorliste = col_Autor.split(",");
                int[] select;
                select = new int[col_Autorliste.length];
                int selcount = 0;
                for (String strAutor : col_Autorliste) {
                    result_a = SQLAnfrage2.executeQuery(
                            "SELECT * FROM tbl_adresse WHERE ADRESSEN_ID = " + strAutor);
                    result_a.next();
                    field_Autor = Integer.toString(result_a.getInt("ADRESSEN_ID")) + ", "
                            + result_a.getString("ADRESSEN_Name") + ", "
                            + result_a.getString("ADRESSEN_Vorname");
                    lbAutor.setSelectedValue(field_Autor, true);
                    select[selcount] = lbAutor.getSelectedIndex();
                    selcount = selcount + 1;
                }
                lbAutor.setSelectedIndices(select);

                col_Druckerei = result.getInt("BUCH_DRUCKEREI");
                result_d = SQLAnfrage2.executeQuery(
                        "SELECT * FROM tbl_adresse WHERE ADRESSEN_ID = " + Integer.toString(col_Druckerei)); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 
                result_d.next();
                field_Druckerei = Integer.toString(result_d.getInt("ADRESSEN_ID")) + ", "
                        + result_d.getString("ADRESSEN_Name") + ", "
                        + result_d.getString("ADRESSEN_Vorname");
                cbDruckerei.setSelectedItem(field_Druckerei);

            } catch (SQLException exept) {
                Modulhelferlein.Fehlermeldung("SQL-Exception: Einf?gen: " + exept.getMessage());
            }
        } else {
            Modulhelferlein.Infomeldung("ohne ISBN kein Buchprojekt!");
        } // if   
    }//GEN-LAST:event_EinfuegenActionPerformed

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
                if (result.getRow() > 0) {
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

                    field_ID.setText(Integer.toString(result.getInt("Buch_ID")));
                    field_Titel.setText(result.getString("Buch_Titel"));
                    field_Preis.setText(Float.toString(result.getFloat("Buch_Preis")));
                    field_EK.setText(Float.toString(result.getFloat("Buch_EK")));
                    field_Marge.setText(Float.toString(result.getFloat("Buch_Marge")));
                    field_ISBN.setText(result.getString("Buch_ISBN"));
                    field_Seiten.setText(Integer.toString(result.getInt("BUCH_SEITEN")));
                    field_Beschreibung.setText(result.getString("BUCH_BESCHREIBUNG"));
                    field_Auflage.setText(Integer.toString(result.getInt("BUCH_AUFLAGE")));
                    field_DruckNr.setText(result.getString("Buch_Druckereinummer"));
                    field_Jahr.setText(result.getString("Buch_JAHR"));
                    field_DNB.setSelected(result.getBoolean("Buch_DEUNATBIBL"));
                    field_BLB.setSelected(result.getBoolean("Buch_BERLLBIBL"));
                    field_VLB.setSelected(result.getBoolean("Buch_VLB"));
                    field_Bestand.setText(Integer.toString(result.getInt("Buch_Bestand")));
                    field_Cover.setText(result.getString("Buch_COVER"));
                    field_Cover_gross.setText(result.getString("Buch_COVER_GROSS"));
                    field_Flyer.setText(result.getString("Buch_FLYER"));
                    field_Vertrag.setText(result.getString("Buch_VERTRAG"));
                    field_VertragBOD.setText(result.getString("Buch_BOD_VERTRAG"));
                    field_Text.setText(result.getString("Buch_TEXT"));
                    field_Honorar.setSelected(result.getBoolean("Buch_HONORAR"));
                    field_Honorar_Anzahl.setText(Integer.toString(result.getInt("BUCH_HONORAR_ANZAHL")));
                    field_Honorar_Prozent.setText(Integer.toString(result.getInt("BUCH_HONORAR_PROZENT")));
                    field_Honorar_2_Anzahl.setText(Integer.toString(result.getInt("BUCH_HONORAR_2_ANZAHL")));
                    field_Honorar_2_Prozent.setText(Integer.toString(result.getInt("BUCH_HONORAR_2_PROZENT")));
                    field_Aktiv.setSelected(result.getBoolean("BUCH_AKTIV"));
                    if (result.getBoolean("BUCH_GESAMTBETRACHTUNG")) {
                        field_Gesamtbetrachtung.setSelected(true);
                    } else {
                        field_BODgetrennt.setSelected(true);
                    }
                    field_BoDProzent.setText(Integer.toString(result.getInt("BUCH_BODPROZENT")));
                    field_BoDFix.setText(Float.toString(result.getFloat("BUCH_BODFIX")));
                    switch (result.getInt("Buch_HC")) {
                        case 0:
                            rbPB.setSelected(true);
                            break;
                        case 1:
                            rbHC.setSelected(true);
                            break;
                        case 2:
                            rbKindle.setSelected(true);
                            break;
                    }

                    LabelBild.setIcon(new ImageIcon(result.getString("BUCH_COVER")));
                    //Bild = new Background(result.getString("Buch_COVER"));this.add(Bild); Bild.setBounds(520, 350, 120, 160);

                    cbHerausgeber.setSelected(result.getBoolean("BUCH_HERAUSGEBER"));
                    col_Autor = result.getString("BUCH_AUTOR");
                    col_Autorliste = col_Autor.split(",");
                    int[] select;
                    select = new int[col_Autorliste.length];
                    int selcount = 0;
                    for (String strAutor : col_Autorliste) {
                        result_a = SQLAnfrage2.executeQuery(
                                "SELECT * FROM tbl_adresse WHERE ADRESSEN_ID = " + strAutor);
                        result_a.next();
                        field_Autor = Integer.toString(result_a.getInt("ADRESSEN_ID")) + ", "
                                + result_a.getString("ADRESSEN_Name") + ", "
                                + result_a.getString("ADRESSEN_Vorname");
                        lbAutor.setSelectedValue(field_Autor, true);
                        select[selcount] = lbAutor.getSelectedIndex();
                        selcount = selcount + 1;
                    }
                    lbAutor.setSelectedIndices(select);

                    col_Druckerei = result.getInt("BUCH_DRUCKEREI");
                    result_d = SQLAnfrage2.executeQuery(
                            "SELECT * FROM tbl_adresse WHERE ADRESSEN_ID = " + Integer.toString(col_Druckerei)); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 
                    result_d.next();
                    field_Druckerei = Integer.toString(result_d.getInt("ADRESSEN_ID")) + ", "
                            + result_d.getString("ADRESSEN_Name") + ", "
                            + result_d.getString("ADRESSEN_Vorname");
                    cbDruckerei.setSelectedItem(field_Druckerei);
                } else {
                    resultIsEmpty = true;

                    field_ID.setText("");
                    field_Titel.setText("");
                    field_Preis.setText("");
                    field_EK.setText("");
                    field_ISBN.setText("");
                    field_Seiten.setText("");
                    field_Beschreibung.setText("");
                    field_Auflage.setText("");
                    field_DruckNr.setText("");
                    field_Jahr.setText("");
                    field_DNB.setSelected(false);
                    field_BLB.setSelected(false);
                    field_VLB.setSelected(false);
                    field_Bestand.setText("");
                    field_Cover.setText("");
                    field_Cover_gross.setText("");
                    field_Flyer.setText("");
                    field_Vertrag.setText("");
                    field_VertragBOD.setText("");
                    field_Text.setText("");
                    cbDruckerei.setSelectedItem("");
                    field_Honorar.setSelected(false);
                    field_Aktiv.setSelected(true);
                    cbHerausgeber.setSelected(false);

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
        String[] Kriterien = {"Autor", "Titel", "ISBN", "BOD-Nummer", "Jahr"};
        Kriterium = (String) JOptionPane.showInputDialog(null,
                "Suchen",
                "Nach was soll gesucht werden?",
                JOptionPane.QUESTION_MESSAGE,
                null, Kriterien,
                Kriterien[2]);

        if (Kriterium != null) {
            try {
                result.first();
                count = 1;
                switch (Kriterium) {
                    case "ISBN":
                        SuchString = field_ISBN.getText();
                        break;
                    case "Titel":
                        SuchString = field_Titel.getText();
                        break;
                    case "Autor":
                        SuchString = result.getString("BUCH_Autor");
                        break;
                    case "BOD-Nummer":
                        SuchString = field_DruckNr.getText();
                        break;
                    case "Jahr":
                        SuchString = field_Jahr.getText();
                        break;
                }
                do {
                    field_count.setText(Integer.toString(count));
                    switch (Kriterium) {
                        case "ISBN":
                            if (result.getString("BUCH_ISBN").equals(SuchString)) {
                                gefunden = true;
                            } else {
                                count = count + 1;
                            }
                            break;
                        case "Autor":
                            SuchString = result.getString("BUCH_Autor");

                            if (lbAutor.getSelectedValue().contains(SuchString)) {
                                gefunden = true;
                            } else {
                                count = count + 1;
                            }
                            break;
                        case "Titel":
                            if (result.getString("BUCH_TITEL").equals(SuchString)) {
                                gefunden = true;
                            } else {
                                count = count + 1;
                            }
                            break;
                        case "BOD-Nummer":
                            if (result.getString("Buch_Druckereinummer").equals(SuchString)) {
                                gefunden = true;
                            } else {
                                count = count + 1;
                            }
                            break;
                        case "Jahr":
                            if (result.getString("BUCH_JAHR").equals(SuchString)) {
                                gefunden = true;
                            } else {
                                count = count + 1;
                            }
                            break;
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

                    field_ID.setText(Integer.toString(result.getInt("Buch_ID")));
                    field_Titel.setText(result.getString("Buch_Titel"));
                    field_Preis.setText(Float.toString(result.getFloat("Buch_Preis")));
                    field_EK.setText(Float.toString(result.getFloat("Buch_EK")));
                    field_Marge.setText(Float.toString(result.getFloat("Buch_Marge")));
                    field_ISBN.setText(result.getString("Buch_ISBN"));
                    field_Seiten.setText(Integer.toString(result.getInt("BUCH_SEITEN")));
                    field_Beschreibung.setText(result.getString("BUCH_BESCHREIBUNG"));
                    field_Auflage.setText(Integer.toString(result.getInt("BUCH_AUFLAGE")));
                    field_DruckNr.setText(result.getString("Buch_Druckereinummer"));
                    field_Jahr.setText(result.getString("Buch_JAHR"));
                    field_DNB.setSelected(result.getBoolean("Buch_DEUNATBIBL"));
                    field_BLB.setSelected(result.getBoolean("Buch_BERLLBIBL"));
                    field_VLB.setSelected(result.getBoolean("Buch_VLB"));
                    field_Bestand.setText(Integer.toString(result.getInt("Buch_Bestand")));
                    field_Cover.setText(result.getString("Buch_COVER"));
                    field_Cover_gross.setText(result.getString("Buch_COVER_GROSS"));
                    field_Flyer.setText(result.getString("Buch_FLYER"));
                    field_Vertrag.setText(result.getString("Buch_VERTRAG"));
                    field_VertragBOD.setText(result.getString("Buch_BOD_VERTRAG"));
                    field_Text.setText(result.getString("Buch_TEXT"));
                    field_Honorar.setSelected(result.getBoolean("Buch_HONORAR"));
                    field_Honorar_Anzahl.setText(Integer.toString(result.getInt("BUCH_HONORAR_ANZAHL")));
                    field_Honorar_Prozent.setText(Integer.toString(result.getInt("BUCH_HONORAR_PROZENT")));
                    field_Honorar_2_Anzahl.setText(Integer.toString(result.getInt("BUCH_HONORAR_2_ANZAHL")));
                    field_Honorar_2_Prozent.setText(Integer.toString(result.getInt("BUCH_HONORAR_2_PROZENT")));
                    field_Aktiv.setSelected(result.getBoolean("BUCH_AKTIV"));
                    cbHerausgeber.setSelected(result.getBoolean("BUCH_HERAUSGEBER"));
                    if (result.getBoolean("BUCH_GESAMTBETRACHTUNG")) {
                        field_Gesamtbetrachtung.setSelected(true);
                    } else {
                        field_BODgetrennt.setSelected(true);
                    }
                    field_BoDProzent.setText(Integer.toString(result.getInt("BUCH_BODPROZENT")));
                    field_BoDFix.setText(Float.toString(result.getFloat("BUCH_BODFIX")));
                    switch (result.getInt("Buch_HC")) {
                        case 0:
                            rbPB.setSelected(true);
                            break;
                        case 1:
                            rbHC.setSelected(true);
                            break;
                        case 2:
                            rbKindle.setSelected(true);
                            break;
                    }

                    LabelBild.setIcon(new ImageIcon(result.getString("BUCH_COVER")));

//                    col_Autor = result.getInt("BUCH_AUTOR");
//                    result_a = SQLAnfrage2.executeQuery(
//                            "SELECT * FROM tbl_adresse WHERE ADRESSEN_ID = " + Integer.toString(col_Autor)); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 
//                    result_a.next();
//                    field_Autor = Integer.toString(result_a.getInt("ADRESSEN_ID")) + ", "
//                            + result_a.getString("ADRESSEN_Name") + ", "
//                            + result_a.getString("ADRESSEN_Vorname");
//                    cbAutor.setSelectedItem(field_Autor);
//                    lbAutor.setSelectedValue(field_Autor, true);
                    col_Autor = result.getString("BUCH_AUTOR");
                    col_Autorliste = col_Autor.split(",");
                    int[] select;
                    select = new int[col_Autorliste.length];
                    int selcount = 0;
                    for (String strAutor : col_Autorliste) {
                        result_a = SQLAnfrage2.executeQuery(
                                "SELECT * FROM tbl_adresse WHERE ADRESSEN_ID = " + strAutor);
                        result_a.next();
                        field_Autor = Integer.toString(result_a.getInt("ADRESSEN_ID")) + ", "
                                + result_a.getString("ADRESSEN_Name") + ", "
                                + result_a.getString("ADRESSEN_Vorname");
                        lbAutor.setSelectedValue(field_Autor, true);
                        select[selcount] = lbAutor.getSelectedIndex();
                        selcount = selcount + 1;
                    }
                    lbAutor.setSelectedIndices(select);

                    col_Druckerei = result.getInt("BUCH_DRUCKEREI");
                    result_d = SQLAnfrage2.executeQuery(
                            "SELECT * FROM tbl_adresse WHERE ADRESSEN_ID = " + Integer.toString(col_Druckerei)); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 
                    result_d.next();
                    field_Druckerei = Integer.toString(result_d.getInt("ADRESSEN_ID")) + ", "
                            + result_d.getString("ADRESSEN_Name") + ", "
                            + result_d.getString("ADRESSEN_Vorname");
                    cbDruckerei.setSelectedItem(field_Druckerei);

                } else {
                    Modulhelferlein.Infomeldung(Kriterium + " wurde nicht gefunden!");
                    AnfangActionPerformed(evt);
                }

            } catch (SQLException exept) {
                Modulhelferlein.Fehlermeldung("SQL-Exception: " + exept.getMessage());
            }
        }
    }//GEN-LAST:event_SuchenActionPerformed

    private void WSuchenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_WSuchenActionPerformed
        // TODO add your handling code here:
        boolean gefunden = false;

        try {
            result.next();
            do {
                switch (Kriterium) {
                    case "ISBN":
                        if (result.getString("BUCH_ISBN").equals(SuchString)) {
                            gefunden = true;
                        } else {
                            count = count + 1;
                        }
                        break;
                    case "Autor":
                        SuchString = result.getString("BUCH_Autor");
                        if (lbAutor.getSelectedValue().contains(SuchString)) {
                            gefunden = true;
                        } else {
                            count = count + 1;
                        }
                        break;
                    case "Titel":
                        if (result.getString("BUCH_TITEL").equals(SuchString)) {
                            gefunden = true;
                        } else {
                            count = count + 1;
                        }
                        break;
                    case "BOD-Nummer":
                        if (result.getString("Buch_Druckereinummer").equals(SuchString)) {
                            gefunden = true;
                        } else {
                            count = count + 1;
                        }
                        break;
                    case "Jahr":
                        if (result.getString("BUCH_JAHR").equals(SuchString)) {
                            gefunden = true;
                        } else {
                            count = count + 1;
                        }
                        break;
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

                cbHerausgeber.setSelected(result.getBoolean("BUCH_HERAUSGEBER"));
                field_ID.setText(Integer.toString(result.getInt("Buch_ID")));
                field_Titel.setText(result.getString("Buch_Titel"));
                field_Preis.setText(Float.toString(result.getFloat("Buch_Preis")));
                field_EK.setText(Float.toString(result.getFloat("Buch_EK")));
                field_Marge.setText(Float.toString(result.getFloat("Buch_Marge")));
                field_ISBN.setText(result.getString("Buch_ISBN"));
                field_Seiten.setText(Integer.toString(result.getInt("BUCH_SEITEN")));
                field_Beschreibung.setText(result.getString("BUCH_BESCHREIBUNG"));
                field_Auflage.setText(Integer.toString(result.getInt("BUCH_AUFLAGE")));
                field_DruckNr.setText(result.getString("Buch_Druckereinummer"));
                field_Jahr.setText(result.getString("Buch_JAHR"));
                field_DNB.setSelected(result.getBoolean("Buch_DEUNATBIBL"));
                field_BLB.setSelected(result.getBoolean("Buch_BERLLBIBL"));
                field_VLB.setSelected(result.getBoolean("Buch_VLB"));
                field_Bestand.setText(Integer.toString(result.getInt("Buch_Bestand")));
                field_Cover.setText(result.getString("Buch_COVER"));
                field_Cover_gross.setText(result.getString("Buch_COVER_GROSS"));
                field_Flyer.setText(result.getString("Buch_FLYER"));
                field_Vertrag.setText(result.getString("Buch_VERTRAG"));
                field_VertragBOD.setText(result.getString("Buch_BOD_VERTRAG"));
                field_Text.setText(result.getString("Buch_TEXT"));
                field_Honorar.setSelected(result.getBoolean("Buch_HONORAR"));
                field_Honorar_Anzahl.setText(Integer.toString(result.getInt("BUCH_HONORAR_ANZAHL")));
                field_Honorar_Prozent.setText(Integer.toString(result.getInt("BUCH_HONORAR_PROZENT")));
                field_Honorar_2_Anzahl.setText(Integer.toString(result.getInt("BUCH_HONORAR_2_ANZAHL")));
                field_Honorar_2_Prozent.setText(Integer.toString(result.getInt("BUCH_HONORAR_2_PROZENT")));
                field_Aktiv.setSelected(result.getBoolean("BUCH_AKTIV"));
                if (result.getBoolean("BUCH_GESAMTBETRACHTUNG")) {
                    field_Gesamtbetrachtung.setSelected(true);
                } else {
                    field_BODgetrennt.setSelected(true);
                }
                field_BoDProzent.setText(Integer.toString(result.getInt("BUCH_BODPROZENT")));
                field_BoDFix.setText(Float.toString(result.getFloat("BUCH_BODFIX")));
                switch (result.getInt("Buch_HC")) {
                    case 0:
                        rbPB.setSelected(true);
                        break;
                    case 1:
                        rbHC.setSelected(true);
                        break;
                    case 2:
                        rbKindle.setSelected(true);
                        break;
                }

                LabelBild.setIcon(new ImageIcon(result.getString("BUCH_COVER")));

//                col_Autor = result.getInt("BUCH_AUTOR");
//                lbAutor.setSelectedValue(field_Autor, true);
//                result_a = SQLAnfrage2.executeQuery(
//                        "SELECT * FROM tbl_adresse WHERE ADRESSEN_ID = " + Integer.toString(col_Autor)); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 
//                result_a.next();
//                field_Autor = Integer.toString(result_a.getInt("ADRESSEN_ID")) + ", "
//                        + result_a.getString("ADRESSEN_Name") + ", "
//                        + result_a.getString("ADRESSEN_Vorname");
//                cbAutor.setSelectedItem(field_Autor);
                col_Autor = result.getString("BUCH_AUTOR");
                col_Autorliste = col_Autor.split(",");
                int[] select;
                select = new int[col_Autorliste.length];
                int selcount = 0;
                for (String strAutor : col_Autorliste) {
                    result_a = SQLAnfrage2.executeQuery(
                            "SELECT * FROM tbl_adresse WHERE ADRESSEN_ID = " + strAutor);
                    result_a.next();
                    field_Autor = Integer.toString(result_a.getInt("ADRESSEN_ID")) + ", "
                            + result_a.getString("ADRESSEN_Name") + ", "
                            + result_a.getString("ADRESSEN_Vorname");
                    lbAutor.setSelectedValue(field_Autor, true);
                    select[selcount] = lbAutor.getSelectedIndex();
                    selcount = selcount + 1;
                }
                lbAutor.setSelectedIndices(select);

                col_Druckerei = result.getInt("BUCH_DRUCKEREI");
                result_d = SQLAnfrage2.executeQuery(
                        "SELECT * FROM tbl_adresse WHERE ADRESSEN_ID = " + Integer.toString(col_Druckerei)); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 
                result_d.next();
                field_Druckerei = Integer.toString(result_d.getInt("ADRESSEN_ID")) + ", "
                        + result_d.getString("ADRESSEN_Name") + ", "
                        + result_d.getString("ADRESSEN_Vorname");
                cbDruckerei.setSelectedItem(field_Druckerei);

            } else {
                Modulhelferlein.Infomeldung(Kriterium + " wurde nicht gefunden!");
                AnfangActionPerformed(evt);
            }
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: " + exept.getMessage());
        }
    }//GEN-LAST:event_WSuchenActionPerformed

    private void DruckenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DruckenActionPerformed
        // TODO add your handling code here:
        berBuch.Buch(field_ID.getText(), 1);
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

    private void btnDNBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDNBActionPerformed
        try {
            // TODO add your handling code here:
            field_Autor = "";

            List<String> selectedValues = lbAutor.getSelectedValuesList();
            selectedValues.forEach((selectedValue) -> {
                field_Autor = field_Autor + (String) selectedValue + ", ";
            });
            field_Autor = field_Autor.substring(0, field_Autor.length() - 2);
            String Autor[] = field_Autor.split(", ");
            field_Autor = "";
            for (int i = 1; i < Autor.length; i = i + 3) {
                field_Autor = field_Autor + Autor[i] + ", " + Autor[i + 1] + "; ";
            }
            field_Autor = field_Autor.substring(0, field_Autor.length() - 2) + ".";
            String[] args = {"Pflichtexemplar", // 0
                "DNB", // 1
                field_Autor, // 2
                field_Titel.getText(), // 3
                field_ISBN.getText(), // 4
                field_Jahr.getText(), // 5
                result.getString("BUCH_ID")};   // 6
            _DlgAusgabeFormat.main(args);
        } catch (SQLException ex) {
            Modulhelferlein.Fehlermeldung("Drucke Pflichtexemplar Deutsche Nationalbibliothek", "SQL-Exception", ex.getMessage());
        }

    }//GEN-LAST:event_btnDNBActionPerformed

    private void btnBLBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBLBActionPerformed
        try {
            // TODO add your handling code here:
            field_Autor = "";

            List<String> selectedValues = lbAutor.getSelectedValuesList();
            selectedValues.forEach((selectedValue) -> {
                field_Autor = field_Autor + (String) selectedValue + ", ";
            });
            field_Autor = field_Autor.substring(0, field_Autor.length() - 2);
            String Autor[] = field_Autor.split(", ");
            field_Autor = "";
            for (int i = 1; i < Autor.length; i = i + 3) {
                field_Autor = field_Autor + Autor[i] + ", " + Autor[i + 1] + "; ";
            }
            field_Autor = field_Autor.substring(0, field_Autor.length() - 2) + ".";
            String[] args = {"Pflichtexemplar", "BLB", field_Autor, field_Titel.getText(), field_ISBN.getText(), field_Jahr.getText(), result.getString("BUCH_ID")};
            _DlgAusgabeFormat.main(args);
        } catch (SQLException ex) {
            Modulhelferlein.Fehlermeldung("Drucke Pflichtexemplar Berliner Landesbibliothek", "SQL-Exception", ex.getMessage());
        }

    }//GEN-LAST:event_btnBLBActionPerformed

    private void RezensionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RezensionActionPerformed
        // dlgRezension fenster = new dlgRezension();
        VerwaltenDatenbankRezensionAus.main(null);
        /*        
        try {
            // TODO add your handling code here:
            String CmdLine;
            String[] args;
            field_Autor = "";
            List selectedValues = lbAutor.getSelectedValuesList();
            selectedValues.forEach((selectedValue) -> {
                field_Autor = field_Autor + (String) selectedValue + "#";
            });
            field_Autor = field_Autor.substring(0, field_Autor.length() - 1);
            result_a = SQLAnfrage_a.executeQuery("SELECT * FROM tbl_adresse WHERE ADRESSEN_ID = '"
                    + field_Autor.split(",")[0]
                    + "'");
            result_a.next();
            field_Anrede = result_a.getString("ADRESSEN_ANREDE");
            CmdLine = field_Anrede + "!"
                    + field_Autor + "!"
                    + field_Titel.getText() + "!"
                    + field_Beschreibung.getText() + "!"
                    + field_ISBN.getText() + "!"
                    + field_Preis.getText() + "!"
                    + field_Seiten.getText() + "!"
                    + result.getString("BUCH_ID") + "!"
                    + "1" + "!"
                    + "1";
//helferlein.Infomeldung(CmdLine);            
            args = CmdLine.split("!");
//helferlein.Infomeldung(args[1]);            
            _DlgRezensionErzeugen.main(args);
        } catch (SQLException ex) {
            Logger.getLogger(VerwaltenDatenbankBuch.class.getName()).log(Level.SEVERE, null, ex);
        }
         */
    }//GEN-LAST:event_RezensionActionPerformed

    private void CoverAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CoverAddActionPerformed
        // TODO add your handling code here:
        String Dateiname = "";
        if (chooser.showDialog(null, "Datei mit dem Cover wählen") == JFileChooser.APPROVE_OPTION) {
            try {
                Dateiname = chooser.getSelectedFile().getCanonicalPath();
                System.out.println("Cover-Datei");
                System.out.println("-> " + Modulhelferlein.pathUserDir);
                System.out.println("-> " + Modulhelferlein.pathBuchprojekte);
                System.out.println("-> " + Dateiname);

                field_Cover_gross.setText(Dateiname);
            } catch (IOException e) {
                Modulhelferlein.Fehlermeldung("Exception: " + e.getMessage());
            }
        }
    }//GEN-LAST:event_CoverAddActionPerformed

    private void TextAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TextAddActionPerformed
        // TODO add your handling code here:
        if (chooser.showDialog(null, "Datei mit dem Quelltext wählen") == JFileChooser.APPROVE_OPTION) {
            try {
                field_Text.setText(chooser.getSelectedFile().getCanonicalPath());
            } catch (IOException e) {
                Modulhelferlein.Fehlermeldung("Exception: " + e.getMessage());
            }
        }
    }//GEN-LAST:event_TextAddActionPerformed

    private void FlyerAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FlyerAddActionPerformed
        // TODO add your handling code here:
        if (chooser.showDialog(null, "Datei mit dem Flyer wählen") == JFileChooser.APPROVE_OPTION) {
            try {
                field_Flyer.setText(chooser.getSelectedFile().getCanonicalPath());
            } catch (IOException e) {
                Modulhelferlein.Fehlermeldung("Exception: " + e.getMessage());
            }
        }
    }//GEN-LAST:event_FlyerAddActionPerformed

    private void VertragAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_VertragAddActionPerformed
        // TODO add your handling code here:
        if (chooser.showDialog(null, "Datei mit dem Autorenvertrag wählen") == JFileChooser.APPROVE_OPTION) {
            try {
                field_Vertrag.setText(chooser.getSelectedFile().getCanonicalPath());
            } catch (IOException e) {
                Modulhelferlein.Fehlermeldung("Exception: " + e.getMessage());
            }
        }
    }//GEN-LAST:event_VertragAddActionPerformed

    private void VertragBODAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_VertragBODAddActionPerformed
        // TODO add your handling code here:
        if (chooser.showDialog(null, "Datei mit dem BOD-Vertrag wählen") == JFileChooser.APPROVE_OPTION) {
            try {
                field_VertragBOD.setText(chooser.getSelectedFile().getCanonicalPath());
            } catch (IOException e) {
                Modulhelferlein.Fehlermeldung("Exception: " + e.getMessage());
            }
        }
    }//GEN-LAST:event_VertragBODAddActionPerformed

    private void cbDruckereiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbDruckereiActionPerformed
        // TODO add your handling code here:
        field_Druckerei = (String) cbDruckerei.getSelectedItem();
    }//GEN-LAST:event_cbDruckereiActionPerformed

    private void field_SeitenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_field_SeitenActionPerformed
        // TODO add your handling code here:
        if (Modulhelferlein.checkNumberFormatInt(field_Seiten.getText()) < 0) {
            Modulhelferlein.Fehlermeldung("fehlerhafte Eingabe - die ist keine korrekte Ganzzahl");
        }
    }//GEN-LAST:event_field_SeitenActionPerformed

    private void field_BestandActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_field_BestandActionPerformed
        // TODO add your handling code here:
        if (Modulhelferlein.checkNumberFormatInt(field_Seiten.getText()) < 0) {
            Modulhelferlein.Fehlermeldung("fehlerhafte Eingabe - die ist keine korrekte Ganzzahl");
        }
    }//GEN-LAST:event_field_BestandActionPerformed

    private void field_Honorar_AnzahlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_field_Honorar_AnzahlActionPerformed
        // TODO add your handling code here:
        if (Modulhelferlein.checkNumberFormatInt(field_Seiten.getText()) < 0) {
            Modulhelferlein.Fehlermeldung("fehlerhafte Eingabe - die ist keine korrekte Ganzzahl");
        }
    }//GEN-LAST:event_field_Honorar_AnzahlActionPerformed

    private void field_Honorar_ProzentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_field_Honorar_ProzentActionPerformed
        // TODO add your handling code here:
        if (Modulhelferlein.checkNumberFormatInt(field_Seiten.getText()) < 0) {
            Modulhelferlein.Fehlermeldung("fehlerhafte Eingabe - die ist keine korrekte Ganzzahl");
        }
    }//GEN-LAST:event_field_Honorar_ProzentActionPerformed

    private void field_AuflageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_field_AuflageActionPerformed
        // TODO add your handling code here:
        if (Modulhelferlein.checkNumberFormatInt(field_Seiten.getText()) < 0) {
            Modulhelferlein.Fehlermeldung("fehlerhafte Eingabe - die ist keine korrekte Ganzzahl");
        }
    }//GEN-LAST:event_field_AuflageActionPerformed

    private void field_JahrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_field_JahrActionPerformed
        // TODO add your handling code here:
        if (Modulhelferlein.checkNumberFormatInt(field_Seiten.getText()) < 0) {
            Modulhelferlein.Fehlermeldung("fehlerhafte Eingabe - die ist keine korrekte Ganzzahl");
        }
    }//GEN-LAST:event_field_JahrActionPerformed

    private void field_PreisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_field_PreisActionPerformed
        // TODO add your handling code here:
        if (Modulhelferlein.checkNumberFormatFloat(field_Preis.getText()) < 0) {
            Modulhelferlein.Fehlermeldung("fehlerhafte Eingabe - die ist keine korrekte Zahl");
        }
    }//GEN-LAST:event_field_PreisActionPerformed

    private void field_EKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_field_EKActionPerformed
        // TODO add your handling code here:
        if (Modulhelferlein.checkNumberFormatFloat(field_Preis.getText()) < 0) {
            Modulhelferlein.Fehlermeldung("fehlerhafte Eingabe - die ist keine korrekte Zahl");
        }
    }//GEN-LAST:event_field_EKActionPerformed

    private void field_AktivActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_field_AktivActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_field_AktivActionPerformed

    private void jbtnBelegexemplarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnBelegexemplarActionPerformed
        // TODO add your handling code here:
        try {
            // TODO add your handling code here:
            String CmdLine;
            String[] args;

            field_Autor = "";
            List<String> selectedValues = lbAutor.getSelectedValuesList();
            selectedValues.forEach((selectedValue) -> {
                field_Autor = field_Autor + (String) selectedValue + "#";
            });
            field_Autor = field_Autor.substring(0, field_Autor.length() - 1);

            result_a = SQLAnfrage_a.executeQuery("SELECT * FROM tbl_adresse WHERE ADRESSEN_ID = '"
                    + field_Autor.split(",")[0]
                    + "'");
            result_a.next();
            field_Anrede = result_a.getString("ADRESSEN_ANREDE");
            CmdLine = field_Anrede + "!"
                    + field_Autor + "!"
                    + field_Titel.getText() + "!"
                    + field_Beschreibung.getText() + "!"
                    + field_ISBN.getText() + "!"
                    + field_Preis.getText() + "!"
                    + field_Seiten.getText() + "!"
                    + result.getString("BUCH_ID") + "!"
                    + "1" + "!"
                    + "1";
//helferlein.Infomeldung(CmdLine);            
            args = CmdLine.split("!");
//helferlein.Infomeldung(args[1]);            
            _DlgBelegexemplareErzeugen.main(args);
        } catch (SQLException ex) {
            Modulhelferlein.Fehlermeldung("Belegexemplar erzeugen", ex.getMessage());
            //Logger.getLogger(VerwaltenDatenbankBuch.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jbtnBelegexemplarActionPerformed

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
            VerwaltenDatenbankBuch dialog = new VerwaltenDatenbankBuch(new javax.swing.JFrame(), true);
//            try {
//                javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
//            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
//               Logger.getLogger(VerwaltenDatenbankBuch.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            SwingUtilities.updateComponentTreeUI(dialog);
//            updateComponentTreeUI(dialog);

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
    private JScrollPane jScrollPane1;
    private JTextArea field_Beschreibung;
    private JLabel jLabel17;
    private JScrollPane jScrollPane2;
    private JList<String> lbAutor;
    private JCheckBox cbHerausgeber;
    private JLabel jLabel5;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JTextField field_count;
    private JLabel label1;
    private JTextField field_countMax;
    private JLabel jLabel4;
    private JTextField field_ID;
    private JCheckBox field_Aktiv;
    private JTextField field_Titel;
    private JButton jbtnBelegexemplar;
    private JButton Rezension;
    private JLabel label2;
    private JLabel label3;
    private JLabel jLabel9;
    private JLabel jLabel14;
    private JCheckBox field_VLB;
    private JTextField field_ISBN;
    private JTextField field_Seiten;
    private JTextField field_Auflage;
    private JTextField field_Jahr;
    private JLabel label4;
    private JLabel label5;
    private JLabel jLabel15;
    private JLabel jLabel24;
    private JTextField field_Preis;
    private JTextField field_EK;
    private JTextField field_Bestand;
    private JCheckBox field_BLB;
    private JButton btnBLB;
    private JLabel jLabel13;
    private JLabel jLabel12;
    private JCheckBox field_DNB;
    private JButton btnDNB;
    private JTextField field_DruckNr;
    private JComboBox<String> cbDruckerei;
    private JLabel label6;
    private JRadioButton rbPB;
    private JRadioButton rbHC;
    private JRadioButton rbKindle;
    private JLabel jLabel16;
    private JTextField field_Cover;
    private JButton CoverAdd;
    private JButton LabelBild;
    private JLabel jLabel18;
    private JTextField field_Text;
    private JButton TextAdd;
    private JLabel jLabel19;
    private JTextField field_Flyer;
    private JButton FlyerAdd;
    private JLabel jLabel20;
    private JTextField field_Vertrag;
    private JButton VertragAdd;
    private JLabel jLabel21;
    private JTextField field_VertragBOD;
    private JButton VertragBODAdd;
    private JCheckBox field_Honorar;
    private JTextField field_Honorar_Anzahl;
    private JLabel jLabel22;
    private JTextField field_Honorar_Prozent;
    private JLabel jLabel23;
    private JTextField field_Honorar_2_Anzahl;
    private JLabel label8;
    private JTextField field_Honorar_2_Prozent;
    private JLabel label7;
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
    private JLabel label9;
    private JTextField field_Marge;
    private JRadioButton field_Gesamtbetrachtung;
    private JRadioButton field_BODgetrennt;
    private JTextField field_BoDFix;
    private JTextField field_BoDProzent;
    private JLabel label10;
    private JLabel label11;
    private JTextField field_Cover_gross;
    private JButton Flyer;
    // End of variables declaration//GEN-END:variables

    private JFileChooser chooser = new JFileChooser(new File(System.getProperty("user.dir")));

    private Integer count = 0;
    private Integer countMax = 0;

    private Connection conn;
    private Statement SQLAnfrage;
    private Statement SQLAnfrage2;
    private Statement SQLAnfrage_a;
    private ResultSet result;
    private ResultSet result_a;
    private ResultSet result_d;

    private boolean resultIsEmpty = true;

    private String col_Autor = "";
    private int col_Druckerei = 0;
    private int maxID = 0;

    //private String sFilePathAndName = "";
    private String field_Anrede = "";
    private String field_Autor = "";
    private String field_Druckerei = "";

    private JComboBox<String> cbAnrede = new JComboBox<>();

    private String Kriterium = "";
    private String SuchString = "";

    private DefaultListModel<String> listModel = new DefaultListModel<>();

    private String[] col_Autorliste;
    //private JPanel Bild = new Background("Buch.jpg");

    ButtonGroup buttonGroupBuchtyp = new ButtonGroup();
}
