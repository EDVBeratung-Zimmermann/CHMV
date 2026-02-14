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
import javax.swing.*;
import javax.swing.GroupLayout;

/**
 *
 * @author EDV-Beratung Zimmermann
 */
public class _DlgChangeLog extends javax.swing.JDialog {

    /**
     * Creates new form _DlgChangeLog
     *
     * @param parent
     * @param modal
     */
    public _DlgChangeLog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        String changeLog = 
                "20260214 Anpassen Honorarabrechung\n" +
                "         - robustes Lesen BoD-Sales.CSV\n"+
                "         Anpassen Log-Meldungen\n"+
                "\n" +
                "Bug-Fix\n" + 
                " * Bericht Einnahmen - UStr bei ausl. Einnahmen\n"+ 
                "Feature\n" + 
                " * Anpassen Adresse - Aktualisieren der Stra\u00dfe\n" + 
                "Feature\n" + 
                " * Umsatzsteueranpassung auf Grund Corona einpflegen\n" + 
                "      - Rechnung/Mahnung\n" + 
                "      - Umsatz\u00fcbersicht Ausgaben  \n\n" + 
                "Feature\n" + 
                "  * Flyer erstellen  \n" + 
                "Bug-Fix\n" + 
                "  * Honorarabrechnung  \n" + 
                "Feature\n" + 
                "  * Mail aus dem Bestelldialog heraus versenden  \n" + 
                "Feature\n" + 
                "   * Benutzerverwaltung   \n" + 
                "Feature\n" + 
                "  * PDF-Ausgabe/E-Rechnung/Hybrid-PDF    \n" + 
                "Feature\n" + 
                "  * Sichern und Bereinigen der Datenbank zum Jahresabschluss/-wechsel    \n" + 
                "Feature\n" + 
                "  * Bericht zu den verkauften B\u00fcchern als Excel-Datei    \n" + 
                "Feature\n" + 
                "  * Pr\u00fcfen Umsatzsteuer-ID und Ausgabe des Ergebnisses    \n" + 
                "Feature\n" + 
                "  * Freiexemplar als Option bei Rezensionen    \n" + 
                "Feature\n" + 
                "  * Buchrechnung um Freitext-Zeilen erg\u00e4nzen    \n" + 
                "Feature\n" + 
                "  * Adressetikettendruck aus dem Adressen-Dialog    \n" + 
                "Feature\n" + 
                "  * Rechnungsbetrag Fett    \n" + 
                "Feature\n" + 
                "  * Dialog Adresse erg\u00e4nzt um Auswahl-Liste    \n" + 
                "Bug-Fix\n" + 
                "  * Ausgabe der Beschreibung bei Rezensionen\n" + 
                "  * Richtiger Autorentitel bei mehreren Rezensionen    \n" + 
                "Bug-Fix\n" + 
                "  * Adressaufkleber Adresse 7,8 neu gesetzt\n" + 
                "  * Adressaufkleber Adresse 9, 10 neu gesetzt    \n" + 
                "Feature\n" + 
                "  * Einf\u00fcgen eines Feldes \"BESTELLUNG_STORNIERT\"\n" + 
                "    - Anpassen der Rechnung f\u00fcr den Fall \"STORNIERT\"\n" + 
                "    - Anpassen der Bestellung f\u00fcr den Fall \"STORNIERT\"\n" + 
                "    - Anpassen Berichte Einnahmen, Ums\u00e4tze      \n" + 
                "Feature\n" + 
                "  * Etikettendruck f\u00fcr Avery Zweckform     \n" + 
                "Bug-Fix\n" + 
                "  * Brief Rezension\n" + 
                "     - Zeilenl\u00e4nge 1. Satz;\n" + 
                "     - Zeilenumbruch 2. Satz, Leerzeichen\n" + 
                "Bug-Fix\n" + 
                "  * Umbenennen DOC zu DOCX\n" + 
                "  * Brief Rezension: Herausgeber/mehrere Autoren ber\u00fccksichtigen\n" + 
                "Bug-Fix\n" + 
                "  * Bericht Einnahmen: Abfrage der Tabelle bzgl. Einnahmearten\n" + 
                "  * Bericht Ums\u00e4tze: Abfrage der Tabelle bzgl. Einnahmearten    \n" + 
                "Feature\n" + 
                "  * Brief Rezensions-, Pflicht- und Belegexemplar um Text-Feld erg\u00e4nzen      \n" + 
                "Feature\n" + 
                "  * Info- und Fehlermeldung ein-, zwei- und dreizeilig erm\u00f6glichen    \n" + 
                "Feature\n" + 
                "  * Belegexemplar als Bestell-Typ    \n" + 
                "Bug-Fix\n" + 
                "  * Liste der B\u00fccher mit f\u00fchrenden Nullen bei ID, Courier New wg. Lesbarkeit";
        jTextArea1.setText(changeLog);
        jTextArea1.setFont(new Font("Courier", Font.PLAIN, 12));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPanel1 = new JPanel();
        panel1 = new JPanel();
        jButtonSchliessen = new JButton();
        jLabel1 = new JLabel();
        jScrollPane1 = new JScrollPane();
        jTextArea1 = new JTextArea();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Carola Hartmann Miles-Verlag");
        setResizable(false);
        var contentPane = getContentPane();

        //======== jPanel1 ========
        {

            GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
            jPanel1.setLayout(jPanel1Layout);
            jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
            );
            jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup()
                    .addGap(0, 203, Short.MAX_VALUE)
            );
        }

        //======== panel1 ========
        {

            //---- jButtonSchliessen ----
            jButtonSchliessen.setText("Schlie\u00dfen");
            jButtonSchliessen.addActionListener(e -> jButtonSchliessenActionPerformed(e));

            //---- jLabel1 ----
            jLabel1.setFont(new Font("Tahoma", Font.BOLD, 12));
            jLabel1.setText("Change-Log");

            //======== jScrollPane1 ========
            {

                //---- jTextArea1 ----
                jTextArea1.setEditable(false);
                jTextArea1.setColumns(20);
                jTextArea1.setLineWrap(true);
                jTextArea1.setRows(5);
                jTextArea1.setWrapStyleWord(true);
                jTextArea1.setFocusable(false);
                jScrollPane1.setViewportView(jTextArea1);
            }

            GroupLayout panel1Layout = new GroupLayout(panel1);
            panel1.setLayout(panel1Layout);
            panel1Layout.setHorizontalGroup(
                panel1Layout.createParallelGroup()
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panel1Layout.createParallelGroup()
                            .addComponent(jLabel1)
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGap(322, 322, 322)
                                .addComponent(jButtonSchliessen))
                            .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 684, GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(16, Short.MAX_VALUE))
            );
            panel1Layout.setVerticalGroup(
                panel1Layout.createParallelGroup()
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 203, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButtonSchliessen)
                        .addContainerGap(17, Short.MAX_VALUE))
            );
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addComponent(panel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(50, 50, 50)
                    .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                    .addContainerGap(45, Short.MAX_VALUE)
                    .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(46, 46, 46))
                .addComponent(panel1, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pack();
        setLocationRelativeTo(getOwner());
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonSchliessenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSchliessenActionPerformed
        // TODO add your handling code here:
        dispose();
    }//GEN-LAST:event_jButtonSchliessenActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        //try {
        //    for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
        //        if ("Nimbus".equals(info.getName())) {
        //            javax.swing.UIManager.setLookAndFeel(info.getClassName());
        //            break;
        //        }
        //    }
        //} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
        //    java.util.logging.Logger.getLogger(_DlgChangeLog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        //}
        //</editor-fold>

        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(() -> {
            _DlgChangeLog dialog = new _DlgChangeLog(new javax.swing.JFrame(), true);
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
    private JPanel jPanel1;
    private JPanel panel1;
    private JButton jButtonSchliessen;
    private JLabel jLabel1;
    private JScrollPane jScrollPane1;
    private JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables

}
