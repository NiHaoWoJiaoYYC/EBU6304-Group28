/**
 * @version 1.0
 * @author Yang Yuchen
 * Function: Import CSV
 */


package Import_csv;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

/**
 * 手动实现 CSV 导入功能的 Swing 面板：
 *  - 跳过开头空行
 *  - 遇到第一个空白行后停止读取
 *  - 将数据保存在 dataList
 *  - 构建 tableModel 并提供预览
 */
public class ImportCSVPanel extends JPanel {
    private JComboBox<String> functionCombo;
    private JComboBox<String> timeUnitCombo;
    private JList<String> columnList;
    private DefaultListModel<String> columnListModel;
    private JButton uploadButton;
    private JLabel fileLabel;
    private JButton submitButton;

    private File csvFile;
    private List<String[]> dataList;
    private DefaultTableModel tableModel;

    public ImportCSVPanel() {
        setLayout(new BorderLayout(10, 10));

        // 上部：功能与时间单位
        JPanel topPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        topPanel.add(new JLabel("Choose Function:"));
        functionCombo = new JComboBox<>(new String[]{"Personal finance Advice", "Classification", "Alert"});
        topPanel.add(functionCombo);
        topPanel.add(new JLabel("Choose Time Unit:"));
        timeUnitCombo = new JComboBox<>(new String[]{"Day", "Month", "Year"});
        topPanel.add(timeUnitCombo);
        add(topPanel, BorderLayout.NORTH);

        // 中部：列选择 & 文件上传
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        columnListModel = new DefaultListModel<>();
        columnList = new JList<>(columnListModel);
        columnList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        centerPanel.add(new JScrollPane(columnList));

        JPanel filePanel = new JPanel(new BorderLayout(5,5));
        uploadButton = new JButton("Upload CSV File");
        fileLabel = new JLabel("No file selected");
        uploadButton.addActionListener(this::onUpload);
        filePanel.add(uploadButton, BorderLayout.NORTH);
        filePanel.add(fileLabel, BorderLayout.CENTER);
        centerPanel.add(filePanel);
        add(centerPanel, BorderLayout.CENTER);

        // 底部：提交 & 预览按钮
        submitButton = new JButton("Submit & Preview");
        submitButton.addActionListener(this::onSubmit);
        add(submitButton, BorderLayout.SOUTH);
    }

    // 上传文件，读取首行 header，跳过空行
    private void onUpload(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            csvFile = chooser.getSelectedFile();
            fileLabel.setText(csvFile.getName());
            try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
                String line;
                // 跳过开头全部空白行
                while ((line = br.readLine()) != null) {
                    if (!line.trim().isEmpty()) break;
                }
                if (line != null) {
                    String[] header = parseCsvLine(line);
                    columnListModel.clear();
                    for (String col : header) columnListModel.addElement(col);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error reading CSV file: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // 提交读取，将跳过后停止再遇空行，并弹出预览
    private void onSubmit(ActionEvent e) {
        if (csvFile == null) {
            JOptionPane.showMessageDialog(this,
                    "Please upload a CSV file first.", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        List<String> selectedCols = columnList.getSelectedValuesList();
        if (selectedCols.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please select at least one column.", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<String[]> rows = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            // 跳过开头空行
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) break;
            }
            boolean headerRead = false;
            while (line != null) {
                if (line.trim().isEmpty()) break; // 第一个空白行后停止读取
                String[] parsed = parseCsvLine(line);
                rows.add(parsed);
                headerRead = true;
                line = br.readLine();
            }
            dataList = rows;
            tableModel = buildTableModel(dataList, selectedCols);
            JOptionPane.showMessageDialog(this,
                    "Import successful! Rows read: " + (dataList.size()-1));
            previewData();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Import failed: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 解析 CSV 行，支持逗号与双引号
    private String[] parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(sb.toString()); sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        result.add(sb.toString());
        return result.toArray(new String[0]);
    }

    // 根据选择的列构造 DefaultTableModel
    private DefaultTableModel buildTableModel(List<String[]> raw, List<String> cols) {
        String[] header = raw.get(0);
        List<Integer> idx = new ArrayList<>();
        for (String c : cols) {
            for (int i = 0; i < header.length; i++) {
                if (header[i].equals(c)) { idx.add(i); break; }
            }
        }
        DefaultTableModel model = new DefaultTableModel(cols.toArray(new String[0]), 0);
        for (int r = 1; r < raw.size(); r++) {
            String[] row = raw.get(r);
            Object[] subRow = new Object[idx.size()];
            for (int j = 0; j < idx.size(); j++) {
                subRow[j] = idx.get(j) < row.length ? row[idx.get(j)] : "";
            }
            model.addRow(subRow);
        }
        return model;
    }

    // 预览 dataList 内容
    private void previewData() {
        StringBuilder sb = new StringBuilder();
        for (String[] row : dataList) {
            sb.append(Arrays.toString(row)).append("\n");
        }
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setPreferredSize(new Dimension(400, 300));
        JOptionPane.showMessageDialog(this, scroll, "Preview DataList", JOptionPane.INFORMATION_MESSAGE);
    }

    // 对外接口：
    public List<String[]> getDataList() { return dataList; }
    public DefaultTableModel getTableModel() { return tableModel; }
}