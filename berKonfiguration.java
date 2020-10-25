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
import static milesVerlagMain.ModulHelferlein.Linie;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import static org.apache.pdfbox.pdmodel.common.PDRectangle.A4;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import static milesVerlagMain.ModulHelferlein.AusgabeLB;
import static milesVerlagMain.ModulHelferlein.Ausgabe;

/**
 * Klasse zur Erzeugung einer Konfigurationsübersicht
 *
 * @author Thomas Zimmermann
 *
 */
public class berKonfiguration {

    /**
     * Erzeugt die Konfigurationsübersicht
     *
     * @throws IOException
     */
    public static void bericht() throws IOException {
        // Create a document and add a page to it
        PDDocument document = new PDDocument();
        PDPage page1 = new PDPage(A4);
        document.addPage(page1);

        // Start a new content stream which will "hold" the to be created content
        PDPageContentStream cos = new PDPageContentStream(document, page1);

        String outputFileName = ModulHelferlein.pathKonfiguration
                + "\\Konfiguration-"
                + ModulHelferlein.printSimpleDateFormat("yyyyMMdd")
                + ".pdf";

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
            Class.forName(ModulHelferlein.dbDriver);
        } catch (ClassNotFoundException exept) {
            ModulHelferlein.Fehlermeldung("ClassNotFoundException: Treiber nicht gefunden: " + exept.getMessage());
        }
        // Verbindung zur Datenbank über die JDBC-Brücke
        try {
            conn = DriverManager.getConnection(ModulHelferlein.dbUrl, ModulHelferlein.dbUser, ModulHelferlein.dbPassword);
        } catch (SQLException exept) {
            ModulHelferlein.Fehlermeldung("SQL-Exception: Verbindung zur Datenbank nicht moeglich: " + exept.getMessage());
        }

        if (conn != null) {
            Statement SQLAnfrage = null; // Anfrage erzeugen

            try {
                SQLAnfrage = conn.createStatement(); // Anfrage der DB conn2 zuordnen
                ResultSet result = SQLAnfrage.executeQuery("SELECT * FROM tbl_konfiguration");

                PDFont fontPlain = PDType1Font.HELVETICA;
                PDFont fontBold = PDType1Font.HELVETICA_BOLD;

                AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Konfiguration");
                AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 755, "Verzeichnis der Ablagepfade, Stand: "
                        + ModulHelferlein.printSimpleDateFormat(
                                "dd.MM.yyyy"));

                Linie(cos,1,56, 725, 539, 725);
                
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 710, "Verzeichnis der ...");

                Linie(cos,1,56, 700, 539, 700);
                
                result.next();

                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 685, "... Stammdaten :");
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 670, "... ");
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 655, "... ");
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 640, "... ");
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 625, "... Rechnungen :");
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 610, "... Sicherung :");
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 595, "... ");
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 580, "... Konfiguration :");
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 565, "... Buchprojekte :");
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 550, "... Steuer :");

                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 685, result.getString("KONFIGURATION_STAMMDATEN"));
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 670, result.getString("KONFIGURATION_EINNAHMEN"));
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 655, result.getString("KONFIGURATION_AUSGABEN"));
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 640, result.getString("KONFIGURATION_UMSAETZE"));
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 625, result.getString("KONFIGURATION_RECHNUNGEN"));
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 610, result.getString("KONFIGURATION_SICHERUNG"));
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 595, result.getString("KONFIGURATION_MAHNUNGEN"));
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 580, result.getString("KONFIGURATION_TERMINE"));
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 565, result.getString("KONFIGURATION_SCHRIFTVERKEHR"));
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 550, result.getString("KONFIGURATION_STEUER"));

                result.next();
                // Steuer 1    
                // Neue Seite beginnen
                cos.close();
                PDPage page = new PDPage(A4);
                document.addPage(page);
                cos = new PDPageContentStream(document, page);
                AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Konfiguration");
                AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 755, "Verzeichnis der Steuerdaten, Stand: "
                        + ModulHelferlein.printSimpleDateFormat(
                                "dd.MM.yyyy"));

                Linie(cos,1,56, 725, 539, 725);
                
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 710, "Verzeichnis der ...");

                Linie(cos,1,56, 700, 539, 700);
                
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 685, "... Steuernummer :");
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 670, "... Finanzamtbez. :");
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 655, "... Person :");
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 640, "... Straße :");
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 625, "... Postleitzahl :");
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 610, "... Ortsangabe :");
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 595, "... Telefonnummer :");
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 580, "... e-Mail-Adresse :");
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 565, "... Zertifikat-PIN :");
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 550, "... Zertifikat :");

                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 685, result.getString("KONFIGURATION_STAMMDATEN"));
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 670, result.getString("KONFIGURATION_EINNAHMEN"));
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 655, result.getString("KONFIGURATION_AUSGABEN"));
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 640, result.getString("KONFIGURATION_UMSAETZE"));
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 625, result.getString("KONFIGURATION_SICHERUNG"));
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 610, result.getString("KONFIGURATION_MAHNUNGEN"));
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 595, result.getString("KONFIGURATION_RECHNUNGEN"));
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 580, result.getString("KONFIGURATION_TERMINE"));
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 565, result.getString("KONFIGURATION_SCHRIFTVERKEHR"));
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 550, result.getString("KONFIGURATION_STEUER"));

                result.next();
                // Steuer 2    
                // Neue Seite beginnen
                cos.close();
                PDPage page3 = new PDPage(A4);
                document.addPage(page3);
                cos = new PDPageContentStream(document, page3);
                AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Konfiguration");
                AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 755, "Verzeichnis der Steuerdaten, Stand: "
                        + ModulHelferlein.printSimpleDateFormat(
                                "dd.MM.yyyy"));

                Linie(cos,1,56, 725, 539, 725);
                
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 710, "Verzeichnis der ...");

                Linie(cos,1,56, 700, 539, 700);
                
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 685, "... FA-Nummer :");
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 670, "... Finanzamtbez. :");
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 655, "... Ergänzung :");
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 640, "... Straße :");
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 625, "... PLZ Ort :");
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 610, "... Telefonnummer :");
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 595, "... e-Mail-Adresse :");
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 580, "... Bank :");
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 565, "... IBAN :");
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 550, "... BIC :");

                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 685, result.getString("KONFIGURATION_STAMMDATEN"));
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 670, result.getString("KONFIGURATION_EINNAHMEN"));
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 655, result.getString("KONFIGURATION_AUSGABEN"));
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 640, result.getString("KONFIGURATION_UMSAETZE"));
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 625, result.getString("KONFIGURATION_SICHERUNG"));
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 610, result.getString("KONFIGURATION_MAHNUNGEN"));
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 595, result.getString("KONFIGURATION_RECHNUNGEN"));
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 580, result.getString("KONFIGURATION_TERMINE"));
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 565, result.getString("KONFIGURATION_SCHRIFTVERKEHR"));
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 550, result.getString("KONFIGURATION_STEUER"));

                result.next();
                // Währungen    
                // Neue Seite beginnen
                cos.close();
                PDPage page4 = new PDPage(A4);
                document.addPage(page4);
                cos = new PDPageContentStream(document, page4);
                AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Konfiguration");
                AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 755, "Verzeichnis der Währungsumrechnungen, Stand: "
                        + ModulHelferlein.printSimpleDateFormat(
                                "dd.MM.yyyy"));

                Linie(cos,1,56, 725, 539, 725);
                
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 710, "Verzeichnis der ...");

                Linie(cos,1,56, 700, 539, 700);
                
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 685, "... USD :");
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 670, "... GBP :");
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 655, "... CHF :");
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 640, "... NOK :");
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 625, "... ILS :");
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 610, "... DKK :");
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 595, "... CAD :");
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 580, "... --- :");
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 565, "... --- :");
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 550, "... --- :");

                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 685, result.getString("KONFIGURATION_STAMMDATEN"));
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 670, result.getString("KONFIGURATION_EINNAHMEN"));
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 655, result.getString("KONFIGURATION_AUSGABEN"));
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 640, result.getString("KONFIGURATION_UMSAETZE"));
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 625, result.getString("KONFIGURATION_SICHERUNG"));
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 610, result.getString("KONFIGURATION_MAHNUNGEN"));
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 595, result.getString("KONFIGURATION_RECHNUNGEN"));
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 580, result.getString("KONFIGURATION_TERMINE"));
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 565, result.getString("KONFIGURATION_SCHRIFTVERKEHR"));
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 550, result.getString("KONFIGURATION_STEUER"));

                result.next();
                // Sicherung-Konfiguration
                // Neue Seite beginnen
                cos.close();
                PDPage page5 = new PDPage(A4);
                document.addPage(page5);
                cos = new PDPageContentStream(document, page5);
                AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Konfiguration");
                AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 755, "Verzeichnis der Sicherungspfade, Stand: "
                        + ModulHelferlein.printSimpleDateFormat(
                                "dd.MM.yyyy"));

                Linie(cos,1,56, 725, 539, 725);
                
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 710, "Verzeichnis der ...");

                Linie(cos,1,56, 700, 539, 700);
                
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 685, "... Quelle :");
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 670, "... Ziel :");
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 655, "... --- :");
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 640, "... --- :");
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 625, "... --- :");
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 610, "... --- :");
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 595, "... --- :");
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 580, "... --- :");
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 565, "... --- :");
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 550, "... --- :");

                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 685, result.getString("KONFIGURATION_STAMMDATEN"));
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 670, result.getString("KONFIGURATION_EINNAHMEN"));
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 655, result.getString("KONFIGURATION_AUSGABEN"));
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 640, result.getString("KONFIGURATION_UMSAETZE"));
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 625, result.getString("KONFIGURATION_SICHERUNG"));
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 610, result.getString("KONFIGURATION_MAHNUNGEN"));
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 595, result.getString("KONFIGURATION_RECHNUNGEN"));
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 580, result.getString("KONFIGURATION_TERMINE"));
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 565, result.getString("KONFIGURATION_SCHRIFTVERKEHR"));
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 550, result.getString("KONFIGURATION_STEUER"));

                result.next();
                // Mail-Konfiguration
                // Neue Seite beginnen
                cos.close();
                PDPage page6 = new PDPage(A4);
                document.addPage(page6);
                cos = new PDPageContentStream(document, page6);
                AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Konfiguration");
                AusgabeLB(cos, fontBold, 12, Color.BLACK, 56, 755, "Verzeichnis der Mailkonfiguration, Stand: "
                        + ModulHelferlein.printSimpleDateFormat(
                                "dd.MM.yyyy"));

                Linie(cos,1,56, 725, 539, 725);
                
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 710, "Verzeichnis der ...");

                Linie(cos,1,56, 700, 539, 700);
                
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 685, "... Host :");
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 670, "... Port :");
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 655, "... User :");
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 640, "... Pass :");
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 625, "... --- :");
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 610, "... --- :");
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 595, "... --- :");
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 580, "... --- :");
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 565, "... --- :");
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 56, 550, "... --- :");

                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 685, result.getString("KONFIGURATION_STAMMDATEN"));
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 670, result.getString("KONFIGURATION_EINNAHMEN"));
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 655, result.getString("KONFIGURATION_AUSGABEN"));
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 640, result.getString("KONFIGURATION_UMSAETZE"));
                /*
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 625, result.getString("KONFIGURATION_SICHERUNG"));
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 610, result.getString("KONFIGURATION_MAHNUNGEN"));
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 595, result.getString("KONFIGURATION_RECHNUNGEN"));
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 580, result.getString("KONFIGURATION_TERMINE"));
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 565, result.getString("KONFIGURATION_SCHRIFTVERKEHR"));
                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 170, 550, result.getString("KONFIGURATION_STEUER"));
                 */
                // close the content stream for page 1
                cos.close();

                // Save the results and ensure that the document is properly closed:
                document.save(outputFileName);
                document.close();

                ModulHelferlein.Infomeldung("Konfiguration als PDF gespeichert!");
                try {
                    Runtime.getRuntime().exec("cmd.exe /c " + "\"" + outputFileName + "\"");
                } catch (IOException exept) {
                    ModulHelferlein.Fehlermeldung(
                            "Exception: " + exept.getMessage());
                }
            } catch (SQLException exept) {
                ModulHelferlein.Fehlermeldung(
                        "SQL-Exception: SQL-Anfrage nicht moeglich: "
                        + exept.getMessage());
                System.exit(1);
            } catch (IOException  e) {
                ModulHelferlein.Fehlermeldung("IO-Exception: " + e.getMessage());
            }
        }

    } //void

} //class
