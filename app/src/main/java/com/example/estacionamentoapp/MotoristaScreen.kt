package com.example.estacionamentoapp

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.estacionamentoapp.data.Motorista
import kotlinx.coroutines.launch

// Importa a anotação para silenciar o aviso sobre APIs experimentais
import androidx.compose.material3.ExperimentalMaterial3Api

// ATENÇÃO: O enum 'Screen' DEVE ser definido APENAS no MainActivity.kt
// Removido o enum duplicado daqui para resolver o erro de compilação.

// Adiciona a anotação OptIn para permitir o uso de TopAppBar e Scaffold
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MotoristaScreen(
    controller: EstacionamentoController,
    onNavigateTo: (Screen) -> Unit,
    onBack: () -> Boolean
) {

    val scope = rememberCoroutineScope()
    var motoristas by remember { mutableStateOf(emptyList<Motorista>()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var refreshKey by remember { mutableStateOf(0) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedMotorista by remember { mutableStateOf<Motorista?>(null) } // Motorista para edição ou null para criação

    // Função de recarregamento
    fun fetchData() {
        isLoading = true
        error = null
        scope.launch {
            try {
                motoristas = controller.fetchMotoristas()
            } catch (e: Exception) {
                error = "Erro ao carregar motoristas: ${e.message}"
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
        selectedMotorista = null
        refreshKey++ // Força o LaunchedEffect a buscar novos dados
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gerenciar Motoristas (CRUD)") },
                actions = {
                    Button(onClick = { onNavigateTo(Screen.VAGAS) }) {
                        Text("Vagas")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                selectedMotorista = null // Null para criar um novo
                showDialog = true
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Adicionar Motorista")
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
                // Lista de Motoristas
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(motoristas) { motorista ->
                        MotoristaItem(
                            motorista = motorista,
                            onEdit = {
                                selectedMotorista = motorista
                                showDialog = true
                            },
                            onDelete = {
                                scope.launch {
                                    try {
                                        // Garante que o ID não seja nulo antes de deletar
                                        val idMotorista = it.id ?: throw IllegalArgumentException("ID do motorista ausente.")
                                        controller.deleteMotorista(idMotorista)
                                        onActionSuccess("Motorista ${it.nome} excluído.")
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
        MotoristaFormDialog(
            controller = controller,
            motoristaToEdit = selectedMotorista,
            onDismiss = { showDialog = false },
            onSuccess = onActionSuccess
        )
    }
}

// Componente para exibir um único item Motorista
@Composable
fun MotoristaItem(motorista: Motorista, onEdit: (Motorista) -> Unit, onDelete: (Motorista) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onEdit(motorista) }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(text = motorista.nome, fontWeight = FontWeight.Bold)
                Text(text = "CPF: ${motorista.cpf}", fontSize = 12.sp, color = Color.Gray)
                Text(text = "ID: ${motorista.id}", fontSize = 10.sp, color = Color.LightGray)
            }
            // Botão de Edição
            IconButton(onClick = { onEdit(motorista) }) {
                Icon(Icons.Filled.Edit, contentDescription = "Editar")
            }
            // Botão de Exclusão
            IconButton(onClick = { onDelete(motorista) }) {
                Icon(Icons.Filled.Delete, contentDescription = "Excluir", tint = Color.Red)
            }
        }
    }
}

// Dialog para o Formulário de Criação e Edição
@Composable
fun MotoristaFormDialog(
    controller: EstacionamentoController,
    motoristaToEdit: Motorista?,
    onDismiss: () -> Unit,
    onSuccess: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    var nome by remember { mutableStateOf(motoristaToEdit?.nome ?: "") }
    var cpf by remember { mutableStateOf(motoristaToEdit?.cpf ?: "") }
    var isSaving by remember { mutableStateOf(false) }

    val isEditing = motoristaToEdit != null
    val title = if (isEditing) "Editar Motorista (ID: ${motoristaToEdit?.id})" else "Adicionar Novo Motorista"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Campo Nome
                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Campo CPF
                OutlinedTextField(
                    value = cpf,
                    onValueChange = { cpf = it },
                    label = { Text("CPF") },
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
                    isSaving = true
                    scope.launch {
                        try {
                            val novoMotorista = Motorista(
                                id = motoristaToEdit?.id,
                                nome = nome,
                                cpf = cpf
                            )
                            if (isEditing) {
                                // Update
                                controller.updateMotorista(novoMotorista)
                                onSuccess("Motorista ${novoMotorista.nome} atualizado.")
                            } else {
                                // Create
                                controller.createMotorista(novoMotorista)
                                onSuccess("Motorista ${novoMotorista.nome} criado com sucesso.")
                            }
                        } catch (e: Exception) {
                            onSuccess("Falha ao salvar: ${e.message}") // Usa onSuccess para exibir o erro como Toast
                        } finally {
                            isSaving = false
                        }
                    }
                },
                enabled = nome.isNotBlank() && cpf.isNotBlank() && !isSaving // Validação básica
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
