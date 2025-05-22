package org.bupt.persosnalfinance.Front.AIBudgetPlanner;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.*;

public class FullBudgetPlannerManager {
    private static JFrame currentBudgetWindow;

    public static synchronized void showBudgetPlanner() {
        // 如果窗口已存在且未关闭，则将其前置显示
        if (currentBudgetWindow != null && currentBudgetWindow.isDisplayable()) {
            currentBudgetWindow.toFront();
            return;
        }

        // 创建新窗口
        SwingUtilities.invokeLater(() -> {
            FullBudgetPlannerApp.main(new String[]{});

            // 等待窗口初始化完成
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 查找并管理新创建的窗口
            Window[] windows = Window.getWindows();
            for (Window window : windows) {
                if (window instanceof JFrame) {
                    JFrame frame = (JFrame)window;
                    // 根据实际窗口标题调整匹配条件
                    if (frame.getTitle() != null && frame.getTitle().contains("Budget Planner")) {

                        currentBudgetWindow = frame;
                        currentBudgetWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                        // 添加窗口监听器
                        currentBudgetWindow.addWindowListener(new WindowAdapter() {
                            @Override
                            public void windowClosed(WindowEvent e) {
                                currentBudgetWindow = null;
                            }
                        });

                        // 不需要修改按钮行为，所以移除了findAndModifyViewButton调用
                    }
                }
            }
        });
    }
}