package org.bupt.persosnalfinance.Front.HomePage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;

import org.bupt.persosnalfinance.Front.Dashboard.Dashboard;
import org.bupt.persosnalfinance.Front.importcsv.ImportCSVPanel;

public class HomePage extends JFrame {

    public HomePage() {
        setTitle("Welcome to Personal Finance Manager");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 主面板设置
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JLabel welcomeLabel = new JLabel("Welcome to Personal Finance Manager", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(welcomeLabel);

        JButton createButton = new JButton("Create New Book");
        createButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String filePath = "src/main/data/transactionInformation.json";
                    FileWriter fileWriter = new FileWriter(filePath);
                    fileWriter.write(""); // 写入空JSON对象
                    fileWriter.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    // 可以根据需要添加错误处理逻辑
                }
                Dashboard dashboard = new Dashboard();
                dashboard.setVisible(true);
                dispose(); // 关闭 HomePage 窗口
            }
        });

        JButton importButton = new JButton("Import CSV File");
        importButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Create a new window (JFrame or JDialog)
                JDialog importDialog = new JDialog();
                importDialog.setTitle("Import CSV");

                // Add your ImportCSVPanel to it
                ImportCSVPanel importPanel = new ImportCSVPanel();
                importDialog.add(importPanel);

                // Configure and show the window
                importDialog.pack();
                importDialog.setLocationRelativeTo(null); // Center on screen
                importDialog.setModal(true); // Make it modal if desired
                importDialog.setVisible(true);

            }
        });

        panel.add(createButton);
        panel.add(importButton);

        add(panel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new HomePage().setVisible(true);
        });
    }
}
