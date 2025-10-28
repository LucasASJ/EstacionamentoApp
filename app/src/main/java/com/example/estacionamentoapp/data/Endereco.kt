// Endereco.kt
package com.example.estacionamentoapp.data // <--- ESTA LINHA É ESSENCIAL!

data class Endereco(
    val cep: String,
    val logradouro: String,
    val complemento: String,
    val bairro: String,
    val localidade: String,
    val uf: String,
    val ibge: String,
    val gia: String
)