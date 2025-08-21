package ru.mirea.moviestash.presentation.news

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
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import ru.mirea.moviestash.MovieStashApplication
import ru.mirea.moviestash.R
import ru.mirea.moviestash.databinding.FragmentNewsBinding
import ru.mirea.moviestash.domain.entities.NewsEntity
import ru.mirea.moviestash.presentation.ViewModelFactory
import javax.inject.Inject

class NewsFragment : Fragment() {

    private var _binding: FragmentNewsBinding? = null
    private val binding: FragmentNewsBinding
        get() = _binding!!
    private val arguments by navArgs<NewsFragmentArgs>()

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: NewsPageViewModel by viewModels {
        viewModelFactory
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as MovieStashApplication)
            .appComponent
            .newsComponentFactory()
            .create(arguments.newsId)
            .inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewsBinding.inflate(
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
        binding.toolbarNews.apply {
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
        binding.floatingActionButtonsDeleteNews.setOnClickListener {
            viewModel.deleteNews()
        }
        binding.floatingActionButtonsEditNews.setOnClickListener {
            navigateToNewsEditorFragment(arguments.newsId)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.state.collect { state ->
                    binding.progressBarNews.visibility = View.INVISIBLE
                    when (state) {
                        is NewsPageState.Loading -> {
                            binding.progressBarNews.visibility = View.VISIBLE
                        }
                        is NewsPageState.Error -> {
                            showToast(getString(R.string.loading_error))
                        }
                        is NewsPageState.Success -> {
                            displayNews(state.news)
                            if (state.isModerator) {
                                binding.linearLayoutModeratorActions.visibility = View.VISIBLE
                            } else {
                                binding.linearLayoutModeratorActions.visibility = View.GONE
                            }
                        }
                        NewsPageState.Deleted -> {
                            findNavController().popBackStack()
                        }
                    }
                }
            }
        }
    }

    private fun displayNews(news: NewsEntity) {
        binding.textViewNewsDate.text = news.date
        binding.textViewNewsTitle.text = news.title
        binding.textViewNewsDescription.text = news.description
        Glide.with(this)
            .load(news.imageUrl)
            .placeholder(R.drawable.r_placeholder)
            .into(binding.imageViewNews)
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToNewsEditorFragment(newsId: Int) {
        findNavController().navigate(
            NewsFragmentDirections.actionFragmentNewsToFragmentNewsEditor(newsId)
        )
    }
}