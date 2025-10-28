package com.example.estacionamentoapp.network

import com.example.estacionamentoapp.data.Motorista // NOVO: Importar Motorista
import com.example.estacionamentoapp.data.RegistroEntrada
import com.example.estacionamentoapp.data.RegistroSaida
import com.example.estacionamentoapp.data.Vaga
import retrofit2.http.* // Importar tudo para garantir que o DELETE entre

interface EstacionamentoApiService {

    // --- CRUD de Vagas (Leitura principal) ---
    @GET("vagas")
    suspend fun getVagas(): List<Vaga>

    // --- CRUD de Registros (Entrada/Saída) ---
    @POST("registros") // C: Cria um novo registro (Entrada)
    suspend fun registrarEntrada(@Body entrada: RegistroEntrada): RegistroSaida

    @PUT("registros/{id}/saida") // U: Atualiza o registro (Saída)
    suspend fun registrarSaida(@Path("id") idRegistro: Int): RegistroSaida

    // --- CRUD de Motoristas (Requisito) ---

    @GET("motoristas") // R: Leitura de todos os Motoristas
    suspend fun getMotoristas(): List<Motorista>

    @POST("motoristas") // C: Cria um novo Motorista
    suspend fun createMotorista(@Body motorista: Motorista): Motorista

    @PUT("motoristas/{id}") // U: Atualiza um Motorista existente
    suspend fun updateMotorista(@Path("id") id: Int, @Body motorista: Motorista): Unit // Unit para status 204 No Content

    @DELETE("motoristas/{id}") // D: Deleta um Motorista
    suspend fun deleteMotorista(@Path("id") id: Int): Unit // Unit para status 204 No Content
}
