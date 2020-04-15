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

import java.awt.Color;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import static milesVerlagMain.Modulhelferlein.Ausgabe;
import static milesVerlagMain.Modulhelferlein.AusgabeDB;
import static milesVerlagMain.Modulhelferlein.AusgabeRB;

import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.elster.eric.wrapper.CertificateParameters;
import de.elster.eric.wrapper.Eric;
import de.elster.eric.wrapper.Eric.SendResponse;
import de.elster.eric.wrapper.exception.EricException;
import de.elster.eric.wrapper.exception.WrapperException;
import de.elster.eric.wrapper.EricResult;
import de.elster.eric.wrapper.Errorcode;
import de.elster.eric.wrapper.Finanzamt;
import de.elster.eric.wrapper.FinanzamtLand;
import de.elster.eric.wrapper.PrintParameters;

import java.util.logging.Level;
import java.util.logging.Logger;
import static milesVerlagMain.Modulhelferlein.Linie;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import static org.apache.pdfbox.pdmodel.common.PDRectangle.A4;

/**
 * Haupt-Klasse für die Verwaltung des miles-Verlages
 *
 * @author Thomas Zimmermann
 *
 */
public class ModulAbrechnungUSteuerVorA implements ActionListener {

    final JFrame meinFenster = new JFrame("Carola Hartmann Miles Verlag - Verlagsverwaltung");
    final JFrame f4 = new JFrame("Carola Hartmann Miles Verlag - Verlagsverwaltung");
    final JFrame f5 = new JFrame("Carola Hartmann Miles Verlag - Verlagsverwaltung");

    private JButton btnStrNr = new JButton("<html><body>Steuernummer<br>und Finanzamt</body></html>");

    JButton Anfang = new JButton("<<");
    JButton Zurueck = new JButton("<");
    JButton Vor = new JButton(">");
    JButton Ende = new JButton(">>");
    JButton Update = new JButton("!");
    JButton Einfuegen = new JButton("+");
    JButton Loeschen = new JButton("-");
    JButton Suchen = new JButton("?");
    JButton WSuchen = new JButton("...");
    JButton Senden = new JButton("^");
    JButton Speichern = new JButton("v");
    JButton Testen = new JButton("T");
    JButton Drucken = new JButton("D");
    JButton Schliessen = new JButton("X");

    JButton btnSchliessen = new JButton("Schließen");
    JButton btnSchliessen5 = new JButton("Schließen");
    JButton btnSpeichern5 = new JButton("Speichern");

    private final JRadioButton rbMonat = new JRadioButton("Monatlich");
    private final JRadioButton rbQuartal = new JRadioButton("Quartalsweise");
    private final ButtonGroup cbZeitraum = new ButtonGroup();

    JComboBox<?> field_Monat = new JComboBox<Object>(Modulhelferlein.MonatListe);
    JComboBox<?> field_Quartal = new JComboBox<Object>(Modulhelferlein.QuartalListe);

    private final JLabel FAName = new JLabel();

    private final JCheckBox cb10 = new JCheckBox("Berichtigte Anmeldung");
    private final JCheckBox cb22 = new JCheckBox("Belege sind beigefügt");
    private final JCheckBox cb29 = new JCheckBox("Verrechnung des Erstattungsbetrages");
    private final JCheckBox cb26 = new JCheckBox("SEPA-Lastschriftmandat wird widerrufen");

    private final JComboBox<String> cbLaender = new JComboBox<String>();
    private final JComboBox<String> cbFALaender = new JComboBox<String>();

    private final JTextField SteuerNr = new JTextField();
    private final JTextField field_Zeile3 = new JTextField();
    private final JTextField field_Zeile4 = new JTextField();
    private final JTextField field_Zeile12 = new JTextField();
    private final JTextField field_Zeile13_1 = new JTextField();
    private final JTextField field_Zeile13_21 = new JTextField();
    private final JTextField field_Zeile13_22 = new JTextField();
    private final JTextField field_Zeile14 = new JTextField();
    private final JTextField field_Zeile15 = new JTextField();

    private final JTextField field_KZ66 = new JTextField("0.00");
    private final JTextField field_KZ83 = new JTextField("0.00");
    private final JTextField field_KZ81 = new JTextField("0");
    private final JTextField field_KZ86 = new JTextField("0");
    private final JTextField field_KZ35 = new JTextField("0");
    private final JTextField field_KZ36 = new JTextField("0.00");
    private final JTextField field_Zeile26 = new JTextField();
    private final JTextField field_Zeile27 = new JTextField();
    private final JTextField field_Datum = new JTextField();

    Integer count = 0;
    Integer countMax = 0;
    JTextField field_count = new JTextField();
    JTextField field_countMax = new JTextField();

    Connection conn;
    Statement SQLAnfrage;
    Statement SQLAnfrage2;
    Statement SQLAnfrage3;
    ResultSet result;
    ResultSet result2;
    ResultSet result3;

    Statement SQLAnfrageBest;
    Statement SQLAnfrageBuch;
    Statement SQLAnfragePreis;
    ResultSet resultBest;
    ResultSet resultBuch;
    ResultSet resultPreis;

    boolean resultIsEmpty = true;
    boolean boolXMLgetestet = false;
    boolean boolXMLgespeichert = false;

    FileWriter writer;
    File file;

    String strXMLdatei = "";
    String Finanzamtname = "";
    String Finanzamtnummer = "";
    String FinanzamtTelefon = "";
    String FinanzamtErgaenzung = "";
    String FinanzamtStrasse = "";
    String FinanzamtPLZOrt = "";
    String FinanzamteMail = "";
    String FinanzamtBank = "";
    String FinanzamtIBAN = "";
    String FinanzamtBIC = "";

    String von = "";
    String bis = "";

    Eric eric;
    List<FinanzamtLand> finanzamtLaender = null;
    List<Finanzamt> finanzaemter = new ArrayList<Finanzamt>();

    String certificate = "";
    String certificatePin = "";
    String ericPath = getCurrentDirectory();
    String logPath = getCurrentDirectory();

    String Testmerker = "700000004";
    String datenartVersion = "UStVA_2015";
    String steuersatz = "";
    String outputFileName = "";

    Double UStr81 = 0D;
    Double UStr83 = 0D;
    Double UStr86 = 0D;
    Double UStr66 = 0D;
    Double UStr35 = 0D;
    Double Gesamtsumme = 0D;
    Double Netto = 0D;
    Double Ustr = 0D;
    Double BestellungZeile = 0D;
    Double GesamtNetto = 0D;
    Double GesamtUstr = 0D;

    private static String getCurrentDirectory() {
        return new File(".").getAbsolutePath();
    }

    /**
     * Prüfe eine einzelne Steuernummer
     */
    private boolean checkStNr(Eric eric, String stNr) {
        Errorcode rc = eric.checkStNr(stNr);
        if (rc == Errorcode.ERIC_OK) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Lese eine Datei in ein Byte-Array
     */
    private static byte[] readFile(File file) throws IOException {
        RandomAccessFile f = new RandomAccessFile(file, "r");
        try {
            byte[] data = new byte[(int) file.length()];
            f.readFully(data);
            return data;
        } finally {
            f.close();
        }
    }


    /**
     * Validiere und Sende Steuerfall
     */
    private void validateAndSendTaxCase(Eric eric, Boolean einreichen) throws EricException, WrapperException {
        f4.setSize(500, 300);
        f4.setResizable(false);
        f4.setLayout(null);
        f4.setLocation(220, 80);

        JLabel Titel = new JLabel("Validiere und sende den Steuerfall");
        f4.add(Titel);
        Titel.setBounds(10, 10, 200, 20);

        JLabel lblLese = new JLabel("-> lese Steuersatz ...");
        lblLese.setBounds(10, 50, 390, 20);
        lblLese.setVisible(false);
        f4.add(lblLese);

        JLabel lblfertig_0 = new JLabel("... fertig");
        lblfertig_0.setBounds(400, 50, 100, 20);
        lblfertig_0.setVisible(false);
        f4.add(lblfertig_0);

        JLabel lblVal = new JLabel("-> validiere Steuersatz ...");
        lblVal.setBounds(10, 80, 390, 20);
        lblVal.setVisible(false);
        f4.add(lblVal);

        JLabel lblfertig_1 = new JLabel("... fertig");
        lblfertig_1.setBounds(400, 80, 100, 20);
        lblfertig_1.setVisible(false);
        f4.add(lblfertig_1);

        JLabel lblEVal = new JLabel("");
        lblEVal.setBounds(10, 110, 390, 20);
        lblEVal.setVisible(false);
        f4.add(lblEVal);

        JLabel lblSD = new JLabel("-> Sende und drucke den Steuerdatensatz...");
        lblSD.setBounds(10, 140, 390, 20);
        lblSD.setVisible(false);
        f4.add(lblSD);

        JLabel lblAut = new JLabel("    - mit Authentisierung ...");
        lblAut.setBounds(10, 170, 390, 20);
        lblAut.setVisible(false);
        f4.add(lblAut);

        JLabel lblfertig_2 = new JLabel("... fertig");
        lblfertig_2.setBounds(400, 170, 200, 20);
        lblfertig_2.setVisible(false);
        f4.add(lblfertig_2);

        JLabel lblESD = new JLabel("");
        lblESD.setBounds(10, 110, 390, 20);
        lblESD.setVisible(false);
        f4.add(lblESD);

        JLabel lblfertig_3 = new JLabel("... fertig");
        lblfertig_2.setBounds(400, 140, 200, 20);
        lblfertig_3.setVisible(false);
        f4.add(lblfertig_3);

        btnSchliessen.setBounds(200, 200, 100, 30);
        btnSchliessen.setVisible(false);
        btnSchliessen.addActionListener(this);
        f4.add(btnSchliessen);

        f4.setVisible(true);
        lblLese.setVisible(true);

        String edsXml;

        try {
            edsXml = new String(readFile(new File(steuersatz)), "ISO-8859-15");
            //edsXml = new String(readFile(new File("20150222-Steuerdatensatz-Miles-Verlag.xml")),	"ISO-8859-15");
            lblfertig_0.setVisible(true);
            lblVal.setVisible(true);

            EricResult res;
            try {
                res = eric.validate(datenartVersion, edsXml);
                //res = eric.validate("UStVA_2015", edsXml);
                lblfertig_1.setVisible(true);
                lblEVal.setText("   ... Validierung erfolgreich. " + res);
                lblEVal.setVisible(true);

                if (einreichen) {
                    if (JOptionPane.showConfirmDialog(null, "Soll der Steuerdatensatz gedruckt und versendet werden?") == JOptionPane.YES_OPTION) {
                        lblSD.setVisible(true);

                        CertificateParameters certParams = null;

                        if (certificate != null) {
                            lblAut.setVisible(true);

                            certParams = new CertificateParameters(certificate, certificatePin);
                            //certParams.setCertificatePath(certificate);
                            //certParams.setPin(certificatePin);
                        }

                        SendResponse sendResponse;
                        sendResponse = eric.sendAndPrint(datenartVersion,
                                //sendResponse = eric.sendAndPrint("UStVA_2015",
                                edsXml,
                                certParams,
                                new PrintParameters(0, 0, 0, outputFileName + ".PDF", null));
                        lblfertig_2.setVisible(true);
                        lblEVal.setText("Senden und Drucken erfolgreich");
                        lblEVal.setVisible(true);
                        try {
                            result.moveToInsertRow();
                            result.updateBoolean("USTVA_GESENDET", true);
                            result.insertRow();
                        } catch (SQLException e1) {
                            Modulhelferlein.Fehlermeldung("SQL-Exception: " + e1.getMessage());
                            //e1.printStackTrace();
                        }
                        // Serverantwort speichern	outputFileName + -return.xml
                        String Serverantwort[] = sendResponse.serverResponse.split("</");
                        file = new File(outputFileName + "-return.xml");
                        try {
                            writer = new FileWriter(file);
                            for (int i = 0; i < Serverantwort.length; i++) {
                                if (i > 0) {
                                    writer.write("</");
                                }
                                writer.write(Serverantwort[i]);
                                writer.write(System.getProperty("line.separator"));
                            }
                            writer.flush();
                            writer.close();
                        } catch (IOException e) {
                            Modulhelferlein.Infomeldung("IO-Exception: " + e.getMessage());
                            //e.printStackTrace();
                        } // try
                    } // if
                } // if test
                btnSchliessen.setVisible(true);
            } catch (de.elster.eric.wrapper.exception.EricException | de.elster.eric.wrapper.exception.WrapperException ex) {
                Logger.getLogger(ModulAbrechnungUSteuerVorA.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (UnsupportedEncodingException e) {
            Modulhelferlein.Fehlermeldung("Unerwartete Java-Ausnahme: " + e);
        } catch (IOException e) {
            Modulhelferlein.Fehlermeldung("Konnte Eingabedatensatz nicht einlesen: " + e);
        } // try

    } // void

    /**
     * schließt den Dialog 5 - Steuernummer und Finanzamt
     *
     * @param evt Ereignis, das beim Klicken des Buttons + ausgelöst wurde
     */
    private void KlickBtnSchliessen5(java.awt.event.ActionEvent evt) {
        f5.setVisible(false);
    }

    /**
     * schließt den Dialog 4 - Steuerfall senden
     *
     * @param evt Ereignis, das beim Klicken des Buttons + ausgelöst wurde
     */
    private void KlickBtnSchliessen(java.awt.event.ActionEvent evt) {
        f4.setVisible(false);
    }

    /**
     * speichert Steuernummer und Finanzamt
     *
     * @param evt Ereignis, das beim Klicken des Buttons + ausgelöst wurde
     */
    private void KlickBtnSpeichern5(java.awt.event.ActionEvent evt) {
        if (SteuerNr.getText().length() == 10) {
            if (checkStNr(eric, Finanzamtnummer + "0" + SteuerNr.getText().substring(2, SteuerNr.getText().length()))) {
                field_Zeile3.setText(SteuerNr.getText());
                field_Zeile4.setText(Finanzamtname);
                try {
                    result3.updateString("Konfiguration_Stammdaten", Finanzamtnummer);
                    result3.updateString("Konfiguration_Einnahmen", Finanzamtname);
                    result3.updateString("Konfiguration_Ausgaben", FinanzamtErgaenzung);
                    result3.updateString("Konfiguration_Umsaetze", FinanzamtStrasse);
                    result3.updateString("Konfiguration_Rechnungen", FinanzamtPLZOrt);
                    result3.updateString("Konfiguration_Sicherung", FinanzamtTelefon);
                    result3.updateString("Konfiguration_Mahnungen", FinanzamteMail);
                    result3.updateString("Konfiguration_Termine", FinanzamtBank);
                    result3.updateString("Konfiguration_Schriftverkehr", FinanzamtIBAN);
                    result3.updateString("Konfiguration_Steuer", FinanzamtBIC);
                    result3.updateRow();
                    Modulhelferlein.Infomeldung("Finanzamtsdaten wurden gespeichert!");
                } catch (SQLException | NullPointerException e) {
                    Modulhelferlein.Fehlermeldung("SQL-Exception: " + e.getMessage());
                }
            } else {
                Modulhelferlein.Fehlermeldung("Steuernummer ist fehlerhaft!");
            }
        } else {
            Modulhelferlein.Fehlermeldung("Steuernummer hat keine 10 Stellen!");
        }
    }

    private void LaenderChanged() {
        int Eintrag = cbLaender.getSelectedIndex();
        try {
            if (Eintrag >= 0) {
                finanzaemter = new ArrayList<>();
                finanzaemter.addAll(eric.getFinanzaemter(finanzamtLaender.get(Eintrag).id));
                cbFALaender.removeAllItems();
                finanzaemter.forEach((finanzamt) -> {
                    cbFALaender.addItem(Integer.toString(finanzamt.getId()) + ", " + finanzamt.getName());
                });
            }
        } catch (de.elster.eric.wrapper.exception.EricException | de.elster.eric.wrapper.exception.WrapperException ex) {
            Logger.getLogger(ModulAbrechnungUSteuerVorA.class.getName()).log(Level.SEVERE, null, ex);
        }
    } // void

    private void FALaenderChanged() {
        int Eintrag = cbFALaender.getSelectedIndex();
        try {
            if ((finanzaemter.size() != 0) && (Eintrag >= 0)) {
                String newLine = System.getProperty("line.separator");
                String Adresse = eric.getFinanzamtsdaten(finanzaemter.get(Eintrag).getId()).toString();
                String AdresseSplit[] = Adresse.split(newLine);
                FAName.setText("<html><body>" + AdresseSplit[0] + "<br>"
                        + AdresseSplit[1] + "<br>"
                        + AdresseSplit[2] + "<br>"
                        + AdresseSplit[3] + "<br>"
                        + AdresseSplit[4] + "<br>"
                        + AdresseSplit[5] + "<br>"
                        + AdresseSplit[6] + "<br>"
                        + AdresseSplit[7] + "<br>"
                        + AdresseSplit[13] + "<br>"
                        + AdresseSplit[14] + "<br>"
                        + AdresseSplit[15] + "<br>"
                        + AdresseSplit[23] + "<br>"
                        + AdresseSplit[24] + "<br>"
                        + AdresseSplit[25] + "<br>"
                        + "</body></html>");
                Finanzamtname = eric.getFinanzamtsdaten(finanzaemter.get(Eintrag).getId()).getFAName();
                Finanzamtnummer = eric.getFinanzamtsdaten(finanzaemter.get(Eintrag).getId()).getFANummer();
                FinanzamtTelefon = eric.getFinanzamtsdaten(finanzaemter.get(Eintrag).getId()).getFATelefon();
                FinanzamtErgaenzung = eric.getFinanzamtsdaten(finanzaemter.get(Eintrag).getId()).getFAErgaenzung();
                FinanzamtStrasse = eric.getFinanzamtsdaten(finanzaemter.get(Eintrag).getId()).getFAPLZOrt();
                FinanzamtPLZOrt = eric.getFinanzamtsdaten(finanzaemter.get(Eintrag).getId()).getFATelefon();
                FinanzamteMail = eric.getFinanzamtsdaten(finanzaemter.get(Eintrag).getId()).getFAeMail();
                FinanzamtBank = eric.getFinanzamtsdaten(finanzaemter.get(Eintrag).getId()).getFABank();
                FinanzamtIBAN = eric.getFinanzamtsdaten(finanzaemter.get(Eintrag).getId()).getFAIBAN();
                FinanzamtBIC = eric.getFinanzamtsdaten(finanzaemter.get(Eintrag).getId()).getFABIC();

            } // if
        } catch (de.elster.eric.wrapper.exception.EricException ex) {
            Logger.getLogger(ModulAbrechnungUSteuerVorA.class.getName()).log(Level.SEVERE, null, ex);
        } // try // try // try // try
    }

    /**
     * die Liste der Finanzämter als Auswahldialog
     *
     * @param Eric Eric, das beim Klicken des Buttons + ausgelöst wurde
     */
    private void printFinanzaemter(Eric eric) throws EricException, WrapperException {
        f5.setSize(600, 500);
        f5.setResizable(false);
        f5.setLayout(null);
        f5.setLocation(220, 80);

        JLabel Titel = new JLabel("Eingabe der Steuernummer und des Finanzamtes");
        f5.add(Titel);
        Titel.setBounds(10, 10, 400, 20);
        JLabel Laender = new JLabel("Finanzämter in ");
        f5.add(Laender);
        Laender.setBounds(10, 50, 110, 20);
        cbLaender.setBounds(130, 50, 450, 20);
        cbLaender.addActionListener((ActionEvent e) -> {
            LaenderChanged();
        });
        f5.add(cbLaender);

        JLabel FA = new JLabel("Finanzamt");
        f5.add(FA);
        FA.setBounds(10, 80, 200, 20);

        cbFALaender.setBounds(130, 80, 450, 20);
        cbFALaender.addActionListener((ActionEvent e) -> {
            FALaenderChanged();
        });
        f5.add(cbFALaender);

        JLabel FADaten = new JLabel("Finanzamtdaten");
        f5.add(FADaten);
        FADaten.setBounds(10, 110, 200, 20);

        FAName.setBounds(130, 110, 400, 400);
        FAName.setVerticalAlignment(JLabel.TOP);
        f5.add(FAName);

        JLabel StrNr = new JLabel("Steuernummer");
        f5.add(StrNr);
        StrNr.setBounds(10, 380, 200, 20);

        SteuerNr.setBounds(130, 380, 400, 20);
        f5.add(SteuerNr);

        btnSchliessen5.setBounds(190, 420, 100, 30);
        btnSchliessen5.addActionListener(this);
        f5.add(btnSchliessen5);
        btnSpeichern5.setBounds(300, 420, 100, 30);
        btnSpeichern5.addActionListener(this);
        f5.add(btnSpeichern5);

        f5.setVisible(true);

        try {
            finanzamtLaender = eric.getFinanzamtLaender();

            if (finanzamtLaender.isEmpty()) {
                Modulhelferlein.Fehlermeldung(" Keine Laender erhalten.");
            } else {
                for (int i = 0; i < (finanzamtLaender.size() - 1); i++) {
                    cbLaender.addItem(Integer.toString(i) + ", " + "ID: " + Integer.toString(finanzamtLaender.get(i).getId()) + finanzamtLaender.get(i).getLand());
                } // for
            } // if
            //finanzaemter.addAll(eric.getFinanzaemter(finanzamtLaenderListe.get(0).id));

            /**
             * List<Finanzamt> finanzaemter = new ArrayList<>(); for
             * (FinanzamtLand finanzamtLand : finanzamtLaender) {
             * finanzaemter.addAll(eric.getFinanzaemter(finanzamtLand.id)); }
             */
            finanzaemter.addAll(eric.getFinanzaemter(finanzamtLaender.get(0).id));
            if (finanzaemter.isEmpty()) {
                Modulhelferlein.Fehlermeldung(" Keine Finanzaemter erhalten.");
            } else {
                finanzaemter.forEach((finanzamt) -> {
                    cbFALaender.addItem(Integer.toString(finanzamt.getId()) + ", " + finanzamt.getName());
                }); // for
                String newLine = System.getProperty("line.separator"); // try
                String Adresse;
                Adresse = eric.getFinanzamtsdaten(finanzaemter.get(0).getId()).toString();
                String AdresseSplit[] = Adresse.split(newLine);
                FAName.setText("<html><body>" + AdresseSplit[0] + "<br>"
                        + AdresseSplit[1] + "<br>"
                        + AdresseSplit[2] + "<br>"
                        + AdresseSplit[3] + "<br>"
                        + AdresseSplit[4] + "<br>"
                        + AdresseSplit[5] + "<br>"
                        + AdresseSplit[6] + "<br>"
                        + AdresseSplit[7] + "<br>"
                        + AdresseSplit[13] + "<br>"
                        + AdresseSplit[14] + "<br>"
                        + AdresseSplit[15] + "<br>"
                        + AdresseSplit[23] + "<br>"
                        + AdresseSplit[24] + "<br>"
                        + AdresseSplit[25] + "<br>"
                        + "</body></html>");
                Finanzamtname = eric.getFinanzamtsdaten(finanzaemter.get(0).getId()).getFAName();
                Finanzamtnummer = eric.getFinanzamtsdaten(finanzaemter.get(0).getId()).getFANummer();
                FinanzamtTelefon = eric.getFinanzamtsdaten(finanzaemter.get(0).getId()).getFATelefon();
                FinanzamtErgaenzung = eric.getFinanzamtsdaten(finanzaemter.get(0).getId()).getFAErgaenzung();
                FinanzamtStrasse = eric.getFinanzamtsdaten(finanzaemter.get(0).getId()).getFAPLZOrt();
                FinanzamtPLZOrt = eric.getFinanzamtsdaten(finanzaemter.get(0).getId()).getFATelefon();
                FinanzamteMail = eric.getFinanzamtsdaten(finanzaemter.get(0).getId()).getFAeMail();
                FinanzamtBank = eric.getFinanzamtsdaten(finanzaemter.get(0).getId()).getFABank();
                FinanzamtIBAN = eric.getFinanzamtsdaten(finanzaemter.get(0).getId()).getFAIBAN();
                FinanzamtBIC = eric.getFinanzamtsdaten(finanzaemter.get(0).getId()).getFABIC();
            } // if
        } catch (de.elster.eric.wrapper.exception.EricException | de.elster.eric.wrapper.exception.WrapperException ex) {
            Logger.getLogger(ModulAbrechnungUSteuerVorA.class.getName()).log(Level.SEVERE, null, ex);
        }
    } // void

    /**
     * ruft den Dialog zur Eingabe der Steuernummer und des Finanzamtes auf
     *
     * @param evt Ereignis, das beim Klicken des Buttons + ausgelöst wurde
     */
    private void KlickBtnStrNr(java.awt.event.ActionEvent evt) {
        try {
            printFinanzaemter(eric);
        } catch (EricException | WrapperException e) {
            Modulhelferlein.Fehlermeldung("ERiC-Exception: " + e.getMessage());
        }
    }

    /**
     * sendet die XML-Datei ans Finanzamt
     *
     * @param evt Ereignis, das beim Klicken des Buttons + ausgelöst wurde
     */
    private void KlickSenden(java.awt.event.ActionEvent evt) {

        if (boolXMLgespeichert) {
            if (boolXMLgetestet) {
                Testmerker = "000000000";
                KlickSpeichern(evt);
                try {
                    Eric.initialize(ericPath, logPath);
                    Eric eric = Eric.getInstance();
                    validateAndSendTaxCase(eric, true);
                } catch (EricException e) {
                    Modulhelferlein.Fehlermeldung("Eric Fehler: " + e.getMessage());
                } catch (WrapperException e) {
                    Modulhelferlein.Fehlermeldung("Unerwartete Java-Ausnahme: " + e.getMessage());
                } catch (Exception e) {
                    Modulhelferlein.Fehlermeldung("Exception: " + e.getMessage());
                }
            } else {
                Modulhelferlein.Fehlermeldung("Steuerdatei wurde noch nicht getestet!");
            }
        } else {
            Modulhelferlein.Fehlermeldung("Steuerdatei wurde noch nicht gespeichert!");
        }
    } // void

    /**
     * sendet die XML-Datei mit Testmerker ans Finanzamt
     *
     * @param evt Ereignis, das beim Klicken des Buttons + ausgelöst wurde
     */
    private void KlickTesten(java.awt.event.ActionEvent evt) {

        if (boolXMLgespeichert) {
            //steuersatz = "20150222-Steuerdatensatz-Miles-Verlag.XML";        	
            try {
                Eric.initialize(ericPath, logPath);
                eric = Eric.getInstance();
                validateAndSendTaxCase(eric, false);

                boolXMLgetestet = true;
                Speichern.setEnabled(true);
                Senden.setEnabled(true);
                Testen.setEnabled(true);

            } catch (EricException e) {
                Modulhelferlein.Fehlermeldung("Eric Fehler: " + e.getMessage());
            } catch (WrapperException e) {
                Modulhelferlein.Fehlermeldung("Unerwartete Java-Ausnahme: " + e.getMessage());
            } catch (Exception e) {
                Modulhelferlein.Fehlermeldung("Exception: " + e.getMessage());
            }
        } else {
            Modulhelferlein.Fehlermeldung("Steuerdatei existiert noch nicht!");
        }
    } // void

    /**
     * speichert den Datensatz als XML-Datei
     *
     * @param evt Ereignis, das beim Klicken des Buttons + ausgelöst wurde
     */
    private void KlickSpeichern(java.awt.event.ActionEvent evt) {
        // File anlegen
        String zeitraum = "";
        if (rbMonat.isSelected()) {
            switch (field_Monat.getSelectedIndex()) {
                case 1:
                    zeitraum = "01";
                    break;
                case 2:
                    zeitraum = "02";
                    break;
                case 3:
                    zeitraum = "03";
                    break;
                case 4:
                    zeitraum = "04";
                    break;
                case 5:
                    zeitraum = "05";
                    break;
                case 6:
                    zeitraum = "06";
                    break;
                case 7:
                    zeitraum = "07";
                    break;
                case 8:
                    zeitraum = "08";
                    break;
                case 9:
                    zeitraum = "19";
                    break;
                case 10:
                    zeitraum = "10";
                    break;
                case 11:
                    zeitraum = "11";
                    break;
                case 12:
                    zeitraum = "12";
                    break;
                default:
                    zeitraum = "";
                    break;
            }
        } else {
            switch (field_Quartal.getSelectedIndex()) {
                case 1:
                    zeitraum = "41";
                    break;
                case 2:
                    zeitraum = "42";
                    break;
                case 3:
                    zeitraum = "43";
                    break;
                case 4:
                    zeitraum = "44";
                    break;
                default:
                    zeitraum = "";
                    break;
            }
        }
        outputFileName = Modulhelferlein.pathSteuer + "/"
                + "UStVA-"
                + Modulhelferlein.printSimpleDateFormat("yyyy") + "-"
                + zeitraum + "-"
                + Modulhelferlein.printSimpleDateFormat("yyyyMMdd");
        steuersatz = outputFileName + ".xml";

        file = new File(steuersatz);

        //SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        //String uhrzeit = sdf.format(new Date());
        try {
            // new FileWriter(file ,true) - falls die Datei bereits existiert
            // werden die Bytes an das Ende der Datei geschrieben

            // new FileWriter(file) - falls die Datei bereits existiert
            // wird diese überschrieben
            writer = new FileWriter(file);

            // Text wird in den Stream geschrieben
            writer.write("<?xml version='1.0' encoding='ISO-8859-15'?>");
            writer.write(System.getProperty("line.separator"));
            writer.write("<Elster xmlns='http://www.elster.de/2002/XMLSchema'" + " "
                    + "xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'" + " "
                    + "xsi:schemaLocation='http://www.elster.de/2002/XMLSchema" + " "
                    + ".\\Schemata\\elster0810_UStA_201501_extern.xsd'>");
            writer.write(System.getProperty("line.separator"));
            writer.write("    <TransferHeader version='8'>");
            writer.write(System.getProperty("line.separator"));
            writer.write("        <Verfahren>ElsterAnmeldung</Verfahren>");
            writer.write(System.getProperty("line.separator"));
            writer.write("        <DatenArt>UStVA</DatenArt>");
            writer.write(System.getProperty("line.separator"));
            writer.write("        <Vorgang>send-Auth</Vorgang>");
            writer.write(System.getProperty("line.separator"));
            writer.write("        <Testmerker>" + Testmerker + "</Testmerker>");
            writer.write(System.getProperty("line.separator"));
            writer.write("        <HerstellerID>21781</HerstellerID>");
            writer.write(System.getProperty("line.separator"));
            writer.write("        <DatenLieferant>Carola Hartmann</DatenLieferant>");
            writer.write(System.getProperty("line.separator"));
            writer.write("        <Datei>");
            writer.write(System.getProperty("line.separator"));
            writer.write("            <Verschluesselung>PKCS#7v1.5</Verschluesselung>");
            writer.write(System.getProperty("line.separator"));
            writer.write("            <Kompression>GZIP</Kompression>");
            writer.write(System.getProperty("line.separator"));
            writer.write("            <DatenGroesse>123456789</DatenGroesse>");
            writer.write(System.getProperty("line.separator"));
            writer.write("            <TransportSchluessel/>");
            writer.write(System.getProperty("line.separator"));
            writer.write("        </Datei>");
            writer.write(System.getProperty("line.separator"));
            writer.write("        <RC>");
            writer.write(System.getProperty("line.separator"));
            writer.write("            <Rueckgabe>");
            writer.write(System.getProperty("line.separator"));
            writer.write("    	        <Code>0</Code>");
            writer.write(System.getProperty("line.separator"));
            writer.write("    	        <Text/>");
            writer.write(System.getProperty("line.separator"));
            writer.write("            </Rueckgabe>");
            writer.write(System.getProperty("line.separator"));
            writer.write("            <Stack>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                <Code>0</Code>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                <Text/>");
            writer.write(System.getProperty("line.separator"));
            writer.write("            </Stack>");
            writer.write(System.getProperty("line.separator"));
            writer.write("        </RC>");
            writer.write(System.getProperty("line.separator"));
            writer.write("        <VersionClient>C.Hartmann Miles-Verlag Verlagsverwaltung</VersionClient>");
            writer.write(System.getProperty("line.separator"));
            writer.write("    </TransferHeader>");
            writer.write(System.getProperty("line.separator"));
            writer.write("    <DatenTeil>");
            writer.write(System.getProperty("line.separator"));
            writer.write("        <Nutzdatenblock>");
            writer.write(System.getProperty("line.separator"));
            writer.write("            <NutzdatenHeader version='10'>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                <NutzdatenTicket>234234234</NutzdatenTicket>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                <Empfaenger id='F'>1119</Empfaenger>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                <Hersteller>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                    <ProduktName>Carola Hartmann Miles-Verlag Verlagsverwaltung</ProduktName>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                    <ProduktVersion>2015.1/0</ProduktVersion>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                </Hersteller>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                <DatenLieferant>Carola Hartmann</DatenLieferant>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                <Zusatz>");
            writer.write(System.getProperty("line.separator"));
            writer.write("               	    <Info>....</Info>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                </Zusatz>");
            writer.write(System.getProperty("line.separator"));
            writer.write("            </NutzdatenHeader>");
            writer.write(System.getProperty("line.separator"));
            writer.write("            <Nutzdaten>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                <Anmeldungssteuern art='UStVA' version='201501'>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                    <DatenLieferant>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                        <Name>" + field_Zeile12.getText() + "</Name>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                        <Strasse>" + field_Zeile13_1.getText() + "</Strasse>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                        <PLZ>" + field_Zeile13_21.getText() + "</PLZ>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                        <Ort>" + field_Zeile13_22.getText() + "</Ort>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                        <Telefon>" + field_Zeile14.getText() + "</Telefon>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                        <Email>" + field_Zeile15.getText() + "</Email>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                    </DatenLieferant>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                    <Erstellungsdatum>" + Modulhelferlein.printSimpleDateFormat("yyyyMMdd") + "</Erstellungsdatum>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                    <Steuerfall>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                        <Umsatzsteuervoranmeldung>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                            <Jahr>" + Modulhelferlein.printSimpleDateFormat("yyyy") + "</Jahr>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                            <Zeitraum>" + zeitraum + "</Zeitraum>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                            <Steuernummer>11" + field_Zeile3.getText().substring(0, 2) + "0" + field_Zeile3.getText().substring(2, 10) + "</Steuernummer>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                            <Kz09>21781</Kz09>");
            writer.write(System.getProperty("line.separator"));
            if (cb10.isSelected()) {
                writer.write("                            <Kz10>1</Kz10>");
                writer.write(System.getProperty("line.separator"));
            }
            /**
             * writer.write("                            <Kz21>0</Kz21>");
             * writer.write(System.getProperty("line.separator"));
             *
             */
            if (cb22.isSelected()) {
                writer.write("                            <Kz22>1</Kz22>");
                writer.write(System.getProperty("line.separator"));
            }
            if (cb26.isSelected()) {
                writer.write("                            <Kz26>1</Kz26>");
                writer.write(System.getProperty("line.separator"));
            }
            if (cb29.isSelected()) {
                writer.write("                            <Kz29>1</Kz29>");
                writer.write(System.getProperty("line.separator"));
            }
            writer.write("                            <Kz35>" + field_KZ35.getText() + "</Kz35>");
            writer.write(System.getProperty("line.separator"));
            String s = field_KZ36.getText();
            if (s.indexOf(".") == s.length() - 2) {
                s = s + "0";
            }
            writer.write("                            <Kz36>" + s + "</Kz36>");
            writer.write(System.getProperty("line.separator"));
            /**
             * writer.write("                            <Kz39>0</Kz39>");
             * writer.write(System.getProperty("line.separator"));
             * writer.write("                            <Kz41>0</Kz41>");
             * writer.write(System.getProperty("line.separator"));
             * writer.write("                            <Kz42>0</Kz42>");
             * writer.write(System.getProperty("line.separator"));
             * writer.write("                            <Kz43>0</Kz43>");
             * writer.write(System.getProperty("line.separator"));
             * writer.write("                            <Kz44>0</Kz44>");
             * writer.write(System.getProperty("line.separator"));
             * writer.write("                            <Kz45>0</Kz45>");
             * writer.write(System.getProperty("line.separator"));
             * writer.write("                            <Kz48>0</Kz48>");
             * writer.write(System.getProperty("line.separator"));
             * writer.write("                            <Kz49>0</Kz49>");
             * writer.write(System.getProperty("line.separator"));
             * writer.write("                            <Kz52>0</Kz52>");
             * writer.write(System.getProperty("line.separator"));
             * writer.write("                            <Kz53>0</Kz53>");
             * writer.write(System.getProperty("line.separator"));
             * writer.write("                            <Kz59>0</Kz59>");
             * writer.write(System.getProperty("line.separator"));
             * writer.write("                            <Kz60>0</Kz60>");
             * writer.write(System.getProperty("line.separator"));
             * writer.write("                            <Kz61>0</Kz61>");
             * writer.write(System.getProperty("line.separator"));
             * writer.write("                            <Kz64>0</Kz64>");
             * writer.write(System.getProperty("line.separator"));
             * writer.write("                            <Kz65>0</Kz65>");
             * writer.write(System.getProperty("line.separator"));
             *
             */
            s = field_KZ66.getText();
            if (s.indexOf(".") == s.length() - 2) {
                s = s + "0";
            }
            writer.write("                            <Kz66>" + s + "</Kz66>");
            writer.write(System.getProperty("line.separator"));
            /**
             * writer.write("                            <Kz67>0</Kz67>");
             * writer.write(System.getProperty("line.separator"));
             * writer.write("                            <Kz69>0</Kz69>");
             * writer.write(System.getProperty("line.separator"));
             * writer.write("                            <Kz73>0</Kz73>");
             * writer.write(System.getProperty("line.separator"));
             * writer.write("                            <Kz74>0</Kz74>");
             * writer.write(System.getProperty("line.separator"));
             * writer.write("                            <Kz76>0</Kz76>");
             * writer.write(System.getProperty("line.separator"));
             * writer.write("                            <Kz77>0</Kz77>");
             * writer.write(System.getProperty("line.separator"));
             * writer.write("                            <Kz80>0</Kz80>");
             * writer.write(System.getProperty("line.separator"));
             *
             */
            writer.write("                            <Kz81>" + field_KZ81.getText() + "</Kz81>");
            writer.write(System.getProperty("line.separator"));
            s = field_KZ83.getText();
            if (s.indexOf(".") == s.length() - 2) {
                s = s + "0";
            }
            writer.write("                            <Kz83>" + s + "</Kz83>");
            writer.write(System.getProperty("line.separator"));
            /**
             * writer.write("                            <Kz84>0</Kz84>");
             * writer.write(System.getProperty("line.separator"));
             * writer.write("                            <Kz85>0</Kz85>");
             * writer.write(System.getProperty("line.separator"));
             *
             */
            writer.write("                            <Kz86>" + field_KZ86.getText() + "</Kz86>");
            writer.write(System.getProperty("line.separator"));
            /**
             * writer.write("                            <Kz89>0</Kz89>");
             * writer.write(System.getProperty("line.separator"));
             * writer.write("                            <Kz91>0</Kz91>");
             * writer.write(System.getProperty("line.separator"));
             * writer.write("                            <Kz93>0</Kz93>");
             * writer.write(System.getProperty("line.separator"));
             * writer.write("                            <Kz94>0</Kz94>");
             * writer.write(System.getProperty("line.separator"));
             * writer.write("                            <Kz95>0</Kz95>");
             * writer.write(System.getProperty("line.separator"));
             * writer.write("                            <Kz96>0</Kz96>");
             * writer.write(System.getProperty("line.separator"));
             * writer.write("                            <Kz98>0</Kz98>");
             * writer.write(System.getProperty("line.separator"));
             *
             */
            writer.write("                        </Umsatzsteuervoranmeldung>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                    </Steuerfall>");
            writer.write(System.getProperty("line.separator"));
            writer.write("                </Anmeldungssteuern>");
            writer.write(System.getProperty("line.separator"));
            writer.write("            </Nutzdaten>");
            writer.write(System.getProperty("line.separator"));
            writer.write("        </Nutzdatenblock>");
            writer.write(System.getProperty("line.separator"));
            writer.write("    </DatenTeil>");
            writer.write(System.getProperty("line.separator"));
            writer.write("</Elster>");
            writer.write(System.getProperty("line.separator"));

            /**
             * Templates writer.write("<>");
             * writer.write(System.getProperty("line.separator"));
             * writer.write("    <></>");
             * writer.write(System.getProperty("line.separator"));
             * writer.write("    <Feld index='' lfdNr='' nr='' wert=''/>");
             * writer.write(System.getProperty("line.separator"));
             * writer.write("</>");
             * writer.write(System.getProperty("line.separator"));
             *
             */
            // Schreibt den Stream in die Datei
            // Sollte immer am Ende ausgeführt werden, sodass der Stream 
            // leer ist und alles in der Datei steht.
            writer.flush();

            // Schließt den Stream
            writer.close();

            Modulhelferlein.Infomeldung(steuersatz + " wurde gespeichert!");

            // Datenbank aktualisieren
            result2.updateString("Konfiguration_Stammdaten", field_Zeile3.getText());
            result2.updateString("Konfiguration_Einnahmen", field_Zeile4.getText());
            result2.updateString("Konfiguration_Ausgaben", field_Zeile12.getText());
            result2.updateString("Konfiguration_Umsaetze", field_Zeile13_1.getText());
            result2.updateString("Konfiguration_Rechnungen", field_Zeile13_21.getText());
            result2.updateString("Konfiguration_Sicherung", field_Zeile13_22.getText());
            result2.updateString("Konfiguration_Mahnungen", field_Zeile14.getText());
            result2.updateString("Konfiguration_Termine", field_Zeile15.getText());
            result2.updateRow();

            boolXMLgespeichert = true;
            Speichern.setEnabled(true);
            Senden.setEnabled(false);
            Testen.setEnabled(true);

        } catch (IOException e) {
            Modulhelferlein.Infomeldung("IO-Exception: " + e.getMessage());
        } catch (SQLException e) {
            Modulhelferlein.Infomeldung("SQL-Exception: " + e.getMessage());
        } // try
    } // void

    @SuppressWarnings("empty-statement")
    private void KlickSchliessen(java.awt.event.ActionEvent evt) {
        try {
            if (result != null) {
                result.close();
            }
        } catch (SQLException e) {
            Modulhelferlein.Fehlermeldung("Exception: " + e.getMessage());
        }

        try {
            if (result2 != null) {
                result2.close();
            }
        } catch (SQLException e) {
            Modulhelferlein.Fehlermeldung("Exception: " + e.getMessage());
        }

        try {
            if (result3 != null) {
                result3.close();
            }
        } catch (SQLException e) {
            Modulhelferlein.Fehlermeldung("Exception: " + e.getMessage());
        }

        try {
            if (SQLAnfrage != null) {
                SQLAnfrage.close();
            }
        } catch (SQLException e) {
            Modulhelferlein.Fehlermeldung("Exception: " + e.getMessage());
        }

        try {
            if (SQLAnfrage2 != null) {
                SQLAnfrage2.close();
            }
        } catch (SQLException e) {
            Modulhelferlein.Fehlermeldung("Exception: " + e.getMessage());
        }

        try {
            if (SQLAnfrage3 != null) {
                SQLAnfrage3.close();
            }
        } catch (SQLException e) {
            Modulhelferlein.Fehlermeldung("Exception: " + e.getMessage());
        }

        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            Modulhelferlein.Fehlermeldung("Exception: " + e.getMessage());
        }


        meinFenster.setVisible(false);
    }

    private void KlickAnfang(java.awt.event.ActionEvent evt) {
        // System.out.println("Anfang"); 
        try {
            result.first();
            count = 1;
            field_count.setText(Integer.toString(count));

            field_Datum.setText(result.getString("USTVA_DATUM"));
            cb10.setSelected(result.getBoolean("USTVA_KZ10"));
            cb22.setSelected(result.getBoolean("USTVA_KZ26"));
            cb26.setSelected(result.getBoolean("USTVA_KZ22"));
            cb29.setSelected(result.getBoolean("USTVA_KZ29"));
            if (result.getInt("USTVA_ZEITRAUM") > 12) {
                field_Quartal.setSelectedIndex(result.getInt("USTVA_ZEITRAUM"));
                rbQuartal.setSelected(true);
            } else {
                field_Monat.setSelectedIndex(result.getInt("USTVA_ZEITRAUM"));
                rbMonat.setSelected(true);
            }
            field_KZ35.setText(Integer.toString(result.getInt("USTVA_KZ35")));
            field_KZ36.setText(Modulhelferlein.Fstr2dec(result.getFloat("USTVA_KZ36")));
            field_KZ66.setText(Float.toString(result.getFloat("USTVA_KZ66")));
            field_KZ81.setText(Integer.toString(result.getInt("USTVA_KZ81")));
            field_KZ83.setText(Float.toString(result.getFloat("USTVA_KZ83")));
            field_KZ86.setText(Integer.toString(result.getInt("USTVA_KZ86")));
            berechne_felder(result.getInt("USTVA_ZEITRAUM"));

            // Schalterzustand anpassen
            Anfang.setEnabled(false);
            Zurueck.setEnabled(false);
            Vor.setEnabled(true);
            Ende.setEnabled(true);
            Update.setEnabled(true);
            Einfuegen.setEnabled(true);
            Loeschen.setEnabled(true);
            Suchen.setEnabled(true);
            WSuchen.setEnabled(true);
            if (result.getBoolean("USTVA_GESENDET")) {
                Speichern.setEnabled(false);
            } else {
                Speichern.setEnabled(true);
            }
            Senden.setEnabled(false);
            Testen.setEnabled(false);
            Schliessen.setEnabled(true);

        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: " + exept.getMessage());
        }
    }

    private void KlickEnde(java.awt.event.ActionEvent evt) {
        // System.out.println("Ende"); 
        try {
            result.last();
            count = countMax;
            field_count.setText(Integer.toString(count));

            field_Datum.setText(result.getString("USTVA_DATUM"));
            cb10.setSelected(result.getBoolean("USTVA_KZ10"));
            cb22.setSelected(result.getBoolean("USTVA_KZ26"));
            cb26.setSelected(result.getBoolean("USTVA_KZ22"));
            cb29.setSelected(result.getBoolean("USTVA_KZ29"));
            if (result.getInt("USTVA_ZEITRAUM") > 12) {
                field_Quartal.setSelectedIndex(result.getInt("USTVA_ZEITRAUM"));
                rbQuartal.setSelected(true);
            } else {
                field_Monat.setSelectedIndex(result.getInt("USTVA_ZEITRAUM"));
                rbMonat.setSelected(true);
            }
            field_KZ35.setText(Integer.toString(result.getInt("USTVA_KZ35")));
            field_KZ36.setText(Modulhelferlein.Fstr2dec(result.getFloat("USTVA_KZ36")));
            field_KZ66.setText(Float.toString(result.getFloat("USTVA_KZ66")));
            field_KZ81.setText(Integer.toString(result.getInt("USTVA_KZ81")));
            field_KZ83.setText(Float.toString(result.getFloat("USTVA_KZ83")));
            field_KZ86.setText(Integer.toString(result.getInt("USTVA_KZ86")));
            berechne_felder(result.getInt("USTVA_ZEITRAUM"));

            // Schalterzustand anpassen
            Anfang.setEnabled(true);
            Zurueck.setEnabled(true);
            Vor.setEnabled(false);
            Ende.setEnabled(false);
            Update.setEnabled(true);
            Einfuegen.setEnabled(true);
            Loeschen.setEnabled(true);
            Suchen.setEnabled(true);
            WSuchen.setEnabled(true);
            if (result.getBoolean("USTVA_GESENDET")) {
                Speichern.setEnabled(false);
            } else {
                Speichern.setEnabled(true);
            }
            Senden.setEnabled(false);
            Testen.setEnabled(false);
            Schliessen.setEnabled(true);

        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: " + exept.getMessage());
        }
    }

    private void KlickVor(java.awt.event.ActionEvent evt) {
        // System.out.println("Vor"); 
        try {
            if (result.next()) {
                count = count + 1;
                field_count.setText(Integer.toString(count));

                field_Datum.setText(result.getString("USTVA_DATUM"));
                cb10.setSelected(result.getBoolean("USTVA_KZ10"));
                cb22.setSelected(result.getBoolean("USTVA_KZ26"));
                cb26.setSelected(result.getBoolean("USTVA_KZ22"));
                cb29.setSelected(result.getBoolean("USTVA_KZ29"));
                if (result.getInt("USTVA_ZEITRAUM") > 12) {
                    field_Quartal.setSelectedIndex(result.getInt("USTVA_ZEITRAUM"));
                    rbQuartal.setSelected(true);
                } else {
                    field_Monat.setSelectedIndex(result.getInt("USTVA_ZEITRAUM"));
                    rbMonat.setSelected(true);
                }
                field_KZ35.setText(Integer.toString(result.getInt("USTVA_KZ35")));
                field_KZ36.setText(Modulhelferlein.Fstr2dec(result.getFloat("USTVA_KZ36")));
                field_KZ66.setText(Float.toString(result.getFloat("USTVA_KZ66")));
                field_KZ81.setText(Integer.toString(result.getInt("USTVA_KZ81")));
                field_KZ83.setText(Float.toString(result.getFloat("USTVA_KZ83")));
                field_KZ86.setText(Integer.toString(result.getInt("USTVA_KZ86")));
                berechne_felder(result.getInt("USTVA_ZEITRAUM"));

                // Schalterzustand anpassen
                if (count < countMax) {
                    Ende.setEnabled(true);
                    Vor.setEnabled(true);
                } else {
                    Ende.setEnabled(false);
                    Vor.setEnabled(false);
                }
                Anfang.setEnabled(true);
                Zurueck.setEnabled(true);
                Update.setEnabled(true);
                Einfuegen.setEnabled(true);
                Loeschen.setEnabled(true);
                Suchen.setEnabled(true);
                WSuchen.setEnabled(true);
                if (result.getBoolean("USTVA_GESENDET")) {
                    Speichern.setEnabled(false);
                } else {
                    Speichern.setEnabled(true);
                }
                Senden.setEnabled(false);
                Testen.setEnabled(false);
                Schliessen.setEnabled(true);

            } else {
                result.previous();
            }

        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: " + exept.getMessage());
        }
    }

    private void KlickZurueck(java.awt.event.ActionEvent evt) {
        // System.out.println("Zuruck"); 
        try {
            if (result.previous()) {
                count = count - 1;
                field_count.setText(Integer.toString(count));

                field_Datum.setText(result.getString("USTVA_DATUM"));
                cb10.setSelected(result.getBoolean("USTVA_KZ10"));
                cb22.setSelected(result.getBoolean("USTVA_KZ26"));
                cb26.setSelected(result.getBoolean("USTVA_KZ22"));
                cb29.setSelected(result.getBoolean("USTVA_KZ29"));
                if (result.getInt("USTVA_ZEITRAUM") > 12) {
                    field_Quartal.setSelectedIndex(result.getInt("USTVA_ZEITRAUM"));
                    rbQuartal.setSelected(true);
                } else {
                    field_Monat.setSelectedIndex(result.getInt("USTVA_ZEITRAUM"));
                    rbMonat.setSelected(true);
                }
                field_KZ35.setText(Integer.toString(result.getInt("USTVA_KZ35")));
                field_KZ36.setText(Modulhelferlein.Fstr2dec(result.getFloat("USTVA_KZ36")));
                field_KZ66.setText(Float.toString(result.getFloat("USTVA_KZ66")));
                field_KZ81.setText(Integer.toString(result.getInt("USTVA_KZ81")));
                field_KZ83.setText(Float.toString(result.getFloat("USTVA_KZ83")));
                field_KZ86.setText(Integer.toString(result.getInt("USTVA_KZ86")));
                berechne_felder(result.getInt("USTVA_ZEITRAUM"));

                // Schalterzustand anpassen
                if (count > 1) {
                    Anfang.setEnabled(true);
                    Zurueck.setEnabled(true);
                } else {
                    Anfang.setEnabled(false);
                    Zurueck.setEnabled(false);
                }
                Vor.setEnabled(true);
                Ende.setEnabled(true);
                Update.setEnabled(true);
                Einfuegen.setEnabled(true);
                Loeschen.setEnabled(true);
                Suchen.setEnabled(true);
                WSuchen.setEnabled(true);
                if (result.getBoolean("USTVA_GESENDET")) {
                    Speichern.setEnabled(false);
                } else {
                    Speichern.setEnabled(true);
                }
                Senden.setEnabled(false);
                Testen.setEnabled(false);
                Schliessen.setEnabled(true);

            } else {
                result.next();
            }
            Senden.setEnabled(true);
            Testen.setEnabled(true);
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: " + exept.getMessage());
        }
    }

    private void KlickEinfuegen(java.awt.event.ActionEvent evt) {
        // System.out.println("Einfuegen"); 
        int ID;

        try {
            if (resultIsEmpty) {
                ID = 1;
            } else {
                result.last();
                ID = 1 + result.getInt(1);
            }
            result.moveToInsertRow();
            result.updateInt("USTVA_ID", ID);
            result.updateDate("USTVA_DATUM", Modulhelferlein.Date2SQLDate(Modulhelferlein.CurDate));
            result.updateInt("USTVA_ZEITRAUM", 1);
            result.updateBoolean("USTVA_KZ10", false);
            result.updateBoolean("USTVA_KZ22", false);
            result.updateBoolean("USTVA_KZ26", false);
            result.updateBoolean("USTVA_KZ29", false);
            result.updateBoolean("USTVA_GESENDET", false);
            result.updateInt("USTVA_KZ35", 0);
            result.updateFloat("USTVA_KZ36", 0F);
            result.updateFloat("USTVA_KZ66", 0F);
            result.updateInt("USTVA_KZ81", 0);
            result.updateFloat("USTVA_KZ83", 0F);
            result.updateInt("USTVA_KZ86", 0);

            result.insertRow();
            countMax = countMax + 1;
            field_countMax.setText(Integer.toString(countMax));
            count = countMax;
            field_count.setText(Integer.toString(count));

            field_Datum.setText(result.getString("USTVA_DATUM"));
            cb10.setSelected(result.getBoolean("USTVA_KZ10"));
            cb22.setSelected(result.getBoolean("USTVA_KZ26"));
            cb26.setSelected(result.getBoolean("USTVA_KZ22"));
            cb29.setSelected(result.getBoolean("USTVA_KZ29"));
            if (result.getInt("USTVA_ZEITRAUM") > 12) {
                field_Quartal.setSelectedIndex(result.getInt("USTVA_ZEITRAUM"));
                rbQuartal.setSelected(true);
            } else {
                field_Monat.setSelectedIndex(result.getInt("USTVA_ZEITRAUM"));
                rbMonat.setSelected(true);
            }
            field_KZ35.setText(Integer.toString(result.getInt("USTVA_KZ35")));
            field_KZ36.setText(Modulhelferlein.Fstr2dec(result.getFloat("USTVA_KZ36")));
            field_KZ66.setText(Float.toString(result.getFloat("USTVA_KZ66")));
            field_KZ81.setText(Integer.toString(result.getInt("USTVA_KZ81")));
            field_KZ83.setText(Float.toString(result.getFloat("USTVA_KZ83")));
            field_KZ86.setText(Integer.toString(result.getInt("USTVA_KZ86")));

            // Schalterzustand anpassen
            Anfang.setEnabled(true);
            Zurueck.setEnabled(true);
            Vor.setEnabled(false);
            Ende.setEnabled(false);
            Update.setEnabled(true);
            Einfuegen.setEnabled(true);
            Loeschen.setEnabled(true);
            Suchen.setEnabled(true);
            WSuchen.setEnabled(true);
            Speichern.setEnabled(true);
            Senden.setEnabled(false);
            Testen.setEnabled(false);
            Schliessen.setEnabled(true);

            resultIsEmpty = false;
            result.last();

        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: " + exept.getMessage());
        }
    }

    private void KlickLoeschen(java.awt.event.ActionEvent evt) {
        // System.out.println("Loeschen"); 
        try {
            result.deleteRow();
            countMax = countMax - 1;
            field_countMax.setText(Integer.toString(countMax));
            count = 1;
            field_count.setText(Integer.toString(count));

            field_Datum.setText(result.getString("USTVA_DATUM"));
            cb10.setSelected(result.getBoolean("USTVA_KZ10"));
            cb22.setSelected(result.getBoolean("USTVA_KZ26"));
            cb26.setSelected(result.getBoolean("USTVA_KZ22"));
            cb29.setSelected(result.getBoolean("USTVA_KZ29"));
            if (result.getInt("USTVA_ZEITRAUM") > 12) {
                field_Quartal.setSelectedIndex(result.getInt("USTVA_ZEITRAUM"));
                rbQuartal.setSelected(true);
            } else {
                field_Monat.setSelectedIndex(result.getInt("USTVA_ZEITRAUM"));
                rbMonat.setSelected(true);
            }
            field_KZ35.setText(Integer.toString(result.getInt("USTVA_KZ35")));
            field_KZ36.setText(Modulhelferlein.Fstr2dec(result.getFloat("USTVA_KZ36")));
            field_KZ66.setText(Float.toString(result.getFloat("USTVA_KZ66")));
            field_KZ81.setText(Integer.toString(result.getInt("USTVA_KZ81")));
            field_KZ83.setText(Float.toString(result.getFloat("USTVA_KZ83")));
            field_KZ86.setText(Integer.toString(result.getInt("USTVA_KZ86")));
            berechne_felder(result.getInt("USTVA_ZEITRAUM"));

            result.first();
            if (countMax > 0) {

                // Schalterzustand anpassen
                Anfang.setEnabled(true);
                Zurueck.setEnabled(true);
                Vor.setEnabled(true);
                Ende.setEnabled(true);
                Update.setEnabled(true);
                Einfuegen.setEnabled(true);
                Loeschen.setEnabled(true);
                Suchen.setEnabled(true);
                WSuchen.setEnabled(true);
                Speichern.setEnabled(true);
                Senden.setEnabled(false);
                Testen.setEnabled(false);
                Schliessen.setEnabled(true);
            } else {
                resultIsEmpty = true;

                // Schalterzustand anpassen
                Anfang.setEnabled(false);
                Zurueck.setEnabled(false);
                Vor.setEnabled(false);
                Ende.setEnabled(false);
                Update.setEnabled(false);
                Einfuegen.setEnabled(true);
                Loeschen.setEnabled(false);
                Suchen.setEnabled(false);
                WSuchen.setEnabled(false);
                Speichern.setEnabled(true);
                Senden.setEnabled(false);
                Testen.setEnabled(false);
                Schliessen.setEnabled(true);
            }
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: " + exept.getMessage());
        }
    }

    @SuppressWarnings("empty-statement")
    private void KlickUpdate(java.awt.event.ActionEvent evt) {
        // System.out.println("Update"); 
        try {
            if (resultIsEmpty) {
                Modulhelferlein.Fehlermeldung("Die Datenbank ist leer - bitte Datensatz einfügen!");
            } else {
                if (rbMonat.isSelected()) {
                    if (field_Monat.getSelectedIndex() < 1) {
                        Modulhelferlein.Fehlermeldung("Es ist kein Monat ausgewählt!");
                    } else {
                        result.updateDate("USTVA_DATUM", Modulhelferlein.Date2SQLDate(Modulhelferlein.CurDate));
                        result.updateInt("USTVA_ZEITRAUM", field_Monat.getSelectedIndex());
                        result.updateBoolean("USTVA_KZ10", cb10.isSelected());
                        result.updateBoolean("USTVA_KZ22", cb22.isSelected());
                        result.updateBoolean("USTVA_KZ26", cb26.isSelected());
                        result.updateBoolean("USTVA_KZ29", cb29.isSelected());
                        result.updateInt("USTVA_KZ35", Integer.parseInt(field_KZ35.getText()));
                        result.updateFloat("USTVA_KZ36", Float.parseFloat(field_KZ36.getText()));
                        result.updateFloat("USTVA_KZ66", Float.parseFloat(field_KZ66.getText()));
                        result.updateInt("USTVA_KZ81", Integer.parseInt(field_KZ81.getText()));
                        result.updateFloat("USTVA_KZ83", Float.parseFloat(field_KZ83.getText()));
                        result.updateInt("USTVA_KZ86", Integer.parseInt(field_KZ86.getText()));
                    }
                } else {
                    if (field_Quartal.getSelectedIndex() < 1) {
                        Modulhelferlein.Fehlermeldung("Es ist kein Quartal ausgewählt!");
                    } else {
                        result.updateDate("USTVA_DATUM", Modulhelferlein.Date2SQLDate(Modulhelferlein.CurDate));
                        result.updateInt("USTVA_ZEITRAUM", field_Quartal.getSelectedIndex() + 40);
                        result.updateBoolean("USTVA_KZ10", cb10.isSelected());
                        result.updateBoolean("USTVA_KZ22", cb22.isSelected());
                        result.updateBoolean("USTVA_KZ26", cb26.isSelected());
                        result.updateBoolean("USTVA_KZ29", cb29.isSelected());
                        result.updateInt("USTVA_KZ35", Integer.parseInt(field_KZ35.getText()));
                        result.updateFloat("USTVA_KZ36", Float.parseFloat(field_KZ36.getText()));
                        result.updateFloat("USTVA_KZ66", Float.parseFloat(field_KZ66.getText()));
                        result.updateInt("USTVA_KZ81", Integer.parseInt(field_KZ81.getText()));
                        result.updateFloat("USTVA_KZ83", Float.parseFloat(field_KZ83.getText()));
                        result.updateInt("USTVA_KZ86", Integer.parseInt(field_KZ86.getText()));
                    }
                };

                result.updateRow();
            }
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: " + exept.getMessage());
        }
    }

    private void KlickSuchen(java.awt.event.ActionEvent evt) {
        boolean gefunden = false;

        try {
            result.first();
            do {
            } while ((!gefunden) && result.next());
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: " + exept.getMessage());
        }
    }

    private void KlickWSuchen(java.awt.event.ActionEvent evt) {
        boolean gefunden = false;

        try {
            do {
            } while ((!gefunden) && result.next());
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: " + exept.getMessage());
        }
    }

    @SuppressWarnings("empty-statement")
    private void KlickDrucken(java.awt.event.ActionEvent evt) {
        //	Anlagen speichern 
        String zeitraum = "";
        if (rbMonat.isSelected()) {
            switch (field_Monat.getSelectedIndex()) {
                case 1:
                    zeitraum = "01";
                    break;
                case 2:
                    zeitraum = "02";
                    break;
                case 3:
                    zeitraum = "03";
                    break;
                case 4:
                    zeitraum = "04";
                    break;
                case 5:
                    zeitraum = "05";
                    break;
                case 6:
                    zeitraum = "06";
                    break;
                case 7:
                    zeitraum = "07";
                    break;
                case 8:
                    zeitraum = "08";
                    break;
                case 9:
                    zeitraum = "19";
                    break;
                case 10:
                    zeitraum = "10";
                    break;
                case 11:
                    zeitraum = "11";
                    break;
                case 12:
                    zeitraum = "12";
                    break;
                default:
                    zeitraum = "";
                    break;
            }
        } else {
            switch (field_Quartal.getSelectedIndex()) {
                case 1:
                    zeitraum = "41";
                    break;
                case 2:
                    zeitraum = "42";
                    break;
                case 3:
                    zeitraum = "43";
                    break;
                case 4:
                    zeitraum = "44";
                    break;
                default:
                    zeitraum = "";
                    break;
            }
        };
        outputFileName = Modulhelferlein.pathSteuer + "/"
                + "UStVA-"
                + Modulhelferlein.printSimpleDateFormat("yyyy") + "-"
                + zeitraum + "-"
                + Modulhelferlein.printSimpleDateFormat("yyyyMMdd");

        //  Create a document and add a page to it
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(A4);
        document.addPage(page);

        // Start a new content stream which will "hold" the to be created content
        PDPageContentStream cos;
        try {
            cos = new PDPageContentStream(document, page);

            PDDocumentInformation docInfo = document.getDocumentInformation();

            docInfo.setSubject("Umsatzsteuervoranmeldung");
            docInfo.setTitle("miles-Verlag Steuer");
            docInfo.setAuthor("miles-Verlag");
            docInfo.setCreationDate(Calendar.getInstance());
            docInfo.setCreator("miles-Verlag");
            docInfo.setProducer("miles-Verlag");

            PDFont fontPlain = PDType1Font.HELVETICA;
            PDFont fontBold = PDType1Font.HELVETICA_BOLD;

            Ausgabe(cos, fontBold, 16, Color.BLACK,56, 770, "Carola Hartmann Miles-Verlag");
            Ausgabe(cos, fontBold, 16, Color.BLACK,56, 750, "Umsatzsteuervoranmeldung");
            Ausgabe(cos, fontBold, 16, Color.BLACK,56, 730, "Zeitraum: " + von + " - " + bis);
            Ausgabe(cos, fontBold, 16, Color.BLACK,56, 700, "Steuernummer " + field_Zeile3.getText());
            Ausgabe(cos, fontBold, 14, Color.BLACK,56, 460, "Beilage 1 - Steuerpflichtige Umsätze zum Steuersatz von 19%");
            Ausgabe(cos, fontBold, 14, Color.BLACK,56, 440, "Beilage 2 - Steuerpflichtige Umsätze zum Steuersatz von 7%");
            Ausgabe(cos, fontBold, 14, Color.BLACK,56, 420, "Beilage 3 - Steuerpflichtige Umsätze zu anderen Steuersätzen");
            Ausgabe(cos, fontBold, 14, Color.BLACK,56, 400, "Beilage 4 - Abziehbare Vorsteuerbeträge");

            cos.close();

            // Beilage 1 erstellen  
            page = new PDPage(A4);
            document.addPage(page);

            cos = new PDPageContentStream(document, page);

            Integer zeile = 1;
            Integer seite = 1;

            Ausgabe(cos, fontBold, 11, Color.BLACK,56, 770, "Carola Hartmann Miles-Verlag");
            Ausgabe(cos, fontBold, 11, Color.BLACK,56, 755, "Anlagen zur Umsatzsteuervoranmeldung, Steuernummer " + field_Zeile3.getText());
            Ausgabe(cos, fontBold, 11, Color.BLACK,56, 730, "Beilage 1 - Steuerpflichtige Umsätze zum Steuersatz von 19%");
            Ausgabe(cos, fontBold, 11, Color.BLACK,500, 730, "Seite " + Integer.toString(seite));
            Ausgabe(cos, fontBold, 11, Color.BLACK,56, 700, "Datum");
            Ausgabe(cos, fontBold, 11, Color.BLACK,130, 700, "Beschreibung");
            Ausgabe(cos, fontBold, 11, Color.BLACK,450, 700, "Netto");
            Ausgabe(cos, fontBold, 11, Color.BLACK,500, 700, "Steuer");

            conn = null;

            try { // Datenbank-Treiber laden
                Class.forName(Modulhelferlein.dbDriver);
            } catch (ClassNotFoundException exept) {
                Modulhelferlein.Fehlermeldung("Treiber nicht gefunden.");
            }

            try { // Verbindung zur Datenbank über die JDBC-Brücke
                conn = DriverManager.getConnection(Modulhelferlein.dbUrl, Modulhelferlein.dbUser, Modulhelferlein.dbPassword);
            } catch (SQLException exept) {
                Modulhelferlein.Fehlermeldung("SQL-Exception: Verbindung zur Datenbank nicht moeglich.");
            }

            if (conn != null) {
                SQLAnfrageBest = null; // Anfrage erzeugen

                try {
                    SQLAnfrageBest = conn.createStatement();

                    resultBest = SQLAnfrageBest.executeQuery("SELECT * FROM TBL_EINNAHMEN "
                            + " WHERE EINNAHMEN_RECHDATUM BETWEEN '" + von + "' AND '" + bis + "'"
                            + " AND EINNAHMEN_USTR = '19'");
                    Gesamtsumme = 0D;
                    GesamtUstr = 0D;
                    GesamtNetto = 0D;
                    while (resultBest.next()) {
                        if (zeile == 41) {
                            seite = seite + 1;
                            zeile = 1;
                            Ausgabe(cos, fontBold, 11, Color.BLACK,56, 770, "Carola Hartmann Miles-Verlag");
                            Ausgabe(cos, fontBold, 11, Color.BLACK,56, 755, "Anlagen zur Umsatzsteuervoranmeldung, Steuernummer " + field_Zeile3.getText());
                            Ausgabe(cos, fontBold, 11, Color.BLACK,56, 730, "Beilage 1 - Steuerpflichtige Umsätze zum Steuersatz von 19%");
                            Ausgabe(cos, fontBold, 11, Color.BLACK,500, 730, "Seite " + Integer.toString(seite));
                            Ausgabe(cos, fontBold, 11, Color.BLACK,56, 700, "Datum");
                            Ausgabe(cos, fontBold, 11, Color.BLACK,130, 700, "Beschreibung");
                            Ausgabe(cos, fontBold, 11, Color.BLACK,450, 700, "Netto");
                            Ausgabe(cos, fontBold, 11, Color.BLACK,500, 700, "Steuer");
                        }
                        Ausgabe(cos, fontPlain, 11, Color.BLACK,56, 680 - 15 * (zeile - 1), resultBest.getString("EINNAHMEN_RECHDATUM"));
                        Ausgabe(cos, fontPlain, 11, Color.BLACK,130, 680 - 15 * (zeile - 1), resultBest.getString("EINNAHMEN_BESCHREIBUNG"));
                        Netto = resultBest.getFloat("EINNAHMEN_KOSTEN") * 100.0 / 119;
                        Ustr = resultBest.getFloat("EINNAHMEN_KOSTEN") - Netto;
                        AusgabeDB(cos, fontPlain, 11, Color.BLACK,470, 680 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Netto)));
                        AusgabeDB(cos, fontPlain, 11, Color.BLACK,520, 680 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Ustr)));
                        zeile = zeile + 1;
                        Gesamtsumme = Gesamtsumme + resultBest.getFloat("EINNAHMEN_KOSTEN");
                        GesamtNetto = GesamtNetto + Netto;
                        GesamtUstr = GesamtUstr + Ustr;
                    } // while
                    Linie(cos,1,56, 687 - 15 * zeile, 539, 687 - 15 * zeile);
                    Ausgabe(cos, fontBold, 11, Color.BLACK,56, 680 - 15 * (zeile + 1), "Gesamtsumme der Umsätze");
                    AusgabeDB(cos, fontBold, 11, Color.BLACK,470, 680 - 15 * (zeile + 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(GesamtNetto)));
                    AusgabeDB(cos, fontBold, 11, Color.BLACK,520, 680 - 15 * (zeile + 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(GesamtUstr)));

                } catch (SQLException exept) {
                    Modulhelferlein.Fehlermeldung("SQL-Exception: SQL-Anfrage nicht moeglich. " + exept.getMessage());
                } // try
            } // if

            cos.close();

            // Beilage 2 erstellen  
            page = new PDPage(A4);
            document.addPage(page);

            cos = new PDPageContentStream(document, page);

            zeile = 1;
            seite = 1;

            Ausgabe(cos, fontBold, 11, Color.BLACK,56, 770, "Carola Hartmann Miles-Verlag");
            Ausgabe(cos, fontBold, 11, Color.BLACK,56, 755, "Anlagen zur Umsatzsteuervoranmeldung, Steuernummer " + field_Zeile3.getText());
            Ausgabe(cos, fontBold, 11, Color.BLACK,56, 730, "Beilage 2 - Steuerpflichtige Umsätze zum Steuersatz von 7%");
            Ausgabe(cos, fontBold, 11, Color.BLACK,500, 730, "Seite " + Integer.toString(seite));
            Ausgabe(cos, fontBold, 11, Color.BLACK,56, 700, "Datum");
            Ausgabe(cos, fontBold, 11, Color.BLACK,130, 700, "Beschreibung");
            Ausgabe(cos, fontBold, 11, Color.BLACK,450, 700, "Netto");
            Ausgabe(cos, fontBold, 11, Color.BLACK,500, 700, "Steuer");

            conn = null;

            try { // Datenbank-Treiber laden
                Class.forName(Modulhelferlein.dbDriver);
            } catch (ClassNotFoundException exept) {
                Modulhelferlein.Fehlermeldung("Treiber nicht gefunden.");
            }

            try { // Verbindung zur Datenbank über die JDBC-Brücke
                conn = DriverManager.getConnection(Modulhelferlein.dbUrl, Modulhelferlein.dbUser, Modulhelferlein.dbPassword);
            } catch (SQLException exept) {
                Modulhelferlein.Fehlermeldung("SQL-Exception: Verbindung zur Datenbank nicht moeglich.");
            }

            if (conn != null) {

                try {
                    SQLAnfrageBest = null; // Anfrage erzeugen
                    SQLAnfrageBuch = null; // Anfrage erzeugen
                    SQLAnfragePreis = null; // Anfrage erzeugen

                    try {
                        SQLAnfrageBest = conn.createStatement();
                        SQLAnfrageBuch = conn.createStatement();
                        SQLAnfragePreis = conn.createStatement();

                        resultBest = SQLAnfrageBest.executeQuery("SELECT * FROM TBL_BESTELLUNG "
                                + "WHERE BESTELLUNG_DATUM BETWEEN '" + von + "' AND '" + bis + "'");
                        GesamtUstr = 0D;
                        GesamtNetto = 0D;
                        while (resultBest.next()) {
                            if (zeile == 41) {
                                seite = seite + 1;
                                zeile = 1;
                                Ausgabe(cos, fontBold, 11, Color.BLACK,56, 770, "Carola Hartmann Miles-Verlag");
                                Ausgabe(cos, fontBold, 11, Color.BLACK,56, 755, "Anlagen zur Umsatzsteuervoranmeldung, Steuernummer " + field_Zeile3.getText());
                                Ausgabe(cos, fontBold, 11, Color.BLACK,56, 730, "Beilage 2 - Steuerpflichtige Umsätze zum Steuersatz von 7%");
                                Ausgabe(cos, fontBold, 11, Color.BLACK,500, 730, "Seite " + Integer.toString(seite));
                                Ausgabe(cos, fontBold, 11, Color.BLACK,56, 700, "Datum");
                                Ausgabe(cos, fontBold, 11, Color.BLACK,130, 700, "Beschreibung");
                                Ausgabe(cos, fontBold, 11, Color.BLACK,450, 700, "Netto");
                                Ausgabe(cos, fontBold, 11, Color.BLACK,500, 700, "Steuer");
                            } // if

                            resultBuch = SQLAnfrageBuch.executeQuery("SELECT * FROM TBL_BESTELLUNG_DETAIL WHERE BESTELLUNG_DETAIL_RECHNR = '" + resultBest.getString("BESTELLUNG_RECHNR") + "'");
                            BestellungZeile = 0D;
                            while (resultBuch.next()) {
                                resultPreis = SQLAnfragePreis.executeQuery("SELECT * FROM TBL_BUCH WHERE BUCH_ID = " + Integer.toString(resultBuch.getInt("BESTELLUNG_DETAIL_BUCH")));
                                if (resultPreis.next()) {
                                    BestellungZeile = (double) resultPreis.getFloat("BUCH_PREIS");
                                    BestellungZeile = BestellungZeile - (resultBuch.getInt("BESTELLUNG_DETAIL_RABATT") * BestellungZeile / 100);
                                    BestellungZeile = BestellungZeile * resultBuch.getInt("BESTELLUNG_DETAIL_ANZAHL");
                                } // if
                            } // while
                            Ausgabe(cos, fontPlain, 11, Color.BLACK,56, 680 - 15 * (zeile - 1), resultBest.getString("BESTELLUNG_DATUM"));
                            Ausgabe(cos, fontPlain, 11, Color.BLACK,130, 680 - 15 * (zeile - 1), resultBest.getString("BESTELLUNG_RECHNR"));
                            Netto = BestellungZeile * 100.0 / 107;
                            Ustr = BestellungZeile - Netto;
                            AusgabeDB(cos, fontPlain, 11, Color.BLACK,470, 680 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Netto)));
                            AusgabeDB(cos, fontPlain, 11, Color.BLACK,520, 680 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Ustr)));
                            GesamtNetto = GesamtNetto + Netto;
                            GesamtUstr = GesamtUstr + Ustr;
                            zeile = zeile + 1;
                        } // while

                    } catch (SQLException exept) {
                        Modulhelferlein.Fehlermeldung("SQL-Exception: SQL-Anfrage nicht moeglich. " + exept.getMessage());
                        // System.exit(1);
                    } // try

                    resultBest = SQLAnfrageBest.executeQuery("SELECT * FROM TBL_EINNAHMEN "
                            + " WHERE EINNAHMEN_RECHDATUM BETWEEN '" + von + "' AND '" + bis + "'"
                            + " AND EINNAHMEN_USTR = '7'");

                    while (resultBest.next()) {
                        if (zeile == 41) {
                            seite = seite + 1;
                            zeile = 1;
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 56, 770, "Carola Hartmann Miles-Verlag");
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 56, 755, "Anlagen zur Umsatzsteuervoranmeldung, Steuernummer " + field_Zeile3.getText());
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 56, 730, "Beilage 2 - Steuerpflichtige Umsätze zum Steuersatz von 7%");
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 500, 730, "Seite " + Integer.toString(seite));
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 56, 700, "Datum");
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 130, 700, "Beschreibung");
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 450, 700, "Netto");
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 500, 700, "Steuer");
                        } // if
                        Ausgabe(cos, fontPlain, 11, Color.BLACK, 56, 680 - 15 * (zeile - 1), resultBest.getString("EINNAHMEN_RECHDATUM"));
                        Ausgabe(cos, fontPlain, 11, Color.BLACK, 130, 680 - 15 * (zeile - 1), resultBest.getString("EINNAHMEN_BESCHREIBUNG"));
                        Netto = resultBest.getFloat("EINNAHMEN_KOSTEN") * 100.0 / 107;
                        Ustr = resultBest.getFloat("EINNAHMEN_KOSTEN") - Netto;
                        AusgabeDB(cos, fontPlain, 11, Color.BLACK, 470, 680 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Netto)));
                        AusgabeDB(cos, fontPlain, 11, Color.BLACK, 520, 680 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Ustr)));
                        zeile = zeile + 1;
                        GesamtNetto = GesamtNetto + Netto;
                        GesamtUstr = GesamtUstr + Ustr;
                    } // while
                    Linie(cos,1,56, 687 - 15 * zeile, 539, 687 - 15 * zeile);
                    Ausgabe(cos, fontBold, 11, Color.BLACK, 56, 680 - 15 * (zeile + 1), "Gesamtsumme der Umsätze");
                    AusgabeDB(cos, fontBold, 11, Color.BLACK, 470, 680 - 15 * (zeile + 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(GesamtNetto)));
                    AusgabeDB(cos, fontBold, 11, Color.BLACK, 520, 680 - 15 * (zeile + 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(GesamtUstr)));
                } catch (SQLException exept) {
                    Modulhelferlein.Fehlermeldung("SQL-Exception: SQL-Anfrage nicht moeglich. " + exept.getMessage());
                } // try
            } // if
            cos.close();

            // Beilage 3 erstellen  
            page = new PDPage(A4);
            document.addPage(page);

            cos = new PDPageContentStream(document, page);

            zeile = 1;
            seite = 1;

            Ausgabe(cos, fontBold, 11, Color.BLACK, 56, 770, "Carola Hartmann Miles-Verlag");
            Ausgabe(cos, fontBold, 11, Color.BLACK, 56, 755, "Anlagen zur Umsatzsteuervoranmeldung, Steuernummer " + field_Zeile3.getText());
            Ausgabe(cos, fontBold, 11, Color.BLACK, 56, 730, "Beilage 3 - Steuerpflichtige Umsätze zu anderen Steuersätzen");
            Ausgabe(cos, fontBold, 11, Color.BLACK, 500, 730, "Seite " + Integer.toString(seite));
            Ausgabe(cos, fontBold, 11, Color.BLACK, 56, 700, "Datum");
            Ausgabe(cos, fontBold, 11, Color.BLACK, 130, 700, "Beschreibung");
            Ausgabe(cos, fontBold, 11, Color.BLACK, 410, 700, "UStr");
            Ausgabe(cos, fontBold, 11, Color.BLACK, 450, 700, "Netto");
            Ausgabe(cos, fontBold, 11, Color.BLACK, 500, 700, "Steuer");

            conn = null;

            try { // Datenbank-Treiber laden
                Class.forName(Modulhelferlein.dbDriver);
            } catch (ClassNotFoundException exept) {
                Modulhelferlein.Fehlermeldung("Treiber nicht gefunden.");
            }

            try { // Verbindung zur Datenbank über die JDBC-Brücke
                conn = DriverManager.getConnection(Modulhelferlein.dbUrl, Modulhelferlein.dbUser, Modulhelferlein.dbPassword);
            } catch (SQLException exept) {
                Modulhelferlein.Fehlermeldung("SQL-Exception: Verbindung zur Datenbank nicht moeglich.");
            }

            if (conn != null) {
                SQLAnfrageBest = null; // Anfrage erzeugen

                try {
                    SQLAnfrageBest = conn.createStatement();

                    resultBest = SQLAnfrageBest.executeQuery("SELECT * FROM TBL_EINNAHMEN "
                            + " WHERE EINNAHMEN_RECHDATUM BETWEEN '" + von + "' AND '" + bis + "'"
                            + " AND ((EINNAHMEN_USTR <> '19') AND (EINNAHMEN_USTR <> '7'))");
                    GesamtNetto = 0D;
                    GesamtUstr = 0D;
                    while (resultBest.next()) {
                        if (zeile == 41) {
                            seite = seite + 1;
                            zeile = 1;
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 56, 770, "Carola Hartmann Miles-Verlag");
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 56, 755, "Anlagen zur Umsatzsteuervoranmeldung, Steuernummer " + field_Zeile3.getText());
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 56, 730, "Beilage 4 - Abziehbare Vorsteuerbeträge");
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 500, 730, "Seite " + Integer.toString(seite));
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 56, 700, "Datum");
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 130, 700, "Beschreibung");
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 410, 700, "UStr");
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 450, 700, "Netto");
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 500, 700, "Steuer");
                        }
                        Ausgabe(cos, fontPlain, 11, Color.BLACK, 56, 680 - 15 * (zeile - 1), resultBest.getString("EINNAHMEN_RECHDATUM"));
                        if (resultBest.getString("EINNAHMEN_BESCHREIBUNG").length() > 55) {
                            Ausgabe(cos, fontPlain, 11, Color.BLACK, 130, 680 - 15 * (zeile - 1), resultBest.getString("EINNAHMEN_BESCHREIBUNG").substring(0, 59) + "...");
                        } else {
                            Ausgabe(cos, fontPlain, 11, Color.BLACK, 130, 680 - 15 * (zeile - 1), resultBest.getString("EINNAHMEN_BESCHREIBUNG"));
                        }
                        Netto = resultBest.getFloat("EINNAHMEN_KOSTEN") * 100.0 / (100 + resultBest.getInt("EINNAHMEN_USTR"));
                        Ustr = resultBest.getFloat("EINNAHMEN_KOSTEN") - Netto;
                        AusgabeRB(cos, fontPlain, 11, Color.BLACK, 430, 680 - 15 * (zeile - 1), Integer.toString(resultBest.getInt("EINNAHMEN_USTR")));
                        AusgabeDB(cos, fontPlain, 11, Color.BLACK, 470, 680 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Netto)));
                        AusgabeDB(cos, fontPlain, 11, Color.BLACK, 520, 680 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Ustr)));
                        zeile = zeile + 1;

                        GesamtNetto = GesamtNetto + Netto;
                        GesamtUstr = GesamtUstr + Ustr;
                    } // while
                    Linie(cos,1,56, 687 - 15 * zeile, 539, 687 - 15 * zeile);
                    Ausgabe(cos, fontBold, 11, Color.BLACK, 56, 680 - 15 * (zeile + 1), "Gesamtsumme der Umsätze");
                    AusgabeDB(cos, fontBold, 11, Color.BLACK, 470, 680 - 15 * (zeile + 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(GesamtNetto)));
                    AusgabeDB(cos, fontBold, 11, Color.BLACK, 520, 680 - 15 * (zeile + 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(GesamtUstr)));

                    
                } catch (SQLException exept) {
                    Modulhelferlein.Fehlermeldung("SQL-Exception: SQL-Anfrage nicht moeglich. " , exept.getMessage());
                } // try
            } // if

            cos.close();

            // Beilage 4 erstellen  
            page = new PDPage(A4);
            document.addPage(page);

            cos = new PDPageContentStream(document, page);

            zeile = 1;
            seite = 1;

            Ausgabe(cos, fontBold, 11, Color.BLACK, 56, 770, "Carola Hartmann Miles-Verlag");
            Ausgabe(cos, fontBold, 11, Color.BLACK, 56, 755, "Anlagen zur Umsatzsteuervoranmeldung, Steuernummer " + field_Zeile3.getText());
            Ausgabe(cos, fontBold, 11, Color.BLACK, 56, 730, "Beilage 4 - Abziehbare Vorsteuerbeträge");
            Ausgabe(cos, fontBold, 11, Color.BLACK, 500, 730, "Seite " + Integer.toString(seite));
            Ausgabe(cos, fontBold, 11, Color.BLACK, 56, 700, "Datum");
            Ausgabe(cos, fontBold, 11, Color.BLACK, 130, 700, "Beschreibung");
            Ausgabe(cos, fontBold, 11, Color.BLACK, 450, 700, "Netto");
            Ausgabe(cos, fontBold, 11, Color.BLACK, 500, 700, "Steuer");

            conn = null;

            try { // Datenbank-Treiber laden
                Class.forName(Modulhelferlein.dbDriver);
            } catch (ClassNotFoundException exept) {
                Modulhelferlein.Fehlermeldung("Treiber nicht gefunden.");
            }

            try { // Verbindung zur Datenbank über die JDBC-Brücke
                conn = DriverManager.getConnection(Modulhelferlein.dbUrl, Modulhelferlein.dbUser, Modulhelferlein.dbPassword);
            } catch (SQLException exept) {
                Modulhelferlein.Fehlermeldung("SQL-Exception: Verbindung zur Datenbank nicht moeglich.");
            }

            if (conn != null) {
                SQLAnfrageBest = null; // Anfrage erzeugen

                try {
                    SQLAnfrageBest = conn.createStatement();

                    resultBest = SQLAnfrageBest.executeQuery("SELECT * FROM TBL_AUSGABEN "
                            + " WHERE AUSGABEN_RECHDATUM BETWEEN '" + von + "' AND '" + bis + "'");
                    GesamtNetto = 0D;
                    GesamtUstr = 0D;
                    while (resultBest.next()) {
                        if (zeile == 41) {
                            seite = seite + 1;
                            zeile = 1;
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 56, 770, "Carola Hartmann Miles-Verlag");
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 56, 755, "Anlagen zur Umsatzsteuervoranmeldung, Steuernummer " + field_Zeile3.getText());
                            Ausgabe(cos, fontBold, 11,Color.BLACK,  56, 730, "Beilage 4 - Abziehbare Vorsteuerbeträge");
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 500, 730, "Seite " + Integer.toString(seite));
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 56, 700, "Datum");
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 130, 700, "Beschreibung");
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 450, 700, "Netto");
                            Ausgabe(cos, fontBold, 11, Color.BLACK, 500, 700, "Steuer");
                        }
                        Ausgabe(cos, fontPlain, 11, Color.BLACK, 56, 680 - 15 * (zeile - 1), resultBest.getString("AUSGABEN_RECHDATUM"));
                        if (resultBest.getString("AUSGABEN_BESCHREIBUNG").length() > 60) {
                            Ausgabe(cos, fontPlain, 11, Color.BLACK, 130, 680 - 15 * (zeile - 1), resultBest.getString("AUSGABEN_BESCHREIBUNG").substring(0, 59) + "...");
                        } else {
                            Ausgabe(cos, fontPlain, 11, Color.BLACK, 130, 680 - 15 * (zeile - 1), resultBest.getString("AUSGABEN_BESCHREIBUNG"));
                        }
                        Netto = resultBest.getFloat("AUSGABEN_KOSTEN") * 100.0 / (100 + resultBest.getInt("AUSGABEN_USTR"));
                        Ustr = resultBest.getFloat("AUSGABEN_KOSTEN") - Netto;
                        AusgabeDB(cos, fontPlain, 11, Color.BLACK, 470, 680 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Netto)));
                        AusgabeDB(cos, fontPlain, 11, Color.BLACK, 520, 680 - 15 * (zeile - 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(Ustr)));
                        zeile = zeile + 1;

                        GesamtNetto = GesamtNetto + Netto;
                        GesamtUstr = GesamtUstr + Ustr;
                    } // while
                    Linie(cos,1,56, 687 - 15 * zeile, 539, 687 - 15 * zeile);
                    cos.closeAndStroke();
                    Ausgabe(cos, fontBold, 11, Color.BLACK, 56, 680 - 15 * (zeile + 1), "Gesamtsumme der Umsätze");
                    AusgabeDB(cos, fontBold, 11, Color.BLACK, 470, 680 - 15 * (zeile + 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(GesamtNetto)));
                    AusgabeDB(cos, fontBold, 11, Color.BLACK, 520, 680 - 15 * (zeile + 1), Modulhelferlein.df.format(Modulhelferlein.round2dec(GesamtUstr)));

                    if (resultBuch != null) {
                        resultBuch.close();
                    }
                    if (resultPreis != null) {
                        resultPreis.close();
                    }
                    if (SQLAnfrageBuch != null) {
                        SQLAnfrageBuch.close();
                    }
                    if (SQLAnfragePreis != null) {
                        SQLAnfragePreis.close();
                    }
                    if (conn != null) {
                        conn.close();
                    }

                } catch (SQLException exept) {
                    Modulhelferlein.Fehlermeldung("SQL-Exception: SQL-Anfrage nicht moeglich. " + exept.getMessage());
                    // System.exit(1);
                } // try
            } // if

            // close the content stream for the document
            cos.close();

            // Save the results and ensure that the document is properly closed:
                document.save(outputFileName + "-anlagen.pdf");
            document.close();

            Modulhelferlein.Infomeldung("Anlagen zur Umsatzsteuervoranmeldung als PDF gespeichert!");
            try {
                Runtime.getRuntime().exec("cmd.exe /c " + outputFileName + "-anlagen.pdf");
            } catch (IOException exept) {
                Modulhelferlein.Fehlermeldung("Exception: " + exept.getMessage());
            } // try 
        } catch (IOException e1) {
            Modulhelferlein.Fehlermeldung("IO-Exception: " + e1.getMessage());
        }
    }


    /*
     *  Berechnet die Felder der UStVA aus den Einnahmen, Ausgaben
     *  
     *  @param int zeitraum
     */
    public void berechne_felder(int zeitraum) {
        // Berechne Feld 81 aus den Einnahmen - 19%
        field_KZ81.setText(berechne_81(zeitraum));
        UStr81 = (double) (Integer.parseInt(field_KZ81.getText()) * 19 / 100);
        UStr81 = Modulhelferlein.round2dec(UStr81);
        field_Zeile26.setText(Double.toString(UStr81));

        // Berechne Feld 86 aus den Buchbestellungen/Einnahmen - 7%
        field_KZ86.setText(berechne_86(zeitraum));
        UStr86 = (double) (Integer.parseInt(field_KZ86.getText()) * 7 / 100);
        UStr86 = Modulhelferlein.round2dec(UStr86);
        field_Zeile27.setText(Double.toString(UStr86));

        // Berechne Feld 35, 36 aus den Einnahmen <> 7%, 19%
        field_KZ35.setText(berechne_35(zeitraum));
        field_KZ36.setText(berechne_36(field_Monat.getSelectedIndex()));
        UStr35 = Double.parseDouble(field_KZ36.getText());
        UStr35 = Modulhelferlein.round2dec(UStr35);

        // Berechne Feld 66 aus den Ausgaben - jeweils die UStr aufsummieren    
        field_KZ66.setText(berechne_66(zeitraum));
        UStr66 = Double.parseDouble(field_KZ66.getText());
        UStr66 = Modulhelferlein.round2dec(UStr66);

        // Berechne Feld 83     
        UStr83 = Modulhelferlein.round2dec(UStr81 + UStr86 + UStr35 - UStr66);
        field_KZ83.setText(Double.toString(UStr83));
    }

    /**
     * analysiert den Event
     *
     * @param e Ereignis, das beim Klicken des Buttons ausgelöst wurde
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == Anfang) {
            KlickAnfang(e);
        } else if (e.getSource() == Vor) {
            KlickVor(e);
        } else if (e.getSource() == Zurueck) {
            KlickZurueck(e);
        } else if (e.getSource() == Ende) {
            KlickEnde(e);
        } else if (e.getSource() == Einfuegen) {
            KlickEinfuegen(e);
        } else if (e.getSource() == Loeschen) {
            if (JOptionPane.showConfirmDialog(null, "Soll der Datensatz wirklich gelöscht werden?") == JOptionPane.YES_OPTION) {
                KlickLoeschen(e);
            }
        } else if (e.getSource() == Update) {
            KlickUpdate(e);
        } else if (e.getSource() == Suchen) {
            KlickSuchen(e);
        } else if (e.getSource() == WSuchen) {
            KlickWSuchen(e);
        } else if (e.getSource() == Schliessen) {
            KlickSchliessen(e);
        } else if (e.getSource() == Drucken) {
            KlickDrucken(e);
        } else if (e.getSource() == Senden) {
            KlickSenden(e);
        } else if (e.getSource() == Speichern) {
            KlickSpeichern(e);
        } else if (e.getSource() == Testen) {
            KlickTesten(e);
        } else if (e.getSource() == btnSchliessen5) {
            KlickBtnSchliessen5(e);
        } else if (e.getSource() == btnStrNr) {
            KlickBtnStrNr(e);
        } else if (e.getSource() == btnSchliessen) {
            KlickBtnSchliessen(e);
        } else if (e.getSource() == btnSpeichern5) {
            KlickBtnSpeichern5(e);
        } else if (e.getSource() == field_Monat) {
            rbMonat.setSelected(true);
            berechne_felder(field_Monat.getSelectedIndex());
        } else if (e.getSource() == field_Quartal) {
            rbQuartal.setSelected(true);
            switch (field_Quartal.getSelectedIndex()) {
                case 0:
                    berechne_felder(0);
                    break;
                case 1:
                    berechne_felder(41);
                    break;
                case 2:
                    berechne_felder(42);
                    break;
                case 3:
                    berechne_felder(43);
                    break;
                case 4:
                    berechne_felder(44);
                    break;
            }
        } // if
    } // void

    /**
     * Berechnet die erhaltenen Umsätze mit anderer Ustr in einem Zeitraum
     *
     * @param selectedIndex
     * @return Netto-Umsätze im Monat/Quartal
     */
    private String berechne_35(int selectedIndex) {

        switch (selectedIndex) {
            case 0:
                von = "2015-00-00";
                bis = "2015-00-00";
                break;
            case 1:
                von = "2015-01-01";
                bis = "2015-01-31";
                break;
            case 2:
                von = "2015-02-01";
                bis = "2015-02-29";
                break;
            case 3:
                von = "2015-03-01";
                bis = "2015-03-31";
                break;
            case 4:
                von = "2015-04-01";
                bis = "2015-04-30";
                break;
            case 5:
                von = "2015-05-01";
                bis = "2015-05-31";
                break;
            case 6:
                von = "2015-06-01";
                bis = "2015-06-30";
                break;
            case 7:
                von = "2015-07-01";
                bis = "2015-07-31";
                break;
            case 8:
                von = "2015-08-01";
                bis = "2015-08-31";
                break;
            case 9:
                von = "2015-09-01";
                bis = "2015-09-30";
                break;
            case 10:
                von = "2015-10-01";
                bis = "2015-10-31";
                break;
            case 11:
                von = "2015-11-01";
                bis = "2015-11-30";
                break;
            case 12:
                von = "2015-12-01";
                bis = "2015-12-31";
                break;
            case 41:
                von = "2015-01-01";
                bis = "2015-03-31";
                break;
            case 42:
                von = "2015-04-01";
                bis = "2015-06-30";
                break;
            case 43:
                von = "2015-07-01";
                bis = "2015-09-30";
                break;
            case 44:
                von = "2015-10-01";
                bis = "2015-12-31";
                break;
        }

        conn = null;

        try { // Datenbank-Treiber laden
            Class.forName(Modulhelferlein.dbDriver);
        } catch (ClassNotFoundException exept) {
            Modulhelferlein.Fehlermeldung("Treiber nicht gefunden.");
        }

        try { // Verbindung zur Datenbank über die JDBC-Brücke
            conn = DriverManager.getConnection(Modulhelferlein.dbUrl, Modulhelferlein.dbUser, Modulhelferlein.dbPassword);
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: Verbindung zur Datenbank nicht moeglich.");
        }

        if (conn != null) {
            SQLAnfrageBest = null; // Anfrage erzeugen

            try {
                SQLAnfrageBest = conn.createStatement();

                resultBest = SQLAnfrageBest.executeQuery("SELECT * FROM TBL_EINNAHMEN "
                        + " WHERE EINNAHMEN_RECHDATUM BETWEEN '" + von + "' AND '" + bis + "'"
                        + " AND ((EINNAHMEN_USTR <> '19') AND (EINNAHMEN_USTR <> '7'))");
                Gesamtsumme = 0D;
                while (resultBest.next()) {
                    Gesamtsumme = Gesamtsumme + resultBest.getFloat("EINNAHMEN_KOSTEN") * 100 / (100 + resultBest.getInt("EINNAHMEN_USTR"));
                } // while

                if (resultBest != null) {
                    resultBest.close();
                }
                if (SQLAnfrageBest != null) {
                    SQLAnfrageBest.close();
                }
                if (conn != null) {
                    conn.close();
                }

            } catch (SQLException exept) {
                Modulhelferlein.Fehlermeldung("SQL-Exception: SQL-Anfrage nicht moeglich. " + exept.getMessage());
            } // try
        } // if
        return Long.toString(Math.round(Math.ceil(Gesamtsumme)));
    }

    /**
     * Berechnet die erhaltenen Umsatzsteuer mit anderer % Ustr in einem
     * Zeitraum
     *
     * @param selectedIndex
     * @return Gesamtsumme an erhaltener UStr im Monat/Quartal
     */
    private String berechne_36(int selectedIndex) {

        switch (selectedIndex) {
            case 0:
                von = "2015-00-00";
                bis = "2015-00-00";
                break;
            case 1:
                von = "2015-01-01";
                bis = "2015-01-31";
                break;
            case 2:
                von = "2015-02-01";
                bis = "2015-02-29";
                break;
            case 3:
                von = "2015-03-01";
                bis = "2015-03-31";
                break;
            case 4:
                von = "2015-04-01";
                bis = "2015-04-30";
                break;
            case 5:
                von = "2015-05-01";
                bis = "2015-05-31";
                break;
            case 6:
                von = "2015-06-01";
                bis = "2015-06-30";
                break;
            case 7:
                von = "2015-07-01";
                bis = "2015-07-31";
                break;
            case 8:
                von = "2015-08-01";
                bis = "2015-08-31";
                break;
            case 9:
                von = "2015-09-01";
                bis = "2015-09-30";
                break;
            case 10:
                von = "2015-10-01";
                bis = "2015-10-31";
                break;
            case 11:
                von = "2015-11-01";
                bis = "2015-11-30";
                break;
            case 12:
                von = "2015-12-01";
                bis = "2015-12-31";
                break;
            case 41:
                von = "2015-01-01";
                bis = "2015-03-31";
                break;
            case 42:
                von = "2015-04-01";
                bis = "2015-06-30";
                break;
            case 43:
                von = "2015-07-01";
                bis = "2015-09-30";
                break;
            case 44:
                von = "2015-10-01";
                bis = "2015-12-31";
                break;
        }

        conn = null;

        try { // Datenbank-Treiber laden
            Class.forName(Modulhelferlein.dbDriver);
        } catch (ClassNotFoundException exept) {
            Modulhelferlein.Fehlermeldung("Treiber nicht gefunden.");
        }

        try { // Verbindung zur Datenbank über die JDBC-Brücke
            conn = DriverManager.getConnection(Modulhelferlein.dbUrl, Modulhelferlein.dbUser, Modulhelferlein.dbPassword);
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: Verbindung zur Datenbank nicht moeglich.");
        }

        if (conn != null) {
            SQLAnfrageBest = null; // Anfrage erzeugen

            try {
                SQLAnfrageBest = conn.createStatement();

                resultBest = SQLAnfrageBest.executeQuery("SELECT * FROM TBL_EINNAHMEN "
                        + " WHERE EINNAHMEN_RECHDATUM BETWEEN '" + von + "' AND '" + bis + "'"
                        + " AND ((EINNAHMEN_USTR <> '19') AND (EINNAHMEN_USTR <> '7'))");
                Gesamtsumme = 0D;
                while (resultBest.next()) {
                    Gesamtsumme = Gesamtsumme + (resultBest.getFloat("EINNAHMEN_KOSTEN") - resultBest.getFloat("EINNAHMEN_KOSTEN") * 100 / (100 + resultBest.getInt("EINNAHMEN_USTR")));
                } // while

                if (resultBest != null) {
                    resultBest.close();
                }
                if (SQLAnfrageBest != null) {
                    SQLAnfrageBest.close();
                }
                if (conn != null) {
                    conn.close();
                }

            } catch (SQLException exept) {
                Modulhelferlein.Fehlermeldung("SQL-Exception: SQL-Anfrage nicht moeglich. " + exept.getMessage());
            } // try
        } // if
        return Modulhelferlein.str2dec(Modulhelferlein.round2dec(Gesamtsumme));
    }

    /**
     * Berechnet die erhaltenen Umsätze mit 19% Ustr in einem Zeitraum
     *
     * @param selectedIndex
     * @return Gesamtsumme an gezahlter UStr im Monat/Quartal
     */
    private String berechne_81(int selectedIndex) {

        switch (selectedIndex) {
            case 0:
                von = "2015-00-00";
                bis = "2015-00-00";
                break;
            case 1:
                von = "2015-01-01";
                bis = "2015-01-31";
                break;
            case 2:
                von = "2015-02-01";
                bis = "2015-02-29";
                break;
            case 3:
                von = "2015-03-01";
                bis = "2015-03-31";
                break;
            case 4:
                von = "2015-04-01";
                bis = "2015-04-30";
                break;
            case 5:
                von = "2015-05-01";
                bis = "2015-05-31";
                break;
            case 6:
                von = "2015-06-01";
                bis = "2015-06-30";
                break;
            case 7:
                von = "2015-07-01";
                bis = "2015-07-31";
                break;
            case 8:
                von = "2015-08-01";
                bis = "2015-08-31";
                break;
            case 9:
                von = "2015-09-01";
                bis = "2015-09-30";
                break;
            case 10:
                von = "2015-10-01";
                bis = "2015-10-31";
                break;
            case 11:
                von = "2015-11-01";
                bis = "2015-11-30";
                break;
            case 12:
                von = "2015-12-01";
                bis = "2015-12-31";
                break;
            case 41:
                von = "2015-01-01";
                bis = "2015-03-31";
                break;
            case 42:
                von = "2015-04-01";
                bis = "2015-06-30";
                break;
            case 43:
                von = "2015-07-01";
                bis = "2015-09-30";
                break;
            case 44:
                von = "2015-10-01";
                bis = "2015-12-31";
                break;
        }

        conn = null;

        try { // Datenbank-Treiber laden
            Class.forName(Modulhelferlein.dbDriver);
        } catch (ClassNotFoundException exept) {
            Modulhelferlein.Fehlermeldung("Treiber nicht gefunden.");
        }

        try { // Verbindung zur Datenbank über die JDBC-Brücke
            conn = DriverManager.getConnection(Modulhelferlein.dbUrl, Modulhelferlein.dbUser, Modulhelferlein.dbPassword);
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: Verbindung zur Datenbank nicht moeglich.");
        }

        if (conn != null) {
            SQLAnfrageBest = null; // Anfrage erzeugen

            try {
                SQLAnfrageBest = conn.createStatement();

                resultBest = SQLAnfrageBest.executeQuery("SELECT * FROM TBL_EINNAHMEN "
                        + " WHERE EINNAHMEN_RECHDATUM BETWEEN '" + von + "' AND '" + bis + "'"
                        + " AND EINNAHMEN_USTR = '19'");
                Gesamtsumme = 0D;
                while (resultBest.next()) {
                    Gesamtsumme = Gesamtsumme + resultBest.getFloat("EINNAHMEN_KOSTEN");
                } // while

                if (resultBest != null) {
                    resultBest.close();
                }
                if (SQLAnfrageBest != null) {
                    SQLAnfrageBest.close();
                }
                if (conn != null) {
                    conn.close();
                }

            } catch (SQLException exept) {
                Modulhelferlein.Fehlermeldung("SQL-Exception: SQL-Anfrage nicht moeglich. " + exept.getMessage());
                // System.exit(1);
            } // try
        } // if
        Gesamtsumme = Gesamtsumme * 100 / 119;
        return Long.toString(Math.round(Math.ceil(Gesamtsumme)));
    }

    /**
     * Berechnet die erhaltenen Netto-Umsätze mit 7% Ustr in einem Zeitraum
     *
     * @param selectedIndex
     * @return Gesamtsumme an gezahlter UStr im Monat/Quartal
     */
    private String berechne_86(int selectedIndex) {

        switch (selectedIndex) {
            case 0:
                von = "2015-00-00";
                bis = "2015-00-00";
                break;
            case 1:
                von = "2015-01-01";
                bis = "2015-01-31";
                break;
            case 2:
                von = "2015-02-01";
                bis = "2015-02-29";
                break;
            case 3:
                von = "2015-03-01";
                bis = "2015-03-31";
                break;
            case 4:
                von = "2015-04-01";
                bis = "2015-04-30";
                break;
            case 5:
                von = "2015-05-01";
                bis = "2015-05-31";
                break;
            case 6:
                von = "2015-06-01";
                bis = "2015-06-30";
                break;
            case 7:
                von = "2015-07-01";
                bis = "2015-07-31";
                break;
            case 8:
                von = "2015-08-01";
                bis = "2015-08-31";
                break;
            case 9:
                von = "2015-09-01";
                bis = "2015-09-30";
                break;
            case 10:
                von = "2015-10-01";
                bis = "2015-10-31";
                break;
            case 11:
                von = "2015-11-01";
                bis = "2015-11-30";
                break;
            case 12:
                von = "2015-12-01";
                bis = "2015-12-31";
                break;
            case 41:
                von = "2015-01-01";
                bis = "2015-03-31";
                break;
            case 42:
                von = "2015-04-01";
                bis = "2015-06-30";
                break;
            case 43:
                von = "2015-07-01";
                bis = "2015-09-30";
                break;
            case 44:
                von = "2015-10-01";
                bis = "2015-12-31";
                break;
        }

        conn = null;

        try { // Datenbank-Treiber laden
            Class.forName(Modulhelferlein.dbDriver);
        } catch (ClassNotFoundException exept) {
            Modulhelferlein.Fehlermeldung("Treiber nicht gefunden.");
        }

        try { // Verbindung zur Datenbank über die JDBC-Brücke
            conn = DriverManager.getConnection(Modulhelferlein.dbUrl, Modulhelferlein.dbUser, Modulhelferlein.dbPassword);
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: Verbindung zur Datenbank nicht moeglich.");
        }

        if (conn != null) {
            SQLAnfrageBest = null; // Anfrage erzeugen
            SQLAnfrageBuch = null; // Anfrage erzeugen
            SQLAnfragePreis = null; // Anfrage erzeugen

            try {
                SQLAnfrageBest = conn.createStatement();
                SQLAnfrageBuch = conn.createStatement();
                SQLAnfragePreis = conn.createStatement();

                resultBest = SQLAnfrageBest.executeQuery("SELECT * FROM TBL_BESTELLUNG "
                        + "WHERE BESTELLUNG_DATUM BETWEEN '" + von + "' AND '" + bis + "'");
                GesamtNetto = 0D;
                GesamtUstr = 0D;
                while (resultBest.next()) { // gehe durch alle Bestellungen
                    resultBuch = SQLAnfrageBuch.executeQuery("SELECT * FROM TBL_BESTELLUNG_DETAIL WHERE BESTELLUNG_DETAIL_RECHNR = '" + resultBest.getString("BESTELLUNG_RECHNR") + "'");
                    BestellungZeile = 0D;
                    while (resultBuch.next()) { // hole alle Bücher der Bestellung
                        resultPreis = SQLAnfragePreis.executeQuery("SELECT * FROM TBL_BUCH WHERE BUCH_ID = " + Integer.toString(resultBuch.getInt("BESTELLUNG_DETAIL_BUCH")));
                        if (resultPreis.next()) { // hole den Preis des Buches
                            BestellungZeile = (double) resultPreis.getFloat("BUCH_PREIS");
                            BestellungZeile = BestellungZeile - (resultBuch.getInt("BESTELLUNG_DETAIL_RABATT") * BestellungZeile / 100);
                            BestellungZeile = BestellungZeile * resultBuch.getInt("BESTELLUNG_DETAIL_ANZAHL");
                        } // if
                    } // while
                    Netto = BestellungZeile * 100.0 / 107;
                    Ustr = BestellungZeile - Netto;
                    GesamtNetto = GesamtNetto + Netto;
                    GesamtUstr = GesamtUstr + Ustr;
                } // while

                resultBest = SQLAnfrageBest.executeQuery("SELECT * FROM TBL_EINNAHMEN "
                        + " WHERE EINNAHMEN_RECHDATUM BETWEEN '" + von + "' AND '" + bis + "'"
                        + " AND EINNAHMEN_USTR = '7'");

                while (resultBest.next()) {
                    Netto = resultBest.getFloat("EINNAHMEN_KOSTEN") * 100.0 / 107;
                    Ustr = resultBest.getFloat("EINNAHMEN_KOSTEN") - Netto;
                    GesamtNetto = GesamtNetto + Netto;
                    GesamtUstr = GesamtUstr + Ustr;
                } // while

                if (resultBuch != null) {
                    resultBuch.close();
                }
                if (resultBest != null) {
                    resultBest.close();
                }
                if (resultPreis != null) {
                    resultPreis.close();
                }
                if (SQLAnfrageBuch != null) {
                    SQLAnfrageBuch.close();
                }
                if (SQLAnfrageBest != null) {
                    SQLAnfrageBest.close();
                }
                if (SQLAnfragePreis != null) {
                    SQLAnfragePreis.close();
                }
                if (conn != null) {
                    conn.close();
                }

            } catch (SQLException exept) {
                Modulhelferlein.Fehlermeldung("SQL-Exception: SQL-Anfrage nicht moeglich. " + exept.getMessage());
                // System.exit(1);
            } // try
        } // if
        return Long.toString(Math.round(Math.ceil(GesamtNetto)));
    } // void

    /**
     * Berechnet die gezahlte Umsatzsteuer in einem Zeitraum
     *
     * @param selectedIndex
     * @return Gesamtsumme an gezahlter UStr im Monat/Quartal
     */
    private String berechne_66(int selectedIndex) {

        switch (selectedIndex) {
            case 0:
                von = "2015-00-00";
                bis = "2015-00-00";
                break;
            case 1:
                von = "2015-01-01";
                bis = "2015-01-31";
                break;
            case 2:
                von = "2015-02-01";
                bis = "2015-02-29";
                break;
            case 3:
                von = "2015-03-01";
                bis = "2015-03-31";
                break;
            case 4:
                von = "2015-04-01";
                bis = "2015-04-30";
                break;
            case 5:
                von = "2015-05-01";
                bis = "2015-05-31";
                break;
            case 6:
                von = "2015-06-01";
                bis = "2015-06-30";
                break;
            case 7:
                von = "2015-07-01";
                bis = "2015-07-31";
                break;
            case 8:
                von = "2015-08-01";
                bis = "2015-08-31";
                break;
            case 9:
                von = "2015-09-01";
                bis = "2015-09-30";
                break;
            case 10:
                von = "2015-10-01";
                bis = "2015-10-31";
                break;
            case 11:
                von = "2015-11-01";
                bis = "2015-11-30";
                break;
            case 12:
                von = "2015-12-01";
                bis = "2015-12-31";
                break;
            case 41:
                von = "2015-01-01";
                bis = "2015-03-31";
                break;
            case 42:
                von = "2015-04-01";
                bis = "2015-06-30";
                break;
            case 43:
                von = "2015-07-01";
                bis = "2015-09-30";
                break;
            case 44:
                von = "2015-10-01";
                bis = "2015-12-31";
                break;
        }

        conn = null;

        try { // Datenbank-Treiber laden
            Class.forName(Modulhelferlein.dbDriver);
        } catch (ClassNotFoundException exept) {
            Modulhelferlein.Fehlermeldung("Treiber nicht gefunden.");
        }

        try { // Verbindung zur Datenbank über die JDBC-Brücke
            conn = DriverManager.getConnection(Modulhelferlein.dbUrl, Modulhelferlein.dbUser, Modulhelferlein.dbPassword);
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: Verbindung zur Datenbank nicht moeglich.");
        }

        if (conn != null) {
            SQLAnfrageBest = null; // Anfrage erzeugen

            try {
                SQLAnfrageBest = conn.createStatement();

                resultBest = SQLAnfrageBest.executeQuery("SELECT * FROM TBL_AUSGABEN "
                        + " WHERE AUSGABEN_RECHDATUM BETWEEN '" + von + "' AND '" + bis + "'");
                GesamtUstr = 0D;
                while (resultBest.next()) {
                    Netto = (double) (resultBest.getFloat("AUSGABEN_KOSTEN") * 100 / (100 + resultBest.getInt("AUSGABEN_USTR")));
                    Ustr = resultBest.getFloat("AUSGABEN_KOSTEN") - Netto;
                    GesamtUstr = GesamtUstr + Ustr;
                } // while

                if (resultBest != null) {
                    resultBest.close();
                }
                if (SQLAnfrageBest != null) {
                    SQLAnfrageBest.close();
                }
                if (conn != null) {
                    conn.close();
                }

            } catch (SQLException exept) {
                Modulhelferlein.Fehlermeldung("SQL-Exception: SQL-Anfrage nicht moeglich. " + exept.getMessage());
                // System.exit(1);
            } // try
        } // if
        return Modulhelferlein.str2dec(Modulhelferlein.round2dec(GesamtUstr));
    }

    /**
     * Erzeugt das Hauptfenster mit Menü und Hintergrundbild
     */
    public void dlgUStrVor() {

        try {
            Eric.initialize(ericPath, logPath);

            eric = Eric.getInstance();

            // Aufbau Dialog 1	
            meinFenster.setSize(800, 650);
            meinFenster.setLayout(null);
            meinFenster.setResizable(false);
            meinFenster.setLocation(100, 10);

            JLabel lblStrNr = new JLabel("Steuernummer");
            lblStrNr.setBounds(10, 10, 100, 15);
            meinFenster.add(lblStrNr);
            field_Zeile3.setBounds(100, 10, 100, 15);
            field_Zeile3.setForeground(Color.BLUE);
            field_Zeile3.setOpaque(false);
            field_Zeile3.setEditable(false);
            meinFenster.add(field_Zeile3);

            JLabel lblFA = new JLabel("Finanzamt");
            lblFA.setBounds(10, 30, 100, 15);
            meinFenster.add(lblFA);
            field_Zeile4.setBounds(100, 30, 100, 15);
            field_Zeile4.setForeground(Color.BLUE);
            field_Zeile4.setOpaque(false);
            field_Zeile4.setEditable(false);
            meinFenster.add(field_Zeile4);

            btnStrNr.setBounds(220, 10, 125, 45);
            btnStrNr.addActionListener(this);
            meinFenster.add(btnStrNr);

            JLabel lblName = new JLabel("Unternehmen");
            lblName.setBounds(10, 80, 100, 15);
            meinFenster.add(lblName);
            field_Zeile12.setBounds(100, 80, 245, 15);
            field_Zeile12.setForeground(Color.BLUE);
            field_Zeile12.setOpaque(false);
            meinFenster.add(field_Zeile12);

            JLabel lblStrasse = new JLabel("Straße");
            lblStrasse.setBounds(10, 100, 100, 15);
            meinFenster.add(lblStrasse);
            field_Zeile13_1.setBounds(100, 100, 245, 15);
            field_Zeile13_1.setForeground(Color.BLUE);
            field_Zeile13_1.setOpaque(false);
            meinFenster.add(field_Zeile13_1);

            JLabel lblPLZ = new JLabel("PLZ, Ort");
            lblPLZ.setBounds(10, 120, 100, 15);
            meinFenster.add(lblPLZ);
            field_Zeile13_21.setBounds(100, 120, 65, 15);
            field_Zeile13_21.setForeground(Color.BLUE);
            field_Zeile13_21.setOpaque(false);
            field_Zeile13_21.addActionListener((ActionEvent e) -> {
                if (Modulhelferlein.checkNumberFormatInt(field_Zeile13_21.getText()) < 0) {
                    Modulhelferlein.Infomeldung("fehlerhafte Eingabe - die ist keine korrekte Postleitzahl");
                }
            });
            field_Zeile13_21.setForeground(Color.BLUE);
            meinFenster.add(field_Zeile13_21);
            field_Zeile13_22.setBounds(170, 120, 175, 15);
            field_Zeile13_22.setForeground(Color.BLUE);
            field_Zeile13_22.setOpaque(false);
            meinFenster.add(field_Zeile13_22);

            JLabel lblTelefon = new JLabel("Telefon");
            lblTelefon.setBounds(10, 140, 100, 15);
            meinFenster.add(lblTelefon);
            field_Zeile14.setBounds(100, 140, 245, 15);
            field_Zeile14.setForeground(Color.BLUE);
            field_Zeile14.setOpaque(false);
            meinFenster.add(field_Zeile14);

            JLabel lblemail = new JLabel("eMail");
            lblemail.setBounds(10, 160, 160, 15);
            meinFenster.add(lblemail);
            field_Zeile15.setBounds(100, 160, 245, 15);
            field_Zeile15.setForeground(Color.BLUE);
            field_Zeile15.setOpaque(false);
            meinFenster.add(field_Zeile15);

            JLabel lblTitel = new JLabel("Umsatzsteuer-Voranmeldung 2015");
            lblTitel.setFont(lblTitel.getFont().deriveFont(18f));
            lblTitel.setBounds(400, 20, 400, 30);
            meinFenster.add(lblTitel);

            JLabel lblZeitraum = new JLabel("Voranmeldungszeitraum");
            lblZeitraum.setFont(lblZeitraum.getFont().deriveFont(14f));
            lblZeitraum.setBounds(400, 80, 300, 15);
            meinFenster.add(lblZeitraum);
            rbMonat.setBounds(400, 100, 100, 20);
            meinFenster.add(rbMonat);
            rbMonat.setSelected(true);
            cbZeitraum.add(rbMonat);
            field_Monat.setBounds(420, 120, 90, 20);
            field_Monat.addActionListener(this);
            meinFenster.add(field_Monat);
            rbQuartal.setBounds(520, 100, 200, 20);
            meinFenster.add(rbQuartal);
            cbZeitraum.add(rbQuartal);
            field_Quartal.setBounds(540, 120, 90, 20);
            field_Quartal.addActionListener(this);
            meinFenster.add(field_Quartal);

            cb10.setBounds(400, 160, 200, 25);
            meinFenster.add(cb10);
            cb22.setBounds(600, 160, 200, 25);
            meinFenster.add(cb22);

            JLabel lblZeile17 = new JLabel("I. Anmeldung der Umsatzsteuer-Vorauszahlung");
            lblZeile17.setFont(lblZeile17.getFont().deriveFont(14f));
            lblZeile17.setBounds(10, 200, 500, 15);
            meinFenster.add(lblZeile17);
            JLabel lblZeile25 = new JLabel("Steuerpflichtige Umsätze");
            lblZeile25.setBounds(20, 220, 200, 15);
            meinFenster.add(lblZeile25);
            JLabel lblZeile26 = new JLabel("   zum Steuersatz 19%");
            lblZeile26.setBounds(20, 240, 200, 15);
            meinFenster.add(lblZeile26);
            field_KZ81.setBounds(200, 240, 50, 15);
            field_KZ81.setForeground(Color.BLUE);
            field_Zeile26.setBounds(280, 240, 65, 15);
            field_Zeile26.setEditable(false);
            meinFenster.add(field_Zeile26);
            field_KZ81.addActionListener((ActionEvent e) -> {
                if (Modulhelferlein.checkNumberFormatInt(field_KZ81.getText()) < 0) {
                    Modulhelferlein.Infomeldung("fehlerhafte Eingabe - die ist keine korrekte Ganzzahl");
                } else {
                    Double d = (double) (Integer.parseInt(field_KZ81.getText()) * 19 / 100);
                    d = Modulhelferlein.round2dec(d);
                    field_Zeile26.setText(Double.toString(d));
                }
            });
            meinFenster.add(field_KZ81);

            JLabel lblZeile27 = new JLabel("   zum Steuersatz  7%");
            lblZeile27.setBounds(20, 260, 200, 15);
            meinFenster.add(lblZeile27);
            field_KZ86.setBounds(200, 260, 50, 15);
            field_KZ86.setForeground(Color.BLUE);
            field_Zeile27.setBounds(280, 260, 65, 15);
            field_Zeile27.setEditable(false);
            meinFenster.add(field_Zeile27);
            field_KZ86.addActionListener((ActionEvent e) -> {
                if (Modulhelferlein.checkNumberFormatInt(field_KZ86.getText()) < 0) {
                    Modulhelferlein.Infomeldung("fehlerhafte Eingabe - die ist keine korrekte Ganzzahl");
                } else {
                    Double d = (double) (Integer.parseInt(field_KZ86.getText()) * 7 / 100);
                    d = Modulhelferlein.round2dec(d);
                    field_Zeile27.setText(Double.toString(d));
                }
            });
            meinFenster.add(field_KZ86);

            JLabel lblZeile28 = new JLabel("   zu anderen Steuersätzen");
            lblZeile28.setBounds(20, 280, 200, 15);
            meinFenster.add(lblZeile28);
            field_KZ35.setBounds(200, 280, 50, 15);
            field_KZ35.setForeground(Color.BLUE);
            field_KZ35.addActionListener((ActionEvent e) -> {
                if (Modulhelferlein.checkNumberFormatInt(field_KZ35.getText()) < 0) {
                    Modulhelferlein.Infomeldung("fehlerhafte Eingabe - die ist keine korrekte Ganzzahl");
                }
            });
            meinFenster.add(field_KZ35);
            field_KZ36.setBounds(280, 280, 65, 15);
            field_KZ36.setForeground(Color.BLUE);
            field_KZ36.addActionListener((ActionEvent e) -> {
                if (Modulhelferlein.checkNumberFormatFloat(field_KZ36.getText()) < 0) {
                    Modulhelferlein.Infomeldung("fehlerhafte Eingabe - die ist keine korrekte Zahl");
                } else {
                    Float f = Float.parseFloat(field_KZ36.getText());
                    field_KZ36.setText(Modulhelferlein.str2dec(Modulhelferlein.round2dec(f)));
                    UStr35 = Modulhelferlein.round2dec(Double.parseDouble(field_KZ36.getText()));
                }
            });
            meinFenster.add(field_KZ36);

            JLabel lblZeile55 = new JLabel("Abziehbare Vorsteuerbeträge");
            lblZeile55.setBounds(400, 220, 200, 15);
            meinFenster.add(lblZeile55);
            field_KZ66.setBounds(700, 220, 65, 15);
            field_KZ66.setForeground(Color.BLUE);
            field_KZ66.addActionListener((ActionEvent e) -> {
                if (Modulhelferlein.checkNumberFormatFloat(field_KZ66.getText()) < 0) {
                    Modulhelferlein.Infomeldung("fehlerhafte Eingabe - die ist keine korrekte Zahl");
                }
            });
            meinFenster.add(field_KZ66);

            JLabel lblZeile68 = new JLabel("Verbleibende Umsatzsteuervorauszahlung");
            lblZeile68.setBounds(400, 300, 400, 15);
            meinFenster.add(lblZeile68);
            field_KZ83.setBounds(700, 300, 65, 15);
            field_KZ83.setForeground(Color.BLUE);
            field_KZ83.addActionListener((ActionEvent e) -> {
                if (Modulhelferlein.checkNumberFormatFloat(field_KZ83.getText()) < 0) {
                    Modulhelferlein.Infomeldung("fehlerhafte Eingabe - die ist keine korrekte Zahl");
                }
            });
            meinFenster.add(field_KZ83);

            JLabel lblZeile85 = new JLabel("Datum");
            lblZeile85.setBounds(20, 450, 50, 15);
            meinFenster.add(lblZeile85);
            field_Datum.setBounds(70, 450, 100, 15);
            field_Datum.setForeground(Color.BLUE);
            field_Datum.setEditable(false);
            meinFenster.add(field_Datum);

            JLabel lblZeile70 = new JLabel("II. Sonstige Angaben");
            lblZeile70.setFont(lblZeile70.getFont().deriveFont(14f));
            lblZeile70.setBounds(10, 400, 200, 15);
            meinFenster.add(lblZeile70);
            cb29.setBounds(20, 420, 300, 25);
            meinFenster.add(cb29);
            cb26.setBounds(400, 420, 300, 25);
            meinFenster.add(cb26);

            Anfang.setBounds(20, 500, 50, 30);
            Anfang.addActionListener(this);
            Anfang.setToolTipText("Geht zum ersten Datensatz");
            meinFenster.add(Anfang);

            Zurueck.setBounds(70, 500, 50, 30);
            Zurueck.addActionListener(this);
            Zurueck.setToolTipText("Geht zum veorherigen Datensatz");
            meinFenster.add(Zurueck);

            Vor.setBounds(120, 500, 50, 30);
            Vor.addActionListener(this);
            Vor.setToolTipText("Geht zum nächsten Datensatz");
            meinFenster.add(Vor);

            Ende.setBounds(170, 500, 50, 30);
            Ende.addActionListener(this);
            Ende.setToolTipText("Geht zum letzten Datensatz");
            meinFenster.add(Ende);

            Update.setBounds(230, 500, 50, 30);
            Update.addActionListener(this);
            Update.setToolTipText("Aktualisiert den angezeigten Datensatz");
            meinFenster.add(Update);

            Einfuegen.setBounds(280, 500, 50, 30);
            Einfuegen.addActionListener(this);
            Einfuegen.setToolTipText("Fügt einen Datensatz hinzu");
            meinFenster.add(Einfuegen);

            Loeschen.setBounds(330, 500, 50, 30);
            Loeschen.addActionListener(this);
            Loeschen.setToolTipText("Löscht den angezeigten Datensatz");
            meinFenster.add(Loeschen);

            Suchen.setBounds(390, 500, 50, 30);
            Suchen.addActionListener(this);
            Suchen.setToolTipText("Sucht einen Datensatz");
            meinFenster.add(Suchen);

            WSuchen.setBounds(440, 500, 50, 30);
            WSuchen.addActionListener(this);
            WSuchen.setToolTipText("Sucht weiteren Datensatz");
            meinFenster.add(WSuchen);

            Speichern.setBounds(500, 500, 50, 30);
            Speichern.addActionListener(this);
            Speichern.setToolTipText("Speichert den Steuersatz");
            meinFenster.add(Speichern);

            Testen.setBounds(550, 500, 50, 30);
            Testen.addActionListener(this);
            Testen.setToolTipText("Testet den Steuersatz");
            meinFenster.add(Testen);

            Senden.setBounds(600, 500, 50, 30);
            Senden.addActionListener(this);
            Senden.setToolTipText("Sendet den Steuersatz");
            meinFenster.add(Senden);

            Drucken.setBounds(650, 500, 50, 30);
            Drucken.addActionListener(this);
            Drucken.setToolTipText("Druckt die Anlagen zum Steuersatz");
            meinFenster.add(Drucken);

            Schliessen.setBounds(710, 500, 50, 30);
            Schliessen.addActionListener(this);
            Schliessen.setToolTipText("Schließt den Dialog");
            meinFenster.add(Schliessen);

            field_count.setBounds(200, 567, 30, 30);
            field_count.setEditable(false);
            field_count.setHorizontalAlignment(JTextField.CENTER);
            meinFenster.add(field_count);
            field_countMax.setBounds(280, 567, 30, 30);
            field_countMax.setEditable(false);
            field_countMax.setHorizontalAlignment(JTextField.CENTER);
            meinFenster.add(field_countMax);
            JLabel Az1 = new JLabel("Angezeigt wird Datensatz Nr.");
            Az1.setBounds(20, 570, 200, 20);
            meinFenster.add(Az1);
            JLabel Az2 = new JLabel("von");
            Az2.setBounds(240, 570, 30, 20);
            meinFenster.add(Az2);

            meinFenster.setVisible(true);

//helferlein.Infomeldung("starte datenbank");
            conn = null;

            // Datenbank-Treiber laden
            try {
                Class.forName(Modulhelferlein.dbDriver);
            } catch (ClassNotFoundException exept) {
                Modulhelferlein.Fehlermeldung("ClassNotFoundException: Treiber nicht gefunden: " + exept.getMessage());
                System.exit(1);
            }

            // Verbindung zur Datenbank über die JDBC-Brücke
            try {
                conn = DriverManager.getConnection(Modulhelferlein.dbUrl, Modulhelferlein.dbUser, Modulhelferlein.dbPassword);
            } catch (SQLException exept) {
                Modulhelferlein.Fehlermeldung(
                        "SQL-Exception: Verbindung zur Datenbank nicht moeglich: " + exept.getMessage());
            } // try

            if (conn != null) {
                SQLAnfrage = null; // Anfrage erzeugen
                SQLAnfrage2 = null; // Anfrage erzeugen
                SQLAnfrage3 = null; // Anfrage erzeugen

                try {
                    SQLAnfrage2 = conn.createStatement(
                            ResultSet.TYPE_SCROLL_SENSITIVE,
                            ResultSet.CONCUR_UPDATABLE); // Anfrage der DB conn2 zuordnen
                    result2 = SQLAnfrage2.executeQuery("SELECT * FROM TBL_KONFIGURATION WHERE KONFIGURATION_ID = '2'"); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 
                    result2.next();  // Datensatz 2 - Steuerdaten
                    field_Zeile3.setText(result2.getString("Konfiguration_Stammdaten"));
                    field_Zeile4.setText(result2.getString("Konfiguration_Einnahmen"));
                    field_Zeile12.setText(result2.getString("Konfiguration_Ausgaben"));
                    field_Zeile13_1.setText(result2.getString("Konfiguration_Umsaetze"));
                    field_Zeile13_21.setText(result2.getString("Konfiguration_Rechnungen"));
                    field_Zeile13_22.setText(result2.getString("Konfiguration_Sicherung"));
                    field_Zeile14.setText(result2.getString("Konfiguration_Mahnungen"));
                    field_Zeile15.setText(result2.getString("Konfiguration_Termine"));
                    certificatePin = result2.getString("Konfiguration_Schriftverkehr");
                    certificate = result2.getString("Konfiguration_Steuer");
                    SQLAnfrage3 = conn.createStatement(
                            ResultSet.TYPE_SCROLL_SENSITIVE,
                            ResultSet.CONCUR_UPDATABLE); // Anfrage der DB conn2 zuordnen
                    result3 = SQLAnfrage3.executeQuery("SELECT * FROM TBL_KONFIGURATION WHERE KONFIGURATION_ID = '3'"); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 
                    result3.next();  // Datensatz 3 - Finanzamtsdaten

                    SQLAnfrage = conn.createStatement(
                            ResultSet.TYPE_SCROLL_SENSITIVE,
                            ResultSet.CONCUR_UPDATABLE); // Anfrage der DB conn2 zuordnen
                    result = SQLAnfrage.executeQuery("SELECT * FROM TBL_USTVA "); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 

                    // Anzahl der Datensätze ermitteln
                    countMax = 0;
                    count = 0;
                    field_count.setText(Integer.toString(count));
                    while (result.next()) {
                        ++countMax;
                    }
                    field_countMax.setText(Integer.toString(countMax));

                    meinFenster.setVisible(true);

                    // gehe zum ersten Datensatz - wenn nicht leer
                    if (result.first()) {
                        count = 1;
                        field_count.setText(Integer.toString(count));
                        resultIsEmpty = false;

                        field_Datum.setText(result.getString("USTVA_DATUM"));
                        cb10.setSelected(result.getBoolean("USTVA_KZ10"));
                        cb22.setSelected(result.getBoolean("USTVA_KZ26"));
                        cb26.setSelected(result.getBoolean("USTVA_KZ22"));
                        cb29.setSelected(result.getBoolean("USTVA_KZ29"));
                        if (result.getInt("USTVA_ZEITRAUM") > 12) {
                            field_Quartal.setSelectedIndex(result.getInt("USTVA_ZEITRAUM"));
                            rbQuartal.setSelected(true);
                        } else {
                            field_Monat.setSelectedIndex(result.getInt("USTVA_ZEITRAUM"));
                            rbMonat.setSelected(true);
                        }
                        field_KZ35.setText(Integer.toString(result.getInt("USTVA_KZ35")));
                        field_KZ36.setText(Modulhelferlein.Fstr2dec(result.getFloat("USTVA_KZ36")));
                        field_KZ66.setText(Float.toString(result.getFloat("USTVA_KZ66")));
                        field_KZ81.setText(Integer.toString(result.getInt("USTVA_KZ81")));
                        field_KZ83.setText(Float.toString(result.getFloat("USTVA_KZ83")));
                        field_KZ86.setText(Integer.toString(result.getInt("USTVA_KZ86")));

                        berechne_felder(result.getInt("USTVA_ZEITRAUM"));

                        // Schalterzustände setzen   
                        Anfang.setEnabled(true);
                        Zurueck.setEnabled(true);
                        Vor.setEnabled(true);
                        Ende.setEnabled(true);
                        Update.setEnabled(true);
                        Einfuegen.setEnabled(true);
                        Loeschen.setEnabled(true);
                        Suchen.setEnabled(true);
                        WSuchen.setEnabled(true);
                        if (result.getBoolean("USTVA_GESENDET")) {
                            Speichern.setEnabled(false);
                        } else {
                            Speichern.setEnabled(true);
                        }
                        Senden.setEnabled(false);
                        Testen.setEnabled(false);
                        Schliessen.setEnabled(true);
                    } else {
                        // Schalterzustände setzen   
                        Anfang.setEnabled(false);
                        Zurueck.setEnabled(false);
                        Vor.setEnabled(false);
                        Ende.setEnabled(false);
                        Update.setEnabled(false);
                        Einfuegen.setEnabled(true);
                        Loeschen.setEnabled(false);
                        Suchen.setEnabled(false);
                        WSuchen.setEnabled(false);
                        Speichern.setEnabled(true);
                        Senden.setEnabled(false);
                        Testen.setEnabled(false);
                        Schliessen.setEnabled(true);
                    }

                } catch (SQLException exept) {
                    Modulhelferlein.Fehlermeldung("SQLException: SQL-Anfrage nicht moeglich: " + exept.getMessage());
                } // try
            } // if

        } catch (de.elster.eric.wrapper.exception.EricException | de.elster.eric.wrapper.exception.WrapperException ex) {
            Logger.getLogger(ModulAbrechnungUSteuerVorA.class.getName()).log(Level.SEVERE, null, ex);
        }
        // try
        // try
        // try
        // try
        // try
        // try
        // try
        // try

    } // void

} // class
