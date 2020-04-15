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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.Insets;
import java.io.PrintStream;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * @author Thomas Zimmermann
 *
 * Erstellt ein Ausgabefenster für Debug-Informationen. Der Fensterinhalt kann
 * wahlweise in die Zwischenablage kopiert oder Mail versendet werden.
 */
public class ModulAusgabe extends JFrame {

    /**
     *
     * @param args sind null
     *
     * Erzeugt ein neues Ausgabefenster
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ModulAusgabe().setVisible(true);
        });
    }

    /**
     * Generiert das Ausgabefenster
     */
    public ModulAusgabe() {
        super("Carola Hartmann Miles Verlag");
        
        try {
            initComponents();
            //PrintStream printStream = new PrintStream(new CustomOutputStream(AusgabeTextArea));
            try {
                Font f = Font.createFont( Font.TRUETYPE_FONT, new FileInputStream("FreeMonoBold.ttf") );
                Font font = f.deriveFont( 12f );
                AusgabeTextArea.setFont(font);
            } catch (FileNotFoundException ex) {
                System.out.println("Modul Ausgabe: FileNotFoundException"+ex.getMessage());
            } catch (FontFormatException ex) {
                System.out.println("Modul Ausgabe: FontFormatException"+ex.getMessage());
            } catch (IOException ex) {
                System.out.println("Modul Ausgabe: IOException"+ex.getMessage());
            }
            PrintStream printStream;
            printStream = new PrintStream(new CustomOutputStream(AusgabeTextArea), true, "CP1252");
            //printStream = new PrintStream(new CustomOutputStream(AusgabeTextArea), true, "CP850");
            //printStream = new PrintStream(new CustomOutputStream(AusgabeTextArea), true, StandardCharsets.ISO_8859_1);
            //printStream = new PrintStream(new CustomOutputStream(AusgabeTextArea), true, StandardCharsets.UTF_8);
            // keeps reference of standard output stream
            standardOut = System.out;
            // re-assigns standard output stream and error output stream
            System.setOut(printStream);
            System.setErr(printStream);
        } catch (UnsupportedEncodingException ex) {
            System.out.println("Modul Ausgabe: UnsupportedEncodingException"+ex.getMessage());
        }
        
    }

    private void buttonMailActionPerformed(ActionEvent e) {
        // TODO add your code here

        // TODO add your handling code here:
        System.out.println("TLSEmail Start");

        // change accordingly 
        String host = ModulHelferlein.MailHost;
        System.out.println("- host     " + host);

        String port = ModulHelferlein.MailPort;
        System.out.println("- port     " + port);

        String IMAPhost = ModulHelferlein.MailIMAPHost;
        System.out.println("- host     " + IMAPhost);

        String IMAPport = ModulHelferlein.MailIMAPPort;
        System.out.println("- port     " + IMAPport);

        String username = ModulHelferlein.MailUser;
        System.out.println("- username " + username);

        String password = ModulHelferlein.MailPass;
        System.out.println("- password " + password);

        String from = username;
        System.out.println("- from     " + from);

        String to = "kontakt@edv-beratung.familiezimmermann.de";
        System.out.println("- to       " + to);

        String record = ModulHelferlein.MailIMAPGesendet;   //"Sent Items"

        // Get the session object 
        // Get system properties 
        Properties properties = System.getProperties();

        // Setup mail server 
        properties.setProperty("mail.smtp.host", host);
        properties.setProperty("mail.imap.host", IMAPhost);

        // SSL Port 
        properties.put("mail.smtp.port", port);
        properties.put("mail.imap.port", IMAPport);

        // enable authentication 
        properties.put("mail.smtp.auth", "true");
        //properties.put("mail.imap.auth", "true");

        // SSL Factory 
        properties.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");

        properties.put("mail.imap.starttls.enable", "true");

        properties.put("mail.transport.protocol", "smtp");
        //properties.put("mail.store.protocol", "imap");

        // creating Session instance referenced to  
        // Authenticator object to pass in  
        // Session.getInstance argument 
        Session session = Session.getDefaultInstance(properties,
                new javax.mail.Authenticator() {

            // override the getPasswordAuthentication  
            // method 
            protected PasswordAuthentication
                    getPasswordAuthentication() {
                return new PasswordAuthentication(username,
                        password);
            }
        });

        //compose the message 
        try {
            // javax.mail.internet.MimeMessage class is mostly  
            // used for abstraction. 
            MimeMessage message = new MimeMessage(session);

            // header field of the header. 
            message.setFrom(new InternetAddress(from));

            //InternetAddress[] addresses = new InternetAddress[2];
            //addresses[0] = new InternetAddress(to);
            //addresses[1] = new InternetAddress(from);            
            //message.setRecipients(Message.RecipientType.TO, addresses);
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));

            // Set Subject: header field
            message.setSubject("Log-Daten vom " + ModulHelferlein.CurDate);

            // Create a multipar message
            Multipart multipart = new MimeMultipart();

            // Create the message part
            BodyPart messageBodyPart = new MimeBodyPart();

            // Fill the message
            String text = AusgabeTextArea.getText();

            // Set text message part
            messageBodyPart.setText(text);
            multipart.addBodyPart(messageBodyPart);

            // Send the complete message parts
            message.setContent(multipart);

            //message.setSubject("Ihre Bestellung vom " + Datum);
            //message.setText("Hello, aas is sending email ");
            // Send message 
            Transport.send(message);
            ModulHelferlein.Infomeldung("Mail wurde gesendet");
            System.out.println("Mail wurde gesendet.");

            // Message im Ordner gesendet - sent ablegen
            /*
	     * Save a copy of the message
             */
            // get a store object
            Store store = session.getStore("imaps");
            store.connect(IMAPhost, username, password);

            Folder folder = store.getFolder(record);
            if (folder == null) {
                System.err.println("Finde den gesendet-Ordner nicht.");
                ModulHelferlein.Fehlermeldung("Finde den gesendet-Ordner nicht!");
                //System.exit(1);
            } else {
                Message[] msgs = new Message[1];
                msgs[0] = message;
                folder.appendMessages(msgs);

                ModulHelferlein.Infomeldung("Mail wurde im Ordner '" + ModulHelferlein.MailIMAPGesendet + "' gespeichert");
                System.out.println("Mail was recorded successfully.");
            }

        } catch (MessagingException mex) {
            ModulHelferlein.Fehlermeldung("E-Mail-versenden", mex.getMessage());
        }
    }

    private void buttonCopyActionPerformed(ActionEvent e) {
        // TODO add your code here
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                new StringSelection(AusgabeTextArea.getText()), null);
    }

    /**
     * Schließt das Ausgabefenster und speichert ggf. die Log-Datei
     */
    public static void AusgabeSchliessen() {
        // TODO add your code here
        // TODO add your code here
        String message = "Soll die Log-Datei gespeichert werden?";
        String title = "Wirklich speichern?";
        // display the JOptionPane showConfirmDialog
        int reply = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION);
        if (reply == JOptionPane.YES_OPTION) {
            String Filename = ModulHelferlein.pathSicherung
                    + "\\"
                    + "miles-verlag-"
                    + ModulHelferlein.printSimpleDateFormat("yyyyMMdd") + ".log";
            PrintWriter pWriter = null;
            try {
                pWriter = new PrintWriter(new BufferedWriter(new FileWriter(Filename)));
                pWriter.println(AusgabeTextArea.getText());
            } catch (IOException ioe) {
                JOptionPane.showMessageDialog(null, ioe.getMessage(), "Achtung Fehler", JOptionPane.WARNING_MESSAGE);
            } finally {
                if (pWriter != null) {
                    pWriter.flush();
                    pWriter.close();
                } // if
            } // finally
        } // if
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        scrollPane1 = new JScrollPane();
        AusgabeTextArea = new JTextArea();
        panel1 = new JPanel();
        button2 = new JButton();
        button3 = new JButton();
        vSpacer1 = new JPanel(null);

        //======== this ========
        setTitle("Carola Hartmann Miles Verlag");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setAutoRequestFocus(false);
        setResizable(false);
        setUndecorated(true);
        setIconImage(new ImageIcon(getClass().getResource("/milesVerlagMain/CarolaHartmannMilesVerlag.png")).getImage());
        var contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(null);

                //======== scrollPane1 ========
                {
                    scrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

                    //---- AusgabeTextArea ----
                    AusgabeTextArea.setFont(new Font("Courier New", Font.BOLD, 11));
                    AusgabeTextArea.setLineWrap(true);
                    AusgabeTextArea.setWrapStyleWord(true);
                    AusgabeTextArea.setColumns(80);
                    AusgabeTextArea.setRows(25);
                    scrollPane1.setViewportView(AusgabeTextArea);
                }
                contentPanel.add(scrollPane1);
                scrollPane1.setBounds(0, 0, 590, 250);

                {
                    // compute preferred size
                    Dimension preferredSize = new Dimension();
                    for(int i = 0; i < contentPanel.getComponentCount(); i++) {
                        Rectangle bounds = contentPanel.getComponent(i).getBounds();
                        preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                        preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                    }
                    Insets insets = contentPanel.getInsets();
                    preferredSize.width += insets.right;
                    preferredSize.height += insets.bottom;
                    contentPanel.setMinimumSize(preferredSize);
                    contentPanel.setPreferredSize(preferredSize);
                }
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);
        }
        contentPane.add(dialogPane, BorderLayout.PAGE_START);

        //======== panel1 ========
        {
            panel1.setLayout(null);

            //---- button2 ----
            button2.setText("Copy");
            button2.addActionListener(e -> buttonCopyActionPerformed(e));
            panel1.add(button2);
            button2.setBounds(20, 10, 70, button2.getPreferredSize().height);

            //---- button3 ----
            button3.setText("Mail");
            button3.addActionListener(e -> buttonMailActionPerformed(e));
            panel1.add(button3);
            button3.setBounds(105, 10, 70, button3.getPreferredSize().height);
            panel1.add(vSpacer1);
            vSpacer1.setBounds(40, 35, 30, 10);

            {
                // compute preferred size
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
        contentPane.add(panel1, BorderLayout.CENTER);
        pack();
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private static JPanel dialogPane;
    private static JPanel contentPanel;
    private static JScrollPane scrollPane1;
    public static JTextArea AusgabeTextArea;
    private JPanel panel1;
    private JButton button2;
    private JButton button3;
    private JPanel vSpacer1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
    private PrintStream standardOut;
}
