package com.example.drawingactivity

import android.graphics.Bitmap
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.GridLayout
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.drawingactivity.databinding.FragmentDrawingScreenBinding
import com.example.drawingactivity.drawingdata.DrawingViewModel
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import kotlin.math.min


/**
 * This fragment acts as the UI layer in the MVVM architecture.
 * It's responsible for rendering the CanvasView and interacting with the user.
 * The fragment observes the LiveData from the ViewModel and updates the CanvasView accordingly.
 */
class DrawingScreen : Fragment() {
    private var _binding: FragmentDrawingScreenBinding? = null
    val binding get() = _binding!!
    val drawingViewModel: DrawingViewModel by activityViewModels()
    private var isDragEnabled = false

    /** C++ functions **/

    init {
        System.loadLibrary("drawingactivity")
    }

    external fun InvertColors_Bitmap(buffer: ByteBuffer, width: Int, height: Int)
    external fun AddNoise_Bitmap(buffer: ByteBuffer, width: Int, height: Int)

    /* End of C++ functions */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDrawingScreenBinding.inflate(layoutInflater)
        isDragEnabled = false
        drawingViewModel.toggleDrag(isDragEnabled)

        observeViewModel()  // Observe ViewModel LiveData
        UISetup()  // Set up the UI

        // Restore the drawing if available
        if (drawingViewModel.selectedDrawing.value != null) {
            val savedBitmap = drawingViewModel.selectedDrawing.value!!.pic
            savedBitmap?.let {
                // TODO: Scale the drawing with the canvas size
                binding.canvasView.restoreState(savedBitmap)
            }
            val title = drawingViewModel.selectedDrawing.value!!.title
            binding.drawingTitle.text = Editable.Factory.getInstance().newEditable(title)
        }
        if (drawingViewModel._tempSelectedDrawing != null) {
            Log.e("Drawing", "a: Restoring temp drawing${drawingViewModel._tempSelectedDrawing}")
            binding.canvasView.restoreState(drawingViewModel._tempSelectedDrawing)
        }

        Log.e("Drawing", "b: Drawing Screen Created")
        return binding.root
    }

    /**
     * Get the current bitmap on the canvas
     */
    fun getCurrentBitmapOnCanvas(): Bitmap? {
        return binding.canvasView.saveState()
    }

    /**
     * Observe the ViewModel LiveData
     * From viewModel, update the Canvas view
     */
    private fun observeViewModel() {
        drawingViewModel.penSize.observe(viewLifecycleOwner, Observer { size ->
            binding.canvasView.setBrushSize(size)
        })

        drawingViewModel.penColor.observe(viewLifecycleOwner, Observer { color ->
            binding.canvasView.setBrushColor(color)
        })

        drawingViewModel.colorChoices.observe(viewLifecycleOwner, Observer { colors ->
            setupColorSwatches()
        })

        drawingViewModel.penShape.observe(viewLifecycleOwner, Observer { shape ->
            binding.canvasView.setBrushShape(shape)
        })

        drawingViewModel.isDragEnabled.observe(viewLifecycleOwner, Observer { isDragEnabled ->
            Log.e("Drawing", "isDragEnabled in drawingscreen: $isDragEnabled")
            binding.canvasView.toggleDrag(isDragEnabled)
        })
    }

    /**
     * Set up the UI
     */
    private fun UISetup() {
        // Set up the top bar buttons
        topBar()
        // Set up the SeekBar to change the pen size
        seekBar()
        // Set up the shape bar buttons
        shapeBar()
        // Set up the bitmap bar buttons
        bitmapBar()

        binding.customizeColorButton.setOnClickListener() {
            Dialogs.showCustomColorDialog(this, drawingViewModel)
        }
    }

    /**
     * Set up the top bar buttons
     */
    private fun topBar() {
        // Go back button
        binding.goBackButton.setOnClickListener {
            Log.e("Drawing", "goBackButton Clicked")
            findNavController().navigate(R.id.select_drawing_action)
        }

        // Save button
        binding.saveButton.setOnClickListener {
            binding.drawingTitle.clearFocus()

            Log.e("Drawing", "saveButton Clicked")
            val currentBitmap = getCurrentBitmapOnCanvas()
            currentBitmap?.let {
                val pictureTitle = binding.drawingTitle.text.toString()
                lifecycleScope.launch {
                    drawingViewModel.saveDrawing(pictureTitle, it, requireContext())
                }
            }
        }

        // Drag button
        binding.dragButton.setOnClickListener {
            isDragEnabled = !isDragEnabled  // Toggle the state
            if (isDragEnabled) {
                binding.dragButton.text = "Drag Enabled"
                drawingViewModel.toggleDrag(true)
            } else {
                binding.dragButton.text = "Drag Disabled"
                drawingViewModel.toggleDrag(false)
            }
        }
    }

    /**
     * Set up the SeekBar to change the pen size
     */
    private fun seekBar() {
        binding.seekBarPenSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // Update the pen size in the ViewModel
                val newPenSize = progress.toFloat()
                drawingViewModel.setPenSize(newPenSize)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Implementation not needed
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Implementation not needed
            }
        })
    }

    /**
     * Set up the shape bar buttons
     */
    private fun shapeBar() {
        binding.StrokeButton.setOnClickListener() {
            drawingViewModel.setPenShape("stroke")
        }
        binding.CircleButton.setOnClickListener() {
            drawingViewModel.setPenShape("circle")
        }
        binding.TriangleButton.setOnClickListener() {
            drawingViewModel.setPenShape("triangle")
        }
    }

    /**
     * Set up the bitmap bar buttons
     */
    private fun bitmapBar() {

        // Invert color button
        binding.InvertColorButton.setOnClickListener {
            val currentBitmap = getCurrentBitmapOnCanvas()

            val requiredBufferSize =
                currentBitmap!!.width * currentBitmap.height * 4 // for ARGB_8888 bitmap
            val buffer = ByteBuffer.allocateDirect(requiredBufferSize)

            currentBitmap.copyPixelsToBuffer(buffer)
            InvertColors_Bitmap(buffer, currentBitmap.width, currentBitmap.height)
            buffer.rewind() // Reset the buffer's position to the start

            // Create a new bitmap and copy the inverted colors into it
            val invertedBitmap = Bitmap.createBitmap(
                currentBitmap.width,
                currentBitmap.height,
                Bitmap.Config.ARGB_8888
            )
            invertedBitmap.copyPixelsFromBuffer(buffer)

            // Update the CanvasView with the new inverted bitmap
            binding.canvasView.restoreState(invertedBitmap)
        }

        // Add noise button
        binding.AddNoiseButton.setOnClickListener {
            val currentBitmap = getCurrentBitmapOnCanvas()

            val requiredBufferSize =
                currentBitmap!!.width * currentBitmap.height * 4 // for ARGB_8888 bitmap
            val buffer = ByteBuffer.allocateDirect(requiredBufferSize)

            currentBitmap.copyPixelsToBuffer(buffer)
            AddNoise_Bitmap(buffer, currentBitmap.width, currentBitmap.height)
            buffer.rewind() // Reset the buffer's position to the start

            // Create a new bitmap and copy the noisy colors into it
            val noisyBitmap = Bitmap.createBitmap(
                currentBitmap.width,
                currentBitmap.height,
                Bitmap.Config.ARGB_8888
            )
            noisyBitmap.copyPixelsFromBuffer(buffer)

            // Update the CanvasView with the new noisy bitmap
            binding.canvasView.restoreState(noisyBitmap)
        }
    }

    /**
     * Set up the color swatches
     */
    private fun setupColorSwatches() {
        val gridLayout: GridLayout = binding.colorSwatchesGrid
        gridLayout.removeAllViews()
        val colors = drawingViewModel.colorChoices.value ?: emptyList()

        val viewTreeObserver = gridLayout.viewTreeObserver
        viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // Remove the listener to ensure it's only called once
                gridLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)

                colors.forEach { color ->
                    val colorSwatch = View(context).apply {
                        layoutParams = GridLayout.LayoutParams().apply {
                            val min = min(gridLayout.width, gridLayout.height)
                            width = min
                            height = min
                            setMargins(10, 10, 10, 10)
                        }
                        setBackgroundColor(color)

                        setOnClickListener {
                            // Update the pen color in the ViewModel
                            drawingViewModel.setPenColor(color)
                        }
                    }

                    gridLayout.addView(colorSwatch)
                }
            }
        })
    }
}
