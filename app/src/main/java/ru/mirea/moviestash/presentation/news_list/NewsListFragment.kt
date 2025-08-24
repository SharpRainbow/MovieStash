package ru.mirea.moviestash.presentation.news_list

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
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import ru.mirea.moviestash.MovieStashApplication
import ru.mirea.moviestash.R
import ru.mirea.moviestash.databinding.FragmentNewsListBinding
import ru.mirea.moviestash.presentation.ViewModelFactory
import javax.inject.Inject

class NewsListFragment : Fragment() {

    private var _binding: FragmentNewsListBinding? = null
    private val binding: FragmentNewsListBinding
        get() = _binding!!
    private var addButtonProvider: AddButtonProvider? = null
    private val newsAdapter: NewsPagingAdapter by lazy {
        NewsPagingAdapter().apply {
            onNewsClick = { news ->
                navigateToNewsFragment(
                    news.id
                )
            }
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: NewsListViewModel by viewModels {
        viewModelFactory
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is AddButtonProvider) {
            addButtonProvider = context
            addButtonProvider?.setOnAddButtonClickListener {
                navigateToNewsEditorFragment()
            }
        } else {
            throw IllegalStateException("Activity must implement AddButtonProvider")
        }
        (requireActivity().application as MovieStashApplication)
            .appComponent
            .rootDestinationsComponentFactory()
            .create()
            .inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsListBinding.inflate(
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

    override fun onDetach() {
        super.onDetach()
        addButtonProvider?.removeOnAddButtonClickListener()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.isModerator()
                launch {
                    viewModel.state.collect { state ->
                        if (state.isModerator) {
                            addButtonProvider?.showAddButton()
                        } else {
                            addButtonProvider?.hideAddButton()
                        }
                    }
                }
                launch {
                    viewModel.newsList.collect { pagingData ->
                        newsAdapter.submitData(pagingData)
                    }
                }
                launch {
                    newsAdapter.loadStateFlow.collect { state ->
                        if (state.hasError) {
                            showToast(getString(R.string.loading_error))
                        }
                        binding.swipeRefreshLayoutNewsList.isRefreshing =
                            state.refresh is LoadState.Loading
                    }
                }
            }
        }
    }

    private fun bindViews() {
        binding.recyclerViewNewsList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = newsAdapter
        }
    }

    private fun bindListeners() {
        binding.swipeRefreshLayoutNewsList.setOnRefreshListener {
            newsAdapter.refresh()
        }
    }

    private fun showToast(message: String) {
        context?.let {
            Toast.makeText(
                it, message, Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onStop() {
        super.onStop()
        addButtonProvider?.hideAddButton()
    }

    private fun navigateToNewsFragment(newsId: Int) {
        findNavController().navigate(
            NewsListFragmentDirections.actionFragmentNewsListToFragmentNews(
                newsId
            )
        )
    }

    private fun navigateToNewsEditorFragment() {
        findNavController().navigate(
            R.id.action_fragment_news_list_to_fragment_news_editor
        )
    }

    interface AddButtonProvider {

        fun showAddButton()

        fun hideAddButton()

        fun setOnAddButtonClickListener(listener: View.OnClickListener)

        fun removeOnAddButtonClickListener()
    }
}