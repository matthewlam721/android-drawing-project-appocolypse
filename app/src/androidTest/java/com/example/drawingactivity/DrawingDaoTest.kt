package com.example.drawingactivity.drawingdata

import android.graphics.Bitmap
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4
import android.content.Context
import com.google.android.gms.tasks.Tasks

@RunWith(AndroidJUnit4::class)
class DrawingRepositoryTest {
    private val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    private val appContext = ApplicationProvider.getApplicationContext<Context>()
    private lateinit var drawingRepository: DrawingRepository
    private val testScope = TestCoroutineScope()

    @Before
    fun setUp() {
        drawingRepository = DrawingRepository(testScope)
    }

    @Test
    fun testBitmapToString() {
        val result = drawingRepository.bitmapToString(bitmap)
        Assert.assertNotNull(result)
    }

    @Test
    fun testStringToBitmap() {
        val bitmapString = drawingRepository.bitmapToString(bitmap)
        val result = drawingRepository.stringToBitmap(bitmapString)
        Assert.assertNotNull(result)
    }


    @Test
    fun testSaveDrawing() {
        val title = "Test Drawing"
        val drawingData = DrawingData(title, bitmap)

        // Save the drawing
        val saveTask = drawingRepository.saveDrawing(drawingData, appContext)
        Tasks.await(saveTask)

        // Try to get the drawing
        val getTask = drawingRepository.getDrawing()
        val result = Tasks.await(getTask).find { it.title == title }

        // Verify that the drawing was saved correctly
        Assert.assertNotNull(result)
    }

    @Test
    fun testDeleteDrawing() {
        val title = "Test Drawing"
        val drawingData = DrawingData(title, bitmap)

        // Save the drawing
        val saveTask = drawingRepository.saveDrawing(drawingData, appContext)
        Tasks.await(saveTask)

        // Delete the drawing
        val deleteTask = drawingRepository.deleteDrawing(drawingData, appContext)
        Tasks.await(deleteTask)

        // Try to get the drawing
        val getTask = drawingRepository.getDrawing()
        val result = Tasks.await(getTask).find { it.title == title }

        // Verify that the drawing no longer exists
        Assert.assertNull(result)
    }
}