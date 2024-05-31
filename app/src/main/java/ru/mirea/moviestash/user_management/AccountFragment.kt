package ru.mirea.moviestash.user_management

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ru.mirea.moviestash.ChildFragment
import ru.mirea.moviestash.DatabaseController
import ru.mirea.moviestash.R
import ru.mirea.moviestash.Result
import ru.mirea.moviestash.Utils
import ru.mirea.moviestash.collections.UserCollectionActivity
import ru.mirea.moviestash.databinding.FragmentAccountBinding

class AccountFragment : Fragment(), ChildFragment {

    private lateinit var binding: FragmentAccountBinding
    private lateinit var logOutBtn: Button
    private var isRefreshing = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountBinding.inflate(inflater, container, false)
        binding.user = DatabaseController.user
        bindViews()
        bindListeners()
        lifecycleScope.launch {
            loadContent()
        }
        return binding.root
    }

    private fun bindViews() {
        logOutBtn = binding.exitButton
    }

    private fun bindListeners() {
        logOutBtn.setOnClickListener {
            (parentFragment as? AccountHolderFragment)?.logOut()
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

    companion object {
        @JvmStatic
        fun newInstance() = AccountFragment()
    }

    override suspend fun loadContent(refresh: Boolean): Boolean {
        if (isRefreshing) return true
        isRefreshing = true
        when (val result: Result<Boolean> = DatabaseController.isModerator()) {
            is Result.Success<Boolean> -> {
                if (result.data) {
                    binding.bannedUsers.visibility = View.VISIBLE
                } else binding.bannedUsers.visibility = View.INVISIBLE
            }

            is Result.Error -> {
                showToast(result.exception.message ?: "Ошибка")
            }
        }
        DatabaseController.refreshUserData()
        DatabaseController.user?.let {
            if (it.banned) {
                binding.banMessage.visibility = View.VISIBLE
                binding.banMessage.text = getString(
                    R.string.ban_message, Utils.dateToString(it.banDate), it.banReason
                )
            } else binding.banMessage.visibility = View.INVISIBLE
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
        return true
    }


}