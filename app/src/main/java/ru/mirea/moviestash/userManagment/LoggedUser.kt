package ru.mirea.moviestash.userManagment

import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import kotlinx.parcelize.Parcelize
import ru.mirea.moviestash.BR

@Parcelize
data class LoggedUser(
    val uid: Int, var login: String, private var _email: String,
    private var _nickname: String
) : Parcelable, BaseObservable() {

    var email
        @Bindable get() = _email
        set(value) {
            _email = value
            notifyPropertyChanged(BR.email)
        }

    var nickname
        @Bindable get() = _nickname
        set(value) {
            _nickname = value
            notifyPropertyChanged(BR.nickname)
        }
}
