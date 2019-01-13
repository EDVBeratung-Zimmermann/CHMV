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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.JOptionPane;
import javax.swing.plaf.ActionMapUIResource;

/**
 *
 * @author Thomas Zimmermann
 */
public class VerwaltenDatenbankBenutzer extends javax.swing.JDialog {

    /**
     * Creates new form VerwaltenDatenbankMailverteiler
     *
     * @param parent
     * @param modal
     */
    public VerwaltenDatenbankBenutzer(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        ActionMap actionMap = new ActionMapUIResource();
        actionMap.put("action_anfang", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AnfangActionPerformed(e);
                System.out.println("anfang action performed.");
            }
        });
        actionMap.put("action_ende", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EndeActionPerformed(e);
                System.out.println("ende action performed.");
            }
        });
        actionMap.put("action_vor", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                VorActionPerformed(e);
                System.out.println("vor action performed.");
            }
        });
        actionMap.put("action_zurueck", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ZurueckActionPerformed(e);
                System.out.println("zurueck action performed.");
            }
        });
        actionMap.put("action_insert", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EinfuegenActionPerformed(e);
                System.out.println("insert action performed.");
            }
        });
        actionMap.put("action_del", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LoeschenActionPerformed(e);
                System.out.println("del action performed.");
            }
        });
        actionMap.put("action_save", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UpdateActionPerformed(e);
                System.out.println("Save action performed.");
            }
        });
        actionMap.put("action_exit", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SchliessenActionPerformed(e);
                System.out.println("Exit action performed.");
            }
        });
        actionMap.put("action_print", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DruckenActionPerformed(e);
                System.out.println("print action performed.");
            }
        });

        InputMap keyMap = new ComponentInputMap(panel1);
        //keyMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.Event.CTRL_MASK), "action_anfang");
        //keyMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.Event.CTRL_MASK), "action_ende");

        keyMap.put(KeyStroke.getKeyStroke("control alt A"), "action_anfang");
        keyMap.put(KeyStroke.getKeyStroke("control alt E"), "action_ende");
        keyMap.put(KeyStroke.getKeyStroke("control alt V"), "action_vor");
        keyMap.put(KeyStroke.getKeyStroke("control alt Z"), "action_zurueck");
        keyMap.put(KeyStroke.getKeyStroke("control alt I"), "action_insert");
        keyMap.put(KeyStroke.getKeyStroke("control alt D"), "action_del");
        keyMap.put(KeyStroke.getKeyStroke("control alt S"), "action_save");
        keyMap.put(KeyStroke.getKeyStroke("control alt X"), "action_exit");
        keyMap.put(KeyStroke.getKeyStroke("control alt P"), "action_print");
        SwingUtilities.replaceUIActionMap(panel1, actionMap);
        SwingUtilities.replaceUIInputMap(panel1, JComponent.WHEN_IN_FOCUSED_WINDOW, keyMap);

        conn = null;

        // Datenbank-Treiber laden
        try {
            Class.forName(Modulhelferlein.dbDriver);
        } catch (ClassNotFoundException exept) {
            Modulhelferlein.Fehlermeldung(
                    "ClassNotFoundException: Treiber nicht gefunden. "
                    + exept.getMessage());
        }

        // Verbindung zur Datenbank über die JDBC-Brücke
        try {
            conn = DriverManager.getConnection(Modulhelferlein.dbUrl, Modulhelferlein.dbUser, Modulhelferlein.dbPassword);
            // final Connection conn2=conn;

            if (conn != null) {
                SQLAnfrage = null; // Anfrage erzeugen
                SQLAnfrageKonfiguration = null;

                try {
                    SQLAnfrageKonfiguration = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    resultKonfiguration = SQLAnfrageKonfiguration.executeQuery("SELECT * FROM tbl_konfiguration");

                    SQLAnfrage = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    result = SQLAnfrage.executeQuery("SELECT * FROM TBL_BENUTZER ORDER BY BENUTZER_ID DESC");
                    if (result.next()) {
                        maxID = 1 + result.getInt("BENUTZER_ID");
                    } else {
                        maxID = 0;
                    }

                    result = SQLAnfrage.executeQuery("SELECT * FROM TBL_BENUTZER");
                    // Anzahl der Datensätze ermitteln
                    countMax = 0;
                    count = 0;
                    field_count.setText(Integer.toString(count));
                    while (result.next()) {
                        ++countMax;
                    }
                    field_countMax.setText(Integer.toString(countMax));
                    // gehe zum ersten Datensatz - wenn nicht leer
                    if (result.first()) {
                        count = 1;
                        field_count.setText(Integer.toString(count));
                        resultIsEmpty = false;
                        field_ID.setText(Integer.toString(result.getInt("BENUTZER_ID")));
                        field_Beschreibung.setText(result.getString("BENUTZER_NAME"));
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
                        Drucken.setEnabled(true);
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
                        Drucken.setEnabled(false);
                        Schliessen.setEnabled(true);
                    }
                } catch (SQLException exept) {
                    Modulhelferlein.Fehlermeldung("SQL-Exception: SQL-Anfrage nicht moeglich. " + exept.getMessage());
                }
            }
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: Verbindung nicht moeglich. " + exept.getMessage());
        }

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
        jLabelTitel = new JLabel();
        jLabelID = new JLabel();
        field_ID = new JTextField();
        jLabel1 = new JLabel();
        field_count = new JTextField();
        jLabel2 = new JLabel();
        field_countMax = new JTextField();
        jLabel3 = new JLabel();
        field_Beschreibung = new JTextField();
        Anfang = new JButton();
        Zurueck = new JButton();
        Vor = new JButton();
        Ende = new JButton();
        Update = new JButton();
        Einfuegen = new JButton();
        Loeschen = new JButton();
        Suchen = new JButton();
        WSuchen = new JButton();
        Drucken = new JButton();
        Schliessen = new JButton();
        jLabel4 = new JLabel();
        field_Kennwort = new JTextField();
        jLabel5 = new JLabel();
        field_Rechte = new JComboBox<>();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Carola Hartmann Miles Verlag");
        setResizable(false);
        setFont(new Font(Font.DIALOG, Font.BOLD, 12));
        setMinimumSize(new Dimension(685, 220));
        var contentPane = getContentPane();

        //======== panel1 ========
        {
            panel1.setLayout(null);

            //---- jLabelTitel ----
            jLabelTitel.setFont(new Font("Tahoma", Font.BOLD, 12));
            jLabelTitel.setText("Verwaltung der Benutzer");
            panel1.add(jLabelTitel);
            jLabelTitel.setBounds(0, 0, 215, 20);

            //---- jLabelID ----
            jLabelID.setText("ID");
            jLabelID.setHorizontalAlignment(SwingConstants.RIGHT);
            panel1.add(jLabelID);
            jLabelID.setBounds(330, 0, 50, 20);

            //---- field_ID ----
            field_ID.setEditable(false);
            field_ID.setText("0000");
            field_ID.setEnabled(false);
            field_ID.setFocusable(false);
            field_ID.setMaximumSize(new Dimension(30, 20));
            field_ID.setMinimumSize(new Dimension(30, 20));
            panel1.add(field_ID);
            field_ID.setBounds(new Rectangle(new Point(385, 0), field_ID.getPreferredSize()));

            //---- jLabel1 ----
            jLabel1.setText("Datensatz Nr.");
            panel1.add(jLabel1);
            jLabel1.setBounds(440, 0, 105, 20);

            //---- field_count ----
            field_count.setEditable(false);
            field_count.setText("000");
            field_count.setEnabled(false);
            field_count.setFocusable(false);
            field_count.setMaximumSize(new Dimension(30, 20));
            field_count.setMinimumSize(new Dimension(30, 20));
            field_count.setPreferredSize(new Dimension(30, 20));
            panel1.add(field_count);
            field_count.setBounds(new Rectangle(new Point(550, 0), field_count.getPreferredSize()));

            //---- jLabel2 ----
            jLabel2.setText("von");
            jLabel2.setHorizontalAlignment(SwingConstants.CENTER);
            panel1.add(jLabel2);
            jLabel2.setBounds(580, 0, 43, 20);

            //---- field_countMax ----
            field_countMax.setEditable(false);
            field_countMax.setText("000");
            field_countMax.setEnabled(false);
            field_countMax.setFocusable(false);
            field_countMax.setMaximumSize(new Dimension(30, 20));
            field_countMax.setMinimumSize(new Dimension(30, 20));
            panel1.add(field_countMax);
            field_countMax.setBounds(628, 0, 30, field_countMax.getPreferredSize().height);

            //---- jLabel3 ----
            jLabel3.setText("Benutzer - Vorname Name");
            panel1.add(jLabel3);
            jLabel3.setBounds(0, 50, 175, jLabel3.getPreferredSize().height);

            //---- field_Beschreibung ----
            field_Beschreibung.setPreferredSize(new Dimension(645, 25));
            panel1.add(field_Beschreibung);
            field_Beschreibung.setBounds(0, 69, 245, field_Beschreibung.getPreferredSize().height);

            //---- Anfang ----
            Anfang.setText("<<");
            Anfang.setToolTipText("Gehe zum Anfang - erster Datensatz");
            Anfang.setMaximumSize(new Dimension(50, 25));
            Anfang.setMinimumSize(new Dimension(20, 25));
            Anfang.setPreferredSize(new Dimension(50, 25));
            Anfang.addActionListener(e -> AnfangActionPerformed(e));
            panel1.add(Anfang);
            Anfang.setBounds(new Rectangle(new Point(0, 124), Anfang.getPreferredSize()));

            //---- Zurueck ----
            Zurueck.setText("<");
            Zurueck.setToolTipText("Gehe zum vorherigen Datensatz");
            Zurueck.setMaximumSize(new Dimension(50, 25));
            Zurueck.setMinimumSize(new Dimension(50, 25));
            Zurueck.setPreferredSize(new Dimension(50, 25));
            Zurueck.addActionListener(e -> ZurueckActionPerformed(e));
            panel1.add(Zurueck);
            Zurueck.setBounds(new Rectangle(new Point(55, 124), Zurueck.getPreferredSize()));

            //---- Vor ----
            Vor.setText(">");
            Vor.setToolTipText("Gehe zum n\u00e4chsten Datensatz");
            Vor.setMaximumSize(new Dimension(50, 25));
            Vor.setMinimumSize(new Dimension(50, 25));
            Vor.setPreferredSize(new Dimension(50, 25));
            Vor.addActionListener(e -> VorActionPerformed(e));
            panel1.add(Vor);
            Vor.setBounds(new Rectangle(new Point(110, 124), Vor.getPreferredSize()));

            //---- Ende ----
            Ende.setText(">>");
            Ende.setToolTipText("Gehe zum Ende - zum letzten Datensatz");
            Ende.setMaximumSize(new Dimension(50, 25));
            Ende.setMinimumSize(new Dimension(50, 25));
            Ende.setPreferredSize(new Dimension(50, 25));
            Ende.addActionListener(e -> EndeActionPerformed(e));
            panel1.add(Ende);
            Ende.setBounds(new Rectangle(new Point(165, 124), Ende.getPreferredSize()));

            //---- Update ----
            Update.setText("!");
            Update.setToolTipText("Datensatz aktualisieren");
            Update.setMaximumSize(new Dimension(50, 25));
            Update.setMinimumSize(new Dimension(50, 25));
            Update.setPreferredSize(new Dimension(50, 25));
            Update.addActionListener(e -> UpdateActionPerformed(e));
            panel1.add(Update);
            Update.setBounds(new Rectangle(new Point(220, 124), Update.getPreferredSize()));

            //---- Einfuegen ----
            Einfuegen.setText("+");
            Einfuegen.setToolTipText("Datensatz einf\u00fcgen");
            Einfuegen.setMaximumSize(new Dimension(50, 25));
            Einfuegen.setMinimumSize(new Dimension(50, 25));
            Einfuegen.setPreferredSize(new Dimension(50, 25));
            Einfuegen.addActionListener(e -> EinfuegenActionPerformed(e));
            panel1.add(Einfuegen);
            Einfuegen.setBounds(new Rectangle(new Point(275, 124), Einfuegen.getPreferredSize()));

            //---- Loeschen ----
            Loeschen.setText("-");
            Loeschen.setToolTipText("Datensatz l\u00f6schen");
            Loeschen.setMaximumSize(new Dimension(50, 25));
            Loeschen.setMinimumSize(new Dimension(50, 25));
            Loeschen.setPreferredSize(new Dimension(50, 25));
            Loeschen.addActionListener(e -> LoeschenActionPerformed(e));
            panel1.add(Loeschen);
            Loeschen.setBounds(new Rectangle(new Point(330, 124), Loeschen.getPreferredSize()));

            //---- Suchen ----
            Suchen.setText("?");
            Suchen.setToolTipText("Datensatz suchen");
            Suchen.setMaximumSize(new Dimension(50, 25));
            Suchen.setMinimumSize(new Dimension(50, 25));
            Suchen.setPreferredSize(new Dimension(50, 25));
            Suchen.addActionListener(e -> SuchenActionPerformed(e));
            panel1.add(Suchen);
            Suchen.setBounds(new Rectangle(new Point(385, 124), Suchen.getPreferredSize()));

            //---- WSuchen ----
            WSuchen.setText("...");
            WSuchen.setToolTipText("Weitersuchen");
            WSuchen.setMaximumSize(new Dimension(50, 25));
            WSuchen.setMinimumSize(new Dimension(50, 25));
            WSuchen.setPreferredSize(new Dimension(50, 25));
            WSuchen.addActionListener(e -> WSuchenActionPerformed(e));
            panel1.add(WSuchen);
            WSuchen.setBounds(new Rectangle(new Point(440, 124), WSuchen.getPreferredSize()));

            //---- Drucken ----
            Drucken.setText("D");
            Drucken.setToolTipText("Liste der Einnahmen drucken");
            Drucken.setMaximumSize(new Dimension(50, 25));
            Drucken.setMinimumSize(new Dimension(50, 25));
            Drucken.setPreferredSize(new Dimension(50, 25));
            Drucken.addActionListener(e -> DruckenActionPerformed(e));
            panel1.add(Drucken);
            Drucken.setBounds(new Rectangle(new Point(495, 124), Drucken.getPreferredSize()));

            //---- Schliessen ----
            Schliessen.setText("X");
            Schliessen.setMaximumSize(new Dimension(50, 25));
            Schliessen.setMinimumSize(new Dimension(50, 25));
            Schliessen.setPreferredSize(new Dimension(50, 25));
            Schliessen.addActionListener(e -> SchliessenActionPerformed(e));
            panel1.add(Schliessen);
            Schliessen.setBounds(new Rectangle(new Point(550, 124), Schliessen.getPreferredSize()));

            //---- jLabel4 ----
            jLabel4.setText("Kennwort");
            panel1.add(jLabel4);
            jLabel4.setBounds(255, 50, 175, 14);

            //---- field_Kennwort ----
            field_Kennwort.setPreferredSize(new Dimension(645, 25));
            panel1.add(field_Kennwort);
            field_Kennwort.setBounds(255, 70, 175, 25);

            //---- jLabel5 ----
            jLabel5.setText("Rechte");
            panel1.add(jLabel5);
            jLabel5.setBounds(440, 50, 175, 14);

            //---- field_Rechte ----
            field_Rechte.setModel(new DefaultComboBoxModel<>(new String[] {
                "1 Berichtswesen",
                "2 Bewegungsdaten",
                "4 Stammdaten",
                "8 Administrator"
            }));
            panel1.add(field_Rechte);
            field_Rechte.setBounds(440, 70, 160, field_Rechte.getPreferredSize().height);

            { // compute preferred size
                Dimension preferredSize = new Dimension();
                for(int i = 0; i < panel1.getComponentCount(); i++) {
                    Rectangle bounds = panel1.getComponent(i).getBounds();
                    preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                    preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                }
                Insets insets = panel1.getInsets();
                preferredSize.width += insets.right;
                preferredSize.height += insets.bottom;
                panel1.setMinimumSize(preferredSize);
                panel1.setPreferredSize(preferredSize);
            }
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(panel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(panel1, GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE)
                    .addContainerGap())
        );
        setSize(680, 215);
        setLocationRelativeTo(getOwner());
    }// </editor-fold>//GEN-END:initComponents

    private void UpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UpdateActionPerformed
        // TODO add your handling code here:
        try {
            if (resultIsEmpty) {
                Modulhelferlein.Fehlermeldung("Die Datenbank ist leer - bitte Datensatz einfügen!");
            } else {
                String password = JOptionPane.showInputDialog("Bestätigung Kennwort?");
                if (password.equals(field_Kennwort.getText())) {
                    MessageDigest md = MessageDigest.getInstance("MD5");
                    byte[] messageDigest = md.digest(password.getBytes());
                    BigInteger no = new BigInteger(1, messageDigest);
                    String hashtext = no.toString(16);
                    while (hashtext.length() < 32) {
                        hashtext = "0" + hashtext;
                    }
                    System.out.println("MD5-HashCode is: " + hashtext);
                    result.updateString("BENUTZER_KENNWORT", hashtext);
                    result.updateString("BENUTZER_NAME", field_Beschreibung.getText());
                    String Rechte[] = field_Rechte.getItemAt(field_Rechte.getSelectedIndex()).split(" ");
                    result.updateInt("BENUTZER_RECHTE", Integer.parseInt(Rechte[0]));
                    result.updateRow();
                    // Konfiguration KONFIGURATION_BENUTZER anpassen
                    resultKonfiguration = SQLAnfrage.executeQuery("SELECT * FROM tbl_konfiguration WHERE KONFIGURATION_BENUTZER ='" + Modulhelferlein.CHMVBenutzer + "'");
                    resultKonfiguration.first();
                    resultKonfiguration.updateString("KONFIGURATION_BENUTZER", field_Beschreibung.getText());
                    resultKonfiguration.updateRow();
                } else {
                    Modulhelferlein.Fehlermeldung("Die Kennwörter stimmen nicht überein!");
                }
            }
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: " + exept.getMessage());
        } catch (NoSuchAlgorithmException exept) {
            Modulhelferlein.Fehlermeldung("NoSuchAlgorithm-Exception: " + exept.getMessage());
        }
    }//GEN-LAST:event_UpdateActionPerformed

    private void EinfuegenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EinfuegenActionPerformed
        // TODO add your handling code here:
        try {
            int ID;
            String eingabeBenutzer = JOptionPane.showInputDialog(null, "Wie heißt der neue Benutzer (Vorname Name)?",
                    "Neuer Benutzer",
                    JOptionPane.PLAIN_MESSAGE);
            if ("".equals(eingabeBenutzer)) {
                Modulhelferlein.Fehlermeldung("Kein neuer Benutzer angegeben");
            } else {
                maxID = maxID + 1;
                result.moveToInsertRow();
                result.updateInt("BENUTZER_ID", maxID);
                result.updateString("BENUTZER_NAME", eingabeBenutzer);
                result.insertRow();
                countMax = countMax + 1;
                field_countMax.setText(Integer.toString(countMax));
                count = countMax;
                field_count.setText(Integer.toString(count));

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
                Drucken.setEnabled(true);
                Schliessen.setEnabled(true);

                resultIsEmpty = false;
                result.last();

                field_ID.setText(Integer.toString(result.getInt("BENUTZER_ID")));
                field_Beschreibung.setText(result.getString("BENUTZER_NAME"));

                // Neuer Datensatz in der Konfiguration anlegen
                resultKonfiguration = SQLAnfrageKonfiguration.executeQuery("SELECT * FROM tbl_konfiguration");
                resultKonfiguration.last();
                ID = resultKonfiguration.getInt("Konfiguration_ID");
                resultKonfiguration.moveToInsertRow();
                resultKonfiguration.updateInt("Konfiguration_ID", ID + 1);
                resultKonfiguration.updateString("Konfiguration_Stammdaten", "");
                resultKonfiguration.updateString("Konfiguration_Einnahmen", "");
                resultKonfiguration.updateString("Konfiguration_Ausgaben", "");
                resultKonfiguration.updateString("Konfiguration_Umsaetze", "");
                resultKonfiguration.updateString("Konfiguration_Rechnungen", "");
                resultKonfiguration.updateString("Konfiguration_Sicherung", "");
                resultKonfiguration.updateString("Konfiguration_Mahnungen", "");
                resultKonfiguration.updateString("Konfiguration_Termine", "");
                resultKonfiguration.updateString("Konfiguration_Schriftverkehr", "");
                resultKonfiguration.updateString("Konfiguration_Steuer", "");
                resultKonfiguration.updateString("Konfiguration_Benutzer", eingabeBenutzer);
                Modulhelferlein.CHMVBenutzer = eingabeBenutzer;
                resultKonfiguration.insertRow();
            }
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: " + exept.getMessage());
        }
    }//GEN-LAST:event_EinfuegenActionPerformed

    private void LoeschenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LoeschenActionPerformed
        // TODO add your handling code here:
        if (JOptionPane.showConfirmDialog(null, "Soll der Datensatz wirklich gelöscht werden?") == JOptionPane.YES_OPTION) {
            try {
                result.deleteRow();
                countMax = countMax - 1;
                field_countMax.setText(Integer.toString(countMax));
                count = 1;
                field_count.setText(Integer.toString(count));

                result.first();
                if (countMax > 0) {
                    field_ID.setText(Integer.toString(result.getInt("BENUTZER_ID")));
                    field_Beschreibung.setText(result.getString("BENUTZER_NAME"));
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
                    Drucken.setEnabled(true);
                    Schliessen.setEnabled(true);
                } else {
                    resultIsEmpty = true;
                    field_ID.setText("");
                    field_Beschreibung.setText("");
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
                    Drucken.setEnabled(false);
                    Schliessen.setEnabled(true);
                }
            } catch (SQLException exept) {
                Modulhelferlein.Fehlermeldung("SQL-Exception: " + exept.getMessage());
            }
        }
    }//GEN-LAST:event_LoeschenActionPerformed

    private void SuchenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SuchenActionPerformed
        // TODO add your handling code here:
        boolean gefunden = false;
        try {
            do {
                result.first();
                count = 1;
                field_count.setText(Integer.toString(count));
                if (result.getString("BENUTZER_NAME").contains(field_Beschreibung.getText())) {
                    gefunden = true;
                } else {
                    count = count + 1;
                }
            } while ((!gefunden) && result.next());
            if (gefunden) {
                Anfang.setEnabled(true);
                Zurueck.setEnabled(true);
                Vor.setEnabled(true);
                Ende.setEnabled(true);
                Update.setEnabled(true);
                Einfuegen.setEnabled(true);
                Loeschen.setEnabled(true);
                Suchen.setEnabled(true);
                WSuchen.setEnabled(true);
                Drucken.setEnabled(true);
                Schliessen.setEnabled(true);

                field_count.setText(Integer.toString(count));

                field_ID.setText(Integer.toString(result.getInt("BENUTZER_ID")));
                field_Beschreibung.setText(result.getString("BENUTZER_NAME"));
            } else {
                Modulhelferlein.Infomeldung("Datensatz wurde nicht gefunden!");
                AnfangActionPerformed(evt);
            }
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: " + exept.getMessage());
            Logger.getLogger(VerwaltenDatenbankEinnahmen.class.getName()).log(Level.SEVERE, null, exept);
        }

    }//GEN-LAST:event_SuchenActionPerformed

    private void WSuchenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_WSuchenActionPerformed
        // TODO add your handling code here:
        boolean gefunden = false;

        try {
            do {
                if (result.getString("BENUTZER_NAME").contains(field_Beschreibung.getText())) {
                    gefunden = true;
                } else {
                    count = count + 1;
                }
            } while ((!gefunden) && result.next());
            if (gefunden) {
                Anfang.setEnabled(true);
                Zurueck.setEnabled(true);
                Vor.setEnabled(true);
                Ende.setEnabled(true);
                Update.setEnabled(true);
                Einfuegen.setEnabled(true);
                Loeschen.setEnabled(true);
                Suchen.setEnabled(true);
                WSuchen.setEnabled(true);
                Drucken.setEnabled(true);
                Schliessen.setEnabled(true);

                field_count.setText(Integer.toString(count));

                field_ID.setText(Integer.toString(result.getInt("BENUTZER_ID")));
                field_Beschreibung.setText(result.getString("BENUTZER_NAME"));
            } else {
                Modulhelferlein.Infomeldung("Datensatz wurde nicht gefunden!");
                AnfangActionPerformed(evt);
            }
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: " + exept.getMessage());
            Logger.getLogger(VerwaltenDatenbankEinnahmen.class.getName()).log(Level.SEVERE, null, exept);
        }
    }//GEN-LAST:event_WSuchenActionPerformed

    private void DruckenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DruckenActionPerformed
        // TODO add your handling code here:
        berMailVerteiler.bericht();
    }//GEN-LAST:event_DruckenActionPerformed

    private void SchliessenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SchliessenActionPerformed
        // TODO add your handling code here:
        try {
            result.close();
            SQLAnfrage.close();
            conn.close();
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: " + exept.getMessage());
        }
        this.dispose();
    }//GEN-LAST:event_SchliessenActionPerformed

    private void ZurueckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ZurueckActionPerformed
        // TODO add your handling code here:
        try {
            if (result.previous()) {
                count = count - 1;
                field_count.setText(Integer.toString(count));
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
                Drucken.setEnabled(true);
                Schliessen.setEnabled(true);
            } else {
                result.next();
            }
            field_ID.setText(Integer.toString(result.getInt("BENUTZER_ID")));
            field_Beschreibung.setText(result.getString("BENUTZER_NAME"));
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: " + exept.getMessage());
        }
    }//GEN-LAST:event_ZurueckActionPerformed

    private void AnfangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AnfangActionPerformed
        // TODO add your handling code here:
        try {
            result.first();
            count = 1;
            field_count.setText(Integer.toString(count));

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
            Drucken.setEnabled(true);
            Schliessen.setEnabled(true);

            field_ID.setText(Integer.toString(result.getInt("BENUTZER_ID")));
            field_Beschreibung.setText(result.getString("BENUTZER_NAME"));
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: " + exept.getMessage());
        }
    }//GEN-LAST:event_AnfangActionPerformed

    private void VorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_VorActionPerformed
        // TODO add your handling code here:
        try {
            if (result.next()) {
                count = count + 1;
                field_count.setText(Integer.toString(count));
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
                Drucken.setEnabled(true);
                Schliessen.setEnabled(true);
            } else {
                result.previous();
                // Schalterzustände anpassen
                Anfang.setEnabled(true);
                Zurueck.setEnabled(true);
                Vor.setEnabled(false);
                Ende.setEnabled(false);
                Update.setEnabled(true);
                Einfuegen.setEnabled(true);
                Loeschen.setEnabled(true);
                Suchen.setEnabled(true);
                WSuchen.setEnabled(true);
                Drucken.setEnabled(true);
                Schliessen.setEnabled(true);
            }
            field_ID.setText(Integer.toString(result.getInt("BENUTZER_ID")));
            field_Beschreibung.setText(result.getString("BENUTZER_NAME"));
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: " + exept.getMessage());
        }
    }//GEN-LAST:event_VorActionPerformed

    private void EndeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EndeActionPerformed
        // TODO add your handling code here:
        try {
            result.last();
            count = countMax;
            field_count.setText(Integer.toString(count));

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
            Drucken.setEnabled(true);
            Schliessen.setEnabled(true);

            field_ID.setText(Integer.toString(result.getInt("EINNAHMEN_ID")));
            field_Beschreibung.setText(result.getString("EINNAHMEN_BESCHREIBUNG"));
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: " + exept.getMessage());
        }
    }//GEN-LAST:event_EndeActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
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

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(() -> {
            VerwaltenDatenbankBenutzer dialog = new VerwaltenDatenbankBenutzer(new javax.swing.JFrame(), true);
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
    private JLabel jLabelTitel;
    private JLabel jLabelID;
    private JTextField field_ID;
    private JLabel jLabel1;
    private JTextField field_count;
    private JLabel jLabel2;
    private JTextField field_countMax;
    private JLabel jLabel3;
    private JTextField field_Beschreibung;
    private JButton Anfang;
    private JButton Zurueck;
    private JButton Vor;
    private JButton Ende;
    private JButton Update;
    private JButton Einfuegen;
    private JButton Loeschen;
    private JButton Suchen;
    private JButton WSuchen;
    private JButton Drucken;
    private JButton Schliessen;
    private JLabel jLabel4;
    private JTextField field_Kennwort;
    private JLabel jLabel5;
    private JComboBox<String> field_Rechte;
    // End of variables declaration//GEN-END:variables

    Integer count = 0;
    Integer countMax = 0;
    Integer maxID = 0;

    Connection conn;
    Statement SQLAnfrage;
    Statement SQLAnfrageKonfiguration;
    ResultSet result;
    ResultSet resultKonfiguration;

    boolean resultIsEmpty = true;

}
