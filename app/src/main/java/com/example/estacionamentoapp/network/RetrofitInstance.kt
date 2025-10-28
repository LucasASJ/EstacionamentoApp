// network/RetrofitInstance.kt

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Nova URL Base para a API ViaCEP
private const val BASE_URL_CEP = "https://viacep.com.br/ws/"

object RetrofitInstance {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_CEP) // Define a nova URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Agora o objeto 'api' retorna a interface de serviço de CEP
    val api: CepApiService by lazy { // Nome da interface foi alterado!
        retrofit.create(CepApiService::class.java)
    }
}