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

    private lateinit var binding: FragmentAccountHolderBinding
    private val viewModel: AccountHolderViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountHolderBinding.inflate(
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
        } else {
            navigateToLoginFragment()
        }
    }

    private fun navigateToLoginFragment() {
        val innerNavController = childFragmentManager
            .findFragmentById(R.id.fragmentContainerViewAccount)?.findNavController()
        innerNavController?.navigate(
            R.id.fragment_login
        )
    }

    private fun navigateToAccountFragment() {
        val innerNavController = childFragmentManager
            .findFragmentById(R.id.fragmentContainerViewAccount)?.findNavController()
        innerNavController?.navigate(
            R.id.fragment_account
        )
    }

}