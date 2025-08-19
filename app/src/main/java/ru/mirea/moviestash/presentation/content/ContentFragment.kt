package ru.mirea.moviestash.presentation.content

import android.os.Bundle
import android.util.Log
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
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.mirea.moviestash.R
import ru.mirea.moviestash.databinding.DialogAddListBinding
import ru.mirea.moviestash.databinding.DialogCollectionsBinding
import ru.mirea.moviestash.databinding.DialogRatingBinding
import ru.mirea.moviestash.databinding.FragmentContentBinding
import ru.mirea.moviestash.domain.entities.CollectionEntity
import ru.mirea.moviestash.domain.entities.ContentEntity
import ru.mirea.moviestash.presentation.review.ReviewAdapter

class ContentFragment : Fragment() {

    private var _binding: FragmentContentBinding? = null
    private val binding: FragmentContentBinding
        get() = _binding!!
    private val arguments by navArgs<ContentFragmentArgs>()
    private val viewModel: ContentViewModel by viewModels{
        ContentViewModel.provideFactory(
            arguments.contentId,
            requireActivity().application
        )
    }
    private val castAdapter by lazy {
        CelebrityAdapter().apply {
            onCelebrityClick = { celebrity ->
                navigateToCelebrityFragment(
                    celebrity.id
                )
            }
        }
    }
    private val crewAdapter by lazy {
        CelebrityAdapter().apply {
            onCelebrityClick = { celebrity ->
                navigateToCelebrityFragment(
                    celebrity.id
                )
            }
        }
    }
    private val reviewAdapter by lazy {
        ReviewAdapter().apply {
            onReviewClick = { review ->
                navigateToReviewFragment(review.id)
            }
        }
    }
    private val collectionContentAdapter by lazy {
        DialogCollectionPagedAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentContentBinding.inflate(
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
                viewModel.getReviews()
                viewModel.state.onEach { state ->
                    if (state.isLoading) {
                        binding.progressBarContent.visibility = View.VISIBLE
                    } else {
                        binding.progressBarContent.visibility = View.INVISIBLE
                        if (state.error != null) {
                            showToast(getString(R.string.loading_error))
                        }
                        state.content?.let {
                            showContent(it)
                        }
                        binding.linearLayoutContentUserActions.visibility =
                            if (state.isLoggedIn) View.VISIBLE else View.GONE
                        castAdapter.submitList(state.castList)
                        crewAdapter.submitList(state.crewList)
                        reviewAdapter.submitList(state.reviews)
                    }
                }.launchIn(this)
                viewModel.userCollections.onEach { collectionPagedData ->
                    collectionContentAdapter.submitData(
                        collectionPagedData
                    )
                }.launchIn(this)
                collectionContentAdapter.loadStateFlow.onEach { state ->
                    if (state.hasError) {
                        showToast(getString(R.string.loading_error))
                    }
                    binding.progressBarContent.visibility =
                        if (state.refresh is LoadState.Loading) {
                            View.VISIBLE
                        } else {
                            View.INVISIBLE
                        }
                }.launchIn(this)
            }
        }
    }

    private fun showContent(content: ContentEntity) {
        Glide.with(requireContext())
            .load(content.image)
            .into(binding.imageViewContent)
        with(binding) {
            textViewContentName.text = content.name
            textViewMvstshRating.text = content.rating.toString()
            textViewKinopoiskRating.text = content.ratingKinopoisk.toString()
            textViewImdbRating.text = content.ratingImdb.toString()
            textViewGenres.text = content.genres
            textViewDescription.text = content.description
            textViewCountries.text = content.countries
            textViewReleaseDate.text = content.releaseDate
            textViewDuration.text = content.duration
            textViewBudget.text = content.budget.toString()
            textViewBoxOffice.text = content.boxOffice.toString()
        }
    }

    private fun bindViews() {
        with(binding) {
            recyclerViewCast.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = castAdapter
            }
            recyclerViewCrew.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = crewAdapter
            }
            recyclerViewReview.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = reviewAdapter
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun bindListeners() {
        with(binding) {
            toolbarContent.apply {
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
            buttonRate.setOnClickListener {
                showRateDialog()
            }
            buttonMoreCast.setOnClickListener {
                navigateToContentListFragment(true)
            }
            buttonMoreCrew.setOnClickListener {
                navigateToContentListFragment(false)
            }
            buttonAddToList.setOnClickListener {
                showCollectionsDialog()
            }
            buttonAddReview.setOnClickListener {
                navigateToReviewEditorFragment()
            }
            buttonMoreReviews.setOnClickListener {
                navigateToReviewListFragment()
            }
        }
    }

    private fun showRateDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setTitle(getString(R.string.rate_content))
        val localBinding = DialogRatingBinding.inflate(
            layoutInflater,
            null,
            false
        )
        localBinding.ratingBarContentRating.apply {
            rating = (viewModel.state.value.userStar?.rating ?: 0) / 2f
        }
        builder.setView(localBinding.root)
        builder.setPositiveButton(getString(R.string.ok)) { dialog, _ ->
            viewModel.rateContent(
                (localBinding.ratingBarContentRating.rating * 2).toInt()
            )
        }
        builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->

        }
        builder.show()
    }

    private fun showCollectionsDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setTitle(getString(R.string.select_collection))
        val dialogBinding = DialogCollectionsBinding.inflate(
            LayoutInflater.from(context),
            null,
            false
        )
        builder.setView(dialogBinding.root)
        val dialog = builder.create()
        dialogBinding.recyclerViewDialogCollections.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = collectionContentAdapter.apply {
                onCollectionClick = { collection ->
                    viewModel.addToCollection(collection.id)
                    dialog.dismiss()
                }
            }
        }
        viewModel.refreshCollections()
        dialog.show()
    }

    private fun navigateToContentListFragment(isActors: Boolean) {
        findNavController().navigate(
            ContentFragmentDirections.actionFragmentContentToFragmentCelebrityList(
                arguments.contentId,
                isActors
            )
        )
    }

    private fun navigateToCelebrityFragment(celebrityId: Int) {
        findNavController().navigate(
            ContentFragmentDirections.actionFragmentContentToFragmentCelebrity(
                celebrityId
            )
        )
    }

    private fun navigateToReviewListFragment() {
        findNavController().navigate(
            ContentFragmentDirections.actionFragmentContentToFragmentReviewList(
                arguments.contentId
            )
        )
    }

    private fun navigateToReviewEditorFragment() {
        findNavController().navigate(
            ContentFragmentDirections.actionFragmentContentToFragmentReviewEditor(
                contentId = arguments.contentId
            )
        )
    }

    private fun navigateToReviewFragment(reviewId: Int) {
        findNavController().navigate(
            ContentFragmentDirections.actionFragmentContentToFragmentReview(
                reviewId
            )
        )
    }

}