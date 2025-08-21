package ru.mirea.moviestash.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.mirea.moviestash.presentation.account.AccountHolderViewModel
import ru.mirea.moviestash.presentation.account.AccountViewModel
import ru.mirea.moviestash.presentation.banned_users.BannedUsersViewModel
import ru.mirea.moviestash.presentation.collections.CollectionsListViewModel
import ru.mirea.moviestash.presentation.home.HomeViewModel
import ru.mirea.moviestash.presentation.login.LoginViewModel
import ru.mirea.moviestash.presentation.news_list.NewsListViewModel
import ru.mirea.moviestash.presentation.registration.RegisterViewModel
import ru.mirea.moviestash.presentation.search.SearchViewModel
import ru.mirea.moviestash.presentation.user_collections.UserCollectionsViewModel
import ru.mirea.moviestash.presentation.user_data.UpdateUserDataViewModel

@Module
interface ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(AccountViewModel::class)
    fun bindAccountViewModel(impl: AccountViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AccountHolderViewModel::class)
    fun bindAccountHolderViewModel(impl: AccountHolderViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BannedUsersViewModel::class)
    fun bindBannedUsersViewModel(impl: BannedUsersViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CollectionsListViewModel::class)
    fun bindCollectionsListViewModel(impl: CollectionsListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    fun bindHomeViewModel(impl: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    fun bindLoginViewModel(impl: LoginViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NewsListViewModel::class)
    fun bindNewsListViewModel(impl: NewsListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RegisterViewModel::class)
    fun bindRegisterViewModel(impl: RegisterViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel::class)
    fun bindSearchViewModel(impl: SearchViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(UserCollectionsViewModel::class)
    fun bindUserCollectionsViewModel(impl: UserCollectionsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(UpdateUserDataViewModel::class)
    fun bindUpdateUserDataViewModel(impl: UpdateUserDataViewModel): ViewModel
}