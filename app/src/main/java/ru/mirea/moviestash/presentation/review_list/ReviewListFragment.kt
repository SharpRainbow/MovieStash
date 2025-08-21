package ru.mirea.moviestash.presentation.review_list

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import ru.mirea.moviestash.MovieStashApplication
import ru.mirea.moviestash.R
import ru.mirea.moviestash.databinding.FragmentReviewListBinding
import ru.mirea.moviestash.presentation.ViewModelFactory
import javax.inject.Inject

class ReviewListFragment : Fragment() {

    private var _binding: FragmentReviewListBinding? = null
    private val binding: FragmentReviewListBinding
        get() = _binding!!
    private val arguments by navArgs<ReviewListFragmentArgs>()

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: ReviewListViewModel by viewModels {
        viewModelFactory
    }
    private val reviewAdapter by lazy {
        ReviewPagingAdapter().apply {
            onReviewClick = { review ->
                navigateToReviewFragment(review.id)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as MovieStashApplication)
            .appComponent
            .reviewListComponentFactory()
            .create(arguments.contentId)
            .inject(this)
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
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                launch {
                    viewModel.reviewList.collect {
                        reviewAdapter.submitData(it)
                    }
                }
                launch {
                    reviewAdapter.loadStateFlow.collect { state ->
                        binding.progressBarReviewList.visibility =
                            if (state.refresh is LoadState.Loading) {
                                View.VISIBLE
                            } else {
                                View.GONE
                            }
                        if (state.hasError) {
                            showToast(getString(R.string.loading_error))
                        }
                    }
                }
            }
        }
    }

    private fun bindViews() {
        binding.recyclerViewReviewList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = reviewAdapter
        }
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

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToReviewFragment(reviewId: Int) {
        findNavController().navigate(
            ReviewListFragmentDirections.actionFragmentReviewListToFragmentReview(
                reviewId
            )
        )
    }

}