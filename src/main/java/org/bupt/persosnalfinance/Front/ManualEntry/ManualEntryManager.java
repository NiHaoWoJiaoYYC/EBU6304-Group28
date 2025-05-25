package org.bupt.persosnalfinance.Front.ManualEntry;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Manages the display and reuse of the manual transaction entry window.
 * <p>
 * This class ensures that only one instance of the manual entry window is shown at a time.
 * If the window already exists and is still open, it is brought to the front.
 * Otherwise, a new instance of the manual entry window is created and initialized.
 * </p>
 *
 * @author Xuerui Dong
 */
public class ManualEntryManager {

    /**
     * The current instance of the manual entry window, if it is open.
     */
    private static JFrame currentEntryWindow;

    /**
     * Launches the manual entry window in a separate thread.
     * <p>
     * If an existing window is open, it is brought to the front. Otherwise, a new instance
     * of the manual entry window is launched using {@link ManualEntry#main(String[])}.
     * The window is then tracked and a custom window close listener is attached.
     * If a "View Existing Transactions" button is found, its action listener is modified
     * to invoke {@link TransactionListManager#showTransactionList()}.
     * </p>
     */
    public static synchronized void showManualEntry() {
        // If the window already exists and is not closed, bring it to the front
        if (currentEntryWindow != null && currentEntryWindow.isDisplayable()) {
            currentEntryWindow.toFront();
            return;
        }

        // Launch a new window in a separate thread
        new Thread(() -> {
            ManualEntry.main(new String[]{});

            // Wait for the window to be initialized
            try { Thread.sleep(300); } catch (InterruptedException e) {}

            // Find and manage the newly created window
            Window[] windows = Window.getWindows();
            for (Window window : windows) {
                if (window instanceof JFrame &&
                        "Transaction information > Manual Entry".equals(((JFrame)window).getTitle())) {

                    currentEntryWindow = (JFrame)window;
                    currentEntryWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                    // Add a window close listener to reset the reference
                    currentEntryWindow.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosed(WindowEvent e) {
                            currentEntryWindow = null;
                        }
                    });

                    // Find and modify "View Existing Transactions" button if it exists
                    findAndModifyViewButton(currentEntryWindow);
                }
            }
        }).start();
    }

    /**
     * Recursively searches for a button labeled "View Existing Transactions"
     * within the specified container and replaces its action listener.
     *
     * @param container the container to search within
     */
    private static void findAndModifyViewButton(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JButton && "View Existing Transactions".equals(((JButton)comp).getText())) {
                // Remove existing listeners
                for (ActionListener al : ((JButton)comp).getActionListeners()) {
                    ((JButton)comp).removeActionListener(al);
                }
                // Add new listener to show the transaction list
                ((JButton)comp).addActionListener(e -> {
                    TransactionListManager.showTransactionList();
                });
            } else if (comp instanceof Container) {
                // Recursively search nested containers
                findAndModifyViewButton((Container)comp);
            }
        }
    }
}
