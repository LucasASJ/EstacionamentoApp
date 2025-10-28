package com.example.estacionamentoapp.data

/**
 * Representa uma vaga de estacionamento.
 * Os campos devem corresponder à resposta JSON da sua API.
 * Adicionamos 'registroId' para permitir a operação de 'Saída'.
 */
data class Vaga(
    val id: Int,
    val numero: String,
    val ocupada: Boolean,
    val tipo: String,       // Ex: "carro", "moto"
    // NOVOS CAMPOS ADICIONADOS (devem ser nulos se a vaga estiver livre)
    val registroId: Int?,   // ID do registro de entrada, necessário para a Saída
    val veiculoId: Int?     // ID do veículo que está estacionado (para exibição)
)