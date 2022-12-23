package ru.mirea.moviestash.userManagment

import java.sql.Date

data class BannedUser(val uid: Int, val nickname: String, val email: String, val date: Date,
    val reason: String?)
