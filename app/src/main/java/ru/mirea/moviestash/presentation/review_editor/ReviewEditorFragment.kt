package ru.mirea.moviestash.presentation.review_editor

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.launch
import ru.mirea.moviestash.R
import ru.mirea.moviestash.databinding.FragmentReviewEditorBinding
import ru.mirea.moviestash.domain.entities.OpinionEntity
import ru.mirea.moviestash.domain.entities.ReviewEntity

class ReviewEditorFragment : Fragment() {

    private var _binding: FragmentReviewEditorBinding? = null
    private val binding: FragmentReviewEditorBinding
        get() = _binding!!
    private val arguments by navArgs<ReviewEditorFragmentArgs>()
    private val viewModel: ReviewEditorViewModel by viewModels(
        factoryProducer = {
            ReviewEditorViewModel.provideFactory(
                requireActivity().application,
                arguments.contentId
            )
        }
    )
    private lateinit var spinnerAdapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReviewEditorBinding.inflate(
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
                if (arguments.reviewId > 0) {
                    viewModel.loadReview(arguments.reviewId)
                }
                viewModel.state.collect { state ->
                    when(state) {
                        is ReviewEditorState.Editing -> {
                            binding.opSpinner.adapter = ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_spinner_item,
                                state.opinions.map(OpinionEntity::name)
                            ).apply {
                                setDropDownViewResource(
                                    android.R.layout.simple_spinner_dropdown_item
                                )
                            }.also {
                                spinnerAdapter = it
                            }
                            state.review?.let {
                                displayReview(it)
                            }
                        }
                        is ReviewEditorState.Error -> {
                            binding.textInputLayoutTitle.error =
                                if (state.errorInputTitle)
                                    getString(R.string.review_title_not_empty)
                                else
                                    null
                            binding.editTextDescription.error =
                                if (state.errorInputText)
                                    getString(R.string.review_title_not_empty)
                                else
                                    null
                            if (state.dataError) {
                                showToast(getString(R.string.loading_error))
                            }
                        }
                        ReviewEditorState.Loading -> {

                        }
                        is ReviewEditorState.Finished -> {
                            findNavController().popBackStack()
                        }
                    }
                }
            }
        }
    }

    private fun displayReview(review: ReviewEntity) {
        binding.editTextTitle.setText(review.title)
        binding.editTextDescription.setText(review.description)
        binding.opSpinner.setSelection(spinnerAdapter.getPosition(review.opinion))
    }

    private fun bindListeners() {
        binding.toolbarReviewEditor.apply {
            setNavigationIcon(R.drawable.arrow_back)
            navigationIcon?.setTint(
                resources.getColor(
                    R.color.md_theme_onSurface,
                    requireActivity().theme
                )
            )
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }
        }
        binding.buttonSaveReview.setOnClickListener {
            if (arguments.reviewId > 0) {
                viewModel.updateReview(
                    arguments.reviewId,
                    binding.editTextTitle.text?.toString(),
                    binding.editTextDescription.text?.toString(),
                    binding.opSpinner.selectedItemPosition + 1
                )
            } else {
                viewModel.addReview(
                    binding.editTextTitle.text?.toString(),
                    binding.editTextDescription.text?.toString(),
                    binding.opSpinner.selectedItemPosition + 1
                )
            }
        }
        binding.editTextTitle.addTextChangedListener(
            onTextChanged = { _, _, _, _ ->
                viewModel.resetErrorInputTitle()
            }
        )
        binding.editTextDescription.addTextChangedListener(
            onTextChanged = { _, _, _, _ ->
                viewModel.resetErrorInputText()
            }
        )
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

}