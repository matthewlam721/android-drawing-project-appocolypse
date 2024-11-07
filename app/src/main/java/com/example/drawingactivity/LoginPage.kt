package com.example.drawingactivity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.drawingactivity.databinding.FragmentLoginScreenBinding
import com.example.drawingactivity.drawingdata.DrawingViewModel
import com.example.drawingactivity.drawingdata.DrawingViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class LoginPage : Fragment() {

    lateinit var binding: FragmentLoginScreenBinding
    private var email: String = "user@gmail.com"
    private var password: String = "useruser"
    private var mauth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflator: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginScreenBinding.inflate(layoutInflater)
        val drawingViewModel: DrawingViewModel by activityViewModels {
            DrawingViewModelFactory((requireActivity().application as DrawingApplication).DrawingtRepository)
        }
        mauth = FirebaseAuth.getInstance()

        binding.composableLogin.setContent {
            loginComposable(drawingViewModel)
        }

        Log.e("Drawing", "Login Page Created")
        return binding.root
    }

    @Composable
    fun loginComposable(
        drawingViewModel: DrawingViewModel
    ) {
        var passwordValue by remember { mutableStateOf("") }
        var emailValue by remember { mutableStateOf("") }

        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            ) {
                Text(
                    text = "Login",
                    color = Color.Black,
                    fontSize = 24.sp,
                )
                Spacer(modifier = Modifier.height(18.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Email",
                        color = Color.Black,
                    )
                    TextField(
                        // Set the TextField's value to the mutable state
                        value = emailValue,
                        // Update the mutable state when the value changes
                        onValueChange = { newValue ->
                            emailValue = newValue
                            email = newValue
                        },
                        label = { Text("Enter Email") }
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Password",
                        color = Color.Black,
                    )
                    TextField(
                        // Set the TextField's value to the mutable state
                        value = passwordValue,
                        // Update the mutable state when the value changes
                        onValueChange = { newValue ->
                            passwordValue = newValue
                            password = newValue
                        },
                        label = { Text("Enter Password") },
                        visualTransformation = PasswordVisualTransformation()
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                )
                {
                    Button(onClick = {
                        registerUser(email, password, drawingViewModel)
                    }) {
                        Text(text = "Register")
                    }
                    Button(
                        onClick = {
                            // Navigate to another fragment when button is clicked
                            loginUser(email, password)
                        }
                    ) {
                        Text("Login")
                    }
                }
            }
        }
    }

    /**
     * Register a user with email and password
     */
    fun registerUser(email: String, password: String, drawingViewModel: DrawingViewModel) {
        if (Email_Password_Not_Empty()) {
            mauth!!.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val currentUser = mauth!!.currentUser
                        if (currentUser != null) {
                            // The user is signed in
                            val userId = currentUser.uid  // This is the unique user ID
                            val email = currentUser.email // This is the user's email

                            // Add the user to the users collection
                            drawingViewModel.addUser(userId, email!!)
                        }
                        Toast.makeText(context, "Registration Success", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(
                            context,
                            "Error: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }

        }
    }

    /**
     * Login a user with email and password
     */
    fun loginUser(email: String, password: String) {
        if (Email_Password_Not_Empty()) {
            mauth!!.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Login Success", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_login_to_main)
                    } else {
                        Toast.makeText(
                            context,
                            "Error: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
        }
    }

    /**
     * Check if email and password are not empty
     */
    private fun Email_Password_Not_Empty(): Boolean {
        if (email.isEmpty() && password.isEmpty()) {
            Toast.makeText(
                context,
                "Please enter email and password",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        return true
    }

//    @Preview
//    @Composable
//    fun previewMain() {
//        loginComposable()
//    }
}