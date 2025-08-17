package ru.mirea.moviestash.presentation.news_editor

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import ru.mirea.moviestash.R
import ru.mirea.moviestash.databinding.FragmentNewsEditorBinding
import ru.mirea.moviestash.domain.entities.NewsEntity

class NewsEditorFragment : Fragment() {

    private var _binding: FragmentNewsEditorBinding? = null
    private val binding: FragmentNewsEditorBinding
        get() = _binding!!
    private val arguments by navArgs<NewsEditorFragmentArgs>()
    private val viewModel: NewsEditorViewModel by viewModels()
    private var imageUri: Uri? = null

    private val pickImageIntent =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { pictureUri ->
            pictureUri?.let {
                binding.imageViewNews.setImageURI(pictureUri)
                imageUri = pictureUri
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewsEditorBinding.inflate(
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
                if (arguments.newsId != -1) {
                    viewModel.getNewsById(arguments.newsId)
                }
                viewModel.state.collect { state ->
                    when (state) {
                        is NewsEditorState.Success -> {
                            displayNews(state.news)
                        }

                        is NewsEditorState.Finished -> {
                            findNavController().popBackStack()
                        }

                        is NewsEditorState.Error -> {
                            if (state.dataError) {
                                showToast(getString(R.string.error_connection))
                            }
                            binding.textInputLayoutNewsTitle.error =
                                if (state.errorInputTitle)
                                    getString(R.string.news_title_not_empty)
                                else
                                    null
                            binding.editTextNewsDescription.error =
                                if (state.errorInputContent)
                                    getString(R.string.news_text_not_empty)
                                else
                                    null
                        }

                        NewsEditorState.Initial -> {}
                    }
                }
            }
        }
    }

    private fun displayNews(news: NewsEntity) {
        binding.apply {
            editTextNewsTitle.setText(news.title)
            editTextNewsDescription.setText(news.description)
            Glide.with(requireContext())
                .load(news.imageUrl)
                .placeholder(R.drawable.r_placeholder)
                .error(imageUri)
                .into(binding.imageViewNews)
        }
    }

    private fun bindListeners() {
        binding.toolbarNewsEditor.apply {
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
        binding.imageViewNews.setOnClickListener {
            pickImageIntent.launch(PickVisualMediaRequest(
                ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
        binding.buttonSaveNews.setOnClickListener {
            if (arguments.newsId != -1) {
                viewModel.updateNews(
                    arguments.newsId,
                    binding.editTextNewsTitle.text?.toString(),
                    binding.editTextNewsDescription.text?.toString(),
                    imageUri
                )
            } else {
                viewModel.addNews(
                    binding.editTextNewsTitle.text?.toString(),
                    binding.editTextNewsDescription.text?.toString(),
                    imageUri
                )
            }
        }
        binding.editTextNewsTitle.addTextChangedListener(
            onTextChanged = { _, _, _, _ ->
                viewModel.resetErrorInputTitle()
            }
        )
        binding.editTextNewsDescription.addTextChangedListener(
            onTextChanged = { _, _, _, _ ->
                viewModel.resetErrorInputContent()
            }
        )
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

}