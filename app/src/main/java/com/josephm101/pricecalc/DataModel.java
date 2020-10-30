package com.josephm101.pricecalc;

import java.io.Serializable;

public class DataModel implements Serializable {
    String itemName;
    String itemPrice;
    Boolean isTaxable;
    String itemQuantity;

    public DataModel(String itemName, String itemPrice, Boolean isTaxDeductible, String itemQuantity) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.isTaxable = isTaxDeductible;
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
}