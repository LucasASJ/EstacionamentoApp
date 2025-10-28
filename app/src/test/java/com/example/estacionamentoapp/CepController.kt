package com.example.estacionamentoapp

// CepController.kt

import com.example.estacionamentoapp.network.RetrofitInstance // Importa o nosso Singleton de API
import com.example.estacionamentoapp.data.Endereco // Importa o nosso modelo de dados
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CepController {

    // Esta função é 'suspend' porque faz uma chamada assíncrona (I/O)
    // e usa o CEP como Path Parameter (Aula 20).
    suspend fun buscarCep(cep: String): Endereco {
        // Usa withContext(Dispatchers.IO) para garantir que a operação de rede
        // ocorra em uma thread de I/O, evitando bloquear a UI (Aula 19).
        return withContext(Dispatchers.IO) {
            // Chama o endpoint do serviço de CEP através do Retrofit
            RetrofitInstance.api.buscarEnderecoPorCep(cep)
        }
    }
}