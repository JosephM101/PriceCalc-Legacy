package com.josephm101.pricecalc.ListFileHandler.Common;

import com.josephm101.pricecalc.DataModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

public class ListInstance implements Serializable {
    private UUID uuid;
    private String ListName;
    public ArrayList<DataModel> ListItems;

    public ListInstance (UUID uuid, String ListName, ArrayList<DataModel> ListItems) {
        this.uuid = uuid;
        this.ListName = ListName;
        this.ListItems = ListItems;
    }

    public boolean setListFriendlyName(String newListName) {
        if (newListName.isEmpty()) {
            return false;
        } else {
            this.ListName = newListName;
            return true;
        }
    }

    public String getListFriendlyName() {
        return this.ListName;
    }

    public UUID getUuid() {
        return this.uuid;
    }
}
