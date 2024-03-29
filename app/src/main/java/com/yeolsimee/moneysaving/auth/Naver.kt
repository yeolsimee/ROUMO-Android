package com.yeolsimee.moneysaving.auth

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.navercorp.nid.NaverIdLoginSDK

object Naver {
    fun init(
        result: ActivityResult,
        tokenCallback: (String) -> Unit,
        failedCallback: (String) -> Unit
    ) {
        try {
            val functions = Firebase.functions(regionOrCustomDomain = "asia-northeast1")
            when (result.resultCode) {
                ComponentActivity.RESULT_OK -> {
                    // 네이버 로그인 인증이 성공했을 때 수행할 코드 추가
                    val accessToken = NaverIdLoginSDK.getAccessToken()
                    AuthFunctions.customAuthByFirebaseFunctions(
                        functions,
                        accessToken,
                        "naverCustomAuth"
                    ) { token ->
                        FirebaseAuth.getInstance().signInWithCustomToken(token).addOnCompleteListener {
                            tokenCallback(token)
                        }
                    }
                }

                ComponentActivity.RESULT_CANCELED -> {
                    // 실패 or 에러
                    val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                    val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                    failedCallback("errorCode:$errorCode, errorDesc:$errorDescription")
                }
            }
        } catch (_: Exception) {}
    }

    fun login(
        context: Context,
        launcher: ActivityResultLauncher<Intent>
    ): Boolean {
        try {
            NaverIdLoginSDK.authenticate(context, launcher)
        } catch (_: Exception) {
            return false
        }
        return true
    }

    fun logout() {
        try {
            NaverIdLoginSDK.logout()
        } catch (_: Exception) { }
    }
}