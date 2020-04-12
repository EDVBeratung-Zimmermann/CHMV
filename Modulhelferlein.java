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
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
//import org.apache.pdfbox.encoding.Encoding;
//import org.apache.pdfbox.encoding.EncodingManager;
//import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

/**
 * Klasse mit Hilfsroutinen und globalen Variablen
 *
 * @author Thomas Zimmermann
 *
 */
public class Modulhelferlein {

    // instance variables - replace the example below with your own
    public static String Trenner = "~#!#~";
    //public static String Trenner = "\n";
    //public static String Trenner = "\\u000A";
    //public static String Trenner = System.getProperty("line.separator");
    
    public static char ctrlA = 0x1;
    public static char ctrlB = 0x2;
    public static char ctrlC = 0x3;
    public static char ctrlD = 0x4;
    public static char ctrlE = 0x5;
    public static char ctrlF = 0x6;
    public static char ctrlG = 0x7;
    public static char ctrlH = 0x8;
    public static char ctrlI = 0x9;
    public static char ctrlJ = 0x10;
    public static char ctrlK = 0x11;
    public static char ctrlL = 0x12;
    public static char ctrlM = 0x13;
    public static char ctrlN = 0x14;
    public static char ctrlO = 0x15;
    public static char ctrlP = 0x16;
    public static char ctrlQ = 0x17;
    public static char ctrlR = 0x18;
    public static char ctrlS = 0x19;
    public static char ctrlT = 0x20;
    public static char ctrlU = 0x21;
    public static char ctrlV = 0x22;
    public static char ctrlW = 0x23;
    public static char ctrlX = 0x24;
    public static char ctrlY = 0x25;
    public static char ctrlZ = 0x26;

    public static String Semaphore = "NutzerAktiv.srv";

    public static String pathUserDir = "";
    public static String pathRechnungen = "";
    public static String pathBerichte = "";
    public static String pathUmsaetze = "";
    public static String pathRezensionen = "";
    public static String pathEinnahmen = "";
    public static String pathAusgaben = "";
    public static String pathSicherung = "";
    public static String pathKonfiguration = "";
    public static String pathBuchprojekte = "";
    public static String pathSteuer = "";
    public static String pathQuelle = "";
    public static String pathZiel = "";
    public static String pathBenutzer = "";
    public static String CHMVBenutzer = "";
    public static String dbDriver = "com.mysql.jdbc.Driver";
    public static String dbUrl = "";
    public static String dbPort = "";
    public static String dbName = "";
    public static String dbUser = "";
    public static Boolean dbLive = true;
    public static String dbPassword = "";
    public static String MailHost = "";
    public static String MailPort = "";
    public static String MailIMAPHost = "";
    public static String MailIMAPPort = "";
    public static String MailIMAPGesendet = "";
    public static String MailUser = "";
    public static String MailPass = "";
    public static Float USD = 0F;
    public static Float GBP = 0F;
    public static Float CHF = 0F;
    public static Float NOK = 0F;
    public static Float ILS = 0F;
    public static Float DKK = 0F;
    public static Float CAD = 0F;
    public static java.text.DecimalFormatSymbols germany = new java.text.DecimalFormatSymbols(java.util.Locale.GERMANY);
    public static DecimalFormat df = new DecimalFormat("##,##0.00", germany); //new java.text.DecimalFormat( "##,###.00", germany );
    public static Date CurDate = new Date();
    public static String LeereListe[] = {""};
    public static String MonatListe[] = {
        "----------", "Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober",
        "November", "Dezember"
    };
    public static String QuartalListe[] = {"----------", "1. Quartal", "2. Quartal", "3. Quartal", "4. Quartal"};
    public static String AdressTypListe[] = {
        "Autor", "Kunde", "Rezensent", "Druckerei", "Lieferant", "Zeitschrift", "Sonstige"
    };
    public static String EinnahmenTypListe[] = {"Kundenbestellungen", // 0
        "Margenabrechnungen", // 1
        "Druckkostenzuschüsse", // 2
        "Auszahlung VG Wort", // 3
        "Sonstige"};            // 4
    public static String AusgabenTypListe[] = {
        "Porto-Kosten", "Büromaterial", "Buchbestellungen", "Honorarabrechnung", "Kontoführung", "Telefonkosten",
        "Büro- und Lagerräume inkl. Nebenkosten", "Bucherstellung", "Fahrtkosten", "Sonstige"
    };
    public static String[] Laender = {"", "DEUTSCHLAND",
        "BELGIEN", "FRANKREICH", "IRLAND", "ITALIEN", "NIEDERLANDE", "ÖSTERREICH", "PORTUGAL",
        "BULGARIEN", "DÄNEMARK", "ESTLAND", "FINNLAND", "GRIECHENLAND", "KROATIEN",
        "LETTLAND", "LITAUEN", "LUXEMBURG", "MALTA", "POLEN", "RUMÄNIEN", "SCHWEDEN",
        "SLOWAKEI", "SLOWENIEN", "SPANIEN", "TSCHECHIEN", "UNGARN", "ZYPERN", "GROßBRITANNIEN",
        "SCHWEIZ"};
    // in ERechnung die Ländercodes ergänzen!

    public static String[] SpracheRechnung = {"Rechnung", "Invoice"};
    public static String[] SpracheMahnung = {"Mahnung", "Reminder"};
    public static String[] SpracheGeschenk = {"Werbeexemplar", "Sales campaign"};
    public static String[] SpracheRezensionsexemplar = {"Rezensionsexemplar", "For Review"};
    public static String[] SpracheRemittende = {"Remittende", "Replacement"};
    public static String[] SpracheBelegexemplar = {"Belegexemplar", "Author's copy"};
    public static String[] SprachePflichtexempar = {"Pflichtexemplar", ""};
    public static String[] SpracheRechnungsnummer = {"Rechnungsnummer", "Invoice No."};
    public static String[] SpracheRechnungsdatum = {"Rechnungsdatum", "Date of Invoice"};
    public static String[] SpracheBestellzeichen = {"Ihr Bestellzeichen", "Your Sign of order"};
    public static String[] SpracheVersanddatum = {"Versanddatum = Rechnungsdatum", "Date of Shipment = Date of Invoice"};
    public static String[] SpracheBestelldatum = {"Ihre Buchbestellung vom", "Your order from"};
    public static String[] SpracheUstrID = {"Ihre UStr-IDNr", "Your VAT-TaxNo"};
    public static String[] SpracheAnrede = {"Sehr geehrte Damen und Herren,", "Dear Madam, dear Sir,"};
    public static String[] SpracheEinleitung1 = {"für Ihre Bestellung danken wir Ihnen sehr herzlich und würden uns freuen, wenn Sie uns",
        "thank you very much for your order."};
    public static String[] SpracheEinleitung2 = {"einmal auf unserer Homepage www.miles-verlag.jimdo.com besuchen.",
        "We would appreciate your visit of our website at www.miles-verlag.jimdo.com"};
    public static String[] SpracheEinMahnung1 = {"für Ihre Bestellung danken wir Ihnen sehr herzlich. ",
        "thank you very much for your order."};
    public static String[] SpracheEinMahnung2 = {"Leider konnten wir bisher keinen Zahlungseingang verzeichnen.",
        "Unfortunately we could not identify any payments."};
    public static String[] SpracheAnzahl = {"Anzahl", "Amount"};
    public static String[] SpracheAutor = {"ISBN/Autor/Herausgeber", "ISBN/Author/Editor"};
    public static String[] SpracheTitel = {"Titel", "Title"};
    public static String[] SpracheEinzelpreis = {"Einzelpreis", "Unit price"};
    public static String[] SpracheGesamtpreis = {"Gesamtpreis", "Total price"};
    public static String[] SpracheVersandkosten = {"Versandkosten", "Shipping costs"};
    public static String[] SpracheUmsatzsteuer = {"Umsatzsteuer", "Value added Tax"};
    public static String[] SpracheNetto = {"Nettogesamtbetrag", "Subtotal without VAT"};
    public static String[] SpracheGesamtVersand = {"Gesamtkosten inkl. Versand", "Total price incl. shipping costs"};
    public static String[] SpracheReverseCharge = {"Reverse Charge - Steuerschuldnerschaft des Leistungsempfängers", "Reverse Charge"};
    public static String[] SpracheEULieferung = {"Innergemeinschaftliche Lieferung", "Intra-EU Export"};
    public static String[] SpracheSteuerfrei1 = {"Steuerfreie Ausfuhrlieferung, da es sich um eine", "Tax free export"};
    public static String[] SpracheSteuerfrei2 = {"nicht im Inland steuerbare Leistung handelt.", ""};
    public static String[] SpracheStorniert = {"storniert", "canceled"};
    public static String[] SpracheSchluss1 = {"Den Rechnungsbetrag von", "Please transfer the amount of"};
    public static String[] SpracheSchluss21 = {" bitten wir innerhalb von ", " on the Invoice within "};
    public static String[] SpracheSchluss22 = {" Tagen auf unser Konto", " days to our bank account"};
    public static String[] SpracheSchluss3 = {"bei der Berliner Volksbank zu überweisen.", "at the Berliner Volksbank."};
    public static String[] SpracheVerrechnung1 = {"Den Rechnungsbetrag von", "The amount of"};
    public static String[] SpracheVerrechnung2 = {"verrechnen wir mit der kommenden Honorarabrechnung", "will be charged with your author's fees"};
    public static String[] SpracheVerrechnung3 = {"für das aktuelle Geschäftsjahr.", "for the current year."};
    public static String[] SpracheGruss = {"Mit freundlichen Grüßen", "Yours sincerely"};
    public static String[] SpracheBeruf = {"Diplom Kauffrau", "Master of Science in Business Administration"};
    public static String[] SpracheAnlage1 = {"Dite Details Ihrer Bestellung entnehmen Sie bitte der Anlage.", "Please find the details of your order on the next page."};
    public static String[] SpracheAnlage2 = {"Anlage Bestelldetails", "Annex order"};
    public static String[] Sprache = {"", ""};

    /**
     * Constructor for objects of class helferlein
     */
    public Modulhelferlein() {    // initialise instance variables
    }

    /**
     *
     * @param ISBN ISBN-Nummer
     * @return
     */
    public static String makeISBN13(String ISBN) {
        String ISBN13 = ISBN.substring(0, 3) + "-"
                + ISBN.substring(3, 4) + "-"
                + ISBN.substring(4, 9) + "-"
                + ISBN.substring(9, 12) + "-"
                + ISBN.substring(12, 13);
        return ISBN13;
    }

    /**
     *
     * @param dirName Verzeichnisname, das verifiziert/erzeugt werden soll
     * @return
     */
    public static boolean checkDir(String dirName) {
        File stats = new File(dirName);
        if (stats.exists()) // Überprüfen, ob es den Ordner gibt
        {
            return true;
        } else {
            if (stats.mkdir()) // Erstellen des Ordners
            {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     *
     * @param cos
     * @param Width
     * @param xs
     * @param ys
     * @param xe
     * @param ye
     */
    public static void Linie(PDPageContentStream cos, Integer Width, Integer xs, Integer ys, Integer xe, Integer ye) {
        try {
            cos.setLineWidth(Width);
            cos.moveTo(xs, ys);
            cos.lineTo(xe, ye);
            cos.closeAndStroke();
        } catch (IOException ex) {
            Logger.getLogger(Modulhelferlein.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Wandelt ein Datum um 2018-03-05 => 05.03.2018
     *
     * @param Eingabe SQL-Datum
     * @return normalisierter Datum
     */
    public static String SQLDate2Normal(String Eingabe) {
        String Ausgabe = "";
        Ausgabe = Eingabe.substring(8, 10) + "."
                + Eingabe.substring(5, 7) + "."
                + Eingabe.substring(0, 4);
        return Ausgabe;
    }

    public static String Normalisiere(String Input) {
        String Output = Input;
        int index = 0;
        String m_text = Input;
        char[] c = new char[Input.length()];
        int inside = 0;
        for (index = 0; index < Input.length(); index++) {
            char buchstabe = Input.charAt(index);
            if ("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".indexOf(buchstabe) >= 0) {
                continue; // Wird continue ausgelassen, werden nur die nur genau die Zeichen angezeigt wie in der if bedingung aufgelistet
            }
            c[inside++] = buchstabe;
        }
        return new String(c, 0, inside); // Gebe neuen String zurück, da Strings "immutable" sind.
    }

    /**
     *
     * @param cos
     * @param font
     * @param size
     * @param color
     * @param x
     * @param y
     * @param text
     * @param breite
     */
    public static void AusgabeLeistung(PDPageContentStream cos, PDFont font, int size, Color color, int x, int y, String text, Integer breite) {
        String hilfstext = text + " ENDE ENDE";
        String[] Gesamttext = hilfstext.split(" ");
        Integer woerter = Gesamttext.length;
//helferlein.Infomeldung("woerter - 1", Gesamttext[woerter-1]);
        System.out.println("Ausgabe Leistung: " + hilfstext + " mit " + Integer.toString(woerter) + " Wörter");
        Gesamttext[woerter - 1] = ""; //ENDE
        woerter = woerter - 2;
        String[] zeile = {"", "", ""};
        Integer i = 0;
        Integer laenge = 0;
        Integer ZeilenNr = 0;
        zeile[0] = Gesamttext[0];
        try {
            while ((i < woerter - 1) && (ZeilenNr < 3)) {
                zeile[ZeilenNr] = Gesamttext[i];
                laenge = Modulhelferlein.float2Int(font.getStringWidth(zeile[ZeilenNr] + " " + Gesamttext[i + 1]) / 1000 * size);
                while ((laenge < breite) && (zeile[ZeilenNr].length() < 90) && (i < woerter - 1)) {
                    zeile[ZeilenNr] = zeile[ZeilenNr] + " " + Gesamttext[i + 1];
                    i = i + 1;
                    laenge = Modulhelferlein.float2Int(font.getStringWidth(zeile[ZeilenNr] + " " + Gesamttext[i + 1]) / 1000 * size);
                }
//helferlein.Infomeldung(Float.toString(laenge) + " => " + zeile);                                
                i = i + 1;
//helferlein.Infomeldung(Float.toString(laenge) + " => " + zeile[ZeilenNr]);   
                ZeilenNr = ZeilenNr + 1;
            }

            cos.beginText();
            cos.setFont(font, size);
            cos.setNonStrokingColor(color);
            cos.newLineAtOffset(x, y);
            cos.showText(zeile[0]);
            cos.endText();

            cos.beginText();
            cos.setFont(font, size);
            cos.setNonStrokingColor(color);
            cos.newLineAtOffset(x, y - (size + 2));
            cos.showText(zeile[1]);
            cos.endText();
        } catch (IOException e) {
            Modulhelferlein.Fehlermeldung("Ausgabe Text", "IO-Exception: ", e.getMessage());
        }
    }

    /**
     * gibt einen Text an der Stelle x,y in der Größe size im Zeichensatz font
     * aus
     *
     * @param cos Contentstream
     * @param font Zeichensatz
     * @param size Zeichengröße
     * @param color
     * @param x x-Koordinate
     * @param y y-Koordinate
     * @param text auszugebender Text
     * @param breite
     */
    public static void Ausgabe(PDPageContentStream cos, PDFont font, int size, Color color, int x, int y, String text, Integer breite) {

        try {
            while ((font.getStringWidth(text) / 1000 * size) > breite) {
                text = text.substring(0, text.length() - 2);
            }
            cos.beginText();
            cos.setFont(font, size);
            cos.setNonStrokingColor(color);
            cos.newLineAtOffset(x, y);
            cos.showText(text);
            cos.endText();
        } catch (IOException e) {
            Modulhelferlein.Fehlermeldung("IO-Exception: " + e.getMessage());
        }
    }

    /**
     * gibt einen Text an der Stelle x,y in der Größe size im Zeichensatz font
     * aus
     *
     * @param cos Contentstream
     * @param font Zeichensatz
     * @param size Zeichengröße
     * @param color
     * @param x x-Koordinate
     * @param y y-Koordinate
     * @param text auszugebender Text
     */
    public static void Ausgabe(PDPageContentStream cos, PDFont font, int size, Color color, int x, int y, String text) {
        try {
            cos.beginText();
            cos.setFont(font, size);
            cos.setNonStrokingColor(color);
            cos.newLineAtOffset(x, y);
            cos.showText(text);
            cos.endText();
        } catch (IOException e) {
            Modulhelferlein.Fehlermeldung("IO-Exception: " + e.getMessage());
        }
    }

    /**
     * gibt einen Text an der Stelle x,y in der Größe size im Zeichensatz font
     * aus Rechtsbündig
     *
     * @param cos Contentstream
     * @param font Zeichensatz
     * @param size Zeichengröße
     * @param color
     * @param x x-Koordinate
     * @param y y-Koordinate
     * @param text auszugebender Text
     */
    public static void AusgabeRB(PDPageContentStream cos, PDFont font, int size, Color color, int x, int y, String text) {
        try {
            float tw = font.getStringWidth(text) / 1000 * size;
            cos.beginText();
            cos.setFont(font, size);
            cos.setNonStrokingColor(color);
            cos.newLineAtOffset(x - tw, y);
            cos.showText(text);
            cos.endText();
        } catch (IOException e) {
            Modulhelferlein.Fehlermeldung("IO-Exception: " + e.getMessage());
        }
    }

    private static void ausgabe(PDPageContentStream cos, PDFont font, int size, Color color, int x, int y, String text) {
        try {
            cos.beginText();
            cos.setFont(font, size);
            cos.setNonStrokingColor(color);
            cos.newLineAtOffset(x, y);
            cos.showText(text);
            cos.endText();
        } catch (IOException ex) {
            Logger.getLogger(Modulhelferlein.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Gibt einen Text an der Stelle x,y mit der Breite breite im Blocksatz aus
     *
     * @param cos
     * @param font
     * @param size
     * @param color
     * @param x
     * @param y
     * @param text
     * @param breite
     */
    public static void AusgabeBS(PDPageContentStream cos, PDFont font, int size, Color color, int x, int y, String text, Integer breite) {

        String[] wortliste = text.split(" ");
        int woerter = wortliste.length;
        try {
            int textbreite = (int) Math.round(font.getStringWidth(text) / 1000 * size);
            if (textbreite > breite) {
                System.out.println("Textbreite=" + Integer.toString(textbreite));
                ausgabe(cos, font, size, color, x, y, text);
            } else {
                textbreite = 0;
                for (int i = 0; i < woerter; i++) {
                    textbreite = textbreite + (int) Math.round(font.getStringWidth(wortliste[i]) / 1000 * size);
                }
                int leerraum = breite - textbreite;
                int abstand = 0;
                if (woerter==1) {
                    abstand=breite;
                } else {
                    abstand = (int) (leerraum / (woerter - 1));
                }
                if (abstand < 2) {
                    abstand = 2;
                }
                if (abstand > 20) {
                    abstand = 2;
                }
                int delta = leerraum - (woerter - 1) * abstand;
                if (text.indexOf("-", text.length()-1)>0) { // Zeile mit Trennzeichen
                    delta = delta + 1;
                }
                if (delta < 0) {
                    delta = 0;
                }
                System.out.println("Textbreite=" + Integer.toString(textbreite) + " Leeraum=" + Integer.toString(leerraum) + " Woerter=" + Integer.toString(woerter) + " Abstand=" + Integer.toString(abstand) + " Delta=" + Integer.toString(delta));
                //System.out.println("Blocksatzzeile bei y=" + Integer.toString(y));

                int startx = x;
                for (int i = 0; i < woerter; i++) {
                    //System.out.println("x= " + Integer.toString(startx) + wortliste[i]);
                    ausgabe(cos, font, size, color, startx, y, wortliste[i]);
                    startx = startx + (int) Math.round(font.getStringWidth(wortliste[i]) / 1000 * size) + abstand;
                    if (delta > 0) {
                        startx = startx + 1;
                        delta = delta - 1;
                    }
                }
            }
        } catch (IOException e) {
            Modulhelferlein.Fehlermeldung("IO-Exception: " + e.getMessage());
        }
    }

    public static void AusgabeZ(PDPageContentStream cos, PDFont font, int size, Color color, int x, int y, String text, Integer breite) {

        try {
            cos.beginText();
            cos.setFont(font, size);
            cos.setNonStrokingColor(color);
            int offset = (int) Math.round(breite - (font.getStringWidth(text) / 1000 * size)) / 2;
            cos.newLineAtOffset(x + offset, y);
            cos.showText(text);
            cos.endText();
        } catch (IOException e) {
            Modulhelferlein.Fehlermeldung("IO-Exception: " + e.getMessage());
        }
    }

    /**
     * gibt einen Text an der Stelle x,y in der Größe size im Zeichensatz font
     * aus Die Stelle x ist der Dezimalpunkt, der die Bündigkeit bestimmt
     *
     * @param cos Contentstream
     * @param font Zeichensatz
     * @param size Zeichengröße
     * @param color
     * @param x x-Koordinate
     * @param y y-Koordinate
     * @param text auszugebender Text
     */
    public static void AusgabeDB(PDPageContentStream cos, PDFont font, int size, Color color, int x, int y, String text) {
        if (text.contains(".")) {
            text = text.replace(".", ",");
        }
        String zahl[] = text.split(",");
        try {
            if (zahl.length == 2) {
                float tw = font.getStringWidth(zahl[0]) / 1000 * size;
                cos.beginText();
                cos.setFont(font, size);
                cos.newLineAtOffset(x - tw, y);
                cos.showText(zahl[0]);
                cos.endText();
                cos.beginText();
                cos.setFont(font, size);
                cos.newLineAtOffset(x, y);
                cos.showText(",");
                cos.endText();
                cos.beginText();
                cos.setFont(font, size);
                cos.newLineAtOffset(x + 4, y);
                cos.showText(zahl[1]);
                cos.endText();
            } else {
                float tw = font.getStringWidth(zahl[0] + zahl[1]) / 1000 * size;
                cos.beginText();
                cos.setFont(font, size);
                cos.newLineAtOffset(x - tw, y);
                cos.showText(zahl[0] + zahl[1]);
                cos.endText();
                cos.beginText();
                cos.setFont(font, size);
                cos.newLineAtOffset(x, y);
                cos.showText(",");
                cos.endText();
                cos.beginText();
                cos.setFont(font, size);
                cos.newLineAtOffset(x + 4, y);
                cos.showText(zahl[2]);
                cos.endText();
            }
        } catch (IOException e) {
            Modulhelferlein.Fehlermeldung("IO-Exception: " + e.getMessage());
        }
    }

    /**
     * Erstellt aus Titel, Vorname, Name einen String und reduziert die
     * Leerzeichen
     *
     * @param Titel
     * @param Vorname
     * @param Name
     * @return
     */
    public static String makeAnrede(String Titel, String Vorname, String Name) {
        String Ergebnis = "";
        if (Titel.equals("")) {
        } else {
            Ergebnis = Titel + " ";
        }
        if (Vorname.equals("")) {
        } else {
            Ergebnis = Ergebnis + Vorname + " ";
        }
        if (Name.equals("")) {
        } else {
            Ergebnis = Ergebnis + Name;
        }
        return Ergebnis;
    }

    /**
     * prüft, ob der übergebene String ein korrekter Integer ist
     *
     * @param s String
     * @return Double auf zwei Stellen gerundet
     */
    public static Integer checkNumberFormatInt(String s) {
        try {
            Integer i = Integer.parseInt(s);

            return i;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * prüft, ob der übergebene String ein korrekter Float ist
     *
     * @param s String
     * @return Float auf zwei Stellen gerundet
     */
    public static Float checkNumberFormatFloat(String s) {
        try {
            Float i = Float.parseFloat(s);

            return i;
        } catch (NumberFormatException e) {
            return (float) -1.0;
        }
    }

    /**
     * rundet einen Double-Wert auf zwei Dezimalstellen
     *
     * @param d Double
     * @return Double auf zwei Stellen gerundet
     */
    public static Double round2dec(final Double d) {

        // DecimalFormat f = new DecimalFormat("##,##");
        // Double toFormat = ((double)Math.round(d*100))/100;
        // return Double.valueOf(f.format(toFormat));
        return Math.round(d * 100) / 100.0;
    }

    /**
     * wandelt einen Double-Wert in einen String mit zwei Dezimalstellen
     *
     * @param d Double
     * @return String auf zwei Stellen gerundet
     */
    public static String str2dec(final Double d) {
        Double r = Math.round(d * 100) / 100.0;
        String s = Double.toString(r);

        if (s.indexOf(".") == s.length() - 2) {
            s = s + "0";
        }

        return s;
    }

    /**
     * wandelt einen Float-Wert in einen String mit zwei Dezimalstellen
     *
     * @param f Float
     * @return String auf zwei Stellen gerundet
     */
    public static String Fstr2dec(float f) {
        Double r = Math.round(f * 100) / 100.0;
        String s = Double.toString(r);

        if (s.indexOf(".") == s.length() - 2) {
            s = s + "0";
        }

        return s;
    }

    /**
     * Wandelt einen Double in einen Integer um und rundet nach oben
     *
     * @param d Double
     * @return Integer
     */
    public static int double2Int(Double d) {
        return (int) Math.round(d);
    }

    /**
     * Wandelt einen Float in einen Integer um und rundet nach oben
     *
     * @param f Float
     * @return Integer
     */
    public static int float2Int(Float f) {
        return (int) Math.round(f);
    }

    /**
     * Wandelt einen Float in einen Double mit zwei Nachkommastellen um
     *
     * @param f Float
     * @return Double
     */
    public static double round2dec(float f) {
        int r = Math.round(f * 100);

        return r / 100.0;
    }

    /**
     * Erzeugt das aktuelle Datum als String im Format dd.MM.yyyy
     *
     * @param df String Datumsformat
     * @return formatiertes Datum als String
     */
    public static String printSimpleDateFormat(final String df) {
        SimpleDateFormat formatter = new SimpleDateFormat(df);
        Date currentTime = new Date();

        return formatter.format(currentTime);    // 2012.04.14 - 21:34:07
    }

    /**
     * Erzeugt ein Datum als String mit einem bestimmten Format
     *
     * @param df Formatvorgabe
     * @param d Datum
     * @return formatiertes Datum als String
     */
    public static String printDateFormat(final String df, final Date d) {
        SimpleDateFormat formatter = new SimpleDateFormat(df);

        return formatter.format(d);    // 2012.04.14 - 21:34:07
    }

    /**
     * Erzeugt aus einem String im SQLDate-Format ein Java-Datum
     *
     * @param SQLDate String im SQLDate-Format
     * @return Java-Datum
     */
    public static Date SQLDateString2Date(final String SQLDate) {
        Date rDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        try {
            rDate = sdf.parse(SQLDate);
        } catch (ParseException e) {
            Modulhelferlein.Fehlermeldung("ParseException: " + e.getMessage());
        }

        return rDate;
    }

    /**
     * Erzeugt aus einem String im SQLDate-Format ein Java-Datum
     *
     * @param Date String im Datum-Format
     * @return Java-Datum
     */
    public static Date DateString2Date(final String Date) {
        Date rDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

        try {
            rDate = sdf.parse(Date);
        } catch (ParseException e) {
            Modulhelferlein.Fehlermeldung("ParseException: " + e.getMessage());
        }

        return rDate;
    }

    /**
     * Erzeugt aus einem SQLDate-Format ein Java-Datum
     *
     * @param SQLDate String im SQLDate-Format
     * @return Java-Datum
     */
    public static Date SQLDate2Date(java.sql.Date SQLDate) {
        java.util.Date javaDate = null;

        if (SQLDate != null) {
            javaDate = new Date(SQLDate.getTime());
        }

        return javaDate;
    }

    /**
     * Erzeugt aus einem Java-Datum ein SQL-Datum
     *
     * @param JavaDate
     * @return SQL-Datum
     */
    public static java.sql.Date Date2SQLDate(final java.util.Date JavaDate) {
        java.util.Calendar cal = Calendar.getInstance();

        cal.setTime(JavaDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        java.sql.Date sqlDate = new java.sql.Date(cal.getTime().getTime());

        return sqlDate;
    }

    /**
     * Meldet einen Fehler
     *
     * @param Meldung String auszugebender Fehler
     */
    public static void Fehlermeldung(final String Meldung) {
        Fehlermeldung(Meldung, "", "");
    }

    /**
     * Meldet einen Fehler
     *
     * @param Meldung1
     * @param Meldung2
     */
    public static void Fehlermeldung(final String Meldung1, String Meldung2) {
        Fehlermeldung(Meldung1, Meldung2, "");
    }

    /**
     * Meldet einen Fehler
     *
     * @param Meldung1
     * @param Meldung2
     * @param Meldung3
     */
    public static void Fehlermeldung(final String Meldung1, String Meldung2, String Meldung3) {
        // put your code here
        String Ausgabe = "";
        Ausgabe = Meldung1 + "\n" + Meldung2 + "\n" + Meldung3;
        JOptionPane.showMessageDialog(null, Ausgabe, "Achtung Fehler", JOptionPane.WARNING_MESSAGE);
        System.out.println(Meldung1 + " " + Meldung2 + " " + Meldung3);
    }

    /**
     * Meldet eine Information
     *
     * @param Meldung String auszugebende Information
     */
    public static void Infomeldung(final String Meldung) {
        Infomeldung(Meldung, "", "");
    }

    /**
     * Meldet eine Information
     *
     * @param Meldung1
     * @param Meldung2
     */
    public static void Infomeldung(final String Meldung1, String Meldung2) {
        Infomeldung(Meldung1, Meldung2, "");
    }

    /**
     * Meldet eine Information
     *
     * @param Meldung1
     * @param Meldung2
     * @param Meldung3
     */
    public static void Infomeldung(final String Meldung1, String Meldung2, String Meldung3) {

        // put your code here
        String Ausgabe = "";
        Ausgabe = Meldung1 + "\n" + Meldung2 + "\n" + Meldung3;
        JOptionPane.showMessageDialog(null, Ausgabe, "Information", JOptionPane.INFORMATION_MESSAGE);
        System.out.println(Meldung1 + " " + Meldung2 + " " + Meldung3);
    }

    /**
     * Prüft String auf Zeichen ausßerhalb des bekannten Zeichensatzes und passt
     * diesen an
     *
     * @param titel zu prüfender String
     * @return Angepasster String
     */
    public static String CheckStr(String titel) {
        /*
        char[] tc = titel.toCharArray();
        StringBuilder te = new StringBuilder();
        Encoding e;

        e.addCharacterEncoding(223, "a255");    // germandbls ß
        for (int i = 0; i < tc.length; i++) {
            Character c = tc[i];
            int code = 0;
            
            if (Character.isWhitespace(c)) {
                code = e.getCode("space");
            } else {
                code = e.getCode(e.getNameFromCharacter(c));
            }
            
            te.appendCodePoint(code);
        }

        return te.toString();
         */
        return titel;
    }

    /**
     *
     * @param Zeile1
     * @param Zeile2
     * @param Zeile3
     * @param Zeile4
     * @param Zeile5
     * @param Zeile6
     * @param Buch
     * @param Typ
     * @param Anzahl
     * @return
     */
    public static String makeBestellung(String Zeile1,
            String Zeile2,
            String Zeile3,
            String Zeile4,
            String Zeile5,
            String Zeile6,
            Integer Buch,
            Integer Typ,
            Integer Anzahl) {

        int imaxID;
        int imaxBID;
        int iBNr;
        String BestNrString = "";

        ResultSet iresultB = null;
        ResultSet iresultBD = null;
        ResultSet iresultBuch = null;

        Statement iSQLAnfrageB = null;
        Statement iSQLAnfrageBD = null;
        Statement iSQLAnfrageBNr = null;
        Statement iSQLAnfrageBuch = null;

        Connection conn = null;

        try {
            Class.forName(Modulhelferlein.dbDriver);
            conn = DriverManager.getConnection(Modulhelferlein.dbUrl, Modulhelferlein.dbUser, Modulhelferlein.dbPassword);

            if (conn != null) {

                iSQLAnfrageBD = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                iSQLAnfrageB = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                iSQLAnfrageBNr = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                iSQLAnfrageBuch = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

                // Bestellnummer lesen
                iresultB = iSQLAnfrageBNr.executeQuery("SELECT * FROM TBL_BESTELLNR WHERE BESTELLNR_ID = 1");
                iresultB.next();
                iBNr = iresultB.getInt("BESTELLNR_NUMMER");
                iresultB.updateInt("BESTELLNR_NUMMER", iBNr + 1);
                iresultB.updateRow();

                // Bestellungen bearbeiten
                iresultB = iSQLAnfrageB.executeQuery("SELECT * FROM TBL_BESTELLUNG ORDER BY BESTELLUNG_ID DESC");

                if (iresultB.first()) {
                    imaxID = iresultB.getInt("BESTELLUNG_ID");
                } else {
                    imaxID = 0;
                }

                iresultBD = iSQLAnfrageBD.executeQuery(
                        "SELECT * FROM TBL_BESTELLUNG_DETAIL ORDER BY BESTELLUNG_DETAIL_ID DESC");

                if (iresultBD.first()) {
                    imaxBID = iresultBD.getInt("BESTELLUNG_DETAIL_ID");
                } else {
                    imaxBID = 0;
                }

                BestNrString = Integer.toString(iBNr);

                while (BestNrString.length() < 3) {
                    BestNrString = "0" + BestNrString;
                }

                BestNrString = Modulhelferlein.printSimpleDateFormat("yyyyMMdd") + "-" + BestNrString;
                iresultB.moveToInsertRow();
                iresultB.updateInt("BESTELLUNG_TYP", Typ);
                iresultB.updateInt("BESTELLUNG_ID", imaxID + 1);
                iresultB.updateDate("BESTELLUNG_BEZAHLT", Modulhelferlein.Date2SQLDate(CurDate));
                iresultB.updateDate("BESTELLUNG_DATUM", Modulhelferlein.Date2SQLDate(CurDate));
                iresultB.updateInt("BESTELLUNG_KUNDE", -1);
                iresultB.updateInt("BESTELLUNG_ZAHLUNGSZIEL", 14);
                iresultB.updateString("BESTELLUNG_BESTNR", "ohne");
                iresultB.updateString("BESTELLUNG_USTR_ID", "");
                iresultB.updateString("BESTELLUNG_ZEILE_1", Zeile1);
                iresultB.updateString("BESTELLUNG_ZEILE_2", Zeile2);
                iresultB.updateString("BESTELLUNG_ZEILE_3", Zeile3);
                iresultB.updateString("BESTELLUNG_ZEILE_4", Zeile4);
                iresultB.updateString("BESTELLUNG_ZEILE_5", Zeile5);
                iresultB.updateString("BESTELLUNG_ZEILE_6", Zeile6);
                iresultB.updateString("BESTELLUNG_LINK", "");
                iresultB.updateString("BESTELLUNG_EMAIL", "");
                iresultB.updateString("BESTELLUNG_DHL", "");
                iresultB.updateFloat("BESTELLUNG_VERSAND", 0);
                iresultB.updateInt("BESTELLUNG_LAND", 0);
                iresultB.updateBoolean("BESTELLUNG_PRIVAT", false);
                iresultB.updateBoolean("BESTELLUNG_STORNIERT", false);
                iresultB.updateBoolean("BESTELLUNG_BESTAND", true);
                iresultB.updateBoolean("BESTELLUNG_BEZAHLUNG", false);
                iresultB.updateString("BESTELLUNG_RECHNR", BestNrString);
                iresultB.updateDate("BESTELLUNG_RECHDAT", Modulhelferlein.Date2SQLDate(CurDate));
                iresultB.updateBoolean("BESTELLUNG_TB", false);
                iresultB.updateString("BESTELLUNG_TEXT", "");

                iresultB.insertRow();
                iresultB.last();
                iresultB.updateRow();

                iresultBD.moveToInsertRow();
                iresultBD.updateInt("BESTELLUNG_DETAIL_ID", imaxBID + 1);
                iresultBD.updateFloat("BESTELLUNG_DETAIL_RABATT", 100F);
                iresultBD.updateBoolean("BESTELLUNG_DETAIL_SONST", false);
                iresultBD.updateString("BESTELLUNG_DETAIL_SONST_TEXT", "");
                iresultBD.updateFloat("BESTELLUNG_DETAIL_SONST_PREIS", 0F);
                iresultBD.updateInt("BESTELLUNG_DETAIL_BUCH", Buch);
                iresultBD.updateDate("BESTELLUNG_DETAIL_DATUM", Modulhelferlein.Date2SQLDate(CurDate));
                iresultBD.updateString("BESTELLUNG_DETAIL_RECHNR", BestNrString);
                iresultBD.updateInt("BESTELLUNG_DETAIL_ANZAHL", Anzahl);
                iresultBD.insertRow();
                iresultBD.last();
                iresultBD.updateRow();

                iresultBuch = iSQLAnfrageBuch.executeQuery("SELECT * FROM TBL_BUCH WHERE BUCH_ID = " + Integer.toString(Buch));
                iresultBuch.next();
                Integer Bestand = iresultBuch.getInt("BUCH_BESTAND") - Anzahl;
                iresultBuch.updateInt("BUCH_BESTAND", Bestand);
                iresultBuch.updateRow();

                iresultBuch.close();
                iresultB.close();
                iSQLAnfrageB.close();
                iresultBD.close();
                iSQLAnfrageBD.close();
                iSQLAnfrageBuch.close();
            }
        } catch (ClassNotFoundException ex) {
            Modulhelferlein.Fehlermeldung("Makebestellung", "ClassNotFound-Exception", ex.getMessage());
        } catch (SQLException ex) {
            Modulhelferlein.Fehlermeldung("Makebestellung", "SQL-Exception", ex.getMessage());
        }
        return BestNrString;
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
