package ru.mirea.moviestash.presentation.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import ru.mirea.moviestash.R
import ru.mirea.moviestash.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding: FragmentRegisterBinding
        get() = _binding!!
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(
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
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.state.collect { state ->
                    when (state) {
                        is RegisterScreenState.Initial -> {
                            binding.buttonRegisterUser.isEnabled = true
                        }
                        is RegisterScreenState.Loading -> {
                            binding.buttonRegisterUser.isEnabled = false
                        }
                        is RegisterScreenState.Error -> {
                            binding.buttonRegisterUser.isEnabled = true
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
                            binding.textInputLayoutNickname.error =
                                if (state.errorInputNickname)
                                    getString(R.string.nickname_тще_empty)
                                else
                                    null
                            binding.textInputLayoutEmail.error =
                                if (state.errorInputEmail)
                                    getString(R.string.email_not_empty)
                                else
                                    null
                        }
                        is RegisterScreenState.Success -> {
                            showToast(getString(R.string.registration_successful))
                            findNavController().popBackStack()
                        }
                    }
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun bindListeners() {
        binding.buttonRegisterUser.setOnClickListener {
            viewModel.register(
                login = binding.editTextLogin.text?.toString(),
                password = binding.editTextPassword.text?.toString(),
                nickname = binding.editTextNickname.text?.toString(),
                email = binding.editTextEmail.text?.toString()
            )
        }
        binding.editTextLogin.addTextChangedListener(onTextChanged = {_, _, _, _ ->
            viewModel.resetErrorInputLogin()
        })
        binding.editTextPassword.addTextChangedListener(onTextChanged = {_, _, _, _ ->
            viewModel.resetErrorInputPassword()
        })
        binding.editTextNickname.addTextChangedListener(onTextChanged = {_, _, _, _ ->
            viewModel.resetErrorInputNickname()
        })
        binding.editTextEmail.addTextChangedListener(onTextChanged = {_, _, _, _ ->
            viewModel.resetErrorInputEmail()
        })
    }

}