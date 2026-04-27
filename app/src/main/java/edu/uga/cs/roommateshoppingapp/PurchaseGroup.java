package edu.uga.cs.roommateshoppingapp;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a group of items purchased together.
 * Stores the roommate who checked out and the total price including tax.
 */
public class PurchaseGroup {
    private String key;
    private String roommate;
    private ArrayList<ShoppingItem> items;
    private double totalWithTax;
    private String timestamp;

    public PurchaseGroup() {}

    public PurchaseGroup(double totalWithTax) {
        this.roommate = null;
        this.items = new ArrayList<>();
        this.totalWithTax = totalWithTax;
        this.timestamp = LocalDate.now().toString();
    }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
    public String getRoommate() { return roommate; }
    public void setRoommate(String roommate) { this.roommate = roommate; }
    public ArrayList<ShoppingItem> getItems() { return items; }
    public void setItems(ArrayList<ShoppingItem> items) { this.items = items; }
    public double getTotalWithTax() { return totalWithTax; }
    public void setTotalWithTax(double totalWithTax) { this.totalWithTax = totalWithTax; }
    public String getTimestamp() { return timestamp; }
}
