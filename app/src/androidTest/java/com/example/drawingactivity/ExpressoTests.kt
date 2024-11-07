//package com.example.drawingactivity
//
//
//import android.view.View
//import android.view.ViewGroup
//import androidx.test.espresso.Espresso.onView
//import androidx.test.espresso.action.ViewActions.*
//import androidx.test.espresso.assertion.ViewAssertions.*
//import androidx.test.espresso.matcher.ViewMatchers.*
//import androidx.test.ext.junit.rules.ActivityScenarioRule
//import androidx.test.ext.junit.runners.AndroidJUnit4
//import androidx.test.filters.LargeTest
//import org.hamcrest.Description
//import org.hamcrest.Matcher
//import org.hamcrest.Matchers.allOf
//import org.hamcrest.Matchers.`is`
//import org.hamcrest.TypeSafeMatcher
//import org.hamcrest.core.IsInstanceOf
//import org.junit.Rule
//import org.junit.Test
//import org.junit.runner.RunWith
//
//@LargeTest
//@RunWith(AndroidJUnit4::class)
//class ExpressoTests {
//
//    @Rule
//    @JvmField
//    var mActivityScenarioRule = ActivityScenarioRule(MainActivity::class.java)
//
//    @Test
//    fun expressoTests() {
//        // This is for waiting out the splash screen
//        Thread.sleep(5000)
//
//        val materialButton = onView(
//            allOf(
//                withId(R.id.go_drawing_screen), withText("Go To Drawing Screen"),
//                childAtPosition(
//                    childAtPosition(
//                        withId(R.id.fragment_container),
//                        0
//                    ),
//                    1
//                ),
//                isDisplayed()
//            )
//        )
//        materialButton.perform(click())
//
//        val view = onView(
//            allOf(
//                withId(R.id.canvasView),
//                withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
//                isDisplayed()
//            )
//        )
//        view.check(matches(isDisplayed()))
//
//        val materialButton2 = onView(
//            allOf(
//                withId(R.id.goBackButton), withText("Go Back"),
//                childAtPosition(
//                    childAtPosition(
//                        withId(R.id.toolbar),
//                        0
//                    ),
//                    0
//                ),
//                isDisplayed()
//            )
//        )
//        materialButton2.perform(click())
//
//        val button = onView(
//            allOf(
//                withId(R.id.go_drawing_screen), withText("Go To Drawing Screen"),
//                withParent(withParent(withId(R.id.fragment_container))),
//                isDisplayed()
//            )
//        )
//        button.check(matches(isDisplayed()))
//
//        val materialButton3 = onView(
//            allOf(
//                withId(R.id.go_drawing_screen), withText("Go To Drawing Screen"),
//                childAtPosition(
//                    childAtPosition(
//                        withId(R.id.fragment_container),
//                        0
//                    ),
//                    1
//                ),
//                isDisplayed()
//            )
//        )
//        materialButton3.perform(click())
//
//        val materialButton4 = onView(
//            allOf(
//                withId(R.id.customizeColorButton), withText("Customize"),
//                childAtPosition(
//                    childAtPosition(
//                        withClassName(`is`("android.widget.TableLayout")),
//                        3
//                    ),
//                    3
//                ),
//                isDisplayed()
//            )
//        )
//        materialButton4.perform(click())
//
//        val appCompatEditText = onView(
//            allOf(
//                withId(R.id.editTextRed),
//                childAtPosition(
//                    childAtPosition(
//                        withId(android.R.id.custom),
//                        0
//                    ),
//                    0
//                ),
//                isDisplayed()
//            )
//        )
//        appCompatEditText.perform(replaceText("0"), closeSoftKeyboard())
//
//        val appCompatEditText2 = onView(
//            allOf(
//                withId(R.id.editTextGreen),
//                childAtPosition(
//                    childAtPosition(
//                        withId(android.R.id.custom),
//                        0
//                    ),
//                    1
//                ),
//                isDisplayed()
//            )
//        )
//        appCompatEditText2.perform(replaceText("0"), closeSoftKeyboard())
//
//        val appCompatEditText3 = onView(
//            allOf(
//                withId(R.id.editTextBlue),
//                childAtPosition(
//                    childAtPosition(
//                        withId(android.R.id.custom),
//                        0
//                    ),
//                    2
//                ),
//                isDisplayed()
//            )
//        )
//        appCompatEditText3.perform(replaceText("0"), closeSoftKeyboard())
//
//        val materialButton5 = onView(
//            allOf(
//                withId(R.id.buttonCreateColor), withText("Create Color"),
//                childAtPosition(
//                    childAtPosition(
//                        withId(android.R.id.custom),
//                        0
//                    ),
//                    3
//                ),
//                isDisplayed()
//            )
//        )
//        materialButton5.perform(click())
//
//        val materialButton6 = onView(
//            allOf(
//                withId(R.id.saveButton), withText("Save"),
//                childAtPosition(
//                    childAtPosition(
//                        withId(R.id.toolbar),
//                        0
//                    ),
//                    2
//                ),
//                isDisplayed()
//            )
//        )
//        materialButton6.perform(click())
//
//        val materialButton7 = onView(
//            allOf(
//                withId(R.id.goBackButton), withText("Go Back"),
//                childAtPosition(
//                    childAtPosition(
//                        withId(R.id.toolbar),
//                        0
//                    ),
//                    0
//                ),
//                isDisplayed()
//            )
//        )
//        materialButton7.perform(click())
//
////        val recyclerView = onView(
////            allOf(
////                withId(R.id.recycler_view),
////                withParent(withParent(withId(R.id.fragment_container))),
////                isDisplayed()
////            )
////        )
////        recyclerView.check(matches(isDisplayed()))
//    }
//
//    /**
//     * returns a Matcher<View> object that matches a View at a specific position within a parent ViewGroup.
//     */
//    private fun childAtPosition(
//        parentMatcher: Matcher<View>, position: Int
//    ): Matcher<View> {
//
//        return object : TypeSafeMatcher<View>() {
//            override fun describeTo(description: Description) {
//                description.appendText("Child at position $position in parent ")
//                parentMatcher.describeTo(description)
//            }
//
//            public override fun matchesSafely(view: View): Boolean {
//                val parent = view.parent
//                return parent is ViewGroup && parentMatcher.matches(parent)
//                        && view == parent.getChildAt(position)
//            }
//        }
//    }
//}
