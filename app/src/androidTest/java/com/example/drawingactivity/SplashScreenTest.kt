package com.example.drawingactivity

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.fragment.app.testing.launchFragmentInContainer
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test

class SplashScreenTestOld {

    /**
     * This test checks if the initial fragment is the SplashScreen
     */
//    @Test
//    fun onCreateView_displaysSplashScreen() {
//        val scenario = launchFragmentInContainer<SplashScreen>()
//        scenario.onFragment { fragment ->
//            assertNotNull(fragment.binding.root)
//        }
//    }

//    /**
//     * This test checks if the timer is set to switch to the MainScreen after 5 seconds
//     */
//    @Test
//    fun setTimer_switchesToMainScreen() {
//        val scenario = launchFragmentInContainer<SplashScreen>()
//        scenario.onFragment { fragment ->
//            Thread.sleep(5000) // Wait for 5 seconds
//        }
//    }

}
class SplashScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testSplashScreenUI() {
        composeTestRule.setContent {
            SplashScreenComposable { true }
        }

        composeTestRule
            .onNodeWithContentDescription("Paint Icon")
            .assertIsDisplayed()
    }
}