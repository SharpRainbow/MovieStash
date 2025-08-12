package ru.mirea.moviestash.presentation.news

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
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import ru.mirea.moviestash.R
import ru.mirea.moviestash.databinding.FragmentNewsBinding
import ru.mirea.moviestash.domain.entities.NewsEntity
import kotlin.getValue

class NewsFragment : Fragment() {

    private var _binding: FragmentNewsBinding? = null
    private val binding: FragmentNewsBinding
        get() = _binding!!
    private val arguments by navArgs<NewsFragmentArgs>()
    private val viewModel: NewsPageViewModel by viewModels {
        NewsPageViewModel.provideFactory(
            requireActivity().application,
            arguments.newsId
        )
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
        binding.newsToolbar.apply {
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
        binding.deleteNews.setOnClickListener {
            viewModel.deleteNews()
        }
        binding.editNews.setOnClickListener {
            navigateToNewsEditorFragment(arguments.newsId)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.getNews()
                viewModel.state.collect { state ->
                    when (state) {
                        is NewsPageState.Loading -> {
                            //TODO: Show loading indicator if needed
                        }
                        is NewsPageState.Error -> {
                            showToast(getString(R.string.loading_error))
                        }
                        is NewsPageState.Success -> {
                            displayNews(state.news)
                            if (state.isModerator) {
                                binding.moderatorGroup.visibility = View.VISIBLE
                            } else {
                                binding.moderatorGroup.visibility = View.GONE
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