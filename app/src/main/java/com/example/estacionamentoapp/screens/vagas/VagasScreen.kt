package com.example.estacionamentoapp.screens.vagas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.estacionamentoapp.EstacionamentoController
import com.example.estacionamentoapp.data.Vaga
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VagasScreen(
    controller: EstacionamentoController,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()

    var vagas by remember { mutableStateOf(emptyList<Vaga>()) }
    var loading by remember { mutableStateOf(true) }

    fun loadData() {
        scope.launch {
            loading = true
            vagas = controller.fetchVagas()
            loading = false
        }
    }

    LaunchedEffect(Unit) { loadData() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vagas") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier.padding(padding).fillMaxSize()
        ) {

            if (loading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                return@Column
            }

            LazyColumn {
                items(vagas) { vaga ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Column(modifier = Modifier.weight(1f)) {
                                Text("Vaga: ${vaga.numero}")
                                Text("Status: ${if (vaga.ocupada) "Ocupada" else "Livre"}")
                            }

                            if (!vaga.ocupada) {
                                IconButton(onClick = {
                                    scope.launch {
                                        controller.ocuparVaga(vaga.numero)
                                        loadData()
                                    }
                                }) {
                                    Icon(
                                        Icons.Filled.Check,
                                        contentDescription = "Ocupar Vaga"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
