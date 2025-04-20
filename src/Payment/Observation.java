import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Observation extends JFrame {
    private List<Record> records = new ArrayList<>();
    private ChartPanel chartPanel;
    private JTextArea conclusionArea;
    private JComboBox<String> comboBox;
    private DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy/M/d");

    public Observation() {
        super("Personal Wealth Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        loadRecords();

        // Top panel with classification combo
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Classify by:"));
        comboBox = new JComboBox<>(new String[]{"Categories", "Payment", "Month"});
        comboBox.setSelectedIndex(0);
        comboBox.addActionListener(this::onComboChanged);
        topPanel.add(comboBox);

        // Chart panel in center
        chartPanel = new ChartPanel();
        updateChart((String) comboBox.getSelectedItem());

        // Bottom panel with AI conclusion
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bottomPanel.add(new JLabel("AI Conclusion:"), BorderLayout.NORTH);
        conclusionArea = new JTextArea("Hello", 4, 30);
        conclusionArea.setLineWrap(true);
        conclusionArea.setWrapStyleWord(true);
        bottomPanel.add(new JScrollPane(conclusionArea), BorderLayout.CENTER);

        // Layout frame
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(topPanel, BorderLayout.NORTH);
        getContentPane().add(chartPanel, BorderLayout.CENTER);
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);
    }

    private void onComboChanged(ActionEvent e) {
        updateChart((String) comboBox.getSelectedItem());
    }

    private void updateChart(String method) {
        Map<String, Double> aggregated;
        switch (method) {
            case "Payment":
                aggregated = records.stream()
                        .collect(Collectors.groupingBy(r -> r.payment,
                                Collectors.summingDouble(r -> r.expenditure)));
                break;
            case "Month":
                aggregated = records.stream()
                        .collect(Collectors.groupingBy(r -> LocalDate.parse(r.date, fmt).getMonth().toString(),
                                Collectors.summingDouble(r -> r.expenditure)));
                break;
            case "Categories":
            default:
                aggregated = records.stream()
                        .collect(Collectors.groupingBy(r -> r.category,
                                Collectors.summingDouble(r -> r.expenditure)));
                break;
        }
        chartPanel.setData(aggregated);
    }

    private void loadRecords() {
        // Define data array directly, excluding Income entries
        String[][] data = {
                {"2024/1/2","Food","WX","213"},
                {"2024/1/3","Housing/Rent","Alipay","2313"},
                {"2024/1/4","Daily Necessities","WX","67"},
                {"2024/1/5","Shopping","WX","87"},
                {"2024/1/6","Childcare","WX","55"},
                {"2024/1/7","Transportation","CC","44"},
                {"2024/1/8","Daily Necessities","WX","65"},
                {"2024/1/9","Savings","Alipay","33"},
                {"2024/1/10","Gifts","Alipay","66"},
                {"2024/1/11","Daily Necessities","CC","77"},
                {"2024/1/12","Transportation","WX","88"},
                {"2024/1/13","Daily Necessities","WX","33"},
                {"2024/1/14","Shopping","Alipay","11"},
                {"2024/1/15","Daily Necessities","WX","34"},
                {"2024/1/16","Transportation","WX","22"},
                {"2024/1/18","Childcare","Alipay","1000"},
                {"2024/1/19","Daily Necessities","WX","224"},
                {"2024/1/20","Transportation","WX","22"},
                {"2024/1/21","Daily Necessities","WX","113"},
                {"2024/1/22","Shopping","CC","3134"},
                {"2024/1/23","Gifts","Alipay","123"},
                {"2024/1/24","Daily Necessities","WX","1334"},
                {"2024/1/25","Childcare","Alipay","13"},
                {"2024/1/26","Daily Necessities","Alipay","223"},
                {"2024/1/27","Savings","Alipay","42"},
                {"2024/1/28","Transportation","CC","131"},
                {"2024/1/29","Daily Necessities","CC","443"},
                {"2024/1/30","Daily Necessities","CC","4"},
                {"2024/1/31","Childcare","CC","23"},
                {"2024/2/1","Daily Necessities","CC","34"},
                {"2024/2/2","Savings","CC","110"},
                {"2024/2/3","Transportation","WX","5"},
                {"2024/2/4","Shopping","WX","400"},
                {"2024/2/5","Gifts","CC","3000"},
                {"2024/2/6","Shopping","CC","323"},
                {"2024/2/7","Transportation","CC","50"},
                {"2024/2/8","Daily Necessities","CC","34"},
                {"2024/2/9","Childcare","CC","990"},
                {"2024/2/10","Shopping","CC","34"},
                {"2024/2/11","Transportation","WX","35"},
                {"2024/2/12","Shopping","CC","345"},
                {"2024/2/13","Daily Necessities","CC","24"},
                {"2024/2/14","Daily Necessities","CC","244"},
                {"2024/2/15","Shopping","CC","67"},
                {"2024/2/16","Daily Necessities","CC","89"},
                {"2024/2/18","Shopping","WX","99"}
        };
        for (String[] row : data) {
            records.add(new Record(row[0], row[1], row[2], Double.parseDouble(row[3])));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Observation().setVisible(true));
    }

    // Inner record class
    private static class Record {
        String date;
        String category;
        String payment;
        double expenditure;
        Record(String d, String c, String p, double e) {
            date = d;
            category = c;
            payment = p;
            expenditure = e;
        }
    }

    // Custom panel to draw pie chart
    private static class ChartPanel extends JPanel {
        private Map<String, Double> data = Map.of();

        void setData(Map<String, Double> map) {
            data = map;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g0) {
            super.paintComponent(g0);
            if (data == null || data.isEmpty()) return;
            Graphics2D g = (Graphics2D) g0;
            int width = getWidth();
            int height = getHeight();
            int size = Math.min(width, height) - 50;
            int x = (width - size) / 2;
            int y = (height - size) / 2;
            double total = data.values().stream().mapToDouble(Double::doubleValue).sum();
            int startAngle = 0;
            for (Map.Entry<String, Double> entry : data.entrySet()) {
                int angle = (int) Math.round(entry.getValue() / total * 360);
                g.setColor(new Color((float)Math.random(), (float)Math.random(), (float)Math.random()));
                g.fillArc(x, y, size, size, startAngle, angle);
                double mid = startAngle + angle / 2.0;
                double rad = Math.toRadians(-mid);
                int cx = x + size/2;
                int cy = y + size/2;
                int rx = (int)(cx + (size/2 + 15) * Math.cos(rad));
                int ry = (int)(cy + (size/2 + 15) * Math.sin(rad));
                g.setColor(Color.BLACK);
                String label = String.format("%s(%.0f)", entry.getKey(), entry.getValue());
                g.drawString(label, rx, ry);
                startAngle += angle;
            }
        }
    }
}
