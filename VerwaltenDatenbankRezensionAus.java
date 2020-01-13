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
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import com.toedter.calendar.*;
import java.awt.event.ActionEvent;
import javax.swing.plaf.ActionMapUIResource;

/**
 *
 * @author Thomas Zimmermann
 */
public class VerwaltenDatenbankRezensionAus extends javax.swing.JDialog {

    private String makePseudoBestellung(String BestNrString, Integer Kunde, java.sql.Date Datum) {
        int imaxID;
        int imaxBID;
        int iBNr;

        ResultSet iresultB = null;
        ResultSet iresultBD = null;
        ResultSet iresultR = null;
        ResultSet iresultRD = null;
        ResultSet iresultBuch = null;

        Statement iSQLAnfrageB = null;
        Statement iSQLAnfrageBD = null;
        Statement iSQLAnfrageR = null;
        Statement iSQLAnfrageRD = null;
        Statement iSQLAnfrageBuch = null;

        try {
            iSQLAnfrageBD = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            iSQLAnfrageB = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            iSQLAnfrageRD = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            iSQLAnfrageR = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            iSQLAnfrageBuch = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

            // prüfen, ob Bestellung bereits angelegt wurde
            iresultB = iSQLAnfrageB.executeQuery("SELECT * FROM TBL_BESTELLUNG WHERE BESTELLUNG_RECHNR ='" + BestNrString + "'");
            iresultB.last();
            Integer size = iresultB.getRow();

            if (size == 0) { // bisher keine Bestellung angelegt
                // Rezensionen bearbeiten
                Modulhelferlein.Infomeldung("Erzeuge fiktive Rechnung");
                iresultB = iSQLAnfrageB.executeQuery("SELECT * FROM TBL_BESTELLUNG ORDER BY BESTELLUNG_ID DESC");
                if (iresultB.next()) {
                    imaxID = iresultB.getInt("BESTELLUNG_ID");
                } else {
                    imaxID = 0;
                }

                iresultBD = iSQLAnfrageBD.executeQuery("SELECT * FROM TBL_BESTELLUNG_DETAIL ORDER BY BESTELLUNG_DETAIL_ID DESC");
                if (iresultBD.next()) {
                    imaxBID = iresultBD.getInt("BESTELLUNG_DETAIL_ID");
                } else {
                    imaxBID = 0;
                }

                iresultB = iSQLAnfrageB.executeQuery("SELECT * FROM TBL_BESTELLUNG");
                iresultR = iSQLAnfrageR.executeQuery("SELECT * FROM TBL_REZENSIONEN_AUS WHERE REZENSIONEN_AUS_NUMMER='" + BestNrString + "'");
                iresultR.next();

                iresultB.moveToInsertRow();
//helferlein.Infomeldung("schreibe bestellung"); 
                iresultB.updateInt("BESTELLUNG_ID", imaxID + 1);
                if (iresultR.getInt("REZENSIONEN_AUS_TYP") == 4) {
                    iresultB.updateInt("BESTELLUNG_TYP", 4);
                } else {
                    iresultB.updateInt("BESTELLUNG_TYP", 1);
                }
                iresultB.updateDate("BESTELLUNG_BEZAHLT", Modulhelferlein.Date2SQLDate(Modulhelferlein.CurDate));
                iresultB.updateDate("BESTELLUNG_DATUM", Datum);
                iresultB.updateDate("BESTELLUNG_RECHDAT", Modulhelferlein.Date2SQLDate(Modulhelferlein.CurDate));
                iresultB.updateInt("BESTELLUNG_KUNDE", Kunde);
                iresultB.updateString("BESTELLUNG_RECHNR", BestNrString);
                iresultB.updateInt("BESTELLUNG_ZAHLUNGSZIEL", 14);
                iresultB.updateString("BESTELLUNG_BESTNR", "ohne");
                iresultB.updateString("BESTELLUNG_ZEILE_1", "");
                iresultB.updateString("BESTELLUNG_ZEILE_2", "");
                iresultB.updateString("BESTELLUNG_ZEILE_3", "");
                iresultB.updateString("BESTELLUNG_ZEILE_4", "");
                iresultB.updateString("BESTELLUNG_ZEILE_5", "");
                iresultB.updateString("BESTELLUNG_ZEILE_6", "");
                iresultB.updateString("BESTELLUNG_USTR_ID", "");
                iresultB.updateString("BESTELLUNG_LINK", "");
                iresultB.updateString("BESTELLUNG_EMAIL", "");
                iresultB.updateString("BESTELLUNG_DHL", "");
                iresultB.updateFloat("BESTELLUNG_VERSAND", 0);
                iresultB.updateInt("BESTELLUNG_LAND", 0);
                iresultB.updateBoolean("BESTELLUNG_PRIVAT", true);
                iresultB.updateBoolean("BESTELLUNG_TB", false);
                iresultB.updateBoolean("BESTELLUNG_BESTAND", true);
                iresultB.updateBoolean("BESTELLUNG_BEZAHLUNG", false);
                iresultB.updateBoolean("BESTELLUNG_STORNIERT", false);
                iresultB.updateString("BESTELLUNG_TEXT", "");

                iresultB.insertRow();

//helferlein.Infomeldung("schreibe detailst"); 
                iresultBD = iSQLAnfrageBD.executeQuery("SELECT * FROM TBL_BESTELLUNG_DETAIL");
                iresultRD = iSQLAnfrageRD.executeQuery("SELECT * FROM TBL_REZENSIONEN_AUS_DETAIL WHERE REZENSIONEN_AUS_DETAIL_NUMMER='" + BestNrString + "'");
                while (iresultRD.next()) {
                    iresultBD.moveToInsertRow();

                    iresultBD.updateInt("BESTELLUNG_DETAIL_ID", imaxBID + 1);
                    imaxBID = imaxBID + 1;
                    iresultBD.updateFloat("BESTELLUNG_DETAIL_RABATT", 100F);
                    iresultBD.updateBoolean("BESTELLUNG_DETAIL_SONST", false);
                    iresultBD.updateString("BESTELLUNG_DETAIL_SONST_TEXT", "");
                    iresultBD.updateFloat("BESTELLUNG_DETAIL_SONST_PREIS", 0F);
                    iresultBD.updateInt("BESTELLUNG_DETAIL_BUCH", iresultRD.getInt("REZENSIONEN_AUS_DETAIL_BUCH"));
                    iresultBD.updateDate("BESTELLUNG_DETAIL_DATUM", Modulhelferlein.Date2SQLDate(CurDate));
                    iresultBD.updateString("BESTELLUNG_DETAIL_RECHNR", BestNrString);
                    iresultBD.updateInt("BESTELLUNG_DETAIL_ANZAHL", 1);

                    iresultBD.insertRow();

                    iresultBuch = iSQLAnfrageBuch.executeQuery("SELECT * FROM TBL_BUCH WHERE BUCH_ID = " + Integer.toString(iresultRD.getInt("REZENSIONEN_AUS_DETAIL_BUCH")));
                    iresultBuch.next();
                    Integer Bestand = iresultBuch.getInt("BUCH_BESTAND") - 1;
                    iresultBuch.updateInt("BUCH_BESTAND", Bestand);
                    iresultBuch.updateRow();
                }

                iresultB.close();
                iSQLAnfrageB.close();
                iresultBD.close();
                iSQLAnfrageBD.close();
//helferlein.Infomeldung("fiktive rechnung ist erstellt");                
            } else {
                // rechnung aktualisieren
                Modulhelferlein.Infomeldung("Aktualisiere fiktive Rechnung " + BestNrString);
                iresultB = iSQLAnfrageB.executeQuery("SELECT * FROM TBL_BESTELLUNG WHERE BESTELLUNG_RECHNR = '" + BestNrString + "'");
                iresultB.next();

                iresultBD = iSQLAnfrageBD.executeQuery("SELECT * FROM TBL_BESTELLUNG_DETAIL ORDER BY BESTELLUNG_DETAIL_ID DESC");
                iresultBD.next();
                imaxBID = iresultBD.getInt("BESTELLUNG_DETAIL_ID");

                iresultR = iSQLAnfrageR.executeQuery("SELECT * FROM TBL_REZENSIONEN_AUS WHERE REZENSIONEN_AUS_NUMMER='" + BestNrString + "'");
                iresultR.next();

                iresultB.updateDate("BESTELLUNG_BEZAHLT", Modulhelferlein.Date2SQLDate(Modulhelferlein.CurDate));
                iresultB.updateDate("BESTELLUNG_RECHDAT", Modulhelferlein.Date2SQLDate(Modulhelferlein.CurDate));
                iresultB.updateInt("BESTELLUNG_KUNDE", Kunde);
                if (iresultR.getInt("REZENSIONEN_AUS_TYP") == 4) {
                    iresultB.updateInt("BESTELLUNG_TYP", 4);
                } else {
                    iresultB.updateInt("BESTELLUNG_TYP", 1);
                }
                iresultB.updateRow();
                Modulhelferlein.Infomeldung("Bestellung aktualisiert");

                iresultBD = iSQLAnfrageBD.executeQuery("SELECT * FROM TBL_BESTELLUNG_DETAIL WHERE BESTELLUNG_DETAIL_RECHNR = '" + BestNrString + "'");
                iresultRD = iSQLAnfrageRD.executeQuery("SELECT * FROM TBL_REZENSIONEN_AUS_DETAIL WHERE REZENSIONEN_AUS_DETAIL_NUMMER='" + BestNrString + "'");
                while (iresultRD.next()) {
                    if (iresultBD.next()) {
                        iresultBD.updateInt("BESTELLUNG_DETAIL_BUCH", iresultRD.getInt("REZENSIONEN_AUS_DETAIL_BUCH"));
                        iresultBD.updateBoolean("BESTELLUNG_DETAIL_SONST", false);
                        iresultBD.updateString("BESTELLUNG_DETAIL_SONST_TEXT", "");
                        iresultBD.updateFloat("BESTELLUNG_DETAIL_SONST_PREIS", 0F);
                        iresultBD.updateRow();
                    } else {
                        iresultBD.moveToInsertRow();

                        iresultBD.updateInt("BESTELLUNG_DETAIL_ID", imaxBID + 1);
                        imaxBID = imaxBID + 1;
                        iresultBD.updateInt("BESTELLUNG_DETAIL_RABATT", 100);
                        iresultBD.updateInt("BESTELLUNG_DETAIL_BUCH", iresultRD.getInt("REZENSIONEN_AUS_DETAIL_BUCH"));
                        iresultBD.updateDate("BESTELLUNG_DETAIL_DATUM", Modulhelferlein.Date2SQLDate(CurDate));
                        iresultBD.updateString("BESTELLUNG_DETAIL_RECHNR", BestNrString);
                        iresultBD.updateInt("BESTELLUNG_DETAIL_ANZAHL", 1);
                        iresultBD.updateBoolean("BESTELLUNG_DETAIL_SONST", false);
                        iresultBD.updateString("BESTELLUNG_DETAIL_SONST_TEXT", "");
                        iresultBD.updateFloat("BESTELLUNG_DETAIL_SONST_PREIS", 0F);

                        iresultBD.insertRow();
                    }
                }

                iresultB.close();
                iSQLAnfrageB.close();
                iresultBD.close();
                iSQLAnfrageBD.close();
            }
        } catch (SQLException exc) {
            Modulhelferlein.Fehlermeldung("Rezensionen versenden", "Make Pseudebestellung: ", exc.getMessage());
        }
        return BestNrString;
    }

    /**
     * Creates new form VerwaltenDatenbankRezensionAus
     *
     * @param parent
     * @param modal
     */
    public VerwaltenDatenbankRezensionAus(java.awt.Frame parent, boolean modal) {
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

        buttonGroupTyp.add(rbNachfrage);
        buttonGroupTyp.add(rbAutor);
        buttonGroupTyp.add(rbInitiative);
        buttonGroupTyp.add(rbFreiexemplar);
        buttonGroupTyp.add(rbDritte);

        conn = null;

        try { // Datenbank-Treiber laden
            Class.forName(Modulhelferlein.dbDriver);
        } catch (ClassNotFoundException exept) {
            Modulhelferlein.Fehlermeldung("Rezensionen versenden", "Treiber nicht gefunden: ", exept.getMessage());
            System.exit(1);
        } // Datenbank-Treiber laden

        try { // Verbindung zur Datenbank über die JDBC-Brücke
            conn = DriverManager.getConnection(Modulhelferlein.dbUrl, Modulhelferlein.dbUser, Modulhelferlein.dbPassword);
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("Rezensionen versenden", "Verbindung nicht moeglich: ", exept.getMessage());
            System.exit(1);
        } // try Verbindung zur Datenbank über die JDBC-Brücke

        if (conn != null) {

            SQLAnfrageR = null; // Anfrage erzeugen für result => Bestellungen
            SQLAnfrageRD = null; // Anfrage erzeugen für resultB => Bestellungen Details
            SQLAnfrageA = null; // Anfrage erzeugen für resultA => Aufbau Kundenliste
            SQLAnfrageBNr = null; // Anfrage erzeugen für resultBNr => Bestellnummer
            SQLAnfrageBuch = null; // Anfrage erzeugen für resultBuch => Aufbau Bücherliste

            try { // SQL-Anfragen an die Datenbank
                SQLAnfrageR = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE); // Anfrage der DB conn2 zuordnen
                SQLAnfrageRD = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE); // Anfrage der DB conn2 zuordnen
                SQLAnfrageA = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE); // Anfrage der DB conn2 zuordnen
                SQLAnfrageBNr = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE); // Anfrage der DB conn2 zuordnen
                SQLAnfrageBuch = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE); // Anfrage der DB conn2 zuordnen

                String eintrag = "0, ----------, -----------";
                cbRezensent.addItem(eintrag);

                // Auswahlliste für Rezensenten erstellen   
                resultA = SQLAnfrageA.executeQuery("SELECT * FROM TBL_ADRESSE WHERE ADRESSEN_Typ = 'Rezensent' OR ADRESSEN_Typ = 'Autor' ORDER BY ADRESSEN_NAME");
                count = 0;
                while (resultA.next()) {
                    count = count + 1;
                    eintrag = Integer.toString(resultA.getInt("ADRESSEN_ID")) + ", "
                            + resultA.getString("ADRESSEN_Name") + ", "
                            + resultA.getString("ADRESSEN_Vorname");
                    cbRezensent.addItem(eintrag);
                } // while

                eintrag = "0, ---------------------";
                cbZeitschrift.addItem(eintrag);

                // Auswahlliste für Zeitschriften erstellen   
                resultA = SQLAnfrageA.executeQuery("SELECT * FROM TBL_ADRESSE WHERE ADRESSEN_Typ = 'Zeitschrift' ORDER BY ADRESSEN_ZEITSCHRIFT");
                count = 0;
                while (resultA.next()) {
                    count = count + 1;
                    eintrag = Integer.toString(resultA.getInt("ADRESSEN_ID")) + ", "
                            + resultA.getString("ADRESSEN_ZEITSCHRIFT");
                    cbZeitschrift.addItem(eintrag);
                } // while

                // Auswahlliste für Bücher erstellen   
                cbBuch.addItem("000, --------------, --------------------------------");
                resultBuch = SQLAnfrageBuch.executeQuery("SELECT * FROM TBL_BUCH ORDER BY BUCH_ISBN"); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert
                while (resultBuch.next()) {
                    eintrag = "";
                    if (resultBuch.getInt("BUCH_ID") < 10) {
                        eintrag = "00" + Integer.toString(resultBuch.getInt("BUCH_ID"));
                    } else if (resultBuch.getInt("BUCH_ID") < 100) {
                        eintrag = "0" + Integer.toString(resultBuch.getInt("BUCH_ID"));
                    } else {
                        eintrag = Integer.toString(resultBuch.getInt("BUCH_ID"));
                    }
                    eintrag = eintrag + ", "
                            + resultBuch.getString("BUCH_ISBN") + ", "
                            + resultBuch.getString("BUCH_TITEL");
                    cbBuch.addItem(eintrag);
                } // while

                // Bestellnummer lesen
                resultBNr = SQLAnfrageBNr.executeQuery("SELECT * FROM TBL_BESTELLNR WHERE BESTELLNR_ID = 1"); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 
                resultBNr.next();

                // Rezensionen bearbeiten
                resultR = SQLAnfrageR.executeQuery("SELECT * FROM TBL_REZENSIONEN_AUS ORDER BY REZENSIONEN_AUS_ID DESC"); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 
                if (resultR.first()) {
                    maxID = resultR.getInt("REZENSIONEN_AUS_ID");
                } else {
                    maxID = 0;
                }

                resultR = SQLAnfrageR.executeQuery("SELECT * FROM TBL_REZENSIONEN_AUS"); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 

                // Anzahl der Datensätze ermitteln
                countMax = 0;
                count = 0;
                field_count.setText(Integer.toString(count));
                while (resultR.next()) {
                    ++countMax;
                }
                field_countMax.setText(Integer.toString(countMax));

                // gehe zum ersten Datensatz - wenn nicht leer
                if (resultR.first()) {
                    count = 1;
                    field_count.setText(Integer.toString(count));
                    resultIsEmpty = false;

                    // Typ lesen
                    switch (resultR.getInt("REZENSIONEN_AUS_TYP")) {
                        case 0:
                            rbNachfrage.setSelected(true);
                            break;
                        case 1:
                            rbAutor.setSelected(true);
                            break;
                        case 3:
                            rbInitiative.setSelected(true);
                            break;
                        case 4:
                            rbFreiexemplar.setSelected(true);
                            break;
                        case 5:
                            rbDritte.setSelected(true);
                            break;
                    }
                    field_Dritte.setText(resultR.getString("REZENSIONEN_AUS_DRITTE"));
                    if (resultR.getInt("REZENSIONEN_AUS_REZENSENT") > 0) {
                        resultA = SQLAnfrageA.executeQuery("SELECT * FROM tbl_adresse WHERE ADRESSEN_ID = '" + Integer.toString(resultR.getInt("REZENSIONEN_AUS_REZENSENT")) + "'");
                        resultA.next();
                        cbRezensent.setSelectedItem(Integer.toString(resultR.getInt("REZENSIONEN_AUS_REZENSENT")) + ", " + resultA.getString("ADRESSEN_NAME") + ", " + resultA.getString("ADRESSEN_VORNAME"));
                    } else {
                        cbRezensent.setSelectedItem("0, ----------, -----------");
                    }
                    if (resultR.getInt("REZENSIONEN_AUS_ZEITSCHRIFT") > 0) {
                        resultA = SQLAnfrageA.executeQuery("SELECT * FROM tbl_adresse WHERE ADRESSEN_ID = '" + Integer.toString(resultR.getInt("REZENSIONEN_AUS_ZEITSCHRIFT")) + "'");
                        resultA.next();
                        cbZeitschrift.setSelectedItem(Integer.toString(resultA.getInt("ADRESSEN_ID")) + ", " + resultA.getString("ADRESSEN_ZEITSCHRIFT"));
                    } else {
                        cbZeitschrift.setSelectedItem("0, ---------------------");
                    }
                    field_NachfrageDatum.setDate(Modulhelferlein.Date2SQLDate(resultR.getDate("REZENSIONEN_AUS_DATUM")));
                    field_Nummer.setText(resultR.getString("REZENSIONEN_AUS_NUMMER"));

// Rezensionsdetails lesen  
                    resultRD = SQLAnfrageRD.executeQuery("SELECT * FROM TBL_REZENSIONEN_AUS_DETAIL ORDER BY REZENSIONEN_AUS_DETAIL_ID DESC");
                    if (resultRD.first()) {
                        maxBID = resultRD.getInt("REZENSIONEN_AUS_DETAIL_ID");
                    } else {
                        maxBID = 0;
                    }

                    resultRD = SQLAnfrageRD.executeQuery(
                            "SELECT * FROM TBL_REZENSIONEN_AUS_DETAIL WHERE REZENSIONEN_AUS_DETAIL_NUMMER = '" + resultR.getString("REZENSIONEN_AUS_NUMMER") + "'");
                    countB = 0;
                    countBMax = 0;
                    while (resultRD.next()) {
                        ++countBMax;
                    }

                    if (countBMax > 0) { // es gibt Details zur Bestellung
                        resultBIsEmpty = false;
                        resultRD.first();
                        countB = 1;
                        field_B_Count.setText(Integer.toString(countB));
                        field_B_CountMax.setText(Integer.toString(countBMax));

                        // Felder füllen
                        resultBuch = SQLAnfrageBuch.executeQuery("SELECT * FROM TBL_BUCH WHERE BUCH_ID = '" + resultRD.getString("REZENSIONEN_AUS_DETAIL_BUCH") + "'");
                        resultBuch.next();
                        eintrag = "";
                        if (resultBuch.getInt("BUCH_ID") < 10) {
                            eintrag = "00" + Integer.toString(resultBuch.getInt("BUCH_ID"));
                        } else if (resultBuch.getInt("BUCH_ID") < 100) {
                            eintrag = "0" + Integer.toString(resultBuch.getInt("BUCH_ID"));
                        } else {
                            eintrag = Integer.toString(resultBuch.getInt("BUCH_ID"));
                        }
                        eintrag = eintrag + ", "
                                + resultBuch.getString("BUCH_ISBN") + ", "
                                + resultBuch.getString("BUCH_TITEL");
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
                    }

                    // Schalterzustände REZENSIONEN_AUS setzen
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
                    cbBuch.setEditable(false);
                }
            } catch (SQLException exept) {
                Modulhelferlein.Fehlermeldung("DB-Bestellung: SQL-Exception: SQL-Anfrage nicht moeglich: "
                        + exept.getMessage());
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
        jLabel5 = new JLabel();
        field_Nummer = new JTextField();
        cbRezensent = new JComboBox<>();
        jLabel25 = new JLabel();
        jLabel13 = new JLabel();
        rbInitiative = new JRadioButton();
        jLabel12 = new JLabel();
        rbNachfrage = new JRadioButton();
        jLabel7 = new JLabel();
        cbZeitschrift = new JComboBox<>();
        rbAutor = new JRadioButton();
        field_NachfrageDatum = new JDateChooser();
        rbDritte = new JRadioButton();
        rbBestellung = new JCheckBox();
        field_Dritte = new JTextField();
        rbFreiexemplar = new JRadioButton();
        jLabel21 = new JLabel();
        cbBuch = new JComboBox<>();
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
        Anfang = new JButton();
        Zurueck = new JButton();
        Vor = new JButton();
        Update = new JButton();
        Ende = new JButton();
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
        setMinimumSize(new Dimension(765, 445));
        var contentPane = getContentPane();

        //======== panel1 ========
        {
            panel1.setLayout(null);

            //---- jLabel1 ----
            jLabel1.setFont(new Font("Tahoma", Font.BOLD, 12));
            jLabel1.setText("Verwalten der Rezensionen - Anfragen, Nachfragen, Autorenw\u00fcnsche");
            panel1.add(jLabel1);
            jLabel1.setBounds(0, 0, 441, 25);

            //---- jLabel2 ----
            jLabel2.setText("Datensatz");
            panel1.add(jLabel2);
            jLabel2.setBounds(534, 0, 75, 25);

            //---- field_count ----
            field_count.setEditable(false);
            field_count.setText("000");
            field_count.setEnabled(false);
            field_count.setFocusable(false);
            field_count.setMinimumSize(new Dimension(30, 25));
            field_count.setPreferredSize(new Dimension(30, 25));
            panel1.add(field_count);
            field_count.setBounds(new Rectangle(new Point(614, 0), field_count.getPreferredSize()));

            //---- jLabel3 ----
            jLabel3.setText("von");
            panel1.add(jLabel3);
            jLabel3.setBounds(649, 0, 50, 25);

            //---- field_countMax ----
            field_countMax.setEditable(false);
            field_countMax.setText("000");
            field_countMax.setEnabled(false);
            field_countMax.setFocusable(false);
            field_countMax.setMinimumSize(new Dimension(30, 25));
            field_countMax.setPreferredSize(new Dimension(30, 25));
            panel1.add(field_countMax);
            field_countMax.setBounds(new Rectangle(new Point(704, 0), field_countMax.getPreferredSize()));

            //---- jLabel5 ----
            jLabel5.setText("Nummer");
            panel1.add(jLabel5);
            jLabel5.setBounds(534, 30, 75, 25);

            //---- field_Nummer ----
            field_Nummer.setEditable(false);
            field_Nummer.setText("jTextField1");
            field_Nummer.setEnabled(false);
            field_Nummer.setFocusable(false);
            field_Nummer.setPreferredSize(new Dimension(100, 25));
            panel1.add(field_Nummer);
            field_Nummer.setBounds(614, 30, 120, field_Nummer.getPreferredSize().height);

            //---- cbRezensent ----
            cbRezensent.setModel(new DefaultComboBoxModel<>(new String[] {

            }));
            cbRezensent.addActionListener(e -> cbRezensentActionPerformed(e));
            panel1.add(cbRezensent);
            cbRezensent.setBounds(0, 79, 345, 23);

            //---- jLabel25 ----
            jLabel25.setText("Rezensent");
            panel1.add(jLabel25);
            jLabel25.setBounds(0, 60, 249, jLabel25.getPreferredSize().height);

            //---- jLabel13 ----
            jLabel13.setText("Warum");
            panel1.add(jLabel13);
            jLabel13.setBounds(396, 60, 89, jLabel13.getPreferredSize().height);

            //---- rbInitiative ----
            rbInitiative.setText("Initiative Verlag");
            rbInitiative.addActionListener(e -> rbInitiativeActionPerformed(e));
            panel1.add(rbInitiative);
            rbInitiative.setBounds(396, 79, 158, rbInitiative.getPreferredSize().height);

            //---- jLabel12 ----
            jLabel12.setText("Zeitschrift");
            panel1.add(jLabel12);
            jLabel12.setBounds(0, 107, 345, 23);

            //---- rbNachfrage ----
            rbNachfrage.setSelected(true);
            rbNachfrage.setText("Nachfrage");
            rbNachfrage.addActionListener(e -> rbNachfrageActionPerformed(e));
            panel1.add(rbNachfrage);
            rbNachfrage.setBounds(396, 107, 158, rbNachfrage.getPreferredSize().height);

            //---- jLabel7 ----
            jLabel7.setText("von wann");
            panel1.add(jLabel7);
            jLabel7.setBounds(559, 107, 85, 23);

            //---- cbZeitschrift ----
            cbZeitschrift.setModel(new DefaultComboBoxModel<>(new String[] {

            }));
            cbZeitschrift.addActionListener(e -> cbZeitschriftActionPerformed(e));
            panel1.add(cbZeitschrift);
            cbZeitschrift.setBounds(0, 135, 345, 23);

            //---- rbAutor ----
            rbAutor.setText("Wunsch Autor");
            rbAutor.addActionListener(e -> rbAutorActionPerformed(e));
            panel1.add(rbAutor);
            rbAutor.setBounds(396, 135, 158, rbAutor.getPreferredSize().height);
            panel1.add(field_NachfrageDatum);
            field_NachfrageDatum.setBounds(559, 135, 140, 23);

            //---- rbDritte ----
            rbDritte.setText("Wunsch Dritter");
            panel1.add(rbDritte);
            rbDritte.setBounds(396, 163, 158, rbDritte.getPreferredSize().height);

            //---- rbBestellung ----
            rbBestellung.setSelected(true);
            rbBestellung.setText("erstelle Bestellung");
            panel1.add(rbBestellung);
            rbBestellung.setBounds(0, 191, 149, rbBestellung.getPreferredSize().height);
            panel1.add(field_Dritte);
            field_Dritte.setBounds(415, 191, 319, 23);

            //---- rbFreiexemplar ----
            rbFreiexemplar.setText("Freiexemplar");
            panel1.add(rbFreiexemplar);
            rbFreiexemplar.setBounds(396, 219, 158, rbFreiexemplar.getPreferredSize().height);

            //---- jLabel21 ----
            jLabel21.setText("Buch");
            panel1.add(jLabel21);
            jLabel21.setBounds(54, 277, 41, jLabel21.getPreferredSize().height);

            //---- cbBuch ----
            cbBuch.setFont(new Font("Courier New", Font.BOLD, 14));
            cbBuch.setModel(new DefaultComboBoxModel<>(new String[] {

            }));
            cbBuch.setPreferredSize(new Dimension(534, 25));
            panel1.add(cbBuch);
            cbBuch.setBounds(100, 272, 599, cbBuch.getPreferredSize().height);

            //---- BAnfang ----
            BAnfang.setText("<<");
            BAnfang.setToolTipText("gehe zum ersten Datensatz");
            BAnfang.addActionListener(e -> BAnfangActionPerformed(e));
            panel1.add(BAnfang);
            BAnfang.setBounds(100, 305, 49, BAnfang.getPreferredSize().height);

            //---- BZurueck ----
            BZurueck.setText("<");
            BZurueck.setToolTipText("gehe zum vorherigen Datensatz");
            BZurueck.addActionListener(e -> BZurueckActionPerformed(e));
            panel1.add(BZurueck);
            BZurueck.setBounds(151, 305, 49, BZurueck.getPreferredSize().height);

            //---- BVor ----
            BVor.setText(">");
            BVor.setToolTipText("gehe zum n\u00e4chsten Datensatz");
            BVor.addActionListener(e -> BVorActionPerformed(e));
            panel1.add(BVor);
            BVor.setBounds(202, 305, 49, BVor.getPreferredSize().height);

            //---- BEnde ----
            BEnde.setText(">>");
            BEnde.setToolTipText("gehe zum letzten Datensatz");
            BEnde.addActionListener(e -> BEndeActionPerformed(e));
            panel1.add(BEnde);
            BEnde.setBounds(253, 305, 49, BEnde.getPreferredSize().height);

            //---- BUpdate ----
            BUpdate.setText("!");
            BUpdate.setToolTipText("Datensatz aktualisieren");
            BUpdate.addActionListener(e -> BUpdateActionPerformed(e));
            panel1.add(BUpdate);
            BUpdate.setBounds(320, 305, 49, BUpdate.getPreferredSize().height);

            //---- BEinfuegen ----
            BEinfuegen.setText("+");
            BEinfuegen.setToolTipText("Buch hinzuf\u00fcgen");
            BEinfuegen.addActionListener(e -> BEinfuegenActionPerformed(e));
            panel1.add(BEinfuegen);
            BEinfuegen.setBounds(375, 305, 49, BEinfuegen.getPreferredSize().height);

            //---- BLoeschen ----
            BLoeschen.setText("-");
            BLoeschen.setToolTipText("Datensatz l\u00f6schen");
            BLoeschen.addActionListener(e -> BLoeschenActionPerformed(e));
            panel1.add(BLoeschen);
            BLoeschen.setBounds(430, 305, 49, BLoeschen.getPreferredSize().height);

            //---- jLabel11 ----
            jLabel11.setText("Buch");
            panel1.add(jLabel11);
            jLabel11.setBounds(515, 307, 39, jLabel11.getPreferredSize().height);

            //---- field_B_Count ----
            field_B_Count.setEditable(false);
            field_B_Count.setText("jTextField12");
            field_B_Count.setEnabled(false);
            field_B_Count.setFocusable(false);
            field_B_Count.setPreferredSize(new Dimension(50, 25));
            panel1.add(field_B_Count);
            field_B_Count.setBounds(new Rectangle(new Point(559, 302), field_B_Count.getPreferredSize()));

            //---- jLabel24 ----
            jLabel24.setText("von");
            jLabel24.setRequestFocusEnabled(false);
            panel1.add(jLabel24);
            jLabel24.setBounds(614, 307, 30, jLabel24.getPreferredSize().height);

            //---- field_B_CountMax ----
            field_B_CountMax.setEditable(false);
            field_B_CountMax.setText("jTextField12");
            field_B_CountMax.setEnabled(false);
            field_B_CountMax.setFocusable(false);
            field_B_CountMax.setPreferredSize(new Dimension(50, 25));
            panel1.add(field_B_CountMax);
            field_B_CountMax.setBounds(new Rectangle(new Point(649, 302), field_B_CountMax.getPreferredSize()));

            //---- Anfang ----
            Anfang.setText("<<");
            Anfang.setToolTipText("gehe zum ersten Datensatz");
            Anfang.addActionListener(e -> AnfangActionPerformed(e));
            panel1.add(Anfang);
            Anfang.setBounds(4, 360, 49, 23);

            //---- Zurueck ----
            Zurueck.setText("<");
            Zurueck.setToolTipText("gehe zum vorherigen Datensatz");
            Zurueck.addActionListener(e -> ZurueckActionPerformed(e));
            panel1.add(Zurueck);
            Zurueck.setBounds(58, 360, 49, 23);

            //---- Vor ----
            Vor.setText(">");
            Vor.setToolTipText("gehe zum n\u00e4chsten Datensatz");
            Vor.addActionListener(e -> VorActionPerformed(e));
            panel1.add(Vor);
            Vor.setBounds(104, 360, 49, 23);

            //---- Update ----
            Update.setText("!");
            Update.setToolTipText("Datensatz aktualisieren");
            Update.addActionListener(e -> UpdateActionPerformed(e));
            panel1.add(Update);
            Update.setBounds(234, 360, 49, 23);

            //---- Ende ----
            Ende.setText(">>");
            Ende.setToolTipText("gehe zum letzten Datensatz");
            Ende.addActionListener(e -> EndeActionPerformed(e));
            panel1.add(Ende);
            Ende.setBounds(159, 360, 49, 23);

            //---- Einfuegen ----
            Einfuegen.setText("+");
            Einfuegen.setToolTipText("Datensatz f\u00fcr eine Rezension erstellen");
            Einfuegen.addActionListener(e -> EinfuegenActionPerformed(e));
            panel1.add(Einfuegen);
            Einfuegen.setBounds(284, 360, 49, 23);

            //---- Loeschen ----
            Loeschen.setText("-");
            Loeschen.setToolTipText("Datensatz l\u00f6schen");
            Loeschen.addActionListener(e -> LoeschenActionPerformed(e));
            panel1.add(Loeschen);
            Loeschen.setBounds(334, 360, 49, 23);

            //---- Suchen ----
            Suchen.setText("?");
            Suchen.setToolTipText("Suche nach Autor, Titel, ISBN oder Druckereinummer");
            Suchen.addActionListener(e -> SuchenActionPerformed(e));
            panel1.add(Suchen);
            Suchen.setBounds(394, 360, 49, 23);

            //---- WSuchen ----
            WSuchen.setText("...");
            WSuchen.setToolTipText("Weitersuchen");
            WSuchen.addActionListener(e -> WSuchenActionPerformed(e));
            panel1.add(WSuchen);
            WSuchen.setBounds(445, 360, 49, 23);

            //---- Drucken ----
            Drucken.setText("D");
            Drucken.setToolTipText("Druckt die aktuellen Rezensionsschreiben");
            Drucken.addActionListener(e -> DruckenActionPerformed(e));
            panel1.add(Drucken);
            Drucken.setBounds(520, 360, 49, 23);

            //---- Schliessen ----
            Schliessen.setText("X");
            Schliessen.setToolTipText("Schlie\u00dft den Dialog");
            Schliessen.addActionListener(e -> SchliessenActionPerformed(e));
            panel1.add(Schliessen);
            Schliessen.setBounds(580, 360, 49, 23);

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
                    .addContainerGap(14, Short.MAX_VALUE))
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(panel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        setSize(760, 440);
        setLocationRelativeTo(getOwner());

        //---- buttonGroup1 ----
        var buttonGroup1 = new ButtonGroup();
        buttonGroup1.add(rbInitiative);
        buttonGroup1.add(rbNachfrage);
        buttonGroup1.add(rbAutor);
        buttonGroup1.add(rbDritte);
        buttonGroup1.add(rbFreiexemplar);
    }// </editor-fold>//GEN-END:initComponents

    private void WSuchenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_WSuchenActionPerformed
        // TODO add your handling code here:
        boolean gefunden = false;

        try {
            do {
            } while ((!gefunden) && resultR.next());
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("rezensionen versenden", "SQL-Exception: ", exept.getMessage());
        }
    }//GEN-LAST:event_WSuchenActionPerformed

    private void AnfangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AnfangActionPerformed
        // TODO add your handling code here:
        try {
            resultR.first();
            count = 1;
            field_count.setText(Integer.toString(count));

            switch (resultR.getInt("REZENSIONEN_AUS_TYP")) {
                case 0:
                    rbNachfrage.setSelected(true);
                    break;
                case 1:
                    rbAutor.setSelected(true);
                    break;
                case 3:
                    rbInitiative.setSelected(true);
                    break;
                case 5:
                    rbDritte.setSelected(true);
                    break;
            }
            field_Dritte.setText(resultR.getString("REZENSIONEN_AUS_DRITTE"));

            field_Nummer.setText(resultR.getString("REZENSIONEN_AUS_NUMMER"));

            field_NachfrageDatum.setDate(Modulhelferlein.Date2SQLDate(resultR.getDate("REZENSIONEN_AUS_DATUM")));
            if (resultR.getInt("REZENSIONEN_AUS_REZENSENT") > 0) {
                resultA = SQLAnfrageA.executeQuery("SELECT * FROM tbl_adresse WHERE ADRESSEN_ID = '" + Integer.toString(resultR.getInt("REZENSIONEN_AUS_REZENSENT")) + "'");
                resultA.next();
                cbRezensent.setSelectedItem(Integer.toString(resultR.getInt("REZENSIONEN_AUS_REZENSENT")) + ", " + resultA.getString("ADRESSEN_NAME") + ", " + resultA.getString("ADRESSEN_VORNAME"));
            } else {
                cbRezensent.setSelectedItem("0, ----------, -----------");
            }
            if (resultR.getInt("REZENSIONEN_AUS_ZEITSCHRIFT") > 0) {
                resultA = SQLAnfrageA.executeQuery("SELECT * FROM tbl_adresse WHERE ADRESSEN_ID = '" + Integer.toString(resultR.getInt("REZENSIONEN_AUS_ZEITSCHRIFT")) + "'");
                resultA.next();
                cbZeitschrift.setSelectedItem(Integer.toString(resultA.getInt("ADRESSEN_ID")) + ", " + resultA.getString("ADRESSEN_ZEITSCHRIFT"));
            } else {
                cbZeitschrift.setSelectedItem("0, ---------------------");
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
            resultRD = SQLAnfrageRD.executeQuery(
                    "SELECT * FROM TBL_REZENSIONEN_AUS_DETAIL WHERE REZENSIONEN_AUS_DETAIL_NUMMER = '" + resultR.getString("REZENSIONEN_AUS_NUMMER") + "'"); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 
            countBMax = 0;
            while (resultRD.next()) {
                ++countBMax;
            }
            resultRD.first();
            field_B_CountMax.setText(Integer.toString(countBMax));
            if (countBMax == 0) {
                // Schalterzustände setzen
                countB = 0;
                field_B_Count.setText(Integer.toString(countB));
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
                resultBuch = SQLAnfrageBuch.executeQuery("SELECT * FROM TBL_BUCH WHERE BUCH_ID = '" + resultRD.getString("REZENSIONEN_AUS_DETAIL_BUCH") + "'");
                resultBuch.next();
                String eintrag = "";
                if (resultBuch.getInt("BUCH_ID") < 10) {
                    eintrag = "00" + Integer.toString(resultBuch.getInt("BUCH_ID"));
                } else if (resultBuch.getInt("BUCH_ID") < 100) {
                    eintrag = "0" + Integer.toString(resultBuch.getInt("BUCH_ID"));
                } else {
                    eintrag = Integer.toString(resultBuch.getInt("BUCH_ID"));
                }
                eintrag = eintrag + ", "
                        + resultBuch.getString("BUCH_ISBN") + ", "
                        + resultBuch.getString("BUCH_TITEL");
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
            Modulhelferlein.Fehlermeldung("Rezensionen versenden", "Bestellung: zum Anfang: SQL-Exception: ", exept.getMessage());
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
                resultR.last();
                ID = 1 + resultR.getInt("REZENSIONEN_AUS_ID");
            }
            resultR.moveToInsertRow();
            resultR.updateInt("REZENSIONEN_AUS_TYP", 0);
            resultR.updateInt("REZENSIONEN_AUS_ID", ID);
            resultR.updateInt("REZENSIONEN_AUS_REZENSENT", 0);
            resultR.updateInt("REZENSIONEN_AUS_ZEITSCHRIFT", 0);
            resultR.updateString("REZENSIONEN_AUS_DRITTE", "");
            resultR.updateDate("REZENSIONEN_AUS_DATUM", Modulhelferlein.Date2SQLDate(CurDate));

            int BestNr = resultBNr.getInt("BESTELLNR_NUMMER");
            String BestNrString = Integer.toString(BestNr);
            while (BestNrString.length() < 3) {
                BestNrString = "0" + BestNrString;
            }
            resultR.updateString("REZENSIONEN_AUS_NUMMER", Modulhelferlein.printSimpleDateFormat("yyyyMMdd") + "-" + BestNrString);
            resultR.insertRow();

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

            resultR.last();
            switch (resultR.getInt("REZENSIONEN_AUS_TYP")) {
                case 0:
                    rbNachfrage.setSelected(true);
                    break;
                case 1:
                    rbAutor.setSelected(true);
                    break;
                case 3:
                    rbInitiative.setSelected(true);
                    break;
                case 4:
                    rbFreiexemplar.setSelected(true);
                    break;
            }
            field_NachfrageDatum.setDate(Modulhelferlein.Date2SQLDate(resultR.getDate("REZENSIONEN_AUS_DATUM")));
            field_Nummer.setText(resultR.getString("REZENSIONEN_AUS_NUMMER"));
            cbRezensent.setSelectedIndex(0);
            if (resultR.getInt("REZENSIONEN_AUS_ZEITSCHRIFT") > 0) {
                resultA = SQLAnfrageA.executeQuery("SELECT * FROM tbl_adresse WHERE ADRESSEN_ID = '" + Integer.toString(resultR.getInt("REZENSIONEN_AUS_ZEITSCHRIFT")) + "'");
                resultA.next();
                cbZeitschrift.setSelectedItem(Integer.toString(resultA.getInt("ADRESSEN_ID")) + ", " + resultA.getString("ADRESSEN_ZEITSCHRIFT"));
            } else {
                cbZeitschrift.setSelectedItem("0, ---------------------");
            }
            // Schalterzustände setzen Bestelldetails   
            countB = 0;
            field_B_Count.setText("0");
            field_B_Count.setEditable(false);
            countBMax = 0;
            field_B_CountMax.setText("0");
            field_B_CountMax.setEditable(false);

            BAnfang.setEnabled(false);
            BZurueck.setEnabled(false);
            BVor.setEnabled(false);
            BEnde.setEnabled(false);
            BUpdate.setEnabled(false);
            BEinfuegen.setEnabled(true);
            BLoeschen.setEnabled(false);
            cbBuch.setEditable(false);

        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("Rezensionen versenden", "Bestellung: Einfügen: SQL-Exception: Einfügen: ", exept.getMessage());
        }
    }//GEN-LAST:event_EinfuegenActionPerformed

    private void SuchenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SuchenActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_SuchenActionPerformed

    private void UpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UpdateActionPerformed
        // TODO add your handling code here:
        try {
            resultR.updateDate("REZENSIONEN_AUS_DATUM", Modulhelferlein.Date2SQLDate(field_NachfrageDatum.getDate()));
            if (rbNachfrage.isSelected()) {
                resultR.updateInt("REZENSIONEN_AUS_TYP", 0);
            } else if (rbAutor.isSelected()) {
                resultR.updateInt("REZENSIONEN_AUS_TYP", 1);
            } else if (rbInitiative.isSelected()) {
                resultR.updateInt("REZENSIONEN_AUS_TYP", 3);
            } else if (rbFreiexemplar.isSelected()) {
                resultR.updateInt("REZENSIONEN_AUS_TYP", 4);
            } else {
                resultR.updateInt("REZENSIONEN_AUS_TYP", 5);
            }
            resultR.updateString("REZENSIONEN_AUS_DRITTE", field_Dritte.getText());
            String Rezensent[] = cbRezensent.getSelectedItem().toString().split(",");
            resultR.updateInt("REZENSIONEN_AUS_REZENSENT", Integer.parseInt(Rezensent[0]));

            String Zeitschrift[] = cbZeitschrift.getSelectedItem().toString().split(",");
            resultR.updateInt("REZENSIONEN_AUS_ZEITSCHRIFT", Integer.parseInt(Zeitschrift[0]));

            resultR.updateRow();
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("Rezensionen versenden", "Bestellung: Update: SQL-Exception: ", exept.getMessage());
        }
    }//GEN-LAST:event_UpdateActionPerformed

    private void LoeschenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LoeschenActionPerformed
        // TODO add your handling code here:
        if (JOptionPane.showConfirmDialog(null, "Soll der Datensatz wirklich gelöscht werden?") == JOptionPane.YES_OPTION) {
            try {
                // Rezensionen Details löschen
                resultRD = SQLAnfrageRD.executeQuery(
                        "SELECT * FROM TBL_REZENSIONEN_AUS_DETAIL WHERE REZENSIONEN_AUS_DETAIL_NUMMER = '" + resultR.getString("REZENSIONEN_AUS_NUMMER") + "'");
                while (resultRD.next()) {
                    resultRD.deleteRow();
                }

                resultR.deleteRow();
                countMax = countMax - 1;
                field_countMax.setText(Integer.toString(countMax));
                count = 1;
                field_count.setText(Integer.toString(count));

                resultR.first();

                switch (resultR.getInt("REZENSIONEN_AUS_TYP")) {
                    case 0:
                        rbNachfrage.setSelected(true);
                        break;
                    case 1:
                        rbAutor.setSelected(true);
                        break;
                    case 3:
                        rbInitiative.setSelected(true);
                        break;
                    case 5:
                        rbDritte.setSelected(true);
                        break;
                }
                field_Dritte.setText(resultR.getString("REZENSIONEN_AUS_DRITTE"));
                field_NachfrageDatum.setDate(Modulhelferlein.Date2SQLDate(resultR.getDate("REZENSIONEN_AUS_DATUM")));
                field_Nummer.setText(resultR.getString("REZENSIONEN_AUS_NUMMER"));
                if (resultR.getInt("REZENSIONEN_AUS_REZENSENT") > 0) {
                    resultA = SQLAnfrageA.executeQuery("SELECT * FROM tbl_adresse WHERE ADRESSEN_ID = '" + Integer.toString(resultR.getInt("REZENSIONEN_AUS_REZENSENT")) + "'");
                    resultA.next();
                    cbRezensent.setSelectedItem(Integer.toString(resultR.getInt("REZENSIONEN_AUS_REZENSENT")) + ", " + resultA.getString("ADRESSEN_NAME") + ", " + resultA.getString("ADRESSEN_VORNAME"));
                } else {
                    cbRezensent.setSelectedItem("0, ----------, -----------");
                }
                if (resultR.getInt("REZENSIONEN_AUS_ZEITSCHRIFT") > 0) {
                    resultA = SQLAnfrageA.executeQuery("SELECT * FROM tbl_adresse WHERE ADRESSEN_ID = '" + Integer.toString(resultR.getInt("REZENSIONEN_AUS_ZEITSCHRIFT")) + "'");
                    resultA.next();
                    cbZeitschrift.setSelectedItem(Integer.toString(resultA.getInt("ADRESSEN_ID")) + ", " + resultA.getString("ADRESSEN_ZEITSCHRIFT"));
                } else {
                    cbZeitschrift.setSelectedItem("0, ---------------------");
                }

                if (resultR.getRow() > 0) {
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
                resultRD = SQLAnfrageRD.executeQuery(
                        "SELECT * FROM TBL_REZENSIONEN_AUS_DETAIL WHERE REZENSIONEN_AUS_DETAIL_NUMMER = '" + resultR.getString("REZENSIONEN_AUS_NUMMER") + "'"); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 
                countBMax = 0;
                while (resultRD.next()) {
                    ++countBMax;
                }
                field_B_CountMax.setText(Integer.toString(countBMax));
                if (countBMax == 0) {
                    // Schalterzustände setzen
                    countB = 0;
                    field_B_Count.setText(Integer.toString(countB));
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
                    resultBuch = SQLAnfrageBuch.executeQuery("SELECT * FROM TBL_BUCH WHERE BUCH_ID = '" + resultRD.getString("REZENSIONEN_AUS_DETAIL_BUCH") + "'");
                    resultBuch.next();
                    String eintrag = "";
                    if (resultBuch.getInt("BUCH_ID") < 10) {
                        eintrag = "00" + Integer.toString(resultBuch.getInt("BUCH_ID"));
                    } else if (resultBuch.getInt("BUCH_ID") < 100) {
                        eintrag = "0" + Integer.toString(resultBuch.getInt("BUCH_ID"));
                    } else {
                        eintrag = Integer.toString(resultBuch.getInt("BUCH_ID"));
                    }
                    eintrag = eintrag + ", "
                            + resultBuch.getString("BUCH_ISBN") + ", "
                            + resultBuch.getString("BUCH_TITEL");
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
                Modulhelferlein.Fehlermeldung("Rezensionen versenden", "Löschen: SQL-Exception: ", exept.getMessage());
            }
        }
    }//GEN-LAST:event_LoeschenActionPerformed

    private void SchliessenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SchliessenActionPerformed
        // TODO add your handling code here:
        try {
            if (resultR != null) {
                resultR.close();
            }
        } catch (SQLException e) {
            Modulhelferlein.Fehlermeldung("Rezensionen versenden", "Dialog schließen: SQL-Exception: ", e.getMessage());
        }
        try {
            if (resultRD != null) {
                resultRD.close();
            }
        } catch (SQLException e) {
            Modulhelferlein.Fehlermeldung("Rezensionen versenden", "Dialog schließen: SQL-Exception: ", e.getMessage());
        }
        try {
            if (resultA != null) {
                resultA.close();
            }
        } catch (SQLException e) {
            Modulhelferlein.Fehlermeldung("Rezensionen versenden", "Dialog schließen: SQL-Exception: ", e.getMessage());
        }
        try {
            if (resultBNr != null) {
                resultBNr.close();
            }
        } catch (SQLException e) {
            Modulhelferlein.Fehlermeldung("Rezensionen versenden", "Dialog schließen: SQL-Exception: ", e.getMessage());
        }
        try {
            if (resultBuch != null) {
                resultBuch.close();
            }
        } catch (SQLException e) {
            Modulhelferlein.Fehlermeldung("Rezensionen versenden", "Dialog schließen: SQL-Exception: ", e.getMessage());
        }
        try {
            if (SQLAnfrageR != null) {
                SQLAnfrageR.close();
            }
        } catch (SQLException e) {
            Modulhelferlein.Fehlermeldung("Rezensionen versenden", "Dialog schließen: SQL-Exception: ", e.getMessage());
        }
        try {
            if (SQLAnfrageRD != null) {
                SQLAnfrageRD.close();
            }
        } catch (SQLException e) {
            Modulhelferlein.Fehlermeldung("Rezensionen versenden", "Dialog schließen: SQL-Exception: ", e.getMessage());
        }
        try {
            if (SQLAnfrageA != null) {
                SQLAnfrageA.close();
            }
        } catch (SQLException e) {
            Modulhelferlein.Fehlermeldung("Rezensionen versenden", "Dialog schließen: SQL-Exception: ", e.getMessage());
        }
        try {
            if (SQLAnfrageBNr != null) {
                SQLAnfrageBNr.close();
            }
        } catch (SQLException e) {
            Modulhelferlein.Fehlermeldung("Rezensionen versenden", "Dialog schließen: SQL-Exception: ", e.getMessage());
        }
        try {
            if (SQLAnfrageBuch != null) {
                SQLAnfrageBuch.close();
            }
        } catch (SQLException e) {
            Modulhelferlein.Fehlermeldung("Rezensionen versenden", "Dialog schließen: SQL-Exception: ", e.getMessage());
        }
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            Modulhelferlein.Fehlermeldung("Rezensionen versenden", "Dialog schließen: SQL-Exception: ", e.getMessage());
        }
        this.dispose();
    }//GEN-LAST:event_SchliessenActionPerformed

    private void ZurueckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ZurueckActionPerformed
        // TODO add your handling code here:
        try {
            if (resultR.previous()) {
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
                resultR.next();
            }
            switch (resultR.getInt("REZENSIONEN_AUS_TYP")) {
                case 0:
                    rbNachfrage.setSelected(true);
                    break;
                case 1:
                    rbAutor.setSelected(true);
                    break;
                case 3:
                    rbInitiative.setSelected(true);
                    break;
                case 5:
                    rbDritte.setSelected(true);
                    break;
            }
            field_Dritte.setText(resultR.getString("REZENSIONEN_AUS_DRITTE"));
            field_NachfrageDatum.setDate(Modulhelferlein.Date2SQLDate(resultR.getDate("REZENSIONEN_AUS_DATUM")));
            if (resultR.getInt("REZENSIONEN_AUS_REZENSENT") > 0) {
                resultA = SQLAnfrageA.executeQuery("SELECT * FROM tbl_adresse WHERE ADRESSEN_ID = '" + Integer.toString(resultR.getInt("REZENSIONEN_AUS_REZENSENT")) + "'");
                resultA.next();
                cbRezensent.setSelectedItem(Integer.toString(resultR.getInt("REZENSIONEN_AUS_REZENSENT")) + ", " + resultA.getString("ADRESSEN_NAME") + ", " + resultA.getString("ADRESSEN_VORNAME"));
            } else {
                cbRezensent.setSelectedItem("0, ----------, -----------");
            }
            if (resultR.getInt("REZENSIONEN_AUS_ZEITSCHRIFT") > 0) {
                resultA = SQLAnfrageA.executeQuery("SELECT * FROM tbl_adresse WHERE ADRESSEN_ID = '" + Integer.toString(resultR.getInt("REZENSIONEN_AUS_ZEITSCHRIFT")) + "'");
                resultA.next();
                cbZeitschrift.setSelectedItem(Integer.toString(resultA.getInt("ADRESSEN_ID")) + ", " + resultA.getString("ADRESSEN_ZEITSCHRIFT"));
            } else {
                cbZeitschrift.setSelectedItem("0, ---------------------");
            }

            // Bestellungdetails lesen   
            resultRD = SQLAnfrageRD.executeQuery(
                    "SELECT * FROM TBL_REZENSIONEN_AUS_DETAIL WHERE REZENSIONEN_AUS_DETAIL_NUMMER = '" + resultR.getString("REZENSIONEN_AUS_NUMMER") + "'"); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 
            countBMax = 0;
            while (resultRD.next()) {
                ++countBMax;
            }
            resultRD.first();
            field_B_CountMax.setText(Integer.toString(countBMax));
            if (countBMax == 0) {
                // Schalterzustände setzen
                countB = 0;
                field_B_Count.setText(Integer.toString(countB));
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
                resultBuch = SQLAnfrageBuch.executeQuery("SELECT * FROM TBL_BUCH WHERE BUCH_ID = '" + resultRD.getString("REZENSIONEN_AUS_DETAIL_BUCH") + "'");
                resultBuch.next();
                String eintrag = "";
                if (resultBuch.getInt("BUCH_ID") < 10) {
                    eintrag = "00" + Integer.toString(resultBuch.getInt("BUCH_ID"));
                } else if (resultBuch.getInt("BUCH_ID") < 100) {
                    eintrag = "0" + Integer.toString(resultBuch.getInt("BUCH_ID"));
                } else {
                    eintrag = Integer.toString(resultBuch.getInt("BUCH_ID"));
                }
                eintrag = eintrag + ", "
                        + resultBuch.getString("BUCH_ISBN") + ", "
                        + resultBuch.getString("BUCH_TITEL");
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
            Modulhelferlein.Fehlermeldung("Rezensionen versenden", "zum vorherigen: SQL-Exception: ", exept.getMessage());
        }
    }//GEN-LAST:event_ZurueckActionPerformed

    private void VorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_VorActionPerformed
        // TODO add your handling code here:
        try {
            if (resultR.next()) {
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
                resultR.previous();
            }
            switch (resultR.getInt("REZENSIONEN_AUS_TYP")) {
                case 0:
                    rbNachfrage.setSelected(true);
                    break;
                case 1:
                    rbAutor.setSelected(true);
                    break;
                case 3:
                    rbInitiative.setSelected(true);
                    break;
                case 5:
                    rbDritte.setSelected(true);
                    break;
            }
            field_Dritte.setText(resultR.getString("REZENSIONEN_AUS_DRITTE"));
            field_NachfrageDatum.setDate(Modulhelferlein.Date2SQLDate(resultR.getDate("REZENSIONEN_AUS_DATUM")));

            if (resultR.getInt("REZENSIONEN_AUS_REZENSENT") > 0) {
                resultA = SQLAnfrageA.executeQuery("SELECT * FROM tbl_adresse WHERE ADRESSEN_ID = '" + Integer.toString(resultR.getInt("REZENSIONEN_AUS_REZENSENT")) + "'");
                resultA.next();
                cbRezensent.setSelectedItem(Integer.toString(resultR.getInt("REZENSIONEN_AUS_REZENSENT")) + ", " + resultA.getString("ADRESSEN_NAME") + ", " + resultA.getString("ADRESSEN_VORNAME"));
            } else {
                cbRezensent.setSelectedItem("0, ----------, -----------");
            }
            if (resultR.getInt("REZENSIONEN_AUS_ZEITSCHRIFT") > 0) {
                resultA = SQLAnfrageA.executeQuery("SELECT * FROM tbl_adresse WHERE ADRESSEN_ID = '" + Integer.toString(resultR.getInt("REZENSIONEN_AUS_ZEITSCHRIFT")) + "'");
                resultA.next();
                cbZeitschrift.setSelectedItem(Integer.toString(resultA.getInt("ADRESSEN_ID")) + ", " + resultA.getString("ADRESSEN_ZEITSCHRIFT"));
            } else {
                cbZeitschrift.setSelectedItem("0, ---------------------");
            }

            // Bestellungdetails lesen 
            resultRD = SQLAnfrageRD.executeQuery(
                    "SELECT * FROM TBL_REZENSIONEN_AUS_DETAIL WHERE REZENSIONEN_AUS_DETAIL_NUMMER = '" + resultR.getString("REZENSIONEN_AUS_NUMMER") + "'");
            countBMax = 0;
            while (resultRD.next()) {
                ++countBMax;
            }
            resultRD.first();
            field_B_CountMax.setText(Integer.toString(countBMax));
            if (countBMax == 0) {
                // Schalterzustände setzen
                countB = 0;
                field_B_Count.setText(Integer.toString(countB));
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
                resultBuch = SQLAnfrageBuch.executeQuery("SELECT * FROM TBL_BUCH WHERE BUCH_ID = '" + resultRD.getString("REZENSIONEN_AUS_DETAIL_BUCH") + "'");
                resultBuch.next();
                String eintrag = "";
                if (resultBuch.getInt("BUCH_ID") < 10) {
                    eintrag = "00" + Integer.toString(resultBuch.getInt("BUCH_ID"));
                } else if (resultBuch.getInt("BUCH_ID") < 100) {
                    eintrag = "0" + Integer.toString(resultBuch.getInt("BUCH_ID"));
                } else {
                    eintrag = Integer.toString(resultBuch.getInt("BUCH_ID"));
                }
                eintrag = eintrag + ", "
                        + resultBuch.getString("BUCH_ISBN") + ", "
                        + resultBuch.getString("BUCH_TITEL");
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
            Modulhelferlein.Fehlermeldung("Rezensionen versenden", "zum nächsten: SQL-Exception: ", exept.getMessage());
        }
    }//GEN-LAST:event_VorActionPerformed

    private void EndeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EndeActionPerformed
        // TODO add your handling code here:
        try {
            resultR.last();
            count = countMax;
            field_count.setText(Integer.toString(count));
            switch (resultR.getInt("REZENSIONEN_AUS_TYP")) {
                case 0:
                    rbNachfrage.setSelected(true);
                    break;
                case 1:
                    rbAutor.setSelected(true);
                    break;
                case 3:
                    rbInitiative.setSelected(true);
                    break;
                case 5:
                    rbDritte.setSelected(true);
                    break;
            }
            field_Dritte.setText(resultR.getString("REZENSIONEN_AUS_DRITTE"));
            field_NachfrageDatum.setDate(Modulhelferlein.Date2SQLDate(resultR.getDate("REZENSIONEN_AUS_DATUM")));
            if (resultR.getInt("REZENSIONEN_AUS_REZENSENT") > 0) {
                resultA = SQLAnfrageA.executeQuery("SELECT * FROM tbl_adresse WHERE ADRESSEN_ID = '" + Integer.toString(resultR.getInt("REZENSIONEN_AUS_REZENSENT")) + "'");
                resultA.next();
                cbRezensent.setSelectedItem(Integer.toString(resultR.getInt("REZENSIONEN_AUS_REZENSENT")) + ", " + resultA.getString("ADRESSEN_NAME") + ", " + resultA.getString("ADRESSEN_VORNAME"));
            } else {
                cbRezensent.setSelectedItem("0, ----------, -----------");
            }
            if (resultR.getInt("REZENSIONEN_AUS_ZEITSCHRIFT") > 0) {
                resultA = SQLAnfrageA.executeQuery("SELECT * FROM tbl_adresse WHERE ADRESSEN_ID = '" + Integer.toString(resultR.getInt("REZENSIONEN_AUS_ZEITSCHRIFT")) + "'");
                resultA.next();
                cbZeitschrift.setSelectedItem(Integer.toString(resultA.getInt("ADRESSEN_ID")) + ", " + resultA.getString("ADRESSEN_ZEITSCHRIFT"));
            } else {
                cbZeitschrift.setSelectedItem("0, ---------------------");
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
            resultRD = SQLAnfrageRD.executeQuery(
                    "SELECT * FROM TBL_REZENSIONEN_AUS_DETAIL WHERE REZENSIONEN_AUS_DETAIL_NUMMER = '" + resultR.getString("REZENSIONEN_AUS_NUMMER") + "'"); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 
            countBMax = 0;
            while (resultRD.next()) {
                ++countBMax;
            }
            resultRD.first();
            field_B_CountMax.setText(Integer.toString(countBMax));
            if (countBMax == 0) {
                // Schalterzustände setzen
                countB = 0;
                field_B_Count.setText(Integer.toString(countB));
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
                resultBuch = SQLAnfrageBuch.executeQuery("SELECT * FROM TBL_BUCH WHERE BUCH_ID = '" + resultRD.getString("REZENSIONEN_AUS_DETAIL_BUCH") + "'");
                resultBuch.next();
                String eintrag = "";
                if (resultBuch.getInt("BUCH_ID") < 10) {
                    eintrag = "00" + Integer.toString(resultBuch.getInt("BUCH_ID"));
                } else if (resultBuch.getInt("BUCH_ID") < 100) {
                    eintrag = "0" + Integer.toString(resultBuch.getInt("BUCH_ID"));
                } else {
                    eintrag = Integer.toString(resultBuch.getInt("BUCH_ID"));
                }
                eintrag = eintrag + ", "
                        + resultBuch.getString("BUCH_ISBN") + ", "
                        + resultBuch.getString("BUCH_TITEL");
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
            Modulhelferlein.Fehlermeldung("Rezensionen versenden", "zum Ende: SQL-Exception: ", exept.getMessage());
        }
    }//GEN-LAST:event_EndeActionPerformed

    private void DruckenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DruckenActionPerformed
        // TODO add your handling code here:
        BUpdateActionPerformed(evt);
        UpdateActionPerformed(evt);

// Modulhelferlein.Infomeldung("Aufruf Brief Rechnung für RechNr " + field_RechNr.getText());
        Integer Typ = 0;

        String Kunde[] = cbRezensent.getSelectedItem().toString().split(",");

        if (rbNachfrage.isSelected()) {
            Typ = 0;
        } else if (rbAutor.isSelected()) {
            Typ = 1;
        } else if (rbInitiative.isSelected()) {
            Typ = 3;
        } else if (rbFreiexemplar.isSelected()) {
            Typ = 4;
        } else {
            Typ = 5;
        }
        try {
            String[] Kriterien = {"PDF", "DOC"};
            String Kriterium = (String) JOptionPane.showInputDialog(null,
                    "Drucken",
                    "In welchem Format soll gedruckt werden?",
                    JOptionPane.QUESTION_MESSAGE,
                    null, Kriterien,
                    Kriterien[0]);

            if (Kriterium != null) {
                if (rbBestellung.isSelected()) {
                    if (Kriterium.equals("PDF")) {
                        briefRechnungMahnung.briefPDF(makePseudoBestellung(resultR.getString("REZENSIONEN_AUS_NUMMER"),
                                Integer.parseInt(Kunde[0]),
                                resultR.getDate("REZENSIONEN_AUS_DATUM")),
                                1,
                                "",
                                0);
                        briefRezension.brief2PDF(Typ, resultR.getString("REZENSIONEN_AUS_NUMMER"));
                    } else {
                        briefRechnungMahnung.briefDOC(makePseudoBestellung(resultR.getString("REZENSIONEN_AUS_NUMMER"),
                                Integer.parseInt(Kunde[0]),
                                resultR.getDate("REZENSIONEN_AUS_DATUM")),
                                1,
                                "");
                        briefRezension.brief2DOC(Typ, resultR.getString("REZENSIONEN_AUS_NUMMER"));
                    }
                } else {
                    if (Kriterium.equals("PDF")) {
                        briefRezension.brief2PDF(Typ, resultR.getString("REZENSIONEN_AUS_NUMMER"));
                    } else {
                        briefRezension.brief2DOC(Typ, resultR.getString("REZENSIONEN_AUS_NUMMER"));
                    }
                }
            }
        } catch (Exception e) {
            Modulhelferlein.Fehlermeldung("Rezensionen versenden", "Bestellung drucken: Exception: ", e.getMessage());
        }
    }//GEN-LAST:event_DruckenActionPerformed

    private void rbNachfrageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbNachfrageActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_rbNachfrageActionPerformed

    private void rbAutorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbAutorActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_rbAutorActionPerformed

    private void rbInitiativeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbInitiativeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rbInitiativeActionPerformed

    private void cbRezensentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbRezensentActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbRezensentActionPerformed

    private void BLoeschenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BLoeschenActionPerformed
        // TODO add your handling code here:
        if (JOptionPane.showConfirmDialog(null, "Soll der Datensatz wirklich gelöscht werden?") == JOptionPane.YES_OPTION) {
            try {
                resultRD.deleteRow();
                countBMax = countBMax - 1;
                field_B_CountMax.setText(Integer.toString(countBMax));
                resultRD.first();
                countB = 1;
                field_B_Count.setText(Integer.toString(countB));
                if (resultRD.getRow() > 0) {
                    // Schalterzustand anpassen
                    BAnfang.setEnabled(true);
                    BZurueck.setEnabled(true);
                    BVor.setEnabled(true);
                    BEnde.setEnabled(true);
                    BUpdate.setEnabled(true);
                    BEinfuegen.setEnabled(true);
                    BLoeschen.setEnabled(true);

                    // Felder füllen
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
                }
            } catch (SQLException exept) {
                Modulhelferlein.Fehlermeldung("Rezensionen versenden", "Löschen: SQL-Exception: ", exept.getMessage());
            }
        }
    }//GEN-LAST:event_BLoeschenActionPerformed

    private void BEinfuegenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BEinfuegenActionPerformed
        // TODO add your handling code here:
//        int ID;
//        ResultSet rb;
//        Statement sb;

//        ID = maxBID + 1;
        maxBID = maxBID + 1;

        try {
//            sb = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
//            rb = sb.executeQuery("SELECT * FROM TBL_REZENSIONEN_AUS_DETAIL ORDER BY REZENSIONEN_AUS_DETAIL_ID DESC");
//            if (rb.first()) {
//                ID = rb.getInt("REZENSIONEN_AUS_DETAIL_ID") + 1;
//            } else {
//                ID = 1;
//            }

            //if (resultBIsEmpty) {
            //    ID = rb.getInt("REZENSIONEN_AUS_DETAIL_ID") + 1;
            //} else {
            //    resultRD.last();
            //    ID = 1 + resultRD.getInt("REZENSIONEN_AUS_DETAIL_ID");
            //}
            resultRD.moveToInsertRow();
            resultRD.updateInt("REZENSIONEN_AUS_DETAIL_ID", maxBID);
            resultRD.updateInt("REZENSIONEN_AUS_DETAIL_BUCH", 0);
            resultRD.updateString("REZENSIONEN_AUS_DETAIL_NUMMER", resultR.getString("REZENSIONEN_AUS_NUMMER"));
            resultRD.insertRow();

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

            resultRD.last();

            // Felder füllen
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("Rezensionen versenden", "Details Einfügen: SQL-Exception: ", exept.getMessage());
        }
    }//GEN-LAST:event_BEinfuegenActionPerformed

    private void BUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BUpdateActionPerformed
        // TODO add your handling code here:
        try {
            String buch[] = cbBuch.getItemAt(cbBuch.getSelectedIndex()).split(",");
            resultRD.updateInt("REZENSIONEN_AUS_DETAIL_BUCH", Integer.parseInt(buch[0]));
            resultRD.updateRow();

        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("Rezensionen versenden", "Details Update: SQL-Exception: ", exept.getMessage());
        }
    }//GEN-LAST:event_BUpdateActionPerformed

    private void BEndeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BEndeActionPerformed
        // TODO add your handling code here:
        try {
            resultRD.last();
            countB = countBMax;
            field_B_Count.setText(Integer.toString(countB));
            // Felder füllen
            resultBuch = SQLAnfrageBuch.executeQuery("SELECT * FROM TBL_BUCH WHERE BUCH_ID = '" + resultRD.getString("REZENSIONEN_AUS_DETAIL_BUCH") + "'");
            resultBuch.next();
            String eintrag = "";
            if (resultBuch.getInt("BUCH_ID") < 10) {
                eintrag = "00" + Integer.toString(resultBuch.getInt("BUCH_ID"));
            } else if (resultBuch.getInt("BUCH_ID") < 100) {
                eintrag = "0" + Integer.toString(resultBuch.getInt("BUCH_ID"));
            } else {
                eintrag = Integer.toString(resultBuch.getInt("BUCH_ID"));
            }
            eintrag = eintrag + ", "
                    + resultBuch.getString("BUCH_ISBN") + ", "
                    + resultBuch.getString("BUCH_TITEL");
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
            Modulhelferlein.Fehlermeldung("Rezensionen versenden", "Details zum Ende: SQL-Exception: ", exept.getMessage());
        }
    }//GEN-LAST:event_BEndeActionPerformed

    private void BVorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BVorActionPerformed
        // TODO add your handling code here:
        try {
            if (resultRD.next()) {
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
                resultRD.previous();
            }
            // Felder füllen
            resultBuch = SQLAnfrageBuch.executeQuery("SELECT * FROM TBL_BUCH WHERE BUCH_ID = '" + resultRD.getString("REZENSIONEN_AUS_DETAIL_BUCH") + "'");
            resultBuch.next();
            String eintrag = "";
            if (resultBuch.getInt("BUCH_ID") < 10) {
                eintrag = "00" + Integer.toString(resultBuch.getInt("BUCH_ID"));
            } else if (resultBuch.getInt("BUCH_ID") < 100) {
                eintrag = "0" + Integer.toString(resultBuch.getInt("BUCH_ID"));
            } else {
                eintrag = Integer.toString(resultBuch.getInt("BUCH_ID"));
            }
            eintrag = eintrag + ", "
                    + resultBuch.getString("BUCH_ISBN") + ", "
                    + resultBuch.getString("BUCH_TITEL");
            cbBuch.setSelectedItem(eintrag);
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("Rezensionen versenden", "Details zum nächsten: SQL-Exception: ", exept.getMessage());
        }
    }//GEN-LAST:event_BVorActionPerformed

    private void BZurueckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BZurueckActionPerformed
        // TODO add your handling code here:
        try {
//            if (resultRD.previous()) {
            if (countB > 1) {
                resultRD.previous();
                countB = countB - 1;
                field_B_Count.setText(Integer.toString(countB));
                // Schalterzustand anpassen
                if (resultRD.getRow() > 1) {
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
//                resultRD.next();
                BAnfang.setEnabled(false);
                BZurueck.setEnabled(false);
                BVor.setEnabled(true);
                BEnde.setEnabled(true);
                BUpdate.setEnabled(true);
                BEinfuegen.setEnabled(true);
                BLoeschen.setEnabled(true);
            }
            // Felder füllen
            resultBuch = SQLAnfrageBuch.executeQuery("SELECT * FROM TBL_BUCH WHERE BUCH_ID = '" + resultRD.getString("REZENSIONEN_AUS_DETAIL_BUCH") + "'");
            resultBuch.next();
            String eintrag = "";
            if (resultBuch.getInt("BUCH_ID") < 10) {
                eintrag = "00" + Integer.toString(resultBuch.getInt("BUCH_ID"));
            } else if (resultBuch.getInt("BUCH_ID") < 100) {
                eintrag = "0" + Integer.toString(resultBuch.getInt("BUCH_ID"));
            } else {
                eintrag = Integer.toString(resultBuch.getInt("BUCH_ID"));
            }
            eintrag = eintrag + ", "
                    + resultBuch.getString("BUCH_ISBN") + ", "
                    + resultBuch.getString("BUCH_TITEL");
            cbBuch.setSelectedItem(eintrag);
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("Rezensionen versenden", "Details zum vorherigen SQL-Exception: ", exept.getMessage());
        }
    }//GEN-LAST:event_BZurueckActionPerformed

    private void BAnfangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BAnfangActionPerformed
        // TODO add your handling code here:
        try {
            resultRD.first();
            countB = 1;
            field_B_Count.setText(Integer.toString(countB));

            // Felder füllen
            resultBuch = SQLAnfrageBuch.executeQuery("SELECT * FROM TBL_BUCH WHERE BUCH_ID = '" + resultRD.getString("REZENSIONEN_AUS_DETAIL_BUCH") + "'");
            resultBuch.next();
            String eintrag = "";
            if (resultBuch.getInt("BUCH_ID") < 10) {
                eintrag = "00" + Integer.toString(resultBuch.getInt("BUCH_ID"));
            } else if (resultBuch.getInt("BUCH_ID") < 100) {
                eintrag = "0" + Integer.toString(resultBuch.getInt("BUCH_ID"));
            } else {
                eintrag = Integer.toString(resultBuch.getInt("BUCH_ID"));
            }
            eintrag = eintrag + ", "
                    + resultBuch.getString("BUCH_ISBN") + ", "
                    + resultBuch.getString("BUCH_TITEL");
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
            Modulhelferlein.Fehlermeldung("Rezensionen versenden", "Details zum Anfang: SQL-Exception: ", exept.getMessage());
        }
    }//GEN-LAST:event_BAnfangActionPerformed

    private void cbZeitschriftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbZeitschriftActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbZeitschriftActionPerformed

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
            VerwaltenDatenbankRezensionAus dialog = new VerwaltenDatenbankRezensionAus(new javax.swing.JFrame(), true);
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
    private JLabel jLabel5;
    private JTextField field_Nummer;
    private JComboBox<String> cbRezensent;
    private JLabel jLabel25;
    private JLabel jLabel13;
    private JRadioButton rbInitiative;
    private JLabel jLabel12;
    private JRadioButton rbNachfrage;
    private JLabel jLabel7;
    private JComboBox<String> cbZeitschrift;
    private JRadioButton rbAutor;
    private JDateChooser field_NachfrageDatum;
    private JRadioButton rbDritte;
    private JCheckBox rbBestellung;
    private JTextField field_Dritte;
    private JRadioButton rbFreiexemplar;
    private JLabel jLabel21;
    private JComboBox<String> cbBuch;
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
    private JButton Anfang;
    private JButton Zurueck;
    private JButton Vor;
    private JButton Update;
    private JButton Ende;
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
    Statement SQLAnfrageR;
    Statement SQLAnfrageRD;
    Statement SQLAnfrageA;
    Statement SQLAnfrageBNr;
    Statement SQLAnfrageBuch;
    ResultSet resultR;
    ResultSet resultRD;
    ResultSet resultA;
    ResultSet resultBNr;
    ResultSet resultBuch;

    boolean resultIsEmpty = true;
    boolean resultBIsEmpty = true;

    int countB = 0;
    int countBMax = 0;

    int maxID = 0;
    int maxBID = 0;

    java.util.Date CurDate = new java.util.Date(); // das aktuelle Datum

    ButtonGroup buttonGroupTyp = new ButtonGroup();

}
