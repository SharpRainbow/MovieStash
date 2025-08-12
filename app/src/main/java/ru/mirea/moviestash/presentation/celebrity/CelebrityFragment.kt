package ru.mirea.moviestash.presentation.celebrity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import ru.mirea.moviestash.R
import ru.mirea.moviestash.Utils
import ru.mirea.moviestash.databinding.FragmentCelebrityBinding
import ru.mirea.moviestash.presentation.content.ContentAdapter

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
        ContentAdapter().apply {
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
        binding.celebFilmsRecycler.apply {
            layoutManager = GridLayoutManager(
                context,
                3
            )
            adapter = contentAdapter
        }
        binding.personToolbar.apply {
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
        lifecycleScope.launch {
            viewModel.loadCelebrity()
            viewModel.loadContent()
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.state.collect { state ->
                    if (state.isLoading) {

                    } else {
                        if (state.error == null) {
                            state.celebrity?.let { celebrity ->
                                Glide.with(requireContext())
                                    .load(celebrity.image)
                                    .into(binding.personProfileImage)
                                binding.textViewPersonName.text = celebrity.name
                                binding.textViewPersonHeight.text = getString(
                                    R.string.height,
                                    celebrity.height.toString()
                                )
                                binding.textViewPersonBirthplace.text = getString(
                                    R.string.birthplace,
                                    celebrity.birthPlace
                                )
                                binding.textViewPersonLifeDates.text = Utils.getLiveDates(
                                    celebrity.birthDate,
                                    celebrity.death
                                )
                                binding.textViewPersonCareer.text = celebrity.career
                            }
                            contentAdapter.submitList(state.contentList)
                        }
                    }
                }
            }
        }
    }

    private fun navigateToContentFragment(contentId: Int) {
        findNavController().navigate(
            CelebrityFragmentDirections.actionFragmentCelebrityToFragmentContent(
                contentId
            )
        )
    }
}