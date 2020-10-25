/*
 *
 * Das JAVA-Programm Miles-Verlag Verlagsverwaltung stellt alle notwendigen
 * Funktionen f?r die Verwaltung des Carola Hartman Miles-Verlags bereit.
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
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import javax.swing.*;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.LayoutStyle;
import com.toedter.calendar.*;
import javax.swing.plaf.ActionMapUIResource;
import net.miginfocom.swing.*;
import static milesVerlagMain.ModulMyOwnFocusTraversalPolicy.newPolicy;

/**
 *
 * @author Thomas Zimmermann
 */
public class VerwaltenDatenbankAusgaben extends javax.swing.JDialog {

    /**
     * Creates new form VerwaltenDatenbankAusgaben
     *
     * @param parent
     * @param modal
     */
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public VerwaltenDatenbankAusgaben(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.resultIsEmpty = true;
        this.countMax = 0;
        this.chooser = new JFileChooser(new File(System.getProperty("user.dir")));

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
        
        Vector<Component> order = new Vector<>(21);
        order.add(field_Typ);
        order.add(field_Betrag);
        order.add(field_Ustr);
        order.add(field_Bezahlt);
        order.add(field_Lieferant);
        order.add(field_RechnNr);
        order.add(field_RechnDat);
        order.add(field_Beschreibung);
        order.add(BDatei);
        order.add(field_Link);
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

        for (String AusgabenTypListe : ModulHelferlein.AusgabenTypListe) {
            field_Typ.addItem(AusgabenTypListe);
        }

        conn = null;

        // Datenbank-Treiber laden
        try {
            Class.forName(ModulHelferlein.dbDriver);
        } catch (ClassNotFoundException exept) {
            ModulHelferlein.Fehlermeldung("Verwalten der Ausgaben",
                    "ClassNotFoundException: Treiber nicht gefunden: ",
                     exept.getMessage());
        }

        // Verbindung zur Datenbank über die JDBC-Brücke
        try {
            conn = DriverManager.getConnection(ModulHelferlein.dbUrl, ModulHelferlein.dbUser, ModulHelferlein.dbPassword);
        } catch (SQLException exept) {
            ModulHelferlein.Fehlermeldung("Verwalten der Ausgaben",
                    "SQL-Excetion: Verbindung nicht moeglich: ",
                     exept.getMessage());
        }

        // final Connection conn2=conn;
        if (conn != null) {
            SQLAnfrage2 = null; // Anfrage erzeugen
            try {
                SQLAnfrage2 = conn.createStatement(
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE); // Anfrage der DB conn2 zuordnen
                result2 = SQLAnfrage2.executeQuery("SELECT * FROM tbl_ADRESSE "
                        + " WHERE ((ADRESSEN_TYP = 'Autor') "
                        + "OR (ADRESSEN_TYP = 'Druckerei') "
                        + "OR (ADRESSEN_TYP = 'Lieferant')) "
                        + "ORDER BY ADRESSEN_NAME");
                // gehe zum ersten Datensatz - wenn nicht leer
                while (result2.next()) {
                    field_Lieferant.addItem(result2.getString("ADRESSEN_NAME") + ", "
                                            + result2.getString("ADRESSEN_VORNAME"));
                }

                SQLAnfrage = null; // Anfrage erzeugen
                SQLAnfrage_Liste = null;
                
                try {
                    SQLAnfrage = conn.createStatement(
                            ResultSet.TYPE_SCROLL_SENSITIVE,
                            ResultSet.CONCUR_UPDATABLE); // Anfrage der DB conn2 zuordnen
                    // MaxID ermitteln
                    result = SQLAnfrage.executeQuery("SELECT * FROM TBL_AUSGABEN ORDER BY AUSGABEN_ID DESC"); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 
                    if (result.next()) {
                        MaxID = result.getInt("AUSGABEN_ID");
                    } else {
                        MaxID = 0;
                    }
                    // Übersichtsliste einfügen
                    SQLAnfrage_Liste = conn.createStatement(
                            ResultSet.TYPE_SCROLL_SENSITIVE,
                            ResultSet.CONCUR_UPDATABLE);
                    initListeRechnungenbestand();
                    
                    // Anzahl der Datensätze ermitteln
                    result = SQLAnfrage.executeQuery("SELECT * FROM TBL_AUSGABEN ORDER BY AUSGABEN_RECHDATUM, Ausgaben_RECHNNR"); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 
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
                        field_ID.setText(Integer.toString(result.getInt("Ausgaben_ID")));
                        field_RechnNr.setText(result.getString("Ausgaben_RECHNNR"));
                        field_RechnDat.setDate(ModulHelferlein.Date2SQLDate(result.getDate("Ausgaben_RECHDATUM")));
                        field_Beschreibung.setText(result.getString("Ausgaben_BESCHREIBUNG"));
                        field_Betrag.setText(Float.toString(result.getFloat("Ausgaben_KOSTEN")));
                        field_Bezahlt.setDate(ModulHelferlein.Date2SQLDate(result.getDate("Ausgaben_BEZAHLT")));
                        field_Ustr.setText(Integer.toString(result.getInt("Ausgaben_Ustr")));
                        field_Typ.setSelectedIndex(result.getInt("Ausgaben_TYP"));
                        field_Lieferant.setSelectedItem(result.getString("Ausgaben_LIEFERANT"));
                        field_Link.setText(result.getString("AUSGABEN_LINK"));
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
                    ModulHelferlein.Fehlermeldung("SQL-Exception: SQL-Anfrage nicht moeglich: "
                            + exept.getMessage());
                } // try catch

            } catch (SQLException exept) {
                ModulHelferlein.Fehlermeldung("SQL-Exception: SQL-Anfrage nicht moeglich: "
                        + exept.getMessage());
            } //try catch Adressen

        } // if conn != 0

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
        jLabelTitel = new JLabel();
        jLabelID = new JLabel();
        field_ID = new JTextField();
        jLabel1 = new JLabel();
        field_count = new JTextField();
        jLabel2 = new JLabel();
        field_countMax = new JTextField();
        field_Typ = new JComboBox<>();
        jLabelRechNr = new JLabel();
        field_RechnNr = new JTextField();
        jLabelRechDat = new JLabel();
        field_Beschreibung = new JTextField();
        jLabel6 = new JLabel();
        BDatei = new JButton();
        field_Link = new JTextField();
        jLabel3 = new JLabel();
        jLabelBetrag = new JLabel();
        jLabelUStr = new JLabel();
        jLabel5 = new JLabel();
        jLabelLieferant = new JLabel();
        field_Betrag = new JTextField();
        jLabel4 = new JLabel();
        field_Ustr = new JTextField();
        field_Bezahlt = new JDateChooser();
        field_Lieferant = new JComboBox<>();
        jLabelBeschreibung = new JLabel();
        field_RechnDat = new JDateChooser();
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
        jPanel1 = new JPanel();
        scrollPane1 = new JScrollPane();
        jList1 = new JList<>();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Carola Hartmann Miles Verlag");
        setResizable(false);
        setFont(new Font("Dialog", Font.BOLD, 12));
        Container contentPane = getContentPane();

        //======== panel1 ========
        {
            panel1.setLayout(null);

            //---- jLabelTitel ----
            jLabelTitel.setFont(new Font("Tahoma", Font.BOLD, 12));
            jLabelTitel.setText("Verwaltung der Ausgaben");
            panel1.add(jLabelTitel);
            jLabelTitel.setBounds(0, 0, jLabelTitel.getPreferredSize().width, 20);

            //---- jLabelID ----
            jLabelID.setText("ID");
            jLabelID.setHorizontalAlignment(SwingConstants.RIGHT);
            panel1.add(jLabelID);
            jLabelID.setBounds(235, 0, 50, 20);

            //---- field_ID ----
            field_ID.setEditable(false);
            field_ID.setText("0000");
            field_ID.setEnabled(false);
            field_ID.setFocusable(false);
            field_ID.setMaximumSize(new Dimension(30, 20));
            field_ID.setMinimumSize(new Dimension(30, 20));
            panel1.add(field_ID);
            field_ID.setBounds(new Rectangle(new Point(290, 0), field_ID.getPreferredSize()));

            //---- jLabel1 ----
            jLabel1.setText("Datensatz Nr.");
            panel1.add(jLabel1);
            jLabel1.setBounds(345, 0, 120, 20);

            //---- field_count ----
            field_count.setEditable(false);
            field_count.setText("000");
            field_count.setEnabled(false);
            field_count.setFocusable(false);
            field_count.setMaximumSize(new Dimension(30, 20));
            field_count.setMinimumSize(new Dimension(30, 20));
            field_count.setPreferredSize(new Dimension(32, 20));
            panel1.add(field_count);
            field_count.setBounds(470, 0, 30, field_count.getPreferredSize().height);

            //---- jLabel2 ----
            jLabel2.setText("von");
            panel1.add(jLabel2);
            jLabel2.setBounds(525, 0, 50, 20);

            //---- field_countMax ----
            field_countMax.setEditable(false);
            field_countMax.setText("000");
            field_countMax.setEnabled(false);
            field_countMax.setFocusable(false);
            field_countMax.setMaximumSize(new Dimension(30, 20));
            field_countMax.setMinimumSize(new Dimension(30, 20));
            field_countMax.setPreferredSize(new Dimension(32, 20));
            panel1.add(field_countMax);
            field_countMax.setBounds(580, 0, 30, field_countMax.getPreferredSize().height);

            //---- field_Typ ----
            field_Typ.setModel(new DefaultComboBoxModel<>(new String[] {

            }));
            field_Typ.setName("");
            field_Typ.setPreferredSize(new Dimension(28, 30));
            panel1.add(field_Typ);
            field_Typ.setBounds(0, 69, 160, field_Typ.getPreferredSize().height);

            //---- jLabelRechNr ----
            jLabelRechNr.setText("Rechnungsnummer");
            panel1.add(jLabelRechNr);
            jLabelRechNr.setBounds(0, 104, 105, jLabelRechNr.getPreferredSize().height);

            //---- field_RechnNr ----
            field_RechnNr.setPreferredSize(new Dimension(6, 30));
            panel1.add(field_RechnNr);
            field_RechnNr.setBounds(0, 123, 105, field_RechnNr.getPreferredSize().height);

            //---- jLabelRechDat ----
            jLabelRechDat.setText("Rechn.Datum");
            panel1.add(jLabelRechDat);
            jLabelRechDat.setBounds(110, 104, 120, jLabelRechDat.getPreferredSize().height);

            //---- field_Beschreibung ----
            field_Beschreibung.setPreferredSize(new Dimension(6, 30));
            panel1.add(field_Beschreibung);
            field_Beschreibung.setBounds(235, 123, 395, field_Beschreibung.getPreferredSize().height);

            //---- jLabel6 ----
            jLabel6.setText("Rechnungsdatei");
            panel1.add(jLabel6);
            jLabel6.setBounds(0, 158, 705, jLabel6.getPreferredSize().height);

            //---- BDatei ----
            BDatei.setText("...");
            BDatei.setPreferredSize(new Dimension(45, 30));
            BDatei.addActionListener(e -> BDateiActionPerformed(e));
            panel1.add(BDatei);
            BDatei.setBounds(0, 177, 50, BDatei.getPreferredSize().height);

            //---- field_Link ----
            field_Link.setPreferredSize(new Dimension(6, 30));
            panel1.add(field_Link);
            field_Link.setBounds(55, 177, 575, field_Link.getPreferredSize().height);

            //---- jLabel3 ----
            jLabel3.setText("Ausgabenart");
            panel1.add(jLabel3);
            jLabel3.setBounds(0, 50, 160, jLabel3.getPreferredSize().height);

            //---- jLabelBetrag ----
            jLabelBetrag.setText("Bruttobetrag");
            panel1.add(jLabelBetrag);
            jLabelBetrag.setBounds(165, 50, 120, jLabelBetrag.getPreferredSize().height);

            //---- jLabelUStr ----
            jLabelUStr.setText("UStr");
            panel1.add(jLabelUStr);
            jLabelUStr.setBounds(290, 50, 50, jLabelUStr.getPreferredSize().height);

            //---- jLabel5 ----
            jLabel5.setText("Bezahlt am");
            panel1.add(jLabel5);
            jLabel5.setBounds(new Rectangle(new Point(345, 50), jLabel5.getPreferredSize()));

            //---- jLabelLieferant ----
            jLabelLieferant.setText("Kostenverursacher/Lieferant");
            panel1.add(jLabelLieferant);
            jLabelLieferant.setBounds(470, 50, 160, jLabelLieferant.getPreferredSize().height);

            //---- field_Betrag ----
            field_Betrag.setText("0000.00");
            field_Betrag.setPreferredSize(new Dimension(46, 30));
            panel1.add(field_Betrag);
            field_Betrag.setBounds(165, 69, 65, field_Betrag.getPreferredSize().height);

            //---- jLabel4 ----
            jLabel4.setText("\u20ac");
            panel1.add(jLabel4);
            jLabel4.setBounds(235, 69, 50, 30);

            //---- field_Ustr ----
            field_Ustr.setText("00");
            field_Ustr.setMinimumSize(new Dimension(20, 30));
            field_Ustr.setPreferredSize(new Dimension(20, 30));
            panel1.add(field_Ustr);
            field_Ustr.setBounds(290, 69, 50, field_Ustr.getPreferredSize().height);

            //---- field_Bezahlt ----
            field_Bezahlt.setPreferredSize(new Dimension(120, 30));
            panel1.add(field_Bezahlt);
            field_Bezahlt.setBounds(new Rectangle(new Point(345, 69), field_Bezahlt.getPreferredSize()));

            //---- field_Lieferant ----
            field_Lieferant.setModel(new DefaultComboBoxModel<>(new String[] {

            }));
            field_Lieferant.setPreferredSize(new Dimension(28, 30));
            panel1.add(field_Lieferant);
            field_Lieferant.setBounds(470, 69, 160, field_Lieferant.getPreferredSize().height);

            //---- jLabelBeschreibung ----
            jLabelBeschreibung.setText("Beschreibung");
            panel1.add(jLabelBeschreibung);
            jLabelBeschreibung.setBounds(235, 104, 395, jLabelBeschreibung.getPreferredSize().height);

            //---- field_RechnDat ----
            field_RechnDat.setPreferredSize(new Dimension(120, 30));
            panel1.add(field_RechnDat);
            field_RechnDat.setBounds(new Rectangle(new Point(110, 123), field_RechnDat.getPreferredSize()));

            //---- Anfang ----
            Anfang.setText("<<");
            Anfang.setToolTipText("Gehe zum Anfang - erster Datensatz");
            Anfang.setMaximumSize(new Dimension(50, 25));
            Anfang.setMinimumSize(new Dimension(50, 25));
            Anfang.setPreferredSize(new Dimension(50, 23));
            Anfang.addActionListener(e -> AnfangActionPerformed(e));
            panel1.add(Anfang);
            Anfang.setBounds(0, 237, Anfang.getPreferredSize().width, 25);

            //---- Zurueck ----
            Zurueck.setText("<");
            Zurueck.setToolTipText("Gehe zum vorherigen Datensatz");
            Zurueck.setMaximumSize(new Dimension(50, 25));
            Zurueck.setMinimumSize(new Dimension(50, 25));
            Zurueck.setPreferredSize(new Dimension(50, 23));
            Zurueck.addActionListener(e -> ZurueckActionPerformed(e));
            panel1.add(Zurueck);
            Zurueck.setBounds(55, 237, Zurueck.getPreferredSize().width, 25);

            //---- Vor ----
            Vor.setText(">");
            Vor.setToolTipText("Gehe zum n\u00e4chsten Datensatz");
            Vor.setMaximumSize(new Dimension(50, 25));
            Vor.setMinimumSize(new Dimension(50, 25));
            Vor.setPreferredSize(new Dimension(50, 23));
            Vor.addActionListener(e -> VorActionPerformed(e));
            panel1.add(Vor);
            Vor.setBounds(110, 237, Vor.getPreferredSize().width, 25);

            //---- Ende ----
            Ende.setText(">>");
            Ende.setToolTipText("Gehe zum Ende - zum letzten Datensatz");
            Ende.setMaximumSize(new Dimension(50, 25));
            Ende.setMinimumSize(new Dimension(50, 25));
            Ende.setPreferredSize(new Dimension(50, 23));
            Ende.addActionListener(e -> EndeActionPerformed(e));
            panel1.add(Ende);
            Ende.setBounds(165, 237, Ende.getPreferredSize().width, 25);

            //---- Update ----
            Update.setText("!");
            Update.setToolTipText("Datensatz aktualisieren");
            Update.setMaximumSize(new Dimension(50, 25));
            Update.setMinimumSize(new Dimension(50, 25));
            Update.setPreferredSize(new Dimension(50, 23));
            Update.addActionListener(e -> UpdateActionPerformed(e));
            panel1.add(Update);
            Update.setBounds(235, 237, Update.getPreferredSize().width, 25);

            //---- Einfuegen ----
            Einfuegen.setText("+");
            Einfuegen.setToolTipText("Datensatz einf\u00fcgen");
            Einfuegen.setMaximumSize(new Dimension(50, 25));
            Einfuegen.setMinimumSize(new Dimension(50, 25));
            Einfuegen.setPreferredSize(new Dimension(50, 23));
            Einfuegen.addActionListener(e -> EinfuegenActionPerformed(e));
            panel1.add(Einfuegen);
            Einfuegen.setBounds(290, 237, Einfuegen.getPreferredSize().width, 25);

            //---- Loeschen ----
            Loeschen.setText("-");
            Loeschen.setToolTipText("Datensatz l\u00f6schen");
            Loeschen.setMaximumSize(new Dimension(50, 25));
            Loeschen.setMinimumSize(new Dimension(50, 25));
            Loeschen.setPreferredSize(new Dimension(50, 23));
            Loeschen.addActionListener(e -> LoeschenActionPerformed(e));
            panel1.add(Loeschen);
            Loeschen.setBounds(345, 237, Loeschen.getPreferredSize().width, 25);

            //---- Suchen ----
            Suchen.setText("?");
            Suchen.setToolTipText("Datensatz suchen");
            Suchen.setMaximumSize(new Dimension(50, 25));
            Suchen.setMinimumSize(new Dimension(50, 25));
            Suchen.setPreferredSize(new Dimension(50, 23));
            Suchen.addActionListener(e -> SuchenActionPerformed(e));
            panel1.add(Suchen);
            Suchen.setBounds(402, 237, Suchen.getPreferredSize().width, 25);

            //---- WSuchen ----
            WSuchen.setText("...");
            WSuchen.setToolTipText("Weitersuchen");
            WSuchen.setMaximumSize(new Dimension(50, 25));
            WSuchen.setMinimumSize(new Dimension(50, 25));
            WSuchen.setPreferredSize(new Dimension(50, 23));
            WSuchen.addActionListener(e -> WSuchenActionPerformed(e));
            panel1.add(WSuchen);
            WSuchen.setBounds(470, 237, WSuchen.getPreferredSize().width, 25);

            //---- Drucken ----
            Drucken.setText("D");
            Drucken.setToolTipText("Liste der Ausgaben drucken");
            Drucken.setMaximumSize(new Dimension(50, 25));
            Drucken.setMinimumSize(new Dimension(50, 25));
            Drucken.setPreferredSize(new Dimension(50, 23));
            Drucken.addActionListener(e -> DruckenActionPerformed(e));
            panel1.add(Drucken);
            Drucken.setBounds(525, 237, Drucken.getPreferredSize().width, 25);

            //---- Schliessen ----
            Schliessen.setText("X");
            Schliessen.setMaximumSize(new Dimension(50, 25));
            Schliessen.setMinimumSize(new Dimension(50, 25));
            Schliessen.setPreferredSize(new Dimension(50, 23));
            Schliessen.addActionListener(e -> SchliessenActionPerformed(e));
            panel1.add(Schliessen);
            Schliessen.setBounds(580, 237, Schliessen.getPreferredSize().width, 25);

            //======== jPanel1 ========
            {

                //======== scrollPane1 ========
                {
                    jList1.setModel(listModel);
                                        jList1.addMouseListener(new MouseAdapter() {
                                            @Override
                                            public void mouseClicked(MouseEvent e) {
                                                jList1MouseClicked(e);
                                            }
                                        });
                    scrollPane1.setViewportView(jList1);
                }

                GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
                jPanel1.setLayout(jPanel1Layout);
                jPanel1Layout.setHorizontalGroup(
                    jPanel1Layout.createParallelGroup()
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 138, GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 2, Short.MAX_VALUE))
                );
                jPanel1Layout.setVerticalGroup(
                    jPanel1Layout.createParallelGroup()
                        .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                );
            }
            panel1.add(jPanel1);
            jPanel1.setBounds(640, -5, 140, 265);

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
                    .addComponent(panel1, GroupLayout.DEFAULT_SIZE, 783, Short.MAX_VALUE)
                    .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(panel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        setSize(805, 315);
        setLocationRelativeTo(getOwner());
    }// </editor-fold>//GEN-END:initComponents

    private void BDateiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BDateiActionPerformed
        // TODO add your handling code here:
        if (chooser.showDialog(null, "Datei mit der Rechnung wählen") == JFileChooser.APPROVE_OPTION) {
            try {
                field_Link.setText(chooser.getSelectedFile().getCanonicalPath());
            } catch (IOException e) {
                ModulHelferlein.Fehlermeldung("Verwalten der Ausgaben - Dateiauswahl", "IO-Exception: ", e.getMessage());
            }
        } // if
    }//GEN-LAST:event_BDateiActionPerformed

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

            field_ID.setText(Integer.toString(result.getInt("Ausgaben_ID")));
            field_RechnNr.setText(result.getString("Ausgaben_RECHNNR"));
            field_RechnDat.setDate(ModulHelferlein.Date2SQLDate(result.getDate("Ausgaben_RECHDATUM")));
            field_Beschreibung.setText(result.getString("Ausgaben_BESCHREIBUNG"));
            field_Betrag.setText(Float.toString(result.getFloat("Ausgaben_KOSTEN")));
            field_Bezahlt.setDate(ModulHelferlein.Date2SQLDate(result.getDate("Ausgaben_BEZAHLT")));
            field_Ustr.setText(Integer.toString(result.getInt("Ausgaben_Ustr")));
            field_Typ.setSelectedIndex(result.getInt("Ausgaben_TYP"));
            field_Lieferant.setSelectedItem(result.getString("Ausgaben_LIEFERANT"));
            field_Link.setText(result.getString("AUSGABEN_LINK"));
        } catch (SQLException exept) {
            ModulHelferlein.Fehlermeldung("Verwalten der Ausgaben", "SQL-Exception: ", exept.getMessage());
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
            field_ID.setText(Integer.toString(result.getInt("Ausgaben_ID")));
            field_RechnNr.setText(result.getString("Ausgaben_RECHNNR"));
            field_RechnDat.setDate(ModulHelferlein.Date2SQLDate(result.getDate("Ausgaben_RECHDATUM")));
            field_Beschreibung.setText(result.getString("Ausgaben_BESCHREIBUNG"));
            field_Betrag.setText(Float.toString(result.getFloat("Ausgaben_KOSTEN")));
            field_Bezahlt.setDate(ModulHelferlein.Date2SQLDate(result.getDate("Ausgaben_BEZAHLT")));
            field_Ustr.setText(Integer.toString(result.getInt("Ausgaben_Ustr")));
            field_Typ.setSelectedIndex(result.getInt("Ausgaben_TYP"));
            field_Lieferant.setSelectedItem(result.getString("Ausgaben_LIEFERANT"));
            field_Link.setText(result.getString("AUSGABEN_LINK"));
        } catch (SQLException exept) {
            ModulHelferlein.Fehlermeldung("Verwalten der Ausgaben", "SQL-Exception: ", exept.getMessage());
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
            field_ID.setText(Integer.toString(result.getInt("Ausgaben_ID")));
            field_RechnNr.setText(result.getString("Ausgaben_RECHNNR"));
            field_RechnDat.setDate(ModulHelferlein.Date2SQLDate(result.getDate("Ausgaben_RECHDATUM")));
            field_Beschreibung.setText(result.getString("Ausgaben_BESCHREIBUNG"));
            field_Betrag.setText(Float.toString(result.getFloat("Ausgaben_KOSTEN")));
            field_Bezahlt.setDate(ModulHelferlein.Date2SQLDate(result.getDate("Ausgaben_BEZAHLT")));
            field_Ustr.setText(Integer.toString(result.getInt("Ausgaben_Ustr")));
            field_Typ.setSelectedIndex(result.getInt("Ausgaben_TYP"));
            field_Lieferant.setSelectedItem(result.getString("Ausgaben_LIEFERANT"));
            field_Link.setText(result.getString("AUSGABEN_LINK"));
        } catch (SQLException exept) {
            ModulHelferlein.Fehlermeldung("Verwalten der Ausgaben", "SQL-Exception: ", exept.getMessage());
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

            field_ID.setText(Integer.toString(result.getInt("Ausgaben_ID")));
            field_RechnNr.setText(result.getString("Ausgaben_RECHNNR"));
            field_RechnDat.setDate(ModulHelferlein.Date2SQLDate(result.getDate("Ausgaben_RECHDATUM")));
            field_Beschreibung.setText(result.getString("Ausgaben_BESCHREIBUNG"));
            field_Betrag.setText(Float.toString(result.getFloat("Ausgaben_KOSTEN")));
            field_Bezahlt.setDate(ModulHelferlein.Date2SQLDate(result.getDate("Ausgaben_BEZAHLT")));
            field_Ustr.setText(Integer.toString(result.getInt("Ausgaben_Ustr")));
            field_Typ.setSelectedIndex(result.getInt("Ausgaben_TYP"));
            field_Lieferant.setSelectedItem(result.getString("Ausgaben_LIEFERANT"));
            field_Link.setText(result.getString("AUSGABEN_LINK"));
        } catch (SQLException exept) {
            ModulHelferlein.Fehlermeldung("Verwalten der Ausgaben", "SQL-Exception: ", exept.getMessage());
        }
    }//GEN-LAST:event_EndeActionPerformed

    private void UpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UpdateActionPerformed
        // TODO add your handling code here:
        try {
            if (ModulHelferlein.checkNumberFormatInt(field_Ustr.getText()) < 0) {
                ModulHelferlein.Infomeldung("fehlerhafte Eingabe - die ist keine korrekte Ganzzahl");
            } else {
                if (ModulHelferlein.checkNumberFormatFloat(field_Betrag.getText()) < 0) {
                    ModulHelferlein.Infomeldung("fehlerhafte Eingabe - die ist keine korrekte Zahl");
                } else {
                    result.updateString("Ausgaben_RECHNNR", field_RechnNr.getText());
                    result.updateDate("Ausgaben_RECHDATUM", ModulHelferlein.Date2SQLDate(field_RechnDat.getDate()));
                    result.updateString("Ausgaben_BESCHREIBUNG", field_Beschreibung.getText());
                    result.updateFloat("Ausgaben_KOSTEN", Float.parseFloat(field_Betrag.getText()));
                    result.updateDate("Ausgaben_BEZAHLT", ModulHelferlein.Date2SQLDate(field_Bezahlt.getDate()));
                    result.updateInt("Ausgaben_Ustr", Integer.parseInt(field_Ustr.getText()));
                    result.updateInt("Ausgaben_TYP", field_Typ.getSelectedIndex());
                    result.updateString("Ausgaben_LIEFERANT", (String) field_Lieferant.getSelectedItem());
                    result.updateString("Ausgaben_LINK", field_Link.getText());
                    result.updateRow();
                    initListeRechnungenbestand();
                }
            }
        } catch (SQLException exept) {
            ModulHelferlein.Fehlermeldung("Verwalten der Ausgaben", "SQL-Exception: ", exept.getMessage());
        }
    }//GEN-LAST:event_UpdateActionPerformed

    private void EinfuegenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EinfuegenActionPerformed
        // TODO add your handling code here:
        int ID;

        try {
            MaxID = MaxID + 1;

            result.moveToInsertRow();
            result.updateInt("Ausgaben_ID", MaxID);
            result.updateString("Ausgaben_RECHNNR", "");
            result.updateDate("Ausgaben_RECHDATUM", ModulHelferlein.Date2SQLDate(ModulHelferlein.CurDate));
            result.updateString("Ausgaben_BESCHREIBUNG", "");
            result.updateFloat("Ausgaben_KOSTEN", 0);
            result.updateDate("Ausgaben_BEZAHLT", ModulHelferlein.Date2SQLDate(new Date(0)));
            result.updateInt("Ausgaben_Ustr", 19);
            result.updateInt("Ausgaben_TYP", 0);
            result.updateString("Ausgaben_LIEFERANT", "");
            result.updateString("AUSGABEN_LINK", "");
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
            field_ID.setText(Integer.toString(result.getInt("Ausgaben_ID")));
            field_RechnNr.setText(result.getString("Ausgaben_RECHNNR"));
            field_RechnDat.setDate(ModulHelferlein.Date2SQLDate(result.getDate("Ausgaben_RECHDATUM")));
            field_Beschreibung.setText(result.getString("Ausgaben_BESCHREIBUNG"));
            field_Betrag.setText(Float.toString(result.getFloat("Ausgaben_KOSTEN")));
            field_Bezahlt.setDate(ModulHelferlein.Date2SQLDate(result.getDate("Ausgaben_BEZAHLT")));
            field_Ustr.setText(Integer.toString(result.getInt("Ausgaben_Ustr")));
            field_Typ.setSelectedIndex(result.getInt("Ausgaben_TYP"));
            field_Lieferant.setSelectedItem(result.getString("Ausgaben_LIEFERANT"));
            field_Link.setText(result.getString("AUSGABEN_LINK"));
        } catch (SQLException exept) {
            ModulHelferlein.Fehlermeldung("Verwalten der Ausgaben", "SQL-Exception: ", exept.getMessage());
        }
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
                if (countMax > 0) {
                    field_ID.setText(Integer.toString(result.getInt("Ausgaben_ID")));
                    field_RechnNr.setText(result.getString("Ausgaben_RECHNNR"));
                    field_RechnDat.setDate(ModulHelferlein.Date2SQLDate(result.getDate("Ausgaben_RECHDATUM")));
                    field_Beschreibung.setText(result.getString("Ausgaben_BESCHREIBUNG"));
                    field_Betrag.setText(Float.toString(result.getFloat("Ausgaben_KOSTEN")));
                    field_Bezahlt.setDate(ModulHelferlein.Date2SQLDate(result.getDate("Ausgaben_BEZAHLT")));
                    field_Ustr.setText(Integer.toString(result.getInt("Ausgaben_Ustr")));
                    field_Typ.setSelectedIndex(result.getInt("Ausgaben_TYP"));
                    field_Lieferant.setSelectedItem(result.getString("Ausgaben_LIEFERANT"));
                    field_Link.setText(result.getString("AUSGABEN_LINK"));

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
                    field_RechnNr.setText("");
                    field_RechnDat.setDate(new Date());
                    field_Beschreibung.setText("");
                    field_Betrag.setText("");
                    field_Bezahlt.setDate(new Date());
                    field_Ustr.setText("");
                    field_Typ.setSelectedIndex(0);
                    field_Lieferant.setSelectedItem("");

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
                ModulHelferlein.Fehlermeldung("Verwalten der Ausgaben", "SQL-Exception: ", exept.getMessage());
            }
        }
    }//GEN-LAST:event_LoeschenActionPerformed

    private void SuchenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SuchenActionPerformed
        // TODO add your handling code here:
        boolean gefunden = false;
        String[] Kriterien = {"Rechnungsnummer", "Rechnungsdatum"};
        Kriterium = (String) JOptionPane.showInputDialog(null,
                "Suchen",
                "Nach was soll gesucht werden?",
                JOptionPane.QUESTION_MESSAGE,
                null, Kriterien,
                Kriterien[0]);

        if (Kriterium != null) {
            try {
                do {
                    result.first();
                    count = 1;
                    field_count.setText(Integer.toString(count));
                    switch (Kriterium) {
                        case "Rechnungsnummer":
                            if (result.getString("Ausgaben_RECHNNR").equals(field_RechnNr.getText())) {
                                gefunden = true;
                            } else {
                                count = count + 1;
                            }
                            break;
                        case "Rechnungsdatum":
                            if (result.getString("Ausgaben_RECHDATUM").equals(field_RechnDat.getDate().toString())) {
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

                    field_ID.setText(Integer.toString(result.getInt("Ausgaben_ID")));
                    field_RechnNr.setText(result.getString("Ausgaben_RECHNNR"));
                    field_RechnDat.setDate(ModulHelferlein.Date2SQLDate(result.getDate("Ausgaben_RECHDATUM")));
                    field_Beschreibung.setText(result.getString("Ausgaben_BESCHREIBUNG"));
                    field_Betrag.setText(Float.toString(result.getFloat("Ausgaben_KOSTEN")));
                    field_Bezahlt.setDate(ModulHelferlein.Date2SQLDate(result.getDate("Ausgaben_BEZAHLT")));
                    field_Ustr.setText(Integer.toString(result.getInt("Ausgaben_Ustr")));
                    field_Typ.setSelectedIndex(result.getInt("Ausgaben_TYP"));
                    field_Lieferant.setSelectedItem(result.getString("Ausgaben_LIEFERANT"));
                    field_Link.setText(result.getString("AUSGABEN_LINK"));

                } else {
                    ModulHelferlein.Infomeldung(Kriterium + " wurde nicht gefunden!");
                    AnfangActionPerformed(evt);
                }
            } catch (SQLException exept) {
                ModulHelferlein.Fehlermeldung("Verwalten der Ausgaben", "SQL-Exception: ", exept.getMessage());

            }
        }
    }//GEN-LAST:event_SuchenActionPerformed

    private void WSuchenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_WSuchenActionPerformed
        // TODO add your handling code here:
        boolean gefunden = false;

        try {
            do {
                result.first();
                count = 1;
                field_count.setText(Integer.toString(count));
                switch (Kriterium) {
                    case "Rechnungsnummer":
                        if (result.getString("Ausgaben_RECHNNR").equals(field_RechnNr.getText())) {
                            gefunden = true;
                        } else {
                            count = count + 1;
                        }
                        break;
                    case "Rechnungsdatum":
                        if (result.getString("Ausgaben_RECHDATUM").equals(field_RechnDat.getDate().toString())) {
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

                field_ID.setText(Integer.toString(result.getInt("Ausgaben_ID")));
                field_RechnNr.setText(result.getString("Ausgaben_RECHNNR"));
                field_RechnDat.setDate(ModulHelferlein.Date2SQLDate(result.getDate("Ausgaben_RECHDATUM")));
                field_Beschreibung.setText(result.getString("Ausgaben_BESCHREIBUNG"));
                field_Betrag.setText(Float.toString(result.getFloat("Ausgaben_KOSTEN")));
                field_Bezahlt.setDate(ModulHelferlein.Date2SQLDate(result.getDate("Ausgaben_BEZAHLT")));
                field_Ustr.setText(Integer.toString(result.getInt("Ausgaben_Ustr")));
                field_Typ.setSelectedIndex(result.getInt("Ausgaben_TYP"));
                field_Lieferant.setSelectedItem(result.getString("Ausgaben_LIEFERANT"));
                field_Link.setText(result.getString("AUSGABEN_LINK"));

            } else {
                ModulHelferlein.Infomeldung(Kriterium + " wurde nicht gefunden!");
                AnfangActionPerformed(evt);
            }
        } catch (SQLException exept) {
            ModulHelferlein.Fehlermeldung("Verwalten der Ausgaben", "SQL-Exception: ", exept.getMessage());
        }
    }//GEN-LAST:event_WSuchenActionPerformed

    private void DruckenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DruckenActionPerformed
        // TODO add your handling code here:
        _DlgUebersichtAusgaben.main(null);
    }//GEN-LAST:event_DruckenActionPerformed

    private void SchliessenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SchliessenActionPerformed
        // TODO add your handling code here:
        try {
            result.close();
            SQLAnfrage.close();
            conn.close();
        } catch (SQLException exept) {
            ModulHelferlein.Fehlermeldung("Verwalten der Ausgaben", "SQL-Exception: ", exept.getMessage());
        }
        this.dispose();
    }//GEN-LAST:event_SchliessenActionPerformed

    private void jList1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList1MouseClicked
        // TODO add your handling code here:
        try {
            // get selected item
            List<String> ListeAdressen = jList1.getSelectedValuesList();
            // get Adresssen_ID
            String[] Listeneintrag = ListeAdressen.get(0).split(", ");
//helferlein.Infomeldung(ListeAdressen.get(0)+" "+Integer.toString(Listeneintrag.length)+" "+Listeneintrag[Listeneintrag.length-1]);            
            // gehe zu dieser ID
            if (result.first()) {
                count = 1;
                boolean ende = false;
                while (!ende) {
//helferlein.Infomeldung(result.getString("AUSGABEN_RECHNNR")+" - "+Listeneintrag[0]);                    
                    ende = (result.getString("AUSGABEN_RECHNNR").equals(Listeneintrag[0]) && result.getString("Ausgaben_RECHDATUM").equals(Listeneintrag[1]));
                    result.next();
                    count = count + 1;
                }
                result.previous();
                count = count - 1;
                // Felder aktualisieren
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
                field_ID.setText(Integer.toString(result.getInt("Ausgaben_ID")));
                field_RechnNr.setText(result.getString("Ausgaben_RECHNNR"));
                field_RechnDat.setDate(ModulHelferlein.Date2SQLDate(result.getDate("Ausgaben_RECHDATUM")));
                field_Beschreibung.setText(result.getString("Ausgaben_BESCHREIBUNG"));
                field_Betrag.setText(Float.toString(result.getFloat("Ausgaben_KOSTEN")));
                field_Bezahlt.setDate(ModulHelferlein.Date2SQLDate(result.getDate("Ausgaben_BEZAHLT")));
                field_Ustr.setText(Integer.toString(result.getInt("Ausgaben_Ustr")));
                field_Typ.setSelectedIndex(result.getInt("Ausgaben_TYP"));
                field_Lieferant.setSelectedItem(result.getString("Ausgaben_LIEFERANT"));
                field_Link.setText(result.getString("AUSGABEN_LINK"));
            }
        } catch (SQLException ex) {
            ModulHelferlein.Fehlermeldung("Ausgaben verwalten", "direkt zur Rechnung gehen: SQL-Exception", ex.getMessage());
        }
    }//GEN-LAST:event_jList1MouseClicked

    /**
     * Diese Methode initialisiert die Liste mit dem Rechnungen Der Aufruf
     * erfolgt beim Start des Dialoges sowie beim Update von Rechnungen
     */
    private void initListeRechnungenbestand() {
        try {
            listModel.clear();
            String eintragRechnung = "";

            // Auswahlliste f?r Autoren erstellen
            result_Liste = SQLAnfrage_Liste.executeQuery("SELECT * FROM TBL_AUSGABEN ORDER BY AUSGABEN_RECHDATUM");
            while (result_Liste.next()) {
                eintragRechnung = result_Liste.getString("AUSGABEN_RECHNNR") + ", "
                        + result_Liste.getString("AUSGABEN_RECHDATUM");
                listModel.addElement(eintragRechnung);
            } // while
        } catch (SQLException ex) {
            ModulHelferlein.Fehlermeldung("Ausgaben verwalten", "ARechnungen auflisten: SQL-Exception", ex.getMessage());
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
            VerwaltenDatenbankAusgaben dialog = new VerwaltenDatenbankAusgaben(new javax.swing.JFrame(), true);
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
    private JLabel jLabelTitel;
    private JLabel jLabelID;
    private JTextField field_ID;
    private JLabel jLabel1;
    private JTextField field_count;
    private JLabel jLabel2;
    private JTextField field_countMax;
    private JComboBox<String> field_Typ;
    private JLabel jLabelRechNr;
    private JTextField field_RechnNr;
    private JLabel jLabelRechDat;
    private JTextField field_Beschreibung;
    private JLabel jLabel6;
    private JButton BDatei;
    private JTextField field_Link;
    private JLabel jLabel3;
    private JLabel jLabelBetrag;
    private JLabel jLabelUStr;
    private JLabel jLabel5;
    private JLabel jLabelLieferant;
    private JTextField field_Betrag;
    private JLabel jLabel4;
    private JTextField field_Ustr;
    private JDateChooser field_Bezahlt;
    private JComboBox<String> field_Lieferant;
    private JLabel jLabelBeschreibung;
    private JDateChooser field_RechnDat;
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
    private JPanel jPanel1;
    private JScrollPane scrollPane1;
    private JList<String> jList1;
    // End of variables declaration//GEN-END:variables

    @SuppressWarnings("FieldMayBeFinal")
    private JFileChooser chooser;

    @SuppressWarnings("FieldMayBeFinal")
    private Integer count = 0;
    @SuppressWarnings("FieldMayBeFinal")
    private Integer countMax;

    private Connection conn;
    private Statement SQLAnfrage;
    private Statement SQLAnfrage2;
    private ResultSet result;
    private ResultSet result2;

    @SuppressWarnings("FieldMayBeFinal")
    private boolean resultIsEmpty;

    private String Kriterium = "";
    private Integer MaxID = 0;

    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private Statement SQLAnfrage_Liste;
    private ResultSet result_Liste;
}
