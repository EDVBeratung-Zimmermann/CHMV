/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package milesVerlagMain;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.beans.*;
import java.io.File;
import java.util.Random;
import javax.swing.filechooser.FileNameExtensionFilter;
 
public class ModulAbrechnungHonorarProgressBar extends JPanel
                             implements ActionListener, 
                                        PropertyChangeListener {
 
    private static JFrame frame = new JFrame("Abrechnung ...");
    private JProgressBar progressBar;
    private JButton startButton;
    private JTextArea taskOutput;
    private Task task;
 
    class Task extends SwingWorker<Void, Void> {
        /*
         * Main task. Executed in background thread.
         */
        @Override
        public Void doInBackground() {
            Random random = new Random();
            int progress = 0;
            //Initialize progress property.
            setProgress(0);
            while (progress < 100) {
                //Sleep for up to one second.
                try {
                    Thread.sleep(random.nextInt(100));
                } catch (InterruptedException ignore) {}
                
                //Make random progress.
                progress += random.nextInt(10);
                setProgress(Math.min(progress, 100));
            }
            return null;
        }
 
        /*
         * Executed in event dispatching thread
         */
        @Override
        public void done() {
            Toolkit.getDefaultToolkit().beep();
            startButton.setEnabled(true);
            setCursor(null); //turn off the wait cursor
            taskOutput.append("Done!\n");
            
            frame.setVisible(false);
        }
    }
 
    public ModulAbrechnungHonorarProgressBar() {
        super(new BorderLayout());
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV-Datei", "csv");
        JFileChooser chooser = new JFileChooser(new File(System.getProperty("user.dir")));
 
        //Create the demo's UI.
        
        startButton = new JButton("Start");
        startButton.setActionCommand("start");
        startButton.addActionListener(this);
 
        progressBar = new JProgressBar(0, 700);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
 
        taskOutput = new JTextArea(5, 20);
        taskOutput.setMargin(new Insets(5,5,5,5));
        taskOutput.setEditable(false);
 
        JPanel panel = new JPanel();
        panel.add(startButton);
        panel.add(progressBar);
 
        add(panel, BorderLayout.PAGE_START);
        add(new JScrollPane(taskOutput), BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        chooser.setMultiSelectionEnabled(false);
        chooser.setFileFilter(filter);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int Ergebnis = chooser.showDialog(null, "SALES.CSV wählen");

        if (Ergebnis == JFileChooser.APPROVE_OPTION) {
            task = new Task();
            task.addPropertyChangeListener(this);
            task.execute();
        }

    }
 
    /**
     * Invoked when the user presses the start button.
     */
    public void actionPerformed(ActionEvent evt) {
        startButton.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        //Instances of javax.swing.SwingWorker are not reusuable, so
        //we create new instances as needed.
        task = new Task();
        task.addPropertyChangeListener(this);
        task.execute();
    }
 
    /**
     * Invoked when task's progress property changes.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress" == evt.getPropertyName()) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
            //taskOutput.append(String.format("Completed %d%% of task.\n", task.getProgress()));
        } 
    }
 
 
    /**
     * Create the GUI and show it. As with all GUI code, this must run
     * on the event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        frame.setSize(600,100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        //Create and set up the content pane.
        JComponent newContentPane = new ModulAbrechnungHonorarProgressBar();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);
 
        //Display the window.
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    private static void hideGUI(){
        frame.setVisible(false);
    }
 
    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
