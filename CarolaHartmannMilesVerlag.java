/*
 *
 * Das JAVA-Programm Miles-Verlag Verlagsverwaltung stellt alle notwendigen
 * Funktionen f�r die Verwaltung des Carola Hartman Miles-Verlags bereit.
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

import com.formdev.flatlaf.FlatLightLaf;
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

/**
 * Hauptprogramm
 * 
 */
public class CarolaHartmannMilesVerlag extends javax.swing.JFrame {

    private void backupDatenbank() {
        String cmdline = null;
        if (ModulHelferlein.dbLive) {
            cmdline = "C:\\xampp\\mysql\\bin\\mysqldump.exe -P"+ModulHelferlein.dbPort+
                                                          " -u"+ModulHelferlein.dbUser+
                                                          " -p"+ModulHelferlein.dbPassword+
                                                          " milesverlag > "
                    + "\""
                    + ModulHelferlein.pathSicherung
                    + "\""
                    + "\\"
                    + "miles-verlag.backup-"
                    + ModulHelferlein.printSimpleDateFormat("yyyyMMdd") + ".sql";
        } else {
            cmdline = "C:\\xampp\\mysql\\bin\\mysqldump.exe -P"+ModulHelferlein.dbPort+
                                                          " -u"+ModulHelferlein.dbUser+
                                                          " -p"+ModulHelferlein.dbPassword+
                                                         "milesverlag-train > "
                    + "\""
                    + ModulHelferlein.pathSicherung
                    + "\""
                    + "\\"
                    + "miles-verlag.backup-"
                    + ModulHelferlein.printSimpleDateFormat("yyyyMMdd") + ".sql";
        }

        try {
            System.out.println("Datenbanksicherung: cmd /c " + cmdline);
            Runtime.getRuntime().exec("cmd /c " + cmdline);
            ModulHelferlein.Infomeldung("Datenbank wurde gesichert!");
        } catch (IOException e) {
            ModulHelferlein.Fehlermeldung("IO-Exception: " + e.getMessage());
        }
    }

    private void menuItemSoftwareverwaltungBenutzerActionPerformed(ActionEvent e) {
        // TODO add your code here
        VerwaltenDatenbankBenutzer.main(null);
    }

    private void menuItemVerlagsfuerhrungFlyerActionPerformed(ActionEvent e) {
        // TODO add your code here
        _DlgFlyer.main(null);
    }

    /**
     * Creates new form CHMV
     */
    public CarolaHartmannMilesVerlag() {
        //pr�fe, ob die Parameter stimmen:
        // 0    Adresse f�r den Datenbankserver
        // 1    Port f�r den Datenbankserver
        // 2    Name der Datenbank 
        // 3    Benutzername 
        // 4    Kennwort
        if (parameter != 6) {
            ModulHelferlein.Fehlermeldung("Die Anzahl der Parameter stimmt nicht!");
            System.exit(-1);
        }

        initComponents();

        this.setSize(650, 210);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        if (ModulHelferlein.dbLive) {
            this.setContentPane(new ModulBackground("background.jpg"));
        } else {
            this.setContentPane(new ModulBackground("BackgroundTraining.jpg"));
        }

        ImageIcon img = new ImageIcon("CarolaHartmannMilesVerlag.png");
        this.setIconImage(img.getImage());

        ModulHelferlein.CurDate = new Date();

        ModulHelferlein.dbUrl = ModulHelferlein.dbUrl + ":" + ModulHelferlein.dbPort + "/" + ModulHelferlein.dbName;

        ModulAusgabe.main(null);

        ModulHelferlein.pathUserDir = System.getProperty("user.dir");
        System.out.println("UserDir = " + ModulHelferlein.pathUserDir);

        // pr�fe, ob ein Nutzer aktiv ist
        File file = new File(ModulHelferlein.Semaphore);
        String Benutzer = null;
        if (file.canRead() || file.isFile()) {
            BufferedReader in = null;
            try {
                in = new BufferedReader(new FileReader(ModulHelferlein.Semaphore));
                Benutzer = in.readLine();
                System.out.println("aktiver Benutzer: " + Benutzer);
            } catch (IOException e) {
                ModulHelferlein.Fehlermeldung("Pr�fe, ob Nutzer aktiv ist", "IOException", e.getMessage());
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        ModulHelferlein.Fehlermeldung("Pr�fe, ob Nutzer aktiv ist", "IOException", e.getMessage());
                    }
                }
            }
            ModulHelferlein.Fehlermeldung("Das Programm wird bereits genutzt", Benutzer);
            System.exit(0);
        } else { // kein Nutzer aktiv
            // Datenbank-Treiber laden
            try {
                Class.forName(ModulHelferlein.dbDriver);
            } catch (ClassNotFoundException exept) {
                ModulHelferlein.Fehlermeldung("Carola Hartmann Miles-Verlag", "ClassNotFoundException: Treiber nicht gefunden: ", exept.getMessage());
                System.exit(1);
            }

            // Verbindung zur Datenbank �ber die JDBC-Br�cke
            try {
                conn = DriverManager.getConnection(ModulHelferlein.dbUrl, ModulHelferlein.dbUser, ModulHelferlein.dbPassword);
            } catch (SQLException exept) {
                //Logger.getLogger(CarolaHartmannMilesVerlag.class.getName()).log(Level.SEVERE, "SQL-Exception: Verbindung zur Datenbank nicht moeglich: " + exept.getMessage(), exept);
                ModulHelferlein.Fehlermeldung("Carola Hartmann Miles-Verlag",
                        "SQL-Exception: Verbindung zur Datenbank nicht moeglich: ", exept.getMessage());
                // Starte XAMPP
                if (exept.getMessage().equals("Unknown database '" + ModulHelferlein.dbName + "'")) {
                    _DlgDatenbankErstellen.main(null);
                } else {
                    try {
                        Runtime.getRuntime().exec("C:/xampp/xampp-control.exe");
                    } catch (IOException e) {
                        //Logger.getLogger(CarolaHartmannMilesVerlag.class.getName()).log(Level.SEVERE, "IO-Exception: " + e.getMessage(), e);
                        ModulHelferlein.Fehlermeldung("IO-Exception: " + e.getMessage());
                    } // try
                } // if
            } // try

            // final Connection conn2=conn;
            if (conn != null) {
                SQLAnfrage = null; // Anfrage erzeugen
                SQLAnfrageKonfiguration = null;

                try {

                    // Tabelle Benutzer abfragen und Nutzer ausw�hlen
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
                            ModulHelferlein.CHMVBenutzer = JOptionPane.showInputDialog(null, "<html>Es gibt noch keinen Benutzer!<br>Wie hei�t der Benutzer (Vorname Name):</html>", "Benutzer anlegen", JOptionPane.QUESTION_MESSAGE);
                            if (ModulHelferlein.CHMVBenutzer == null) {
                                ModulHelferlein.Fehlermeldung("Systemstart", "kein Benutzer ausgew�hlt!");
                                System.exit(-1);
                            }
                            NeuerBenutzer = true;
                            result.moveToInsertRow();
                            result.updateInt("BENUTZER_ID", 1);
                            result.updateString("BENUTZER_NAME", ModulHelferlein.CHMVBenutzer);
                            result.insertRow();
                            System.err.println("Benutzer ist " + ModulHelferlein.CHMVBenutzer);
                        } else {
                            ModulHelferlein.CHMVBenutzer = (String) JOptionPane.showInputDialog(
                                    null,
                                    "Wer ist der Benutzer?",
                                    "Benutzerauswahl",
                                    JOptionPane.QUESTION_MESSAGE,
                                    null,
                                    BenutzerOption,
                                    BenutzerOption[0]);
                            if (ModulHelferlein.CHMVBenutzer == null) {
                                ModulHelferlein.CHMVBenutzer = BenutzerOption[0];
                            }
                        } //
                        resultKonfiguration = SQLAnfrageKonfiguration.executeQuery("SHOW TABLES LIKE 'tbl_konfiguration'");

                        // gehe zum ersten Datensatz - wenn nicht leer
                        if (resultKonfiguration.next()) {

                            // gehe zum ersten Datensatz - wenn nicht leer
                            if (resultKonfiguration.first()) {
                                if (NeuerBenutzer) {
                                    resultKonfiguration = SQLAnfrageKonfiguration.executeQuery("SELECT * FROM tbl_konfiguration WHERE KONFIGURATION_ID='1'");
                                    resultKonfiguration.first();
                                    resultKonfiguration.updateString("Konfiguration_Benutzer", ModulHelferlein.CHMVBenutzer);
                                    resultKonfiguration.updateRow();
                                }
                                menuItemBenutzer.setText(ModulHelferlein.CHMVBenutzer);
                                resultKonfiguration = SQLAnfrage.executeQuery("SELECT * FROM tbl_konfiguration");
                                while (resultKonfiguration.next()) {
                                    ModulHelferlein.pathBenutzer = resultKonfiguration.getString("Konfiguration_Benutzer");
                                    if (ModulHelferlein.pathBenutzer.equals("Mailkonfiguration")) {
                                        System.out.println("Mailkonfiguration gelesen");
                                        ModulHelferlein.MailHost = resultKonfiguration.getString("Konfiguration_Stammdaten");
                                        System.out.println("-> Host      "+ModulHelferlein.MailHost);
                                        ModulHelferlein.MailPort = resultKonfiguration.getString("Konfiguration_Einnahmen");
                                        System.out.println("-> Port      "+ModulHelferlein.MailPort);
                                        ModulHelferlein.MailIMAPHost = resultKonfiguration.getString("Konfiguration_Sicherung");
                                        System.out.println("-> IMAP-Host "+ModulHelferlein.MailIMAPHost);
                                        ModulHelferlein.MailIMAPPort = resultKonfiguration.getString("Konfiguration_Mahnungen");
                                        System.out.println("-> IMAP-Port "+ModulHelferlein.MailIMAPPort);
                                        ModulHelferlein.MailIMAPGesendet = resultKonfiguration.getString("Konfiguration_Rechnungen");
                                        System.out.println("-> IMAP-Sent "+ModulHelferlein.MailIMAPGesendet);
                                        ModulHelferlein.MailUser = resultKonfiguration.getString("Konfiguration_Ausgaben");
                                        System.out.println("-> User      "+ModulHelferlein.MailUser);
                                        ModulHelferlein.MailPass = resultKonfiguration.getString("Konfiguration_Umsaetze");
                                        System.out.println("-> Passwort  ****************");
                                    }
                                    if (ModulHelferlein.pathBenutzer.equals("Waehrung")) {
                                        System.out.println("W�hrungsdaten gelesen");
                                        ModulHelferlein.USD = Float.parseFloat(resultKonfiguration.getString("Konfiguration_Stammdaten"));
                                        ModulHelferlein.GBP = Float.parseFloat(resultKonfiguration.getString("Konfiguration_Einnahmen"));
                                        ModulHelferlein.CHF = Float.parseFloat(resultKonfiguration.getString("Konfiguration_Ausgaben"));
                                        ModulHelferlein.NOK = Float.parseFloat(resultKonfiguration.getString("Konfiguration_Umsaetze"));
                                        ModulHelferlein.ILS = Float.parseFloat(resultKonfiguration.getString("Konfiguration_Rechnungen"));
                                        ModulHelferlein.DKK = Float.parseFloat(resultKonfiguration.getString("Konfiguration_Sicherung"));
                                        ModulHelferlein.CAD = Float.parseFloat(resultKonfiguration.getString("Konfiguration_Mahnungen"));
                                    }
                                    if (ModulHelferlein.pathBenutzer.equals("Datensicherung")) {
                                        System.out.println("Konfiguration Datensicherung gelesen");
                                        ModulHelferlein.pathQuelle = resultKonfiguration.getString("Konfiguration_Stammdaten");
                                        System.out.println("-> Quelle "+ModulHelferlein.pathQuelle);
                                        ModulHelferlein.pathZiel = resultKonfiguration.getString("Konfiguration_Einnahmen");
                                        System.out.println("-> Ziel   "+ModulHelferlein.pathZiel);
                                    }
                                    if (ModulHelferlein.pathBenutzer.equals(ModulHelferlein.CHMVBenutzer)) {
                                        System.out.println("Benutzerkonfiguration gelesen");
                                        ModulHelferlein.pathBerichte = resultKonfiguration.getString("Konfiguration_Stammdaten");
                                        ModulHelferlein.pathEinnahmen = resultKonfiguration.getString("Konfiguration_Einnahmen");
                                        ModulHelferlein.pathAusgaben = resultKonfiguration.getString("Konfiguration_Ausgaben");
                                        ModulHelferlein.pathUmsaetze = resultKonfiguration.getString("Konfiguration_Umsaetze");
                                        ModulHelferlein.pathRechnungen = resultKonfiguration.getString("Konfiguration_Rechnungen");
                                        ModulHelferlein.pathSicherung = resultKonfiguration.getString("Konfiguration_Sicherung");
                                        ModulHelferlein.pathRezensionen = resultKonfiguration.getString("Konfiguration_Mahnungen");
                                        ModulHelferlein.pathKonfiguration = resultKonfiguration.getString("Konfiguration_Termine");
                                        ModulHelferlein.pathBuchprojekte = resultKonfiguration.getString("Konfiguration_Schriftverkehr");
                                        ModulHelferlein.pathSteuer = resultKonfiguration.getString("Konfiguration_Steuer");
                                    } // if
                                } // while
                            } // if
                        } else { // tbl_konfiguration existiert nicht
                            ModulHelferlein.Fehlermeldung("Tabelle Konfiguration muss erstellt werden!");
                            ModulHelferlein.pathBerichte = "";
                            ModulHelferlein.pathEinnahmen = "";
                            ModulHelferlein.pathAusgaben = "";
                            ModulHelferlein.pathUmsaetze = "";
                            ModulHelferlein.pathRechnungen = "";
                            ModulHelferlein.pathSicherung = "";
                            ModulHelferlein.pathRezensionen = "";
                            ModulHelferlein.pathKonfiguration = "";
                            ModulHelferlein.pathBuchprojekte = "";
                            ModulHelferlein.pathBenutzer = "";
                            ModulHelferlein.CHMVBenutzer = "";
                        } // tbl_konfiguration existiert nicht
                    } // if shown tbl_benutzer
                    else {
                        ModulHelferlein.Fehlermeldung("Tabelle Benutzer muss erstellt werden!");
                        ModulHelferlein.CHMVBenutzer = "";
                    } // tbl_benutzer existiert nicht

                    PrintWriter pWriter = null;

                    System.out.println("Programm ist gestartet");
                    try {
                        pWriter = new PrintWriter(new BufferedWriter(new FileWriter(ModulHelferlein.Semaphore)));
                        pWriter.println(ModulHelferlein.CHMVBenutzer);
                    } catch (IOException ioe) {
                        ModulHelferlein.Fehlermeldung("Semaphore schreiben", ioe.getMessage());
                    } finally {
                        if (pWriter != null) {
                            pWriter.flush();
                            pWriter.close();
                        }
                    }
                } catch (SQLException exept) {
                    ModulHelferlein.Fehlermeldung(
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
        menuItemVerlagsfuerhrungFlyer = new JMenuItem();
        jMenuVerlagsfuehrungDatensicherung = new JMenu();
        jMenuItemVerlagsf�hrungDatensicherungKonfiguration = new JMenuItem();
        jMenuItemVerlagsf�hrungDatensicherungDatensicherung = new JMenuItem();
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
        menuItemBenutzer = new JMenuItem();
        jMenuItem6 = new JMenuItem();
        jMenuItem7 = new JMenuItem();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Carola Hartmann Miles Verlag");
        setResizable(false);
        setIconImage(new ImageIcon(getClass().getResource("/milesVerlagMain/CarolaHartmannMilesVerlag.png")).getImage());
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                formWindowClosing(e);
            }
        });
        var contentPane = getContentPane();
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

                //---- menuItemVerlagsfuerhrungFlyer ----
                menuItemVerlagsfuerhrungFlyer.setText("Flyer erstellen ...");
                menuItemVerlagsfuerhrungFlyer.addActionListener(e -> menuItemVerlagsfuerhrungFlyerActionPerformed(e));
                jMenuVerlagsfuehrung.add(menuItemVerlagsfuerhrungFlyer);
                jMenuVerlagsfuehrung.addSeparator();

                //======== jMenuVerlagsfuehrungDatensicherung ========
                {
                    jMenuVerlagsfuehrungDatensicherung.setText("Datensicherung");

                    //---- jMenuItemVerlagsf�hrungDatensicherungKonfiguration ----
                    jMenuItemVerlagsf�hrungDatensicherungKonfiguration.setText("Konfiguration ...");
                    jMenuItemVerlagsf�hrungDatensicherungKonfiguration.addActionListener(e -> jMenuItemVerlagsf�hrungDatensicherungKonfigurationActionPerformed(e));
                    jMenuVerlagsfuehrungDatensicherung.add(jMenuItemVerlagsf�hrungDatensicherungKonfiguration);

                    //---- jMenuItemVerlagsf�hrungDatensicherungDatensicherung ----
                    jMenuVerlagsfuehrungDatensicherung.addSeparator();
                    jMenuItemVerlagsf�hrungDatensicherungDatensicherung.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK));
                    jMenuItemVerlagsf�hrungDatensicherungDatensicherung.setText("Datensicherung");
                    jMenuItemVerlagsf�hrungDatensicherungDatensicherung.addActionListener(e -> jMenuItemVerlagsf�hrungDatensicherungDatensicherungActionPerformed(e));
                    jMenuVerlagsfuehrungDatensicherung.add(jMenuItemVerlagsf�hrungDatensicherungDatensicherung);
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
                    jMenuItemBerichteStammdatenVerlagsprogramm.setText("Verlagsprogramm ...");
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
            }
            jMenuBar1.add(jMenuHilfe);

            //---- menuItemBenutzer ----
            menuItemBenutzer.setText("-");
            menuItemBenutzer.setForeground(Color.blue);
            menuItemBenutzer.setFont(new Font("Segoe UI", Font.BOLD, 10));
            jMenuBar1.add(menuItemBenutzer);
        }
        setJMenuBar(jMenuBar1);

        {
            // compute preferred size
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
        System.out.println("Dialog Hilfe ueber");
        _DlgUeber.main(null);
    }//GEN-LAST:event_jMenuItemHilfeUeberActionPerformed

    private void jMenuItemHilfeHilfeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemHilfeHilfeActionPerformed
        // TODO add your handling code here:
        System.out.println("Dialog Hilfe");
        //
        //try {
        //    Runtime.getRuntime().exec("cmd /c start " + System.getProperty("user.dir") + "/miles-Verlag-Handbuch.html");
        //} catch (IOException exept) {
        //    ModulHelferlein.Fehlermeldung("Exception: " + exept.getMessage());
        //}
        try {
            Runtime.getRuntime().exec("cmd.exe /c CarolaHartmannMilesVerlagHandbuch.pdf");
        } catch (IOException exept) {
            ModulHelferlein.Fehlermeldung("Hilfe", "Handbuch �ffen: IO-Exception: ", exept.getMessage());
        } // try Handbuch �ffnen
    }//GEN-LAST:event_jMenuItemHilfeHilfeActionPerformed

    private void jMenuItemBerichteVerkaufsstatistikGesamtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBerichteVerkaufsstatistikGesamtActionPerformed
        // TODO add your handling code here:
        System.out.println("Dialog Bericht Verkaufsstatistik gesamt");
        _DlgVerkaufsstatistikCHMVGesamt.main(null);
    }//GEN-LAST:event_jMenuItemBerichteVerkaufsstatistikGesamtActionPerformed

    private void jMenuItemBerichteKonfigurationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBerichteKonfigurationActionPerformed
        System.out.println("Dialog Bericht Konfiguration");
        try {
            // TODO add your handling code here:
            berKonfiguration.bericht();
        } catch (IOException ex) {
            ModulHelferlein.Fehlermeldung("IO-Exception: " + ex.getMessage());
            //Logger.getLogger(CarolaHartmannMilesVerlag.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }//GEN-LAST:event_jMenuItemBerichteKonfigurationActionPerformed

    private void jMenuItemBerichteTermineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBerichteTermineActionPerformed
        // TODO add your handling code here:
        System.out.println("Dialog Bericht Termine");
        berTermine.bericht();
    }//GEN-LAST:event_jMenuItemBerichteTermineActionPerformed

    private void jMenuItemStammdatenBuchprojekteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemStammdatenBuchprojekteActionPerformed
        // TODO add your handling code here:
        //dbBuch fenster = new dbBuch();
        //fenster.dbbuch();
        System.out.println("Dialog Verwalten Buchprojekte");
        VerwaltenDatenbankBuch.main(null);
    }//GEN-LAST:event_jMenuItemStammdatenBuchprojekteActionPerformed

    private void jMenuItemStammdatenAdressenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemStammdatenAdressenActionPerformed
        // TODO add your handling code here:
        // dbAdresse fenster = new dbAdresse();
        // fenster.dbadressen();
        System.out.println("Dialog Verwalten Adressen");
        VerwaltenDatenbankAdressen.main(null);
    }//GEN-LAST:event_jMenuItemStammdatenAdressenActionPerformed

    private void jMenuItemVerlagsf�hrungDatensicherungKonfigurationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemVerlagsf�hrungDatensicherungKonfigurationActionPerformed
        // TODO add your handling code here:
        System.out.println("Dialog Konfiguration Sicherung");
        VerwaltenDatenbankKonfigurationSicherung.main(null);
    }//GEN-LAST:event_jMenuItemVerlagsf�hrungDatensicherungKonfigurationActionPerformed

    private void jMenuItemVerlagsfuehrungInternetBODActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemVerlagsfuehrungInternetBODActionPerformed
        // TODO add your handling code here:
        System.out.println("Webseite bod aufrufen");
        try {
            Runtime.getRuntime().exec("cmd /c start  https://www.bod.de/mybod.html");
        } catch (IOException e) {
            ModulHelferlein.Fehlermeldung("IOException: " + e.getMessage());
        }
    }//GEN-LAST:event_jMenuItemVerlagsfuehrungInternetBODActionPerformed

    private void jMenuItemVerlagsfuehrungInternetVolksbankActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemVerlagsfuehrungInternetVolksbankActionPerformed
        // TODO add your handling code here:
        System.out.println("Webseite volksbank aufrufen");
        try {
            Runtime.getRuntime().exec(
                    "cmd /c start https://www.berliner-volksbank.de/banking-private/entry?trackid=piwik908caf721f22c378");
        } catch (IOException e) {
            ModulHelferlein.Fehlermeldung("IOException: " + e.getMessage());
        }
    }//GEN-LAST:event_jMenuItemVerlagsfuehrungInternetVolksbankActionPerformed

    private void jMenuItemVerlagsfuehrungInternetPrintusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemVerlagsfuehrungInternetPrintusActionPerformed
        // TODO add your handling code here:
        System.out.println("Webseite printus aufrufen");
        try {
            Runtime.getRuntime().exec(
                    "cmd /c start http://www.printus.de");
        } catch (IOException e) {
            ModulHelferlein.Fehlermeldung("IOException: " + e.getMessage());
        }
    }//GEN-LAST:event_jMenuItemVerlagsfuehrungInternetPrintusActionPerformed

    private void jMenuItemVerlagsfuehrungInternetVistaprintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemVerlagsfuehrungInternetVistaprintActionPerformed
        // TODO add your handling code here:
        System.out.println("Webseite vistaprint aufrufen");
        try {
            Runtime.getRuntime().exec(
                    "cmd /c start http://www.vistaprint.de");
        } catch (IOException e) {
            ModulHelferlein.Fehlermeldung("IOException: " + e.getMessage());
        }
    }//GEN-LAST:event_jMenuItemVerlagsfuehrungInternetVistaprintActionPerformed

    private void jMenuItemVerlagsfuehrungInternetAmazonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemVerlagsfuehrungInternetAmazonActionPerformed
        // TODO add your handling code here:
        System.out.println("Webseite amazon aufrufen");
        try {
            Runtime.getRuntime().exec(
                    "cmd /c start http://www.amazon.de");
        } catch (IOException e) {
            ModulHelferlein.Fehlermeldung("IO-Exception: " + e.getMessage());
        }
    }//GEN-LAST:event_jMenuItemVerlagsfuehrungInternetAmazonActionPerformed

    private void jMenuItemVerlagsfuehrungInternetVLBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemVerlagsfuehrungInternetVLBActionPerformed
        // TODO add your handling code here:
        System.out.println("Webseite VLB aufrufen");
        try {
            Runtime.getRuntime().exec("cmd /c start https://www.vlb.de/login.html");
        } catch (IOException e) {
            ModulHelferlein.Fehlermeldung("IOException: " + e.getMessage());
        }
    }//GEN-LAST:event_jMenuItemVerlagsfuehrungInternetVLBActionPerformed

    private void jMenuItemEndeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemEndeActionPerformed
        // TODO add your handling code here:
        System.out.println("Programm beenden");
        try {
            result.close();
            SQLAnfrage.close();
            conn.close();
        } catch (SQLException e) {
            ModulHelferlein.Fehlermeldung("SQL-Exception: SQL-Anfrage nicht moeglich: " + e.getMessage());
        }

        System.exit(0);
    }//GEN-LAST:event_jMenuItemEndeActionPerformed

    private void jMenuItemServerBestellnummerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemServerBestellnummerActionPerformed
        // TODO add your handling code here:
        System.out.println("Dialog Bestellnummer verwalten");
        VerwaltenDatenbankBestellnummer.main(null);
    }//GEN-LAST:event_jMenuItemServerBestellnummerActionPerformed

    private void jMenuItemServerKonfigurationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemServerKonfigurationActionPerformed
        // TODO add your handling code here:
        System.out.println("Dialog Konfiguration verwalten");
        String[] args = {ModulHelferlein.CHMVBenutzer};
        VerwaltenDatenbankKonfigurationPfade.main(args);
    }//GEN-LAST:event_jMenuItemServerKonfigurationActionPerformed

    private void jMenuItemVerlagsfuehrungSteuerEURActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemVerlagsfuehrungSteuerEURActionPerformed
        // TODO add your handling code here:
        System.out.println("Dialog Einnahmenueberschussrechnung");
        ModulHelferlein.Infomeldung("noch nicht implementiert");
    }//GEN-LAST:event_jMenuItemVerlagsfuehrungSteuerEURActionPerformed

    private void jMenuItemVerlagsfuehrungSteuerUstrEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemVerlagsfuehrungSteuerUstrEActionPerformed
        // TODO add your handling code here:
        System.out.println("Dialog Umsatzsteuererkl�rung");
        ModulHelferlein.Infomeldung("noch nicht implementiert");
    }//GEN-LAST:event_jMenuItemVerlagsfuehrungSteuerUstrEActionPerformed

    private void jMenuItemVerlagsfuehrungSteuerUstrVAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemVerlagsfuehrungSteuerUstrVAActionPerformed
        // TODO add your handling code here:
        System.out.println("Dialog Umsatzsteuervoranmeldung");
        ModulAbrechnungUSteuerVorA fenster = new ModulAbrechnungUSteuerVorA();
        fenster.dlgUStrVor();
    }//GEN-LAST:event_jMenuItemVerlagsfuehrungSteuerUstrVAActionPerformed

    private void jMenuItemVerlagsfuehrungSteuerKonfigurationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemVerlagsfuehrungSteuerKonfigurationActionPerformed
        // TODO add your handling code here:
        System.out.println("Dialog Konfiguration Steuerdaten");
        VerwaltenDatenbankKonfigurationSteuer.main(null);
    }//GEN-LAST:event_jMenuItemVerlagsfuehrungSteuerKonfigurationActionPerformed

    private void jMenuItemSoftwareXAMPPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSoftwareXAMPPActionPerformed
        // TODO add your handling code here:
        System.out.println("Dialog XAMPP starten");
        try {
            Runtime.getRuntime().exec("C:/xampp/xampp-control.exe");
        } catch (IOException e) {
            ModulHelferlein.Fehlermeldung("IO-Exception: " + e.getMessage());
        }
    }//GEN-LAST:event_jMenuItemSoftwareXAMPPActionPerformed

    private void jMenuItemSoftwareSQLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSoftwareSQLActionPerformed
        // TODO add your handling code here:
        System.out.println("Dialog Datenbank verwalten");
        try {
            Runtime.getRuntime().exec(
                    "cmd /c start http://localhost:8888/phpmyadmin/");
        } catch (IOException e) {
            ModulHelferlein.Fehlermeldung("IO-Exception: " + e.getMessage());
        }
    }//GEN-LAST:event_jMenuItemSoftwareSQLActionPerformed

    private void jMenuItemSoftwareDBErstellenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSoftwareDBErstellenActionPerformed
        // TODO add your handling code here:
        System.out.println("Dialog Datenbank erstellen");
        _DlgDatenbankErstellen.main(null);
    }//GEN-LAST:event_jMenuItemSoftwareDBErstellenActionPerformed

    private void jMenuItemSoftwareDBLoeschenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSoftwareDBLoeschenActionPerformed
        // TODO add your handling code here:
        System.out.println("Dialog Datenbank l�schen");
        _DlgDatenbankLoeschen.main(null);
    }//GEN-LAST:event_jMenuItemSoftwareDBLoeschenActionPerformed

    private void jMenuItemSoftwareDBSichernActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSoftwareDBSichernActionPerformed
        // TODO add your handling code here:
        System.out.println("Dialog Datenbank sichern");
        backupDatenbank();
    }//GEN-LAST:event_jMenuItemSoftwareDBSichernActionPerformed

    private void jMenuItemSoftwareDBWiederherstellenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSoftwareDBWiederherstellenActionPerformed
        // TODO add your handling code here:
        System.out.println("Dialog Datenbank wiederherstellen");
        String Filename = "";
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Datenbanksicherung", "sql");
        JFileChooser chooser = new JFileChooser(ModulHelferlein.pathSicherung);

        chooser.setMultiSelectionEnabled(false);
        chooser.setFileFilter(filter);

        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int Ergebnis = chooser.showDialog(null, "Sicherungsdatei w�hlen");

        if (Ergebnis == JFileChooser.APPROVE_OPTION) {
            Filename = chooser.getSelectedFile().getName();
            String cmdline = "";
            try {
                if (ModulHelferlein.dbLive) {
                    cmdline = "C:\\xampp\\mysql\\bin\\mysql.exe -P"+ModulHelferlein.dbPort+
                                                          " -u"+ModulHelferlein.dbUser+
                                                          " -p"+ModulHelferlein.dbPassword
                            + " milesverlag < " + ModulHelferlein.pathSicherung
                            + "\\"
                            + Filename;
                } else {
                    cmdline = "C:\\xampp\\mysql\\bin\\mysql.exe -P"+ModulHelferlein.dbPort+
                                                          " -u"+ModulHelferlein.dbUser+
                                                          " -p"+ModulHelferlein.dbPassword
                            + " milesverlag-train < " + ModulHelferlein.pathSicherung
                            + "\\"
                            + Filename;
                }
                System.out.println("Datenbankwiederherstellung: cmd /c " + cmdline);
                Runtime.getRuntime().exec("cmd /c " + cmdline);
                ModulHelferlein.Infomeldung("Datenbank wurde wiederhergestellt");
            } catch (IOException e) {
                ModulHelferlein.Fehlermeldung("IO-Exception: " + e.getMessage());
            }
        }
    }//GEN-LAST:event_jMenuItemSoftwareDBWiederherstellenActionPerformed

    private void jMenuItemTabellenLeerenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemTabellenLeerenActionPerformed
        // TODO add your handling code here:
        System.out.println("Dialog Tabellen leeren");
        _DlgTabellenLeeren.main(null);
    }//GEN-LAST:event_jMenuItemTabellenLeerenActionPerformed

    private void jMenuItemSoftwareTabellenLoeschenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSoftwareTabellenLoeschenActionPerformed
        // TODO add your handling code here:
        System.out.println("Dialog Tabellen l�schen");
        _DlgTabellenLoeschen.main(null);
    }//GEN-LAST:event_jMenuItemSoftwareTabellenLoeschenActionPerformed

    private void jMenuItemSoftwareTabellenErstellenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSoftwareTabellenErstellenActionPerformed
        // TODO add your handling code here:
        System.out.println("Dialog Tabellen erstellen");
        _DlgTabellenErstellen.main(null);
    }//GEN-LAST:event_jMenuItemSoftwareTabellenErstellenActionPerformed

    private void jMenuItemVerlagsf�hrungDatensicherungDatensicherungActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemVerlagsf�hrungDatensicherungDatensicherungActionPerformed
        // TODO add your handling code here:
        System.out.println("Dialog Datensicherung");
        _DlgDatensicherung fenster = new _DlgDatensicherung();
        fenster.Dialog();
    }//GEN-LAST:event_jMenuItemVerlagsf�hrungDatensicherungDatensicherungActionPerformed

    private void jMenuItemVerlagsfuehrungWerbungActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemVerlagsfuehrungWerbungActionPerformed
        // TODO add your handling code here:
        System.out.println("Dialog Werbung versenden");
        _DlgMail.main(null);
    }//GEN-LAST:event_jMenuItemVerlagsfuehrungWerbungActionPerformed

    private void jMenuItemVerlagsfuehrungTermineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemVerlagsfuehrungTermineActionPerformed
        // TODO add your handling code here:
        System.out.println("Dialog Termine verwalten");
        VerwaltenDatenbankTermine.main(null);
    }//GEN-LAST:event_jMenuItemVerlagsfuehrungTermineActionPerformed

    private void jMenuItemVerlagsfuehrungFremdwaehrungActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemVerlagsfuehrungFremdwaehrungActionPerformed
        // TODO add your handling code here:
        System.out.println("Dialog Waehrungen verwalten");
        VerwaltenDatenbankKonfigurationWaehrung.main(null);
    }//GEN-LAST:event_jMenuItemVerlagsfuehrungFremdwaehrungActionPerformed

    private void jMenuItemStammdatenEMailVerteilerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemStammdatenEMailVerteilerActionPerformed
        // TODO add your handling code here:
        System.out.println("Dialog Mailverteiler verwalten");
        VerwaltenDatenbankMailverteiler.main(null);
    }//GEN-LAST:event_jMenuItemStammdatenEMailVerteilerActionPerformed

    private void jMenuItemStammdatenEMailAdressenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemStammdatenEMailAdressenActionPerformed
        // TODO add your handling code here:
        System.out.println("Dialog Mailadressen verwalten");
        VerwaltenDatenbankMailadressen.main(null);
    }//GEN-LAST:event_jMenuItemStammdatenEMailAdressenActionPerformed

    private void jMenuItemBewegungsdatenBuchbestellungenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBewegungsdatenBuchbestellungenActionPerformed
        // TODO add your handling code here:
        System.out.println("Dialog Bestellungen bearbeiten");
        VerwaltenDatenbankBestellung.main(null);
        //dbBestellung fenster = new dbBestellung();
        //fenster.dbbestellung();
    }//GEN-LAST:event_jMenuItemBewegungsdatenBuchbestellungenActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        // TODO add your handling code here:
        System.out.println("Dialog Einnahmen bearbeiten");
        VerwaltenDatenbankEinnahmen.main(null);
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItemBewegungsdatenAusgabenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBewegungsdatenAusgabenActionPerformed
        // TODO add your handling code here:
        System.out.println("Dialog Ausgaben bearbeiten");
        VerwaltenDatenbankAusgaben.main(null);
    }//GEN-LAST:event_jMenuItemBewegungsdatenAusgabenActionPerformed

    private void jMenuItemBewegungsdatenRezensionenErfassenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBewegungsdatenRezensionenErfassenActionPerformed
        // TODO add your handling code here:
        System.out.println("Dialog Rezensionen erfassen");
        VerwaltenDatenbankRezensionen.main(null);
    }//GEN-LAST:event_jMenuItemBewegungsdatenRezensionenErfassenActionPerformed

    private void jMenuItemVerlagsfuehrungHonorarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemVerlagsfuehrungHonorarActionPerformed
        // TODO add your handling code here:
        System.out.println("Dialog Honorarabrechnung");
        ModulHonorarProgressBar.main(null);
    }//GEN-LAST:event_jMenuItemVerlagsfuehrungHonorarActionPerformed

    private void jMenuItemBerichteZahlungenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBerichteZahlungenActionPerformed
        // TODO add your handling code here:
        System.out.println("Dialog Bericht offene Zahlungen");
        _DlgUebersichtZahlungen.main(null);
    }//GEN-LAST:event_jMenuItemBerichteZahlungenActionPerformed

    private void jMenuItemBerichteMahnungenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBerichteMahnungenActionPerformed
        // TODO add your handling code here:
        System.out.println("Dialog Bericht offene Rechnungen/Mahnungen");
        _DlgUebersichtMahnungen.main(null);
    }//GEN-LAST:event_jMenuItemBerichteMahnungenActionPerformed

    private void jMenuItemBerichteUmsaetzeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBerichteUmsaetzeActionPerformed
        // TODO add your handling code here:
        System.out.println("Dialog Bericht Umsaetze");
        _DlgUebersichtUmsaetze.main(null);
    }//GEN-LAST:event_jMenuItemBerichteUmsaetzeActionPerformed

    private void jMenuItemBerichteAusgabenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBerichteAusgabenActionPerformed
        // TODO add your handling code here:
        System.out.println("Dialog Bericht Uebersicht Ausgaben");
        _DlgUebersichtAusgaben.main(null);
    }//GEN-LAST:event_jMenuItemBerichteAusgabenActionPerformed

    private void jMenuItemBerichteEinnahmenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBerichteEinnahmenActionPerformed
        // TODO add your handling code here:
        System.out.println("Dialog Bericht Uebersicht Einnahmen");
        _DlgUebersichtEinnahmen.main(null);
    }//GEN-LAST:event_jMenuItemBerichteEinnahmenActionPerformed

    private void jMenuItemBewegungsdatenRezensionenVersendenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBewegungsdatenRezensionenVersendenActionPerformed
        // TODO add your handling code here:
        System.out.println("Dialog Rezensionen erfassen");
        VerwaltenDatenbankRezensionAus.main(null);
    }//GEN-LAST:event_jMenuItemBewegungsdatenRezensionenVersendenActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        // semaphore l�schen
        File file = new File(ModulHelferlein.Semaphore);
        if (file.exists()) {
            file.delete();
        }
        System.out.println("Semaphore gel�scht");
        backupDatenbank();
        ModulAusgabe.AusgabeSchliessen();
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
            ModulHelferlein.Fehlermeldung("IOException: " + e.getMessage());
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
        String SQL = "";
        SQL = JOptionPane.showInputDialog("", "SQL-Befehl?");
        if (SQL.isEmpty())  {
        } else {
            try {
                Statement SQLAnfrageSQL = conn.createStatement();
                if (JOptionPane.showConfirmDialog(null,
                        "Soll der Befehl -> " + SQL + " <- wirklich ausgf�hrt werden?",
                        "Best�tigung",
                        JOptionPane.YES_NO_OPTION) == 0) {
                    SQLAnfrageSQL.executeUpdate(SQL);
                }
            } catch (SQLException ex) {
                ModulHelferlein.Fehlermeldung("SQL-Befehl ausf�hren", "SQL-Exception", ex.getMessage());
            }
        }
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItemVerlagsfuehrungBriefpapierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemVerlagsfuehrungBriefpapierActionPerformed
        // TODO add your handling code here:
        ModulBriefpapierDrucken.PDF();
    }//GEN-LAST:event_jMenuItemVerlagsfuehrungBriefpapierActionPerformed

    private void jMenuItemBriefpapierAdresseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBriefpapierAdresseActionPerformed
        // TODO add your handling code here:
        //_DlgBrief.main(null);
        VerwaltenDatenbankBrief.main(null);
    }//GEN-LAST:event_jMenuItemBriefpapierAdresseActionPerformed

    private void jMenuItemVerlagsfuerhungInternetIBANActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemVerlagsfuerhungInternetIBANActionPerformed
        // TODO add your handling code here:
        try {
            Runtime.getRuntime().exec("cmd /c start https://www.iban-rechner.de");
        } catch (IOException e) {
            ModulHelferlein.Fehlermeldung("Aufruf IBAN-Rechner", "IO-Exception: ", e.getMessage());
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

        if (parameter != 6) {
            ModulHelferlein.Fehlermeldung("Die Anzahl der Parameter stimmt nicht!");
            System.exit(-1);
        }
        ModulHelferlein.dbUrl = args[0];
        ModulHelferlein.dbPort = args[1];
        ModulHelferlein.dbName = args[2];
        ModulHelferlein.dbUser = args[3];
        ModulHelferlein.dbPassword = args[4];
        ModulHelferlein.dbLive = "live".equals(args[5]);

        FlatLightLaf.install();
        //try {
        //    UIManager.setLookAndFeel( new FlatLightLaf() );
        //} catch( Exception ex ) {
        //    System.err.println( "Failed to initialize Look and Feel" );
        //}
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
    private JMenuItem menuItemVerlagsfuerhrungFlyer;
    private JMenu jMenuVerlagsfuehrungDatensicherung;
    private JMenuItem jMenuItemVerlagsf�hrungDatensicherungKonfiguration;
    private JMenuItem jMenuItemVerlagsf�hrungDatensicherungDatensicherung;
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


    private class FlyerErstellen extends AbstractAction {
        private FlyerErstellen() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            putValue(NAME, "Flyer erstellen ...");
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // TODO add your code here
           
        }
    }
}
