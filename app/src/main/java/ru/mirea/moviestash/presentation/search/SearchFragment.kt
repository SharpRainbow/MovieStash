package ru.mirea.moviestash.presentation.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.mirea.moviestash.MovieStashApplication
import ru.mirea.moviestash.R
import ru.mirea.moviestash.databinding.FragmentSearchBinding
import ru.mirea.moviestash.presentation.ViewModelFactory
import javax.inject.Inject

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding
        get() = _binding!!

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: SearchViewModel by viewModels {
        viewModelFactory
    }
    private val celebrityPagingAdapter: SearchPagingCelebrityAdapter by lazy {
        SearchPagingCelebrityAdapter().apply {
            onCelebrityClick = { celebrity ->
                navigateToCelebrityFragment(celebrity.id)
            }
        }
    }
    private val contentPagingAdapter: SearchPagingContentAdapter by lazy {
        SearchPagingContentAdapter().apply {
            onContentClick = { content ->
                navigateToContentFragment(content.id)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as MovieStashApplication)
            .appComponent
            .rootDestinationsComponentFactory()
            .create()
            .inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(
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
                merge(
                    celebrityPagingAdapter.loadStateFlow,
                    contentPagingAdapter.loadStateFlow
                ).onEach { state ->
                    binding.progressBarSearch.visibility =
                        if (state.refresh is LoadState.Loading) {
                            View.VISIBLE
                        } else {
                            View.GONE
                        }
                    if (state.hasError) {
                        showToast(getString(R.string.loading_error))
                    }
                }.launchIn(this)
                viewModel.state.onEach { state ->
                    selectCorrectTab(state.currentTab)
                }.launchIn(this)
                viewModel.pagedContentList.onEach {
                    contentPagingAdapter.submitData(it)
                    resetRecyclerViewPosition()
                }.launchIn(this)
                viewModel.pagedCelebrityList.onEach {
                    celebrityPagingAdapter.submitData(it)
                    resetRecyclerViewPosition()
                }.launchIn(this)
            }
        }
    }

    private fun resetRecyclerViewPosition() {
        binding.recyclerViewSearch.postDelayed({
            binding.recyclerViewSearch.scrollToPosition(0)
        }, 100)
    }

    private fun selectCorrectTab(currentTab: SearchTab) {
        val tabIndex = currentTab.tabId
        if (binding.tabLayoutSearch.selectedTabPosition != tabIndex) {
            binding.tabLayoutSearch.getTabAt(tabIndex)?.select()
        }

        val expectedAdapter =
            if (currentTab == SearchTab.CONTENT)
                contentPagingAdapter
            else
                celebrityPagingAdapter
        if (binding.recyclerViewSearch.adapter != expectedAdapter) {
            binding.recyclerViewSearch.adapter = expectedAdapter
        }
    }

    private fun bindListeners() {
        with(binding) {
            toolbarSearch.apply {
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
            editTextSearchQuery.apply {
                postDelayed({
                    isFocusableInTouchMode = true
                    requestFocus()
                    val keyboard = ContextCompat.getSystemService<InputMethodManager>(
                        context,
                        InputMethodManager::class.java
                    )
                    keyboard?.showSoftInput(this, 0)
                }, 200)
                addTextChangedListener(
                    afterTextChanged = {
                        viewModel.search(binding.editTextSearchQuery.text?.toString())
                    }
                )
            }
            recyclerViewSearch.layoutManager = LinearLayoutManager(context)
            tabLayoutSearch.apply {
                addTab(
                    newTab()
                        .setId(SearchTab.CONTENT.tabId)
                        .setText(
                            getString(R.string.search_content)
                        )
                )
                addTab(
                    newTab()
                        .setId(SearchTab.CELEBRITY.tabId)
                        .setText(
                            getString(R.string.search_celebrity)
                        )
                )
            }
            tabLayoutSearch.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.let { selectedTab ->
                        if (selectedTab.id == SearchTab.CONTENT.tabId) {
                            viewModel.changeTab(SearchTab.CONTENT)
                        } else {
                            viewModel.changeTab(SearchTab.CELEBRITY)
                        }
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {

                }

                override fun onTabReselected(tab: TabLayout.Tab?) {

                }
            })
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToCelebrityFragment(celebrityId: Int) {
        findNavController().navigate(
            SearchFragmentDirections.actionFragmentSearchToFragmentCelebrity(
                celebrityId
            )
        )
    }

    private fun navigateToContentFragment(contentId: Int) {
        findNavController().navigate(
            SearchFragmentDirections.actionFragmentSearchToContentFragment(
                contentId
            )
        )
    }
}