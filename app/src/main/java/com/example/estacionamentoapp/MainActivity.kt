// MainActivity.kt (CÓDIGO FINAL COM CORREÇÕES E TELA CEP)
package com.example.estacionamentoapp

import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope // Permite o uso do lifecycleScope
import kotlinx.coroutines.launch

// Importa as classes que foram movidas/criadas
import com.example.estacionamentoapp.data.Endereco
import com.example.estacionamentoapp.network.RetrofitInstance

// A CLASSE PRINCIPAL (ACTIVITY) - Ponto de Entrada
class MainActivity : ComponentActivity() {

    // 1. Instanciar o Controller (Lógica de Negócios)
    val controller = CepController()

    /**
     * Função que inicia a chamada assíncrona da API usando o escopo da Activity (Aula 19).
     */
    fun iniciarBusca(cep: String, onResultado: (Endereco?) -> Unit, onError: (String) -> Unit) {
        // Usa o lifecycleScope (o escopo de vida da Activity) para lançar a coroutine
        lifecycleScope.launch {
            try {
                val endereco = controller.buscarCep(cep)
                onResultado(endereco)
            } catch (e: Exception) {
                onError("CEP não encontrado ou erro de rede. Erro: ${e.message}")
            }
        }
    }

    // O PONTO DE ENTRADA DA ACTIVITY
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Define o conteúdo da tela principal (setContent - Aula 05)
        setContent {
            // Chama a tela Compose, passando 'this' (a Activity) como referência
            MaterialTheme {
                CepScreen(activity = this)
            }
        }
    }
}


// A FUNÇÃO COMPOSABLE QUE CONSTRÓI A UI (FRONTEND)
@Composable
fun CepScreen(activity: MainActivity) {

    // Acessa o Controller a partir da Activity (Aula 04 - Injeção de dependência simples)
    val controller = remember { activity.controller }

    // Estados para gerenciar a UI (mutablesStateOf)
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
                onValueChange = { cepInput = it.filter { char -> char.isDigit() }.take(8) },
                label = { Text("Digite o CEP (apenas números)") },
                keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                ),
                modifier = Modifier.weight(1f) // Modificador para ocupar espaço (Aula 06)
            )
            Spacer(Modifier.width(8.dp))

            // Botão de Busca
            Button(
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
                enabled = !isLoading && cepInput.length == 8
            ) {
                Text(if (isLoading) "Buscando..." else "Buscar")
            }
        }

        Spacer(Modifier.height(24.dp))

        // Exibição dos Resultados
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

@Composable
fun KeyboardOptions(keyboardType: KeyboardType) {
    TODO("Not yet implemented")
}