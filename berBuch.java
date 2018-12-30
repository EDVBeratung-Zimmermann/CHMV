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
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

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
     */
    public static void Buch(String BuchID) {

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
                Class.forName(Modulhelferlein.dbDriver);
            } catch (ClassNotFoundException exept) {
                Modulhelferlein.Fehlermeldung(
                        "ClassNotFoundException: Treiber nicht gefunden. "
                        + exept.getMessage());
            } // Datenbank-Treiber laden

            try { // Verbindung zur Datenbank ?ber die JDBC-Br?cke
                conn = DriverManager.getConnection(Modulhelferlein.dbUrl, Modulhelferlein.dbUser, Modulhelferlein.dbPassword);
            } catch (SQLException exept) {
                Modulhelferlein.Fehlermeldung("SQL-Exception: Verbindung nicht moeglich: " + exept.getMessage());
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
                    result = SQLAnfrage.executeQuery("SELECT * FROM tbl_buch WHERE BUCH_ID='" + BuchID + "'"); 

                    Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 770, "miles-Verlag Verlagsverwaltung - Stammdaten");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 755, "Buchprojekt " + BuchID + ", Stand: "
                            + Modulhelferlein.printSimpleDateFormat("dd.MM.yyyy"));

                    Linie(cos,2,56, 750, 539, 750);
                    
                    while (result.next()) { // geht durch alle zeilen
                        try {
                            PDImageXObject pdImage = PDImageXObject.createFromFile(result.getString("BUCH_COVER"), document);
                            cos.drawImage(pdImage, 55, 100, 120, 160);
                        } catch (FileNotFoundException fnfex) {
                            Modulhelferlein.Fehlermeldung("Bericht: Verlagsprogramm: kein Bild gefunden");
                        }
                        Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 735, "ID");
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 200, 735, Integer.toString(result.getInt("BUCH_ID")));

                        if (result.getBoolean("BUCH_HERAUSGEBER")) {
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 720, "Herausgeber");
                        } else {
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 720, "Autor");
                        }
                        resultAdresse = SQLAnfrageAdresse.executeQuery("SELECT * FROM TBL_ADRESSE WHERE ADRESSEN_ID ='" + result.getString("BUCH_AUTOR") + "'");
                        if (resultAdresse.next()) {
                            outputFileName = Modulhelferlein.pathBerichte + "\\Buchprojekte\\"
                                    + "Buchprojekt-"
                                    + BuchID
                                    + "-"
                                    + result.getString("BUCH_ISBN")
                                    + "-"
                                    + resultAdresse.getString("ADRESSEN_NAME")
                                    + "-"
                                    + Modulhelferlein.printSimpleDateFormat("yyyyMMdd")
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
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 200, 720, AutorEintrag);
                        } else {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 200, 720, "-");
                            outputFileName = Modulhelferlein.pathBerichte + "/Buchprojekte/"
                                    + "Buchprojekt-"
                                    + BuchID
                                    + "-"
                                    + result.getString("BUCH_ISBN")
                                    + "-"
                                    + "OHNE"
                                    + "-"
                                    + Modulhelferlein.printSimpleDateFormat("yyyyMMdd")
                                    + ".pdf";
                        }

                        Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 705, "Titel");
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 200, 705, result.getString("BUCH_TITEL"));

                        Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 690, "EK");
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 200, 690, Float.toString(result.getFloat("BUCH_EK")));

                        Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 675, "VK");
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 200, 675, Float.toString(result.getFloat("BUCH_PREIS")));

                        Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 660, "ISBN");
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 200, 660, result.getString("BUCH_ISBN"));

                        Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 645, "Seitenzahl");
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 200, 645, result.getString("BUCH_SEITEN"));

                        Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 630, "Auflage");
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 200, 630, result.getString("BUCH_AUFLAGE"));

                        Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 615, "Erscheinungsjahr");
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 200, 615, result.getString("BUCH_JAHR"));

                        Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 600, "Bestand");
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 200, 600, result.getString("BUCH_BESTAND"));

                        Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 585, "Druckerei");
                        resultAdresse = SQLAnfrageAdresse.executeQuery("SELECT * FROM TBL_ADRESSE WHERE ADRESSEN_ID ='" + result.getString("BUCH_DRUCKEREI") + "'");
                        if (resultAdresse.next()) {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 200, 585, resultAdresse.getString("ADRESSEN_NAME"));
                        } else {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 200, 585, "-");
                        }

                        Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 570, "Druckereinummer");
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 200, 570, result.getString("BUCH_DRUCKEREINUMMER"));

                        Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 555, "DEU NatBibliothek");
                        if (result.getBoolean("BUCH_DEUNATBIBL")) {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 200, 555, "Pflichtexemplar versendet");
                        } else {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 200, 555, "Pflichtexemplar nicht versendet");
                        }

                        Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 540, "B LandesBibliothek");
                        if (result.getBoolean("BUCH_BERLLBIBL")) {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 200, 540, "Pflichtexemplar versendet");
                        } else {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 200, 540, "Pflichtexemplar nicht versendet");
                        }

                        Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 525, "Vertrag Autor");
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 200, 525, result.getString("BUCH_VERTRAG"));

                        Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 510, "Vertrag BOD");
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 200, 510, result.getString("BUCH_BOD_VERTRAG"));

                        Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 495, "Cover");
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 200, 495, result.getString("BUCH_COVER"));

                        Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 480, "Werbeflyer");
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 200, 480, result.getString("BUCH_FLYER"));

                        Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 465, "Quelltext");
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 200, 465, result.getString("BUCH_TEXT"));

                        Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 450, "Autor erhält Honorar");
                        if (result.getBoolean("BUCH_HONORAR")) {
                            Prozent = result.getInt("BUCH_HONORAR_PROZENT");
                            Anzahl = result.getInt("BUCH_HONORAR_ANZAHL");
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 200, 450, "ja, ab "
                                    + Anzahl.toString() + " Stück mit je "
                                    + Prozent.toString() + "% vom Netto-VK pro Stück");
                        } else {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 200, 450, "nein");
                        }

                        Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 435, "Verz. lief. Bücher");
                        if (result.getBoolean("BUCH_VLB")) {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 200, 435, "Datensatz angelegt");
                        } else {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 200, 435, "Datensatz ist nicht angelegt");
                        }

                        Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 420, "Beschreibung");
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
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 200, 420 - 15 * (zeilenNr - 1), zeile);
                            zeilenNr = zeilenNr + 1;
                        } // while

                    } //while - gehe durch alle Datensätze der BUCH-DB

                    // close the content stream for page 1
                    cos.close();

                    // Save the results and ensure that the document is properly closed:
                    document.save(outputFileName);
                    document.close();

                    Modulhelferlein.Infomeldung("Titelseite des Buchprojektes ist als PDF gespeichert!");

                    try { //Ausgabe der Liste
                        Runtime.getRuntime().exec("cmd.exe /c " + "\"" + outputFileName + "\"");
                    } catch (IOException exept) {
                        Modulhelferlein.Fehlermeldung(
                                "Exception: " + exept.getMessage());
                    }//try Ausgabe der Liste

                } catch (SQLException exept) {
                    Modulhelferlein.Fehlermeldung("SQL-Exception: SQL-Anfrage nicht möglich: " + exept.getMessage());
                } catch (IOException  e) {
                    Modulhelferlein.Fehlermeldung("IO-Exception: " + e.getMessage());
                } //SQL-Anfrage

            } //if verbindung steht

        } catch (IOException e) {
            Modulhelferlein.Fehlermeldung("FileNotFoundException: " + e.getMessage());
        } //try Dokument erstellen

    }

    public static void bericht() {

        PDDocument document = new PDDocument();
        PDPage page1 = new PDPage(A4);
        document.addPage(page1);

        Integer Prozent = 0;
        Integer Anzahl = 0;

        // Start a new content stream which will "hold" the to be created content
        try { //Dokument erstellen
            PDPageContentStream cos = new PDPageContentStream(document, page1);

            String outputFileName = Modulhelferlein.pathBerichte + "/Buchprojekte/"
                    + "Buchprojekte-"
                    + Modulhelferlein.printSimpleDateFormat("yyyyMMdd")
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
                Class.forName(Modulhelferlein.dbDriver);
            } catch (ClassNotFoundException exept) {
                Modulhelferlein.Fehlermeldung(
                        "ClassNotFoundException: Treiber nicht gefunden. "
                        + exept.getMessage());
            } // Datenbank-Treiber laden

            try { // Verbindung zur Datenbank ?ber die JDBC-Br?cke
                conn = DriverManager.getConnection(Modulhelferlein.dbUrl, Modulhelferlein.dbUser, Modulhelferlein.dbPassword);
            } catch (SQLException exept) {
                Modulhelferlein.Fehlermeldung("SQL-Exception: Verbindung nicht moeglich: " + exept.getMessage());
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
                    result = SQLAnfrage.executeQuery("SELECT * FROM tbl_buch"); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert

                    Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 770, "miles-Verlag Verlagsverwaltung - Stammdaten");
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 755, "Buchprojekte, Stand: "
                            + Modulhelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                    Ausgabe(cos, fontBold, 12, Color.BLACK, 455, 770, "Seite: " + Integer.toString(seite));

                    Linie(cos,2,56, 750, 539, 750);
                    
                    while (result.next()) { // geht durch alle zeilen
                        try {
                            PDImageXObject pdImage = PDImageXObject.createFromFile(result.getString("BUCH_COVER"), document);
                            cos.drawImage(pdImage, 55, 100, 120, 160);
                        } catch (FileNotFoundException fnfex) {
                            Modulhelferlein.Fehlermeldung("Bericht: Buch: kein Bild gefunden");
                        }

                        Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 735, "ID");
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 200, 735, Integer.toString(result.getInt("BUCH_ID")));

                        if (result.getBoolean("BUCH_HERAUSGEBER")) {
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 720, "Herausgeber");
                        } else {
                            Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 720, "Autor");
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
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 200, 720, AutorEintrag);
                        } else {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 200, 720, "-");
                        }

                        Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 705, "Titel");
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 200, 705, result.getString("BUCH_TITEL"));

                        Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 690, "EK");
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 200, 690, Float.toString(result.getFloat("BUCH_EK")));

                        Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 675, "VK");
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 200, 675, Float.toString(result.getFloat("BUCH_PREIS")));

                        Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 660, "ISBN");
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 200, 660, result.getString("BUCH_ISBN"));

                        Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 645, "Seitenzahl");
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 200, 645, result.getString("BUCH_SEITEN"));

                        Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 630, "Auflage");
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 200, 630, result.getString("BUCH_AUFLAGE"));

                        Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 615, "Erscheinungsjahr");
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 200, 615, result.getString("BUCH_JAHR"));

                        Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 600, "Bestand");
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 200, 600, result.getString("BUCH_BESTAND"));

                        Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 585, "Druckerei");
                        resultAdresse = SQLAnfrageAdresse.executeQuery("SELECT * FROM TBL_ADRESSE WHERE ADRESSEN_ID ='" + result.getString("BUCH_DRUCKEREI") + "'");
                        if (resultAdresse.next()) {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 200, 585, resultAdresse.getString("ADRESSEN_NAME"));
                        } else {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 200, 585, "-");
                        }

                        Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 570, "Druckereinummer");
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 200, 570, result.getString("BUCH_DRUCKEREINUMMER"));

                        Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 555, "DEU NatBibliothek");
                        if (result.getBoolean("BUCH_DEUNATBIBL")) {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 200, 555, "Pflichtexemplar versendet");
                        } else {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 200, 555, "Pflichtexemplar nicht versendet");
                        }

                        Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 540, "B LandesBibliothek");
                        if (result.getBoolean("BUCH_BERLLBIBL")) {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 200, 540, "Pflichtexemplar versendet");
                        } else {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 200, 540, "Pflichtexemplar nicht versendet");
                        }

                        Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 525, "Vertrag Autor");
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 200, 525, result.getString("BUCH_VERTRAG"));

                        Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 510, "Vertrag BOD");
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 200, 510, result.getString("BUCH_BOD_VERTRAG"));

                        Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 495, "Cover");
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 200, 495, result.getString("BUCH_COVER"));

                        Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 480, "Werbeflyer");
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 200, 480, result.getString("BUCH_FLYER"));

                        Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 465, "Quelltext");
                        Ausgabe(cos, fontPlain, 12, Color.BLACK, 200, 465, result.getString("BUCH_TEXT"));

                        Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 450, "Autor erhält Honorar");
                        if (result.getBoolean("BUCH_HONORAR")) {
                            Prozent = result.getInt("BUCH_HONORAR_PROZENT");
                            Anzahl = result.getInt("BUCH_HONORAR_ANZAHL");
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 200, 450, "ja, ab "
                                    + Anzahl.toString() + " Stück mit je "
                                    + Prozent.toString() + "% vom Netto-VK pro Stück");
                        } else {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 200, 450, "nein");
                        }

                        Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 435, "Verz. lief. Bücher");
                        if (result.getBoolean("BUCH_VLB")) {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 200, 435, "Datensatz angelegt");
                        } else {
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 200, 435, "Datensatz ist nicht angelegt");
                        }

                        Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 420, "Beschreibung");
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
                            Ausgabe(cos, fontPlain, 12, Color.BLACK, 200, 420 - 15 * (zeilenNr - 1), zeile);
                            zeilenNr = zeilenNr + 1;
                        } // while

                        // Neue Seite beginnen
                        cos.close();
                        PDPage page = new PDPage(A4);
                        document.addPage(page);
                        seite = seite + 1;
                        cos = new PDPageContentStream(document, page);
                        Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 770, "miles-Verlag Verlagsverwaltung - Stammdaten");
                        Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 755, "Buchprojekte, Stand: "
                                + Modulhelferlein.printSimpleDateFormat("dd.MM.yyyy"));
                        Ausgabe(cos, fontBold, 12, Color.BLACK, 455, 770, "Seite: " + Integer.toString(seite));

                        Linie(cos,2,56, 750, 539, 750);
                        
                    } //while - gehe durch alle Datensätze der BUCH-DB

                    // close the content stream for page 1
                    cos.close();

                    // Save the results and ensure that the document is properly closed:
                    document.save(outputFileName);
                    document.close();

                    Modulhelferlein.Infomeldung("Liste der Buchprojekte ist als PDF gespeichert!");

                    try { //Ausgabe der Liste
                        Runtime.getRuntime().exec("cmd.exe /c " + outputFileName);
                    } catch (IOException exept) {
                        Modulhelferlein.Fehlermeldung(
                                "Exception: " + exept.getMessage());
                    }//try Ausgabe der Liste

                } catch (SQLException exept) {
                    Modulhelferlein.Fehlermeldung("SQL-Exception: SQL-Anfrage nicht möglich: " + exept.getMessage());
                } catch (IOException e) {
                    Modulhelferlein.Fehlermeldung("IO-Exception: " + e.getMessage());
                } //SQL-Anfrage

            } //if verbindung steht

        } catch (IOException e) {
            Modulhelferlein.Fehlermeldung("FileNotFoundException: " + e.getMessage());
        } //try Dokument erstellen

    } //void bericht

} //class

