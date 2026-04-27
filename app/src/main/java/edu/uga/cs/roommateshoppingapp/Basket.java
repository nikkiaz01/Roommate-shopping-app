package edu.uga.cs.roommateshoppingapp;

import java.util.ArrayList;

public class Basket {
    private String key;
    private String roommate;
    private ArrayList<ShoppingItem> basketList;

    public Basket() {
        this.key = null;
        this.basketList = null;
        this.roommate = null;
    }

    public Basket(ArrayList<ShoppingItem> basketList, String roommate) {
        this.key = null;
        this.basketList = basketList;
        this.roommate = roommate;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getRoommate() {
        return roommate;
    }

    public void setRoommate(String roommate) {
        this.roommate = roommate;
    }

    public ArrayList<ShoppingItem> getBasketList() {
        return basketList;
    }

    public void setBasketList(ArrayList<ShoppingItem> basketItems) {
        this.basketList = basketItems;
    }

    public String toString() {
        return roommate + " " + basketList;
    }
}