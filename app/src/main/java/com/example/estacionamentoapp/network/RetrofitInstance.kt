// network/RetrofitInstance.kt
package com.example.estacionamentoapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// URL Base da API ViaCEP
private const val BASE_URL_CEP = "https://viacep.com.br/ws/"

// Objeto Singleton para a instância do Retrofit (Aula 19)
object RetrofitInstance {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_CEP)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Cria e expõe a interface de serviço da API
    val api: CepApiService by lazy {
        retrofit.create(CepApiService::class.java)
    }
}