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
import java.io.FileNotFoundException;
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
 * Klasse zur Erzeugung einer Übersicht der Lieferanten
 *
 * @author Thomas Zimmermann
 *
 */
public class berLieferanten {

    /**
     * Erzeugt die Übersicht der Lieferanten
     */
    public static void bericht() {
        PDDocument document = new PDDocument();
        PDPage page1 = new PDPage(A4);
        document.addPage(page1);

        // Start a new content stream which will "hold" the to be created content
        PDPageContentStream cos;

        try {
            cos = new PDPageContentStream(document, page1);

            String outputFileName;
            outputFileName = Modulhelferlein.pathBerichte + "\\Adressen\\"
                    + "Adressen-Lieferanten-"
                    + Modulhelferlein.printSimpleDateFormat("yyyyMMdd") + ".pdf";

            PDDocumentInformation docInfo = document.getDocumentInformation();

            docInfo.setSubject("Konfiguration");
            docInfo.setTitle("miles-Verlag Stammdaten");
            docInfo.setAuthor("miles-Verlag");
            docInfo.setCreationDate(Calendar.getInstance());
            docInfo.setCreator("miles-Verlag");
            docInfo.setProducer("miles-Verlag");

            // Einfügen der Texte und Bilder
            Connection conn = null;

            // Datenbank-Treiber laden
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException exept) {
                Modulhelferlein.Fehlermeldung(
                        "ClassNotFoundException: Treiber nicht gefunden: "
                        + exept.getMessage());
            }

            // Verbindung zur Datenbank über die JDBC-Brücke
            try {
                conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost/miles-verlag", "root", "clausewitz");
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
                            "SELECT * FROM TBL_LIEFERANT"); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 

                    PDFont fontPlain = PDType1Font.HELVETICA;
                    PDFont fontBold = PDType1Font.HELVETICA_BOLD;

                    Integer zeile = 1;
                    Integer seite = 1;

                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Lieferanten");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 755, "Übersicht der Lieferanten, Stand: "
                            + Modulhelferlein.printSimpleDateFormat(
                                    "dd.MM.yyyy"));
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 455, 770, "Seite: " + Integer.toString(seite));
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 720, "ID");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 80, 720, "Firma");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 235, 720, "Kundennummer");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 390, 720, "Ansprechpartner");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 80, 705, "Straße und Hausnummer");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 390, 705, "PLZ");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 420, 705, "Ort");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 80, 690, "Telefon");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 390, 690, "Telefax");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 80, 675, "e-Mail");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 390, 675, "Internet");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 80, 660, "Benutzerkennung");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 390, 660, "Kennwort");
                    Linie(cos, 3, 56, 655, 539, 655);

                    while (result.next()) { // geht durch alle zeilen
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 56, 715 - zeile * 75, result.getString("LIEFERANT_ID"));
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 80, 715 - zeile * 75, result.getString("LIEFERANT_NAME"));
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 235, 715 - zeile * 75, result.getString("LIEFERANT_Kundennummer"));
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 390, 715 - zeile * 75, result.getString("LIEFERANT_POC"));
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 80, 700 - zeile * 75, result.getString("LIEFERANT_STRASSE"));
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 390, 700 - zeile * 75, result.getString("LIEFERANT_PLZ"));
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 420, 700 - zeile * 75, result.getString("LIEFERANT_ORT"));
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 80, 685 - zeile * 75, result.getString("LIEFERANT_TELEFON"));
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 390, 685 - zeile * 75, result.getString("LIEFERANT_TELEFAX"));
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 80, 670 - zeile * 75, result.getString("LIEFERANT_EMAIL"));
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 390, 670 - zeile * 75, result.getString("LIEFERANT_WEB"));
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 80, 655 - zeile * 75, result.getString("LIEFERANT_ANMELDUNG"));
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 390, 655 - zeile * 75, result.getString("LIEFERANT_KENNWORT"));
                        Linie(cos, 1, 56, 652 - zeile * 75, 539, 652 - zeile * 75);

                        zeile = zeile + 1;

                        if (zeile == 8) {
                            zeile = 1;
                            seite = seite + 1;
                            cos.close();
                            PDPage page = new PDPage(A4);
                            document.addPage(page);
                            cos = new PDPageContentStream(document, page);

                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Lieferanten");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 755, "Übersicht der Lieferanten, Stand: "
                                    + Modulhelferlein.printSimpleDateFormat(
                                            "dd.MM.yyyy"));
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 455, 770, "Seite: " + Integer.toString(seite));
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 720, "ID");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 80, 720, "Firma");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 235, 720, "Kundennummer");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 390, 720, "Ansprechpartner");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 80, 705, "Straße und Hausnummer");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 390, 705, "PLZ");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 420, 705, "Ort");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 80, 690, "Telefon");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 390, 690, "Telefax");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 80, 675, "e-Mail");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 390, 675, "Internet");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 80, 660, "Benutzerkennung");
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 390, 660, "Kennwort");
                            Linie(cos, 3, 56, 655, 539, 655);
                        }
                    }

                    // close the content stream for page 1
                    cos.close();

                    // Save the results and ensure that the document is properly closed:
                    document.save(outputFileName);
                    document.close();

                    Modulhelferlein.Infomeldung(
                            "Liste der Lieferanten ist als PDF gespeichert!");
                    try {
                        Runtime.getRuntime().exec("cmd.exe /c " + "\"" + outputFileName + "\"");
                    } catch (IOException exept) {
                        Modulhelferlein.Fehlermeldung(
                                "Exception: " + exept.getMessage());
                    }
                } catch (SQLException exept) {
                    Modulhelferlein.Fehlermeldung(
                            "SQLException: SQL-Anfrage nicht moeglich: ",
                             exept.getMessage());
                } catch (IOException e) {
                    Modulhelferlein.Fehlermeldung("IO-Exception: ", e.getMessage());
                }
            }

        } catch (FileNotFoundException e) {
            Modulhelferlein.Fehlermeldung("FileNotFoundException: ", e.getMessage());
        } catch (IOException e1) {
            // TODO Auto-generated catch block

        }

    }

}
