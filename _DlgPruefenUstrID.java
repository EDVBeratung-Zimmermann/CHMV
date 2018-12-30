/*
 * Copyright (C) 2018 thoma
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
 */
package milesVerlagMain;

import java.awt.*;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;
import net.miginfocom.swing.*;
import static milesVerlagMain.Modulhelferlein.Ausgabe;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import static org.apache.pdfbox.pdmodel.common.PDRectangle.A4;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

/**
 *
 * @author thoma
 */
public class _DlgPruefenUstrID extends javax.swing.JDialog {

    /**
     * Creates new form _DlgPruefenUstrID
     * @param parent
     * @param modal
     */
    public _DlgPruefenUstrID(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        field_Rechtsform.addItem(" ; keine Rechtsform/natürliche Person");
        field_Rechtsform.addItem("AG; Aktiengesellschaft");
        field_Rechtsform.addItem("OG; offene Gesellschaft");
        field_Rechtsform.addItem("EGEN; eingetragene Genossenschaft");
        field_Rechtsform.addItem("EWIV; Europäische eingetragene Interessenvereinigung");
        field_Rechtsform.addItem("GEN; Genossenschaft");
        field_Rechtsform.addItem("GESBR; Gesellschaft bürgerlichen Rechts");
        field_Rechtsform.addItem("GESMBH; Gesellschaft mit beschränkter Haftung");
        field_Rechtsform.addItem("GESMBHCO; Gesellschaft mit beschränkter Haftung Co");
        field_Rechtsform.addItem("GESMBHCOKG; Gesellschaft mit beschränkter Haftung Co KG");
        field_Rechtsform.addItem("GESMBHCOKEG;Gesellschaft mit beschränkter Haftung Co KEG");
        field_Rechtsform.addItem("GESMBHCOOG; Gesellschaft mit beschränkter Haftung Co OG");
        field_Rechtsform.addItem("GESMBHCOOHG; Gesellschaft mit beschränkter Haftung Co OHG");
        field_Rechtsform.addItem("GNBR; Gesellschaftnach bürgelichem Recht");
        field_Rechtsform.addItem("KEG; Komanditerwerbsgesellschaft");
        field_Rechtsform.addItem("KG; Komanditgesellschaft");
        field_Rechtsform.addItem("OEG; Offene Erwerbsgesellschaft");
        field_Rechtsform.addItem("OHG; Offene Handelsgesellschaft");
        field_Rechtsform.addItem("PS; Privatstiftung");
        field_Rechtsform.addItem("REGEN; Registrierte Genossenschaft");
        field_Rechtsform.addItem("REGENMBH; Registrierte Genossenschaft mbH");
        field_Rechtsform.addItem("VAG; Versicherungsverein auf Gegenseitigkeit");
        field_Rechtsform.addItem("--- britische Rechtsformen");
        field_Rechtsform.addItem("Ltd; Limited");
        field_Rechtsform.addItem("PLC; Public limited company");
        field_Rechtsform.addItem("--- französische Rechtsformen");
        field_Rechtsform.addItem("ASS; ");
        field_Rechtsform.addItem("EURL; ");
        field_Rechtsform.addItem("EARL; ");
        field_Rechtsform.addItem("GAEC; ");
        field_Rechtsform.addItem("GIE; ");
        field_Rechtsform.addItem("GIP; ");
        field_Rechtsform.addItem("GAIE; ");
        field_Rechtsform.addItem("GFA; ");
        field_Rechtsform.addItem("SOFICA; ");
        field_Rechtsform.addItem("SC; ");
        field_Rechtsform.addItem("SAM; ");
        field_Rechtsform.addItem("SCCV; ");
        field_Rechtsform.addItem("SCM; ");
        field_Rechtsform.addItem("; ");
        field_Rechtsform.addItem("SA; Societe anonyme");
        field_Rechtsform.addItem("SARL; Societe responsabilite limite");
        field_Rechtsform.addItem("SAS; Societe par actions simplifee");
        field_Rechtsform.addItem("--- europäische Rechtsformen");
        field_Rechtsform.addItem("SE; Societe Europaea");
        field_Rechtsform.addItem("--- niederländische Rechtsformen");
        field_Rechtsform.addItem("BV; Besloten vennootschap");
        field_Rechtsform.addItem("COOP; Cooperative vereinigung ");
        field_Rechtsform.addItem("COPPBA; Cooperative vereinigung Beperkte Aansprakelijkheid");
        field_Rechtsform.addItem("COOPUA; Cooperative vereinigung Uitgesloten Aansprakelijkhei");
        field_Rechtsform.addItem("COOPWA; Cooperative vereinigung Wetelijke Aansprakelijkheid");
        field_Rechtsform.addItem("CV; Commanditaire vennootschap");
        field_Rechtsform.addItem("CVBV; Commanditaire vennootschap met beherend vennoot");
        field_Rechtsform.addItem("CVOA; Commanditaire vennootschap op aandelen");
        field_Rechtsform.addItem("OCV; Open Commanditaire vennootschap");
        field_Rechtsform.addItem("M; Maatschap");
        field_Rechtsform.addItem("NV; Naamloze vennootschap");
        field_Rechtsform.addItem("OWBA; Onderlinge waarborgmaatschappij Beperkte Aansprakelijkheid");
        field_Rechtsform.addItem("OWUA; Onderlinge waarborgmaatschappij Uitgesloten Aansprakelijkheid");
        field_Rechtsform.addItem("OWWA; Onderlinge waarborgmaatschappij Wettelijke Aansprakelijkheid");
        field_Rechtsform.addItem("VOF; Vennootschap onder firma");
        field_Rechtsform.addItem("VOFOA; Vennootschap onder firma op aandelen");
             
        
        /**
    <option value="SCPI"    >SCPI&nbsp;&nbsp; - Soci&#x00E9;t&#x00E9; civile de placement immobilier</option>
    <option value="SCEA"    >SCEA&nbsp;&nbsp; - Soci&#x00E9;t&#x00E9; civile en action</option>
    <option value="SCI"     >SCI&nbsp;&nbsp;&nbsp; - Soci&#x00E9;t&#x00E9; civile immobili&#x00E8;re</option>
    <option value="SCP"     >SCP&nbsp;&nbsp;&nbsp; - Soci&#x00E9;t&#x00E9; civile professionnelle</option>
    <option value="SCRI"    >SCRI&nbsp;&nbsp; - Soci&#x00E9;t&#x00E9; coop&#x00E9;rative &#x00E0; responsabilit&#x00E9; limit&#x00E9;e</option>
    <option value="SCC"     >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; - Soci&#x00E9;t&#x00E9; coop&#x00E9;rative de consommation</option>
    <option value="SCT"     >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; - Soci&#x00E9;t&#x00E9; coop&#x00E9;rative de transformation</option>
    <option value="SCOP"    >SCOP&nbsp;&nbsp; - Soci&#x00E9;t&#x00E9; coop&#x00E9;rative ouvri&#x00E8;re de production</option>
    <option value="SELAFA"  >SELAFA - Soci&#x00E9;t&#x00E9; d&#x0092;exercice lib&#x00E9;ral &#x00E0; forme anonyme</option>
    <option value="SLRL"    >SLRL&nbsp;&nbsp; - Soci&#x00E9;t&#x00E9; d&#x0092;exercice lib&#x00E9;ral &#x00E0; responsabilit&#x00E9; limit&#x00E9;e</option>
    <option value="SELARL"  >SELARL - Soci&#x00E9;t&#x00E9; d&#x0092;exercice lib&#x00E9;ral &#x00E0; responsabilit&#x00E9; limit&#x00E9;e</option>
    <option value="SEL"     >SEL&nbsp;&nbsp;&nbsp; - Soci&#x00E9;t&#x00E9; d&#x0092;exercice lib&#x00E9;ral</option>
    <option value="SICA"    >SICA&nbsp;&nbsp; - Soci&#x00E9;t&#x00E9; dint&#x00E9;r&#x00EA;t collectif agricole</option>
    <option value="SICAV"   >SICAV&nbsp; - Soci&#x00E9;t&#x00E9; d&#x0092;investissement &#x00E0; capital variable</option>
    <option value="SCR"     >SCR&nbsp;&nbsp;&nbsp; - Soci&#x00E9;t&#x00E9; de capital risque</option>
    <option value="SDR"     >SDR&nbsp;&nbsp;&nbsp; - Soci&#x00E9;t&#x00E9; de d&#x00E9;veloppement r&#x00E9;gional</option>
    <option value="SF"      >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; - Soci&#x00E9;t&#x00E9; de fait</option>
    <option value="SCA"     >SCA&nbsp;&nbsp;&nbsp; - Soci&#x00E9;t&#x00E9; en commandite par actions</option>
    <option value="SCS"     >SCS&nbsp;&nbsp;&nbsp; - Soci&#x00E9;t&#x00E9; en commandite simple</option>
    <option value="SNC"     >SNC&nbsp;&nbsp;&nbsp; - Soci&#x00E9;t&#x00E9; en nom collectif</option>
    <option value="SEP"     >SEP&nbsp;&nbsp;&nbsp; - Soci&#x00E9;t&#x00E9; en participation</option>
    <option value="SFI"     >SFI&nbsp;&nbsp;&nbsp; - Soci&#x00E9;t&#x00E9; financi&#x00E8;re d&#x0092;innovation</option>
    <option value="SII"     >SII&nbsp;&nbsp;&nbsp; - Soci&#x00E9;t&#x00E9; immobili&#x00E8;re d&#x0092;investissement</option>
    <option value="SICOMI"  >SICOMI - Soci&#x00E9;t&#x00E9; immobili&#x00E8;re pour le commerce et l&#x0092;industrie</option>
    <option value="SASU"    >SASU&nbsp;&nbsp; - Soci&#x00E9;t&#x00E9; par actions simplifi&#x00E9;e unipersonnelle</option>

 */
    }    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        panel1 = new JPanel();
        jLabel1 = new JLabel();
        jLabel10 = new JLabel();
        jLabel7 = new JLabel();
        jLabel8 = new JLabel();
        jLabel9 = new JLabel();
        jLabel2 = new JLabel();
        field_UStrIDFremd = new JTextField();
        jLabelErgebnisUStrID = new JLabel();
        jLabel4 = new JLabel();
        field_Firma = new JTextField();
        jLabelErgebnisFirma = new JLabel();
        jLabel6 = new JLabel();
        field_Rechtsform = new JComboBox<>();
        jLabel5 = new JLabel();
        field_Ort = new JTextField();
        jLabelErgebnisOrt = new JLabel();
        jLabel3 = new JLabel();
        field_PLZ = new JTextField();
        jLabelErgebnisPLZ = new JLabel();
        jLabel15 = new JLabel();
        field_Strasse = new JTextField();
        jLabelErgebnisStrasse = new JLabel();
        field_Druck = new JCheckBox();
        jLabelErgebnisAb = new JLabel();
        jLabelErgebnisBis = new JLabel();
        jButtonPruefen = new JButton();
        jButtonDrucken = new JButton();
        jButtonSchliessen = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Carola Hartmann Miles Verlag");
        setResizable(false);
        setFont(this.getFont().deriveFont(this.getFont().getStyle() | Font.BOLD));
        Container contentPane = getContentPane();

        //======== panel1 ========
        {
            panel1.setLayout(new MigLayout(
                "insets 0,hidemode 3,gap 5 5",
                // columns
                "[fill]" +
                "[fill]" +
                "[fill]" +
                "[fill]" +
                "[fill]" +
                "[fill]",
                // rows
                "[fill]" +
                "[fill]" +
                "[fill]" +
                "[fill]" +
                "[fill]" +
                "[fill]" +
                "[fill]" +
                "[fill]" +
                "[fill]" +
                "[fill]" +
                "[]" +
                "[]" +
                "[]"));

            //---- jLabel1 ----
            jLabel1.setFont(new Font("Tahoma", Font.BOLD, 12));
            jLabel1.setText("Pr\u00fcfen einer Umsatzsteuer-ID");
            panel1.add(jLabel1, "cell 0 0 3 1");

            //---- jLabel10 ----
            jLabel10.setFont(new Font("Tahoma", Font.BOLD, 11));
            jLabel10.setText("Daten");
            panel1.add(jLabel10, "cell 0 2");

            //---- jLabel7 ----
            jLabel7.setFont(new Font("Tahoma", Font.BOLD, 11));
            jLabel7.setText("Pr\u00fcfergebnis");
            panel1.add(jLabel7, "cell 3 2");

            //---- jLabel8 ----
            jLabel8.setText("Datum");
            panel1.add(jLabel8, "cell 4 2");

            //---- jLabel9 ----
            jLabel9.setText("Uhrzeit");
            panel1.add(jLabel9, "cell 5 2");

            //---- jLabel2 ----
            jLabel2.setText("UStrID");
            panel1.add(jLabel2, "cell 0 3");
            panel1.add(field_UStrIDFremd, "cell 1 3 2 1");

            //---- jLabelErgebnisUStrID ----
            jLabelErgebnisUStrID.setText("Ergebnis:");
            panel1.add(jLabelErgebnisUStrID, "cell 3 3");

            //---- jLabel4 ----
            jLabel4.setText("Firma");
            panel1.add(jLabel4, "cell 0 4");
            panel1.add(field_Firma, "cell 1 4 2 1");

            //---- jLabelErgebnisFirma ----
            jLabelErgebnisFirma.setText("Ergebnis:");
            panel1.add(jLabelErgebnisFirma, "cell 3 4");

            //---- jLabel6 ----
            jLabel6.setText("Rechtsform");
            panel1.add(jLabel6, "cell 0 5");

            //---- field_Rechtsform ----
            field_Rechtsform.setModel(new DefaultComboBoxModel<>(new String[] {

            }));
            field_Rechtsform.addActionListener(e -> field_RechtsformActionPerformed(e));
            panel1.add(field_Rechtsform, "cell 1 5 2 1");

            //---- jLabel5 ----
            jLabel5.setText("Ort");
            panel1.add(jLabel5, "cell 0 6");
            panel1.add(field_Ort, "cell 1 6 2 1");

            //---- jLabelErgebnisOrt ----
            jLabelErgebnisOrt.setText("Ergebnis:");
            panel1.add(jLabelErgebnisOrt, "cell 3 6");

            //---- jLabel3 ----
            jLabel3.setText("PLZ");
            panel1.add(jLabel3, "cell 0 7");
            panel1.add(field_PLZ, "cell 1 7 2 1");

            //---- jLabelErgebnisPLZ ----
            jLabelErgebnisPLZ.setText("Ergebnis:");
            panel1.add(jLabelErgebnisPLZ, "cell 3 7");

            //---- jLabel15 ----
            jLabel15.setText("Strasse");
            panel1.add(jLabel15, "cell 0 8");
            panel1.add(field_Strasse, "cell 1 8 2 1");

            //---- jLabelErgebnisStrasse ----
            jLabelErgebnisStrasse.setText("Ergebnis:");
            panel1.add(jLabelErgebnisStrasse, "cell 3 8");

            //---- field_Druck ----
            field_Druck.setText("Best\u00e4tigung anfordern");
            panel1.add(field_Druck, "cell 0 9 2 1");

            //---- jLabelErgebnisAb ----
            jLabelErgebnisAb.setText("g\u00fcltig ab:");
            panel1.add(jLabelErgebnisAb, "cell 3 9");

            //---- jLabelErgebnisBis ----
            jLabelErgebnisBis.setText("g\u00fcltig bis:");
            panel1.add(jLabelErgebnisBis, "cell 5 9");

            //---- jButtonPruefen ----
            jButtonPruefen.setText("Pr\u00fcfen");
            jButtonPruefen.addActionListener(e -> jButtonPruefenActionPerformed(e));
            panel1.add(jButtonPruefen, "cell 3 11");

            //---- jButtonDrucken ----
            jButtonDrucken.setText("Drucken");
            jButtonDrucken.addActionListener(e -> jButtonDruckenActionPerformed(e));
            panel1.add(jButtonDrucken, "cell 4 11");

            //---- jButtonSchliessen ----
            jButtonSchliessen.setText("Schlie\u00dfen");
            jButtonSchliessen.addActionListener(e -> jButtonSchliessenActionPerformed(e));
            panel1.add(jButtonSchliessen, "cell 5 11");
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(panel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(9, Short.MAX_VALUE))
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(panel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(2, Short.MAX_VALUE))
        );
        setSize(490, 360);
        setLocationRelativeTo(getOwner());
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonSchliessenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSchliessenActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_jButtonSchliessenActionPerformed
   
    private String GetCode(int Errorcode) {
        String Code = "";
        switch (Errorcode) {
                case 200 : Code = "Die angefragte UStrIDNr ist gültig"; break;
                case 201 : Code = "Die angefragte UStrIDNr ist ungültig"; break;
                case 202 : Code = "Die angefragte USt-IdNr. ist ungültig. Sie ist nicht in der Unternehmerdatei des betreffenden EU-Mitgliedstaates registriert.\n" +
"Hinweis:\n" +
"Ihr Geschäftspartner kann seine gültige USt-IdNr. bei der für ihn zuständigen Finanzbehörde in Erfahrung bringen. Möglicherweise muss er einen Antrag stellen, damit seine USt-IdNr. in die Datenbank aufgenommen wird. "; break;
                case 203 : Code = "Die angefragte UStrIDNr ist ungültig. Sier ist erst gültig ab - siehe Feld Gueltig ab"; break;
                case 204 : Code = "Die angefragte UStrIDNr ist ungültig Sie war gültig im Zeittraum - siehe gueltig von/bis"; break;
                case 205 : Code = "Ihre Anfrage kann derzeit durch den angefragten EU-Mitgliedstaat oder aus anderen Gründen nicht beantwortet werden. Bitte versuchen Sie es später noch einmal. Bei wiederholten Problemen wenden Sie sich bitte an das Bundeszentralamt für Steuern - Dienstsitz Saarlouis. "; break;
                case 206 : Code = "Ihre deutsche USt-IdNr. ist ungültig. Eine Bestätigungsanfrage ist daher nicht möglich. Den Grund hierfür können Sie beim Bundeszentralamt für Steuern - Dienstsitz Saarlouis - erfragen. "; break;
                case 207 : Code = "Ihnen wurde die deutsche USt-IdNr. ausschliesslich zu Zwecken der Besteuerung des innergemeinschaftlichen Erwerbs erteilt. Sie sind somit nicht berechtigt, Bestätigungsanfragen zu stellen. "; break;
                case 208 : Code = "Für die von Ihnen angefragte USt-IdNr. läuft gerade eine Anfrage von einem anderen Nutzer. Eine Bearbeitung ist daher nicht möglich. Bitte versuchen Sie es später noch einmal. "; break;
                case 209 : Code = "Die angefragte USt-IdNr. ist ungültig. Sie entspricht nicht dem Aufbau der für diesen EU-Mitgliedstaat gilt. ( Aufbau der USt-IdNr. aller EU-Länder) "; break;
                case 211 : Code = "Die angefragte USt-IdNr. ist ungültig. Sie enthält unzulässige Zeichen (wie z.B. Leerzeichen oder Punkt oder Bindestrich usw.). "; break;
                case 210 : Code = "Die angefragte USt-IdNr. ist ungültig. Sie entspricht nicht den Prüfziffernregeln die für diesen EU-Mitgliedstaat gelten. "; break;
                case 212 : Code = "Die angefragte USt-IdNr. ist ungültig. Sie enthält ein unzulässiges Länderkennzeichen. "; break;
                case 213 : Code = "Die Abfrage einer deutschen USt-IdNr. ist nicht möglich. "; break;
                case 214 : Code = " 	Ihre deutsche USt-IdNr. ist fehlerhaft. Sie beginnt mit 'DE' gefolgt von 9 Ziffern."; break;
                case 215 : Code = "Ihre Anfrage enthält nicht alle notwendigen Angaben für eine einfache Bestätigungsanfrage (Ihre deutsche USt-IdNr. und die ausl. USt-IdNr.).\n" +
"Ihre Anfrage kann deshalb nicht bearbeitet werden. "; break;
                case 216 : Code = "Ihre Anfrage enthält nicht alle notwendigen Angaben für eine qualifizierte Bestätigungsanfrage (Ihre deutsche USt-IdNr., die ausl. USt-IdNr., Firmenname einschl. Rechtsform und Ort).\n" +
"Es wurde eine einfache Bestätigungsanfrage durchgeführt mit folgenden Ergebnis:\n" +
"Die angefragte USt-IdNr. ist gültig. "; break;
                case 217 : Code = "Bei der Verarbeitung der Daten aus dem angefragten EU-Mitgliedstaat ist ein Fehler aufgetreten. Ihre Anfrage kann deshalb nicht bearbeitet werden. "; break;
                case 218 : Code = "Eine qualifizierte Bestätigung ist zur Zeit nicht möglich. Es wurde eine einfache Bestätigungsanfrage mit folgendem Ergebnis durchgeführt:\n" +
"Die angefragte USt-IdNr. ist gültig. "; break;
                case 219 : Code = "Bei der Durchführung der qualifizierten Bestätigungsanfrage ist ein Fehler aufgetreten. Es wurde eine einfache Bestätigungsanfrage mit folgendem Ergebnis durchgeführt:\n" +
"Die angefragte USt-IdNr. ist gültig. "; break;
                case 220 : Code = "Bei der Anforderung der amtlichen Bestätigungsmitteilung ist ein Fehler aufgetreten. Sie werden kein Schreiben erhalten. "; break;
                case 221 : Code = "Die Anfragedaten enthalten nicht alle notwendigen Parameter oder einen ungültigen Datentyp. Weitere Informationen erhalten Sie bei den Hinweisen zum Schnittstelle - Aufruf. "; break;
                case 222 : Code = "Die angefragte USt-IdNr. ist gültig. Bitte beachten Sie die Umstellung auf ausschließlich HTTPS (TLS 1.2) zum 07.01.2019."; break;
                case 999 : Code = "Eine Bearbeitung ist zur Zeit nicht möglich"; break;
            }

        return Code;
    }
    
    private String NormiereName(String Parameter) {
        String Input = Parameter;
        Input = Input.replace("%","%25");
        Input = Input.replace(" ","%20");
        Input = Input.replace("-","%2D");
        Input = Input.replace("!","%21");
        Input = Input.replace("\"","%22");
        Input = Input.replace("#","%23");
        Input = Input.replace("$","%24");
        Input = Input.replace("&","%26");
        Input = Input.replace("'","%27");
        Input = Input.replace("(","%28");
        Input = Input.replace(")","%29");
        Input = Input.replace("*","%2A");
        Input = Input.replace("+","%2B");
        Input = Input.replace(",","%2C");
        Input = Input.replace(".","%2E");
        Input = Input.replace("/","%2F");
        Input = Input.replace(":","%3A");
        Input = Input.replace(";","%3B");
        Input = Input.replace("<","%3C");
        Input = Input.replace("=","%3D");
        Input = Input.replace(">","%3E");
        Input = Input.replace("?","%3F");
        Input = Input.replace("@","%40");
        Input = Input.replace("[","%5B");
        Input = Input.replace("]","%5D");
                
        return Input;
    } 
    
    private String NormiereErgebnis(String Parameter, int Grenze) {
        String Output = "";
        int i = 0;
        int index = 0;
        while (i <= Grenze) {
            if (ErgebnisFeld[i].equals(Parameter)) {
                index = i;
                i = 1000;
            }
        }
        if (index != 0) {
            switch (ErgebnisFeld[index]) {
                case "A": Output = "stimmt überein"; break;
                case "B": Output = "stimmt nicht überein"; break;
                case "C": Output = "nicht angefragt"; break;
                case "D": Output = "vom EU-Mitgliedstaat nicht mitgeteilt"; break;
            }    
        }        
        
        return Output;
    }
    
    private void jButtonPruefenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPruefenActionPerformed
        // TODO add your handling code here:
        Rechtsform = field_Rechtsform.getItemAt(field_Rechtsform.getSelectedIndex()).split(";");
        Firmenname = NormiereName(field_Firma.getText());
                
        System.out.println("Firmenname normiert " + Firmenname);
        String ServerURL = "https://evatr.bff-online.de/evatrRPC?"
                            + "UstId_1=DE269369280"
                            + "&UstId_2=" + field_UStrIDFremd.getText()
                            + "&Firmenname=" + Firmenname
                            + "&Ort=" + field_Ort.getText()
                            + "&PLZ=" + field_PLZ.getText()
                            + "&Strasse=" + field_Strasse.getText();
        
        if (field_Druck.isSelected()) {
            ServerURL = ServerURL + "&Druck=ja";
        } else {
            ServerURL = ServerURL + "&Druck=nein";
        }
        String USER_AGENT = "Mozilla/5.0";
        
        // FR 33709801872
        
        try {
            System.out.println("Sende GET an " + ServerURL);
            URL obj = new URL(ServerURL);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);
            int responseCode = con.getResponseCode();
            Einfach = GetCode(responseCode);
            jLabelErgebnisUStrID.setText(Einfach);

            System.out.println(Code);
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String InputLine;
            while((InputLine = in.readLine())!=null) {
                response.append(InputLine);
                //System.out.println(InputLine);
            }
            in.close();
            Ergebnis = response.toString();
            Ergebnis = Ergebnis.replace("<params>","#");
            Ergebnis = Ergebnis.replace("</params>","#");
            Ergebnis = Ergebnis.replace("<param>","#");
            Ergebnis = Ergebnis.replace("</param>","#");
            Ergebnis = Ergebnis.replace("<value>","#");
            Ergebnis = Ergebnis.replace("<array>","#");
            Ergebnis = Ergebnis.replace("<data>","#");
            Ergebnis = Ergebnis.replace("<string>","#");
            Ergebnis = Ergebnis.replace("</value>","#");
            Ergebnis = Ergebnis.replace("</array>","#");
            Ergebnis = Ergebnis.replace("</data>","#");
            Ergebnis = Ergebnis.replace("</string>","#");
            Ergebnis = Ergebnis.replace("############"," ");
            Ergebnis = Ergebnis.replace("#######","");
            Ergebnis = Ergebnis.replace("####"," ");
            /**
             Ergebnis = Ergebnis.replace("  "," ");
            */
            
            ErgebnisFeld = Ergebnis.split(" ");
            
            Qualifiziert = GetCode(Integer.parseInt(ErgebnisFeld[3]));
            
            jLabelErgebnisStrasse.setText(NormiereErgebnis("Erg_Str", ErgebnisFeld.length));
            jLabelErgebnisFirma.setText(NormiereErgebnis("Erg_Name", ErgebnisFeld.length));
            jLabelErgebnisOrt.setText(NormiereErgebnis("Erg_Ort", ErgebnisFeld.length));
            jLabelErgebnisPLZ.setText(NormiereErgebnis("Erg_PLZ", ErgebnisFeld.length));
            
            System.out.println(response.toString());
            System.out.println(Ergebnis);
            
            int faktor = 0;
            while ( faktor < ErgebnisFeld.length -1) {
                System.out.println(ErgebnisFeld[faktor] + " " + ErgebnisFeld[faktor + 1]);
                faktor = faktor + 2;
            }
            
            /**
             * Als Rückgabe wird ein Hash mit folgenden Parametern ausgegeben:
                0   UstId_1     Ihre deutsche USt-IdNr.
                2   ErrorCode   Fehlernummer der Anfrage ( Übersicht der ErrorCodes).
                4   UstId_2     Angefragte ausländische USt-IdNr.
                6   Druck       Gibt an, ob Sie eine amtliche Bestätigungsmitteilung angefordert haben:
                8   Erg_PLZ     Ergebnis für die angefragte Postleitzahl der Firma
                10  Ort         Der von Ihnen angefragte Ort der Firma
                12  Datum       Das Datum der Anfrage (Format: tt.mm.jjjj).
                14  PLZ         Die von Ihnen angefragte Postleitzahl der Firma
                16  Erg_Ort     Ergebnis für den angefragten Ort der Firma
                18  Uhrzeit     Uhrzeit der Anfrage (Format: hh:mm:ss).
                20  Erg_Name    Ergebnis für den angefragten Namen der Firma
                22  Gueltig_ab  Wird nur beim ErrorCode 203 bzw. 204 angegeben.
                24  Gueltig_bis Wird nur beim ErrorCode 204 angegeben.
                26  Strasse     Die von Ihnen angefragte Strasse der Firma
                28  Firmenname  Der von Ihnen angefragte Firmenname
                30  Erg_Str     Ergebnis für die angefragte Strasse der Firma

                Werte für die Parameter Erg_Name, Erg_Ort, Erg_PLZ und Erg_Str
                    A = stimmt überein
                    B = stimmt nicht überein
                    C = nicht angefragt
                    D = vom EU-Mitgliedsstaat nicht mitgeteilt
              
                    ja     = Mitteilung angefordert
                    nein = ohne Miteilung
             */
            
        } catch (MalformedURLException ex) {
            Modulhelferlein.Fehlermeldung("UStrID prüfen", "MalformedURL-Exception", ex.getMessage());
        } catch (IOException ex) {
            Modulhelferlein.Fehlermeldung("UStrID prüfen", "http IO-Exception", ex.getMessage());
        }
    
    
    }//GEN-LAST:event_jButtonPruefenActionPerformed

    private void jButtonDruckenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDruckenActionPerformed
        // TODO add your handling code here:
        // erzeugt eine PDF-Datei mit dem Druckergebnis
        try {                                               
            // TODO add your handling code here:
            String outputFileName = Modulhelferlein.pathBerichte + "\\" + field_UStrIDFremd.getText() + ".pdf";
            
            // Create a document and add a page to it
            PDDocument document = new PDDocument();
            PDPage page = new PDPage(A4);
            document.addPage(page);
            
            // Create a new font object selecting one of the PDF base fonts
            PDFont fontPlain = PDType1Font.HELVETICA;
            PDFont fontBold = PDType1Font.HELVETICA_BOLD;
            PDFont fontItalic = PDType1Font.HELVETICA_OBLIQUE;
            //        PDFont fontMono = PDType1Font.COURIER;
            
             
            // Start a new content stream which will "hold" the to be created content
            PDPageContentStream cos = new PDPageContentStream(document, page);

            Ausgabe(cos, fontBold, 14, Color.BLACK,  55, 750, "Abfrage Umsatzsteuer-ID");
            Ausgabe(cos, fontBold, 14, Color.BLACK, 400, 750, field_UStrIDFremd.getText());
            
            Ausgabe(cos, fontBold, 14, Color.BLACK,  55, 715, "Datum");
            Ausgabe(cos, fontBold, 14, Color.BLACK, 155, 715, ErgebnisFeld[13]);
            
            Ausgabe(cos, fontBold, 14, Color.BLACK,  55, 700, "Uhrzeit");
            Ausgabe(cos, fontBold, 14, Color.BLACK, 155, 700, ErgebnisFeld[19]);
            
            Ausgabe(cos, fontBold, 14, Color.BLACK,  55, 680, "Ergebnis");
            Ausgabe(cos, fontBold, 14, Color.BLACK, 155, 680, Einfach);
            
            Ausgabe(cos, fontBold, 14, Color.BLACK,  55, 650, "Firmenname");
            Ausgabe(cos, fontBold, 14, Color.BLACK, 155, 650, field_Firma.getText());
            Ausgabe(cos, fontBold, 14, Color.BLACK,  55, 635, "Rechtsform");
            Ausgabe(cos, fontBold, 14, Color.BLACK, 155, 635, Rechtsform[0]);
            Ausgabe(cos, fontBold, 14, Color.BLACK, 155, 615, "Ergebnis " + ErgebnisFeld[21]);
            Ausgabe(cos, fontBold, 14, Color.BLACK, 250, 615, "=> " + jLabelErgebnisFirma.getText());

            Ausgabe(cos, fontBold, 14, Color.BLACK,  55, 590, "Strasse");
            Ausgabe(cos, fontBold, 14, Color.BLACK, 155, 590, field_Strasse.getText());
            Ausgabe(cos, fontBold, 14, Color.BLACK, 155, 575, "Ergebnis " + ErgebnisFeld[31]);
            Ausgabe(cos, fontBold, 14, Color.BLACK, 250, 575, "=> " + jLabelErgebnisStrasse.getText());

            Ausgabe(cos, fontBold, 14, Color.BLACK,  55, 555, "Ort");
            Ausgabe(cos, fontBold, 14, Color.BLACK, 155, 555, field_Ort.getText());
            Ausgabe(cos, fontBold, 14, Color.BLACK, 155, 540, "Ergebnis " + ErgebnisFeld[17]);
            Ausgabe(cos, fontBold, 14, Color.BLACK, 250, 540, "=> " + jLabelErgebnisOrt.getText());

            Ausgabe(cos, fontBold, 14, Color.BLACK,  55, 520, "Postleitzahl");
            Ausgabe(cos, fontBold, 14, Color.BLACK, 155, 520, field_PLZ.getText());
            Ausgabe(cos, fontBold, 14, Color.BLACK, 155, 505, "Ergebnis " + ErgebnisFeld[9]);
            Ausgabe(cos, fontBold, 14, Color.BLACK, 250, 505, "=> " + jLabelErgebnisPLZ.getText());

            Ausgabe(cos, fontBold, 14, Color.BLACK,  55, 470, "Ergebnis");
            Ausgabe(cos, fontBold, 14, Color.BLACK, 155, 470, Qualifiziert);

            Ausgabe(cos, fontBold, 14, Color.BLACK,  55, 400, "Bestätigung angefordert");
            Ausgabe(cos, fontBold, 14, Color.BLACK, 255, 400, ErgebnisFeld[7]);
            
            // Make sure that the content stream is closed:
            cos.close();
            
            // Save the results and ensure that the document is properly closed:
            document.save(outputFileName);
            document.close();
            
            try {
                Runtime.getRuntime().exec("cmd.exe /c " + "\"" + outputFileName + "\"");
            } catch (IOException exept) {
                Modulhelferlein.Fehlermeldung("Ausgabe Abfrage UStrID: Exception: " + exept.getMessage());
            }// try Brief ausgeben
        } catch (IOException ex) {
            Modulhelferlein.Fehlermeldung("Ausgabe Abfrage UStrID: IO-Exception: " + ex.getMessage());
            //Logger.getLogger(_DlgAdresseDrucken.class.getName()).log(Level.SEVERE, null, ex);
        }// try Brief ausgeben
    }//GEN-LAST:event_jButtonDruckenActionPerformed

    private void field_RechtsformActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_field_RechtsformActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_field_RechtsformActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(_DlgPruefenUstrID.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(_DlgPruefenUstrID.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(_DlgPruefenUstrID.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(_DlgPruefenUstrID.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(() -> {
            _DlgPruefenUstrID dialog = new _DlgPruefenUstrID(new javax.swing.JFrame(), true);
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    dialog.setVisible(false);
                }
            });
            dialog.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JPanel panel1;
    private JLabel jLabel1;
    private JLabel jLabel10;
    private JLabel jLabel7;
    private JLabel jLabel8;
    private JLabel jLabel9;
    private JLabel jLabel2;
    private JTextField field_UStrIDFremd;
    private JLabel jLabelErgebnisUStrID;
    private JLabel jLabel4;
    private JTextField field_Firma;
    private JLabel jLabelErgebnisFirma;
    private JLabel jLabel6;
    private JComboBox<String> field_Rechtsform;
    private JLabel jLabel5;
    private JTextField field_Ort;
    private JLabel jLabelErgebnisOrt;
    private JLabel jLabel3;
    private JTextField field_PLZ;
    private JLabel jLabelErgebnisPLZ;
    private JLabel jLabel15;
    private JTextField field_Strasse;
    private JLabel jLabelErgebnisStrasse;
    private JCheckBox field_Druck;
    private JLabel jLabelErgebnisAb;
    private JLabel jLabelErgebnisBis;
    private JButton jButtonPruefen;
    private JButton jButtonDrucken;
    private JButton jButtonSchliessen;
    // End of variables declaration//GEN-END:variables

    String Code = "";
    String Einfach = "";
    String Qualifiziert = "";
    StringBuffer response = new StringBuffer();
    String Ergebnis = "";
    String[] ErgebnisFeld;
    String[] Rechtsform;
    String Firmenname = "";    
}
