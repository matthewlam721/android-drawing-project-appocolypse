package com.example.drawingactivity.drawingdata

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.util.Base64
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import java.io.ByteArrayOutputStream


/**
 * Manages the data sources and fetches data from the network.
 */
open class DrawingRepository(
    private val scope: CoroutineScope,
) {

    fun getAllDrawings(): Task<List<DrawingData>> {
        val userDrawingsTask = getDrawing()
        val sharedDrawingsTask = getSharedDrawings()

        return Tasks.whenAllSuccess<List<DrawingData>>(userDrawingsTask, sharedDrawingsTask)
            .continueWith { task ->
                if (task.isSuccessful) {
                    val result = task.result
                    val userDrawings = result[0] as List<DrawingData>
                    val sharedDrawings = result[1] as List<DrawingData>
                    userDrawings + sharedDrawings // Merge the two lists into one
                } else {
                    throw task.exception!!
                }
            }
    }

    fun getDrawing(): Task<List<DrawingData>> {
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        var userId = ""

        if (currentUser != null) {
            // The user is signed in
            userId = currentUser.uid  // This is the unique user ID
        } else {
            // No user is signed in
        }
        val drawingsList = mutableListOf<DrawingData>()

        return db.collection("drawings")
            .whereEqualTo("userId", userId)  // Add this line to filter by userId
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.e("DrawingRepository", "${document.id} => ${document.data}")
                    val bitmapString = document.data["bitmap"] as? String
                    val bitmap = if (bitmapString != null) stringToBitmap(bitmapString) else null
                    if (bitmap != null) {
                        val mutableBitmap = bitmap.copy(bitmap.config, true)
                        val title = document.id.split("_")[0]
                        val drawing = DrawingData(title, mutableBitmap)
                        drawingsList.add(drawing)
                    } else {
                        // Handle the case where the bitmap is null
                        Log.e(
                            "DrawingRepository",
                            "Failed to convert string to bitmap for document ${document.id}"
                        )
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("DrawingRepository", "Error getting documents.", exception)
            }
            .continueWith { task ->
                if (task.isSuccessful) {
                    drawingsList
                } else {
                    throw task.exception!!
                }
            }
    }

    fun getUserIdFromEmail(email: String, context: Context): Task<String> {
        val db = FirebaseFirestore.getInstance()

        return db.collection("users").document(email).get()
            .continueWithTask { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document != null && document.exists()) {
                        // The email is registered with Firebase Authentication
                        // Now, get the user ID
                        val userId = document.getString("userID")
                        Tasks.forResult(userId)
                    } else {
                        // The email is not registered with Firebase Authentication
                        (context as Activity).runOnUiThread {
                            Toast.makeText(
                                context,
                                "The email is not registered.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        Tasks.forException<String>(
                            Exception("The email is not registered.")
                        )
                    }
                } else {
                    Tasks.forException<String>(task.exception!!)
                }
            }
    }

    fun saveDrawing(drawing: DrawingData, context: Context): Task<Void> {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        Log.e("User in drawing repo:", currentUser.toString())
        var userId = ""

        if (currentUser != null) {
            // The user is signed in
            userId = currentUser.uid  // This is the unique user ID
        } else {
            // No user is signed in
        }
        val db = FirebaseFirestore.getInstance()
        val drawingData = mapOf(
            "bitmap" to bitmapToString(drawing.pic),
            "userId" to userId  // Include the userId in the document
        )

        Log.e("User in drawing repo:", userId)

        // Include the userId in the document ID
        val documentId = "${drawing.title}_$userId"

        return db.collection("drawings").document(documentId).set(drawingData)
            .addOnSuccessListener {
                (context as Activity).runOnUiThread {
                    Log.d("DrawingRepository", "DocumentSnapshot successfully written!")
                }
                Toast.makeText(context, "Drawing saved", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                (context as Activity).runOnUiThread {
                    Log.w("DrawingRepository", "Error writing document", e)
                }
                Toast.makeText(context, "Fail to save drawing: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    fun addUser(userId: String, email: String): Task<Void> {
        val db = FirebaseFirestore.getInstance()
        val userData = mapOf(
            "userID" to userId
        )

        return db.collection("users").document(email).set(userData)
            .addOnSuccessListener {
                Log.d("DrawingRepository", "User successfully added!")
            }
            .addOnFailureListener { e ->
                Log.w("DrawingRepository", "Error adding user", e)
            }
    }

    fun shareDrawing(drawing: DrawingData, userEmail: String, context: Context) {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        var currentUserId = ""

        if (currentUser != null) {
            // The user is signed in
            currentUserId = currentUser.uid  // This is the unique user ID
            Log.d("DrawingRepository", "Current user ID: $currentUserId")
        } else {
            // No user is signed in
            Log.d("DrawingRepository", "No user is currently signed in.")
        }

        Log.d("DrawingRepository", "Attempting to get user ID from email: $userEmail")

        getUserIdFromEmail(userEmail, context).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val shareToUserId = task.result
                if (shareToUserId != null) {
                    Log.d("DrawingRepository", "User ID from email: $shareToUserId")
                    val db = FirebaseFirestore.getInstance()
                    val drawingData = mapOf(
                        "bitmap" to bitmapToString(drawing.pic),
                        "shareToUserId" to shareToUserId  // Include the shareToUserId in the document
                    )

                    // Include the currentUserId in the document ID
                    val documentId = "${drawing.title}_${currentUserId}_shareTo_${shareToUserId}"

                    Log.d("DrawingRepository", "Attempting to write to document: $documentId")

                    db.collection("sharedDrawings").document(documentId).set(drawingData)
                        .addOnSuccessListener {
                            (context as Activity).runOnUiThread {
                                Log.d("DrawingRepository", "DocumentSnapshot successfully written!")
                                Toast.makeText(context, "Drawing shared", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .addOnFailureListener { e ->
                            (context as Activity).runOnUiThread {
                                Log.w("DrawingRepository", "Error writing document", e)
                                Toast.makeText(
                                    context,
                                    "Fail to share drawing: ${e.message}",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        }
                } else {
                    // Handle the case where the user ID is null
                    Log.e("DrawingRepository", "Failed to get user ID from email.")
                }
            } else {
                Toast.makeText(
                    context,
                    "Fail to share drawing: ${task.exception?.message}",
                    Toast.LENGTH_SHORT
                )
                    .show()
                Log.e("DrawingRepository", "Error getting user ID from email.", task.exception)
            }
        }
    }

    fun getSharedDrawings(): Task<List<DrawingData>> {
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        var userId = ""

        if (currentUser != null) {
            // The user is signed in
            userId = currentUser.uid  // This is the unique user ID
        } else {
            // No user is signed in
        }
        val drawingsList = mutableListOf<DrawingData>()

        return db.collection("sharedDrawings")
            .whereEqualTo("shareToUserId", userId)  // Filter by shareToUserId
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.e("DrawingRepository", "${document.id} => ${document.data}")
                    val bitmapString = document.data["bitmap"] as? String
                    val bitmap = if (bitmapString != null) stringToBitmap(bitmapString) else null
                    if (bitmap != null) {
                        val mutableBitmap = bitmap.copy(bitmap.config, true)
                        val title = document.id.split("_")[0]
                        val drawing = DrawingData(title, mutableBitmap)
                        drawingsList.add(drawing)
                    } else {
                        // Handle the case where the bitmap is null
                        Log.e(
                            "DrawingRepository",
                            "Failed to convert string to bitmap for document ${document.id}"
                        )
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("DrawingRepository", "Error getting documents.", exception)
            }
            .continueWith { task ->
                if (task.isSuccessful) {
                    drawingsList
                } else {
                    throw task.exception!!
                }
            }
    }


    fun bitmapToString(bitmap: Bitmap): String {
        // Create a new bitmap with the same dimensions as the original bitmap
        val newBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)

        // Create a canvas to draw on the new bitmap
        val canvas = Canvas(newBitmap)

        // Draw a white background on the canvas
        canvas.drawColor(Color.WHITE)

        // Draw the original bitmap onto the new bitmap
        canvas.drawBitmap(bitmap, 0f, 0f, null)

        // Now compress and encode the new bitmap instead of the original bitmap
        val baos = ByteArrayOutputStream()
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val bytes = baos.toByteArray()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    fun stringToBitmap(encodedString: String): Bitmap? {
        return try {
            val bytes = Base64.decode(encodedString, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun deleteDrawing(drawing: DrawingData, context: Context): Task<Void> {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        var userId = ""

        if (currentUser != null) {
            // The user is signed in
            userId = currentUser.uid  // This is the unique user ID
        } else {
            // No user is signed in
        }
        val db = FirebaseFirestore.getInstance()

        // Include the userId in the document ID
        val documentId = "${drawing.title}_$userId"

        return db.collection("drawings").document(documentId).delete()
            .addOnSuccessListener {
                (context as Activity).runOnUiThread {
                    Log.d("DrawingRepository", "DocumentSnapshot successfully deleted!")
                    Toast.makeText(context, "Drawing deleted", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                (context as Activity).runOnUiThread {
                    Log.w("DrawingRepository", "Error deleting document", e)
                    Toast.makeText(context, "Fail to delete drawing", Toast.LENGTH_SHORT).show()
                }
            }
    }
}

