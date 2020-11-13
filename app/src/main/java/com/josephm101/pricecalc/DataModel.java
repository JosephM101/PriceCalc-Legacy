package com.josephm101.pricecalc;

import java.io.Serializable;

public class DataModel implements Serializable {
    final String itemName;
    final String itemPrice;
    final Boolean isTaxable;
    final String itemQuantity;

    public DataModel(String itemName, String itemPrice, Boolean isTaxable, String itemQuantity) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.isTaxable = isTaxable;
        this.itemQuantity = itemQuantity;
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

    //Item name, price, BoolTaxable, quantity
    //Use Integer.parseInt() to convert string to int.
}