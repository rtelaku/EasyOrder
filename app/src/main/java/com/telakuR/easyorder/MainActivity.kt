package com.telakuR.easyorder

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.telakuR.easyorder.authentication.models.services.AccountService
import com.telakuR.easyorder.authentication.ui.activities.AuthenticationActivity
import com.telakuR.easyorder.ui.theme.EasyOrderTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var accountService: AccountService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            EasyOrderTheme {
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val currentUser = accountService.currentUser
        if(currentUser != null){
            Log.d("rigiii", "logged in ${currentUser.email}")
        } else {
            val loginIntent = Intent(this@MainActivity, AuthenticationActivity::class.java)
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(loginIntent)
        }

    }
}
