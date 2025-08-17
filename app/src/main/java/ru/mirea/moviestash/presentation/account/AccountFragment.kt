package ru.mirea.moviestash.presentation.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import ru.mirea.moviestash.R
import ru.mirea.moviestash.databinding.FragmentAccountBinding
import ru.mirea.moviestash.domain.entities.UserEntity

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding
        get() = _binding!!
    private val viewModel: AccountViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindListeners()
        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.state.collect { state ->
                    when(state) {
                        is AccountState.Loading -> {

                        }
                        is AccountState.Success -> {
                            displayUserData(
                                state.userData,
                                state.isModerator
                            )
                        }
                        is AccountState.Error -> {
                            showToast(getString(R.string.loading_error))
                        }
                    }
                }
            }
        }
    }

    private fun displayUserData(userData: UserEntity, isModerator: Boolean) {
        binding.textViewLogin.text = userData.login
        binding.textViewNickname.text = userData.nickname
        binding.textViewEmail.text = userData.email
        if (userData.isBanned) {
            binding.textViewBanMessage.visibility = View.VISIBLE
            binding.textViewBanMessage.text = getString(
                R.string.ban_message,
                userData.banDate,
                userData.banReason
            )
        } else {
            binding.textViewBanMessage.visibility = View.GONE
        }
        if (isModerator) {
            binding.buttonBannedUsers.visibility = View.VISIBLE
        } else {
            binding.buttonBannedUsers.visibility = View.GONE
        }
    }

    private fun bindListeners() {
        binding.buttonExit.setOnClickListener {
            viewModel.logout()
            navigateToLoginFragment()
        }
        binding.buttonPersonalCollections.setOnClickListener {
            navigateToCollectionsFragment()
        }
        binding.buttonBannedUsers.setOnClickListener {
            navigateToBannedUsersFragment()
        }
        binding.buttonChangeUserData.setOnClickListener {
            navigateToChangeUserDataFragment()
        }
    }

    private fun navigateToLoginFragment() {
        findNavController().navigate(
            R.id.action_fragment_account_to_fragment_login
        )
    }

    private fun navigateToBannedUsersFragment() {
        requireActivity().findNavController(
            R.id.fragment_container
        ).navigate(
            R.id.action_fragment_account_holder_to_fragment_banned_users
        )
    }

    private fun navigateToCollectionsFragment() {
        requireActivity().findNavController(
            R.id.fragment_container
        ).navigate(
            R.id.action_fragment_account_holder_to_fragment_user_collections
        )
    }

    private fun navigateToChangeUserDataFragment() {
        requireActivity().findNavController(
            R.id.fragment_container
        ).navigate(
            R.id.action_fragment_account_holder_to_fragment_update_user_data
        )
    }

    private fun showToast(message: String) {
        context?.let {
            Toast.makeText(
                it, message, Toast.LENGTH_SHORT
            ).show()
        }
    }

}