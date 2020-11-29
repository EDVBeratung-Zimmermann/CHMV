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

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import static milesVerlagMain.ModulHelferlein.Linie;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import static org.apache.pdfbox.pdmodel.common.PDRectangle.A4;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblLayoutType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblLayoutType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
import static milesVerlagMain.ModulHelferlein.AusgabeLB;
import static milesVerlagMain.ModulHelferlein.Ausgabe;

public class briefPflichtexemplar {

    @SuppressWarnings("FieldMayBeFinal")
    private static JFrame Dialog = new JFrame("Carola Hartmann Miles Verlag");
    private static JLabel dlgLabel = new JLabel("");
    private static JPanel dlgPanel;
    private static JScrollPane dlgScrollpane;
    private static JTextArea dlgTextarea;

    private static void Bildschirmausgabe(String zeile) {
        dlgTextarea.append(zeile + "\r\n");
        dlgTextarea.update(dlgTextarea.getGraphics());
    }

    public static void briefDNBDOC(String Autor, String Titel, String ISBN, String Jahr) {

        String outputFileName = ModulHelferlein.pathBuchprojekte + "\\" + ISBN + "\\Pflichtexemplare"
                + "\\Pflichtexemplar"
                + "-"
                + ISBN
                + "-"
                + "DNB"
                + "-"
                + ModulHelferlein.printSimpleDateFormat("yyyyMMdd")
                + ".doc";

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
        dlgLabel.setText("Erzeuge " + outputFileName);
        Dialog.setVisible(true);

        Integer twipsPerInch = 1440;

        // Create a document and add a page to it
        try ( //Blank Document
                XWPFDocument document = new XWPFDocument()) {
            // create header-footer
            XWPFHeaderFooterPolicy headerFooterPolicy = document.getHeaderFooterPolicy();
            if (headerFooterPolicy == null) {
                headerFooterPolicy = document.createHeaderFooterPolicy();
            }
            CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
            CTPageMar pageMar = sectPr.addNewPgMar();
            pageMar.setLeft(BigInteger.valueOf(1152));
            pageMar.setTop(BigInteger.valueOf(288));
            pageMar.setRight(BigInteger.valueOf(1152));
            pageMar.setBottom(BigInteger.valueOf(0));
            pageMar.setFooter(BigInteger.valueOf(144));

            // create header start
            Bildschirmausgabe("... erzeuge Textblock Kopfzeile ...");
            XWPFHeader header = headerFooterPolicy.createHeader(XWPFHeaderFooterPolicy.DEFAULT);
            XWPFParagraph paragraphHeader = document.createParagraph();
            XWPFRun runHeader = paragraphHeader.createRun();
            paragraphHeader = header.createParagraph();
            paragraphHeader.setAlignment(ParagraphAlignment.CENTER);
            Path imagePath = Paths.get("header-brief.jpg");
            runHeader = paragraphHeader.createRun();
            runHeader.addPicture(Files.newInputStream(imagePath),
                    XWPFDocument.PICTURE_TYPE_PNG,
                    imagePath.getFileName().toString(), Units.toEMU(500), Units.toEMU(50));

            // Adresse
            Bildschirmausgabe("... erzeuge Textblock Adresse ...");
            XWPFTable table = document.createTable(1, 4);
            CTTblLayoutType type = table.getCTTbl().getTblPr().addNewTblLayout();
            type.setType(STTblLayoutType.FIXED);
            table.getCTTbl().getTblPr().unsetTblBorders();
            table.getCTTbl().addNewTblGrid().addNewGridCol().setW(BigInteger.valueOf(5000));
            table.getCTTbl().addNewTblGrid().addNewGridCol().setW(BigInteger.valueOf(576));
            table.getCTTbl().addNewTblGrid().addNewGridCol().setW(BigInteger.valueOf(2400));
            table.getCTTbl().addNewTblGrid().addNewGridCol().setW(BigInteger.valueOf(2400));

            XWPFTableRow rows = null;
            XWPFTableCell cell = null;
            XWPFParagraph paragraph;
            XWPFRun run;

            rows = table.getRow(0);
            cell = rows.getCell(0);
            paragraph = cell.getParagraphs().get(0);
            run = paragraph.createRun();
            run.setFontSize(9);
            run.setUnderline(UnderlinePatterns.SINGLE);
            run.setText("Carola Hartmann Miles Verlag - George-Cylay Str. 38 - 14089 Berlin     ");

            // Adresse
            paragraph = cell.addParagraph();
            run = paragraph.createRun();
            run.setFontFamily("Arial");
            run.setFontSize(12);

            run.setText("Deutsche Nationalbibliothek ");
            run.addBreak();
            run.setText("z.Hd. Frau Silvia Kellermann");
            run.addBreak();
            run.setText("Referat L 1.3");
            run.addBreak();
            run.setText("Deutscher Platz 1");
            run.addBreak();
            run.setText("");
            run.setText("04103 Leipzig");

            cell = rows.getCell(2);
            paragraph = cell.getParagraphs().get(0);
            paragraph.setAlignment(ParagraphAlignment.RIGHT);
            run = paragraph.createRun();
            run.setFontFamily("Arial");
            run.setFontSize(12);
            run.setBold(true);
            run.setText("  ");
            run.addBreak();
            run.setBold(true);
            run.setText(" ");
            run.addBreak();
            run.setText(" ");
            run.addBreak();
            run.setText(" ");
            run.addBreak();
            run.setText("Datum: ");
            run.addBreak();
            run.addBreak();
            run.setBold(false);
            run.setText(" ");

            cell = rows.getCell(3);
            paragraph = cell.getParagraphs().get(0);
            paragraph.setAlignment(ParagraphAlignment.LEFT);
            run = paragraph.createRun();
            run.setFontFamily("Arial");
            run.setFontSize(12);
            run.setText("  ");
            run.addBreak();
            run.setText(" ");
            run.addBreak();
            run.addBreak();
            run.setText(" ");
            run.setText(" ");
            run.addBreak();
            run.setText(ModulHelferlein.printSimpleDateFormat("dd.MM.yyyy"));
            run.addBreak();
            run.addBreak();
            run.setText(" ");
            run.addBreak();
            run.setText(" ");

            // Betreff
            if (Titel.length() > 50) {
                Titel = Titel.substring(0, 49) + "...";
            }
            Bildschirmausgabe("... erzeuge Textblock Betreffzeile ...");
            XWPFTable tableBetreff = document.createTable(1, 1);
            type = tableBetreff.getCTTbl().getTblPr().addNewTblLayout();
            type.setType(STTblLayoutType.FIXED);
            tableBetreff.getCTTbl().getTblPr().unsetTblBorders();
            tableBetreff.getCTTbl().addNewTblGrid().addNewGridCol().setW(BigInteger.valueOf(9000));

            rows = tableBetreff.getRow(0);
            cell = rows.getCell(0);
            XWPFParagraph paragraphBetreff = cell.getParagraphs().get(0);
            paragraphBetreff.setAlignment(ParagraphAlignment.LEFT);
            XWPFRun runBetreff = paragraphBetreff.createRun();
            runBetreff.setFontFamily("Arial");
            runBetreff.setFontSize(12);
            runBetreff.setBold(true);
            runBetreff.setText("Pflichtexemplare der Publikationen des Carola Hartmann Miles-Verlag");
            runBetreff.addBreak();
            runBetreff.addBreak();

            // Anrede
            Bildschirmausgabe("... erzeuge Textblock Anrede ...");
            XWPFParagraph paragraphAnrede = document.createParagraph();
            paragraphAnrede.setAlignment(ParagraphAlignment.LEFT);
            XWPFRun runAnrede = paragraphAnrede.createRun();
            runAnrede.addBreak();
            runAnrede.addBreak();
            runAnrede.setText("Sehr geehrte Damen und Herren,");
            runAnrede.addBreak();

            // Text
            runAnrede.setText("beiliegend übersende ich Ihnen ein Pflichtexemplar unserer Neuerscheinung von");
            runAnrede.addBreak();
            runAnrede.setText(Autor);
            runAnrede.addBreak();
            runAnrede.setText(Titel);
            runAnrede.addBreak();
            runAnrede.setText("ISBN: " + ISBN);
            runAnrede.addBreak();
            runAnrede.setText("Carola Hartmann Miles-Verlag, " + Jahr);
            runAnrede.addBreak();
            runAnrede.setText("zur Aufnahme in die Deutsche Nationalbibliothek.");
            runAnrede.addBreak();
            runAnrede.addBreak();

            // Schlussformel
            Bildschirmausgabe("... erzeuge Schlussformel ...");
            runAnrede.setText("Mit freundlichen Grüßen");
            runAnrede.addBreak();
            runAnrede.addBreak();
            runAnrede.addBreak();
            runAnrede.setText("Carola Hartmann");
            runAnrede.addBreak();
            runAnrede.setText("Diplom Kauffrau");

            // Schlussformel
            // create footer start
            Bildschirmausgabe("... erzeuge Fußzeile ...");
            XWPFFooter footer = headerFooterPolicy.createFooter(XWPFHeaderFooterPolicy.DEFAULT);

            XWPFParagraph paragraphFooter = footer.createParagraph();
            paragraphFooter.setAlignment(ParagraphAlignment.CENTER);
            XmlCursor cursor = paragraphFooter.getCTP().newCursor();
            XWPFTable tableFooter = footer.insertNewTbl(cursor);

            String[] strFooter1 = {"Carola Hartmann Miles Verlag", "George-Caylay-Straße 38", "14089 Berlin"};
            String[] strFooter2 = {"Dipl.Kff. Carola Hartmann", "Telefon +49 (0)30 36 28 86 77", "Volksbank Berlin"};
            String[] strFooter3 = {"StrNr 19 332 6006 5", "E-Mail miles-verlag@t-online.de", "IBAN DE61 1009 0000 2233 8320 17"};
            String[] strFooter4 = {"UStrID DE 269 369 5", "www.miles-verlag.jimdo.com", "BIC BEVODEBB"};

            XWPFTableRow tableFooterrow1 = tableFooter.createRow();
            for (int i = 0; i < 3; i++) {
                cell = tableFooterrow1.createCell();
                CTTblWidth tblWidth = cell.getCTTc().addNewTcPr().addNewTcW();
                tblWidth.setW(BigInteger.valueOf(3666));
                tblWidth.setType(STTblWidth.DXA);
                paragraphFooter = cell.getParagraphs().get(0);
                paragraphFooter.setAlignment(ParagraphAlignment.CENTER);
                XWPFRun runFooter = paragraphFooter.createRun();
                runFooter.setBold(false);
                runFooter.setFontSize(10);
                runFooter.setColor("c0c0c0");
                runFooter.setText(strFooter1[i]);
                runFooter.addBreak();
                runFooter.setText(strFooter2[i]);
                runFooter.addBreak();
                runFooter.setText(strFooter3[i]);
                runFooter.addBreak();
                runFooter.setText(strFooter4[i]);
            } // for

            // Make sure that the content stream is closed:
            Bildschirmausgabe("... Fertig! Dokument ist erzeugt");
            Bildschirmausgabe("... Dokument wird gespeichert");
            try ( // speichern
                    FileOutputStream out = new FileOutputStream(new File(outputFileName))) {
                document.write(out);
            } catch (FileNotFoundException ex) {
                ModulHelferlein.Fehlermeldung("Brief Pflichtexemplar", "FilenotFound-Exception", ex.getMessage());
            } catch (IOException ex) {
                ModulHelferlein.Fehlermeldung("Brief Pflichtexemplar", "IO-Exception", ex.getMessage());
            }

            ModulHelferlein.Infomeldung(outputFileName + " ist als DOC gespeichert!");
            Dialog.setVisible(false);
            try {
                Runtime.getRuntime().exec("cmd.exe /c " + "\"" + outputFileName + "\"");
            } catch (IOException exept) {
                ModulHelferlein.Fehlermeldung("Ausgabe Brief",
                        "IO-Exception: ", exept.getMessage());
            }// try Brief ausgeben

            String[] args = {" ", "Deutsche Nationalbibliothek", "Referat L 1.3", "Deutscher Platz 1", "04103 Leipzig", ""};
            _DlgAdresseDrucken.main(args);

        } catch (IOException ex) {
            ModulHelferlein.Fehlermeldung("Brief Pflichtexemplar", "IO-Exception", ex.getMessage());
        } catch (InvalidFormatException ex) {
            ModulHelferlein.Fehlermeldung("Brief Pflichtexemplar", "InvalidFormat-Exception", ex.getMessage());
        } // try Brief erzeugen
    } // void

    public static void briefBLBDOC(String Autor, String Titel, String ISBN, String Jahr) {

        String outputFileName = ModulHelferlein.pathBuchprojekte + "\\" + ISBN + "\\Pflichtexemplare"
                + "\\Pflichtexemplar"
                + "-"
                + ISBN
                + "-"
                + "BLB"
                + "-"
                + ModulHelferlein.printSimpleDateFormat("yyyyMMdd")
                + ".doc";

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
        dlgLabel.setText("Erzeuge " + outputFileName);
        Dialog.setVisible(true);

        Integer twipsPerInch = 1440;

        // Create a document and add a page to it
        try ( //Blank Document
                XWPFDocument document = new XWPFDocument()) {
            // create header-footer
            XWPFHeaderFooterPolicy headerFooterPolicy = document.getHeaderFooterPolicy();
            if (headerFooterPolicy == null) {
                headerFooterPolicy = document.createHeaderFooterPolicy();
            }
            CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
            CTPageMar pageMar = sectPr.addNewPgMar();
            pageMar.setLeft(BigInteger.valueOf(1152));
            pageMar.setTop(BigInteger.valueOf(288));
            pageMar.setRight(BigInteger.valueOf(1152));
            pageMar.setBottom(BigInteger.valueOf(0));
            pageMar.setFooter(BigInteger.valueOf(144));

            // create header start
            Bildschirmausgabe("... erzeuge Textblock Kopfzeile ...");
            XWPFHeader header = headerFooterPolicy.createHeader(XWPFHeaderFooterPolicy.DEFAULT);
            XWPFParagraph paragraphHeader = document.createParagraph();
            XWPFRun runHeader = paragraphHeader.createRun();
            paragraphHeader = header.createParagraph();
            paragraphHeader.setAlignment(ParagraphAlignment.CENTER);
            Path imagePath = Paths.get("header-brief.jpg");
            runHeader = paragraphHeader.createRun();
            runHeader.addPicture(Files.newInputStream(imagePath),
                    XWPFDocument.PICTURE_TYPE_PNG,
                    imagePath.getFileName().toString(), Units.toEMU(500), Units.toEMU(50));

            // Adresse
            Bildschirmausgabe("... erzeuge Textblock Adresse ...");
            XWPFTable table = document.createTable(1, 4);
            CTTblLayoutType type = table.getCTTbl().getTblPr().addNewTblLayout();
            type.setType(STTblLayoutType.FIXED);
            table.getCTTbl().getTblPr().unsetTblBorders();
            table.getCTTbl().addNewTblGrid().addNewGridCol().setW(BigInteger.valueOf(5000));
            table.getCTTbl().addNewTblGrid().addNewGridCol().setW(BigInteger.valueOf(576));
            table.getCTTbl().addNewTblGrid().addNewGridCol().setW(BigInteger.valueOf(2400));
            table.getCTTbl().addNewTblGrid().addNewGridCol().setW(BigInteger.valueOf(2400));

            XWPFTableRow rows = null;
            XWPFTableCell cell = null;
            XWPFParagraph paragraph;
            XWPFRun run;

            rows = table.getRow(0);
            cell = rows.getCell(0);
            paragraph = cell.getParagraphs().get(0);
            run = paragraph.createRun();
            run.setFontSize(9);
            run.setUnderline(UnderlinePatterns.SINGLE);
            run.setText("Carola Hartmann Miles Verlag - George-Cylay Str. 38 - 14089 Berlin     ");

            // Adresse
            paragraph = cell.addParagraph();
            run = paragraph.createRun();
            run.setFontFamily("Arial");
            run.setFontSize(12);

            run.setText("Berliner Landesbibliothek ");
            run.addBreak();
            run.addBreak();
            run.setText("Pflichtexemplarstelle");
            run.addBreak();
            run.setText("Breite Straße 30 - 36");
            run.addBreak();
            run.setText("");
            run.setText("10178 Berlin");

            cell = rows.getCell(2);
            paragraph = cell.getParagraphs().get(0);
            paragraph.setAlignment(ParagraphAlignment.RIGHT);
            run = paragraph.createRun();
            run.setFontFamily("Arial");
            run.setFontSize(12);
            run.setBold(true);
            run.setText("  ");
            run.addBreak();
            run.setBold(true);
            run.setText(" ");
            run.addBreak();
            run.setText(" ");
            run.addBreak();
            run.setText(" ");
            run.addBreak();
            run.setText("Datum: ");
            run.addBreak();
            run.addBreak();
            run.setBold(false);
            run.setText(" ");

            cell = rows.getCell(3);
            paragraph = cell.getParagraphs().get(0);
            paragraph.setAlignment(ParagraphAlignment.LEFT);
            run = paragraph.createRun();
            run.setFontFamily("Arial");
            run.setFontSize(12);
            run.setText("  ");
            run.addBreak();
            run.setText(" ");
            run.addBreak();
            run.addBreak();
            run.setText(" ");
            run.setText(" ");
            run.addBreak();
            run.setText(ModulHelferlein.printSimpleDateFormat("dd.MM.yyyy"));
            run.addBreak();
            run.addBreak();
            run.setText(" ");
            run.addBreak();
            run.setText(" ");

            // Betreff
            if (Titel.length() > 50) {
                Titel = Titel.substring(0, 49) + "...";
            }
            Bildschirmausgabe("... erzeuge Textblock Betreffzeile ...");
            XWPFTable tableBetreff = document.createTable(1, 1);
            type = tableBetreff.getCTTbl().getTblPr().addNewTblLayout();
            type.setType(STTblLayoutType.FIXED);
            tableBetreff.getCTTbl().getTblPr().unsetTblBorders();
            tableBetreff.getCTTbl().addNewTblGrid().addNewGridCol().setW(BigInteger.valueOf(9000));

            rows = tableBetreff.getRow(0);
            cell = rows.getCell(0);
            XWPFParagraph paragraphBetreff = cell.getParagraphs().get(0);
            paragraphBetreff.setAlignment(ParagraphAlignment.LEFT);
            XWPFRun runBetreff = paragraphBetreff.createRun();
            runBetreff.setFontFamily("Arial");
            runBetreff.setFontSize(12);
            runBetreff.setBold(true);
            runBetreff.setText("Pflichtexemplare der Publikationen des Carola Hartmann Miles-Verlag");
            runBetreff.addBreak();
            runBetreff.addBreak();

            // Anrede
            Bildschirmausgabe("... erzeuge Textblock Anrede ...");
            XWPFParagraph paragraphAnrede = document.createParagraph();
            paragraphAnrede.setAlignment(ParagraphAlignment.LEFT);
            XWPFRun runAnrede = paragraphAnrede.createRun();
            runAnrede.addBreak();
            runAnrede.addBreak();
            runAnrede.setText("Sehr geehrte Damen und Herren,");
            runAnrede.addBreak();

            // Text
            runAnrede.setText("beiliegend übersende ich Ihnen ein Pflichtexemplar unserer Neuerscheinung von");
            runAnrede.addBreak();
            runAnrede.setText(Autor);
            runAnrede.addBreak();
            runAnrede.setText(Titel);
            runAnrede.addBreak();
            runAnrede.setText("ISBN: " + ISBN);
            runAnrede.addBreak();
            runAnrede.setText("Carola Hartmann Miles-Verlag, " + Jahr);
            runAnrede.addBreak();
            runAnrede.setText("zur Aufnahme in die Zentral- und Landesbibliothek Berlin.");
            runAnrede.addBreak();
            runAnrede.addBreak();

            // Schlussformel
            Bildschirmausgabe("... erzeuge Schlussformel ...");
            runAnrede.setText("Mit freundlichen Grüßen");
            runAnrede.addBreak();
            runAnrede.addBreak();
            runAnrede.addBreak();
            runAnrede.setText("Carola Hartmann");
            runAnrede.addBreak();
            runAnrede.setText("Diplom Kauffrau");

            // Schlussformel
            // create footer start
            Bildschirmausgabe("... erzeuge Fußzeile ...");
            XWPFFooter footer = headerFooterPolicy.createFooter(XWPFHeaderFooterPolicy.DEFAULT);

            XWPFParagraph paragraphFooter = footer.createParagraph();
            paragraphFooter.setAlignment(ParagraphAlignment.CENTER);
            XmlCursor cursor = paragraphFooter.getCTP().newCursor();
            XWPFTable tableFooter = footer.insertNewTbl(cursor);

            String[] strFooter1 = {"Carola Hartmann Miles Verlag", "George-Caylay-Straße 38", "14089 Berlin"};
            String[] strFooter2 = {"Dipl.Kff. Carola Hartmann", "Telefon +49 (0)30 36 28 86 77", "Volksbank Berlin"};
            String[] strFooter3 = {"StrNr 19 332 6006 5", "E-Mail miles-verlag@t-online.de", "IBAN DE61 1009 0000 2233 8320 17"};
            String[] strFooter4 = {"UStrID DE 269 369 5", "www.miles-verlag.jimdo.de", "BIC BEVODEBB"};

            XWPFTableRow tableFooterrow1 = tableFooter.createRow();
            for (int i = 0; i < 3; i++) {
                cell = tableFooterrow1.createCell();
                CTTblWidth tblWidth = cell.getCTTc().addNewTcPr().addNewTcW();
                tblWidth.setW(BigInteger.valueOf(3666));
                tblWidth.setType(STTblWidth.DXA);
                paragraphFooter = cell.getParagraphs().get(0);
                paragraphFooter.setAlignment(ParagraphAlignment.CENTER);
                XWPFRun runFooter = paragraphFooter.createRun();
                runFooter.setBold(false);
                runFooter.setFontSize(10);
                runFooter.setColor("c0c0c0");
                runFooter.setText(strFooter1[i]);
                runFooter.addBreak();
                runFooter.setText(strFooter2[i]);
                runFooter.addBreak();
                runFooter.setText(strFooter3[i]);
                runFooter.addBreak();
                runFooter.setText(strFooter4[i]);
            } // for
            // Make sure that the content stream is closed:
            Bildschirmausgabe("... Fertig! Dokument ist erzeugt");
            Bildschirmausgabe("... Dokument wird gespeichert");
            try ( // speichern
                    FileOutputStream out = new FileOutputStream(new File(outputFileName))) {
                document.write(out);
            }

            ModulHelferlein.Infomeldung(outputFileName + " ist als DOC gespeichert!");
            Dialog.setVisible(false);
            try {
                Runtime.getRuntime().exec("cmd.exe /c " + outputFileName);
            } catch (IOException exept) {
                ModulHelferlein.Fehlermeldung("Anzeige des Briefes",
                        "IO-Exception: ", exept.getMessage());
            }// try Brief ausgeben

            String[] args = {"", "Berliner Landesbibliothek", "Pflichtexemplarstelle", "Breite Straße 30 - 36", "10178 Berlin", ""};
            _DlgAdresseDrucken.main(args);

        } catch (IOException ex) {
            ModulHelferlein.Fehlermeldung("Brief Pflichtexemplar", "IO-Exception", ex.getMessage());
        } catch (InvalidFormatException ex) {
            ModulHelferlein.Fehlermeldung("Brief Pflichtexemplar", "InvalidFormat-Exception", ex.getMessage());
        } // try
    }

    public static void briefDNBPDF(String Autor, String Titel, String ISBN, String Jahr) {
        try {

            String outputFileName = ModulHelferlein.pathBuchprojekte + "\\" + ISBN + "\\Pflichtexemplare"
                    + "\\Pflichtexemplar"
                    + "-"
                    + ISBN
                    + "-"
                    + "DNB"
                    + "-"
                    + ModulHelferlein.printSimpleDateFormat("yyyyMMdd")
                    + ".pdf";

            // Create a document and add a page to it
            PDDocument document = new PDDocument();
            PDPage page1 = new PDPage(A4);
            document.addPage(page1);
            // Create a new font object selecting one of the PDF base fonts
            PDFont fontPlain = PDType1Font.HELVETICA;
            PDFont fontBold = PDType1Font.HELVETICA_BOLD;
            PDFont fontItalic = PDType1Font.HELVETICA_OBLIQUE;
//        int line = 0;
// Start a new content stream which will "hold" the to be created content
            PDPageContentStream cos = new PDPageContentStream(document, page1);
//        int line = 0;
// Kopfzeile mit Bild
            try {
                BufferedImage awtImage = ImageIO.read(new File("header-brief.jpg"));
                //PDImageXObject  ximage = new PDPixelMap(document, awtImage);
                PDImageXObject pdImage = PDImageXObject.createFromFile("header-brief.jpg", document);
                float scaley = 0.5f; // alter this value to set the image size
                float scalex = 0.75f; // alter this value to set the image size
                cos.drawImage(pdImage, 55, 770, pdImage.getWidth() * scalex, pdImage.getHeight() * scaley);
                //cos.drawXObject(pdImage, 55, 770, pdImage.getWidth() * scalex, pdImage.getHeight() * scaley);
            } catch (FileNotFoundException fnfex) {
                System.out.println("No image for you");
            }
// Fu?zeile
            AusgabeLB(cos, fontBold, 10, Color.GRAY, 55, 35, "Carola Hartmann Miles - Verlag");
            AusgabeLB(cos, fontBold, 9, Color.GRAY, 55, 25, "Dipl.Kff. Carola Hartmann");
            AusgabeLB(cos, fontBold, 9, Color.GRAY, 55, 15, "Steuernr.: 19 332 6006 5");
            AusgabeLB(cos, fontBold, 9, Color.GRAY, 55, 5, "USt-IDNr: DE 269 369 280");
            AusgabeLB(cos, fontBold, 10, Color.GRAY, 230, 35, ModulHelferlein.CheckStr("George-Caylay-Straße 38"));
            AusgabeLB(cos, fontBold, 9, Color.GRAY, 230, 25, "Telefon: +49 (0)30 36 28 86 77");
            AusgabeLB(cos, fontBold, 9, Color.GRAY, 230, 15, "e-Mail: miles-verlag@t-online.de");
            AusgabeLB(cos, fontBold, 9, Color.GRAY, 230, 5, "Internet: www.miles-verlag.jimdo.com");
            AusgabeLB(cos, fontBold, 10, Color.GRAY, 400, 35, "14089 Berlin");
            AusgabeLB(cos, fontBold, 9, Color.GRAY, 400, 25, "Volksbank Berlin");
            AusgabeLB(cos, fontBold, 9, Color.GRAY, 400, 15, "IBAN: DE61 1009 0000 2233 8320 17");
            AusgabeLB(cos, fontBold, 9, Color.GRAY, 400, 5, "BIC: BEVODEBB");
// Faltmarke, Lochmarke, Faltmarke
                        Linie(cos,1,0,595,15,595);
                        Linie(cos,1,0,415,25,415);
                        Linie(cos,1,0,285,15,285);
// Absenderzeile
            Linie(cos,1,50, 749, 297, 749);
            AusgabeLB(cos, fontPlain, 8, Color.BLACK, 50, 751, ModulHelferlein.CheckStr("C. Hartmann Miles-Verlag - George-Caylay-Straße 38 - 14089 Berlin"));
// Datum
            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 354, 655, "Datum: " + ModulHelferlein.printSimpleDateFormat("dd.MM.yyyy"));
// Adresse
            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 730, "Deutsche Nationalbibliothek ");
            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 715, "Pflichtexemplarstelle");
            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 700, "Referat L 1.3");
            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 685, "Deutscher Platz 1");
            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 655, "04103 Leipzig");
// Betreff
            AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 575, "Pflichtexemplare der Publikationen des Carola Hartmann Miles-Verlag");
// Anrede
            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 540, "Sehr geehrte Damen und Herren,");
// Text
            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 510, ModulHelferlein.CheckStr("beiliegend übersende ich Ihnen zwei Pflichtexemplare unserer Neuerscheinung von"));
            AusgabeLB(cos, fontItalic, 12, Color.BLACK, 100, 490, Autor);
            AusgabeLB(cos, fontItalic, 12, Color.BLACK, 100, 475, Titel);
            AusgabeLB(cos, fontItalic, 12, Color.BLACK, 100, 460, "ISBN: " + ModulHelferlein.makeISBN13(ISBN));
            AusgabeLB(cos, fontItalic, 12, Color.BLACK, 100, 445, "Carola Hartmann Miles-Verlag, " + Jahr);
            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 425, "zur Aufnahme in die Deutsche Nationalbibliothek.");
// Schlussformel
            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 390, ModulHelferlein.CheckStr("Mit freundlichen Grüßen"));
            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 330, "Carola Hartmann");
            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 315, "Diplom Kauffrau");
// Save the results and ensure that the document is properly closed:
// Make sure that the content stream is closed:
            cos.close();

// Save the results and ensure that the document is properly closed:
            document.save(outputFileName);
            document.close();

            ModulHelferlein.Infomeldung("Brief an die Deutsche Nationalbibliothek ist als PDF gespeichert!");
            try {
                Runtime.getRuntime().exec("cmd.exe /c " + "\"" + outputFileName + "\"");
            } catch (IOException exept) {
                ModulHelferlein.Fehlermeldung(
                        "Ausgabe Brief", "IO-Exception: ", exept.getMessage());
            }// try Brief ausgeben

            String[] args = {" ", "Deutsche Nationalbibliothek", "Referat L 1.3", "Deutscher Platz 1", "04103 Leipzig", ""};
            _DlgAdresseDrucken.main(args);

        } // void briefDNB // void briefDNB // void briefDNB // void briefDNB
        catch (IOException ex) {
            ModulHelferlein.Fehlermeldung("Brief Pflichtexemplar", "IO-Exception", ex.getMessage());
        }

    }

    public static void briefBLBPDF(String Autor, String Titel, String ISBN, String Jahr) {
        try {
            //helferlein.Infomeldung(Autor + "-"+Titel+"-"+ISBN+"-"+Jahr);

            String outputFileName = ModulHelferlein.pathBuchprojekte + "\\" + ISBN + "\\Pflichtexemplare"
                    + "\\Pflichtexemplar"
                    + "-"
                    + ISBN
                    + "-"
                    + "BLB"
                    + "-"
                    + ModulHelferlein.printSimpleDateFormat("yyyyMMdd")
                    + ".pdf";

            // Create a document and add a page to it
            PDDocument document = new PDDocument();
            PDPage page1 = new PDPage(A4);
            document.addPage(page1);
            // Create a new font object selecting one of the PDF base fonts
            PDFont fontPlain = PDType1Font.HELVETICA;
            PDFont fontBold = PDType1Font.HELVETICA_BOLD;
            PDFont fontItalic = PDType1Font.HELVETICA_OBLIQUE;
//        int line = 0;
// Start a new content stream which will "hold" the to be created content
            PDPageContentStream cos = new PDPageContentStream(document, page1);
//        int line = 0;
// Kopfzeile mit Bild
            try {
                BufferedImage awtImage = ImageIO.read(new File("header-brief.jpg"));
                //PDImageXObject  ximage = new PDPixelMap(document, awtImage);
                PDImageXObject pdImage = PDImageXObject.createFromFile("header-brief.jpg", document);
                float scaley = 0.5f; // alter this value to set the image size
                float scalex = 0.75f; // alter this value to set the image size
                cos.drawImage(pdImage, 55, 770, pdImage.getWidth() * scalex, pdImage.getHeight() * scaley);
                //cos.drawXObject(pdImage, 55, 770, pdImage.getWidth() * scalex, pdImage.getHeight() * scaley);
            } catch (FileNotFoundException fnfex) {
                System.out.println("No image for you");
            }
// Fu?zeile
            AusgabeLB(cos, fontBold, 10, Color.GRAY, 55, 35, "Carola Hartmann Miles - Verlag");
            AusgabeLB(cos, fontBold, 9, Color.GRAY, 55, 25, "Dipl.Kff. Carola Hartmann");
            AusgabeLB(cos, fontBold, 9, Color.GRAY, 55, 15, "Steuernr.: 19 332 6006 5");
            AusgabeLB(cos, fontBold, 9, Color.GRAY, 55, 5, "USt-IDNr: DE 269 369 280");
            AusgabeLB(cos, fontBold, 10, Color.GRAY, 230, 35, ModulHelferlein.CheckStr("George-Caylay-Straße 38"));
            AusgabeLB(cos, fontBold, 9, Color.GRAY, 230, 25, "Telefon: +49 (0)30 36 28 86 77");
            AusgabeLB(cos, fontBold, 9, Color.GRAY, 230, 15, "e-Mail: miles-verlag@t-online.de");
            AusgabeLB(cos, fontBold, 9, Color.GRAY, 230, 5, "Internet: www.miles-verlag.jimdo.com");
            AusgabeLB(cos, fontBold, 10, Color.GRAY, 400, 35, "14089 Berlin");
            AusgabeLB(cos, fontBold, 9, Color.GRAY, 400, 25, "Volksbank Berlin");
            AusgabeLB(cos, fontBold, 9, Color.GRAY, 400, 15, "IBAN: DE61 1009 0000 2233 8320 17");
            AusgabeLB(cos, fontBold, 9, Color.GRAY, 400, 5, "BIC: BEVODEBB");
// Faltmarke, Lochmarke, Faltmarke
                        Linie(cos,1,0,595,15,595);
                        Linie(cos,1,0,415,25,415);
                        Linie(cos,1,0,285,15,285);
// Absenderzeile
            Linie(cos,1,50, 749, 297, 749);
            AusgabeLB(cos, fontPlain, 8, Color.BLACK, 50, 751, ModulHelferlein.CheckStr("C. Hartmann Miles-Verlag - George-Caylay-Straße 38 - 14089 Berlin"));
// Datum
            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 354, 655, "Datum: " + ModulHelferlein.printSimpleDateFormat("dd.MM.yyyy"));
// Adresse
            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 730, "Zentral- und Landesbibliothek Berlin");
            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 700, "Pflichtexemplarstelle");
            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 685, ModulHelferlein.CheckStr("Breite Straße 30 - 36"));
            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 655, "10178 Berlin");
// Betreff
            AusgabeLB(cos, fontBold, 12, Color.BLACK, 55, 575, "Pflichtexemplare der Publikationen des Carola Hartmann Miles-Verlag");
// Anrede
            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 540, "Sehr geehrte Damen und Herren,");
// Text
            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 510, ModulHelferlein.CheckStr("beiliegend übersende ich Ihnen ein Pflichtexemplar unserer Neuerscheinung von"));

            AusgabeLB(cos, fontItalic, 12, Color.BLACK, 100, 490, Autor);
            AusgabeLB(cos, fontItalic, 12, Color.BLACK, 100, 475, Titel);
            AusgabeLB(cos, fontItalic, 12, Color.BLACK, 100, 460, "ISBN: " + ModulHelferlein.makeISBN13(ISBN));
            AusgabeLB(cos, fontItalic, 12, Color.BLACK, 100, 445, "Carola Hartmann Miles-Verlag, " + Jahr);
            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 425, "zur Aufnahme in die Zentral- und Landesbibliothek Berlin.");
// Schlussformel
            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 390, ModulHelferlein.CheckStr("Mit freundlichen Grüßen"));
            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 330, "Carola Hartmann");
            AusgabeLB(cos, fontPlain, 12, Color.BLACK, 55, 315, "Diplom Kauffrau");
// Save the results and ensure that the document is properly closed:
// Make sure that the content stream is closed:
            cos.close();

// Save the results and ensure that the document is properly closed:
            document.save(outputFileName);
            document.close();

            ModulHelferlein.Infomeldung("Brief an die Zentral- und Landesbibliothek Berlin ist als PDF gespeichert!");
            try {
                Runtime.getRuntime().exec("cmd.exe /c " + "\"" + outputFileName + "\"");
            } catch (IOException exept) {
                ModulHelferlein.Fehlermeldung(
                        "Ausgabe Brief: Exception: " + exept.getMessage());
            }// try Brief ausgeben

            String[] args = {"", "Berliner Landesbibliothek", "Pflichtexemplarstelle", "Breite Straße 30 - 36", "10178 Berlin", ""};
            _DlgAdresseDrucken.main(args);

        } catch (IOException ex) {
            ModulHelferlein.Fehlermeldung("Brief Pflichtexemplar", "IO-Exception", ex.getMessage());
        }

    }
} // clas
