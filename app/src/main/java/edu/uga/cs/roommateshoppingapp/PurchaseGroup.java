package edu.uga.cs.roommateshoppingapp;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Represents a group of items purchased together.
 * Stores the roommate who made the purchase, the items, total price, and date.
 */
public class PurchaseGroup {

    private String key;
    private String roommate;
    private ArrayList<ShoppingItem> items;
    private double totalWithTax;
    private String timestamp;

    /**
     * Default constructor required for Firebase.
     */
    public PurchaseGroup() {}

    /**
     * Constructor to create a new purchase group.
     *
     * @param totalWithTax total price of the purchase including tax
     */
    public PurchaseGroup(double totalWithTax) {
        this.roommate = null;
        this.items = new ArrayList<>();
        this.totalWithTax = totalWithTax;
        this.timestamp = LocalDate.now().toString();
    }

    /**
     * Gets the database key for this purchase.
     *
     * @return purchase key
     */
    public String getKey() { return key; }

    /**
     * Sets the database key for this purchase.
     *
     * @param key unique key for the purchase
     */
    public void setKey(String key) { this.key = key; }

    /**
     * Gets the roommate who made the purchase.
     *
     * @return roommate name or email
     */
    public String getRoommate() { return roommate; }

    /**
     * Sets the roommate who made the purchase.
     *
     * @param roommate name or email of the roommate
     */
    public void setRoommate(String roommate) { this.roommate = roommate; }

    /**
     * Gets the list of items in this purchase.
     *
     * @return list of shopping items
     */
    public ArrayList<ShoppingItem> getItems() { return items; }

    /**
     * Sets the list of items in this purchase.
     *
     * @param items list of shopping items
     */
    public void setItems(ArrayList<ShoppingItem> items) { this.items = items; }

    /**
     * Gets the total price of the purchase including tax.
     *
     * @return total price
     */
    public double getTotalWithTax() { return totalWithTax; }

    /**
     * Sets the total price of the purchase.
     *
     * @param totalWithTax total price including tax
     */
    public void setTotalWithTax(double totalWithTax) { this.totalWithTax = totalWithTax; }

    /**
     * Gets the timestamp of when the purchase was created.
     *
     * @return date as a string
     */
    public String getTimestamp() { return timestamp; }
}