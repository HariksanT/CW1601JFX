package com.cw1601;

public class TransactionRecord {
    private String itemCode;
    private double internalPrice, discount, salePrice, lineTotal, profit;
    private int quantity, checksum;
    private boolean valid;
    private String invalidReason = "";

    public TransactionRecord(String itemCode, double internalPrice, double discount,
                             double salePrice, int quantity, double lineTotal, int checksum) {
        this.itemCode = itemCode;
        this.internalPrice = internalPrice;
        this.discount = discount;
        this.salePrice = salePrice;
        this.quantity = quantity;
        this.lineTotal = lineTotal;
        this.checksum = checksum;
        calculateProfit();
        validate();
    }

    public void calculateProfit() {
        this.profit = (salePrice * quantity * (1 - discount)) - (internalPrice * quantity);
    }

    public int calculateChecksum() {
        // Match Python's string format (1 decimal place)
        String transactionString = String.format("%s%.1f%.1f%.1f%d%.1f",
                itemCode, internalPrice, discount, salePrice, quantity, lineTotal);

        int capital = (int) transactionString.chars().filter(Character::isUpperCase).count();
        int simple = (int) transactionString.chars().filter(Character::isLowerCase).count();
        int numbers = (int) transactionString.chars().filter(Character::isDigit).count();
        int decimals = (int) transactionString.chars().filter(c -> c == '.').count();

        return capital + simple + numbers + decimals;
    }

    public void validate() {
        valid = true;
        invalidReason = "";

        if (checksum != calculateChecksum()) {
            valid = false;
            invalidReason += "Checksum mismatch | ";
        }
        if (!itemCode.matches("[A-Za-z0-9_]+")) {
            valid = false;
            invalidReason += "Invalid item code | ";
        }
        if (internalPrice < 0 || salePrice < 0 || discount < 0 || discount > 1) {
            valid = false;
            invalidReason += "Invalid price/discount | ";
        }
    }

    // Getters and Setters
    public String getInvalidReason() { return invalidReason; }
    public String getItemCode() { return itemCode; }
    public void setItemCode(String itemCode) { this.itemCode = itemCode; }
    public double getInternalPrice() { return internalPrice; }
    public void setInternalPrice(double internalPrice) { this.internalPrice = internalPrice; }
    public double getDiscount() { return discount; }
    public void setDiscount(double discount) { this.discount = discount; }
    public double getSalePrice() { return salePrice; }
    public void setSalePrice(double salePrice) { this.salePrice = salePrice; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public int getChecksum() { return checksum; }
    public void setChecksum(int checksum) { this.checksum = checksum; }
    public boolean isValid() { return valid; }
    public double getProfit() { return profit; }
    public double getLineTotal() { return lineTotal; }
    public void recalculateChecksum() { this.checksum = calculateChecksum(); }
}