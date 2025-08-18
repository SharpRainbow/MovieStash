package ru.mirea.moviestash.presentation.celebrity_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.mirea.moviestash.R
import ru.mirea.moviestash.databinding.FragmentCelebrityListBinding

class CelebrityListFragment : Fragment() {

    private var _binding: FragmentCelebrityListBinding? = null
    private val binding: FragmentCelebrityListBinding
        get() = _binding!!
    private val arguments by navArgs<CelebrityListFragmentArgs>()
    private val viewModel: PersonListViewModel by viewModels {
        PersonListViewModel.provideFactory(
            arguments.contentId,
            arguments.isActors
        )
    }
    private val celebrityAdapter by lazy {
        CelebrityPagingAdapter().apply {
            onCelebrityClick = { celebrity ->
                navigateToCelebrityFragment(celebrity.id)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCelebrityListBinding.inflate(
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

    private fun bindViews() {
        with(binding) {
            if (arguments.isActors) {
                textViewCelebrityType.text = getString(R.string.actors)
            } else {
                textViewCelebrityType.text = getString(R.string.crew)
            }
            recyclerViewCelebrityList.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = celebrityAdapter
            }
        }
    }

    private fun bindListeners() {
        binding.toolbarCelebrityList.apply {
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
                celebrityAdapter.loadStateFlow.onEach { state ->
                    binding.progressBarCelebrityList.visibility =
                        if (state.refresh is LoadState.Loading) {
                            View.VISIBLE
                        } else {
                            View.GONE
                        }
                }.launchIn(this)
                viewModel.celebrityFlow.onEach { celebrityData ->
                    celebrityAdapter.submitData(celebrityData)
                }.launchIn(this)
            }
        }
    }

    private fun navigateToCelebrityFragment(celebrityId: Int) {
        findNavController().navigate(
            CelebrityListFragmentDirections.actionFragmentCelebrityListToFragmentCelebrity(
                celebrityId
            )
        )
    }
}