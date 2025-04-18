package com.cw1601;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.BufferedReader;
import java.io.FileReader;

public class TaxSystemController {
    private ObservableList<TransactionRecord> transactions = FXCollections.observableArrayList();
    private int totalRecords = 0;
    private int validRecords = 0;
    private int invalidRecords = 0;

    public boolean importCSV(String filePath) {
        transactions.clear();
        totalRecords = 0;
        validRecords = 0;
        invalidRecords = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length >= 9) {
                    try {
                        // Extract fields from the correct positions
                        String itemCode = parts[2];
                        double internalPrice = Double.parseDouble(parts[3]);
                        double discount = Double.parseDouble(parts[4]);
                        double salePrice = Double.parseDouble(parts[5]);
                        int quantity = Integer.parseInt(parts[6]);
                        int checksum = Integer.parseInt(parts[8]);

                        TransactionRecord record = new TransactionRecord(itemCode, internalPrice, discount, salePrice, quantity, checksum);
                        record.validate();
                        transactions.add(record);
                        totalRecords++;

                        if (record.isValid()) {
                            validRecords++;
                        } else {
                            invalidRecords++;
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Skipping invalid line: " + line);
                    }
                }
            }
            return true;
        } catch (Exception e) {
            System.err.println("Failed to import CSV: " + e.getMessage());
            return false;
        }
    }

    public ObservableList<TransactionRecord> getTransactions() {
        return transactions;
    }

    public void validateAllRecords() {
        validRecords = 0;
        invalidRecords = 0;

        for (TransactionRecord record : transactions) {
            record.validate();
            if (record.isValid()) {
                validRecords++;
            } else {
                invalidRecords++;
            }
        }
    }

    public void deleteInvalidRecords() {
        transactions.removeIf(tr -> !tr.isValid());
        validateAllRecords();
    }

    public void deleteZeroProfitRecords() {
        transactions.removeIf(tr -> tr.getProfit() == 0);
        validateAllRecords();
    }

    public void updateRecord(TransactionRecord record) {
        record.recalculateChecksum();
        validateAllRecords();
    }

    public double calculateFinalTax(double taxRatePercent) {
        double totalProfit = transactions.stream()
                .filter(TransactionRecord::isValid)
                .mapToDouble(TransactionRecord::getProfit)
                .sum();
        return totalProfit * (taxRatePercent / 100);
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public int getValidRecords() {
        return validRecords;
    }

    public int getInvalidRecords() {
        return invalidRecords;
    }
}