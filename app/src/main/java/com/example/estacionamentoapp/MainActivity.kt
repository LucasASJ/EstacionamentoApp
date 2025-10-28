// com.example.estacionamentoapp/MainActivity.kt

package com.example.estacionamentoapp

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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.estacionamentoapp.data.Endereco
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    val controller = CepController()

    fun iniciarBusca(cep: String, onResultado: (Endereco?) -> Unit, onError: (String) -> Unit) {
        lifecycleScope.launch {
            try {
                val endereco = controller.buscarCep(cep)
                onResultado(endereco)
            } catch (e: Exception) {
                onError("CEP não encontrado ou erro de rede. Erro: ${e.message}")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                CepScreen(activity = this)
            }
        }
    }
}


@Composable
fun CepScreen(activity: MainActivity) {

    var cepInput by remember { mutableStateOf("") }
    var endereco by remember { mutableStateOf<Endereco?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Consulta de Endereço (ViaCEP)", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = cepInput,
                onValueChange = { cepInput = it.filter { char -> char.isDigit() }.take(8) },
                label = { Text("Digite o CEP (apenas números)") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),

                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))

            Button(
                onClick = {
                    isLoading = true
                    endereco = null
                    error = null

                    if (cepInput.length == 8) {
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
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Endereço Encontrado:", fontWeight = FontWeight.SemiBold)
                    HorizontalDivider()
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
