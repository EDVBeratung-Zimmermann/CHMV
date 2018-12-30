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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Vector;
import javax.swing.*;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.plaf.ActionMapUIResource;
import net.miginfocom.swing.*;
import static milesVerlagMain.ModulMyOwnFocusTraversalPolicy.newPolicy;

/**
 *
 * @author Thomas Zimmermann
 */
public class VerwaltenDatenbankAdressen extends javax.swing.JDialog {

    /**
     * Creates new form VerwaltenDatenbankAdressen
     *
     * @param parent
     * @param modal
     */
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public VerwaltenDatenbankAdressen(java.awt.Frame parent, boolean modal) {
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
        
        Vector<Component> order = new Vector<>(38);
        order.add(field_Typ);
        order.add(field_Zusatz_1);
        order.add(field_Namenszusatz);
        order.add(field_Vorname);
        order.add(field_Firmenname);
        order.add(field_Zusatz_2);
        order.add(field_Strasse);
        order.add(field_PLZ);
        order.add(field_Ort);
        order.add(field_Anrede);
        order.add(field_Zeitschrift);
        order.add(field_POC);
        order.add(field_Kundennummer);
        order.add(field_Anmeldung);
        order.add(field_Kennwort);
        order.add(field_Telefon);
        order.add(field_Telefax);
        order.add(field_Mobil);
        order.add(field_eMail);
        order.add(field_Web);
        order.add(field_Bank);
        order.add(field_IBAN);
        order.add(field_BIC);
        order.add(field_UstrID);
        order.add(field_Sonderkondition);
        order.add(field_Rabatt);
        order.add(field_Ustr);
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

        for (String AdressTypListe : Modulhelferlein.AdressTypListe) {
            field_Typ.addItem(AdressTypListe);
        }

        conn = null;

        // Datenbank-Treiber laden
        try {
            Class.forName(Modulhelferlein.dbDriver);
        } catch (ClassNotFoundException exept) {
            Modulhelferlein.Fehlermeldung(
                    "Treiber nicht gefunden. " + exept.getMessage());
        }

        // Verbindung zur Datenbank über die JDBC-Brücke
        try {
            conn = DriverManager.getConnection(Modulhelferlein.dbUrl, Modulhelferlein.dbUser, Modulhelferlein.dbPassword);
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung(
                    "SQL-Exception: Verbindung nicht moeglich. "
                    + exept.getMessage());
        } //try catch Datenbank

        // final Connection conn2=conn;
        if (conn != null) {
            SQLAnfrage = null; // Anfrage erzeugen

            try {
                SQLAnfrage = conn.createStatement(
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE); // Anfrage der DB conn2 zuordnen
                SQLAnfrage_Liste = conn.createStatement(
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE); // Anfrage der DB conn2 zuordnen
                result = SQLAnfrage.executeQuery("SELECT * FROM TBL_ADRESSE "
                        + " WHERE ADRESSEN_ID > '0' "
                        + " ORDER BY ADRESSEN_NAME");
                // Anzahl der Datensätze ermitteln
                countMax = 0;
                count = 0;
                field_count.setText(Integer.toString(count));
                while (result.next()) {
                    if (maxID < result.getInt("ADRESSEN_ID")) {
                        maxID = result.getInt("ADRESSEN_ID");
                    }
                    ++countMax;
                }
                field_countMax.setText(Integer.toString(countMax));
                // gehe zum ersten Datensatz - wenn nicht leer
                if (result.first()) {
                    initListeAdressbestand();
                    count = 1;
                    field_count.setText(Integer.toString(count));
                    resultIsEmpty = false;
                    field_ID.setText(Integer.toString(result.getInt("ADRESSEN_ID")));
                    field_POC.setText(result.getString("ADRESSEN_POC"));
                    field_Strasse.setText(result.getString("ADRESSEN_Strasse"));
                    field_PLZ.setText(result.getString("ADRESSEN_PLZ"));
                    field_Ort.setText(result.getString("ADRESSEN_Ort"));
                    field_Telefon.setText(result.getString("ADRESSEN_Telefon"));
                    field_Telefax.setText(result.getString("ADRESSEN_Telefax"));
                    field_eMail.setText(result.getString("ADRESSEN_eMail"));
                    field_Web.setText(result.getString("ADRESSEN_Web"));
                    field_Kundennummer.setText(result.getString("ADRESSEN_Kundennummer"));
                    field_Firmenname.setText(result.getString("ADRESSEN_Name"));
                    field_Anmeldung.setText(result.getString("ADRESSEN_Anmeldung"));
                    field_Kennwort.setText(result.getString("ADRESSEN_Kennwort"));
                    field_Mobil.setText(result.getString("ADRESSEN_Mobil"));
                    field_Vorname.setText(result.getString("ADRESSEN_Vorname"));
                    field_Anrede.setText(result.getString("ADRESSEN_Anrede"));
                    field_Namenszusatz.setText(result.getString("ADRESSEN_NAMENSZUSATZ"));
                    field_Zusatz_1.setText(result.getString("ADRESSEN_ZUSATZ_1"));
                    field_Zusatz_2.setText(result.getString("ADRESSEN_ZUSATZ_2"));
                    field_Zusatz_3.setText(result.getString("ADRESSEN_ZUSATZ_3"));
                    field_Zeitschrift.setText(result.getString("ADRESSEN_ZEITSCHRIFT"));
                    field_UstrID.setText(result.getString("ADRESSEN_USTR_ID"));
                    field_IBAN.setText(result.getString("ADRESSEN_IBAN"));
                    field_BIC.setText(result.getString("ADRESSEN_BIC"));
                    field_Bank.setText(result.getString("ADRESSEN_BANK"));
                    field_Sonderkondition.setSelected(result.getBoolean("ADRESSEN_SONDERKONDITION"));
                    field_Ustr.setSelected(result.getBoolean("ADRESSEN_USTR"));
                    field_Rabatt.setText(Integer.toString(result.getInt("ADRESSEN_RABATT")));

                    String typ = result.getString("ADRESSEN_Typ");
                    // {"Autor", "Kunde", "Rezensent", "Druckerei", "Lieferant", "Sonstige"};
                    switch (typ) {
                        case "Autor":
                            field_Typ.setSelectedIndex(0);
                            break;
                        case "Kunde":
                            field_Typ.setSelectedIndex(1);
                            break;
                        case "Rezensent":
                            field_Typ.setSelectedIndex(2);
                            break;
                        case "Druckerei":
                            field_Typ.setSelectedIndex(3);
                            break;
                        case "Lieferant":
                            field_Typ.setSelectedIndex(4);
                            break;
                        case "Zeitschrift":
                            field_Typ.setSelectedIndex(5);
                            break;
                        default:
                            field_Typ.setSelectedIndex(6);
                            break;
                    }
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
                Modulhelferlein.Fehlermeldung(
                        "SQL-Exception: SQL-Anfrage nicht moeglich. "
                        + exept.getMessage());
            } // try catch resultset
        } // if conn

    }

    /**
     * Diese Methode initialisiert die Liste mit dem Adressbestand Der Aufruf
     * erfolgt beim Start des Dialoges sowie beim Update von Adressen
     */
    private void initListeAdressbestand() {
        try {
            String eintragAutor = "";

            // Auswahlliste f?r Autoren erstellen
            listModel.clear();
            result_Liste = SQLAnfrage_Liste.executeQuery(
                    "SELECT * FROM tbl_adresse WHERE ADRESSEN_Typ = 'Autor' "
                    + "OR ADRESSEN_Typ = 'Rezensent' "
                    + "OR ADRESSEN_Typ = 'Druckerei' "
                    + "OR ADRESSEN_Typ = 'Kunde' "
                    + "OR ADRESSEN_Typ = 'Lieferant' "
                    + "ORDER BY ADRESSEN_NAME");
            while (result_Liste.next()) {
                eintragAutor = result_Liste.getString("ADRESSEN_NAME") + ", "
                        + result_Liste.getString("ADRESSEN_VORNAME") + ", "
                        + result_Liste.getString("ADRESSEN_TYP") + ", "
                        + result_Liste.getString("ADRESSEN_ID");
                listModel.addElement(eintragAutor);
            } // while
            listModel.addElement("--- ZEITSCHRIFT ---");
            result_Liste = SQLAnfrage_Liste.executeQuery(
                    "SELECT * FROM tbl_adresse WHERE ADRESSEN_Typ = 'Zeitschrift' "
                    + "ORDER BY ADRESSEN_Zeitschrift");
            while (result_Liste.next()) {
                eintragAutor = result_Liste.getString("ADRESSEN_Zeitschrift") + ", "
                        + result_Liste.getString("ADRESSEN_ID");
                listModel.addElement(eintragAutor);
            } // while
            listModel.addElement("--- SONSTIGES ---");
            result_Liste = SQLAnfrage_Liste.executeQuery(
                    "SELECT * FROM tbl_adresse WHERE ADRESSEN_Typ = 'Sonstige' "
                    + "ORDER BY ADRESSEN_NAME");
            while (result_Liste.next()) {
                eintragAutor = result_Liste.getString("ADRESSEN_NAME") + ", "
                        + result_Liste.getString("ADRESSEN_VORNAME") + ", "
                        + result_Liste.getString("ADRESSEN_ID");
                listModel.addElement(eintragAutor);
            } // while
        } catch (SQLException ex) {
            Modulhelferlein.Fehlermeldung("Adressenverwalten", "Adressbestand auflisten: SQL-Exception", ex.getMessage());
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
        jLabelID = new JLabel();
        jLabel4 = new JLabel();
        jLabel2 = new JLabel();
        field_count = new JTextField();
        jLabel3 = new JLabel();
        field_countMax = new JTextField();
        hSpacer1 = new JPanel(null);
        jLabel29 = new JLabel();
        jLabel5 = new JLabel();
        field_Zusatz_1 = new JTextField();
        jLabel6 = new JLabel();
        field_Namenszusatz = new JTextField();
        jLabel9 = new JLabel();
        field_Zusatz_2 = new JTextField();
        jLabel10 = new JLabel();
        field_Strasse = new JTextField();
        jLabel11 = new JLabel();
        jLabel12 = new JLabel();
        field_PLZ = new JTextField();
        jLabel13 = new JLabel();
        field_Zeitschrift = new JTextField();
        jLabel25 = new JLabel();
        field_Anrede = new JTextField();
        field_ID = new JTextField();
        field_Typ = new JComboBox<>();
        jScrollPane1 = new JScrollPane();
        jListAdressbestand = new JList<>();
        jLabel20 = new JLabel();
        jLabel18 = new JLabel();
        jLabel19 = new JLabel();
        field_Kundennummer = new JTextField();
        field_Anmeldung = new JTextField();
        field_Kennwort = new JTextField();
        jLabel7 = new JLabel();
        jLabel8 = new JLabel();
        jLabel21 = new JLabel();
        jLabel22 = new JLabel();
        jLabel23 = new JLabel();
        field_Vorname = new JTextField();
        field_Firmenname = new JTextField();
        field_Telefon = new JTextField();
        field_Telefax = new JTextField();
        field_Mobil = new JTextField();
        jLabel24 = new JLabel();
        field_eMail = new JTextField();
        jLabel26 = new JLabel();
        field_Web = new JTextField();
        jLabel28 = new JLabel();
        field_Ort = new JTextField();
        field_Bank = new JTextField();
        jLabel30 = new JLabel();
        jLabel15 = new JLabel();
        jLabel16 = new JLabel();
        jLabel17 = new JLabel();
        field_Zusatz_3 = new JTextField();
        jButtonLand = new JButton();
        field_IBAN = new JTextField();
        field_BIC = new JTextField();
        field_UstrID = new JTextField();
        field_Sonderkondition = new JCheckBox();
        jLabel27 = new JLabel();
        field_Rabatt = new JTextField();
        field_Ustr = new JCheckBox();
        jLabel14 = new JLabel();
        field_POC = new JTextField();
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
        setMinimumSize(new Dimension(975, 600));
        Container contentPane = getContentPane();

        //======== panel1 ========
        {
            panel1.setLayout(null);

            //---- jLabel1 ----
            jLabel1.setFont(new Font("Tahoma", Font.BOLD, 12));
            jLabel1.setText("Verwalten der Adressen");
            panel1.add(jLabel1);
            jLabel1.setBounds(0, 0, 275, 20);

            //---- jLabelID ----
            jLabelID.setText("ID");
            panel1.add(jLabelID);
            jLabelID.setBounds(335, 0, 50, 20);

            //---- jLabel4 ----
            jLabel4.setText("Adresstyp");
            panel1.add(jLabel4);
            jLabel4.setBounds(390, 0, 105, 20);

            //---- jLabel2 ----
            jLabel2.setText("Datensatz Nr.");
            panel1.add(jLabel2);
            jLabel2.setBounds(615, 0, jLabel2.getPreferredSize().width, 20);

            //---- field_count ----
            field_count.setEditable(false);
            field_count.setText("000");
            field_count.setEnabled(false);
            field_count.setFocusable(false);
            field_count.setMaximumSize(new Dimension(30, 20));
            field_count.setMinimumSize(new Dimension(30, 20));
            field_count.setPreferredSize(null);
            panel1.add(field_count);
            field_count.setBounds(687, 0, 30, field_count.getPreferredSize().height);

            //---- jLabel3 ----
            jLabel3.setText("von");
            panel1.add(jLabel3);
            jLabel3.setBounds(722, 0, jLabel3.getPreferredSize().width, 20);

            //---- field_countMax ----
            field_countMax.setEditable(false);
            field_countMax.setText("000");
            field_countMax.setEnabled(false);
            field_countMax.setFocusable(false);
            field_countMax.setMaximumSize(new Dimension(30, 20));
            field_countMax.setMinimumSize(new Dimension(30, 20));
            field_countMax.setPreferredSize(new Dimension(35, 25));
            panel1.add(field_countMax);
            field_countMax.setBounds(745, 0, 30, 20);
            panel1.add(hSpacer1);
            hSpacer1.setBounds(780, 0, hSpacer1.getPreferredSize().width, 20);

            //---- jLabel29 ----
            jLabel29.setFont(new Font("Tahoma", Font.BOLD, 11));
            jLabel29.setText("Adress\u00fcbersicht");
            panel1.add(jLabel29);
            jLabel29.setBounds(795, 0, 150, 20);

            //---- jLabel5 ----
            jLabel5.setText("Adresszusatz - Adresse Zeile 1");
            panel1.add(jLabel5);
            jLabel5.setBounds(0, 50, 330, jLabel5.getPreferredSize().height);

            //---- field_Zusatz_1 ----
            field_Zusatz_1.setPreferredSize(new Dimension(300, 25));
            panel1.add(field_Zusatz_1);
            field_Zusatz_1.setBounds(0, 69, 330, field_Zusatz_1.getPreferredSize().height);

            //---- jLabel6 ----
            jLabel6.setText("Titel");
            panel1.add(jLabel6);
            jLabel6.setBounds(0, 99, 55, jLabel6.getPreferredSize().height);

            //---- field_Namenszusatz ----
            field_Namenszusatz.setText("jTextField1");
            field_Namenszusatz.setPreferredSize(new Dimension(300, 25));
            panel1.add(field_Namenszusatz);
            field_Namenszusatz.setBounds(0, 118, 55, field_Namenszusatz.getPreferredSize().height);

            //---- jLabel9 ----
            jLabel9.setText("Adresszusatz - Adresse Zeile 3");
            panel1.add(jLabel9);
            jLabel9.setBounds(0, 148, 330, jLabel9.getPreferredSize().height);

            //---- field_Zusatz_2 ----
            field_Zusatz_2.setText("jTextField1");
            field_Zusatz_2.setPreferredSize(new Dimension(300, 25));
            panel1.add(field_Zusatz_2);
            field_Zusatz_2.setBounds(0, 167, 330, field_Zusatz_2.getPreferredSize().height);

            //---- jLabel10 ----
            jLabel10.setText("Stra\u00dfe");
            panel1.add(jLabel10);
            jLabel10.setBounds(0, 197, 330, jLabel10.getPreferredSize().height);

            //---- field_Strasse ----
            field_Strasse.setText("jTextField1");
            field_Strasse.setPreferredSize(new Dimension(300, 25));
            panel1.add(field_Strasse);
            field_Strasse.setBounds(0, 216, 330, field_Strasse.getPreferredSize().height);

            //---- jLabel11 ----
            jLabel11.setText("PLZ");
            panel1.add(jLabel11);
            jLabel11.setBounds(0, 246, 110, jLabel11.getPreferredSize().height);

            //---- jLabel12 ----
            jLabel12.setText("Ort");
            panel1.add(jLabel12);
            jLabel12.setBounds(115, 246, 105, jLabel12.getPreferredSize().height);

            //---- field_PLZ ----
            field_PLZ.setText("jTextField1");
            field_PLZ.setPreferredSize(new Dimension(300, 25));
            panel1.add(field_PLZ);
            field_PLZ.setBounds(0, 265, 110, field_PLZ.getPreferredSize().height);

            //---- jLabel13 ----
            jLabel13.setText("Zeitschrift");
            panel1.add(jLabel13);
            jLabel13.setBounds(0, 402, 330, jLabel13.getPreferredSize().height);

            //---- field_Zeitschrift ----
            field_Zeitschrift.setText("jTextField1");
            field_Zeitschrift.setPreferredSize(new Dimension(300, 25));
            panel1.add(field_Zeitschrift);
            field_Zeitschrift.setBounds(0, 421, 330, field_Zeitschrift.getPreferredSize().height);

            //---- jLabel25 ----
            jLabel25.setText("Anrede");
            panel1.add(jLabel25);
            jLabel25.setBounds(0, 344, 330, 23);

            //---- field_Anrede ----
            field_Anrede.setText("jTextField1");
            field_Anrede.setPreferredSize(new Dimension(300, 25));
            panel1.add(field_Anrede);
            field_Anrede.setBounds(0, 372, 330, field_Anrede.getPreferredSize().height);

            //---- field_ID ----
            field_ID.setEditable(false);
            field_ID.setText("0000");
            field_ID.setEnabled(false);
            field_ID.setFocusable(false);
            field_ID.setMaximumSize(new Dimension(30, 20));
            field_ID.setMinimumSize(new Dimension(30, 20));
            panel1.add(field_ID);
            field_ID.setBounds(new Rectangle(new Point(335, 25), field_ID.getPreferredSize()));

            //---- field_Typ ----
            field_Typ.setModel(new DefaultComboBoxModel<>(new String[] {

            }));
            field_Typ.setName("");
            panel1.add(field_Typ);
            field_Typ.setBounds(390, 25, 165, field_Typ.getPreferredSize().height);

            //======== jScrollPane1 ========
            {
                jScrollPane1.setPreferredSize(new Dimension(150, 130));
                jScrollPane1.setMinimumSize(new Dimension(150, 23));

                //---- jListAdressbestand ----
                jListAdressbestand.setModel(listModel);
                jListAdressbestand.setMinimumSize(new Dimension(150, 450));
                jListAdressbestand.setMaximumSize(new Dimension(150, 450));
                jListAdressbestand.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        jListAdressbestandMouseClicked(e);
                    }
                });
                jScrollPane1.setViewportView(jListAdressbestand);
            }
            panel1.add(jScrollPane1);
            jScrollPane1.setBounds(795, 50, jScrollPane1.getPreferredSize().width, 396);

            //---- jLabel20 ----
            jLabel20.setText("Kundennummer");
            panel1.add(jLabel20);
            jLabel20.setBounds(390, 50, 105, jLabel20.getPreferredSize().height);

            //---- jLabel18 ----
            jLabel18.setText("Nutzerkennung");
            panel1.add(jLabel18);
            jLabel18.setBounds(500, 50, 110, jLabel18.getPreferredSize().height);

            //---- jLabel19 ----
            jLabel19.setText("Kennwort");
            panel1.add(jLabel19);
            jLabel19.setBounds(615, 50, 102, jLabel19.getPreferredSize().height);

            //---- field_Kundennummer ----
            field_Kundennummer.setText("jTextField1");
            field_Kundennummer.setPreferredSize(new Dimension(300, 25));
            panel1.add(field_Kundennummer);
            field_Kundennummer.setBounds(390, 69, 105, field_Kundennummer.getPreferredSize().height);

            //---- field_Anmeldung ----
            field_Anmeldung.setText("jTextField1");
            field_Anmeldung.setPreferredSize(new Dimension(300, 25));
            panel1.add(field_Anmeldung);
            field_Anmeldung.setBounds(500, 69, 110, field_Anmeldung.getPreferredSize().height);

            //---- field_Kennwort ----
            field_Kennwort.setText("jTextField1");
            field_Kennwort.setPreferredSize(new Dimension(300, 25));
            panel1.add(field_Kennwort);
            field_Kennwort.setBounds(615, 69, 160, field_Kennwort.getPreferredSize().height);

            //---- jLabel7 ----
            jLabel7.setText("Vorname");
            panel1.add(jLabel7);
            jLabel7.setBounds(60, 99, 50, jLabel7.getPreferredSize().height);

            //---- jLabel8 ----
            jLabel8.setText("Name");
            panel1.add(jLabel8);
            jLabel8.setBounds(170, 99, 50, jLabel8.getPreferredSize().height);

            //---- jLabel21 ----
            jLabel21.setText("Telefon");
            panel1.add(jLabel21);
            jLabel21.setBounds(390, 99, 105, jLabel21.getPreferredSize().height);

            //---- jLabel22 ----
            jLabel22.setText("Telefax");
            panel1.add(jLabel22);
            jLabel22.setBounds(560, 99, 75, jLabel22.getPreferredSize().height);

            //---- jLabel23 ----
            jLabel23.setText("Mobiltelefon");
            panel1.add(jLabel23);
            jLabel23.setBounds(640, 99, 100, jLabel23.getPreferredSize().height);

            //---- field_Vorname ----
            field_Vorname.setText("jTextField1");
            field_Vorname.setPreferredSize(new Dimension(300, 25));
            panel1.add(field_Vorname);
            field_Vorname.setBounds(60, 118, 105, field_Vorname.getPreferredSize().height);

            //---- field_Firmenname ----
            field_Firmenname.setText("jTextField1");
            field_Firmenname.setPreferredSize(new Dimension(300, 25));
            panel1.add(field_Firmenname);
            field_Firmenname.setBounds(170, 118, 160, field_Firmenname.getPreferredSize().height);

            //---- field_Telefon ----
            field_Telefon.setText("jTextField1");
            field_Telefon.setPreferredSize(new Dimension(300, 25));
            panel1.add(field_Telefon);
            field_Telefon.setBounds(390, 118, 165, field_Telefon.getPreferredSize().height);

            //---- field_Telefax ----
            field_Telefax.setText("jTextField1");
            field_Telefax.setPreferredSize(new Dimension(300, 25));
            panel1.add(field_Telefax);
            field_Telefax.setBounds(560, 118, 75, field_Telefax.getPreferredSize().height);

            //---- field_Mobil ----
            field_Mobil.setText("jTextField1");
            field_Mobil.setPreferredSize(new Dimension(300, 25));
            panel1.add(field_Mobil);
            field_Mobil.setBounds(640, 118, 135, field_Mobil.getPreferredSize().height);

            //---- jLabel24 ----
            jLabel24.setText("E-Mail");
            panel1.add(jLabel24);
            jLabel24.setBounds(390, 148, 105, jLabel24.getPreferredSize().height);

            //---- field_eMail ----
            field_eMail.setText("jTextField1");
            field_eMail.setPreferredSize(new Dimension(300, 25));
            panel1.add(field_eMail);
            field_eMail.setBounds(390, 167, 385, field_eMail.getPreferredSize().height);

            //---- jLabel26 ----
            jLabel26.setText("Webseite");
            panel1.add(jLabel26);
            jLabel26.setBounds(390, 197, 292, jLabel26.getPreferredSize().height);

            //---- field_Web ----
            field_Web.setText("jTextField1");
            field_Web.setPreferredSize(new Dimension(300, 25));
            panel1.add(field_Web);
            field_Web.setBounds(390, 216, 385, field_Web.getPreferredSize().height);

            //---- jLabel28 ----
            jLabel28.setText("Bank");
            panel1.add(jLabel28);
            jLabel28.setBounds(390, 246, 50, jLabel28.getPreferredSize().height);

            //---- field_Ort ----
            field_Ort.setText("jTextField1");
            field_Ort.setPreferredSize(new Dimension(300, 25));
            panel1.add(field_Ort);
            field_Ort.setBounds(115, 265, 215, field_Ort.getPreferredSize().height);

            //---- field_Bank ----
            field_Bank.setText("jTextField1");
            field_Bank.setPreferredSize(new Dimension(340, 25));
            panel1.add(field_Bank);
            field_Bank.setBounds(390, 265, 385, field_Bank.getPreferredSize().height);

            //---- jLabel30 ----
            jLabel30.setText("Zeile 6 - Land");
            panel1.add(jLabel30);
            jLabel30.setBounds(0, 295, 330, jLabel30.getPreferredSize().height);

            //---- jLabel15 ----
            jLabel15.setText("IBAN");
            panel1.add(jLabel15);
            jLabel15.setBounds(390, 295, 50, jLabel15.getPreferredSize().height);

            //---- jLabel16 ----
            jLabel16.setText("BIC");
            panel1.add(jLabel16);
            jLabel16.setBounds(560, 295, 75, jLabel16.getPreferredSize().height);

            //---- jLabel17 ----
            jLabel17.setText("UStrID");
            panel1.add(jLabel17);
            jLabel17.setBounds(687, 295, 53, jLabel17.getPreferredSize().height);

            //---- field_Zusatz_3 ----
            field_Zusatz_3.setEditable(false);
            field_Zusatz_3.setText("jTextField1");
            field_Zusatz_3.setPreferredSize(new Dimension(59, 25));
            panel1.add(field_Zusatz_3);
            field_Zusatz_3.setBounds(0, 314, 275, field_Zusatz_3.getPreferredSize().height);

            //---- jButtonLand ----
            jButtonLand.setText("...");
            jButtonLand.addActionListener(e -> jButtonLandActionPerformed(e));
            panel1.add(jButtonLand);
            jButtonLand.setBounds(280, 314, 50, 25);

            //---- field_IBAN ----
            field_IBAN.setText("jTextField1");
            field_IBAN.setPreferredSize(new Dimension(300, 25));
            panel1.add(field_IBAN);
            field_IBAN.setBounds(390, 314, 165, field_IBAN.getPreferredSize().height);

            //---- field_BIC ----
            field_BIC.setText("jTextField1");
            field_BIC.setPreferredSize(new Dimension(300, 25));
            panel1.add(field_BIC);
            field_BIC.setBounds(560, 314, 75, field_BIC.getPreferredSize().height);

            //---- field_UstrID ----
            field_UstrID.setText("jTextField1");
            field_UstrID.setPreferredSize(new Dimension(300, 25));
            panel1.add(field_UstrID);
            field_UstrID.setBounds(687, 314, 88, field_UstrID.getPreferredSize().height);

            //---- field_Sonderkondition ----
            field_Sonderkondition.setText("Sonderkonditionen");
            panel1.add(field_Sonderkondition);
            field_Sonderkondition.setBounds(390, 344, 350, field_Sonderkondition.getPreferredSize().height);

            //---- jLabel27 ----
            jLabel27.setText("Rabatt in %");
            panel1.add(jLabel27);
            jLabel27.setBounds(390, 372, 105, 25);

            //---- field_Rabatt ----
            field_Rabatt.setText("jTextField1");
            field_Rabatt.setPreferredSize(new Dimension(300, 25));
            field_Rabatt.addActionListener(e -> field_RabattActionPerformed(e));
            panel1.add(field_Rabatt);
            field_Rabatt.setBounds(500, 372, 55, field_Rabatt.getPreferredSize().height);

            //---- field_Ustr ----
            field_Ustr.setText("Ustr auf rabatt. VK-Preis");
            panel1.add(field_Ustr);
            field_Ustr.setBounds(615, 372, 160, 25);

            //---- jLabel14 ----
            jLabel14.setText("Ansprechpartner");
            panel1.add(jLabel14);
            jLabel14.setBounds(390, 402, 105, jLabel14.getPreferredSize().height);

            //---- field_POC ----
            field_POC.setText("jTextField1");
            field_POC.setPreferredSize(new Dimension(300, 25));
            panel1.add(field_POC);
            field_POC.setBounds(390, 421, 385, field_POC.getPreferredSize().height);

            //---- Anfang ----
            Anfang.setText("<<");
            Anfang.setToolTipText("Gehe zum Anfang - erster Datensatz");
            Anfang.setMaximumSize(new Dimension(50, 25));
            Anfang.setMinimumSize(new Dimension(50, 25));
            Anfang.setPreferredSize(new Dimension(50, 23));
            Anfang.addActionListener(e -> AnfangActionPerformed(e));
            panel1.add(Anfang);
            Anfang.setBounds(0, 476, Anfang.getPreferredSize().width, 25);

            //---- Zurueck ----
            Zurueck.setText("<");
            Zurueck.setToolTipText("Gehe zum vorherigen Datensatz");
            Zurueck.setMaximumSize(new Dimension(50, 25));
            Zurueck.setMinimumSize(new Dimension(50, 25));
            Zurueck.setPreferredSize(new Dimension(50, 23));
            Zurueck.addActionListener(e -> ZurueckActionPerformed(e));
            panel1.add(Zurueck);
            Zurueck.setBounds(60, 476, Zurueck.getPreferredSize().width, 25);

            //---- Vor ----
            Vor.setText(">");
            Vor.setToolTipText("Gehe zum n\u00e4chsten Datensatz");
            Vor.setMaximumSize(new Dimension(50, 25));
            Vor.setMinimumSize(new Dimension(50, 25));
            Vor.setPreferredSize(new Dimension(50, 23));
            Vor.addActionListener(e -> VorActionPerformed(e));
            panel1.add(Vor);
            Vor.setBounds(115, 476, Vor.getPreferredSize().width, 25);

            //---- Ende ----
            Ende.setText(">>");
            Ende.setToolTipText("Gehe zum Ende - zum letzten Datensatz");
            Ende.setMaximumSize(new Dimension(50, 25));
            Ende.setMinimumSize(new Dimension(50, 25));
            Ende.setPreferredSize(new Dimension(50, 23));
            Ende.addActionListener(e -> EndeActionPerformed(e));
            panel1.add(Ende);
            Ende.setBounds(170, 476, Ende.getPreferredSize().width, 25);

            //---- Update ----
            Update.setText("!");
            Update.setToolTipText("Datensatz aktualisieren");
            Update.setMaximumSize(new Dimension(50, 25));
            Update.setMinimumSize(new Dimension(50, 25));
            Update.setPreferredSize(new Dimension(50, 23));
            Update.addActionListener(e -> UpdateActionPerformed(e));
            panel1.add(Update);
            Update.setBounds(225, 476, Update.getPreferredSize().width, 25);

            //---- Einfuegen ----
            Einfuegen.setText("+");
            Einfuegen.setToolTipText("Datensatz einf\u00fcgen");
            Einfuegen.setMaximumSize(new Dimension(50, 25));
            Einfuegen.setMinimumSize(new Dimension(50, 25));
            Einfuegen.setPreferredSize(new Dimension(50, 23));
            Einfuegen.addActionListener(e -> EinfuegenActionPerformed(e));
            panel1.add(Einfuegen);
            Einfuegen.setBounds(280, 476, Einfuegen.getPreferredSize().width, 25);

            //---- Loeschen ----
            Loeschen.setText("-");
            Loeschen.setToolTipText("Datensatz l\u00f6schen");
            Loeschen.setMaximumSize(new Dimension(50, 25));
            Loeschen.setMinimumSize(new Dimension(50, 25));
            Loeschen.setPreferredSize(new Dimension(50, 23));
            Loeschen.addActionListener(e -> LoeschenActionPerformed(e));
            panel1.add(Loeschen);
            Loeschen.setBounds(335, 476, Loeschen.getPreferredSize().width, 25);

            //---- Suchen ----
            Suchen.setText("?");
            Suchen.setToolTipText("Datensatz suchen");
            Suchen.setMaximumSize(new Dimension(50, 25));
            Suchen.setMinimumSize(new Dimension(50, 25));
            Suchen.setPreferredSize(new Dimension(50, 23));
            Suchen.addActionListener(e -> SuchenActionPerformed(e));
            panel1.add(Suchen);
            Suchen.setBounds(390, 476, Suchen.getPreferredSize().width, 25);

            //---- WSuchen ----
            WSuchen.setText("...");
            WSuchen.setToolTipText("Weitersuchen");
            WSuchen.setMaximumSize(new Dimension(50, 25));
            WSuchen.setMinimumSize(new Dimension(50, 25));
            WSuchen.setPreferredSize(new Dimension(50, 23));
            WSuchen.addActionListener(e -> WSuchenActionPerformed(e));
            panel1.add(WSuchen);
            WSuchen.setBounds(445, 476, WSuchen.getPreferredSize().width, 25);

            //---- Drucken ----
            Drucken.setText("D");
            Drucken.setToolTipText("Liste der Ausgaben drucken");
            Drucken.setMaximumSize(new Dimension(50, 25));
            Drucken.setMinimumSize(new Dimension(50, 25));
            Drucken.setPreferredSize(new Dimension(50, 23));
            Drucken.addActionListener(e -> DruckenActionPerformed(e));
            panel1.add(Drucken);
            Drucken.setBounds(500, 476, Drucken.getPreferredSize().width, 25);

            //---- Schliessen ----
            Schliessen.setText("X");
            Schliessen.setMaximumSize(new Dimension(50, 25));
            Schliessen.setMinimumSize(new Dimension(50, 25));
            Schliessen.setPreferredSize(new Dimension(50, 23));
            Schliessen.addActionListener(e -> SchliessenActionPerformed(e));
            panel1.add(Schliessen);
            Schliessen.setBounds(560, 476, Schliessen.getPreferredSize().width, 25);

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
                    .addComponent(panel1, GroupLayout.PREFERRED_SIZE, 945, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(18, Short.MAX_VALUE))
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(panel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(16, Short.MAX_VALUE))
        );
        setSize(975, 560);
        setLocationRelativeTo(getOwner());
    }// </editor-fold>//GEN-END:initComponents

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

                field_ID.setText(Integer.toString(result.getInt("ADRESSEN_ID")));
                field_POC.setText(result.getString("ADRESSEN_POC"));
                field_Strasse.setText(result.getString("ADRESSEN_Strasse"));
                field_PLZ.setText(result.getString("ADRESSEN_PLZ"));
                field_Ort.setText(result.getString("ADRESSEN_Ort"));
                field_Telefon.setText(result.getString("ADRESSEN_Telefon"));
                field_Telefax.setText(result.getString("ADRESSEN_Telefax"));
                field_eMail.setText(result.getString("ADRESSEN_eMail"));
                field_Web.setText(result.getString("ADRESSEN_Web"));
                field_Kundennummer.setText(result.getString("ADRESSEN_Kundennummer"));
                field_Firmenname.setText(result.getString("ADRESSEN_Name"));
                field_Anmeldung.setText(result.getString("ADRESSEN_Anmeldung"));
                field_Kennwort.setText(result.getString("ADRESSEN_Kennwort"));
                field_Mobil.setText(result.getString("ADRESSEN_Mobil"));
                field_Vorname.setText(result.getString("ADRESSEN_Vorname"));
                field_Anrede.setText(result.getString("ADRESSEN_Anrede"));
                field_Namenszusatz.setText(result.getString("ADRESSEN_NAMENSZUSATZ"));
                field_Zusatz_1.setText(result.getString("ADRESSEN_ZUSATZ_1"));
                field_Zusatz_2.setText(result.getString("ADRESSEN_ZUSATZ_2"));
                field_Zusatz_3.setText(result.getString("ADRESSEN_ZUSATZ_3"));
                field_Zeitschrift.setText(result.getString("ADRESSEN_ZEITSCHRIFT"));
                field_UstrID.setText(result.getString("ADRESSEN_USTR_ID"));
                field_Sonderkondition.setSelected(result.getBoolean("ADRESSEN_SONDERKONDITION"));
                field_Ustr.setSelected(result.getBoolean("ADRESSEN_USTR"));
                field_Rabatt.setText(Integer.toString(result.getInt("ADRESSEN_RABATT")));
                String typ = result.getString("ADRESSEN_Typ");
                field_IBAN.setText(result.getString("ADRESSEN_IBAN"));
                field_BIC.setText(result.getString("ADRESSEN_BIC"));
                field_Bank.setText(result.getString("ADRESSEN_BANK"));
                // {"Autor", "Kunde", "Rezensent", "Druckerei", "Lieferant", "Sonstige"};
                switch (typ) {
                    case "Autor":
                        field_Typ.setSelectedIndex(0);
                        break;
                    case "Kunde":
                        field_Typ.setSelectedIndex(1);
                        break;
                    case "Rezensent":
                        field_Typ.setSelectedIndex(2);
                        break;
                    case "Druckerei":
                        field_Typ.setSelectedIndex(3);
                        break;
                    case "Lieferant":
                        field_Typ.setSelectedIndex(4);
                        break;
                    case "Zeitschrift":
                        field_Typ.setSelectedIndex(5);
                        break;
                    default:
                        field_Typ.setSelectedIndex(6);
                        break;
                }
            } else {
                result.next();

            }

        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: ", exept.getMessage());
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

            field_ID.setText(Integer.toString(result.getInt("ADRESSEN_ID")));
            field_POC.setText(result.getString("ADRESSEN_POC"));
            field_Strasse.setText(result.getString("ADRESSEN_Strasse"));
            field_PLZ.setText(result.getString("ADRESSEN_PLZ"));
            field_Ort.setText(result.getString("ADRESSEN_Ort"));
            field_Telefon.setText(result.getString("ADRESSEN_Telefon"));
            field_Telefax.setText(result.getString("ADRESSEN_Telefax"));
            field_eMail.setText(result.getString("ADRESSEN_eMail"));
            field_Web.setText(result.getString("ADRESSEN_Web"));
            field_Kundennummer.setText(result.getString("ADRESSEN_Kundennummer"));
            field_Firmenname.setText(result.getString("ADRESSEN_Name"));
            field_Anmeldung.setText(result.getString("ADRESSEN_Anmeldung"));
            field_Kennwort.setText(result.getString("ADRESSEN_Kennwort"));
            field_Mobil.setText(result.getString("ADRESSEN_Mobil"));
            field_Vorname.setText(result.getString("ADRESSEN_Vorname"));
            field_Anrede.setText(result.getString("ADRESSEN_Anrede"));
            field_Zeitschrift.setText(result.getString("ADRESSEN_ZEITSCHRIFT"));
            field_Namenszusatz.setText(result.getString("ADRESSEN_NAMENSZUSATZ"));
            field_Zusatz_1.setText(result.getString("ADRESSEN_ZUSATZ_1"));
            field_Zusatz_2.setText(result.getString("ADRESSEN_ZUSATZ_2"));
            field_Zusatz_3.setText(result.getString("ADRESSEN_ZUSATZ_3"));
            field_UstrID.setText(result.getString("ADRESSEN_USTR_ID"));
            field_Sonderkondition.setSelected(result.getBoolean("ADRESSEN_SONDERKONDITION"));
            field_Ustr.setSelected(result.getBoolean("ADRESSEN_USTR"));
            field_Rabatt.setText(Integer.toString(result.getInt("ADRESSEN_RABATT")));
            String typ = result.getString("ADRESSEN_Typ");
            field_IBAN.setText(result.getString("ADRESSEN_IBAN"));
            field_BIC.setText(result.getString("ADRESSEN_BIC"));
            field_Bank.setText(result.getString("ADRESSEN_BANK"));
            // {"Autor", "Kunde", "Rezensent", "Druckerei", "Lieferant", "Sonstige"};
            switch (typ) {
                case "Autor":
                    field_Typ.setSelectedIndex(0);
                    break;
                case "Kunde":
                    field_Typ.setSelectedIndex(1);
                    break;
                case "Rezensent":
                    field_Typ.setSelectedIndex(2);
                    break;
                case "Druckerei":
                    field_Typ.setSelectedIndex(3);
                    break;
                case "Lieferant":
                    field_Typ.setSelectedIndex(4);
                    break;
                case "Zeitschrift":
                    field_Typ.setSelectedIndex(5);
                    break;
                default:
                    field_Typ.setSelectedIndex(6);
                    break;
            }
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: ", exept.getMessage());
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
            field_ID.setText(Integer.toString(result.getInt("ADRESSEN_ID")));
            field_POC.setText(result.getString("ADRESSEN_POC"));
            field_Strasse.setText(result.getString("ADRESSEN_Strasse"));
            field_PLZ.setText(result.getString("ADRESSEN_PLZ"));
            field_Ort.setText(result.getString("ADRESSEN_Ort"));
            field_Telefon.setText(result.getString("ADRESSEN_Telefon"));
            field_Telefax.setText(result.getString("ADRESSEN_Telefax"));
            field_eMail.setText(result.getString("ADRESSEN_eMail"));
            field_Web.setText(result.getString("ADRESSEN_Web"));
            field_Kundennummer.setText(result.getString("ADRESSEN_Kundennummer"));
            field_Firmenname.setText(result.getString("ADRESSEN_Name"));
            field_Anmeldung.setText(result.getString("ADRESSEN_Anmeldung"));
            field_Kennwort.setText(result.getString("ADRESSEN_Kennwort"));
            field_Mobil.setText(result.getString("ADRESSEN_Mobil"));
            field_Vorname.setText(result.getString("ADRESSEN_Vorname"));
            field_Anrede.setText(result.getString("ADRESSEN_Anrede"));
            field_Namenszusatz.setText(result.getString("ADRESSEN_NAMENSZUSATZ"));
            field_Zusatz_1.setText(result.getString("ADRESSEN_ZUSATZ_1"));
            field_Zusatz_2.setText(result.getString("ADRESSEN_ZUSATZ_2"));
            field_Zusatz_3.setText(result.getString("ADRESSEN_ZUSATZ_3"));
            field_Zeitschrift.setText(result.getString("ADRESSEN_ZEITSCHRIFT"));
            field_UstrID.setText(result.getString("ADRESSEN_USTR_ID"));
            field_Sonderkondition.setSelected(result.getBoolean("ADRESSEN_SONDERKONDITION"));
            field_Ustr.setSelected(result.getBoolean("ADRESSEN_USTR"));
            field_Rabatt.setText(Integer.toString(result.getInt("ADRESSEN_RABATT")));
            String typ = result.getString("ADRESSEN_Typ");
            field_IBAN.setText(result.getString("ADRESSEN_IBAN"));
            field_BIC.setText(result.getString("ADRESSEN_BIC"));
            field_Bank.setText(result.getString("ADRESSEN_BANK"));
            // {"Autor", "Kunde", "Rezensent", "Druckerei", "Lieferant", "Sonstige"};
            switch (typ) {
                case "Autor":
                    field_Typ.setSelectedIndex(0);
                    break;
                case "Kunde":
                    field_Typ.setSelectedIndex(1);
                    break;
                case "Rezensent":
                    field_Typ.setSelectedIndex(2);
                    break;
                case "Druckerei":
                    field_Typ.setSelectedIndex(3);
                    break;
                case "Lieferant":
                    field_Typ.setSelectedIndex(4);
                    break;
                case "Zeitschrift":
                    field_Typ.setSelectedIndex(5);
                    break;
                default:
                    field_Typ.setSelectedIndex(6);
                    break;
            }
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: ", exept.getMessage());
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

            field_ID.setText(Integer.toString(result.getInt("ADRESSEN_ID")));
            field_POC.setText(result.getString("ADRESSEN_POC"));
            field_Strasse.setText(result.getString("ADRESSEN_Strasse"));
            field_PLZ.setText(result.getString("ADRESSEN_PLZ"));
            field_Ort.setText(result.getString("ADRESSEN_Ort"));
            field_Telefon.setText(result.getString("ADRESSEN_Telefon"));
            field_Telefax.setText(result.getString("ADRESSEN_Telefax"));
            field_eMail.setText(result.getString("ADRESSEN_eMail"));
            field_Web.setText(result.getString("ADRESSEN_Web"));
            field_Kundennummer.setText(result.getString("ADRESSEN_Kundennummer"));
            field_Firmenname.setText(result.getString("ADRESSEN_Name"));
            field_Anmeldung.setText(result.getString("ADRESSEN_Anmeldung"));
            field_Kennwort.setText(result.getString("ADRESSEN_Kennwort"));
            field_Mobil.setText(result.getString("ADRESSEN_Mobil"));
            field_Vorname.setText(result.getString("ADRESSEN_Vorname"));
            field_Anrede.setText(result.getString("ADRESSEN_Anrede"));
            field_Namenszusatz.setText(result.getString("ADRESSEN_NAMENSZUSATZ"));
            field_Zusatz_1.setText(result.getString("ADRESSEN_ZUSATZ_1"));
            field_Zusatz_2.setText(result.getString("ADRESSEN_ZUSATZ_2"));
            field_Zusatz_3.setText(result.getString("ADRESSEN_ZUSATZ_3"));
            field_Zeitschrift.setText(result.getString("ADRESSEN_ZEITSCHRIFT"));
            field_UstrID.setText(result.getString("ADRESSEN_USTR_ID"));
            field_Sonderkondition.setSelected(result.getBoolean("ADRESSEN_SONDERKONDITION"));
            field_Ustr.setSelected(result.getBoolean("ADRESSEN_USTR"));
            field_Rabatt.setText(Integer.toString(result.getInt("ADRESSEN_RABATT")));
            String typ = result.getString("ADRESSEN_Typ");
            field_IBAN.setText(result.getString("ADRESSEN_IBAN"));
            field_BIC.setText(result.getString("ADRESSEN_BIC"));
            field_Bank.setText(result.getString("ADRESSEN_BANK"));
            // {"Autor", "Kunde", "Rezensent", "Druckerei", "Lieferant", "Sonstige"};
            switch (typ) {
                case "Autor":
                    field_Typ.setSelectedIndex(0);
                    break;
                case "Kunde":
                    field_Typ.setSelectedIndex(1);
                    break;
                case "Rezensent":
                    field_Typ.setSelectedIndex(2);
                    break;
                case "Druckerei":
                    field_Typ.setSelectedIndex(3);
                    break;
                case "Lieferant":
                    field_Typ.setSelectedIndex(4);
                    break;
                case "Zeitschrift":
                    field_Typ.setSelectedIndex(5);
                    break;
                default:
                    field_Typ.setSelectedIndex(6);
                    break;
            }
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: ", exept.getMessage());
        }
    }//GEN-LAST:event_EndeActionPerformed

    private void UpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UpdateActionPerformed
        // TODO add your handling code here:
        try {
            // result.updateInt("ADRESSEN_ID",Integer.parseInt(field_ID.getText()));
            result.updateString("ADRESSEN_Name", field_Firmenname.getText());
            result.updateString("ADRESSEN_POC", field_POC.getText());
            result.updateString("ADRESSEN_Strasse", field_Strasse.getText());
            result.updateString("ADRESSEN_PLZ", field_PLZ.getText());
            result.updateString("ADRESSEN_Ort", field_Ort.getText());
            result.updateString("ADRESSEN_Telefon", field_Telefon.getText());
            result.updateString("ADRESSEN_Telefax", field_Telefax.getText());
            result.updateString("ADRESSEN_eMail", field_eMail.getText());
            result.updateString("ADRESSEN_Web", field_Web.getText());
            result.updateString("ADRESSEN_Kundennummer", field_Kundennummer.getText());
            result.updateString("ADRESSEN_Kennwort", field_Kennwort.getText());
            result.updateString("ADRESSEN_Anmeldung", field_Anmeldung.getText());
            result.updateString("ADRESSEN_Vorname", field_Vorname.getText());
            result.updateString("ADRESSEN_Anrede", field_Anrede.getText());
            result.updateString("ADRESSEN_Mobil", field_Mobil.getText());
            result.updateString("ADRESSEN_NAMENSZUSATZ", field_Namenszusatz.getText());
            result.updateString("ADRESSEN_ZUSATZ_1", field_Zusatz_1.getText());
            result.updateString("ADRESSEN_ZUSATZ_2", field_Zusatz_2.getText());
            result.updateString("ADRESSEN_ZUSATZ_3", field_Zusatz_3.getText());
            result.updateString("ADRESSEN_Zeitschrift", field_Zeitschrift.getText());
            result.updateString("ADRESSEN_USTR_ID", field_UstrID.getText());
            result.updateString("ADRESSEN_IBAN", field_IBAN.getText());
            result.updateString("ADRESSEN_BIC", field_BIC.getText());
            result.updateString("ADRESSEN_BANK", field_Bank.getText());
            result.updateBoolean("ADRESSEN_SONDERKONDITION", field_Sonderkondition.isSelected());
            result.updateBoolean("ADRESSEN_USTR", field_Ustr.isSelected());
            result.updateInt("ADRESSEN_RABATT", Integer.parseInt(field_Rabatt.getText()));

            switch (field_Typ.getSelectedIndex()) {
                case 0:
                    result.updateString("ADRESSEN_Typ", "Autor");
                    break;
                case 1:
                    result.updateString("ADRESSEN_Typ", "Kunde");
                    break;
                case 2:
                    result.updateString("ADRESSEN_Typ", "Rezensent");
                    break;
                case 3:
                    result.updateString("ADRESSEN_Typ", "Druckerei");
                    break;
                case 4:
                    result.updateString("ADRESSEN_Typ", "Lieferant");
                    break;
                case 5:
                    result.updateString("ADRESSEN_Typ", "Zeitschrift");
                    break;
                case 6:
                    result.updateString("ADRESSEN_Typ", "Sonstige");
                    break;
            }
            result.updateRow();
            initListeAdressbestand();
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: ", exept.getMessage());
        }
    }//GEN-LAST:event_UpdateActionPerformed

    private void EinfuegenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EinfuegenActionPerformed
        // TODO add your handling code here:
        try {
            if (resultIsEmpty) {
                maxID = 1;
            } else {
                maxID = maxID + 1;
            }
            result.moveToInsertRow();
            result.updateInt("ADRESSEN_ID", maxID);
            result.updateString("ADRESSEN_Name", "");
            result.updateString("ADRESSEN_POC", "");
            result.updateString("ADRESSEN_Strasse", "");
            result.updateString("ADRESSEN_PLZ", "");
            result.updateString("ADRESSEN_Ort", "");
            result.updateString("ADRESSEN_Telefon", "");
            result.updateString("ADRESSEN_Telefax", "");
            result.updateString("ADRESSEN_eMail", "");
            result.updateString("ADRESSEN_Web", "");
            result.updateString("ADRESSEN_Kundennummer", "");
            result.updateString("ADRESSEN_Kennwort", "");
            result.updateString("ADRESSEN_Anmeldung", "");
            result.updateString("ADRESSEN_Vorname", "");
            result.updateString("ADRESSEN_Anrede", "Sehr geehrter Herr");
            result.updateString("ADRESSEN_Mobil", "");
            result.updateString("ADRESSEN_Typ", "");
            result.updateString("ADRESSEN_Zeitschrift", "");
            result.updateString("ADRESSEN_NAMENSZUSATZ", "");
            result.updateString("ADRESSEN_ZUSATZ_1", "");
            result.updateString("ADRESSEN_ZUSATZ_2", "");
            result.updateString("ADRESSEN_ZUSATZ_3", "");
            result.updateString("ADRESSEN_USTR_ID", "");
            result.updateString("ADRESSEN_IBAN", "");
            result.updateString("ADRESSEN_BIC", "");
            result.updateString("ADRESSEN_BANK", "");
            result.updateBoolean("ADRESSEN_SONDERKONDITION", false);
            result.updateBoolean("ADRESSEN_USTR", false);
            result.updateInt("ADRESSEN_RABATT", 0);
            result.insertRow();
            resultIsEmpty = false;
            result.last();
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

            field_ID.setText(Integer.toString(result.getInt("ADRESSEN_ID")));
            field_POC.setText(result.getString("ADRESSEN_POC"));
            field_Strasse.setText(result.getString("ADRESSEN_Strasse"));
            field_PLZ.setText(result.getString("ADRESSEN_PLZ"));
            field_Ort.setText(result.getString("ADRESSEN_Ort"));
            field_Telefon.setText(result.getString("ADRESSEN_Telefon"));
            field_Telefax.setText(result.getString("ADRESSEN_Telefax"));
            field_eMail.setText(result.getString("ADRESSEN_eMail"));
            field_Web.setText(result.getString("ADRESSEN_Web"));
            field_Kundennummer.setText(result.getString("ADRESSEN_Kundennummer"));
            field_Firmenname.setText(result.getString("ADRESSEN_Name"));
            field_Anmeldung.setText(result.getString("ADRESSEN_Anmeldung"));
            field_Kennwort.setText(result.getString("ADRESSEN_Kennwort"));
            field_Mobil.setText(result.getString("ADRESSEN_Mobil"));
            field_Vorname.setText(result.getString("ADRESSEN_Vorname"));
            field_Anrede.setText(result.getString("ADRESSEN_Anrede"));
            field_Namenszusatz.setText(result.getString("ADRESSEN_NAMENSZUSATZ"));
            field_Zusatz_1.setText(result.getString("ADRESSEN_ZUSATZ_1"));
            field_Zusatz_2.setText(result.getString("ADRESSEN_ZUSATZ_2"));
            field_Zusatz_3.setText(result.getString("ADRESSEN_ZUSATZ_3"));
            field_Zeitschrift.setText(result.getString("ADRESSEN_ZEITSCHRIFT"));
            field_UstrID.setText(result.getString("ADRESSEN_USTR_ID"));
            field_IBAN.setText(result.getString("ADRESSEN_IBAN"));
            field_BIC.setText(result.getString("ADRESSEN_BIC"));
            field_Bank.setText(result.getString("ADRESSEN_BANK"));
            field_Sonderkondition.setSelected(result.getBoolean("ADRESSEN_SONDERKONDITION"));
            field_Ustr.setSelected(result.getBoolean("ADRESSEN_USTR"));
            field_Rabatt.setText(Integer.toString(result.getInt("ADRESSEN_RABATT")));
            String typ = result.getString("ADRESSEN_Typ");
            // {"Autor", "Kunde", "Rezensent", "Druckerei", "Lieferant", "Sonstige"};
            switch (typ) {
                case "Autor":
                    field_Typ.setSelectedIndex(0);
                    break;
                case "Kunde":
                    field_Typ.setSelectedIndex(1);
                    break;
                case "Rezensent":
                    field_Typ.setSelectedIndex(2);
                    break;
                case "Druckerei":
                    field_Typ.setSelectedIndex(3);
                    break;
                case "Lieferant":
                    field_Typ.setSelectedIndex(4);
                    break;
                case "Zeitschrift":
                    field_Typ.setSelectedIndex(5);
                    break;
                default:
                    field_Typ.setSelectedIndex(6);
                    break;
            }
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: ", exept.getMessage());
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

                    field_ID.setText(Integer.toString(result.getInt("ADRESSEN_ID")));
                    field_POC.setText(result.getString("ADRESSEN_POC"));
                    field_Strasse.setText(result.getString("ADRESSEN_Strasse"));
                    field_PLZ.setText(result.getString("ADRESSEN_PLZ"));
                    field_Ort.setText(result.getString("ADRESSEN_Ort"));
                    field_Telefon.setText(result.getString("ADRESSEN_Telefon"));
                    field_Telefax.setText(result.getString("ADRESSEN_Telefax"));
                    field_eMail.setText(result.getString("ADRESSEN_eMail"));
                    field_Web.setText(result.getString("ADRESSEN_Web"));
                    field_Kundennummer.setText(result.getString("ADRESSEN_Kundennummer"));
                    field_Firmenname.setText(result.getString("ADRESSEN_Name"));
                    field_Anmeldung.setText(result.getString("ADRESSEN_Anmeldung"));
                    field_Kennwort.setText(result.getString("ADRESSEN_Kennwort"));
                    field_Mobil.setText(result.getString("ADRESSEN_Mobil"));
                    field_Vorname.setText(result.getString("ADRESSEN_Vorname"));
                    field_Anrede.setText(result.getString("ADRESSEN_Anrede"));
                    field_Namenszusatz.setText(result.getString("ADRESSEN_NAMENSZUSATZ"));
                    field_Zusatz_1.setText(result.getString("ADRESSEN_ZUSATZ_1"));
                    field_Zusatz_2.setText(result.getString("ADRESSEN_ZUSATZ_2"));
                    field_Zusatz_3.setText(result.getString("ADRESSEN_ZUSATZ_3"));
                    field_Zeitschrift.setText(result.getString("ADRESSEN_ZEITSCHRIFT"));
                    field_UstrID.setText(result.getString("ADRESSEN_USTR_ID"));
                    field_IBAN.setText(result.getString("ADRESSEN_IBAN"));
                    field_BIC.setText(result.getString("ADRESSEN_BIC"));
                    field_Bank.setText(result.getString("ADRESSEN_BANK"));
                    field_Sonderkondition.setSelected(result.getBoolean("ADRESSEN_SONDERKONDITION"));
                    field_Ustr.setSelected(result.getBoolean("ADRESSEN_USTR"));
                    field_Rabatt.setText(Integer.toString(result.getInt("ADRESSEN_RABATT")));
                    String typ = result.getString("ADRESSEN_Typ");
                    // {"Autor", "Kunde", "Rezensent", "Druckerei", "Lieferant", "Sonstige"};
                    switch (typ) {
                        case "Autor":
                            field_Typ.setSelectedIndex(0);
                            break;
                        case "Kunde":
                            field_Typ.setSelectedIndex(1);
                            break;
                        case "Rezensent":
                            field_Typ.setSelectedIndex(2);
                            break;
                        case "Druckerei":
                            field_Typ.setSelectedIndex(3);
                            break;
                        case "Lieferant":
                            field_Typ.setSelectedIndex(4);
                            break;
                        case "Zeitschrift":
                            field_Typ.setSelectedIndex(4);
                            break;
                        default:
                            field_Typ.setSelectedIndex(6);
                            break;
                    }
                } else {
                    resultIsEmpty = true;
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

                    field_ID.setText("");
                    field_POC.setText("");
                    field_Strasse.setText("");
                    field_PLZ.setText("");
                    field_Ort.setText("");
                    field_Telefon.setText("");
                    field_Telefax.setText("");
                    field_eMail.setText("");
                    field_Web.setText("");
                    field_Kundennummer.setText("");
                    field_Firmenname.setText("");
                    field_Anmeldung.setText("");
                    field_Kennwort.setText("");
                    field_Mobil.setText("");
                    field_Vorname.setText("");
                    field_Anrede.setText("");
                    field_Namenszusatz.setText("");
                    field_Zusatz_1.setText("");
                    field_Zusatz_2.setText("");
                    field_Zeitschrift.setText("");
                    field_Typ.setSelectedIndex(0);
                    field_Sonderkondition.setSelected(false);
                    field_Ustr.setSelected(false);
                    field_Bank.setText("");
                    field_Rabatt.setText("");
                }
            } catch (SQLException exept) {
                Modulhelferlein.Fehlermeldung("SQL-Exception: ", exept.getMessage());
            }
        }
    }//GEN-LAST:event_LoeschenActionPerformed

    private void SuchenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SuchenActionPerformed
        // TODO add your handling code here:
        boolean gefunden = false;
        String[] Kriterien = {"Ort", "Zeitschrift", "Name"};
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
                do {
                    field_count.setText(Integer.toString(count));
                    switch (Kriterium) {
                        case "Name":
                            if (result.getString("ADRESSEN_NAME").equals(field_Firmenname.getText())) {
                                gefunden = true;
                            } else {
                                count = count + 1;
                            }
                            break;
                        case "Ort":
                            if (result.getString("ADRESSEN_ORT").equals(field_Ort.getText())) {
                                gefunden = true;
                            } else {
                                count = count + 1;
                            }
                            break;
                        case "Zeitschrift":
                            if (result.getString("ADRESSEN_ZEITSCHRIFT").equals(field_Zeitschrift.getText())) {
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

                    field_ID.setText(Integer.toString(result.getInt("ADRESSEN_ID")));
                    field_ID.setText(Integer.toString(result.getInt("ADRESSEN_ID")));
                    field_POC.setText(result.getString("ADRESSEN_POC"));
                    field_Strasse.setText(result.getString("ADRESSEN_Strasse"));
                    field_PLZ.setText(result.getString("ADRESSEN_PLZ"));
                    field_Ort.setText(result.getString("ADRESSEN_Ort"));
                    field_Telefon.setText(result.getString("ADRESSEN_Telefon"));
                    field_Telefax.setText(result.getString("ADRESSEN_Telefax"));
                    field_eMail.setText(result.getString("ADRESSEN_eMail"));
                    field_Web.setText(result.getString("ADRESSEN_Web"));
                    field_Kundennummer.setText(result.getString("ADRESSEN_Kundennummer"));
                    field_Firmenname.setText(result.getString("ADRESSEN_Name"));
                    field_Anmeldung.setText(result.getString("ADRESSEN_Anmeldung"));
                    field_Kennwort.setText(result.getString("ADRESSEN_Kennwort"));
                    field_Mobil.setText(result.getString("ADRESSEN_Mobil"));
                    field_Vorname.setText(result.getString("ADRESSEN_Vorname"));
                    field_Anrede.setText(result.getString("ADRESSEN_Anrede"));
                    field_Namenszusatz.setText(result.getString("ADRESSEN_NAMENSZUSATZ"));
                    field_Zusatz_1.setText(result.getString("ADRESSEN_ZUSATZ_1"));
                    field_Zusatz_2.setText(result.getString("ADRESSEN_ZUSATZ_2"));
                    field_Zusatz_3.setText(result.getString("ADRESSEN_ZUSATZ_3"));
                    field_Zeitschrift.setText(result.getString("ADRESSEN_ZEITSCHRIFT"));
                    field_UstrID.setText(result.getString("ADRESSEN_USTR_ID"));
                    field_IBAN.setText(result.getString("ADRESSEN_IBAN"));
                    field_BIC.setText(result.getString("ADRESSEN_BIC"));
                    field_Bank.setText(result.getString("ADRESSEN_BANK"));
                    field_Sonderkondition.setSelected(result.getBoolean("ADRESSEN_SONDERKONDITION"));
                    field_Ustr.setSelected(result.getBoolean("ADRESSEN_USTR"));
                    field_Rabatt.setText(Integer.toString(result.getInt("ADRESSEN_RABATT")));
                    String typ = result.getString("ADRESSEN_Typ");
                    // {"Autor", "Kunde", "Rezensent", "Druckerei", "Lieferant", "Sonstige"};
                    switch (typ) {
                        case "Autor":
                            field_Typ.setSelectedIndex(0);
                            break;
                        case "Kunde":
                            field_Typ.setSelectedIndex(1);
                            break;
                        case "Rezensent":
                            field_Typ.setSelectedIndex(2);
                            break;
                        case "Druckerei":
                            field_Typ.setSelectedIndex(3);
                            break;
                        case "Lieferant":
                            field_Typ.setSelectedIndex(4);
                            break;
                        case "Zeitschrift":
                            field_Typ.setSelectedIndex(4);
                            break;
                        default:
                            field_Typ.setSelectedIndex(6);
                            break;
                    }
                } else {
                    Modulhelferlein.Infomeldung(Kriterium + " wurde nicht gefunden!");
                    AnfangActionPerformed(evt);
                }

            } catch (SQLException exept) {
                Modulhelferlein.Fehlermeldung("SQL-Exception: ", exept.getMessage());
            }
        }
    }//GEN-LAST:event_SuchenActionPerformed

    private void WSuchenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_WSuchenActionPerformed
        // TODO add your handling code here:
        boolean gefunden = false;

        try {
            result.next();
            count = 1;
            do {
                field_count.setText(Integer.toString(count));
                switch (Kriterium) {
                    case "Name":
                        if (result.getString("ADRESSEN_NAME").equals(field_Firmenname.getText())) {
                            gefunden = true;
                        } else {
                            count = count + 1;
                        }
                        break;
                    case "Ort":
                        if (result.getString("ADRESSEN_ORT").equals(field_Ort.getText())) {
                            gefunden = true;
                        } else {
                            count = count + 1;
                        }
                        break;
                    case "Zeitschrift":
                        if (result.getString("ADRESSEN_ZEITSCHRIFT").equals(field_Zeitschrift.getText())) {
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

                field_ID.setText(Integer.toString(result.getInt("ADRESSEN_ID")));
                field_ID.setText(Integer.toString(result.getInt("ADRESSEN_ID")));
                field_POC.setText(result.getString("ADRESSEN_POC"));
                field_Strasse.setText(result.getString("ADRESSEN_Strasse"));
                field_PLZ.setText(result.getString("ADRESSEN_PLZ"));
                field_Ort.setText(result.getString("ADRESSEN_Ort"));
                field_Telefon.setText(result.getString("ADRESSEN_Telefon"));
                field_Telefax.setText(result.getString("ADRESSEN_Telefax"));
                field_eMail.setText(result.getString("ADRESSEN_eMail"));
                field_Web.setText(result.getString("ADRESSEN_Web"));
                field_Kundennummer.setText(result.getString("ADRESSEN_Kundennummer"));
                field_Firmenname.setText(result.getString("ADRESSEN_Name"));
                field_Anmeldung.setText(result.getString("ADRESSEN_Anmeldung"));
                field_Kennwort.setText(result.getString("ADRESSEN_Kennwort"));
                field_Mobil.setText(result.getString("ADRESSEN_Mobil"));
                field_Vorname.setText(result.getString("ADRESSEN_Vorname"));
                field_Anrede.setText(result.getString("ADRESSEN_Anrede"));
                field_Namenszusatz.setText(result.getString("ADRESSEN_NAMENSZUSATZ"));
                field_Zusatz_1.setText(result.getString("ADRESSEN_ZUSATZ_1"));
                field_Zusatz_2.setText(result.getString("ADRESSEN_ZUSATZ_2"));
                field_Zusatz_3.setText(result.getString("ADRESSEN_ZUSATZ_3"));
                field_Zeitschrift.setText(result.getString("ADRESSEN_ZEITSCHRIFT"));
                field_UstrID.setText(result.getString("ADRESSEN_USTR_ID"));
                field_IBAN.setText(result.getString("ADRESSEN_IBAN"));
                field_BIC.setText(result.getString("ADRESSEN_BIC"));
                field_Bank.setText(result.getString("ADRESSEN_BANK"));
                field_Sonderkondition.setSelected(result.getBoolean("ADRESSEN_SONDERKONDITION"));
                field_Ustr.setSelected(result.getBoolean("ADRESSEN_USTR"));
                field_Rabatt.setText(Integer.toString(result.getInt("ADRESSEN_RABATT")));
                String typ = result.getString("ADRESSEN_Typ");
                // {"Autor", "Kunde", "Rezensent", "Druckerei", "Lieferant", "Sonstige"};
                switch (typ) {
                    case "Autor":
                        field_Typ.setSelectedIndex(0);
                        break;
                    case "Kunde":
                        field_Typ.setSelectedIndex(1);
                        break;
                    case "Rezensent":
                        field_Typ.setSelectedIndex(2);
                        break;
                    case "Druckerei":
                        field_Typ.setSelectedIndex(3);
                        break;
                    case "Lieferant":
                        field_Typ.setSelectedIndex(4);
                        break;
                    case "Zeitschrift":
                        field_Typ.setSelectedIndex(4);
                        break;
                    default:
                        field_Typ.setSelectedIndex(6);
                        break;
                }
            } else {
                Modulhelferlein.Infomeldung(Kriterium + " wurde nicht gefunden!");
                AnfangActionPerformed(evt);
            }

        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: ", exept.getMessage());
        }

    }//GEN-LAST:event_WSuchenActionPerformed

    private void DruckenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DruckenActionPerformed
        // TODO add your handling code here:
        String[] args = {field_Zusatz_1.getText(),
            Modulhelferlein.makeAnrede(field_Namenszusatz.getText(),
            field_Vorname.getText(),
            field_Firmenname.getText()),
            field_Zusatz_2.getText(),
            field_Strasse.getText(),
            field_PLZ.getText() + " " + field_Ort.getText(),
            field_Zusatz_3.getText()};
        _DlgAusgabeAdressen.main(args);
    }//GEN-LAST:event_DruckenActionPerformed

    private void SchliessenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SchliessenActionPerformed
        // TODO add your handling code here:
        try {
            result.close();
            SQLAnfrage.close();
            conn.close();
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: ", exept.getMessage());
        }
        this.dispose();
    }//GEN-LAST:event_SchliessenActionPerformed

    private void field_RabattActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_field_RabattActionPerformed
        // TODO add your handling code here:
        if (Modulhelferlein.checkNumberFormatInt(field_Rabatt.getText()) < 0) {
            Modulhelferlein.Infomeldung("fehlerhafte Eingabe - die ist keine korrekte Ganzzahl");
        }
    }//GEN-LAST:event_field_RabattActionPerformed

    private void jListAdressbestandMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListAdressbestandMouseClicked
        try {
            // TODO add your handling code here:
            // get selected item
            List<String> ListeAdressen = jListAdressbestand.getSelectedValuesList();
            // get Adresssen_ID
            String[] Listeneintrag = ListeAdressen.get(0).split(", ");
//helferlein.Infomeldung(ListeAdressen.get(0)+" "+Integer.toString(Listeneintrag.length)+" "+Listeneintrag[Listeneintrag.length-1]);            
            // gehe zu dieser ID
            if (result.first()) {
                count = 1;
                boolean ende = false;
                while (!ende) {
//helferlein.Infomeldung(result.getString("ADRESSEN_ID")+" - "+Listeneintrag[Listeneintrag.length-1]);                    
                    ende = (result.getString("ADRESSEN_ID").equals(Listeneintrag[Listeneintrag.length - 1]));
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
                field_ID.setText(Integer.toString(result.getInt("ADRESSEN_ID")));
                field_POC.setText(result.getString("ADRESSEN_POC"));
                field_Strasse.setText(result.getString("ADRESSEN_Strasse"));
                field_PLZ.setText(result.getString("ADRESSEN_PLZ"));
                field_Ort.setText(result.getString("ADRESSEN_Ort"));
                field_Telefon.setText(result.getString("ADRESSEN_Telefon"));
                field_Telefax.setText(result.getString("ADRESSEN_Telefax"));
                field_eMail.setText(result.getString("ADRESSEN_eMail"));
                field_Web.setText(result.getString("ADRESSEN_Web"));
                field_Kundennummer.setText(result.getString("ADRESSEN_Kundennummer"));
                field_Firmenname.setText(result.getString("ADRESSEN_Name"));
                field_Anmeldung.setText(result.getString("ADRESSEN_Anmeldung"));
                field_Kennwort.setText(result.getString("ADRESSEN_Kennwort"));
                field_Mobil.setText(result.getString("ADRESSEN_Mobil"));
                field_Vorname.setText(result.getString("ADRESSEN_Vorname"));
                field_Anrede.setText(result.getString("ADRESSEN_Anrede"));
                field_Namenszusatz.setText(result.getString("ADRESSEN_NAMENSZUSATZ"));
                field_Zusatz_1.setText(result.getString("ADRESSEN_ZUSATZ_1"));
                field_Zusatz_2.setText(result.getString("ADRESSEN_ZUSATZ_2"));
                field_Zusatz_3.setText(result.getString("ADRESSEN_ZUSATZ_3"));
                field_Zeitschrift.setText(result.getString("ADRESSEN_ZEITSCHRIFT"));
                field_UstrID.setText(result.getString("ADRESSEN_USTR_ID"));
                field_Sonderkondition.setSelected(result.getBoolean("ADRESSEN_SONDERKONDITION"));
                field_Ustr.setSelected(result.getBoolean("ADRESSEN_USTR"));
                field_Rabatt.setText(Integer.toString(result.getInt("ADRESSEN_RABATT")));
                String typ = result.getString("ADRESSEN_Typ");
                field_IBAN.setText(result.getString("ADRESSEN_IBAN"));
                field_BIC.setText(result.getString("ADRESSEN_BIC"));
                field_Bank.setText(result.getString("ADRESSEN_BANK"));
                // {"Autor", "Kunde", "Rezensent", "Druckerei", "Lieferant", "Sonstige"};
                switch (typ) {
                    case "Autor":
                        field_Typ.setSelectedIndex(0);
                        break;
                    case "Kunde":
                        field_Typ.setSelectedIndex(1);
                        break;
                    case "Rezensent":
                        field_Typ.setSelectedIndex(2);
                        break;
                    case "Druckerei":
                        field_Typ.setSelectedIndex(3);
                        break;
                    case "Lieferant":
                        field_Typ.setSelectedIndex(4);
                        break;
                    case "Zeitschrift":
                        field_Typ.setSelectedIndex(5);
                        break;
                    default:
                        field_Typ.setSelectedIndex(6);
                        break;
                }
            }
        } catch (SQLException ex) {
            Modulhelferlein.Fehlermeldung("Adressen verwalten", "SQL-Exception", ex.getMessage());
        }
    }//GEN-LAST:event_jListAdressbestandMouseClicked

    private void jButtonLandActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLandActionPerformed
        // TODO add your handling code here:
        String Land = (String) JOptionPane.showInputDialog(null,
                "Land des Empfängers?",
                "Land",
                JOptionPane.QUESTION_MESSAGE,
                null,
                Modulhelferlein.Laender,
                Modulhelferlein.Laender[0]);
        if (Land != null) {
            field_Zusatz_3.setText(Land);
        }
    }//GEN-LAST:event_jButtonLandActionPerformed

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
            VerwaltenDatenbankAdressen dialog = new VerwaltenDatenbankAdressen(new javax.swing.JFrame(), true);
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
    private JLabel jLabelID;
    private JLabel jLabel4;
    private JLabel jLabel2;
    private JTextField field_count;
    private JLabel jLabel3;
    private JTextField field_countMax;
    private JPanel hSpacer1;
    private JLabel jLabel29;
    private JLabel jLabel5;
    private JTextField field_Zusatz_1;
    private JLabel jLabel6;
    private JTextField field_Namenszusatz;
    private JLabel jLabel9;
    private JTextField field_Zusatz_2;
    private JLabel jLabel10;
    private JTextField field_Strasse;
    private JLabel jLabel11;
    private JLabel jLabel12;
    private JTextField field_PLZ;
    private JLabel jLabel13;
    private JTextField field_Zeitschrift;
    private JLabel jLabel25;
    private JTextField field_Anrede;
    private JTextField field_ID;
    private JComboBox<String> field_Typ;
    private JScrollPane jScrollPane1;
    private JList<String> jListAdressbestand;
    private JLabel jLabel20;
    private JLabel jLabel18;
    private JLabel jLabel19;
    private JTextField field_Kundennummer;
    private JTextField field_Anmeldung;
    private JTextField field_Kennwort;
    private JLabel jLabel7;
    private JLabel jLabel8;
    private JLabel jLabel21;
    private JLabel jLabel22;
    private JLabel jLabel23;
    private JTextField field_Vorname;
    private JTextField field_Firmenname;
    private JTextField field_Telefon;
    private JTextField field_Telefax;
    private JTextField field_Mobil;
    private JLabel jLabel24;
    private JTextField field_eMail;
    private JLabel jLabel26;
    private JTextField field_Web;
    private JLabel jLabel28;
    private JTextField field_Ort;
    private JTextField field_Bank;
    private JLabel jLabel30;
    private JLabel jLabel15;
    private JLabel jLabel16;
    private JLabel jLabel17;
    private JTextField field_Zusatz_3;
    private JButton jButtonLand;
    private JTextField field_IBAN;
    private JTextField field_BIC;
    private JTextField field_UstrID;
    private JCheckBox field_Sonderkondition;
    private JLabel jLabel27;
    private JTextField field_Rabatt;
    private JCheckBox field_Ustr;
    private JLabel jLabel14;
    private JTextField field_POC;
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

    private Integer count = 0;
    private Integer countMax = 0;
    private Integer maxID = 0;

    private Connection conn;
    private Statement SQLAnfrage;
    private Statement SQLAnfrage_Liste;
    private ResultSet result;
    private ResultSet result_Liste;

    private boolean resultIsEmpty = true;

    private String Kriterium = "";

    private DefaultListModel<String> listModel = new DefaultListModel<>();
}
