/*
 *
 * Das JAVA-Programm Miles-Verlag Verlagsverwaltung stellt alle notwendigen
 * Funktionen f?r die Verwaltung des Carola Hartman Miles-Verlags bereit.
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

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.*;
import javax.swing.JFrame;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
//import org.apache.pdfbox.exceptions.COSVisitorException;

/**
 *
 * @author thomas
 */
public class CarolaHartmannMilesVerlag extends javax.swing.JFrame {

    private void backupDatenbank() {
        String cmdline = "C:\\xampp\\mysql\\bin\\mysqldump.exe -P3063 -uroot -pclausewitz milesverlag > "
                + "\""
                + Modulhelferlein.pathSicherung
                + "\""
                + "\\"
                + "miles-verlag.backup-"
                + Modulhelferlein.printSimpleDateFormat("yyyyMMdd") + ".sql";
        try {
            Runtime.getRuntime().exec("cmd /c " + cmdline);
            Modulhelferlein.Infomeldung("Datenbank wurde gesichert!");
        } catch (IOException e) {
            Modulhelferlein.Fehlermeldung("IO-Exception: " + e.getMessage());
        }
    }

    private void menuItemSoftwareverwaltungBenutzerActionPerformed(ActionEvent e) {
        // TODO add your code here
        VerwaltenDatenbankBenutzer.main(null);
    }

    /**
     * Creates new form CHMV
     */
    public CarolaHartmannMilesVerlag() {
        initComponents();

        this.setSize(650, 210);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.setContentPane(new ModulBackground("background.jpg"));
        
        ImageIcon img = new ImageIcon("CarolaHartmannMilesVerlag.png");
        this.setIconImage(img.getImage());

        Modulhelferlein.CurDate = new Date();

        Modulhelferlein.dbUrl = Modulhelferlein.dbUrl + ":" + Modulhelferlein.dbPort + "/" + Modulhelferlein.dbName;

        //prüfe, ob die Parameter stimmen:
        // 0    Adresse für den Datenbankserver
        // 1    Port für den Datenbankserver
        // 2    Name der Datenbank 
        // 3    Benutzername 
        // 4    Kennwort
        if (parameter != 5) {
            Modulhelferlein.Fehlermeldung("Die Anzahl der Parameter stimmt nicht!");
            System.exit(-1);
        }

        // prüfe, ob ein Nutzer aktiv ist
        File file = new File(Modulhelferlein.Semaphore);
        String Benutzer = null;
        if (file.canRead() || file.isFile()) {
            BufferedReader in = null;
            try {
                in = new BufferedReader(new FileReader(Modulhelferlein.Semaphore));
                Benutzer = in.readLine();
                System.out.println("aktiver Benutzer: " + Benutzer);
            } catch (IOException e) {
                Modulhelferlein.Fehlermeldung("Prüfe, ob Nutzer aktiv ist", "IOException", e.getMessage());
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        Modulhelferlein.Fehlermeldung("Prüfe, ob Nutzer aktiv ist", "IOException", e.getMessage());
                    }
                }
            }
            Modulhelferlein.Fehlermeldung("Das Programm wird bereits genutzt", Benutzer);
            System.exit(0);
        } else { // kein Nutzer aktiv
            // Datenbank-Treiber laden
            try {
                Class.forName(Modulhelferlein.dbDriver);
            } catch (ClassNotFoundException exept) {
                Modulhelferlein.Fehlermeldung("Carola Hartmann Miles-Verlag", "ClassNotFoundException: Treiber nicht gefunden: ", exept.getMessage());
                System.exit(1);
            }

            // Verbindung zur Datenbank über die JDBC-Brücke
            try {
                conn = DriverManager.getConnection(Modulhelferlein.dbUrl, Modulhelferlein.dbUser, Modulhelferlein.dbPassword);
            } catch (SQLException exept) {
                //Logger.getLogger(CarolaHartmannMilesVerlag.class.getName()).log(Level.SEVERE, "SQL-Exception: Verbindung zur Datenbank nicht moeglich: " + exept.getMessage(), exept);
                Modulhelferlein.Fehlermeldung("Carola Hartmann Miles-Verlag",
                        "SQL-Exception: Verbindung zur Datenbank nicht moeglich: ", exept.getMessage());
                // Starte XAMPP
                if (exept.getMessage().equals("Unknown database '" + Modulhelferlein.dbName + "'")) {
                    _DlgDatenbankErstellen.main(null);
                } else {
                    try {
                        Runtime.getRuntime().exec("C:/xampp/xampp-control.exe");
                    } catch (IOException e) {
                        //Logger.getLogger(CarolaHartmannMilesVerlag.class.getName()).log(Level.SEVERE, "IO-Exception: " + e.getMessage(), e);
                        Modulhelferlein.Fehlermeldung("IO-Exception: " + e.getMessage());
                    } // try
                } // if
            } // try

            // final Connection conn2=conn;
            if (conn != null) {
                SQLAnfrage = null; // Anfrage erzeugen
                SQLAnfrageKonfiguration = null;

                try {

                    // Tabelle Benutzer abfragen und Nutzer auswählen
                    SQLAnfrage = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    SQLAnfrageKonfiguration = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    result = SQLAnfrage.executeQuery("SHOW TABLES LIKE 'tbl_benutzer'");

                    // gehe zum ersten Datensatz - wenn nicht leer
                    if (result.next()) {
                        result = SQLAnfrage.executeQuery("SELECT * FROM tbl_benutzer");
                        result.last();
                        int BenutzerZahl = result.getRow();
                        String[] BenutzerOption = new String[BenutzerZahl];

                        // gehe zum ersten Datensatz - wenn nicht leer
                        result.beforeFirst();
                        int i = 0;
                        while (result.next()) {
                            BenutzerOption[i] = result.getString("BENUTZER_NAME");
                            i = i + 1;
                        } // result.next tbl_benutzer
                        // Benutzerauswahl
                        if (i == 0) {
                            Modulhelferlein.CHMVBenutzer = JOptionPane.showInputDialog(null, "<html>Es gibt noch keinen Benutzer!<br>Wie heißt der Benutzer (Vorname Name):</html>", "Benutzer anlegen", JOptionPane.QUESTION_MESSAGE);
                            if (Modulhelferlein.CHMVBenutzer == null) {
                                Modulhelferlein.Fehlermeldung("Systemstart", "kein Benutzer ausgewählt!");
                                System.exit(-1);
                            }
                            NeuerBenutzer = true;
                            result.moveToInsertRow();
                            result.updateInt("BENUTZER_ID", 1);
                            result.updateString("BENUTZER_NAME", Modulhelferlein.CHMVBenutzer);
                            result.insertRow();
                            System.err.println("Benutzer ist " + Modulhelferlein.CHMVBenutzer);
                        } else {
                            Modulhelferlein.CHMVBenutzer = (String) JOptionPane.showInputDialog(null,
                                    "Wer ist der Benutzer?",
                                    "Benutzerauswahl",
                                    JOptionPane.QUESTION_MESSAGE,
                                    null,
                                    BenutzerOption,
                                    BenutzerOption[0]);

//                        BenutzerZahl = JOptionPane.showOptionDialog(null, "Wer ist der Benutzer?", "Benutzerauswahl",
//                                JOptionPane.DEFAULT_OPTION,
//                                JOptionPane.QUESTION_MESSAGE, null,
//                                BenutzerOption, null);
//                        Modulhelferlein.CHMVBenutzer = BenutzerOption[BenutzerZahl];
                        } //
                        resultKonfiguration = SQLAnfrageKonfiguration.executeQuery("SHOW TABLES LIKE 'tbl_konfiguration'");

                        // gehe zum ersten Datensatz - wenn nicht leer
                        if (resultKonfiguration.next()) {

                            // gehe zum ersten Datensatz - wenn nicht leer
                            if (resultKonfiguration.first()) {
                                if (NeuerBenutzer) {
                                    resultKonfiguration = SQLAnfrageKonfiguration.executeQuery("SELECT * FROM tbl_konfiguration WHERE KONFIGURATION_ID='1'");
                                    resultKonfiguration.first();
                                    resultKonfiguration.updateString("Konfiguration_Benutzer", Modulhelferlein.CHMVBenutzer);
                                    resultKonfiguration.updateRow();
                                }
                                menuItemBenutzer.setText(Modulhelferlein.CHMVBenutzer);
                                resultKonfiguration = SQLAnfrage.executeQuery("SELECT * FROM tbl_konfiguration");
                                while (resultKonfiguration.next()) {
                                    Modulhelferlein.pathBenutzer = resultKonfiguration.getString("Konfiguration_Benutzer");
                                    switch (Modulhelferlein.pathBenutzer) {
                                        case "Mailkonfiguration":
                                            // 6. Datensatz Mailkonfiguration
                                            Modulhelferlein.MailHost = resultKonfiguration.getString("Konfiguration_Stammdaten");
                                            Modulhelferlein.MailPort = resultKonfiguration.getString("Konfiguration_Einnahmen");
                                            Modulhelferlein.MailUser = resultKonfiguration.getString("Konfiguration_Ausgaben");
                                            Modulhelferlein.MailPass = resultKonfiguration.getString("Konfiguration_Umsaetze");
                                            break;
                                        case "Waehrung":
                                            // 4. Datensatz Konfigurationsdaten für Währungsumrechnung
                                            Modulhelferlein.USD = Float.parseFloat(resultKonfiguration.getString("Konfiguration_Stammdaten"));
                                            Modulhelferlein.GBP = Float.parseFloat(resultKonfiguration.getString("Konfiguration_Einnahmen"));
                                            Modulhelferlein.CHF = Float.parseFloat(resultKonfiguration.getString("Konfiguration_Ausgaben"));
                                            Modulhelferlein.NOK = Float.parseFloat(resultKonfiguration.getString("Konfiguration_Umsaetze"));
                                            Modulhelferlein.ILS = Float.parseFloat(resultKonfiguration.getString("Konfiguration_Rechnungen"));
                                            Modulhelferlein.DKK = Float.parseFloat(resultKonfiguration.getString("Konfiguration_Sicherung"));
                                            Modulhelferlein.CAD = Float.parseFloat(resultKonfiguration.getString("Konfiguration_Mahnungen"));
                                            break;
                                        case "Datensicherung":
                                            // 5. Datensatz Konfigurationsdaten für onfiguration der  Datensicherung
                                            Modulhelferlein.pathQuelle = resultKonfiguration.getString("Konfiguration_Stammdaten");
                                            Modulhelferlein.pathZiel = resultKonfiguration.getString("Konfiguration_Einnahmen");
                                            break;
                                        default:
                                            if (Modulhelferlein.pathBenutzer.equals(Modulhelferlein.CHMVBenutzer)) {
                                                // 1. Datensatz Konfigurationsdaten für Pfade
                                                Modulhelferlein.pathBerichte = resultKonfiguration.getString("Konfiguration_Stammdaten");
                                                Modulhelferlein.pathEinnahmen = resultKonfiguration.getString("Konfiguration_Einnahmen");
                                                Modulhelferlein.pathAusgaben = resultKonfiguration.getString("Konfiguration_Ausgaben");
                                                Modulhelferlein.pathUmsaetze = resultKonfiguration.getString("Konfiguration_Umsaetze");
                                                Modulhelferlein.pathRechnungen = resultKonfiguration.getString("Konfiguration_Rechnungen");
                                                Modulhelferlein.pathSicherung = resultKonfiguration.getString("Konfiguration_Sicherung");
                                                Modulhelferlein.pathRezensionen = resultKonfiguration.getString("Konfiguration_Mahnungen");
                                                Modulhelferlein.pathKonfiguration = resultKonfiguration.getString("Konfiguration_Termine");
                                                Modulhelferlein.pathBuchprojekte = resultKonfiguration.getString("Konfiguration_Schriftverkehr");
                                                Modulhelferlein.pathSteuer = resultKonfiguration.getString("Konfiguration_Steuer");
                                            } // if
                                    } // switch
                                } // while
                            } // if
                        } else { // tbl_konfiguration existiert nicht
                            Modulhelferlein.Fehlermeldung("Tabelle Konfiguration muss erstellt werden!");
                            Modulhelferlein.pathBerichte = "";
                            Modulhelferlein.pathEinnahmen = "";
                            Modulhelferlein.pathAusgaben = "";
                            Modulhelferlein.pathUmsaetze = "";
                            Modulhelferlein.pathRechnungen = "";
                            Modulhelferlein.pathSicherung = "";
                            Modulhelferlein.pathRezensionen = "";
                            Modulhelferlein.pathKonfiguration = "";
                            Modulhelferlein.pathBuchprojekte = "";
                            Modulhelferlein.pathBenutzer = "";
                            Modulhelferlein.CHMVBenutzer = "";
                        } // tbl_konfiguration existiert nicht
                    } // if shown tbl_benutzer
                    else {
                        Modulhelferlein.Fehlermeldung("Tabelle Benutzer muss erstellt werden!");
                        Modulhelferlein.CHMVBenutzer = "";
                    } // tbl_benutzer existiert nicht
                    
                    PrintWriter pWriter = null;
                    try {
                        pWriter = new PrintWriter(new BufferedWriter(new FileWriter(Modulhelferlein.Semaphore)));
                        pWriter.println(Modulhelferlein.CHMVBenutzer);
                    } catch (IOException ioe) {
                        Modulhelferlein.Fehlermeldung("Semaphore schreiben", ioe.getMessage());
                    } finally {
                        if (pWriter != null) {
                            pWriter.flush();
                            pWriter.close();
                        }
                    }
                } catch (SQLException exept) {
                    Modulhelferlein.Fehlermeldung(
                            "SQLException:"
                            + "\n"
                            + "SQL-Anfrage nicht moeglich: "
                            + "\n"
                            + exept.getMessage());
                } // try
            } // if conn != null
        } // kein Nutzer aktiv
    } // void

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        jMenuBar1 = new JMenuBar();
        jMenuSoftware = new JMenu();
        jMenuSoftwareServer = new JMenu();
        jMenuItemSoftwareXAMPP = new JMenuItem();
        jMenuItemSoftwareSQL = new JMenuItem();
        jMenuItem3 = new JMenuItem();
        jMenuItemSoftwareDBErstellen = new JMenuItem();
        jMenuItemSoftwareDBLoeschen = new JMenuItem();
        jMenuItemSoftwareDBSichern = new JMenuItem();
        jMenuItemSoftwareDBWiederherstellen = new JMenuItem();
        jMenuItemTabellenLeeren = new JMenuItem();
        jMenuItemSoftwareTabellenLoeschen = new JMenuItem();
        jMenuItemSoftwareTabellenErstellen = new JMenuItem();
        jMenuItemServerKonfiguration = new JMenuItem();
        menuItemSoftwareverwaltungBenutzer = new JMenuItem();
        jMenuItemServerBestellnummer = new JMenuItem();
        jMenuItemEnde = new JMenuItem();
        jMenuVerlagsfuehrung = new JMenu();
        jMenu2 = new JMenu();
        jMenuItemVerlagsfuehrungWerbung = new JMenuItem();
        jMenuItem2 = new JMenuItem();
        jMenuItemVerlagsfuehrungAufgaben = new JMenuItem();
        jMenuItemVerlagsfuehrungTermine = new JMenuItem();
        jMenuItemVerlagsfuehrungHonorar = new JMenuItem();
        jMenuItemVerlagsfuehrungFremdwaehrung = new JMenuItem();
        jMenuItemVerlagsfuehrungJahreswechsel = new JMenuItem();
        jMenuItemVerlagsfuehrungBriefpapier = new JMenuItem();
        jMenuItemBriefpapierAdresse = new JMenuItem();
        jMenuVerlagsfuehrungDatensicherung = new JMenu();
        jMenuItemVerlagsführungDatensicherungKonfiguration = new JMenuItem();
        jMenuItemVerlagsführungDatensicherungDatensicherung = new JMenuItem();
        jMenuVerlagsfuehrungSteuer = new JMenu();
        jMenuItemVerlagsfuehrungSteuerKonfiguration = new JMenuItem();
        jMenuItemVerlagsfuehrungSteuerUstrVA = new JMenuItem();
        jMenuItemVerlagsfuehrungSteuerUstrE = new JMenuItem();
        jMenuItemVerlagsfuehrungSteuerEUR = new JMenuItem();
        jMenuItemVerlagsfuehrungInternetPruefenUStrID = new JMenuItem();
        jMenuVerlagsfuehrungInternet = new JMenu();
        jMenuItemVerlagsfuehrungInternetBOD = new JMenuItem();
        jMenuItemVerlagsfuehrungInternetVolksbank = new JMenuItem();
        jMenuItemVerlagsfuehrungInternetPrintus = new JMenuItem();
        jMenuItemVerlagsfuehrungInternetVistaprint = new JMenuItem();
        jMenuItemVerlagsfuehrungInternetAmazon = new JMenuItem();
        jMenuItemVerlagsfuehrungInternetVLB = new JMenuItem();
        jMenuItemVerlagsfuerhungInternetIBAN = new JMenuItem();
        jMenuStammdaten = new JMenu();
        jMenuItemStammdatenBuchprojekte = new JMenuItem();
        jMenuItemStammdatenAdressen = new JMenuItem();
        jMenuItemStammdatenEMailVerteiler = new JMenuItem();
        jMenuItemStammdatenEMailAdressen = new JMenuItem();
        jMenuBewegungsdaten = new JMenu();
        jMenuBewegungsdatenEinanhmen = new JMenu();
        jMenuItemBewegungsdatenBuchbestellungen = new JMenuItem();
        jMenuItem4 = new JMenuItem();
        jMenuItemBewegungsdatenAusgaben = new JMenuItem();
        jMenuBewegungsdatenRezensionen = new JMenu();
        jMenuItemBewegungsdatenRezensionenVersenden = new JMenuItem();
        jMenuItemBewegungsdatenRezensionenErfassen = new JMenuItem();
        jMenuBerichte = new JMenu();
        jMenuBerichteStammdaten = new JMenu();
        jMenuItemBerichteStammdatenBuchprojekte = new JMenuItem();
        jMenuItemBerichteStammdatenVerlagsprogramm = new JMenuItem();
        jMenuItemBerichteStammdatenAdressen = new JMenuItem();
        jMenuItemBerichteStammdatenInventur = new JMenuItem();
        jMenuBerichteUmsaetze = new JMenu();
        jMenuItemBerichteEinnahmen = new JMenuItem();
        jMenuItemBerichteAusgaben = new JMenuItem();
        jMenuItemBerichteUmsaetze = new JMenuItem();
        jMenu1 = new JMenu();
        jMenuItemBerichteMahnungen = new JMenuItem();
        jMenuItemBerichteZahlungen = new JMenuItem();
        jMenuBerichteVerkaufsstatistik = new JMenu();
        jMenuItemBerichteVerkaufsstatistikEinzel = new JMenuItem();
        jMenuItemBerichteVerkaufsstatistikGesamt = new JMenuItem();
        jMenuItem5 = new JMenuItem();
        jMenuItemBerichteTermine = new JMenuItem();
        jMenuItemBerichteAufgaben = new JMenuItem();
        jMenuItemBerichteKonfiguration = new JMenuItem();
        jMenuHilfe = new JMenu();
        jMenuItemHilfeHilfe = new JMenuItem();
        jMenuItemHilfeUeber = new JMenuItem();
        jMenuItemHilfeChanges = new JMenuItem();
        jMenuItem1 = new JMenuItem();
        menuItemBenutzer = new JMenuItem();
        jMenuItem6 = new JMenuItem();
        jMenuItem7 = new JMenuItem();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Carola Hartmann Miles Verlag");
        setResizable(false);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                formWindowClosing(e);
            }
        });
        Container contentPane = getContentPane();
        contentPane.setLayout(null);

        //======== jMenuBar1 ========
        {

            //======== jMenuSoftware ========
            {
                jMenuSoftware.setMnemonic('v');
                jMenuSoftware.setText("Softwareverwaltung");
                jMenuSoftware.setToolTipText("Alle Funktionen zur Verwaltung der Datenbank und Tabellen");

                //======== jMenuSoftwareServer ========
                {
                    jMenuSoftwareServer.setText("Server-/Datenbankverwaltung");

                    //---- jMenuItemSoftwareXAMPP ----
                    jMenuItemSoftwareXAMPP.setText("Verwalten der XAMPP-Umgebung");
                    jMenuItemSoftwareXAMPP.addActionListener(e -> jMenuItemSoftwareXAMPPActionPerformed(e));
                    jMenuSoftwareServer.add(jMenuItemSoftwareXAMPP);

                    //---- jMenuItemSoftwareSQL ----
                    jMenuItemSoftwareSQL.setText("Verwalten des SQL-Datenbankservers");
                    jMenuItemSoftwareSQL.addActionListener(e -> jMenuItemSoftwareSQLActionPerformed(e));
                    jMenuSoftwareServer.add(jMenuItemSoftwareSQL);
                    jMenuSoftwareServer.addSeparator();

                    //---- jMenuItem3 ----
                    jMenuItem3.setText("SQL-Injection ...");
                    jMenuItem3.addActionListener(e -> jMenuItem3ActionPerformed(e));
                    jMenuSoftwareServer.add(jMenuItem3);
                    jMenuSoftwareServer.addSeparator();

                    //---- jMenuItemSoftwareDBErstellen ----
                    jMenuItemSoftwareDBErstellen.setText("Erstellen der milesverlag-Datenbank ...");
                    jMenuItemSoftwareDBErstellen.addActionListener(e -> jMenuItemSoftwareDBErstellenActionPerformed(e));
                    jMenuSoftwareServer.add(jMenuItemSoftwareDBErstellen);

                    //---- jMenuItemSoftwareDBLoeschen ----
                    jMenuItemSoftwareDBLoeschen.setText("L\u00f6schen der milesverlag-Datenbank ...");
                    jMenuItemSoftwareDBLoeschen.addActionListener(e -> jMenuItemSoftwareDBLoeschenActionPerformed(e));
                    jMenuSoftwareServer.add(jMenuItemSoftwareDBLoeschen);
                    jMenuSoftwareServer.addSeparator();

                    //---- jMenuItemSoftwareDBSichern ----
                    jMenuItemSoftwareDBSichern.setText("Sichern der milesverlag-Datenbank ...");
                    jMenuItemSoftwareDBSichern.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK|KeyEvent.ALT_MASK));
                    jMenuItemSoftwareDBSichern.addActionListener(e -> jMenuItemSoftwareDBSichernActionPerformed(e));
                    jMenuSoftwareServer.add(jMenuItemSoftwareDBSichern);

                    //---- jMenuItemSoftwareDBWiederherstellen ----
                    jMenuItemSoftwareDBWiederherstellen.setText("Wiederherstellen der milesverlag-Datenbank ...");
                    jMenuItemSoftwareDBWiederherstellen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_MASK|KeyEvent.ALT_MASK));
                    jMenuItemSoftwareDBWiederherstellen.addActionListener(e -> jMenuItemSoftwareDBWiederherstellenActionPerformed(e));
                    jMenuSoftwareServer.add(jMenuItemSoftwareDBWiederherstellen);
                    jMenuSoftwareServer.addSeparator();

                    //---- jMenuItemTabellenLeeren ----
                    jMenuItemTabellenLeeren.setText("Tabellen leeren ...");
                    jMenuItemTabellenLeeren.addActionListener(e -> jMenuItemTabellenLeerenActionPerformed(e));
                    jMenuSoftwareServer.add(jMenuItemTabellenLeeren);

                    //---- jMenuItemSoftwareTabellenLoeschen ----
                    jMenuItemSoftwareTabellenLoeschen.setText("Tabellen l\u00f6schen ...");
                    jMenuItemSoftwareTabellenLoeschen.addActionListener(e -> jMenuItemSoftwareTabellenLoeschenActionPerformed(e));
                    jMenuSoftwareServer.add(jMenuItemSoftwareTabellenLoeschen);

                    //---- jMenuItemSoftwareTabellenErstellen ----
                    jMenuItemSoftwareTabellenErstellen.setText("Tabellen erstellen ...");
                    jMenuItemSoftwareTabellenErstellen.addActionListener(e -> jMenuItemSoftwareTabellenErstellenActionPerformed(e));
                    jMenuSoftwareServer.add(jMenuItemSoftwareTabellenErstellen);
                }
                jMenuSoftware.add(jMenuSoftwareServer);

                //---- jMenuItemServerKonfiguration ----
                jMenuSoftware.addSeparator();
                jMenuItemServerKonfiguration.setText("Konfiguration der Ausgabepfade ...");
                jMenuItemServerKonfiguration.addActionListener(e -> jMenuItemServerKonfigurationActionPerformed(e));
                jMenuSoftware.add(jMenuItemServerKonfiguration);

                //---- menuItemSoftwareverwaltungBenutzer ----
                menuItemSoftwareverwaltungBenutzer.setText("Benutzer verwalten ...");
                menuItemSoftwareverwaltungBenutzer.addActionListener(e -> menuItemSoftwareverwaltungBenutzerActionPerformed(e));
                jMenuSoftware.add(menuItemSoftwareverwaltungBenutzer);

                //---- jMenuItemServerBestellnummer ----
                jMenuItemServerBestellnummer.setText("Bestellnummer zur\u00fccksetzen ...");
                jMenuItemServerBestellnummer.addActionListener(e -> jMenuItemServerBestellnummerActionPerformed(e));
                jMenuSoftware.add(jMenuItemServerBestellnummer);

                //---- jMenuItemEnde ----
                jMenuSoftware.addSeparator();
                jMenuItemEnde.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_MASK));
                jMenuItemEnde.setText("Ende");
                jMenuItemEnde.addActionListener(e -> jMenuItemEndeActionPerformed(e));
                jMenuSoftware.add(jMenuItemEnde);
            }
            jMenuBar1.add(jMenuSoftware);

            //======== jMenuVerlagsfuehrung ========
            {
                jMenuVerlagsfuehrung.setMnemonic('f');
                jMenuVerlagsfuehrung.setText("Verlagsf\u00fchrung");

                //======== jMenu2 ========
                {
                    jMenu2.setText("Mails");

                    //---- jMenuItemVerlagsfuehrungWerbung ----
                    jMenuItemVerlagsfuehrungWerbung.setText("Versenden von Werbe-E-Mails ...");
                    jMenuItemVerlagsfuehrungWerbung.addActionListener(e -> jMenuItemVerlagsfuehrungWerbungActionPerformed(e));
                    jMenu2.add(jMenuItemVerlagsfuehrungWerbung);

                    //---- jMenuItem2 ----
                    jMenuItem2.setText("Konfiguration ...");
                    jMenuItem2.addActionListener(e -> jMenuItem2ActionPerformed(e));
                    jMenu2.add(jMenuItem2);
                }
                jMenuVerlagsfuehrung.add(jMenu2);

                //---- jMenuItemVerlagsfuehrungAufgaben ----
                jMenuItemVerlagsfuehrungAufgaben.setText("Aufgaben ...");
                jMenuItemVerlagsfuehrungAufgaben.addActionListener(e -> jMenuItemVerlagsfuehrungAufgabenActionPerformed(e));
                jMenuVerlagsfuehrung.add(jMenuItemVerlagsfuehrungAufgaben);

                //---- jMenuItemVerlagsfuehrungTermine ----
                jMenuItemVerlagsfuehrungTermine.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, KeyEvent.CTRL_MASK));
                jMenuItemVerlagsfuehrungTermine.setText("Termine ...");
                jMenuItemVerlagsfuehrungTermine.addActionListener(e -> jMenuItemVerlagsfuehrungTermineActionPerformed(e));
                jMenuVerlagsfuehrung.add(jMenuItemVerlagsfuehrungTermine);

                //---- jMenuItemVerlagsfuehrungHonorar ----
                jMenuItemVerlagsfuehrungHonorar.setText("Honorarabrechnung ...");
                jMenuItemVerlagsfuehrungHonorar.addActionListener(e -> jMenuItemVerlagsfuehrungHonorarActionPerformed(e));
                jMenuVerlagsfuehrung.add(jMenuItemVerlagsfuehrungHonorar);

                //---- jMenuItemVerlagsfuehrungFremdwaehrung ----
                jMenuItemVerlagsfuehrungFremdwaehrung.setText("Fremdw\u00e4hrungen ...");
                jMenuItemVerlagsfuehrungFremdwaehrung.addActionListener(e -> jMenuItemVerlagsfuehrungFremdwaehrungActionPerformed(e));
                jMenuVerlagsfuehrung.add(jMenuItemVerlagsfuehrungFremdwaehrung);

                //---- jMenuItemVerlagsfuehrungJahreswechsel ----
                jMenuItemVerlagsfuehrungJahreswechsel.setText("Jahreswechsel");
                jMenuItemVerlagsfuehrungJahreswechsel.addActionListener(e -> jMenuItemVerlagsfuehrungJahreswechselActionPerformed(e));
                jMenuVerlagsfuehrung.add(jMenuItemVerlagsfuehrungJahreswechsel);
                jMenuVerlagsfuehrung.addSeparator();

                //---- jMenuItemVerlagsfuehrungBriefpapier ----
                jMenuItemVerlagsfuehrungBriefpapier.setText("Briefpapier blanko");
                jMenuItemVerlagsfuehrungBriefpapier.addActionListener(e -> jMenuItemVerlagsfuehrungBriefpapierActionPerformed(e));
                jMenuVerlagsfuehrung.add(jMenuItemVerlagsfuehrungBriefpapier);

                //---- jMenuItemBriefpapierAdresse ----
                jMenuItemBriefpapierAdresse.setText("Brief ...");
                jMenuItemBriefpapierAdresse.addActionListener(e -> jMenuItemBriefpapierAdresseActionPerformed(e));
                jMenuVerlagsfuehrung.add(jMenuItemBriefpapierAdresse);
                jMenuVerlagsfuehrung.addSeparator();

                //======== jMenuVerlagsfuehrungDatensicherung ========
                {
                    jMenuVerlagsfuehrungDatensicherung.setText("Datensicherung");

                    //---- jMenuItemVerlagsführungDatensicherungKonfiguration ----
                    jMenuItemVerlagsführungDatensicherungKonfiguration.setText("Konfiguration ...");
                    jMenuItemVerlagsführungDatensicherungKonfiguration.addActionListener(e -> jMenuItemVerlagsführungDatensicherungKonfigurationActionPerformed(e));
                    jMenuVerlagsfuehrungDatensicherung.add(jMenuItemVerlagsführungDatensicherungKonfiguration);

                    //---- jMenuItemVerlagsführungDatensicherungDatensicherung ----
                    jMenuVerlagsfuehrungDatensicherung.addSeparator();
                    jMenuItemVerlagsführungDatensicherungDatensicherung.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK));
                    jMenuItemVerlagsführungDatensicherungDatensicherung.setText("Datensicherung");
                    jMenuItemVerlagsführungDatensicherungDatensicherung.addActionListener(e -> jMenuItemVerlagsführungDatensicherungDatensicherungActionPerformed(e));
                    jMenuVerlagsfuehrungDatensicherung.add(jMenuItemVerlagsführungDatensicherungDatensicherung);
                }
                jMenuVerlagsfuehrung.add(jMenuVerlagsfuehrungDatensicherung);

                //======== jMenuVerlagsfuehrungSteuer ========
                {
                    jMenuVerlagsfuehrung.addSeparator();
                    jMenuVerlagsfuehrungSteuer.setText("Steuer");

                    //---- jMenuItemVerlagsfuehrungSteuerKonfiguration ----
                    jMenuItemVerlagsfuehrungSteuerKonfiguration.setText("Konfiguration Steuererkl\u00e4rung ...");
                    jMenuItemVerlagsfuehrungSteuerKonfiguration.addActionListener(e -> jMenuItemVerlagsfuehrungSteuerKonfigurationActionPerformed(e));
                    jMenuVerlagsfuehrungSteuer.add(jMenuItemVerlagsfuehrungSteuerKonfiguration);

                    //---- jMenuItemVerlagsfuehrungSteuerUstrVA ----
                    jMenuVerlagsfuehrungSteuer.addSeparator();
                    jMenuItemVerlagsfuehrungSteuerUstrVA.setText("Umsatzsteuervoranmeldung ...");
                    jMenuItemVerlagsfuehrungSteuerUstrVA.addActionListener(e -> jMenuItemVerlagsfuehrungSteuerUstrVAActionPerformed(e));
                    jMenuVerlagsfuehrungSteuer.add(jMenuItemVerlagsfuehrungSteuerUstrVA);

                    //---- jMenuItemVerlagsfuehrungSteuerUstrE ----
                    jMenuItemVerlagsfuehrungSteuerUstrE.setText("Umsatzsteuererkl\u00e4rung ...");
                    jMenuItemVerlagsfuehrungSteuerUstrE.addActionListener(e -> jMenuItemVerlagsfuehrungSteuerUstrEActionPerformed(e));
                    jMenuVerlagsfuehrungSteuer.add(jMenuItemVerlagsfuehrungSteuerUstrE);

                    //---- jMenuItemVerlagsfuehrungSteuerEUR ----
                    jMenuItemVerlagsfuehrungSteuerEUR.setText("Einnahmen\u00fcberschussrechnung ...");
                    jMenuItemVerlagsfuehrungSteuerEUR.addActionListener(e -> jMenuItemVerlagsfuehrungSteuerEURActionPerformed(e));
                    jMenuVerlagsfuehrungSteuer.add(jMenuItemVerlagsfuehrungSteuerEUR);
                }
                jMenuVerlagsfuehrung.add(jMenuVerlagsfuehrungSteuer);

                //---- jMenuItemVerlagsfuehrungInternetPruefenUStrID ----
                jMenuItemVerlagsfuehrungInternetPruefenUStrID.setText("pr\u00fcfen UStrID");
                jMenuItemVerlagsfuehrungInternetPruefenUStrID.addActionListener(e -> jMenuItemVerlagsfuehrungInternetPruefenUStrIDActionPerformed(e));
                jMenuVerlagsfuehrung.add(jMenuItemVerlagsfuehrungInternetPruefenUStrID);

                //======== jMenuVerlagsfuehrungInternet ========
                {
                    jMenuVerlagsfuehrung.addSeparator();
                    jMenuVerlagsfuehrungInternet.setText("Aufruf von Internet-Seiten");

                    //---- jMenuItemVerlagsfuehrungInternetBOD ----
                    jMenuItemVerlagsfuehrungInternetBOD.setText("Books on Demand");
                    jMenuItemVerlagsfuehrungInternetBOD.addActionListener(e -> jMenuItemVerlagsfuehrungInternetBODActionPerformed(e));
                    jMenuVerlagsfuehrungInternet.add(jMenuItemVerlagsfuehrungInternetBOD);

                    //---- jMenuItemVerlagsfuehrungInternetVolksbank ----
                    jMenuItemVerlagsfuehrungInternetVolksbank.setText("Volksbank");
                    jMenuItemVerlagsfuehrungInternetVolksbank.addActionListener(e -> jMenuItemVerlagsfuehrungInternetVolksbankActionPerformed(e));
                    jMenuVerlagsfuehrungInternet.add(jMenuItemVerlagsfuehrungInternetVolksbank);

                    //---- jMenuItemVerlagsfuehrungInternetPrintus ----
                    jMenuItemVerlagsfuehrungInternetPrintus.setText("Printus");
                    jMenuItemVerlagsfuehrungInternetPrintus.addActionListener(e -> jMenuItemVerlagsfuehrungInternetPrintusActionPerformed(e));
                    jMenuVerlagsfuehrungInternet.add(jMenuItemVerlagsfuehrungInternetPrintus);

                    //---- jMenuItemVerlagsfuehrungInternetVistaprint ----
                    jMenuItemVerlagsfuehrungInternetVistaprint.setText("Vistaprint");
                    jMenuItemVerlagsfuehrungInternetVistaprint.addActionListener(e -> jMenuItemVerlagsfuehrungInternetVistaprintActionPerformed(e));
                    jMenuVerlagsfuehrungInternet.add(jMenuItemVerlagsfuehrungInternetVistaprint);

                    //---- jMenuItemVerlagsfuehrungInternetAmazon ----
                    jMenuItemVerlagsfuehrungInternetAmazon.setText("Amazon");
                    jMenuItemVerlagsfuehrungInternetAmazon.addActionListener(e -> jMenuItemVerlagsfuehrungInternetAmazonActionPerformed(e));
                    jMenuVerlagsfuehrungInternet.add(jMenuItemVerlagsfuehrungInternetAmazon);

                    //---- jMenuItemVerlagsfuehrungInternetVLB ----
                    jMenuItemVerlagsfuehrungInternetVLB.setText("VLB");
                    jMenuItemVerlagsfuehrungInternetVLB.addActionListener(e -> jMenuItemVerlagsfuehrungInternetVLBActionPerformed(e));
                    jMenuVerlagsfuehrungInternet.add(jMenuItemVerlagsfuehrungInternetVLB);

                    //---- jMenuItemVerlagsfuerhungInternetIBAN ----
                    jMenuItemVerlagsfuerhungInternetIBAN.setText("IBAN-Rechner");
                    jMenuItemVerlagsfuerhungInternetIBAN.addActionListener(e -> jMenuItemVerlagsfuerhungInternetIBANActionPerformed(e));
                    jMenuVerlagsfuehrungInternet.add(jMenuItemVerlagsfuerhungInternetIBAN);
                }
                jMenuVerlagsfuehrung.add(jMenuVerlagsfuehrungInternet);
            }
            jMenuBar1.add(jMenuVerlagsfuehrung);

            //======== jMenuStammdaten ========
            {
                jMenuStammdaten.setMnemonic('S');
                jMenuStammdaten.setText("Stammdaten");

                //---- jMenuItemStammdatenBuchprojekte ----
                jMenuItemStammdatenBuchprojekte.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_MASK));
                jMenuItemStammdatenBuchprojekte.setText("Buchprojekte ...");
                jMenuItemStammdatenBuchprojekte.addActionListener(e -> jMenuItemStammdatenBuchprojekteActionPerformed(e));
                jMenuStammdaten.add(jMenuItemStammdatenBuchprojekte);

                //---- jMenuItemStammdatenAdressen ----
                jMenuItemStammdatenAdressen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_MASK));
                jMenuItemStammdatenAdressen.setText("Adressen ...");
                jMenuItemStammdatenAdressen.addActionListener(e -> jMenuItemStammdatenAdressenActionPerformed(e));
                jMenuStammdaten.add(jMenuItemStammdatenAdressen);

                //---- jMenuItemStammdatenEMailVerteiler ----
                jMenuItemStammdatenEMailVerteiler.setText("E-Mail-Verteiler ...");
                jMenuItemStammdatenEMailVerteiler.addActionListener(e -> jMenuItemStammdatenEMailVerteilerActionPerformed(e));
                jMenuStammdaten.add(jMenuItemStammdatenEMailVerteiler);

                //---- jMenuItemStammdatenEMailAdressen ----
                jMenuItemStammdatenEMailAdressen.setText("E-Mail-Adressen ...");
                jMenuItemStammdatenEMailAdressen.addActionListener(e -> jMenuItemStammdatenEMailAdressenActionPerformed(e));
                jMenuStammdaten.add(jMenuItemStammdatenEMailAdressen);
            }
            jMenuBar1.add(jMenuStammdaten);

            //======== jMenuBewegungsdaten ========
            {
                jMenuBewegungsdaten.setMnemonic('d');
                jMenuBewegungsdaten.setText("Bewegungsdaten");

                //======== jMenuBewegungsdatenEinanhmen ========
                {
                    jMenuBewegungsdatenEinanhmen.setText("Einnahmen");

                    //---- jMenuItemBewegungsdatenBuchbestellungen ----
                    jMenuItemBewegungsdatenBuchbestellungen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, KeyEvent.CTRL_MASK|KeyEvent.SHIFT_MASK));
                    jMenuItemBewegungsdatenBuchbestellungen.setText("Buchbestellungen ...");
                    jMenuItemBewegungsdatenBuchbestellungen.addActionListener(e -> jMenuItemBewegungsdatenBuchbestellungenActionPerformed(e));
                    jMenuBewegungsdatenEinanhmen.add(jMenuItemBewegungsdatenBuchbestellungen);

                    //---- jMenuItem4 ----
                    jMenuItem4.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_MASK|KeyEvent.SHIFT_MASK));
                    jMenuItem4.setText("sonstige Einnahmen ...");
                    jMenuItem4.addActionListener(e -> jMenuItem4ActionPerformed(e));
                    jMenuBewegungsdatenEinanhmen.add(jMenuItem4);
                }
                jMenuBewegungsdaten.add(jMenuBewegungsdatenEinanhmen);

                //---- jMenuItemBewegungsdatenAusgaben ----
                jMenuItemBewegungsdatenAusgaben.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_MASK|KeyEvent.SHIFT_MASK));
                jMenuItemBewegungsdatenAusgaben.setText("Ausgaben ...");
                jMenuItemBewegungsdatenAusgaben.addActionListener(e -> jMenuItemBewegungsdatenAusgabenActionPerformed(e));
                jMenuBewegungsdaten.add(jMenuItemBewegungsdatenAusgaben);

                //======== jMenuBewegungsdatenRezensionen ========
                {
                    jMenuBewegungsdatenRezensionen.setText("Rezensionen/Freiexemplare");

                    //---- jMenuItemBewegungsdatenRezensionenVersenden ----
                    jMenuItemBewegungsdatenRezensionenVersenden.setText("versenden ...");
                    jMenuItemBewegungsdatenRezensionenVersenden.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_MASK|KeyEvent.SHIFT_MASK));
                    jMenuItemBewegungsdatenRezensionenVersenden.addActionListener(e -> jMenuItemBewegungsdatenRezensionenVersendenActionPerformed(e));
                    jMenuBewegungsdatenRezensionen.add(jMenuItemBewegungsdatenRezensionenVersenden);

                    //---- jMenuItemBewegungsdatenRezensionenErfassen ----
                    jMenuItemBewegungsdatenRezensionenErfassen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.ALT_MASK));
                    jMenuItemBewegungsdatenRezensionenErfassen.setText("erfassen ...");
                    jMenuItemBewegungsdatenRezensionenErfassen.addActionListener(e -> jMenuItemBewegungsdatenRezensionenErfassenActionPerformed(e));
                    jMenuBewegungsdatenRezensionen.add(jMenuItemBewegungsdatenRezensionenErfassen);
                }
                jMenuBewegungsdaten.add(jMenuBewegungsdatenRezensionen);
            }
            jMenuBar1.add(jMenuBewegungsdaten);

            //======== jMenuBerichte ========
            {
                jMenuBerichte.setMnemonic('B');
                jMenuBerichte.setText("Berichte");

                //======== jMenuBerichteStammdaten ========
                {
                    jMenuBerichteStammdaten.setMnemonic('S');
                    jMenuBerichteStammdaten.setText("Stammdaten");

                    //---- jMenuItemBerichteStammdatenBuchprojekte ----
                    jMenuItemBerichteStammdatenBuchprojekte.setText("Buchprojekte ...");
                    jMenuItemBerichteStammdatenBuchprojekte.addActionListener(e -> jMenuItemBerichteStammdatenBuchprojekteActionPerformed(e));
                    jMenuBerichteStammdaten.add(jMenuItemBerichteStammdatenBuchprojekte);

                    //---- jMenuItemBerichteStammdatenVerlagsprogramm ----
                    jMenuItemBerichteStammdatenVerlagsprogramm.setText("Verlagsprogramm");
                    jMenuItemBerichteStammdatenVerlagsprogramm.addActionListener(e -> jMenuItemBerichteStammdatenVerlagsprogrammActionPerformed(e));
                    jMenuBerichteStammdaten.add(jMenuItemBerichteStammdatenVerlagsprogramm);

                    //---- jMenuItemBerichteStammdatenAdressen ----
                    jMenuItemBerichteStammdatenAdressen.setText("Adressen ...");
                    jMenuItemBerichteStammdatenAdressen.addActionListener(e -> jMenuItemBerichteStammdatenAdressenActionPerformed(e));
                    jMenuBerichteStammdaten.add(jMenuItemBerichteStammdatenAdressen);

                    //---- jMenuItemBerichteStammdatenInventur ----
                    jMenuItemBerichteStammdatenInventur.setText("Inventur");
                    jMenuItemBerichteStammdatenInventur.addActionListener(e -> jMenuItemBerichteStammdatenInventurActionPerformed(e));
                    jMenuBerichteStammdaten.add(jMenuItemBerichteStammdatenInventur);
                }
                jMenuBerichte.add(jMenuBerichteStammdaten);

                //======== jMenuBerichteUmsaetze ========
                {
                    jMenuBerichteUmsaetze.setMnemonic('E');
                    jMenuBerichteUmsaetze.setText("Einnahmen/Ausgaben");

                    //---- jMenuItemBerichteEinnahmen ----
                    jMenuItemBerichteEinnahmen.setText("Einnahmen ...");
                    jMenuItemBerichteEinnahmen.addActionListener(e -> jMenuItemBerichteEinnahmenActionPerformed(e));
                    jMenuBerichteUmsaetze.add(jMenuItemBerichteEinnahmen);

                    //---- jMenuItemBerichteAusgaben ----
                    jMenuItemBerichteAusgaben.setText("Ausgaben ...");
                    jMenuItemBerichteAusgaben.addActionListener(e -> jMenuItemBerichteAusgabenActionPerformed(e));
                    jMenuBerichteUmsaetze.add(jMenuItemBerichteAusgaben);

                    //---- jMenuItemBerichteUmsaetze ----
                    jMenuItemBerichteUmsaetze.setText("Ums\u00e4tze ...");
                    jMenuItemBerichteUmsaetze.addActionListener(e -> jMenuItemBerichteUmsaetzeActionPerformed(e));
                    jMenuBerichteUmsaetze.add(jMenuItemBerichteUmsaetze);

                    //======== jMenu1 ========
                    {
                        jMenu1.setText("offene Rechnungen");

                        //---- jMenuItemBerichteMahnungen ----
                        jMenuItemBerichteMahnungen.setText("Einnahmen/Mahnungen");
                        jMenuItemBerichteMahnungen.addActionListener(e -> jMenuItemBerichteMahnungenActionPerformed(e));
                        jMenu1.add(jMenuItemBerichteMahnungen);

                        //---- jMenuItemBerichteZahlungen ----
                        jMenuItemBerichteZahlungen.setText("Ausgaben/Zahlungen");
                        jMenuItemBerichteZahlungen.addActionListener(e -> jMenuItemBerichteZahlungenActionPerformed(e));
                        jMenu1.add(jMenuItemBerichteZahlungen);
                    }
                    jMenuBerichteUmsaetze.add(jMenu1);
                }
                jMenuBerichte.add(jMenuBerichteUmsaetze);

                //======== jMenuBerichteVerkaufsstatistik ========
                {
                    jMenuBerichteVerkaufsstatistik.setText("Verkaufsstatistik");

                    //---- jMenuItemBerichteVerkaufsstatistikEinzel ----
                    jMenuItemBerichteVerkaufsstatistikEinzel.setText("Einzelnes Buch ...");
                    jMenuItemBerichteVerkaufsstatistikEinzel.addActionListener(e -> jMenuItemBerichteVerkaufsstatistikEinzelActionPerformed(e));
                    jMenuBerichteVerkaufsstatistik.add(jMenuItemBerichteVerkaufsstatistikEinzel);

                    //---- jMenuItemBerichteVerkaufsstatistikGesamt ----
                    jMenuItemBerichteVerkaufsstatistikGesamt.setMnemonic('V');
                    jMenuItemBerichteVerkaufsstatistikGesamt.setText("Alle B\u00fccher ...");
                    jMenuItemBerichteVerkaufsstatistikGesamt.addActionListener(e -> jMenuItemBerichteVerkaufsstatistikGesamtActionPerformed(e));
                    jMenuBerichteVerkaufsstatistik.add(jMenuItemBerichteVerkaufsstatistikGesamt);
                }
                jMenuBerichte.add(jMenuBerichteVerkaufsstatistik);

                //---- jMenuItem5 ----
                jMenuItem5.setText("Rezensionen ...");
                jMenuItem5.addActionListener(e -> jMenuItem5ActionPerformed(e));
                jMenuBerichte.add(jMenuItem5);

                //---- jMenuItemBerichteTermine ----
                jMenuItemBerichteTermine.setMnemonic('T');
                jMenuItemBerichteTermine.setText("Termine");
                jMenuItemBerichteTermine.addActionListener(e -> jMenuItemBerichteTermineActionPerformed(e));
                jMenuBerichte.add(jMenuItemBerichteTermine);

                //---- jMenuItemBerichteAufgaben ----
                jMenuItemBerichteAufgaben.setText("Aufgaben");
                jMenuItemBerichteAufgaben.addActionListener(e -> jMenuItemBerichteAufgabenActionPerformed(e));
                jMenuBerichte.add(jMenuItemBerichteAufgaben);

                //---- jMenuItemBerichteKonfiguration ----
                jMenuItemBerichteKonfiguration.setMnemonic('k');
                jMenuItemBerichteKonfiguration.setText("Konfiguration");
                jMenuItemBerichteKonfiguration.addActionListener(e -> jMenuItemBerichteKonfigurationActionPerformed(e));
                jMenuBerichte.add(jMenuItemBerichteKonfiguration);
            }
            jMenuBar1.add(jMenuBerichte);

            //======== jMenuHilfe ========
            {
                jMenuHilfe.setMnemonic('i');
                jMenuHilfe.setText("Hilfe");
                jMenuHilfe.setHorizontalAlignment(SwingConstants.RIGHT);
                jMenuHilfe.setHorizontalTextPosition(SwingConstants.RIGHT);

                //---- jMenuItemHilfeHilfe ----
                jMenuItemHilfeHilfe.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, KeyEvent.CTRL_MASK));
                jMenuItemHilfeHilfe.setText("Hilfe");
                jMenuItemHilfeHilfe.addActionListener(e -> jMenuItemHilfeHilfeActionPerformed(e));
                jMenuHilfe.add(jMenuItemHilfeHilfe);

                //---- jMenuItemHilfeUeber ----
                jMenuItemHilfeUeber.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, KeyEvent.SHIFT_MASK));
                jMenuItemHilfeUeber.setText("\u00dcber ...");
                jMenuItemHilfeUeber.addActionListener(e -> jMenuItemHilfeUeberActionPerformed(e));
                jMenuHilfe.add(jMenuItemHilfeUeber);

                //---- jMenuItemHilfeChanges ----
                jMenuItemHilfeChanges.setText("Change-Log ...");
                jMenuItemHilfeChanges.addActionListener(e -> jMenuItemHilfeChangesActionPerformed(e));
                jMenuHilfe.add(jMenuItemHilfeChanges);

                //---- jMenuItem1 ----
                jMenuItem1.setText("Test ...");
                jMenuItem1.addActionListener(e -> jMenuItem1ActionPerformed(e));
                jMenuHilfe.add(jMenuItem1);
            }
            jMenuBar1.add(jMenuHilfe);

            //---- menuItemBenutzer ----
            menuItemBenutzer.setText("-");
            menuItemBenutzer.setForeground(Color.blue);
            menuItemBenutzer.setFont(new Font("Segoe UI", Font.BOLD, 10));
            jMenuBar1.add(menuItemBenutzer);
        }
        setJMenuBar(jMenuBar1);

        { // compute preferred size
            Dimension preferredSize = new Dimension();
            for(int i = 0; i < contentPane.getComponentCount(); i++) {
                Rectangle bounds = contentPane.getComponent(i).getBounds();
                preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
            }
            Insets insets = contentPane.getInsets();
            preferredSize.width += insets.right;
            preferredSize.height += insets.bottom;
            contentPane.setMinimumSize(preferredSize);
            contentPane.setPreferredSize(preferredSize);
        }
        pack();
        setLocationRelativeTo(getOwner());

        //---- jMenuItem6 ----
        jMenuItem6.setText("jMenuItem6");

        //---- jMenuItem7 ----
        jMenuItem7.setText("jMenuItem7");
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItemHilfeUeberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemHilfeUeberActionPerformed
        // TODO add your handling code here:
        _DlgUeber.main(null);
    }//GEN-LAST:event_jMenuItemHilfeUeberActionPerformed

    private void jMenuItemHilfeHilfeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemHilfeHilfeActionPerformed
        // TODO add your handling code here:
        try {
            Runtime.getRuntime().exec("cmd /c start " + System.getProperty("user.dir") + "/miles-Verlag-Handbuch.html");
        } catch (IOException exept) {
            Modulhelferlein.Fehlermeldung("Exception: " + exept.getMessage());
        }
    }//GEN-LAST:event_jMenuItemHilfeHilfeActionPerformed

    private void jMenuItemBerichteVerkaufsstatistikGesamtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBerichteVerkaufsstatistikGesamtActionPerformed
        // TODO add your handling code here:
        _DlgVerkaufsstatistikCHMVGesamt.main(null);
    }//GEN-LAST:event_jMenuItemBerichteVerkaufsstatistikGesamtActionPerformed

    private void jMenuItemBerichteKonfigurationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBerichteKonfigurationActionPerformed
        try {
            // TODO add your handling code here:
            berKonfiguration.bericht();
        } catch (IOException ex) {
            Modulhelferlein.Fehlermeldung("IO-Exception: " + ex.getMessage());
            //Logger.getLogger(CarolaHartmannMilesVerlag.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }//GEN-LAST:event_jMenuItemBerichteKonfigurationActionPerformed

    private void jMenuItemBerichteTermineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBerichteTermineActionPerformed
        // TODO add your handling code here:
        berTermine.bericht();
    }//GEN-LAST:event_jMenuItemBerichteTermineActionPerformed

    private void jMenuItemStammdatenBuchprojekteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemStammdatenBuchprojekteActionPerformed
        // TODO add your handling code here:
        //dbBuch fenster = new dbBuch();
        //fenster.dbbuch();
        VerwaltenDatenbankBuch.main(null);
    }//GEN-LAST:event_jMenuItemStammdatenBuchprojekteActionPerformed

    private void jMenuItemStammdatenAdressenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemStammdatenAdressenActionPerformed
        // TODO add your handling code here:
        // dbAdresse fenster = new dbAdresse();
        // fenster.dbadressen();
        VerwaltenDatenbankAdressen.main(null);
    }//GEN-LAST:event_jMenuItemStammdatenAdressenActionPerformed

    private void jMenuItemVerlagsführungDatensicherungKonfigurationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemVerlagsführungDatensicherungKonfigurationActionPerformed
        // TODO add your handling code here:
        VerwaltenDatenbankKonfigurationSicherung.main(null);
    }//GEN-LAST:event_jMenuItemVerlagsführungDatensicherungKonfigurationActionPerformed

    private void jMenuItemVerlagsfuehrungInternetBODActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemVerlagsfuehrungInternetBODActionPerformed
        // TODO add your handling code here:
        try {
            Runtime.getRuntime().exec("cmd /c start  https://www.bod.de/mybod.html");
        } catch (IOException e) {
            Modulhelferlein.Fehlermeldung("IOException: " + e.getMessage());
        }
    }//GEN-LAST:event_jMenuItemVerlagsfuehrungInternetBODActionPerformed

    private void jMenuItemVerlagsfuehrungInternetVolksbankActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemVerlagsfuehrungInternetVolksbankActionPerformed
        // TODO add your handling code here:
        try {
            Runtime.getRuntime().exec(
                    "cmd /c start https://www.berliner-volksbank.de/banking-private/entry?trackid=piwik908caf721f22c378");
        } catch (IOException e) {
            Modulhelferlein.Fehlermeldung("IOException: " + e.getMessage());
        }
    }//GEN-LAST:event_jMenuItemVerlagsfuehrungInternetVolksbankActionPerformed

    private void jMenuItemVerlagsfuehrungInternetPrintusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemVerlagsfuehrungInternetPrintusActionPerformed
        // TODO add your handling code here:
        try {
            Runtime.getRuntime().exec(
                    "cmd /c start http://www.printus.de");
        } catch (IOException e) {
            Modulhelferlein.Fehlermeldung("IOException: " + e.getMessage());
        }
    }//GEN-LAST:event_jMenuItemVerlagsfuehrungInternetPrintusActionPerformed

    private void jMenuItemVerlagsfuehrungInternetVistaprintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemVerlagsfuehrungInternetVistaprintActionPerformed
        // TODO add your handling code here:
        try {
            Runtime.getRuntime().exec(
                    "cmd /c start http://www.vistaprint.de");
        } catch (IOException e) {
            Modulhelferlein.Fehlermeldung("IOException: " + e.getMessage());
        }
    }//GEN-LAST:event_jMenuItemVerlagsfuehrungInternetVistaprintActionPerformed

    private void jMenuItemVerlagsfuehrungInternetAmazonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemVerlagsfuehrungInternetAmazonActionPerformed
        // TODO add your handling code here:
        try {
            Runtime.getRuntime().exec(
                    "cmd /c start http://www.amazon.de");
        } catch (IOException e) {
            Modulhelferlein.Fehlermeldung("IO-Exception: " + e.getMessage());
        }
    }//GEN-LAST:event_jMenuItemVerlagsfuehrungInternetAmazonActionPerformed

    private void jMenuItemVerlagsfuehrungInternetVLBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemVerlagsfuehrungInternetVLBActionPerformed
        // TODO add your handling code here:
        try {
            Runtime.getRuntime().exec("cmd /c start https://www.vlb.de/login.html");
        } catch (IOException e) {
            Modulhelferlein.Fehlermeldung("IOException: " + e.getMessage());
        }
    }//GEN-LAST:event_jMenuItemVerlagsfuehrungInternetVLBActionPerformed

    private void jMenuItemEndeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemEndeActionPerformed
        // TODO add your handling code here:
        try {
            result.close();
            SQLAnfrage.close();
            conn.close();
        } catch (SQLException e) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: SQL-Anfrage nicht moeglich: " + e.getMessage());
        }

        System.exit(0);
    }//GEN-LAST:event_jMenuItemEndeActionPerformed

    private void jMenuItemServerBestellnummerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemServerBestellnummerActionPerformed
        // TODO add your handling code here:
        VerwaltenDatenbankBestellnummer.main(null);
    }//GEN-LAST:event_jMenuItemServerBestellnummerActionPerformed

    private void jMenuItemServerKonfigurationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemServerKonfigurationActionPerformed
        // TODO add your handling code here:
        String[] args = {Modulhelferlein.CHMVBenutzer};
        VerwaltenDatenbankKonfigurationPfade.main(args);
    }//GEN-LAST:event_jMenuItemServerKonfigurationActionPerformed

    private void jMenuItemVerlagsfuehrungSteuerEURActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemVerlagsfuehrungSteuerEURActionPerformed
        // TODO add your handling code here:
        Modulhelferlein.Infomeldung("noch nicht implementiert");
    }//GEN-LAST:event_jMenuItemVerlagsfuehrungSteuerEURActionPerformed

    private void jMenuItemVerlagsfuehrungSteuerUstrEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemVerlagsfuehrungSteuerUstrEActionPerformed
        // TODO add your handling code here:
        Modulhelferlein.Infomeldung("noch nicht implementiert");
    }//GEN-LAST:event_jMenuItemVerlagsfuehrungSteuerUstrEActionPerformed

    private void jMenuItemVerlagsfuehrungSteuerUstrVAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemVerlagsfuehrungSteuerUstrVAActionPerformed
        // TODO add your handling code here:
        ModulAbrechnungUSteuerVorA fenster = new ModulAbrechnungUSteuerVorA();
        fenster.dlgUStrVor();
    }//GEN-LAST:event_jMenuItemVerlagsfuehrungSteuerUstrVAActionPerformed

    private void jMenuItemVerlagsfuehrungSteuerKonfigurationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemVerlagsfuehrungSteuerKonfigurationActionPerformed
        // TODO add your handling code here:
        VerwaltenDatenbankKonfigurationSteuer.main(null);
    }//GEN-LAST:event_jMenuItemVerlagsfuehrungSteuerKonfigurationActionPerformed

    private void jMenuItemSoftwareXAMPPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSoftwareXAMPPActionPerformed
        // TODO add your handling code here:
        try {
            Runtime.getRuntime().exec("C:/xampp/xampp-control.exe");
        } catch (IOException e) {
            Modulhelferlein.Fehlermeldung("IO-Exception: " + e.getMessage());
        }
    }//GEN-LAST:event_jMenuItemSoftwareXAMPPActionPerformed

    private void jMenuItemSoftwareSQLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSoftwareSQLActionPerformed
        // TODO add your handling code here:
        try {
            Runtime.getRuntime().exec(
                    "cmd /c start http://localhost:8888/phpmyadmin/");
        } catch (IOException e) {
            Modulhelferlein.Fehlermeldung("IO-Exception: " + e.getMessage());
        }
    }//GEN-LAST:event_jMenuItemSoftwareSQLActionPerformed

    private void jMenuItemSoftwareDBErstellenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSoftwareDBErstellenActionPerformed
        // TODO add your handling code here:
        _DlgDatenbankErstellen.main(null);
    }//GEN-LAST:event_jMenuItemSoftwareDBErstellenActionPerformed

    private void jMenuItemSoftwareDBLoeschenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSoftwareDBLoeschenActionPerformed
        // TODO add your handling code here:
        _DlgDatenbankLoeschen.main(null);
    }//GEN-LAST:event_jMenuItemSoftwareDBLoeschenActionPerformed

    private void jMenuItemSoftwareDBSichernActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSoftwareDBSichernActionPerformed
        // TODO add your handling code here:
        backupDatenbank();
    }//GEN-LAST:event_jMenuItemSoftwareDBSichernActionPerformed

    private void jMenuItemSoftwareDBWiederherstellenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSoftwareDBWiederherstellenActionPerformed
        // TODO add your handling code here:
        String Filename = "";
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Datenbanksicherung", "sql");
        JFileChooser chooser = new JFileChooser(Modulhelferlein.pathSicherung);

        chooser.setMultiSelectionEnabled(false);
        chooser.setFileFilter(filter);

        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int Ergebnis = chooser.showDialog(null, "Sicherungsdatei wählen");

        if (Ergebnis == JFileChooser.APPROVE_OPTION) {
            Filename = chooser.getSelectedFile().getName();
            try {
                String cmdline = "C:\\xampp\\mysql\\bin\\mysql.exe -P3063 -uroot -pclausewitz milesverlag < " + Modulhelferlein.pathSicherung
                        + "\\"
                        + Filename;
                Runtime.getRuntime().exec("cmd /c " + cmdline);
                Modulhelferlein.Infomeldung("Datenbank wurde wiederhergestellt");
            } catch (IOException e) {
                Modulhelferlein.Fehlermeldung("IO-Exception: " + e.getMessage());
            }
        }
    }//GEN-LAST:event_jMenuItemSoftwareDBWiederherstellenActionPerformed

    private void jMenuItemTabellenLeerenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemTabellenLeerenActionPerformed
        // TODO add your handling code here:
        _DlgTabellenLeeren.main(null);
    }//GEN-LAST:event_jMenuItemTabellenLeerenActionPerformed

    private void jMenuItemSoftwareTabellenLoeschenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSoftwareTabellenLoeschenActionPerformed
        // TODO add your handling code here:
        _DlgTabellenLoeschen.main(null);
    }//GEN-LAST:event_jMenuItemSoftwareTabellenLoeschenActionPerformed

    private void jMenuItemSoftwareTabellenErstellenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSoftwareTabellenErstellenActionPerformed
        // TODO add your handling code here:
        _DlgTabellenErstellen.main(null);
    }//GEN-LAST:event_jMenuItemSoftwareTabellenErstellenActionPerformed

    private void jMenuItemVerlagsführungDatensicherungDatensicherungActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemVerlagsführungDatensicherungDatensicherungActionPerformed
        // TODO add your handling code here:
        _DlgDatensicherung fenster = new _DlgDatensicherung();
        fenster.Dialog();
    }//GEN-LAST:event_jMenuItemVerlagsführungDatensicherungDatensicherungActionPerformed

    private void jMenuItemVerlagsfuehrungWerbungActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemVerlagsfuehrungWerbungActionPerformed
        // TODO add your handling code here:
        _DlgMail.main(null);
    }//GEN-LAST:event_jMenuItemVerlagsfuehrungWerbungActionPerformed

    private void jMenuItemVerlagsfuehrungTermineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemVerlagsfuehrungTermineActionPerformed
        // TODO add your handling code here:
        VerwaltenDatenbankTermine.main(null);
    }//GEN-LAST:event_jMenuItemVerlagsfuehrungTermineActionPerformed

    private void jMenuItemVerlagsfuehrungFremdwaehrungActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemVerlagsfuehrungFremdwaehrungActionPerformed
        // TODO add your handling code here:
        VerwaltenDatenbankKonfigurationWaehrung.main(null);
    }//GEN-LAST:event_jMenuItemVerlagsfuehrungFremdwaehrungActionPerformed

    private void jMenuItemStammdatenEMailVerteilerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemStammdatenEMailVerteilerActionPerformed
        // TODO add your handling code here:
        VerwaltenDatenbankMailverteiler.main(null);
    }//GEN-LAST:event_jMenuItemStammdatenEMailVerteilerActionPerformed

    private void jMenuItemStammdatenEMailAdressenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemStammdatenEMailAdressenActionPerformed
        // TODO add your handling code here:
        VerwaltenDatenbankMailadressen.main(null);
    }//GEN-LAST:event_jMenuItemStammdatenEMailAdressenActionPerformed

    private void jMenuItemBewegungsdatenBuchbestellungenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBewegungsdatenBuchbestellungenActionPerformed
        // TODO add your handling code here:
        VerwaltenDatenbankBestellung.main(null);
        //dbBestellung fenster = new dbBestellung();
        //fenster.dbbestellung();
    }//GEN-LAST:event_jMenuItemBewegungsdatenBuchbestellungenActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        // TODO add your handling code here:
        VerwaltenDatenbankEinnahmen.main(null);
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItemBewegungsdatenAusgabenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBewegungsdatenAusgabenActionPerformed
        // TODO add your handling code here:
        VerwaltenDatenbankAusgaben.main(null);
    }//GEN-LAST:event_jMenuItemBewegungsdatenAusgabenActionPerformed

    private void jMenuItemBewegungsdatenRezensionenErfassenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBewegungsdatenRezensionenErfassenActionPerformed
        // TODO add your handling code here:
        VerwaltenDatenbankRezensionen.main(null);
    }//GEN-LAST:event_jMenuItemBewegungsdatenRezensionenErfassenActionPerformed

    private void jMenuItemVerlagsfuehrungHonorarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemVerlagsfuehrungHonorarActionPerformed
        // TODO add your handling code here:
        ModulAbrechnungHonorar.honorar();
    }//GEN-LAST:event_jMenuItemVerlagsfuehrungHonorarActionPerformed

    private void jMenuItemBerichteZahlungenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBerichteZahlungenActionPerformed
        // TODO add your handling code here:
        _DlgUebersichtZahlungen.main(null);
    }//GEN-LAST:event_jMenuItemBerichteZahlungenActionPerformed

    private void jMenuItemBerichteMahnungenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBerichteMahnungenActionPerformed
        // TODO add your handling code here:
        _DlgUebersichtMahnungen.main(null);
    }//GEN-LAST:event_jMenuItemBerichteMahnungenActionPerformed

    private void jMenuItemBerichteUmsaetzeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBerichteUmsaetzeActionPerformed
        // TODO add your handling code here:
        _DlgUebersichtUmsaetze.main(null);
    }//GEN-LAST:event_jMenuItemBerichteUmsaetzeActionPerformed

    private void jMenuItemBerichteAusgabenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBerichteAusgabenActionPerformed
        // TODO add your handling code here:
        _DlgUebersichtAusgaben.main(null);
    }//GEN-LAST:event_jMenuItemBerichteAusgabenActionPerformed

    private void jMenuItemBerichteEinnahmenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBerichteEinnahmenActionPerformed
        // TODO add your handling code here:
        _DlgUebersichtEinnahmen.main(null);
    }//GEN-LAST:event_jMenuItemBerichteEinnahmenActionPerformed

    private void jMenuItemBewegungsdatenRezensionenVersendenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBewegungsdatenRezensionenVersendenActionPerformed
        // TODO add your handling code here:
        VerwaltenDatenbankRezensionAus.main(null);
    }//GEN-LAST:event_jMenuItemBewegungsdatenRezensionenVersendenActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        backupDatenbank();
        // semaphore löschen
        File file = new File(Modulhelferlein.Semaphore);
        if (file.exists()) {
            file.delete();
        }
        System.out.println("Semaphore gelöscht");
    }//GEN-LAST:event_formWindowClosing

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // TODO add your handling code here:
        VerwaltenDatenbankKonfigurationMail.main(null);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItemBerichteStammdatenInventurActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBerichteStammdatenInventurActionPerformed
        // TODO add your handling code here:
        _DlgInventur.main(null);
    }//GEN-LAST:event_jMenuItemBerichteStammdatenInventurActionPerformed

    private void jMenuItemBerichteStammdatenAdressenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBerichteStammdatenAdressenActionPerformed
        // TODO add your handling code here:
        _DlgAusgabeAdressen.main(null);
    }//GEN-LAST:event_jMenuItemBerichteStammdatenAdressenActionPerformed

    private void jMenuItemBerichteStammdatenVerlagsprogrammActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBerichteStammdatenVerlagsprogrammActionPerformed
        // TODO add your handling code here:
        _DlgVerlagsprogramm.main(null);
    }//GEN-LAST:event_jMenuItemBerichteStammdatenVerlagsprogrammActionPerformed

    private void jMenuItemBerichteStammdatenBuchprojekteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBerichteStammdatenBuchprojekteActionPerformed
        // TODO add your handling code here:
        berBuch.bericht();
    }//GEN-LAST:event_jMenuItemBerichteStammdatenBuchprojekteActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        // TODO add your handling code here:
        _DlgRezensionen.main(null);
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItemVerlagsfuehrungInternetPruefenUStrIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemVerlagsfuehrungInternetPruefenUStrIDActionPerformed
        // TODO add your handling code here:
        try {
            Runtime.getRuntime().exec("cmd /c start https://evatr.bff-online.de/eVatR/index_html");
        } catch (IOException e) {
            Modulhelferlein.Fehlermeldung("IOException: " + e.getMessage());
        }
        _DlgPruefenUstrID.main(null);
    }//GEN-LAST:event_jMenuItemVerlagsfuehrungInternetPruefenUStrIDActionPerformed

    private void jMenuItemVerlagsfuehrungAufgabenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemVerlagsfuehrungAufgabenActionPerformed
        // TODO add your handling code here:
        VerwaltenDatenbankAufgaben.main(null);
    }//GEN-LAST:event_jMenuItemVerlagsfuehrungAufgabenActionPerformed

    private void jMenuItemBerichteAufgabenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBerichteAufgabenActionPerformed
        // TODO add your handling code here:
        berAufgaben.bericht();
    }//GEN-LAST:event_jMenuItemBerichteAufgabenActionPerformed

    private void jMenuItemHilfeChangesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemHilfeChangesActionPerformed
        // TODO add your handling code here:
        // TestEtikettendruck.main(null);
        _DlgChangeLog.main(null);
    }//GEN-LAST:event_jMenuItemHilfeChangesActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        //_DlgTest.main(null);
        // TestPDFA.CreatePDFA();
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        // TODO add your handling code here:
        String SQL;
        SQL = JOptionPane.showInputDialog(null, "SQL-Befehl?");
        if (SQL != null) {
            try {
                Statement SQLAnfrageSQL = conn.createStatement();
                if (JOptionPane.showConfirmDialog(null,
                        "Soll der Befehl -> " + SQL + " <- wirklich ausgführt werden?",
                        "Bestätigung",
                        JOptionPane.YES_NO_OPTION) == 0) {
                    SQLAnfrageSQL.executeUpdate(SQL);
                }
            } catch (SQLException ex) {
                Modulhelferlein.Fehlermeldung("SQL-Befehl ausführen", "SQL-Exception", ex.getMessage());
            }
        }
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItemVerlagsfuehrungBriefpapierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemVerlagsfuehrungBriefpapierActionPerformed
        // TODO add your handling code here:
        ModulBriefpapierDrucken.PDF();
    }//GEN-LAST:event_jMenuItemVerlagsfuehrungBriefpapierActionPerformed

    private void jMenuItemBriefpapierAdresseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBriefpapierAdresseActionPerformed
        // TODO add your handling code here:
        _DlgBrief.main(null);
    }//GEN-LAST:event_jMenuItemBriefpapierAdresseActionPerformed

    private void jMenuItemVerlagsfuerhungInternetIBANActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemVerlagsfuerhungInternetIBANActionPerformed
        // TODO add your handling code here:
        try {
            Runtime.getRuntime().exec("cmd /c start https://www.iban-rechner.de");
        } catch (IOException e) {
            Modulhelferlein.Fehlermeldung("Aufruf IBAN-Rechner", "IO-Exception: ", e.getMessage());
        }
    }//GEN-LAST:event_jMenuItemVerlagsfuerhungInternetIBANActionPerformed

    private void jMenuItemVerlagsfuehrungJahreswechselActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemVerlagsfuehrungJahreswechselActionPerformed
        // TODO add your handling code here:
        ModulJahresabschluss.Jahresabschluss();
    }//GEN-LAST:event_jMenuItemVerlagsfuehrungJahreswechselActionPerformed

    private void jMenuItemBerichteVerkaufsstatistikEinzelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBerichteVerkaufsstatistikEinzelActionPerformed
        // TODO add your handling code here:
        _DlgVerkaufsstatistikCHMVEinzel.main(null);
    }//GEN-LAST:event_jMenuItemBerichteVerkaufsstatistikEinzelActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */

        parameter = args.length;

        if (parameter != 5) {
            Modulhelferlein.Fehlermeldung("Die Anzahl der Parameter stimmt nicht!");
            System.exit(-1);
        }
        Modulhelferlein.dbUrl = args[0];
        Modulhelferlein.dbPort = args[1];
        Modulhelferlein.dbName = args[2];
        Modulhelferlein.dbUser = args[3];
        Modulhelferlein.dbPassword = args[4];

        /**
         * try { for (javax.swing.UIManager.LookAndFeelInfo info :
         * javax.swing.UIManager.getInstalledLookAndFeels()) { if
         * ("Nimbus".equals(info.getName())) {
         * javax.swing.UIManager.setLookAndFeel(info.getClassName()); break; } }
         * } catch (ClassNotFoundException | InstantiationException |
         * IllegalAccessException | javax.swing.UnsupportedLookAndFeelException
         * ex) {
         * java.util.logging.Logger.getLogger(CarolaHartmannMilesVerlag.class.getName()).log(java.util.logging.Level.SEVERE,
         * null, ex); }
         */
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new CarolaHartmannMilesVerlag().setVisible(true);
        });

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JMenuBar jMenuBar1;
    private JMenu jMenuSoftware;
    private JMenu jMenuSoftwareServer;
    private JMenuItem jMenuItemSoftwareXAMPP;
    private JMenuItem jMenuItemSoftwareSQL;
    private JMenuItem jMenuItem3;
    private JMenuItem jMenuItemSoftwareDBErstellen;
    private JMenuItem jMenuItemSoftwareDBLoeschen;
    private JMenuItem jMenuItemSoftwareDBSichern;
    private JMenuItem jMenuItemSoftwareDBWiederherstellen;
    private JMenuItem jMenuItemTabellenLeeren;
    private JMenuItem jMenuItemSoftwareTabellenLoeschen;
    private JMenuItem jMenuItemSoftwareTabellenErstellen;
    private JMenuItem jMenuItemServerKonfiguration;
    private JMenuItem menuItemSoftwareverwaltungBenutzer;
    private JMenuItem jMenuItemServerBestellnummer;
    private JMenuItem jMenuItemEnde;
    private JMenu jMenuVerlagsfuehrung;
    private JMenu jMenu2;
    private JMenuItem jMenuItemVerlagsfuehrungWerbung;
    private JMenuItem jMenuItem2;
    private JMenuItem jMenuItemVerlagsfuehrungAufgaben;
    private JMenuItem jMenuItemVerlagsfuehrungTermine;
    private JMenuItem jMenuItemVerlagsfuehrungHonorar;
    private JMenuItem jMenuItemVerlagsfuehrungFremdwaehrung;
    private JMenuItem jMenuItemVerlagsfuehrungJahreswechsel;
    private JMenuItem jMenuItemVerlagsfuehrungBriefpapier;
    private JMenuItem jMenuItemBriefpapierAdresse;
    private JMenu jMenuVerlagsfuehrungDatensicherung;
    private JMenuItem jMenuItemVerlagsführungDatensicherungKonfiguration;
    private JMenuItem jMenuItemVerlagsführungDatensicherungDatensicherung;
    private JMenu jMenuVerlagsfuehrungSteuer;
    private JMenuItem jMenuItemVerlagsfuehrungSteuerKonfiguration;
    private JMenuItem jMenuItemVerlagsfuehrungSteuerUstrVA;
    private JMenuItem jMenuItemVerlagsfuehrungSteuerUstrE;
    private JMenuItem jMenuItemVerlagsfuehrungSteuerEUR;
    private JMenuItem jMenuItemVerlagsfuehrungInternetPruefenUStrID;
    private JMenu jMenuVerlagsfuehrungInternet;
    private JMenuItem jMenuItemVerlagsfuehrungInternetBOD;
    private JMenuItem jMenuItemVerlagsfuehrungInternetVolksbank;
    private JMenuItem jMenuItemVerlagsfuehrungInternetPrintus;
    private JMenuItem jMenuItemVerlagsfuehrungInternetVistaprint;
    private JMenuItem jMenuItemVerlagsfuehrungInternetAmazon;
    private JMenuItem jMenuItemVerlagsfuehrungInternetVLB;
    private JMenuItem jMenuItemVerlagsfuerhungInternetIBAN;
    private JMenu jMenuStammdaten;
    private JMenuItem jMenuItemStammdatenBuchprojekte;
    private JMenuItem jMenuItemStammdatenAdressen;
    private JMenuItem jMenuItemStammdatenEMailVerteiler;
    private JMenuItem jMenuItemStammdatenEMailAdressen;
    private JMenu jMenuBewegungsdaten;
    private JMenu jMenuBewegungsdatenEinanhmen;
    private JMenuItem jMenuItemBewegungsdatenBuchbestellungen;
    private JMenuItem jMenuItem4;
    private JMenuItem jMenuItemBewegungsdatenAusgaben;
    private JMenu jMenuBewegungsdatenRezensionen;
    private JMenuItem jMenuItemBewegungsdatenRezensionenVersenden;
    private JMenuItem jMenuItemBewegungsdatenRezensionenErfassen;
    private JMenu jMenuBerichte;
    private JMenu jMenuBerichteStammdaten;
    private JMenuItem jMenuItemBerichteStammdatenBuchprojekte;
    private JMenuItem jMenuItemBerichteStammdatenVerlagsprogramm;
    private JMenuItem jMenuItemBerichteStammdatenAdressen;
    private JMenuItem jMenuItemBerichteStammdatenInventur;
    private JMenu jMenuBerichteUmsaetze;
    private JMenuItem jMenuItemBerichteEinnahmen;
    private JMenuItem jMenuItemBerichteAusgaben;
    private JMenuItem jMenuItemBerichteUmsaetze;
    private JMenu jMenu1;
    private JMenuItem jMenuItemBerichteMahnungen;
    private JMenuItem jMenuItemBerichteZahlungen;
    private JMenu jMenuBerichteVerkaufsstatistik;
    private JMenuItem jMenuItemBerichteVerkaufsstatistikEinzel;
    private JMenuItem jMenuItemBerichteVerkaufsstatistikGesamt;
    private JMenuItem jMenuItem5;
    private JMenuItem jMenuItemBerichteTermine;
    private JMenuItem jMenuItemBerichteAufgaben;
    private JMenuItem jMenuItemBerichteKonfiguration;
    private JMenu jMenuHilfe;
    private JMenuItem jMenuItemHilfeHilfe;
    private JMenuItem jMenuItemHilfeUeber;
    private JMenuItem jMenuItemHilfeChanges;
    private JMenuItem jMenuItem1;
    private JMenuItem menuItemBenutzer;
    private JMenuItem jMenuItem6;
    private JMenuItem jMenuItem7;
    // End of variables declaration//GEN-END:variables

    private static int parameter = 0;
    private static boolean NeuerBenutzer = false;
    private static Connection conn = null;
    private static Statement SQLAnfrage;
    private static ResultSet result;
    private static Statement SQLAnfrageKonfiguration;
    private static ResultSet resultKonfiguration;

}
