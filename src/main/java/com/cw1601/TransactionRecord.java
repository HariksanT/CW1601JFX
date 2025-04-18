package com.cw1601;

public class TransactionRecord {
    private String itemCode;
    private double internalPrice;
    private double discount;
    private double salePrice;
    private int quantity;
    private int checksum;
    private boolean valid;
    private double profit;

    public TransactionRecord(String itemCode, double internalPrice, double discount, double salePrice, int quantity, int checksum) {
        this.itemCode = itemCode;
        this.internalPrice = internalPrice;
        this.discount = discount;
        this.salePrice = salePrice;
        this.quantity = quantity;
        this.checksum = checksum;
        this.valid = false;
        calculateProfit();
    }

    public void calculateProfit() {
        // Corrected profit calculation based on the requirement:

        this.profit = (internalPrice * quantity) - (salePrice * quantity - discount);
    }

    public int calculateChecksum() {
        int upper = (int) itemCode.chars().filter(Character::isUpperCase).count();
        int lower = (int) itemCode.chars().filter(Character::isLowerCase).count();
        int numbers = (int) itemCode.chars().filter(Character::isDigit).count();

        // Format like Python: 1 decimal place floats
        String formatted = String.format("%.1f%.1f%.1f%d", internalPrice, discount, salePrice, quantity);
        int digits = (int) formatted.chars().filter(c -> Character.isDigit(c) || c == '.').count();

        return upper + lower + numbers + digits;
    }

    public void validate() {
        valid = true;

        // Check if the checksum matches
        if (checksum != calculateChecksum()) {
            valid = false;
        }

        // Check if the item code contains only valid characters (letters, numbers, underscore)
        if (!itemCode.matches("[A-Za-z0-9_]+")) {
            valid = false;
        }

        // Check if prices are non-negative
        if (internalPrice < 0 || salePrice < 0) {
            valid = false;
        }
    }

    // Getters and setters
    public String getItemCode() { return itemCode; }
    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
        validate();
    }

    public double getInternalPrice() { return internalPrice; }
    public void setInternalPrice(double internalPrice) {
        this.internalPrice = internalPrice;
        calculateProfit();
        validate();
    }

    public double getDiscount() { return discount; }
    public void setDiscount(double discount) {
        this.discount = discount;
        calculateProfit();
        validate();
    }

    public double getSalePrice() { return salePrice; }
    public void setSalePrice(double salePrice) {
        this.salePrice = salePrice;
        calculateProfit();
        validate();
    }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        calculateProfit();
        validate();
    }

    public int getChecksum() { return checksum; }
    public void setChecksum(int checksum) {
        this.checksum = checksum;
    }

    public void recalculateChecksum() {
        this.checksum = calculateChecksum();
        validate();
    }

    public boolean isValid() { return valid; }
    public double getProfit() { return profit; }
}