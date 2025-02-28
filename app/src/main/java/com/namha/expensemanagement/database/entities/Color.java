package com.namha.expensemanagement.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "colors")
public class Color {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name_color;

    public Color(String name_color) {
        this.name_color = name_color;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName_color() {
        return name_color;
    }

    public void setName_color(String name_color) {
        this.name_color = name_color;
    }

    @Override
    public String toString() {
        return name_color;
    }
}
