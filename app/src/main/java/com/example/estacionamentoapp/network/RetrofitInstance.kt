// network/RetrofitInstance.kt
package com.example.estacionamentoapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// URL Base da sua API de Estacionamento
// ATENÇÃO: Substitua "10.0.2.2" pelo IP do seu computador ou o endereço do seu servidor.
// O IP "10.0.2.2" é um endereço especial que o emulador Android usa para
// se conectar ao 'localhost' (127.0.0.1) da máquina onde ele está rodando.

// CORRIGIDO: Removido o prefixo "/api/" para evitar a duplicação de rota (BASE_URL + ENDPOINT)
private const val BASE_URL_ESTACIONAMENTO = "http://10.0.2.2:5237/"

// Objeto Singleton para a instância do Retrofit
object RetrofitInstance {

    // Configuração do Retrofit para a API de Estacionamento
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_ESTACIONAMENTO) // Usa a URL da sua API
            .addConverterFactory(GsonConverterFactory.create()) // Converte JSON em objetos Kotlin
            .build()
    }

    // Cria e expõe a interface de serviço da API de Estacionamento
    // A propriedade 'api' agora é do tipo EstacionamentoApiService
    val api: EstacionamentoApiService by lazy {
        retrofit.create(EstacionamentoApiService::class.java)
    }
}
