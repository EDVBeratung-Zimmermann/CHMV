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
import static milesVerlagMain.Modulhelferlein.Ausgabe;
import static milesVerlagMain.Modulhelferlein.AusgabeDB;
import static milesVerlagMain.Modulhelferlein.Linie;
import static milesVerlagMain.Modulhelferlein.SQLDate2Normal;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

/**
 * Klasse zur Erzeugung einer Ausgabenübersicht
 *
 * @author Thomas Zimmermann
 *
 */
public class berAusgaben {

    /**
     * Erzeugt eine Ausgabenübersicht für den Zeitraum strVon bis strBis im MS
     * EXCEL-Format
     *
     * @param Umfang
     * @param strVon Startdatum
     * @param strBis Endedatum
     */
    public static void berichtXLS(boolean Umfang, final String strVon, final String strBis) {
        Double Gesamtsumme = 0D;
        Double Gesamteinnahmen = 0D;
        Double Gesamtzeile = 0D;

        Integer zeile = 0;

        String Sql = "";

        Statement SQLAusgaben = null;
        ResultSet resultAusgaben = null;

        String outputFileName;
        outputFileName = Modulhelferlein.pathBerichte + "\\Ausgaben\\"
                + "Liste-Ausgaben-"
                + Modulhelferlein.printSimpleDateFormat("yyyyMMdd") 
                + ".xls";

        try {
            WritableWorkbook workbook = Workbook.createWorkbook(new File(outputFileName));
            WritableSheet sheet_Uebersicht = workbook.createSheet("Übersicht", 0);
            WritableSheet sheet_Porto = workbook.createSheet("Portokosten", 1);
            WritableSheet sheet_Material = workbook.createSheet("Büromaterial", 2);
            WritableSheet sheet_Bestellung = workbook.createSheet("Buchbestellungen", 3);
            WritableSheet sheet_Honorar = workbook.createSheet("Honorarabrechnung", 4);
            WritableSheet sheet_Konto = workbook.createSheet("Kontoführung BOD", 5);
            WritableSheet sheet_Telefon = workbook.createSheet("Telefonkosten", 6);
            WritableSheet sheet_Raum = workbook.createSheet("Büro- und Lagerräume", 7);
            WritableSheet sheet_Erstellung = workbook.createSheet("Bucherstellung", 8);
            WritableSheet sheet_Fahrt = workbook.createSheet("Fahrtkosten", 9);
            WritableSheet sheet_Sonstig = workbook.createSheet("Sonstiges", 10);

            // A3 = 0, 2
            // D5 = 3, 4
            // Number number = new Number(3, 4, 3.1459);
            // sheet.addCell(number);
            // Aufbau der Tabellenblätter
            // Create a cell format for Arial 10 point font
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
            Label label = new Label(0, 0, "Übersicht der Ausgaben", arial14formatBold);
            sheet_Uebersicht.addCell(label);

            label = new Label(0, 2, "Ausgaben wegen Portokosten", arial10formatBold);
            sheet_Uebersicht.addCell(label);
            label = new Label(0, 3, "Ausgaben wegen Büromaterial", arial10formatBold);
            sheet_Uebersicht.addCell(label);
            label = new Label(0, 4, "Ausgaben wegen Buchbestellungen", arial10formatBold);
            sheet_Uebersicht.addCell(label);
            label = new Label(0, 5, "Ausgaben wegen Honorarabrechnungen", arial10formatBold);
            sheet_Uebersicht.addCell(label);
            label = new Label(0, 6, "Ausgaben wegen Kontoführung", arial10formatBold);
            sheet_Uebersicht.addCell(label);
            label = new Label(0, 7, "Ausgaben wegen Telefonkosten", arial10formatBold);
            sheet_Uebersicht.addCell(label);
            label = new Label(0, 8, "Ausgaben wegen Raummieten", arial10formatBold);
            sheet_Uebersicht.addCell(label);
            label = new Label(0, 9, "Ausgaben wegen Bucherstellung", arial10formatBold);
            sheet_Uebersicht.addCell(label);
            label = new Label(0, 10, "Ausgaben wegen Fahrtkosten", arial10formatBold);
            sheet_Uebersicht.addCell(label);
            label = new Label(0, 11, "Ausgaben wegen Sonstigem", arial10formatBold);
            sheet_Uebersicht.addCell(label);
            label = new Label(0, 13, "Gesamtsumme der Ausgaben", arial10formatBold);
            sheet_Uebersicht.addCell(label);

            Connection conn = null;

            try { // Datenbank-Treiber laden
                Class.forName(Modulhelferlein.dbDriver);
            } catch (ClassNotFoundException exept) {
                Modulhelferlein.Fehlermeldung("ClassNotFoundException: Treiber nicht gefunden: " ,exept.getMessage());
            } // try Datenbank-Treiber laden

            try { // Verbindung zur Datenbank über die JDBC-Brücke
                conn = DriverManager.getConnection(Modulhelferlein.dbUrl, Modulhelferlein.dbUser, Modulhelferlein.dbPassword);
            } catch (SQLException exept) {
                Modulhelferlein.Fehlermeldung("SQL-Exception: Verbindung nicht moeglich: " ,exept.getMessage());
            } // try Verbindung zur Datenbank über die JDBC-Brücke

            final Connection conn2 = conn;

            if (conn2 != null) { // Datenbankverbindung steht

                try { // Datenbankabfrage
                    SQLAusgaben = conn2.createStatement();

                    label = new Label(0, 0, "Ausgaben für Porto", arial14formatBold);
                    sheet_Porto.addCell(label);
                    label = new Label(0, 2, "Datum", arial10formatBold);
                    sheet_Porto.addCell(label);
                    label = new Label(1, 2, "RechnungsNr", arial10formatBold);
                    sheet_Porto.addCell(label);
                    label = new Label(2, 2, "Lieferant", arial10formatBold);
                    sheet_Porto.addCell(label);
                    label = new Label(3, 2, "Ausgaben", arial10formatBold);
                    sheet_Porto.addCell(label);
                    label = new Label(4, 2, "UStr", arial10formatBold);
                    sheet_Porto.addCell(label);
                    label = new Label(5, 2, "Bezahlt", arial10formatBold);
                    sheet_Porto.addCell(label);
                    label = new Label(6, 2, "Beschreibung", arial10formatBold);
                    sheet_Porto.addCell(label);

                    zeile = 3;
                    Gesamtsumme = 0D;

                    if (Umfang) {
                    Sql = "SELECT * FROM TBL_AUSGABEN"
                            + " WHERE ((AUSGABEN_RECHDATUM BETWEEN '" + strVon + "'  AND '" + strBis + "')"
                            + "       OR (AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'))"
                            + "       AND AUSGABEN_TYP = '0'"
                            + " ORDER BY AUSGABEN_RECHDATUM";
                    } else {
                    Sql = "SELECT * FROM TBL_AUSGABEN"
                            + " WHERE AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'"
                            + "       AND AUSGABEN_TYP = '0'"
                            + " ORDER BY AUSGABEN_RECHDATUM";
                    }
                    resultAusgaben = SQLAusgaben.executeQuery(Sql); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 

                    while (resultAusgaben.next()) { // geht durch alle zeilen
                        // Aufbau des Tabellenblattes sheet_<AUSGABEN>
                        // Datum - RechNr  Lieferant  Ausgaben  UStr  Bezahlt   Beschreibung
                        //  0        1         2         3        4      5           6
                        label = new Label(0, zeile, SQLDate2Normal(resultAusgaben.getString("AUSGABEN_RECHDATUM")), arial10formatL);
                        sheet_Porto.addCell(label);
                        label = new Label(1, zeile, resultAusgaben.getString("AUSGABEN_RECHNNR"), arial10formatL);
                        sheet_Porto.addCell(label);
                        label = new Label(2, zeile, resultAusgaben.getString("AUSGABEN_LIEFERANT"));
                        sheet_Porto.addCell(label);
                        Gesamtzeile = resultAusgaben.getFloat("AUSGABEN_KOSTEN") * 1D;
                        label = new Label(3, zeile, Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtzeile)), arial10formatR);
                        sheet_Porto.addCell(label);
                        label = new Label(4, zeile, resultAusgaben.getString("AUSGABEN_USTR"), arial10formatR);
                        sheet_Porto.addCell(label);

                        if (resultAusgaben.getString("AUSGABEN_BEZAHLT").equals("1970-01-01")) {
                            label = new Label(5, zeile, "----------", arial10formatL);
                            sheet_Porto.addCell(label);
                        } else {
                            label = new Label(5, zeile, SQLDate2Normal(resultAusgaben.getString("AUSGABEN_BEZAHLT")), arial10formatL);
                            sheet_Porto.addCell(label);
                        }
                        label = new Label(6, zeile, resultAusgaben.getString("AUSGABEN_BESCHREIBUNG"), arial10formatL);
                        sheet_Porto.addCell(label);

                        zeile = zeile + 1;
                        Gesamtsumme = Gesamtsumme + Gesamtzeile;
                    } // while resultAusgaben

                    // Gesamtsumme berechnen
                    zeile = zeile + 1;
                    Gesamteinnahmen = Gesamteinnahmen + Gesamtsumme;
                    label = new Label(0, zeile, "Gesamtsumme der Ausgaben für Porto", arial10formatR);
                    sheet_Porto.addCell(label);
                    label = new Label(3, zeile, Modulhelferlein.str2dec(Gesamtsumme), arial10formatR);
                    sheet_Porto.addCell(label);
                    
                    // Gesamtsumme auf Übersichtsblatt schreiben
                    label = new Label(3, 2, Modulhelferlein.str2dec(Gesamtsumme), arial10formatR);
                    sheet_Uebersicht.addCell(label);
                    
                    // Tabellenblatt Büromaterial - Typ 1
                    label = new Label(0, 0, "Ausgaben für Büromaterial", arial14formatBold);
                    sheet_Material.addCell(label);
                    label = new Label(0, 2, "Datum", arial10formatBold);
                    sheet_Material.addCell(label);
                    label = new Label(1, 2, "RechnungsNr", arial10formatBold);
                    sheet_Material.addCell(label);
                    label = new Label(2, 2, "Lieferant", arial10formatBold);
                    sheet_Material.addCell(label);
                    label = new Label(3, 2, "Ausgaben", arial10formatBold);
                    sheet_Material.addCell(label);
                    label = new Label(4, 2, "UStr", arial10formatBold);
                    sheet_Material.addCell(label);
                    label = new Label(5, 2, "Bezahlt", arial10formatBold);
                    sheet_Material.addCell(label);
                    label = new Label(6, 2, "Beschreibung", arial10formatBold);
                    sheet_Material.addCell(label);

                    zeile = 3;
                    Gesamtsumme = 0D;
                    
                    if (Umfang) {
                        Sql = "SELECT * FROM TBL_AUSGABEN"
                            + " WHERE ((AUSGABEN_RECHDATUM BETWEEN '" + strVon + "'  AND '" + strBis + "')"
                            + "       OR (AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'))"
                                + "       AND AUSGABEN_TYP = '1'"
                                + " ORDER BY AUSGABEN_RECHDATUM";
                    } else {
                        Sql = "SELECT * FROM TBL_AUSGABEN"
                                + " WHERE AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'"
                                + "       AND AUSGABEN_TYP = '1'"
                                + " ORDER BY AUSGABEN_RECHDATUM";
                    }
                    resultAusgaben = SQLAusgaben.executeQuery(Sql); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 

                    while (resultAusgaben.next()) { // geht durch alle zeilen
                        // Aufbau des Tabellenblattes sheet_<AUSGABEN>
                        // Datum - RechNr  Lieferant  Ausgaben  UStr  Bezahlt   Beschreibung
                        //  0        1         2         3        4      5           6
                        label = new Label(0, zeile, SQLDate2Normal(resultAusgaben.getString("AUSGABEN_RECHDATUM")), arial10formatL);
                        sheet_Material.addCell(label);
                        label = new Label(1, zeile, resultAusgaben.getString("AUSGABEN_RECHNNR"), arial10formatL);
                        sheet_Material.addCell(label);
                        label = new Label(2, zeile, resultAusgaben.getString("AUSGABEN_LIEFERANT"));
                        sheet_Material.addCell(label);
                        Gesamtzeile = resultAusgaben.getFloat("AUSGABEN_KOSTEN") * 1D;
                        label = new Label(3, zeile, Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtzeile)), arial10formatR);
                        sheet_Material.addCell(label);
                        label = new Label(4, zeile, resultAusgaben.getString("AUSGABEN_USTR"), arial10formatR);
                        sheet_Material.addCell(label);

                        if (resultAusgaben.getString("AUSGABEN_BEZAHLT").equals("1970-01-01")) {
                            label = new Label(5, zeile, "----------", arial10formatL);
                            sheet_Material.addCell(label);
                        } else {
                            label = new Label(5, zeile, SQLDate2Normal(resultAusgaben.getString("AUSGABEN_BEZAHLT")), arial10formatL);
                            sheet_Material.addCell(label);
                        }
                        label = new Label(6, zeile, resultAusgaben.getString("AUSGABEN_BESCHREIBUNG"), arial10formatL);
                        sheet_Material.addCell(label);

                        zeile = zeile + 1;
                        Gesamtsumme = Gesamtsumme + Gesamtzeile;
                    } // while resultAusgaben

                    // Gesamtsumme berechnen
                    zeile = zeile + 1;
                    Gesamteinnahmen = Gesamteinnahmen + Gesamtsumme;
                    label = new Label(0, zeile, "Gesamtsumme der Ausgaben für Büromaterial", arial10formatR);
                    sheet_Material.addCell(label);
                    label = new Label(3, zeile, Modulhelferlein.str2dec(Gesamtsumme), arial10formatR);
                    sheet_Material.addCell(label);
                    
                    // Gesamtsumme auf Übersichtsblatt schreiben
                    label = new Label(3, 3, Modulhelferlein.str2dec(Gesamtsumme), arial10formatR);
                    sheet_Uebersicht.addCell(label);
                    
                    
                    // Tabellenblatt Buchbestellung - Typ 2 
                    label = new Label(0, 0, "Ausgaben wegen Buchbestellungen", arial14formatBold);
                    sheet_Bestellung.addCell(label);
                    label = new Label(0, 2, "Datum", arial10formatBold);
                    sheet_Bestellung.addCell(label);
                    label = new Label(1, 2, "RechnungsNr", arial10formatBold);
                    sheet_Bestellung.addCell(label);
                    label = new Label(2, 2, "Lieferant", arial10formatBold);
                    sheet_Bestellung.addCell(label);
                    label = new Label(3, 2, "Ausgaben", arial10formatBold);
                    sheet_Bestellung.addCell(label);
                    label = new Label(4, 2, "UStr", arial10formatBold);
                    sheet_Bestellung.addCell(label);
                    label = new Label(5, 2, "Bezahlt", arial10formatBold);
                    sheet_Bestellung.addCell(label);
                    label = new Label(6, 2, "Beschreibung", arial10formatBold);
                    sheet_Bestellung.addCell(label);

                    zeile = 3;
                    Gesamtsumme = 0D;

                    if (Umfang) {
                        Sql = "SELECT * FROM TBL_AUSGABEN"
                            + " WHERE ((AUSGABEN_RECHDATUM BETWEEN '" + strVon + "'  AND '" + strBis + "')"
                            + "       OR (AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'))"
                                + "       AND AUSGABEN_TYP = '2'"
                                + " ORDER BY AUSGABEN_RECHDATUM";
                    } else {
                        Sql = "SELECT * FROM TBL_AUSGABEN"
                                + " WHERE AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'"
                                + "       AND AUSGABEN_TYP = '2'"
                                + " ORDER BY AUSGABEN_RECHDATUM";
                    }
                    resultAusgaben = SQLAusgaben.executeQuery(Sql); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 

                    while (resultAusgaben.next()) { // geht durch alle zeilen
                        // Aufbau des Tabellenblattes sheet_<AUSGABEN>
                        // Datum - RechNr  Lieferant  Ausgaben  UStr  Bezahlt   Beschreibung
                        //  0        1         2         3        4      5           6
                        label = new Label(0, zeile, SQLDate2Normal(resultAusgaben.getString("AUSGABEN_RECHDATUM")), arial10formatL);
                        sheet_Bestellung.addCell(label);
                        label = new Label(1, zeile, resultAusgaben.getString("AUSGABEN_RECHNNR"), arial10formatL);
                        sheet_Bestellung.addCell(label);
                        label = new Label(2, zeile, resultAusgaben.getString("AUSGABEN_LIEFERANT"));
                        sheet_Bestellung.addCell(label);
                        Gesamtzeile = resultAusgaben.getFloat("AUSGABEN_KOSTEN") * 1D;
                        label = new Label(3, zeile, Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtzeile)), arial10formatR);
                        sheet_Bestellung.addCell(label);
                        label = new Label(4, zeile, resultAusgaben.getString("AUSGABEN_USTR"), arial10formatR);
                        sheet_Bestellung.addCell(label);

                        if (resultAusgaben.getString("AUSGABEN_BEZAHLT").equals("1970-01-01")) {
                            label = new Label(5, zeile, "----------", arial10formatL);
                            sheet_Bestellung.addCell(label);
                        } else {
                            label = new Label(5, zeile, SQLDate2Normal(resultAusgaben.getString("AUSGABEN_BEZAHLT")), arial10formatL);
                            sheet_Bestellung.addCell(label);
                        }
                        label = new Label(6, zeile, resultAusgaben.getString("AUSGABEN_BESCHREIBUNG"), arial10formatL);
                        sheet_Bestellung.addCell(label);

                        zeile = zeile + 1;
                        Gesamtsumme = Gesamtsumme + Gesamtzeile;
                    } // while resultAusgaben

                    // Gesamtsumme berechnen
                    zeile = zeile + 1;
                    Gesamteinnahmen = Gesamteinnahmen + Gesamtsumme;
                    label = new Label(0, zeile, "Gesamtsumme der Ausgaben für Buchbestellungen", arial10formatR);
                    sheet_Bestellung.addCell(label);
                    label = new Label(3, zeile, Modulhelferlein.str2dec(Gesamtsumme), arial10formatR);
                    sheet_Bestellung.addCell(label);
                    
                    // Gesamtsumme auf Übersichtsblatt schreiben
                    label = new Label(3, 4, Modulhelferlein.str2dec(Gesamtsumme), arial10formatR);
                    sheet_Uebersicht.addCell(label);
                    
                    
                    // Tabellenblatt Honorarabrechnung - Typ 3 
                    label = new Label(0, 0, "Ausgaben wegen Honorarabrechnung", arial14formatBold);
                    sheet_Honorar.addCell(label);
                    label = new Label(0, 2, "Datum", arial10formatBold);
                    sheet_Honorar.addCell(label);
                    label = new Label(1, 2, "RechnungsNr", arial10formatBold);
                    sheet_Honorar.addCell(label);
                    label = new Label(2, 2, "Lieferant", arial10formatBold);
                    sheet_Honorar.addCell(label);
                    label = new Label(3, 2, "Ausgaben", arial10formatBold);
                    sheet_Honorar.addCell(label);
                    label = new Label(4, 2, "UStr", arial10formatBold);
                    sheet_Honorar.addCell(label);
                    label = new Label(5, 2, "Bezahlt", arial10formatBold);
                    sheet_Honorar.addCell(label);
                    label = new Label(6, 2, "Beschreibung", arial10formatBold);
                    sheet_Honorar.addCell(label);

                    zeile = 3;
                    Gesamtsumme = 0D;

                    if (Umfang) {
                        Sql = "SELECT * FROM TBL_AUSGABEN"
                            + " WHERE ((AUSGABEN_RECHDATUM BETWEEN '" + strVon + "'  AND '" + strBis + "')"
                            + "       OR (AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'))"
                                + "       AND AUSGABEN_TYP = '3'"
                                + " ORDER BY AUSGABEN_RECHDATUM";
                    } else {
                        Sql = "SELECT * FROM TBL_AUSGABEN"
                                + " WHERE AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'"
                                + "       AND AUSGABEN_TYP = '3'"
                                + " ORDER BY AUSGABEN_RECHDATUM";
                    }
                    resultAusgaben = SQLAusgaben.executeQuery(Sql); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 

                    while (resultAusgaben.next()) { // geht durch alle zeilen
                        // Aufbau des Tabellenblattes sheet_<AUSGABEN>
                        // Datum - RechNr  Lieferant  Ausgaben  UStr  Bezahlt   Beschreibung
                        //  0        1         2         3        4      5           6
                        label = new Label(0, zeile, SQLDate2Normal(resultAusgaben.getString("AUSGABEN_RECHDATUM")), arial10formatL);
                        sheet_Honorar.addCell(label);
                        label = new Label(1, zeile, resultAusgaben.getString("AUSGABEN_RECHNNR"), arial10formatL);
                        sheet_Honorar.addCell(label);
                        label = new Label(2, zeile, resultAusgaben.getString("AUSGABEN_LIEFERANT"));
                        sheet_Honorar.addCell(label);
                        Gesamtzeile = resultAusgaben.getFloat("AUSGABEN_KOSTEN") * 1D;
                        label = new Label(3, zeile, Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtzeile)), arial10formatR);
                        sheet_Honorar.addCell(label);
                        label = new Label(4, zeile, resultAusgaben.getString("AUSGABEN_USTR"), arial10formatR);
                        sheet_Honorar.addCell(label);

                        if (resultAusgaben.getString("AUSGABEN_BEZAHLT").equals("1970-01-01")) {
                            label = new Label(5, zeile, "----------", arial10formatL);
                            sheet_Honorar.addCell(label);
                        } else {
                            label = new Label(5, zeile, SQLDate2Normal(resultAusgaben.getString("AUSGABEN_BEZAHLT")), arial10formatL);
                            sheet_Honorar.addCell(label);
                        }
                        label = new Label(6, zeile, resultAusgaben.getString("AUSGABEN_BESCHREIBUNG"), arial10formatL);
                        sheet_Honorar.addCell(label);

                        zeile = zeile + 1;
                        Gesamtsumme = Gesamtsumme + Gesamtzeile;
                    } // while resultAusgaben

                    // Gesamtsumme berechnen
                    zeile = zeile + 1;
                    Gesamteinnahmen = Gesamteinnahmen + Gesamtsumme;
                    label = new Label(0, zeile, "Gesamtsumme der Ausgaben für Honorare", arial10formatR);
                    sheet_Honorar.addCell(label);
                    label = new Label(3, zeile, Modulhelferlein.str2dec(Gesamtsumme), arial10formatR);
                    sheet_Honorar.addCell(label);
                    
                    // Gesamtsumme auf Übersichtsblatt schreiben
                    label = new Label(3, 5, Modulhelferlein.str2dec(Gesamtsumme), arial10formatR);
                    sheet_Uebersicht.addCell(label);
                    
                    
                    // Tabellenblatt Kontoführung - Typ 4 
                    label = new Label(0, 0, "Ausgaben wegen Kontoführung", arial14formatBold);
                    sheet_Konto.addCell(label);
                    label = new Label(0, 2, "Datum", arial10formatBold);
                    sheet_Konto.addCell(label);
                    label = new Label(1, 2, "RechnungsNr", arial10formatBold);
                    sheet_Konto.addCell(label);
                    label = new Label(2, 2, "Lieferant", arial10formatBold);
                    sheet_Konto.addCell(label);
                    label = new Label(3, 2, "Ausgaben", arial10formatBold);
                    sheet_Konto.addCell(label);
                    label = new Label(4, 2, "UStr", arial10formatBold);
                    sheet_Konto.addCell(label);
                    label = new Label(5, 2, "Bezahlt", arial10formatBold);
                    sheet_Konto.addCell(label);
                    label = new Label(6, 2, "Beschreibung", arial10formatBold);
                    sheet_Konto.addCell(label);

                    zeile = 3;
                    Gesamtsumme = 0D;

                    if (Umfang) {
                        Sql = "SELECT * FROM TBL_AUSGABEN"
                            + " WHERE ((AUSGABEN_RECHDATUM BETWEEN '" + strVon + "'  AND '" + strBis + "')"
                            + "       OR (AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'))"
                                + "       AND AUSGABEN_TYP = '4'"
                                + " ORDER BY AUSGABEN_RECHDATUM";
                    } else {
                        Sql = "SELECT * FROM TBL_AUSGABEN"
                                + " WHERE AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'"
                                + "       AND AUSGABEN_TYP = '4'"
                                + " ORDER BY AUSGABEN_RECHDATUM";
                    }
                    resultAusgaben = SQLAusgaben.executeQuery(Sql); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 

                    while (resultAusgaben.next()) { // geht durch alle zeilen
                        // Aufbau des Tabellenblattes sheet_<AUSGABEN>
                        // Datum - RechNr  Lieferant  Ausgaben  UStr  Bezahlt   Beschreibung
                        //  0        1         2         3        4      5           6
                        label = new Label(0, zeile, SQLDate2Normal(resultAusgaben.getString("AUSGABEN_RECHDATUM")), arial10formatL);
                        sheet_Konto.addCell(label);
                        label = new Label(1, zeile, resultAusgaben.getString("AUSGABEN_RECHNNR"), arial10formatL);
                        sheet_Konto.addCell(label);
                        label = new Label(2, zeile, resultAusgaben.getString("AUSGABEN_LIEFERANT"));
                        sheet_Konto.addCell(label);
                        Gesamtzeile = resultAusgaben.getFloat("AUSGABEN_KOSTEN") * 1D;
                        label = new Label(3, zeile, Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtzeile)), arial10formatR);
                        sheet_Konto.addCell(label);
                        label = new Label(4, zeile, resultAusgaben.getString("AUSGABEN_USTR"), arial10formatR);
                        sheet_Konto.addCell(label);

                        if (resultAusgaben.getString("AUSGABEN_BEZAHLT").equals("1970-01-01")) {
                            label = new Label(5, zeile, "----------", arial10formatL);
                            sheet_Konto.addCell(label);
                        } else {
                            label = new Label(5, zeile, SQLDate2Normal(resultAusgaben.getString("AUSGABEN_BEZAHLT")), arial10formatL);
                            sheet_Konto.addCell(label);
                        }
                        label = new Label(6, zeile, resultAusgaben.getString("AUSGABEN_BESCHREIBUNG"), arial10formatL);
                        sheet_Konto.addCell(label);

                        zeile = zeile + 1;
                        Gesamtsumme = Gesamtsumme + Gesamtzeile;
                    } // while resultAusgaben

                    // Gesamtsumme berechnen
                    zeile = zeile + 1;
                    Gesamteinnahmen = Gesamteinnahmen + Gesamtsumme;
                    label = new Label(0, zeile, "Gesamtsumme der Ausgaben für Kontoführung", arial10formatR);
                    sheet_Konto.addCell(label);
                    label = new Label(3, zeile, Modulhelferlein.str2dec(Gesamtsumme), arial10formatR);
                    sheet_Konto.addCell(label);
                    
                    // Gesamtsumme auf Übersichtsblatt schreiben
                    label = new Label(3, 6, Modulhelferlein.str2dec(Gesamtsumme), arial10formatR);
                    sheet_Uebersicht.addCell(label);
                    
                    
                    // Tabellenblatt Telefonkosten - Typ 5 
                    label = new Label(0, 0, "Ausgaben wegen Telefonkosten", arial14formatBold);
                    sheet_Telefon.addCell(label);
                    label = new Label(0, 2, "Datum", arial10formatBold);
                    sheet_Telefon.addCell(label);
                    label = new Label(1, 2, "RechnungsNr", arial10formatBold);
                    sheet_Telefon.addCell(label);
                    label = new Label(2, 2, "Lieferant", arial10formatBold);
                    sheet_Telefon.addCell(label);
                    label = new Label(3, 2, "Ausgaben", arial10formatBold);
                    sheet_Telefon.addCell(label);
                    label = new Label(4, 2, "UStr", arial10formatBold);
                    sheet_Telefon.addCell(label);
                    label = new Label(5, 2, "Bezahlt", arial10formatBold);
                    sheet_Telefon.addCell(label);
                    label = new Label(6, 2, "Beschreibung", arial10formatBold);
                    sheet_Telefon.addCell(label);

                    zeile = 3;
                    Gesamtsumme = 0D;

                    if (Umfang) {
                        Sql = "SELECT * FROM TBL_AUSGABEN"
                            + " WHERE ((AUSGABEN_RECHDATUM BETWEEN '" + strVon + "'  AND '" + strBis + "')"
                            + "       OR (AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'))"
                                + "       AND AUSGABEN_TYP = '5'"
                                + " ORDER BY AUSGABEN_RECHDATUM";
                    } else {
                        Sql = "SELECT * FROM TBL_AUSGABEN"
                                + " WHERE AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'"
                                + "       AND AUSGABEN_TYP = '5'"
                                + " ORDER BY AUSGABEN_RECHDATUM";
                    }
                    resultAusgaben = SQLAusgaben.executeQuery(Sql); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 

                    while (resultAusgaben.next()) { // geht durch alle zeilen
                        // Aufbau des Tabellenblattes sheet_<AUSGABEN>
                        // Datum - RechNr  Lieferant  Ausgaben  UStr  Bezahlt   Beschreibung
                        //  0        1         2         3        4      5           6
                        label = new Label(0, zeile, SQLDate2Normal(resultAusgaben.getString("AUSGABEN_RECHDATUM")), arial10formatL);
                        sheet_Telefon.addCell(label);
                        label = new Label(1, zeile, resultAusgaben.getString("AUSGABEN_RECHNNR"), arial10formatL);
                        sheet_Telefon.addCell(label);
                        label = new Label(2, zeile, resultAusgaben.getString("AUSGABEN_LIEFERANT"));
                        sheet_Telefon.addCell(label);
                        Gesamtzeile = resultAusgaben.getFloat("AUSGABEN_KOSTEN") * 1D;
                        label = new Label(3, zeile, Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtzeile)), arial10formatR);
                        sheet_Telefon.addCell(label);
                        label = new Label(4, zeile, resultAusgaben.getString("AUSGABEN_USTR"), arial10formatR);
                        sheet_Telefon.addCell(label);

                        if (resultAusgaben.getString("AUSGABEN_BEZAHLT").equals("1970-01-01")) {
                            label = new Label(5, zeile, "----------", arial10formatL);
                            sheet_Telefon.addCell(label);
                        } else {
                            label = new Label(5, zeile, SQLDate2Normal(resultAusgaben.getString("AUSGABEN_BEZAHLT")), arial10formatL);
                            sheet_Telefon.addCell(label);
                        }
                        label = new Label(6, zeile, resultAusgaben.getString("AUSGABEN_BESCHREIBUNG"), arial10formatL);
                        sheet_Telefon.addCell(label);

                        zeile = zeile + 1;
                        Gesamtsumme = Gesamtsumme + Gesamtzeile;
                    } // while resultAusgaben

                    // Gesamtsumme berechnen
                    zeile = zeile + 1;
                    Gesamteinnahmen = Gesamteinnahmen + Gesamtsumme;
                    label = new Label(0, zeile, "Gesamtsumme der Ausgaben für Telefon", arial10formatR);
                    sheet_Telefon.addCell(label);
                    label = new Label(3, zeile, Modulhelferlein.str2dec(Gesamtsumme), arial10formatR);
                    sheet_Telefon.addCell(label);
                    
                    // Gesamtsumme auf Übersichtsblatt schreiben
                    label = new Label(3, 7, Modulhelferlein.str2dec(Gesamtsumme), arial10formatR);
                    sheet_Uebersicht.addCell(label);
                    
                    
                    // Tabellenblatt Mieten - Typ 6 
                    label = new Label(0, 0, "Ausgaben wegen Büromiete", arial14formatBold);
                    sheet_Raum.addCell(label);
                    label = new Label(0, 2, "Datum", arial10formatBold);
                    sheet_Raum.addCell(label);
                    label = new Label(1, 2, "RechnungsNr", arial10formatBold);
                    sheet_Raum.addCell(label);
                    label = new Label(2, 2, "Lieferant", arial10formatBold);
                    sheet_Raum.addCell(label);
                    label = new Label(3, 2, "Ausgaben", arial10formatBold);
                    sheet_Raum.addCell(label);
                    label = new Label(4, 2, "UStr", arial10formatBold);
                    sheet_Raum.addCell(label);
                    label = new Label(5, 2, "Bezahlt", arial10formatBold);
                    sheet_Raum.addCell(label);
                    label = new Label(6, 2, "Beschreibung", arial10formatBold);
                    sheet_Raum.addCell(label);

                    zeile = 3;
                    Gesamtsumme = 0D;

                    if (Umfang) {
                        Sql = "SELECT * FROM TBL_AUSGABEN"
                            + " WHERE ((AUSGABEN_RECHDATUM BETWEEN '" + strVon + "'  AND '" + strBis + "')"
                            + "       OR (AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'))"
                                + "       AND AUSGABEN_TYP = '6'"
                                + " ORDER BY AUSGABEN_RECHDATUM";
                    } else {
                        Sql = "SELECT * FROM TBL_AUSGABEN"
                                + " WHERE AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'"
                                + "       AND AUSGABEN_TYP = '6'"
                                + " ORDER BY AUSGABEN_RECHDATUM";
                    }
                    resultAusgaben = SQLAusgaben.executeQuery(Sql); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 

                    while (resultAusgaben.next()) { // geht durch alle zeilen
                        // Aufbau des Tabellenblattes sheet_<AUSGABEN>
                        // Datum - RechNr  Lieferant  Ausgaben  UStr  Bezahlt   Beschreibung
                        //  0        1         2         3        4      5           6
                        label = new Label(0, zeile, SQLDate2Normal(resultAusgaben.getString("AUSGABEN_RECHDATUM")), arial10formatL);
                        sheet_Raum.addCell(label);
                        label = new Label(1, zeile, resultAusgaben.getString("AUSGABEN_RECHNNR"), arial10formatL);
                        sheet_Raum.addCell(label);
                        label = new Label(2, zeile, resultAusgaben.getString("AUSGABEN_LIEFERANT"));
                        sheet_Raum.addCell(label);
                        Gesamtzeile = resultAusgaben.getFloat("AUSGABEN_KOSTEN") * 1D;
                        label = new Label(3, zeile, Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtzeile)), arial10formatR);
                        sheet_Raum.addCell(label);
                        label = new Label(4, zeile, resultAusgaben.getString("AUSGABEN_USTR"), arial10formatR);
                        sheet_Raum.addCell(label);

                        if (resultAusgaben.getString("AUSGABEN_BEZAHLT").equals("1970-01-01")) {
                            label = new Label(5, zeile, "----------", arial10formatL);
                            sheet_Raum.addCell(label);
                        } else {
                            label = new Label(5, zeile, SQLDate2Normal(resultAusgaben.getString("AUSGABEN_BEZAHLT")), arial10formatL);
                            sheet_Raum.addCell(label);
                        }
                        label = new Label(6, zeile, resultAusgaben.getString("AUSGABEN_BESCHREIBUNG"), arial10formatL);
                        sheet_Raum.addCell(label);

                        zeile = zeile + 1;
                        Gesamtsumme = Gesamtsumme + Gesamtzeile;
                    } // while resultAusgaben

                    // Gesamtsumme berechnen
                    zeile = zeile + 1;
                    Gesamteinnahmen = Gesamteinnahmen + Gesamtsumme;
                    label = new Label(0, zeile, "Gesamtsumme der Ausgaben für Büromiete", arial10formatR);
                    sheet_Raum.addCell(label);
                    label = new Label(3, zeile, Modulhelferlein.str2dec(Gesamtsumme), arial10formatR);
                    sheet_Raum.addCell(label);
                    
                    // Gesamtsumme auf Übersichtsblatt schreiben
                    label = new Label(3, 8, Modulhelferlein.str2dec(Gesamtsumme), arial10formatR);
                    sheet_Uebersicht.addCell(label);
                    
                    
                    // Tabellenblatt Bucherstellung - Typ 7 
                    label = new Label(0, 0, "Ausgaben wegen Bucherstellung", arial14formatBold);
                    sheet_Erstellung.addCell(label);
                    label = new Label(0, 2, "Datum", arial10formatBold);
                    sheet_Erstellung.addCell(label);
                    label = new Label(1, 2, "RechnungsNr", arial10formatBold);
                    sheet_Erstellung.addCell(label);
                    label = new Label(2, 2, "Lieferant", arial10formatBold);
                    sheet_Erstellung.addCell(label);
                    label = new Label(3, 2, "Ausgaben", arial10formatBold);
                    sheet_Erstellung.addCell(label);
                    label = new Label(4, 2, "UStr", arial10formatBold);
                    sheet_Erstellung.addCell(label);
                    label = new Label(5, 2, "Bezahlt", arial10formatBold);
                    sheet_Erstellung.addCell(label);
                    label = new Label(6, 2, "Beschreibung", arial10formatBold);
                    sheet_Erstellung.addCell(label);

                    zeile = 3;
                    Gesamtsumme = 0D;

                    if (Umfang) {
                        Sql = "SELECT * FROM TBL_AUSGABEN"
                            + " WHERE ((AUSGABEN_RECHDATUM BETWEEN '" + strVon + "'  AND '" + strBis + "')"
                            + "       OR (AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'))"
                                + "       AND AUSGABEN_TYP = '7'"
                                + " ORDER BY AUSGABEN_RECHDATUM";
                    } else {
                        Sql = "SELECT * FROM TBL_AUSGABEN"
                                + " WHERE AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'"
                                + "       AND AUSGABEN_TYP = '7'"
                                + " ORDER BY AUSGABEN_RECHDATUM";
                    }
                    resultAusgaben = SQLAusgaben.executeQuery(Sql); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 

                    while (resultAusgaben.next()) { // geht durch alle zeilen
                        // Aufbau des Tabellenblattes sheet_<AUSGABEN>
                        // Datum - RechNr  Lieferant  Ausgaben  UStr  Bezahlt   Beschreibung
                        //  0        1         2         3        4      5           6
                        label = new Label(0, zeile, SQLDate2Normal(resultAusgaben.getString("AUSGABEN_RECHDATUM")), arial10formatL);
                        sheet_Erstellung.addCell(label);
                        label = new Label(1, zeile, resultAusgaben.getString("AUSGABEN_RECHNNR"), arial10formatL);
                        sheet_Erstellung.addCell(label);
                        label = new Label(2, zeile, resultAusgaben.getString("AUSGABEN_LIEFERANT"));
                        sheet_Erstellung.addCell(label);
                        Gesamtzeile = resultAusgaben.getFloat("AUSGABEN_KOSTEN") * 1D;
                        label = new Label(3, zeile, Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtzeile)), arial10formatR);
                        sheet_Erstellung.addCell(label);
                        label = new Label(4, zeile, resultAusgaben.getString("AUSGABEN_USTR"), arial10formatR);
                        sheet_Erstellung.addCell(label);

                        if (resultAusgaben.getString("AUSGABEN_BEZAHLT").equals("1970-01-01")) {
                            label = new Label(5, zeile, "----------", arial10formatL);
                            sheet_Erstellung.addCell(label);
                        } else {
                            label = new Label(5, zeile, SQLDate2Normal(resultAusgaben.getString("AUSGABEN_BEZAHLT")), arial10formatL);
                            sheet_Erstellung.addCell(label);
                        }
                        label = new Label(6, zeile, resultAusgaben.getString("AUSGABEN_BESCHREIBUNG"), arial10formatL);
                        sheet_Erstellung.addCell(label);

                        zeile = zeile + 1;
                        Gesamtsumme = Gesamtsumme + Gesamtzeile;
                    } // while resultAusgaben

                    // Gesamtsumme berechnen
                    zeile = zeile + 1;
                    Gesamteinnahmen = Gesamteinnahmen + Gesamtsumme;
                    label = new Label(0, zeile, "Gesamtsumme der Ausgaben für Bucherstellungen", arial10formatR);
                    sheet_Erstellung.addCell(label);
                    label = new Label(3, zeile, Modulhelferlein.str2dec(Gesamtsumme), arial10formatR);
                    sheet_Erstellung.addCell(label);
                    
                    // Gesamtsumme auf Übersichtsblatt schreiben
                    label = new Label(3, 9, Modulhelferlein.str2dec(Gesamtsumme), arial10formatR);
                    sheet_Uebersicht.addCell(label);
                    
                    
                    // Tabellenblatt Fahrtkosten - Typ 8 
                    label = new Label(0, 0, "Ausgaben für Fahrtkosten", arial14formatBold);
                    sheet_Fahrt.addCell(label);
                    label = new Label(0, 2, "Datum", arial10formatBold);
                    sheet_Fahrt.addCell(label);
                    label = new Label(1, 2, "RechnungsNr", arial10formatBold);
                    sheet_Fahrt.addCell(label);
                    label = new Label(2, 2, "Lieferant", arial10formatBold);
                    sheet_Fahrt.addCell(label);
                    label = new Label(3, 2, "Ausgaben", arial10formatBold);
                    sheet_Fahrt.addCell(label);
                    label = new Label(4, 2, "UStr", arial10formatBold);
                    sheet_Fahrt.addCell(label);
                    label = new Label(5, 2, "Bezahlt", arial10formatBold);
                    sheet_Fahrt.addCell(label);
                    label = new Label(6, 2, "Beschreibung", arial10formatBold);
                    sheet_Fahrt.addCell(label);

                    zeile = 3;
                    Gesamtsumme = 0D;

                    if (Umfang) {
                        Sql = "SELECT * FROM TBL_AUSGABEN"
                            + " WHERE ((AUSGABEN_RECHDATUM BETWEEN '" + strVon + "'  AND '" + strBis + "')"
                            + "       OR (AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'))"
                                + "       AND AUSGABEN_TYP = '8'"
                                + " ORDER BY AUSGABEN_RECHDATUM";
                    } else {
                        Sql = "SELECT * FROM TBL_AUSGABEN"
                                + " WHERE AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'"
                                + "       AND AUSGABEN_TYP = '8'"
                                + " ORDER BY AUSGABEN_RECHDATUM";
                    }
                    resultAusgaben = SQLAusgaben.executeQuery(Sql); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 

                    while (resultAusgaben.next()) { // geht durch alle zeilen
                        // Aufbau des Tabellenblattes sheet_<AUSGABEN>
                        // Datum - RechNr  Lieferant  Ausgaben  UStr  Bezahlt   Beschreibung
                        //  0        1         2         3        4      5           6
                        label = new Label(0, zeile, SQLDate2Normal(resultAusgaben.getString("AUSGABEN_RECHDATUM")), arial10formatL);
                        sheet_Fahrt.addCell(label);
                        label = new Label(1, zeile, resultAusgaben.getString("AUSGABEN_RECHNNR"), arial10formatL);
                        sheet_Fahrt.addCell(label);
                        label = new Label(2, zeile, resultAusgaben.getString("AUSGABEN_LIEFERANT"));
                        sheet_Fahrt.addCell(label);
                        Gesamtzeile = resultAusgaben.getFloat("AUSGABEN_KOSTEN") * 1D;
                        label = new Label(3, zeile, Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtzeile)), arial10formatR);
                        sheet_Fahrt.addCell(label);
                        label = new Label(4, zeile, resultAusgaben.getString("AUSGABEN_USTR"), arial10formatR);
                        sheet_Fahrt.addCell(label);

                        if (resultAusgaben.getString("AUSGABEN_BEZAHLT").equals("1970-01-01")) {
                            label = new Label(5, zeile, "----------", arial10formatL);
                            sheet_Fahrt.addCell(label);
                        } else {
                            label = new Label(5, zeile, SQLDate2Normal(resultAusgaben.getString("AUSGABEN_BEZAHLT")), arial10formatL);
                            sheet_Fahrt.addCell(label);
                        }
                        label = new Label(6, zeile, resultAusgaben.getString("AUSGABEN_BESCHREIBUNG"), arial10formatL);
                        sheet_Fahrt.addCell(label);

                        zeile = zeile + 1;
                        Gesamtsumme = Gesamtsumme + Gesamtzeile;
                    } // while resultAusgaben

                    // Gesamtsumme berechnen
                    zeile = zeile + 1;
                    Gesamteinnahmen = Gesamteinnahmen + Gesamtsumme;
                    label = new Label(0, zeile, "Gesamtsumme der Ausgaben für Fahrtkosten", arial10formatR);
                    sheet_Fahrt.addCell(label);
                    label = new Label(3, zeile, Modulhelferlein.str2dec(Gesamtsumme), arial10formatR);
                    sheet_Fahrt.addCell(label);
                    
                    // Gesamtsumme auf Übersichtsblatt schreiben
                    label = new Label(3, 10, Modulhelferlein.str2dec(Gesamtsumme), arial10formatR);
                    sheet_Uebersicht.addCell(label);
                    
                    
                    // Tabellenblatt Sonstige - Typ 9 
                    label = new Label(0, 0, "Ausgaben für Sonstiges", arial14formatBold);
                    sheet_Sonstig.addCell(label);
                    label = new Label(0, 2, "Datum", arial10formatBold);
                    sheet_Sonstig.addCell(label);
                    label = new Label(1, 2, "RechnungsNr", arial10formatBold);
                    sheet_Sonstig.addCell(label);
                    label = new Label(2, 2, "Lieferant", arial10formatBold);
                    sheet_Sonstig.addCell(label);
                    label = new Label(3, 2, "Ausgaben", arial10formatBold);
                    sheet_Sonstig.addCell(label);
                    label = new Label(4, 2, "UStr", arial10formatBold);
                    sheet_Sonstig.addCell(label);
                    label = new Label(5, 2, "Bezahlt", arial10formatBold);
                    sheet_Sonstig.addCell(label);
                    label = new Label(6, 2, "Beschreibung", arial10formatBold);
                    sheet_Sonstig.addCell(label);

                    zeile = 3;
                    Gesamtsumme = 0D;

                    if (Umfang) {
                        Sql = "SELECT * FROM TBL_AUSGABEN"
                            + " WHERE ((AUSGABEN_RECHDATUM BETWEEN '" + strVon + "'  AND '" + strBis + "')"
                            + "       OR (AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'))"
                                + "       AND AUSGABEN_TYP = '9'"
                                + " ORDER BY AUSGABEN_RECHDATUM";
                    } else {
                        Sql = "SELECT * FROM TBL_AUSGABEN"
                                + " WHERE AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'"
                                + "       AND AUSGABEN_TYP = '9'"
                                + " ORDER BY AUSGABEN_RECHDATUM";
                    }
                    resultAusgaben = SQLAusgaben.executeQuery(Sql); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 

                    while (resultAusgaben.next()) { // geht durch alle zeilen
                        // Aufbau des Tabellenblattes sheet_<AUSGABEN>
                        // Datum - RechNr  Lieferant  Ausgaben  UStr  Bezahlt   Beschreibung
                        //  0        1         2         3        4      5           6
                        label = new Label(0, zeile, SQLDate2Normal(resultAusgaben.getString("AUSGABEN_RECHDATUM")), arial10formatL);
                        sheet_Sonstig.addCell(label);
                        label = new Label(1, zeile, resultAusgaben.getString("AUSGABEN_RECHNNR"), arial10formatL);
                        sheet_Sonstig.addCell(label);
                        label = new Label(2, zeile, resultAusgaben.getString("AUSGABEN_LIEFERANT"));
                        sheet_Sonstig.addCell(label);
                        Gesamtzeile = resultAusgaben.getFloat("AUSGABEN_KOSTEN") * 1D;
                        label = new Label(3, zeile, Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtzeile)), arial10formatR);
                        sheet_Sonstig.addCell(label);
                        label = new Label(4, zeile, resultAusgaben.getString("AUSGABEN_USTR"), arial10formatR);
                        sheet_Sonstig.addCell(label);

                        if (resultAusgaben.getString("AUSGABEN_BEZAHLT").equals("1970-01-01")) {
                            label = new Label(5, zeile, "----------", arial10formatL);
                            sheet_Sonstig.addCell(label);
                        } else {
                            label = new Label(5, zeile, SQLDate2Normal(resultAusgaben.getString("AUSGABEN_BEZAHLT")), arial10formatL);
                            sheet_Sonstig.addCell(label);
                        }
                        label = new Label(6, zeile, resultAusgaben.getString("AUSGABEN_BESCHREIBUNG"), arial10formatL);
                        sheet_Sonstig.addCell(label);

                        zeile = zeile + 1;
                        Gesamtsumme = Gesamtsumme + Gesamtzeile;
                    } // while resultAusgaben

                    // Gesamtsumme berechnen
                    zeile = zeile + 1;
                    Gesamteinnahmen = Gesamteinnahmen + Gesamtsumme;
                    label = new Label(0, zeile, "Gesamtsumme der Ausgaben für Sonstiges", arial10formatR);
                    sheet_Sonstig.addCell(label);
                    label = new Label(3, zeile, Modulhelferlein.str2dec(Gesamtsumme), arial10formatR);
                    sheet_Sonstig.addCell(label);
                    
                    // Gesamtsumme auf Übersichtsblatt schreiben
                    label = new Label(3, 11, Modulhelferlein.str2dec(Gesamtsumme), arial10formatR);
                    sheet_Uebersicht.addCell(label);
                    

                } catch (SQLException ex) {
                    Modulhelferlein.Fehlermeldung("SQL-Exception: " , ex.getMessage());
                    //Logger.getLogger(berAusgaben.class.getName()).log(Level.SEVERE, null, ex);
                } // try Datenbankabfrage

                // Gesamteinnahmn schreiben
                label = new Label(3, 13, Modulhelferlein.str2dec(Gesamteinnahmen), arial10formatR);
                sheet_Uebersicht.addCell(label);

                // Fertig - alles schließen
                try {// workbook write
                    workbook.write();
                } catch (IOException e) {
                    Modulhelferlein.Fehlermeldung("XLS-Bericht Ausgaben: IO-Exception: " , e.getMessage());
                } // workbook write

                try { // try workbook close
                    workbook.close();
                } catch (IOException e) {
                    Modulhelferlein.Fehlermeldung("XLS-Bericht Ausgaben: IO-Exception: " , e.getMessage());
                } catch (WriteException ex) {
                    Modulhelferlein.Fehlermeldung("XLS-Bericht Ausgaben: Write-Exception: " , ex.getMessage());
                    //Logger.getLogger(berAusgaben.class.getName()).log(Level.SEVERE, null, ex);
                } // try workbook close

                try { // try XLS anzeigen
                    Runtime.getRuntime().exec("cmd.exe /c " + "\"" + outputFileName + "\"");
                } catch (IOException exept) {
                    Modulhelferlein.Fehlermeldung("Bericht Ausgaben: Anzeige XLS-Export: Exception: " , exept.getMessage());
                } // try XLS anzeigen
            } // if

        } catch (IOException e) {
            Modulhelferlein.Fehlermeldung("XLS-Bericht: IO-Exception: " , e.getMessage());
        } catch (WriteException ex) {
            Modulhelferlein.Fehlermeldung("XLS-Bericht Ausgaben: Write-Exception: " , ex.getMessage());
            //Logger.getLogger(berAusgaben.class.getName()).log(Level.SEVERE, null, ex);
        } // try Workbook create

        Modulhelferlein.Infomeldung("Liste der Ausgaben ist als XLS gespeichert!");
    } // void 

    /**
     * Erzeugt eine Ausgabenübersicht für den Zeitraum strVon bis strBis im MS
     * WOrd-Format
     *
     * @param Umfang
     * @param strVon Startdatum
     * @param strBis Endedatum
     */
    public static void berichtDOC(boolean Umfang, final String strVon, final String strBis) {
        Modulhelferlein.Infomeldung("Ausgabe als DOC ist noch nicht implementiert");
    }

    /**
     * Erzeugt eine Ausgabenübersicht für den Zeitraum strVon bis strBis im
     * PDF-Format AusgabenTypListe[] = {"Porto-Kosten", 0 "Büromaterial", 1
     * "Buchbestellungen", 2 "Honorarabrechnung", 3 "Kontoführung", 4
     * "Telefonkosten", 5 "Büro- und Lagerräume inkl. Nebenkosten", 6
     * "Bucherstellung", 7 "Sonstige"}; 8 "Sonstige"}; 9
     *
     * @param Umfang
     * @param strVon Startdatum
     * @param strBis Endedatum
     */
    public static void berichtPDF(boolean Umfang, final String strVon, final String strBis) {
        Double Gesamtsumme = 0D;
        Double Gesamtzeile = 0D;

        Double ausPorto = 0D;
        Double ausMat = 0D;
        Double ausBest = 0D;
        Double ausPflicht = 0D;
        Double ausRez = 0D;
        Double ausHon = 0D;
        Double ausKonto = 0D;
        Double ausTel = 0D;
        Double ausBuero = 0D;
        Double ausErst = 0D;
        Double ausFahrt = 0D;
        Double ausSonst = 0D;

        Statement SQLAusgaben = null;

        ResultSet resultAusgaben = null;

        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        PDPageContentStream cos;

        String Sql;

        String outputFileName = Modulhelferlein.pathBerichte + "\\Ausgaben\\"
                + "Liste-Ausgaben-"
                + Modulhelferlein.printSimpleDateFormat("yyyyMMdd") 
                + ".pdf";

        PDDocumentInformation docInfo = document.getDocumentInformation();

        docInfo.setSubject("Ausgaben");
        docInfo.setTitle("miles-Verlag Umsatz");
        docInfo.setAuthor("miles-Verlag");
        docInfo.setCreationDate(Calendar.getInstance());
        docInfo.setCreator("miles-Verlag");
        docInfo.setProducer("miles-Verlag");

        PDFont fontPlain = PDType1Font.HELVETICA;
        PDFont fontBold = PDType1Font.HELVETICA_BOLD;

        try { // Start a new content stream which will "hold" the to be created content
            cos = new PDPageContentStream(document, page);

            Ausgabe(cos, fontBold, 16, Color.BLACK, 56, 770, "Carola Hartmann Miles-Verlag");
            Ausgabe(cos, fontBold, 16, Color.BLACK, 56, 750, "Ausgaben im Zeitraum " + strVon + " - " + strBis);
            Ausgabe(cos, fontBold, 14, Color.BLACK, 100, 580, "- Porto-Kosten");
            Ausgabe(cos, fontBold, 14, Color.BLACK, 100, 560, "- Büromaterial");
            Ausgabe(cos, fontBold, 14, Color.BLACK, 100, 540, "- Buchbestellungen bei BoD");
            Ausgabe(cos, fontBold, 14, Color.BLACK, 100, 520, "- Honorarabrechnung");
            Ausgabe(cos, fontBold, 14, Color.BLACK, 100, 500, "- Kontoführung");
            Ausgabe(cos, fontBold, 14, Color.BLACK, 100, 480, "- Telefonkosten");
            Ausgabe(cos, fontBold, 14, Color.BLACK, 100, 460, "- Büro- und Lagerräume inkl. Nebenkosten");
            Ausgabe(cos, fontBold, 14, Color.BLACK, 100, 440, "- Bucherstellung");
            Ausgabe(cos, fontBold, 14, Color.BLACK, 100, 420, "- Kfz-/Fahrtkosten");
            Ausgabe(cos, fontBold, 14, Color.BLACK, 100, 400, "- Sonstige");

            cos.close();
            page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            cos = new PDPageContentStream(document, page);

            Connection conn = null;

            try { // Datenbank-Treiber laden
                Class.forName(Modulhelferlein.dbDriver);
            } catch (ClassNotFoundException exept) {
                Modulhelferlein.Fehlermeldung("ClassNotFoundException: Treiber nicht gefunden: ", exept.getMessage());
            } // try Datenbank-Treiber laden

            try { // Verbindung zur Datenbank über die JDBC-Brücke
                conn = DriverManager.getConnection(Modulhelferlein.dbUrl, Modulhelferlein.dbUser, Modulhelferlein.dbPassword);
            } catch (SQLException exept) {
                Modulhelferlein.Fehlermeldung("SQL-Exception: Verbindung nicht moeglich: " , exept.getMessage());
            } // try Verbindung zur Datenbank über die JDBC-Brücke

            final Connection conn2 = conn;

            if (conn2 != null) {

                try {
                    // Neuer Ausgabenabschnitt Portokosten - Typ 0  
                    Integer zeile = 1;
                    Integer seite = 1;
                    Gesamtsumme = 0D;

                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Portokosten");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + Modulhelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 130, 730, "RechnungsNr");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

                    Linie(cos,1,56, 725, 539, 725);
                    
                    SQLAusgaben = conn2.createStatement();

                    if (Umfang) {
                        Sql = "SELECT * FROM TBL_AUSGABEN"
                                + " WHERE ((AUSGABEN_RECHDATUM BETWEEN '" + strVon + "'  AND '" + strBis + "')"
                                + "       OR (AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'))"
                                + "       AND AUSGABEN_TYP = '0'"
                                + " ORDER BY AUSGABEN_RECHDATUM";
                    } else {
                        Sql = "SELECT * FROM TBL_AUSGABEN"
                                + " WHERE AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'"
                                + "       AND AUSGABEN_TYP = '0'"
                                + " ORDER BY AUSGABEN_RECHDATUM";
                    }
                    resultAusgaben = SQLAusgaben.executeQuery(Sql); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 

                    while (resultAusgaben.next()) { // geht durch alle zeilen
                        if (zeile == 48) {
                            cos.close();

                            page = new PDPage(PDRectangle.A4);
                            document.addPage(page);
                            cos = new PDPageContentStream(document, page);

                            zeile = 1;
                            seite = seite + 1;
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Portokosten");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + Modulhelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 130, 730, "RechnungsNr");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

                            Linie(cos,1,56, 725, 539, 725);
                        } // if

                        Gesamtzeile = resultAusgaben.getFloat("AUSGABEN_KOSTEN") * 1.0;

                        // Ausgabe RechNr, Kunde, Betrag    
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), SQLDate2Normal(resultAusgaben.getString("AUSGABEN_RECHDATUM")));
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 130, 710 - 15 * (zeile - 1), resultAusgaben.getString("AUSGABEN_RECHNNR"));
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 220, 710 - 15 * (zeile - 1), resultAusgaben.getString("AUSGABEN_LIEFERANT"));
                        AusgabeDB(cos, fontPlain, 12, Color.BLACK, 440, 710 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtzeile)));
                        if (resultAusgaben.getString("AUSGABEN_BEZAHLT").equals("1970-01-01")) {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), "----------");
                        } else {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), SQLDate2Normal(resultAusgaben.getString("AUSGABEN_BEZAHLT")));
                        }

                        Gesamtsumme = Gesamtsumme + Gesamtzeile;
                        zeile = zeile + 1;
                    } // while
                    Linie(cos,1,56, 710 - 15 * (zeile - 1), 539, 710 - 15 * (zeile - 1));
                    zeile = zeile + 1;
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), "Gesamtsumme der Ausgaben für Porto: ");
                    AusgabeDB(cos, fontBold, 12, Color.BLACK, 440, 710 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtsumme)));
                    ausPorto = Gesamtsumme;

                    // close the content stream for page 
                    cos.close();

                    // Neuer Ausgabenabschnitt Büromaterial - Typ 1                    
                    zeile = 1;
                    seite = 1;
                    Gesamtsumme = 0D;

                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);

                    cos = new PDPageContentStream(document, page);

                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Büromaterial");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + Modulhelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 130, 730, "RechnungsNr");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 220, 730, "Lieferant");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

                    Linie(cos,1,56, 725, 539, 725);
                    
                    SQLAusgaben = conn2.createStatement();

                    if (Umfang) {
                        Sql = "SELECT * FROM TBL_AUSGABEN"
                                + " WHERE ((AUSGABEN_RECHDATUM BETWEEN '" + strVon + "'  AND '" + strBis + "')"
                                + "       OR (AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'))"
                                + "       AND AUSGABEN_TYP = '1'"
                                + " ORDER BY AUSGABEN_RECHDATUM";
                    } else {
                        Sql = "SELECT * FROM TBL_AUSGABEN"
                                + " WHERE AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'"
                                + "       AND AUSGABEN_TYP = '1'"
                                + " ORDER BY AUSGABEN_RECHDATUM";
                    }
                    resultAusgaben = SQLAusgaben.executeQuery(Sql); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 

                    while (resultAusgaben.next()) { // geht durch alle zeilen
                        if (zeile == 48) {
                            cos.close();

                            page = new PDPage(PDRectangle.A4);
                            document.addPage(page);
                            cos = new PDPageContentStream(document, page);

                            zeile = 1;
                            seite = seite + 1;
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Büromaterial");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + Modulhelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 130, 730, "RechnungsNr");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 220, 730, "Lieferant");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

                            Linie(cos,1,56, 725, 539, 725);
                            
                        } // if

                        Gesamtzeile = resultAusgaben.getFloat("AUSGABEN_KOSTEN") * 1.0;

                        // Ausgabe RechNr, Kunde, Betrag    
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), SQLDate2Normal(resultAusgaben.getString("AUSGABEN_RECHDATUM")));
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 130, 710 - 15 * (zeile - 1), resultAusgaben.getString("AUSGABEN_RECHNNR"));
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 220, 710 - 15 * (zeile - 1), resultAusgaben.getString("AUSGABEN_LIEFERANT"));
                        AusgabeDB(cos, fontPlain, 12, Color.BLACK, 440, 710 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtzeile)));
                        if (resultAusgaben.getString("AUSGABEN_BEZAHLT").equals("1970-01-01")) {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), "----------");
                        } else {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), SQLDate2Normal(resultAusgaben.getString("AUSGABEN_BEZAHLT")));
                        }

                        Gesamtsumme = Gesamtsumme + Gesamtzeile;
                        zeile = zeile + 1;
                    } // while
                    Linie(cos,1,56, 710 - 15 * (zeile - 1), 539, 710 - 15 * (zeile - 1));
                    zeile = zeile + 1;
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), "Gesamtsumme der Ausgaben für Büromaterial: ");
                    AusgabeDB(cos, fontBold, 12, Color.BLACK, 440, 710 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtsumme)));
                    ausMat = Gesamtsumme;

                    // close the content stream for page 
                    cos.close();

                    // Neuer Ausgabenabschnitt Buchbestellungen - Typ 2  
                    zeile = 1;
                    seite = 1;
                    Gesamtsumme = 0D;

                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);

                    cos = new PDPageContentStream(document, page);

                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Buchbestellungen bei BoD");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + Modulhelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 130, 730, "RechnungsNr");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 220, 730, "Beschreibung");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

                    Linie(cos,1,56, 725, 539, 725);
                    
                    SQLAusgaben = conn2.createStatement();

                    if (Umfang) {
                        Sql = "SELECT * FROM TBL_AUSGABEN"
                                + " WHERE ((AUSGABEN_RECHDATUM BETWEEN '" + strVon + "'  AND '" + strBis + "')"
                                + "       OR (AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'))"
                                + "       AND AUSGABEN_TYP = '2'"
                                + " ORDER BY AUSGABEN_RECHDATUM";
                    } else {
                        Sql = "SELECT * FROM TBL_AUSGABEN"
                                + " WHERE AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'"
                                + "       AND AUSGABEN_TYP = '2'"
                                + " ORDER BY AUSGABEN_RECHDATUM";
                    }
                    resultAusgaben = SQLAusgaben.executeQuery(Sql); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 

                    while (resultAusgaben.next()) { // geht durch alle zeilen
                        if (zeile == 48) {
                            cos.close();

                            page = new PDPage(PDRectangle.A4);
                            document.addPage(page);
                            cos = new PDPageContentStream(document, page);

                            zeile = 1;
                            seite = seite + 1;
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Buchbestellungen bei BoD");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + Modulhelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 130, 730, "RechnungsNr");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 220, 730, "Beschreibung");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

                            Linie(cos,1,56, 725, 539, 725);
                            
                        } // if

                        Gesamtzeile = resultAusgaben.getFloat("AUSGABEN_KOSTEN") * 1.0;

                        // Ausgabe RechNr, Kunde, Betrag    
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), SQLDate2Normal(resultAusgaben.getString("AUSGABEN_RECHDATUM")));
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 130, 710 - 15 * (zeile - 1), resultAusgaben.getString("AUSGABEN_RECHNNR"));
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 220, 710 - 15 * (zeile - 1), resultAusgaben.getString("AUSGABEN_BESCHREIBUNG"));
                        AusgabeDB(cos, fontPlain, 12, Color.BLACK, 440, 710 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtzeile)));
                        if (resultAusgaben.getString("AUSGABEN_BEZAHLT").equals("1970-01-01")) {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), "----------");
                        } else {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), SQLDate2Normal(resultAusgaben.getString("AUSGABEN_BEZAHLT")));
                        }

                        Gesamtsumme = Gesamtsumme + Gesamtzeile;
                        zeile = zeile + 1;
                    } // while
                    Linie(cos,1,56, 710 - 15 * (zeile - 1), 539, 710 - 15 * (zeile - 1));
                    
                    zeile = zeile + 1;
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), "Gesamtsumme der Ausgaben für Buchbestellungen bei BoD: ");
                    AusgabeDB(cos, fontBold, 12, Color.BLACK, 440, 710 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtsumme)));
                    ausBest = Gesamtsumme;

                    // close the content stream for page 
                    cos.close();

                    // Neuer Ausgabenabschnitt Honorarabrechnung - Typ 3 
                    zeile = 1;
                    seite = 1;
                    Gesamtsumme = 0D;

                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);

                    cos = new PDPageContentStream(document, page);

                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Honorarabrechnung");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + Modulhelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 130, 730, "Autor");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

                    Linie(cos,1,56, 725, 539, 725);
                    
                    SQLAusgaben = conn2.createStatement();

                    if (Umfang) {
                        Sql = "SELECT * FROM TBL_AUSGABEN"
                                + " WHERE ((AUSGABEN_RECHDATUM BETWEEN '" + strVon + "'  AND '" + strBis + "')"
                                + "       OR (AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'))"
                                + "       AND AUSGABEN_TYP = '3'"
                                + " ORDER BY AUSGABEN_RECHDATUM";
                    } else {
                        Sql = "SELECT * FROM TBL_AUSGABEN"
                                + " WHERE AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'"
                                + "       AND AUSGABEN_TYP = '3'"
                                + " ORDER BY AUSGABEN_RECHDATUM";
                    }
                    resultAusgaben = SQLAusgaben.executeQuery(Sql); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 

                    while (resultAusgaben.next()) { // geht durch alle zeilen
                        if (zeile == 48) {
                            cos.close();

                            page = new PDPage(PDRectangle.A4);
                            document.addPage(page);
                            cos = new PDPageContentStream(document, page);

                            zeile = 1;
                            seite = seite + 1;
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Honorarabrechnung");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + Modulhelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 130, 730, "Autor");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

                            Linie(cos,1,56, 725, 539, 725);
                            
                        } // if

                        Gesamtzeile = resultAusgaben.getFloat("AUSGABEN_KOSTEN") * 1.0;

                        // Ausgabe RechNr, Kunde, Betrag    
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), SQLDate2Normal(resultAusgaben.getString("AUSGABEN_RECHDATUM")));
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 130, 710 - 15 * (zeile - 1), resultAusgaben.getString("AUSGABEN_LIEFERANT"));
                        AusgabeDB(cos, fontPlain, 12, Color.BLACK, 440, 710 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtzeile)));
                        if (resultAusgaben.getString("AUSGABEN_BEZAHLT").equals("1970-01-01")) {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), "----------");
                        } else {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), SQLDate2Normal(resultAusgaben.getString("AUSGABEN_BEZAHLT")));
                        }

                        Gesamtsumme = Gesamtsumme + Gesamtzeile;
                        zeile = zeile + 1;
                    } // while
                    Linie(cos,1,56, 710 - 15 * (zeile - 1), 539, 710 - 15 * (zeile - 1));
                    zeile = zeile + 1;
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), "Gesamtsumme der Ausgaben für Honorarabrechnung: ");
                    AusgabeDB(cos, fontBold, 12, Color.BLACK, 440, 710 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtsumme)));
                    ausHon = Gesamtsumme;

                    // close the content stream for page 
                    cos.close();

                    // Neuer Ausgabenabschnitt Kontoführung - Typ 4     
                    zeile = 1;
                    seite = 1;
                    Gesamtsumme = 0D;

                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);

                    cos = new PDPageContentStream(document, page);

                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Kontoführung");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + Modulhelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 130, 730, "Kontoauszug Nr");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

                    Linie(cos,1,56, 725, 539, 725);
                    
                    SQLAusgaben = conn2.createStatement();

                    if (Umfang) {
                        Sql = "SELECT * FROM TBL_AUSGABEN"
                                + " WHERE ((AUSGABEN_RECHDATUM BETWEEN '" + strVon + "'  AND '" + strBis + "')"
                                + "       OR (AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'))"
                                + "       AND AUSGABEN_TYP = '4'"
                                + " ORDER BY AUSGABEN_RECHDATUM";
                    } else {
                        Sql = "SELECT * FROM TBL_AUSGABEN"
                                + " WHERE AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'"
                                + "       AND AUSGABEN_TYP = '4'"
                                + " ORDER BY AUSGABEN_RECHDATUM";
                    }
                    resultAusgaben = SQLAusgaben.executeQuery(Sql); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 

                    while (resultAusgaben.next()) { // geht durch alle zeilen
                        if (zeile == 48) {
                            cos.close();

                            page = new PDPage(PDRectangle.A4);
                            document.addPage(page);
                            cos = new PDPageContentStream(document, page);

                            zeile = 1;
                            seite = seite + 1;
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Kontoführung");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + Modulhelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 130, 730, "Kontoauszug Nr");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

                            Linie(cos,1,56, 725, 539, 725);
                            
                        } // if

                        Gesamtzeile = resultAusgaben.getFloat("AUSGABEN_KOSTEN") * 1.0;

                        // Ausgabe RechNr, Kunde, Betrag    
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), SQLDate2Normal(resultAusgaben.getString("AUSGABEN_RECHDATUM")));
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 130, 710 - 15 * (zeile - 1), resultAusgaben.getString("AUSGABEN_RECHNNR"));
                        AusgabeDB(cos, fontPlain, 12, Color.BLACK, 440, 710 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtzeile)));
                        if (resultAusgaben.getString("AUSGABEN_BEZAHLT").equals("1970-01-01")) {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), "----------");
                        } else {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), SQLDate2Normal(resultAusgaben.getString("AUSGABEN_BEZAHLT")));
                        }

                        Gesamtsumme = Gesamtsumme + Gesamtzeile;
                        zeile = zeile + 1;
                    } // while
                    Linie(cos,1,56, 710 - 15 * (zeile - 1), 539, 710 - 15 * (zeile - 1));
                    zeile = zeile + 1;
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), "Gesamtsumme der Ausgaben für Kontoführung: ");
                    ausKonto = Gesamtsumme;

                    // close the content stream for page 
                    cos.close();

                    // Neuer Ausgabenabschnitt Telefonkosten - Typ 5       
                    zeile = 1;
                    seite = 1;
                    Gesamtsumme = 0D;

                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);

                    cos = new PDPageContentStream(document, page);

                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Telefonkosten");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + Modulhelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 130, 730, "Beschreibung");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

                    Linie(cos,1,56, 725, 539, 725);
                    
                    SQLAusgaben = conn2.createStatement();

                    if (Umfang) {
                        Sql = "SELECT * FROM TBL_AUSGABEN"
                                + " WHERE ((AUSGABEN_RECHDATUM BETWEEN '" + strVon + "'  AND '" + strBis + "')"
                                + "       OR (AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'))"
                                + "       AND AUSGABEN_TYP = '5'"
                                + " ORDER BY AUSGABEN_RECHDATUM";
                    } else {
                        Sql = "SELECT * FROM TBL_AUSGABEN"
                                + " WHERE AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'"
                                + "       AND AUSGABEN_TYP = '5'"
                                + " ORDER BY AUSGABEN_RECHDATUM";
                    }
                    resultAusgaben = SQLAusgaben.executeQuery(Sql); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 

                    while (resultAusgaben.next()) { // geht durch alle zeilen
                        if (zeile == 48) {
                            cos.close();

                            page = new PDPage(PDRectangle.A4);
                            document.addPage(page);
                            cos = new PDPageContentStream(document, page);

                            zeile = 1;
                            seite = seite + 1;
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Telefonkosten");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + Modulhelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 130, 730, "Beschreibung");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

                            Linie(cos,1,56, 725, 539, 725);
                            
                        } // if

                        Gesamtzeile = resultAusgaben.getFloat("AUSGABEN_KOSTEN") * 1.0;

                        // Ausgabe RechNr, Kunde, Betrag    
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), SQLDate2Normal(resultAusgaben.getString("AUSGABEN_RECHDATUM")));
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 130, 710 - 15 * (zeile - 1), resultAusgaben.getString("AUSGABEN_BESCHREIBUNG"));
                        AusgabeDB(cos, fontPlain, 12, Color.BLACK, 440, 710 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtzeile)));
                        if (resultAusgaben.getString("AUSGABEN_BEZAHLT").equals("1970-01-01")) {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), "----------");
                        } else {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), SQLDate2Normal(resultAusgaben.getString("AUSGABEN_BEZAHLT")));
                        }

                        Gesamtsumme = Gesamtsumme + Gesamtzeile;
                        zeile = zeile + 1;
                    } // while
                    Linie(cos,1,56, 710 - 15 * (zeile - 1), 539, 710 - 15 * (zeile - 1));
                    zeile = zeile + 1;
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), "Gesamtsumme der Ausgaben für Telefonkosten: ");
                    AusgabeDB(cos, fontBold, 12, Color.BLACK, 440, 710 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtsumme)));
                    ausTel = Gesamtsumme;

                    // close the content stream for page 
                    cos.close();

                    // Neuer Ausgabenabschnitt Büro- und Lagerräume inkl. Nebenkosten - Typ 6    
                    zeile = 1;
                    seite = 1;
                    Gesamtsumme = 0D;

                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);

                    cos = new PDPageContentStream(document, page);

                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Büro- und Lagerräume inkl. Nebenkosten");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + Modulhelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 130, 730, "Beschreibung");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

                    Linie(cos,1,56, 725, 539, 725);
                    
                    SQLAusgaben = conn2.createStatement();

                    if (Umfang) {
                        Sql = "SELECT * FROM TBL_AUSGABEN"
                                + " WHERE ((AUSGABEN_RECHDATUM BETWEEN '" + strVon + "'  AND '" + strBis + "')"
                                + "       OR (AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'))"
                                + "       AND AUSGABEN_TYP = '6'"
                                + " ORDER BY AUSGABEN_RECHDATUM";
                    } else {
                        Sql = "SELECT * FROM TBL_AUSGABEN"
                                + " WHERE AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'"
                                + "       AND AUSGABEN_TYP = '6'"
                                + " ORDER BY AUSGABEN_RECHDATUM";
                    }
                    resultAusgaben = SQLAusgaben.executeQuery(Sql); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 

                    while (resultAusgaben.next()) { // geht durch alle zeilen
                        if (zeile == 48) {
                            cos.close();

                            page = new PDPage(PDRectangle.A4);
                            document.addPage(page);
                            cos = new PDPageContentStream(document, page);

                            zeile = 1;
                            seite = seite + 1;
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Büro und Lagerräume inkl. Nebenkosten");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + Modulhelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 130, 730, "Beschreibung");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

                            Linie(cos,1,56, 725, 539, 725);
                            
                        } // if

                        Gesamtzeile = resultAusgaben.getFloat("AUSGABEN_KOSTEN") * 1.0;

                        // Ausgabe RechNr, Kunde, Betrag    
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), SQLDate2Normal(resultAusgaben.getString("AUSGABEN_RECHDATUM")));
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 130, 710 - 15 * (zeile - 1), resultAusgaben.getString("AUSGABEN_BESCHREIBUNG"));
                        AusgabeDB(cos, fontPlain, 12, Color.BLACK, 440, 710 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtzeile)));
                        if (resultAusgaben.getString("AUSGABEN_BEZAHLT").equals("1970-01-01")) {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), "----------");
                        } else {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), SQLDate2Normal(resultAusgaben.getString("AUSGABEN_BEZAHLT")));
                        }

                        Gesamtsumme = Gesamtsumme + Gesamtzeile;
                        zeile = zeile + 1;
                    } // while
                    Linie(cos,1,56, 710 - 15 * (zeile - 1), 539, 710 - 15 * (zeile - 1));
                    
                    zeile = zeile + 1;
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), "Gesamtsumme der Ausgaben für Büro- und Lagerräume inkl. Nebenkosten: ");
                    AusgabeDB(cos, fontBold, 12, Color.BLACK, 440, 710 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtsumme)));
                    ausBuero = Gesamtsumme;

                    // close the content stream for page 
                    cos.close();

                    // Neuer Ausgabenabschnitt Bucherstellung - Typ 7                    
                    zeile = 1;
                    seite = 1;
                    Gesamtsumme = 0D;

                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);

                    cos = new PDPageContentStream(document, page);

                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Kosten für Bucherstellung");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + Modulhelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 130, 730, "RechnungsNr");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 220, 730, "Beschreibung");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

                    Linie(cos,1,56, 725, 539, 725);
                    
                    SQLAusgaben = conn2.createStatement();

                    if (Umfang) {
                        Sql = "SELECT * FROM TBL_AUSGABEN"
                                + " WHERE ((AUSGABEN_RECHDATUM BETWEEN '" + strVon + "'  AND '" + strBis + "')"
                                + "       OR (AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'))"
                                + "       AND AUSGABEN_TYP = '7'"
                                + " ORDER BY AUSGABEN_RECHDATUM";
                    } else {
                        Sql = "SELECT * FROM TBL_AUSGABEN"
                                + " WHERE AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'"
                                + "       AND AUSGABEN_TYP = '7'"
                                + " ORDER BY AUSGABEN_RECHDATUM";
                    }
                    resultAusgaben = SQLAusgaben.executeQuery(Sql); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 

                    while (resultAusgaben.next()) { // geht durch alle zeilen
                        if (zeile == 48) {
                            cos.close();

                            page = new PDPage(PDRectangle.A4);
                            document.addPage(page);
                            cos = new PDPageContentStream(document, page);

                            zeile = 1;
                            seite = seite + 1;
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Kosten für Bucherstellung");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + Modulhelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 130, 730, "RechnungsNr");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 220, 730, "Beschreibung");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

                            Linie(cos,1,56, 725, 539, 725);
                            
                        } // if

                        Gesamtzeile = resultAusgaben.getFloat("AUSGABEN_KOSTEN") * 1.0;

                        // Ausgabe RechNr, Kunde, Betrag    
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), SQLDate2Normal(resultAusgaben.getString("AUSGABEN_RECHDATUM")));
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 130, 710 - 15 * (zeile - 1), resultAusgaben.getString("AUSGABEN_RECHNNR"));
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 220, 710 - 15 * (zeile - 1), resultAusgaben.getString("AUSGABEN_BESCHREIBUNG"));
                        AusgabeDB(cos, fontPlain, 12, Color.BLACK, 440, 710 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtzeile)));
                        if (resultAusgaben.getString("AUSGABEN_BEZAHLT").equals("1970-01-01")) {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), "----------");
                        } else {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), SQLDate2Normal(resultAusgaben.getString("AUSGABEN_BEZAHLT")));
                        }

                        Gesamtsumme = Gesamtsumme + Gesamtzeile;
                        zeile = zeile + 1;
                    } // while
                    Linie(cos,1,56, 710 - 15 * (zeile - 1), 539, 710 - 15 * (zeile - 1));
                    zeile = zeile + 1;
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), "Gesamtsumme der Ausgaben für Bucherstellung: ");
                    AusgabeDB(cos, fontBold, 12, Color.BLACK, 440, 710 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtsumme)));
                    ausErst = Gesamtsumme;

                    // close the content stream for page 
                    cos.close();

                    // Neuer Ausgabenabschnitt Kfz/Fahrtkosten - Typ 8                    
                    zeile = 1;
                    seite = 1;
                    Gesamtsumme = 0D;

                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);

                    cos = new PDPageContentStream(document, page);

                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - sonstige Ausgaben");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + Modulhelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 130, 730, "Beschreibung");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

                    Linie(cos,1,56, 725, 539, 725);
                    
                    SQLAusgaben = conn2.createStatement();

                    if (Umfang) {
                        Sql = "SELECT * FROM TBL_AUSGABEN"
                                + " WHERE ((AUSGABEN_RECHDATUM BETWEEN '" + strVon + "'  AND '" + strBis + "')"
                                + "       OR (AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'))"
                                + "       AND AUSGABEN_TYP = '8'"
                                + " ORDER BY AUSGABEN_RECHDATUM";
                    } else {
                        Sql = "SELECT * FROM TBL_AUSGABEN"
                                + " WHERE AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'"
                                + "       AND AUSGABEN_TYP = '8'"
                                + " ORDER BY AUSGABEN_RECHDATUM";
                    }
                    resultAusgaben = SQLAusgaben.executeQuery(Sql); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 

                    while (resultAusgaben.next()) { // geht durch alle zeilen
                        if (zeile == 48) {
                            cos.close();

                            page = new PDPage(PDRectangle.A4);
                            document.addPage(page);
                            cos = new PDPageContentStream(document, page);

                            zeile = 1;
                            seite = seite + 1;
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Kfz-/Fahrtkosten");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + Modulhelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 130, 730, "Beschreibung");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

                            Linie(cos,1,56, 725, 539, 725);
                            
                        } // if

                        Gesamtzeile = resultAusgaben.getFloat("AUSGABEN_KOSTEN") * 1.0;

                        // Ausgabe RechNr, Kunde, Betrag    
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), SQLDate2Normal(resultAusgaben.getString("AUSGABEN_RECHDATUM")));
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 130, 710 - 15 * (zeile - 1), resultAusgaben.getString("AUSGABEN_BESCHREIBUNG"));
                        AusgabeDB(cos, fontPlain, 12, Color.BLACK, 440, 710 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtzeile)));
                        if (resultAusgaben.getString("AUSGABEN_BEZAHLT").equals("1970-01-01")) {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), "----------");
                        } else {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), SQLDate2Normal(resultAusgaben.getString("AUSGABEN_BEZAHLT")));
                        }

                        Gesamtsumme = Gesamtsumme + Gesamtzeile;
                        zeile = zeile + 1;
                    } // while
                    Linie(cos,1,56, 710 - 15 * (zeile - 1), 539, 710 - 15 * (zeile - 1));
                    zeile = zeile + 1;
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), "Gesamtsumme der Ausgaben für Kfz-/Fahrtkosten: ");
                    ausFahrt = Gesamtsumme;

                    // close the content stream for page 
                    cos.close();

                    // Neuer Ausgabenabschnitt Sonstige - Typ 9                    
                    zeile = 1;
                    seite = 1;
                    Gesamtsumme = 0D;

                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);

                    cos = new PDPageContentStream(document, page);

                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - sonstige Ausgaben");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + Modulhelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 130, 730, "RechnungsNr");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 220, 730, "Beschreibung");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

                    Linie(cos,1,56, 725, 539, 725);
                    
                    SQLAusgaben = conn2.createStatement();

                    if (Umfang) {
                        Sql = "SELECT * FROM TBL_AUSGABEN"
                                + " WHERE ((AUSGABEN_RECHDATUM BETWEEN '" + strVon + "'  AND '" + strBis + "')"
                                + "       OR (AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'))"
                                + "       AND AUSGABEN_TYP = '9'"
                                + " ORDER BY AUSGABEN_RECHDATUM";
                    } else {
                        Sql = "SELECT * FROM TBL_AUSGABEN"
                                + " WHERE AUSGABEN_BEZAHLT BETWEEN '" + strVon + "'  AND '" + strBis + "'"
                                + "       AND AUSGABEN_TYP = '9'"
                                + " ORDER BY AUSGABEN_RECHDATUM";
                    }
                    resultAusgaben = SQLAusgaben.executeQuery(Sql); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 

                    while (resultAusgaben.next()) { // geht durch alle zeilen
                        if (zeile == 48) {
                            cos.close();

                            page = new PDPage(PDRectangle.A4);
                            document.addPage(page);
                            cos = new PDPageContentStream(document, page);

                            zeile = 1;
                            seite = seite + 1;
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - sonstige Ausgaben");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + Modulhelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 130, 730, "RechnungsNr");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 220, 730, "Beschreibung");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

                            Linie(cos,1,56, 725, 539, 725);
                            
                        } // if

                        Gesamtzeile = resultAusgaben.getFloat("AUSGABEN_KOSTEN") * 1.0;

                        // Ausgabe RechNr, Kunde, Betrag    
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), SQLDate2Normal(resultAusgaben.getString("AUSGABEN_RECHDATUM")));
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 130, 710 - 15 * (zeile - 1), resultAusgaben.getString("AUSGABEN_RECHNNR"));
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 220, 710 - 15 * (zeile - 1), resultAusgaben.getString("AUSGABEN_BESCHREIBUNG"));
                        AusgabeDB(cos, fontPlain, 12, Color.BLACK, 440, 710 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Gesamtzeile)));
                        if (resultAusgaben.getString("AUSGABEN_BEZAHLT").equals("1970-01-01")) {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), "----------");
                        } else {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), SQLDate2Normal(resultAusgaben.getString("AUSGABEN_BEZAHLT")));
                        }

                        Gesamtsumme = Gesamtsumme + Gesamtzeile;
                        zeile = zeile + 1;
                    } // while
                    Linie(cos,1,56, 710 - 15 * (zeile - 1), 539, 710 - 15 * (zeile - 1));
                    zeile = zeile + 1;
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), "Gesamtsumme der Ausgaben für sonstige Ausgaben: ");
                    ausSonst = Gesamtsumme;

                    // close the content stream for page 
                    cos.close();

                    // jetzt Gesamtbilanz ausgeben
                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);
                    cos = new PDPageContentStream(document, page);

                    Ausgabe(cos, fontBold, 16, Color.BLACK, 56, 770, "Carola Hartmann Miles-Verlag");
                    Ausgabe(cos, fontBold, 16, Color.BLACK, 56, 750, "Ausgaben im Zeitraum " + strVon + " - " + strBis);
                    Ausgabe(cos, fontBold, 14, Color.BLACK, 100, 580, "- Porto-Kosten");
                    AusgabeDB(cos, fontBold, 16, Color.BLACK, 440, 580, Modulhelferlein.df.format(Modulhelferlein.round2dec(ausPorto)));
                    Ausgabe(cos, fontBold, 16, Color.BLACK, 500, 580, "Euro");
                    Ausgabe(cos, fontBold, 14, Color.BLACK, 100, 560, "- Büromaterial");
                    AusgabeDB(cos, fontBold, 16, Color.BLACK, 440, 560, Modulhelferlein.df.format(Modulhelferlein.round2dec(ausMat)));
                    Ausgabe(cos, fontBold, 16, Color.BLACK, 500, 560, "Euro");
                    Ausgabe(cos, fontBold, 14, Color.BLACK, 100, 540, "- Buchbestellungen bei BoD");
                    AusgabeDB(cos, fontBold, 16, Color.BLACK, 440, 540, Modulhelferlein.df.format(Modulhelferlein.round2dec(ausBest)));
                    Ausgabe(cos, fontBold, 16, Color.BLACK, 500, 540, "Euro");
                    Ausgabe(cos, fontBold, 14, Color.BLACK, 100, 520, "- Honorarabrechnung");
                    AusgabeDB(cos, fontBold, 16, Color.BLACK, 440, 520, Modulhelferlein.df.format(Modulhelferlein.round2dec(ausHon)));
                    Ausgabe(cos, fontBold, 16, Color.BLACK, 500, 520, "Euro");
                    Ausgabe(cos, fontBold, 14, Color.BLACK, 100, 500, "- Kontoführung");
                    AusgabeDB(cos, fontBold, 16, Color.BLACK, 440, 500, Modulhelferlein.df.format(Modulhelferlein.round2dec(ausKonto)));
                    Ausgabe(cos, fontBold, 16, Color.BLACK, 500, 500, "Euro");
                    Ausgabe(cos, fontBold, 14, Color.BLACK, 100, 480, "- Telefonkosten");
                    AusgabeDB(cos, fontBold, 16, Color.BLACK, 440, 480, Modulhelferlein.df.format(Modulhelferlein.round2dec(ausTel)));
                    Ausgabe(cos, fontBold, 16, Color.BLACK, 500, 480, "Euro");
                    Ausgabe(cos, fontBold, 14, Color.BLACK, 100, 460, "- Büro- und Lagerräume inkl. Nebenkosten");
                    AusgabeDB(cos, fontBold, 16, Color.BLACK, 440, 460, Modulhelferlein.df.format(Modulhelferlein.round2dec(ausBuero)));
                    Ausgabe(cos, fontBold, 16, Color.BLACK, 500, 460, "Euro");
                    Ausgabe(cos, fontBold, 14, Color.BLACK, 100, 440, "- Bucherstellung");
                    AusgabeDB(cos, fontBold, 16, Color.BLACK, 440, 440, Modulhelferlein.df.format(Modulhelferlein.round2dec(ausErst)));
                    Ausgabe(cos, fontBold, 16, Color.BLACK, 500, 440, "Euro");
                    Ausgabe(cos, fontBold, 14, Color.BLACK, 100, 420, "- Kfz-/Fahrtkosten");
                    AusgabeDB(cos, fontBold, 16, Color.BLACK, 440, 420, Modulhelferlein.df.format(Modulhelferlein.round2dec(ausFahrt)));
                    Ausgabe(cos, fontBold, 16, Color.BLACK, 500, 420, "Euro");
                    Ausgabe(cos, fontBold, 14, Color.BLACK, 100, 400, "- Sonstige");
                    AusgabeDB(cos, fontBold, 16, Color.BLACK, 440, 400, Modulhelferlein.df.format(Modulhelferlein.round2dec(ausSonst)));
                    Ausgabe(cos, fontBold, 16, Color.BLACK, 500, 400, "Euro");

                    Ausgabe(cos, fontBold, 16, Color.BLACK, 56, 300, "Gesamtsumme der Ausgaben : ");
                    AusgabeDB(cos, fontBold, 16, Color.BLACK, 440, 300, Modulhelferlein.df.format(Modulhelferlein.round2dec(ausPorto + ausMat + ausBest + ausPflicht + ausRez + ausHon + ausKonto + ausTel + ausBuero + ausErst + ausFahrt + ausSonst)));
                    Ausgabe(cos, fontBold, 16, Color.BLACK, 500, 300, "Euro");

                    // close the content stream for page 
                    cos.close();

                    // Save the results and ensure that the document is properly closed:
                    document.save(outputFileName);
                    document.close();

                    Modulhelferlein.Infomeldung(
                            "Liste der Ausgaben ist als PDF gespeichert!");
                    try {
                        Runtime.getRuntime().exec("cmd.exe /c " + "\"" + outputFileName + "\"");
                    } catch (IOException exept) {
                        Modulhelferlein.Fehlermeldung("Exception: " , exept.getMessage());
                    } // try PDF anzeigen

                    SQLAusgaben.close();
                    resultAusgaben.close();
                    conn2.close();
                    conn.close();

                } catch (SQLException exept) {
                    Modulhelferlein.Fehlermeldung("SQL-Exception: SQL-Anfrage nicht moeglich: " , exept.getMessage());
                } // try 
            }
        } catch (IOException e) {
            Modulhelferlein.Fehlermeldung("IO-Exception: " , e.getMessage());
        }

    } // void

} // class

