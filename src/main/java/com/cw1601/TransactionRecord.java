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
    private double lineTotal;

    public TransactionRecord(String itemCode, double internalPrice, double discount, double salePrice, int quantity, int checksum) {
        this.itemCode = itemCode;
        this.internalPrice = internalPrice;
        this.discount = discount;
        this.salePrice = salePrice;
        this.quantity = quantity;
        this.checksum = checksum;
        this.valid = false;
        calculateProfit();
        calculateLineTotal();
    }

    public void calculateProfit() {
        this.profit = (internalPrice * quantity) - (salePrice * quantity - discount);
    }

    public void calculateLineTotal() {
        this.lineTotal = (salePrice * quantity) - discount;
    }

    public int calculateChecksum() {
        int upper = (int) itemCode.chars().filter(Character::isUpperCase).count();
        int lower = (int) itemCode.chars().filter(Character::isLowerCase).count();
        int numbers = (int) itemCode.chars().filter(Character::isDigit).count();

        String formatted = String.format("%.1f%.1f%.1f%.1f%d", internalPrice, discount, salePrice, lineTotal, quantity);
        int digits = (int) formatted.chars().filter(c -> Character.isDigit(c) || c == '.').count();

        return upper + lower + numbers + digits;
    }

    public void validate() {
        valid = true;

        if (checksum != calculateChecksum()) {
            valid = false;
        }

        if (!itemCode.matches("[A-Za-z0-9_]+")) {
            valid = false;
        }

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
        calculateLineTotal();
        validate();
    }

    public double getDiscount() { return discount; }
    public void setDiscount(double discount) {
        this.discount = discount;
        calculateProfit();
        calculateLineTotal();
        validate();
    }

    public double getSalePrice() { return salePrice; }
    public void setSalePrice(double salePrice) {
        this.salePrice = salePrice;
        calculateProfit();
        calculateLineTotal();
        validate();
    }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        calculateProfit();
        calculateLineTotal();
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
    public double getLineTotal() { return lineTotal; }
}
