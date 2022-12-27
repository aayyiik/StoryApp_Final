package com.ari.submission.storyapp.ui.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.ari.submission.storyapp.databinding.ActivitySignUpBinding
import com.ari.submission.storyapp.ui.login.LoginActivity
import com.ari.submission.storyapp.utils.MyResult

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    private val registerViewModel by viewModels<RegisterViewModel>{
        RegisterViewModel.Factory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBar.visibility = View.GONE

        //button signup
        binding.btnRegister.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()
            when{
                name.isEmpty()->{
                    binding.edRegisterName.error = "Name is required"
                }
                email.isEmpty()->{
                    binding.edRegisterEmail.error = "Email is required"
                }
                password.isEmpty()->{
                    binding.edRegisterPassword.error = "Password is required"
                }else->{
                    register(name,email,password)
                }
            }
        }

        binding.btnLogin.setOnClickListener {
            val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }


    private fun register(name: String, email: String, password: String){
        registerViewModel.getAllUserRegister(name, email,password).observe(this@SignUpActivity){ result ->
            if (result != null){
                when(result){
                    is MyResult.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is MyResult.Success -> {
                        binding.progressBar.visibility = View.GONE
                        showToast()
                    }
                    is MyResult.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this@SignUpActivity, result.Error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun showToast(){
        Toast.makeText(this@SignUpActivity, "Berhasil Signup", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
            startActivity(intent)
            finishAffinity()

    }


}