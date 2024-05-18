package ru.mirea.moviestash

import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class MvstshUnitTest {

    @After
    fun logout(): Unit = runBlocking {
        DatabaseController.logOut()
    }

    @Test
    fun login_test(): Unit = runBlocking {
        DatabaseController.login(AppConstants.testLogin, AppConstants.testPass)
        assertEquals(AppConstants.testLogin, DatabaseController.user?.login)
    }

    @Test
    fun restore_connection_test(): Unit = runBlocking {
        DatabaseController.closeConnection()
        val res = (DatabaseController.checkConnection() as? Result.Success<Boolean>)?.data
        assertEquals(true, res)
    }

    @Test
    fun logout_test(): Unit = runBlocking {
        DatabaseController.login(AppConstants.testLogin, AppConstants.testPass)
        assertEquals(AppConstants.testLogin, DatabaseController.user?.login)
        DatabaseController.logOut()
        DatabaseController.login()
        val res = (DatabaseController.checkConnection() as? Result.Success<Boolean>)?.data
        assertEquals(true, res)
    }

    @Test
    fun edit_account_data_test(): Unit = runBlocking {
        DatabaseController.login(AppConstants.testLogin, AppConstants.testPass)
        val nickname = DatabaseController.user?.nickname.toString()
        val email = DatabaseController.user?.email.toString()
        val newNickname = "test12345"
        val newEmail = "example12345@email.com"
        DatabaseController.modUserData(newNickname, newEmail)
        DatabaseController.refreshUserData()
        assertEquals(newNickname, DatabaseController.user?.nickname)
        assertEquals(newEmail, DatabaseController.user?.email)
        DatabaseController.modUserData(nickname, email)
        DatabaseController.refreshUserData()
        assertEquals(nickname, DatabaseController.user?.nickname)
        assertEquals(email, DatabaseController.user?.email)
    }

}