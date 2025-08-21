package ru.mirea.moviestash.presentation.collection_content

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.mirea.moviestash.MovieStashApplication
import ru.mirea.moviestash.R
import ru.mirea.moviestash.databinding.FragmentCollectionContentBinding
import ru.mirea.moviestash.domain.entities.CollectionEntity
import ru.mirea.moviestash.domain.entities.ContentEntityBase
import ru.mirea.moviestash.presentation.ViewModelFactory
import ru.mirea.moviestash.presentation.content.ContentPagedAdapter
import javax.inject.Inject


class CollectionContentFragment : Fragment() {

    private val arguments by navArgs<CollectionContentFragmentArgs>()
    private var _binding: FragmentCollectionContentBinding? = null
    private val binding
        get() = _binding!!

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: CollectionContentViewModel by viewModels {
        viewModelFactory
    }
    private val collectionContentAdapter by lazy {
        ContentPagedAdapter().apply {
            onContentClick = { content ->
                navigateToContentFragment(
                    content.id
                )
            }
            onContentLongClick = { itemView, content ->
                if (arguments.userId > 0) {
                    showContentMenu(itemView, content)
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as MovieStashApplication).appComponent
            .collectionContentComponentFactory()
            .create(
                collectionId = arguments.collectionId,
                userId = arguments.userId
            )
            .inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCollectionContentBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews()
        binding.toolbarCollectionContent.apply {
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
        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.state.onEach { state ->
                    if (state.error == null) {
                        state.collectionInfo?.let {
                            showCollectionInfo(it)
                        }
                    } else {
                        showToast(getString(R.string.loading_error))
                    }
                }.launchIn(this)
                viewModel.collectionContentFlow.onEach {
                    collectionContentAdapter.submitData(it)
                }.launchIn(this)
                collectionContentAdapter.loadStateFlow.onEach { state ->
                    if (state.hasError) {
                        showToast(getString(R.string.loading_error))
                    }
                }.launchIn(this)
            }
        }
    }

    private fun showCollectionInfo(collection: CollectionEntity) {
        with(binding) {
            textViewCollectionName.text = collection.name
            textViewCollectionDescription.text = collection.description
        }
    }

    private fun bindViews() {
        val metrics = resources.displayMetrics
        val columnsCount = metrics.widthPixels / TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            COLUMN_WIDTH,
            metrics
        )
        binding.recyclerViewCollectionItems.apply {
            layoutManager = GridLayoutManager(
                context,
                columnsCount.toInt()
            )
            adapter = collectionContentAdapter
        }
    }

    private fun showContentMenu(itemView: View, content: ContentEntityBase) {
        val popup = PopupMenu(
            itemView.context,
            itemView
        )
        popup.menu.add(getString(R.string.delete)).setOnMenuItemClickListener {
            viewModel.deleteContentFromCollection(content.id)
            true
        }
        popup.gravity = Gravity.END
        popup.show()
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToContentFragment(contentId: Int) {
        findNavController().navigate(
            CollectionContentFragmentDirections.actionFragmentCollectionContentToContentFragment(
                contentId
            )
        )
    }

    companion object {
        private const val COLUMN_WIDTH = 120f
    }

}