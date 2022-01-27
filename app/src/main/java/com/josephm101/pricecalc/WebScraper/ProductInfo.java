package com.josephm101.pricecalc.WebScraper;

public class ProductInfo {
    //enum Source {
    //    Amazon
    //}
    private String _name;
    private double _price;
    public ProductInfo(String name, double price)
    {
        _name = name;
        _price = price;
    }

    public double Price()
    {
        return _price;
    }
    public String Name()
    {
        return _name;
    }
}
