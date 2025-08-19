package ru.mirea.moviestash.presentation.collections

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
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.mirea.moviestash.R
import ru.mirea.moviestash.databinding.FragmentCollectionBinding
import ru.mirea.moviestash.domain.entities.CollectionEntity

class CollectionFragment : Fragment() {

    private var _binding: FragmentCollectionBinding? = null
    private val binding: FragmentCollectionBinding
        get() = _binding!!
    private val collectionsAdapter: CollectionPagedAdapter by lazy {
        CollectionPagedAdapter().apply {
            onCollectionClick = { collection ->
                navigateToContentCollectionFragment(
                    collection.id,
                    collection.userId
                )
            }
        }
    }
    private val viewModel: CollectionsListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCollectionBinding.inflate(
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

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.isModerator()
                viewModel.state.onEach { state ->
                    if (state.error != null) {
                        showToast(getString(R.string.loading_error))
                    }
                }.launchIn(this)
                viewModel.collectionsFlow.onEach { pagedCollections ->
                    collectionsAdapter.submitData(pagedCollections)
                }.launchIn(this)
                collectionsAdapter.loadStateFlow.onEach { state ->
                    if (state.hasError) {
                        showToast(getString(R.string.loading_error))
                    }
                    binding.swipeRefreshLayoutCollections.isRefreshing =
                        state.refresh is LoadState.Loading
                }.launchIn(this)
            }
        }
    }

    private fun showPopupMenu(itemView: View, collection: CollectionEntity) {
        val popup = PopupMenu(
            itemView.context,
            itemView
        )
        popup.inflate(R.menu.collection_context_menu)
        popup.menu.findItem(R.id.hide_collection).isVisible = true
        popup.menu.findItem(R.id.publish_collection).isVisible = false
        popup.menu.findItem(R.id.edit_collection).isVisible = false
        popup.menu.findItem(R.id.delete_collection).isVisible = false
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.hide_collection -> {
                    viewModel.hideCollection(collection.id)
                    true
                }

                else -> false
            }
        }
        popup.gravity = Gravity.END
        popup.show()
    }

    private fun bindViews() {
        val columnsCount = calculateColumnsCount()
        binding.recyclerViewCollections.apply {
            layoutManager = GridLayoutManager(context, maxOf(2, columnsCount))
            adapter = collectionsAdapter.apply {
                onCollectionLongClick = { itemView, collection ->
                    if (viewModel.state.value.isModerator) {
                        showPopupMenu(itemView, collection)
                    }
                }
            }
        }
    }

    private fun bindListeners() {
        binding.swipeRefreshLayoutCollections.setOnRefreshListener {
            collectionsAdapter.refresh()
        }
    }

    private fun showToast(message: String) {
        context?.let {
            Toast.makeText(
                it, message, Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun navigateToContentCollectionFragment(collectionId: Int, userId: Int) {
        findNavController().navigate(
            CollectionFragmentDirections.actionFragmentCollectionToFragmentCollectionContent(
                collectionId,
                userId
            )
        )
    }

    private fun calculateColumnsCount(): Int {
        val metrics = resources.displayMetrics
        return metrics.widthPixels / TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            COLUMN_WIDTH,
            metrics
        ).toInt()
    }

    companion object {
        private const val COLUMN_WIDTH = 160f
    }
}