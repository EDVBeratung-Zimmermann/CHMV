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
import static milesVerlagMain.Modulhelferlein.Ausgabe;
import static milesVerlagMain.Modulhelferlein.Linie;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import static org.apache.pdfbox.pdmodel.common.PDRectangle.A4;

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

/**
 * Klasse zur Erzeugung einer Übersicht der Mail-Verteiler
 *
 * @author Thomas Zimmermann
 *
 */
public class berMailVerteiler {

    /**
     * Erzeugt die Übersicht der Kundentypen
     */
    public static void bericht() {
        try {
            // Create a document and add a page to it
            PDDocument document = new PDDocument();
            PDPage page1 = new PDPage(A4);
            document.addPage(page1);

            // Start a new content stream which will "hold" the to be created content
            PDPageContentStream cos;
            try {
                cos = new PDPageContentStream(document, page1);

                String outputFileName;
                outputFileName = Modulhelferlein.pathBerichte + "\\Mailverteiler\\"
                        + "Liste-Mailverteiler-"
                        + Modulhelferlein.printSimpleDateFormat("yyyyMMdd")
                        + ".pdf";

                PDDocumentInformation docInfo = document.getDocumentInformation();

                docInfo.setSubject("Mail-Verteiler");
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
                    Modulhelferlein.Fehlermeldung("ClassNotFoundException: Treiber nicht gefunden: " + exept.getMessage());
                }

                // Verbindung zur Datenbank über die JDBC-Brücke
                try {
                    conn = DriverManager.getConnection(Modulhelferlein.dbUrl, Modulhelferlein.dbUser, Modulhelferlein.dbPassword);
                } catch (SQLException exept) {
                    Modulhelferlein.Fehlermeldung("SQL-Exception: Verbindung nicht moeglich: " + exept.getMessage());
                }

                final Connection conn2 = conn;

                if (conn2 != null) {
                    Statement SQLAnfrageVerteiler = null; // Anfrage erzeugen
                    Statement SQLAnfrageAdresse = null; // Anfrage erzeugen

                    try {
                        SQLAnfrageVerteiler = conn2.createStatement(); // Anfrage der DB conn2 zuordnen
                        SQLAnfrageAdresse = conn2.createStatement(); // Anfrage der DB conn2 zuordnen

                        ResultSet resultVerteiler = SQLAnfrageVerteiler.executeQuery("SELECT * FROM TBL_MAILVERTEILER");

                        PDFont fontPlain = PDType1Font.HELVETICA;
                        PDFont fontBold = PDType1Font.HELVETICA_BOLD;

                        Integer zeile = 1;
                        Integer seite = 1;

                        Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - e-Mail-Verteiler");
                        Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 755, "Übersicht der e-Mail-Verteiler, Stand: "
                                + Modulhelferlein.printSimpleDateFormat(
                                        "dd.MM.yyyy"));
                        Ausgabe(cos, fontBold, 12, Color.BLACK, 455, 770, "Seite: " + Integer.toString(seite));
                        Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 720, "ID");
                        Ausgabe(cos, fontBold, 12, Color.BLACK, 100, 720, "Beschreibung");

                        Linie(cos, 1, 56, 715, 539, 715);

                        while (resultVerteiler.next()) { // geht durch alle zeilen
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 715 - zeile * 15, resultVerteiler.getString("MAILVERTEILER_ID"));
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 100, 715 - zeile * 15, resultVerteiler.getString("MAILVERTEILER_NAME"));

                            zeile = zeile + 1;

                            ResultSet resultAdresse = SQLAnfrageAdresse.executeQuery("SELECT * FROM TBL_MAILADRESSE WHERE MAILADRESSE_VERTEILER = '" + Integer.toString(resultVerteiler.getInt("MAILVERTEILER_ID") - 1) + "'");
                            while (resultAdresse.next()) { // geht durch alle zeilen
                                Ausgabe(cos, fontPlain, 12, Color.BLACK, 100, 715 - zeile * 15, resultAdresse.getString("MAILADRESSE_ID"));
                                Ausgabe(cos, fontPlain, 12, Color.BLACK, 150, 715 - zeile * 15, resultAdresse.getString("MAILADRESSE_ADRESSE"));

                                zeile = zeile + 1;

                                if (zeile == 47) {
                                    zeile = 1;
                                    seite = seite + 1;
                                    cos.close();
                                    PDPage page = new PDPage(A4);
                                    document.addPage(page);
                                    cos = new PDPageContentStream(document, page);
                                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - e-Mail-Verteiler");
                                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 755, "Übersicht der e-Mail-Verteiler, Stand: "
                                            + Modulhelferlein.printSimpleDateFormat(
                                                    "dd.MM.yyyy"));
                                    Ausgabe(cos, fontBold, 12, Color.BLACK, 455, 770, "Seite: " + Integer.toString(seite));
                                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 720, "ID");
                                    Ausgabe(cos, fontBold, 12, Color.BLACK, 100, 720, "Beschreibung");

                                    Linie(cos, 1, 56, 715, 539, 715);
                                } else {

                                } // if
                            } // while
                        } // while

                        // close the content stream for page 1
                        cos.close();

                        // Save the results and ensure that the document is properly closed:
                        document.save(outputFileName);
                        document.close();

                        Modulhelferlein.Infomeldung(
                                "Liste der e-Mail-Verteiler ist als PDF gespeichert!");
                        try {
                            Runtime.getRuntime().exec("cmd.exe /c " + "\"" + outputFileName + "\"");
                        } catch (IOException exept) {
                            Modulhelferlein.Fehlermeldung(
                                    "IO-Exception: ", exept.getMessage());
                        }
                    } catch (SQLException exept) {
                        Modulhelferlein.Fehlermeldung(
                                "SQL-Exception: SQL-Anfrage nicht moeglich: ",
                                 exept.getMessage());
                        System.exit(1);
                    } catch (IOException e) {
                        Modulhelferlein.Fehlermeldung("IO-Exception: ", e.getMessage());
                    }
                }

            } catch (IOException e1) {
                Modulhelferlein.Fehlermeldung("IO-Exception: ", e1.getMessage());
            }
        } catch (Exception e) {
            Modulhelferlein.Fehlermeldung("Exception: ", e.getMessage());
        }
    } //void
} //class
