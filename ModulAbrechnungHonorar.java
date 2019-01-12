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

//~--- non-JDK imports --------------------------------------------------------
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Thomas Zimmermann
 */
public class ModulAbrechnungHonorar {

    static Connection conn = null;

    private static Statement SQLHonorar = null;
    private static Statement SQLBuch = null;
    private static Statement SQLBestellungDetails = null;
    private static Statement SQLAutor = null;
    private static Statement SQLKunde = null;
    private static Statement SQLBestellung = null;
    private static Statement SQLBestellung2 = null;
    private static Statement SQLVerrechnung = null;

    private static ResultSet resultBuch = null;
    private static ResultSet resultAutor = null;
    private static ResultSet resultKunde = null;
    private static ResultSet resultBestellung = null;
    private static ResultSet resultBestellung2 = null;
    private static ResultSet resultHonorar = null;
    private static ResultSet resultBestellungDetails = null;
    private static ResultSet resultVerrechnung = null;

    private static Integer ID = 0;
    private static Integer zeile = 0;
    private static String Filename = "";
    private static String strVon = "";
    private static String strBis = "";

    private static FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV-Datei", "csv");
    private static JFileChooser chooser = new JFileChooser(new File(System.getProperty("user.dir")));

    private static String strISBN = "";
    private static String strAnzahl = "";

    private static Float Rechnungsbetrag = 0F;
    private static Float Betrag19 = 0F;  // Bruttobetrag 19%
    private static Float Betrag7 = 0F;   // Bruttobetrag  7%

    public static void honorar() {

        chooser.setMultiSelectionEnabled(false);
        chooser.setFileFilter(filter);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int Ergebnis = chooser.showDialog(null, "SALES.CSV w�hlen");

        if (Ergebnis == JFileChooser.APPROVE_OPTION) {
            try {                   // Datenbank-Treiber laden
                Class.forName(Modulhelferlein.dbDriver);
            } catch (ClassNotFoundException exept) {
                Modulhelferlein.Fehlermeldung("Honorar berechnen", "ClassNotFoundException: Treiber nicht gefunden: ", exept.getMessage());
            } // try Datenbank-Treiber laden

            try {                   // Verbindung zur Datenbank �ber die JDBC-Br�cke
                conn = DriverManager.getConnection(Modulhelferlein.dbUrl, Modulhelferlein.dbUser, Modulhelferlein.dbPassword);
            } catch (SQLException exept) {
                Modulhelferlein.Fehlermeldung("Honorar berechnen", "SQL-Exception: Verbindung nicht moeglich: ", exept.getMessage());
            }                       // try Verbindung zur Datenbank �ber die JDBC-Br�cke

            if (conn != null) {    // Datenbankverbindung steht
                try {
                    SQLBestellungDetails = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    SQLBestellung = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    SQLBestellung2 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    SQLKunde = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    SQLVerrechnung = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    Filename = chooser.getSelectedFile().getCanonicalPath();
// Tabelle Honorar leeren   
                    System.out.println("Tabelle Honorar leeren ...");
                    SQLHonorar = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    SQLHonorar.executeUpdate("DELETE FROM TBL_HONORAR");
                    System.out.println("-> Tabelle Honorar ist geleert");

// Tabelle Honorar erstellen 
                    SQLBuch = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    resultBuch = SQLBuch.executeQuery("SELECT * FROM TBL_BUCH WHERE BUCH_ID > '0'");
                    ID = 1;
                    System.out.println("Tabelle Honorar erstellen");
                    while (resultBuch.next()) {
                        String Titel = resultBuch.getString("BUCH_TITEL");
                        Titel = Titel.replace(",", " ");
                        Titel = Titel.replace("+", " ");
                        Titel = Titel.replace("/", " ");
                        Titel = Titel.replace("?", " ");
                        Titel = Titel.replace("!", " ");
                        Titel = Titel.replace("\"", " ");
                        Titel = Titel.replace("'", " ");
                        Titel = Titel.replace("*", " ");
                        Titel = Titel.replace(";", " ");
                        int c = 0;
                        c = 0x0027;
                        Titel = Titel.replace(Character.toString((char) c), " "); // '
                        c = 0x0022;
                        Titel = Titel.replace(Character.toString((char) c), " "); // 
                        c = 0x2018;
                        Titel = Titel.replace(Character.toString((char) c), " "); // '
                        c = 0x2019;
                        Titel = Titel.replace(Character.toString((char) c), " "); // '
                        c = 0x003F;
                        Titel = Titel.replace(Character.toString((char) c), " "); // 
                        c = 0x002A;
                        Titel = Titel.replace(Character.toString((char) c), " "); // *
                        c = 0x0021;
                        Titel = Titel.replace(Character.toString((char) c), " "); // !
                        c = 0x005C;
                        Titel = Titel.replace(Character.toString((char) c), " "); // \
                        c = 0x002F;
                        Titel = Titel.replace(Character.toString((char) c), " "); // /
                        c = 0x002B;
                        Titel = Titel.replace(Character.toString((char) c), " "); // +
                        c = 0x002C;
                        Titel = Titel.replace(Character.toString((char) c), " "); // ,
                        c = 0x003B;
                        Titel = Titel.replace(Character.toString((char) c), " "); // ;
                        c = 0x0060;
                        Titel = Titel.replace(Character.toString((char) c), " "); // ,
                        c = 0x003A;
                        Titel = Titel.replace(Character.toString((char) c), " "); // ,
                        c = 0x00B4;
                        Titel = Titel.replace(Character.toString((char) c), " "); // ,
                        // Titel = Normalisiere(Titel);
                        resultHonorar = SQLHonorar.executeQuery("SELECT * FROM TBL_HONORAR"
                                + " WHERE HONORAR_TITEL = '" + Titel + "'");
                        if (resultHonorar.next()) { // der Titel existiert bereits in der Honorardatenbank
                            switch (resultBuch.getInt("BUCH_HC")) {
                                case 1: // HC
                                    resultHonorar.updateString("HONORAR_ISBN_HC", resultBuch.getString("BUCH_ISBN"));
                                    resultHonorar.updateFloat("HONORAR_PREIS_HC", resultBuch.getFloat("BUCH_PREIS"));
                                    System.out.println(".. Erg�nze Titel HC " + Titel);
                                    break;
                                case 0: // PB
                                    resultHonorar.updateString("HONORAR_ISBN_PB", resultBuch.getString("BUCH_ISBN"));
                                    resultHonorar.updateFloat("HONORAR_PREIS_PB", resultBuch.getFloat("BUCH_PREIS"));
                                    System.out.println(".. Erg�nze Titel PB " + Titel);
                                    break;
                                case 2: // EB
                                    resultHonorar.updateString("HONORAR_ISBN_EB", resultBuch.getString("BUCH_ISBN"));
                                    resultHonorar.updateFloat("HONORAR_PREIS_EB", resultBuch.getFloat("BUCH_PREIS"));
                                    System.out.println(".. Erg�nze Titel EB " + Titel);
                                    break;
                            }
                            resultHonorar.updateRow();
                        } else { // der Titel existiert noch nicht in der Honorardatenbank
                            System.out.println(".. Erg�nze Titel " + Titel);
                            resultHonorar.moveToInsertRow();
                            String Autoren = resultBuch.getString("BUCH_AUTOR") + ",0";
                            String[] Autor = Autoren.split(",");
                            resultHonorar.moveToInsertRow();
                            resultHonorar.updateInt("HONORAR_ID", ID);
                            resultHonorar.updateInt("HONORAR_ZAHLEN", 0);
                            resultHonorar.updateString("HONORAR_TITEL", Titel);
                            resultHonorar.updateBoolean("HONORAR_VEREINBART", resultBuch.getBoolean("BUCH_HONORAR"));
                            resultHonorar.updateInt("HONORAR_ANZAHL_1", resultBuch.getInt("BUCH_HONORAR_ANZAHL"));
                            resultHonorar.updateInt("HONORAR_PROZENT_1", resultBuch.getInt("BUCH_HONORAR_PROZENT"));
                            resultHonorar.updateInt("HONORAR_ANZAHL_2", resultBuch.getInt("BUCH_HONORAR_2_ANZAHL"));
                            resultHonorar.updateInt("HONORAR_PROZENT_2", resultBuch.getInt("BUCH_HONORAR_2_PROZENT"));
                            resultHonorar.updateString("HONORAR_ISBN_PB", "");
                            resultHonorar.updateInt("HONORAR_ANZAHL_PB", 0);
                            resultHonorar.updateFloat("HONORAR_PREIS_PB", 0F);
                            resultHonorar.updateString("HONORAR_ISBN_HC", "");
                            resultHonorar.updateInt("HONORAR_ANZAHL_HC", 0);
                            resultHonorar.updateFloat("HONORAR_PREIS_HC", 0F);
                            resultHonorar.updateString("HONORAR_ISBN_EB", "");
                            resultHonorar.updateFloat("HONORAR_PREIS_EB", 0F);
                            resultHonorar.updateInt("HONORAR_ANZAHL_EB", 0);
                            switch (resultBuch.getInt("BUCH_HC")) {
                                case 1: // HC
                                    resultHonorar.updateString("HONORAR_ISBN_HC", resultBuch.getString("BUCH_ISBN"));
                                    resultHonorar.updateFloat("HONORAR_PREIS_HC", resultBuch.getFloat("BUCH_PREIS"));
                                    break;
                                case 0: // PB
                                    resultHonorar.updateString("HONORAR_ISBN_PB", resultBuch.getString("BUCH_ISBN"));
                                    resultHonorar.updateFloat("HONORAR_PREIS_PB", resultBuch.getFloat("BUCH_PREIS"));
                                    break;
                                case 2: // EB
                                    resultHonorar.updateString("HONORAR_ISBN_EB", resultBuch.getString("BUCH_ISBN"));
                                    resultHonorar.updateFloat("HONORAR_PREIS_EB", resultBuch.getFloat("BUCH_PREIS"));
                                    break;
                            }
                            if (Autor[1].equals("0")) {
                                resultHonorar.updateBoolean("HONORAR_VERTEILEN", false);
                            } else {
                                resultHonorar.updateBoolean("HONORAR_VERTEILEN", true);
                            }
                            resultHonorar.updateInt("HONORAR_AUTOR_1", Integer.parseInt(Autor[0]));
                            resultHonorar.updateInt("HONORAR_AUTOR_2", Integer.parseInt(Autor[1]));
                            resultHonorar.insertRow();
                            ID = ID + 1;
                        } // Datensatz vorhanden
                    } // while Buch
                    System.out.println("-> Honorardatenbank erstellt");

// Honorardatenbank f�llen                    
                    System.out.println("F�llen der Honorardatenbank mit BoD-Sales");
                    Integer AnzahlGesamt = 0;
                    BufferedReader in = new BufferedReader(new FileReader(Filename));
                    String CSVzeileIn = null;
                    zeile = 1;
                    while ((CSVzeileIn = in.readLine()) != null) {
                        // Zeile lesen
                        String CSVZeile[] = CSVzeileIn.split(";");
                        switch (zeile) {
                            case 1: // Kopf mit Zeitraum
                                String Zeitraum = CSVZeile[0].substring(10, CSVZeile[0].length());
                                Zeitraum = Zeitraum.replace("-", ".");
                                strVon = Zeitraum.substring(0, 10);
                                strVon = strVon.replace(".", "-");
                                strBis = Zeitraum.substring(13, Zeitraum.length());
                                strBis = strBis.replace(".", "-");
                                break;
                            case 2: // Spaltenk�pfe
                                break;
                            default:
                                String ISBN = CSVZeile[1];

                                ISBN = ISBN.replace("-", "");
                                String sAnzahlGesamt = CSVZeile[4];
                                System.out.println(".. " + ISBN + " " + sAnzahlGesamt);
                                if (sAnzahlGesamt.contains(".")) {
                                    sAnzahlGesamt = sAnzahlGesamt.replace(".", "");
                                }
                                if (sAnzahlGesamt.contains(",")) {
                                    sAnzahlGesamt = sAnzahlGesamt.replace(",", ".");
                                }
                                AnzahlGesamt = Math.round(Float.parseFloat(sAnzahlGesamt));
                                // Buch in der Honorar-DB suchen und Daten erg�nzen
                                String SQL = "SELECT * FROM TBL_HONORAR "
                                        + "WHERE ("
                                        + "(HONORAR_ISBN_PB = '" + ISBN + "') OR "
                                        + "(HONORAR_ISBN_HC = '" + ISBN + "') OR "
                                        + "(HONORAR_ISBN_EB = '" + ISBN + "')"
                                        + ")";
                                resultHonorar = SQLHonorar.executeQuery(SQL);
                                if (resultHonorar.next()) {
                                    if (resultHonorar.getString("HONORAR_ISBN_PB").equals(ISBN)) {
                                        resultHonorar.updateInt("HONORAR_ANZAHL_PB", AnzahlGesamt);
                                    } else if (resultHonorar.getString("HONORAR_ISBN_HC").equals(ISBN)) {
                                        resultHonorar.updateInt("HONORAR_ANZAHL_HC", AnzahlGesamt);
                                    } else {
                                        resultHonorar.updateInt("HONORAR_ANZAHL_EB", AnzahlGesamt);
                                    }
                                    resultHonorar.updateRow();
                                } else { // warum auch immer existiert das Buch bisher nicht
                                    resultBuch = SQLBuch.executeQuery("SELECT * FROM TBL_BUCH WHERE BUCH_ISBN = '" + ISBN + "'");
                                    resultBuch.next();
                                    resultHonorar.moveToInsertRow();
                                    System.out.println("   -> erg�nze Datensatz mit ID " + Integer.toString(ID));
                                    resultHonorar.updateInt("HONORAR_ID", ID);
                                    resultHonorar.updateInt("HONORAR_ZAHLEN", 0);
                                    resultHonorar.updateString("HONORAR_TITEL", resultBuch.getString("BUCH_TITEL"));
                                    resultHonorar.updateBoolean("HONORAR_VEREINBART", resultBuch.getBoolean("BUCH_HONORAR"));
                                    resultHonorar.updateInt("HONORAR_ANZAHL_1", resultBuch.getInt("BUCH_HONORAR_ANZAHL"));
                                    resultHonorar.updateInt("HONORAR_PROZENT_1", resultBuch.getInt("BUCH_HONORAR_PROZENT"));
                                    resultHonorar.updateInt("HONORAR_ANZAHL_2", resultBuch.getInt("BUCH_HONORAR_2_ANZAHL"));
                                    resultHonorar.updateInt("HONORAR_PROZENT_2", resultBuch.getInt("BUCH_HONORAR_2_PROZENT"));
                                    resultHonorar.updateString("HONORAR_ISBN_PB", "");
                                    resultHonorar.updateInt("HONORAR_ANZAHL_PB", 0);
                                    resultHonorar.updateFloat("HONORAR_PREIS_PB", 0F);
                                    resultHonorar.updateString("HONORAR_ISBN_HC", "");
                                    resultHonorar.updateInt("HONORAR_ANZAHL_HC", 0);
                                    resultHonorar.updateFloat("HONORAR_PREIS_HC", 0F);
                                    resultHonorar.updateString("HONORAR_ISBN_EB", "");
                                    resultHonorar.updateInt("HONORAR_ANZAHL_EB", 0);
                                    resultHonorar.updateFloat("HONORAR_PREIS_PB", 0F);
                                    switch (resultBuch.getInt("BUCH_HC")) {
                                        case 0: // PB
                                            resultHonorar.updateString("HONORAR_ISBN_PB", resultBuch.getString("BUCH_ISBN"));
                                            resultHonorar.updateInt("HONORAR_ANZAHL_PB", AnzahlGesamt);
                                            resultHonorar.updateFloat("HONORAR_PREIS_PB", resultBuch.getFloat("BUCH_PREIS"));
                                            break;
                                        case 1: // HC
                                            resultHonorar.updateString("HONORAR_ISBN_HC", resultBuch.getString("BUCH_ISBN"));
                                            resultHonorar.updateInt("HONORAR_ANZAHL_HC", AnzahlGesamt);
                                            resultHonorar.updateFloat("HONORAR_PREIS_HC", resultBuch.getFloat("BUCH_PREIS"));
                                            break;
                                        case 2: // EB
                                            resultHonorar.updateString("HONORAR_ISBN_EB", resultBuch.getString("BUCH_ISBN"));
                                            resultHonorar.updateInt("HONORAR_ANZAHL_EB", AnzahlGesamt);
                                            resultHonorar.updateFloat("HONORAR_PREIS_EB", resultBuch.getFloat("BUCH_PREIS"));
                                            break;
                                    }
                                    String Autoren = resultBuch.getString("BUCH_AUTOR") + ",0";
                                    String[] Autor = Autoren.split(",");
                                    if (Autor[1].equals("0")) {
                                        resultHonorar.updateBoolean("HONORAR_VERTEILEN", false);
                                    } else {
                                        resultHonorar.updateBoolean("HONORAR_VERTEILEN", true);
                                    }
                                    resultHonorar.updateInt("HONORAR_AUTOR_1", Integer.parseInt(Autor[0]));
                                    resultHonorar.updateInt("HONORAR_AUTOR_2", Integer.parseInt(Autor[1]));
                                    resultHonorar.insertRow();
                                    ID = ID + 1;
                                }
                        }
                        zeile = zeile + 1;
                    }    // while CSV-einlesen
                    System.out.println("-> BoD-Sales eingelesen");

// Bestellungen erg�nzen im Zeitraum strVon strBis
                    System.out.println("F�llen der Honorardatenbank mit Miles-Verk�ufen ");
                    AnzahlGesamt = 0;
                    Integer AnzahlAutor = 0;
                    Integer AnzahlGeschenk = 0;

                    SQLAutor = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

                    SQLBuch = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    resultBuch = SQLBuch.executeQuery("SELECT * FROM TBL_BUCH WHERE BUCH_ID > '0'");

                    while (resultBuch.next()) {
                        AnzahlGesamt = 0;
                        AnzahlAutor = 0;
                        AnzahlGeschenk = 0;

                        // hole die Autorendaten
                        String ISBN = resultBuch.getString("BUCH_ISBN");
                        System.out.println(".. Berechne die Verk�ufe f�r " + ISBN + " " + resultBuch.getString("BUCH_TITEL"));
                        String[] AutorenIDs = resultBuch.getString("BUCH_AUTOR").split(",");
                        String Autor = "";
                        for (int i = 0; i < AutorenIDs.length; i++) {
                            resultAutor = SQLAutor.executeQuery("SELECT * FROM TBL_ADRESSE "
                                    + "WHERE ADRESSEN_ID = '"
                                    + AutorenIDs[i] + "'");
                            resultAutor.next();
                            Autor = Autor + resultAutor.getString("ADRESSEN_NAME") + ", "
                                    + resultAutor.getString("ADRESSEN_VORNAME") + "; ";
                        }
                        Autor = Autor.substring(0, Autor.length() - 2);

                        // hole alle Bestelldetails f�r BuchID
                        String SQL = "SELECT * FROM TBL_BESTELLUNG_DETAIL"
                                + " WHERE ((BESTELLUNG_DETAIL_BUCH = '"
                                + resultBuch.getString("BUCH_ID") + "') "
                                + "AND (('" + strVon + "' <= BESTELLUNG_DETAIL_DATUM) "
                                + "AND (BESTELLUNG_DETAIL_DATUM <= '" + strBis + "')))";
                        System.out.println("   -> " + SQL);
                        resultBestellungDetails = SQLBestellungDetails.executeQuery(SQL);

                        while (resultBestellungDetails.next()) { // Schleife gehe durch alle Bestelldetails
                            AnzahlGesamt = AnzahlGesamt + resultBestellungDetails.getInt("BESTELLUNG_DETAIL_ANZAHL");
                            String Bestellnummer = resultBestellungDetails.getString("BESTELLUNG_DETAIL_RECHNR");
                            // hole Bestellung mit Bestellnummer
                            SQL = "SELECT * FROM TBL_BESTELLUNG "
                                    + " WHERE BESTELLUNG_RECHNR = '" + Bestellnummer + "'";
                            System.out.println("      -> " + SQL);
                            resultBestellung = SQLBestellung.executeQuery(SQL);
                            resultBestellung.first();
                            // if Bestelltyp != Bestellung => AnzahlGeschenk = ANzahlGescehnk + 1
                            if (resultBestellung.getInt("BESTELLUNG_TYP") != 0) {
                                AnzahlGeschenk = AnzahlGeschenk + resultBestellungDetails.getInt("BESTELLUNG_DETAIL_ANZAHL");
                            } // if

                            // if Kunde = Teil vom Autor => ANzahlAutor = AnzahlAutor +1
                            if (resultBestellung.getInt("BESTELLUNG_KUNDE") > 0) {
                                resultKunde = SQLKunde.executeQuery("SELECT * FROM TBL_ADRESSE "
                                        + "WHERE ADRESSEN_ID = '"
                                        + resultBestellung.getString("BESTELLUNG_KUNDE") + "'");
                                resultKunde.next();
                                if (Autor.contains(resultKunde.getString("ADRESSEN_NAME"))) {
                                    AnzahlAutor = AnzahlAutor + resultBestellungDetails.getInt("BESTELLUNG_DETAIL_ANZAHL");
                                }
                            }
                        } // while resultBestellungDetails.next()

                        // update Honorardatenbank f�r Buch ISBN mit ISBN, Titel, Autoren, AnzahlGesamt, AnzahlAutor, AnzahlGeschenk, Preis * (AnzahlGesamt-AnzahlAutor)
                        resultHonorar = SQLHonorar.executeQuery("SELECT * FROM TBL_HONORAR "
                                + "WHERE ("
                                + "(HONORAR_ISBN_PB = '" + ISBN + "') OR "
                                + "(HONORAR_ISBN_HC = '" + ISBN + "') OR "
                                + "(HONORAR_ISBN_EB = '" + ISBN + "')"
                                + ")");
                        resultHonorar.next();
                        AnzahlGesamt = AnzahlGesamt - AnzahlAutor - AnzahlGeschenk;
                        if (resultHonorar.getString("HONORAR_ISBN_PB").equals(ISBN)) {
                            AnzahlGesamt = AnzahlGesamt + resultHonorar.getInt("HONORAR_ANZAHL_PB");
                            resultHonorar.updateInt("HONORAR_ANZAHL_PB", AnzahlGesamt);
                        } else if (resultHonorar.getString("HONORAR_ISBN_HC").equals(ISBN)) {
                            AnzahlGesamt = AnzahlGesamt + resultHonorar.getInt("HONORAR_ANZAHL_HC");
                            resultHonorar.updateInt("HONORAR_ANZAHL_HC", AnzahlGesamt);
                        } else {
                            AnzahlGesamt = AnzahlGesamt + resultHonorar.getInt("HONORAR_ANZAHL_EB");
                            resultHonorar.updateInt("HONORAR_ANZAHL_EB", AnzahlGesamt);
                        }
                        resultHonorar.updateRow();
                    } // while resultBuch.next()
                    System.out.println("-> Verk�ufe Miles-Verlag eingelesen");

// Honorartabelle auswerten
                    System.out.println("Honorardatenbank auswerten");
                    Integer Anz_PB = 0;
                    Integer Anz_HC = 0;
                    Integer Anz_EB = 0;
                    Integer Anzahl = 0;
                    Integer S1 = 0;
                    Integer S2 = 0;
                    resultHonorar = SQLHonorar.executeQuery("SELECT * FROM TBL_HONORAR ");
                    while (resultHonorar.next()) {
                        Anz_PB = resultHonorar.getInt("HONORAR_ANZAHL_PB");
                        Anz_HC = resultHonorar.getInt("HONORAR_ANZAHL_HC");
                        Anz_EB = resultHonorar.getInt("HONORAR_ANZAHL_EB");
                        Anzahl = Anz_PB + Anz_HC + Anz_EB;
                        S1 = resultHonorar.getInt("HONORAR_ANZAHL_1");
                        S2 = resultHonorar.getInt("HONORAR_ANZAHL_2");
                        if (S2 == 0) { // es gibt nur eine Schwelle
                            if (Anzahl >= S1) {
                                resultHonorar.updateInt("HONORAR_ZAHLEN", 1);
                            } else {
                                resultHonorar.updateInt("HONORAR_ZAHLEN", 0);
                            }
                        } else { // es gibt zwei Schwellen
                            if (Anzahl >= S2) {
                                resultHonorar.updateInt("HONORAR_ZAHLEN", 2);
                            } else if (Anzahl >= S1) {
                                resultHonorar.updateInt("HONORAR_ZAHLEN", 1);
                            } else {
                                resultHonorar.updateInt("HONORAR_ZAHLEN", 0);
                            }
                        }
                        resultHonorar.updateRow();
                    } // while resultHonorar.next()
                    System.out.println("-> Honorardatenbank ausgewertet");

// ausgeben der Gesamt-Honorar-Tabelle
                    System.out.println("Honorardatenbank ausgeben");
                    resultHonorar = SQLHonorar.executeQuery("SELECT * FROM TBL_HONORAR "
                            + "WHERE HONORAR_VEREINBART = TRUE");
                    while (resultHonorar.next()) {

                        if (resultHonorar.getBoolean("HONORAR_VERTEILEN")) {
                            briefHonorar.briefPDF(
                                    resultHonorar.getInt("HONORAR_ZAHLEN"), //  0
                                    resultHonorar.getString("HONORAR_TITEL"), //  1
                                    resultHonorar.getString("HONORAR_AUTOR_1"), //  2
                                    resultHonorar.getString("HONORAR_ISBN_PB"), //  3
                                    resultHonorar.getInt("HONORAR_ANZAHL_PB"), //  4
                                    resultHonorar.getFloat("HONORAR_PREIS_PB"), //  5
                                    resultHonorar.getString("HONORAR_ISBN_HC"), //  6
                                    resultHonorar.getInt("HONORAR_ANZAHL_HC"), //  7
                                    resultHonorar.getFloat("HONORAR_PREIS_HC"), //  8
                                    resultHonorar.getString("HONORAR_ISBN_EB"), //  9
                                    resultHonorar.getInt("HONORAR_ANZAHL_EB"), // 10
                                    resultHonorar.getFloat("HONORAR_PREIS_EB"), // 11
                                    resultHonorar.getInt("HONORAR_ANZAHL_1"), // 12
                                    resultHonorar.getInt("HONORAR_PROZENT_1"), // 13
                                    resultHonorar.getInt("HONORAR_ANZAHL_2"), // 14
                                    resultHonorar.getInt("HONORAR_PROZENT_2"), // 15
                                    true // 16
                            );
                            briefHonorar.briefPDF(
                                    resultHonorar.getInt("HONORAR_ZAHLEN"), //  0
                                    resultHonorar.getString("HONORAR_TITEL"), //  1
                                    resultHonorar.getString("HONORAR_AUTOR_2"), //  2
                                    resultHonorar.getString("HONORAR_ISBN_PB"), //  3
                                    resultHonorar.getInt("HONORAR_ANZAHL_PB"), //  4
                                    resultHonorar.getFloat("HONORAR_PREIS_PB"), //  5
                                    resultHonorar.getString("HONORAR_ISBN_HC"), //  6
                                    resultHonorar.getInt("HONORAR_ANZAHL_HC"), //  7
                                    resultHonorar.getFloat("HONORAR_PREIS_HC"), //  8
                                    resultHonorar.getString("HONORAR_ISBN_EB"), //  9
                                    resultHonorar.getInt("HONORAR_ANZAHL_EB"), // 10
                                    resultHonorar.getFloat("HONORAR_PREIS_EB"), // 11
                                    resultHonorar.getInt("HONORAR_ANZAHL_1"), // 12
                                    resultHonorar.getInt("HONORAR_PROZENT_1"), // 13
                                    resultHonorar.getInt("HONORAR_ANZAHL_2"), // 14
                                    resultHonorar.getInt("HONORAR_PROZENT_2"), // 15
                                    true // 16
                            );
                        } else {
                            briefHonorar.briefPDF(
                                    resultHonorar.getInt("HONORAR_ZAHLEN"), //  0
                                    resultHonorar.getString("HONORAR_TITEL"), //  1
                                    resultHonorar.getString("HONORAR_AUTOR_1"), //  2
                                    resultHonorar.getString("HONORAR_ISBN_PB"), //  3
                                    resultHonorar.getInt("HONORAR_ANZAHL_PB"), //  4
                                    resultHonorar.getFloat("HONORAR_PREIS_PB"), //  5
                                    resultHonorar.getString("HONORAR_ISBN_HC"), //  6
                                    resultHonorar.getInt("HONORAR_ANZAHL_HC"), //  7
                                    resultHonorar.getFloat("HONORAR_PREIS_HC"), //  8
                                    resultHonorar.getString("HONORAR_ISBN_EB"), //  9
                                    resultHonorar.getInt("HONORAR_ANZAHL_EB"), // 10
                                    resultHonorar.getFloat("HONORAR_PREIS_EB"), // 11
                                    resultHonorar.getInt("HONORAR_ANZAHL_1"), // 12
                                    resultHonorar.getInt("HONORAR_PROZENT_1"), // 13
                                    resultHonorar.getInt("HONORAR_ANZAHL_2"), // 14
                                    resultHonorar.getInt("HONORAR_PROZENT_2"), // 15
                                    false // 16
                            );
                        }
                    } // while 

                    // pr�fen auf verrechnungen
                    System.out.println("Verrechnungen ermitteln");
                    resultVerrechnung = SQLVerrechnung.executeQuery("SELECT * FROM TBL_VERRECHNUNG");
                    resultBestellung2 = SQLBestellung2.executeQuery("SELECT * FROM TBL_BESTELLUNG"
                            + " WHERE BESTELLUNG_BEZAHLUNG = '1'"
                            + " GROUP BY BESTELLUNG_KUNDE");
                    System.out.println("-> zu verrechnende Autoren ermittelt");
                    int ID = 1;
                    while (resultBestellung2.next()) {
                        SQLHonorar.executeUpdate("DELETE FROM TBL_VERRECHNUNG");
                        System.out.println("   -> Tabelle Verrechnung ist geleert");
                        ID = 1;

                        resultBestellung = SQLBestellung.executeQuery("SELECT * FROM TBL_BESTELLUNG"
                                + " WHERE BESTELLUNG_KUNDE = '" + resultBestellung2.getString("BESTELLUNG_KUNDE") + "'");
                        System.out.println("   -> zu verrechnende Rechnungen des Autors ermittelt");
                        while (resultBestellung.next()) {
                            Betrag19 = 0F;
                            Betrag7 = 0F;
                            // Tabelle Verrechnung f�llen mit Bestellungen    
// Rechnungsbetrag ermitteln
                            resultBestellungDetails = SQLBestellungDetails.executeQuery(
                                    "SELECT * FROM TBL_BESTELLUNG_DETAIL WHERE BESTELLUNG_DETAIL_RECHNR = '" + resultBestellung.getString("BESTELLUNG_RECHNR") + "'"); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 
                            while (resultBestellungDetails.next()) {
                                if (resultBestellungDetails.getBoolean("BESTELLUNG_DETAIL_SONST")) {
                                    Betrag19 = Betrag19 + resultBestellungDetails.getFloat("BESTELLUNG_DETAIL_SONST_PREIS");
                                } else {
                                    resultBuch = SQLBuch.executeQuery("SELECT * FROM TBL_BUCH WHERE BUCH_ID = '" + resultBestellungDetails.getString("BESTELLUNG_DETAIL_BUCH") + "'");
                                    resultBuch.next();

                                    Float ZPreis = resultBuch.getFloat("BUCH_PREIS");
                                    ZPreis = ZPreis - ZPreis / 100 * resultBestellungDetails.getFloat("BESTELLUNG_DETAIL_RABATT");

                                    Betrag7 = Betrag7 + resultBestellungDetails.getInt("BESTELLUNG_DETAIL_ANZAHL") * ZPreis;
                                }
                            }
// Rechnungsbetrag festlegen
// DEU:       Gesch�ft = Privat = Netto + UStr
// EU:        Gesch�ft = Netto
//            Privat   = Netto + UStr
// Drittland: Gesch�ft = Privat = Netto
                            Rechnungsbetrag = 0F;
                            switch (resultBestellung.getInt("BESTELLUNG_LAND")) {
                                case 0:
                                    Rechnungsbetrag = Betrag7 + Betrag19;
                                    break;
                                case 10:
                                case 11:
                                    if (resultBestellung.getBoolean("BESTELLUNG_PRIVAT")) {
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
                            Rechnungsbetrag = Rechnungsbetrag + resultBestellung.getFloat("BESTELLUNG_VERSAND");
                            
                            resultVerrechnung.moveToInsertRow();
                            resultVerrechnung.updateInt("VERRECHNUNG_ID", ID);
                            resultVerrechnung.updateFloat("VERRECHNUNG_BETRAG", Rechnungsbetrag);
                            resultVerrechnung.updateString("VERRECHNUNG_ISBN", resultBestellung.getString("BESTELLUNG_RECHNR"));
                            resultVerrechnung.updateBoolean("VERRECHNUNG_RECHNUNG", true);
                            resultVerrechnung.insertRow();
                    
                            ID = ID + 1;
                        } // while resultBestellung

                        briefVerrechnungHonorar.briefPDF(resultBestellung2.getString("BESTELLUNG_KUNDE"));
                    } // while resultVerrechnung.next

                    SQLHonorar.close();
                    SQLBuch.close();

                    resultHonorar.close();
                    resultBuch.close();
                } catch (SQLException ex) {
                    Modulhelferlein.Fehlermeldung("Abrechnung erstellen", "SQL-Exception", ex.getMessage());
                } catch (IOException ex) {
                    Modulhelferlein.Fehlermeldung("Abrechnung erstellen", "IO-Exception", ex.getMessage());
                }
                Modulhelferlein.Infomeldung("Die Briefe mit der Abrechnung sind als PDF gespeichert!");
            } // conn steht 
        } // if Dateiauswahl
    }    // void honorar
}    // class


//~ Formatted by Jindent --- http://www.jindent.com
