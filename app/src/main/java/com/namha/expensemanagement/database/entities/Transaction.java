package com.namha.expensemanagement.database.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "transactions",
        foreignKeys = {
                @ForeignKey(entity = DailyLimit.class,
                        parentColumns = "id",
                        childColumns = "id_daily_limit",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = MonthlyLimit.class,
                        parentColumns = "id",
                        childColumns = "id_monthly_limit",
                        onDelete = ForeignKey.CASCADE)
        }
)
public class Transaction {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private double amount;
    private int typeId;
    private String content;
    private String date;
    private int categoryId;
    private double totalBalance;

    @ColumnInfo(name = "id_daily_limit")
    private Integer idDailyLimit;

    @ColumnInfo(name = "id_monthly_limit")
    private Integer idMonthlyLimit;

    // Constructor
    public Transaction(double amount, int typeId, String content, String date, int categoryId, double totalBalance, Integer idDailyLimit, Integer idMonthlyLimit) {
        this.amount = amount;
        this.typeId = typeId;
        this.content = content;
        this.date = date;
        this.categoryId = categoryId;
        this.totalBalance = totalBalance;
        this.idDailyLimit = idDailyLimit;
        this.idMonthlyLimit = idMonthlyLimit;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
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

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public double getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(double totalBalance) {
        this.totalBalance = totalBalance;
    }

    public Integer getIdDailyLimit() {
        return idDailyLimit;
    }

    public void setIdDailyLimit(Integer idDailyLimit) {
        this.idDailyLimit = idDailyLimit;
    }

    public Integer getIdMonthlyLimit() {
        return idMonthlyLimit;
    }

    public void setIdMonthlyLimit(Integer idMonthlyLimit) {
        this.idMonthlyLimit = idMonthlyLimit;
    }

    @NonNull
    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", amount=" + amount +
                ", typeId=" + typeId +
                ", content='" + content + '\'' +
                ", date='" + date + '\'' +
                ", categoryId=" + categoryId +
                ", totalBalance=" + totalBalance +
                ", idDailyLimit=" + idDailyLimit +
                ", idMonthlyLimit=" + idMonthlyLimit +
                '}';
    }

}
