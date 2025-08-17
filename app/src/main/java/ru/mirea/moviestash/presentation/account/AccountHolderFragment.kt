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
        }
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