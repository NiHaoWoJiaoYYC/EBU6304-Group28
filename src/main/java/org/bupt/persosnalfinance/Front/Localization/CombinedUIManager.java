package org.bupt.persosnalfinance.Front.Localization;

import javax.swing.*;

/**
 * Manager class for displaying and handling the CombinedUI window.
 * <p>
 * Ensures that only one instance of the CombinedUI window is displayed at a time.
 * If the window already exists and is still open, it brings it to the front.
 * Otherwise, it creates and initializes a new window on the Event Dispatch Thread (EDT).
 * </p>
 *
 * @author Xuerui Dong
 */
public class CombinedUIManager {

    /**
     * The current instance of the CombinedUI window, if it is open.
     */
    private static JFrame currentWindow;

    /**
     * Displays the CombinedUI window.
     * <p>
     * If the window already exists and is displayable, it brings it to the front.
     * Otherwise, it creates a new instance of {@link CombinedUI}, sets its default close operation,
     * and registers a window listener to reset the reference when the window is closed.
     * </p>
     */
    public static synchronized void showCombinedUI() {
        // If the window already exists and is not closed, bring it to the front
        if (currentWindow != null && currentWindow.isDisplayable()) {
            currentWindow.toFront();
            return;
        }

        // Create and initialize a new window on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            CombinedUI combinedUI = new CombinedUI();
            currentWindow = combinedUI;
            currentWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            // Add a window listener to clear the reference when the window is closed
            currentWindow.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                    currentWindow = null;
                }
            });
        });
    }
}
