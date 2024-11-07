package com.example.drawingactivity
import androidx.test.core.app.ActivityScenario
import android.view.MotionEvent
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import android.util.AttributeSet

@RunWith(RobolectricTestRunner::class)
@Config(manifest=Config.NONE)
@LooperMode(LooperMode.Mode.PAUSED)
class CanvasViewTest {

    @Test
    fun testOnTouchEvent() {
        // Get the application context
        val scenario = ActivityScenario.launch(MainActivity::class.java)

        scenario.onActivity { activity ->
            // Create a real AttributeSet using Robolectric
            val attributeSet = Robolectric.buildAttributeSet().build()

            val canvasView = CanvasView(activity, attributeSet)

            // Create a MotionEvent
            val motionEvent = MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 50f, 50f, 0)

            // Call onTouchEvent
            canvasView.onTouchEvent(motionEvent)

            // Add assertions to check that the path was updated correctly
            // This is just a placeholder, replace it with the actual checks
            // Assert.assertEquals(expectedPath, canvasView.getPath())
        }
    }
}