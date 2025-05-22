package org.bupt.persosnalfinance.Front.Localization;

import javax.swing.*;
import java.awt.*;

public class CombinedUIManager {
    private static JFrame currentWindow;

    public static synchronized void showCombinedUI() {
        // 如果窗口已存在且未关闭，则将其前置显示
        if (currentWindow != null && currentWindow.isDisplayable()) {
            currentWindow.toFront();
            return;
        }

        // 创建新窗口
        SwingUtilities.invokeLater(() -> {
            CombinedUI combinedUI = new CombinedUI();
            currentWindow = combinedUI;
            currentWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            // 添加窗口监听器
            currentWindow.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                    currentWindow = null;
                }
            });
        });
    }
}
