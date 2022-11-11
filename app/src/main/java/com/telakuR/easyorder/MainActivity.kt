package com.telakuR.easyorder

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.telakuR.easyorder.authentication.ui.AuthenticationActivity
import com.telakuR.easyorder.ui.theme.EasyOrderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            EasyOrderTheme {
                openLoginActivity()
            }
        }
    }

    private fun openLoginActivity() {
        val loginIntent = Intent(this@MainActivity, AuthenticationActivity::class.java)
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(loginIntent)
    }
}
