package com.example.estacionamentoapp.screens.motoristas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.estacionamentoapp.EstacionamentoController
import com.example.estacionamentoapp.data.Motorista
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MotoristaScreen(
    controller: EstacionamentoController,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()

    var motoristas by remember { mutableStateOf(emptyList<Motorista>()) }
    var loading by remember { mutableStateOf(true) }
    var dialogOpen by remember { mutableStateOf(false) }
    var motoristaSelecionado by remember { mutableStateOf<Motorista?>(null) }

    fun loadData() {
        scope.launch {
            loading = true
            motoristas = controller.fetchMotoristas()
            loading = false
        }
    }

    LaunchedEffect(Unit) { loadData() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Motoristas") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                motoristaSelecionado = null
                dialogOpen = true
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Adicionar Motorista")
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier.padding(padding).fillMaxSize()
        ) {

            if (loading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
                return@Column
            }

            LazyColumn {
                items(motoristas) { m ->

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
                                Text("ID: ${m.id ?: "-"}")
                                Text("Nome: ${m.nome}")
                                Text("Telefone: ${m.telefone}")
                            }

                            IconButton(onClick = {
                                motoristaSelecionado = m
                                dialogOpen = true
                            }) {
                                Icon(Icons.Filled.Edit, contentDescription = "Editar")
                            }

                            IconButton(onClick = {
                                scope.launch {
                                    m.id?.let { controller.deleteMotorista(it) }
                                    loadData()
                                }
                            }) {
                                Icon(
                                    Icons.Filled.Delete,
                                    contentDescription = "Excluir",
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (dialogOpen) {
        MotoristaFormDialog(
            motoristaSelecionado,
            controller,
            onDismiss = { dialogOpen = false },
            onSave = {
                loadData()
                dialogOpen = false
            }
        )
    }
}

@Composable
fun MotoristaFormDialog(
    motorista: Motorista?,
    controller: EstacionamentoController,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    var nome by remember { mutableStateOf(motorista?.nome ?: "") }
    var telefone by remember { mutableStateOf(motorista?.telefone ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (motorista == null) "Novo Motorista" else "Editar Motorista") },
        text = {
            Column {
                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = telefone,
                    onValueChange = { telefone = it },
                    label = { Text("Telefone") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val novo = Motorista(
                    id = motorista?.id,
                    nome = nome,
                    telefone = telefone
                )

                if (motorista == null) controller.createMotorista(novo)
                else controller.updateMotorista(novo)

                onSave()
            }) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
