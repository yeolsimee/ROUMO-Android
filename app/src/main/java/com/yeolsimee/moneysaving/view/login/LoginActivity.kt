package com.yeolsimee.moneysaving.view.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.yeolsimee.moneysaving.auth.*
import com.yeolsimee.moneysaving.ui.theme.MoneyCatcherTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
@ExperimentalMaterial3Api
class LoginActivity : ComponentActivity() {



    private lateinit var googleLoginLauncher: ActivityResultLauncher<Intent>
    private lateinit var naverLoginLauncher: ActivityResultLauncher<Intent>

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initAuth()

        setContent {
            MoneyCatcherTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(modifier = Modifier) {
                            Button(onClick = {
                                viewModel.googleLogin(googleLoginLauncher)
                            }) {
                                Text(text = "Google Login")
                            }

                            Button(onClick = {
                                viewModel.googleLogout()
                            }) {
                                Text(text = "Google Logout")
                            }
                        }

                        Row(modifier = Modifier) {
                            Button(onClick = {
                                Naver.login(applicationContext, naverLoginLauncher)
                            }) {
                                Text(text = "Naver Login")
                            }

                            Button(onClick = {
                                Naver.logout()
                            }) {
                                Text(text = "Naver Logout")
                            }
                        }

                        Row(modifier = Modifier) {
                            Button(onClick = {
                                Kakao.login(this@LoginActivity)
                            }) {
                                Text(text = "Kakao Login")
                            }

                            Button(onClick = {
                                Kakao.logout()
                            }) {
                                Text(text = "Kakao Logout")
                            }
                        }

                        val textState = remember { mutableStateOf("") }

                        TextField(value = textState.value, onValueChange = {
                            textState.value = it
                        })

                        Button(onClick = {
                            val email = textState.value
                            Toast.makeText(applicationContext, email, Toast.LENGTH_SHORT).show()
                            Email.send(email)
                        }) {
                            Text(text = "Email Login")
                        }

                        Button(onClick = {
                            viewModel.appleLogin(this@LoginActivity)
                        }) {
                            Text(text = "Apple Login")
                        }
                    }
                }
            }
        }
    }

    private fun initAuth() {
        initGoogleLogin()
        initNaverLogin()
    }

    override fun onResume() {
        super.onResume()
        Email.receive(intent, this@LoginActivity)
        Firebase.auth.pendingAuthResult?.addOnSuccessListener { authResult ->
            if (authResult.credential != null) {
                val task = Firebase.auth.signInWithCredential(authResult.credential!!)
                AuthFunctions.getAuthResult(task)
            }
        }
    }

    private fun initNaverLogin() {
        naverLoginLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                Naver.init(result)
            }
    }

    private fun initGoogleLogin() {
        viewModel.init(this@LoginActivity)
        googleLoginLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                viewModel.googleInit(it)
            }
    }
}