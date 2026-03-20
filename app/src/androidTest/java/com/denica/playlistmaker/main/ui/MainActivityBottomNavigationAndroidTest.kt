package com.denica.playlistmaker.main.ui

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.navigation.fragment.NavHostFragment
import com.denica.playlistmaker.KoinStartRule
import com.denica.playlistmaker.R
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityBottomNavigationAndroidTest {

    @get:Rule
    val koinRule = KoinStartRule()

    @Test
    fun bottomNavigation_switchesBetweenMainScreens() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            onView(withId(R.id.bottomNavigationView)).check(matches(isDisplayed()))

            onView(withId(R.id.searchFragment)).perform(click())
            assertCurrentDestination(scenario, R.id.searchFragment)

            onView(withId(R.id.settingsFragment)).perform(click())
            assertCurrentDestination(scenario, R.id.settingsFragment)

            onView(withId(R.id.mediaLibraryFragment)).perform(click())
            assertCurrentDestination(scenario, R.id.mediaLibraryFragment)
        }
    }

    private fun assertCurrentDestination(
        scenario: ActivityScenario<MainActivity>,
        expectedDestinationId: Int
    ) {
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        scenario.onActivity { activity ->
            val navHostFragment =
                activity.supportFragmentManager.findFragmentById(R.id.rootFragmentContainerView) as NavHostFragment
            val actualDestinationId = navHostFragment.navController.currentDestination?.id
            assertEquals(expectedDestinationId, actualDestinationId)
        }
    }
}
