package com.josephm101.pricecalc.ListFileHandler.Common

import com.josephm101.pricecalc.ListFileHandler.Common.ListInstance
import java.util.ArrayList

// List collection data model (for JSON serialization)
class ListInstanceArray {
    var lists: ArrayList<ListInstance>

    constructor() {
        lists = ArrayList()
    }

    constructor(lists: ArrayList<ListInstance>) {
        this.lists = lists
    }
}