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
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.launch
import ru.mirea.moviestash.R
import ru.mirea.moviestash.databinding.FragmentCollectionBinding
import ru.mirea.moviestash.domain.entities.CollectionEntity

class CollectionFragment : Fragment() {

    private lateinit var binding: FragmentCollectionBinding
    private val collectionsAdapter: CollectionAdapter by lazy {
        CollectionAdapter().apply {
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
        binding = FragmentCollectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews()
        bindListeners()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.isModerator()
                viewModel.state.collect { state ->
                    if (state.isLoading) {
                        binding.swipeRefreshLayoutCollections.isRefreshing = true
                    } else {
                        binding.swipeRefreshLayoutCollections.isRefreshing = false
                        if (state.error == null) {
                            collectionsAdapter.submitList(
                                state.collections
                            )
                        } else {
                            showToast(getString(R.string.loading_error))
                        }
                    }
                }
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
        val metrics = resources.displayMetrics
        val columnsCount = metrics.widthPixels / TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            COLUMN_WIDTH,
            metrics
        ).toInt()
        binding.colRcVw.apply {
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
            viewModel.resetPage()
            viewModel.getCollections()
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

    companion object {
        private const val COLUMN_WIDTH = 160f
    }
}