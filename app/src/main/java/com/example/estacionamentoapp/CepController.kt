// CepController.kt
package com.example.estacionamentoapp

import com.example.estacionamentoapp.network.RetrofitInstance
import com.example.estacionamentoapp.data.Endereco
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Camada de Lógica/Controller (Inspirado no FeriadoController - Aula 19)
class CepController {

    // Função suspend (assíncrona) que chama o serviço
    suspend fun buscarCep(cep: String): Endereco {
        // Usa Dispatchers.IO para a operação de rede (Aula 19)
        return withContext(Dispatchers.IO) {
            RetrofitInstance.api.buscarEnderecoPorCep(cep)
        }
    }
}