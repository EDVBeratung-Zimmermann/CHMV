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

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import jxl.Workbook;
import static jxl.format.Alignment.LEFT;
import static jxl.format.Alignment.RIGHT;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import static milesVerlagMain.ModulHelferlein.Linie;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import static org.apache.pdfbox.pdmodel.common.PDRectangle.A4;
import static milesVerlagMain.ModulHelferlein.AusgabeLB;
import static milesVerlagMain.ModulHelferlein.Ausgabe;


/**
 * Klasse zur Erzeugung einer Bestandsübersicht der Bücher des miles Verlages
 *
 * @author Thomas Zimmermann
 *
 */
public class berBuchbestand {

    /**
     * Erzeugt die Übersicht der Bücher des miles Verlages
     *
     * @param Sortierung
     */
    public static void berichtXLS(String Sortierung) {
        String outputFileName = ModulHelferlein.pathBerichte + "\\Inventur\\"
                + "Inventur-"
                + ModulHelferlein.printSimpleDateFormat("yyyyMMdd")
                + "-" + Sortierung
                + ".xls";
        Connection conn = null;

        try { // Datenbank-Treiber laden
            Class.forName(ModulHelferlein.dbDriver);
        } catch (ClassNotFoundException exept) {
            ModulHelferlein.Fehlermeldung("Bericht Inventur",
                    "ClassNotFoundException: Treiber nicht gefunden. ",
                    exept.getMessage());
        } // Datenbank-Treiber laden

        try { // Verbindung zur Datenbank über die JDBC-Brücke
            conn = DriverManager.getConnection(ModulHelferlein.dbUrl, ModulHelferlein.dbUser, ModulHelferlein.dbPassword);
        } catch (SQLException exept) {
            ModulHelferlein.Fehlermeldung("Bericht Inventur",
                    "SQL-Exception: Verbindung nicht moeglich: ",
                    exept.getMessage());
        } // Verbindung zur Datenbank über die JDBC-Brücke

        final Connection conn2 = conn;

        if (conn2 != null) { //verbindung steht
            Statement SQLAnfrage = null; // Anfrage erzeugen
            Statement SQLAnfrageAdresse = null; // Anfrage erzeugen
            Statement SQLAnfrageHilfe = null;
            ResultSet result = null;
            ResultSet resultAdresse;
            ResultSet resultHilfe = null;

            Integer ID = 0;

            try { //SQL-Anfrage
                SQLAnfrage = conn2.createStatement(); // Anfrage der DB conn2 zuordnen
                SQLAnfrageAdresse = conn2.createStatement(); // Anfrage der DB conn2 zuordnen
                SQLAnfrageHilfe = conn2.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
                if (Sortierung.equals("ISBN")) {
                    result = SQLAnfrage.executeQuery(
                            "SELECT * FROM TBL_BUCH ORDER BY BUCH_ISBN");
                } else {
                    // neue Hilfstabelle erzeugen
                    SQLAnfrageHilfe.executeUpdate("DELETE FROM TBL_HILFE_BUCH");
                    resultHilfe = SQLAnfrageHilfe.executeQuery("SELECT * FROM TBL_HILFE_BUCH");
                    result = SQLAnfrage.executeQuery("SELECT * FROM tbl_buch");
                    // Hilfstabelle füllen
                    while (result.next()) {
                        resultHilfe.moveToInsertRow();

                        resultHilfe.updateInt("BUCH_ID", ID);
                        resultHilfe.updateString("BUCH_TITEL", result.getString("BUCH_TITEL"));
                        resultHilfe.updateString("BUCH_ISBN", result.getString("BUCH_ISBN"));
                        resultHilfe.updateString("BUCH_COVER", result.getString("BUCH_COVER"));
                        resultHilfe.updateString("BUCH_JAHR", result.getString("BUCH_JAHR"));
                        resultHilfe.updateInt("BUCH_BESTAND", result.getInt("BUCH_BESTAND"));
                        String[] col_Autorliste = result.getString("BUCH_AUTOR").split(",");
                        String AutorEintrag = "";
                        for (String strAutor : col_Autorliste) {
                            resultAdresse = SQLAnfrageAdresse.executeQuery(
                                    "SELECT * FROM tbl_adresse WHERE ADRESSEN_ID = " + strAutor);
                            resultAdresse.next();
                            AutorEintrag = AutorEintrag
                                    + resultAdresse.getString("ADRESSEN_Name") + ", "
                                    + resultAdresse.getString("ADRESSEN_Vorname") + "; ";
                        }
                        AutorEintrag = AutorEintrag.substring(0, AutorEintrag.length() - 2);
                        if (result.getBoolean("BUCH_HERAUSGEBER")) {
                            AutorEintrag = AutorEintrag + " (Hrsg.)";
                        }
                        resultHilfe.updateString("BUCH_AUTOR", AutorEintrag);
                        resultHilfe.updateFloat("BUCH_PREIS", result.getFloat("BUCH_PREIS"));
                        resultHilfe.updateFloat("BUCH_EK", result.getFloat("BUCH_EK"));
                        resultHilfe.updateInt("BUCH_SEITEN", result.getInt("BUCH_SEITEN"));
                        resultHilfe.updateInt("BUCH_HC", result.getInt("BUCH_HC"));
                        resultHilfe.updateInt("BUCH_AUFLAGE", result.getInt("BUCH_AUFLAGE"));

                        resultHilfe.insertRow();

                        ID = ID + 1;
                    }
                    // Hilfstabelle nach Autor sortieren ins Resultset:
                    result = SQLAnfrage.executeQuery("SELECT * FROM tbl_HILFE_BUCH ORDER BY BUCH_AUTOR, BUCH_JAHR, BUCH_ISBN");
                }
                // result ist erstellt und kann nun ausgegeben werden
                WritableWorkbook workbook = Workbook.createWorkbook(new File(outputFileName));
                WritableSheet sheet_Uebersicht = workbook.createSheet("Inventur", 0);
                WritableFont arial14fontBold = new WritableFont(WritableFont.ARIAL, 14, WritableFont.BOLD);
                WritableCellFormat arial14formatBold = new WritableCellFormat(arial14fontBold);

                WritableFont arial10fontBold = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
                WritableCellFormat arial10formatBold = new WritableCellFormat(arial10fontBold);

                WritableFont arial10font = new WritableFont(WritableFont.ARIAL, 10);
                WritableCellFormat arial10formatR = new WritableCellFormat(arial10font);
                arial10formatR.setAlignment(RIGHT);
                WritableCellFormat arial10formatL = new WritableCellFormat(arial10font);
                arial10formatL.setAlignment(LEFT);

                // Aufbau des Tabellenblattes sheet_Uebersicht
                Label label = new Label(0, 0, "Übersicht des Buchbestandes", arial14formatBold);
                sheet_Uebersicht.addCell(label);

                label = new Label(0, 2, "ISBN", arial10formatBold);
                sheet_Uebersicht.addCell(label);
                label = new Label(1, 2, "Autor/Herausgeber", arial10formatBold);
                sheet_Uebersicht.addCell(label);
                label = new Label(2, 2, "Typ", arial10formatBold);
                sheet_Uebersicht.addCell(label);
                label = new Label(3, 2, "Titel", arial10formatBold);
                sheet_Uebersicht.addCell(label);
                label = new Label(4, 2, "Bestand", arial10formatBold);
                sheet_Uebersicht.addCell(label);
                label = new Label(5, 2, "Wert EK", arial10formatBold);
                sheet_Uebersicht.addCell(label);
                label = new Label(6, 2, "Wert VK", arial10formatBold);
                sheet_Uebersicht.addCell(label);

                Integer zeile = 2;

                while (result.next()) {
                    zeile = zeile + 1;
                    label = new Label(0, zeile, result.getString("BUCH_ISBN"), arial10formatL);
                    sheet_Uebersicht.addCell(label);

                    if (Sortierung.equals("ISBN")) {
                        resultAdresse = SQLAnfrageAdresse.executeQuery("SELECT * FROM TBL_ADRESSE WHERE ADRESSEN_ID ='" + result.getString("BUCH_AUTOR") + "'");
                        resultAdresse.next();
                        label = new Label(1, zeile, resultAdresse.getString("ADRESSEN_NAME") + ", " + resultAdresse.getString("ADRESSEN_VORNAME"));
                        sheet_Uebersicht.addCell(label);
                    } else {
                        label = new Label(1, zeile, result.getString("BUCH_AUTOR"));
                        sheet_Uebersicht.addCell(label);
                    }

                    switch (result.getInt("BUCH_HC")) {
                        case 0:
                            label = new Label(2, zeile, "PB");
                            sheet_Uebersicht.addCell(label);
                            break;
                        case 1:
                            label = new Label(2, zeile, "HC");
                            sheet_Uebersicht.addCell(label);
                            break;
                        case 2:
                            label = new Label(2, zeile, "eB");
                            sheet_Uebersicht.addCell(label);
                            break;
                    }

                    label = new Label(3, zeile, result.getString("BUCH_TITEL"));
                    sheet_Uebersicht.addCell(label);

                    jxl.write.Number number = new jxl.write.Number(4, zeile, result.getInt("BUCH_BESTAND"), arial10formatR);
                    sheet_Uebersicht.addCell(number);

                    number = new jxl.write.Number(5, zeile, result.getInt("BUCH_BESTAND") * result.getFloat("BUCH_EK"), arial10formatR);
                    sheet_Uebersicht.addCell(number);

                    number = new jxl.write.Number(6, zeile, result.getInt("BUCH_BESTAND") * result.getFloat("BUCH_PREIS"), arial10formatR);
                    sheet_Uebersicht.addCell(number);
                } // while

                // Fertig - alles schließen
                try {// workbook write
                    workbook.write();
                } catch (IOException e) {
                    ModulHelferlein.Fehlermeldung("XLS-Bericht Buchbestand", "IO-Exception: ", e.getMessage());
                } // workbook write

                try { // try workbook close
                    workbook.close();
                } catch (IOException e) {
                    ModulHelferlein.Fehlermeldung("XLS-Bericht Buchbestand", "IO-Exception: ", e.getMessage());
                } // try workbook close

                ModulHelferlein.Infomeldung("XLS-Bericht Buchbestand","gespeichert unter",outputFileName);
                
                try { // try XLS anzeigen
                    Runtime.getRuntime().exec("cmd.exe /c " + "\"" + outputFileName + "\"");
                } catch (IOException exept) {
                    ModulHelferlein.Fehlermeldung("Bericht Buchbestand", "Anzeige XLS-Export: Exception: ", exept.getMessage());
                } // try XLS anzeigen

                // 
            } catch (SQLException exept) {
                ModulHelferlein.Fehlermeldung("Bericht Inventur XLS",
                        "SQL-Exception: Verbindung nicht moeglich: ",
                        exept.getMessage());
            } catch (IOException ex) {
                ModulHelferlein.Fehlermeldung("Bericht Inventur XLS",
                        "IO-Exception: ",
                        ex.getMessage());
            } catch (WriteException ex) {
                ModulHelferlein.Fehlermeldung("Bericht Inventur XLS",
                        "Write-Exception: ",
                        ex.getMessage());
            }
        }
    } // berichtXLS

    /**
     * Erzeugt die Übersicht der Bücher des miles Verlages
     *
     * @param Sortierung
     */
    public static void berichtPDF(String Sortierung) {

        PDDocument document = new PDDocument();
        PDPage page1 = new PDPage(A4);
        document.addPage(page1);

        // Start a new content stream which will "hold" the to be created content
        try { //Dokument erstellen
            PDPageContentStream cos = new PDPageContentStream(document, page1);

            String outputFileName = ModulHelferlein.pathBerichte + "\\Inventur\\"
                    + "Inventur-"
                    + ModulHelferlein.printSimpleDateFormat("yyyyMMdd")
                    + "-" + Sortierung
                    + ".pdf";

            PDDocumentInformation docInfo = document.getDocumentInformation();

            docInfo.setSubject("Buchbestand");
            docInfo.setTitle("miles-Verlag Stammdaten");
            docInfo.setAuthor("miles-Verlag");
            docInfo.setCreationDate(Calendar.getInstance());
            docInfo.setCreator("miles-Verlag");
            docInfo.setProducer("miles-Verlag");

            PDFont fontPlain = PDType1Font.HELVETICA;
            PDFont fontBold = PDType1Font.HELVETICA_BOLD;

            Integer zeile = 1;
            Integer seite = 1;

            String strZeile = "";

            // Einfügen der Texte und Bilder
            Connection conn = null;

            try { // Datenbank-Treiber laden
                Class.forName(ModulHelferlein.dbDriver);
            } catch (ClassNotFoundException exept) {
                ModulHelferlein.Fehlermeldung("Bericht Inventur",
                        "ClassNotFoundException: Treiber nicht gefunden. ",
                        exept.getMessage());
            } // Datenbank-Treiber laden

            try { // Verbindung zur Datenbank über die JDBC-Brücke
                conn = DriverManager.getConnection(ModulHelferlein.dbUrl, ModulHelferlein.dbUser, ModulHelferlein.dbPassword);
            } catch (SQLException exept) {
                ModulHelferlein.Fehlermeldung("Bericht Inventur",
                        "SQL-Exception: Verbindung nicht moeglich: ",
                        exept.getMessage());
            } // Verbindung zur Datenbank über die JDBC-Brücke

            final Connection conn2 = conn;

            if (conn2 != null) { //verbindung steht
                Statement SQLAnfrage = null; // Anfrage erzeugen
                Statement SQLAnfrageAdresse = null; // Anfrage erzeugen
                Statement SQLAnfrageHilfe = null;
                ResultSet result = null;
                ResultSet resultAdresse;
                ResultSet resultHilfe = null;

                Integer ID = 0;

                try { //SQL-Anfrage
                    SQLAnfrage = conn2.createStatement(); // Anfrage der DB conn2 zuordnen
                    SQLAnfrageAdresse = conn2.createStatement(); // Anfrage der DB conn2 zuordnen
                    SQLAnfrageHilfe = conn2.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                            ResultSet.CONCUR_UPDATABLE);
                    if (Sortierung.equals("ISBN")) {
                        result = SQLAnfrage.executeQuery(
                                "SELECT * FROM TBL_BUCH ORDER BY BUCH_ISBN");
                    } else {
                        // neue Hilfstabelle erzeugen
                        SQLAnfrageHilfe.executeUpdate("DELETE FROM TBL_HILFE_BUCH");
                        resultHilfe = SQLAnfrageHilfe.executeQuery("SELECT * FROM TBL_HILFE_BUCH");
                        result = SQLAnfrage.executeQuery("SELECT * FROM tbl_buch");
                        // Hilfstabelle füllen
                        while (result.next()) {
                            resultHilfe.moveToInsertRow();

                            resultHilfe.updateInt("BUCH_ID", ID);
                            resultHilfe.updateString("BUCH_TITEL", result.getString("BUCH_TITEL"));
                            resultHilfe.updateString("BUCH_ISBN", result.getString("BUCH_ISBN"));
                            resultHilfe.updateString("BUCH_COVER", result.getString("BUCH_COVER"));
                            resultHilfe.updateString("BUCH_JAHR", result.getString("BUCH_JAHR"));
                            resultHilfe.updateInt("BUCH_BESTAND", result.getInt("BUCH_BESTAND"));
                            String[] col_Autorliste = result.getString("BUCH_AUTOR").split(",");
                            String AutorEintrag = "";
                            for (String strAutor : col_Autorliste) {
                                resultAdresse = SQLAnfrageAdresse.executeQuery(
                                        "SELECT * FROM tbl_adresse WHERE ADRESSEN_ID = " + strAutor);
                                resultAdresse.next();
                                AutorEintrag = AutorEintrag
                                        + resultAdresse.getString("ADRESSEN_Name") + ", "
                                        + resultAdresse.getString("ADRESSEN_Vorname") + "; ";
                            }
                            AutorEintrag = AutorEintrag.substring(0, AutorEintrag.length() - 2);
                            if (result.getBoolean("BUCH_HERAUSGEBER")) {
                                AutorEintrag = AutorEintrag + " (Hrsg.)";
                            }
                            resultHilfe.updateString("BUCH_AUTOR", AutorEintrag);
                            resultHilfe.updateFloat("BUCH_PREIS", result.getFloat("BUCH_PREIS"));
                            resultHilfe.updateFloat("BUCH_EK", result.getFloat("BUCH_EK"));
                            resultHilfe.updateInt("BUCH_SEITEN", result.getInt("BUCH_SEITEN"));
                            resultHilfe.updateInt("BUCH_HC", result.getInt("BUCH_HC"));
                            resultHilfe.updateInt("BUCH_AUFLAGE", result.getInt("BUCH_AUFLAGE"));

                            resultHilfe.insertRow();

                            ID = ID + 1;
                        }
                        // Hilfstabelle nach Autor sortieren ins Resultset:
                        result = SQLAnfrage.executeQuery("SELECT * FROM tbl_HILFE_BUCH ORDER BY BUCH_AUTOR, BUCH_JAHR, BUCH_ISBN");
                    }
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 770, "miles-Verlag Verlagsverwaltung - Stammdaten");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 455, 770, "Seite: " + Integer.toString(seite));
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 755, "Buchbestand/Inventur, Stand: "
                            + ModulHelferlein.printSimpleDateFormat(
                                    "dd.MM.yyyy"));

                    Linie(cos,2,56, 750, 539, 750);
                    
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 735, "ISBN");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 150, 735, "Autor");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 250, 735, "Typ");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 275, 735, "Titel");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 500, 735, "Bestand");

                    while (result.next()) { // geht durch alle zeilen
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 55, 735 - zeile * 20, result.getString("BUCH_ISBN"), 92);

                        if (Sortierung.equals("ISBN")) {
                            resultAdresse = SQLAnfrageAdresse.executeQuery("SELECT * FROM TBL_ADRESSE WHERE ADRESSEN_ID ='" + result.getString("BUCH_AUTOR") + "'");
                            resultAdresse.next();
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 150, 735 - zeile * 20, resultAdresse.getString("ADRESSEN_NAME") + ", " + resultAdresse.getString("ADRESSEN_VORNAME"), 95);
                        } else {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 150, 735 - zeile * 20, result.getString("BUCH_AUTOR"), 95);
                        }

                        switch (result.getInt("BUCH_HC")) {
                            case 0:
                                Ausgabe(cos, fontPlain, 12, Color.BLACK, 250, 735 - zeile * 20, "PB", 20);
                                break;
                            case 1:
                                Ausgabe(cos, fontPlain, 12, Color.BLACK, 250, 735 - zeile * 20, "HC", 20);
                                break;
                            case 2:
                                Ausgabe(cos, fontPlain, 12, Color.BLACK, 250, 735 - zeile * 20, "eB", 20);
                                break;
                        }

                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 275, 735 - zeile * 20, result.getString("BUCH_TITEL"), 220);
                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 526, 735 - zeile * 20, Integer.toString(result.getInt("BUCH_BESTAND")));

                        zeile = zeile + 1;

                        if (zeile > 35) {
                            zeile = 1;
                            seite = seite + 1;
                            cos.close();
                            PDPage page = new PDPage(A4);
                            document.addPage(page);
                            cos = new PDPageContentStream(document, page);

                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 770, "miles-Verlag Verlagsverwaltung - Stammdaten");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 455, 770, "Seite: " + Integer.toString(seite));
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 755, "Buchbestand/Inventur, Stand: "
                                    + ModulHelferlein.printSimpleDateFormat(
                                            "dd.MM.yyyy"));
                            Linie(cos,2,56, 750, 539, 750);
                            
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 735, "ISBN");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 150, 735, "Autor");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 250, 735, "Typ");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 275, 735, "Titel");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 500, 735, "Bestand");
                        }
                    } //while

                    // close the content stream for page 1
                    cos.close();

                    // Save the results and ensure that the document is properly closed:
                    document.save(outputFileName);
                    document.close();

                    ModulHelferlein.Infomeldung(
                            "Bestandsliste der Bücher ist als PDF gespeichert!");
                    try { //Ausgabe der Liste
                        Runtime.getRuntime().exec("cmd.exe /c " + "\"" + outputFileName + "\"");
                    } catch (IOException exept) {
                        ModulHelferlein.Fehlermeldung("Bericht Inventur",
                                "Exception: ", exept.getMessage());
                    }//try AusgabeLB der Liste

                } catch (SQLException exept) {
                    ModulHelferlein.Fehlermeldung("Bericht Inventur",
                            "SQL-Exception: SQL-Anfrage nicht möglich: ",
                            exept.getMessage());
                } catch (IOException  e) {
                    ModulHelferlein.Fehlermeldung("Bericht Inventur", "IO-Exception: ", e.getMessage());
                } //SQL-Anfrage

            } //if verbindung steht

        } catch (IOException e) {
            ModulHelferlein.Fehlermeldung("Bericht Inventur", "FileNotFoundException: ", e.getMessage());
        } //try Dokument erstellen

    } //void bericht

} //class

