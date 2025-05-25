package org.bupt.persosnalfinance.Front.importcsv;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.*;
import java.util.List;


import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.bupt.persosnalfinance.Util.ImportSuccessListener;

/**
 * Swing panel：choose CSV -> preview -> upload to backend /api/importcsv
 * write in transactionInformation.json
 */
public class ImportCSVPanel extends JPanel {

    /* -----------------  ----------------- */
    private static final String BACKEND_IMPORT_URL = "http://localhost:8080/api/importcsv";

    /* ----------------- UI  ----------------- */
    private JComboBox<String> functionCombo;
    private JComboBox<String> timeUnitCombo;
    private JList<String> columnList;
    private DefaultListModel<String> columnListModel;
    private JButton uploadButton;
    private JLabel fileLabel;
    private JButton previewButton;
    private JButton submitButton;

    /* ----------------- data ----------------- */
    private File csvFile;
    private List<String[]> dataList;
    private DefaultTableModel tableModel;

    private static final Set<String> MANDATORY =
            Set.of("date","amount","type","object","remarks");

    public ImportCSVPanel() {
        setLayout(new BorderLayout(10, 10));
        buildTopPanel();
        buildCenterPanel();
        buildBottomPanel();
    }

    /* ===== top ===== */
    private void buildTopPanel() {
        JPanel top = new JPanel(new GridLayout(2, 2, 5, 5));
        top.add(new JLabel("Choose Function:"));
        functionCombo = new JComboBox<>(new String[]{
                "Personal finance Advice", "Classification", "Alert"});
        top.add(functionCombo);

        top.add(new JLabel("Choose Time Unit:"));
        timeUnitCombo = new JComboBox<>(new String[]{"Day"});
        top.add(timeUnitCombo);

        add(top, BorderLayout.NORTH);
    }

    /* ===== middle ===== */
    private void buildCenterPanel() {
        JPanel center = new JPanel(new GridLayout(1, 2, 10, 10));

        columnListModel = new DefaultListModel<>();
        columnList = new JList<>(columnListModel);
        columnList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        center.add(new JScrollPane(columnList));

        JPanel filePanel = new JPanel(new BorderLayout(5, 5));
        uploadButton = new JButton("Choose CSV File");
        fileLabel = new JLabel("No file selected");
        uploadButton.addActionListener(this::onChooseFile);
        filePanel.add(uploadButton, BorderLayout.NORTH);
        filePanel.add(fileLabel, BorderLayout.CENTER);

        center.add(filePanel);
        add(center, BorderLayout.CENTER);
    }

    /* ===== bottom ===== */
    private void buildBottomPanel() {
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        previewButton = new JButton("Preview");
        previewButton.addActionListener(this::onPreview);
        bottom.add(previewButton);

        submitButton = new JButton("Submit to Backend");
        submitButton.addActionListener(this::onSubmit);
        bottom.add(submitButton);

        add(bottom, BorderLayout.SOUTH);
    }

    /* ----------------- handler ----------------- */

    /** 选择文件：读取第一行 Header，填充列名列表 */
    private void onChooseFile(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            csvFile = chooser.getSelectedFile();
            fileLabel.setText(csvFile.getName());

            try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
                String line;
                while ((line = br.readLine()) != null && line.trim().isEmpty()) {
                    // skip empty lines
                }
                if (line != null) {
                    String[] header = parseCsvLine(line);
                    columnListModel.clear();


                    Set<String> mandatory = Set.of("date","amount","type","object","remarks");
                    List<Integer> autoSelect = new ArrayList<>();

                    for (int i = 0; i < header.length; i++) {
                        String col = header[i];
                        columnListModel.addElement(col);

                        if (mandatory.contains(col.trim().toLowerCase())) {
                            autoSelect.add(i);
                        }
                    }


                    for (Integer idx : autoSelect) {
                        columnList.addSelectionInterval(idx, idx);
                    }
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error reading CSV: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /** preview */
    private void onPreview(ActionEvent e) {
        if (csvFile == null) {
            JOptionPane.showMessageDialog(this,
                    "Please choose a CSV file first!", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!checkColumnSelection()) return;

        loadDataList();                 // dataList + tableModel
        JTextArea ta = new JTextArea(tableModelToString());
        ta.setEditable(false);
        JScrollPane scroll = new JScrollPane(ta);
        scroll.setPreferredSize(new Dimension(500, 300));
        JOptionPane.showMessageDialog(this, scroll,
                "CSV Preview (first 100 rows)", JOptionPane.INFORMATION_MESSAGE);
    }

    private ImportSuccessListener successListener;

    public void setImportSuccessListener(ImportSuccessListener listener) {
        this.successListener = listener;
    }

    /** Submit */
    private void onSubmit(ActionEvent e) {
        if (csvFile == null) {
            JOptionPane.showMessageDialog(this,
                    "Please choose a CSV file first!", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!checkColumnSelection()) return;

        try {
            RestTemplate rt = new RestTemplate();
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new FileSystemResource(csvFile));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> req =
                    new HttpEntity<>(body, headers);

            Integer inserted = rt.postForObject(
                    BACKEND_IMPORT_URL, req, Integer.class);

            JOptionPane.showMessageDialog(this,
                    "Successfully imported " + inserted + " transactions!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            if (inserted != null) {

                Window parentWindow = SwingUtilities.getWindowAncestor(this);
                if (parentWindow != null) {
                    parentWindow.dispose();
                }


                if (successListener != null) {
                    successListener.onImportSuccess();
                } else {
                    System.err.println("successListener is null！");
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Upload failed: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /* ----------------- 工具方法 ----------------- */

    /** */
    private String[] parseCsvLine(String line) {
        List<String> res = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                res.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        res.add(sb.toString());
        return res.toArray(new String[0]);
    }

    /**  dataList & tableModel */
    private void loadDataList() {
        dataList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = br.readLine()) != null && line.trim().isEmpty()) {
            }
            while (line != null && !line.trim().isEmpty()) {
                dataList.add(parseCsvLine(line));
                line = br.readLine();
            }
        } catch (IOException ex) {
            // ignore
        }

        tableModel = new DefaultTableModel(dataList.get(0), 0);
        for (int i = 1; i < Math.min(dataList.size(), 100); i++) {
            tableModel.addRow(dataList.get(i));
        }
    }

    /** 把 tableModel */
    private String tableModelToString() {
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < tableModel.getRowCount(); r++) {
            for (int c = 0; c < tableModel.getColumnCount(); c++) {
                sb.append(tableModel.getValueAt(r, c)).append(
                        c == tableModel.getColumnCount() - 1 ? "" : ", ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private boolean checkColumnSelection() {

        List<String> selected = columnList.getSelectedValuesList()
                .stream()
                .map(s -> s.trim().toLowerCase())
                .toList();

        if (!selected.containsAll(MANDATORY)) {           // ← 核心判断
            JOptionPane.showMessageDialog(this,
                    "Please select mandatory columns: " + MANDATORY,
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }
}


