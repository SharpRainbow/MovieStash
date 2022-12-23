package ru.mirea.moviestash.collections

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import ru.mirea.moviestash.DatabaseController
import ru.mirea.moviestash.MainActivity
import ru.mirea.moviestash.R
import ru.mirea.moviestash.Result
import ru.mirea.moviestash.databinding.FragmentCollectionBinding
import java.sql.ResultSet

class CollectionFragment : Fragment() {

    private lateinit var binding: FragmentCollectionBinding
    private lateinit var colContainer: RecyclerView
    private var isRefreshing = false
    private val cols = mutableListOf<Collection>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCollectionBinding.inflate(inflater, container, false)
        bindViews()
        return binding.root
    }

    private fun bindViews(){
        colContainer = binding.colRcVw
        colContainer.layoutManager = GridLayoutManager(context, 2)

            lifecycleScope.launch {
                when(val result: Result<Boolean> = DatabaseController.isModerator()) {
                    is Result.Success<Boolean> ->
                        if (result.data)
                            colContainer.adapter = CollectionAdapter(cols, false){
                                if (it.id <= 0)
                                    return@CollectionAdapter
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
                                    if (name.isEmpty())
                                        return@setOnClickListener
                                    lifecycleScope.launch {
                                        DatabaseController.user?.let { _ ->
                                            when (val res : Result<Boolean> =
                                                DatabaseController.addModUserCols(
                                                    it.id,
                                                    name, desc, true
                                                )) {
                                                is Result.Success<Boolean> -> {}
                                                is Result.Error -> {
                                                    Toast.makeText(
                                                        context,
                                                        res.exception.message, Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                        }
                                    }
                                    refreshContent()
                                    dlg.dismiss()
                                }
                                view.findViewById<TextView>(R.id.cancelBtn).setOnClickListener {
                                    dlg.dismiss()
                                }
                                delete.visibility = View.VISIBLE
                                hide.visibility = View.VISIBLE
                                delete.setOnClickListener { _ ->
                                    lifecycleScope.launch {
                                        when(val res: Result<Boolean> =
                                            DatabaseController.deleteUserCols(it.id)){
                                            is Result.Success<Boolean> -> {
                                                refreshContent()
                                                dlg.dismiss()
                                            }
                                            is Result.Error -> {
                                                Toast.makeText(
                                                    context,
                                                    res.exception.message, Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    }
                                }
                                hide.setOnClickListener { _ ->
                                    lifecycleScope.launch {
                                        when(val res: Result<Boolean> =
                                            DatabaseController.hideCols(it.id)){
                                            is Result.Success<Boolean> -> {
                                                refreshContent()
                                                dlg.dismiss()
                                            }
                                            is Result.Error -> {
                                                Toast.makeText(
                                                    context,
                                                    res.exception.message, Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    }
                                }
                                dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                                dlg.show()
                            }
                        else
                            colContainer.adapter = CollectionAdapter(cols, false){}
                    is Result.Error -> Toast.makeText(context,
                        result.exception.message, Toast.LENGTH_SHORT).show()
                }
            }

    }

    companion object {
        @JvmStatic
        fun newInstance() =
            CollectionFragment()
    }

    fun refreshContent(){
        if (isRefreshing)
            return
        isRefreshing = true
        if (cols.size > 0) {
            val prevSize = cols.size
            cols.clear()
            binding.colRcVw.adapter?.notifyItemRangeRemoved(0, prevSize)
        }
        cols.add(
            Collection(0, "Лучшие фильмы", description = "Лучшие фильмы по версии пользователей MovieStash")
        )
        lifecycleScope.launch{
            //when(val result: Result<Boolean> = DatabaseController.checkConnection()) {
            //    is Result.Success<Boolean> -> {
            //        if (!result.data){
            //            Toast.makeText(context, "Ошибка сетевого запроса", Toast.LENGTH_SHORT).show()
            //            (activity as MainActivity).endRefresh()
            //            return@launch
            //        }
            //    }
            //    is Result.Error -> {
            //        Toast.makeText(context, result.exception.message, Toast.LENGTH_SHORT).show()
            //    }
            //}
            when (val result : Result<ResultSet> = DatabaseController.getEditorCols()) {
                is Result.Success<ResultSet> -> {
                    result.data.let { set ->
                        //val prevSize = cols.size
                        while (set.next()){
                                val tmp = Collection(set.getInt("collection_id"),
                                    set.getString("name"), 0)
                                tmp.description = set.getString("description")
                            cols.add(tmp)
                            }
                            if (cols.size > 1)
                                colContainer.adapter?.notifyItemRangeInserted(1, cols.size - 1)
                            else
                                Toast.makeText(context, "Ничего не найдено", Toast.LENGTH_SHORT).show()
                    }
                    }
                is Result.Error -> {
                    Toast.makeText(
                        requireContext(),
                        result.exception.message, Toast.LENGTH_SHORT
                    ).show()
                    (activity as MainActivity).showError()
                }
            }
            (activity as MainActivity).endRefresh()
            isRefreshing = false
        }
    }
}