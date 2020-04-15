/*
 *
 * Das JAVA-Programm Miles-Verlag Verlagsverwaltung stellt alle notwendigen
 * Funktionen für die Verwaltung des Carola Hartman Miles-Verlags bereit.
 *
 * Copyright (C) 2017 EDV-Beratung und Betrieb, Entwicklung von Software
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
import javax.swing.JOptionPane;

/**
 *
 * @author thoma
 */
public class ModulJahresabschluss {

    public static void Jahresabschluss() {

        Connection conn;
        Statement SQLAnfrage;
        Statement SQLAnfrageDetail;
        ResultSet result;
        
        if (JOptionPane.showConfirmDialog(null,
                "Soll der Jahreswechsel mit dem \n"
                        + "Zurücksetzen der Datenbanken\n"
                        + " - Bestellnummer\n"
                        + " - Einnahmen\n"
                        + " - Ausgaben\n"
                        + " - Bestellung\n"
                        + " - Bestelldetails\n"
                        + "wirklich durchgeführt werden?",
                "Bestätigung",
                JOptionPane.YES_NO_OPTION) == 0) {

            // Sicherungskopie der gesamten Datenbank mit neuem Namen
            String Jahr = String.valueOf(Integer.parseInt(ModulHelferlein.printSimpleDateFormat("yyyy")) - 1);
            String cmdline = "C:\\xampp\\mysql\\bin\\mysqldump.exe -P3063 -uroot -pclausewitz milesverlag > "
                    + "\""
                    + ModulHelferlein.pathSicherung
                    + "\""
                    + "\\"
                    + "miles-verlag."
                    + Jahr
                    + ".Jahressicherung"
                    + ".sql";
            try {
                Runtime.getRuntime().exec("cmd /c " + cmdline);
                ModulHelferlein.Infomeldung("Jahressicherung " + Jahr + " wurde erstellt!");
                System.out.println("Jahressicherung ist erstellt");
            } catch (IOException e) {
                ModulHelferlein.Fehlermeldung("IO-Exception: " + e.getMessage());
            }

            // Bestellnummer zurücksetzen
            conn = null;

            // Datenbank-Treiber laden
            try {
                Class.forName(ModulHelferlein.dbDriver);
            } catch (ClassNotFoundException exept) {
                ModulHelferlein.Fehlermeldung("Treiber nicht gefunden.");
            }

            // Verbindung zur Datenbank über die JDBC-Brücke
            try {
                conn = DriverManager.getConnection(ModulHelferlein.dbUrl, ModulHelferlein.dbUser, ModulHelferlein.dbPassword);
            } catch (SQLException exept) {
                ModulHelferlein.Fehlermeldung("Verbindung zur Datenbank nicht moeglich.");
            }

            // final Connection conn2=conn;
            if (conn != null) {
                
                try {
                    SQLAnfrage = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);    // Anfrage der DB conn2 zuordnen
                    result = SQLAnfrage.executeQuery("SELECT * FROM TBL_BESTELLNR");    // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert

                    // gehe zum ersten Datensatz - wenn nicht leer
                    if (result.next()) {
                        Boolean resultIsEmpty = false;

                        result.updateString("BESTELLNR_NUMMER", "1");
                        result.updateRow();
                    }
                    ModulHelferlein.Infomeldung("Bestellnummer ist zurückgesetzt!");
                    System.out.println("Bestellnummer ist zurückgesetzt");

                    // Tabellen zurücksetzen
                    // Einnahmen
                    SQLAnfrage.executeUpdate("DELETE FROM TBL_EINNAHMEN WHERE EINNAHMEN_BEZAHLT <> '1970-01-01'");

                    System.out.println("Tabelle Einnahmen ist zurückgesetzt");
                    ModulHelferlein.Infomeldung("Tabelle Einnahmen ist zurückgesetzt!");

                    // Ausgaben
                    SQLAnfrage.executeUpdate("DELETE FROM TBL_AUSGABEN WHERE AUSGABEN_BEZAHLT <> '1970-01-01'");

                    System.out.println("Tabelle Ausgaben ist zurückgesetzt");
                    ModulHelferlein.Infomeldung("Tabelle Ausgaben ist zurückgesetzt!");

                    // Bestellungen + BestellDetails
                    SQLAnfrage = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    SQLAnfrageDetail = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    result = SQLAnfrage.executeQuery("SELECT * FROM TBL_BESTELLUNG WHERE BESTELLUNG_BEZAHLT <> '1970-01-01'");

                    // gehe zum ersten Datensatz - wenn nicht leer
                    while (result.next()) {
                        // Suche Bestellungdetails und lösche diese
                        String RechNr = result.getString("BESTELLUNG_RECHNR");
                        System.out.println("Bearbeite bezahlte Bestellung "+RechNr);
                        SQLAnfrageDetail.executeUpdate("DELETE FROM TBL_BESTELLUNG_DETAIL WHERE BESTELLUNG_DETAIL_RECHNR = '" + RechNr + "'");
                    } // while
                    SQLAnfrage.executeUpdate("DELETE FROM TBL_BESTELLUNG WHERE BESTELLUNG_BEZAHLT <> '1970-01-01'");
                    System.out.println("Tabelle Bestellungen ist zurückgesetzt");
                    ModulHelferlein.Infomeldung("Tabelle Bestellungen ist zurückgesetzt!");
                    
                } catch (SQLException exept) {
                    ModulHelferlein.Fehlermeldung("SQL-Exception: SQL-Anfrage nicht moeglich. "
                            + exept.getMessage());
                } // try catch
            }  // if conn != null
        } // if optionPane
    } // void
} // class
