// A função Compose CepScreen agora recebe a Activity
@Composable
fun CepScreen(activity: MainActivity) {
    // 1. Gerenciamento de Estado
    // Usamos os componentes da Activity
    val controller = remember { activity.controller }

    var cepInput by remember { mutableStateOf("") }
    var endereco by remember { mutableStateOf<Endereco?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Consulta de Endereço (ViaCEP)", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            // ... (OutlinedTextField, Spacer) ...

            // Botão de Busca
            Button(
                onClick = {
                    isLoading = true
                    endereco = null
                    error = null

                    if (cepInput.length == 8) {
                        // CHAMA A FUNÇÃO CORRETA NA ACTIVITY!
                        activity.iniciarBusca(
                            cep = cepInput,
                            onResultado = { result ->
                                endereco = result
                                isLoading = false
                            },
                            onError = { errorMessage ->
                                error = errorMessage
                                isLoading = false
                            }
                        )
                    } else {
                        error = "CEP deve ter 8 dígitos."
                        isLoading = false
                    }
                },
                enabled = !isLoading && cepInput.length == 8
            ) {
                Text(if (isLoading) "Buscando..." else "Buscar")
            }
        }
        // ... (Exibição dos Resultados: if/else) ...
    }
}