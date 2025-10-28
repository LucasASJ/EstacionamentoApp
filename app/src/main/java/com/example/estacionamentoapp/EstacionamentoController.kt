package com.example.estacionamentoapp

import com.example.estacionamentoapp.data.Motorista // NOVO: Importar Motorista
import com.example.estacionamentoapp.data.Vaga
import com.example.estacionamentoapp.data.RegistroEntrada
import com.example.estacionamentoapp.data.RegistroSaida
import com.example.estacionamentoapp.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EstacionamentoController {

    // --- Operações de Estacionamento ---

    suspend fun fetchVagas(): List<Vaga> {
        return withContext(Dispatchers.IO) {
            RetrofitInstance.api.getVagas()
        }
    }

    suspend fun registrarEntrada(veiculoId: Int, vagaId: Int): RegistroSaida {
        val entradaRequest = RegistroEntrada(veiculoId, vagaId)
        return withContext(Dispatchers.IO) { RetrofitInstance.api.registrarEntrada(entradaRequest) }
    }

    suspend fun registrarSaida(idRegistro: Int): RegistroSaida {
        return withContext(Dispatchers.IO) { RetrofitInstance.api.registrarSaida(idRegistro) }
    }

    // --- Operações de CRUD de Motorista (NOVO) ---

    suspend fun fetchMotoristas(): List<Motorista> {
        return withContext(Dispatchers.IO) {
            RetrofitInstance.api.getMotoristas()
        }
    }

    suspend fun createMotorista(motorista: Motorista): Motorista {
        return withContext(Dispatchers.IO) {
            RetrofitInstance.api.createMotorista(motorista)
        }
    }

    suspend fun updateMotorista(motorista: Motorista) {
        // Garantir que o ID não seja nulo antes de tentar atualizar
        val id = motorista.id ?: throw IllegalArgumentException("Motorista ID é necessário para atualização.")
        return withContext(Dispatchers.IO) {
            RetrofitInstance.api.updateMotorista(id, motorista)
        }
    }

    suspend fun deleteMotorista(id: Int) {
        return withContext(Dispatchers.IO) {
            RetrofitInstance.api.deleteMotorista(id)
        }
    }
}
