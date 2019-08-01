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

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.awt.Color;
import static milesVerlagMain.Modulhelferlein.Ausgabe;
import static milesVerlagMain.Modulhelferlein.Linie;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

/**
 * Klasse zur Erzeugung einer Übersicht der Kundentypen
 *
 * @author Thomas Zimmermann
 *
 */
public class berAufgaben {

    /**
     * Erzeugt die Übersicht der Kundentypen
     */
    public static void bericht() {
        try {
            // Create a document and add a page to it
            PDDocument document = new PDDocument();
            PDPage page1 = new PDPage(PDRectangle.A4);
            document.addPage(page1);

            // Start a new content stream which will "hold" the to be created content
            PDPageContentStream cos;
            try {
                cos = new PDPageContentStream(document, page1);

                String outputFileName;
                outputFileName = Modulhelferlein.pathBerichte + "\\Aufgaben\\"
                        + "Liste-Aufgaben-"
                        + Modulhelferlein.printSimpleDateFormat("yyyyMMdd") 
                        + ".pdf";

                PDDocumentInformation docInfo = document.getDocumentInformation();

                docInfo.setSubject("Konfiguration");
                docInfo.setTitle("miles-Verlag Stammdaten");
                docInfo.setAuthor("miles-Verlag");
                docInfo.setCreationDate(Calendar.getInstance());
                docInfo.setCreator("miles-Verlag");
                docInfo.setProducer("miles-Verlag");

                Connection conn = null;

                // Datenbank-Treiber laden
                try {
                    Class.forName(Modulhelferlein.dbDriver);
                } catch (ClassNotFoundException exept) {
                    Modulhelferlein.Fehlermeldung(
                            "ClassNotFoundException: Treiber nicht gefunden: "
                            + exept.getMessage());
                }

                // Verbindung zur Datenbank über die JDBC-Brücke
                try {
                    conn = DriverManager.getConnection(Modulhelferlein.dbUrl, Modulhelferlein.dbUser, Modulhelferlein.dbPassword);
                } catch (SQLException exept) {
                    Modulhelferlein.Fehlermeldung(
                            "SQL-Exception: Verbindung nicht moeglich: "
                            + exept.getMessage());
                }

                final Connection conn2 = conn;

                if (conn2 != null) {
                    Statement SQLAnfrage = null; // Anfrage erzeugen

                    try {
                        SQLAnfrage = conn2.createStatement(); // Anfrage der DB conn2 zuordnen
                        ResultSet result = SQLAnfrage.executeQuery(
                                "SELECT * FROM tbl_aufgaben ORDER BY AUFGABE_Datum"); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 

                        PDFont fontPlain = PDType1Font.HELVETICA;
                        PDFont fontBold = PDType1Font.HELVETICA_BOLD;

                        Integer zeile = 1;
                        Integer seite = 1;

                        Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Aufgaben");
                        Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 755, "Übersicht der Aufgaben, Stand: "
                                + Modulhelferlein.printSimpleDateFormat(
                                        "dd.MM.yyyy"));
                        Ausgabe(cos, fontBold, 12, Color.BLACK, 455, 770, "Seite: " + Integer.toString(seite));

                        Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 720, "ID");
                        Ausgabe(cos, fontBold, 12, Color.BLACK, 80, 720, "Datum");
                        Ausgabe(cos, fontBold, 12, Color.BLACK, 150, 720, "Beschreibung");
                        Ausgabe(cos, fontBold, 12, Color.BLACK, 455, 720, "Erledigt");

                        Linie(cos, 1, 56, 715, 539, 715);
                        Linie(cos, 1, 75, 730, 75, 0);
                        Linie(cos, 1, 146, 730, 146, 0);
                        Linie(cos, 1, 450, 730, 450, 0);

                        while (result.next()) { // geht durch alle zeilen
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 715 - zeile * 15, result.getString("AUFGABE_ID"));
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 80, 715 - zeile * 15, result.getString("AUFGABE_DATUM"));
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 150, 715 - zeile * 15, result.getString("AUFGABE_Beschreibung"));
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 455, 715 - zeile * 15, result.getString("AUFGABE_ERLEDIGT"));

                            zeile = zeile + 1;

                            if (zeile == 47) {
                                zeile = 1;
                                seite = seite + 1;
                                cos.close();
                                PDPage page = new PDPage(PDRectangle.A4);
                                document.addPage(page);
                                cos = new PDPageContentStream(document, page);
                                Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Termine");
                                Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 755, "Übersicht der Termine, Stand: "
                                        + Modulhelferlein.printSimpleDateFormat(
                                                "dd.MM.yyyy"));
                                Ausgabe(cos, fontBold, 12, Color.BLACK, 455, 770, "Seite: " + Integer.toString(seite));

                                Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 720, "ID");
                                Ausgabe(cos, fontBold, 12, Color.BLACK, 80, 720, "Datum");
                                Ausgabe(cos, fontBold, 12, Color.BLACK, 150, 720, "Beschreibung");
                                Ausgabe(cos, fontBold, 12, Color.BLACK, 455, 720, "Erledigt");

                                Linie(cos, 1, 56, 715, 539, 715);
                                Linie(cos, 1, 75, 730, 75, 0);
                                Linie(cos, 1, 146, 730, 146, 0);
                                Linie(cos, 1, 450, 730, 450, 0);
                            } else {
                            }
                        }

                        // close the content stream for page 1
                        cos.close();

                        // Save the results and ensure that the document is properly closed:
                        document.save(outputFileName);
                        document.close();

                        Modulhelferlein.Infomeldung(
                                "Liste der Aufgaben ist als PDF gespeichert!");
                        try {
                            Runtime.getRuntime().exec("cmd.exe /c " + "\"" + outputFileName + "\"");
                        } catch (IOException exept) {
                            Modulhelferlein.Fehlermeldung("Exception: " + exept.getMessage());
                        }
                    } catch (SQLException exept) {
                        Modulhelferlein.Fehlermeldung(
                                "SQL-Exception: SQL-Anfrage nicht moeglich: "
                                + exept.getMessage());
                        System.exit(1);
                    } catch (IOException e) {
                        Modulhelferlein.Fehlermeldung("Exception: " + e.getMessage());
                    }
                }

            } catch (IOException e1) {
                Modulhelferlein.Fehlermeldung("IO-Exception: " + e1.getMessage());
            }
        } catch (Exception e) {
            Modulhelferlein.Fehlermeldung("Exception: " + e.getMessage());
        }
    } //void
} //class
