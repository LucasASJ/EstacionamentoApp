// MainActivity.kt (CÓDIGO FINAL DE ESTACIONAMENTO - CORRIGIDO)
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
import com.example.estacionamentoapp.data.RegistroSaida // Para resposta da operação

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

    val controller = remember { activity.estController }
    val scope = rememberCoroutineScope()

    // Estados para gerenciar a lista de vagas, o carregamento e o erro
    var vagas by remember { mutableStateOf(emptyList<Vaga>()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // NOVO: Chave para forçar o recarregamento dos dados
    var refreshKey by remember { mutableStateOf(0) }

    // NOVO: Estado para exibir o Toast de sucesso
    var successMessage by remember { mutableStateOf<String?>(null) }

    // Função para buscar os dados da API
    fun fetchData() {
        isLoading = true
        error = null
        scope.launch {
            try {
                // Chama a API de forma assíncrona (GET /vagas)
                vagas = controller.fetchVagas()
            } catch (e: Exception) {
                // Mensagem de erro atualizada para refletir a porta 5000
                error = "Erro de API: ${e.message}. Verifique sua API C# e o endereço http://10.0.2.2:5000/api/"
            } finally {
                isLoading = false
            }
        }
    }

    // Lógica de Carregamento da API (Roda na inicialização e quando 'refreshKey' muda)
    LaunchedEffect(refreshKey) {
        fetchData()
    }

    // Função de callback para recarregar a tela após uma ação
    val onRefresh: (String) -> Unit = { message ->
        successMessage = message
        refreshKey++ // Altera a chave para forçar o LaunchedEffect a rodar novamente
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
            // LazyColumn para exibir a lista eficientemente
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(vagas) { vaga ->
                    VagaItem(
                        vaga = vaga,
                        controller = controller,
                        onActionSuccess = onRefresh // Passa o callback de refresh
                    )
                }
            }
        }

        // Exibe uma mensagem de sucesso temporária
        successMessage?.let { message ->
            LaunchedEffect(message) {
                kotlinx.coroutines.delay(2000)
                successMessage = null
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Green.copy(alpha = 0.8f))
                    .padding(8.dp)
            ) {
                Text(text = message, color = Color.White, modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

// Componente Composable para exibir uma única vaga
@Composable
fun VagaItem(
    vaga: Vaga,
    controller: EstacionamentoController,
    onActionSuccess: (String) -> Unit
) {
    // Para simplificar, usaremos um veiculoId fixo para o registro de entrada
    // Em uma tela real, este valor seria coletado via input do usuário.
    val veiculoIdFixo = 1

    // Define a aparência com base no estado
    val status = if (vaga.ocupada) "OCUPADA" else "LIVRE"
    val corFundo = if (vaga.ocupada) Color(0xFFFFCCCC) else Color(0xFFCCFFCC)
    val corStatus = if (vaga.ocupada) Color.Red else Color.Green.copy(alpha = 0.6f)

    val scope = rememberCoroutineScope()

    // Card para agrupar o conteúdo
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        // Row para alinhar horizontalmente
        Row(
            modifier = Modifier
                .background(corFundo)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Coluna de Informações
            Column(Modifier.weight(1f)) {
                Text(text = "Vaga: ${vaga.numero}", fontWeight = FontWeight.Bold)
                Text(text = "Status: $status", color = corStatus)
                // NOVO: Adicionar ID do registro e do veículo se estiver ocupada
                if (vaga.ocupada) {
                    Text(text = "Registro ID: ${vaga.registroId ?: "N/A"}", fontSize = 12.sp)
                    Text(text = "Veículo ID: ${vaga.veiculoId ?: "N/A"}", fontSize = 12.sp)
                }
            }

            // Botão de Ação
            Button(
                onClick = {
                    scope.launch {
                        try {
                            if (vaga.ocupada) {
                                // Ação de Saída: Requer o registroId
                                val registroId = vaga.registroId
                                if (registroId != null) {
                                    val result: RegistroSaida = controller.registrarSaida(registroId)
                                    val valor = result.valor?.let { "R$ %.2f".format(it) } ?: "Gratuito"
                                    onActionSuccess("Saída registrada. Valor: $valor")
                                } else {
                                    // Adicionar tratamento de erro, pois o ID do registro está faltando
                                    onActionSuccess("Erro: Vaga ocupada, mas ID do registro está faltando.")
                                }

                            } else {
                                // Ação de Entrada: Requer veiculoId e o ID da vaga
                                val result: RegistroSaida = controller.registrarEntrada(veiculoIdFixo, vaga.id)
                                onActionSuccess("Entrada registrada! ID do Registro: ${result.id}")
                            }
                        } catch (e: Exception) {
                            onActionSuccess("Falha na operação: ${e.message}")
                        }
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