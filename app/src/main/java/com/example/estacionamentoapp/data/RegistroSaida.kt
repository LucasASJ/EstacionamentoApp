package com.example.estacionamentoapp.data

/**
 * Representa a resposta da API (response body) ao registrar
 * uma entrada ou uma saída de veículo.
 * Os campos devem corresponder exatamente ao JSON retornado pela API.
 */
data class RegistroSaida(
    val id: Int,
    val veiculoId: Int,
    val vagaId: Int,
    val horaEntrada: String, // Usar String para datas/horas no formato ISO 8601 é comum
    val horaSaida: String?,  // '?' indica que o campo pode ser nulo (null)
    val valor: Double?      // '?' indica que o campo pode ser nulo (null)
)
