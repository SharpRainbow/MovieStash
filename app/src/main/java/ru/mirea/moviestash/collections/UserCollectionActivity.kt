package ru.mirea.moviestash.collections

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import ru.mirea.moviestash.DatabaseController
import ru.mirea.moviestash.R
import ru.mirea.moviestash.Result
import ru.mirea.moviestash.databinding.ActivityUserCollectionBinding
import ru.mirea.moviestash.entites.Collection

class UserCollectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserCollectionBinding
    private val cols = mutableListOf<Collection>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserCollectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bindViews()
        bindListeners()
        refresh()
        setSupportActionBar(binding.userListToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun bindViews() {
        binding.userColRv.layoutManager = LinearLayoutManager(this)
        binding.userColRv.adapter = CollectionAdapter(cols, false) {
            val bld = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.dialog_add_list, null)
            bld.setView(view)
            val nameEd = view.findViewById<EditText>(R.id.colNameEd)
            val descEd = view.findViewById<EditText>(R.id.colDescEd)
            nameEd.setText(it.name)
            descEd.setText(it.description)
            val dlg = bld.create()
            val delete = view.findViewById<TextView>(R.id.deleteUsrColBtn)
            val publish = view.findViewById<TextView>(R.id.publishColBtn)
            view.findViewById<TextView>(R.id.addBtn).setOnClickListener { _ ->
                val name = nameEd.text.toString().trim()
                val desc = descEd.text.toString().trim()
                if (name.isEmpty()) return@setOnClickListener
                lifecycleScope.launch {
                    DatabaseController.user?.let { _ ->
                        when (val result: Result<Boolean> = DatabaseController.addModUserCols(
                            it.id, name, desc, true
                        )) {
                            is Result.Success<Boolean> -> {

                            }

                            is Result.Error -> {
                                Toast.makeText(
                                    this@UserCollectionActivity,
                                    result.exception.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
                refresh()
                dlg.dismiss()
            }
            view.findViewById<TextView>(R.id.cancelBtn).setOnClickListener {
                dlg.dismiss()
            }
            delete.visibility = View.VISIBLE
            delete.setOnClickListener { _ ->
                lifecycleScope.launch {
                    when (val result: Result<Boolean> = DatabaseController.deleteUserCols(it.id)) {
                        is Result.Success<Boolean> -> {
                            refresh()
                            dlg.dismiss()
                        }

                        is Result.Error -> {
                            Toast.makeText(
                                this@UserCollectionActivity,
                                result.exception.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
            publish.setOnClickListener { _ ->
                lifecycleScope.launch {
                    when (val result: Result<Boolean> = DatabaseController.publishUserCols(it.id)) {
                        is Result.Success<Boolean> -> {
                            refresh()
                            dlg.dismiss()
                        }

                        is Result.Error -> {
                            Toast.makeText(
                                this@UserCollectionActivity,
                                result.exception.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
            lifecycleScope.launch {
                when (val result: Result<Boolean> = DatabaseController.isModerator()) {
                    is Result.Success<Boolean> -> if (result.data) publish.visibility = View.VISIBLE

                    is Result.Error -> Toast.makeText(
                        this@UserCollectionActivity, result.exception.message, Toast.LENGTH_SHORT
                    ).show()
                }
            }
            dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dlg.show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun bindListeners() {
        binding.addToUserCol.setOnClickListener {
            val bld = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.dialog_add_list, null)
            bld.setView(view)
            val dlg = bld.create()
            view.findViewById<TextView>(R.id.addBtn).setOnClickListener inClickListener@{
                val name = view.findViewById<EditText>(R.id.colNameEd).text.toString().trim()
                val desc = view.findViewById<EditText>(R.id.colDescEd).text.toString().trim()
                if (name.isEmpty()) return@inClickListener
                lifecycleScope.launch {
                    DatabaseController.user?.let {
                        when (val result: Result<Boolean> = DatabaseController.addModUserCols(
                            it.id, name, desc, false
                        )) {
                            is Result.Success<Boolean> -> {

                            }

                            is Result.Error -> {
                                Toast.makeText(
                                    this@UserCollectionActivity,
                                    result.exception.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
                refresh()
                dlg.dismiss()
            }
            view.findViewById<TextView>(R.id.cancelBtn).setOnClickListener {
                dlg.dismiss()
            }
            dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dlg.show()
        }
    }

    private fun refresh() {
        var prevSize = cols.size
        cols.clear()
        binding.userColRv.adapter?.notifyItemRangeRemoved(0, prevSize)
        lifecycleScope.launch {
            when (val result: Result<Boolean> = DatabaseController.checkConnection()) {
                is Result.Success<Boolean> -> {
                    if (!result.data) {
                        Toast.makeText(
                            this@UserCollectionActivity,
                            "Ошибка сетевого запроса",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@launch
                    }
                }

                is Result.Error -> {
                    Toast.makeText(
                        this@UserCollectionActivity, result.exception.message, Toast.LENGTH_SHORT
                    ).show()
                }
            }
            DatabaseController.user?.let {
                when (val result: Result<List<ru.mirea.moviestash.entites.Collection>> =
                    DatabaseController.getUserCols(it.id)) {
                    is Result.Success<List<ru.mirea.moviestash.entites.Collection>> -> {
                        result.data.let { set ->
                            prevSize = cols.size
                            cols.addAll(set)
                            if (cols.size - prevSize == 1) binding.userColRv.adapter?.notifyItemInserted(
                                cols.size
                            )
                            else if (cols.size - prevSize > 1) binding.userColRv.adapter?.notifyItemRangeInserted(
                                0,
                                cols.size
                            )
                            else Toast.makeText(
                                this@UserCollectionActivity, "Ничего не найдено", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    is Result.Error -> {
                        Toast.makeText(
                            this@UserCollectionActivity,
                            result.exception.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}