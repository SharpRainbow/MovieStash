package ru.mirea.moviestash.presentation.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch
import ru.mirea.moviestash.R
import ru.mirea.moviestash.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding
        get() = _binding!!
    private val viewModel: SearchViewModel by viewModels()
    private val contentAdapter: SearchMovieAdapter by lazy {
        SearchMovieAdapter().apply {
            onListEndReached = {
                viewModel.loadMore()
            }
            onContentClick = { content ->
                navigateToContentFragment(content.id)
            }
        }
    }
    private val celebrityAdapter: SearchCelebrityAdapter by lazy {
        SearchCelebrityAdapter().apply {
            onListEndReached = {
                viewModel.loadMore()
            }
            onCelebrityClick = { celebrity ->
                navigateToCelebrityFragment(celebrity.id)
            }
        }
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
                viewModel.state.collect { state ->
                    if (state.isLoading) {
                        hideNotFound()
                        showProgress()
                    } else {
                        hideProgress()
                        selectCorrectTab(state.currentTab)
                        if (state.currentTab == SearchTab.CONTENT) {
                            contentAdapter.submitList(state.contentList)
                            if (state.contentList.isEmpty())
                                showNotFound()
                        } else {
                            celebrityAdapter.submitList(state.celebrityList)
                            if (state.celebrityList.isEmpty())
                                showNotFound()
                        }
                    }
                }
            }
        }
    }

    private fun selectCorrectTab(currentTab: SearchTab) {
        val tabIndex = currentTab.tabId
        if (binding.tabs.selectedTabPosition != tabIndex) {
            binding.tabs.getTabAt(tabIndex)?.select()
        }

        val expectedAdapter =
            if (currentTab == SearchTab.CONTENT)
                contentAdapter
            else
                celebrityAdapter
        if (binding.recyclerViewSearch.adapter != expectedAdapter) {
            binding.recyclerViewSearch.adapter = expectedAdapter
        }
    }

    private fun bindListeners() {
        binding.customToolbar.apply {
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
        binding.contentSearcher.addTextChangedListener(
            afterTextChanged = {
                viewModel.search(binding.contentSearcher.text?.toString())
            }
        )
        binding.contentSearcher.apply {
            postDelayed({
                isFocusableInTouchMode = true
                requestFocus()
                val keyboard = ContextCompat.getSystemService<InputMethodManager>(
                    context,
                    InputMethodManager::class.java
                )
                keyboard?.showSoftInput(this, 0)
            }, 200)
        }
        binding.recyclerViewSearch.layoutManager = LinearLayoutManager(context)
        binding.tabs.apply {
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
        binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let { selectedTab ->
                    if (selectedTab.id == SearchTab.CONTENT.tabId) {
                        viewModel.changeTab(SearchTab.CONTENT)
                    } else {
                        viewModel.changeTab(SearchTab.CELEBRITY)
                    }
                    viewModel.search(binding.contentSearcher.text?.toString())
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })
    }

    private fun showNotFound() {
        binding.notFoundTv.visibility = View.VISIBLE
    }

    private fun hideNotFound() {
        binding.notFoundTv.visibility = View.GONE
    }

    private fun showProgress() {
        binding.searchPrBar.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        binding.searchPrBar.visibility = View.INVISIBLE
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