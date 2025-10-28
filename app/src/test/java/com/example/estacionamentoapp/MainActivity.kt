// com.example.estacionamentoapp/MainActivity.kt

package com.example.estacionamentoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
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
import com.example.estacionamentoapp.data.Endereco

// A CLASSE PRINCIPAL (ACTIVITY)
class MainActivity : ComponentActivity() {

    // 1. Instanciar o Controller (Lógica de Negócios)
    private val controller = CepController()

    /**
     * Função que inicia a chamada assíncrona da API usando o escopo da Activity.
     * Recebe o CEP e duas funções lambda (onResultado e onError) para atualizar a UI.
     */
    fun iniciarBusca(cep: String, onResultado: (Endereco?) -> Unit, onError: (String) -> Unit) {
        // Usa o lifecycleScope (o escopo de vida da Activity) para lançar a coroutine (Aula 19)
        lifecycleScope.launch {
            try {
                val endereco = controller.buscarCep(cep)
                onResultado(endereco)
            } catch (e: Exception) {
                // Em caso de erro, chama a função onError com a mensagem de erro
                onError("CEP não encontrado ou erro de rede. Erro: ${e.message}")
            }
        }
    }

    // O PONTO DE ENTRADA DA ACTIVITY
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Define o conteúdo da tela principal (setContent - Aula 05)
        setContent {
            MaterialTheme {
                // Chama a tela Compose, passando a Activity (THIS) para que
                // a tela possa iniciar a coroutine de busca (iniciarBusca)
                CepScreen(activity = this)
            }
        }
    }
}


// A FUNÇÃO COMPOSABLE QUE CONSTRÓI A UI (FRONTEND)
@Composable
fun CepScreen(activity: MainActivity) {
    // Acessa o Controller a partir da Activity para fazer a busca
    val controller = remember { activity.controller }

    // Estados para gerenciar a UI (mudanças de estado re-renderizam a tela)
    var cepInput by remember { mutableStateOf("") }
    var endereco by remember { mutableStateOf<Endereco?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    // Layout Coluna (organiza elementos verticalmente - Aula 06)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Consulta de Endereço (ViaCEP)", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(Modifier.height(16.dp))

        // Layout Linha (organiza elementos horizontalmente - Aula 06)
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = cepInput,
                onValueChange = { cepInput = it.filter { char -> char.isDigit() }.take(8) }, // Limita a 8 dígitos numéricos
                label = { Text("Digite o CEP (apenas números)") },
                keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                ),
                modifier = Modifier.weight(1f) // Faz o campo ocupar o espaço restante na linha
            )
            Spacer(Modifier.width(8.dp))

            // Botão de Busca
            Button(
                // Lógica executada ao clicar no botão
                onClick = {
                    isLoading = true
                    endereco = null
                    error = null

                    if (cepInput.length == 8) {
                        // Chama a função da Activity para iniciar a busca assíncrona
                        activity.iniciarBusca(
                            cep = cepInput,
                            onResultado = { result ->
                                endereco = result
                                isLoading = false
                            },
                            onError = { errorMessage ->
                                error = errorMessage
                                isLoading = false
                            }
                        )
                    } else {
                        error = "CEP deve ter 8 dígitos."
                        isLoading = false
                    }
                },
                // Habilita o botão apenas se não estiver carregando E o CEP tiver 8 dígitos
                enabled = !isLoading && cepInput.length == 8
            ) {
                Text(if (isLoading) "Buscando..." else "Buscar")
            }
        }

        Spacer(Modifier.height(24.dp))

        // Exibição dos Resultados (usa estruturas de desvio implícitas do Kotlin)
        if (isLoading) {
            Text("Carregando...")
        } else if (error != null) {
            Text("ERRO: $error", color = Color.Red)
        } else if (endereco != null) {
            // Card (Container com bordas e sombra - Aula 07)
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Endereço Encontrado:", fontWeight = FontWeight.SemiBold)
                    Divider()
                    Spacer(Modifier.height(8.dp))
                    Text("CEP: ${endereco!!.cep}")
                    Text("Logradouro: ${endereco!!.logradouro}")
                    Text("Bairro: ${endereco!!.bairro}")
                    Text("Cidade/UF: ${endereco!!.localidade} - ${endereco!!.uf}")
                }
            }
        } else {
            Text("Aguardando busca de CEP.")
        }
    }
}