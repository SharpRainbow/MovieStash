package ru.mirea.moviestash.presentation.user_data

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import ru.mirea.moviestash.R
import ru.mirea.moviestash.databinding.FragmentUpdateUserDataBinding
import ru.mirea.moviestash.domain.entities.UserEntity

class UpdateUserDataFragment : Fragment() {

    private var _binding: FragmentUpdateUserDataBinding? = null
    private val binding: FragmentUpdateUserDataBinding
        get() = _binding!!
    private val viewModel: UpdateUserDataViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateUserDataBinding.inflate(
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
        binding.buttonUpdateUserData.setOnClickListener {
            viewModel.updateUserData(
                binding.editTextNickname.text?.toString(),
                binding.editTextEmail.text?.toString(),
                null
            )
        }
        binding.editTextNickname.addTextChangedListener(
            onTextChanged = { _, _, _, _ ->
                viewModel.resetErrorInputNickname()
            }
        )
        binding.editTextEmail.addTextChangedListener(
            onTextChanged = { _, _, _, _ ->
                viewModel.resetErrorInputEmail()
            }
        )
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                binding.buttonUpdateUserData.isEnabled =
                    state !is UpdateUserDataScreenState.Loading
                when (state) {
                    is UpdateUserDataScreenState.Editing -> {
                        displayUserData(state.userData)
                    }
                    is UpdateUserDataScreenState.Error -> {
                        if (state.dataError) {
                            showToast(getString(R.string.error_connection))
                        }
                        binding.textInputLayoutNickname.error =
                            if (state.errorInputNickname)
                                getString(R.string.nickname_not_empty)
                            else
                                null
                        binding.textInputLayoutEmail.error =
                            if (state.errorInputEmail)
                                getString(R.string.email_not_empty)
                            else
                                null
                    }
                    UpdateUserDataScreenState.Loading -> {}
                    UpdateUserDataScreenState.Saved -> {
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }

    private fun displayUserData(userData: UserEntity) {
        binding.editTextNickname.setText(userData.nickname)
        binding.editTextEmail.setText(userData.email)
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}