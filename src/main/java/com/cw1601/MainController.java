package com.cw1601;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.FileChooser;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    private TaxSystemController controller = new TaxSystemController();

    @FXML private TextField filePathInput;
    @FXML private Button browseBtn, importBtn, validateBtn, deleteInvalidBtn, deleteZeroProfitBtn, calcProfitBtn, calcTaxBtn;
    @FXML private TextField taxRateInput;
    @FXML private TableView<TransactionRecord> table;
    @FXML private TableColumn<TransactionRecord, String> colItem;
    @FXML private TableColumn<TransactionRecord, Double> colInternal, colSale, colDiscount, colLineTotal, colProfit;
    @FXML private TableColumn<TransactionRecord, Integer> colQty, colChecksum;
    @FXML private TableColumn<TransactionRecord, Boolean> colValid;
    @FXML private Label summaryLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        setupButtonHandlers();
    }

    private class PercentageDoubleStringConverter extends DoubleStringConverter {
        @Override
        public String toString(Double value) {
            return (value == null) ? "" : String.format("%.0f%%", value * 100);
        }

        @Override
        public Double fromString(String value) {
            try {
                String cleaned = value.replace("%", "").trim();
                double val = Double.parseDouble(cleaned);
                return val >= 1 ? val / 100 : val;
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
    }

    private void setupTable() {
        table.setEditable(true);

        colItem.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        colItem.setCellFactory(TextFieldTableCell.forTableColumn());
        colItem.setOnEditCommit(event -> {
            event.getRowValue().setItemCode(event.getNewValue());
            controller.updateRecord(event.getRowValue());
            refreshTable();
        });

        colInternal.setCellValueFactory(new PropertyValueFactory<>("internalPrice"));
        colInternal.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        colInternal.setOnEditCommit(event -> {
            event.getRowValue().setInternalPrice(event.getNewValue());
            controller.updateRecord(event.getRowValue());
            refreshTable();
        });

        colSale.setCellValueFactory(new PropertyValueFactory<>("salePrice"));
        colSale.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        colSale.setOnEditCommit(event -> {
            event.getRowValue().setSalePrice(event.getNewValue());
            controller.updateRecord(event.getRowValue());
            refreshTable();
        });

        colDiscount.setCellValueFactory(new PropertyValueFactory<>("discount"));
        colDiscount.setCellFactory(TextFieldTableCell.forTableColumn(new PercentageDoubleStringConverter()));
        colDiscount.setOnEditCommit(event -> {
            event.getRowValue().setDiscount(event.getNewValue());
            controller.updateRecord(event.getRowValue());
            refreshTable();
        });

        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colQty.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colQty.setOnEditCommit(event -> {
            event.getRowValue().setQuantity(event.getNewValue());
            controller.updateRecord(event.getRowValue());
            refreshTable();
        });

        colChecksum.setCellValueFactory(new PropertyValueFactory<>("checksum"));
        colValid.setCellValueFactory(new PropertyValueFactory<>("valid"));
        colProfit.setCellValueFactory(new PropertyValueFactory<>("profit"));
        colLineTotal.setCellValueFactory(new PropertyValueFactory<>("lineTotal"));
    }

    private void setupButtonHandlers() {
        browseBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Tax Transaction CSV");
            File file = fileChooser.showOpenDialog(browseBtn.getScene().getWindow());
            if (file != null) filePathInput.setText(file.getAbsolutePath());
        });

        importBtn.setOnAction(e -> {
            if (controller.importCSV(filePathInput.getText())) {
                table.setItems(controller.getTransactions());
                updateSummary();
            } else {
                showAlert("Import Error", "Failed to import CSV file");
            }
        });

        validateBtn.setOnAction(e -> {
            controller.validateAllRecords();
            refreshTable();
            updateSummary();
        });

        deleteInvalidBtn.setOnAction(e -> {
            controller.deleteInvalidRecords();
            refreshTable();
            updateSummary();
        });

        deleteZeroProfitBtn.setOnAction(e -> {
            controller.deleteZeroProfitRecords();
            refreshTable();
            updateSummary();
        });

        calcTaxBtn.setOnAction(e -> {
            try {
                double taxRate = Double.parseDouble(taxRateInput.getText());
                double tax = controller.calculateFinalTax(taxRate);
                summaryLabel.setText(String.format("Tax Due: Rs.%.2f", tax));
            } catch (NumberFormatException ex) {
                showAlert("Input Error", "Invalid tax rate percentage");
            }
        });
    }

    @FXML
    private void handleCalcProfit() {
        double profit = controller.calculateTotalProfit();
        summaryLabel.setText(String.format("Total Profit: Rs.%.2f", profit));
    }

    private void refreshTable() {
        table.refresh();
        updateSummary();
    }

    private void updateSummary() {
        summaryLabel.setText(String.format(
                "Total: %d | Valid: %d | Invalid: %d",
                controller.getTotalRecords(),
                controller.getValidRecords(),
                controller.getInvalidRecords()
        ));
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}