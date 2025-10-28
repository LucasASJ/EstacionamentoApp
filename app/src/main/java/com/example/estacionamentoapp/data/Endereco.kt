// data/Endereco.kt
package com.example.estacionamentoapp.data

// Data class: ideal para armazenar dados (Aula 04 - POO)
data class Endereco(
    val cep: String,
    val logradouro: String,
    val complemento: String,
    val bairro: String,
    val localidade: String, // Cidade
    val uf: String,         // Estado
    val ibge: String,
    val gia: String
)