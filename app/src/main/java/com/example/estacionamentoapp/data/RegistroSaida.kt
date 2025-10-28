data class RegistroSaida(
    val id: Int,
    val veiculoId: Int,
    val vagaId: Int,
    val horaEntrada: String?,
    val horaSaida: String?,
    val valor: Double?
)