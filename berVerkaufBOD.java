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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
import static milesVerlagMain.ModulHelferlein.AusgabeDB;
import static milesVerlagMain.ModulHelferlein.AusgabeRB;
import static milesVerlagMain.ModulHelferlein.Linie;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import static milesVerlagMain.ModulHelferlein.AusgabeLB;
import static milesVerlagMain.ModulHelferlein.Ausgabe;

/**
 * Klasse zur Erzeugung einer Übersicht von Verkäufen durch BoD
 *
 * @author Thomas Zimmermann
 *
 */
public class berVerkaufBOD {

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

    static String AstrVon = "";
    static String AstrBis = "";

    private static void berichtPDF(String Sortierung, String strVon, String strBis) {
        Integer zeile = 1;
        Integer seite = 1;
        Integer basis = 470;
        Double Umsatz = 0.0;

        PDDocument document = new PDDocument();
        //PDPage page1 = new PDPage(PDPage.PAGE_SIZE_A4);
        //PDPage page1 = new PDPage(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));
        float POINTS_PER_INCH = 72;
        float POINTS_PER_MM = 1 / (10 * 2.54f) * POINTS_PER_INCH;
        PDPage page1 = new PDPage(new PDRectangle(297 * POINTS_PER_MM, 210 * POINTS_PER_MM));
        document.addPage(page1);

        PDPageContentStream cos;

        PDFont fontPlain = PDType1Font.HELVETICA;
        PDFont fontBold = PDType1Font.HELVETICA_BOLD;

        try { // Start a new content stream which will "hold" the to be created content
            Connection conn = null;

            Class.forName(ModulHelferlein.dbDriver);
            conn = DriverManager.getConnection(ModulHelferlein.dbUrl, ModulHelferlein.dbUser, ModulHelferlein.dbPassword);

            if (conn != null) {
                cos = new PDPageContentStream(document, page1);

                String outputFileName = ModulHelferlein.pathBerichte + "/Verkäufe/"
                        + "Verkaufsstatistik-BOD"
                        + "-"
                        + Sortierung
                        + "-"
                        + strVon
                        + "-"
                        + strBis
                        + "-"
                        + ModulHelferlein.printSimpleDateFormat("yyyyMMdd")
                        + ".pdf";
                PDDocumentInformation docInfo = document.getDocumentInformation();

                docInfo.setSubject("Übersicht Miles-Verlag " + strVon + "-" + strBis);
                docInfo.setTitle("miles-Verlag");
                docInfo.setAuthor("miles-Verlag");
                docInfo.setCreationDate(Calendar.getInstance());
                docInfo.setCreator("miles-Verlag");
                docInfo.setProducer("miles-Verlag");

                switch (Sortierung) {
                    case "Autor":
                        resultVerkauf = SQLVerkauf.executeQuery("SELECT DISTINCT VERKAUF_AUTOR FROM TBL_VERKAUF ORDER BY VERKAUF_AUTOR");
                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 550, "Übersicht der BOD-Verkäufe im Zeitraum " + strVon + " - " + strBis);
                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 525, "Stand " + ModulHelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 750, 525, "Seite " + Integer.toString(seite));

                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 495, "Autor");
                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 100, 495, "Titel");
                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 465, 495, "ISBN");

                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 570, 495, "Anzahl");
                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 620, 495, "- Autor");
                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 670, 495, "- Werbung");
                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 750, 495, "Umsatz");

                        Linie(cos,1,55, 490, 800, 490);
                        
                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 475, "Übertrag");
                        AusgabeDB(cos, fontBold, 12, Color.BLACK, 790, 475, String.valueOf(ModulHelferlein.round2dec(Umsatz)));

                        Linie(cos,2,55, 470, 800, 470);
                        
                        zeile = 1;
                        while (resultVerkauf.next()) {
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, basis - zeile * 15, resultVerkauf.getString("VERKAUF_Autor"));
                            resultVerkauf2 = SQLVerkauf2.executeQuery("SELECT * FROM TBL_VERKAUF "
                                    + " WHERE VERKAUF_AUTOR = '" + resultVerkauf.getString("VERKAUF_AUTOR") + "'"
                                    + " ORDER BY VERKAUF_TITEL");

                            zeile = zeile + 1;
                            while (resultVerkauf2.next()) {
                                if (zeile == 30) {
                                    cos.close();
                                    // neue Seite 
                                    seite = seite + 1;
                                    zeile = 1;
                                    //PDPage page = new PDPage(PDPage.PAGE_SIZE_A4);
                                    PDPage page = new PDPage(new PDRectangle(297 * POINTS_PER_MM, 210 * POINTS_PER_MM));
                                    document.addPage(page);
                                    cos = new PDPageContentStream(document, page);
                                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 550, "Übersicht der BOD-Verkäufe im Zeitraum " + strVon + " - " + strBis);
                                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 525, "Stand " + ModulHelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 750, 525, "Seite " + Integer.toString(seite));

                                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 495, "Autor");
                                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 100, 495, "Titel");
                                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 465, 495, "ISBN");
                                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 570, 495, "Anzahl");
                                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 620, 495, "- Autor");
                                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 670, 495, "- Werbung");
                                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 750, 495, "Umsatz");

                                    Linie(cos,1,55, 490, 800, 490);
                                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 475, "Übertrag");
                                    AusgabeDB(cos, fontBold, 12, Color.BLACK, 790, 475, String.valueOf(ModulHelferlein.round2dec(Umsatz)));

                                    Linie(cos,1,55, 470, 800, 470);
                                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, basis - zeile * 15, resultVerkauf.getString("VERKAUF_Autor"));
                                    zeile = zeile + 1;
                                } // if
                                Ausgabe(cos, fontBold, 12, Color.BLACK, 100, basis - zeile * 15, ModulHelferlein.CheckStr(resultVerkauf2.getString("VERKAUF_TITEL")), 360);
                                Ausgabe(cos, fontBold, 12, Color.BLACK, 465, basis - zeile * 15, resultVerkauf2.getString("VERKAUF_ISBN"), 100);
                                AusgabeRB(cos, fontBold, 12, Color.BLACK, 600, basis - zeile * 15, resultVerkauf2.getString("VERKAUF_Anzahl_GESAMT"));
                                AusgabeRB(cos, fontBold, 12, Color.BLACK, 650, basis - zeile * 15, resultVerkauf2.getString("VERKAUF_Anzahl_Autor"));
                                AusgabeRB(cos, fontBold, 12, Color.BLACK, 700, basis - zeile * 15, resultVerkauf2.getString("VERKAUF_Anzahl_Geschenk"));
                                AusgabeDB(cos, fontBold, 12, Color.BLACK, 790, basis - zeile * 15, String.valueOf(ModulHelferlein.round2dec(resultVerkauf2.getFloat("VERKAUF_Umsatz"))));

                                Umsatz = Umsatz + resultVerkauf2.getFloat("VERKAUF_Umsatz");
                                zeile = zeile + 1;

                                if (zeile == 30) {
                                    cos.close();
                                    // neue Seite 
                                    seite = seite + 1;
                                    zeile = 1;
                                    //PDPage page = new PDPage(PDPage.PAGE_SIZE_A4);
                                    PDPage page = new PDPage(new PDRectangle(297 * POINTS_PER_MM, 210 * POINTS_PER_MM));
                                    document.addPage(page);
                                    cos = new PDPageContentStream(document, page);
                                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 550, "Übersicht der BOD-Verkäufe im Zeitraum " + strVon + " - " + strBis);
                                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 525, "Stand " + ModulHelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 750, 525, "Seite " + Integer.toString(seite));

                                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 495, "Autor");
                                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 100, 495, "Titel");
                                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 465, 495, "ISBN");
                                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 570, 495, "Anzahl");
                                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 620, 495, "- Autor");
                                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 670, 495, "- Werbung");
                                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 750, 495, "Umsatz");

                                    Linie(cos,1,55, 490, 800, 490);
                                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 475, "Übertrag");
                                    AusgabeDB(cos, fontBold, 12, Color.BLACK, 790, 475, String.valueOf(ModulHelferlein.round2dec(Umsatz)));

                                    Linie(cos,2,55, 470, 800, 470);
                                    
                                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, basis - zeile * 15, resultVerkauf.getString("VERKAUF_Autor"));
                                    zeile = zeile + 1;
                                } // if
                            } // while bücher des autor
                        } // while autor
                        break;
                    case "ISBN":
                        resultVerkauf = SQLVerkauf.executeQuery("SELECT * FROM TBL_VERKAUF "
                                + " ORDER BY VERKAUF_ISBN");

                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 550, "Übersicht der BOD-Verkäufe im Zeitraum " + strVon + " - " + strBis);
                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 525, "Stand " + ModulHelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 750, 525, "Seite " + Integer.toString(seite));

                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 495, "ISBN");
                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 100, 495, "Titel");
                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 230, 495, "Autor");

                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 570, 495, "Anzahl");
                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 620, 495, "- Autor");
                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 670, 495, "- Werbung");
                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 750, 495, "Umsatz");

                        Linie(cos,1,55, 490, 800, 490);
                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 475, "Übertrag");
                        AusgabeDB(cos, fontBold, 12, Color.BLACK, 790, 475, String.valueOf(ModulHelferlein.round2dec(Umsatz)));

                        Linie(cos,2,55, 470, 800, 470);
                        
                        zeile = 1;
                        while (resultVerkauf.next()) {
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 55, basis - zeile * 15, resultVerkauf.getString("VERKAUF_ISBN"), 100);
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 160, basis - zeile * 15, ModulHelferlein.CheckStr(resultVerkauf.getString("VERKAUF_Titel")), 200);
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 265, basis - zeile * 15, resultVerkauf.getString("VERKAUF_Autor"), 200);
                            AusgabeRB(cos, fontBold, 12, Color.BLACK, 600, basis - zeile * 15, resultVerkauf.getString("VERKAUF_Anzahl_GESAMT"));
                            AusgabeRB(cos, fontBold, 12, Color.BLACK, 650, basis - zeile * 15, resultVerkauf.getString("VERKAUF_Anzahl_Autor"));
                            AusgabeRB(cos, fontBold, 12, Color.BLACK, 700, basis - zeile * 15, resultVerkauf.getString("VERKAUF_Anzahl_Geschenk"));
                            AusgabeDB(cos, fontBold, 12, Color.BLACK, 790, basis - zeile * 15, String.valueOf(ModulHelferlein.round2dec(resultVerkauf.getFloat("VERKAUF_Umsatz"))));

                            Umsatz = Umsatz + resultVerkauf.getFloat("VERKAUF_Umsatz");
                            zeile = zeile + 1;

                            if (zeile == 30) {
                                cos.close();
                                // neue Seite 
                                seite = seite + 1;
                                zeile = 1;
                                //PDPage page = new PDPage(PDPage.PAGE_SIZE_A4);
                                PDPage page = new PDPage(new PDRectangle(297 * POINTS_PER_MM, 210 * POINTS_PER_MM));
                                document.addPage(page);
                                cos = new PDPageContentStream(document, page);
                                AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 550, "Übersicht der BOD-Verkäufe im Zeitraum " + strVon + " - " + strBis);
                                AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 525, "Stand " + ModulHelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                                AusgabeLB(cos, fontBold, 12, Color.BLACK, 750, 525, "Seite " + Integer.toString(seite));

                                Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 495, "ISBN", 100);
                                Ausgabe(cos, fontBold, 12, Color.BLACK, 160, 495, "Titel", 200);
                                Ausgabe(cos, fontBold, 12, Color.BLACK, 265, 495, "Autor", 200);
                                AusgabeLB(cos, fontBold, 12, Color.BLACK, 570, 495, "Anzahl");
                                AusgabeLB(cos, fontBold, 12, Color.BLACK, 620, 495, "- Autor");
                                AusgabeLB(cos, fontBold, 12, Color.BLACK, 670, 495, "- Werbung");
                                AusgabeLB(cos, fontBold, 12, Color.BLACK, 750, 495, "Umsatz");

                                Linie(cos,1,55, 490, 800, 490);
                                AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 475, "Übertrag");
                                AusgabeDB(cos, fontBold, 12, Color.BLACK, 790, 475, String.valueOf(ModulHelferlein.round2dec(Umsatz)));

                                Linie(cos,2,55, 470, 800, 470);
                                
                            } // if
                        } // while
                        break;
                    case "Titel":
                        resultVerkauf = SQLVerkauf.executeQuery("SELECT * FROM TBL_VERKAUF "
                                + " ORDER BY VERKAUF_TITEL");
                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 550, "Übersicht der BOD-Verkäufe im Zeitraum " + strVon + " - " + strBis);
                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 525, "Stand " + ModulHelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 750, 525, "Seite " + Integer.toString(seite));

                        Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 495, "Titel", 200);
                        Ausgabe(cos, fontBold, 12, Color.BLACK, 260, 495, "ISBN", 100);
                        Ausgabe(cos, fontBold, 12, Color.BLACK, 365, 495, "Autor", 200);

                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 570, 495, "Anzahl");
                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 620, 495, "- Autor");
                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 670, 495, "- Werbung");
                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 750, 495, "Umsatz");

                        Linie(cos,1,55, 490, 800, 490);
                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 475, "Übertrag");
                        AusgabeDB(cos, fontBold, 12, Color.BLACK, 790, 475, String.valueOf(ModulHelferlein.round2dec(Umsatz)));

                        Linie(cos,2,55, 470, 800, 470);
                        
                        zeile = 1;
                        while (resultVerkauf.next()) {
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 55, basis - zeile * 15, ModulHelferlein.CheckStr(resultVerkauf.getString("VERKAUF_Titel")), 200);
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 260, basis - zeile * 15, resultVerkauf.getString("VERKAUF_ISBN"), 100);
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 365, basis - zeile * 15, resultVerkauf.getString("VERKAUF_Autor"), 200);
                            AusgabeRB(cos, fontBold, 12, Color.BLACK, 600, basis - zeile * 15, resultVerkauf.getString("VERKAUF_Anzahl_GESAMT"));
                            AusgabeRB(cos, fontBold, 12, Color.BLACK, 650, basis - zeile * 15, resultVerkauf.getString("VERKAUF_Anzahl_Autor"));
                            AusgabeRB(cos, fontBold, 12, Color.BLACK, 700, basis - zeile * 15, resultVerkauf.getString("VERKAUF_Anzahl_Geschenk"));
                            AusgabeDB(cos, fontBold, 12, Color.BLACK, 790, basis - zeile * 15, String.valueOf(ModulHelferlein.round2dec(resultVerkauf.getFloat("VERKAUF_Umsatz"))));

                            Umsatz = Umsatz + resultVerkauf.getFloat("VERKAUF_Umsatz");
                            zeile = zeile + 1;

                            if (zeile == 30) {
                                cos.close();
                                // neue Seite 
                                seite = seite + 1;
                                zeile = 1;
                                //PDPage page = new PDPage(PDPage.PAGE_SIZE_A4);
                                PDPage page = new PDPage(new PDRectangle(297 * POINTS_PER_MM, 210 * POINTS_PER_MM));
                                document.addPage(page);
                                cos = new PDPageContentStream(document, page);
                                AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 550, "Übersicht der BOD-Verkäufe im Zeitraum " + strVon + " - " + strBis);
                                AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 525, "Stand " + ModulHelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                                AusgabeLB(cos, fontBold, 12, Color.BLACK, 750, 525, "Seite " + Integer.toString(seite));

                                Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 495, "Titel", 200);
                                Ausgabe(cos, fontBold, 12, Color.BLACK, 260, 495, "ISBN", 100);
                                Ausgabe(cos, fontBold, 12, Color.BLACK, 365, 495, "Autor", 200);
                                AusgabeLB(cos, fontBold, 12, Color.BLACK, 570, 495, "Anzahl");
                                AusgabeLB(cos, fontBold, 12, Color.BLACK, 620, 495, "- Autor");
                                AusgabeLB(cos, fontBold, 12, Color.BLACK, 670, 495, "- Werbung");
                                AusgabeLB(cos, fontBold, 12, Color.BLACK, 750, 495, "Umsatz");

                                Linie(cos,1,55, 490, 800, 490);
                                AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 475, "Übertrag");
                                AusgabeDB(cos, fontBold, 12, Color.BLACK, 790, 475, String.valueOf(ModulHelferlein.round2dec(Umsatz)));

                                Linie(cos,2,55, 470, 800, 470);
                                
                            } // if
                        } // while
                        break;
                }

                // Gesamtumsatz
                Linie(cos,2,55, basis - zeile * 15, 800, basis - zeile * 15);
                
                zeile = zeile + 1;
                AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, basis - zeile * 15, "Gesamtumsatz Miles-Verlag");
                AusgabeDB(cos, fontBold, 12, Color.BLACK, 790, basis - zeile * 15, String.valueOf(ModulHelferlein.round2dec(Umsatz)));

                // close the content stream for page 
                cos.close();

                // Save the results and ensure that the document is properly closed:
                document.save(outputFileName);
                document.close();

                ModulHelferlein.Infomeldung("Liste der BOD-Verkäufe ist als PDF gespeichert!");
                Runtime.getRuntime().exec("cmd.exe /c " + outputFileName);
                resultVerkauf.close();
                SQLVerkauf.close();
                resultVerkauf2.close();
                SQLVerkauf2.close();
                conn.close();
            } //if (conn != null)

        } catch (IOException e) {
            ModulHelferlein.Fehlermeldung("IO-Exception: " , e.getMessage());
        } catch (SQLException e) {
            ModulHelferlein.Fehlermeldung("SQL-Exception: " , e.getMessage());
        } catch (ClassNotFoundException ex) {
            ModulHelferlein.Fehlermeldung("ClassNotFound-Exception: ",ex.getMessage());
        } // try contentstream

    }

    private static void berichtXLS(String Sortierung, String strVon, String strBis) {
        try {
            String outputFileName = ModulHelferlein.pathBerichte + "/Verkäufe/"
                    + "Verkaufsstatistik-BOD"
                    + "-"
                    + Sortierung
                    + "-"
                    + strVon
                    + "-"
                    + strBis
                    + "-"
                    + ModulHelferlein.printSimpleDateFormat("yyyyMMdd")
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

            WritableFont arial10fontBold = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
            WritableCellFormat arial10formatBold = new WritableCellFormat(arial10fontBold);

            WritableFont arial10font = new WritableFont(WritableFont.ARIAL, 10);
            WritableCellFormat arial10formatR = new WritableCellFormat(arial10font);
            arial10formatR.setAlignment(RIGHT);
            WritableCellFormat arial10formatL = new WritableCellFormat(arial10font);
            arial10formatL.setAlignment(LEFT);

            Connection conn = null;

            Log("- Erstelle Verkaufsbericht BOD in Excel");
            try { // Datenbank-Treiber laden
                Log("- lade Datenbanktreiber");
                Class.forName(ModulHelferlein.dbDriver);
            } catch (ClassNotFoundException exept) {
                ModulHelferlein.Fehlermeldung("Bericht Umsatz", "ClassNotFound-Exception: Treiber nicht gefunden: ", exept.getMessage());
            } // try Datenbank-Treiber laden

            try { // Verbindung zur Datenbank über die JDBC-Brücke
                Log("- stelle Verbindung zur Datenbank her");
                conn = DriverManager.getConnection(ModulHelferlein.dbUrl, ModulHelferlein.dbUser, ModulHelferlein.dbPassword);
            } catch (SQLException exept) {
                ModulHelferlein.Fehlermeldung("Bericht Umsatz", "SQL-Exception: Verbindung nicht moeglich: ", exept.getMessage());
            } // try Verbindung zur Datenbank über die JDBC-Brücke

            final Connection conn2 = conn;

            if (conn2 != null) { // Datenbankverbindung steht
                Log("- Datenbankverbindung steht");
                Label label = new Label(0, 0, "Carola Hartmann Miles-Verlag", arial14formatBold);
                sheet_Verkauf.addCell(label);
                label = new Label(0, 1, "Verkaufsstatistik BOD-Verkäufe von " + strVon + " - " + strBis, arial14formatBold);
                sheet_Verkauf.addCell(label);

                label = new Label(0, 3, "BoD-Nr.", arial14formatBold);
                sheet_Verkauf.addCell(label);

                switch (Sortierung) {
                    case "Autor":
                        label = new Label(1, 3, "Autor", arial10formatBold);
                        sheet_Verkauf.addCell(label);
                        label = new Label(2, 3, "Titel", arial10formatBold);
                        sheet_Verkauf.addCell(label);
                        label = new Label(3, 3, "ISBN", arial10formatBold);
                        sheet_Verkauf.addCell(label);
                        break;
                    case "ISBN":
                        label = new Label(1, 3, "ISBN", arial10formatBold);
                        sheet_Verkauf.addCell(label);
                        label = new Label(2, 3, "Titel", arial10formatBold);
                        sheet_Verkauf.addCell(label);
                        label = new Label(3, 3, "Autor", arial10formatBold);
                        sheet_Verkauf.addCell(label);
                        break;
                    case "Titel":
                        label = new Label(1, 3, "Titel", arial10formatBold);
                        sheet_Verkauf.addCell(label);
                        label = new Label(2, 3, "ISBN", arial10formatBold);
                        sheet_Verkauf.addCell(label);
                        label = new Label(3, 3, "Autor", arial10formatBold);
                        sheet_Verkauf.addCell(label);
                        break;
                }
                label = new Label(4, 3, "Anzahl", arial10formatBold);
                sheet_Verkauf.addCell(label);
                label = new Label(5, 3, "Umsatz gesamt", arial10formatBold);
                sheet_Verkauf.addCell(label);
                label = new Label(6, 3, "Währung", arial10formatBold);
                sheet_Verkauf.addCell(label);
                label = new Label(7, 3, "> Autor", arial10formatBold);
                sheet_Verkauf.addCell(label);
                label = new Label(8, 3, "> Frei", arial10formatBold);
                sheet_Verkauf.addCell(label);
                label = new Label(9, 3, "Gesamt", arial10formatBold);
                sheet_Verkauf.addCell(label);

                Integer zeile = 5;

                switch (Sortierung) {
                    case "Autor":
                        resultVerkauf = SQLVerkauf.executeQuery("SELECT * FROM TBL_VERKAUF "
                                + " ORDER BY VERKAUF_AUTOR, VERKAUF_TITEL");
                        break;
                    case "ISBN":
                        resultVerkauf = SQLVerkauf.executeQuery("SELECT * FROM TBL_VERKAUF "
                                + " ORDER BY VERKAUF_ISBN");
                        break;
                    case "Titel":
                        resultVerkauf = SQLVerkauf.executeQuery("SELECT * FROM TBL_VERKAUF "
                                + " ORDER BY VERKAUF_TITEL");
                        break;
                }

                Log("- lese Tabelle VERKAUF und werte sie aus");
                while (resultVerkauf.next()) {
                    label = new Label(0, zeile, resultVerkauf.getString("VERKAUF_BOD"), arial10formatL);
                    sheet_Verkauf.addCell(label);

                    switch (Sortierung) {
                        case "Autor":
                            label = new Label(1, zeile, resultVerkauf.getString("VERKAUF_Autor"), arial10formatL);
                            sheet_Verkauf.addCell(label);
                            label = new Label(2, zeile, resultVerkauf.getString("VERKAUF_Titel"), arial10formatL);
                            sheet_Verkauf.addCell(label);
                            label = new Label(3, zeile, resultVerkauf.getString("VERKAUF_ISBN"), arial10formatL);
                            sheet_Verkauf.addCell(label);
                            break;
                        case "ISBN":
                            label = new Label(1, zeile, resultVerkauf.getString("VERKAUF_ISBN"), arial10formatL);
                            sheet_Verkauf.addCell(label);
                            label = new Label(2, zeile, resultVerkauf.getString("VERKAUF_Titel"), arial10formatL);
                            sheet_Verkauf.addCell(label);
                            label = new Label(3, zeile, resultVerkauf.getString("VERKAUF_Autor"), arial10formatL);
                            sheet_Verkauf.addCell(label);
                            break;
                        case "Titel":
                            label = new Label(1, zeile, resultVerkauf.getString("VERKAUF_Titel"), arial10formatL);
                            sheet_Verkauf.addCell(label);
                            label = new Label(2, zeile, resultVerkauf.getString("VERKAUF_ISBN"), arial10formatL);
                            sheet_Verkauf.addCell(label);
                            label = new Label(3, zeile, resultVerkauf.getString("VERKAUF_Autor"), arial10formatL);
                            sheet_Verkauf.addCell(label);
                            break;
                    }
                    jxl.write.Number number = new jxl.write.Number(4, zeile, resultVerkauf.getInt("VERKAUF_ANZAHL_GESAMT") - resultVerkauf.getInt("VERKAUF_ANZAHL_AUTOR") - resultVerkauf.getInt("VERKAUF_ANZAHL_Geschenk"), arial10formatR);
                    sheet_Verkauf.addCell(number);
                    
                    label = new Label(5, zeile, Float.toString(resultVerkauf.getFloat("VERKAUF_Umsatz")), arial10formatR);
                    sheet_Verkauf.addCell(label);
                    
                    label = new Label(6, zeile, "EUR", arial10formatR);
                    sheet_Verkauf.addCell(label);
                    
                    number = new jxl.write.Number(7, zeile, resultVerkauf.getInt("VERKAUF_ANZAHL_AUTOR"), arial10formatR);
                    sheet_Verkauf.addCell(number);
                    
                    number = new jxl.write.Number(8, zeile, resultVerkauf.getInt("VERKAUF_ANZAHL_Geschenk"), arial10formatR);
                    sheet_Verkauf.addCell(number);
                    
                    number = new jxl.write.Number(9, zeile, resultVerkauf.getInt("VERKAUF_ANZAHL_GESAMT"), arial10formatL);
                    sheet_Verkauf.addCell(number);

                    zeile = zeile + 1;
                }

                // Fertig - alles schließen
                try {// workbook write
                    workbook.write();
                } catch (IOException e) {
                    ModulHelferlein.Fehlermeldung("XLS-Bericht Verkaufsstatistik", "IO-Exception: ", e.getMessage());
                } // workbook write

                try { // try workbook close
                    workbook.close();
                } catch (IOException e) {
                    ModulHelferlein.Fehlermeldung("XLS-Bericht Verkaufsstatistik", "IO-Exception: ", e.getMessage());
                } // try workbook close

                try { // try XLS anzeigen
                    Runtime.getRuntime().exec("cmd.exe /c " + "\"" + outputFileName + "\"");
                } catch (IOException exept) {
                    ModulHelferlein.Fehlermeldung("Bericht Verkaufsstatistik", "Anzeige XLS-Export: Exception: ", exept.getMessage());
                } // try XLS anzeigen
            }
        } catch (IOException ex) {
            ModulHelferlein.Fehlermeldung("Verkaufsstatistik Excel", "IO-Exception", ex.getMessage());
        } catch (WriteException ex) {
            ModulHelferlein.Fehlermeldung("Verkaufsstatistik Excel", "Write-Exception", ex.getMessage());
        } catch (SQLException ex) {
            ModulHelferlein.Fehlermeldung("Verkaufsstatistik Excel", "SQL-Exception", ex.getMessage());
        }
    }

    public static void Log(String txt){
        System.out.println(txt);
    }
    
    private static void berichtDOC(String Sortierung, String strVon, String strBis) {
        ModulHelferlein.Infomeldung("DOC noch nicht implementiert");
    }

    /**
     * Erzeugt eine Übersicht der BoD-Verkäufe basieren auf der ausgewählten sales.csv
     *
     * @param CSVName
     * @param Sortierung
     * @param Format
     * @param strVon Beginn des Zeitraums
     * @param strBis Ende des Zeitraums
     */
    public static void bericht(String CSVName, String Sortierung, String Format, String strVon, String strBis) {
        Integer AnzahlGesamt = 0;

        try {                   // Datenbank-Treiber laden
            Class.forName(ModulHelferlein.dbDriver);
        } catch (ClassNotFoundException exept) {
            ModulHelferlein.Fehlermeldung("Bericht Verkaufsstatistik", "ClassNotFoundException: Treiber nicht gefunden: ", exept.getMessage());
        } // try Datenbank-Treiber laden

        try {                   // Verbindung zur Datenbank über die JDBC-Brücke
            conn = DriverManager.getConnection(ModulHelferlein.dbUrl, ModulHelferlein.dbUser, ModulHelferlein.dbPassword);
        } catch (SQLException exept) {
            ModulHelferlein.Fehlermeldung("Bericht Verkaufsstatistik", "SQL-Exception: Verbindung nicht moeglich: ", exept.getMessage());
        }                       // try Verbindung zur Datenbank über die JDBC-Brücke

        // Datenbankverbindung steht
        if (conn != null) {
            try {
                Log("Verkaufsbericht BoD erstellen");
// Hilfsdatenbank initialisieren
                Log("- Hilfsdatenbank initialisieren");
                SQLVerkauf = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                SQLVerkauf2 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                SQLVerkauf.executeUpdate("DELETE FROM TBL_VERKAUF");
                SQLVerkauf.executeUpdate("ALTER TABLE TBL_VERKAUF MODIFY VERKAUF_TITEL TEXT CHARACTER SET utf8");
                SQLVerkauf.executeUpdate("ALTER TABLE TBL_VERKAUF MODIFY VERKAUF_AUTOR TEXT CHARACTER SET utf8");

// Hilfsdatenbank aufbauen
                Log("- Hilfsdatenbank aufbauen");
                resultVerkauf = SQLVerkauf.executeQuery("SELECT * FROM TBL_VERKAUF");
                Integer ID = 1;
                BufferedReader in = new BufferedReader(new FileReader(CSVName));
                String CSVzeileIn = null;
                String Zeitraum = null;

// 1. Zeile lesen : Zeitraum: 01-01-2017 - 31-12-2017
                Log("- Werte BOD-Sales aus");
                CSVzeileIn = in.readLine();
                String[] CSVZeile = CSVzeileIn.split(";");
                Zeitraum = CSVZeile[0];
                Zeitraum = Zeitraum.substring(10, Zeitraum.length());
                Zeitraum = Zeitraum.replace("-", ".");
                AstrVon = Zeitraum.substring(0, 10);
                AstrBis = Zeitraum.substring(13, Zeitraum.length());

// 2. Zeile lesen : BoD-Nr.	ISBN	Autor	Titel	Anzahl	Umsatz gesamt	Währung
                CSVzeileIn = in.readLine();

// Rest lesen                
                while ((CSVzeileIn = in.readLine()) != null) {
                    if (CSVzeileIn.length() > 80)
                        Log("  - lese " + CSVzeileIn.substring(0, 80));
                    else
                        Log("  - lese " + CSVzeileIn);
                    // Zeile lesen
                    CSVZeile = CSVzeileIn.split(";");

                    String BOD = CSVZeile[0];
                    String ISBN = CSVZeile[1];
                    ISBN = ISBN.replace("-", "");
                    String Autor = CSVZeile[2];
                    String Titel = CSVZeile[3];
                    String sAnzahlGesamt = CSVZeile[4];
                    if (sAnzahlGesamt.contains(".")) {
                        sAnzahlGesamt = sAnzahlGesamt.replace(".", "");
                    }
                    if (sAnzahlGesamt.contains(",")) {
                        sAnzahlGesamt = sAnzahlGesamt.replace(",", ".");
                    }
                    AnzahlGesamt = Math.round(Float.parseFloat(sAnzahlGesamt));
                    String sUmsatz = CSVZeile[5];
                    if (sUmsatz.contains(".")) {
                        sUmsatz = sUmsatz.replace(".", "");
                    }
                    if (sUmsatz.contains(",")) {
                        sUmsatz = sUmsatz.replace(",", ".");
                    }
                    Float Umsatz = Float.parseFloat(sUmsatz);
                    switch (CSVZeile[6]) {
                        case "USD":
                            Umsatz = Umsatz * ModulHelferlein.USD;
                            break;
                        case "GBP":
                            Umsatz = Umsatz * ModulHelferlein.GBP;
                            break;
                        case "CHF":
                            Umsatz = Umsatz * ModulHelferlein.CHF;
                            break;
                        case "NOK":
                            Umsatz = Umsatz * ModulHelferlein.NOK;
                            break;
                        case "ILS":
                            Umsatz = Umsatz * ModulHelferlein.ILS;
                            break;
                        case "DKK":
                            Umsatz = Umsatz * ModulHelferlein.DKK;
                            break;
                        case "CAD":
                            Umsatz = Umsatz * ModulHelferlein.CAD;
                            break;
                        default:
                            break;
                    } // switch

                    // update Hilfsdatenbank mit ISBN, Titel, Autoren, AnzahlGesamt, AnzahlAutor, AnzahlGeschenk, Preis * (AnzahlGesamt-AnzahlAutor)
                    // prüfe, ob Buch bereits enthalten => aktualisieren
                    //                                  => hinzufügen 
                    Log("  - prüfe, ob ISBN " + ISBN + " bereits vorhanden ist");
                    resultVerkauf = SQLVerkauf2.executeQuery("SELECT * FROM TBL_VERKAUF "
                                    + " WHERE VERKAUF_ISBN = '" + ISBN + "'");
                    if (resultVerkauf.next()) { // Buch existiert => update
                        Log("    - ISBN existiert => update");
                        resultVerkauf.updateInt("VERKAUF_ANZAHL_BOD", AnzahlGesamt + resultVerkauf.getInt("VERKAUF_ANZAHL_BOD"));
                        resultVerkauf.updateInt("VERKAUF_ANZAHL_GESAMT", AnzahlGesamt + resultVerkauf.getInt("VERKAUF_ANZAHL_GESAMT"));
                        resultVerkauf.updateFloat("VERKAUF_UMSATZ_BOD", Umsatz + resultVerkauf.getFloat("VERKAUF_UMSATZ_BOD"));
                        resultVerkauf.updateFloat("VERKAUF_UMSATZ", Umsatz + resultVerkauf.getFloat("VERKAUF_UMSATZ"));
                        resultVerkauf.updateRow();
                    } else { // Buch exisitiert nicht => hinzufügen
                        Log("    - ISBN existiert nicht => aufnehmen");
                        resultVerkauf.moveToInsertRow();
                        resultVerkauf.updateInt("VERKAUF_ID", ID);
                        resultVerkauf.updateString("VERKAUF_ISBN", ISBN);
                        resultVerkauf.updateString("VERKAUF_AUTOR", Autor);
                        resultVerkauf.updateString("VERKAUF_BOD", BOD);
                        resultVerkauf.updateString("VERKAUF_TITEL", Titel);
                        resultVerkauf.updateInt("VERKAUF_ANZAHL_BOD", AnzahlGesamt);
                        resultVerkauf.updateInt("VERKAUF_ANZAHL_GESAMT", AnzahlGesamt);
                        resultVerkauf.updateInt("VERKAUF_ANZAHL_AUTOR", 0);
                        resultVerkauf.updateInt("VERKAUF_ANZAHL_GESCHENK", 0);
                        resultVerkauf.updateFloat("VERKAUF_UMSATZ", Umsatz);
                        resultVerkauf.updateFloat("VERKAUF_UMSATZ_BOD", Umsatz);
                        ID = ID + 1;
                        resultVerkauf.insertRow();
                    }
                    
                } // while

// Hilfsdatenbank ausgeben
                Log("- Hilfsdatenbank ausgeben");
                switch (Format) {
                    case "PDF":
                        berichtPDF(Sortierung, AstrVon, AstrBis);
                        break;
                    case "XLS":
                        berichtXLS(Sortierung, AstrVon, AstrBis);
                        break;
                    default:
                        berichtDOC(Sortierung, AstrVon, AstrBis);
                        break;
                }
            } catch (SQLException ex) {
                ModulHelferlein.Fehlermeldung("Verkaufsbericht", "SQL-Exception", ex.getMessage());
            } catch (FileNotFoundException ex) {
                ModulHelferlein.Fehlermeldung("Verkaufsbericht", "FileNotFound-Exception", ex.getMessage());
            } catch (IOException ex) {
                ModulHelferlein.Fehlermeldung("Verkaufsbericht", "IO-Exception", ex.getMessage());
            }
        }
    } // void
} // class

