/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
        ResultSet result;

        if (JOptionPane.showConfirmDialog(null,
                "Soll der Jahreswechsel wirklich getan werden?",
                "Bestätigung",
                JOptionPane.YES_NO_OPTION) == 0) {

        // Sicherungskopie der gesamten Datenbank mit neuem Namen
            String cmdline = "C:\\xampp\\mysql\\bin\\mysqldump.exe -P3063 -uroot -pclausewitz milesverlag > "
                    + "\""
                    + Modulhelferlein.pathSicherung
                    + "\""
                    + "\\"
                    + "miles-verlag."
                    + Modulhelferlein.printSimpleDateFormat("yyyy") 
                    + "Jahressicherung"
                    + ".sql";
            try {
                Runtime.getRuntime().exec("cmd /c " + cmdline);
                Modulhelferlein.Infomeldung("Datenbank wurde gesichert!");
            } catch (IOException e) {
                Modulhelferlein.Fehlermeldung("IO-Exception: " + e.getMessage());
            }
            System.out.println("Jahressicherung ist erstellt");

        // Bestellnummer zurücksetzen
            conn = null;

            // Datenbank-Treiber laden
            try {
                Class.forName(Modulhelferlein.dbDriver);
            } catch (ClassNotFoundException exept) {
                Modulhelferlein.Fehlermeldung("Treiber nicht gefunden.");
            }

            // Verbindung zur Datenbank über die JDBC-Brücke
            try {
                conn = DriverManager.getConnection(Modulhelferlein.dbUrl, Modulhelferlein.dbUser, Modulhelferlein.dbPassword);
            } catch (SQLException exept) {
                Modulhelferlein.Fehlermeldung("Verbindung zur Datenbank nicht moeglich.");
            }

            // final Connection conn2=conn;
            if (conn != null) {
                SQLAnfrage = null;    // Anfrage erzeugen

                try {
                    SQLAnfrage = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);    // Anfrage der DB conn2 zuordnen
                    result = SQLAnfrage.executeQuery("SELECT * FROM TBL_BESTELLNR");    // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert

                    // gehe zum ersten Datensatz - wenn nicht leer
                    if (result.next()) {
                        Boolean resultIsEmpty = false;

                        result.updateString("BESTELLNR_NUMMER", "0");
                        result.updateRow();
                    }

                    System.out.println("Bestellnummer ist zurückgesetzt");

                // Tabellen zurücksetzen
                
                // Einnahmen
                    String SQL = "DELETE FROM TBL_EINNAHMEN WHERE EINNAHMEN_BEZAHLT <> '01.01.1970'";
                    SQLAnfrage.executeUpdate(SQL);

                    System.out.println("Tabelle Einnahmen ist zurückgesetzt");

                // Ausgaben
                    SQL = "DELETE FROM TBL_AUSGABEN WHERE AUSGABEN_BEZAHLT <> '01.01.1970'";
                    SQLAnfrage.executeUpdate(SQL);
                    
                    System.out.println("Tabelle Ausgaben ist zurückgesetzt");

                // Bestellungen + BestellDetails
                    SQLAnfrage = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);    
                    result = SQLAnfrage.executeQuery("SELECT * FROM TBL_BESTELLUNG WHERE BESTELLUNG_BEZAHLT <> '01.01.1970'");    

                    // gehe zum ersten Datensatz - wenn nicht leer
                    if (result.first()) {
                        while (result.next()) {
                            String RechNr = result.getString("BESTELLUNG_RECHNR");
                            SQL = "DELETE FROM TBL_BESTELLUNG_DETAIL WHERE BESTELLUNG_DETAIL_RECHNR = '" + RechNr + "'";
                            SQLAnfrage.executeUpdate(SQL);
                            
                            result.deleteRow();
                        }
                    }
                    
                    System.out.println("Tabelle Bestellungen ist zurückgesetzt");

                } catch (SQLException exept) {
                    Modulhelferlein.Fehlermeldung("SQL-Exception: SQL-Anfrage nicht moeglich. "
                            + exept.getMessage());

                    // System.exit(1);
                }    // try
            }        // if
        }

    }
}
