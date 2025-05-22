package org.bupt.persosnalfinance.Front.Localization;

import org.bupt.persosnalfinance.dto.HolidayDTO;
import org.bupt.persosnalfinance.Back.Controller.LocalizationController;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * HolidayPanel：假期选择与日期管理面板，支持通过 Controller 获取数据、更新并验证日期逻辑。
 */
public class HolidayPanel extends JPanel {
    private LocalizationController controller;
    private final JComboBox<String> holidayCombo;
    private final JSpinner startSpinner;
    private final JSpinner endSpinner;
    private final JLabel daysLeftLabel;
    private List<HolidayDTO> holidays = Collections.emptyList();
    private BiConsumer<LocalDate, LocalDate> onDateRangeChanged;

    public HolidayPanel() {
        super(new BorderLayout(8, 8));
        setBorder(BorderFactory.createTitledBorder("Holiday Selector"));

        // 西侧：假期下拉 + 自定义按钮
        holidayCombo = new JComboBox<>();
        JButton addBtn = new JButton("Add Custom");
        addBtn.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "Holiday name:");
            if (name != null && !name.trim().isEmpty()) {
                controller.addCustomHoliday(name.trim());
                loadHolidays();
                holidayCombo.setSelectedItem(name.trim());
            }
        });
        JPanel west = new JPanel(new BorderLayout(4, 4));
        west.add(holidayCombo, BorderLayout.CENTER);
        west.add(addBtn, BorderLayout.SOUTH);
        add(west, BorderLayout.WEST);

        // 中部：日期范围 & 倒计时
        JPanel center = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        startSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
        startSpinner.setEditor(new JSpinner.DateEditor(startSpinner, "yyyy-MM-dd"));
        endSpinner   = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
        endSpinner  .setEditor(new JSpinner.DateEditor(endSpinner,   "yyyy-MM-dd"));
        daysLeftLabel = new JLabel("0");

        center.add(new JLabel("From:")); center.add(startSpinner);
        center.add(new JLabel("To:"));   center.add(endSpinner);
        center.add(new JLabel("Days left:")); center.add(daysLeftLabel);
        add(center, BorderLayout.CENTER);

        // 事件绑定
        holidayCombo.addActionListener(e -> applySelectedHoliday());
        ChangeListener dateListener = e -> notifyDateChanged();
        startSpinner.addChangeListener(dateListener);
        endSpinner  .addChangeListener(dateListener);
    }

    /**
     * 注入 Controller 并初始化数据
     */
    public void setController(LocalizationController controller) {
        this.controller = controller;
        loadHolidays();
    }

    private void loadHolidays() {
        holidays = controller.getAllHolidays();
        holidayCombo.removeAllItems();
        for (HolidayDTO h : holidays) {
            holidayCombo.addItem(h.getName());
        }
        if (!holidays.isEmpty()) {
            holidayCombo.setSelectedIndex(0);
            applySelectedHoliday();
        }
    }

    private void applySelectedHoliday() {
        String name = (String) holidayCombo.getSelectedItem();
        if (name == null) return;
        for (HolidayDTO dto : holidays) {
            if (name.equals(dto.getName())) {
                Date ds = Date.from(dto.getStartDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
                Date de = Date.from(dto.getEndDate()  .atStartOfDay(ZoneId.systemDefault()).toInstant());
                startSpinner.setValue(ds);
                endSpinner  .setValue(de);
                notifyDateChanged();
                break;
            }
        }
    }

    private void notifyDateChanged() {
        String name = (String) holidayCombo.getSelectedItem();
        HolidayDTO dto = holidays.stream()
                .filter(h -> h.getName().equals(name))
                .findFirst().orElse(null);
        if (dto == null) return;

        LocalDate start = ((Date) startSpinner.getValue())
                .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate end   = ((Date) endSpinner.getValue())
                .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        // 验证日期逻辑
        JFormattedTextField fStart = ((JSpinner.DefaultEditor) startSpinner.getEditor()).getTextField();
        JFormattedTextField fEnd   = ((JSpinner.DefaultEditor) endSpinner.getEditor()).getTextField();
        if (end.isBefore(start)) {
            fStart.setBackground(Color.PINK);
            fEnd  .setBackground(Color.PINK);
            daysLeftLabel.setText("End date must be after start");
            daysLeftLabel.setForeground(Color.RED);
            return;
        } else {
            fStart.setBackground(Color.WHITE);
            fEnd  .setBackground(Color.WHITE);
        }

        long daysLeft = Math.max(0, ChronoUnit.DAYS.between(LocalDate.now(), start));
        daysLeftLabel.setText(String.valueOf(daysLeft));
        daysLeftLabel.setForeground(Color.BLACK);

        // 更新服务
        controller.updateHolidayDates(dto, start, end);

        // 回调通知
        if (onDateRangeChanged != null) {
            onDateRangeChanged.accept(start, end);
        }
    }

    /**
     * 外部注册：当日期范围或假期选择改变时触发
     */
    public void setOnDateRangeChanged(BiConsumer<LocalDate, LocalDate> listener) {
        this.onDateRangeChanged = listener;
    }

    /**
     * 获取当前选中假期的 ID
     */
    public Integer getSelectedHolidayId() {
        String name = (String) holidayCombo.getSelectedItem();
        if (holidays == null || holidays.isEmpty()) {
            return holidayCombo.getSelectedIndex();
        }
        for (HolidayDTO dto : holidays) {
            if (dto.getName().equals(name)) {
                return dto.getId() != null ? dto.getId() : holidays.indexOf(dto);
            }
        }
        return holidayCombo.getSelectedIndex();
    }
}
