/*
 * Copyright (C) 2017 EDV-Beratung Zimmermann
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

/**
 *
 * @author thoma
 */
public class _DlgDatensicherung extends javax.swing.JDialog {

    static JFrame Dialog = new JFrame("Carola Hartmann Miles Verlag");
    static JLabel dlgLabel = new JLabel("Datensicherung läuft ...");
    static JPanel dlgPanel;
    static JScrollPane dlgScrollpane;
    static JTextArea dlgTextarea;

    public void Dialog() {
        if (JOptionPane.showConfirmDialog(null,
                "Ist der Datenträger für die Sicherung eingelegt?",
                "Datensicherung",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            Dialog.setSize(600, 500);
            Dialog.setLocationRelativeTo(null);
            Dialog.setResizable(false);

            dlgPanel = new JPanel();
            dlgPanel.setBounds(10, 40, 580, 440);

            dlgLabel.setBounds(10, 10, 580, 25);
            dlgPanel.add(dlgLabel);

            dlgTextarea = new JTextArea(5, 50);
            dlgTextarea.setBounds(10, 40, 580, 440);
            dlgTextarea.setLineWrap(true);
            dlgTextarea.setWrapStyleWord(true);
            dlgTextarea.setFont(new Font("Monospaced", Font.BOLD, 12));
            dlgTextarea.setForeground(Color.WHITE);
            dlgTextarea.setBackground(Color.BLACK);

            dlgScrollpane = new JScrollPane(dlgTextarea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            dlgScrollpane.setPreferredSize(new Dimension(580, 440));
            dlgPanel.add(dlgScrollpane);

            Dialog.add(dlgPanel);

            Dialog.setVisible(true);

            String[] command = {"cmd", "/c", "robocopy.exe",
                ModulHelferlein.pathQuelle,
                ModulHelferlein.pathZiel,
                "/S", "/E", "/TS", "/FP", "/M", "/R:3", "/W:3"
            };
            String OutFilename = ModulHelferlein.pathSicherung + "/"
                    + ModulHelferlein.printSimpleDateFormat("yyyyMMdd")
                    + "-Vollsicherung.log";
            Integer exitVal = 0;
            try {
                ProcessBuilder probuilder = new ProcessBuilder(command);
                Process process = probuilder.start();
                InputStream is = process.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line;
                while ((line = br.readLine()) != null) {
                    //helferlein.Infomeldung(line);
                    System.out.println(line);
                    dlgTextarea.append(line + "\r\n");
                    dlgTextarea.update(dlgTextarea.getGraphics());
                }

                //Wait to get exit value
                try {
                    int exitValue = process.waitFor();

                    try {
                        try (FileWriter fWriter = new FileWriter(OutFilename, false); BufferedWriter bufferedWriter = new BufferedWriter(fWriter)) {
                            bufferedWriter.write(dlgTextarea.getText());
                        }
                    } catch (IOException e) {
                        ModulHelferlein.Fehlermeldung("Fehler beim Schreiben der Log-Datei: " + e.getMessage());
                    }
                    switch (exitValue) {
                        case 0:
                            ModulHelferlein.Infomeldung("KEINE ÄNDERUNG\n"
                                    + "Quelle und Ziel sind snychron");
                            break;
                        case 1:
                            ModulHelferlein.Infomeldung("OK\n"
                                    + "Verlagsdaten wurden fehlerfrei gesichert");
                            break;
                        case 2:
                            ModulHelferlein.Infomeldung("EXTRA DATEIEN\n"
                                    + "Im Zielverzeichnis befinden sich Dateien, die nicht im Quellverzeichnis sind.\n"
                                    + "Details sind in der Log-Datei.\n"
                                    + "Verlagsdaten wurden nicht gesichert");
                            break;
                        case 3:
                            ModulHelferlein.Infomeldung("TEILWEISE ERFOLGREICH\n"
                                    + "Im Zielverzeichnis befinden sich Dateien, die nicht im Quellverzeichnis sind.\n"
                                    + "Einige Dateien wurden fehlerfrei gesichert");
                            break;
                        case 4:
                            ModulHelferlein.Infomeldung("UNGEREIMTHEITEN\n"
                                    + "Verlagsdaten wurden nicht gesichert");
                            break;
                        case 8:
                            ModulHelferlein.Infomeldung("FEHLER\n"
                                    + "Einige Dateien oder Verzeichnisse konnten nicht kopiert werden.\n"
                                    + "Details sind in der Log-Datei.");
                            break;
                        case 5:
                            ModulHelferlein.Infomeldung("TEILWEISE ERFOLGREICH\n"
                                    + "Ungereimtheiten bei Dateien und/oder Verzeichnissen wurden festgestellt.\n"
                                    + "Einige Dateien wurden fehlerfrei kopiert. ");
                            break;
                        case 6:
                            ModulHelferlein.Infomeldung("TEILWEISE ERFOLGREICH\n"
                                    + "Ungereimtheiten wurden festgestellt. Zusätzliche Dateien wurden festgestellt.\n"
                                    + "Dateien wurden nicht kopiert.");
                            break;
                        case 7:
                            ModulHelferlein.Infomeldung("TEILWEISE ERFOLGREICH\n"
                                    + "Ungereimtheiten wurden festgestellt.\n"
                                    + "Zusätzliche Dateien wurden festgestellt.\n"
                                    + "Einige Dateien wurden fehlerfrei kopiert.");
                            break;
                        case 9:
                            ModulHelferlein.Infomeldung("TEILWEISE ERFOLGREICH\n"
                                    + "Details siehe Handbuch");
                            break;
                        case 10:
                            ModulHelferlein.Infomeldung("TEILWEISE ERFOLGREICH\n"
                                    + "Details siehe Handbuch");
                            break;
                        case 11:
                            ModulHelferlein.Infomeldung("TEILWEISE ERFOLGREICH\n"
                                    + "Details siehe Handbuch");
                            break;
                        case 12:
                            ModulHelferlein.Infomeldung("TEILWEISE ERFOLGREICH\n"
                                    + "Details siehe Handbuch");
                            break;
                        case 13:
                            ModulHelferlein.Infomeldung("TEILWEISE ERFOLGREICH\n"
                                    + "Details siehe Handbuch");
                            break;
                        case 14:
                            ModulHelferlein.Infomeldung("TEILWEISE ERFOLGREICH\n"
                                    + "Details siehe Handbucht");
                            break;
                        case 15:
                            ModulHelferlein.Infomeldung("TEILWEISE ERFOLGREICH\n"
                                    + "Details siehe Handbuch");
                            break;
                        default:
                            ModulHelferlein.Infomeldung("FATALER FEHLER\n"
                                    + "Verlagsdaten wurden nicht gesichert");
                            break;
                    }

                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    ModulHelferlein.Fehlermeldung(e.getMessage());
                }
            } catch (IOException ex) {
                ModulHelferlein.Fehlermeldung("Fehler beim Schreiben der Log-Datei: " + ex.getMessage());
            }
            Dialog.setVisible(false);
        }
    }

}
