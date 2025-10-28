package com.example.estacionamentoapp.network

import com.example.estacionamentoapp.data.RegistroEntrada
import com.example.estacionamentoapp.data.RegistroSaida
import com.example.estacionamentoapp.data.Vaga
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT // Importe PUT
import retrofit2.http.Path

interface EstacionamentoApiService {

    @GET("vagas") // Deve ser exatamente "vagas"
    suspend fun getVagas(): List<Vaga>

    /**
     * CORREÇÃO DE ENTRADA: Usa POST para /registros
     * (Igual ao app.MapPost("/registros", ...) do C#)
     */
    @POST("registros") // Rota corrigida
    suspend fun registrarEntrada(@Body entrada: RegistroEntrada): RegistroSaida

    /**
     * CORREÇÃO DE SAÍDA: Usa PUT para /registros/{id}/saida
     * (Igual ao app.MapPut("/registros/{id}/saida", ...) do C#)
     */
    @PUT("registros/{id}/saida") // Rota e método corrigidos
    suspend fun registrarSaida(@Path("id") idRegistro: Int): RegistroSaida

}