package ru.mirea.moviestash.presentation.banned_users

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import ru.mirea.moviestash.MovieStashApplication
import ru.mirea.moviestash.R
import ru.mirea.moviestash.databinding.FragmentBannedUsersBinding
import ru.mirea.moviestash.domain.entities.BannedUserEntity
import ru.mirea.moviestash.presentation.ViewModelFactory
import javax.inject.Inject

class BannedUsersFragment : Fragment() {

    private var _binding: FragmentBannedUsersBinding? = null
    private val binding
        get() = _binding!!

    private val userAdapter: BannedUserAdapter by lazy {
        BannedUserAdapter().apply {
            onUserClick = { itemView, user ->
                showPopupMenu(itemView, user)
            }
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: BannedUsersViewModel by viewModels {
        viewModelFactory
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as MovieStashApplication)
            .appComponent
            .rootDestinationsComponentFactory()
            .create()
            .inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBannedUsersBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews()
        bindListeners()
        observeViewModel()
    }

    private fun bindListeners() {
        binding.toolbarBannedUsers.apply {
            setNavigationIcon(R.drawable.arrow_back)
            navigationIcon?.setTint(resources.getColor(
                R.color.md_theme_onSurface, activity?.theme
            ))
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun bindViews() {
        binding.recyclerViewBannedUsers.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewBannedUsers.adapter = userAdapter
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                launch {
                    viewModel.state.collect { state ->
                        if (state.error != null) {
                            showToast(getString(R.string.error_connection))
                        }
                    }
                }
                launch {
                    viewModel.bannedUsers.collect { pagingData ->
                        userAdapter.submitData(pagingData)
                    }
                }
                launch {
                    userAdapter.loadStateFlow.collect { state ->
                        if (state.hasError) {
                            showToast(getString(R.string.loading_error))
                        }
                    }
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun showPopupMenu(itemView: View, user: BannedUserEntity) {
        val popup = PopupMenu(
            itemView.context,
            itemView
        )
        popup.menu.add(getString(R.string.unban_user)).setOnMenuItemClickListener {
            viewModel.unbanUser(user.id)
            true
        }
        popup.gravity = Gravity.END
        popup.show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}