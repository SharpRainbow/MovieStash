package ru.mirea.moviestash.presentation.review

import android.app.Activity.RESULT_OK
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import ru.mirea.moviestash.R
import ru.mirea.moviestash.databinding.DialogBanBinding
import ru.mirea.moviestash.databinding.FragmentReviewBinding
import ru.mirea.moviestash.domain.entities.ReviewEntity
import kotlin.getValue

class ReviewFragment : Fragment() {

    private var _binding: FragmentReviewBinding? = null
    private val binding: FragmentReviewBinding
        get() = _binding!!
    private val arguments by navArgs<ReviewFragmentArgs>()
    private val viewModel: ReviewViewModel by viewModels {
        ReviewViewModel.provideFactory(
            arguments.reviewId,
            requireActivity().application
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReviewBinding.inflate(
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
                viewModel.loadReview()
                viewModel.state.collect { state ->
                    binding.progressBarReview.visibility = View.INVISIBLE
                    when (state) {
                        ReviewScreenState.Initial -> {
                            binding.progressBarReview.visibility = View.VISIBLE
                        }

                        is ReviewScreenState.Loaded -> {
                            if (state.dataError) {
                                showToast(getString(R.string.loading_error))
                            }
                            if (state.errorInputReason) {
                                showToast(
                                    getString(R.string.ban_reason_not_empty)
                                )
                            }
                            state.review?.let {
                                showReview(it)
                            }
                            displayActionButtons(
                                state.isAuthor,
                                state.isModerator
                            )
                        }

                        ReviewScreenState.Deleted -> {
                            findNavController().popBackStack()
                        }
                    }
                }
            }
        }
    }

    private fun showReview(review: ReviewEntity) {
        binding.textViewUserNickname.text = review.userName
        binding.textViewReviewDate.text = review.date
        binding.textViewReviewOpinion.text = review.opinion
        binding.textViewReviewTitle.text = review.title
        binding.textViewReviewDescription.text = review.description
    }

    private fun bindListeners() {
        binding.reviewToolbar.apply {
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
        binding.floatingActionButtonEdit.setOnClickListener {
            navigateToReviewEditorFragment(arguments.reviewId)
        }
        binding.floatingActionButtonDelete.setOnClickListener {
            viewModel.deleteReview()
        }
        binding.floatingActionButtonBan.setOnClickListener {
            val currentState = viewModel.state.value
            if (currentState is ReviewScreenState.Loaded) {
                currentState.review?.let { review ->
                    showBanDialog(
                        review.userId,
                        review.userName
                    )
                }
            }
        }
    }

    private fun displayActionButtons(isAuthor: Boolean, isModerator: Boolean) {
        binding.floatingActionButtonEdit.visibility =
            if (isAuthor) View.VISIBLE else View.GONE
        binding.floatingActionButtonDelete.visibility =
            if (isAuthor || isModerator) View.VISIBLE else View.GONE
        binding.floatingActionButtonBan.visibility =
            if (isModerator && !isAuthor) View.VISIBLE else View.GONE
    }

    private fun showBanDialog(userId: Int, nickname: String) {
        viewModel.resetErrorInputReason()
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setTitle(getString(R.string.block_user, nickname))
        val localBinding = DialogBanBinding.inflate(
            layoutInflater,
            null,
            false
        )
        builder.setView(localBinding.root)
        builder.setPositiveButton(getString(R.string.ban)) { _, _ ->
            viewModel.banUser(
                userId,
                localBinding.editTextBanReason.text?.toString()
            )
        }
        builder.setNegativeButton(getString(R.string.cancel)) { _, _ ->

        }
        builder.show()
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToReviewEditorFragment(reviewId: Int) {
        findNavController().navigate(
            ReviewFragmentDirections.actionFragmentReviewToFragmentReviewEditor(
                reviewId
            )
        )
    }

}