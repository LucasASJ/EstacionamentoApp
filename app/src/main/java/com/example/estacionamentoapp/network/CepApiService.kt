// CepApiService.kt
package com.example.estacionamentoapp.network

import com.example.estacionamentoapp.data.Endereco // <--- CORRIGIDO

import retrofit2.http.*

interface CepApiService {
    @GET("{cep}/json/")
    suspend fun buscarEnderecoPorCep(@Path("cep") cep: String): Endereco
}