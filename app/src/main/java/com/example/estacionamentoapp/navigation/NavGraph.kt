package com.example.estacionamentoapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.estacionamentoapp.EstacionamentoController
import com.example.estacionamentoapp.screens.veiculos.VeiculoScreen
import com.example.estacionamentoapp.screens.vagas.VagasScreen
import com.example.estacionamentoapp.screens.motoristas.MotoristaScreen

@Composable
fun NavGraph(modifier: Modifier = Modifier) {

    val navController = rememberNavController()
    val controller = EstacionamentoController()

    NavHost(
        navController = navController,
        startDestination = "veiculos",
        modifier = modifier
    ) {

        // Tela principal - Veículos
        composable("veiculos") {
            VeiculoScreen(
                controller = controller,
                onNavigateToVagas = { navController.navigate("vagas") },
                onNavigateToMotoristas = { navController.navigate("motoristas") }
            )
        }

        // Tela de Vagas
        composable("vagas") {
            VagasScreen(
                controller = controller,
                onBack = { navController.popBackStack() }
            )
        }

        // Tela de Motoristas
        composable("motoristas") {
            MotoristaScreen(
                controller = controller,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
