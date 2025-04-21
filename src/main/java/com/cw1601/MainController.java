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
    @FXML private Button browseBtn;
    @FXML private Button importBtn;
    @FXML private Button validateBtn;
    @FXML private Button deleteInvalidBtn;
    @FXML private Button deleteZeroProfitBtn;
    @FXML private TextField taxRateInput;
    @FXML private Button calcTaxBtn;
    @FXML private TableView<TransactionRecord> table;
    @FXML private TableColumn<TransactionRecord, String> colItem;
    @FXML private TableColumn<TransactionRecord, Double> colInternal;
    @FXML private TableColumn<TransactionRecord, Double> colSale;
    @FXML private TableColumn<TransactionRecord, Double> colDiscount;
    @FXML private TableColumn<TransactionRecord, Integer> colQty;
    @FXML private TableColumn<TransactionRecord, Integer> colChecksum;
    @FXML private TableColumn<TransactionRecord, Boolean> colValid;
    @FXML private TableColumn<TransactionRecord, Double> colProfit;
    @FXML private Label summaryLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        setupButtonHandlers();
    }

    private void setupTable() {
        table.setEditable(true);

        // Configure table columns
        colItem.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        colItem.setCellFactory(TextFieldTableCell.forTableColumn());
        colItem.setOnEditCommit(event -> {
            TransactionRecord record = event.getRowValue();
            record.setItemCode(event.getNewValue());
            controller.updateRecord(record);
            table.refresh();
            updateSummaryLabel();
        });

        colInternal.setCellValueFactory(new PropertyValueFactory<>("internalPrice"));
        colInternal.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        colInternal.setOnEditCommit(event -> {
            TransactionRecord record = event.getRowValue();
            record.setInternalPrice(event.getNewValue());
            controller.updateRecord(record);
            table.refresh();
            updateSummaryLabel();
        });

        colSale.setCellValueFactory(new PropertyValueFactory<>("salePrice"));
        colSale.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        colSale.setOnEditCommit(event -> {
            TransactionRecord record = event.getRowValue();
            record.setSalePrice(event.getNewValue());
            controller.updateRecord(record);
            table.refresh();
            updateSummaryLabel();
        });

        colDiscount.setCellValueFactory(new PropertyValueFactory<>("discount"));
        colDiscount.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        colDiscount.setOnEditCommit(event -> {
            TransactionRecord record = event.getRowValue();
            record.setDiscount(event.getNewValue());
            controller.updateRecord(record);
            table.refresh();
            updateSummaryLabel();
        });

        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colQty.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colQty.setOnEditCommit(event -> {
            TransactionRecord record = event.getRowValue();
            record.setQuantity(event.getNewValue());
            controller.updateRecord(record);
            table.refresh();
            updateSummaryLabel();
        });

        colChecksum.setCellValueFactory(new PropertyValueFactory<>("checksum"));
        colValid.setCellValueFactory(new PropertyValueFactory<>("valid"));
        colProfit.setCellValueFactory(new PropertyValueFactory<>("profit"));
    }

    private void setupButtonHandlers() {
        browseBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Tax Transaction CSV File");
            File selectedFile = fileChooser.showOpenDialog(browseBtn.getScene().getWindow());
            if (selectedFile != null) {
                filePathInput.setText(selectedFile.getAbsolutePath());
            }
        });

        importBtn.setOnAction(e -> {
            boolean success = controller.importCSV(filePathInput.getText());
            if (success) {
                table.setItems(controller.getTransactions());
                updateSummaryLabel();
                summaryLabel.setText("File imported successfully. Total records: " +
                        controller.getTotalRecords() +
                        ", Valid records: " + controller.getValidRecords() +
                        ", Invalid records: " + controller.getInvalidRecords());
            } else {
                summaryLabel.setText("Failed to import file. Check console for details.");
            }
        });

        validateBtn.setOnAction(e -> {
            controller.validateAllRecords();
            table.refresh();
            updateSummaryLabel();
            summaryLabel.setText("Validation complete. Valid records: " +
                    controller.getValidRecords() +
                    ", Invalid records: " + controller.getInvalidRecords());
        });

        deleteInvalidBtn.setOnAction(e -> {
            controller.deleteInvalidRecords();
            table.refresh();
            updateSummaryLabel();
            summaryLabel.setText("Invalid records deleted. Total records: " +
                    controller.getTotalRecords() +
                    ", Valid records: " + controller.getValidRecords());
        });

        deleteZeroProfitBtn.setOnAction(e -> {
            controller.deleteZeroProfitRecords();
            table.refresh();
            updateSummaryLabel();
            summaryLabel.setText("Zero profit records deleted. Total records: " +
                    controller.getTotalRecords());
        });

        calcTaxBtn.setOnAction(e -> {
            try {
                double taxRate = Double.parseDouble(taxRateInput.getText());
                double tax = controller.calculateFinalTax(taxRate);
                summaryLabel.setText("Final Tax: Rs. " + String.format("%.2f", tax));
            } catch (Exception ex) {
                summaryLabel.setText("Invalid tax rate.");
            }
        });
    }

    private void updateSummaryLabel() {
        summaryLabel.setText("Total records: " + controller.getTotalRecords() +
                ", Valid records: " + controller.getValidRecords() +
                ", Invalid records: " + controller.getInvalidRecords());
    }
}