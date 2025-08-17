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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import ru.mirea.moviestash.R
import ru.mirea.moviestash.databinding.FragmentCelebrityListBinding
import ru.mirea.moviestash.presentation.celebrity.CelebrityAdapter

class CelebrityListFragment : Fragment() {

    private var _binding: FragmentCelebrityListBinding? = null
    private val binding: FragmentCelebrityListBinding
        get() = _binding!!
    private val arguments by navArgs< CelebrityListFragmentArgs>()
    private val viewModel: PersonListViewModel by viewModels {
        PersonListViewModel.provideFactory(
            arguments.contentId,
            arguments.isActors
        )
    }
    private val celebrityAdapter by lazy {
        CelebrityAdapter().apply {
            onReachEndListener = {
                viewModel.loadCelebrityList()
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
        binding.personListRv.layoutManager = LinearLayoutManager(context)
        binding.personListRv.adapter = celebrityAdapter
    }

    private fun bindListeners() {
        binding.personListToolbar.apply {
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
            viewModel.loadCelebrityList()
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.state.collect { state ->
                    if (state.isLoading) {
                        binding.progressBarPersonList.visibility = View.VISIBLE
                    } else {
                        binding.progressBarPersonList.visibility = View.GONE
                        if (state.error == null) {
                            celebrityAdapter.submitList(
                                state.celebrityList
                            )
                        }
                    }
                }
            }
        }
    }
}