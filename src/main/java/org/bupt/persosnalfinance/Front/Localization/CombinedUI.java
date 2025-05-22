package org.bupt.persosnalfinance.Front.Localization;

import java.time.ZoneId;
import org.bupt.persosnalfinance.Back.Controller.LocalizationController;

import javax.swing.*;
import java.awt.*;

/**
 * CombinedUI：将 HolidayPanel 与 PlanPanel 嵌入同一窗口，实现上下联动并使用 LocalizationController。
 */
public class CombinedUI extends JFrame {
    private final HolidayPanel holidayPanel;
    private final PlanPanel planPanel;
    private final LocalizationController controller;

    public CombinedUI() {
        super("Holiday → Spending Dashboard");
        // 初始化 Controller
        controller = new LocalizationController();

        // 初始化面板，并注入 controller
        holidayPanel = new HolidayPanel();
        holidayPanel.setController(controller);

        planPanel = new PlanPanel();
        planPanel.setController(controller);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 垂直分割，上半是假期面板，下半是预算面板
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, holidayPanel, planPanel);
        split.setResizeWeight(0.3);
        add(split, BorderLayout.CENTER);

        // 注册回调：当假期选择或日期范围改变时，刷新计划面板的节假日ID
        holidayPanel.setOnDateRangeChanged((from, to) -> {
            Integer hid = holidayPanel.getSelectedHolidayId();
            planPanel.setHolidayId(hid);
        });

        // 初始加载：设置默认选中假期的预算列表
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
