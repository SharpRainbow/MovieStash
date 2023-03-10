package ru.mirea.moviestash.userManagment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

import kotlinx.coroutines.launch
import ru.mirea.moviestash.DatabaseController
import ru.mirea.moviestash.Result
import ru.mirea.moviestash.databinding.ActivityBannedUsersBinding
import java.sql.ResultSet

class BannedUsersActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var binding: ActivityBannedUsersBinding
    private val bannedUsersList: MutableList<BannedUser> by lazy {
        mutableListOf()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBannedUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bindViews()
        binding.bannedUsersRefr.isRefreshing = true
        onRefresh()

        setSupportActionBar(binding.bannedUsersToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun bindViews(){
        binding.bannedUsersRv.layoutManager = LinearLayoutManager(this)
        binding.bannedUsersRv.adapter = BannedUserAdapter(bannedUsersList){
            val bld = AlertDialog.Builder(this@BannedUsersActivity)
            bld.setTitle("Разбанить пользователя?")
            bld.setPositiveButton("Да") { _, _ ->
                lifecycleScope.launch {
                    when (val result = DatabaseController.manageUser(it, "", false)) {
                        is Result.Success<Boolean> -> {
                            binding.bannedUsersRefr.isRefreshing = true
                            onRefresh()
                        }
                        is Result.Error -> Toast.makeText(this@BannedUsersActivity,
                            result.exception.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            bld.setNegativeButton("Отмена") { _, _ -> }
            bld.create().show()
        }
        binding.bannedUsersRefr.setOnRefreshListener(this)
    }

    override fun onRefresh() {
        if (bannedUsersList.size > 0) {
            val size = bannedUsersList.size
            bannedUsersList.clear()
            binding.bannedUsersRv.adapter?.notifyItemRangeRemoved(0, size)
        }
        lifecycleScope.launch {
            when(val result: Result<Boolean> = DatabaseController.checkConnection()) {
                is Result.Success<Boolean> -> {
                    if (!result.data){
                        Toast.makeText(this@BannedUsersActivity,
                            "Ошибка сетевого запроса", Toast.LENGTH_SHORT).show()
                        return@launch
                    }
                }
                is Result.Error -> {
                    Toast.makeText(this@BannedUsersActivity,
                        result.exception.message, Toast.LENGTH_SHORT).show()}
            }
            when (val result = DatabaseController.getBannedUsers()) {
                is Result.Success<ResultSet> -> {
                    result.data.let {
                        while (it.next()){
                            bannedUsersList.add(
                                BannedUser(it.getInt("uid"), it.getString("nickname"),
                                it.getString("email"), it.getDate("ban_date"),
                                it.getString("ban_reason"))
                            )
                        }
                        binding.bannedUsersRv.adapter?.notifyItemRangeInserted(0, bannedUsersList.size)
                    }
                }
                is Result.Error -> Toast.makeText(this@BannedUsersActivity,
                    result.exception.message, Toast.LENGTH_SHORT).show()
            }
            binding.bannedUsersRefr.isRefreshing = false
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
}