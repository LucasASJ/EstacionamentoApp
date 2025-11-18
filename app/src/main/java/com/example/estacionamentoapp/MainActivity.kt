package com.example.estacionamentoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.example.estacionamentoapp.navigation.NavGraph

class MainActivity : ComponentActivity() {

    val controller = EstacionamentoController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                NavGraph()
            }
        }
    }
}
