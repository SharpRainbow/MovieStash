package ru.mirea.moviestash

import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.allOf

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

    @Test
    fun logInAppTest() {
        launchActivity<MainActivity>().use {
            onView(withId(R.id.navigation_account)).perform(ViewActions.click())
            try {
                onView(withId(R.id.exitButton)).check(matches(isDisplayed()))
                onView(withId(R.id.exitButton)).perform(ViewActions.click())
            }
            catch (e: NoMatchingViewException) {

            }
            onView(withId(R.id.enterButton)).check(matches(isDisplayed()))
            onView(withId(R.id.loginEdMain)).perform(ViewActions.typeText(AppConstants.testLogin))
            onView(withId(R.id.passEdMain)).perform(ViewActions.typeText(AppConstants.testPass), ViewActions.closeSoftKeyboard())
            onView(withId(R.id.enterButton)).perform(ViewActions.click())
            TimeUnit.SECONDS.sleep(2)
            onView(withId(R.id.exitButton)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun addCollectionTest() {
        launchActivity<UserCollectionActivity>().use {
            if (DatabaseController.user == null)
                runBlocking {
                    DatabaseController.login(AppConstants.testLogin, AppConstants.testPass)
                }
            onView(withId(R.id.addToUserCol)).perform(ViewActions.click())
            val collectionName = "Test Collection 12345"
            onView(withId(R.id.editTextCollectionName)).perform(ViewActions.typeText(collectionName), ViewActions.closeSoftKeyboard())
            onView(withId(R.id.buttonAdd)).perform(ViewActions.click())
            onView(allOf(withId(R.id.colName), withText(collectionName))).check(matches(isDisplayed()))
            onView(allOf(withId(R.id.colName), withText(collectionName))).perform(ViewActions.longClick())
            onView(withId(R.id.buttonDeleteCollection)).perform(ViewActions.click())
            TimeUnit.SECONDS.sleep(1)
            onView(allOf(withId(R.id.colName), withText(collectionName))).check(doesNotExist())
        }
    }

    @Test
    fun searchContentTest() {
        launchActivity<SearchActivity>().use {
            onView(withId(R.id.contentSearcher)).perform(ViewActions.replaceText("интер"), ViewActions.closeSoftKeyboard())
            TimeUnit.SECONDS.sleep(5)
            onView(allOf(withId(R.id.searchName), withText("Интерстеллар"))).check(matches(isDisplayed()))
        }
    }

    @Test
    fun addNewsTest() {
        launchActivity<MainActivity>().use {
            if (DatabaseController.user == null)
                runBlocking {
                    DatabaseController.login(AppConstants.testLogin, AppConstants.testPass)
                }
            val header = "Test New 12345"
            val desc = "Lorem Ipsum Dolor Sit Amet"
            onView(withId(R.id.navigation_news)).perform(ViewActions.click())
            TimeUnit.SECONDS.sleep(1)
            onView(withId(R.id.addButton)).perform(ViewActions.click())
            onView(withId(R.id.headerNews)).perform(ViewActions.typeText(header), ViewActions.closeSoftKeyboard())
            onView(withId(R.id.descriptionNews)).perform(ViewActions.typeText(desc), ViewActions.closeSoftKeyboard())
            onView(withId(R.id.saveNewsBtn)).perform(ViewActions.click())
            onView(withId(R.id.refresher)).perform(ViewActions.swipeDown())
            TimeUnit.SECONDS.sleep(1)
            onView(allOf(withId(R.id.newsText), withText(header))).perform(ViewActions.click())
            onView(withId(R.id.floatin_action_buttons_delete_news)).perform(ViewActions.click())
            onView(withText("Удалить")).perform(ViewActions.click())
            onView(withId(R.id.refresher)).perform(ViewActions.swipeDown())
            TimeUnit.SECONDS.sleep(1)
            onView(allOf(withId(R.id.newsText), withText(header))).check(doesNotExist())
        }
    }

}