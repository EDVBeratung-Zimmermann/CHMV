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
import javax.swing.SwingConstants;
import javax.swing.border.*;
import com.toedter.calendar.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.ActionMapUIResource;
import net.miginfocom.swing.*;
import static milesVerlagMain.ModulMyOwnFocusTraversalPolicy.newPolicy;

/**
 *
 * @author Thomas Zimmermann
 */
public class VerwaltenDatenbankBestellung extends javax.swing.JDialog {

    /**
     * Creates new form VerwaltenDatenbankBestellung
     *
     * @param parent
     * @param modal
     */
    @SuppressWarnings("OverridableMethodCallInConstructor")
    private void rbRemittendeActionPerformed(ActionEvent e) {
      // TODO add your handling code here:
        if (rbRezension.isSelected() || rbPflicht.isSelected() || rbGeschenk.isSelected() || rbBeleg.isSelected() || rbRemittende.isSelected()) {
            field_B_Rabatt.setText("100");
            field_Bezahldatum.setDate(CurDate);
        }    }

    public VerwaltenDatenbankBestellung(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        buttonGroupTyp.add(rbBestellung);
        buttonGroupTyp.add(rbPflicht);
        buttonGroupTyp.add(rbRezension);
        buttonGroupTyp.add(rbGeschenk);
        buttonGroupTyp.add(rbBeleg);
        buttonGroupTyp.add(rbRemittende);

        buttonGroupAdresse.add(KDB);
        buttonGroupAdresse.add(manuell);

        Vector<Component> order = new Vector<>(45);

        order.add(field_Privat);
        order.add(KDB);
        order.add(cbKunde);
        order.add(manuell);
        order.add(field_UstrID);
        order.add(field_Zeile1);
        order.add(field_Zeile2);
        order.add(field_Zeile3);
        order.add(field_Zeile4);
        order.add(field_Zeile5);

        order.add(field_Zeile6);
        order.add(field_Bestelldatum);
        order.add(field_Bestellzeichen);
        order.add(field_Link);
        order.add(BDatei);
        order.add(field_Land);
        order.add(field_EU);
        order.add(field_Sprache);
        order.add(field_Versand);
        order.add(rbBestellung);

        order.add(rbRezension);
        order.add(rbPflicht);
        order.add(rbGeschenk);
        order.add(rbBeleg);
        order.add(cbBuch);
        order.add(field_B_Anzahl);
        order.add(field_B_Rabatt);
        order.add(BAnfang);
        order.add(BZurueck);
        order.add(BVor);
        order.add(BEnde);

        order.add(BUpdate);
        order.add(BEinfuegen);
        order.add(BLoeschen);
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
        order.add(Mahnung);
        order.add(Schliessen);

        newPolicy = new ModulMyOwnFocusTraversalPolicy(order);
        setFocusTraversalPolicy(newPolicy);

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

                eintrag = "0, ----------, -----------";
                cbKunde.addItem(eintrag);

                // Auswahlliste für Kunden und Autoren erstellen   
                resultK = SQLAnfrageK.executeQuery("SELECT * FROM TBL_ADRESSE "
                        + "WHERE ((ADRESSEN_Typ = 'Kunde') "
                        + "OR (ADRESSEN_Typ = 'Autor') "
                        + "OR (ADRESSEN_Typ = 'Rezensent')) "
                        + "ORDER BY ADRESSEN_NAME");
                count = 0;
                while (resultK.next()) {
                    count = count + 1;
                    eintrag = Integer.toString(resultK.getInt("ADRESSEN_ID")) + ", "
                            + resultK.getString("ADRESSEN_Name") + ", "
                            + resultK.getString("ADRESSEN_Vorname");
                    cbKunde.addItem(eintrag);
                } // while
                if (count == 0) {
                    KDB.setEnabled(false);
                } else {
                    KDB.setEnabled(true);
                }

                // Auswahlliste für Bücher erstellen   
                cbBuch.addItem("000, -------------, --------------------------------");
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
                    cbBuch.addItem(eintrag);
                } // while

                // Übersichtsliste einfügen
                SQLAnfrage_Liste = conn.createStatement(
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
                initListeRechnungenbestand();

                // Bestellnummer lesen
                resultBNr = SQLAnfrageBNr.executeQuery("SELECT * FROM TBL_BESTELLNR WHERE BESTELLNR_ID = 1");
                resultBNr.next();

                // Bestellungen bearbeiten
                resultB = SQLAnfrageB.executeQuery("SELECT * FROM TBL_BESTELLUNG ORDER BY BESTELLUNG_ID DESC");
                if (resultB.first()) {
                    maxID = resultB.getInt("BESTELLUNG_ID");
                } else {
                    maxID = 0;
                }

                resultB = SQLAnfrageB.executeQuery("SELECT * FROM TBL_BESTELLUNG");

                // Anzahl der Datensätze ermitteln
                countMax = 0;
                count = 0;
                field_count.setText(Integer.toString(count));
                while (resultB.next()) {
                    ++countMax;
                }
                field_countMax.setText(Integer.toString(countMax));

                // gehe zum ersten Datensatz - wenn nicht leer
                if (resultB.first()) {

                    count = 1;
                    field_count.setText(Integer.toString(count));
                    resultIsEmpty = false;

                    // Bestellung lesen
                    switch (resultB.getInt("BESTELLUNG_TYP")) {
                        case 0:
                            rbBestellung.setSelected(true);
                            break;
                        case 1:
                            rbRezension.setSelected(true);
                            break;
                        case 2:
                            rbPflicht.setSelected(true);
                            break;
                        case 3:
                            rbGeschenk.setSelected(true);
                            break;
                        case 4:
                            rbBeleg.setSelected(true);
                            break;
                    }
                    field_Zahlungsziel.setText(resultB.getString("BESTELLUNG_ZAHLUNGSZIEL"));
                    field_Zusatz_Text.setText(resultB.getString("BESTELLUNG_TEXT"));
                    field_Zusatz_cb.setSelected(resultB.getBoolean("BESTELLUNG_TB"));
                    field_Verrechnung.setSelected(resultB.getBoolean("BESTELLUNG_BEZAHLUNG"));
                    field_Ueberweisung.setSelected(!resultB.getBoolean("BESTELLUNG_BEZAHLUNG"));
                    field_Bestellzeichen.setText(resultB.getString("BESTELLUNG_BESTNR"));
                    field_RechNr.setText(resultB.getString("BESTELLUNG_RECHNR"));
                    field_RechDat.setDate(Modulhelferlein.Date2SQLDate(resultB.getDate("BESTELLUNG_RECHDAT")));
                    field_Zeile1.setText(resultB.getString("BESTELLUNG_ZEILE_1"));
                    field_Zeile2.setText(resultB.getString("BESTELLUNG_ZEILE_2"));
                    field_Zeile3.setText(resultB.getString("BESTELLUNG_ZEILE_3"));
                    field_Zeile4.setText(resultB.getString("BESTELLUNG_ZEILE_4"));
                    field_Zeile5.setText(resultB.getString("BESTELLUNG_ZEILE_5"));
                    field_Zeile6.setText(resultB.getString("BESTELLUNG_ZEILE_6"));
                    field_UstrID.setText(resultB.getString("BESTELLUNG_USTR_ID"));
                    field_Versand.setText(Float.toString(resultB.getFloat("BESTELLUNG_VERSAND")));
                    field_Privat.setSelected(resultB.getBoolean("BESTELLUNG_PRIVAT"));
                    field_storniert.setSelected(resultB.getBoolean("BESTELLUNG_STORNIERT"));
                    field_Link.setText(resultB.getString("BESTELLUNG_LINK"));
                    switch (resultB.getInt("BESTELLUNG_LAND")) {
                        case 0:
                            field_Land.setSelected(false);
                            field_EU.setSelected(false);
                            field_Sprache.setSelected(false);
                            break;
                        case 1:
                            field_Land.setSelected(false);
                            field_EU.setSelected(false);
                            field_Sprache.setSelected(true);
                            break;
                        case 10:
                            field_Land.setSelected(true);
                            field_EU.setSelected(true);
                            field_Sprache.setSelected(false);
                            break;
                        case 11:
                            field_Land.setSelected(true);
                            field_EU.setSelected(true);
                            field_Sprache.setSelected(true);
                            break;
                        case 20:
                            field_Land.setSelected(true);
                            field_EU.setSelected(false);
                            field_Sprache.setSelected(false);
                            break;
                        case 21:
                            field_Land.setSelected(true);
                            field_EU.setSelected(false);
                            field_Sprache.setSelected(true);
                            break;
                    }
                    field_Bestelldatum.setDate(Modulhelferlein.Date2SQLDate(resultB.getDate("BESTELLUNG_DATUM")));
                    field_Bezahldatum.setDate(Modulhelferlein.Date2SQLDate(resultB.getDate("BESTELLUNG_BEZAHLT")));
                    if (resultB.getInt("BESTELLUNG_KUNDE") < 0) {
                        manuell.setSelected(true);
                        KDB.setSelected(false);
                    } else {
                        manuell.setSelected(false);
                        KDB.setSelected(true);
                        resultK = SQLAnfrageK.executeQuery("SELECT * FROM TBL_ADRESSE WHERE ADRESSEN_ID = '" + Integer.toString(resultB.getInt("BESTELLUNG_KUNDE")) + "'"); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 
                        resultK.next();
                        cbKunde.setSelectedItem(Integer.toString(resultB.getInt("BESTELLUNG_KUNDE")) + ", " + resultK.getString("ADRESSEN_NAME") + ", " + resultK.getString("ADRESSEN_VORNAME"));
                    }

                    // Bestellungdetails lesen - ermittle größe ID
                    resultBD = SQLAnfrageBD.executeQuery("SELECT * FROM TBL_BESTELLUNG_DETAIL ORDER BY BESTELLUNG_DETAIL_ID DESC"); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 
                    if (resultBD.first()) {
                        maxBID = resultBD.getInt("BESTELLUNG_DETAIL_ID");
                        Anzahl = resultBD.getInt("BESTELLUNG_DETAIL_ANZAHL");
                    } else {
                        maxBID = 0;
                        Anzahl = 0;
                    }

                    // Bestellungdetails lesen  
                    resultBD = SQLAnfrageBD.executeQuery(
                            "SELECT * FROM TBL_BESTELLUNG_DETAIL WHERE BESTELLUNG_DETAIL_RECHNR = '" + resultB.getString("BESTELLUNG_RECHNR") + "'"); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 
                    countB = 0;
                    countBMax = 0;
                    while (resultBD.next()) {
                        ++countBMax;
                    }

                    if (countBMax > 0) { // es gibt Details zur Bestellung

// Rechnungsbetrag ermitteln
                        resultBIsEmpty = false;
                        resultBD = SQLAnfrageBD.executeQuery(
                                "SELECT * FROM TBL_BESTELLUNG_DETAIL WHERE BESTELLUNG_DETAIL_RECHNR = '" + resultB.getString("BESTELLUNG_RECHNR") + "'"); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 
                        Double Rechnungsbetrag = 0D;
                        Double Betrag19 = 0D;  // Bruttobetrag 19%
                        Double Betrag7 = 0D;   // Bruttobetrag  7%
                        while (resultBD.next()) {
                            if (resultBD.getBoolean("BESTELLUNG_DETAIL_SONST")) {
                                Betrag19 = Betrag19 + resultBD.getFloat("BESTELLUNG_DETAIL_SONST_PREIS") * 1D;
                            } else {
                                resultBuch = SQLAnfrageBuch.executeQuery("SELECT * FROM TBL_BUCH WHERE BUCH_ID = '" + resultBD.getString("BESTELLUNG_DETAIL_BUCH") + "'");
                                resultBuch.next();

                                Double ZPreis = resultBuch.getFloat("BUCH_PREIS") * 1D;
                                ZPreis = ZPreis - ZPreis / 100 * resultBD.getFloat("BESTELLUNG_DETAIL_RABATT");

                                ZPreis = Modulhelferlein.round2dec(ZPreis);
                                Betrag7 = Betrag7 + resultBD.getInt("BESTELLUNG_DETAIL_ANZAHL") * ZPreis;
                            }
                        }
// Rechnungsbetrag festlegen
// DEU:       Geschäft = Privat = Netto + UStr
// EU:        Geschäft = Netto
//            Privat   = Netto + UStr
// Drittland: Geschäft = Privat = Netto
                        Rechnungsbetrag = 0D;
                        switch (resultB.getInt("BESTELLUNG_LAND")) {
                            case 0:
                                Rechnungsbetrag = Betrag7 + Betrag19;
                                break;
                            case 10:
                            case 11:
                                if (resultB.getBoolean("BESTELLUNG_PRIVAT")) {
                                    Rechnungsbetrag = Betrag7 + Betrag19;
                                } else {
                                    Rechnungsbetrag = Betrag7 / 107 * 100 + Betrag19 / 119 * 100;
                                }
                                break;
                            case 20:
                            case 21:
                                Rechnungsbetrag = Betrag7 - Betrag7 / 107 * 100 + Betrag19 - Betrag19 / 119 * 100;
                                break;
                        }
                        Rechnungsbetrag = Rechnungsbetrag + resultB.getFloat("BESTELLUNG_VERSAND");
                        field_Betrag.setText(Modulhelferlein.str2dec(Rechnungsbetrag));

                        resultBD.first();
                        countB = 1;
                        field_B_Count.setText(Integer.toString(countB));
                        field_B_CountMax.setText(Integer.toString(countBMax));

                        // Felder füllen
                        field_B_Sonstiges.setSelected(resultBD.getBoolean("BESTELLUNG_DETAIL_SONST"));
                        field_B_Text.setText(resultBD.getString("BESTELLUNG_DETAIL_SONST_TEXT"));
                        field_B_Preis.setText(Float.toString(resultBD.getFloat("BESTELLUNG_DETAIL_SONST_PREIS")));
                        field_B_Rabatt.setText(Float.toString(resultBD.getFloat("BESTELLUNG_DETAIL_RABATT")));
                        field_B_Anzahl.setText(Integer.toString(resultBD.getInt("BESTELLUNG_DETAIL_ANZAHL")));
                        resultBuch = SQLAnfrageBuch.executeQuery("SELECT * FROM TBL_BUCH WHERE BUCH_ID = '" + resultBD.getString("BESTELLUNG_DETAIL_BUCH") + "'");
                        resultBuch.next();
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
                        cbBuch.setSelectedItem(eintrag);

                        // Schalterzustände DETAILS setzen
                        BAnfang.setEnabled(false);
                        BZurueck.setEnabled(false);
                        BVor.setEnabled(true);
                        BEnde.setEnabled(true);
                        BUpdate.setEnabled(true);
                        BEinfuegen.setEnabled(true);
                        BLoeschen.setEnabled(true);

                    } else {             // es gibt keine Details zur Bestellung
                        resultBIsEmpty = true;

                        // Schalterzustände DETAILS setzen
                        BAnfang.setEnabled(false);
                        BZurueck.setEnabled(false);
                        BVor.setEnabled(false);
                        BEnde.setEnabled(false);
                        BUpdate.setEnabled(false);
                        BEinfuegen.setEnabled(true);
                        BLoeschen.setEnabled(false);

                        field_B_Rabatt.setEditable(false);
                        field_B_Anzahl.setEditable(false);
                    }

                    // Schalterzustände BESTELLUNG setzen
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

                } else { // es gibt keine Bestellungen
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

                    // Schalterzustände setzen Bestell Details  
                    BAnfang.setEnabled(false);
                    BZurueck.setEnabled(false);
                    BVor.setEnabled(false);
                    BEnde.setEnabled(false);
                    BUpdate.setEnabled(false);
                    BEinfuegen.setEnabled(false);
                    BLoeschen.setEnabled(false);
                    field_B_Rabatt.setEditable(false);
                    field_B_Anzahl.setEditable(false);
                    cbBuch.setEditable(false);
                }
            } catch (SQLException exept) {
                Modulhelferlein.Fehlermeldung("DB-Bestellung", "SQL-Exception: SQL-Anfrage nicht moeglich: ",
                        exept.getMessage());
                // System.exit(1);
            } // try SQL-Anfragen an die Datenbank

        } // if conn != null

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
        field_count = new JTextField();
        jLabel3 = new JLabel();
        field_countMax = new JTextField();
        hSpacer1 = new JPanel(null);
        jLabel20 = new JLabel();
        hSpacer2 = new JPanel(null);
        field_RechNr = new JTextField();
        field_Privat = new JCheckBox();
        jLabel6 = new JLabel();
        jLabel14 = new JLabel();
        jLabel26 = new JLabel();
        jPanel2 = new JPanel();
        jScrollPane1 = new JScrollPane();
        jList1 = new JList<>();
        jLabel5 = new JLabel();
        jLabel29 = new JLabel();
        KDB = new JRadioButton();
        field_Link = new JTextField();
        BDatei = new JButton();
        field_RechDat = new JDateChooser();
        cbKunde = new JComboBox<>();
        jLabel16 = new JLabel();
        jLabel17 = new JLabel();
        manuell = new JRadioButton();
        field_Bestelldatum = new JDateChooser();
        field_Bestellzeichen = new JTextField();
        jLabel28 = new JLabel();
        field_Betrag = new JTextField();
        jLabel25 = new JLabel();
        field_UstrID = new JTextField();
        rbBestellung = new JRadioButton();
        jLabel19 = new JLabel();
        jLabel7 = new JLabel();
        field_Zeile1 = new JTextField();
        rbRezension = new JRadioButton();
        field_Bezahldatum = new JDateChooser();
        jLabel8 = new JLabel();
        field_Zeile2 = new JTextField();
        rbPflicht = new JRadioButton();
        jLabel9 = new JLabel();
        field_Zeile3 = new JTextField();
        rbGeschenk = new JRadioButton();
        field_storniert = new JCheckBox();
        jLabel10 = new JLabel();
        field_Zeile4 = new JTextField();
        rbBeleg = new JRadioButton();
        jLabel12 = new JLabel();
        field_Zeile5 = new JTextField();
        jLabel18 = new JLabel();
        field_Versand = new JTextField();
        jLabel13 = new JLabel();
        field_Zeile6 = new JTextField();
        jButtonLand = new JButton();
        field_Land = new JCheckBox();
        field_EU = new JCheckBox();
        field_Sprache = new JCheckBox();
        label1 = new JLabel();
        jLabel21 = new JLabel();
        jLabel22 = new JLabel();
        jLabel23 = new JLabel();
        cbBuch = new JComboBox<>();
        field_B_Anzahl = new JTextField();
        field_B_Rabatt = new JTextField();
        jLabel27 = new JLabel();
        field_B_Sonstiges = new JCheckBox();
        field_B_Text = new JTextField();
        field_B_Preis = new JTextField();
        BAnfang = new JButton();
        BZurueck = new JButton();
        BVor = new JButton();
        BEnde = new JButton();
        BUpdate = new JButton();
        BEinfuegen = new JButton();
        BLoeschen = new JButton();
        jLabel11 = new JLabel();
        field_B_Count = new JTextField();
        jLabel24 = new JLabel();
        field_B_CountMax = new JTextField();
        field_Ueberweisung = new JRadioButton();
        field_Zahlungsziel = new JTextField();
        jLabel30 = new JLabel();
        field_Verrechnung = new JRadioButton();
        field_Zusatz_cb = new JCheckBox();
        field_Zusatz_Text = new JTextField();
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
        Mahnung = new JButton();
        Schliessen = new JButton();
        label2 = new JLabel();
        field_EMail = new JTextField();
        label3 = new JLabel();
        field_DHL = new JTextField();
        EMail = new JButton();
        rbRemittende = new JRadioButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Carola Hartmann Miles Verlag");
        setResizable(false);
        setFont(new Font(Font.DIALOG, Font.BOLD, 12));
        var contentPane = getContentPane();

        //======== panel1 ========
        {
            panel1.setLayout(null);

            //---- jLabel1 ----
            jLabel1.setFont(new Font("Tahoma", Font.BOLD, 12));
            jLabel1.setText("Verwalten der Einnahmen - Buchbestellungen");
            panel1.add(jLabel1);
            jLabel1.setBounds(0, 0, 449, 25);

            //---- jLabel2 ----
            jLabel2.setText("Datensatz");
            panel1.add(jLabel2);
            jLabel2.setBounds(454, 0, jLabel2.getPreferredSize().width, 25);

            //---- field_count ----
            field_count.setEditable(false);
            field_count.setText("000");
            field_count.setEnabled(false);
            field_count.setFocusable(false);
            field_count.setMinimumSize(new Dimension(30, 25));
            field_count.setPreferredSize(new Dimension(30, 25));
            panel1.add(field_count);
            field_count.setBounds(508, 0, 65, field_count.getPreferredSize().height);

            //---- jLabel3 ----
            jLabel3.setText("von");
            panel1.add(jLabel3);
            jLabel3.setBounds(578, 0, 39, 25);

            //---- field_countMax ----
            field_countMax.setEditable(false);
            field_countMax.setText("000");
            field_countMax.setEnabled(false);
            field_countMax.setFocusable(false);
            field_countMax.setMinimumSize(new Dimension(30, 25));
            field_countMax.setPreferredSize(new Dimension(30, 25));
            panel1.add(field_countMax);
            field_countMax.setBounds(622, 0, 72, field_countMax.getPreferredSize().height);
            panel1.add(hSpacer1);
            hSpacer1.setBounds(699, 0, 33, 25);

            //---- jLabel20 ----
            jLabel20.setFont(new Font("Tahoma", Font.BOLD, 12));
            jLabel20.setText("Rechnungsnummer");
            panel1.add(jLabel20);
            jLabel20.setBounds(737, 0, 122, 25);
            panel1.add(hSpacer2);
            hSpacer2.setBounds(864, 0, hSpacer2.getPreferredSize().width, 25);

            //---- field_RechNr ----
            field_RechNr.setFont(new Font("Tahoma", Font.BOLD, 11));
            field_RechNr.setDisabledTextColor(Color.black);
            field_RechNr.setEnabled(false);
            field_RechNr.setFocusable(false);
            field_RechNr.setMinimumSize(new Dimension(100, 25));
            field_RechNr.setPreferredSize(new Dimension(100, 25));
            panel1.add(field_RechNr);
            field_RechNr.setBounds(879, 0, 207, field_RechNr.getPreferredSize().height);

            //---- field_Privat ----
            field_Privat.setText("Privatkunde");
            panel1.add(field_Privat);
            field_Privat.setBounds(0, 50, 203, field_Privat.getPreferredSize().height);

            //---- jLabel6 ----
            jLabel6.setFont(new Font("Tahoma", Font.BOLD, 12));
            jLabel6.setText("Kundendaten");
            panel1.add(jLabel6);
            jLabel6.setBounds(0, 30, 203, jLabel6.getPreferredSize().height);

            //---- jLabel14 ----
            jLabel14.setFont(new Font("Tahoma", Font.BOLD, 12));
            jLabel14.setText("Bestelldaten");
            panel1.add(jLabel14);
            jLabel14.setBounds(454, 30, 240, jLabel14.getPreferredSize().height);

            //---- jLabel26 ----
            jLabel26.setFont(new Font("Tahoma", Font.BOLD, 12));
            jLabel26.setText("Rechnungsdaten");
            panel1.add(jLabel26);
            jLabel26.setBounds(737, 30, 122, jLabel26.getPreferredSize().height);

            //======== jPanel2 ========
            {

                //======== jScrollPane1 ========
                {

                    //---- jList1 ----
                    jList1.setModel(listModel);
                    jList1.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            jList1MouseClicked(e);
                        }
                    });
                    jScrollPane1.setViewportView(jList1);
                }

                GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
                jPanel2.setLayout(jPanel2Layout);
                jPanel2Layout.setHorizontalGroup(
                    jPanel2Layout.createParallelGroup()
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 190, GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 17, Short.MAX_VALUE))
                );
                jPanel2Layout.setVerticalGroup(
                    jPanel2Layout.createParallelGroup()
                        .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 649, Short.MAX_VALUE)
                );
            }
            panel1.add(jPanel2);
            jPanel2.setBounds(new Rectangle(new Point(879, 30), jPanel2.getPreferredSize()));

            //---- jLabel5 ----
            jLabel5.setText("Bestellung");
            panel1.add(jLabel5);
            jLabel5.setBounds(454, 50, 119, 23);

            //---- jLabel29 ----
            jLabel29.setText("Datum");
            panel1.add(jLabel29);
            jLabel29.setBounds(737, 50, 57, 23);

            //---- KDB ----
            KDB.setText("Adresse aus Kundendatenbank");
            panel1.add(KDB);
            KDB.setBounds(0, 70, 303, 25);

            //---- field_Link ----
            field_Link.setMinimumSize(new Dimension(100, 25));
            field_Link.setPreferredSize(new Dimension(100, 25));
            panel1.add(field_Link);
            field_Link.setBounds(454, 78, 163, 30);

            //---- BDatei ----
            BDatei.setText("...");
            BDatei.setPreferredSize(new Dimension(73, 30));
            BDatei.addActionListener(e -> BDateiActionPerformed(e));
            panel1.add(BDatei);
            BDatei.setBounds(622, 78, 72, BDatei.getPreferredSize().height);

            //---- field_RechDat ----
            field_RechDat.setEnabled(false);
            field_RechDat.setFocusable(false);
            field_RechDat.setFont(new Font("Tahoma", Font.BOLD, 11));
            field_RechDat.setPreferredSize(new Dimension(120, 25));
            panel1.add(field_RechDat);
            field_RechDat.setBounds(737, 78, 122, 30);

            //---- cbKunde ----
            cbKunde.setModel(new DefaultComboBoxModel<>(new String[] {

            }));
            cbKunde.setPreferredSize(new Dimension(28, 25));
            panel1.add(cbKunde);
            cbKunde.setBounds(55, 95, 345, cbKunde.getPreferredSize().height);

            //---- jLabel16 ----
            jLabel16.setText("Bestelldatum");
            panel1.add(jLabel16);
            jLabel16.setBounds(454, 113, 119, 25);

            //---- jLabel17 ----
            jLabel17.setText("Bestellzeichen");
            panel1.add(jLabel17);
            jLabel17.setBounds(578, 113, 116, 25);

            //---- manuell ----
            manuell.setSelected(true);
            manuell.setText("Adresse manuell eingeben");
            panel1.add(manuell);
            manuell.setBounds(55, 125, 249, 30);

            //---- field_Bestelldatum ----
            field_Bestelldatum.setPreferredSize(new Dimension(120, 30));
            panel1.add(field_Bestelldatum);
            field_Bestelldatum.setBounds(454, 143, 119, field_Bestelldatum.getPreferredSize().height);

            //---- field_Bestellzeichen ----
            field_Bestellzeichen.setMinimumSize(new Dimension(100, 25));
            field_Bestellzeichen.setPreferredSize(new Dimension(100, 25));
            panel1.add(field_Bestellzeichen);
            field_Bestellzeichen.setBounds(578, 143, 116, 30);

            //---- jLabel28 ----
            jLabel28.setText("Betrag");
            panel1.add(jLabel28);
            jLabel28.setBounds(737, 143, 57, 30);

            //---- field_Betrag ----
            field_Betrag.setEditable(false);
            field_Betrag.setFont(new Font("Tahoma", Font.BOLD, 11));
            field_Betrag.setDisabledTextColor(Color.black);
            field_Betrag.setPreferredSize(new Dimension(60, 25));
            panel1.add(field_Betrag);
            field_Betrag.setBounds(799, 143, field_Betrag.getPreferredSize().width, 30);

            //---- jLabel25 ----
            jLabel25.setText("UstrID");
            panel1.add(jLabel25);
            jLabel25.setBounds(55, 160, 49, 25);

            //---- field_UstrID ----
            field_UstrID.setMinimumSize(new Dimension(150, 25));
            field_UstrID.setPreferredSize(new Dimension(150, 25));
            panel1.add(field_UstrID);
            field_UstrID.setBounds(110, 160, 291, field_UstrID.getPreferredSize().height);

            //---- rbBestellung ----
            rbBestellung.setSelected(true);
            rbBestellung.setText("Bestellung");
            rbBestellung.addActionListener(e -> rbBestellungActionPerformed(e));
            panel1.add(rbBestellung);
            rbBestellung.setBounds(455, 178, 240, 25);

            //---- jLabel19 ----
            jLabel19.setText("bezahlt am");
            panel1.add(jLabel19);
            jLabel19.setBounds(737, 178, 123, 25);

            //---- jLabel7 ----
            jLabel7.setText("Zeile 1");
            panel1.add(jLabel7);
            jLabel7.setBounds(55, 190, 49, 25);

            //---- field_Zeile1 ----
            field_Zeile1.setMinimumSize(new Dimension(150, 25));
            field_Zeile1.setPreferredSize(new Dimension(200, 25));
            panel1.add(field_Zeile1);
            field_Zeile1.setBounds(110, 190, 291, 25);

            //---- rbRezension ----
            rbRezension.setText("Rezensionsexemplar");
            rbRezension.addActionListener(e -> rbRezensionActionPerformed(e));
            panel1.add(rbRezension);
            rbRezension.setBounds(455, 200, 278, 25);

            //---- field_Bezahldatum ----
            field_Bezahldatum.setPreferredSize(new Dimension(120, 25));
            panel1.add(field_Bezahldatum);
            field_Bezahldatum.setBounds(737, 208, 122, field_Bezahldatum.getPreferredSize().height);

            //---- jLabel8 ----
            jLabel8.setText("Name");
            panel1.add(jLabel8);
            jLabel8.setBounds(55, 220, 49, 25);

            //---- field_Zeile2 ----
            field_Zeile2.setMinimumSize(new Dimension(150, 25));
            field_Zeile2.setPreferredSize(new Dimension(200, 25));
            panel1.add(field_Zeile2);
            field_Zeile2.setBounds(110, 220, 291, 25);

            //---- rbPflicht ----
            rbPflicht.setText("Pflichtexemplar");
            rbPflicht.addActionListener(e -> rbPflichtActionPerformed(e));
            panel1.add(rbPflicht);
            rbPflicht.setBounds(455, 222, 278, 25);

            //---- jLabel9 ----
            jLabel9.setText("Zeile 3");
            panel1.add(jLabel9);
            jLabel9.setBounds(55, 250, 49, 25);

            //---- field_Zeile3 ----
            field_Zeile3.setMinimumSize(new Dimension(150, 25));
            field_Zeile3.setPreferredSize(new Dimension(200, 25));
            panel1.add(field_Zeile3);
            field_Zeile3.setBounds(110, 250, 291, 25);

            //---- rbGeschenk ----
            rbGeschenk.setText("Geschenk");
            rbGeschenk.addActionListener(e -> rbGeschenkActionPerformed(e));
            panel1.add(rbGeschenk);
            rbGeschenk.setBounds(455, 244, 278, 25);

            //---- field_storniert ----
            field_storniert.setText("storniert");
            panel1.add(field_storniert);
            field_storniert.setBounds(737, 268, 122, 25);

            //---- jLabel10 ----
            jLabel10.setText("Strasse");
            panel1.add(jLabel10);
            jLabel10.setBounds(55, 280, 49, 25);

            //---- field_Zeile4 ----
            field_Zeile4.setMinimumSize(new Dimension(150, 25));
            field_Zeile4.setPreferredSize(new Dimension(200, 25));
            panel1.add(field_Zeile4);
            field_Zeile4.setBounds(110, 280, 291, 25);

            //---- rbBeleg ----
            rbBeleg.setText("Belegexemplar");
            rbBeleg.addActionListener(e -> rbBelegActionPerformed(e));
            panel1.add(rbBeleg);
            rbBeleg.setBounds(455, 266, 278, 25);

            //---- jLabel12 ----
            jLabel12.setText("PLZ, Ort");
            panel1.add(jLabel12);
            jLabel12.setBounds(55, 310, 49, 25);

            //---- field_Zeile5 ----
            field_Zeile5.setMinimumSize(new Dimension(150, 25));
            field_Zeile5.setPreferredSize(new Dimension(200, 25));
            panel1.add(field_Zeile5);
            field_Zeile5.setBounds(110, 310, 291, 25);

            //---- jLabel18 ----
            jLabel18.setText("Versandkosten");
            panel1.add(jLabel18);
            jLabel18.setBounds(459, 328, 111, 25);

            //---- field_Versand ----
            field_Versand.setMinimumSize(new Dimension(100, 25));
            field_Versand.setPreferredSize(new Dimension(100, 25));
            field_Versand.addActionListener(e -> field_VersandActionPerformed(e));
            panel1.add(field_Versand);
            field_Versand.setBounds(578, 328, 116, 25);

            //---- jLabel13 ----
            jLabel13.setText("Land");
            panel1.add(jLabel13);
            jLabel13.setBounds(55, 340, 49, 25);

            //---- field_Zeile6 ----
            field_Zeile6.setEditable(false);
            field_Zeile6.setText("DEUTSCHLAND");
            field_Zeile6.setMinimumSize(new Dimension(150, 25));
            field_Zeile6.setPreferredSize(new Dimension(200, 20));
            panel1.add(field_Zeile6);
            field_Zeile6.setBounds(110, 340, 241, 25);

            //---- jButtonLand ----
            jButtonLand.setText("...");
            jButtonLand.addActionListener(e -> jButtonLandActionPerformed(e));
            panel1.add(jButtonLand);
            jButtonLand.setBounds(355, 340, jButtonLand.getPreferredSize().width, 25);

            //---- field_Land ----
            field_Land.setText("Ausland");
            panel1.add(field_Land);
            field_Land.setBounds(454, 358, 119, 25);

            //---- field_EU ----
            field_EU.setText("EU");
            panel1.add(field_EU);
            field_EU.setBounds(578, 358, field_EU.getPreferredSize().width, 25);

            //---- field_Sprache ----
            field_Sprache.setText("Englisch");
            panel1.add(field_Sprache);
            field_Sprache.setBounds(622, 358, 110, 25);

            //---- label1 ----
            label1.setText("Bestellung - Inhalte");
            label1.setFont(label1.getFont().deriveFont(label1.getFont().getStyle() | Font.BOLD));
            label1.setBackground(new Color(204, 204, 204));
            panel1.add(label1);
            label1.setBounds(0, 413, 794, label1.getPreferredSize().height);

            //---- jLabel21 ----
            jLabel21.setText("Buchtitel");
            jLabel21.setHorizontalAlignment(SwingConstants.LEFT);
            panel1.add(jLabel21);
            jLabel21.setBounds(0, 432, 349, jLabel21.getPreferredSize().height);

            //---- jLabel22 ----
            jLabel22.setText("Anzahl");
            panel1.add(jLabel22);
            jLabel22.setBounds(622, 432, 72, jLabel22.getPreferredSize().height);

            //---- jLabel23 ----
            jLabel23.setText("Rabatt");
            panel1.add(jLabel23);
            jLabel23.setBounds(699, 432, 91, jLabel23.getPreferredSize().height);

            //---- cbBuch ----
            cbBuch.setFont(new Font("Courier New", Font.BOLD, 14));
            cbBuch.setModel(new DefaultComboBoxModel<>(new String[] {

            }));
            cbBuch.setPreferredSize(new Dimension(0, 25));
            panel1.add(cbBuch);
            cbBuch.setBounds(0, 451, 617, cbBuch.getPreferredSize().height);

            //---- field_B_Anzahl ----
            field_B_Anzahl.setPreferredSize(new Dimension(0, 25));
            field_B_Anzahl.addActionListener(e -> field_B_AnzahlActionPerformed(e));
            panel1.add(field_B_Anzahl);
            field_B_Anzahl.setBounds(622, 451, 72, field_B_Anzahl.getPreferredSize().height);

            //---- field_B_Rabatt ----
            field_B_Rabatt.setPreferredSize(new Dimension(0, 25));
            field_B_Rabatt.addActionListener(e -> field_B_RabattActionPerformed(e));
            panel1.add(field_B_Rabatt);
            field_B_Rabatt.setBounds(699, 451, 33, field_B_Rabatt.getPreferredSize().height);

            //---- jLabel27 ----
            jLabel27.setText("Brutto-Preis");
            panel1.add(jLabel27);
            jLabel27.setBounds(737, 451, 93, 25);

            //---- field_B_Sonstiges ----
            field_B_Sonstiges.setText("Sonstiges");
            panel1.add(field_B_Sonstiges);
            field_B_Sonstiges.setBounds(0, 481, 103, 25);

            //---- field_B_Text ----
            field_B_Text.setText("jTextField1");
            field_B_Text.setPreferredSize(new Dimension(0, 25));
            panel1.add(field_B_Text);
            field_B_Text.setBounds(108, 481, 624, field_B_Text.getPreferredSize().height);

            //---- field_B_Preis ----
            field_B_Preis.setText("jTextField1");
            field_B_Preis.setPreferredSize(new Dimension(0, 25));
            field_B_Preis.addActionListener(e -> field_B_PreisActionPerformed(e));
            panel1.add(field_B_Preis);
            field_B_Preis.setBounds(737, 481, 57, field_B_Preis.getPreferredSize().height);

            //---- BAnfang ----
            BAnfang.setText("<<");
            BAnfang.setToolTipText("gehe zum ersten Datensatz");
            BAnfang.addActionListener(e -> BAnfangActionPerformed(e));
            panel1.add(BAnfang);
            BAnfang.setBounds(54, 511, BAnfang.getPreferredSize().width, 25);

            //---- BZurueck ----
            BZurueck.setText("<");
            BZurueck.setToolTipText("gehe zum vorherigen Datensatz");
            BZurueck.addActionListener(e -> BZurueckActionPerformed(e));
            panel1.add(BZurueck);
            BZurueck.setBounds(108, 511, BZurueck.getPreferredSize().width, 25);

            //---- BVor ----
            BVor.setText(">");
            BVor.setToolTipText("gehe zum n\u00e4chsten Datensatz");
            BVor.addActionListener(e -> BVorActionPerformed(e));
            panel1.add(BVor);
            BVor.setBounds(154, 511, 49, 25);

            //---- BEnde ----
            BEnde.setText(">>");
            BEnde.setToolTipText("gehe zum letzten Datensatz");
            BEnde.addActionListener(e -> BEndeActionPerformed(e));
            panel1.add(BEnde);
            BEnde.setBounds(208, 511, BEnde.getPreferredSize().width, 25);

            //---- BUpdate ----
            BUpdate.setText("!");
            BUpdate.setToolTipText("Buchbestellung aktualisieren");
            BUpdate.addActionListener(e -> BUpdateActionPerformed(e));
            panel1.add(BUpdate);
            BUpdate.setBounds(262, 511, 41, 25);

            //---- BEinfuegen ----
            BEinfuegen.setText("+");
            BEinfuegen.setToolTipText("Neues Buch einf\u00fcgen");
            BEinfuegen.addActionListener(e -> BEinfuegenActionPerformed(e));
            panel1.add(BEinfuegen);
            BEinfuegen.setBounds(308, 511, BEinfuegen.getPreferredSize().width, 25);

            //---- BLoeschen ----
            BLoeschen.setText("-");
            BLoeschen.setToolTipText("Datensatz l\u00f6schen");
            BLoeschen.addActionListener(e -> BLoeschenActionPerformed(e));
            panel1.add(BLoeschen);
            BLoeschen.setBounds(354, 511, 45, 25);

            //---- jLabel11 ----
            jLabel11.setText("Position");
            panel1.add(jLabel11);
            jLabel11.setBounds(454, 511, 49, 25);

            //---- field_B_Count ----
            field_B_Count.setEditable(false);
            field_B_Count.setEnabled(false);
            field_B_Count.setFocusable(false);
            field_B_Count.setPreferredSize(new Dimension(50, 25));
            panel1.add(field_B_Count);
            field_B_Count.setBounds(508, 511, 65, field_B_Count.getPreferredSize().height);

            //---- jLabel24 ----
            jLabel24.setText("von");
            jLabel24.setRequestFocusEnabled(false);
            panel1.add(jLabel24);
            jLabel24.setBounds(578, 511, 39, 25);

            //---- field_B_CountMax ----
            field_B_CountMax.setEditable(false);
            field_B_CountMax.setEnabled(false);
            field_B_CountMax.setFocusable(false);
            field_B_CountMax.setPreferredSize(new Dimension(50, 25));
            panel1.add(field_B_CountMax);
            field_B_CountMax.setBounds(622, 511, 72, field_B_CountMax.getPreferredSize().height);

            //---- field_Ueberweisung ----
            field_Ueberweisung.setSelected(true);
            field_Ueberweisung.setText("Bezahlung per \u00dcberweisung in");
            panel1.add(field_Ueberweisung);
            field_Ueberweisung.setBounds(0, 566, 203, 32);

            //---- field_Zahlungsziel ----
            field_Zahlungsziel.setText("14");
            field_Zahlungsziel.setMinimumSize(new Dimension(20, 32));
            panel1.add(field_Zahlungsziel);
            field_Zahlungsziel.setBounds(208, 566, 49, 32);

            //---- jLabel30 ----
            jLabel30.setText("Tagen");
            panel1.add(jLabel30);
            jLabel30.setBounds(262, 566, 41, 32);

            //---- field_Verrechnung ----
            field_Verrechnung.setText("Bezahlung per Verrechnung mit Honorar");
            panel1.add(field_Verrechnung);
            field_Verrechnung.setBounds(454, 566, 278, 32);

            //---- field_Zusatz_cb ----
            field_Zusatz_cb.setText("Zusatz");
            panel1.add(field_Zusatz_cb);
            field_Zusatz_cb.setBounds(0, 603, 103, field_Zusatz_cb.getPreferredSize().height);
            panel1.add(field_Zusatz_Text);
            field_Zusatz_Text.setBounds(108, 604, 751, field_Zusatz_Text.getPreferredSize().height);

            //---- Anfang ----
            Anfang.setText("<<");
            Anfang.setToolTipText("gehe zum ersten Datensatz");
            Anfang.addActionListener(e -> AnfangActionPerformed(e));
            panel1.add(Anfang);
            Anfang.setBounds(new Rectangle(new Point(0, 656), Anfang.getPreferredSize()));

            //---- Zurueck ----
            Zurueck.setText("<");
            Zurueck.setToolTipText("gehe zum vorherigen Datensatz");
            Zurueck.addActionListener(e -> ZurueckActionPerformed(e));
            panel1.add(Zurueck);
            Zurueck.setBounds(51, 656, 49, 23);

            //---- Vor ----
            Vor.setText(">");
            Vor.setToolTipText("gehe zum n\u00e4chsten Datensatz");
            Vor.addActionListener(e -> VorActionPerformed(e));
            panel1.add(Vor);
            Vor.setBounds(102, 656, 49, 23);

            //---- Ende ----
            Ende.setText(">>");
            Ende.setToolTipText("gehe zum letzten Datensatz");
            Ende.addActionListener(e -> EndeActionPerformed(e));
            panel1.add(Ende);
            Ende.setBounds(153, 656, 49, 23);

            //---- Update ----
            Update.setText("!");
            Update.setToolTipText("Bestellung aktualisieren");
            Update.addActionListener(e -> UpdateActionPerformed(e));
            panel1.add(Update);
            Update.setBounds(208, 656, 49, 23);

            //---- Einfuegen ----
            Einfuegen.setText("+");
            Einfuegen.setToolTipText("Neue Bestellung einf\u00fcgen");
            Einfuegen.addActionListener(e -> EinfuegenActionPerformed(e));
            panel1.add(Einfuegen);
            Einfuegen.setBounds(258, 656, 49, 23);

            //---- Loeschen ----
            Loeschen.setText("-");
            Loeschen.setToolTipText("Datensatz l\u00f6schen");
            Loeschen.addActionListener(e -> LoeschenActionPerformed(e));
            panel1.add(Loeschen);
            Loeschen.setBounds(308, 656, 49, 23);

            //---- Suchen ----
            Suchen.setText("?");
            Suchen.setToolTipText("Suche nach Autor, Titel, ISBN oder Druckereinummer");
            Suchen.addActionListener(e -> SuchenActionPerformed(e));
            panel1.add(Suchen);
            Suchen.setBounds(364, 656, 49, 23);

            //---- WSuchen ----
            WSuchen.setText("...");
            WSuchen.setToolTipText("Weitersuchen");
            WSuchen.addActionListener(e -> WSuchenActionPerformed(e));
            panel1.add(WSuchen);
            WSuchen.setBounds(414, 656, 49, 23);

            //---- Drucken ----
            Drucken.setText("D");
            Drucken.setToolTipText("Druckt die Rechnung f\u00fcr diese Bestellung");
            Drucken.addActionListener(e -> DruckenActionPerformed(e));
            panel1.add(Drucken);
            Drucken.setBounds(469, 656, 49, 23);

            //---- Mahnung ----
            Mahnung.setText("M");
            Mahnung.setToolTipText("Druckt eine Mahnung f\u00fcr die aktuelle Bestellung");
            Mahnung.addActionListener(e -> MahnungActionPerformed(e));
            panel1.add(Mahnung);
            Mahnung.setBounds(520, 656, 49, 23);

            //---- Schliessen ----
            Schliessen.setText("X");
            Schliessen.setToolTipText("Schlie\u00dft den Dialog");
            Schliessen.addActionListener(e -> SchliessenActionPerformed(e));
            panel1.add(Schliessen);
            Schliessen.setBounds(677, 656, 49, 23);

            //---- label2 ----
            label2.setText("E-Mail");
            panel1.add(label2);
            label2.setBounds(55, 380, 45, label2.getPreferredSize().height);
            panel1.add(field_EMail);
            field_EMail.setBounds(110, 371, 290, 25);

            //---- label3 ----
            label3.setText("DHL-Sendungsnr.");
            panel1.add(label3);
            label3.setBounds(460, 395, 115, label3.getPreferredSize().height);
            panel1.add(field_DHL);
            field_DHL.setBounds(580, 388, 115, 25);

            //---- EMail ----
            EMail.setText("E");
            EMail.setToolTipText("Versendet eine E-Mail mit einer Rechnung und DHL-Nummer");
            EMail.addActionListener(e -> EMailActionPerformed(e));
            panel1.add(EMail);
            EMail.setBounds(580, 656, 49, 23);

            //---- rbRemittende ----
            rbRemittende.setText("Remittende");
            rbRemittende.addActionListener(e -> rbRemittendeActionPerformed(e));
            panel1.add(rbRemittende);
            rbRemittende.setBounds(455, 288, 235, rbRemittende.getPreferredSize().height);

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
                    .addComponent(panel1, GroupLayout.PREFERRED_SIZE, 1086, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(12, Short.MAX_VALUE))
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(panel1, GroupLayout.DEFAULT_SIZE, 686, Short.MAX_VALUE)
                    .addContainerGap())
        );
        setSize(1110, 740);
        setLocationRelativeTo(getOwner());

        //---- buttonGroup1 ----
        var buttonGroup1 = new ButtonGroup();
        buttonGroup1.add(KDB);
        buttonGroup1.add(manuell);

        //---- buttonGroup2 ----
        var buttonGroup2 = new ButtonGroup();
        buttonGroup2.add(rbBestellung);
        buttonGroup2.add(rbRezension);
        buttonGroup2.add(rbPflicht);
        buttonGroup2.add(rbGeschenk);
        buttonGroup2.add(rbBeleg);
        buttonGroup2.add(rbRemittende);

        //---- buttonGroup3 ----
        var buttonGroup3 = new ButtonGroup();
        buttonGroup3.add(field_Ueberweisung);
        buttonGroup3.add(field_Verrechnung);
    }// </editor-fold>//GEN-END:initComponents

    private void WSuchenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_WSuchenActionPerformed
        // TODO add your handling code here:
        boolean gefunden = false;

        try {
            do {
            } while ((!gefunden) && resultB.next());
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("Bestellung: Weitersuchen", "SQL-Exception: ", exept.getMessage());
        }
    }//GEN-LAST:event_WSuchenActionPerformed

    private void AnfangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AnfangActionPerformed
        // TODO add your handling code here:
        try {
            resultB.first();
            count = 1;
            field_count.setText(Integer.toString(count));

            field_EMail.setText(resultB.getString("BESTELLUNG_EMAIL"));
            field_DHL.setText(resultB.getString("BESTELLUNG_DHL"));
            field_Zusatz_Text.setText(resultB.getString("BESTELLUNG_TEXT"));
            field_Zusatz_cb.setSelected(resultB.getBoolean("BESTELLUNG_TB"));
            field_Verrechnung.setSelected(resultB.getBoolean("BESTELLUNG_BEZAHLUNG"));
            field_Ueberweisung.setSelected(!resultB.getBoolean("BESTELLUNG_BEZAHLUNG"));
            switch (resultB.getInt("BESTELLUNG_TYP")) {
                case 0:
                    rbBestellung.setSelected(true);
                    break;
                case 1:
                    rbRezension.setSelected(true);
                    break;
                case 2:
                    rbPflicht.setSelected(true);
                    break;
                case 3:
                    rbGeschenk.setSelected(true);
                    break;
                case 4:
                    rbBeleg.setSelected(true);
                    break;
                case 5:
                    rbRemittende.setSelected(true);
                    break;
            }
            field_Zahlungsziel.setText(resultB.getString("BESTELLUNG_ZAHLUNGSZIEL"));
            field_Bestellzeichen.setText(resultB.getString("BESTELLUNG_BESTNR"));
            field_RechNr.setText(resultB.getString("BESTELLUNG_RECHNR"));
            field_Zeile1.setText(resultB.getString("BESTELLUNG_ZEILE_1"));
            field_Zeile2.setText(resultB.getString("BESTELLUNG_ZEILE_2"));
            field_Zeile3.setText(resultB.getString("BESTELLUNG_ZEILE_3"));
            field_Zeile4.setText(resultB.getString("BESTELLUNG_ZEILE_4"));
            field_Zeile5.setText(resultB.getString("BESTELLUNG_ZEILE_5"));
            field_Zeile6.setText(resultB.getString("BESTELLUNG_ZEILE_6"));
            field_UstrID.setText(resultB.getString("BESTELLUNG_USTR_ID"));
            field_Link.setText(resultB.getString("BESTELLUNG_LINK"));
            field_Versand.setText(Float.toString(resultB.getFloat("BESTELLUNG_VERSAND")));
            field_RechDat.setDate(Modulhelferlein.Date2SQLDate(resultB.getDate("BESTELLUNG_RECHDAT")));
            switch (resultB.getInt("BESTELLUNG_LAND")) {
                case 0:
                    field_Land.setSelected(false);
                    field_EU.setSelected(false);
                    field_Sprache.setSelected(false);
                    break;
                case 1:
                    field_Land.setSelected(false);
                    field_EU.setSelected(false);
                    field_Sprache.setSelected(true);
                    break;
                case 10:
                    field_Land.setSelected(true);
                    field_EU.setSelected(true);
                    field_Sprache.setSelected(false);
                    break;
                case 11:
                    field_Land.setSelected(true);
                    field_EU.setSelected(true);
                    field_Sprache.setSelected(true);
                    break;
                case 20:
                    field_Land.setSelected(true);
                    field_EU.setSelected(false);
                    field_Sprache.setSelected(false);
                    break;
                case 21:
                    field_Land.setSelected(true);
                    field_EU.setSelected(false);
                    field_Sprache.setSelected(true);
                    break;
            }
            field_Bestelldatum.setDate(Modulhelferlein.Date2SQLDate(resultB.getDate("BESTELLUNG_DATUM")));
            field_Bezahldatum.setDate(Modulhelferlein.Date2SQLDate(resultB.getDate("BESTELLUNG_BEZAHLT")));
            field_Privat.setSelected(resultB.getBoolean("BESTELLUNG_PRIVAT"));
            field_storniert.setSelected(resultB.getBoolean("BESTELLUNG_STORNIERT"));
            if (resultB.getInt("BESTELLUNG_KUNDE") > 0) {
                resultK = SQLAnfrageK.executeQuery("SELECT * FROM tbl_adresse WHERE ADRESSEN_ID = '" + Integer.toString(resultB.getInt("BESTELLUNG_KUNDE")) + "'");
                resultK.next();
                cbKunde.setSelectedItem(Integer.toString(resultB.getInt("BESTELLUNG_KUNDE")) + ", " + resultK.getString("ADRESSEN_NAME") + ", " + resultK.getString("ADRESSEN_VORNAME"));
                manuell.setSelected(false);
                KDB.setSelected(true);
            } else {
                cbKunde.setSelectedItem("0, ----------, -----------");
                manuell.setSelected(true);
                KDB.setSelected(false);
            }

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

            // Bestellungdetails lesen   
            resultBD = SQLAnfrageBD.executeQuery(
                    "SELECT * FROM TBL_BESTELLUNG_DETAIL WHERE BESTELLUNG_DETAIL_RECHNR = '" + resultB.getString("BESTELLUNG_RECHNR") + "'"); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 
            countBMax = 0;
            while (resultBD.next()) {
                ++countBMax;
            }
            if (resultBD.first()) {
                Anzahl = resultBD.getInt("BESTELLUNG_DETAIL_ANZAHL");
            } else {
                Anzahl = 0;
            }

// Rechnungsbetrag ermitteln
            resultBIsEmpty = false;
            resultBD = SQLAnfrageBD.executeQuery(
                    "SELECT * FROM TBL_BESTELLUNG_DETAIL WHERE BESTELLUNG_DETAIL_RECHNR = '" + resultB.getString("BESTELLUNG_RECHNR") + "'"); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 
            Double Rechnungsbetrag = 0D;
            Double Betrag19 = 0D;  // Bruttobetrag 19%
            Double Betrag7 = 0D;   // Bruttobetrag  7%
            while (resultBD.next()) {
                if (resultBD.getBoolean("BESTELLUNG_DETAIL_SONST")) {
                    Betrag19 = Betrag19 + resultBD.getFloat("BESTELLUNG_DETAIL_SONST_PREIS") * 1D;
                } else {
                    resultBuch = SQLAnfrageBuch.executeQuery("SELECT * FROM TBL_BUCH WHERE BUCH_ID = '" + resultBD.getString("BESTELLUNG_DETAIL_BUCH") + "'");
                    resultBuch.next();

                    Double ZPreis = resultBuch.getFloat("BUCH_PREIS") * 1D;
                    ZPreis = ZPreis - ZPreis / 100 * resultBD.getFloat("BESTELLUNG_DETAIL_RABATT");

                    ZPreis = Modulhelferlein.round2dec(ZPreis);
                    Betrag7 = Betrag7 + resultBD.getInt("BESTELLUNG_DETAIL_ANZAHL") * ZPreis;
                }
            }
// Rechnungsbetrag festlegen
// DEU:       Geschäft = Privat = Netto + UStr
// EU:        Geschäft = Netto
//            Privat   = Netto + UStr
// Drittland: Geschäft = Privat = Netto
            Rechnungsbetrag = 0D;
            switch (resultB.getInt("BESTELLUNG_LAND")) {
                case 0:
                    Rechnungsbetrag = Betrag7 + Betrag19;
                    break;
                case 10:
                case 11:
                    if (resultB.getBoolean("BESTELLUNG_PRIVAT")) {
                        Rechnungsbetrag = Betrag7 + Betrag19;
                    } else {
                        Rechnungsbetrag = Betrag7 / 107 * 100 + Betrag19 / 119 * 100;
                    }
                    break;
                case 20:
                case 21:
                    Rechnungsbetrag = Betrag7 - Betrag7 / 107 * 100 + Betrag19 - Betrag19 / 119 * 100;
                    break;
            }
            Rechnungsbetrag = Rechnungsbetrag + resultB.getFloat("BESTELLUNG_VERSAND");
            field_Betrag.setText(Modulhelferlein.str2dec(Rechnungsbetrag));

            resultBD.first();
            field_B_CountMax.setText(Integer.toString(countBMax));
            if (countBMax == 0) {
                // Schalterzustände setzen
                countB = 0;
                field_B_Count.setText(Integer.toString(countB));
                field_B_Rabatt.setText("");
                field_B_Anzahl.setText("");
                resultBIsEmpty = true;
                cbBuch.setSelectedIndex(0);
                BAnfang.setEnabled(false);
                BZurueck.setEnabled(false);
                BVor.setEnabled(false);
                BEnde.setEnabled(false);
                BUpdate.setEnabled(false);
                BEinfuegen.setEnabled(true);
                BLoeschen.setEnabled(false);
            } else {
                resultBIsEmpty = false;
                countB = 1;
                field_B_Sonstiges.setSelected(resultBD.getBoolean("BESTELLUNG_DETAIL_SONST"));
                field_B_Text.setText(resultBD.getString("BESTELLUNG_DETAIL_SONST_TEXT"));
                field_B_Preis.setText(Float.toString(resultBD.getFloat("BESTELLUNG_DETAIL_SONST_PREIS")));
                field_B_Count.setText(Integer.toString(countB));
                field_B_Rabatt.setText(Float.toString(resultBD.getFloat("BESTELLUNG_DETAIL_RABATT")));
                field_B_Anzahl.setText(Integer.toString(resultBD.getInt("BESTELLUNG_DETAIL_ANZAHL")));
                resultBuch = SQLAnfrageBuch.executeQuery("SELECT * FROM TBL_BUCH WHERE BUCH_ID = '" + resultBD.getString("BESTELLUNG_DETAIL_BUCH") + "'");
                resultBuch.next();
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
                cbBuch.setSelectedItem(eintrag);
                // Schalterzustände setzen   
                BAnfang.setEnabled(true);
                BZurueck.setEnabled(true);
                BVor.setEnabled(true);
                BEnde.setEnabled(true);
                BUpdate.setEnabled(true);
                BEinfuegen.setEnabled(true);
                BLoeschen.setEnabled(true);
            }
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("Bestellung: zum Anfang", "SQL-Exception: ", exept.getMessage());
        }
    }//GEN-LAST:event_AnfangActionPerformed

    private void EinfuegenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EinfuegenActionPerformed
        // TODO add your handling code here:
        int ID;

        ID = maxID + 1;
        maxID = maxID + 1;

        try {
            if (resultIsEmpty) {
                ID = 1;
            } else {
                resultB.last();
                ID = 1 + resultB.getInt("BESTELLUNG_ID");
            }
            resultB.moveToInsertRow();
            resultB.updateInt("BESTELLUNG_TYP", 0);
            resultB.updateInt("BESTELLUNG_ID", ID);
            resultB.updateInt("BESTELLUNG_ZAHLUNGSZIEL", 14);
            resultB.updateDate("BESTELLUNG_BEZAHLT", Modulhelferlein.Date2SQLDate(new Date(0)));
            resultB.updateDate("BESTELLUNG_DATUM", Modulhelferlein.Date2SQLDate(CurDate));
            resultB.updateDate("BESTELLUNG_RECHDAT", Modulhelferlein.Date2SQLDate(CurDate));
            resultB.updateInt("BESTELLUNG_KUNDE", -1);
            resultB.updateString("BESTELLUNG_BESTNR", "ohne");
            resultB.updateString("BESTELLUNG_ZEILE_1", "");
            resultB.updateString("BESTELLUNG_ZEILE_2", "");
            resultB.updateString("BESTELLUNG_ZEILE_3", "");
            resultB.updateString("BESTELLUNG_ZEILE_4", "");
            resultB.updateString("BESTELLUNG_ZEILE_5", "");
            resultB.updateString("BESTELLUNG_ZEILE_6", "");
            resultB.updateString("BESTELLUNG_USTR_ID", "");
            resultB.updateString("BESTELLUNG_LINK", "");
            resultB.updateFloat("BESTELLUNG_VERSAND", 0);
            resultB.updateInt("BESTELLUNG_LAND", 0);
            resultB.updateBoolean("BESTELLUNG_PRIVAT", false);
            resultB.updateBoolean("BESTELLUNG_BESTAND", false);
            resultB.updateBoolean("BESTELLUNG_TB", false);
            resultB.updateBoolean("BESTELLUNG_BEZAHLUNG", false);
            resultB.updateBoolean("BESTELLUNG_STORNIERT", false);
            resultB.updateString("BESTELLUNG_TEXT", "");
            resultB.updateString("BESTELLUNG_EMAIL", "");
            resultB.updateString("BESTELLUNG_DHL", "");
            field_Land.setSelected(false);
            field_Sprache.setSelected(false);
            int BestNr = resultBNr.getInt("BESTELLNR_NUMMER");
            String BestNrString = Integer.toString(BestNr);
            while (BestNrString.length() < 3) {
                BestNrString = "0" + BestNrString;
            }
            resultB.updateString("BESTELLUNG_RECHNR", Modulhelferlein.printSimpleDateFormat("yyyyMMdd") + "-" + BestNrString);
            resultB.insertRow();

            resultBNr.updateInt("BESTELLNR_NUMMER", BestNr + 1);
            resultBNr.updateRow();

            countMax = countMax + 1;
            field_countMax.setText(Integer.toString(countMax));
            count = countMax;
            field_count.setText(Integer.toString(count));
            resultBIsEmpty = true;

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

            resultB.last();
            switch (resultB.getInt("BESTELLUNG_TYP")) {
                case 0:
                    rbBestellung.setSelected(true);
                    break;
                case 1:
                    rbRezension.setSelected(true);
                    break;
                case 2:
                    rbPflicht.setSelected(true);
                    break;
                case 3:
                    rbGeschenk.setSelected(true);
                    break;
                case 4:
                    rbBeleg.setSelected(true);
                    break;
            }
            field_Bestellzeichen.setText(resultB.getString("BESTELLUNG_BESTNR"));
            field_RechNr.setText(resultB.getString("BESTELLUNG_RECHNR"));
            field_Zeile1.setText(resultB.getString("BESTELLUNG_ZEILE_1"));
            field_Zeile2.setText(resultB.getString("BESTELLUNG_ZEILE_2"));
            field_Zeile3.setText(resultB.getString("BESTELLUNG_ZEILE_3"));
            field_Zeile4.setText(resultB.getString("BESTELLUNG_ZEILE_4"));
            field_Zeile5.setText(resultB.getString("BESTELLUNG_ZEILE_5"));
            field_Zeile6.setText(resultB.getString("BESTELLUNG_ZEILE_6"));
            field_UstrID.setText(resultB.getString("BESTELLUNG_USTR_ID"));
            field_Link.setText(resultB.getString("BESTELLUNG_LINK"));
            field_EMail.setText(resultB.getString("BESTELLUNG_EMAIL"));
            field_DHL.setText(resultB.getString("BESTELLUNG_DHL"));
            field_Versand.setText(Float.toString(resultB.getFloat("BESTELLUNG_VERSAND")));
            switch (resultB.getInt("BESTELLUNG_LAND")) {
                case 0:
                    field_Land.setSelected(false);
                    field_EU.setSelected(false);
                    field_Sprache.setSelected(false);
                    break;
                case 1:
                    field_Land.setSelected(false);
                    field_EU.setSelected(false);
                    field_Sprache.setSelected(true);
                    break;
                case 10:
                    field_Land.setSelected(true);
                    field_EU.setSelected(true);
                    field_Sprache.setSelected(false);
                    break;
                case 11:
                    field_Land.setSelected(true);
                    field_EU.setSelected(true);
                    field_Sprache.setSelected(true);
                    break;
                case 20:
                    field_Land.setSelected(true);
                    field_EU.setSelected(false);
                    field_Sprache.setSelected(false);
                    break;
                case 21:
                    field_Land.setSelected(true);
                    field_EU.setSelected(false);
                    field_Sprache.setSelected(true);
                    break;
            }
            field_Privat.setSelected(resultB.getBoolean("BESTELLUNG_PRIVAT"));
            field_storniert.setSelected(resultB.getBoolean("BESTELLUNG_STORNIERT"));
            field_Bestelldatum.setDate(Modulhelferlein.Date2SQLDate(resultB.getDate("BESTELLUNG_DATUM")));
            field_Bezahldatum.setDate(Modulhelferlein.Date2SQLDate(resultB.getDate("BESTELLUNG_BEZAHLT")));
            field_RechDat.setDate(Modulhelferlein.Date2SQLDate(resultB.getDate("BESTELLUNG_RECHDAT")));
            manuell.setSelected(true);

            // Schalterzustände setzen Bestelldetails   
            countB = 0;
            field_B_Count.setText("0");
            field_B_Count.setEditable(false);
            countBMax = 0;
            field_B_CountMax.setText("0");
            field_B_CountMax.setEditable(false);
            field_B_Rabatt.setText("0");
            field_B_Rabatt.setEditable(false);
            field_B_Anzahl.setText("0");
            field_B_Anzahl.setEditable(false);

            BAnfang.setEnabled(false);
            BZurueck.setEnabled(false);
            BVor.setEnabled(false);
            BEnde.setEnabled(false);
            BUpdate.setEnabled(false);
            BEinfuegen.setEnabled(true);
            BLoeschen.setEnabled(false);
            field_B_Rabatt.setEditable(false);
            field_B_Anzahl.setEditable(false);
            cbBuch.setEditable(false);

        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("Bestellung: Einfügen", "SQL-Exception: Einfügen: ", exept.getMessage());
        }
    }//GEN-LAST:event_EinfuegenActionPerformed

    private void SuchenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SuchenActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_SuchenActionPerformed

    private void UpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UpdateActionPerformed
        // TODO add your handling code here:
        try {
            if (Modulhelferlein.checkNumberFormatFloat(field_Versand.getText()) < 0) {
                Modulhelferlein.Infomeldung("fehlerhafte Eingabe der Versandkosten", "es ist keine korrekte Zahl");
            } else if ((Modulhelferlein.checkNumberFormatFloat(field_B_Rabatt.getText()) < 0) && (countB > 0)) {
                Modulhelferlein.Infomeldung("fehlerhafte Eingabe des Rabattes", "es ist keine korrekte Zahl");
            } else if (Modulhelferlein.checkNumberFormatInt(field_Zahlungsziel.getText()) < 0) {
                Modulhelferlein.Infomeldung("fehlerhafte Eingabe der Tage", "es ist keine korrekte Ganzzahl");
            } else {
                if ((Modulhelferlein.checkNumberFormatInt(field_B_Anzahl.getText()) < 0) && (countB > 0)) {
                    Modulhelferlein.Infomeldung("fehlerhafte Eingabe der Anzahl", "es ist keine korrekte Ganzzahl");
                } else {
                    resultB.updateDate("BESTELLUNG_BEZAHLT", Modulhelferlein.Date2SQLDate(field_Bezahldatum.getDate()));
                    resultB.updateDate("BESTELLUNG_DATUM", Modulhelferlein.Date2SQLDate(field_Bestelldatum.getDate()));
                    resultB.updateDate("BESTELLUNG_RECHDAT", Modulhelferlein.Date2SQLDate(field_RechDat.getDate()));
                    if (manuell.isSelected()) {
                        resultB.updateInt("BESTELLUNG_KUNDE", -1);
                    } else {
                        String KdeID[] = cbKunde.getItemAt(cbKunde.getSelectedIndex()).split(",");
                        resultB.updateInt("BESTELLUNG_KUNDE", Integer.parseInt(KdeID[0]));
                    }
                    resultB.updateBoolean("BESTELLUNG_BEZAHLUNG", field_Verrechnung.isSelected());
                    resultB.updateBoolean("BESTELLUNG_TB", field_Zusatz_cb.isSelected());
                    resultB.updateString("BESTELLUNG_TEXT", field_Zusatz_Text.getText());
                    resultB.updateBoolean("BESTELLUNG_PRIVAT", field_Privat.isSelected());
                    resultB.updateBoolean("BESTELLUNG_STORNIERT", field_storniert.isSelected());
                    resultB.updateString("BESTELLUNG_BESTNR", field_Bestellzeichen.getText());
                    resultB.updateString("BESTELLUNG_ZEILE_1", field_Zeile1.getText());
                    resultB.updateString("BESTELLUNG_ZEILE_2", field_Zeile2.getText());
                    resultB.updateString("BESTELLUNG_ZEILE_3", field_Zeile3.getText());
                    resultB.updateString("BESTELLUNG_ZEILE_4", field_Zeile4.getText());
                    resultB.updateString("BESTELLUNG_ZEILE_5", field_Zeile5.getText());
                    resultB.updateString("BESTELLUNG_ZEILE_6", field_Zeile6.getText());
                    resultB.updateString("BESTELLUNG_USTR_ID", field_UstrID.getText());
                    resultB.updateString("BESTELLUNG_LINK", field_Link.getText());
                    resultB.updateString("BESTELLUNG_EMAIL", field_EMail.getText());
                    resultB.updateString("BESTELLUNG_DHL", field_DHL.getText());
                    resultB.updateFloat("BESTELLUNG_VERSAND", Float.parseFloat(field_Versand.getText()));
                    if (rbBestellung.isSelected()) {
                        resultB.updateInt("BESTELLUNG_TYP", 0);
                    } else if (rbRezension.isSelected()) {
                        resultB.updateInt("BESTELLUNG_TYP", 1);
                    } else if (rbPflicht.isSelected()) {
                        resultB.updateInt("BESTELLUNG_TYP", 2);
                    } else if (rbBeleg.isSelected()) {
                        resultB.updateInt("BESTELLUNG_TYP", 3);
                    } else if (rbRemittende.isSelected()) {
                        resultB.updateInt("BESTELLUNG_TYP", 5);
                    } else { // rbGeschenk
                        resultB.updateInt("BESTELLUNG_TYP", 4);
                    }
                    if (field_Land.isSelected()) { // Ausland
                        if (field_EU.isSelected()) { //EU-Ausland
                            if (field_Sprache.isSelected()) {  // Englisch
                                resultB.updateInt("BESTELLUNG_LAND", 11);
                            } else { // Deutsch
                                resultB.updateInt("BESTELLUNG_LAND", 10);
                            }
                        } else { //Nicht-EU-Ausland
                            if (field_Sprache.isSelected()) {  // Englisch
                                resultB.updateInt("BESTELLUNG_LAND", 21);
                            } else { // Deutsch
                                resultB.updateInt("BESTELLUNG_LAND", 20);
                            }
                        }
                    } else {  //Inland
                        if (field_Sprache.isSelected()) {  // Englisch
                            resultB.updateInt("BESTELLUNG_LAND", 1);
                        } else { // Deutsch
                            resultB.updateInt("BESTELLUNG_LAND", 0);
                        }
                    }
                    if (field_storniert.isSelected()) {
                        resultBD = SQLAnfrageBD.executeQuery(
                                "SELECT * FROM TBL_BESTELLUNG_DETAIL WHERE BESTELLUNG_DETAIL_RECHNR = '" + resultB.getString("BESTELLUNG_RECHNR") + "'");
                        while (resultBD.next()) {
                            resultBD.updateFloat("BESTELLUNG_DETAIL_RABATT", 0F);
                            resultBD.updateInt("BESTELLUNG_DETAIL_ANZAHL", 0);
                            resultBD.updateRow();
                        }
                    }
                    initListeRechnungenbestand();
                    resultB.updateRow();
                }
            }
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("Bestellung: Update:", "SQL-Exception: ", exept.getMessage());
        }
    }//GEN-LAST:event_UpdateActionPerformed

    private void LoeschenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LoeschenActionPerformed
        // TODO add your handling code here:
        if (JOptionPane.showConfirmDialog(null, "Soll der Datensatz wirklich gelöscht werden?") == JOptionPane.YES_OPTION) {
            try {
                // Bestellungdetails lesen   
                resultBD = SQLAnfrageBD.executeQuery(
                        "SELECT * FROM TBL_BESTELLUNG_DETAIL WHERE BESTELLUNG_DETAIL_RECHNR = '" + resultB.getString("BESTELLUNG_RECHNR") + "'");
                while (resultBD.next()) {
                    resultBuch = SQLAnfrageBuch.executeQuery("SELECT * FROM TBL_BUCH WHERE BUCH_ID = '" + resultBD.getString("BESTELLUNG_DETAIL_BUCH") + "'");
                    resultBuch.updateInt("BUCH_BESTAND", resultBuch.getInt("BUCH_BESTAND") + resultBD.getInt("BESTELLUNG_DETAIL_ANZAHL"));
                    resultBuch.updateRow();
                    resultBD.deleteRow();
                }
                resultB.deleteRow();
                countMax = countMax - 1;
                field_countMax.setText(Integer.toString(countMax));
                count = 1;
                field_count.setText(Integer.toString(count));

                resultB.first();

                switch (resultB.getInt("BESTELLUNG_TYP")) {
                    case 0:
                        rbBestellung.setSelected(true);
                        break;
                    case 1:
                        rbRezension.setSelected(true);
                        break;
                    case 2:
                        rbPflicht.setSelected(true);
                        break;
                    case 3:
                        rbGeschenk.setSelected(true);
                        break;
                    case 4:
                        rbBeleg.setSelected(true);
                        break;
                    case 5:
                        rbRemittende.setSelected(true);
                        break;
                }
                field_EMail.setText(resultB.getString("BESTELLUNG_EMAIL"));
                field_DHL.setText(resultB.getString("BESTELLUNG_DHL"));
                field_Zahlungsziel.setText(resultB.getString("BESTELLUNG_ZAHLUNGSZIEL"));
                field_Zusatz_Text.setText(resultB.getString("BESTELLUNG_TEXT"));
                field_Zusatz_cb.setSelected(resultB.getBoolean("BESTELLUNG_TB"));
                field_Verrechnung.setSelected(resultB.getBoolean("BESTELLUNG_BEZAHLUNG"));
                field_Ueberweisung.setSelected(!resultB.getBoolean("BESTELLUNG_BEZAHLUNG"));
                field_Bestellzeichen.setText(resultB.getString("BESTELLUNG_BESTNR"));
                field_RechNr.setText(resultB.getString("BESTELLUNG_RECHNR"));
                field_Zeile1.setText(resultB.getString("BESTELLUNG_ZEILE_1"));
                field_Zeile2.setText(resultB.getString("BESTELLUNG_ZEILE_2"));
                field_Zeile3.setText(resultB.getString("BESTELLUNG_ZEILE_3"));
                field_Zeile4.setText(resultB.getString("BESTELLUNG_ZEILE_4"));
                field_Zeile5.setText(resultB.getString("BESTELLUNG_ZEILE_5"));
                field_Zeile6.setText(resultB.getString("BESTELLUNG_ZEILE_6"));
                field_UstrID.setText(resultB.getString("BESTELLUNG_USTR_ID"));
                field_Link.setText(resultB.getString("BESTELLUNG_LINK"));
                field_Versand.setText(Float.toString(resultB.getFloat("BESTELLUNG_VERSAND")));
                switch (resultB.getInt("BESTELLUNG_LAND")) {
                    case 0:
                        field_Land.setSelected(false);
                        field_EU.setSelected(false);
                        field_Sprache.setSelected(false);
                        break;
                    case 1:
                        field_Land.setSelected(false);
                        field_EU.setSelected(false);
                        field_Sprache.setSelected(true);
                        break;
                    case 10:
                        field_Land.setSelected(true);
                        field_EU.setSelected(true);
                        field_Sprache.setSelected(false);
                        break;
                    case 11:
                        field_Land.setSelected(true);
                        field_EU.setSelected(true);
                        field_Sprache.setSelected(true);
                        break;
                    case 20:
                        field_Land.setSelected(true);
                        field_EU.setSelected(false);
                        field_Sprache.setSelected(false);
                        break;
                    case 21:
                        field_Land.setSelected(true);
                        field_EU.setSelected(false);
                        field_Sprache.setSelected(true);
                        break;
                }
                field_Bestelldatum.setDate(Modulhelferlein.Date2SQLDate(resultB.getDate("BESTELLUNG_DATUM")));
                field_Bezahldatum.setDate(Modulhelferlein.Date2SQLDate(resultB.getDate("BESTELLUNG_BEZAHLT")));
                field_RechDat.setDate(Modulhelferlein.Date2SQLDate(resultB.getDate("BESTELLUNG_RECHDAT")));
                field_Privat.setSelected(resultB.getBoolean("BESTELLUNG_PRIVAT"));
                field_storniert.setSelected(resultB.getBoolean("BESTELLUNG_STORNIERT"));
                if (resultB.getInt("BESTELLUNG_KUNDE") > 0) {
                    resultK = SQLAnfrageK.executeQuery("SELECT * FROM tbl_adresse WHERE ADRESSEN_ID = '" + Integer.toString(resultB.getInt("BESTELLUNG_KUNDE")) + "'");
                    resultK.next();
                    cbKunde.setSelectedItem(Integer.toString(resultB.getInt("BESTELLUNG_KUNDE")) + ", " + resultK.getString("ADRESSEN_NAME") + ", " + resultK.getString("ADRESSEN_VORNAME"));
                    manuell.setSelected(false);
                    KDB.setSelected(true);
                } else {
                    cbKunde.setSelectedItem("0, ----------, -----------");
                    manuell.setSelected(true);
                    KDB.setSelected(false);
                }

                if (resultB.getRow() > 0) {
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

                // Bestellungdetails lesen   
                resultBD = SQLAnfrageBD.executeQuery(
                        "SELECT * FROM TBL_BESTELLUNG_DETAIL WHERE BESTELLUNG_DETAIL_RECHNR = '" + resultB.getString("BESTELLUNG_RECHNR") + "'"); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 
                countBMax = 0;
                while (resultBD.next()) {
                    ++countBMax;
                }
                field_B_CountMax.setText(Integer.toString(countBMax));
                if (resultBD.first()) {
                    Anzahl = resultBD.getInt("BESTELLUNG_DETAIL_ANZAHL");
                } else {
                    Anzahl = 0;
                }

                // Rechnungsbetrag ermitteln
                resultBIsEmpty = false;
                resultBD = SQLAnfrageBD.executeQuery(
                        "SELECT * FROM TBL_BESTELLUNG_DETAIL WHERE BESTELLUNG_DETAIL_RECHNR = '" + resultB.getString("BESTELLUNG_RECHNR") + "'"); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 
                Double Rechnungsbetrag = 0D;
                while (resultBD.next()) {
                    if (resultBD.getBoolean("BESTELLUNG_DETAIL_SONST")) {
                        Rechnungsbetrag = Rechnungsbetrag + resultBD.getFloat("BESTELLUNG_DETAIL_SONST_PREIS") * 1D;
                    } else {
                        resultBuch = SQLAnfrageBuch.executeQuery("SELECT * FROM TBL_BUCH WHERE BUCH_ID = '" + resultBD.getString("BESTELLUNG_DETAIL_BUCH") + "'");
                        resultBuch.next();

                        Double ZPreis = resultBuch.getFloat("BUCH_PREIS") * 1D;
                        ZPreis = ZPreis - ZPreis / 100 * resultBD.getFloat("BESTELLUNG_DETAIL_RABATT");

                        ZPreis = Modulhelferlein.round2dec(ZPreis);
                        Rechnungsbetrag = Rechnungsbetrag + resultBD.getInt("BESTELLUNG_DETAIL_ANZAHL") * ZPreis;
                    }
                }
                Rechnungsbetrag = Rechnungsbetrag + resultB.getFloat("BESTELLUNG_VERSAND");
                field_Betrag.setText(Modulhelferlein.str2dec(Rechnungsbetrag));

                if (countBMax == 0) {
                    // Schalterzustände setzen
                    countB = 0;
                    field_B_Sonstiges.setSelected(false);
                    field_B_Text.setText("");
                    field_B_Preis.setText("");
                    field_B_Count.setText(Integer.toString(countB));
                    field_B_Rabatt.setText("");
                    field_B_Anzahl.setText("");
                    resultBIsEmpty = true;
                    cbBuch.setSelectedIndex(0);
                    BAnfang.setEnabled(false);
                    BZurueck.setEnabled(false);
                    BVor.setEnabled(false);
                    BEnde.setEnabled(false);
                    BUpdate.setEnabled(false);
                    BEinfuegen.setEnabled(true);
                    BLoeschen.setEnabled(false);
                } else {
                    resultBIsEmpty = false;
                    countB = 1;
                    field_B_Sonstiges.setSelected(resultBD.getBoolean("BESTELLUNG_DETAIL_SONST"));
                    field_B_Text.setText(resultBD.getString("BESTELLUNG_DETAIL_SONST_TEXT"));
                    field_B_Preis.setText(Float.toString(resultBD.getFloat("BESTELLUNG_DETAIL_SONST_PREIS")));
                    field_B_Count.setText(Integer.toString(countB));
                    field_B_Rabatt.setText(Float.toString(resultBD.getFloat("BESTELLUNG_DETAIL_RABATT")));
                    field_B_Anzahl.setText(Integer.toString(resultBD.getInt("BESTELLUNG_DETAIL_ANZAHL")));
                    resultBuch = SQLAnfrageBuch.executeQuery("SELECT * FROM TBL_BUCH WHERE BUCH_ID = '" + resultBD.getString("BESTELLUNG_DETAIL_BUCH") + "'");
                    resultBuch.next();
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
                    cbBuch.setSelectedItem(eintrag);
                    // Schalterzustände setzen   
                    BAnfang.setEnabled(true);
                    BZurueck.setEnabled(true);
                    BVor.setEnabled(true);
                    BEnde.setEnabled(true);
                    BUpdate.setEnabled(true);
                    BEinfuegen.setEnabled(true);
                    BLoeschen.setEnabled(true);
                }
            } catch (SQLException exept) {
                Modulhelferlein.Fehlermeldung("Bestellung: Löschen", "SQL-Exception: ", exept.getMessage());
            }
        }
    }//GEN-LAST:event_LoeschenActionPerformed

    private void SchliessenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SchliessenActionPerformed
        // TODO add your handling code here:
        try {
            if (resultB != null) {
                resultB.close();
            }
        } catch (SQLException e) {
            Modulhelferlein.Fehlermeldung("Bestell-Dialog schließen", "SQL-Exception: ", e.getMessage());
        }
        try {
            if (resultBD != null) {
                resultBD.close();
            }
        } catch (SQLException e) {
            Modulhelferlein.Fehlermeldung("Bestell-Dialog schließen", "SQL-Exception: ", e.getMessage());
        }
        try {
            if (resultK != null) {
                resultK.close();
            }
        } catch (SQLException e) {
            Modulhelferlein.Fehlermeldung("Bestell-Dialog schließen", "SQL-Exception: ", e.getMessage());
        }
        try {
            if (resultBNr != null) {
                resultBNr.close();
            }
        } catch (SQLException e) {
            Modulhelferlein.Fehlermeldung("Bestell-Dialog schließen", "SQL-Exception: ", e.getMessage());
        }
        try {
            if (resultBuch != null) {
                resultBuch.close();
            }
        } catch (SQLException e) {
            Modulhelferlein.Fehlermeldung("Bestell-Dialog schließen", "SQL-Exception: ", e.getMessage());
        }
        try {
            if (SQLAnfrageB != null) {
                SQLAnfrageB.close();
            }
        } catch (SQLException e) {
            Modulhelferlein.Fehlermeldung("Bestell-Dialog schließen", "SQL-Exception: ", e.getMessage());
        }
        try {
            if (SQLAnfrageBD != null) {
                SQLAnfrageBD.close();
            }
        } catch (SQLException e) {
            Modulhelferlein.Fehlermeldung("Bestell-Dialog schließen", "SQL-Exception: ", e.getMessage());
        }
        try {
            if (SQLAnfrageK != null) {
                SQLAnfrageK.close();
            }
        } catch (SQLException e) {
            Modulhelferlein.Fehlermeldung("Bestell-Dialog schließen", "SQL-Exception: ", e.getMessage());
        }
        try {
            if (SQLAnfrageBNr != null) {
                SQLAnfrageBNr.close();
            }
        } catch (SQLException e) {
            Modulhelferlein.Fehlermeldung("Bestell-Dialog schließen", "SQL-Exception: ", e.getMessage());
        }
        try {
            if (SQLAnfrageBuch != null) {
                SQLAnfrageBuch.close();
            }
        } catch (SQLException e) {
            Modulhelferlein.Fehlermeldung("Bestell-Dialog schließen", "SQL-Exception: ", e.getMessage());
        }
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            Modulhelferlein.Fehlermeldung("Bestell-Dialog schließen", "SQL-Exception: ", e.getMessage());
        }
        this.dispose();
    }//GEN-LAST:event_SchliessenActionPerformed

    private void ZurueckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ZurueckActionPerformed
        // TODO add your handling code here:
        try {
            if (resultB.previous()) {
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
                resultB.next();
            }
            switch (resultB.getInt("BESTELLUNG_TYP")) {
                case 0:
                    rbBestellung.setSelected(true);
                    break;
                case 1:
                    rbRezension.setSelected(true);
                    break;
                case 2:
                    rbPflicht.setSelected(true);
                    break;
                case 3:
                    rbGeschenk.setSelected(true);
                    break;
                case 4:
                    rbBeleg.setSelected(true);
                    break;
                case 5:
                    rbRemittende.setSelected(true);
                    break;
            }
            field_EMail.setText(resultB.getString("BESTELLUNG_EMAIL"));
            field_DHL.setText(resultB.getString("BESTELLUNG_DHL"));
            field_Zahlungsziel.setText(resultB.getString("BESTELLUNG_ZAHLUNGSZIEL"));
            field_Zusatz_Text.setText(resultB.getString("BESTELLUNG_TEXT"));
            field_Zusatz_cb.setSelected(resultB.getBoolean("BESTELLUNG_TB"));
            field_Verrechnung.setSelected(resultB.getBoolean("BESTELLUNG_BEZAHLUNG"));
            field_Ueberweisung.setSelected(!resultB.getBoolean("BESTELLUNG_BEZAHLUNG"));
            field_Bestellzeichen.setText(resultB.getString("BESTELLUNG_BESTNR"));
            field_RechNr.setText(resultB.getString("BESTELLUNG_RECHNR"));
            field_Zeile1.setText(resultB.getString("BESTELLUNG_ZEILE_1"));
            field_Zeile2.setText(resultB.getString("BESTELLUNG_ZEILE_2"));
            field_Zeile3.setText(resultB.getString("BESTELLUNG_ZEILE_3"));
            field_Zeile4.setText(resultB.getString("BESTELLUNG_ZEILE_4"));
            field_Zeile5.setText(resultB.getString("BESTELLUNG_ZEILE_5"));
            field_Zeile6.setText(resultB.getString("BESTELLUNG_ZEILE_6"));
            field_Link.setText(resultB.getString("BESTELLUNG_LINK"));
            field_UstrID.setText(resultB.getString("BESTELLUNG_USTR_ID"));
            field_Versand.setText(Float.toString(resultB.getFloat("BESTELLUNG_VERSAND")));
            field_RechDat.setDate(Modulhelferlein.Date2SQLDate(resultB.getDate("BESTELLUNG_RECHDAT")));
            switch (resultB.getInt("BESTELLUNG_LAND")) {
                case 0:
                    field_Land.setSelected(false);
                    field_EU.setSelected(false);
                    field_Sprache.setSelected(false);
                    break;
                case 1:
                    field_Land.setSelected(false);
                    field_EU.setSelected(false);
                    field_Sprache.setSelected(true);
                    break;
                case 10:
                    field_Land.setSelected(true);
                    field_EU.setSelected(true);
                    field_Sprache.setSelected(false);
                    break;
                case 11:
                    field_Land.setSelected(true);
                    field_EU.setSelected(true);
                    field_Sprache.setSelected(true);
                    break;
                case 20:
                    field_Land.setSelected(true);
                    field_EU.setSelected(false);
                    field_Sprache.setSelected(false);
                    break;
                case 21:
                    field_Land.setSelected(true);
                    field_EU.setSelected(false);
                    field_Sprache.setSelected(true);
                    break;
            }
            field_Bestelldatum.setDate(Modulhelferlein.Date2SQLDate(resultB.getDate("BESTELLUNG_DATUM")));
            field_Bezahldatum.setDate(Modulhelferlein.Date2SQLDate(resultB.getDate("BESTELLUNG_BEZAHLT")));
            field_Privat.setSelected(resultB.getBoolean("BESTELLUNG_PRIVAT"));
            field_storniert.setSelected(resultB.getBoolean("BESTELLUNG_STORNIERT"));
            if (resultB.getInt("BESTELLUNG_KUNDE") > 0) {
                resultK = SQLAnfrageK.executeQuery("SELECT * FROM tbl_adresse WHERE ADRESSEN_ID = '" + Integer.toString(resultB.getInt("BESTELLUNG_KUNDE")) + "'");
                resultK.next();
                cbKunde.setSelectedItem(Integer.toString(resultB.getInt("BESTELLUNG_KUNDE")) + ", " + resultK.getString("ADRESSEN_NAME") + ", " + resultK.getString("ADRESSEN_VORNAME"));
                manuell.setSelected(false);
                KDB.setSelected(true);
            } else {
                cbKunde.setSelectedItem("0, ----------, -----------");
                manuell.setSelected(true);
                KDB.setSelected(false);
            }

            // Bestellungdetails lesen   
            resultBD = SQLAnfrageBD.executeQuery(
                    "SELECT * FROM TBL_BESTELLUNG_DETAIL WHERE BESTELLUNG_DETAIL_RECHNR = '" + resultB.getString("BESTELLUNG_RECHNR") + "'"); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 
            countBMax = 0;
            while (resultBD.next()) {
                ++countBMax;
            }
            if (resultBD.first()) {
                Anzahl = resultBD.getInt("BESTELLUNG_DETAIL_ANZAHL");
            } else {
                Anzahl = 0;
            }

// Rechnungsbetrag ermitteln
            resultBIsEmpty = false;
            resultBD = SQLAnfrageBD.executeQuery(
                    "SELECT * FROM TBL_BESTELLUNG_DETAIL WHERE BESTELLUNG_DETAIL_RECHNR = '" + resultB.getString("BESTELLUNG_RECHNR") + "'"); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 
            Double Rechnungsbetrag = 0D;
            Double Betrag19 = 0D;  // Bruttobetrag 19%
            Double Betrag7 = 0D;   // Bruttobetrag  7%
            while (resultBD.next()) {
                if (resultBD.getBoolean("BESTELLUNG_DETAIL_SONST")) {
                    Betrag19 = Betrag19 + resultBD.getFloat("BESTELLUNG_DETAIL_SONST_PREIS") * 1D;
                } else {
                    resultBuch = SQLAnfrageBuch.executeQuery("SELECT * FROM TBL_BUCH WHERE BUCH_ID = '" + resultBD.getString("BESTELLUNG_DETAIL_BUCH") + "'");
                    resultBuch.next();

                    Double ZPreis = resultBuch.getFloat("BUCH_PREIS") * 1D;
                    ZPreis = ZPreis - ZPreis / 100 * resultBD.getFloat("BESTELLUNG_DETAIL_RABATT");

                    ZPreis = Modulhelferlein.round2dec(ZPreis);
                    Betrag7 = Betrag7 + resultBD.getInt("BESTELLUNG_DETAIL_ANZAHL") * ZPreis;
                }
            }
// Rechnungsbetrag festlegen
// DEU:       Geschäft = Privat = Netto + UStr
// EU:        Geschäft = Netto
//            Privat   = Netto + UStr
// Drittland: Geschäft = Privat = Netto
            Rechnungsbetrag = 0D;
            switch (resultB.getInt("BESTELLUNG_LAND")) {
                case 0:
                    Rechnungsbetrag = Betrag7 + Betrag19;
                    break;
                case 10:
                case 11:
                    if (resultB.getBoolean("BESTELLUNG_PRIVAT")) {
                        Rechnungsbetrag = Betrag7 + Betrag19;
                    } else {
                        Rechnungsbetrag = Betrag7 / 107 * 100 + Betrag19 / 119 * 100;
                    }
                    break;
                case 20:
                case 21:
                    Rechnungsbetrag = Betrag7 - Betrag7 / 107 * 100 + Betrag19 - Betrag19 / 119 * 100;
                    break;
            }
            Rechnungsbetrag = Rechnungsbetrag + resultB.getFloat("BESTELLUNG_VERSAND");
            field_Betrag.setText(Modulhelferlein.str2dec(Rechnungsbetrag));

            resultBD.first();
            field_B_CountMax.setText(Integer.toString(countBMax));
            if (countBMax == 0) {
                // Schalterzustände setzen
                countB = 0;
                field_B_Count.setText(Integer.toString(countB));
                field_B_Rabatt.setText("");
                field_B_Anzahl.setText("");
                resultBIsEmpty = true;
                cbBuch.setSelectedIndex(0);
                BAnfang.setEnabled(false);
                BZurueck.setEnabled(false);
                BVor.setEnabled(false);
                BEnde.setEnabled(false);
                BUpdate.setEnabled(false);
                BEinfuegen.setEnabled(true);
                BLoeschen.setEnabled(false);
            } else {
                resultBIsEmpty = false;
                countB = 1;
                field_B_Sonstiges.setSelected(resultBD.getBoolean("BESTELLUNG_DETAIL_SONST"));
                field_B_Text.setText(resultBD.getString("BESTELLUNG_DETAIL_SONST_TEXT"));
                field_B_Preis.setText(Float.toString(resultBD.getFloat("BESTELLUNG_DETAIL_SONST_PREIS")));
                field_B_Count.setText(Integer.toString(countB));
                field_B_Rabatt.setText(Float.toString(resultBD.getFloat("BESTELLUNG_DETAIL_RABATT")));
                field_B_Anzahl.setText(Integer.toString(resultBD.getInt("BESTELLUNG_DETAIL_ANZAHL")));
                resultBuch = SQLAnfrageBuch.executeQuery("SELECT * FROM TBL_BUCH WHERE BUCH_ID = '" + resultBD.getString("BESTELLUNG_DETAIL_BUCH") + "'");
                resultBuch.next();
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
                cbBuch.setSelectedItem(eintrag);
                // Schalterzustände setzen   
                BAnfang.setEnabled(true);
                BZurueck.setEnabled(true);
                BVor.setEnabled(true);
                BEnde.setEnabled(true);
                BUpdate.setEnabled(true);
                BEinfuegen.setEnabled(true);
                BLoeschen.setEnabled(true);
            }
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("Bestellung: zum vorherigen", "SQL-Exception: ", exept.getMessage());
        }
    }//GEN-LAST:event_ZurueckActionPerformed

    private void VorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_VorActionPerformed
        // TODO add your handling code here:
        try {
            if (resultB.next()) {
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
                resultB.previous();
            }
            switch (resultB.getInt("BESTELLUNG_TYP")) {
                case 0:
                    rbBestellung.setSelected(true);
                    break;
                case 1:
                    rbRezension.setSelected(true);
                    break;
                case 2:
                    rbPflicht.setSelected(true);
                    break;
                case 3:
                    rbGeschenk.setSelected(true);
                    break;
                case 4:
                    rbBeleg.setSelected(true);
                    break;
                case 5:
                    rbRemittende.setSelected(true);
                    break;
            }
            field_EMail.setText(resultB.getString("BESTELLUNG_EMAIL"));
            field_DHL.setText(resultB.getString("BESTELLUNG_DHL"));
            field_Zahlungsziel.setText(resultB.getString("BESTELLUNG_ZAHLUNGSZIEL"));
            field_Zusatz_Text.setText(resultB.getString("BESTELLUNG_TEXT"));
            field_Zusatz_cb.setSelected(resultB.getBoolean("BESTELLUNG_TB"));
            field_Verrechnung.setSelected(resultB.getBoolean("BESTELLUNG_BEZAHLUNG"));
            field_Ueberweisung.setSelected(!resultB.getBoolean("BESTELLUNG_BEZAHLUNG"));
            field_Bestellzeichen.setText(resultB.getString("BESTELLUNG_BESTNR"));
            field_RechNr.setText(resultB.getString("BESTELLUNG_RECHNR"));
            field_Zeile1.setText(resultB.getString("BESTELLUNG_ZEILE_1"));
            field_Zeile2.setText(resultB.getString("BESTELLUNG_ZEILE_2"));
            field_Zeile3.setText(resultB.getString("BESTELLUNG_ZEILE_3"));
            field_Zeile4.setText(resultB.getString("BESTELLUNG_ZEILE_4"));
            field_Zeile5.setText(resultB.getString("BESTELLUNG_ZEILE_5"));
            field_Zeile6.setText(resultB.getString("BESTELLUNG_ZEILE_6"));
            field_Link.setText(resultB.getString("BESTELLUNG_LINK"));
            field_UstrID.setText(resultB.getString("BESTELLUNG_USTR_ID"));
            field_Versand.setText(Float.toString(resultB.getFloat("BESTELLUNG_VERSAND")));
            field_RechDat.setDate(Modulhelferlein.Date2SQLDate(resultB.getDate("BESTELLUNG_RECHDAT")));
            switch (resultB.getInt("BESTELLUNG_LAND")) {
                case 0:
                    field_Land.setSelected(false);
                    field_EU.setSelected(false);
                    field_Sprache.setSelected(false);
                    break;
                case 1:
                    field_Land.setSelected(false);
                    field_EU.setSelected(false);
                    field_Sprache.setSelected(true);
                    break;
                case 10:
                    field_Land.setSelected(true);
                    field_EU.setSelected(true);
                    field_Sprache.setSelected(false);
                    break;
                case 11:
                    field_Land.setSelected(true);
                    field_EU.setSelected(true);
                    field_Sprache.setSelected(true);
                    break;
                case 20:
                    field_Land.setSelected(true);
                    field_EU.setSelected(false);
                    field_Sprache.setSelected(false);
                    break;
                case 21:
                    field_Land.setSelected(true);
                    field_EU.setSelected(false);
                    field_Sprache.setSelected(true);
                    break;
            }
            field_Bestelldatum.setDate(Modulhelferlein.Date2SQLDate(resultB.getDate("BESTELLUNG_DATUM")));
            field_Bezahldatum.setDate(Modulhelferlein.Date2SQLDate(resultB.getDate("BESTELLUNG_BEZAHLT")));
            field_Privat.setSelected(resultB.getBoolean("BESTELLUNG_PRIVAT"));
            field_storniert.setSelected(resultB.getBoolean("BESTELLUNG_STORNIERT"));
            if (resultB.getInt("BESTELLUNG_KUNDE") > 0) {
                resultK = SQLAnfrageK.executeQuery("SELECT * FROM tbl_adresse WHERE ADRESSEN_ID = '" + Integer.toString(resultB.getInt("BESTELLUNG_KUNDE")) + "'");
                resultK.next();
                cbKunde.setSelectedItem(Integer.toString(resultB.getInt("BESTELLUNG_KUNDE")) + ", " + resultK.getString("ADRESSEN_NAME") + ", " + resultK.getString("ADRESSEN_VORNAME"));
                manuell.setSelected(false);
                KDB.setSelected(true);
            } else {
                cbKunde.setSelectedItem("0, ----------, -----------");
                manuell.setSelected(true);
                KDB.setSelected(false);
            }

            // Bestellungdetails lesen 
            resultBD = SQLAnfrageBD.executeQuery(
                    "SELECT * FROM TBL_BESTELLUNG_DETAIL WHERE BESTELLUNG_DETAIL_RECHNR = '" + resultB.getString("BESTELLUNG_RECHNR") + "'");
            countBMax = 0;
            while (resultBD.next()) {
                ++countBMax;
            }
            if (resultBD.first()) {
                Anzahl = resultBD.getInt("BESTELLUNG_DETAIL_ANZAHL");
            } else {
                Anzahl = 0;
            }

// Rechnungsbetrag ermitteln
            resultBIsEmpty = false;
            resultBD = SQLAnfrageBD.executeQuery(
                    "SELECT * FROM TBL_BESTELLUNG_DETAIL WHERE BESTELLUNG_DETAIL_RECHNR = '" + resultB.getString("BESTELLUNG_RECHNR") + "'"); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 
            Double Rechnungsbetrag = 0D;
            Double Betrag19 = 0D;  // Bruttobetrag 19%
            Double Betrag7 = 0D;   // Bruttobetrag  7%
            while (resultBD.next()) {
                if (resultBD.getBoolean("BESTELLUNG_DETAIL_SONST")) {
                    Betrag19 = Betrag19 + resultBD.getFloat("BESTELLUNG_DETAIL_SONST_PREIS") * 1D;
                } else {
                    resultBuch = SQLAnfrageBuch.executeQuery("SELECT * FROM TBL_BUCH WHERE BUCH_ID = '" + resultBD.getString("BESTELLUNG_DETAIL_BUCH") + "'");
                    resultBuch.next();

                    Double ZPreis = resultBuch.getFloat("BUCH_PREIS") * 1D;
                    ZPreis = ZPreis - ZPreis / 100 * resultBD.getFloat("BESTELLUNG_DETAIL_RABATT");

                    ZPreis = Modulhelferlein.round2dec(ZPreis);
                    Betrag7 = Betrag7 + resultBD.getInt("BESTELLUNG_DETAIL_ANZAHL") * ZPreis;
                }
            }
// Rechnungsbetrag festlegen
// DEU:       Geschäft = Privat = Netto + UStr
// EU:        Geschäft = Netto
//            Privat   = Netto + UStr
// Drittland: Geschäft = Privat = Netto
            Rechnungsbetrag = 0D;
            switch (resultB.getInt("BESTELLUNG_LAND")) {
                case 0:
                    Rechnungsbetrag = Betrag7 + Betrag19;
                    break;
                case 10:
                case 11:
                    if (resultB.getBoolean("BESTELLUNG_PRIVAT")) {
                        Rechnungsbetrag = Betrag7 + Betrag19;
                    } else {
                        Rechnungsbetrag = Betrag7 / 107 * 100 + Betrag19 / 119 * 100;
                    }
                    break;
                case 20:
                case 21:
                    Rechnungsbetrag = Betrag7 - Betrag7 / 107 * 100 + Betrag19 - Betrag19 / 119 * 100;
                    break;
            }
            Rechnungsbetrag = Rechnungsbetrag + resultB.getFloat("BESTELLUNG_VERSAND");
            field_Betrag.setText(Modulhelferlein.str2dec(Rechnungsbetrag));

            resultBD.first();
            field_B_CountMax.setText(Integer.toString(countBMax));
            if (countBMax == 0) {
                // Schalterzustände setzen
                countB = 0;
                field_B_Sonstiges.setSelected(resultBD.getBoolean("BESTELLUNG_DETAIL_SONST"));
                field_B_Text.setText(resultBD.getString("BESTELLUNG_DETAIL_SONST_TEXT"));
                field_B_Preis.setText(Float.toString(resultBD.getFloat("BESTELLUNG_DETAIL_SONST_PREIS")));
                field_B_Count.setText(Integer.toString(countB));
                field_B_Rabatt.setText("");
                field_B_Anzahl.setText("");
                resultBIsEmpty = true;
                cbBuch.setSelectedIndex(0);
                BAnfang.setEnabled(false);
                BZurueck.setEnabled(false);
                BVor.setEnabled(false);
                BEnde.setEnabled(false);
                BUpdate.setEnabled(false);
                BEinfuegen.setEnabled(true);
                BLoeschen.setEnabled(false);
            } else {
                resultBIsEmpty = false;
                countB = 1;
                field_B_Count.setText(Integer.toString(countB));
                field_B_Rabatt.setText(Float.toString(resultBD.getFloat("BESTELLUNG_DETAIL_RABATT")));
                field_B_Anzahl.setText(Integer.toString(resultBD.getInt("BESTELLUNG_DETAIL_ANZAHL")));
                resultBuch = SQLAnfrageBuch.executeQuery("SELECT * FROM TBL_BUCH WHERE BUCH_ID = '" + resultBD.getString("BESTELLUNG_DETAIL_BUCH") + "'");
                resultBuch.next();
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
                cbBuch.setSelectedItem(eintrag);
                // Schalterzustände setzen   
                BAnfang.setEnabled(true);
                BZurueck.setEnabled(true);
                BVor.setEnabled(true);
                BEnde.setEnabled(true);
                BUpdate.setEnabled(true);
                BEinfuegen.setEnabled(true);
                BLoeschen.setEnabled(true);
            }
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("Bestellung: zum nächsten", "SQL-Exception: ", exept.getMessage());
        }
    }//GEN-LAST:event_VorActionPerformed

    private void EndeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EndeActionPerformed
        // TODO add your handling code here:
        try {
            resultB.last();
            count = countMax;
            field_count.setText(Integer.toString(count));
            switch (resultB.getInt("BESTELLUNG_TYP")) {
                case 0:
                    rbBestellung.setSelected(true);
                    break;
                case 1:
                    rbRezension.setSelected(true);
                    break;
                case 2:
                    rbPflicht.setSelected(true);
                    break;
                case 3:
                    rbGeschenk.setSelected(true);
                    break;
                case 4:
                    rbBeleg.setSelected(true);
                    break;
                case 5:
                    rbRemittende.setSelected(true);
                    break;
            }
            field_EMail.setText(resultB.getString("BESTELLUNG_EMAIL"));
            field_DHL.setText(resultB.getString("BESTELLUNG_DHL"));
            field_Zahlungsziel.setText(resultB.getString("BESTELLUNG_ZAHLUNGSZIEL"));
            field_Zusatz_Text.setText(resultB.getString("BESTELLUNG_TEXT"));
            field_Zusatz_cb.setSelected(resultB.getBoolean("BESTELLUNG_TB"));
            field_Verrechnung.setSelected(resultB.getBoolean("BESTELLUNG_BEZAHLUNG"));
            field_Ueberweisung.setSelected(!resultB.getBoolean("BESTELLUNG_BEZAHLUNG"));
            field_Bestellzeichen.setText(resultB.getString("BESTELLUNG_BESTNR"));
            field_RechNr.setText(resultB.getString("BESTELLUNG_RECHNR"));
            field_Zeile1.setText(resultB.getString("BESTELLUNG_ZEILE_1"));
            field_Zeile2.setText(resultB.getString("BESTELLUNG_ZEILE_2"));
            field_Zeile3.setText(resultB.getString("BESTELLUNG_ZEILE_3"));
            field_Zeile4.setText(resultB.getString("BESTELLUNG_ZEILE_4"));
            field_Zeile5.setText(resultB.getString("BESTELLUNG_ZEILE_5"));
            field_Zeile6.setText(resultB.getString("BESTELLUNG_ZEILE_6"));
            field_UstrID.setText(resultB.getString("BESTELLUNG_USTR_ID"));
            field_Link.setText(resultB.getString("BESTELLUNG_LINK"));
            field_Versand.setText(Float.toString(resultB.getFloat("BESTELLUNG_VERSAND")));
            field_RechDat.setDate(Modulhelferlein.Date2SQLDate(resultB.getDate("BESTELLUNG_RECHDAT")));
            switch (resultB.getInt("BESTELLUNG_LAND")) {
                case 0:
                    field_Land.setSelected(false);
                    field_EU.setSelected(false);
                    field_Sprache.setSelected(false);
                    break;
                case 1:
                    field_Land.setSelected(false);
                    field_EU.setSelected(false);
                    field_Sprache.setSelected(true);
                    break;
                case 10:
                    field_Land.setSelected(true);
                    field_EU.setSelected(true);
                    field_Sprache.setSelected(false);
                    break;
                case 11:
                    field_Land.setSelected(true);
                    field_EU.setSelected(true);
                    field_Sprache.setSelected(true);
                    break;
                case 20:
                    field_Land.setSelected(true);
                    field_EU.setSelected(false);
                    field_Sprache.setSelected(false);
                    break;
                case 21:
                    field_Land.setSelected(true);
                    field_EU.setSelected(false);
                    field_Sprache.setSelected(true);
                    break;
            }
            field_Bestelldatum.setDate(Modulhelferlein.Date2SQLDate(resultB.getDate("BESTELLUNG_DATUM")));
            field_Bezahldatum.setDate(Modulhelferlein.Date2SQLDate(resultB.getDate("BESTELLUNG_BEZAHLT")));
            field_Privat.setSelected(resultB.getBoolean("BESTELLUNG_PRIVAT"));
            field_storniert.setSelected(resultB.getBoolean("BESTELLUNG_STORNIERT"));
            if (resultB.getInt("BESTELLUNG_KUNDE") > 0) {
                resultK = SQLAnfrageK.executeQuery("SELECT * FROM tbl_adresse WHERE ADRESSEN_ID = '" + Integer.toString(resultB.getInt("BESTELLUNG_KUNDE")) + "'");
                resultK.next();
                cbKunde.setSelectedItem(Integer.toString(resultB.getInt("BESTELLUNG_KUNDE")) + ", " + resultK.getString("ADRESSEN_NAME") + ", " + resultK.getString("ADRESSEN_VORNAME"));
                manuell.setSelected(false);
                KDB.setSelected(true);
            } else {
                cbKunde.setSelectedItem("0, ----------, -----------");
                manuell.setSelected(true);
                KDB.setSelected(false);
            }

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

            // Bestellungdetails lesen   
            resultBD = SQLAnfrageBD.executeQuery(
                    "SELECT * FROM TBL_BESTELLUNG_DETAIL WHERE BESTELLUNG_DETAIL_RECHNR = '" + resultB.getString("BESTELLUNG_RECHNR") + "'"); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 
            countBMax = 0;
            while (resultBD.next()) {
                ++countBMax;
            }
            if (resultBD.first()) {
                Anzahl = resultBD.getInt("BESTELLUNG_DETAIL_ANZAHL");
            } else {
                Anzahl = 0;
            }

// Rechnungsbetrag ermitteln
            resultBIsEmpty = false;
            resultBD = SQLAnfrageBD.executeQuery(
                    "SELECT * FROM TBL_BESTELLUNG_DETAIL WHERE BESTELLUNG_DETAIL_RECHNR = '" + resultB.getString("BESTELLUNG_RECHNR") + "'"); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 
            Double Rechnungsbetrag = 0D;
            Double Betrag19 = 0D;  // Bruttobetrag 19%
            Double Betrag7 = 0D;   // Bruttobetrag  7%
            while (resultBD.next()) {
                if (resultBD.getBoolean("BESTELLUNG_DETAIL_SONST")) {
                    Betrag19 = Betrag19 + resultBD.getFloat("BESTELLUNG_DETAIL_SONST_PREIS") * 1D;
                } else {
                    resultBuch = SQLAnfrageBuch.executeQuery("SELECT * FROM TBL_BUCH WHERE BUCH_ID = '" + resultBD.getString("BESTELLUNG_DETAIL_BUCH") + "'");
                    resultBuch.next();

                    Double ZPreis = resultBuch.getFloat("BUCH_PREIS") * 1D;
                    ZPreis = ZPreis - ZPreis / 100 * resultBD.getFloat("BESTELLUNG_DETAIL_RABATT");

                    ZPreis = Modulhelferlein.round2dec(ZPreis);
                    Betrag7 = Betrag7 + resultBD.getInt("BESTELLUNG_DETAIL_ANZAHL") * ZPreis;
                }
            }
// Rechnungsbetrag festlegen
// DEU:       Geschäft = Privat = Netto + UStr
// EU:        Geschäft = Netto
//            Privat   = Netto + UStr
// Drittland: Geschäft = Privat = Netto
            Rechnungsbetrag = 0D;
            switch (resultB.getInt("BESTELLUNG_LAND")) {
                case 0:
                    Rechnungsbetrag = Betrag7 + Betrag19;
                    break;
                case 10:
                case 11:
                    if (resultB.getBoolean("BESTELLUNG_PRIVAT")) {
                        Rechnungsbetrag = Betrag7 + Betrag19;
                    } else {
                        Rechnungsbetrag = Betrag7 / 107 * 100 + Betrag19 / 119 * 100;
                    }
                    break;
                case 20:
                case 21:
                    Rechnungsbetrag = Betrag7 - Betrag7 / 107 * 100 + Betrag19 - Betrag19 / 119 * 100;
                    break;
            }
            Rechnungsbetrag = Rechnungsbetrag + resultB.getFloat("BESTELLUNG_VERSAND");
            field_Betrag.setText(Modulhelferlein.str2dec(Rechnungsbetrag));

            resultBD.first();
            field_B_CountMax.setText(Integer.toString(countBMax));
            if (countBMax == 0) {
                // Schalterzustände setzen
                countB = 0;
                field_B_Count.setText(Integer.toString(countB));
                field_B_Rabatt.setText("");
                field_B_Anzahl.setText("");
                resultBIsEmpty = true;
                cbBuch.setSelectedIndex(0);
                BAnfang.setEnabled(false);
                BZurueck.setEnabled(false);
                BVor.setEnabled(false);
                BEnde.setEnabled(false);
                BUpdate.setEnabled(false);
                BEinfuegen.setEnabled(true);
                BLoeschen.setEnabled(false);
            } else {
                resultBIsEmpty = false;
                countB = 1;
                field_B_Sonstiges.setSelected(resultBD.getBoolean("BESTELLUNG_DETAIL_SONST"));
                field_B_Text.setText(resultBD.getString("BESTELLUNG_DETAIL_SONST_TEXT"));
                field_B_Preis.setText(Float.toString(resultBD.getFloat("BESTELLUNG_DETAIL_SONST_PREIS")));
                field_B_Count.setText(Integer.toString(countB));
                field_B_Rabatt.setText(Float.toString(resultBD.getFloat("BESTELLUNG_DETAIL_RABATT")));
                field_B_Anzahl.setText(Integer.toString(resultBD.getInt("BESTELLUNG_DETAIL_ANZAHL")));
                resultBuch = SQLAnfrageBuch.executeQuery("SELECT * FROM TBL_BUCH WHERE BUCH_ID = '" + resultBD.getString("BESTELLUNG_DETAIL_BUCH") + "'");
                resultBuch.next();
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
                cbBuch.setSelectedItem(eintrag);
                // Schalterzustände setzen   
                BAnfang.setEnabled(true);
                BZurueck.setEnabled(true);
                BVor.setEnabled(true);
                BEnde.setEnabled(true);
                BUpdate.setEnabled(true);
                BEinfuegen.setEnabled(true);
                BLoeschen.setEnabled(true);
            }
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("Bestellung: zum Ende", "SQL-Exception: ", exept.getMessage());
        }
    }//GEN-LAST:event_EndeActionPerformed

    private void DruckenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DruckenActionPerformed
        // TODO add your handling code here:
        BUpdateActionPerformed(evt);
        UpdateActionPerformed(evt);

        Integer iBestand = 0;
        Integer iAnzahl = 0;
        Statement iSQLAnfrageBu = null;
        Statement iSQLAnfrageBe = null;
        Statement iSQLAnfrageBD = null;
        ResultSet iresultBe = null;
        ResultSet iresultBD = null;
        ResultSet iresultBu = null;

        try {
            iSQLAnfrageBu = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            iSQLAnfrageBe = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            iSQLAnfrageBD = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            iresultBe = iSQLAnfrageBe.executeQuery("SELECT * FROM TBL_BESTELLUNG WHERE BESTELLUNG_RECHNR = '" + field_RechNr.getText() + "'");
            iresultBD = iSQLAnfrageBD.executeQuery("SELECT * FROM TBL_BESTELLUNG_DETAIL WHERE BESTELLUNG_DETAIL_RECHNR = '" + field_RechNr.getText() + "'");

            iresultBe.next();

            if (!iresultBe.getBoolean("BESTELLUNG_BESTAND")) {
                while (iresultBD.next()) {
                    iresultBu = iSQLAnfrageBu.executeQuery("SELECT * FROM TBL_BUCH WHERE BUCH_ID = '" + iresultBD.getString("BESTELLUNG_DETAIL_BUCH") + "'");
                    iresultBu.next();
                    iBestand = iresultBu.getInt("BUCH_BESTAND");
                    iAnzahl = iresultBD.getInt("BESTELLUNG_DETAIL_ANZAHL");
                    iBestand = iBestand + iAnzahl - iresultBD.getInt("BESTELLUNG_DETAIL_ANZAHL");
                    iresultBu.updateInt("BUCH_BESTAND", iBestand);
                    iresultBu.updateRow();

                    iresultBe.updateBoolean("BESTELLUNG_BESTAND", true);
                    iresultBe.updateRow();
                }
                iresultBe.close();
                iresultBu.close();
                iresultBD.close();
                iSQLAnfrageBu.close();
                iSQLAnfrageBe.close();
                iSQLAnfrageBD.close();
            }
        } catch (SQLException ex) {
            Modulhelferlein.Fehlermeldung("Bestellung drucken: Bestand anpassen", "SQL-Exception", ex.getMessage());
        }

        String[] args = {"Rechnung", field_RechNr.getText(), "0"};
        args[0] = "Rechnung";
        args[1] = field_RechNr.getText();
        if (rbBestellung.isSelected()) {
            args[2] = "0";
        } else if (rbRezension.isSelected()) {
            args[2] = "1";
        } else if (rbPflicht.isSelected()) {
            args[2] = "2";
        } else if (rbBeleg.isSelected()) {
            args[2] = "3";
        } else {
            args[2] = "4";
        }
        _DlgAusgabeFormat.main(args);
// Modulhelferlein.Infomeldung("Aufruf Brief Rechnung für RechNr " + field_RechNr.getText());

        dispose();
    }//GEN-LAST:event_DruckenActionPerformed

    private void MahnungActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MahnungActionPerformed
        // TODO add your handling code here:
        BUpdateActionPerformed(evt);
        UpdateActionPerformed(evt);
        String[] args = {field_RechNr.getText()};
        _DlgMahnungNr.main(args);
    }//GEN-LAST:event_MahnungActionPerformed

    private void EMailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EMailActionPerformed
        // TODO add your handling code here:
        BUpdateActionPerformed(evt);
        UpdateActionPerformed(evt);

        FileFilter filter = new FileNameExtensionFilter("Rechnungsdatei", "PDF", "DOC", "XML");
        JFileChooser chooser = new JFileChooser(Modulhelferlein.pathRechnungen);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setMultiSelectionEnabled(false);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.addChoosableFileFilter(filter);

        String Filename = "";
        String Kunde = "";

        int Ergebnis = chooser.showDialog(null, "Rechnung wählen");

        if (Ergebnis == JFileChooser.APPROVE_OPTION) {
            Filename = chooser.getSelectedFile().getPath();
            if (manuell.isSelected()) {
                Kunde = "-1";
            } else {
                String KdeID[] = cbKunde.getItemAt(cbKunde.getSelectedIndex()).split(",");
                Kunde = KdeID[0];
            }

            String[] args = {Filename, 
                                Kunde, 
                                field_EMail.getText(), 
                                field_DHL.getText(), 
                                Modulhelferlein.printDateFormat("dd.MM.yyyy", field_Bestelldatum.getDate())};
            ModulEMail.main(args);
        }
    }//GEN-LAST:event_MahnungActionPerformed

    private void BAnfangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BAnfangActionPerformed
        // TODO add your handling code here:
        try {
            if (resultBD.first()) {
                Anzahl = resultBD.getInt("BESTELLUNG_DETAIL_ANZAHL");
            } else {
                Anzahl = 0;
            }
            resultBD.first();
            countB = 1;
            field_B_Count.setText(Integer.toString(countB));

            // Felder füllen
            field_B_Sonstiges.setSelected(resultBD.getBoolean("BESTELLUNG_DETAIL_SONST"));
            field_B_Text.setText(resultBD.getString("BESTELLUNG_DETAIL_SONST_TEXT"));
            field_B_Preis.setText(Float.toString(resultBD.getFloat("BESTELLUNG_DETAIL_SONST_PREIS")));
            field_B_Rabatt.setText(Float.toString(resultBD.getFloat("BESTELLUNG_DETAIL_RABATT")));
            field_B_Anzahl.setText(Integer.toString(resultBD.getInt("BESTELLUNG_DETAIL_ANZAHL")));
            resultBuch = SQLAnfrageBuch.executeQuery("SELECT * FROM TBL_BUCH WHERE BUCH_ID = '" + resultBD.getString("BESTELLUNG_DETAIL_BUCH") + "'");
            resultBuch.next();
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
            cbBuch.setSelectedItem(eintrag);

            // Schalterzustand anpassen
            BAnfang.setEnabled(false);
            BZurueck.setEnabled(false);
            BVor.setEnabled(true);
            BEnde.setEnabled(true);
            BUpdate.setEnabled(true);
            BEinfuegen.setEnabled(true);
            BLoeschen.setEnabled(true);

        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("Bestelldetails: zum Anfang", "SQL-Exception: ", exept.getMessage());
        }
    }//GEN-LAST:event_BAnfangActionPerformed

    private void BZurueckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BZurueckActionPerformed
        // TODO add your handling code here:
        try {
//            if (resultBD.previous()) {
            if (countB > 1) {
                resultBD.previous();
                Anzahl = resultBD.getInt("BESTELLUNG_DETAIL_ANZAHL");
                countB = countB - 1;
                field_B_Count.setText(Integer.toString(countB));
                // Schalterzustand anpassen
                if (resultBD.getRow() > 1) {
                    BAnfang.setEnabled(true);
                    BZurueck.setEnabled(true);
                } else {
                    BAnfang.setEnabled(false);
                    BZurueck.setEnabled(false);
                }
                BVor.setEnabled(true);
                BEnde.setEnabled(true);
                BUpdate.setEnabled(true);
                BEinfuegen.setEnabled(true);
                BLoeschen.setEnabled(true);

            } else {
//                resultBD.next();
                BAnfang.setEnabled(false);
                BZurueck.setEnabled(false);
                BVor.setEnabled(true);
                BEnde.setEnabled(true);
                BUpdate.setEnabled(true);
                BEinfuegen.setEnabled(true);
                BLoeschen.setEnabled(true);
            }
            // Felder füllen
            field_B_Sonstiges.setSelected(resultBD.getBoolean("BESTELLUNG_DETAIL_SONST"));
            field_B_Text.setText(resultBD.getString("BESTELLUNG_DETAIL_SONST_TEXT"));
            field_B_Preis.setText(Float.toString(resultBD.getFloat("BESTELLUNG_DETAIL_SONST_PREIS")));
            field_B_Rabatt.setText(Float.toString(resultBD.getFloat("BESTELLUNG_DETAIL_RABATT")));
            field_B_Anzahl.setText(Integer.toString(resultBD.getInt("BESTELLUNG_DETAIL_ANZAHL")));
            resultBuch = SQLAnfrageBuch.executeQuery("SELECT * FROM TBL_BUCH WHERE BUCH_ID = '" + resultBD.getString("BESTELLUNG_DETAIL_BUCH") + "'");
            resultBuch.next();
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
            cbBuch.setSelectedItem(eintrag);
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("Bestelldetails: zum vorherigen", "SQL-Exception: ", exept.getMessage());
        }
    }//GEN-LAST:event_BZurueckActionPerformed

    private void BVorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BVorActionPerformed
        // TODO add your handling code here:
        try {
            if (resultBD.next()) {
                Anzahl = resultBD.getInt("BESTELLUNG_DETAIL_ANZAHL");
                countB = countB + 1;
                field_B_Count.setText(Integer.toString(countB));

                // Schalterzustand anpassen
                if (countB < countBMax) {
                    BEnde.setEnabled(true);
                    BVor.setEnabled(true);
                } else {
                    BEnde.setEnabled(false);
                    BVor.setEnabled(false);
                }
                BAnfang.setEnabled(true);
                BZurueck.setEnabled(true);
                BUpdate.setEnabled(true);
                BEinfuegen.setEnabled(true);
                BLoeschen.setEnabled(true);

            } else {
                resultBD.previous();
            }
            // Felder füllen
            field_B_Sonstiges.setSelected(resultBD.getBoolean("BESTELLUNG_DETAIL_SONST"));
            field_B_Text.setText(resultBD.getString("BESTELLUNG_DETAIL_SONST_TEXT"));
            field_B_Preis.setText(Float.toString(resultBD.getFloat("BESTELLUNG_DETAIL_SONST_PREIS")));
            field_B_Rabatt.setText(Float.toString(resultBD.getFloat("BESTELLUNG_DETAIL_RABATT")));
            field_B_Anzahl.setText(Integer.toString(resultBD.getInt("BESTELLUNG_DETAIL_ANZAHL")));
            resultBuch = SQLAnfrageBuch.executeQuery("SELECT * FROM TBL_BUCH WHERE BUCH_ID = '" + resultBD.getString("BESTELLUNG_DETAIL_BUCH") + "'");
            resultBuch.next();
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
            cbBuch.setSelectedItem(eintrag);
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("Bestelldetails: zum nächsten", "SQL-Exception: ", exept.getMessage());
        }
    }//GEN-LAST:event_BVorActionPerformed

    private void BEndeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BEndeActionPerformed
        // TODO add your handling code here:
        try {
            resultBD.last();
            Anzahl = resultBD.getInt("BESTELLUNG_DETAIL_ANZAHL");
            countB = countBMax;
            field_B_Count.setText(Integer.toString(countB));
            // Felder füllen
            field_B_Sonstiges.setSelected(resultBD.getBoolean("BESTELLUNG_DETAIL_SONST"));
            field_B_Text.setText(resultBD.getString("BESTELLUNG_DETAIL_SONST_TEXT"));
            field_B_Preis.setText(Float.toString(resultBD.getFloat("BESTELLUNG_DETAIL_SONST_PREIS")));
            field_B_Rabatt.setText(Float.toString(resultBD.getFloat("BESTELLUNG_DETAIL_RABATT")));
            field_B_Anzahl.setText(Integer.toString(resultBD.getInt("BESTELLUNG_DETAIL_ANZAHL")));
            resultBuch = SQLAnfrageBuch.executeQuery("SELECT * FROM TBL_BUCH WHERE BUCH_ID = '" + resultBD.getString("BESTELLUNG_DETAIL_BUCH") + "'");
            resultBuch.next();
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
            cbBuch.setSelectedItem(eintrag);

            // Schalterzustand anpassen
            BAnfang.setEnabled(true);
            BZurueck.setEnabled(true);
            BVor.setEnabled(false);
            BEnde.setEnabled(false);
            BUpdate.setEnabled(true);
            BEinfuegen.setEnabled(true);
            BLoeschen.setEnabled(true);

        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("Bestelldetails: zum Ende", "SQL-Exception: ", exept.getMessage());
        }
    }//GEN-LAST:event_BEndeActionPerformed

    private void BUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BUpdateActionPerformed
        // TODO add your handling code here:
        try {
            if (Modulhelferlein.checkNumberFormatFloat(field_B_Rabatt.getText()) < 0) {
                Modulhelferlein.Infomeldung("fehlerhafte Eingabe des Rabattes - es ist keine korrekte Zahl");
            } else {
                if (Modulhelferlein.checkNumberFormatInt(field_B_Anzahl.getText()) < 0) {
                    Modulhelferlein.Infomeldung("fehlerhafte Eingabe der Anzahl - es ist keine korrekte Ganzzahl");
                } else {
                    if (Modulhelferlein.checkNumberFormatFloat(field_B_Preis.getText()) < 0) {
                        Modulhelferlein.Infomeldung("fehlerhafte Eingabe des Preises - es ist keine korrekte Zahl");
                    } else {
                        resultBD.updateInt("BESTELLUNG_DETAIL_ANZAHL", Integer.parseInt(field_B_Anzahl.getText()));
                        resultBD.updateFloat("BESTELLUNG_DETAIL_RABATT", Float.parseFloat(field_B_Rabatt.getText()));
                        String buch[] = cbBuch.getItemAt(cbBuch.getSelectedIndex()).split(",");
                        resultBD.updateInt("BESTELLUNG_DETAIL_BUCH", Integer.parseInt(buch[0]));
                        resultBD.updateString("BESTELLUNG_DETAIL_RECHNR", field_RechNr.getText());
                        resultBD.updateDate("BESTELLUNG_DETAIL_DATUM", Modulhelferlein.Date2SQLDate(Modulhelferlein.CurDate));
                        resultBD.updateBoolean("BESTELLUNG_DETAIL_SONST", field_B_Sonstiges.isSelected());
                        resultBD.updateString("BESTELLUNG_DETAIL_SONST_TEXT", field_B_Text.getText());
                        resultBD.updateFloat("BESTELLUNG_DETAIL_SONST_PREIS", Float.parseFloat(field_B_Preis.getText()));
                        if (field_B_Sonstiges.isSelected()) {
                            resultBD.updateInt("BESTELLUNG_DETAIL_ANZAHL", 1);
                            resultBD.updateFloat("BESTELLUNG_DETAIL_RABATT", 0F);
                        };
                        resultBD.updateRow();
                    }
                }
            }
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("Bestelldetails: Update: ", "SQL-Exception: ", exept.getMessage());
        }
    }//GEN-LAST:event_BUpdateActionPerformed

    private void BEinfuegenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BEinfuegenActionPerformed
        // TODO add your handling code here:
        int ID;
        ResultSet rb;
        Statement sb;

        Bestand = 1;
        ID = maxBID + 1;
        maxBID = maxBID + 1;

        try {
            //sb = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            //rb = sb.executeQuery("SELECT * FROM TBL_BESTELLUNG_DETAIL ORDER BY_BESTELLUNG_DETAIL_ID DESC");
            //if (rb.first()) {
            //    ID = rb.getInt("BESTELLUNG_DETAIL_ID") + 1;
            //} else {
            //    ID = 1;
            //}

            resultBD.moveToInsertRow();
            resultBD.updateInt("BESTELLUNG_DETAIL_ID", ID);
            resultBD.updateFloat("BESTELLUNG_DETAIL_RABATT", 0F);
            resultBD.updateInt("BESTELLUNG_DETAIL_ANZAHL", 0);
            resultBD.updateInt("BESTELLUNG_DETAIL_BUCH", 0);
            resultBD.updateDate("BESTELLUNG_DETAIL_DATUM", Modulhelferlein.Date2SQLDate(CurDate));
            resultBD.updateString("BESTELLUNG_DETAIL_RECHNR", field_RechNr.getText());
            resultBD.updateBoolean("BESTELLUNG_DETAIL_SONST", false);
            resultBD.updateString("BESTELLUNG_DETAIL_SONST_TEXT", "");
            resultBD.updateFloat("BESTELLUNG_DETAIL_SONST_PREIS", 0F);
            resultBD.insertRow();

            // Schalterzustand anpassen
            BAnfang.setEnabled(true);
            BZurueck.setEnabled(true);
            BVor.setEnabled(false);
            BEnde.setEnabled(false);
            BUpdate.setEnabled(true);
            BEinfuegen.setEnabled(true);
            BLoeschen.setEnabled(true);

            resultBIsEmpty = false;
            countBMax = countBMax + 1;
            field_B_CountMax.setText(Integer.toString(countBMax));
            field_B_CountMax.setEditable(true);
            countB = countBMax;
            field_B_Count.setText(Integer.toString(countB));
            field_B_Count.setEditable(true);

            resultBD.last();

            // Felder füllen
            field_B_Rabatt.setText(Float.toString(resultBD.getFloat("BESTELLUNG_DETAIL_RABATT")));
            field_B_Rabatt.setEditable(true);
            field_B_Anzahl.setText(Integer.toString(resultBD.getInt("BESTELLUNG_DETAIL_ANZAHL")));
            field_B_Anzahl.setEditable(true);

        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("Bestellung", "Bestelldetails: Einfügen: SQL-Exception: ", exept.getMessage());
        }
    }//GEN-LAST:event_BEinfuegenActionPerformed

    private void BLoeschenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BLoeschenActionPerformed
        // TODO add your handling code here:
        if (JOptionPane.showConfirmDialog(null, "Soll der Datensatz wirklich gelöscht werden?") == JOptionPane.YES_OPTION) {
            try {
                resultBD.deleteRow();
                countBMax = countBMax - 1;
                field_B_CountMax.setText(Integer.toString(countBMax));
                resultBD.first();
                countB = 1;
                field_B_Count.setText(Integer.toString(countB));
                if (resultBD.getRow() > 0) {
                    // Schalterzustand anpassen
                    BAnfang.setEnabled(true);
                    BZurueck.setEnabled(true);
                    BVor.setEnabled(true);
                    BEnde.setEnabled(true);
                    BUpdate.setEnabled(true);
                    BEinfuegen.setEnabled(true);
                    BLoeschen.setEnabled(true);

                    // Felder füllen
                    field_B_Sonstiges.setSelected(resultBD.getBoolean("BESTELLUNG_DETAIL_SONST"));
                    field_B_Text.setText(resultBD.getString("BESTELLUNG_DETAIL_SONST_TEXT"));
                    field_B_Preis.setText(Float.toString(resultBD.getFloat("BESTELLUNG_DETAIL_SONST_PREIS")));
                    field_B_Rabatt.setText(Float.toString(resultBD.getFloat("BESTELLUNG_DETAIL_RABATT")));
                    field_B_Anzahl.setText(Integer.toString(resultBD.getInt("BESTELLUNG_DETAIL_ANZAHL")));

                } else {
                    resultBIsEmpty = true;

                    // Schalterzustand anpassen
                    BAnfang.setEnabled(false);
                    BZurueck.setEnabled(false);
                    BVor.setEnabled(false);
                    BEnde.setEnabled(false);
                    BUpdate.setEnabled(false);
                    BEinfuegen.setEnabled(true);
                    BLoeschen.setEnabled(false);

                    // Felder füllen
                    field_B_Rabatt.setText("0");
                    field_B_Anzahl.setText("0");

                    Bestand = resultBuch.getInt("BUCH_BESTAND");
                    // Jetzt in Buchprojekte den Bestand des Buches um Anzahl erhöhen
                    Bestand = Bestand + resultBD.getInt("BESTELLUNG_DETAIL_ANZAHL");
                    resultBuch.updateInt("BUCH_BESTAND", Bestand);
                    resultBuch.updateRow();
                    Bestand = 0;
                }
            } catch (SQLException exept) {
                Modulhelferlein.Fehlermeldung("Bestelldetails: Löschen: SQL-Exception: " + exept.getMessage());
            }
        }
    }//GEN-LAST:event_BLoeschenActionPerformed

    private void BDateiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BDateiActionPerformed
        // TODO add your handling code here:
        int Ergebnis = chooser.showDialog(null, "Datei mit der Rechnung wählen");
        if (Ergebnis == JFileChooser.APPROVE_OPTION) {
            String sFilePathAndName = "";
            try {
                field_Link.setText(chooser.getSelectedFile().getCanonicalPath());
            } catch (IOException e) {
                Modulhelferlein.Fehlermeldung("Exception: " + e.getMessage());
            }
        } // if
    }//GEN-LAST:event_BDateiActionPerformed

    private void field_VersandActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_field_VersandActionPerformed
        // TODO add your handling code here:
        if (Modulhelferlein.checkNumberFormatFloat(field_Versand.getText()) < 0) {
            Modulhelferlein.Infomeldung("fehlerhafte Eingabe der Versandkosten", "die ist keine korrekte Zahl");
        }
    }//GEN-LAST:event_field_VersandActionPerformed

    private void field_B_AnzahlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_field_B_AnzahlActionPerformed
        // TODO add your handling code here:
        if (Modulhelferlein.checkNumberFormatInt(field_B_Anzahl.getText()) < 0) {
            Modulhelferlein.Infomeldung("fehlerhafte Eingabe", "die ist keine korrekte Ganzzahl");
        }
    }//GEN-LAST:event_field_B_AnzahlActionPerformed

    private void field_B_RabattActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_field_B_RabattActionPerformed
        // TODO add your handling code here:
        if (Modulhelferlein.checkNumberFormatInt(field_B_Anzahl.getText()) < 0) {
            Modulhelferlein.Infomeldung("fehlerhafte Eingabe", "die ist keine korrekte Ganzzahl");
        }
    }//GEN-LAST:event_field_B_RabattActionPerformed

    private void rbBestellungActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbBestellungActionPerformed
        // TODO add your handling code here:
        if (rbRezension.isSelected() || rbPflicht.isSelected() || rbGeschenk.isSelected() || rbBeleg.isSelected() || rbRemittende.isSelected()) {
            field_B_Rabatt.setText("100");
            field_Bezahldatum.setDate(CurDate);
        }
    }//GEN-LAST:event_rbBestellungActionPerformed

    private void rbRezensionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbRezensionActionPerformed
        // TODO add your handling code here:
        if (rbRezension.isSelected() || rbPflicht.isSelected() || rbGeschenk.isSelected() || rbBeleg.isSelected() || rbRemittende.isSelected()) {
            field_B_Rabatt.setText("100");
            field_Bezahldatum.setDate(CurDate);
        }
    }//GEN-LAST:event_rbRezensionActionPerformed

    private void rbPflichtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbPflichtActionPerformed
        // TODO add your handling code here:
        if (rbRezension.isSelected() || rbPflicht.isSelected() || rbGeschenk.isSelected() || rbBeleg.isSelected() || rbRemittende.isSelected()) {
            field_B_Rabatt.setText("100");
            field_Bezahldatum.setDate(CurDate);
        }
    }//GEN-LAST:event_rbPflichtActionPerformed

    private void rbGeschenkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbGeschenkActionPerformed
        // TODO add your handling code here:
        if (rbRezension.isSelected() || rbPflicht.isSelected() || rbGeschenk.isSelected() || rbBeleg.isSelected() || rbRemittende.isSelected()) {
            field_B_Rabatt.setText("100");
            field_Bezahldatum.setDate(CurDate);
        }
    }//GEN-LAST:event_rbGeschenkActionPerformed

    private void rbBelegActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbBelegActionPerformed
        // TODO add your handling code here:
        if (rbRezension.isSelected() || rbPflicht.isSelected() || rbGeschenk.isSelected() || rbBeleg.isSelected() || rbRemittende.isSelected()) {
            field_B_Rabatt.setText("100");
            field_Bezahldatum.setDate(CurDate);
        }
    }//GEN-LAST:event_rbBelegActionPerformed

    private void field_B_PreisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_field_B_PreisActionPerformed
        // TODO add your handling code here:
        if (Modulhelferlein.checkNumberFormatFloat(field_B_Preis.getText()) < 0) {
            Modulhelferlein.Infomeldung("fehlerhafte Eingabe des Preises", "die ist keine korrekte Zahl");
        }
    }//GEN-LAST:event_field_B_PreisActionPerformed

    private void jList1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList1MouseClicked
        // TODO add your handling code here:
        try {
            // get selected item
            List<String> ListeAdressen = jList1.getSelectedValuesList();
            // get Adresssen_ID
            String[] Listeneintrag = ListeAdressen.get(0).split(", ");
//helferlein.Infomeldung(ListeAdressen.get(0)+" "+Integer.toString(Listeneintrag.length)+" "+Listeneintrag[Listeneintrag.length-1]);            
            // gehe zu dieser ID
            if (resultB.first()) {
                count = 1;
                boolean ende = false;
                while (!ende) {
//helferlein.Infomeldung(result.getString("AUSGABEN_RECHNNR")+" - "+Listeneintrag[0]);                    
                    ende = (resultB.getString("BESTELLUNG_RECHNR").equals(Listeneintrag[0]));
                    resultB.next();
                    count = count + 1;
                }
                resultB.previous();
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

                switch (resultB.getInt("BESTELLUNG_TYP")) {
                    case 0:
                        rbBestellung.setSelected(true);
                        break;
                    case 1:
                        rbRezension.setSelected(true);
                        break;
                    case 2:
                        rbPflicht.setSelected(true);
                        break;
                    case 3:
                        rbGeschenk.setSelected(true);
                        break;
                    case 4:
                        rbBeleg.setSelected(true);
                        break;
                }
                field_Zusatz_Text.setText(resultB.getString("BESTELLUNG_TEXT"));
                field_Zusatz_cb.setSelected(resultB.getBoolean("BESTELLUNG_TB"));
                field_Verrechnung.setSelected(resultB.getBoolean("BESTELLUNG_BEZAHLUNG"));
                field_Ueberweisung.setSelected(!resultB.getBoolean("BESTELLUNG_BEZAHLUNG"));
                field_Bestellzeichen.setText(resultB.getString("BESTELLUNG_BESTNR"));
                field_RechNr.setText(resultB.getString("BESTELLUNG_RECHNR"));
                field_Zeile1.setText(resultB.getString("BESTELLUNG_ZEILE_1"));
                field_Zeile2.setText(resultB.getString("BESTELLUNG_ZEILE_2"));
                field_Zeile3.setText(resultB.getString("BESTELLUNG_ZEILE_3"));
                field_Zeile4.setText(resultB.getString("BESTELLUNG_ZEILE_4"));
                field_Zeile5.setText(resultB.getString("BESTELLUNG_ZEILE_5"));
                field_Zeile6.setText(resultB.getString("BESTELLUNG_ZEILE_6"));
                field_Link.setText(resultB.getString("BESTELLUNG_LINK"));
                field_UstrID.setText(resultB.getString("BESTELLUNG_USTR_ID"));
                field_Versand.setText(Float.toString(resultB.getFloat("BESTELLUNG_VERSAND")));
                field_RechDat.setDate(Modulhelferlein.Date2SQLDate(resultB.getDate("BESTELLUNG_RECHDAT")));
                switch (resultB.getInt("BESTELLUNG_LAND")) {
                    case 0:
                        field_Land.setSelected(false);
                        field_EU.setSelected(false);
                        field_Sprache.setSelected(false);
                        break;
                    case 1:
                        field_Land.setSelected(false);
                        field_EU.setSelected(false);
                        field_Sprache.setSelected(true);
                        break;
                    case 10:
                        field_Land.setSelected(true);
                        field_EU.setSelected(true);
                        field_Sprache.setSelected(false);
                        break;
                    case 11:
                        field_Land.setSelected(true);
                        field_EU.setSelected(true);
                        field_Sprache.setSelected(true);
                        break;
                    case 20:
                        field_Land.setSelected(true);
                        field_EU.setSelected(false);
                        field_Sprache.setSelected(false);
                        break;
                    case 21:
                        field_Land.setSelected(true);
                        field_EU.setSelected(false);
                        field_Sprache.setSelected(true);
                        break;
                }
                field_Bestelldatum.setDate(Modulhelferlein.Date2SQLDate(resultB.getDate("BESTELLUNG_DATUM")));
                field_Bezahldatum.setDate(Modulhelferlein.Date2SQLDate(resultB.getDate("BESTELLUNG_BEZAHLT")));
                field_Privat.setSelected(resultB.getBoolean("BESTELLUNG_PRIVAT"));
                field_storniert.setSelected(resultB.getBoolean("BESTELLUNG_STORNIERT"));
                if (resultB.getInt("BESTELLUNG_KUNDE") > 0) {
                    resultK = SQLAnfrageK.executeQuery("SELECT * FROM tbl_adresse WHERE ADRESSEN_ID = '" + Integer.toString(resultB.getInt("BESTELLUNG_KUNDE")) + "'");
                    resultK.next();
                    cbKunde.setSelectedItem(Integer.toString(resultB.getInt("BESTELLUNG_KUNDE")) + ", " + resultK.getString("ADRESSEN_NAME") + ", " + resultK.getString("ADRESSEN_VORNAME"));
                    manuell.setSelected(false);
                    KDB.setSelected(true);
                } else {
                    cbKunde.setSelectedItem("0, ----------, -----------");
                    manuell.setSelected(true);
                    KDB.setSelected(false);
                }

                // Bestellungdetails lesen   
                resultBD = SQLAnfrageBD.executeQuery(
                        "SELECT * FROM TBL_BESTELLUNG_DETAIL WHERE BESTELLUNG_DETAIL_RECHNR = '" + resultB.getString("BESTELLUNG_RECHNR") + "'"); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 
                countBMax = 0;
                while (resultBD.next()) {
                    ++countBMax;
                }
                if (resultBD.first()) {
                    Anzahl = resultBD.getInt("BESTELLUNG_DETAIL_ANZAHL");
                } else {
                    Anzahl = 0;
                }

// Rechnungsbetrag ermitteln
                resultBIsEmpty = false;
                resultBD = SQLAnfrageBD.executeQuery(
                        "SELECT * FROM TBL_BESTELLUNG_DETAIL WHERE BESTELLUNG_DETAIL_RECHNR = '" + resultB.getString("BESTELLUNG_RECHNR") + "'"); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 
                Double Rechnungsbetrag = 0D;
                Double Betrag19 = 0D;  // Bruttobetrag 19%
                Double Betrag7 = 0D;   // Bruttobetrag  7%
                while (resultBD.next()) {
                    if (resultBD.getBoolean("BESTELLUNG_DETAIL_SONST")) {
                        Betrag19 = Betrag19 + resultBD.getFloat("BESTELLUNG_DETAIL_SONST_PREIS") * 1D;
                    } else {
                        resultBuch = SQLAnfrageBuch.executeQuery("SELECT * FROM TBL_BUCH WHERE BUCH_ID = '" + resultBD.getString("BESTELLUNG_DETAIL_BUCH") + "'");
                        resultBuch.next();

                        Double ZPreis = resultBuch.getFloat("BUCH_PREIS") * 1D;
                        ZPreis = ZPreis - ZPreis / 100 * resultBD.getFloat("BESTELLUNG_DETAIL_RABATT");

                        ZPreis = Modulhelferlein.round2dec(ZPreis);
                        Betrag7 = Betrag7 + resultBD.getInt("BESTELLUNG_DETAIL_ANZAHL") * ZPreis;
                    }
                }
// Rechnungsbetrag festlegen
// DEU:       Geschäft = Privat = Netto + UStr
// EU:        Geschäft = Netto
//            Privat   = Netto + UStr
// Drittland: Geschäft = Privat = Netto
                Rechnungsbetrag = 0D;
                switch (resultB.getInt("BESTELLUNG_LAND")) {
                    case 0:
                        Rechnungsbetrag = Betrag7 + Betrag19;
                        break;
                    case 10:
                    case 11:
                        if (resultB.getBoolean("BESTELLUNG_PRIVAT")) {
                            Rechnungsbetrag = Betrag7 + Betrag19;
                        } else {
                            Rechnungsbetrag = Betrag7 / 107 * 100 + Betrag19 / 119 * 100;
                        }
                        break;
                    case 20:
                    case 21:
                        Rechnungsbetrag = Betrag7 - Betrag7 / 107 * 100 + Betrag19 - Betrag19 / 119 * 100;
                        break;
                }
                Rechnungsbetrag = Rechnungsbetrag + resultB.getFloat("BESTELLUNG_VERSAND");
                field_Betrag.setText(Modulhelferlein.str2dec(Rechnungsbetrag));

                resultBD.first();
                field_B_CountMax.setText(Integer.toString(countBMax));
                if (countBMax == 0) {
                    // Schalterzustände setzen
                    countB = 0;
                    field_B_Count.setText(Integer.toString(countB));
                    field_B_Rabatt.setText("");
                    field_B_Anzahl.setText("");
                    resultBIsEmpty = true;
                    cbBuch.setSelectedIndex(0);
                    BAnfang.setEnabled(false);
                    BZurueck.setEnabled(false);
                    BVor.setEnabled(false);
                    BEnde.setEnabled(false);
                    BUpdate.setEnabled(false);
                    BEinfuegen.setEnabled(true);
                    BLoeschen.setEnabled(false);
                } else {
                    resultBIsEmpty = false;
                    countB = 1;
                    field_B_Sonstiges.setSelected(resultBD.getBoolean("BESTELLUNG_DETAIL_SONST"));
                    field_B_Text.setText(resultBD.getString("BESTELLUNG_DETAIL_SONST_TEXT"));
                    field_B_Preis.setText(Float.toString(resultBD.getFloat("BESTELLUNG_DETAIL_SONST_PREIS")));
                    field_B_Count.setText(Integer.toString(countB));
                    field_B_Rabatt.setText(Float.toString(resultBD.getFloat("BESTELLUNG_DETAIL_RABATT")));
                    field_B_Anzahl.setText(Integer.toString(resultBD.getInt("BESTELLUNG_DETAIL_ANZAHL")));
                    resultBuch = SQLAnfrageBuch.executeQuery("SELECT * FROM TBL_BUCH WHERE BUCH_ID = '" + resultBD.getString("BESTELLUNG_DETAIL_BUCH") + "'");
                    resultBuch.next();
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
                    cbBuch.setSelectedItem(eintrag);
                    // Schalterzustände setzen   
                    BAnfang.setEnabled(true);
                    BZurueck.setEnabled(true);
                    BVor.setEnabled(true);
                    BEnde.setEnabled(true);
                    BUpdate.setEnabled(true);
                    BEinfuegen.setEnabled(true);
                    BLoeschen.setEnabled(true);
                }

            }
        } catch (SQLException ex) {
            Modulhelferlein.Fehlermeldung("Bestellungen verwalten", "direkt zur Bestellung gehen: SQL-Exception", ex.getMessage());
        }
    }//GEN-LAST:event_jList1MouseClicked

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
            field_Zeile6.setText(Land);
        }
    }//GEN-LAST:event_jButtonLandActionPerformed

    /**
     * Diese Methode initialisiert die Liste mit dem Rechnungen Der Aufruf
     * erfolgt beim Start des Dialoges sowie beim Update von Rechnungen
     */
    private void initListeRechnungenbestand() {
        try {
            listModel.clear();
            String eintragRechnung = "";

            // Auswahlliste f?r Autoren erstellen
            result_Liste = SQLAnfrage_Liste.executeQuery("SELECT * FROM TBL_BESTELLUNG ORDER BY BESTELLUNG_RECHNR");
            while (result_Liste.next()) {
                eintragRechnung = result_Liste.getString("BESTELLUNG_RECHNR") + ", "
                        + result_Liste.getString("BESTELLUNG_DATUM");
                listModel.addElement(eintragRechnung);
            } // while
        } catch (SQLException ex) {
            Modulhelferlein.Fehlermeldung("Bestellungen verwalten", "Bestellungen auflisten: SQL-Exception", ex.getMessage());
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
            VerwaltenDatenbankBestellung dialog = new VerwaltenDatenbankBestellung(new javax.swing.JFrame(), true);
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
    private JTextField field_count;
    private JLabel jLabel3;
    private JTextField field_countMax;
    private JPanel hSpacer1;
    private JLabel jLabel20;
    private JPanel hSpacer2;
    private JTextField field_RechNr;
    private JCheckBox field_Privat;
    private JLabel jLabel6;
    private JLabel jLabel14;
    private JLabel jLabel26;
    private JPanel jPanel2;
    private JScrollPane jScrollPane1;
    private JList<String> jList1;
    private JLabel jLabel5;
    private JLabel jLabel29;
    private JRadioButton KDB;
    private JTextField field_Link;
    private JButton BDatei;
    private JDateChooser field_RechDat;
    private JComboBox<String> cbKunde;
    private JLabel jLabel16;
    private JLabel jLabel17;
    private JRadioButton manuell;
    private JDateChooser field_Bestelldatum;
    private JTextField field_Bestellzeichen;
    private JLabel jLabel28;
    private JTextField field_Betrag;
    private JLabel jLabel25;
    private JTextField field_UstrID;
    private JRadioButton rbBestellung;
    private JLabel jLabel19;
    private JLabel jLabel7;
    private JTextField field_Zeile1;
    private JRadioButton rbRezension;
    private JDateChooser field_Bezahldatum;
    private JLabel jLabel8;
    private JTextField field_Zeile2;
    private JRadioButton rbPflicht;
    private JLabel jLabel9;
    private JTextField field_Zeile3;
    private JRadioButton rbGeschenk;
    private JCheckBox field_storniert;
    private JLabel jLabel10;
    private JTextField field_Zeile4;
    private JRadioButton rbBeleg;
    private JLabel jLabel12;
    private JTextField field_Zeile5;
    private JLabel jLabel18;
    private JTextField field_Versand;
    private JLabel jLabel13;
    private JTextField field_Zeile6;
    private JButton jButtonLand;
    private JCheckBox field_Land;
    private JCheckBox field_EU;
    private JCheckBox field_Sprache;
    private JLabel label1;
    private JLabel jLabel21;
    private JLabel jLabel22;
    private JLabel jLabel23;
    private JComboBox<String> cbBuch;
    private JTextField field_B_Anzahl;
    private JTextField field_B_Rabatt;
    private JLabel jLabel27;
    private JCheckBox field_B_Sonstiges;
    private JTextField field_B_Text;
    private JTextField field_B_Preis;
    private JButton BAnfang;
    private JButton BZurueck;
    private JButton BVor;
    private JButton BEnde;
    private JButton BUpdate;
    private JButton BEinfuegen;
    private JButton BLoeschen;
    private JLabel jLabel11;
    private JTextField field_B_Count;
    private JLabel jLabel24;
    private JTextField field_B_CountMax;
    private JRadioButton field_Ueberweisung;
    private JTextField field_Zahlungsziel;
    private JLabel jLabel30;
    private JRadioButton field_Verrechnung;
    private JCheckBox field_Zusatz_cb;
    private JTextField field_Zusatz_Text;
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
    private JButton Mahnung;
    private JButton Schliessen;
    private JLabel label2;
    private JTextField field_EMail;
    private JLabel label3;
    private JTextField field_DHL;
    private JButton EMail;
    private JRadioButton rbRemittende;
    // End of variables declaration//GEN-END:variables

    private JFileChooser chooser = new JFileChooser(new File(System.getProperty("user.dir")));

    private Integer count = 0;
    private Integer countMax = 0;
    private Integer Bestand = 0;

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

    private boolean resultIsEmpty = true;
    private boolean resultBIsEmpty = true;

    private int countB = 0;
    private int countBMax = 0;

    private int maxID = 0;
    private int maxBID = 0;
    private int Anzahl = 0;

    private String eintrag = "";

    private java.util.Date CurDate = new java.util.Date(); // das aktuelle Datum

    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private Statement SQLAnfrage_Liste;
    private ResultSet result_Liste;

    ButtonGroup buttonGroupTyp = new ButtonGroup();
    ButtonGroup buttonGroupAdresse = new ButtonGroup();

}
