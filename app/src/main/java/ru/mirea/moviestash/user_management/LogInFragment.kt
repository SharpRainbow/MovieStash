package ru.mirea.moviestash.user_management

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.mirea.moviestash.DatabaseController
import ru.mirea.moviestash.Result
import ru.mirea.moviestash.databinding.DialogCollectionsBinding
import ru.mirea.moviestash.databinding.FragmentLoginBinding
import ru.mirea.moviestash.entites.Credentials


class LogInFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var enterBtn: Button
    private lateinit var loginIn: EditText
    private lateinit var passIn: EditText
    private lateinit var sharedPref: SharedPreferences
    private lateinit var creds: List<Credentials>
    private val dao by lazy {
        CredentialsDatabase.getDatabase(requireContext()).credsDao()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        sharedPref = requireContext().getSharedPreferences("AUTH", Context.MODE_PRIVATE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews()
        bindListeners()
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            context?.let {
                creds = withContext(Dispatchers.IO) {
                    dao.getAll()
                }
                binding.autocompleteButton.visibility =
                    if (creds.isNotEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    private fun bindViews() {
        enterBtn = binding.enterButton
        loginIn = binding.loginEdMain
        passIn = binding.passEdMain
    }

    private fun bindListeners() {
        enterBtn.setOnClickListener {
            val login = loginIn.text.toString().trim()
            val pass = passIn.text.toString().trim()
            if (login.isEmpty() || pass.isEmpty()) {
                showToast("Заполните необходимые поля!")
                return@setOnClickListener
            }
            lifecycleScope.launch {
                when (val msg: Result<String> = DatabaseController.login(login, pass)) {
                    is Result.Success<String> -> {
                        (parentFragment as? AccountHolderFragment)?.logIn()
                        sharedPref.edit {
                            putString("LOGIN", login)
                            putString("PASS", pass)
                            apply()
                        }
                        loginIn.text.clear()
                        passIn.text.clear()
                        if (creds.find { x -> x.username == login && x.password == pass } == null) showSaveSnack(
                            pass
                        )
                    }

                    is Result.Error -> {
                        showToast(msg.exception.message ?: "Ошибка")
                    }
                }
            }


        }
        binding.registerButton.setOnClickListener {
            startActivity(Intent(context, RegisterActivity::class.java))
        }
        binding.autocompleteButton.setOnClickListener {
            showAutoCompleteDialog()
        }
    }

    private fun showToast(message: String) {
        context?.let {
            Toast.makeText(
                it, message, Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showSaveSnack(password: String) {
        val saveSnackBar =
            Snackbar.make(binding.root, "Сохранить логин и пароль?", Snackbar.LENGTH_LONG)
        val params = saveSnackBar.view.layoutParams as CoordinatorLayout.LayoutParams
        params.gravity = Gravity.TOP
        saveSnackBar.view.layoutParams = params
        saveSnackBar.setAction("ОК") {
            DatabaseController.user?.let { user ->
                saveSnackBar.dismiss()
                lifecycleScope.launch(Dispatchers.IO) {
                    dao.insertCred(
                        Credentials(user.login, password, user.email)
                    )
                }
            }
        }
        saveSnackBar.show()
    }

    private fun showAutoCompleteDialog() {
        if (!::creds.isInitialized || creds.isEmpty()) return
        val builder = AlertDialog.Builder(requireContext())
        val dialogCollectionsBinding = DialogCollectionsBinding.inflate(layoutInflater)
        builder.setView(dialogCollectionsBinding.root)
        dialogCollectionsBinding.colsDialogTv.text = "Сохраненные учетные записи"
        val dialog = builder.create()
        dialogCollectionsBinding.usrColsRv.layoutManager = LinearLayoutManager(requireContext())
        dialogCollectionsBinding.usrColsRv.adapter =
            CredentialsAdapter((creds as MutableList<Credentials>), {
                binding.loginEdMain.setText(it.username)
                binding.passEdMain.setText(it.password)
                dialog.dismiss()
            }, {
                lifecycleScope.launch {
                    if (creds.isEmpty()) {
                        dialog.dismiss()
                        binding.autocompleteButton.visibility = View.GONE
                    }
                    withContext(Dispatchers.IO) {
                        dao.remove(it)
                    }
                }
            })
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    companion object {
        @JvmStatic
        fun newInstance() = LogInFragment()
    }
}