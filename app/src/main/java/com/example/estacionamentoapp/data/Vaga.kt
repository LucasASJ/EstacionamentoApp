package com.example.estacionamentoapp.data

/**
 * Representa uma vaga de estacionamento.
 * Os campos devem corresponder à resposta JSON da sua API.
 */
data class Vaga(
    val id: Int,
    val numero: String,
    val ocupada: Boolean,
    val tipo: String // Ex: "carro", "moto"
)
