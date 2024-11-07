package com.example.drawingactivity

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.drawingactivity.databinding.ActivityMainBinding
import com.example.drawingactivity.drawingdata.DrawingViewModel
import com.example.drawingactivity.drawingdata.DrawingViewModelFactory

internal fun Context.findActivity(): ComponentActivity {
    var context = this
    while (context is ContextWrapper) {
        if (context is ComponentActivity) return context
        context = context.baseContext
    }
    throw IllegalStateException("Permissions should be called in the context of an Activity")
}

class MainActivity : AppCompatActivity() {
    private lateinit var fragDrawingScreen: DrawingScreen
    val drawingViewModel: DrawingViewModel by viewModels{
        DrawingViewModelFactory((application as DrawingApplication).DrawingtRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)

        if (!this::fragDrawingScreen.isInitialized) {
            fragDrawingScreen = DrawingScreen()
        }

        if (savedInstanceState != null) {
            val savedBitmap = savedInstanceState.getParcelable<Bitmap>("canvasState")
            drawingViewModel.setUnsaveOnRotation(savedBitmap)
        }

        setContentView(binding.root)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // Check if fragDrawingScreen is initialized and is currently added to the activity
        if (this::fragDrawingScreen.isInitialized && fragDrawingScreen.isAdded) {
            // Save the state of the CanvasView
            val currentBitmap = fragDrawingScreen.getCurrentBitmapOnCanvas()
            outState.putParcelable("canvasState", currentBitmap)
        }
    }

}