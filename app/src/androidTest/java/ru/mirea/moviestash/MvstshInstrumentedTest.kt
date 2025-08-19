package ru.mirea.moviestash

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers.allOf
import org.junit.Rule

import org.junit.Test
import org.junit.runner.RunWith
import ru.mirea.moviestash.presentation.MainActivity

import java.util.concurrent.TimeUnit

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class MvstshInstrumentedTest {

    @get:Rule
    var activityRule = ActivityScenarioRule<MainActivity>(MainActivity::class.java)

    @Test
    fun logInAppTest() {
        onView(withId(R.id.fragment_account_holder)).perform(ViewActions.click())
        try {
            onView(withId(R.id.buttonExit)).check(matches(isDisplayed()))
            onView(withId(R.id.buttonExit)).perform(ViewActions.click())
        }
        catch (e: NoMatchingViewException) {

        }
        logInIfNeeded()
    }

    @Test
    fun addCollectionTest() {
        logInIfNeeded()
        onView(withId(R.id.fragment_account_holder)).perform(ViewActions.click())
        onView(withId(R.id.buttonPersonalCollections))
            .perform(ViewActions.click())
        onView(withId(R.id.floating_action_button_add_collection))
            .perform(ViewActions.click())
        onView(withId(R.id.editTextCollectionName))
            .perform(
                ViewActions.typeText(TEST_COLLECTION_NAME),
                ViewActions.closeSoftKeyboard()
            )
        onView(withText(R.string.save)).perform(ViewActions.click())
        onView(allOf(withId(R.id.colName), withText(TEST_COLLECTION_NAME)))
            .check(matches(isDisplayed()))
        onView(allOf(withId(R.id.colName), withText(TEST_COLLECTION_NAME)))
            .perform(ViewActions.longClick())
        onView(withText(R.string.delete)).perform(ViewActions.click())
        TimeUnit.SECONDS.sleep(1)
        onView(allOf(withId(R.id.colName), withText(TEST_COLLECTION_NAME)))
            .check(doesNotExist())
    }

    @Test
    fun searchContentTest() {
        onView(withId(R.id.searchTrigger)).perform(ViewActions.click())
        onView(withId(R.id.edit_text_search_query))
            .perform(
                ViewActions.replaceText(TEST_SEARCH_INPUT),
                ViewActions.closeSoftKeyboard()
            )
        TimeUnit.SECONDS.sleep(5)
        onView(allOf(withId(R.id.searchName), withText(TEST_SEARCH_RESULT)))
            .check(matches(isDisplayed()))
    }

    @Test
    fun addNewsTest() {
        logInIfNeeded()

        onView(withId(R.id.fragment_news_list)).perform(ViewActions.click())
        TimeUnit.SECONDS.sleep(1)
        onView(withId(R.id.addButton)).perform(ViewActions.click())
        onView(withId(R.id.edit_text_news_title))
            .perform(ViewActions.typeText(TEST_NEWS_HEADER), ViewActions.closeSoftKeyboard())
        onView(withId(R.id.edit_text_news_description))
            .perform(ViewActions.typeText(TEST_NEWS_TEXT), ViewActions.closeSoftKeyboard())
        onView(withId(R.id.button_save_news)).perform(ViewActions.click())
        onView(withId(R.id.swipeRefreshLayoutNewsList)).perform(ViewActions.swipeDown())
        TimeUnit.SECONDS.sleep(1)
        onView(withId(R.id.newsRcVw))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(0))
        onView(allOf(withId(R.id.text_view_title_item_news), withText(TEST_NEWS_HEADER)))
            .perform(ViewActions.click())
        onView(withId(R.id.floating_action_buttons_delete_news))
            .perform(ViewActions.click())
        onView(withId(R.id.swipeRefreshLayoutNewsList))
            .perform(ViewActions.swipeDown())
        TimeUnit.SECONDS.sleep(1)
        onView(withId(R.id.newsRcVw))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(0))
        onView(allOf(withId(R.id.text_view_title_item_news), withText(TEST_NEWS_HEADER)))
            .check(doesNotExist())
    }

    private fun logInIfNeeded() {
        onView(withId(R.id.fragment_account_holder))
            .perform(ViewActions.click())
        try {
            onView(withId(R.id.button_enter))
                .check(matches(isDisplayed()))
            onView(withId(R.id.edit_text_login))
                .perform(ViewActions.typeText(TestUserData.TEST_MODERATOR_LOGIN))
            onView(withId(R.id.edit_text_password))
                .perform(
                    ViewActions.typeText(TestUserData.TEST_MODERATOR_PASSWORD),
                    ViewActions.closeSoftKeyboard()
                )
            onView(withId(R.id.button_enter)).perform(ViewActions.click())
            TimeUnit.SECONDS.sleep(1)
            onView(withId(R.id.buttonExit)).check(matches(isDisplayed()))
        } catch (e: NoMatchingViewException) {
            // Already logged in
        }
    }

    companion object {
        private const val TEST_NEWS_HEADER = "Test New 12345"
        private const val TEST_NEWS_TEXT = "Lorem Ipsum Dolor Sit Amet"
        private const val TEST_SEARCH_INPUT = "интер"
        private const val TEST_SEARCH_RESULT = "Интерстеллар"
        private const val TEST_COLLECTION_NAME = "Test Collection 12345"
    }

}