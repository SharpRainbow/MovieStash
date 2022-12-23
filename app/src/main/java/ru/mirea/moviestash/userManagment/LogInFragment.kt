package ru.mirea.moviestash.userManagment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope

import kotlinx.coroutines.launch
import ru.mirea.moviestash.DatabaseController
import ru.mirea.moviestash.MainActivity
import ru.mirea.moviestash.Result
import ru.mirea.moviestash.databinding.FragmentLoginBinding

class LogInFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var enterBtn: Button
    private lateinit var loginIn: EditText
    private lateinit var passIn: EditText
    private val sharedPref by lazy {
        this.activity?.getSharedPreferences("AUTH", Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        bindViews()
        bindListeners()
        return binding.root
    }

    private fun bindViews(){
        enterBtn = binding.enterButton
        loginIn = binding.loginEdMain
        passIn = binding.passEdMain
    }

    private fun bindListeners(){
        enterBtn.setOnClickListener {
            val login = loginIn.text.toString().trim()
            val pass = passIn.text.toString().trim()
            if (login.isEmpty() || pass.isEmpty()) {
                Toast.makeText(context, "Заполните необходимые поля!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            lifecycleScope.launch {
                when (val msg : Result<String> = DatabaseController.login(login, pass)) {
                    is Result.Success<String> -> {
                        (activity as MainActivity).logIn()
                        sharedPref?.let { pr ->
                            pr.edit {
                                putString("LOGIN", login)
                                putString("PASS", pass)
                                apply()
                            }
                            loginIn.text.clear()
                            passIn.text.clear()
                        }
                    }
                    is Result.Error -> {
                        Toast.makeText(
                            context,
                            msg.exception.message, Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }


        }
        binding.registerButton.setOnClickListener {
            startActivity(Intent(context, RegisterActivity::class.java))
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            LogInFragment()
    }
}