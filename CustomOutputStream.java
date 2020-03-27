/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package milesVerlagMain;

import java.io.IOException;
import java.io.OutputStream;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 * This class extends from OutputStream to redirect output to a JTextArrea
 *
 * @author www.codejava.net
 *
 */
public class CustomOutputStream extends OutputStream {

    private JTextArea textArea;

    /**
     *
     * @param textArea Textarea, in den der Outputstream schreibt.
     */
    public CustomOutputStream(JTextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    //
    public void write(int b) throws IOException {
        // redirects data to the text area
        textArea.append(String.valueOf((char) b));
        // scrolls the text area to the end of data
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }
    
    //public void write(int b) throws IOException {
    //    SwingUtilities.invokeLater(new Runnable(){ 
    //        public void run(){
    //            textArea.append(String.valueOf((char) b));
    //            textArea.setCaretPosition(textArea.getDocument().getLength()); } }
    //    );
    //}

}
