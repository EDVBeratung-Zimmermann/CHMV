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
import static milesVerlagMain.briefHonorar.conn;
import static milesVerlagMain.Modulhelferlein.Ausgabe;
import static milesVerlagMain.Modulhelferlein.AusgabeDB;
import static milesVerlagMain.Modulhelferlein.AusgabeRB;
import static milesVerlagMain.Modulhelferlein.Linie;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

/**
 * Klasse zur Erzeugung einer Liste von offenen Einnahmen
 *
 * @author Thomas Zimmermann
 *
 */
public class berVerkaufEinzel {

    static Statement SQLBuch = null;
    static Statement SQLAutor = null;
    static Statement SQLKunde = null;
    static Statement SQLBestellungDetails = null;
    static Statement SQLBestellung = null;
    static Statement SQLVerkauf = null;
    static Statement SQLVerkauf2 = null;

    static ResultSet resultBuch = null;
    static ResultSet resultAutor = null;
    static ResultSet resultKunde = null;
    static ResultSet resultBestellungDetails = null;
    static ResultSet resultBestellung = null;
    static ResultSet resultVerkauf = null;
    static ResultSet resultVerkauf2 = null;

    private static void berichtPDF(String buchISBN, String strVon, String strBis) {
        Modulhelferlein.Infomeldung("PDF noch nicht implementiert");
    }

    private static void berichtXLS(String buchISBN, String strVon, String strBis) {
        try {
            String outputFileName = Modulhelferlein.pathBerichte + "/Verkäufe/"
                    + "Verkaufsstatistik-CHMV"
                    + "-"
                    + buchISBN
                    + "-"
                    + strVon
                    + "-"
                    + strBis
                    + "-"
                    + Modulhelferlein.printSimpleDateFormat("yyyyMMdd")
                    + ".xls";
            WritableWorkbook workbook = Workbook.createWorkbook(new File(outputFileName));
            WritableSheet sheet_Verkauf = workbook.createSheet("Verkaufsstatistik", 0);

            // A3 = 0, 2
            // D5 = 3, 4
            // Number number = new Number(3, 4, 3.1459);
            // sheet.addCell(number);
            // Aufbau der Tabellenblätter
            // Create a cell format for Arial 10 point font
            WritableFont arial14fontBold = new WritableFont(WritableFont.ARIAL, 14, WritableFont.BOLD);
            WritableCellFormat arial14formatBold = new WritableCellFormat(arial14fontBold);

            WritableFont arial12fontBold = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD);
            WritableCellFormat arial12formatBold = new WritableCellFormat(arial12fontBold);

            WritableFont arial10fontBold = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
            WritableCellFormat arial10formatBold = new WritableCellFormat(arial10fontBold);

            WritableFont arial10font = new WritableFont(WritableFont.ARIAL, 10);
            WritableCellFormat arial10formatR = new WritableCellFormat(arial10font);
            arial10formatR.setAlignment(RIGHT);
            WritableCellFormat arial10formatL = new WritableCellFormat(arial10font);
            arial10formatL.setAlignment(LEFT);

            Connection conn = null;

            try { // Datenbank-Treiber laden
                Class.forName(Modulhelferlein.dbDriver);
            } catch (ClassNotFoundException exept) {
                Modulhelferlein.Fehlermeldung("Bericht Verkaufsstatisitik", "ClassNotFound-Exception: Treiber nicht gefunden: ", exept.getMessage());
            } // try Datenbank-Treiber laden

            try { // Verbindung zur Datenbank über die JDBC-Brücke
                conn = DriverManager.getConnection(Modulhelferlein.dbUrl, Modulhelferlein.dbUser, Modulhelferlein.dbPassword);
            } catch (SQLException exept) {
                Modulhelferlein.Fehlermeldung("Bericht Verkaufsstatisitik", "SQL-Exception: Verbindung nicht moeglich: ", exept.getMessage());
            } // try Verbindung zur Datenbank über die JDBC-Brücke

            final Connection conn2 = conn;

            if (conn2 != null) { // Datenbankverbindung steht
                SQLBuch = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                SQLKunde = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                SQLAutor = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                SQLBestellung = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                SQLBestellungDetails = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

                if (buchISBN.contains("---")) {
                    resultBuch = SQLBuch.executeQuery("SELECT * FROM TBL_BUCH WHERE BUCH_ID <> '0' ORDER BY BUCH_ISBN");
                    System.out.println("Ausgabe für alle Bücher von " + strVon + " bis " + strBis);   
                } else {
                    resultBuch = SQLBuch.executeQuery("SELECT * FROM TBL_BUCH WHERE BUCH_ISBN = '" + buchISBN + "' ORDER BY BUCH_ISBN");
                    System.out.println("Ausgabe für " + buchISBN + " von " + strVon + " bis " + strBis);   
                }

                Label label = new Label(0, 0, "Carola Hartmann Miles-Verlag", arial14formatBold);
                sheet_Verkauf.addCell(label);
                label = new Label(0, 1, "Verkaufsstatistik Direktverkäufe von " + strVon + " - " + strBis, arial14formatBold);
                sheet_Verkauf.addCell(label);
                Integer zeile = 4;

                while (resultBuch.next()) { // schleife gehe durch alle Bücher
                    String ISBN = resultBuch.getString("BUCH_ISBN");
System.out.println("WHILE BUCH -> " + ISBN);
                    String Titel = resultBuch.getString("BUCH_TITEL");

                    label = new Label(0, zeile, ISBN, arial14formatBold);
                    sheet_Verkauf.addCell(label);
                    
                    label = new Label(5, zeile, Titel, arial14formatBold);
                    sheet_Verkauf.addCell(label);

                    zeile = zeile + 1;
                    String[] AutorenIDs = resultBuch.getString("BUCH_AUTOR").split(",");
                    String Autor = "";
                    for (int i = 0; i < AutorenIDs.length; i++) {
                        resultAutor = SQLAutor.executeQuery("SELECT * FROM TBL_ADRESSE "
                                + "WHERE ADRESSEN_ID = '"
                                + AutorenIDs[i] + "'");
                        resultAutor.next();
                        label = new Label(5, zeile, resultAutor.getString("ADRESSEN_NAME"), arial12formatBold);
                        sheet_Verkauf.addCell(label);
                        label = new Label(7, zeile, resultAutor.getString("ADRESSEN_VORNAME"), arial12formatBold);
                        sheet_Verkauf.addCell(label);
                
                        zeile = zeile + 1;
                    }
                    
                    zeile = zeile + 1;

                    label = new Label(1, zeile, "Rechnungsnummer", arial10formatBold);
                    sheet_Verkauf.addCell(label);
                    label = new Label(2, zeile, "Datum", arial10formatBold);
                    sheet_Verkauf.addCell(label);
                    label = new Label(3, zeile, "Art", arial10formatBold);
                    sheet_Verkauf.addCell(label);
                    label = new Label(4, zeile, "Anzahl", arial10formatBold);
                    sheet_Verkauf.addCell(label);
                    label = new Label(5, zeile, "Adressat", arial10formatBold);
                    sheet_Verkauf.addCell(label);


                    // hole alle Bestelldetails für BuchID
                    resultBestellungDetails = SQLBestellungDetails.executeQuery("SELECT * FROM TBL_BESTELLUNG_DETAIL"
                            + " WHERE BESTELLUNG_DETAIL_BUCH = '"
                            + resultBuch.getString("BUCH_ID") + "'"
                            + "AND (('" + strVon + "' <= BESTELLUNG_DETAIL_DATUM) "
                            + "AND (BESTELLUNG_DETAIL_DATUM <= '" + strBis + "'))");
                    while (resultBestellungDetails.next()) { // Schleife gehe durch alle Bestelldetails
                        zeile = zeile + 1;
                        label = new Label(1, zeile, resultBestellungDetails.getString("BESTELLUNG_DETAIL_RECHNR"), arial10formatL);
                        sheet_Verkauf.addCell(label);
                        label = new Label(2, zeile, resultBestellungDetails.getString("BESTELLUNG_DETAIL_DATUM"), arial10formatL);
                        sheet_Verkauf.addCell(label);
                        label = new Label(4, zeile, Integer.toString(resultBestellungDetails.getInt("BESTELLUNG_DETAIL_ANZAHL")), arial10formatR);
                        sheet_Verkauf.addCell(label);

                    // hole Bestellung mit Bestellnummer
                        resultBestellung = SQLBestellung.executeQuery("SELECT * FROM TBL_BESTELLUNG "
                                + " WHERE BESTELLUNG_RECHNR = '" + resultBestellungDetails.getString("BESTELLUNG_DETAIL_RECHNR") + "'");
                        resultBestellung.next();
                        // if Bestelltyp != Bestellung => AnzahlGeschenk = ANzahlGescehnk + 1
                        switch (resultBestellung.getInt("BESTELLUNG_TYP")) {
                            case 0: //Bestellung
                                label = new Label(3, zeile, "Bestellung", arial10formatL);
                                sheet_Verkauf.addCell(label);
                                break;
                            case 1: //Rezension
                                label = new Label(3, zeile, "Rezension", arial10formatL);
                                sheet_Verkauf.addCell(label);
                                break;
                            case 2: //Pflicht
                                label = new Label(3, zeile, "Pflichtexemplar", arial10formatL);
                                sheet_Verkauf.addCell(label);
                                break;
                            case 3: //Geschenk
                                label = new Label(3, zeile, "Geschenk", arial10formatL);
                                sheet_Verkauf.addCell(label);
                                break;
                            case 4: //Beleg
                                label = new Label(3, zeile, "Belegexemplar", arial10formatL);
                                sheet_Verkauf.addCell(label);
                                break;
                        } // if

                        // if Kunde = Teil vom Autor => ANzahlAutor = AnzahlAutor +1
                        if (resultBestellung.getInt("BESTELLUNG_KUNDE") > 0) {
                            resultKunde = SQLKunde.executeQuery("SELECT * FROM TBL_ADRESSE "
                                    + "WHERE ADRESSEN_ID = '"
                                    + resultBestellung.getString("BESTELLUNG_KUNDE") + "'");
                            resultKunde.next();
                            label = new Label(5, zeile, resultKunde.getString("ADRESSEN_NAME") + ", " + resultKunde.getString("ADRESSEN_VORNAME"), arial10formatL);
                            sheet_Verkauf.addCell(label);
                        } else {
                            label = new Label(5, zeile, resultBestellung.getString("BESTELLUNG_ZEILE_2"), arial10formatL);
                            sheet_Verkauf.addCell(label);
                        }
                        //if (resultBuch.getString("BUCH_AUTOR").contains(resultBestellung.getString("BESTELLUNG_KUNDE"))) {
                        //    AnzahlAutor = AnzahlAutor + resultBestellungDetails.getInt("BESTELLUNG_DETAIL_ANZAHL");
                        //}
                    } // while resultBestellungDetails.next()
                    zeile = zeile + 4;
                } // while resultBuch.next()

                // Fertig - alles schließen
                try {// workbook write
                    workbook.write();
                } catch (IOException e) {
                    Modulhelferlein.Fehlermeldung("XLS-Bericht Verkaufsstatistik", "IO-Exception: ", e.getMessage());
                } // workbook write

                try { // try workbook close
                    workbook.close();
                } catch (IOException e) {
                    Modulhelferlein.Fehlermeldung("XLS-Bericht Verkaufsstatistik", "IO-Exception: ", e.getMessage());
                } // try workbook close

                try { // try XLS anzeigen
                    Runtime.getRuntime().exec("cmd.exe /c " + "\"" + outputFileName + "\"");
                } catch (IOException exept) {
                    Modulhelferlein.Fehlermeldung("Bericht Verkaufsstatistik", "Anzeige XLS-Export: Exception: ", exept.getMessage());
                } // try XLS anzeigen
            }
        } catch (IOException ex) {
            Modulhelferlein.Fehlermeldung("Verkaufsstatistik Excel", "IO-Exception", ex.getMessage());
        } catch (WriteException ex) {
            Modulhelferlein.Fehlermeldung("Verkaufsstatistik Excel", "Write-Exception", ex.getMessage());
        } catch (SQLException ex) {
            Modulhelferlein.Fehlermeldung("Verkaufsstatistik Excel", "SQL-Exception", ex.getMessage());
        }
    }

    private static void berichtDOC(String buchISBN, String strVon, String strBis) {
        Modulhelferlein.Infomeldung("DOC noch nicht implementiert");
    }

    /**
     * Erzeugt eine Übersicht der BoD-Verkäufe für ein einzelnes Buch
     *
     * @param buchISBN
     * @param Format
     * @param strVon Beginn des Zeitraums
     * @param strBis Ende des Zeitraums
     */
    public static void bericht(String buchISBN, String Format, String strVon, String strBis) {
        switch (Format) {
            case "PDF":
                berichtPDF(buchISBN, strVon, strBis);
                break;
            case "XLS":
                berichtXLS(buchISBN, strVon, strBis);
                break;
            default:
                berichtDOC(buchISBN, strVon, strBis);
                break;
        }
    } // void
} // class

