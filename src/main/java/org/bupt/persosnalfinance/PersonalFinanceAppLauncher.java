package org.bupt.persosnalfinance;

import org.bupt.persosnalfinance.Front.HomePage.HomePage;

import javax.swing.SwingUtilities;

/**
 * Launcher class to start the Personal Finance Manager application.
 *
 * @author Xuerui Dong
 */
public class PersonalFinanceAppLauncher {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new HomePage().setVisible(true);
        });
    }
}
