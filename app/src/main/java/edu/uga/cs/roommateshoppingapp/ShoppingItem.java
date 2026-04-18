package edu.uga.cs.roommateshoppingapp;

/**
 * This class represents a single shopping item, including the item name and
 * quantity.
 */
public class ShoppingItem {
    private String key;
    private String itemName;
    private int quantity;

    public ShoppingItem()
    {
        this.key = null;
        this.itemName = null;
        this.quantity = 0;
    }

    public ShoppingItem(String itemName, int quantity ) {
        this.key = null;
        this.itemName = itemName;
        this.quantity = quantity;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    public String toString() {
        return itemName + " " + quantity;
    }
}
