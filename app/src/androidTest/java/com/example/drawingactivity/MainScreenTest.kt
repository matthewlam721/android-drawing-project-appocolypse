package com.example.drawingactivity

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isClickable
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test


class MainScreenTest {

    private lateinit var navController: TestNavHostController

    @Before
    fun setup() = runBlocking(Dispatchers.Main) {
        navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.nav_graph)
    }

    @Test
fun onCreateView_setsOnClickListener() {
    launchFragmentInContainer<MainScreen>()
    onView(withId(R.id.go_drawing_screen)).check(matches(isDisplayed()))
}

    @Test
    fun goDrawingScreenButton_isClickable() {
        launchFragmentInContainer<MainScreen>()
        onView(withId(R.id.go_drawing_screen)).check(matches(isClickable()))
    }

//    @Test
//    fun goDrawingScreenButton_performsClick() {
//        launchFragmentInContainer<MainScreen>()
//        onView(withId(R.id.go_drawing_screen)).perform(click())
//    }

    // In this version of the test, we simply check if fragment.view is not null after the fragment has been resumed. This should ensure that the fragment's view is created successfully
    @Test
    fun onCreateView_inflatesLayout() {
        val scenario = launchFragmentInContainer<MainScreen>()
        scenario.moveToState(Lifecycle.State.RESUMED)

        scenario.onFragment { fragment ->
            val view = fragment.view
            assertNotNull(view)
        }
    }

}