package com.example.estacionamentoapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.estacionamentoapp.data.RegistroSaida
import com.example.estacionamentoapp.data.Vaga
import kotlinx.coroutines.launch

// Componente Composable para exibir uma única vaga
@Composable
fun VagaItem(
    vaga: Vaga,
    controller: EstacionamentoController,
    onActionSuccess: (String) -> Unit
) {
    // Para simplificar, usaremos um veiculoId fixo para o registro de entrada
    // Usamos um ID de motorista/veículo existente no Seed Data
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
                // Adicionar ID do registro e do veículo se estiver ocupada
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
