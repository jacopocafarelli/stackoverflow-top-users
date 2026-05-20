package com.jc.topstackoverflowusers

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.jc.topstackoverflowusers.presentation.TopStackOverflowUsersScreen
import com.jc.topstackoverflowusers.ui.theme.TopStackoverflowUsersTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TopStackoverflowUsersTheme {
                TopStackOverflowUsersScreen()
            }
        }
    }
}