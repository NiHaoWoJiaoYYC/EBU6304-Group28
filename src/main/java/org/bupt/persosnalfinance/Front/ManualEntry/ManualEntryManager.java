package org.bupt.persosnalfinance.Front.ManualEntry;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ManualEntryManager {
    private static JFrame currentEntryWindow;

    public static synchronized void showManualEntry() {
        // 如果窗口已存在且未关闭，则将其前置显示
        if (currentEntryWindow != null && currentEntryWindow.isDisplayable()) {
            currentEntryWindow.toFront();
            return;
        }

        // 创建新窗口
        new Thread(() -> {
            ManualEntry.main(new String[]{});

            // 等待窗口初始化完成
            try { Thread.sleep(300); } catch (InterruptedException e) {}

            // 查找并管理新创建的窗口
            Window[] windows = Window.getWindows();
            for (Window window : windows) {
                if (window instanceof JFrame &&
                        "Transaction information > Manual Entry".equals(((JFrame)window).getTitle())) {

                    currentEntryWindow = (JFrame)window;
                    currentEntryWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                    // 添加窗口监听器
                    currentEntryWindow.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosed(WindowEvent e) {
                            currentEntryWindow = null;
                        }
                    });

                    // 如果有"View Existing Transactions"按钮，可以同样处理
                    findAndModifyViewButton(currentEntryWindow);
                }
            }
        }).start();
    }

    private static void findAndModifyViewButton(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JButton && "View Existing Transactions".equals(((JButton)comp).getText())) {
                // 移除原有监听器
                for (ActionListener al : ((JButton)comp).getActionListeners()) {
                    ((JButton)comp).removeActionListener(al);
                }
                // 添加新监听器
                ((JButton)comp).addActionListener(e -> {
                    TransactionListManager.showTransactionList();
                });
            } else if (comp instanceof Container) {
                findAndModifyViewButton((Container)comp);
            }
        }
    }
}