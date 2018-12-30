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
import java.awt.Component;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import javax.swing.*;
import javax.swing.JOptionPane;
import com.toedter.calendar.*;
import java.awt.event.ActionEvent;
import javax.swing.plaf.ActionMapUIResource;
import static milesVerlagMain.ModulMyOwnFocusTraversalPolicy.newPolicy;

/**
 *
 * @author Thomas Zimmermann
 */
public class VerwaltenDatenbankTermine extends javax.swing.JDialog {

    /**
     * Creates new form VerwaltenDatenbankTermine
     *
     * @param parent
     * @param modal
     */
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public VerwaltenDatenbankTermine(java.awt.Frame parent, boolean modal) {
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

        
        InputMap keyMap = new ComponentInputMap(panel2);
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
        SwingUtilities.replaceUIActionMap(panel2, actionMap);
        SwingUtilities.replaceUIInputMap(panel2, JComponent.WHEN_IN_FOCUSED_WINDOW,keyMap);

        conn = null;

        Vector<Component> order = new Vector<>(14);
        order.add(field_Datum);
        order.add(field_Beschreibung);
        order.add(field_erledigt);
        order.add(Anfang);
        order.add(Zurueck);
        order.add(Vor);
        order.add(Ende);
        order.add(Update);
        order.add(Einfuegen);
        order.add(Loeschen);
        order.add(Suchen);
        order.add(WSuchen);
        order.add(Drucken);
        order.add(Schliessen);
        
        newPolicy = new ModulMyOwnFocusTraversalPolicy(order);
        setFocusTraversalPolicy(newPolicy);

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

                try {
                    SQLAnfrage = conn.createStatement(
                            ResultSet.TYPE_SCROLL_SENSITIVE,
                            ResultSet.CONCUR_UPDATABLE); // Anfrage der DB conn2 zuordnen
                    result = SQLAnfrage.executeQuery("SELECT * FROM tbl_termine ORDER BY TERMIN_ID DESC"); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 
                    if (result.next()) {
                        maxID = 1 + result.getInt("TERMIN_ID");
                    } else {
                        maxID = 0;
                    }
                    result = SQLAnfrage.executeQuery("SELECT * FROM tbl_termine ORDER BY TERMIN_DATUM"); // schickt SQL an DB und erzeugt ergebnis -> wird in result gespeichert 
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
                        field_ID.setText(Integer.toString(result.getInt("Termin_ID")));
                        field_Beschreibung.setText(result.getString("Termin_Beschreibung"));
                        field_Datum.setDate(Modulhelferlein.SQLDate2Date(result.getDate("Termin_Datum")));
                        field_erledigt.setText(result.getString("Termin_erledigt"));
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
        panel2 = new JPanel();
        field_Datum = new JDateChooser();
        jLabel1 = new JLabel();
        jLabel2 = new JLabel();
        jLabel3 = new JLabel();
        field_Beschreibung = new JTextField();
        field_countMax = new JTextField();
        field_count = new JTextField();
        jLabel5 = new JLabel();
        jLabel4 = new JLabel();
        field_erledigt = new JTextField();
        jLabel6 = new JLabel();
        WSuchen = new JButton();
        Anfang = new JButton();
        Einfuegen = new JButton();
        Suchen = new JButton();
        Update = new JButton();
        Loeschen = new JButton();
        Schliessen = new JButton();
        Zurueck = new JButton();
        Vor = new JButton();
        Ende = new JButton();
        Drucken = new JButton();
        field_ID = new JTextField();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Carola Hartmann Miles Verlag");
        setResizable(false);
        Container contentPane = getContentPane();
        contentPane.setLayout(null);

        //======== panel2 ========
        {

            //---- jLabel1 ----
            jLabel1.setFont(new Font("Tahoma", Font.BOLD, 12));
            jLabel1.setText("Terminverwaltung");

            //---- jLabel2 ----
            jLabel2.setText("Datum");

            //---- jLabel3 ----
            jLabel3.setText("Beschreibung");

            //---- field_Beschreibung ----
            field_Beschreibung.setText("jTextField1");
            field_Beschreibung.setPreferredSize(new Dimension(350, 25));

            //---- field_countMax ----
            field_countMax.setEditable(false);
            field_countMax.setText("000");
            field_countMax.setEnabled(false);
            field_countMax.setFocusable(false);
            field_countMax.setMinimumSize(new Dimension(30, 25));
            field_countMax.setPreferredSize(new Dimension(30, 25));

            //---- field_count ----
            field_count.setEditable(false);
            field_count.setText("000");
            field_count.setEnabled(false);
            field_count.setFocusable(false);
            field_count.setMinimumSize(new Dimension(30, 25));
            field_count.setPreferredSize(new Dimension(30, 25));

            //---- jLabel5 ----
            jLabel5.setText("von");

            //---- jLabel4 ----
            jLabel4.setText("Datensatz");

            //---- field_erledigt ----
            field_erledigt.setText("jTextField2");

            //---- jLabel6 ----
            jLabel6.setText("erledigt");

            //---- WSuchen ----
            WSuchen.setText("...");
            WSuchen.setToolTipText("Weitersuchen");
            WSuchen.addActionListener(e -> WSuchenActionPerformed(e));

            //---- Anfang ----
            Anfang.setText("<<");
            Anfang.setToolTipText("gehe zum ersten Datensatz");
            Anfang.addActionListener(e -> AnfangActionPerformed(e));

            //---- Einfuegen ----
            Einfuegen.setText("+");
            Einfuegen.setToolTipText("Datensatz einf\u00fcgen");
            Einfuegen.addActionListener(e -> EinfuegenActionPerformed(e));

            //---- Suchen ----
            Suchen.setText("?");
            Suchen.setToolTipText("Suche nach Autor, Titel, ISBN oder Druckereinummer");
            Suchen.addActionListener(e -> SuchenActionPerformed(e));

            //---- Update ----
            Update.setText("!");
            Update.setToolTipText("Datensatz aktualisieren");
            Update.addActionListener(e -> UpdateActionPerformed(e));

            //---- Loeschen ----
            Loeschen.setText("-");
            Loeschen.setToolTipText("Datensatz l\u00f6schen");
            Loeschen.addActionListener(e -> LoeschenActionPerformed(e));

            //---- Schliessen ----
            Schliessen.setText("X");
            Schliessen.setToolTipText("Schlie\u00dft den Dialog");
            Schliessen.addActionListener(e -> SchliessenActionPerformed(e));

            //---- Zurueck ----
            Zurueck.setText("<");
            Zurueck.setToolTipText("gehe zum vorherigen Datensatz");
            Zurueck.addActionListener(e -> ZurueckActionPerformed(e));

            //---- Vor ----
            Vor.setText(">");
            Vor.setToolTipText("gehe zum n\u00e4chsten Datensatz");
            Vor.addActionListener(e -> VorActionPerformed(e));

            //---- Ende ----
            Ende.setText(">>");
            Ende.setToolTipText("gehe zum letzten Datensatz");
            Ende.addActionListener(e -> EndeActionPerformed(e));

            //---- Drucken ----
            Drucken.setText("D");
            Drucken.setToolTipText("Druckt das aktuelle Buchprojekt als PDF");
            Drucken.addActionListener(e -> DruckenActionPerformed(e));

            //---- field_ID ----
            field_ID.setEditable(false);
            field_ID.setText("jTextField1");
            field_ID.setEnabled(false);
            field_ID.setFocusable(false);

            GroupLayout panel2Layout = new GroupLayout(panel2);
            panel2.setLayout(panel2Layout);
            panel2Layout.setHorizontalGroup(
                panel2Layout.createParallelGroup()
                    .addGroup(panel2Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel1)
                        .addGap(424, 424, 424)
                        .addComponent(field_ID, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(jLabel4)
                        .addGap(6, 6, 6)
                        .addComponent(field_count, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(jLabel5)
                        .addGap(2, 2, 2)
                        .addComponent(field_countMax, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGroup(panel2Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel2)
                        .addGap(104, 104, 104)
                        .addComponent(jLabel3)
                        .addGap(316, 316, 316)
                        .addComponent(jLabel6))
                    .addGroup(panel2Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(field_Datum, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(field_Beschreibung, GroupLayout.PREFERRED_SIZE, 374, GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(field_erledigt, GroupLayout.PREFERRED_SIZE, 247, GroupLayout.PREFERRED_SIZE))
                    .addGroup(panel2Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(Anfang)
                        .addGap(6, 6, 6)
                        .addComponent(Zurueck, GroupLayout.PREFERRED_SIZE, 49, GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(Vor, GroupLayout.PREFERRED_SIZE, 49, GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(Ende)
                        .addGap(16, 16, 16)
                        .addComponent(Update, GroupLayout.PREFERRED_SIZE, 49, GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(Einfuegen, GroupLayout.PREFERRED_SIZE, 49, GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(Loeschen, GroupLayout.PREFERRED_SIZE, 49, GroupLayout.PREFERRED_SIZE)
                        .addGap(16, 16, 16)
                        .addComponent(Suchen, GroupLayout.PREFERRED_SIZE, 49, GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(WSuchen, GroupLayout.PREFERRED_SIZE, 49, GroupLayout.PREFERRED_SIZE)
                        .addGap(21, 21, 21)
                        .addComponent(Drucken, GroupLayout.PREFERRED_SIZE, 49, GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(Schliessen, GroupLayout.PREFERRED_SIZE, 49, GroupLayout.PREFERRED_SIZE))
            );
            panel2Layout.setVerticalGroup(
                panel2Layout.createParallelGroup()
                    .addGroup(panel2Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(panel2Layout.createParallelGroup()
                            .addComponent(field_count, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(field_countMax, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addGroup(panel2Layout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addGroup(panel2Layout.createParallelGroup()
                                    .addComponent(jLabel1)
                                    .addComponent(field_ID, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel5))))
                        .addGap(20, 20, 20)
                        .addGroup(panel2Layout.createParallelGroup()
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel6))
                        .addGap(1, 1, 1)
                        .addGroup(panel2Layout.createParallelGroup()
                            .addComponent(field_Datum, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(field_Beschreibung, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(field_erledigt, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addGap(45, 45, 45)
                        .addGroup(panel2Layout.createParallelGroup()
                            .addComponent(Anfang)
                            .addComponent(Zurueck)
                            .addComponent(Vor)
                            .addComponent(Ende)
                            .addComponent(Update)
                            .addComponent(Einfuegen)
                            .addComponent(Loeschen)
                            .addComponent(Suchen)
                            .addComponent(WSuchen)
                            .addComponent(Drucken)
                            .addComponent(Schliessen)))
            );
        }
        contentPane.add(panel2);
        panel2.setBounds(0, 0, 785, 175);

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
        setSize(785, 205);
        setLocationRelativeTo(getOwner());
    }// </editor-fold>//GEN-END:initComponents

    private void WSuchenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_WSuchenActionPerformed
        // TODO add your handling code here:
        boolean gefunden = false;

        try {
            do {
            } while ((!gefunden) && result.next());
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: " + exept.getMessage());
        }
    }//GEN-LAST:event_WSuchenActionPerformed

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

            field_ID.setText(Integer.toString(result.getInt("Termin_ID")));
            field_Beschreibung.setText(result.getString("Termin_Beschreibung"));
            field_Datum.setDate(Modulhelferlein.SQLDate2Date(result.getDate("Termin_Datum")));
            field_erledigt.setText(result.getString("Termin_erledigt"));
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: " + exept.getMessage());
        }
    }//GEN-LAST:event_AnfangActionPerformed

    private void EinfuegenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EinfuegenActionPerformed
        // TODO add your handling code here:
        
        try {
            maxID = maxID + 1;
            result.moveToInsertRow();
            result.updateInt("Termin_ID", maxID);
            result.updateString("Termin_Beschreibung", "");
            result.updateDate("Termin_Datum", Modulhelferlein.Date2SQLDate(Modulhelferlein.CurDate));
            result.updateString("Termin_erledigt", "");
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

            field_ID.setText(Integer.toString(result.getInt("Termin_ID")));
            field_Beschreibung.setText(result.getString("Termin_Beschreibung"));
            field_Datum.setDate(Modulhelferlein.SQLDate2Date(result.getDate("Termin_Datum")));
            field_erledigt.setText(result.getString("Termin_erledigt"));
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: " + exept.getMessage());
        }
    }//GEN-LAST:event_EinfuegenActionPerformed

    private void SuchenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SuchenActionPerformed
        // TODO add your handling code here:
        boolean gefunden = false;
        String[] Kriterien = {"Datum", "Beschreibung", "Elredigt"};
        String Kriterium = (String) JOptionPane.showInputDialog(null,
                "Suchen",
                "Nach was soll gesucht werden?",
                JOptionPane.QUESTION_MESSAGE,
                null, Kriterien,
                Kriterien[2]);

        try {
            result.first();
            count = 1;
            do {
                field_count.setText(Integer.toString(count));
                switch (Kriterium) {
                    case "Datum":
                        if (result.getString("TERMIN_DATUM").equals(field_Datum.getDate().toString())) {
                            gefunden = true;
                        } else {
                            count = count + 1;
                        }
                        break;
                }

            } while ((!gefunden) && result.next());
            if (gefunden) {

            } else {
                Modulhelferlein.Infomeldung(Kriterium + " wurde nicht gefunden!");
                AnfangActionPerformed(evt);
            }

        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: " + exept.getMessage());
        }
    }//GEN-LAST:event_SuchenActionPerformed

    private void UpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UpdateActionPerformed
        // TODO add your handling code here:
        try {
            if (resultIsEmpty) {
                Modulhelferlein.Fehlermeldung("Die Datenbank ist leer - bitte Datensatz einfügen!");
            } else {
                result.updateString("Termin_Beschreibung", field_Beschreibung.getText());
                result.updateDate("Termin_Datum", Modulhelferlein.Date2SQLDate(field_Datum.getDate()));
                result.updateString("Termin_erledigt", field_erledigt.getText());
                result.updateRow();
            }
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: " + exept.getMessage());
        }
    }//GEN-LAST:event_UpdateActionPerformed

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
                    field_ID.setText(Integer.toString(result.getInt("Termin_ID")));
                    field_Beschreibung.setText(result.getString("Termin_Beschreibung"));
                    field_Datum.setDate(Modulhelferlein.SQLDate2Date(result.getDate("Termin_Datum")));
                    field_erledigt.setText(result.getString("Termin_erledigt"));
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
                    field_erledigt.setText("");
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
            field_ID.setText(Integer.toString(result.getInt("Termin_ID")));
            field_Beschreibung.setText(result.getString("Termin_Beschreibung"));
            field_Datum.setDate(Modulhelferlein.SQLDate2Date(result.getDate("Termin_Datum")));
            field_erledigt.setText(result.getString("Termin_erledigt"));
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: " + exept.getMessage());
        }
    }//GEN-LAST:event_ZurueckActionPerformed

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
            }
            field_ID.setText(Integer.toString(result.getInt("Termin_ID")));
            field_Beschreibung.setText(result.getString("Termin_Beschreibung"));
            field_Datum.setDate(Modulhelferlein.SQLDate2Date(result.getDate("Termin_Datum")));
            field_erledigt.setText(result.getString("Termin_erledigt"));
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

            field_ID.setText(Integer.toString(result.getInt("Termin_ID")));
            field_Beschreibung.setText(result.getString("Termin_Beschreibung"));
            field_Datum.setDate(Modulhelferlein.SQLDate2Date(result.getDate("Termin_Datum")));
            field_erledigt.setText(result.getString("Termin_erledigt"));
        } catch (SQLException exept) {
            Modulhelferlein.Fehlermeldung("SQL-Exception: " + exept.getMessage());
        }
    }//GEN-LAST:event_EndeActionPerformed

    private void DruckenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DruckenActionPerformed
        // TODO add your handling code here:
        berTermine.bericht();
    }//GEN-LAST:event_DruckenActionPerformed

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
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CarolaHartmannMilesVerlag.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
*/        
        //</editor-fold>

        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(() -> {
            VerwaltenDatenbankTermine dialog = new VerwaltenDatenbankTermine(new javax.swing.JFrame(), true);
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
    private JPanel panel2;
    private JDateChooser field_Datum;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JTextField field_Beschreibung;
    private JTextField field_countMax;
    private JTextField field_count;
    private JLabel jLabel5;
    private JLabel jLabel4;
    private JTextField field_erledigt;
    private JLabel jLabel6;
    private JButton WSuchen;
    private JButton Anfang;
    private JButton Einfuegen;
    private JButton Suchen;
    private JButton Update;
    private JButton Loeschen;
    private JButton Schliessen;
    private JButton Zurueck;
    private JButton Vor;
    private JButton Ende;
    private JButton Drucken;
    private JTextField field_ID;
    // End of variables declaration//GEN-END:variables

    Integer count = 0;
    Integer countMax = 0;
    Integer maxID = 0;

    Connection conn = null;
    Statement SQLAnfrage = null;
    ResultSet result = null;

    boolean resultIsEmpty = true;

}
