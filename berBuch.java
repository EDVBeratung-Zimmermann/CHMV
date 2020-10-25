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
import static milesVerlagMain.ModulHelferlein.Linie;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import static org.apache.pdfbox.pdmodel.common.PDRectangle.A4;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import static milesVerlagMain.ModulHelferlein.AusgabeLB;
import static milesVerlagMain.ModulHelferlein.Ausgabe;

/**
 * Klasse zur Erzeugung einer ?bersicht der B?cher des miles Verlages
 *
 * @author Thomas Zimmermann
 *
 */
public class berBuch {

    /**
     *
     * @param BuchID
     * @param Anzeige
     */
    public static void Buch(String BuchID, Integer Anzeige) {

        PDDocument document = new PDDocument();
        PDPage page1 = new PDPage(A4);
        document.addPage(page1);

        Integer Prozent = 0;
        Integer Anzahl = 0;

        String outputFileName = "";

        // Start a new content stream which will "hold" the to be created content
        try { //Dokument erstellen
            PDPageContentStream cos = new PDPageContentStream(document, page1);

            PDDocumentInformation docInfo = document.getDocumentInformation();

            docInfo.setSubject("Buchprojekte");
            docInfo.setTitle("miles-Verlag Stammdaten");
            docInfo.setAuthor("miles-Verlag");
            docInfo.setCreationDate(Calendar.getInstance());
            docInfo.setCreator("miles-Verlag");
            docInfo.setProducer("miles-Verlag");

            PDFont fontPlain = PDType1Font.HELVETICA;
            PDFont fontBold = PDType1Font.HELVETICA_BOLD;

            // Einf?gen der Texte und Bilder
            Connection conn = null;

            try { // Datenbank-Treiber laden
                Class.forName(ModulHelferlein.dbDriver);
            } catch (ClassNotFoundException exept) {
                ModulHelferlein.Fehlermeldung(
                        "ClassNotFoundException: Treiber nicht gefunden. "
                        + exept.getMessage());
            } // Datenbank-Treiber laden

            try { // Verbindung zur Datenbank ?ber die JDBC-Br?cke
                conn = DriverManager.getConnection(ModulHelferlein.dbUrl, ModulHelferlein.dbUser, ModulHelferlein.dbPassword);
            } catch (SQLException exept) {
                ModulHelferlein.Fehlermeldung("SQL-Exception: Verbindung nicht moeglich: " + exept.getMessage());
            } // Verbindung zur Datenbank ?ber die JDBC-Br?cke

            final Connection conn2 = conn;

            if (conn2 != null) { //verbindung steht
                Statement SQLAnfrage = null; // Anfrage erzeugen
                Statement SQLAnfrageAdresse = null; // Anfrage erzeugen

                ResultSet result = null;
                ResultSet resultAdresse = null;

                try { //SQL-Anfrage für die Buchtabelle
                    SQLAnfrage = conn2.createStatement(); // Anfrage der DB conn2 zuordnen
                    SQLAnfrageAdresse = conn2.createStatement(); // Anfrage der DB conn2 zuordnen
                    result = SQLAnfrage.executeQuery("SELECT * FROM tbl_buch WHERE BUCH_ID='" + BuchID + "'");

                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 770, "miles-Verlag Verlagsverwaltung - Stammdaten");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 755, "Buchprojekt " + BuchID + ", Stand: "
                            + ModulHelferlein.printSimpleDateFormat("dd.MM.yyyy"));

                    Linie(cos, 2, 56, 750, 539, 750);

                    while (result.next()) { // geht durch alle zeilen
                        try {
                            PDImageXObject pdImage = PDImageXObject.createFromFile(result.getString("BUCH_COVER"), document);
                            cos.drawImage(pdImage, 55, 100, 120, 160);
                        } catch (FileNotFoundException fnfex) {
                            ModulHelferlein.Fehlermeldung("Bericht: Buchprojekt: kein Bild gefunden",fnfex.getMessage());
                        }
                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 735, "ID");
                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 200, 735, Integer.toString(result.getInt("BUCH_ID")));

                        if (result.getBoolean("BUCH_HERAUSGEBER")) {
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 720, "Herausgeber");
                        } else {
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 720, "Autor");
                        }
                        resultAdresse = SQLAnfrageAdresse.executeQuery("SELECT * FROM TBL_ADRESSE WHERE ADRESSEN_ID ='" + result.getString("BUCH_AUTOR") + "'");
                        if (resultAdresse.next()) {
                            outputFileName = ModulHelferlein.pathBerichte + "\\Buchprojekte\\"
                                    + "Buchprojekt-"
                                    + BuchID
                                    + "-"
                                    + result.getString("BUCH_ISBN")
                                    + "-"
                                    + resultAdresse.getString("ADRESSEN_NAME")
                                    + "-"
                                    + ModulHelferlein.printSimpleDateFormat("yyyyMMdd")
                                    + ".pdf";

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
                            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 200, 720, AutorEintrag);
                        } else {
                            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 200, 720, "-");
                            outputFileName = ModulHelferlein.pathBerichte + "/Buchprojekte/"
                                    + "Buchprojekt-"
                                    + BuchID
                                    + "-"
                                    + result.getString("BUCH_ISBN")
                                    + "-"
                                    + "OHNE"
                                    + "-"
                                    + ModulHelferlein.printSimpleDateFormat("yyyyMMdd")
                                    + ".pdf";
                        }

                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 705, "Titel");
                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 200, 705, result.getString("BUCH_TITEL"));

                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 690, "EK");
                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 200, 690, Float.toString(result.getFloat("BUCH_EK")));

                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 675, "VK");
                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 200, 675, Float.toString(result.getFloat("BUCH_PREIS")));

                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 660, "ISBN");
                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 200, 660, result.getString("BUCH_ISBN"));

                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 645, "Seitenzahl");
                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 200, 645, result.getString("BUCH_SEITEN"));

                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 630, "Auflage");
                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 200, 630, result.getString("BUCH_AUFLAGE"));

                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 615, "Erscheinungsjahr");
                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 200, 615, result.getString("BUCH_JAHR"));

                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 600, "Bestand");
                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 200, 600, result.getString("BUCH_BESTAND"));

                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 585, "Druckerei");
                        resultAdresse = SQLAnfrageAdresse.executeQuery("SELECT * FROM TBL_ADRESSE WHERE ADRESSEN_ID ='" + result.getString("BUCH_DRUCKEREI") + "'");
                        if (resultAdresse.next()) {
                            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 200, 585, resultAdresse.getString("ADRESSEN_NAME"));
                        } else {
                            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 200, 585, "-");
                        }

                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 570, "Druckereinummer");
                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 200, 570, result.getString("BUCH_DRUCKEREINUMMER"));

                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 555, "DEU NatBibliothek");
                        if (result.getBoolean("BUCH_DEUNATBIBL")) {
                            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 200, 555, "Pflichtexemplar versendet");
                        } else {
                            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 200, 555, "Pflichtexemplar nicht versendet");
                        }

                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 540, "B LandesBibliothek");
                        if (result.getBoolean("BUCH_BERLLBIBL")) {
                            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 200, 540, "Pflichtexemplar versendet");
                        } else {
                            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 200, 540, "Pflichtexemplar nicht versendet");
                        }

                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 525, "Vertrag Autor");
                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 200, 525, result.getString("BUCH_VERTRAG"));

                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 510, "Vertrag BOD");
                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 200, 510, result.getString("BUCH_BOD_VERTRAG"));

                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 495, "Cover");
                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 200, 495, result.getString("BUCH_COVER"));

                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 480, "Werbeflyer");
                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 200, 480, result.getString("BUCH_FLYER"));

                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 465, "Quelltext");
                        AusgabeLB(cos, fontPlain, 12, Color.BLACK, 200, 465, result.getString("BUCH_TEXT"));

                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 450, "Autor erhält Honorar");
                        if (result.getBoolean("BUCH_HONORAR")) {
                            Prozent = result.getInt("BUCH_HONORAR_PROZENT");
                            Anzahl = result.getInt("BUCH_HONORAR_ANZAHL");
                            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 200, 450, "ja, ab "
                                    + Anzahl.toString() + " Stück mit je "
                                    + Prozent.toString() + "% vom Netto-VK pro Stück");
                        } else {
                            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 200, 450, "nein");
                        }

                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 435, "Verz. lief. Bücher");
                        if (result.getBoolean("BUCH_VLB")) {
                            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 200, 435, "Datensatz angelegt");
                        } else {
                            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 200, 435, "Datensatz ist nicht angelegt");
                        }

                        AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 420, "Beschreibung");
                        String[] splitBeschreibung = result.getString("BUCH_BESCHREIBUNG").split(" ");
                        Integer woerter = splitBeschreibung.length;
                        Integer zeilenNr = 1;
                        Integer i = 0;
                        while (i < woerter) {
                            String zeile = "";
                            while ((zeile.length() < 50) && (i < woerter)) {
                                zeile = zeile + " " + splitBeschreibung[i];
                                i = i + 1;
                            } // while
                            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 200, 420 - 15 * (zeilenNr - 1), zeile);
                            zeilenNr = zeilenNr + 1;
                        } // while

                    } //while - gehe durch alle Datensätze der BUCH-DB

                    // close the content stream for page 1
                    cos.close();

                    // Save the results and ensure that the document is properly closed:
                    document.save(outputFileName);
                    document.close();

                    ModulHelferlein.Infomeldung("Titelseite des Buchprojektes ist als PDF gespeichert!");

                    if (Anzeige == 1) {
                        try { //Ausgabe der Liste
                            Runtime.getRuntime().exec("cmd.exe /c " + "\"" + outputFileName + "\"");
                        } catch (IOException exept) {
                            ModulHelferlein.Fehlermeldung(
                                    "Anzeige der PDF: IO-Exception: " + exept.getMessage());
                        }//try AusgabeLB der Liste
                    }
                    
                } catch (SQLException exept) {
                    ModulHelferlein.Fehlermeldung("Durchlauf Buch-Tabelle","SQL-Exception: SQL-Anfrage nicht möglich: " + exept.getMessage());
                } catch (IOException  e) {
                    ModulHelferlein.Fehlermeldung("Durchlauf Buch-Tabelle","IO-Exception: " + e.getMessage());
                } //SQL-Anfrage

            } //if verbindung steht

        } catch (IOException e) {
            ModulHelferlein.Fehlermeldung("FileNotFoundException: " + e.getMessage());
        } //try Dokument erstellen

    }

    public static void bericht() {

        System.out.println("Bericht Stammdaten Buchprojekte gestartet");
        PDDocument document = new PDDocument();
        PDPage page1 = new PDPage(A4);
        document.addPage(page1);

        Integer Prozent = 0;
        Integer Anzahl = 0;

        // Start a new content stream which will "hold" the to be created content
        try { //Dokument erstellen
            PDPageContentStream cos = new PDPageContentStream(document, page1);

            String outputFileName = ModulHelferlein.pathBerichte + "/Buchprojekte/"
                    + "Buchprojekte-"
                    + ModulHelferlein.printSimpleDateFormat("yyyyMMdd")
                    + ".pdf";

            PDDocumentInformation docInfo = document.getDocumentInformation();

            docInfo.setSubject("Buchprojekte");
            docInfo.setTitle("miles-Verlag Stammdaten");
            docInfo.setAuthor("miles-Verlag");
            docInfo.setCreationDate(Calendar.getInstance());
            docInfo.setCreator("miles-Verlag");
            docInfo.setProducer("miles-Verlag");

            PDFont fontPlain = PDType1Font.HELVETICA;
            PDFont fontBold = PDType1Font.HELVETICA_BOLD;

            Integer seite = 1;

            // Einf?gen der Texte und Bilder
            Connection conn = null;

            try { // Datenbank-Treiber laden
                Class.forName(ModulHelferlein.dbDriver);
            } catch (ClassNotFoundException exept) {
                ModulHelferlein.Fehlermeldung(
                        "ClassNotFoundException: Treiber nicht gefunden. "
                        + exept.getMessage());
            } // Datenbank-Treiber laden

            try { // Verbindung zur Datenbank ?ber die JDBC-Br?cke
                conn = DriverManager.getConnection(ModulHelferlein.dbUrl, ModulHelferlein.dbUser, ModulHelferlein.dbPassword);
            } catch (SQLException exept) {
                ModulHelferlein.Fehlermeldung("SQL-Exception: Verbindung nicht moeglich: " + exept.getMessage());
            } // Verbindung zur Datenbank ?ber die JDBC-Br?cke

            final Connection conn2 = conn;

            if (conn2 != null) { //verbindung steht
                Statement SQLAnfrage = null; // Anfrage erzeugen
                Statement SQLAnfrageAdresse = null; // Anfrage erzeugen

                ResultSet result = null;
                ResultSet resultAdresse = null;

                try { //SQL-Anfrage
                    SQLAnfrage = conn2.createStatement(); // Anfrage der DB conn2 zuordnen
                    SQLAnfrageAdresse = conn2.createStatement(); // Anfrage der DB conn2 zuordnen
                    result = SQLAnfrage.executeQuery("SELECT * FROM tbl_buch ORDER BY BUCH_ISBN"); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert

                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 770, "miles-Verlag Verlagsverwaltung - Stammdaten");
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 755, "Buchprojekte, Stand: "
                            + ModulHelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                    AusgabeLB(cos, fontBold, 12, Color.BLACK, 455, 770, "Seite: " + Integer.toString(seite));

                    Linie(cos, 2, 56, 750, 539, 750);

                    while (result.next()) { // geht durch alle zeilen
                        System.out.println("-> Buch " + result.getString("BUCH_ISBN") + " " + result.getString("BUCH_TITEL"));
                        if (result.getString("BUCH_ISBN").equals("-------------"))  {

                        } else {

                            try {
                                PDImageXObject pdImage = PDImageXObject.createFromFile(result.getString("BUCH_COVER"), document);
                                cos.drawImage(pdImage, 55, 100, 120, 160);
                            } catch (FileNotFoundException fnfex) {
                                ModulHelferlein.Fehlermeldung("   Bericht: Buchprojekte: kein Bild gefunden",fnfex.getMessage());
                            }

                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 735, "ID");
                            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 200, 735, Integer.toString(result.getInt("BUCH_ID")));

                            if (result.getBoolean("BUCH_HERAUSGEBER")) {
                                AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 720, "Herausgeber");
                            } else {
                                AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 720, "Autor");
                            }
                            resultAdresse = SQLAnfrageAdresse.executeQuery("SELECT * FROM TBL_ADRESSE WHERE ADRESSEN_ID ='" + result.getString("BUCH_AUTOR") + "'");
                            if (resultAdresse.next()) {
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
                                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 200, 720, AutorEintrag);
                            } else {
                                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 200, 720, "-");
                            }

                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 705, "Titel");
                            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 200, 705, result.getString("BUCH_TITEL"));

                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 690, "EK");
                            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 200, 690, Float.toString(result.getFloat("BUCH_EK")));

                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 675, "VK");
                            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 200, 675, Float.toString(result.getFloat("BUCH_PREIS")));

                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 660, "ISBN");
                            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 200, 660, result.getString("BUCH_ISBN"));

                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 645, "Seitenzahl");
                            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 200, 645, result.getString("BUCH_SEITEN"));

                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 630, "Auflage");
                            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 200, 630, result.getString("BUCH_AUFLAGE"));

                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 615, "Erscheinungsjahr");
                            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 200, 615, result.getString("BUCH_JAHR"));

                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 600, "Bestand");
                            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 200, 600, result.getString("BUCH_BESTAND"));

                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 585, "Druckerei");
                            resultAdresse = SQLAnfrageAdresse.executeQuery("SELECT * FROM TBL_ADRESSE WHERE ADRESSEN_ID ='" + result.getString("BUCH_DRUCKEREI") + "'");
                            if (resultAdresse.next()) {
                                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 200, 585, resultAdresse.getString("ADRESSEN_NAME"));
                            } else {
                                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 200, 585, "-");
                            }

                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 570, "Druckereinummer");
                            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 200, 570, result.getString("BUCH_DRUCKEREINUMMER"));

                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 555, "DEU NatBibliothek");
                            if (result.getBoolean("BUCH_DEUNATBIBL")) {
                                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 200, 555, "Pflichtexemplar versendet");
                            } else {
                                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 200, 555, "Pflichtexemplar nicht versendet");
                            }

                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 540, "B LandesBibliothek");
                            if (result.getBoolean("BUCH_BERLLBIBL")) {
                                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 200, 540, "Pflichtexemplar versendet");
                            } else {
                                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 200, 540, "Pflichtexemplar nicht versendet");
                            }

                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 525, "Vertrag Autor");
                            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 200, 525, result.getString("BUCH_VERTRAG"));

                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 510, "Vertrag BOD");
                            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 200, 510, result.getString("BUCH_BOD_VERTRAG"));

                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 495, "Cover");
                            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 200, 495, result.getString("BUCH_COVER"));

                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 480, "Werbeflyer");
                            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 200, 480, result.getString("BUCH_FLYER"));

                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 465, "Quelltext");
                            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 200, 465, result.getString("BUCH_TEXT"));

                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 450, "Autor erhält Honorar");
                            if (result.getBoolean("BUCH_HONORAR")) {
                                Prozent = result.getInt("BUCH_HONORAR_PROZENT");
                                Anzahl = result.getInt("BUCH_HONORAR_ANZAHL");
                                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 200, 450, "ja, ab "
                                        + Anzahl.toString() + " Stück mit je "
                                        + Prozent.toString() + "% vom Netto-VK pro Stück");
                            } else {
                                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 200, 450, "nein");
                            }

                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 435, "Verz. lief. Bücher");
                            if (result.getBoolean("BUCH_VLB")) {
                                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 200, 435, "Datensatz angelegt");
                            } else {
                                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 200, 435, "Datensatz ist nicht angelegt");
                            }

                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 420, "Beschreibung");
                            String[] splitBeschreibung = result.getString("BUCH_BESCHREIBUNG").split(" ");
                            Integer woerter = splitBeschreibung.length;
                            Integer zeilenNr = 1;
                            Integer i = 0;
                            while (i < woerter) {
                                String zeile = "";
                                while ((zeile.length() < 50) && (i < woerter)) {
                                    zeile = zeile + " " + splitBeschreibung[i];
                                    i = i + 1;
                                } // while
                                AusgabeLB(cos, fontPlain, 12, Color.BLACK, 200, 420 - 15 * (zeilenNr - 1), zeile);
                                zeilenNr = zeilenNr + 1;
                            } // while

                            // Neue Seite beginnen
                            cos.close();
                            PDPage page = new PDPage(A4);
                            document.addPage(page);
                            seite = seite + 1;
                            cos = new PDPageContentStream(document, page);
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 770, "miles-Verlag Verlagsverwaltung - Stammdaten");
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 755, "Buchprojekte, Stand: "
                                    + ModulHelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                            AusgabeLB(cos, fontBold, 12, Color.BLACK, 455, 770, "Seite: " + Integer.toString(seite));

                            Linie(cos, 2, 56, 750, 539, 750);
                        }
                    } //while - gehe durch alle Datensätze der BUCH-DB

                    // close the content stream for page 1
                    cos.close();

                    // Save the results and ensure that the document is properly closed:
                    document.save(outputFileName);
                    document.close();

                    ModulHelferlein.Infomeldung("Liste der Buchprojekte ist als PDF gespeichert!");

                    try { //Ausgabe der Liste
                        Runtime.getRuntime().exec("cmd.exe /c " + outputFileName);
                    } catch (IOException exept) {
                        ModulHelferlein.Fehlermeldung("Ausgabe der Liste: IO-Exception: " + exept.getMessage());
                    }//try AusgabeLB der Liste

                } catch (SQLException exept) {
                    ModulHelferlein.Fehlermeldung("   Liste Buchprojekte","   SQL-Exception: SQL-Anfrage nicht möglich: " + exept.getMessage());
                } catch (IOException e) {
                    ModulHelferlein.Fehlermeldung("   Liste Buchprojekte","   IO-Exception: " + e.getMessage());
                } //SQL-Anfrage

            } //if verbindung steht

        } catch (IOException e) {
            ModulHelferlein.Fehlermeldung("FileNotFoundException: " + e.getMessage());
        } //try Dokument erstellen
        System.out.println("Bericht Stammdaten Buchprojekte beendet");
    } //void bericht

} //class

