package com.josephm101.pricecalc;

import java.io.Serializable;

public class DataModel implements Serializable {
    String itemName;
    String itemPrice;
    Boolean isTaxable;
    String itemQuantity;
    String itemUrl = "";
    String itemNotes = "";

    public DataModel(String itemName, String itemPrice, Boolean isTaxable, String itemQuantity, String itemUrl, String itemNotes) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.isTaxable = isTaxable;
        this.itemQuantity = itemQuantity;
        this.itemUrl = itemUrl;
        this.itemNotes = itemNotes;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public Boolean getIsTaxable() {
        return isTaxable;
    }

    public String getItemQuantity() {
        return itemQuantity;
    }

    public String getItemUrl() { return itemUrl; }

    public String getItemNotes() { return itemNotes; }

    // Item name, price, BoolTaxable, quantity
    // Use Integer.parseInt() to convert string to int.
}