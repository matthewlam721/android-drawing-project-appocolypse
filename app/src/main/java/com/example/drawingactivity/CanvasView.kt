package com.example.drawingactivity

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

/**
 * Custom view for drawing
 * This is where the actual drawing happens.
 * It handles touch events and draws lines on a canvas based on user input.
 */
class CanvasView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var path = Path()
    private var paint = Paint()
    private var bitmap: Bitmap? = null
    private var canvas: Canvas? = null
    private var brushSize = 10f
    private var brushColor = Color.BLACK
    private var brushShape = "line"
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var translateX = 0f
    private var translateY = 0f
    private var isDragEnabled = false

    init {
        setupPaint()
    }

    /**
     * Set up the paint brush for user to draw.
     */
    private fun setupPaint() {
        paint.color = brushColor
        paint.isAntiAlias = true
        paint.isDither = true
        paint.style = Paint.Style.STROKE
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeWidth = brushSize
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val bitmapWidth = 3000 // Fixed width
        val bitmapHeight = 4000 // Fixed height

        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888)
            canvas = Canvas(bitmap!!)

            // Fill the bitmap with a solid color
            bitmap?.eraseColor(Color.WHITE)
        } else {
            val originalBitmap = bitmap
            bitmap = Bitmap.createScaledBitmap(originalBitmap!!, bitmapWidth, bitmapHeight, true)
            canvas = Canvas(bitmap!!)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val sourceRect = Rect(
            translateX.toInt(),
            translateY.toInt(),
            (width + translateX).toInt(),
            (height + translateY).toInt()
        ) // This is the upper left quarter of the bitmap
        val destRect = Rect(0, 0, width, height) // This is the entire canvas

        // canvas.drawBitmap(bitmap!!, 0f, 0f, null)
        canvas.drawBitmap(bitmap!!, sourceRect, destRect, null)
        canvas.drawPath(path, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                Log.e("Drawing", "isDragEnabled: $isDragEnabled")
                if (isDragEnabled) {
                    lastTouchX = x
                    lastTouchY = y
                } else {
                    if (brushShape == "circle") {
                        paint.style = Paint.Style.FILL
                        canvas?.drawCircle(x, y, brushSize, paint)
                    } else if (brushShape == "triangle") {
                        paint.style = Paint.Style.FILL
                        val path = Path()
                        path.moveTo(x, y - brushSize / 2)
                        path.lineTo(x - brushSize / 2, y + brushSize / 2)
                        path.lineTo(x + brushSize / 2, y + brushSize / 2)
                        path.close()
                        canvas?.drawPath(path, paint)
                    } else {
                        paint.style = Paint.Style.STROKE
                        path.moveTo(x, y)
                    }
                }
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                if (isDragEnabled) {
                    val dx = lastTouchX - x
                    val dy = lastTouchY - y

                    if (translateX + dx < 0 || translateX + dx > bitmap!!.width - width) {
                        return true
                    }
                    if (translateY + dy < 0 || translateY + dy > bitmap!!.height - height) {
                        return true
                    }

                    translateX += dx
                    translateY += dy
                    canvas?.translate(dx, dy)
                    lastTouchX = x
                    lastTouchY = y
                } else {
                    if (brushShape == "circle") {
                        canvas?.drawCircle(x, y, brushSize, paint)
                    } else if (brushShape == "triangle") {
                        paint.style = Paint.Style.FILL
                        val path = Path()
                        path.moveTo(x, y - brushSize / 2)
                        path.lineTo(x - brushSize / 2, y + brushSize / 2)
                        path.lineTo(x + brushSize / 2, y + brushSize / 2)
                        path.close()
                        canvas?.drawPath(path, paint)
                    } else {
                        path.lineTo(x, y)
                    }
                }
            }

            MotionEvent.ACTION_UP -> {
                if (!isDragEnabled && brushShape != "circle") {
                    canvas?.drawPath(path, paint)
                    path.reset()
                }
            }

            else -> return false
        }

        invalidate()
        return true
    }

    /**
     * Set the brush size for drawing.
     */
    fun setBrushSize(size: Float) {
        brushSize = size
        paint.strokeWidth = size
    }

    /**
     * Set the brush color for drawing.
     */
    fun setBrushColor(color: Int) {
        brushColor = color
        paint.color = color
    }

    /**
     * Set the brush shape for drawing.
     */
    fun setBrushShape(shape: String) {
        Log.e("Drawing", "Setting brush shape to $shape")
        brushShape = shape
    }

    fun toggleDrag(bool: Boolean) {
        isDragEnabled = bool
    }

    /**
     * Save the current state of the drawing.
     */
    fun saveState(): Bitmap? {
        return bitmap
    }

    /**
     * Restore the state of the drawing.
     */
    fun restoreState(savedBitmap: Bitmap?) {
        Log.e("Drawing", "Restoring state $savedBitmap")
        savedBitmap?.let {
            bitmap = it
            canvas = Canvas(bitmap!!)
            invalidate()
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        return if (superState != null) {
            val savedState = SavedState(superState)
            savedState.translateX = this.translateX
            savedState.translateY = this.translateY
            savedState.bitmap = this.bitmap
            savedState
        } else {
            throw IllegalStateException("Super state must not be null")
        }
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state is SavedState) {
            super.onRestoreInstanceState(state.superState)
            this.translateX = state.translateX
            this.translateY = state.translateY
            this.bitmap = state.bitmap
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    internal class SavedState : BaseSavedState {
        var translateX = 0f
        var translateY = 0f
        var bitmap: Bitmap? = null

        constructor(superState: Parcelable) : super(superState)

        private constructor(`in`: Parcel) : super(`in`) {
            this.translateX = `in`.readFloat()
            this.translateY = `in`.readFloat()
            this.bitmap = `in`.readParcelable(Bitmap::class.java.classLoader)
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeFloat(this.translateX)
            out.writeFloat(this.translateY)
            out.writeParcelable(this.bitmap, flags)
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(`in`: Parcel): SavedState {
                    return SavedState(`in`)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }
}