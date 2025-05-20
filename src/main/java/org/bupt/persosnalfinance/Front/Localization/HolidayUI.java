package org.bupt.persosnalfinance.Front.Localization;

import org.bupt.persosnalfinance.dto.HolidayDTO;
import org.bupt.persosnalfinance.Back.Service.HolidayService;
import javax.swing.*;
import javax.swing.JFormattedTextField;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.Date;
import java.util.List;
import java.time.ZoneId;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;

/**
 * Front 层：Swing 界面，展示和编辑节假日
 */
public class HolidayUI extends JFrame {
    private final HolidayService service = new HolidayService();
    private JComboBox<String> holidayComboBox;
    private JSpinner startDateSpinner;
    private JSpinner endDateSpinner;
    private JLabel  daysLeftValue;

    public HolidayUI() {
        super("Holiday Localization");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        initHolidayPanel();
        initDatePanel();
        refreshHolidayComboBox();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initHolidayPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Select Holiday"));

        holidayComboBox = new JComboBox<>();
        holidayComboBox.addActionListener(e -> onHolidaySelected());
        panel.add(holidayComboBox, BorderLayout.NORTH);

        JButton addBtn = new JButton("Add Custom Holiday");
        addBtn.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "Enter holiday name:");
            if (name != null && !name.trim().isEmpty()) {
                service.addCustomHoliday(name.trim());
                refreshHolidayComboBox();
                holidayComboBox.setSelectedItem(name.trim());
            }
        });
        panel.add(addBtn, BorderLayout.SOUTH);
        add(panel, BorderLayout.WEST);
    }

    private void initDatePanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Holiday Date"));

        panel.add(new JLabel("Start Day:"));
        startDateSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
        startDateSpinner.setEditor(new JSpinner.DateEditor(startDateSpinner, "yyyy-MM-dd"));
        panel.add(startDateSpinner);

        panel.add(new JLabel("End Day:"));
        endDateSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
        endDateSpinner.setEditor(new JSpinner.DateEditor(endDateSpinner, "yyyy-MM-dd"));
        panel.add(endDateSpinner);

        panel.add(new JLabel("Days left:"));
        daysLeftValue = new JLabel("0");
        panel.add(daysLeftValue);

        ChangeListener validationListener = e -> validateDates();
        startDateSpinner.addChangeListener(validationListener);
        endDateSpinner.addChangeListener(validationListener);

        add(panel, BorderLayout.CENTER);
    }

    private void refreshHolidayComboBox() {
        holidayComboBox.removeAllItems();
        for (HolidayDTO dto : service.getAllHolidays()) {
            holidayComboBox.addItem(dto.getName());
        }
    }

    private void onHolidaySelected() {
        String sel = (String) holidayComboBox.getSelectedItem();
        HolidayDTO dto = service.findByName(sel);
        if (dto != null) {
            Date start = Date.from(dto.getStartDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date end   = Date.from(dto.getEndDate()  .atStartOfDay(ZoneId.systemDefault()).toInstant());
            startDateSpinner.setValue(start);
            endDateSpinner  .setValue(end);
            validateDates();
        }
    }

    private void validateDates() {
        String sel = (String) holidayComboBox.getSelectedItem();
        HolidayDTO dto = service.findByName(sel);
        if (dto == null) return;

        LocalDate start = ((Date) startDateSpinner.getValue())
                          .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate end   = ((Date) endDateSpinner  .getValue())
                          .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        boolean valid = !end.isBefore(start);

        JFormattedTextField fStart = ((JSpinner.DefaultEditor) startDateSpinner.getEditor()).getTextField();
        JFormattedTextField fEnd   = ((JSpinner.DefaultEditor) endDateSpinner  .getEditor()).getTextField();
        if (!valid) {
            fStart.setBackground(Color.PINK);
            fEnd  .setBackground(Color.PINK);
            daysLeftValue.setText("End must be after start");
            daysLeftValue.setForeground(Color.RED);
        } else {
            fStart.setBackground(Color.WHITE);
            fEnd  .setBackground(Color.WHITE);
            daysLeftValue.setForeground(Color.BLACK);
            long days = ChronoUnit.DAYS.between(LocalDate.now(), start);
            daysLeftValue.setText(days >= 0 ? String.valueOf(days) : "0");
            service.updateHolidayDates(dto, start, end);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HolidayUI::new);
    }
}

