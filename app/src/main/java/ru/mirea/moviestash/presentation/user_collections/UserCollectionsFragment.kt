package ru.mirea.moviestash.presentation.user_collections

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import ru.mirea.moviestash.R
import ru.mirea.moviestash.databinding.DialogAddListBinding
import ru.mirea.moviestash.databinding.FragmentUserCollectionsBinding
import ru.mirea.moviestash.presentation.collections.CollectionAdapter
import ru.mirea.moviestash.presentation.collections.CollectionPagedAdapter
import kotlin.getValue

class UserCollectionsFragment : Fragment() {

    private var _binding: FragmentUserCollectionsBinding? = null
    private val binding
        get() = _binding!!
    private var dialogBinding: DialogAddListBinding? = null
    private val viewModel: UserCollectionsViewModel by viewModels()
    private val collectionAdapter: CollectionPagedAdapter by lazy {
        CollectionPagedAdapter(true).apply {
            onCollectionClick = { collection ->
                navigateToCollectionActivity(
                    collection.id,
                    collection.userId
                )
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserCollectionsBinding.inflate(
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

    private fun bindViews() {
        binding.recyclerViewUserCollections.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewUserCollections.adapter = collectionAdapter.apply {
            onCollectionLongClick = { itemView, collection ->
                val popup = PopupMenu(
                    itemView.context,
                    itemView
                )
                popup.inflate(R.menu.collection_context_menu)
                popup.menu.findItem(R.id.publish_collection).isVisible =
                    viewModel.state.value.isModerator
                popup.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.edit_collection -> {
                            showCollectionDialog(collection.id)
                            true
                        }

                        R.id.delete_collection -> {
                            viewModel.deleteCollection(collection.id)
                            true
                        }

                        R.id.publish_collection -> {
                            viewModel.publishCollection(collection.id)
                            true
                        }

                        else -> false
                    }
                }
                popup.gravity = Gravity.END
                popup.show()
            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.state.onEach { state ->
                    binding.swipeRefreshLayoutUserCollections.isRefreshing = state.isLoading
                    if (!state.isLoading) {
                        if (state.errorInputName) {
                            showToast(
                                getString(R.string.collection_name_not_empty)
                            )
                        }
                        if (state.error == null) {
                            state.modifiedCollection?.let { collection ->
                                dialogBinding?.editTextCollectionName?.setText(collection.name)
                                dialogBinding?.editTextCollectionDescription?.setText(
                                    collection.description
                                )
                            }
                        } else {
                            showToast(
                                getString(R.string.loading_error)
                            )
                        }
                    }
                }.launchIn(this)
                viewModel.collectionFlow.onEach {
                    collectionAdapter.submitData(it)
                }.launchIn(this)
                collectionAdapter.loadStateFlow.onEach { state ->
                    binding.swipeRefreshLayoutUserCollections.isRefreshing =
                        state.refresh is LoadState.Loading
                    if (state.hasError) {
                        showToast(
                            getString(R.string.loading_error)
                        )
                    }
                }.launchIn(this)
            }
        }
    }

    private fun bindListeners() {
        binding.toolbarUserList.apply {
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
        binding.floatingActionButtonAddCollection.setOnClickListener {
            showCollectionDialog()
        }
        binding.swipeRefreshLayoutUserCollections.setOnRefreshListener {
            viewModel.refreshCollections()
        }
    }

    private fun showCollectionDialog(collectionId: Int = -1) {
        viewModel.resetErrorInputName()
        val builder = MaterialAlertDialogBuilder(requireContext())
        val localBinding = DialogAddListBinding.inflate(
            layoutInflater,
            binding.root,
            false
        ).also {
            dialogBinding = it
        }
        builder.setView(localBinding.root)
        builder.setTitle("Create Collection")
        builder.setPositiveButton("Save") { _, _ ->
            if (collectionId != -1) {
                viewModel.updateCollection(
                    collectionId,
                    localBinding.editTextCollectionName.text?.toString(),
                    localBinding.editTextCollectionDescription.text?.toString()
                )
            } else {
                viewModel.addCollection(
                    localBinding.editTextCollectionName.text?.toString(),
                    localBinding.editTextCollectionDescription.text?.toString()
                )
            }
        }
        builder.setNegativeButton("Cancel") { _, _ ->

        }
        builder.setOnDismissListener {
            dialogBinding = null
        }
        if (collectionId != -1) {
            viewModel.getCollectionInfo(collectionId)
        }
        builder.show()
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToCollectionActivity(collectionId: Int, userId: Int) {
        findNavController().navigate(
            UserCollectionsFragmentDirections.actionFragmentUserCollectionsToFragmentCollectionContent(
                collectionId,
                userId
            )
        )
    }
}