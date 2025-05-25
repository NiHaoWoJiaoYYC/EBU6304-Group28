package org.bupt.persosnalfinance.Front.ManualEntry;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Field;

/**
 * Manages the display and reuse of the transaction list window.
 * <p>
 * This class ensures that only one instance of the transaction list window is shown at a time.
 * If the window already exists and is still open, it is brought to the front.
 * Otherwise, a new instance is created by launching {@link DisplayTransactionInformation}
 * and accessing its internal frame via reflection.
 * </p>
 *
 * @author Xuerui Dong
 */
public class TransactionListManager {

    /**
     * The current instance of the transaction list window, if it is open.
     */
    private static JFrame currentListWindow;

    /**
     * Displays the transaction list window in a separate thread.
     * <p>
     * If the window already exists and is still displayable, it is brought to the front.
     * Otherwise, a new {@link DisplayTransactionInformation} instance is created,
     * and its internal frame is accessed using reflection. The close behavior is customized
     * and the "Back" button's action listener is overridden to properly dispose of the window.
     * A window listener is also added to clear the reference when the window is closed.
     * </p>
     */
    public static synchronized void showTransactionList() {
        // If the window already exists and is not closed, bring it to the front
        if (currentListWindow != null && currentListWindow.isDisplayable()) {
            currentListWindow.toFront();
            return;
        }

        // Create and initialize the window in a separate thread
        new Thread(() -> {
            DisplayTransactionInformation display = new DisplayTransactionInformation();

            try {
                // Use reflection to access the private 'frame' field
                Field frameField = DisplayTransactionInformation.class.getDeclaredField("frame");
                frameField.setAccessible(true);
                currentListWindow = (JFrame) frameField.get(display);

                // Set custom close behavior
                currentListWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                // Modify "Back" button behavior if present
                Component[] components = currentListWindow.getContentPane().getComponents();
                for (Component comp : components) {
                    if (comp instanceof JPanel) {
                        for (Component btn : ((JPanel) comp).getComponents()) {
                            if (btn instanceof JButton && "Back".equals(((JButton) btn).getText())) {
                                // Remove existing action listeners
                                for (ActionListener al : ((JButton) btn).getActionListeners()) {
                                    ((JButton) btn).removeActionListener(al);
                                }
                                // Add new action listener to close the window
                                ((JButton) btn).addActionListener(e -> currentListWindow.dispose());
                            }
                        }
                    }
                }

                // Add a window listener to clear the reference when the window is closed
                currentListWindow.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        currentListWindow = null;
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
