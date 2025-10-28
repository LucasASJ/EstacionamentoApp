// network/CepApiService.kt
package com.example.estacionamentoapp.network

import com.example.estacionamentoapp.data.Endereco
import retrofit2.http.*

// Interface de Contrato da API (Aula 19, 20)
interface CepApiService {

    // GET com Path Parameter para o CEP (Aula 20)
    @GET("{cep}/json/")
    suspend fun buscarEnderecoPorCep(@Path("cep") cep: String): Endereco
}