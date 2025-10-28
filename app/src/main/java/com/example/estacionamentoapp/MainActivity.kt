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
import com.example.estacionamentoapp.data.Vaga // Usaremos a classe Vaga
import com.example.estacionamentoapp.data.RegistroSaida // Para resposta da operação
import androidx.compose.material3.ExperimentalMaterial3Api // Para usar TopAppBar
import kotlinx.coroutines.launch // <--- IMPORTAÇÃO CRUCIAL

// Um enum para gerenciar as telas dentro da navegação principal
// ATENÇÃO: ESTE ENUM DEVE ESTAR PRESENTE APENAS UMA VEZ NO PROJETO (AQUI).
enum class Screen {
    VAGAS, MOTORISTAS, VEICULOS // Os três estados de tela
}

// A CLASSE PRINCIPAL (ACTIVITY)
class MainActivity : ComponentActivity() {

    // 1. Instanciar o Controller (Lógica de Negócios)
    private val controller = EstacionamentoController()

    // O PONTO DE ENTRADA DA ACTIVITY
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                // Chama o conteúdo principal que gerencia a navegação
                MainContent(activity = this)
            }
        }
    }

    // Expõe o Controller para que o Composable possa usá-lo
    val estController: EstacionamentoController get() = controller
}

// ------------------------------------------------------------------
// COMPONENTE PRINCIPAL (GERENCIADOR DE TELAS)
// ------------------------------------------------------------------
@Composable
fun MainContent(activity: MainActivity) {
    val controller = remember { activity.estController }
    var currentScreen by remember { mutableStateOf(Screen.VAGAS) }

    // Função para navegar entre as telas
    val onNavigateTo: (Screen) -> Unit = { screen ->
        currentScreen = screen
    }

    when (currentScreen) {
        Screen.VAGAS -> VagasScreen(activity, onNavigateTo)
        Screen.MOTORISTAS -> MotoristaScreen(controller, onNavigateTo)
        Screen.VEICULOS -> VeiculoScreen(controller, onNavigateTo) // Tela de Veículos
    }
}


// ------------------------------------------------------------------
// VAGAS SCREEN (TELA ORIGINAL)
// ------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VagasScreen(activity: MainActivity, onNavigateTo: (Screen) -> Unit) {

    val controller = remember { activity.estController }
    val scope = rememberCoroutineScope()

    // Estados para gerenciar a lista de vagas, o carregamento e o erro
    var vagas by remember { mutableStateOf(emptyList<Vaga>()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // Chave para forçar o recarregamento dos dados
    var refreshKey by remember { mutableStateOf(0) }

    // Estado para exibir o Toast de sucesso
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
                // Mensagem de erro atualizada para refletir a porta 5237 (sem /api/)
                error = "Erro de API: ${e.message}. Verifique sua API C# e o endereço http://10.0.2.2:5237/"
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

    // Layout Principal usando Scaffold para a barra superior
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Status do Estacionamento") },
                actions = {
                    Button(onClick = { onNavigateTo(Screen.MOTORISTAS) }) {
                        Text("Motoristas") // Botão para navegar para o CRUD
                    }
                    Spacer(modifier = Modifier.width(8.dp)) // Espaçamento
                    Button(onClick = { onNavigateTo(Screen.VEICULOS) }) {
                        Text("Veículos") // NOVO: Botão para Veículos
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .fillMaxSize()
        ) {
            // Exibição dos Resultados (Estruturas de Desvio)
            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator() // Indicador de carregamento
                }
            } else if (error != null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(error ?: "Erro desconhecido", color = Color.Red, modifier = Modifier.padding(16.dp))
                }
            } else {
                // LazyColumn para exibir a lista eficientemente
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(vagas) { vaga ->
                        VagaItem( // VagaItem agora é importado/definido externamente
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
}
// NOTA: A função VagaItem foi movida para o arquivo VagaItem.kt.
