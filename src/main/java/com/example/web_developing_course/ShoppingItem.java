package com.example.web_developing_course;

public class ShoppingItem {
    private String name;
    private String info;
    private String date;
    private int price;
    private boolean paymentMethod;


    public ShoppingItem(String name, String info, String date, int price, boolean paymentMethod) {
        this.name = name;
        this.info = info;
        this.date = date;
        this.price = price;
        this.paymentMethod = paymentMethod;
    }

    public String getName(){
        return name;
    }

    public String getInfo() {
        return info;
    }

    public String getDate() {
        return date;
    }

    public String getPrice() {
        return String.valueOf(price)+",- Ft";
    }

    public boolean isPaymentMethod() {
        return paymentMethod;
    }
}
