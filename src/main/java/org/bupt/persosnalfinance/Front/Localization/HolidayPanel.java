package org.bupt.persosnalfinance.Front.Localization;

import org.bupt.persosnalfinance.Back.Controller.LocalizationController;
import org.bupt.persosnalfinance.dto.HolidayDTO;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import java.awt.*;
import java.time.*;
import java.util.Date;
import java.util.function.BiConsumer;

/**
 * HolidayPanel: allows selection and filtering of holidays, with edit/delete functionality.
 * Displays only holiday names in English and includes persistent custom holidays.
 */
public class HolidayPanel extends JPanel {
    private LocalizationController controller;

    private final JComboBox<HolidayDTO> holidayCombo    = new JComboBox<>();
    private final JButton              btnEditHoliday   = new JButton("Edit Holiday");
    private final JButton              btnDeleteHoliday = new JButton("Delete Holiday");

    private final JSpinner             fromDateSpinner;
    private final JSpinner             toDateSpinner;
    private BiConsumer<LocalDate, LocalDate> onDateRangeChanged;

    public HolidayPanel() {
        super(new BorderLayout(8, 8));
        setBorder(BorderFactory.createTitledBorder("Holiday Filter"));

        // Renderer: only show the holiday name
        holidayCombo.setRenderer(new BasicComboBoxRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof HolidayDTO) {
                    setText(((HolidayDTO) value).getName());
                }
                return this;
            }
        });

        // Top: holiday selector + buttons
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        top.add(new JLabel("Holiday:"));
        top.add(holidayCombo);
        top.add(btnEditHoliday);
        top.add(btnDeleteHoliday);
        add(top, BorderLayout.NORTH);

        // Date range selectors
        JPanel range = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        fromDateSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH));
        fromDateSpinner.setEditor(new JSpinner.DateEditor(fromDateSpinner, "yyyy-MM-dd"));
        toDateSpinner   = new JSpinner(new SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH));
        toDateSpinner.setEditor(new JSpinner.DateEditor(toDateSpinner, "yyyy-MM-dd"));
        range.add(new JLabel("Start:"));
        range.add(fromDateSpinner);
        range.add(new JLabel("End:"));
        range.add(toDateSpinner);
        add(range, BorderLayout.CENTER);

        // Listeners
        holidayCombo.addActionListener(e -> onHolidaySelected());
        fromDateSpinner.addChangeListener(e -> triggerDateRangeChanged());
        toDateSpinner.addChangeListener(e -> triggerDateRangeChanged());

        btnEditHoliday.addActionListener(e -> {
            HolidayDTO sel = (HolidayDTO) holidayCombo.getSelectedItem();
            if (sel == null) {
                JOptionPane.showMessageDialog(this, "Please select a holiday first.");
                return;
            }
            String input = JOptionPane.showInputDialog(this, "Enter new holiday name:", sel.getName());
            if (input != null && !input.isBlank()) {
                controller.editHolidayName(sel, input.trim());
                reloadHolidays();
            }
        });

        btnDeleteHoliday.addActionListener(e -> {
            HolidayDTO sel = (HolidayDTO) holidayCombo.getSelectedItem();
            if (sel == null) {
                JOptionPane.showMessageDialog(this, "Please select a holiday first.");
                return;
            }
            int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete holiday \"" + sel.getName() + "\"?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
            );
            if (choice == JOptionPane.YES_OPTION) {
                controller.deleteHoliday(sel);
                reloadHolidays();
            }
        });
    }

    /**
     * Injects the controller and loads holidays.
     */
    public void setController(LocalizationController controller) {
        this.controller = controller;
        reloadHolidays();
    }

    /**
     * Reloads the holiday combo with all API and custom holidays.
     */
    private void reloadHolidays() {
        holidayCombo.removeAllItems();
        for (HolidayDTO h : controller.getAllHolidays()) {
            holidayCombo.addItem(h);
        }
        if (holidayCombo.getItemCount() > 0) {
            holidayCombo.setSelectedIndex(0);
            onHolidaySelected();
        }
    }

    /**
     * Called when a holiday is selected: update spinners to that holiday's dates and notify.
     */
    private void onHolidaySelected() {
        HolidayDTO sel = (HolidayDTO) holidayCombo.getSelectedItem();
        if (sel != null) {
            // Convert LocalDate to Date for spinner
            ZoneId zone = ZoneId.systemDefault();
            Date start = Date.from(sel.getStartDate().atStartOfDay(zone).toInstant());
            Date end   = Date.from(sel.getEndDate().atStartOfDay(zone).toInstant());
            fromDateSpinner.setValue(start);
            toDateSpinner.setValue(end);
        }
        triggerDateRangeChanged();
    }

    /**
     * Sets the callback invoked when the selected holiday or date range changes.
     */
    public void setOnDateRangeChanged(BiConsumer<LocalDate, LocalDate> callback) {
        this.onDateRangeChanged = callback;
    }

    /**
     * Notifies the callback of the current date range.
     */
    private void triggerDateRangeChanged() {
        if (onDateRangeChanged == null) return;
        LocalDate from = ((Date) fromDateSpinner.getValue())
            .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate to   = ((Date) toDateSpinner.getValue())
            .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        onDateRangeChanged.accept(from, to);
    }

    /**
     * Returns the currently selected holiday ID.
     */
    public Integer getSelectedHolidayId() {
        HolidayDTO sel = (HolidayDTO) holidayCombo.getSelectedItem();
        return sel != null ? sel.getId() : null;
    }
}
