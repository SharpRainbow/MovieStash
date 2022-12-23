package ru.mirea.moviestash.userManagment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ru.mirea.moviestash.*
import ru.mirea.moviestash.collections.UserCollectionActivity
import ru.mirea.moviestash.databinding.FragmentAccountBinding
import java.sql.ResultSet

class AccountFragment : Fragment() {

    private lateinit var binding: FragmentAccountBinding
    private lateinit var logOutBtn: Button
    private var isRefreshing = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("DEBUG", "Account view created")
        binding = FragmentAccountBinding.inflate(inflater, container, false)
        binding.user = DatabaseController.user
        bindViews()
        bindListeners()
        refreshContent()
        return binding.root
    }

    private fun bindViews(){
        logOutBtn = binding.exitButton
    }

    private fun bindListeners(){
        logOutBtn.setOnClickListener {
            (activity as MainActivity).logOut()
        }
        binding.myCols.setOnClickListener {
            startActivity(Intent(context, UserCollectionActivity::class.java))
        }
        binding.bannedUsers.setOnClickListener {
            startActivity(Intent(context, BannedUsersActivity::class.java))
        }
        binding.changeUserData.setOnClickListener {
            startActivity(Intent(context, RegisterActivity::class.java))
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            AccountFragment()
    }

    fun refreshContent(){
        if (isRefreshing)
            return
        isRefreshing = true
        lifecycleScope.launch {
            when(val result: Result<Boolean> = DatabaseController.isModerator()) {
                is Result.Success<Boolean> -> {
                    if (result.data){
                        binding.bannedUsers.visibility = View.VISIBLE
                    }
                    else
                        binding.bannedUsers.visibility = View.GONE
                }
                is Result.Error -> {Toast.makeText(context, result.exception.message, Toast.LENGTH_SHORT).show()}
            }
            when(val result: Result<ResultSet> = DatabaseController.isBanned()) {
                is Result.Success<ResultSet> -> {
                    result.data.let {
                        if (it.next()){
                            if (it.getBoolean("is_banned")){
                                binding.banMessage.visibility = View.VISIBLE
                                binding.banMessage.text = getString(
                                    R.string.ban_message,
                                    Utils.dateToString(it.getDate("ban_date")), it.getString("ban_reason"))
                            }
                            else
                                binding.banMessage.visibility = View.GONE
                        }
                    }

                }
                is Result.Error -> {Toast.makeText(context, result.exception.message, Toast.LENGTH_SHORT).show()}
            }
            DatabaseController.refreshUserData()
            (activity as MainActivity).endRefresh()
            isRefreshing = false
        }
    }
}