package ru.mirea.moviestash.presentation.collection_content

import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
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
import ru.mirea.moviestash.R
import ru.mirea.moviestash.databinding.FragmentCollectionContentBinding
import ru.mirea.moviestash.domain.entities.CollectionEntity
import ru.mirea.moviestash.domain.entities.ContentEntityBase
import ru.mirea.moviestash.presentation.content.ContentAdapter
import ru.mirea.moviestash.presentation.content.ContentPagedAdapter
import kotlin.getValue


class CollectionContentFragment : Fragment() {

    private val arguments by navArgs<CollectionContentFragmentArgs>()
    private var _binding: FragmentCollectionContentBinding? = null
    private val binding
        get() = _binding!!
    private val viewModel: CollectionContentViewModel by viewModels {
        CollectionContentViewModel.provideFactory(
            requireActivity().application,
            arguments.collectionId,
            arguments.userId
        )
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
        binding.colToolbar.apply {
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
                viewModel.getCollectionInfo()
                viewModel.getCollectionContents()
                viewModel.state.collect { state ->
                    if (state.isLoading) {
                        // TODO: Show loading indicator
                    } else {
                        if (state.error == null) {
                            state.collectionInfo?.let {
                                showCollectionInfo(it)
                            }
                        }
                        state.collections?.let { collectionPagedList ->
                            collectionPagedList.onEach {
                                collectionContentAdapter.submitData(it)
                            }.launchIn(this)
                        }
                    }
                }
            }
        }
    }

    private fun showCollectionInfo(collection: CollectionEntity) {
        binding.collectionName.text = collection.name
        binding.colDescription.text = collection.description
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