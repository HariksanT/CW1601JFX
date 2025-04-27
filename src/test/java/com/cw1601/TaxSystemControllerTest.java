package com.cw1601;

import javafx.collections.ObservableList;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TaxSystemControllerTest {

    @Test
    public void testImportCSV() {
        TaxSystemController controller = new TaxSystemController();
        String testFilePath = "src/test/resources/test_transactions.csv"; // Create a sample CSV for testing
        boolean importSuccess = controller.importCSV(testFilePath);
        assertTrue(importSuccess);
        assertTrue(controller.getTotalRecords() > 0);
    }

    @Test
    public void testDeleteInvalidRecords() {
        TaxSystemController controller = new TaxSystemController();
        // Manually add valid and invalid records
        controller.getTransactions().add(new TransactionRecord("VALID1", 10.0, 0.1, 12.0, 1, 10.8, 22));
        controller.getTransactions().add(new TransactionRecord("INVALID", -5.0, 0.1, 12.0, 1, 10.8, 999));

        controller.validateAllRecords();
        assertEquals(1, controller.getInvalidRecords());

        controller.deleteInvalidRecords();
        assertEquals(1, controller.getTotalRecords());
        assertEquals(1, controller.getValidRecords());
    }

    @Test
    public void testCalculateFinalTax() {
        TaxSystemController controller = new TaxSystemController();

        // Create and validate records
        TransactionRecord record1 = new TransactionRecord("ITEM1", 100.0, 0.1, 150.0, 2, 270.0, 0);
        TransactionRecord record2 = new TransactionRecord("ITEM2", 50.0, 0.0, 60.0, 3, 180.0, 0);

        // Recalculate checksums and validate
        record1.recalculateChecksum();
        record2.recalculateChecksum();
        record1.validate();
        record2.validate();

        // Add to controller
        controller.getTransactions().addAll(record1, record2);

        // Calculate tax (10% rate)
        double tax = controller.calculateFinalTax(10.0);

        assertEquals(10.0, tax, 0.001);
    }
}