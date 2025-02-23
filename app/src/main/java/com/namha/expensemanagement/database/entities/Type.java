package com.namha.expensemanagement.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "types")
public class Type {
    @PrimaryKey(autoGenerate = false)
    public int id;

    public String type_name;
    public Type(int id, String type_name) {
        this.type_name = type_name;
        this.id = id;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getType_name() {
        return type_name;
    }
    public void setType_name(String type_name) {
        this.type_name = type_name;
    }
    @Override
    public String toString() {
        return type_name;
    }
}
