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

//~--- non-JDK imports --------------------------------------------------------

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;


//~--- JDK imports ------------------------------------------------------------
import java.awt.Color;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Calendar;

import javax.imageio.ImageIO;
import static milesVerlagMain.ModulHelferlein.Linie;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import static org.apache.pdfbox.pdmodel.common.PDRectangle.A4;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import static milesVerlagMain.ModulHelferlein.AusgabeLB;
import static milesVerlagMain.ModulHelferlein.Ausgabe;

/**
 * Klasse zur Erzeugung einer ?bersicht der B?cher des miles Verlages
 *
 * @author Thomas Zimmermann
 *
 */
public class berVerlagsprogramm {

    /**
     *
     * @param Sortierung
     * @param Aktiv
     */
    public static void PDF(Integer Sortierung, boolean Aktiv) {
        PDDocument document = new PDDocument();
        PDPage page1 = new PDPage(A4);

        document.addPage(page1);

        Integer Prozent = 0;
        Integer Anzahl = 0;

        Integer ID = 0;

        // Start a new content stream which will "hold" the to be created content
        try {    // Dokument erstellen
            PDPageContentStream cos = new PDPageContentStream(document, page1);
            String outputFileName = ModulHelferlein.pathBerichte
                    + "\\Buchprojekte"
                    + "\\Verlagsprogramm";
            switch (Sortierung) {
                case 0:  // Sortierung nach ISBN
                    outputFileName = outputFileName + "-ISBN";
                    break;
                case 1:  // Sortierung nach Autor
                    outputFileName = outputFileName + "-Autor";
                    break;
                case 2:  // Sortierung nach Titel
                    outputFileName = outputFileName + "-Titel";
                    break;
            }
            if (Aktiv) {
                outputFileName = outputFileName + "-Aktiv-";
            } else {
                outputFileName = outputFileName + "-Alles-";
            }
            outputFileName = outputFileName + ModulHelferlein.printSimpleDateFormat("yyyyMMdd");
            outputFileName = outputFileName + ".pdf";
            PDDocumentInformation docInfo = document.getDocumentInformation();

            docInfo.setSubject("Buchprojekte");
            docInfo.setTitle("miles-Verlag Stammdaten Verlagsprogramm");
            docInfo.setAuthor("miles-Verlag");
            docInfo.setCreationDate(Calendar.getInstance());
            docInfo.setCreator("miles-Verlag");
            docInfo.setProducer("miles-Verlag");

            PDFont fontPlain = PDType1Font.HELVETICA;
            PDFont fontBold = PDType1Font.HELVETICA_BOLD;
            Integer seite = 1;
            Integer zeile = 700;

            // Einf?gen der Texte und Bilder
            Connection conn = null;

            try {                                      // Datenbank-Treiber laden
                Class.forName(ModulHelferlein.dbDriver);
            } catch (ClassNotFoundException exept) {
                ModulHelferlein.Fehlermeldung("Bericht: Verlagsprogramm:","ClassNotFoundException: Treiber nicht gefunden. " , exept.getMessage());
            }                                          // Datenbank-Treiber laden

            try {                                      // Verbindung zur Datenbank ?ber die JDBC-Br?cke
                conn = DriverManager.getConnection(ModulHelferlein.dbUrl, ModulHelferlein.dbUser, ModulHelferlein.dbPassword);
            } catch (SQLException exept) {
                ModulHelferlein.Fehlermeldung("Bericht: Verlagsprogramm:","SQL-Exception: Verbindung nicht moeglich: " , exept.getMessage());
            }    // Verbindung zur Datenbank ?ber die JDBC-Br?cke

            final Connection conn2 = conn;

            if (conn2 != null) {                       
                Statement SQLAnfrage = null;    
                Statement SQLAnfrageAdresse = null;   
                Statement SQLAnfrageHilfe = null;

                ResultSet result = null;
                ResultSet resultAdresse = null;
                ResultSet resultHilfe = null;

                try {                                  
                    SQLAnfrage = conn2.createStatement();    
                    SQLAnfrageAdresse = conn2.createStatement();    
                    SQLAnfrageHilfe = conn2.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                                                            ResultSet.CONCUR_UPDATABLE);
                    switch (Sortierung) {
                        case 0:  // Sortierung nach ISBN
                            if (Aktiv) {
                                result = SQLAnfrage.executeQuery("SELECT * FROM tbl_buch WHERE BUCH_AKTIV='1' ORDER BY BUCH_ISBN ");
                            } else {
                                result = SQLAnfrage.executeQuery("SELECT * FROM tbl_buch ORDER BY BUCH_ISBN");
                            }
                            break;
                        case 2:  // Sortierung nach Titel
                            if (Aktiv) {
                                result = SQLAnfrage.executeQuery("SELECT * FROM tbl_buch WHERE BUCH_AKTIV='1' ORDER BY BUCH_TITEL ");
                            } else {
                                result = SQLAnfrage.executeQuery("SELECT * FROM tbl_buch ORDER BY BUCH_TITEL");
                            }
                            break;
                        case 1:  // Sortierung nach Autor
                            // neue Hilfstabelle erzeugen
                            SQLAnfrageHilfe.executeUpdate("DELETE FROM TBL_HILFE_BUCH");
                            resultHilfe = SQLAnfrageHilfe.executeQuery("SELECT * FROM TBL_HILFE_BUCH");
                            if (Aktiv) {
                                result = SQLAnfrage.executeQuery("SELECT * FROM tbl_buch WHERE BUCH_AKTIV='1'");
                            } else {
                                result = SQLAnfrage.executeQuery("SELECT * FROM tbl_buch");
                            }
                            // Hilfstabelle füllen
                            while (result.next()) {
                                resultHilfe.moveToInsertRow();

                                resultHilfe.updateInt("BUCH_ID", ID);
                                resultHilfe.updateString("BUCH_TITEL", result.getString("BUCH_TITEL"));
                                resultHilfe.updateString("BUCH_ISBN", result.getString("BUCH_ISBN"));
                                resultHilfe.updateString("BUCH_COVER", result.getString("BUCH_COVER"));
                                resultHilfe.updateString("BUCH_JAHR", result.getString("BUCH_JAHR"));
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
                                };
                                resultHilfe.updateString("BUCH_AUTOR", AutorEintrag);
                                resultHilfe.updateFloat("BUCH_PREIS", result.getFloat("BUCH_PREIS"));
                                resultHilfe.updateInt("BUCH_SEITEN", result.getInt("BUCH_SEITEN"));
                                resultHilfe.updateInt("BUCH_HC", result.getInt("BUCH_HC"));
                                resultHilfe.updateInt("BUCH_AUFLAGE", result.getInt("BUCH_AUFLAGE"));

                                resultHilfe.insertRow();

                                ID = ID + 1;
                            }
                            // Hilfstabelle nach Autor sortieren ins Resultset:
                            result = SQLAnfrage.executeQuery("SELECT * FROM tbl_HILFE_BUCH ORDER BY BUCH_AUTOR, BUCH_JAHR, BUCH_ISBN");
                            break; // sortierung nach Autor
                    }
                    if (Aktiv) {
                        AusgabeLB(cos, fontBold, 16, Color.BLACK, 55, 770, "Carola Hartmann Miles Verlag - Verlagsprogramm - aktive Verträge");
                    } else {
                        AusgabeLB(cos, fontBold, 16, Color.BLACK, 55, 770, "Carola Hartmann Miles Verlag - Verlagsprogramm - alle Bücher");
                    }
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 750,
                            "Stand: " + ModulHelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 490, 750, "Seite: " + Integer.toString(seite));

                    Linie(cos, 2, 56, 740, 539, 740);
                    zeile = 730;

                    while (result.next()) {
//helferlein.Infomeldung("Bild " , result.getString("BUCH_COVER"));
                        // Kopfzeile mit Bild
                        try {
                            PDImageXObject pdImage = PDImageXObject.createFromFile(result.getString("BUCH_COVER"), document);
                            cos.drawImage(pdImage, 55, zeile - 80, 60, 80);
                        } catch (FileNotFoundException fnfex) {
                            ModulHelferlein.Fehlermeldung("Bericht: Verlagsprogramm:","kein Bild gefunden" , fnfex.getMessage());
                        }

                        switch (Sortierung) {
                            case 0: // ISBN
                                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 150, zeile - 15, "ISBN " + result.getString("BUCH_ISBN"));
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
                                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 165, zeile - 30, AutorEintrag);
                                Ausgabe(cos, fontPlain, 12, Color.BLACK, 165, zeile - 45, result.getString("BUCH_TITEL"), 390);
                                break;
                            case 1: // Autor
                                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 150, zeile - 15, result.getString("BUCH_AUTOR"));
                                Ausgabe(cos, fontPlain, 12, Color.BLACK, 165, zeile - 30, result.getString("BUCH_TITEL"), 390);
                                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 165, zeile - 45, "ISBN " + result.getString("BUCH_ISBN"));
                                break;
                            case 2: // Titel
                                Ausgabe(cos, fontPlain, 12, Color.BLACK, 150, zeile - 15, result.getString("BUCH_TITEL"), 390);
                                col_Autorliste = result.getString("BUCH_AUTOR").split(",");
                                AutorEintrag = "";
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
                                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 150, zeile - 30, AutorEintrag);
                                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 165, zeile - 45, "ISBN " + result.getString("BUCH_ISBN"));
                                break;
                        }
                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 165, zeile - 60,
                                result.getString("BUCH_SEITEN") + " S., ");
                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 265, zeile - 60,
                                Float.toString(result.getFloat("BUCH_PREIS")) + " Euro");
                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 165, zeile - 75,
                                result.getString("BUCH_AUFLAGE") + ". Auflage, ");
                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 265, zeile - 75,
                                "Berlin, " + result.getString("BUCH_JAHR"));
                        switch (result.getInt("BUCH_HC")) {
                            case 1:
                                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 365, zeile - 75, "Hardcover");
                                break;
                            case 0:
                                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 365, zeile - 75, "Paperback");
                                break;
                            case 2:
                                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 365, zeile - 75, "E-Book");
                                break;
                        }

                        zeile = zeile - 100;

                        if (zeile < 100) {

                            // Neue Seite beginnen
                            cos.close();

                            PDPage page = new PDPage(A4);

                            document.addPage(page);
                            zeile = 730;
                            seite = seite + 1;
                            cos = new PDPageContentStream(document, page);
                            AusgabeLB(cos, fontBold, 16, Color.BLACK, 55, 770,
                                    "Carola Hartmann Miles Verlag - Verlagsprogramm");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 750,
                                    "Stand: " + ModulHelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 490, 750, "Seite: " + Integer.toString(seite));
                            Linie(cos,2,56, 740, 539, 740);
                            
                        }
                    }    // while - gehe durch alle Datensätze der BUCH-DB

                    // close the content stream for page 1
                    cos.close();

                    // Save the results and ensure that the document is properly closed:
                    document.save(outputFileName);
                    document.close();
                    ModulHelferlein.Infomeldung("Verlagsprogramm ist als PDF gespeichert!"
                            , outputFileName);

                    try {    // AusgabeLB der Liste
                        Runtime.getRuntime().exec("cmd /c " + "\"" + outputFileName + "\"");
                    } catch (IOException exept) {
                        ModulHelferlein.Fehlermeldung("Bericht: Verlagsprogramm:","IO-Exception: ", exept.getMessage());
                    }    // try AusgabeLB der Liste
                } catch (SQLException exept) {
                    ModulHelferlein.Fehlermeldung("Bericht: Verlagsprogramm:","SQL-Exception: SQL-Anfrage nicht möglich: " , exept.getMessage());
                } catch (IOException e) {
                    ModulHelferlein.Fehlermeldung("Bericht: Verlagsprogramm:","IO-Exception: " ,e.getMessage());
                }        // SQL-Anfrage
            }            // if verbindung steht
        } catch (IOException e) {
            ModulHelferlein.Fehlermeldung("Bericht: Verlagsprogramm:","IO-Exception: " ,e.getMessage());
        }                // try Dokument erstellen
    }    // void PDF

    /**
     *
     * @param Sortierung
     * @param Aktiv
     */
    public static void DOC(Integer Sortierung, boolean Aktiv) {
        ModulHelferlein.Infomeldung("noch nicht implementiert");
    }

    /**
     *
     * @param Sortierung
     * @param Aktiv
     */
    public static void XLS(Integer Sortierung, boolean Aktiv) {
        ModulHelferlein.Infomeldung("noch nicht implementiert");
    }
}    // class


//~ Formatted by Jindent --- http://www.jindent.com
