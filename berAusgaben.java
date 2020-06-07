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

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import jxl.Workbook;
import static jxl.format.Alignment.LEFT;
import static jxl.format.Alignment.RIGHT;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import static milesVerlagMain.ModulHelferlein.AusgabeDB;
import static milesVerlagMain.ModulHelferlein.Linie;
import static milesVerlagMain.ModulHelferlein.SQLDate2Normal;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import static milesVerlagMain.ModulHelferlein.AusgabeLB;

/**
 * Klasse zur Erzeugung einer Ausgaben�bersicht
 *
 * @author Thomas Zimmermann
 *
 */
public class berAusgaben {

    /**
     * Erzeugt eine Ausgaben�bersicht f�r den Zeitraum strVon bis strBis im MS
     * EXCEL-Format
     *
     * @param Umfang
     * @param strVon Startdatum
     * @param strBis Endedatum
     */
    public static void berichtXLS(boolean Umfang, final String strVon, final String strBis) throws ParseException {
        Double Gesamtsumme = 0D;
        Double Gesamteinnahmen = 0D;
        Double Gesamtzeile = 0D;

        Integer zeile = 0;

        String Sql = "";

        Statement SQLAusgaben = null;
        ResultSet resultAusgaben = null;

        String outputFileName;
        
        outputFileName = ModulHelferlein.pathBerichte + "\\Ausgaben";
        if (Umfang) {
            outputFileName = outputFileName + "\\Liste-Ausgaben-Ges-";
        } else {
            outputFileName = outputFileName + "\\Liste-Ausgaben-Bez-";
        };
        outputFileName = outputFileName + ModulHelferlein.printSimpleDateFormat("yyyyMMdd") +"-"
                                        + strBis + "-" + strVon
                                        + ".xls";
        
        try {
            WritableWorkbook workbook = Workbook.createWorkbook(new File(outputFileName));
            WritableSheet sheet_Uebersicht = workbook.createSheet("�bersicht", 0);
            WritableSheet sheet_Porto = workbook.createSheet("Portokosten", 1);
            WritableSheet sheet_Material = workbook.createSheet("B�romaterial", 2);
            WritableSheet sheet_Bestellung = workbook.createSheet("Buchbestellungen", 3);
            WritableSheet sheet_Honorar = workbook.createSheet("Honorarabrechnung", 4);
            WritableSheet sheet_Konto = workbook.createSheet("Kontof�hrung BOD", 5);
            WritableSheet sheet_Telefon = workbook.createSheet("Telefonkosten", 6);
            WritableSheet sheet_Raum = workbook.createSheet("B�ro- und Lagerr�ume", 7);
            WritableSheet sheet_Erstellung = workbook.createSheet("Bucherstellung", 8);
            WritableSheet sheet_Fahrt = workbook.createSheet("Fahrtkosten", 9);
            WritableSheet sheet_Sonstig = workbook.createSheet("Sonstiges", 10);

            // A3 = 0, 2
            // D5 = 3, 4
            // Number number = new Number(3, 4, 3.1459);
            // sheet.addCell(number);
            // Aufbau der Tabellenbl�tter
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
            Label label = null;
            
            if (Umfang) {
                label = new Label(0, 0, "�bersicht der Ausgaben aus allen Rechnungen", arial14formatBold);
            } else {
                label = new Label(0, 0, "�bersicht der Ausgaben aus bezahlten Rechnungen", arial14formatBold);
            };
            sheet_Uebersicht.addCell(label);

            label = new Label(0, 2, "Ausgaben wegen Portokosten", arial10formatBold);
            sheet_Uebersicht.addCell(label);
            label = new Label(0, 3, "Ausgaben wegen B�romaterial", arial10formatBold);
            sheet_Uebersicht.addCell(label);
            label = new Label(0, 4, "Ausgaben wegen Buchbestellungen", arial10formatBold);
            sheet_Uebersicht.addCell(label);
            label = new Label(0, 5, "Ausgaben wegen Honorarabrechnungen", arial10formatBold);
            sheet_Uebersicht.addCell(label);
            label = new Label(0, 6, "Ausgaben wegen Kontof�hrung", arial10formatBold);
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
                Class.forName(ModulHelferlein.dbDriver);
            } catch (ClassNotFoundException exept) {
                ModulHelferlein.Fehlermeldung("ClassNotFoundException: Treiber nicht gefunden: " ,exept.getMessage());
            } // try Datenbank-Treiber laden

            try { // Verbindung zur Datenbank �ber die JDBC-Br�cke
                conn = DriverManager.getConnection(ModulHelferlein.dbUrl, ModulHelferlein.dbUser, ModulHelferlein.dbPassword);
            } catch (SQLException exept) {
                ModulHelferlein.Fehlermeldung("SQL-Exception: Verbindung nicht moeglich: " ,exept.getMessage());
            } // try Verbindung zur Datenbank �ber die JDBC-Br�cke

            final Connection conn2 = conn;

            if (conn2 != null) { // Datenbankverbindung steht

                try { // Datenbankabfrage
                    SQLAusgaben = conn2.createStatement();

                    label = new Label(0, 0, "Ausgaben f�r Porto", arial14formatBold);
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

                    //Vorbereiten Corona-Erm��igung
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date date1 = sdf.parse("2020-06-30");
                    Date date2 = sdf.parse("2021-01-01");
                    java.lang.Boolean Corona;
                    while (resultAusgaben.next()) { // geht durch alle zeilen
                        // gibt es Corona-Erm��igung: BESTELLUNG_RECHDAT im Zeitraum 2020-07-01 und 2020-12-31
                        if ((resultAusgaben.getDate("AUSGABEN_RECHDATUM").compareTo(date1) > 0) && (resultAusgaben.getDate("AUSGABEN_RECHDATUM").compareTo(date2) < 0)) {
                            Corona = true;
                        } else {
                            Corona = false;
                        }

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
                        label = new Label(3, zeile, ModulHelferlein.df.format(ModulHelferlein.round2dec(Gesamtzeile)), arial10formatR);
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
                    label = new Label(0, zeile, "Gesamtsumme der Ausgaben f�r Porto", arial10formatR);
                    sheet_Porto.addCell(label);
                    label = new Label(3, zeile, ModulHelferlein.str2dec(Gesamtsumme), arial10formatR);
                    sheet_Porto.addCell(label);
                    
                    // Gesamtsumme auf �bersichtsblatt schreiben
                    label = new Label(3, 2, ModulHelferlein.str2dec(Gesamtsumme), arial10formatR);
                    sheet_Uebersicht.addCell(label);
                    
                    // Tabellenblatt B�romaterial - Typ 1
                    label = new Label(0, 0, "Ausgaben f�r B�romaterial", arial14formatBold);
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
                        label = new Label(3, zeile, ModulHelferlein.df.format(ModulHelferlein.round2dec(Gesamtzeile)), arial10formatR);
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
                    label = new Label(0, zeile, "Gesamtsumme der Ausgaben f�r B�romaterial", arial10formatR);
                    sheet_Material.addCell(label);
                    label = new Label(3, zeile, ModulHelferlein.str2dec(Gesamtsumme), arial10formatR);
                    sheet_Material.addCell(label);
                    
                    // Gesamtsumme auf �bersichtsblatt schreiben
                    label = new Label(3, 3, ModulHelferlein.str2dec(Gesamtsumme), arial10formatR);
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
                        label = new Label(3, zeile, ModulHelferlein.df.format(ModulHelferlein.round2dec(Gesamtzeile)), arial10formatR);
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
                    label = new Label(0, zeile, "Gesamtsumme der Ausgaben f�r Buchbestellungen", arial10formatR);
                    sheet_Bestellung.addCell(label);
                    label = new Label(3, zeile, ModulHelferlein.str2dec(Gesamtsumme), arial10formatR);
                    sheet_Bestellung.addCell(label);
                    
                    // Gesamtsumme auf �bersichtsblatt schreiben
                    label = new Label(3, 4, ModulHelferlein.str2dec(Gesamtsumme), arial10formatR);
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
                        label = new Label(3, zeile, ModulHelferlein.df.format(ModulHelferlein.round2dec(Gesamtzeile)), arial10formatR);
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
                    label = new Label(0, zeile, "Gesamtsumme der Ausgaben f�r Honorare", arial10formatR);
                    sheet_Honorar.addCell(label);
                    label = new Label(3, zeile, ModulHelferlein.str2dec(Gesamtsumme), arial10formatR);
                    sheet_Honorar.addCell(label);
                    
                    // Gesamtsumme auf �bersichtsblatt schreiben
                    label = new Label(3, 5, ModulHelferlein.str2dec(Gesamtsumme), arial10formatR);
                    sheet_Uebersicht.addCell(label);
                    
                    
                    // Tabellenblatt Kontof�hrung - Typ 4 
                    label = new Label(0, 0, "Ausgaben wegen Kontof�hrung", arial14formatBold);
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
                        label = new Label(3, zeile, ModulHelferlein.df.format(ModulHelferlein.round2dec(Gesamtzeile)), arial10formatR);
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
                    label = new Label(0, zeile, "Gesamtsumme der Ausgaben f�r Kontof�hrung", arial10formatR);
                    sheet_Konto.addCell(label);
                    label = new Label(3, zeile, ModulHelferlein.str2dec(Gesamtsumme), arial10formatR);
                    sheet_Konto.addCell(label);
                    
                    // Gesamtsumme auf �bersichtsblatt schreiben
                    label = new Label(3, 6, ModulHelferlein.str2dec(Gesamtsumme), arial10formatR);
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
                        label = new Label(3, zeile, ModulHelferlein.df.format(ModulHelferlein.round2dec(Gesamtzeile)), arial10formatR);
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
                    label = new Label(0, zeile, "Gesamtsumme der Ausgaben f�r Telefon", arial10formatR);
                    sheet_Telefon.addCell(label);
                    label = new Label(3, zeile, ModulHelferlein.str2dec(Gesamtsumme), arial10formatR);
                    sheet_Telefon.addCell(label);
                    
                    // Gesamtsumme auf �bersichtsblatt schreiben
                    label = new Label(3, 7, ModulHelferlein.str2dec(Gesamtsumme), arial10formatR);
                    sheet_Uebersicht.addCell(label);
                    
                    
                    // Tabellenblatt Mieten - Typ 6 
                    label = new Label(0, 0, "Ausgaben wegen B�romiete", arial14formatBold);
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
                        label = new Label(3, zeile, ModulHelferlein.df.format(ModulHelferlein.round2dec(Gesamtzeile)), arial10formatR);
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
                    label = new Label(0, zeile, "Gesamtsumme der Ausgaben f�r B�romiete", arial10formatR);
                    sheet_Raum.addCell(label);
                    label = new Label(3, zeile, ModulHelferlein.str2dec(Gesamtsumme), arial10formatR);
                    sheet_Raum.addCell(label);
                    
                    // Gesamtsumme auf �bersichtsblatt schreiben
                    label = new Label(3, 8, ModulHelferlein.str2dec(Gesamtsumme), arial10formatR);
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
                        label = new Label(3, zeile, ModulHelferlein.df.format(ModulHelferlein.round2dec(Gesamtzeile)), arial10formatR);
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
                    label = new Label(0, zeile, "Gesamtsumme der Ausgaben f�r Bucherstellungen", arial10formatR);
                    sheet_Erstellung.addCell(label);
                    label = new Label(3, zeile, ModulHelferlein.str2dec(Gesamtsumme), arial10formatR);
                    sheet_Erstellung.addCell(label);
                    
                    // Gesamtsumme auf �bersichtsblatt schreiben
                    label = new Label(3, 9, ModulHelferlein.str2dec(Gesamtsumme), arial10formatR);
                    sheet_Uebersicht.addCell(label);
                    
                    
                    // Tabellenblatt Fahrtkosten - Typ 8 
                    label = new Label(0, 0, "Ausgaben f�r Fahrtkosten", arial14formatBold);
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
                        label = new Label(3, zeile, ModulHelferlein.df.format(ModulHelferlein.round2dec(Gesamtzeile)), arial10formatR);
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
                    label = new Label(0, zeile, "Gesamtsumme der Ausgaben f�r Fahrtkosten", arial10formatR);
                    sheet_Fahrt.addCell(label);
                    label = new Label(3, zeile, ModulHelferlein.str2dec(Gesamtsumme), arial10formatR);
                    sheet_Fahrt.addCell(label);
                    
                    // Gesamtsumme auf �bersichtsblatt schreiben
                    label = new Label(3, 10, ModulHelferlein.str2dec(Gesamtsumme), arial10formatR);
                    sheet_Uebersicht.addCell(label);
                    
                    
                    // Tabellenblatt Sonstige - Typ 9 
                    label = new Label(0, 0, "Ausgaben f�r Sonstiges", arial14formatBold);
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
                        label = new Label(3, zeile, ModulHelferlein.df.format(ModulHelferlein.round2dec(Gesamtzeile)), arial10formatR);
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
                    label = new Label(0, zeile, "Gesamtsumme der Ausgaben f�r Sonstiges", arial10formatR);
                    sheet_Sonstig.addCell(label);
                    label = new Label(3, zeile, ModulHelferlein.str2dec(Gesamtsumme), arial10formatR);
                    sheet_Sonstig.addCell(label);
                    
                    // Gesamtsumme auf �bersichtsblatt schreiben
                    label = new Label(3, 11, ModulHelferlein.str2dec(Gesamtsumme), arial10formatR);
                    sheet_Uebersicht.addCell(label);
                    

                } catch (SQLException ex) {
                    ModulHelferlein.Fehlermeldung("SQL-Exception: " , ex.getMessage());
                    //Logger.getLogger(berAusgaben.class.getName()).log(Level.SEVERE, null, ex);
                } // try Datenbankabfrage

                // Gesamteinnahmn schreiben
                label = new Label(3, 13, ModulHelferlein.str2dec(Gesamteinnahmen), arial10formatR);
                sheet_Uebersicht.addCell(label);

                // Fertig - alles schlie�en
                try {// workbook write
                    workbook.write();
                } catch (IOException e) {
                    ModulHelferlein.Fehlermeldung("XLS-Bericht Ausgaben: IO-Exception: " , e.getMessage());
                } // workbook write

                try { // try workbook close
                    workbook.close();
                } catch (IOException e) {
                    ModulHelferlein.Fehlermeldung("XLS-Bericht Ausgaben: IO-Exception: " , e.getMessage());
                } catch (WriteException ex) {
                    ModulHelferlein.Fehlermeldung("XLS-Bericht Ausgaben: Write-Exception: " , ex.getMessage());
                    //Logger.getLogger(berAusgaben.class.getName()).log(Level.SEVERE, null, ex);
                } // try workbook close

                try { // try XLS anzeigen
                    Runtime.getRuntime().exec("cmd.exe /c " + "\"" + outputFileName + "\"");
                } catch (IOException exept) {
                    ModulHelferlein.Fehlermeldung("Bericht Ausgaben: Anzeige XLS-Export: Exception: " , exept.getMessage());
                } // try XLS anzeigen
            } // if

        } catch (IOException e) {
            ModulHelferlein.Fehlermeldung("XLS-Bericht: IO-Exception: " , e.getMessage());
        } catch (WriteException ex) {
            ModulHelferlein.Fehlermeldung("XLS-Bericht Ausgaben: Write-Exception: " , ex.getMessage());
            //Logger.getLogger(berAusgaben.class.getName()).log(Level.SEVERE, null, ex);
        } // try Workbook create

        ModulHelferlein.Infomeldung("Liste der Ausgaben ist als XLS gespeichert!");
    } // void 

    /**
     * Erzeugt eine Ausgaben�bersicht f�r den Zeitraum strVon bis strBis im MS
     * WOrd-Format
     *
     * @param Umfang
     * @param strVon Startdatum
     * @param strBis Endedatum
     */
    public static void berichtDOC(boolean Umfang, final String strVon, final String strBis) {
        ModulHelferlein.Infomeldung("Ausgabe als DOC ist noch nicht implementiert");
    }

    /**
     * Erzeugt eine Ausgaben�bersicht f�r den Zeitraum strVon bis strBis im
     * PDF-Format AusgabenTypListe[] = {"Porto-Kosten", 0 "B�romaterial", 1
     * "Buchbestellungen", 2 "Honorarabrechnung", 3 "Kontof�hrung", 4
     * "Telefonkosten", 5 "B�ro- und Lagerr�ume inkl. Nebenkosten", 6
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

        String outputFileName = ModulHelferlein.pathBerichte + "\\Ausgaben\\"
                + "Liste-Ausgaben-"
                + ModulHelferlein.printSimpleDateFormat("yyyyMMdd") 
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

            AusgabeLB(cos, fontBold, 16, Color.BLACK, 56, 770, "Carola Hartmann Miles-Verlag");
            AusgabeLB(cos, fontBold, 16, Color.BLACK, 56, 750, "Ausgaben im Zeitraum " + strVon + " - " + strBis);
            AusgabeLB(cos, fontBold, 14, Color.BLACK, 100, 580, "- Porto-Kosten");
            AusgabeLB(cos, fontBold, 14, Color.BLACK, 100, 560, "- B�romaterial");
            AusgabeLB(cos, fontBold, 14, Color.BLACK, 100, 540, "- Buchbestellungen bei BoD");
            AusgabeLB(cos, fontBold, 14, Color.BLACK, 100, 520, "- Honorarabrechnung");
            AusgabeLB(cos, fontBold, 14, Color.BLACK, 100, 500, "- Kontof�hrung");
            AusgabeLB(cos, fontBold, 14, Color.BLACK, 100, 480, "- Telefonkosten");
            AusgabeLB(cos, fontBold, 14, Color.BLACK, 100, 460, "- B�ro- und Lagerr�ume inkl. Nebenkosten");
            AusgabeLB(cos, fontBold, 14, Color.BLACK, 100, 440, "- Bucherstellung");
            AusgabeLB(cos, fontBold, 14, Color.BLACK, 100, 420, "- Kfz-/Fahrtkosten");
            AusgabeLB(cos, fontBold, 14, Color.BLACK, 100, 400, "- Sonstige");

            cos.close();
            page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            cos = new PDPageContentStream(document, page);

            Connection conn = null;

            try { // Datenbank-Treiber laden
                Class.forName(ModulHelferlein.dbDriver);
            } catch (ClassNotFoundException exept) {
                ModulHelferlein.Fehlermeldung("ClassNotFoundException: Treiber nicht gefunden: ", exept.getMessage());
            } // try Datenbank-Treiber laden

            try { // Verbindung zur Datenbank �ber die JDBC-Br�cke
                conn = DriverManager.getConnection(ModulHelferlein.dbUrl, ModulHelferlein.dbUser, ModulHelferlein.dbPassword);
            } catch (SQLException exept) {
                ModulHelferlein.Fehlermeldung("SQL-Exception: Verbindung nicht moeglich: " , exept.getMessage());
            } // try Verbindung zur Datenbank �ber die JDBC-Br�cke

            final Connection conn2 = conn;

            if (conn2 != null) {

                try {
                    // Neuer Ausgabenabschnitt Portokosten - Typ 0  
                    Integer zeile = 1;
                    Integer seite = 1;
                    Gesamtsumme = 0D;

                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Portokosten");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + ModulHelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 130, 730, "RechnungsNr");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

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
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Portokosten");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + ModulHelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 130, 730, "RechnungsNr");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

                            Linie(cos,1,56, 725, 539, 725);
                        } // if

                        Gesamtzeile = resultAusgaben.getFloat("AUSGABEN_KOSTEN") * 1.0;

                        // AusgabeLB RechNr, Kunde, Betrag    
                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), SQLDate2Normal(resultAusgaben.getString("AUSGABEN_RECHDATUM")));
                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 130, 710 - 15 * (zeile - 1), resultAusgaben.getString("AUSGABEN_RECHNNR"));
                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 220, 710 - 15 * (zeile - 1), resultAusgaben.getString("AUSGABEN_LIEFERANT"));
                        AusgabeDB(cos, fontPlain, 12, Color.BLACK, 440, 710 - 15 * (zeile - 1), ModulHelferlein.df.format(ModulHelferlein.round2dec(Gesamtzeile)));
                        if (resultAusgaben.getString("AUSGABEN_BEZAHLT").equals("1970-01-01")) {
                            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), "----------");
                        } else {
                            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), SQLDate2Normal(resultAusgaben.getString("AUSGABEN_BEZAHLT")));
                        }

                        Gesamtsumme = Gesamtsumme + Gesamtzeile;
                        zeile = zeile + 1;
                    } // while
                    Linie(cos,1,56, 710 - 15 * (zeile - 1), 539, 710 - 15 * (zeile - 1));
                    zeile = zeile + 1;
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), "Gesamtsumme der Ausgaben f�r Porto: ");
                    AusgabeDB(cos, fontBold, 12, Color.BLACK, 440, 710 - 15 * (zeile - 1), ModulHelferlein.df.format(ModulHelferlein.round2dec(Gesamtsumme)));
                    ausPorto = Gesamtsumme;

                    // close the content stream for page 
                    cos.close();

                    // Neuer Ausgabenabschnitt B�romaterial - Typ 1                    
                    zeile = 1;
                    seite = 1;
                    Gesamtsumme = 0D;

                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);

                    cos = new PDPageContentStream(document, page);

                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - B�romaterial");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + ModulHelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 130, 730, "RechnungsNr");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 220, 730, "Lieferant");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

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
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - B�romaterial");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + ModulHelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 130, 730, "RechnungsNr");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 220, 730, "Lieferant");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

                            Linie(cos,1,56, 725, 539, 725);
                            
                        } // if

                        Gesamtzeile = resultAusgaben.getFloat("AUSGABEN_KOSTEN") * 1.0;

                        // AusgabeLB RechNr, Kunde, Betrag    
                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), SQLDate2Normal(resultAusgaben.getString("AUSGABEN_RECHDATUM")));
                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 130, 710 - 15 * (zeile - 1), resultAusgaben.getString("AUSGABEN_RECHNNR"));
                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 220, 710 - 15 * (zeile - 1), resultAusgaben.getString("AUSGABEN_LIEFERANT"));
                        AusgabeDB(cos, fontPlain, 12, Color.BLACK, 440, 710 - 15 * (zeile - 1), ModulHelferlein.df.format(ModulHelferlein.round2dec(Gesamtzeile)));
                        if (resultAusgaben.getString("AUSGABEN_BEZAHLT").equals("1970-01-01")) {
                            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), "----------");
                        } else {
                            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), SQLDate2Normal(resultAusgaben.getString("AUSGABEN_BEZAHLT")));
                        }

                        Gesamtsumme = Gesamtsumme + Gesamtzeile;
                        zeile = zeile + 1;
                    } // while
                    Linie(cos,1,56, 710 - 15 * (zeile - 1), 539, 710 - 15 * (zeile - 1));
                    zeile = zeile + 1;
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), "Gesamtsumme der Ausgaben f�r B�romaterial: ");
                    AusgabeDB(cos, fontBold, 12, Color.BLACK, 440, 710 - 15 * (zeile - 1), ModulHelferlein.df.format(ModulHelferlein.round2dec(Gesamtsumme)));
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

                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Buchbestellungen bei BoD");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + ModulHelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 130, 730, "RechnungsNr");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 220, 730, "Beschreibung");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

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
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Buchbestellungen bei BoD");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + ModulHelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 130, 730, "RechnungsNr");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 220, 730, "Beschreibung");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

                            Linie(cos,1,56, 725, 539, 725);
                            
                        } // if

                        Gesamtzeile = resultAusgaben.getFloat("AUSGABEN_KOSTEN") * 1.0;

                        // AusgabeLB RechNr, Kunde, Betrag    
                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), SQLDate2Normal(resultAusgaben.getString("AUSGABEN_RECHDATUM")));
                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 130, 710 - 15 * (zeile - 1), resultAusgaben.getString("AUSGABEN_RECHNNR"));
                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 220, 710 - 15 * (zeile - 1), resultAusgaben.getString("AUSGABEN_BESCHREIBUNG"));
                        AusgabeDB(cos, fontPlain, 12, Color.BLACK, 440, 710 - 15 * (zeile - 1), ModulHelferlein.df.format(ModulHelferlein.round2dec(Gesamtzeile)));
                        if (resultAusgaben.getString("AUSGABEN_BEZAHLT").equals("1970-01-01")) {
                            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), "----------");
                        } else {
                            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), SQLDate2Normal(resultAusgaben.getString("AUSGABEN_BEZAHLT")));
                        }

                        Gesamtsumme = Gesamtsumme + Gesamtzeile;
                        zeile = zeile + 1;
                    } // while
                    Linie(cos,1,56, 710 - 15 * (zeile - 1), 539, 710 - 15 * (zeile - 1));
                    
                    zeile = zeile + 1;
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), "Gesamtsumme der Ausgaben f�r Buchbestellungen bei BoD: ");
                    AusgabeDB(cos, fontBold, 12, Color.BLACK, 440, 710 - 15 * (zeile - 1), ModulHelferlein.df.format(ModulHelferlein.round2dec(Gesamtsumme)));
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

                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Honorarabrechnung");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + ModulHelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 130, 730, "Autor");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

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
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Honorarabrechnung");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + ModulHelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 130, 730, "Autor");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

                            Linie(cos,1,56, 725, 539, 725);
                            
                        } // if

                        Gesamtzeile = resultAusgaben.getFloat("AUSGABEN_KOSTEN") * 1.0;

                        // AusgabeLB RechNr, Kunde, Betrag    
                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), SQLDate2Normal(resultAusgaben.getString("AUSGABEN_RECHDATUM")));
                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 130, 710 - 15 * (zeile - 1), resultAusgaben.getString("AUSGABEN_LIEFERANT"));
                        AusgabeDB(cos, fontPlain, 12, Color.BLACK, 440, 710 - 15 * (zeile - 1), ModulHelferlein.df.format(ModulHelferlein.round2dec(Gesamtzeile)));
                        if (resultAusgaben.getString("AUSGABEN_BEZAHLT").equals("1970-01-01")) {
                            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), "----------");
                        } else {
                            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), SQLDate2Normal(resultAusgaben.getString("AUSGABEN_BEZAHLT")));
                        }

                        Gesamtsumme = Gesamtsumme + Gesamtzeile;
                        zeile = zeile + 1;
                    } // while
                    Linie(cos,1,56, 710 - 15 * (zeile - 1), 539, 710 - 15 * (zeile - 1));
                    zeile = zeile + 1;
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), "Gesamtsumme der Ausgaben f�r Honorarabrechnung: ");
                    AusgabeDB(cos, fontBold, 12, Color.BLACK, 440, 710 - 15 * (zeile - 1), ModulHelferlein.df.format(ModulHelferlein.round2dec(Gesamtsumme)));
                    ausHon = Gesamtsumme;

                    // close the content stream for page 
                    cos.close();

                    // Neuer Ausgabenabschnitt Kontof�hrung - Typ 4     
                    zeile = 1;
                    seite = 1;
                    Gesamtsumme = 0D;

                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);

                    cos = new PDPageContentStream(document, page);

                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Kontof�hrung");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + ModulHelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 130, 730, "Kontoauszug Nr");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

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
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Kontof�hrung");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + ModulHelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 130, 730, "Kontoauszug Nr");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

                            Linie(cos,1,56, 725, 539, 725);
                            
                        } // if

                        Gesamtzeile = resultAusgaben.getFloat("AUSGABEN_KOSTEN") * 1.0;

                        // AusgabeLB RechNr, Kunde, Betrag    
                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), SQLDate2Normal(resultAusgaben.getString("AUSGABEN_RECHDATUM")));
                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 130, 710 - 15 * (zeile - 1), resultAusgaben.getString("AUSGABEN_RECHNNR"));
                        AusgabeDB(cos, fontPlain, 12, Color.BLACK, 440, 710 - 15 * (zeile - 1), ModulHelferlein.df.format(ModulHelferlein.round2dec(Gesamtzeile)));
                        if (resultAusgaben.getString("AUSGABEN_BEZAHLT").equals("1970-01-01")) {
                            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), "----------");
                        } else {
                            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), SQLDate2Normal(resultAusgaben.getString("AUSGABEN_BEZAHLT")));
                        }

                        Gesamtsumme = Gesamtsumme + Gesamtzeile;
                        zeile = zeile + 1;
                    } // while
                    Linie(cos,1,56, 710 - 15 * (zeile - 1), 539, 710 - 15 * (zeile - 1));
                    zeile = zeile + 1;
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), "Gesamtsumme der Ausgaben f�r Kontof�hrung: ");
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

                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Telefonkosten");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + ModulHelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 130, 730, "Beschreibung");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

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
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Telefonkosten");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + ModulHelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 130, 730, "Beschreibung");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

                            Linie(cos,1,56, 725, 539, 725);
                            
                        } // if

                        Gesamtzeile = resultAusgaben.getFloat("AUSGABEN_KOSTEN") * 1.0;

                        // AusgabeLB RechNr, Kunde, Betrag    
                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), SQLDate2Normal(resultAusgaben.getString("AUSGABEN_RECHDATUM")));
                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 130, 710 - 15 * (zeile - 1), resultAusgaben.getString("AUSGABEN_BESCHREIBUNG"));
                        AusgabeDB(cos, fontPlain, 12, Color.BLACK, 440, 710 - 15 * (zeile - 1), ModulHelferlein.df.format(ModulHelferlein.round2dec(Gesamtzeile)));
                        if (resultAusgaben.getString("AUSGABEN_BEZAHLT").equals("1970-01-01")) {
                            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), "----------");
                        } else {
                            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), SQLDate2Normal(resultAusgaben.getString("AUSGABEN_BEZAHLT")));
                        }

                        Gesamtsumme = Gesamtsumme + Gesamtzeile;
                        zeile = zeile + 1;
                    } // while
                    Linie(cos,1,56, 710 - 15 * (zeile - 1), 539, 710 - 15 * (zeile - 1));
                    zeile = zeile + 1;
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), "Gesamtsumme der Ausgaben f�r Telefonkosten: ");
                    AusgabeDB(cos, fontBold, 12, Color.BLACK, 440, 710 - 15 * (zeile - 1), ModulHelferlein.df.format(ModulHelferlein.round2dec(Gesamtsumme)));
                    ausTel = Gesamtsumme;

                    // close the content stream for page 
                    cos.close();

                    // Neuer Ausgabenabschnitt B�ro- und Lagerr�ume inkl. Nebenkosten - Typ 6    
                    zeile = 1;
                    seite = 1;
                    Gesamtsumme = 0D;

                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);

                    cos = new PDPageContentStream(document, page);

                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - B�ro- und Lagerr�ume inkl. Nebenkosten");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + ModulHelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 130, 730, "Beschreibung");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

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
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - B�ro und Lagerr�ume inkl. Nebenkosten");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + ModulHelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 130, 730, "Beschreibung");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

                            Linie(cos,1,56, 725, 539, 725);
                            
                        } // if

                        Gesamtzeile = resultAusgaben.getFloat("AUSGABEN_KOSTEN") * 1.0;

                        // AusgabeLB RechNr, Kunde, Betrag    
                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), SQLDate2Normal(resultAusgaben.getString("AUSGABEN_RECHDATUM")));
                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 130, 710 - 15 * (zeile - 1), resultAusgaben.getString("AUSGABEN_BESCHREIBUNG"));
                        AusgabeDB(cos, fontPlain, 12, Color.BLACK, 440, 710 - 15 * (zeile - 1), ModulHelferlein.df.format(ModulHelferlein.round2dec(Gesamtzeile)));
                        if (resultAusgaben.getString("AUSGABEN_BEZAHLT").equals("1970-01-01")) {
                            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), "----------");
                        } else {
                            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), SQLDate2Normal(resultAusgaben.getString("AUSGABEN_BEZAHLT")));
                        }

                        Gesamtsumme = Gesamtsumme + Gesamtzeile;
                        zeile = zeile + 1;
                    } // while
                    Linie(cos,1,56, 710 - 15 * (zeile - 1), 539, 710 - 15 * (zeile - 1));
                    
                    zeile = zeile + 1;
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), "Gesamtsumme der Ausgaben f�r B�ro- und Lagerr�ume inkl. Nebenkosten: ");
                    AusgabeDB(cos, fontBold, 12, Color.BLACK, 440, 710 - 15 * (zeile - 1), ModulHelferlein.df.format(ModulHelferlein.round2dec(Gesamtsumme)));
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

                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Kosten f�r Bucherstellung");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + ModulHelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 130, 730, "RechnungsNr");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 220, 730, "Beschreibung");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

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
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Kosten f�r Bucherstellung");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + ModulHelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 130, 730, "RechnungsNr");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 220, 730, "Beschreibung");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

                            Linie(cos,1,56, 725, 539, 725);
                            
                        } // if

                        Gesamtzeile = resultAusgaben.getFloat("AUSGABEN_KOSTEN") * 1.0;

                        // AusgabeLB RechNr, Kunde, Betrag    
                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), SQLDate2Normal(resultAusgaben.getString("AUSGABEN_RECHDATUM")));
                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 130, 710 - 15 * (zeile - 1), resultAusgaben.getString("AUSGABEN_RECHNNR"));
                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 220, 710 - 15 * (zeile - 1), resultAusgaben.getString("AUSGABEN_BESCHREIBUNG"));
                        AusgabeDB(cos, fontPlain, 12, Color.BLACK, 440, 710 - 15 * (zeile - 1), ModulHelferlein.df.format(ModulHelferlein.round2dec(Gesamtzeile)));
                        if (resultAusgaben.getString("AUSGABEN_BEZAHLT").equals("1970-01-01")) {
                            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), "----------");
                        } else {
                            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), SQLDate2Normal(resultAusgaben.getString("AUSGABEN_BEZAHLT")));
                        }

                        Gesamtsumme = Gesamtsumme + Gesamtzeile;
                        zeile = zeile + 1;
                    } // while
                    Linie(cos,1,56, 710 - 15 * (zeile - 1), 539, 710 - 15 * (zeile - 1));
                    zeile = zeile + 1;
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), "Gesamtsumme der Ausgaben f�r Bucherstellung: ");
                    AusgabeDB(cos, fontBold, 12, Color.BLACK, 440, 710 - 15 * (zeile - 1), ModulHelferlein.df.format(ModulHelferlein.round2dec(Gesamtsumme)));
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

                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - sonstige Ausgaben");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + ModulHelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 130, 730, "Beschreibung");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

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
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Kfz-/Fahrtkosten");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + ModulHelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 130, 730, "Beschreibung");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

                            Linie(cos,1,56, 725, 539, 725);
                            
                        } // if

                        Gesamtzeile = resultAusgaben.getFloat("AUSGABEN_KOSTEN") * 1.0;

                        // AusgabeLB RechNr, Kunde, Betrag    
                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), SQLDate2Normal(resultAusgaben.getString("AUSGABEN_RECHDATUM")));
                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 130, 710 - 15 * (zeile - 1), resultAusgaben.getString("AUSGABEN_BESCHREIBUNG"));
                        AusgabeDB(cos, fontPlain, 12, Color.BLACK, 440, 710 - 15 * (zeile - 1), ModulHelferlein.df.format(ModulHelferlein.round2dec(Gesamtzeile)));
                        if (resultAusgaben.getString("AUSGABEN_BEZAHLT").equals("1970-01-01")) {
                            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), "----------");
                        } else {
                            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), SQLDate2Normal(resultAusgaben.getString("AUSGABEN_BEZAHLT")));
                        }

                        Gesamtsumme = Gesamtsumme + Gesamtzeile;
                        zeile = zeile + 1;
                    } // while
                    Linie(cos,1,56, 710 - 15 * (zeile - 1), 539, 710 - 15 * (zeile - 1));
                    zeile = zeile + 1;
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), "Gesamtsumme der Ausgaben f�r Kfz-/Fahrtkosten: ");
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

                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - sonstige Ausgaben");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + ModulHelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 130, 730, "RechnungsNr");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 220, 730, "Beschreibung");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

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
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - sonstige Ausgaben");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 755, "Ausgaben im Zeitraum " + strVon + " - " + strBis + ", Stand " + ModulHelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 500, 770, "Seite " + Integer.toString(seite));
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 730, "Datum");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 130, 730, "RechnungsNr");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 220, 730, "Beschreibung");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 400, 730, "Ausgaben");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 480, 730, "Bezahlt");

                            Linie(cos,1,56, 725, 539, 725);
                            
                        } // if

                        Gesamtzeile = resultAusgaben.getFloat("AUSGABEN_KOSTEN") * 1.0;

                        // AusgabeLB RechNr, Kunde, Betrag    
                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), SQLDate2Normal(resultAusgaben.getString("AUSGABEN_RECHDATUM")));
                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 130, 710 - 15 * (zeile - 1), resultAusgaben.getString("AUSGABEN_RECHNNR"));
                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 220, 710 - 15 * (zeile - 1), resultAusgaben.getString("AUSGABEN_BESCHREIBUNG"));
                        AusgabeDB(cos, fontPlain, 12, Color.BLACK, 440, 710 - 15 * (zeile - 1), ModulHelferlein.df.format(ModulHelferlein.round2dec(Gesamtzeile)));
                        if (resultAusgaben.getString("AUSGABEN_BEZAHLT").equals("1970-01-01")) {
                            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), "----------");
                        } else {
                            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 480, 710 - 15 * (zeile - 1), SQLDate2Normal(resultAusgaben.getString("AUSGABEN_BEZAHLT")));
                        }

                        Gesamtsumme = Gesamtsumme + Gesamtzeile;
                        zeile = zeile + 1;
                    } // while
                    Linie(cos,1,56, 710 - 15 * (zeile - 1), 539, 710 - 15 * (zeile - 1));
                    zeile = zeile + 1;
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 710 - 15 * (zeile - 1), "Gesamtsumme der Ausgaben f�r sonstige Ausgaben: ");
                    ausSonst = Gesamtsumme;

                    // close the content stream for page 
                    cos.close();

                    // jetzt Gesamtbilanz ausgeben
                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);
                    cos = new PDPageContentStream(document, page);

                    AusgabeLB(cos, fontBold, 16, Color.BLACK, 56, 770, "Carola Hartmann Miles-Verlag");
                    AusgabeLB(cos, fontBold, 16, Color.BLACK, 56, 750, "Ausgaben im Zeitraum " + strVon + " - " + strBis);
                    AusgabeLB(cos, fontBold, 14, Color.BLACK, 100, 580, "- Porto-Kosten");
                    AusgabeDB(cos, fontBold, 16, Color.BLACK, 440, 580, ModulHelferlein.df.format(ModulHelferlein.round2dec(ausPorto)));
                    AusgabeLB(cos, fontBold, 16, Color.BLACK, 500, 580, "Euro");
                    AusgabeLB(cos, fontBold, 14, Color.BLACK, 100, 560, "- B�romaterial");
                    AusgabeDB(cos, fontBold, 16, Color.BLACK, 440, 560, ModulHelferlein.df.format(ModulHelferlein.round2dec(ausMat)));
                    AusgabeLB(cos, fontBold, 16, Color.BLACK, 500, 560, "Euro");
                    AusgabeLB(cos, fontBold, 14, Color.BLACK, 100, 540, "- Buchbestellungen bei BoD");
                    AusgabeDB(cos, fontBold, 16, Color.BLACK, 440, 540, ModulHelferlein.df.format(ModulHelferlein.round2dec(ausBest)));
                    AusgabeLB(cos, fontBold, 16, Color.BLACK, 500, 540, "Euro");
                    AusgabeLB(cos, fontBold, 14, Color.BLACK, 100, 520, "- Honorarabrechnung");
                    AusgabeDB(cos, fontBold, 16, Color.BLACK, 440, 520, ModulHelferlein.df.format(ModulHelferlein.round2dec(ausHon)));
                    AusgabeLB(cos, fontBold, 16, Color.BLACK, 500, 520, "Euro");
                    AusgabeLB(cos, fontBold, 14, Color.BLACK, 100, 500, "- Kontof�hrung");
                    AusgabeDB(cos, fontBold, 16, Color.BLACK, 440, 500, ModulHelferlein.df.format(ModulHelferlein.round2dec(ausKonto)));
                    AusgabeLB(cos, fontBold, 16, Color.BLACK, 500, 500, "Euro");
                    AusgabeLB(cos, fontBold, 14, Color.BLACK, 100, 480, "- Telefonkosten");
                    AusgabeDB(cos, fontBold, 16, Color.BLACK, 440, 480, ModulHelferlein.df.format(ModulHelferlein.round2dec(ausTel)));
                    AusgabeLB(cos, fontBold, 16, Color.BLACK, 500, 480, "Euro");
                    AusgabeLB(cos, fontBold, 14, Color.BLACK, 100, 460, "- B�ro- und Lagerr�ume inkl. Nebenkosten");
                    AusgabeDB(cos, fontBold, 16, Color.BLACK, 440, 460, ModulHelferlein.df.format(ModulHelferlein.round2dec(ausBuero)));
                    AusgabeLB(cos, fontBold, 16, Color.BLACK, 500, 460, "Euro");
                    AusgabeLB(cos, fontBold, 14, Color.BLACK, 100, 440, "- Bucherstellung");
                    AusgabeDB(cos, fontBold, 16, Color.BLACK, 440, 440, ModulHelferlein.df.format(ModulHelferlein.round2dec(ausErst)));
                    AusgabeLB(cos, fontBold, 16, Color.BLACK, 500, 440, "Euro");
                    AusgabeLB(cos, fontBold, 14, Color.BLACK, 100, 420, "- Kfz-/Fahrtkosten");
                    AusgabeDB(cos, fontBold, 16, Color.BLACK, 440, 420, ModulHelferlein.df.format(ModulHelferlein.round2dec(ausFahrt)));
                    AusgabeLB(cos, fontBold, 16, Color.BLACK, 500, 420, "Euro");
                    AusgabeLB(cos, fontBold, 14, Color.BLACK, 100, 400, "- Sonstige");
                    AusgabeDB(cos, fontBold, 16, Color.BLACK, 440, 400, ModulHelferlein.df.format(ModulHelferlein.round2dec(ausSonst)));
                    AusgabeLB(cos, fontBold, 16, Color.BLACK, 500, 400, "Euro");

                    AusgabeLB(cos, fontBold, 16, Color.BLACK, 56, 300, "Gesamtsumme der Ausgaben : ");
                    AusgabeDB(cos, fontBold, 16, Color.BLACK, 440, 300, ModulHelferlein.df.format(ModulHelferlein.round2dec(ausPorto + ausMat + ausBest + ausPflicht + ausRez + ausHon + ausKonto + ausTel + ausBuero + ausErst + ausFahrt + ausSonst)));
                    AusgabeLB(cos, fontBold, 16, Color.BLACK, 500, 300, "Euro");

                    // close the content stream for page 
                    cos.close();

                    // Save the results and ensure that the document is properly closed:
                    document.save(outputFileName);
                    document.close();

                    ModulHelferlein.Infomeldung(
                            "Liste der Ausgaben ist als PDF gespeichert!");
                    try {
                        Runtime.getRuntime().exec("cmd.exe /c " + "\"" + outputFileName + "\"");
                    } catch (IOException exept) {
                        ModulHelferlein.Fehlermeldung("Exception: " , exept.getMessage());
                    } // try PDF anzeigen

                    SQLAusgaben.close();
                    resultAusgaben.close();
                    conn2.close();
                    conn.close();

                } catch (SQLException exept) {
                    ModulHelferlein.Fehlermeldung("SQL-Exception: SQL-Anfrage nicht moeglich: " , exept.getMessage());
                } // try 
            }
        } catch (IOException e) {
            ModulHelferlein.Fehlermeldung("IO-Exception: " , e.getMessage());
        }

    } // void

} // class

