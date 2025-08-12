package ru.mirea.moviestash.presentation.login

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import ru.mirea.moviestash.R
import ru.mirea.moviestash.databinding.DialogCollectionsBinding
import ru.mirea.moviestash.databinding.FragmentLoginBinding
import ru.mirea.moviestash.domain.entities.CredentialsEntity
import ru.mirea.moviestash.presentation.account.AccountFragment
import androidx.core.graphics.drawable.toDrawable
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindListeners()
        observeViewModel()
    }

    private fun bindListeners() {
        binding.enterButton.setOnClickListener {
            viewModel.login(
                binding.loginEdMain.text?.toString(),
                binding.passEdMain.text?.toString()
            )
        }
        binding.registerButton.setOnClickListener {
            navigateToRegisterFragment()
        }
        binding.autocompleteButton.setOnClickListener {

        }
    }

    private fun showSaveSnack() {
        val saveSnackBar =
            Snackbar.make(
                binding.root,
                getString(R.string.save_credentials),
                Snackbar.LENGTH_LONG
            )
        val params = saveSnackBar.view.layoutParams as CoordinatorLayout.LayoutParams
        params.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        saveSnackBar.view.layoutParams = params
        saveSnackBar.setAction(getString(R.string.ok)) {
            viewModel.saveCredentials()
        }
        saveSnackBar.show()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.state.collect { state ->
                    when (state) {
                        is LoginState.Initial -> {
                            showCredentials(state.credentials)
                        }
                        is LoginState.Loading -> {

                        }
                        is LoginState.Success -> {
                            if (!state.isSaved)
                                showSaveSnack()
                            navigateToAccount()
                        }
                        is LoginState.Error -> {
                            showToast(state.message)
                        }
                    }
                }
            }
        }
    }

    private fun showCredentials(credentials: List<CredentialsEntity>) {
        if (credentials.isEmpty()) {
            binding.autocompleteButton.visibility = View.GONE
        } else {
            binding.autocompleteButton.visibility = View.VISIBLE
            binding.autocompleteButton.setOnClickListener {
                showAutoCompleteDialog(credentials)
            }
        }
    }

    private fun showAutoCompleteDialog(credentials: List<CredentialsEntity>) {
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setTitle("Сохраненные учетные записи")
        var checkedItem = -1
        builder.setSingleChoiceItems(
            credentials.map { it.login }.toTypedArray(),
            -1
        ) { _, which ->
            checkedItem = which
        }
        builder.setPositiveButton("Выбрать") { dialog, _ ->
            if (checkedItem != -1) {
                val selectedCredentials = credentials[checkedItem]
                binding.loginEdMain.setText(selectedCredentials.login)
                binding.passEdMain.setText(selectedCredentials.password)
            }
        }
        builder.setNegativeButton("Удалить") { dialog, _ ->
            if (checkedItem != -1) {
                val selectedCredentials = credentials[checkedItem]
                viewModel.removeCredential(selectedCredentials.login)
            }
        }
        builder.show()
    }

    private fun showToast(message: String) {
        context?.let {
            Toast.makeText(
                it, message, Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun navigateToAccount() {
        findNavController().navigate(
            R.id.action_fragment_login_to_fragment_account
        )
    }

    private fun navigateToRegisterFragment() {
        requireActivity().findNavController(
            R.id.fragment_container
        ).navigate(
            R.id.action_fragment_account_holder_to_fragment_register
        )
    }
}