package com.namha.expensemanagement.database.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "settings",
        foreignKeys = @ForeignKey(
                entity = Color.class,
                parentColumns = "id",
                childColumns = "id_color",
                onDelete = ForeignKey.CASCADE
        )
)
public class Setting {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int zise_text;
    public int id_color;

    public Setting(int zise_text, int id_color) {
        this.zise_text = zise_text;
        this.id_color = id_color;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getZise_text() {
        return zise_text;
    }

    public void setZise_text(int zise_text) {
        this.zise_text = zise_text;
    }

    public int getId_color() {
        return id_color;
    }

    public void setId_color(int id_color) {
        this.id_color = id_color;
    }

    @Override
    public String toString() {
        return "Setting{" +
                "id=" + id +
                ", zise_text=" + zise_text +
                ", id_color=" + id_color +
                '}';
    }
}
