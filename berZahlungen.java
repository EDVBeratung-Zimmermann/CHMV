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
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import static milesVerlagMain.ModulHelferlein.AusgabeDB;
import static milesVerlagMain.ModulHelferlein.Linie;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import static org.apache.pdfbox.pdmodel.common.PDRectangle.A4;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import static milesVerlagMain.ModulHelferlein.AusgabeLB;

/**
 * Klasse zur Erzeugung einer Liste von offenen Ausgaben
 *
 * @author Thomas Zimmermann
 *
 */
public class berZahlungen {

    /**
     * Erzeugt eine Übersicht der Ausgaben im Zeitraum strVon bis strBis im
     * Format MS EXCEL
     *
     * @param strVon Beginn des Zeitraums
     * @param strBis Ende des Zeitraums
     */
    public static void berichtXLS(String strVon, String strBis) {
        ModulHelferlein.Infomeldung("Diese Funktion ist noch nicht implementiert!");
    }

    /**
     * Erzeugt eine Übersicht der Ausgaben im Zeitraum strVon bis strBis im
     * Format MS Word
     *
     * @param strVon Beginn des Zeitraums
     * @param strBis Ende des Zeitraums
     */
    public static void berichtDOC(String strVon, String strBis) {
        ModulHelferlein.Infomeldung("Diese Funktion ist noch nicht implementiert!");
    }

    /**
     * Erzeugt eine Übersicht der offenen Rechnungen/Ausgaben/Zahlungen im
     * Zeitraum strVon bis strBis im Format PDF
     *
     * @param strVon Beginn des Zeitraums
     * @param strBis Ende des Zeitraums
     */
    public static void berichtPDF(String strVon, String strBis) {
        Double Gesamtsumme = 0D;
        Double Gesamtzeile = 0D;

        ResultSet resultAusgaben = null;

        PDDocument document = new PDDocument();
        PDPage page = new PDPage(A4);
        document.addPage(page);

        PDPageContentStream cos;

        String outputFileName;
        outputFileName = ModulHelferlein.pathBerichte + "\\Ausgaben\\"
                + "Liste-Zahlungen-"
                + ModulHelferlein.printSimpleDateFormat("yyyyMMdd") 
                + ".pdf";

        PDDocumentInformation docInfo = document.getDocumentInformation();

        docInfo.setSubject("Zahlungen");
        docInfo.setTitle("miles-Verlag");
        docInfo.setAuthor("miles-Verlag");
        docInfo.setCreationDate(Calendar.getInstance());
        docInfo.setCreator("miles-Verlag");
        docInfo.setProducer("miles-Verlag");

        PDFont fontPlain = PDType1Font.HELVETICA;
        PDFont fontBold = PDType1Font.HELVETICA_BOLD;

        try { // Start a new content stream which will "hold" the to be created content
            cos = new PDPageContentStream(document, page);

            Integer zeile = 1;
            Integer seite = 1;

            AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 770, "Übersicht der offenen Rechnungen/Ausgaben im Zeitraum " + strVon + " - " + strBis);
            AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 755, "Stand " + ModulHelferlein.printSimpleDateFormat("dd.MM.yyyy"));
            AusgabeLB(cos, fontBold, 12, Color.BLACK, 500, 755, "Seite " + Integer.toString(seite));
            AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 735, "RechNr");
            AusgabeLB(cos, fontBold, 12, Color.BLACK, 200, 735, "Lieferant");
            AusgabeLB(cos, fontBold, 12, Color.BLACK, 500, 735, "Betrag");

            Linie(cos,2,56, 730, 539, 730);
            
            Connection conn = null;

            try { // Datenbank-Treiber laden
                Class.forName(ModulHelferlein.dbDriver);
            } catch (ClassNotFoundException exept) {
                ModulHelferlein.Fehlermeldung("ClassNotFoundException: Treiber nicht gefunden: " + exept.getMessage());
            } // try Datenbank-Treiber laden

            try { // Verbindung zur Datenbank über die JDBC-Brücke
                conn = DriverManager.getConnection(ModulHelferlein.dbUrl, ModulHelferlein.dbUser, ModulHelferlein.dbPassword);
            } catch (SQLException exept) {
                ModulHelferlein.Fehlermeldung("SQL-Exception: Verbindung nicht moeglich: " + exept.getMessage());
            } // try Verbindung zur Datenbank über die JDBC-Brücke

            if (conn != null) {

                Statement SQLAusgaben = null;

                try {
                    SQLAusgaben = conn.createStatement();

                    String Sql = "SELECT  *  FROM  TBL_AUSGABEN "
                            + " WHERE "
                            + "(AUSGABEN_RECHDATUM BETWEEN '" + strVon
                            + "' AND '" + strBis + "')"
                            + " AND "
                            + " AUSGABEN_BEZAHLT  =  '1970-01-01' "
                            + " ORDER  BY  AUSGABEN_RECHDATUM";
                    resultAusgaben = SQLAusgaben.executeQuery(Sql);

                    Gesamtsumme = 0D;

                    while (resultAusgaben.next()) { // geht durch alle zeilen
                        if (zeile == 48) {
                            cos.close();

                            page = new PDPage(A4);
                            document.addPage(page);
                            cos = new PDPageContentStream(document, page);

                            zeile = 1;
                            seite = seite + 1;

                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 770, "Übersicht der offenen Rechnungen/Ausgaben im Zeitraum " + strVon + " - " + strBis);
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 755, "Stand " + ModulHelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 500, 755, "Seite " + Integer.toString(seite));
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 735, "RechNr");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 200, 735, "Lieferant");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 500, 735, "Betrag");

                            Linie(cos,2,56, 730, 539, 730);
                            
                        } // if

                        Gesamtzeile = resultAusgaben.getFloat("AUSGABEN_KOSTEN") * 1D;

                        // Gesamtsumme berechnen
                        Gesamtsumme = Gesamtsumme + Gesamtzeile;

                        // AusgabeLB RechNr, Kunde, Betrag    
                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 715 - 15 * (zeile - 1), resultAusgaben.getString("AUSGABEN_RECHNNR"));
                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 200, 715 - 15 * (zeile - 1), resultAusgaben.getString("AUSGABEN_LIEFERANT"));
                        AusgabeDB(cos, fontPlain, 12, Color.BLACK, 520, 715 - 15 * (zeile - 1), ModulHelferlein.df.format(ModulHelferlein.round2dec(Gesamtzeile)));

                        zeile = zeile + 1;
                    } // while bestellungen

                    Linie(cos,2,56, 715 - 15 * (zeile - 1), 539, 715 - 15 * (zeile - 1));
                    cos.closeAndStroke();
                    zeile = zeile + 1;

                    AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 715 - 15 * (zeile - 1), "Gesamtsumme der Zahlungen : ");
                    AusgabeDB(cos, fontPlain, 12, Color.BLACK, 520, 715 - 15 * (zeile - 1), ModulHelferlein.df.format(ModulHelferlein.round2dec(Gesamtsumme)));

                    // close the content stream for page 
                    cos.close();

                    // Save the results and ensure that the document is properly closed:
                    document.save(outputFileName);
                    document.close();

                    ModulHelferlein.Infomeldung(
                            "Liste der offenen Rechnungen/Ausgaben/Zahlungen ist als PDF gespeichert!");
                    try {
                        Runtime.getRuntime().exec("cmd.exe /c " + "\"" + outputFileName + "\"");
                    } catch (IOException exept) {
                        ModulHelferlein.Fehlermeldung("Exception: " + exept.getMessage());
                    } // try PDF anzeigen

                    resultAusgaben.close();
                    SQLAusgaben.close();
                    conn.close();

                } catch (SQLException exept) {
                    ModulHelferlein.Fehlermeldung("SQL-Exception: SQL-Anfrage nicht moeglich: " + exept.getMessage());
                } // try 

            } // if 
        } catch (IOException e) {
            ModulHelferlein.Fehlermeldung("IO-Exception: " + e.getMessage());
        } // try

    } // void
} // class

