package ru.mirea.moviestash.presentation.login

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
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
import androidx.core.widget.addTextChangedListener
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding: FragmentLoginBinding
        get() = _binding!!
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(
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

    private fun bindListeners() {
        binding.buttonEnter.setOnClickListener {
            viewModel.login(
                binding.editTextLogin.text?.toString(),
                binding.editTextPassword.text?.toString()
            )
        }
        binding.buttonRegister.setOnClickListener {
            navigateToRegisterFragment()
        }
        binding.editTextLogin.addTextChangedListener(
            onTextChanged = { _, _, _, _ ->
                viewModel.resetErrorInputLogin()
            }
        )
        binding.editTextPassword.addTextChangedListener(
            onTextChanged = { _, _, _, _ ->
                viewModel.resetErrorInputPassword()
            }
        )
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
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.state.collect { state ->
                    changeUiState(state !is LoginState.Loading)
                    when (state) {
                        is LoginState.Initial -> {
                            showCredentials(state.credentials)
                        }
                        is LoginState.Loading -> {}
                        is LoginState.Success -> {
                            if (!state.isSaved)
                                showSaveSnack()
                            navigateToAccount()
                        }
                        is LoginState.Error -> {
                            if (state.dataError) {
                                showToast(getString(R.string.error_connection))
                            }
                            binding.textInputLayoutLogin.error =
                                if (state.errorInputLogin)
                                    getString(R.string.login_not_empty)
                                else
                                    null
                            binding.textInputLayoutPassword.error =
                                if (state.errorInputPassword)
                                    getString(R.string.password_not_empty)
                                else
                                    null
                        }
                    }
                }
            }
        }
    }

    private fun showCredentials(credentials: List<CredentialsEntity>) {
        if (credentials.isEmpty()) {
            binding.buttonAutocomplete.visibility = View.GONE
        } else {
            binding.buttonAutocomplete.visibility = View.VISIBLE
            binding.buttonAutocomplete.setOnClickListener {
                showAutoCompleteDialog(credentials)
            }
        }
    }

    private fun showAutoCompleteDialog(credentials: List<CredentialsEntity>) {
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setTitle(getString(R.string.saved_credentials))
        var checkedItem = -1
        builder.setSingleChoiceItems(
            credentials.map { it.login }.toTypedArray(),
            -1
        ) { _, which ->
            checkedItem = which
        }
        builder.setPositiveButton(getString(R.string.select)) { dialog, _ ->
            if (checkedItem != -1) {
                val selectedCredentials = credentials[checkedItem]
                binding.editTextLogin.setText(selectedCredentials.login)
                binding.editTextPassword.setText(selectedCredentials.password)
            }
        }
        builder.setNegativeButton(getString(R.string.delete)) { dialog, _ ->
            if (checkedItem != -1) {
                val selectedCredentials = credentials[checkedItem]
                viewModel.removeCredential(selectedCredentials.login)
            }
        }
        builder.show()
    }

    private fun changeUiState(enabled: Boolean) {
        binding.buttonEnter.isEnabled = enabled
        binding.buttonRegister.isEnabled = enabled
        binding.buttonAutocomplete.isEnabled = enabled
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