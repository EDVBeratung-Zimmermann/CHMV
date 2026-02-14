/*
 *
 * Das JAVA-Programm Miles-Verlag Verlagsverwaltung stellt alle notwendigen
 * Funktionen für die Verwaltung des Carola Hartman Miles-Verlags bereit.
 *
 * Copyright (C) 2017 EDV-Beratung und Betrieb, Entwicklung von Software
 *                    Dipl.Inform Thomas Zimmermann
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hsope that it will be useful,
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
import java.awt.event.*;
import javax.swing.*;
import java.beans.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.filechooser.FileNameExtensionFilter;
import static milesVerlagMain.ModulHelferlein.removeExtension;

 
public class ModulHonorarProgressBar extends JPanel
                             implements ActionListener, 
                                        PropertyChangeListener {
 
    private static final JFrame frame = new JFrame("Abrechnung ...");
    private final JProgressBar progressBar;
    private final JButton startButton;
    //private JTextArea taskOutput;
    private static Task task;
    
    static Connection conn = null;

    private static java.sql.Statement SQLHonorar = null;
    private static java.sql.Statement SQLBuch = null;
    private static java.sql.Statement SQLBestellungDetails = null;
    private static java.sql.Statement SQLAutor = null;
    private static java.sql.Statement SQLKunde = null;
    private static java.sql.Statement SQLBestellung = null;
    private static java.sql.Statement SQLBestellung2 = null;
    private static java.sql.Statement SQLVerrechnung = null;

    private static ResultSet resultBuch = null;
    private static ResultSet resultAutor = null;
    private static ResultSet resultKunde = null;
    private static ResultSet resultBestellung = null;
    private static ResultSet resultBestellung2 = null;
    private static ResultSet resultHonorar = null;
    private static ResultSet resultBestellungDetails = null;
    private static ResultSet resultVerrechnung = null;

    private static Integer HONORAR_ID = 0;
    private static Integer zeile = 0;
    private static String Filename = "";
    private static String strVon = "";
    private static String strBis = "";

    private static final FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV-Datei", "csv");
    private static final JFileChooser chooser = new JFileChooser(new File(System.getProperty("user.dir")));

    private static final String strISBN = "";
    private static final String strAnzahl = "";
    private static String Buchtitel = "";

    private static Float Rechnungsbetrag = 0F;
    private static Float Betrag19 = 0F;  // Bruttobetrag 19%
    private static Float Betrag7 = 0F;   // Bruttobetrag  7%
    
    private static boolean buchFehltInBuchDB = false;
    private static String fehlendeBuecher = "";

    
    public static void Log(String txt){
        System.out.println(txt);
    }

 
    class Task extends SwingWorker<Void, Void> {
        
        /*
         * Main task. Executed in background thread.
         */
        @Override
        public Void doInBackground() {
            int progress;
            //Initialize progress property.
            setProgress(0);
            
            // jetzt geht die Abrechnung los
            // der Fortschritt wird in progress abelegt und übergeben
            // progress = ... ; 
            // setProgreess(progress);

            try {                   // Datenbank-Treiber laden
                Class.forName(ModulHelferlein.dbDriver);
            } catch (ClassNotFoundException exept) {
                ModulHelferlein.Fehlermeldung("Honorar berechnen", "ClassNotFoundException: Treiber nicht gefunden: ", exept.getMessage());
            } // try Datenbank-Treiber laden

            try {                   // Verbindung zur Datenbank über die JDBC-Brücke
                conn = DriverManager.getConnection(ModulHelferlein.dbUrl, ModulHelferlein.dbUser, ModulHelferlein.dbPassword);
            } catch (SQLException exept) {
                ModulHelferlein.Fehlermeldung("Honorar berechnen", "SQL-Exception: Verbindung nicht moeglich: ", exept.getMessage());
            }                       // try Verbindung zur Datenbank über die JDBC-Brücke

            if (conn != null) {    // Datenbankverbindung steht
                Log("Verbindung zur Datenbank steht");        
                String rawTitel = "";
                String utf8EncodedTitel = "";
                
                try {
                    SQLBestellungDetails = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    SQLBestellung = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    SQLBestellung2 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    SQLKunde = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    SQLVerrechnung = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    Filename = chooser.getSelectedFile().getCanonicalPath();
// 1. Tabelle Honorar leeren   
                    Log("1. Tabelle Honorar leeren ...");
                    SQLHonorar = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    SQLHonorar.executeUpdate("DELETE FROM TBL_HONORAR");
                    Log("   -> Tabelle Honorar ist geleert");
                    SQLHonorar.executeUpdate("ALTER TABLE TBL_HONORAR MODIFY HONORAR_TITEL TEXT CHARACTER SET utf8");
                    //Log("   -> Spalte Honorar-Titel ist auf UTF umgestellt");
                    Log("");

// 2. Tabelle Honorar erstellen 
// gehe durch die Buch Tabelle und baue zeilenweise die Honoratabelle auf. Jede Zeile in der Honorartabelle enthält alle ISBN
                    progress = 10; 
                    Log("2. Tabelle Honorar erstellen ...");
                    SQLBuch = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    resultBuch = SQLBuch.executeQuery("SELECT * FROM TBL_BUCH WHERE BUCH_ID > '0' AND BUCH_HONORAR = '1'");
                    HONORAR_ID = 1;      
                    while (resultBuch.next()) {
                        Log("   - Prüfe Buch mit ID " + resultBuch.getString("BUCH_ID"));
                        // Titel normalisieren
                        utf8EncodedTitel = resultBuch.getString("BUCH_TITEL");
                        Log("     - normalisiere " + utf8EncodedTitel);
                        utf8EncodedTitel = utf8EncodedTitel.replace(",", " ");
                        utf8EncodedTitel = utf8EncodedTitel.replace("+", " ");
                        utf8EncodedTitel = utf8EncodedTitel.replace("/", " ");
                        utf8EncodedTitel = utf8EncodedTitel.replace("?", " ");
                        utf8EncodedTitel = utf8EncodedTitel.replace("!", " ");
                        utf8EncodedTitel = utf8EncodedTitel.replace("\"", " ");
                        utf8EncodedTitel = utf8EncodedTitel.replace("'", " ");
                        utf8EncodedTitel = utf8EncodedTitel.replace("*", " ");
                        utf8EncodedTitel = utf8EncodedTitel.replace(";", " ");
/*
                        int c = 0x0027;    utf8EncodedTitel = utf8EncodedTitel.replace(Character.toString((char) c), " "); // '
                        c = 0x0022;        utf8EncodedTitel = utf8EncodedTitel.replace(Character.toString((char) c), " "); // 
                        c = 0x2018;        utf8EncodedTitel = utf8EncodedTitel.replace(Character.toString((char) c), " "); // '
                        c = 0x2019;        utf8EncodedTitel = utf8EncodedTitel.replace(Character.toString((char) c), " "); // '
                        c = 0x003F;        utf8EncodedTitel = utf8EncodedTitel.replace(Character.toString((char) c), " "); // 
                        c = 0x002A;        utf8EncodedTitel = utf8EncodedTitel.replace(Character.toString((char) c), " "); // *
                        c = 0x0021;        utf8EncodedTitel = utf8EncodedTitel.replace(Character.toString((char) c), " "); // !
                        c = 0x005C;        utf8EncodedTitel = utf8EncodedTitel.replace(Character.toString((char) c), " "); // \
                        c = 0x002F;        utf8EncodedTitel = utf8EncodedTitel.replace(Character.toString((char) c), " "); // /
                        c = 0x002B;        utf8EncodedTitel = utf8EncodedTitel.replace(Character.toString((char) c), " "); // +
                        c = 0x002C;        utf8EncodedTitel = utf8EncodedTitel.replace(Character.toString((char) c), " "); // ,
                        c = 0x003B;        utf8EncodedTitel = utf8EncodedTitel.replace(Character.toString((char) c), " "); // ;
                        c = 0x0060;        utf8EncodedTitel = utf8EncodedTitel.replace(Character.toString((char) c), " "); // ,
                        c = 0x003A;        utf8EncodedTitel = utf8EncodedTitel.replace(Character.toString((char) c), " "); // ,
                        c = 0x00B4;        utf8EncodedTitel = utf8EncodedTitel.replace(Character.toString((char) c), " "); // ,
                        c = 0x00C4;        utf8EncodedTitel = utf8EncodedTitel.replace(Character.toString((char) c), "Ä"); // Ä
                        c = 0x00D6;        utf8EncodedTitel = utf8EncodedTitel.replace(Character.toString((char) c), "Ö"); // Ö
                        c = 0x00DC;        utf8EncodedTitel = utf8EncodedTitel.replace(Character.toString((char) c), "Ü"); // Ü
                        c = 0x00E4;        utf8EncodedTitel = utf8EncodedTitel.replace(Character.toString((char) c), "ä"); // ä
                        c = 0x00F6;        utf8EncodedTitel = utf8EncodedTitel.replace(Character.toString((char) c), "ö"); // ö
                        c = 0x00FC;        utf8EncodedTitel = utf8EncodedTitel.replace(Character.toString((char) c), "ü"); // ü
                        
                        */
                        
                        Log("       => " + utf8EncodedTitel);
                        
                        // füge Buch in die Honorardatenbank zu
                        resultHonorar = SQLHonorar.executeQuery(
                                "SELECT * FROM TBL_HONORAR WHERE HONORAR_TITEL = '" + utf8EncodedTitel + "'");
                        if (resultHonorar.next()) { 
                            // der Titel existiert bereits in der Honorardatenbank
                            // ergänze die ISBN
                            switch (resultBuch.getInt("BUCH_HC")) {
                                case 1: // HC
                                    resultHonorar.updateString("HONORAR_ISBN_HC", resultBuch.getString("BUCH_ISBN"));
                                    resultHonorar.updateFloat("HONORAR_PREIS_HC", resultBuch.getFloat("BUCH_PREIS"));
                                    resultHonorar.updateFloat("HONORAR_MARGE_HC", resultBuch.getFloat("BUCH_MARGE"));
                                    resultHonorar.updateInt("HONORAR_FIX_HC", resultBuch.getInt("BUCH_BODFIX"));
                                    Log("     ... Ergänze Ausgabe HC " + utf8EncodedTitel);
                                    break;
                                case 0: // PB
                                    resultHonorar.updateString("HONORAR_ISBN_PB", resultBuch.getString("BUCH_ISBN"));
                                    resultHonorar.updateFloat("HONORAR_PREIS_PB", resultBuch.getFloat("BUCH_PREIS"));
                                    resultHonorar.updateFloat("HONORAR_MARGE_PB", resultBuch.getFloat("BUCH_MARGE"));
                                    resultHonorar.updateInt("HONORAR_FIX_PB", resultBuch.getInt("BUCH_BODFIX"));
                                    Log("     ... Ergänze Ausgabe PB " + utf8EncodedTitel);
                                    break;
                                case 2: // EB
                                    resultHonorar.updateString("HONORAR_ISBN_EB", resultBuch.getString("BUCH_ISBN"));
                                    resultHonorar.updateFloat("HONORAR_PREIS_EB", resultBuch.getFloat("BUCH_PREIS"));
                                    resultHonorar.updateFloat("HONORAR_MARGE_EB", resultBuch.getFloat("BUCH_MARGE"));
                                    resultHonorar.updateInt("HONORAR_FIX_EB", resultBuch.getInt("BUCH_BODFIX"));
                                    Log("     ... Ergänze Ausgabe EB " + utf8EncodedTitel);
                                    break;
                            }
                            resultHonorar.updateRow();
                        } else { 
                            // der Titel existiert noch nicht in der Honorardatenbank
                            // füge Buch ein
                            Log("     - ergänze zu Honorar-DB  Titel " + utf8EncodedTitel);
                            resultHonorar.moveToInsertRow();
                            String Autoren = resultBuch.getString("BUCH_AUTOR") + ",0";
                            String[] Autor = Autoren.split(",");
                            resultHonorar.moveToInsertRow();
                            resultHonorar.updateInt("HONORAR_ID", HONORAR_ID);
                            resultHonorar.updateInt("HONORAR_ZAHLEN", 0);
                            resultHonorar.updateString("HONORAR_TITEL", utf8EncodedTitel);
                            resultHonorar.updateBoolean("HONORAR_VEREINBART", resultBuch.getBoolean("BUCH_HONORAR"));
                            resultHonorar.updateInt("HONORAR_ANZAHL_1", resultBuch.getInt("BUCH_HONORAR_ANZAHL"));
                            resultHonorar.updateInt("HONORAR_PROZENT_1", resultBuch.getInt("BUCH_HONORAR_PROZENT"));
                            resultHonorar.updateInt("HONORAR_ANZAHL_2", resultBuch.getInt("BUCH_HONORAR_2_ANZAHL"));
                            resultHonorar.updateInt("HONORAR_PROZENT_2", resultBuch.getInt("BUCH_HONORAR_2_PROZENT"));
                            resultHonorar.updateFloat("HONORAR_MARGE_EB", 0F);
                            resultHonorar.updateFloat("HONORAR_MARGE_PB", 0F);
                            resultHonorar.updateFloat("HONORAR_MARGE_HC", 0F);
                            resultHonorar.updateString("HONORAR_ISBN_PB", "");
                            resultHonorar.updateInt("HONORAR_ANZAHL_PB", 0);
                            resultHonorar.updateInt("HONORAR_ANZAHL_BOD_PB", 0);
                            resultHonorar.updateFloat("HONORAR_PREIS_PB", 0F);
                            resultHonorar.updateString("HONORAR_ISBN_HC", "");
                            resultHonorar.updateInt("HONORAR_ANZAHL_HC", 0);
                            resultHonorar.updateFloat("HONORAR_PREIS_HC", 0F);
                            resultHonorar.updateInt("HONORAR_ANZAHL_BOD_HC", 0);
                            resultHonorar.updateString("HONORAR_ISBN_EB", "");
                            resultHonorar.updateFloat("HONORAR_PREIS_EB", 0F);
                            resultHonorar.updateFloat("HONORAR_HONORAR", 0F);
                            resultHonorar.updateInt("HONORAR_ANZAHL_EB", 0);
                            resultHonorar.updateInt("HONORAR_ANZAHL_BOD_EB", 0);
                            resultHonorar.updateFloat("HONORAR_BODPROZENT", resultBuch.getFloat("BUCH_BODPROZENT"));
                            resultHonorar.updateInt("HONORAR_BODFIX", resultBuch.getInt("BUCH_BODFIX"));
                            resultHonorar.updateInt("HONORAR_FIX_EB", 0);
                            resultHonorar.updateInt("HONORAR_FIX_PB", 0);
                            resultHonorar.updateInt("HONORAR_FIX_HC", 0);
                            resultHonorar.updateBoolean("HONORAR_GESAMTBETRACHTUNG", resultBuch.getBoolean("BUCH_GESAMTBETRACHTUNG"));
                            switch (resultBuch.getInt("BUCH_HC")) {
                                case 1: // HC
                                    resultHonorar.updateString("HONORAR_ISBN_HC", resultBuch.getString("BUCH_ISBN"));
                                    resultHonorar.updateFloat("HONORAR_PREIS_HC", resultBuch.getFloat("BUCH_PREIS"));
                                    resultHonorar.updateFloat("HONORAR_MARGE_HC", resultBuch.getFloat("BUCH_MARGE"));
                                    resultHonorar.updateInt("HONORAR_FIX_HC", resultBuch.getInt("BUCH_BODFIX"));
                                    break;
                                case 0: // PB
                                    resultHonorar.updateString("HONORAR_ISBN_PB", resultBuch.getString("BUCH_ISBN"));
                                    resultHonorar.updateFloat("HONORAR_PREIS_PB", resultBuch.getFloat("BUCH_PREIS"));
                                    resultHonorar.updateFloat("HONORAR_MARGE_PB", resultBuch.getFloat("BUCH_MARGE"));
                                    resultHonorar.updateInt("HONORAR_FIX_PB", resultBuch.getInt("BUCH_BODFIX"));
                                    break;
                                case 2: // EB
                                    resultHonorar.updateString("HONORAR_ISBN_EB", resultBuch.getString("BUCH_ISBN"));
                                    resultHonorar.updateFloat("HONORAR_PREIS_EB", resultBuch.getFloat("BUCH_PREIS"));
                                    resultHonorar.updateFloat("HONORAR_MARGE_EB", resultBuch.getFloat("BUCH_MARGE"));
                                    resultHonorar.updateInt("HONORAR_FIX_EB", resultBuch.getInt("BUCH_BODFIX"));
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
                            HONORAR_ID = HONORAR_ID + 1;
                        } // Datensatz vorhanden
                        progress = progress + 1;  if (progress<100) {setProgress(progress);}
                    } // while Buch
                    Log("   -> Honorardatenbank erstellt");
                    Log("");

// 3. Honorardatenbank mit BoD-Sales füllen   
//          Zeile[0] = BoD-Nummer
//          Zeile[1] = ISBN
//          Zeile[2] = Autor
//          Zeile[3] = Buchtitel
//          Zeile[4] = Anzahl
//          Zeile[5] = Umsatz
//          Zeile[6] = Währung
//          Zeile[7] = Datum
//          Zeile[8] = abgerechnet
                    Log("3. Füllen der Honorardatenbank mit BoD-Sales");
                    progress = 20;
                    Integer AnzahlGesamt = 0;
                    BufferedReader in = new BufferedReader(new FileReader(Filename));
                    String CSVzeileIn = null;
                    String fileName[] = removeExtension(Filename).split("-");
                    strVon = fileName[2] + "-" + fileName[3] + "-" + fileName[4];
                    strBis = fileName[6] + "-" + fileName[7] + "-" + fileName[8];
                    zeile = 1;
                    Log("   - Berechne Honorare für den Zeitraum " + strVon + " - " + strBis);
                    while ((CSVzeileIn = in.readLine()) != null) {
                        progress = progress + 1;  if (progress<100) {setProgress(progress);}
                        // Zeile lesen
                        String CSVZeile[] = CSVzeileIn.split(";");
                        Log("   - lese Zeile "+zeile.toString() + ": "+CSVzeileIn);
                        if (CSVzeileIn.contains("Zeitraum")) { // Zeitraum der Abrechnung
                            String Zeitraum = CSVZeile[0].substring(10, CSVZeile[0].length());
                            Zeitraum = Zeitraum.replace("-", ".");
                            strVon = Zeitraum.substring(0, 10);
                            strVon = strVon.replace(".", "-");
                            strBis = Zeitraum.substring(13, Zeitraum.length());
                            strBis = strBis.replace(".", "-");
                        } else if (CSVzeileIn.contains("BoD-Nr.")) { // Kopfzeile der Tabelle
                            // mache nichts
                            Log("     - ignoriere Zeile ");
                        }
                        else {
                            String ISBN = CSVZeile[1];

                            ISBN = ISBN.replace("-", "");
                            String sAnzahlGesamt = CSVZeile[4];
                            Log("     - übertrage Zeile " + zeile.toString() + " mit ISBN: " + ISBN + ", Anzahl: " + sAnzahlGesamt);
                            if (sAnzahlGesamt.contains(".")) {
                                sAnzahlGesamt = sAnzahlGesamt.replace(".", "");
                            }
                            if (sAnzahlGesamt.contains(",")) {
                                sAnzahlGesamt = sAnzahlGesamt.replace(",", ".");
                            }
                            AnzahlGesamt = Math.round(Float.parseFloat(sAnzahlGesamt));

                            // Buch mit der ISBN in der Honorar-DB suchen und Daten ergänzen
                            String SQL = "SELECT * FROM TBL_HONORAR "
                                    + "WHERE ("
                                        + "(HONORAR_ISBN_PB = '" + ISBN + "') OR "
                                        + "(HONORAR_ISBN_HC = '" + ISBN + "') OR "
                                        + "(HONORAR_ISBN_EB = '" + ISBN + "')"
                                        + ")";
                            Log("     - Suche in Honorar-Datenbank nach " + ISBN);
                            resultHonorar = SQLHonorar.executeQuery(SQL);
                            if (resultHonorar.next()) {
                                try{
                                    Log("       -> Buch mit ISBN " + ISBN + " ist vorhanden in Honorar DB => Update");
                                    if (resultHonorar.getString("HONORAR_ISBN_PB").equals(ISBN)) {
                                        resultHonorar.updateInt("HONORAR_ANZAHL_BOD_PB", AnzahlGesamt + resultHonorar.getInt("HONORAR_ANZAHL_BOD_PB"));
                                    } else if (resultHonorar.getString("HONORAR_ISBN_HC").equals(ISBN)) {
                                        resultHonorar.updateInt("HONORAR_ANZAHL_BOD_HC", AnzahlGesamt + resultHonorar.getInt("HONORAR_ANZAHL_BOD_HC"));
                                    } else {
                                        resultHonorar.updateInt("HONORAR_ANZAHL_BOD_EB", AnzahlGesamt + resultHonorar.getInt("HONORAR_ANZAHL_BOD_EB"));
                                    }
                                    resultHonorar.updateRow();
                                } catch(Exception except) {
                                    Log("          => Fehler beim Update ISBN " + ISBN + " SQL-Exception " + except.getMessage());
                                    Log("          => Honorar-DB-Titel: " + resultHonorar.getString("HONORAR_TITEL").substring(0, 100));
                                    //ModulHelferlein.Fehlermeldung("Fehler beim Update ISBN " + ISBN, "SQL-Exception", except.getMessage());
                                } 
                            } else { // warum auch immer existiert das Buch bisher nicht
                                Log("       -> Buch mit ISBN " + ISBN + " ist nicht in Honorar DB => ergänzen");
                                    
                                // Suche Buch in der Buch-DB und füge es der Honorar-DB zu    
                                resultBuch = SQLBuch.executeQuery("SELECT * FROM TBL_BUCH WHERE BUCH_ISBN = '" + ISBN + "'");
                                try {
                                    resultBuch.next();

                                    // Titel normalisieren
                                    utf8EncodedTitel = resultBuch.getString("BUCH_TITEL");
                                    Log("          => normalisiere " + utf8EncodedTitel);
                                    utf8EncodedTitel = utf8EncodedTitel.replace(",", " ");
                                    utf8EncodedTitel = utf8EncodedTitel.replace("+", " ");
                                    utf8EncodedTitel = utf8EncodedTitel.replace("/", " ");
                                    utf8EncodedTitel = utf8EncodedTitel.replace("?", " ");
                                    utf8EncodedTitel = utf8EncodedTitel.replace("!", " ");
                                    utf8EncodedTitel = utf8EncodedTitel.replace("\"", " ");
                                    utf8EncodedTitel = utf8EncodedTitel.replace("'", " ");
                                    utf8EncodedTitel = utf8EncodedTitel.replace("*", " ");
                                    utf8EncodedTitel = utf8EncodedTitel.replace(";", " ");
                                    /*
                                    int c = 0x0027;    utf8EncodedTitel = utf8EncodedTitel.replace(Character.toString((char) c), " "); // '
                                    c = 0x0022;        utf8EncodedTitel = utf8EncodedTitel.replace(Character.toString((char) c), " "); // 
                                    c = 0x2018;        utf8EncodedTitel = utf8EncodedTitel.replace(Character.toString((char) c), " "); // '
                                    c = 0x2019;        utf8EncodedTitel = utf8EncodedTitel.replace(Character.toString((char) c), " "); // '
                                    c = 0x003F;        utf8EncodedTitel = utf8EncodedTitel.replace(Character.toString((char) c), " "); // 
                                    c = 0x002A;        utf8EncodedTitel = utf8EncodedTitel.replace(Character.toString((char) c), " "); // *
                                    c = 0x0021;        utf8EncodedTitel = utf8EncodedTitel.replace(Character.toString((char) c), " "); // !
                                    c = 0x005C;        utf8EncodedTitel = utf8EncodedTitel.replace(Character.toString((char) c), " "); // \
                                    c = 0x002F;        utf8EncodedTitel = utf8EncodedTitel.replace(Character.toString((char) c), " "); // /
                                    c = 0x002B;        utf8EncodedTitel = utf8EncodedTitel.replace(Character.toString((char) c), " "); // +
                                    c = 0x002C;        utf8EncodedTitel = utf8EncodedTitel.replace(Character.toString((char) c), " "); // ,
                                    c = 0x003B;        utf8EncodedTitel = utf8EncodedTitel.replace(Character.toString((char) c), " "); // ;
                                    c = 0x0060;        utf8EncodedTitel = utf8EncodedTitel.replace(Character.toString((char) c), " "); // ,
                                    c = 0x003A;        utf8EncodedTitel = utf8EncodedTitel.replace(Character.toString((char) c), " "); // ,
                                    c = 0x00B4;        utf8EncodedTitel = utf8EncodedTitel.replace(Character.toString((char) c), " "); // ,
                                    c = 0x00C4;        utf8EncodedTitel = utf8EncodedTitel.replace(Character.toString((char) c), "Ä"); // Ä
                                    c = 0x00D6;        utf8EncodedTitel = utf8EncodedTitel.replace(Character.toString((char) c), "Ö"); // Ö
                                    c = 0x00DC;        utf8EncodedTitel = utf8EncodedTitel.replace(Character.toString((char) c), "Ü"); // Ü
                                    c = 0x00E4;        utf8EncodedTitel = utf8EncodedTitel.replace(Character.toString((char) c), "ä"); // ä
                                    c = 0x00F6;        utf8EncodedTitel = utf8EncodedTitel.replace(Character.toString((char) c), "ö"); // ö
                                    c = 0x00FC;        utf8EncodedTitel = utf8EncodedTitel.replace(Character.toString((char) c), "ü"); // ü

                                    */

                                    Log("        => " + utf8EncodedTitel);

                                    try {
                                        resultHonorar.moveToInsertRow();
                                        resultHonorar.updateInt("HONORAR_ID", HONORAR_ID);
                                        resultHonorar.updateInt("HONORAR_ZAHLEN", 0);
                                        resultHonorar.updateString("HONORAR_TITEL", utf8EncodedTitel);
                                        resultHonorar.updateBoolean("HONORAR_VEREINBART", resultBuch.getBoolean("BUCH_HONORAR"));
                                        resultHonorar.updateInt("HONORAR_ANZAHL_1", resultBuch.getInt("BUCH_HONORAR_ANZAHL"));
                                        resultHonorar.updateInt("HONORAR_PROZENT_1", resultBuch.getInt("BUCH_HONORAR_PROZENT"));
                                        resultHonorar.updateInt("HONORAR_ANZAHL_2", resultBuch.getInt("BUCH_HONORAR_2_ANZAHL"));
                                        resultHonorar.updateInt("HONORAR_PROZENT_2", resultBuch.getInt("BUCH_HONORAR_2_PROZENT"));
                                        resultHonorar.updateFloat("HONORAR_BODPROZENT", resultBuch.getInt("BUCH_BODPROZENT"));
                                        resultHonorar.updateInt("HONORAR_BODFIX", resultBuch.getInt("BUCH_BODFIX"));
                                        resultHonorar.updateFloat("HONORAR_MARGE_EB", 0F);
                                        resultHonorar.updateFloat("HONORAR_MARGE_PB", 0F);
                                        resultHonorar.updateFloat("HONORAR_MARGE_HC", 0F);
                                        resultHonorar.updateBoolean("HONORAR_GESAMTBETRACHTUNG", resultBuch.getBoolean("BUCH_GESAMTBETRACHTUNG"));
                                        resultHonorar.updateString("HONORAR_ISBN_PB", "");
                                        resultHonorar.updateInt("HONORAR_ANZAHL_PB", 0);
                                        resultHonorar.updateInt("HONORAR_ANZAHL_BOD_PB", 0);
                                        resultHonorar.updateFloat("HONORAR_PREIS_PB", 0F);
                                        resultHonorar.updateString("HONORAR_ISBN_HC", "");
                                        resultHonorar.updateInt("HONORAR_ANZAHL_HC", 0);
                                        resultHonorar.updateInt("HONORAR_ANZAHL_BOD_HC", 0);
                                        resultHonorar.updateFloat("HONORAR_PREIS_HC", 0F);
                                        resultHonorar.updateFloat("HONORAR_HONORAR", 0F);
                                        resultHonorar.updateString("HONORAR_ISBN_EB", "");
                                        resultHonorar.updateInt("HONORAR_ANZAHL_EB", 0);
                                        resultHonorar.updateFloat("HONORAR_PREIS_PB", 0F);
                                        resultHonorar.updateInt("HONORAR_ANZAHL_BOD_EB", 0);
                                        resultHonorar.updateInt("HONORAR_FIX_EB", 0);
                                        resultHonorar.updateInt("HONORAR_FIX_PB", 0);
                                        resultHonorar.updateInt("HONORAR_FIX_HC", 0);
                                        switch (resultBuch.getInt("BUCH_HC")) {
                                            case 0: // PB
                                                resultHonorar.updateString("HONORAR_ISBN_PB", resultBuch.getString("BUCH_ISBN"));
                                                resultHonorar.updateInt("HONORAR_ANZAHL_BOD_PB", AnzahlGesamt);
                                                resultHonorar.updateFloat("HONORAR_PREIS_PB", resultBuch.getFloat("BUCH_PREIS"));
                                                resultHonorar.updateFloat("HONORAR_MARGE_PB", resultBuch.getFloat("BUCH_MARGE"));
                                                resultHonorar.updateInt("HONORAR_FIX_PB", resultBuch.getInt("BUCH_BODFIX"));
                                                break;
                                            case 1: // HC
                                                resultHonorar.updateString("HONORAR_ISBN_HC", resultBuch.getString("BUCH_ISBN"));
                                                resultHonorar.updateInt("HONORAR_ANZAHL_BOD_HC", AnzahlGesamt);
                                                resultHonorar.updateFloat("HONORAR_PREIS_HC", resultBuch.getFloat("BUCH_PREIS"));
                                                resultHonorar.updateFloat("HONORAR_MARGE_HC", resultBuch.getFloat("BUCH_MARGE"));
                                                resultHonorar.updateInt("HONORAR_FIX_HC", resultBuch.getInt("BUCH_BODFIX"));
                                                break;
                                            case 2: // EB
                                                resultHonorar.updateString("HONORAR_ISBN_EB", resultBuch.getString("BUCH_ISBN"));
                                                resultHonorar.updateInt("HONORAR_ANZAHL_BOD_EB", AnzahlGesamt);
                                                resultHonorar.updateFloat("HONORAR_PREIS_EB", resultBuch.getFloat("BUCH_PREIS"));
                                                resultHonorar.updateFloat("HONORAR_MARGE_EB", resultBuch.getFloat("BUCH_MARGE"));
                                                resultHonorar.updateInt("HONORAR_FIX_EB", resultBuch.getInt("BUCH_BODFIX"));
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
                                        Log("        => Honorar-DB ergänzt mit ID " + Integer.toString(HONORAR_ID) + " ISBN " + ISBN);
                                        HONORAR_ID = HONORAR_ID + 1;
                                    } catch(Exception except) {
                                        Log("        => Fehler bei der Suche nach ISBN " + ISBN + " SQL-Exception " + except.getMessage());
                                        //ModulHelferlein.Fehlermeldung("Fehler bei der Suche nach ISBN " + ISBN, "SQL-Exception", except.getMessage());
                                    }
                                } catch (Exception except) { // Buch fehlt in der Buch-DB
                                        Log("        => Fehler bei der Suche nach ISBN " + ISBN + " in der Buch-Datenbank - SQL-Exception " + except.getMessage());
                                        buchFehltInBuchDB = true;
                                        if (!fehlendeBuecher.contains(ISBN)) {
                                            fehlendeBuecher = fehlendeBuecher + "ISBN: " + ISBN + "\n";
                                        }
                                } // catch
                            }
                        }
                        zeile = zeile + 1;
                    }    // while CSV-einlesen
                    //Log("   -> BoD-Sales eingelesen");
                    Log("");

// Bestellungen ergänzen im Zeitraum strVon strBis
                    Log("4. Füllen der Honorardatenbank mit Miles-Verkäufen ");
                    progress = 30;
                    AnzahlGesamt = 0;
                    Integer AnzahlAutor = 0;
                    Integer AnzahlGeschenk = 0;

                    SQLAutor = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

                    SQLBuch = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    resultBuch = SQLBuch.executeQuery("SELECT * FROM TBL_BUCH WHERE BUCH_ID > '0' AND BUCH_HONORAR = '1'");

                    while (resultBuch.next()) {                        
                        Log("   " + resultBuch.getString("BUCH_ISBN") + " " + resultBuch.getString("BUCH_TITEL"));
                        progress = progress + 1;  if (progress<100) {setProgress(progress);}
                        AnzahlGesamt = 0;
                        AnzahlAutor = 0;
                        AnzahlGeschenk = 0;
                        String ISBN = resultBuch.getString("BUCH_ISBN");

                        // hole die Autorendaten
                        //Log("   .. hole Autoren");
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
                        //Log("      => " + Autor);

                        // hole alle Bestelldetails für BuchID
                        Log("   .. hole Bestelldetails");
                        String SQL = "SELECT * FROM TBL_BESTELLUNG_DETAIL"
                                + " WHERE ((BESTELLUNG_DETAIL_BUCH = '"
                                + resultBuch.getString("BUCH_ID") + "') "
                                + "AND (('" + strVon + "' <= BESTELLUNG_DETAIL_DATUM) "
                                + "AND (BESTELLUNG_DETAIL_DATUM <= '" + strBis + "')))";
                        //Log("      " + SQL);
                        resultBestellungDetails = SQLBestellungDetails.executeQuery(SQL);

                        while (resultBestellungDetails.next()) { // Schleife gehe durch alle Bestelldetails
                            AnzahlGesamt = AnzahlGesamt + resultBestellungDetails.getInt("BESTELLUNG_DETAIL_ANZAHL");
                            String Bestellnummer = resultBestellungDetails.getString("BESTELLUNG_DETAIL_RECHNR");
                            // hole Bestellung mit Bestellnummer
                            SQL = "SELECT * FROM TBL_BESTELLUNG "
                                    + " WHERE BESTELLUNG_RECHNR = '" + Bestellnummer + "'";
                            //System.out.println("      -> " + SQL);
                            resultBestellung = SQLBestellung.executeQuery(SQL);
                            resultBestellung.first();
                            Log("      Kunde: " + resultBestellung.getString("BESTELLUNG_KUNDE"));
                            // if Bestelltyp != Bestellung => AnzahlGeschenk = ANzahlGescehnk + 1
                            if (resultBestellung.getInt("BESTELLUNG_TYP") != 0) {
                                AnzahlGeschenk = AnzahlGeschenk + resultBestellungDetails.getInt("BESTELLUNG_DETAIL_ANZAHL");
                            } // if

                            // if Kunde = Teil vom Autor => ANzahlAutor = AnzahlAutor +1
                            if (resultBestellung.getInt("BESTELLUNG_KUNDE") > 0) {
                                try {
                                    resultKunde = SQLKunde.executeQuery("SELECT * FROM TBL_ADRESSE "
                                            + "WHERE ADRESSEN_ID = '"
                                            + resultBestellung.getString("BESTELLUNG_KUNDE") + "'");
                                    resultKunde.next();
                                    if (Autor.contains(resultKunde.getString("ADRESSEN_NAME"))) {
                                        AnzahlAutor = AnzahlAutor + resultBestellungDetails.getInt("BESTELLUNG_DETAIL_ANZAHL");
                                    }
                                } catch (Exception except) {
                                    ModulHelferlein.Fehlermeldung("Verkäufe Miles-Verlag - Kunde nicht gefunden", "SQL-Exception", except.getMessage());
                                    Log("      Kunde: " + resultBestellung.getString("BESTELLUNG_KUNDE") + " nicht in Adress-DB gefunden");
                                }
                            }
                        } // while resultBestellungDetails.next()

                        // update Honorardatenbank für Buch ISBN mit ISBN, Titel, Autoren, AnzahlGesamt, AnzahlAutor, AnzahlGeschenk, Preis * (AnzahlGesamt-AnzahlAutor)
                        Log("   .. update Honorardatenbank für ISBN " + ISBN);

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
                    Log("   -> Verkäufe Miles-Verlag eingelesen");
                    Log("");

// Honorartabelle auswerten
                    Log("5. Honorardatenbank auswerten");
                    progress = 40;
                    Integer Anz_PB = 0;
                    Integer Anz_HC = 0;
                    Integer Anz_EB = 0;
                    Integer Anz_B_PB = 0;
                    Integer Anz_B_HC = 0;
                    Integer Anz_B_EB = 0;
                    Integer Anzahl = 0;
                    Integer S1 = 0;
                    Integer S2 = 0;
                    resultHonorar = SQLHonorar.executeQuery("SELECT * FROM TBL_HONORAR ");
                    while (resultHonorar.next()) {
                        progress = progress + 1;  if (progress<100) {setProgress(progress);}
                        Anz_PB = resultHonorar.getInt("HONORAR_ANZAHL_PB");
                        Anz_HC = resultHonorar.getInt("HONORAR_ANZAHL_HC");
                        Anz_EB = resultHonorar.getInt("HONORAR_ANZAHL_EB");
                        Anz_B_PB = resultHonorar.getInt("HONORAR_ANZAHL_BOD_PB");
                        Anz_B_HC = resultHonorar.getInt("HONORAR_ANZAHL_BOD_HC");
                        Anz_B_EB = resultHonorar.getInt("HONORAR_ANZAHL_BOD_EB");
                        Anzahl = Anz_PB + Anz_HC + Anz_EB + Anz_B_PB + Anz_B_HC + Anz_B_EB;
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
                        if (Anzahl == 0) {
                            resultHonorar.updateInt("HONORAR_ZAHLEN", -1);
                        }
                        resultHonorar.updateRow();
                    } // while resultHonorar.next()
                    Log("   -> Honorardatenbank ausgewertet");
                    Log("");

// ausgeben der Gesamt-Honorar-Tabelle
                    Log("6. Honorardatenbank ausgeben");
                    progress = 50;
                    resultHonorar = SQLHonorar.executeQuery("SELECT * FROM TBL_HONORAR "
                            + "WHERE HONORAR_VEREINBART = TRUE");
                    while (resultHonorar.next()) {
                        progress = progress + 1;  if (progress<100) {setProgress(progress);}
                        if (resultHonorar.getBoolean("HONORAR_VERTEILEN")) {
                            Log("   drucke H-Rechnung - verteilt auf "+resultHonorar.getString("HONORAR_AUTOR_1")+", "+resultHonorar.getString("HONORAR_AUTOR_2"));
                            briefHonorar.briefPDF(
                                    resultHonorar.getInt("HONORAR_ID"), //  0
                                    resultHonorar.getInt("HONORAR_ZAHLEN"), //  0
                                    resultHonorar.getString("HONORAR_TITEL"), //  1
                                    resultHonorar.getString("HONORAR_AUTOR_1"), //  2
                                    resultHonorar.getString("HONORAR_ISBN_PB"), //  3
                                    resultHonorar.getInt("HONORAR_ANZAHL_PB"), //  4
                                    resultHonorar.getInt("HONORAR_ANZAHL_BOD_PB"), //  5
                                    resultHonorar.getFloat("HONORAR_PREIS_PB"), //  6
                                    resultHonorar.getString("HONORAR_ISBN_HC"), //  7
                                    resultHonorar.getInt("HONORAR_ANZAHL_HC"), //  8
                                    resultHonorar.getInt("HONORAR_ANZAHL_BOD_HC"), //  9
                                    resultHonorar.getFloat("HONORAR_PREIS_HC"), //  10
                                    resultHonorar.getString("HONORAR_ISBN_EB"), //  11
                                    resultHonorar.getInt("HONORAR_ANZAHL_EB"), // 12
                                    resultHonorar.getInt("HONORAR_ANZAHL_BOD_EB"), // 13
                                    resultHonorar.getFloat("HONORAR_PREIS_EB"), // 14
                                    resultHonorar.getInt("HONORAR_ANZAHL_1"), // 15
                                    resultHonorar.getInt("HONORAR_PROZENT_1"), // 16
                                    resultHonorar.getInt("HONORAR_ANZAHL_2"), // 17
                                    resultHonorar.getInt("HONORAR_PROZENT_2"), // 18
                                    resultHonorar.getFloat("HONORAR_MARGE_EB"), // 19
                                    resultHonorar.getFloat("HONORAR_MARGE_PB"), // 20
                                    resultHonorar.getFloat("HONORAR_MARGE_HC"), // 21
                                    resultHonorar.getInt("HONORAR_FIX_EB"), // 22
                                    resultHonorar.getInt("HONORAR_FIX_PB"), // 23
                                    resultHonorar.getInt("HONORAR_FIX_HC"), // 24
                                    resultHonorar.getFloat("HONORAR_BODPROZENT"), // 25
                                    resultHonorar.getInt("HONORAR_BODFIX"), // 26
                                    resultHonorar.getBoolean("HONORAR_GESAMTBETRACHTUNG"), // 27
                                    true // 28
                            );
                            briefHonorar.briefPDF(
                                    resultHonorar.getInt("HONORAR_ID"), //  0
                                    resultHonorar.getInt("HONORAR_ZAHLEN"), //  0
                                    resultHonorar.getString("HONORAR_TITEL"), //  1
                                    resultHonorar.getString("HONORAR_AUTOR_2"), //  2
                                    resultHonorar.getString("HONORAR_ISBN_PB"), //  3
                                    resultHonorar.getInt("HONORAR_ANZAHL_PB"), //  4
                                    resultHonorar.getInt("HONORAR_ANZAHL_BOD_PB"), //  5
                                    resultHonorar.getFloat("HONORAR_PREIS_PB"), //  6
                                    resultHonorar.getString("HONORAR_ISBN_HC"), //  7
                                    resultHonorar.getInt("HONORAR_ANZAHL_HC"), //  8
                                    resultHonorar.getInt("HONORAR_ANZAHL_BOD_HC"), //  9
                                    resultHonorar.getFloat("HONORAR_PREIS_HC"), //  10
                                    resultHonorar.getString("HONORAR_ISBN_EB"), //  11
                                    resultHonorar.getInt("HONORAR_ANZAHL_EB"), // 12
                                    resultHonorar.getInt("HONORAR_ANZAHL_BOD_EB"), // 13
                                    resultHonorar.getFloat("HONORAR_PREIS_EB"), // 14
                                    resultHonorar.getInt("HONORAR_ANZAHL_1"), // 15
                                    resultHonorar.getInt("HONORAR_PROZENT_1"), // 16
                                    resultHonorar.getInt("HONORAR_ANZAHL_2"), // 17
                                    resultHonorar.getInt("HONORAR_PROZENT_2"), // 18
                                    resultHonorar.getFloat("HONORAR_MARGE_EB"), // 19
                                    resultHonorar.getFloat("HONORAR_MARGE_PB"), // 20
                                    resultHonorar.getFloat("HONORAR_MARGE_HC"), // 21
                                    resultHonorar.getInt("HONORAR_FIX_EB"), // 22
                                    resultHonorar.getInt("HONORAR_FIX_PB"), // 23
                                    resultHonorar.getInt("HONORAR_FIX_HC"), // 24
                                    resultHonorar.getFloat("HONORAR_BODPROZENT"), // 25
                                    resultHonorar.getInt("HONORAR_BODFIX"), // 26
                                    resultHonorar.getBoolean("HONORAR_GESAMTBETRACHTUNG"), // 27
                                    true // 28
                            );
                        } else {
                            Log("   drucke H-Rechnung - Einzelautor  "+resultHonorar.getString("HONORAR_AUTOR_1")+", "+resultHonorar.getString("HONORAR_AUTOR_2"));
                            briefHonorar.briefPDF(
                                    resultHonorar.getInt("HONORAR_ID"), //  0
                                    resultHonorar.getInt("HONORAR_ZAHLEN"), //  0
                                    resultHonorar.getString("HONORAR_TITEL"), //  1
                                    resultHonorar.getString("HONORAR_AUTOR_1"), //  2
                                    resultHonorar.getString("HONORAR_ISBN_PB"), //  3
                                    resultHonorar.getInt("HONORAR_ANZAHL_PB"), //  4
                                    resultHonorar.getInt("HONORAR_ANZAHL_BOD_PB"), //  5
                                    resultHonorar.getFloat("HONORAR_PREIS_PB"), //  6
                                    resultHonorar.getString("HONORAR_ISBN_HC"), //  7
                                    resultHonorar.getInt("HONORAR_ANZAHL_HC"), //  8
                                    resultHonorar.getInt("HONORAR_ANZAHL_BOD_HC"), //  9
                                    resultHonorar.getFloat("HONORAR_PREIS_HC"), //  10
                                    resultHonorar.getString("HONORAR_ISBN_EB"), //  11
                                    resultHonorar.getInt("HONORAR_ANZAHL_EB"), // 12
                                    resultHonorar.getInt("HONORAR_ANZAHL_BOD_EB"), // 13
                                    resultHonorar.getFloat("HONORAR_PREIS_EB"), // 14
                                    resultHonorar.getInt("HONORAR_ANZAHL_1"), // 15
                                    resultHonorar.getInt("HONORAR_PROZENT_1"), // 16
                                    resultHonorar.getInt("HONORAR_ANZAHL_2"), // 17
                                    resultHonorar.getInt("HONORAR_PROZENT_2"), // 18
                                    resultHonorar.getFloat("HONORAR_MARGE_EB"), // 19
                                    resultHonorar.getFloat("HONORAR_MARGE_PB"), // 20
                                    resultHonorar.getFloat("HONORAR_MARGE_HC"), // 21
                                    resultHonorar.getInt("HONORAR_FIX_EB"), // 22
                                    resultHonorar.getInt("HONORAR_FIX_PB"), // 23
                                    resultHonorar.getInt("HONORAR_FIX_HC"), // 24
                                    resultHonorar.getFloat("HONORAR_BODPROZENT"), // 25
                                    resultHonorar.getInt("HONORAR_BODFIX"), // 26
                                    resultHonorar.getBoolean("HONORAR_GESAMTBETRACHTUNG"), // 27
                                    false // 28
                            );
                        }
                    } // while 
                    Log("");
                    Log("   -> Briefe sind erstellt");
                    Log("");
                    
                    // prüfen auf verrechnungen
                    Log("7. Bestellungen nach Verrechnungen durchsuchen ...");
                    progress = 60;
                    resultVerrechnung = SQLVerrechnung.executeQuery("SELECT * FROM TBL_VERRECHNUNG");
                    resultBestellung2 = SQLBestellung2.executeQuery("SELECT * FROM TBL_BESTELLUNG"
                            + " WHERE BESTELLUNG_BEZAHLUNG = '1'"
                            + " GROUP BY BESTELLUNG_KUNDE");
                    Log("   -> zu verrechnende Autoren ermittelt");
                    int ID = 1;
                    while (resultBestellung2.next()) {
                        progress = progress + 1;  if (progress<100) {setProgress(progress);}
                        SQLHonorar.executeUpdate("DELETE FROM TBL_VERRECHNUNG");
                        Log("   -> Tabelle Verrechnung ist geleert");
                        ID = 1;

                        resultBestellung = SQLBestellung.executeQuery("SELECT * FROM TBL_BESTELLUNG"
                                + " WHERE BESTELLUNG_KUNDE = '" + resultBestellung2.getString("BESTELLUNG_KUNDE") + "'");
                        Log("   -> zu verrechnende Rechnungen der Autoren ermitteln ...");
                        while (resultBestellung.next()) {
                            Betrag19 = 0F;
                            Betrag7 = 0F;
                            // Tabelle Verrechnung füllen mit Bestellungen    
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
                                    Buchtitel = resultBuch.getString("BUCH_TITEL");
                                }
                            }
// Rechnungsbetrag festlegen
// DEU:       Geschäft = Privat = Netto + UStr
// EU:        Geschäft = Netto
//            Privat   = Netto + UStr
// Drittland: Geschäft = Privat = Netto
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
                            resultVerrechnung.updateString("VERRECHNUNG_TITEL", Buchtitel);
                            resultVerrechnung.updateBoolean("VERRECHNUNG_RECHNUNG", true);
                            resultVerrechnung.insertRow();

                            ID = ID + 1;
                        } // while resultBestellung
                        Log("   -> Anschreiben erstellen für Autor " + resultBestellung2.getString("BESTELLUNG_KUNDE") + " ...");
                        switch (resultBestellung2.getString("BESTELLUNG_KUNDE")) {
                            case "-1":
                                break;
                            case "0":
                                Log("   -> Autor " + resultBestellung2.getString("BESTELLUNG_KUNDE") + " existiert nicht!");
                                break;
                            default:
                                briefVerrechnungHonorar.briefPDF(resultBestellung2.getString("BESTELLUNG_KUNDE"));
                                break;
                        }
                        
                    } // while resultVerrechnung.next

                    SQLHonorar.close();
                    SQLBuch.close();

                    resultHonorar.close();
                    resultBuch.close();
                } catch (SQLException ex) {
                    Log("   -> SQL-Exception: " + ex.getMessage());
                    ModulHelferlein.Fehlermeldung("Abrechnung erstellen", "SQL-Exception", ex.getMessage());
                } catch (IOException ex) {
                    Log("   -> IO-Exception: " + ex.getMessage());
                    ModulHelferlein.Fehlermeldung("Abrechnung erstellen", "IO-Exception", ex.getMessage());
                }
                ModulHelferlein.Infomeldung("Die Briefe mit der Abrechnung sind als PDF gespeichert!");
                if (buchFehltInBuchDB) {
                    ModulHelferlein.Fehlermeldung("Die Honorar-Abrechnung ist unvollständig", "Es fehlen folgende Bücher in der Buch-Datenbank", fehlendeBuecher);
                } else {
                    Log("Es fehlen keine Bücher in der BUCH-Datenbank");
                }
            } // conn steht 
            
            setProgress(100);
            return null;
        }
 
        /*
         * Executed in event dispatching thread
         */
        @Override
        public void done() {
            Toolkit.getDefaultToolkit().beep();
            startButton.setEnabled(true);
            setCursor(null); //turn off the wait cursor
            //taskOutput.append("Done!\n");
            
            frame.setVisible(false);
            frame.dispose();
        }
    }
 
    public ModulHonorarProgressBar() {
        super(new BorderLayout());
 
        //Create the demo's UI.
        startButton = new JButton("Start");
        startButton.setActionCommand("start");
        startButton.addActionListener(this);

        progressBar = new JProgressBar(0, 100);
        progressBar.setMaximum(100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
 
        //taskOutput = new JTextArea(5, 20);
        //taskOutput.setMargin(new Insets(5,5,5,5));
        //taskOutput.setEditable(false);
 
        JPanel panel = new JPanel();
        panel.add(startButton);
        panel.add(progressBar);
 
        add(panel, BorderLayout.PAGE_START);
        //add(new JScrollPane(taskOutput), BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    }
 
    /**
     * Invoked when the user presses the start button.
     * @param evt
     */
    @Override
    public void actionPerformed(ActionEvent evt) {
        startButton.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        //Instances of javax.swing.SwingWorker are not reusuable, so
        //we create new instances as needed.
        chooser.setMultiSelectionEnabled(false);
        chooser.setFileFilter(filter);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int Ergebnis = chooser.showDialog(null, "SALES.CSV wählen");

        if (Ergebnis == JFileChooser.APPROVE_OPTION) {
            task = new Task();
            task.addPropertyChangeListener(this);
            task.execute();
        }
    }
 
    /**
     * Invoked when task's progress property changes.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ( evt.getPropertyName().equals("progress")) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
            //taskOutput.append(String.format("Completed %d%% of task.\n", task.getProgress()));
        } 
    }
 
 
    /**
     * Create the GUI and show it. As with all GUI code, this must run
     * on the event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        //JFrame frame = new JFrame("Abrechnung ...");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
 
        //Create and set up the content pane.
        JComponent newContentPane = new ModulHonorarProgressBar();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);
 
        //Display the window.
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);
  }
 
    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }
}