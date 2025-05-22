package org.bupt.persosnalfinance.Front.Localization;

import org.bupt.persosnalfinance.Back.Controller.LocalizationController;
import org.bupt.persosnalfinance.dto.HolidayDTO;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.function.BiConsumer;

/**
 * HolidayPanel: allows selection and filtering of holidays, with edit/delete functionality.
 * Displays only holiday names in English and includes persistent custom holidays.
 * Validates that end date is not before start date.
 */
public class HolidayPanel extends JPanel {
    private LocalizationController controller;

    private final JComboBox<HolidayDTO> holidayCombo    = new JComboBox<>();
    private final JButton              btnEditHoliday   = new JButton("Edit Holiday");
    private final JButton              btnDeleteHoliday = new JButton("Delete Holiday");

    private final JSpinner             fromDateSpinner;
    private final JSpinner             toDateSpinner;
    private final JLabel               lblError         = new JLabel(" "); // error message

    private BiConsumer<LocalDate, LocalDate> onDateRangeChanged;

    public HolidayPanel() {
        super(new BorderLayout(8, 8));
        setBorder(BorderFactory.createTitledBorder("Holiday Filter"));

        // Combo renderer: only show name
        holidayCombo.setRenderer(new BasicComboBoxRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int idx,
                                                          boolean sel, boolean focus) {
                super.getListCellRendererComponent(list, value, idx, sel, focus);
                if (value instanceof HolidayDTO) {
                    setText(((HolidayDTO) value).getName());
                }
                return this;
            }
        });

        // Top: selector + buttons
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        top.add(new JLabel("Holiday:"));
        top.add(holidayCombo);
        top.add(btnEditHoliday);
        top.add(btnDeleteHoliday);
        add(top, BorderLayout.NORTH);

        // Date range panel + error label below
        JPanel range = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        fromDateSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH));
        fromDateSpinner.setEditor(new JSpinner.DateEditor(fromDateSpinner, "yyyy-MM-dd"));
        toDateSpinner   = new JSpinner(new SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH));
        toDateSpinner.setEditor(new JSpinner.DateEditor(toDateSpinner, "yyyy-MM-dd"));

        range.add(new JLabel("Start:"));
        range.add(fromDateSpinner);
        range.add(new JLabel("End:"));
        range.add(toDateSpinner);
        range.add(lblError);
        lblError.setForeground(Color.RED);

        add(range, BorderLayout.CENTER);

        // Common listener to validate dates and then notify/change
        ChangeListener validateAndNotify = e -> {
            boolean valid = validateDates();
            if (valid) {
                // clear error
                lblError.setText(" ");
                toDateSpinner.getEditor().getComponent(0).setBackground(Color.WHITE);
                fromDateSpinner.getEditor().getComponent(0).setBackground(Color.WHITE);
                onDatesChanged();
            }
        };

        holidayCombo.addActionListener(e -> {
            populateSpinnersWithSelectedHoliday();
            validateAndNotify.stateChanged(null);
        });
        fromDateSpinner.addChangeListener(validateAndNotify);
        toDateSpinner.addChangeListener(validateAndNotify);

        btnEditHoliday.addActionListener(e -> {
            HolidayDTO sel = (HolidayDTO) holidayCombo.getSelectedItem();
            if (sel == null) {
                JOptionPane.showMessageDialog(this, "Please select a holiday first.");
                return;
            }
            String input = JOptionPane.showInputDialog(this, "Enter new holiday name:", sel.getName());
            if (input != null && !input.isBlank()) {
                controller.updateHolidayName(sel, input.trim());
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
                controller.removeHoliday(sel);
                reloadHolidays();
            }
        });
    }

    /** Inject controller and load */
    public void setController(LocalizationController controller) {
        this.controller = controller;
        reloadHolidays();
    }

    /** Reload combo and select first */
    private void reloadHolidays() {
        holidayCombo.removeAllItems();
        for (HolidayDTO h : controller.getAllHolidays()) {
            holidayCombo.addItem(h);
        }
        if (holidayCombo.getItemCount() > 0) {
            holidayCombo.setSelectedIndex(0);
        }
    }

    /** Populate spinners from selected holiday */
    private void populateSpinnersWithSelectedHoliday() {
        HolidayDTO sel = (HolidayDTO) holidayCombo.getSelectedItem();
        if (sel == null) return;
        ZoneId zone = ZoneId.systemDefault();
        Date start = Date.from(sel.getStartDate().atStartOfDay(zone).toInstant());
        Date end   = Date.from(sel.getEndDate().atStartOfDay(zone).toInstant());
        fromDateSpinner.setValue(start);
        toDateSpinner.setValue(end);
    }

    /** Validate that end >= start; if not, mark red and show error */
    private boolean validateDates() {
        LocalDate from = ((Date) fromDateSpinner.getValue())
            .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate to   = ((Date) toDateSpinner.getValue())
            .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        if (to.isBefore(from)) {
            lblError.setText("End date cannot be before start date");
            toDateSpinner.getEditor().getComponent(0).setBackground(Color.PINK);
            fromDateSpinner.getEditor().getComponent(0).setBackground(Color.PINK);
            return false;
        }
        return true;
    }

    /** Called when dates are valid and changed: update backend and notify parent */
    private void onDatesChanged() {
        HolidayDTO sel = (HolidayDTO) holidayCombo.getSelectedItem();
        if (sel == null) return;
        LocalDate from = ((Date) fromDateSpinner.getValue())
            .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate to   = ((Date) toDateSpinner.getValue())
            .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        // update service
        controller.updateHolidayDates(sel, from, to);

        // propagate to CombinedUI / PlanPanel
        if (onDateRangeChanged != null) {
            onDateRangeChanged.accept(from, to);
        }
    }

    /** Set callback for outside */
    public void setOnDateRangeChanged(BiConsumer<LocalDate, LocalDate> callback) {
        this.onDateRangeChanged = callback;
    }

    /** Get selected holiday ID */
    public Integer getSelectedHolidayId() {
        HolidayDTO sel = (HolidayDTO) holidayCombo.getSelectedItem();
        return sel != null ? sel.getId() : null;
    }
}

