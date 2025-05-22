package org.bupt.persosnalfinance.Front.Localization;

import javax.swing.*;
import java.awt.*;

/**
 * CombinedUI：将 HolidayPanel 与 PlanPanel 嵌入同一窗口，实现上下联动并支持默认预算添加
 */
public class CombinedUI extends JFrame {
    private final HolidayPanel holidayPanel;
    private final PlanPanel planPanel;

    public CombinedUI() {
        super("Holiday → Spending Dashboard");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 初始化面板
        holidayPanel = new HolidayPanel();
        planPanel    = new PlanPanel();

        // 垂直分割，上半是假期面板，下半是预算面板
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, holidayPanel, planPanel);
        split.setResizeWeight(0.3);
        add(split, BorderLayout.CENTER);

        // 注册回调：当假期选择或日期范围改变时，刷新计划面板的节假日ID
        holidayPanel.setOnDateRangeChanged((from, to) -> {
            Integer hid = holidayPanel.getSelectedHolidayId();
            planPanel.setHolidayId(hid);
        });

        // 初始加载：设置默认选中假期的预算列表，使 Add 按钮立即可用
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
