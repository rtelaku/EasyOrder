package com.telakuR.easyorder.authentication.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.telakuR.easyorder.authentication.navigation.AuthenticationNavigation

class AuthenticationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AuthenticationNavigation()
        }
    }
}