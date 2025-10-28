package com.example.estacionamentoapp.network

import com.example.estacionamentoapp.data.RegistroEntrada
import com.example.estacionamentoapp.data.RegistroSaida
import com.example.estacionamentoapp.data.Vaga
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface EstacionamentoApiService {

    /**
     * Busca a lista completa de vagas da API.
     * Esta é a função que estava faltando.
     */
    @GET("vagas") // O endpoint da sua API para obter as vagas
    suspend fun getVagas(): List<Vaga>

    /**
     * Registra a entrada de um veículo.
     */
    @POST("entradas") // Endpoint de exemplo para registrar entrada
    suspend fun registrarEntrada(@Body entrada: RegistroEntrada): RegistroSaida

    /**
     * Registra a saída de um veículo.
     * O {id} é substituído pelo valor do parâmetro idRegistro.
     */
    @POST("saidas/{id}") // Endpoint de exemplo para registrar saída
    suspend fun registrarSaida(@Path("id") idRegistro: Int): RegistroSaida

}
