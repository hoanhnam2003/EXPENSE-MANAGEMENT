package com.namha.expensemanagement.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "monthly_limits")
public class MonthlyLimit {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public double money_month;

    public double money_month_setting;

    public MonthlyLimit(double money_month) {
        this.money_month = money_month;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getMoney_month() {
        return money_month;
    }

    public void setMoney_month(double money_month) {
        this.money_month = money_month;
    }

    public double getMoney_month_setting() {
        return money_month_setting;
    }

    public void setMoney_month_setting(double money_month_setting) {
        this.money_month_setting = money_month_setting;
    }

    @Override
    public String toString() {
        return "MonthlyLimit{" +
                "id=" + id +
                ", money_month=" + money_month +
                '}' + "money_month_setting=" + money_month_setting;
    }
}