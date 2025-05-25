package org.bupt.persosnalfinance.Front.Localization;

import java.time.ZoneId;
import org.bupt.persosnalfinance.Back.Controller.LocalizationController;

import javax.swing.*;
import java.awt.*;

/**
 * CombinedUI：Embed HolidayPanel and PlanPanel in the same window, implement up-down linkage and use LocalizationController.
 */
public class CombinedUI extends JFrame {
    private final HolidayPanel holidayPanel;
    private final PlanPanel planPanel;
    private final LocalizationController controller;

    public CombinedUI() {
        super("Holiday → Spending Dashboard");
        // Initialising the Controller
        controller = new LocalizationController();

        // Initialise the panel and inject the controller
        holidayPanel = new HolidayPanel();
        holidayPanel.setController(controller);

        planPanel = new PlanPanel();
        planPanel.setController(controller);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Vertical split, top half is holiday panel, bottom half is budget panel
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, holidayPanel, planPanel);
        split.setResizeWeight(0.3);
        add(split, BorderLayout.CENTER);

        // Registration callback: Refresh the holiday ID of the schedule panel when the holiday selection or date range is changed
        holidayPanel.setOnDateRangeChanged((from, to) -> {
            Integer hid = holidayPanel.getSelectedHolidayId();
            planPanel.setHolidayId(hid);
        });

        // Initial load: set the budget list for holidays selected by default
        SwingUtilities.invokeLater(() -> {
            Integer initialId = holidayPanel.getSelectedHolidayId();
            planPanel.setHolidayId(initialId);
        });

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CombinedUI::new);
    }
}
