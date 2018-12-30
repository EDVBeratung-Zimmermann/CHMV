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
 * Klasse zur Erzeugung einer Rezensionsübersicht
 *
 * @author Thomas Zimmermann
 *
 */
public class berRezensionen {

    /**
     * Erzeugt die Rezensionsübersicht
     *
     * @param Typ
     * @param Format
     */
    public static void bericht(String Typ, String Format, String Buch) {
        Connection conn = null;
        Statement SQLAnfrage = null; // Anfrage erzeugen
        Statement SQLAdresse = null;
        Statement SQLRezension = null;
        Statement SQLBuch = null;

        ResultSet result = null;
        ResultSet resultAdresse = null;
        ResultSet resultRezension = null;
        ResultSet resultBuch = null;

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
            Modulhelferlein.Fehlermeldung("SQL-Exception: Verbindung zur Datenbank nicht moeglich: "
                    + exept.getMessage());
        }

        final Connection conn2 = conn;

        if (conn2 != null) {
            try {
                SQLAnfrage = conn2.createStatement();
                SQLAdresse = conn2.createStatement();
                SQLRezension = conn2.createStatement();

                switch (Typ) {
                    case "Buch":
                        SQLBuch = conn2.createStatement(); // Anfrage der DB conn2 zuordnen
                        resultBuch = SQLBuch.executeQuery("SELECT * FROM TBL_BUCH WHERE BUCH_ID = '" + Buch + "'");
                        resultBuch.next();
                        System.out.println("... resultSet Buch ist angelegt");
                        String BuchTitel = resultBuch.getString("BUCH_TITEL");
                        String BuchISBN = resultBuch.getString("BUCH_ISBN");

                        String[] Autoren = resultBuch.getString("BUCH_AUTOR").split(",");
                        System.out.println("... das Buch hat " + Integer.toString(Autoren.length) + " Autoren");
                        String SQL = "SELECT * FROM TBL_ADRESSE WHERE ADRESSEN_ID = '" + Autoren[0] + "'";
                        String Autor = "'" + Autoren[0] + "'";
                        for (int i = 1; i < Autoren.length; i++) {
                            Autor = Autor + " OR " + "'" + Autoren[i] + "'";
                            SQL = SQL + " OR " + " ADRESSEN_ID = '" + Autoren[i] + "'";
                        }
                        System.out.println("... erzeuge Autorenliste für " + Autor);
                        System.out.println(SQL);
                        resultAdresse = SQLAdresse.executeQuery(SQL);
                        System.out.println("... result-Set Adressen erzeugt");
                        String Autorennamen = "";
                        while (resultAdresse.next()) {
                            Autorennamen = Autorennamen + resultAdresse.getString("ADRESSEN_NAME") + ", " + resultAdresse.getString("ADRESSEN_VORNAME") + "; ";
                        }
                        System.out.println("... die Autoren sind " + Autorennamen);

                        result = SQLAnfrage.executeQuery("SELECT * FROM TBL_REZENSIONEN_AUS_DETAIL WHERE REZENSIONEN_AUS_DETAIL_BUCH='" + Buch + "'");

                        switch (Format) {
                            case "PDF":
                                PDDocument document = new PDDocument();
                                PDPage page1 = new PDPage(A4);
                                document.addPage(page1);

                                // Start a new content stream which will "hold" the to be created content
                                PDPageContentStream cos;

                                cos = new PDPageContentStream(document, page1);
                                PDDocumentInformation docInfo = document.getDocumentInformation();

                                docInfo.setSubject("Rezensionen");
                                docInfo.setTitle("miles-Verlag Stammdaten");
                                docInfo.setAuthor("miles-Verlag");
                                docInfo.setCreationDate(Calendar.getInstance());
                                docInfo.setCreator("miles-Verlag");
                                docInfo.setProducer("miles-Verlag");

                                PDFont fontPlain = PDType1Font.HELVETICA;
                                PDFont fontBold = PDType1Font.HELVETICA_BOLD;

                                Integer zeile = 0;
                                Integer seite = 1;
                                Integer Datensatz =1;

                                Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Bewegungsdaten");
                                Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 755, "Übersicht der Rezensionen, Stand: "
                                        + Modulhelferlein.printSimpleDateFormat(
                                                "dd.MM.yyyy"));

                                Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 735, "Buch: " + BuchTitel);
                                Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 720, "Autor: " + Autorennamen);
                                Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 705, "ISBN: " + BuchISBN);
                                Ausgabe(cos, fontBold, 12, Color.BLACK, 455, 705, "Seite: " + Integer.toString(seite));

                                Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 685, "Datum");
                                Ausgabe(cos, fontBold, 12, Color.BLACK, 120, 685, "Rezensent/Zeitschrift");
                                Ausgabe(cos, fontBold, 12, Color.BLACK, 300, 685, "Adresse");
                                Linie(cos, 2, 56, 678, 539, 678);
                                System.out.println("... Kopfzeile ist geschrieben");

                                while (result.next()) {
                                    //Ausgabe(cos, font, size, color, xx, yy, text);
                                    resultRezension = SQLRezension.executeQuery("SELECT * FROM TBL_REZENSIONEN_AUS WHERE REZENSIONEN_AUS_NUMMER = '" + result.getString("REZENSIONEN_AUS_DETAIL_NUMMER") + "'");
                                    resultRezension.next();
                                    String Datum = resultRezension.getString("REZENSIONEN_AUS_DATUM");
                                    String Rezensent = resultRezension.getString("REZENSIONEN_AUS_REZENSENT");
                                    String ZielZeitschrift = resultRezension.getString("REZENSIONEN_AUS_ZEITSCHRIFT");

                                    resultAdresse = SQLBuch.executeQuery("SELECT * FROM TBL_ADRESSE WHERE ADRESSEN_ID = '" + Rezensent + "'");
                                    resultAdresse.next();
                                    Rezensent = Modulhelferlein.makeAnrede(resultAdresse.getString("ADRESSEN_NAMENSZUSATZ"), resultAdresse.getString("ADRESSEN_VORNAME"), resultAdresse.getString("ADRESSEN_NAME"));
                                    
                                    String Zeile1 = resultAdresse.getString("ADRESSEN_ZUSATZ_1");
                                    String Zeile3 = resultAdresse.getString("ADRESSEN_ZUSATZ_2"); 
                                    String Zeitschrift = resultAdresse.getString("ADRESSEN_ZEITSCHRIFT");

                                    resultAdresse = SQLBuch.executeQuery("SELECT * FROM TBL_ADRESSE WHERE ADRESSEN_ID = '" + ZielZeitschrift + "'");
                                    resultAdresse.next();
                                    ZielZeitschrift = resultAdresse.getString("ADRESSEN_ZEITSCHRIFT");

                                    Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 665 - zeile * 15, Datum);
                                    Ausgabe(cos, fontBold, 12, Color.BLACK, 120, 665 - zeile * 15, Rezensent);
                                    Ausgabe(cos, fontBold, 12, Color.BLACK, 300, 665 - zeile * 15, Zeile1);
                                    zeile = zeile + 1;
                                    Ausgabe(cos, fontBold, 12, Color.BLACK, 120, 665 - zeile * 15, Zeitschrift);

                                    if (Zeile3.equals(ZielZeitschrift)) {
                                    } else {
                                        Ausgabe(cos, fontBold, 12, Color.BLACK, 300, 665 - zeile * 15, Zeile3);
                                        Ausgabe(cos, fontBold, 12, Color.BLACK, 120, 665 - zeile * 15, Zeitschrift);
                                        if (ZielZeitschrift.equals("----------") || ZielZeitschrift.equals("") || ZielZeitschrift.equals(" ")) {
                                    
                                        } else {
                                            zeile = zeile + 1;
                                            Ausgabe(cos, fontBold, 12, Color.BLACK, 120, 665 - zeile * 15, "gebeten für eine Rezension in " + ZielZeitschrift);
                                        }
                                    }
                                    System.out.println("Zeile " + Integer.toString(zeile) + ": " + Datum + ", " + Rezensent + ", " + Zeile1 +  ", " + Zeile3 +  ", " + Zeitschrift +  ", " + ZielZeitschrift);

                                    zeile = zeile + 2;
                                    Datensatz = Datensatz + 1;

                                    if (Datensatz > 10) {
                                        Datensatz = 1;
                                        zeile = 1;
                                        seite = seite + 1;
                                        cos.close();
                                        PDPage page = new PDPage(A4);
                                        document.addPage(page);
                                        cos = new PDPageContentStream(document, page);

                                        Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 770, "miles-Verlag Verlagsverwaltung - Bewegungsdaten");
                                        Ausgabe(cos, fontBold, 12, Color.BLACK, 56, 755, "Übersicht der Rezensionen, Stand: "
                                                + Modulhelferlein.printSimpleDateFormat(
                                                        "dd.MM.yyyy"));

                                        Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 735, "Buch: " + BuchTitel);
                                        Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 720, "Autor: " + Autorennamen);
                                        Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 705, "ISBN: " + BuchISBN);
                                        Ausgabe(cos, fontBold, 12, Color.BLACK, 455, 705, "Seite: " + Integer.toString(seite));

                                        Ausgabe(cos, fontBold, 12, Color.BLACK, 55, 685, "Datum");
                                        Ausgabe(cos, fontBold, 12, Color.BLACK, 120, 685, "Rezensent/Zeitschrift");
                                        Ausgabe(cos, fontBold, 12, Color.BLACK, 300, 685, "Adresse");
                                        Linie(cos, 2, 56, 678, 539, 678);
                                    } // if neue Seite
                                } // while
                                cos.close();

                                // Save the results and ensure that the document is properly closed:
                                String outputFileName = Modulhelferlein.pathBerichte + "\\Rezensionen\\"
                                        + "Rezension-"
                                        + BuchISBN + "-"
                                        + Modulhelferlein.printSimpleDateFormat("yyyyMMdd") + "." + Format;

                                document.save(outputFileName);
                                document.close();

                                Modulhelferlein.Infomeldung("Rezensionsbericht als PDF gespeichert!");
                                try {
                                    Runtime.getRuntime().exec("cmd.exe /c " + "\"" + outputFileName + "\"");
                                } catch (IOException exept) {
                                    Modulhelferlein.Fehlermeldung("Exception: " + exept.getMessage());
                                }

                                break;
                            case "XLS":
                                Modulhelferlein.Infomeldung("Noch nicht implementiert");
                                break;
                            case "DOC":
                                Modulhelferlein.Infomeldung("Noch nicht implementiert");
                                break;
                        }
                        break;
                    case "Zeitschrift":
                        result = SQLAnfrage.executeQuery("SELECT * FROM TBL_REZENSIONEN_AUS SORT BY EZENSION_ZEITSCHRIFT");
                        Modulhelferlein.Infomeldung("Noch nicht implementiert");
                        break;
                    case "Rezensent":
                        result = SQLAnfrage.executeQuery("SELECT * FROM TBL_REZENSIONEN_AUS SORT BY REZENSION_REZENSENT");
                        Modulhelferlein.Infomeldung("Noch nicht implementiert");
                        break;
                }

            } catch (SQLException exept) {
                Modulhelferlein.Fehlermeldung("SQL-Exception", "Verbindung zur Datenbank nicht moeglich: ", exept.getMessage());
            } catch (IOException ex) {
                Modulhelferlein.Fehlermeldung("IO-Exception", ex.getMessage());
            }
        }
        //------------------------------------------------------------------------------        
        // Create a document and add a page to it

    } //void

} //class
