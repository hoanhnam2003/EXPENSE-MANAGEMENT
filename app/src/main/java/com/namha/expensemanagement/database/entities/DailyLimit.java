package com.namha.expensemanagement.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "daily_limits")
public class DailyLimit {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public double money_day;

    public double money_day_setting;

    public DailyLimit(double money_day) {
        this.money_day = money_day;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getMoney_day() {
        return money_day;
    }

    public void setMoney_day(double money_day) {
        this.money_day = money_day;
    }

    public double getMoney_day_setting() {
        return money_day_setting;
    }

    public void setMoney_day_setting(double money_day_setting) {
        this.money_day_setting = money_day_setting;
    }

    @Override
    public String toString() {
        return "DailyLimit{" +
                "id=" + id +
                ", money_day=" + money_day +
                '}'
                + "money_day_setting=" + money_day_setting;
    }

}
