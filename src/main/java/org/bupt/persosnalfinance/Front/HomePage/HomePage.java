package org.bupt.persosnalfinance.Front.HomePage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;

import org.bupt.persosnalfinance.Front.Dashboard.Dashboard;
import org.bupt.persosnalfinance.Front.importcsv.ImportCSVPanel;
import org.bupt.persosnalfinance.Util.ImportSuccessListener;

/**
 * The HomePage class represents the entry point of the Personal Finance Manager application.
 * It allows the user to either create a new transaction book or import an existing CSV file.
 * After selecting an option, the user is redirected to the main dashboard interface.
 *
 * @author Xuerui Dong
 */
public class HomePage extends JFrame {

    /**
     * Constructs the HomePage window with welcome text and buttons
     * to create a new financial book or import an existing CSV file.
     */
    public HomePage() {
        setTitle("Welcome to Personal Finance Manager");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel setup
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JLabel welcomeLabel = new JLabel("Welcome to Personal Finance Manager", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(welcomeLabel);

        // Button to create a new transaction book
        JButton createButton = new JButton("Create New Book");
        createButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String filePath = "src/main/data/transactionInformation.json";
                    FileWriter fileWriter = new FileWriter(filePath);
                    fileWriter.write(""); // Write an empty JSON object
                    fileWriter.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    // Error handling can be added here if needed
                }
                Dashboard dashboard = new Dashboard();
                dashboard.setVisible(true);
                dispose(); // Close the HomePage window
            }
        });

        // Button to import a CSV file
        JButton importButton = new JButton("Import CSV File");
        importButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Create a new dialog window
                JDialog importDialog = new JDialog();
                importDialog.setTitle("Import CSV");

                // Add the ImportCSVPanel to the dialog
                ImportCSVPanel importPanel = new ImportCSVPanel();
                importDialog.add(importPanel);

                // Set a listener to trigger after successful import
                importPanel.setImportSuccessListener(() -> {
                    dispose();  // Close HomePage
                    new Dashboard().setVisible(true);  // Open Dashboard
                });

                // Configure and show the import dialog
                importDialog.pack();
                importDialog.setLocationRelativeTo(null); // Center on screen
                importDialog.setModal(true); // Modal window
                importDialog.setVisible(true);
            }
        });

        panel.add(createButton);
        panel.add(importButton);

        add(panel);
    }

    /**
     * The main method to launch the HomePage GUI.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new HomePage().setVisible(true);
        });
    }
}
