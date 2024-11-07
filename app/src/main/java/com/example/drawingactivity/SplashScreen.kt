package com.example.drawingactivity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.drawingactivity.databinding.FragmentSplashScreenBinding

class SplashScreen : Fragment() {
    lateinit var binding: FragmentSplashScreenBinding

    override fun onCreateView(
        inflator: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSplashScreenBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        Log.e("Drawing", "Splash Screen Created")

        //ComposeView gives us a `Composable` context to run functions in
        binding.composeViewSplash.setContent {
            SplashScreenComposable(){
                Handler(Looper.getMainLooper()).postDelayed({
                    // Navigate to another fragment after 5 seconds
                    findNavController().navigate(R.id.start_up_action)
                }, 5000)
            }
        }

        return binding.root
    }

}

@Composable
fun SplashScreenComposable(function: () -> Boolean) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.paint_icon),
            contentDescription = "Paint Icon",
            modifier = Modifier
                .padding(16.dp)
                .size(300.dp),
            contentScale = ContentScale.Fit
        )
        function()
    }
}