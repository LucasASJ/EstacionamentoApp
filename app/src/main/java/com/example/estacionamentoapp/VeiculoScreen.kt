package com.example.estacionamentoapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.estacionamentoapp.data.Veiculo
import kotlinx.coroutines.launch

// Adiciona a anotação OptIn para permitir o uso de TopAppBar e Scaffold
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VeiculoScreen(controller: EstacionamentoController, onNavigateTo: (Screen) -> Unit) {

    val scope = rememberCoroutineScope()
    var veiculos by remember { mutableStateOf(emptyList<Veiculo>()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var refreshKey by remember { mutableIntStateOf(0) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedVeiculo by remember { mutableStateOf<Veiculo?>(null) } // Veiculo para edição ou null para criação

    // Função de recarregamento
    fun fetchData() {
        isLoading = true
        error = null
        scope.launch {
            try {
                veiculos = controller.fetchVeiculos()
            } catch (e: Exception) {
                error = "Erro ao carregar veículos: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    // Carrega dados na inicialização ou no refresh
    LaunchedEffect(refreshKey) {
        fetchData()
    }

    // Callback para fechar o modal e forçar o refresh
    val onActionSuccess: (String) -> Unit = { message ->
        showDialog = false
        selectedVeiculo = null
        refreshKey++ // Força o LaunchedEffect a buscar novos dados
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gerenciar Veículos (CRUD)") },
                actions = {
                    Button(onClick = { onNavigateTo(Screen.VAGAS) }) {
                        Text("Vagas")
                    }
                    Button(onClick = { onNavigateTo(Screen.MOTORISTAS) }) {
                        Text("Motoristas")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                selectedVeiculo = null // Null para criar um novo
                showDialog = true
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Adicionar Veículo")
            }
        }
    ) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .fillMaxSize()
        ) {
            // Exibição de Erro
            if (error != null) {
                Text(error ?: "Erro desconhecido", color = Color.Red, modifier = Modifier.padding(16.dp))
            }

            // Exibição de Loading
            if (isLoading && error == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                // Lista de Veículos
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(veiculos) { veiculo ->
                        VeiculoItem(
                            veiculo = veiculo,
                            onEdit = {
                                selectedVeiculo = veiculo
                                showDialog = true
                            },
                            onDelete = {
                                scope.launch {
                                    try {
                                        // Garante que o ID não seja nulo antes de deletar
                                        val idVeiculo = it.id ?: throw IllegalArgumentException("ID do veículo ausente.")
                                        controller.deleteVeiculo(idVeiculo)
                                        onActionSuccess("Veículo ${it.placa} excluído.")
                                    } catch (e: Exception) {
                                        onActionSuccess("Falha ao excluir: ${e.message}")
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // Modal de Criação/Edição (Uso do Dialog)
    if (showDialog) {
        VeiculoFormDialog(
            controller = controller,
            veiculoToEdit = selectedVeiculo,
            onDismiss = { showDialog = false },
            onSuccess = onActionSuccess
        )
    }
}

// Componente para exibir um único item Veículo
@Composable
fun VeiculoItem(veiculo: Veiculo, onEdit: (Veiculo) -> Unit, onDelete: (Veiculo) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onEdit(veiculo) }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(text = "Placa: ${veiculo.placa}", fontWeight = FontWeight.Bold)
                Text(text = "Modelo: ${veiculo.modelo}", fontSize = 12.sp, color = Color.Gray)
                Text(text = "Motorista ID: ${veiculo.motoristaId}", fontSize = 10.sp, color = Color.LightGray)
                Text(text = "ID: ${veiculo.id}", fontSize = 10.sp, color = Color.LightGray)
            }
            // Botão de Edição
            IconButton(onClick = { onEdit(veiculo) }) {
                Icon(Icons.Filled.Edit, contentDescription = "Editar")
            }
            // Botão de Exclusão
            IconButton(onClick = { onDelete(veiculo) }) {
                Icon(Icons.Filled.Delete, contentDescription = "Excluir", tint = Color.Red)
            }
        }
    }
}

// Dialog para o Formulário de Criação e Edição
@Composable
fun VeiculoFormDialog(
    controller: EstacionamentoController,
    veiculoToEdit: Veiculo?,
    onDismiss: () -> Unit,
    onSuccess: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    var placa by remember { mutableStateOf(veiculoToEdit?.placa ?: "") }
    var modelo by remember { mutableStateOf(veiculoToEdit?.modelo ?: "") }
    var motoristaIdText by remember { mutableStateOf(veiculoToEdit?.motoristaId?.toString() ?: "") }
    var isSaving by remember { mutableStateOf(false) }

    val isEditing = veiculoToEdit != null
    val title = if (isEditing) "Editar Veículo (ID: ${veiculoToEdit?.id})" else "Adicionar Novo Veículo"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Campo Placa
                OutlinedTextField(
                    value = placa,
                    onValueChange = { placa = it },
                    label = { Text("Placa") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Campo Modelo
                OutlinedTextField(
                    value = modelo,
                    onValueChange = { modelo = it },
                    label = { Text("Modelo") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Campo Motorista ID
                OutlinedTextField(
                    value = motoristaIdText,
                    onValueChange = { motoristaIdText = it },
                    label = { Text("ID do Motorista") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                if (isSaving) {
                    LinearProgressIndicator(Modifier.fillMaxWidth())
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val motoristaId = motoristaIdText.toIntOrNull()
                    if (motoristaId == null) {
                        onSuccess("Erro: ID do Motorista deve ser um número válido.")
                        return@Button
                    }

                    isSaving = true
                    scope.launch {
                        try {
                            val novoVeiculo = Veiculo(
                                id = veiculoToEdit?.id,
                                placa = placa,
                                modelo = modelo,
                                motoristaId = motoristaId
                            )
                            if (isEditing) {
                                // Update
                                controller.updateVeiculo(novoVeiculo)
                                onSuccess("Veículo ${novoVeiculo.placa} atualizado.")
                            } else {
                                // Create
                                controller.createVeiculo(novoVeiculo)
                                onSuccess("Veículo ${novoVeiculo.placa} criado com sucesso.")
                            }
                        } catch (e: Exception) {
                            onSuccess("Falha ao salvar: ${e.message}")
                        } finally {
                            isSaving = false
                        }
                    }
                },
                enabled = placa.isNotBlank() && modelo.isNotBlank() && motoristaIdText.isNotBlank() && !isSaving
            ) {
                Text(if (isEditing) "Salvar" else "Adicionar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
