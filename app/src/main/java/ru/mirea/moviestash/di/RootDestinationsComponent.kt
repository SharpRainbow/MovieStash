package ru.mirea.moviestash.di

import dagger.Subcomponent
import ru.mirea.moviestash.presentation.account.AccountFragment
import ru.mirea.moviestash.presentation.account.AccountHolderFragment
import ru.mirea.moviestash.presentation.banned_users.BannedUsersFragment
import ru.mirea.moviestash.presentation.collections.CollectionFragment
import ru.mirea.moviestash.presentation.home.HomeFragment
import ru.mirea.moviestash.presentation.login.LoginFragment
import ru.mirea.moviestash.presentation.news_list.NewsListFragment
import ru.mirea.moviestash.presentation.registration.RegisterFragment
import ru.mirea.moviestash.presentation.search.SearchFragment
import ru.mirea.moviestash.presentation.user_collections.UserCollectionsFragment
import ru.mirea.moviestash.presentation.user_data.UpdateUserDataFragment

@Subcomponent(
    modules = [ViewModelModule::class]
)
interface RootDestinationsComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): RootDestinationsComponent
    }

    fun inject(fragment: HomeFragment)

    fun inject(fragment: AccountHolderFragment)

    fun inject(fragment: AccountFragment)

    fun inject(fragment: BannedUsersFragment)

    fun inject(fragment: CollectionFragment)

    fun inject(fragment: LoginFragment)

    fun inject(fragment: NewsListFragment)

    fun inject(fragment: RegisterFragment)

    fun inject(fragment: SearchFragment)

    fun inject(fragment: UserCollectionsFragment)

    fun inject(fragment: UpdateUserDataFragment)
}