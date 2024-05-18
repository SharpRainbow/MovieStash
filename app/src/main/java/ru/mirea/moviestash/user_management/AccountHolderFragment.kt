package ru.mirea.moviestash.user_management

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ru.mirea.moviestash.ChildFragment
import ru.mirea.moviestash.DatabaseController
import ru.mirea.moviestash.R
import ru.mirea.moviestash.Result
import ru.mirea.moviestash.databinding.FragmentAccountHolderBinding


class AccountHolderFragment : Fragment(), ChildFragment {

    private lateinit var binding: FragmentAccountHolderBinding
    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountHolderBinding.inflate(layoutInflater, container, false)
        sharedPref = requireContext().getSharedPreferences("AUTH", Context.MODE_PRIVATE)
        if (DatabaseController.user != null) logIn()
        return binding.root
    }

    fun logIn() {
        childFragmentManager.findFragmentById(R.id.accFragmentContainer)?.let {
            childFragmentManager.beginTransaction().replace(
                R.id.accFragmentContainer, AccountFragment.newInstance()
            ).commit()
        }
    }

    fun logOut() {
        DatabaseController.logOut()
        childFragmentManager.findFragmentById(R.id.accFragmentContainer)?.let {
            childFragmentManager.beginTransaction().replace(
                R.id.accFragmentContainer, LogInFragment.newInstance()
            ).commit()
        }
        lifecycleScope.launch {
            when (val msg: Result<String> = DatabaseController.login()) {
                is Result.Success<String> -> {
                    sharedPref.edit().clear().apply()
                }

                is Result.Error -> {
                    Toast.makeText(
                        requireContext(), msg.exception.message, Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = AccountHolderFragment()
    }

    override suspend fun loadContent(refresh: Boolean): Boolean {
        childFragmentManager.findFragmentById(R.id.accFragmentContainer)?.let {
            (it as? ChildFragment)?.loadContent()
            return true
        }
        return false
    }

    override fun isInitialized(): Boolean {
        return true
    }
}