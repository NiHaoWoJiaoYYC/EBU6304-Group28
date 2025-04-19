package Localization;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.JFormattedTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.Calendar;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

/**
 * Holiday.java
 * 假期本地化界面示例
 * 专注假期选择、日期填充及倒计时功能，支持自定义假期日期持久化以及日期校验
 */
public class Holiday extends JFrame {
    private List<String> userHolidays = new ArrayList<>();
    private List<String> apiHolidays = new ArrayList<>();
    private Map<String, LocalDate[]> holidayDateMap = new HashMap<>();

    private JComboBox<String> holidayComboBox;
    private JSpinner startDateSpinner;
    private JSpinner endDateSpinner;
    private JLabel daysLeftValue;

    public Holiday() {
        super("Holiday Localization");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initHolidayPanel();
        initDatePanel();

        loadApiHolidays();
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

        JButton addHolidayBtn = new JButton("Add Custom Holiday");
        addHolidayBtn.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "Enter holiday name:");
            if (name != null && !name.trim().isEmpty()) {
                String trimmed = name.trim();
                userHolidays.add(trimmed);
                LocalDate today = LocalDate.now();
                holidayDateMap.put(trimmed, new LocalDate[]{today, today});
                refreshHolidayComboBox();
                holidayComboBox.setSelectedItem(trimmed);
            }
        });
        panel.add(addHolidayBtn, BorderLayout.SOUTH);

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

    private void loadApiHolidays() {
        apiHolidays.clear();
        holidayDateMap.clear();

        apiHolidays.add("New Year's Day");
        holidayDateMap.put("New Year's Day", new LocalDate[]{LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 1)});

        apiHolidays.add("Independence Day");
        holidayDateMap.put("Independence Day", new LocalDate[]{LocalDate.of(2025, 7, 4), LocalDate.of(2025, 7, 4)});

        apiHolidays.add("Christmas Day");
        holidayDateMap.put("Christmas Day", new LocalDate[]{LocalDate.of(2025, 12, 25), LocalDate.of(2025, 12, 25)});
    }

    private void refreshHolidayComboBox() {
        holidayComboBox.removeAllItems();
        for (String h : apiHolidays) holidayComboBox.addItem(h);
        for (String h : userHolidays) holidayComboBox.addItem(h);
    }

    private void onHolidaySelected() {
        String sel = (String) holidayComboBox.getSelectedItem();
        if (sel != null && holidayDateMap.containsKey(sel)) {
            LocalDate[] dates = holidayDateMap.get(sel);
            Date start = Date.from(dates[0].atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date end = Date.from(dates[1].atStartOfDay(ZoneId.systemDefault()).toInstant());
            startDateSpinner.setValue(start);
            endDateSpinner.setValue(end);
            validateDates();
        }
    }

    /**
     * 校验开始与结束日期，确保结束晚于开始。
     * 若无效则标红并提示，否则恢复默认并更新倒计时与保存。
     */
    private void validateDates() {
        Date startDate = (Date) startDateSpinner.getValue();
        Date endDate = (Date) endDateSpinner.getValue();
        LocalDate start = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate end = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        boolean valid = !end.isBefore(start);

        JFormattedTextField startField = ((JSpinner.DefaultEditor) startDateSpinner.getEditor()).getTextField();
        JFormattedTextField endField = ((JSpinner.DefaultEditor) endDateSpinner.getEditor()).getTextField();
        if (!valid) {
            startField.setBackground(Color.PINK);
            endField.setBackground(Color.PINK);
            daysLeftValue.setText("End must be after start");
            daysLeftValue.setForeground(Color.RED);
        } else {
            startField.setBackground(Color.WHITE);
            endField.setBackground(Color.WHITE);
            daysLeftValue.setForeground(Color.BLACK);
            updateDaysLeft();
            saveSelectedHolidayDates();
        }
    }

    private void saveSelectedHolidayDates() {
        String sel = (String) holidayComboBox.getSelectedItem();
        if (sel != null) {
            Date startDate = (Date) startDateSpinner.getValue();
            Date endDate = (Date) endDateSpinner.getValue();
            LocalDate start = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate end = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            holidayDateMap.put(sel, new LocalDate[]{start, end});
        }
    }

    private void updateDaysLeft() {
        Date date = (Date) startDateSpinner.getValue();
        LocalDate now = LocalDate.now();
        LocalDate selected = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        long days = ChronoUnit.DAYS.between(now, selected);
        daysLeftValue.setText(days >= 0 ? String.valueOf(days) : "0");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Holiday::new);
    }
}

