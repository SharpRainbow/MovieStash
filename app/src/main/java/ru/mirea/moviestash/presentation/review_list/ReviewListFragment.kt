package ru.mirea.moviestash.presentation.review_list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import ru.mirea.moviestash.R
import ru.mirea.moviestash.databinding.FragmentReviewListBinding
import ru.mirea.moviestash.presentation.review.ReviewAdapter
import kotlin.getValue

class ReviewListFragment : Fragment() {

    private var _binding: FragmentReviewListBinding? = null
    private val binding: FragmentReviewListBinding
        get() = _binding!!
    private val arguments by navArgs<ReviewListFragmentArgs>()
    private val viewModel: ReviewListViewModel by viewModels {
        ReviewListViewModel.provideFactory(
            arguments.contentId
        )
    }
    private val reviewAdapter by lazy {
        ReviewAdapter().apply {
            onReachEnd = {
                viewModel.loadReviews()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReviewListBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews()
        bindListeners()
        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadReviews()
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.state.collect { state ->
                    if (state.isLoading) {
                        binding.progressBarReviewList.visibility = View.VISIBLE
                    } else {
                        binding.progressBarReviewList.visibility = View.GONE
                        if (state.error == null) {
                            reviewAdapter.submitList(state.reviewList)
                        }
                    }
                }
            }
        }
    }

    private fun bindViews() {
        binding.reviewListRv.layoutManager = LinearLayoutManager(context)
        binding.reviewListRv.adapter = reviewAdapter
    }

    private fun bindListeners() {
        binding.reviewListToolbar.apply {
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
    }

}