package ru.mirea.moviestash.presentation.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.mirea.moviestash.R
import ru.mirea.moviestash.databinding.FragmentAccountHolderBinding

class AccountHolderFragment : Fragment() {

    private var _binding: FragmentAccountHolderBinding? = null
    private val binding
        get() = _binding!!
    private val viewModel: AccountHolderViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountHolderBinding.inflate(
            layoutInflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (viewModel.isLoggedIn()) {
            navigateToAccountFragment()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun navigateToAccountFragment() {
        val innerNavController = childFragmentManager
            .findFragmentById(R.id.fragmentContainerViewAccount)?.findNavController()
        if (innerNavController?.currentDestination?.id == R.id.fragment_login) {
            innerNavController.navigate(
                R.id.action_fragment_login_to_fragment_account
            )
        }
    }

}