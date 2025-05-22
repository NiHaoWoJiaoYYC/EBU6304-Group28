package org.bupt.persosnalfinance.Front.ManualEntry;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Field;


public class TransactionListManager {
    private static JFrame currentListWindow;

    public static synchronized void showTransactionList() {
        // 如果窗口已存在且未关闭，则将其前置显示
        if (currentListWindow != null && currentListWindow.isDisplayable()) {
            currentListWindow.toFront();
            return;
        }

        // 创建新窗口
        new Thread(() -> {
            DisplayTransactionInformation display = new DisplayTransactionInformation();

            try {
                // 使用反射获取私有frame字段
                Field frameField = DisplayTransactionInformation.class.getDeclaredField("frame");
                frameField.setAccessible(true);
                currentListWindow = (JFrame) frameField.get(display);

                // 修改关闭行为
                currentListWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                // 修改返回按钮行为
                Component[] components = currentListWindow.getContentPane().getComponents();
                for (Component comp : components) {
                    if (comp instanceof JPanel) {
                        for (Component btn : ((JPanel)comp).getComponents()) {
                            if (btn instanceof JButton && "Back".equals(((JButton)btn).getText())) {
                                // 移除原有监听器
                                for (ActionListener al : ((JButton)btn).getActionListeners()) {
                                    ((JButton)btn).removeActionListener(al);
                                }
                                // 添加新监听器
                                ((JButton)btn).addActionListener(e -> currentListWindow.dispose());
                            }
                        }
                    }
                }

                // 添加窗口监听器，在关闭时清除引用
                currentListWindow.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        currentListWindow = null;
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}