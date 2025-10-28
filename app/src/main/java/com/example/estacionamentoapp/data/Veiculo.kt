package com.example.estacionamentoapp.data

/**
 * Representa um veículo (Requisito de CRUD).
 * O campo 'id' deve ser nulo ao criar um novo veiculo (POST).
 */
data class Veiculo(
    val id: Int? = null,
    val placa: String,
    val modelo: String,
    val motoristaId: Int // Chave estrangeira para o motorista
)
