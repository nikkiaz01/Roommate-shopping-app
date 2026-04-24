package edu.uga.cs.roommateshoppingapp;


import java.util.List;

/**
 * Represents a group of items purchased together.
 * Stores the roommate who checked out and the total price including tax.
 */
public class PurchaseGroup {
    private String key;
    private String roommate;
    private List<ShoppingItem> items;
    private double totalWithTax;
    private long timestamp;

    public PurchaseGroup() {}

    public PurchaseGroup(String roommate, List<ShoppingItem> items, double totalWithTax) {
        this.roommate = roommate;
        this.items = items;
        this.totalWithTax = totalWithTax;
        this.timestamp = System.currentTimeMillis();
    }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
    public String getRoommate() { return roommate; }
    public void setRoommate(String roommate) { this.roommate = roommate; }
    public List<ShoppingItem> getItems() { return items; }
    public void setItems(List<ShoppingItem> items) { this.items = items; }
    public double getTotalWithTax() { return totalWithTax; }
    public void setTotalWithTax(double totalWithTax) { this.totalWithTax = totalWithTax; }
    public long getTimestamp() { return timestamp; }
}
