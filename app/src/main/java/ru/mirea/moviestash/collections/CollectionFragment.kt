package ru.mirea.moviestash.collections

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import ru.mirea.moviestash.ChildFragment
import ru.mirea.moviestash.DatabaseController
import ru.mirea.moviestash.R
import ru.mirea.moviestash.Result
import ru.mirea.moviestash.databinding.FragmentCollectionBinding
import ru.mirea.moviestash.entites.Collection

class CollectionFragment : Fragment(), ChildFragment {

    private lateinit var binding: FragmentCollectionBinding
    private lateinit var colContainer: RecyclerView
    private var isRefreshing = false
    private var offset = 0
    private lateinit var collectionsModel: CollectionsModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentCollectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectionsModel = ViewModelProvider(requireActivity())[CollectionsModel::class.java]
        bindViews()
    }

    private fun bindViews() {
        colContainer = binding.colRcVw
        colContainer.layoutManager = GridLayoutManager(context, 2)
        lifecycleScope.launch {
            when (val result: Result<Boolean> = DatabaseController.isModerator()) {
                is Result.Success<Boolean> -> if (result.data) colContainer.adapter =
                    CollectionAdapter(collectionsModel.getAll(), false) {
                        if (it.id <= 0) return@CollectionAdapter
                        val bld = AlertDialog.Builder(requireContext())
                        val view = layoutInflater.inflate(R.layout.dialog_add_list, null)
                        bld.setView(view)
                        val nameEd = view.findViewById<EditText>(R.id.colNameEd)
                        val descEd = view.findViewById<EditText>(R.id.colDescEd)
                        nameEd.setText(it.name)
                        descEd.setText(it.description)
                        val dlg = bld.create()
                        val delete = view.findViewById<TextView>(R.id.deleteUsrColBtn)
                        val hide = view.findViewById<TextView>(R.id.hideColBtn)
                        view.findViewById<TextView>(R.id.addBtn).setOnClickListener { _ ->
                            val name = nameEd.text.toString().trim()
                            val desc = descEd.text.toString().trim()
                            if (name.isEmpty()) return@setOnClickListener
                            lifecycleScope.launch {
                                DatabaseController.user?.let { _ ->
                                    when (val res: Result<Boolean> =
                                        DatabaseController.addModUserCols(
                                            it.id, name, desc, true
                                        )) {
                                        is Result.Success<Boolean> -> {}
                                        is Result.Error -> {
                                            showToast(res.exception.message ?: "Ошибка")
                                        }
                                    }
                                }
                                loadContent()
                            }
                            dlg.dismiss()
                        }
                        view.findViewById<TextView>(R.id.cancelBtn).setOnClickListener {
                            dlg.dismiss()
                        }
                        delete.visibility = View.VISIBLE
                        hide.visibility = View.VISIBLE
                        delete.setOnClickListener { _ ->
                            lifecycleScope.launch {
                                when (val res: Result<Boolean> =
                                    DatabaseController.deleteUserCols(it.id)) {
                                    is Result.Success<Boolean> -> {
                                        loadContent()
                                        dlg.dismiss()
                                    }

                                    is Result.Error -> {
                                        showToast(res.exception.message ?: "Ошибка")
                                    }
                                }
                            }
                        }
                        hide.setOnClickListener { _ ->
                            lifecycleScope.launch {
                                when (val res: Result<Boolean> =
                                    DatabaseController.hideCols(it.id)) {
                                    is Result.Success<Boolean> -> {
                                        loadContent()
                                        dlg.dismiss()
                                    }

                                    is Result.Error -> {
                                        showToast(res.exception.message ?: "Ошибка")
                                    }
                                }
                            }
                        }
                        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        dlg.show()
                    }
                else colContainer.adapter = CollectionAdapter(collectionsModel.getAll(), false) {}

                is Result.Error -> showToast(result.exception.message ?: "Ошибка")
            }
        }

    }

    companion object {
        @JvmStatic
        fun newInstance() = CollectionFragment()
    }

    override suspend fun loadContent(refresh: Boolean): Boolean {
        if (isRefreshing) return true
        isRefreshing = true
        if (refresh) {
            if (collectionsModel.getSize() > 0) {
                val prevSize = collectionsModel.getSize()
                collectionsModel.clear()
                binding.colRcVw.adapter?.notifyItemRangeRemoved(0, prevSize)
                offset = 0
            }
            collectionsModel.add(
                Collection(
                    id = 0,
                    name = "Лучшие фильмы",
                    description = "Лучшие фильмы по версии пользователей MovieStash",
                    uid = -1
                )
            )
            binding.colRcVw.adapter?.notifyItemInserted(0)
        }
        when (val result: Result<List<Collection>> = DatabaseController.getEditorCols(5, offset)) {
            is Result.Success<List<Collection>> -> {
                result.data.let { set ->
                    val prevSize = collectionsModel.getSize()
                    collectionsModel.addAll(set)
                    if (set.isNotEmpty()) {
                        colContainer.adapter?.notifyItemRangeInserted(prevSize, set.size)
                        offset += set.size
                    } else showToast("Ничего не найдено")
                }
            }

            is Result.Error -> {
                showToast(result.exception.message ?: "Ошибка")
                isRefreshing = false
                return false
            }
        }
        isRefreshing = false
        return true
    }

    private fun showToast(message: String) {
        context?.let {
            Toast.makeText(
                it, message, Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun isInitialized(): Boolean {
        return !collectionsModel.isEmpty()
    }
}