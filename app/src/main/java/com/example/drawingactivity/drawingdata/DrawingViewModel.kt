package com.example.drawingactivity.drawingdata

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.drawingactivity.SplashScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// It holds the application's UI data in a lifecycle-conscious way, surviving configuration changes like screen rotations.
class DrawingViewModel(private val repository: DrawingRepository) : ViewModel() {

    private val _selectedDrawing = MutableLiveData<DrawingData>()
    val selectedDrawing: MutableLiveData<DrawingData> get() = _selectedDrawing

    private val listOfDrawings = MutableStateFlow<List<DrawingData>>(emptyList())

    /**
     * Expose the LiveData list of drawings for main screen
     */
    val viewList: StateFlow<List<DrawingData>> = listOfDrawings.asStateFlow()

    // LiveData for pen size
    private val _penSize = MutableLiveData<Float>()
    val penSize: LiveData<Float> get() = _penSize

    // LiveData for pen color
    private val _penColor = MutableLiveData<Int>()
    val penColor: LiveData<Int> get() = _penColor

    // LiveData for pen color
    private val _penShape = MutableLiveData<String>()
    val penShape: LiveData<String> get() = _penShape

    // LiveData for color choices
    private val _colorChoices = MutableLiveData<List<Int>>()
    val colorChoices: LiveData<List<Int>> get() = _colorChoices

    // LiveData for current fragment
    private var _currentFragment = MutableLiveData<Fragment>()
    val currentFragment: LiveData<Fragment> get() = _currentFragment

    // LiveData for drag enabled
    private var _isDragEnabled = MutableLiveData<Boolean>()
    val isDragEnabled: LiveData<Boolean> get() = _isDragEnabled

    // Create a bitmap and draw on it
    private val bitmap: Bitmap = createRedBitmap()

    private var userId: String = ""

    /**
     * Create a red bitmap
     */
    private fun createRedBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint()
        paint.color = Color.RED
        canvas.drawRect(0f, 0f, 100f, 100f, paint)
        return bitmap
    }

    fun setUserId(id: String) {
        userId = id
    }

    fun getUserId(): String {
        return userId
    }

    init {
        // Initialize with default values
        _penSize.value = 10f // Default pen size
        _penColor.value = 0xFF000000.toInt() // Default color (black)
        _penShape.value = "Pen"
        _colorChoices.value =
            listOf(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.CYAN, Color.MAGENTA)
        _currentFragment.value = SplashScreen()
        _isDragEnabled.value = false
//        repository.saveDrawing(DrawingData("Initial Drawing", bitmap))

        fetchData()
    }

    fun fetchData() {
        repository.getAllDrawings().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val drawingsList = task.result
                listOfDrawings.value = drawingsList
            } else {
                // Handle the error
                Log.e("DrawingViewModel", "Error getting drawings.", task.exception)
            }
        }
    }

    fun addUser(userId: String, email: String) {
        repository.addUser(userId, email)
    }

    fun shareDrawing(drawing: DrawingData, userId: String, context: Context) {
        repository.shareDrawing(drawing, userId, context)
    }

    /**
     * Update the pen size
     */
    fun setPenSize(size: Float) {
        _penSize.value = size
    }

    /**
     * Update the pen color
     */
    fun setPenColor(color: Int) {
        _penColor.value = color
    }

    /**
     * Update the pen color
     */
    fun setPenShape(shape: String) {
        _penShape.value = shape
    }

    /**
     * Update the color choices
     */
    fun addColorChoice(color: Int) {
        val currentList = _colorChoices.value?.toMutableList() ?: mutableListOf()
        currentList.removeAt(0)
        currentList.add(color)
        _colorChoices.value = currentList
    }

    fun toggleDrag(drag: Boolean) {
        _isDragEnabled.value = drag
    }

    /**
     * Save a drawing
     */
    fun saveDrawing(name: String, bitmap: Bitmap, context: Context) {
        repository.saveDrawing(DrawingData(name, bitmap), context)
    }

    /**
     * Delete a drawing
     */
    fun deleteDrawing(drawing: DrawingData, context: Context) {
        repository.deleteDrawing(drawing, context)
    }

    /**
     * Set the current fragment
     */
    fun setCurrentFragment(curFrag: Fragment) {
        _currentFragment.value = curFrag
    }

    var _tempSelectedDrawing: Bitmap? = null

    /**
     * Save the unsve drawing on rotation
     */
    fun setUnsaveOnRotation(bitmap: Bitmap?) {
        if (bitmap != null) {
            _tempSelectedDrawing = bitmap
        }
    }

    /**
     * Set the selected drawing
     */
    fun setSelectDrawing(drawing: DrawingData) {
        _selectedDrawing.value = drawing
    }
}

// This factory class allows us to define custom constructors for the view model
// in order to use LazyColumn
class DrawingViewModelFactory(private val repository: DrawingRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DrawingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DrawingViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
