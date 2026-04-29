package edu.uga.cs.roommateshoppingapp;

import java.util.ArrayList;

/**
 * This class represents a shopping basket.
 * It stores the roommate who owns the basket and the list of items in it.
 */
public class Basket {
    private String key;
    private String roommate;
    private ArrayList<ShoppingItem> basketList;

    /**
     * Default constructor.
     * Initializes fields to null.
     */
    public Basket() {
        this.key = null;
        this.basketList = null;
        this.roommate = null;
    }

    /**
     * Constructor to create a basket with items and a roommate.
     *
     * @param basketList list of shopping items in the basket
     * @param roommate name of the roommate who owns the basket
     */
    public Basket(ArrayList<ShoppingItem> basketList, String roommate) {
        this.key = null;
        this.basketList = basketList;
        this.roommate = roommate;
    }

    /**
     * Gets the database key for this basket.
     *
     * @return key of the basket
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the database key for this basket.
     *
     * @param key unique key for the basket
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Gets the roommate who owns the basket.
     *
     * @return roommate name
     */
    public String getRoommate() {
        return roommate;
    }

    /**
     * Sets the roommate who owns the basket.
     *
     * @param roommate name of the roommate
     */
    public void setRoommate(String roommate) {
        this.roommate = roommate;
    }

    /**
     * Gets the list of items in the basket.
     *
     * @return list of shopping items
     */
    public ArrayList<ShoppingItem> getBasketList() {
        return basketList;
    }

    /**
     * Sets the list of items in the basket.
     *
     * @param basketItems list of shopping items
     */
    public void setBasketList(ArrayList<ShoppingItem> basketItems) {
        this.basketList = basketItems;
    }

    /**
     * Returns a simple string representation of the basket.
     *
     * @return roommate name and basket items
     */
    public String toString() {
        return roommate + " " + basketList;
    }
}