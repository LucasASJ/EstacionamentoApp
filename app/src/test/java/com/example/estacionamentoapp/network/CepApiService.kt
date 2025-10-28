// network/CepApiService.kt

import com.example.seuapp.data.Endereco
import retrofit2.http.*

interface CepApiService {

    // GET: Busca um CEP
    // Exemplo de URL: https://viacep.com.br/ws/80010000/json/
    // O {cep} é um Path Parameter, seguido pelo formato de resposta /json/
    @GET("{cep}/json/")
    suspend fun buscarEnderecoPorCep(@Path("cep") cep: String): Endereco
}