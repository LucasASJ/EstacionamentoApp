package com.example.estacionamentoapp.sscreens.veiculos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.estacionamentoapp.EstacionamentoController
import com.example.estacionamentoapp.data.Veiculo
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VeiculoScreen(
    controller: EstacionamentoController,
    onNavigateToVagas: () -> Unit,
    onNavigateToMotoristas: () -> Unit
) {
    val scope = rememberCoroutineScope()

    var veiculos by remember { mutableStateOf(emptyList<Veiculo>()) }
    var loading by remember { mutableStateOf(true) }
    var dialogOpen by remember { mutableStateOf(false) }
    var veiculoSelecionado by remember { mutableStateOf<Veiculo?>(null) }

    fun loadData() {
        scope.launch {
            loading = true
            veiculos = controller.fetchVeiculos()
            loading = false
        }
    }

    LaunchedEffect(Unit) { loadData() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gerenciar Veículos") },
                actions = {
                    TextButton(onClick = onNavigateToVagas) {
                        Text("Vagas")
                    }
                    TextButton(onClick = onNavigateToMotoristas) {
                        Text("Motoristas")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    veiculoSelecionado = null
                    dialogOpen = true
                }
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Novo Veículo")
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier.padding(padding).fillMaxSize()
        ) {

            if (loading) {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator()
                }
                return@Column
            }

            LazyColumn {
                items(veiculos) { v ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                            .clickable {
                                veiculoSelecionado = v
                                dialogOpen = true
                            }
                    ) {

                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Column(modifier = Modifier.weight(1f)) {
                                Text("Placa: ${v.placa}", fontSize = 18.sp)
                                Text("Modelo: ${v.modelo}", fontSize = 14.sp, color = Color.Gray)
                                Text("Motorista ID: ${v.motoristaId}", fontSize = 12.sp, color = Color.LightGray)
                            }

                            IconButton(onClick = {
                                veiculoSelecionado = v
                                dialogOpen = true
                            }) {
                                Icon(Icons.Filled.Edit, contentDescription = "Editar")
                            }

                            IconButton(onClick = {
                                scope.launch {
                                    controller.deleteVeiculo(v.id!!)
                                    loadData()
                                }
                            }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Excluir", tint = Color.Red)
                            }
                        }
                    }
                }
            }
        }
    }

    if (dialogOpen) {
        VeiculoFormDialog(
            controller = controller,
            veiculoSelecionado,
            onClose = {
                dialogOpen = false
                loadData()
            }
        )
    }
}

@Composable
fun VeiculoFormDialog(
    controller: EstacionamentoController,
    veiculo: Veiculo?,
    onClose: () -> Unit
) {
    var placa by remember { mutableStateOf(veiculo?.placa ?: "") }
    var modelo by remember { mutableStateOf(veiculo?.modelo ?: "") }
    var motoristaId by remember { mutableStateOf(veiculo?.motoristaId?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onClose,
        title = {
            Text(if (veiculo == null) "Novo Veículo" else "Editar Veículo")
        },
        text = {
            Column {
                OutlinedTextField(placa, { placa = it }, label = { Text("Placa") })
                OutlinedTextField(modelo, { modelo = it }, label = { Text("Modelo") })
                OutlinedTextField(
                    motoristaId,
                    { motoristaId = it },
                    label = { Text("Motorista ID") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val novo = Veiculo(
                    id = veiculo?.id,
                    placa = placa,
                    modelo = modelo,
                    motoristaId = motoristaId.toIntOrNull() ?: 0
                )

                if (veiculo == null)
                    controller.createVeiculo(novo)
                else
                    controller.updateVeiculo(novo)

                onClose()
            }) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onClose) { Text("Cancelar") }
        }
    )
}
