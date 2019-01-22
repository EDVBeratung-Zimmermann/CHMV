/*
 * Created by JFormDesigner on Wed Jan 16 21:54:58 CET 2019
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
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
 */
public class ModulAusgabe extends JFrame {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ModulAusgabe().setVisible(true);
        });
    }

    public ModulAusgabe() {
        super("Carola Hartmann Miles Verlag");
        initComponents();
        PrintStream printStream = new PrintStream(new CustomOutputStream(AusgabeTextArea));

        // keeps reference of standard output stream
        standardOut = System.out;

        // re-assigns standard output stream and error output stream
        System.setOut(printStream);
        System.setErr(printStream);
    }

    private void buttonMailActionPerformed(ActionEvent e) {
        // TODO add your code here

        // TODO add your handling code here:
        System.out.println("TLSEmail Start");

        // change accordingly 
        String host = Modulhelferlein.MailHost;
        System.out.println("- host     " + host);

        String port = Modulhelferlein.MailPort;
        System.out.println("- port     " + port);

        String IMAPhost = Modulhelferlein.MailIMAPHost;
        System.out.println("- host     " + IMAPhost);

        String IMAPport = Modulhelferlein.MailIMAPPort;
        System.out.println("- port     " + IMAPport);

        String username = Modulhelferlein.MailUser;
        System.out.println("- username " + username);

        String password = Modulhelferlein.MailPass;
        System.out.println("- password " + password);

        String from = "miles-verlag@t-online.de";
        System.out.println("- from     " + from);

        String to = "kontakt@edv-beratung.familiezimmermann.de";
        System.out.println("- to       " + to);

        String record = Modulhelferlein.MailIMAPGesendet;   //"Sent Items"

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
            message.setSubject("Log-Daten vom " + Modulhelferlein.CurDate);

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
            Modulhelferlein.Infomeldung("Mail wurde gesendet");
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
                Modulhelferlein.Fehlermeldung("Finde den gesendet-Ordner nicht!");
                //System.exit(1);
            } else {
                Message[] msgs = new Message[1];
                msgs[0] = message;
                folder.appendMessages(msgs);

                Modulhelferlein.Infomeldung("Mail wurde im Ordner '" + Modulhelferlein.MailIMAPGesendet + "' gespeichert");
                System.out.println("Mail was recorded successfully.");
            }

        } catch (MessagingException mex) {
            Modulhelferlein.Fehlermeldung("E-Mail-versenden", mex.getMessage());
        }
    }

    private void buttonCopyActionPerformed(ActionEvent e) {
        // TODO add your code here
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                new StringSelection(AusgabeTextArea.getText()), null);
    }

    private void thisWindowClosed(WindowEvent e) {
    }

    private void thisWindowClosing(WindowEvent e) {
        // TODO add your code here
        // TODO add your code here
        String message = "Soll die Log-Datei gespeichert werden?";
        String title = "Wirklich speichern?";
        // display the JOptionPane showConfirmDialog
        int reply = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION);
        if (reply == JOptionPane.YES_OPTION) {
            String Filename = Modulhelferlein.pathSicherung
                    + "\""
                    + "\\"
                    + "miles-verlag.log-"
                    + Modulhelferlein.printSimpleDateFormat("yyyyMMdd") + ".log";
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
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                thisWindowClosing(e);
            }
        });
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

                { // compute preferred size
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
        contentPane.add(panel1, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
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
