package com.example.estacionamentoapp.data

/**
 * Representa um motorista (Requisito de CRUD).
 * O campo 'id' deve ser nulo ao criar um novo motorista (POST).
 */
data class Motorista(
    val id: Int? = null,
    val nome: String,
    val cpf: String
)
