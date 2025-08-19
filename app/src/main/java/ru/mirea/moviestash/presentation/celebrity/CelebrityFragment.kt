package ru.mirea.moviestash.presentation.celebrity

import android.os.Bundle
import android.util.TypedValue
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
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.mirea.moviestash.R
import ru.mirea.moviestash.Utils
import ru.mirea.moviestash.databinding.FragmentCelebrityBinding
import ru.mirea.moviestash.domain.entities.CelebrityEntity
import ru.mirea.moviestash.presentation.content.ContentPagedAdapter

class CelebrityFragment : Fragment() {

    private var _binding: FragmentCelebrityBinding? = null
    private val binding: FragmentCelebrityBinding
        get() = _binding!!
    private val arguments by navArgs<CelebrityFragmentArgs>()
    private val viewModel: CelebrityViewModel by viewModels {
        CelebrityViewModel.provideFactory(
            arguments.celebrityId
        )
    }
    private val contentAdapter by lazy {
        ContentPagedAdapter().apply {
            onContentClick = { content ->
                navigateToContentFragment(content.id)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCelebrityBinding.inflate(
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
        binding.recyclerViewCelebrityContent.apply {
            layoutManager = GridLayoutManager(
                context,
                maxOf(3, calculateColumnsCount())
            )
            adapter = contentAdapter
        }
        binding.toolbarCelebrityScreen.apply {
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

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.state.onEach { state ->
                    if (state.error == null) {
                        state.celebrity?.let { celebrity ->
                            displayCelebrity(celebrity)
                        }
                    } else {
                        showToast(getString(R.string.loading_error))
                    }
                }.launchIn(this)
                viewModel.celebrityContentFlow.onEach {
                    contentAdapter.submitData(it)
                }.launchIn(this)
                contentAdapter.loadStateFlow.onEach { state ->
                    if (state.hasError) {
                        showToast(getString(R.string.loading_error))
                    }
                }.launchIn(this)
            }
        }
    }

    private fun displayCelebrity(celebrity: CelebrityEntity) {
        Glide.with(requireContext())
            .load(celebrity.image)
            .placeholder(R.drawable.placeholder)
            .into(binding.imageViewCelebrityImage)
        with(binding) {
            textViewCelebrityName.text = celebrity.name
            textViewCelebrityHeight.apply {
                visibility = if (celebrity.height > 0) View.VISIBLE else View.GONE
                text = getString(
                    R.string.height,
                    celebrity.height.toString()
                )
            }
            textViewCelebrityBirthplace.apply {
                visibility = if (celebrity.birthPlace.isNotBlank()) View.VISIBLE else View.GONE
                text = getString(
                    R.string.birthplace,
                    celebrity.birthPlace
                )
            }
            textViewCelebrityLifeDates.apply {
                visibility =
                    if (celebrity.birthDate.isNotBlank() || celebrity.death.isNotBlank())
                        View.VISIBLE
                    else
                        View.GONE
                text = Utils.getLiveDates(
                    celebrity.birthDate,
                    celebrity.death
                )
            }
            textViewCelebrityCareer.text = celebrity.career
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToContentFragment(contentId: Int) {
        findNavController().navigate(
            CelebrityFragmentDirections.actionFragmentCelebrityToFragmentContent(
                contentId
            )
        )
    }

    private fun calculateColumnsCount(): Int {
        val metrics = resources.displayMetrics
        return metrics.widthPixels / TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            COLUMN_WIDTH,
            metrics
        ).toInt()
    }

    companion object {
        private const val COLUMN_WIDTH = 160f
    }

}