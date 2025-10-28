// Conteúdo de EstacionamentoController.kt (corrigido)
package com.example.estacionamentoapp

import com.example.estacionamentoapp.data.Vaga
import com.example.estacionamentoapp.data.RegistroEntrada // <-- ADICIONE ESTA LINHA
import com.example.estacionamentoapp.data.RegistroSaida
import com.example.estacionamentoapp.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EstacionamentoController {

    // Função para obter a lista de vagas (GET /vagas)
    suspend fun fetchVagas(): List<Vaga> {
        return withContext(Dispatchers.IO) {
            // Acessa o endpoint /vagas para obter a lista completa
            RetrofitInstance.api.getVagas()
        }
    }

    // Funções para Entrada e Saída
    suspend fun registrarEntrada(veiculoId: Int, vagaId: Int): RegistroSaida {
        val entradaRequest = RegistroEntrada(veiculoId, vagaId)
        return withContext(Dispatchers.IO) { RetrofitInstance.api.registrarEntrada(entradaRequest) }
    }

    suspend fun registrarSaida(idRegistro: Int): RegistroSaida {
        return withContext(Dispatchers.IO) { RetrofitInstance.api.registrarSaida(idRegistro) }
    }
}
