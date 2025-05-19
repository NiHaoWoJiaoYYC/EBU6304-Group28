package org.bupt.persosnalfinance.Front.AlertFront;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.bupt.persosnalfinance.Back.Response.BudgetDataResponse;
import org.bupt.persosnalfinance.Back.Response.BudgetResponse;
import org.bupt.persosnalfinance.dto.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class BudgetApp extends Application {
    private static final String API_BASE = "http://localhost:8080/api/budget";
    private String[] categories;
    private double[] lastQuarterAvg;
    private double[] thisQuarter;
    private double threshold = 0.18;

    private TableView<Spending> table;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        fetchData();

        // BarChart
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setCategories(FXCollections.observableArrayList(categories));
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Spending Comparison");
        chart.setLegendVisible(true);

        XYChart.Series<String, Number> seriesThis = new XYChart.Series<>();
        seriesThis.setName("This Quarter");
        XYChart.Series<String, Number> seriesLast = new XYChart.Series<>();
        seriesLast.setName("Last Quarter");

        for (int i = 0; i < categories.length; i++) {
            seriesThis.getData().add(new XYChart.Data<>(categories[i], thisQuarter[i]));
            seriesLast.getData().add(new XYChart.Data<>(categories[i], lastQuarterAvg[i]));
        }
        chart.getData().addAll(seriesThis, seriesLast);

        // fix size
        chart.setPrefSize(600, 350);
        chart.setMinSize(600, 350);
        chart.setMaxSize(600, 350);

        // Alerts panel
        Label alertTitle = new Label("⚠️ Overspending Alerts");
        alertTitle.getStyleClass().add("alert-title");

        VBox alertsBox = new VBox(8);
        alertsBox.setPadding(new Insets(5));
        alertsBox.getStyleClass().add("alerts-box");

        Slider slider = new Slider(0, 1, threshold);
        slider.setShowTickLabels(true);
        slider.setMajorTickUnit(0.1);
        slider.setBlockIncrement(0.01);
        slider.valueProperty().addListener((obs, oldV, newV) -> {
            threshold = newV.doubleValue();
            updateAlerts(alertsBox);
            updateTableStatus();
        });

        VBox rightPane = new VBox(10,
                alertTitle,
                alertsBox,
                new Label("Alert Threshold (%):"),
                slider
        );
        rightPane.setPadding(new Insets(10));
        rightPane.setPrefWidth(300);

        HBox topPane = new HBox(20, chart, rightPane);
        topPane.setPadding(new Insets(10));
        topPane.getStyleClass().add("top-pane");

        // TableView
        table = new TableView<>();
        table.getStyleClass().add("spending-table");

        TableColumn<Spending, String> colCat  = new TableColumn<>("Category");
        colCat.setCellValueFactory(cd -> cd.getValue().categoryProperty());
        TableColumn<Spending, Number> colLast = new TableColumn<>("Last Quarter Avg.");
        colLast.setCellValueFactory(cd -> cd.getValue().lastQuarterAvgProperty());
        TableColumn<Spending, Number> colThis = new TableColumn<>("This Quarter");
        colThis.setCellValueFactory(cd -> cd.getValue().thisQuarterProperty());
        TableColumn<Spending, String> colStat = new TableColumn<>("Status");
        colStat.setCellValueFactory(cd -> cd.getValue().statusProperty());

        // Status cell style
        colStat.setCellFactory(col -> new TableCell<Spending, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    getStyleClass().removeAll("status-normal", "status-overspending");
                } else {
                    setText(item);
                    getStyleClass().removeAll("status-normal", "status-overspending");
                    if (item.startsWith("Normal")) {
                        getStyleClass().add("status-normal");
                    } else {
                        getStyleClass().add("status-overspending");
                    }
                }
            }
        });

        table.getColumns().addAll(colCat, colLast, colThis, colStat);

        // initial data
        for (int i = 0; i < categories.length; i++) {
            Spending s = new Spending(categories[i], lastQuarterAvg[i], thisQuarter[i]);
            s.updateStatus(threshold);
            table.getItems().add(s);
        }

        // Layout
        VBox root = new VBox(15, topPane, table);
        root.setPadding(new Insets(15));
        root.getStyleClass().add("root-pane");

        Scene scene = new Scene(root, 1000, 700);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        updateAlerts(alertsBox);

        primaryStage.setTitle("Budget Tracking App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void fetchData() {
        RestTemplate rt = new RestTemplate();
        ResponseEntity<BudgetDataResponse> resp =
                rt.getForEntity(API_BASE + "/data", BudgetDataResponse.class);
        if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
            BudgetDataResponse d = resp.getBody();
            categories     = d.getCategories();
            lastQuarterAvg = d.getLastQuarterAvg();
            thisQuarter    = d.getThisQuarter();
        }
    }

    private void updateAlerts(VBox box) {
        box.getChildren().clear();
        RestTemplate rt = new RestTemplate();
        User user = new User();
        user.setLastQuarterAvg(lastQuarterAvg);
        user.setThisQuarter(thisQuarter);
        ResponseEntity<BudgetResponse> resp =
                rt.postForEntity(API_BASE + "/check?threshold=" + threshold, user, BudgetResponse.class);
        if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
            List<String> alerts = resp.getBody().getAlerts();
            for (String a : alerts) {
                if (a.contains("overspent")) {
                    Label lbl = new Label("⚠️  " + a);
                    lbl.getStyleClass().add("alert-item");
                    box.getChildren().add(lbl);
                }
            }
        }
    }

    private void updateTableStatus() {
        for (Spending s : table.getItems()) {
            s.updateStatus(threshold);
        }
        table.refresh();
    }
}