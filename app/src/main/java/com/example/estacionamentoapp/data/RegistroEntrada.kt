package com.example.estacionamentoapp.data

/**
 * Representa o corpo da requisição (request body) para registrar a entrada de um veículo.
 * Os nomes dos campos devem corresponder ao que a sua API espera receber.
 */
data class RegistroEntrada(
    val veiculoId: Int,
    val vagaId: Int
)
