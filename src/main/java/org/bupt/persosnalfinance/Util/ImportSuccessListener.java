package org.bupt.persosnalfinance.Util;

import org.bupt.persosnalfinance.Front.Dashboard.Dashboard;

import javax.swing.*;

/**
 * A listener interface for receiving notifications when a CSV import operation
 * completes successfully.
 *
 * <p>This interface should be implemented by classes that need to perform
 * actions (such as UI updates or data refresh) after a successful data import.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * public class MyDashboard implements ImportSuccessListener {
 *     @Override
 *     public void onImportSuccess() {
 *         // Refresh dashboard data or show success message
 *     }
 * }
 * }</pre>
 *
 * @author Xuerui Dong
 */
public interface ImportSuccessListener {

    /**
     * Invoked when the import operation has completed successfully.
     * Implementing classes should define what should happen next,
     * such as updating the UI or notifying the user.
     */
    void onImportSuccess();
}