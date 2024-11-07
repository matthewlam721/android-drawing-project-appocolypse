package com.example.drawingactivity.drawingdata

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "drawing")
data class DrawingData(val title: String, var pic: Bitmap) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0 // integer primary key for the DB
}
