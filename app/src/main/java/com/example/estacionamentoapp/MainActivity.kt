// MainActivity.kt (CÓDIGO FINAL DE ESTACIONAMENTO)
package com.example.estacionamentoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

// Importe suas classes de dados (Data Class)
import com.example.estacionamentoapp.data.Vaga // Usaremos a classe Vaga

// A CLASSE PRINCIPAL (ACTIVITY)
class MainActivity : ComponentActivity() {

    // 1. Instanciar o Controller (Lógica de Negócios)
    private val controller = EstacionamentoController()

    // O PONTO DE ENTRADA DA ACTIVITY
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                // Chama a tela de visualização das Vagas
                VagasScreen(activity = this)
            }
        }
    }

    // Expõe o Controller para que o Composable possa usá-lo
    val estController: EstacionamentoController get() = controller
}


// A FUNÇÃO COMPOSABLE QUE DEFINE A INTERFACE
@Composable
fun VagasScreen(activity: MainActivity) {

    // Acessa o Controller a partir da Activity (melhoria de arquitetura)
    val controller = remember { activity.estController }

    // Estados para gerenciar a lista de vagas e o carregamento
    var vagas by remember { mutableStateOf(emptyList<Vaga>()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // Lógica de Carregamento da API (Simulação do ciclo de vida)
    LaunchedEffect(Unit) {
        try {
            // Chama a API de forma assíncrona (GET /vagas)
            vagas = controller.fetchVagas()
            isLoading = false
        } catch (e: Exception) {
            error = "Erro de API: ${e.message}. Verifique sua API C# e o endereço 10.0.2.2:5000."
            isLoading = false
        }
    }

    // Layout Principal
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Status do Estacionamento",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            modifier = Modifier.padding(16.dp)
        )

        // Exibição dos Resultados (Estruturas de Desvio)
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator() // Indicador de carregamento
            }
        } else if (error != null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(error ?: "Erro desconhecido", color = Color.Red)
            }
        } else {
            // LazyColumn para exibir a lista eficientemente (Aula 07)
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(vagas) { vaga ->
                    VagaItem(vaga = vaga, controller = controller) // Renderiza um Card para cada vaga
                }
            }
        }
    }
}

// Componente Composable para exibir uma única vaga
@Composable
fun VagaItem(vaga: Vaga, controller: EstacionamentoController) {
    // Define a aparência com base no estado
    val status = if (vaga.ocupada) "OCUPADA" else "LIVRE"
    val corFundo = if (vaga.ocupada) Color(0xFFFFCCCC) else Color(0xFFCCFFCC) // Vermelho claro vs Verde claro
    val corStatus = if (vaga.ocupada) Color.Red else Color.Green.copy(alpha = 0.6f) // Ou ajuste o alpha para escurecer


    // Card para agrupar o conteúdo (Aula 07)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        // Row para alinhar horizontalmente (Aula 06)
        Row(
            modifier = Modifier
                .background(corFundo)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Coluna de Informações
            Column(Modifier.weight(1f)) {
                Text(text = "Vaga: ${vaga.numero}", fontWeight = FontWeight.Bold) // LINHA CORRIGIDA
                Text(text = "Status: $status", color = corStatus)
                // TODO: Adicionar placa do veículo se estiver ocupada
            }

            // Botão de Ação
            Button(
                onClick = {
                    // Implementar a lógica de navegação/ação aqui
                    if (vaga.ocupada) {
                        // Ação de Saída: PUT /registros/{id}/saida
                        // Ação de Saída: PUT /registros/{id}/saida
// CORREÇÃO: Use vaga.numero em vez de vaga.nome
                        println("PUT Saída para Vaga ${vaga.numero}")
                    } else {
// Ação de Entrada: POST /registros
// CORREÇÃO: Use vaga.numero em vez de vaga.nome
                        println("POST Entrada para Vaga ${vaga.numero}")
                    }

                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (vaga.ocupada) Color(0xFF990000) else Color(0xFF006400)
                )
            ) {
                Text(if (vaga.ocupada) "Saída" else "Entrada")
            }
        }
    }
}