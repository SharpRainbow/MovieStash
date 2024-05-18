package ru.mirea.moviestash.entites

import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import ru.mirea.moviestash.BR
import java.sql.Date

@Parcelize
@DatabaseTable(tableName = "site_user")
class SiteUser(
    @DatabaseField(columnName = "uid", id = true)
    var id: Int = 0,
    @DatabaseField
    val login: String = "",
    @DatabaseField(columnName = "is_banned")
    var banned: Boolean = false,
    @DatabaseField(columnName = "ban_date", canBeNull = true)
    var banDate: Date? = null,
    @DatabaseField(columnName = "ban_reason", canBeNull = true)
    var banReason: String? = null,
    @DatabaseField(columnName = "nickname")
    private var _nickname: String = ""
) : BaseObservable(), Parcelable {

    var nickname: String
        @Bindable get() = _nickname
        set(value) {
            _nickname = value
            notifyPropertyChanged(BR.nickname)
        }

    @IgnoredOnParcel
    @DatabaseField
    var email: String = ""
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.email)
        }

    override fun toString(): String {
        return "SiteUser(id=$id, login='$login', banned=$banned, banDate=$banDate, banReason=$banReason, nickname='$nickname', email='$email')"
    }


}