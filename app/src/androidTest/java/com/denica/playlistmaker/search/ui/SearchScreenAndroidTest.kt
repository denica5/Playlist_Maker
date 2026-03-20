package com.denica.playlistmaker.search.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.fragment.NavHostFragment
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.denica.playlistmaker.KoinStartRule
import com.denica.playlistmaker.R
import com.denica.playlistmaker.main.ui.MainActivity
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchScreenAndroidTest {

    private val composeRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val ruleChain: TestRule = RuleChain
        .outerRule(KoinStartRule())
        .around(composeRule)

    @Test
    fun searchInput_isDisplayed_whenNavigatedToSearch() {
        composeRule.activityRule.scenario.onActivity { activity ->
            val navHostFragment =
                activity.supportFragmentManager.findFragmentById(R.id.rootFragmentContainerView) as NavHostFragment
            navHostFragment.navController.navigate(R.id.searchFragment)
        }

        composeRule.waitForIdle()
        composeRule.onNodeWithTag(SearchTestTags.Input).assertIsDisplayed()
    }
}
