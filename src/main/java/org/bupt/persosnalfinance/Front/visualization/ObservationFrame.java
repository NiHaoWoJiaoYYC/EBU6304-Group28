package org.bupt.persosnalfinance.Front.visualization;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bupt.persosnalfinance.dto.TransactionInformation;
import org.bupt.persosnalfinance.dto.ChatRequest;
import org.bupt.persosnalfinance.dto.ChatResponse;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

/**
 * 左：饼图 右：交易表格 底：大模型对话区
 */
public class ObservationFrame extends JFrame {

    /* ------------ 常量与 HTTP ------------ */
    private static final String BACKEND = "http://localhost:8080";
    private final RestTemplate RT = new RestTemplate();
    private final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("M/d/yyyy");

    /* ------------ UI 组件 ------------ */
    private JComboBox<String> combo;
    private JSplitPane split;
    private ChartPanel chartPanel;
    private JTable table;
    private JTextArea chatArea;
    private JTextField input;
    private JButton send;

    /* ------------ 数据 ------------ */
    private List<TransactionInformation> data;

    /* ------------ 构造 ------------ */
    public ObservationFrame() {
        super("Personal Finance");

        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        buildTop();
        buildCenter();
        buildBottom();

        fetchTransactions();
        populateTable();
        refreshChart();
    }

    /* ===== 顶部：分类维度 ===== */
    private void buildTop() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Classify by:"));
        combo = new JComboBox<>(new String[]{"Category", "Payment", "Month"});
        combo.addActionListener(this::onDimensionChange);
        top.add(combo);
        add(top, BorderLayout.NORTH);
    }

    /* ===== 中间：饼图 + 表格 ===== */
    private void buildCenter() {
        chartPanel = emptyChart();
        table = new JTable();
        split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                chartPanel, new JScrollPane(table));
        split.setDividerLocation(480);
        add(split, BorderLayout.CENTER);
    }

    /* ===== 底部：大模型对话区 ===== */
    private void buildBottom() {
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        JScrollPane chatScroll = new JScrollPane(chatArea);
        chatScroll.setPreferredSize(new Dimension(900, 120));

        input = new JTextField();
        send  = new JButton("Send");
        send.addActionListener(e -> sendQuestion());

        JPanel inputBar = new JPanel(new BorderLayout(5, 5));
        inputBar.add(input, BorderLayout.CENTER);
        inputBar.add(send,  BorderLayout.EAST);

        JPanel bottom = new JPanel(new BorderLayout(5, 5));
        bottom.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        bottom.add(chatScroll, BorderLayout.CENTER);
        bottom.add(inputBar,   BorderLayout.SOUTH);

        add(bottom, BorderLayout.SOUTH);
    }

    /* ===== 事件 ===== */
    private void onDimensionChange(ActionEvent e) { refreshChart(); }

    /* ===== 发送问题到后端 LLM ===== */
    private void sendQuestion() {
        String q = input.getText().trim();
        if (q.isEmpty()) return;
        appendChat("You: " + q);
        input.setText("");

        try {
            ChatRequest req = new ChatRequest(q);
            ChatResponse resp = RT.postForObject(
                    BACKEND + "/api/chat", req, ChatResponse.class);
            appendChat("AI: " + resp.getAnswer());
        } catch (Exception ex) {
            appendChat("AI: [error] " + ex.getMessage());
        }
    }

    private void appendChat(String line) {
        chatArea.append(line + "\n");
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    /* ===== HTTP 获取交易数据 ===== */
    private void fetchTransactions() {
        ResponseEntity<List<TransactionInformation>> resp =
                RT.exchange(BACKEND + "/api/transactions",
                        HttpMethod.GET, null,
                        new ParameterizedTypeReference<>() {});
        data = resp.getBody() != null ? resp.getBody() : List.of();
    }

    /* ===== 表格 ===== */
    private void populateTable() {
        String[] cols = {"Date", "Category", "Payment", "Amount"};
        Object[][] rows = data.stream()
                .map(t -> new Object[]{
                        t.getDate(),
                        t.getType(),
                        t.getObject(),
                        t.getAmount()
                }).toArray(Object[][]::new);
        table.setModel(new DefaultTableModel(rows, cols));
    }

    /* ===== 饼图 ===== */
    private void refreshChart() {
        Map<String, Double> grouped = switch ((String) combo.getSelectedItem()) {
            case "Payment" -> data.stream().collect(Collectors.groupingBy(
                    TransactionInformation::getObject,
                    Collectors.summingDouble(TransactionInformation::getAmount)));
            case "Month" -> data.stream().collect(Collectors.groupingBy(
                    t -> LocalDate.parse(t.getDate(), FMT).getMonth().toString(),
                    Collectors.summingDouble(TransactionInformation::getAmount)));
            default -> data.stream().collect(Collectors.groupingBy(
                    TransactionInformation::getType,
                    Collectors.summingDouble(TransactionInformation::getAmount)));
        };

        DefaultPieDataset<String> ds = new DefaultPieDataset<>();
        grouped.forEach(ds::setValue);

        ChartPanel newCp = new ChartPanel(
                ChartFactory.createPieChart(
                        "Spending by " + combo.getSelectedItem(), ds,
                        true, true, false));

        split.setLeftComponent(newCp);
        split.setDividerLocation(480);
    }

    private ChartPanel emptyChart() {
        DefaultPieDataset<String> ds = new DefaultPieDataset<>();
        ds.setValue("No Data", 1);
        return new ChartPanel(
                ChartFactory.createPieChart("Spending", ds, true, true, false));
    }
}
