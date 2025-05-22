package org.bupt.persosnalfinance.Front.ExportCsvPanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import org.bupt.persosnalfinance.dto.ExportRequest;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

/**
 * 左侧显示 data/transactionCSV 下已有文件
 * 右侧输入文件名并点击 Export 调用后端生成 CSV
 */
public class ExportCsvPanel extends JPanel {

    private static final String BACKEND = "http://localhost:8080/api/csv";
    private final RestTemplate rt = new RestTemplate();

    private DefaultListModel<String> listModel;
    private JList<String> fileList;
    private JTextField nameField;

    public ExportCsvPanel() {
        setLayout(new BorderLayout(10,10));

        // 左：文件列表
        listModel = new DefaultListModel<>();
        fileList  = new JList<>(listModel);
        add(new JScrollPane(fileList), BorderLayout.CENTER);

        // 右：输入框 + 按钮
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

        right.add(new JLabel("Save as (no .csv):"));
        nameField = new JTextField(15);
        right.add(nameField);

        JButton btn = new JButton("Export JSON → CSV");
        btn.addActionListener(e -> doExport());
        right.add(Box.createVerticalStrut(10));
        right.add(btn);

        add(right, BorderLayout.EAST);

        refreshFileList();
    }

    private void refreshFileList() {
        listModel.clear();
        List<String> files = rt.getForObject(BACKEND, List.class);
        if (files != null) files.forEach(listModel::addElement);
    }

    private void doExport() {
        String fn = nameField.getText().trim();
        ExportRequest req = new ExportRequest(fn.isEmpty()?null:fn);

        String saved = rt.postForObject(BACKEND, req, String.class);
        JOptionPane.showMessageDialog(this,
                "CSV saved as: " + saved,
                "Success", JOptionPane.INFORMATION_MESSAGE);
        refreshFileList();
    }
}

