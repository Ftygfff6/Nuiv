package com.nuviotv.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.tv.material3.Surface
import com.nuviotv.app.ui.navigation.NiovNavGraph
import com.nuviotv.app.ui.theme.NiovBackground
import com.nuviotv.app.ui.theme.NiovTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NiovTheme {
                Surface(
                    modifier = Modifier.fillMaxSize().background(NiovBackground),
                    color = NiovBackground
                ) { NiovNavGraph() }
            }
        }
    }
}
