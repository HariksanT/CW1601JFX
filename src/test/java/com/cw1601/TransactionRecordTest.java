package com.cw1601;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TransactionRecordTest {

    @Test
    public void testCalculateProfit() {
        TransactionRecord record = new TransactionRecord(
                "ITEM123", 100.0, 0.1, 150.0, 2, 270.0, 0
        );
        // Profit = (150 * 2 * 0.9) - (100 * 2) = 270 - 200 = 70
        assertEquals(70.0, record.getProfit(), 0.001);
    }

    @Test
    public void testCalculateChecksum() {
        TransactionRecord record = new TransactionRecord(
                "Ab12",
                10.0,
                0.2,
                15.0,
                3,
                36.0,
                0
        );
        assertEquals(20, record.calculateChecksum());
    }

    @Test
    public void testValidation() {
        // Valid record
        TransactionRecord validRecord = new TransactionRecord(
                "VALID1", 50.0, 0.1, 60.0, 1, 54.0, 22
        );
        validRecord.recalculateChecksum();
        assertTrue(validRecord.isValid());

        // Invalid record (checksum mismatch)
        TransactionRecord invalidChecksum = new TransactionRecord(
                "ITEM123", 100.0, 0.1, 150.0, 2, 270.0, 999
        );
        assertFalse(invalidChecksum.isValid());

        // Invalid record (discount > 1)
        TransactionRecord invalidDiscount = new TransactionRecord(
                "ITEM123", 100.0, 1.5, 150.0, 2, 270.0, 0
        );
        assertFalse(invalidDiscount.isValid());
    }
}