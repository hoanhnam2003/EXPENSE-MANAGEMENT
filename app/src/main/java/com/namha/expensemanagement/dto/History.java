package com.namha.expensemanagement.dto;

public class History {
    private int id; // Thêm trường id
    private String nameCategory;
    private String content;
    private String date;
    private String typeName;
    private double amount;

    // Constructor, Getters và Setters

    public History(int id, String nameCategory, String content, String date, String typeName, double amount) {
        this.id = id;
        this.nameCategory = nameCategory;
        this.content = content;
        this.date = date;
        this.typeName = typeName;
        this.amount = amount;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNameCategory() {
        return nameCategory;
    }

    public void setNameCategory(String nameCategory) {
        this.nameCategory = nameCategory;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}

