package ru.mirea.moviestash.presentation.content

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import ru.mirea.moviestash.R
import ru.mirea.moviestash.databinding.DialogRatingBinding
import ru.mirea.moviestash.databinding.FragmentContentBinding
import ru.mirea.moviestash.domain.entities.CollectionEntity
import ru.mirea.moviestash.domain.entities.ContentEntity
import ru.mirea.moviestash.presentation.celebrity.CelebrityAdapter
import ru.mirea.moviestash.presentation.review.ReviewAdapter
import kotlin.getValue

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
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.getReviews()
                viewModel.state.collect { state ->
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
                        if (state.isLoggedIn && state.userCollections.isNotEmpty()) {
                            showCollectionsDialog(state.userCollections)
                        }
                        binding.linearLayoutContentUserActions.visibility =
                            if (state.isLoggedIn) View.VISIBLE else View.GONE
                        castAdapter.submitList(state.castList)
                        crewAdapter.submitList(state.crewList)
                        reviewAdapter.submitList(state.reviews)
                    }
                }
            }
        }
    }

    private fun showContent(content: ContentEntity) {
        Glide.with(requireContext())
            .load(content.image)
            .into(binding.imageViewContent)
        binding.textViewContentName.text = content.name
        binding.textViewMvstshRating.text = content.rating.toString()
        binding.textViewKpRating.text = content.ratingKinopoisk.toString()
        binding.textViewImdbRating.text = content.ratingImdb.toString()
        binding.textViewGenres.text = content.genres
        binding.textViewDescription.text = content.description
        binding.textViewCountries.text = content.countries
        binding.textViewReleaseDate.text = content.releaseDate
        binding.textViewDuration.text = content.duration
        binding.textViewBudget.text = content.budget.toString()
        binding.textViewBoxOffice.text = content.boxOffice.toString()
    }

    private fun bindViews() {
        binding.castRecycler.layoutManager = LinearLayoutManager(context)
        binding.castRecycler.adapter = castAdapter
        binding.crewRecycler.layoutManager = LinearLayoutManager(context)
        binding.crewRecycler.adapter = crewAdapter
        binding.reviewRecycler.layoutManager = LinearLayoutManager(context)
        binding.reviewRecycler.adapter = reviewAdapter
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun bindListeners() {
        binding.contToolbar.apply {
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
        binding.rateBtn.setOnClickListener {
            showRateDialog()
        }
        binding.moreActors.setOnClickListener {
            navigateToContentListFragment(true)
        }
        binding.moreCreators.setOnClickListener {
            navigateToContentListFragment(false)
        }
        binding.addToListBtn.setOnClickListener {
            viewModel.getUserCollections()
        }
        binding.addReviewBtn.setOnClickListener {
            navigateToReviewEditorFragment()
        }
        binding.moreReviews.setOnClickListener {
            navigateToReviewListFragment()
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
        localBinding.ratingBar.apply {
            rating = (viewModel.state.value.userStar?.rating ?: 0) / 2f
        }
        builder.setView(localBinding.root)
        builder.setPositiveButton(getString(R.string.ok)) { dialog, _ ->
            viewModel.rateContent(
                (localBinding.ratingBar.rating * 2).toInt()
            )
        }
        builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->

        }
        builder.show()
    }

    private fun showCollectionsDialog(collections: List<CollectionEntity>) {
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setTitle(getString(R.string.select_collection))
        builder.setItems(
            collections.map { it.name }.toTypedArray()
        ) { dialog, which ->
            val collection = collections[which]
            viewModel.addToCollection(collection.id)
        }
        builder.setOnDismissListener {
            viewModel.resetCollectionPage()
        }
        builder.show()
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
                arguments.contentId
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