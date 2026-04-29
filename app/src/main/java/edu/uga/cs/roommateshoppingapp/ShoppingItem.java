package edu.uga.cs.roommateshoppingapp;

import java.io.Serializable;

/**
 * This class represents a single shopping item.
 * It stores the item name, quantity, and Firebase key.
 */
public class ShoppingItem implements Serializable  {

    private String key;
    private String itemName;
    private int quantity;

    /**
     * Default constructor required for Firebase.
     */
    public ShoppingItem() {
        this.key = null;
        this.itemName = null;
        this.quantity = 0;
    }

    /**
     * Constructor to create a shopping item.
     *
     * @param itemName name of the item
     * @param quantity quantity of the item
     */
    public ShoppingItem(String itemName, int quantity ) {
        this.key = null;
        this.itemName = itemName;
        this.quantity = quantity;
    }

    /**
     * Gets the Firebase key of the item.
     *
     * @return item key
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the Firebase key of the item.
     *
     * @param key unique key for the item
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Gets the item name.
     *
     * @return item name
     */
    public String getItemName() {
        return itemName;
    }

    /**
     * Sets the item name.
     *
     * @param itemName name of the item
     */
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    /**
     * Gets the quantity of the item.
     *
     * @return quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity of the item.
     *
     * @param quantity number of items
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Returns a simple string version of the item.
     *
     * @return item name and quantity
     */
    public String toString() {
        return itemName + " " + quantity;
    }
}