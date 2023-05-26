import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;

public class ComputerGUI extends JFrame {
    private JTextArea outputTextArea;

    public ComputerGUI() {
        // Set up the main window
        setTitle("Who Needs MARS ? ");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null); // Center the window
        setLayout(new BorderLayout());
        setResizable(false);

        // Create a panel for input components
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        inputPanel.setBackground(new Color(240, 240, 240));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20));

        // Create a "Select File" button
        JButton selectFileButton = new JButton("Select a File to run (txt) :");
        selectFileButton.setFont(new Font("Arial", Font.BOLD, 14));
        selectFileButton.setPreferredSize(new Dimension(220, 30));
        selectFileButton.addActionListener(new SelectFileButtonListener());

        // Add the button to the input panel
        inputPanel.add(selectFileButton);

        // Create a scrollable text area for output
        outputTextArea = new JTextArea();
        outputTextArea.setFont(new Font("Arial", Font.PLAIN, 14));
        outputTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputTextArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        // Add components to the main window
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private class SelectFileButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt")); // Add file filter
            int returnValue = fileChooser.showOpenDialog(ComputerGUI.this);
    
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                String filePath = selectedFile.getAbsolutePath();
    
                // Copy the selected file to the project directory
                String destinationFilePath = "./" + selectedFile.getName();
                try {
                    Files.copy(selectedFile.toPath(), Paths.get(destinationFilePath), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
    
                Computer computer = new Computer();
    
                // Redirect System.out to the text area
                PrintStream originalOut = System.out;
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                PrintStream customOut = new PrintStream(outputStream);
                System.setOut(customOut);
    
                // Call the run() method from your Computer class
                computer.run(destinationFilePath);
    
                // Restore System.out and update the text area
                System.setOut(originalOut);
                outputTextArea.setText(outputStream.toString());
    
                // Show the text area
                outputTextArea.setCaretPosition(0); // Scroll to the top
                outputTextArea.setVisible(true);
               // selectFileButton.setVisible(false);
            }
        }
    }
    

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ComputerGUI gui = new ComputerGUI();
                gui.setVisible(true);
            }
        });
    }
}
