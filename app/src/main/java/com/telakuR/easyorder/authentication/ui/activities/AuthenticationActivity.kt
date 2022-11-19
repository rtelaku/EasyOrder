package com.telakuR.easyorder.authentication.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.telakuR.easyorder.authentication.navigation.AuthenticationNavigation
import com.telakuR.easyorder.ui.theme.Background
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthenticationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(),
                color = Background
            ) {
                AuthenticationNavigation()
            }
        }
    }
}