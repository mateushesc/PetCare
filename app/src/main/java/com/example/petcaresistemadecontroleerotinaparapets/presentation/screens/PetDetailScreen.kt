package com.example.petcaresistemadecontroleerotinaparapets.presentation.screens

import android.widget.Toast // <-- IMPORT ADICIONADO
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Delete // <-- IMPORT ADICIONADO
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext // <-- IMPORT ADICIONADO
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.petcaresistemadecontroleerotinaparapets.data.local.entities.Evento
import com.example.petcaresistemadecontroleerotinaparapets.presentation.navigation.ScreenRoutes
import com.example.petcaresistemadecontroleerotinaparapets.viewmodel.EventoViewModel
import com.example.petcaresistemadecontroleerotinaparapets.viewmodel.PetViewModel
import com.example.petcaresistemadecontroleerotinaparapets.data.local.entities.Pet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetDetailScreen(
    petId: String?,
    navController: NavController,
    petViewModel: PetViewModel,
    eventoViewModel: EventoViewModel,
    onAddEventClick: () -> Unit
) {
    val petIdInt = petId?.toIntOrNull()
    val pet by petViewModel.selectedPet.collectAsState()
    val eventos by eventoViewModel.eventos.collectAsState()
    val context = LocalContext.current // <-- ADICIONADO

    // --- LÓGICA DE EXCLUSÃO (RF01) ---
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog && pet != null) {
        DeleteConfirmationDialog(
            petName = pet!!.nome,
            onConfirm = {
                petViewModel.deletePet(pet!!)
                showDeleteDialog = false
                Toast.makeText(context, "${pet!!.nome} foi excluído.", Toast.LENGTH_SHORT).show()
                navController.popBackStack() // Volta para a tela 'Meus Pets'
            },
            onDismiss = {
                showDeleteDialog = false
            }
        )
    }
    // --- FIM DA LÓGICA DE EXCLUSÃO ---

    // ✅ --- LÓGICA DE EXCLUSÃO DE EVENTO ---
    var showDeleteEventoDialog by remember { mutableStateOf<Evento?>(null) } // Guarda o evento a ser excluído

    if (showDeleteEventoDialog != null) {
        DeleteEventoConfirmationDialog(
            evento = showDeleteEventoDialog!!,
            onConfirm = { evento ->
                eventoViewModel.excluirEvento(evento)
                showDeleteEventoDialog = null
                Toast.makeText(context, "Evento '${evento.tipoEvento}' excluído.", Toast.LENGTH_SHORT).show()
            },
            onDismiss = {
                showDeleteEventoDialog = null
            }
        )
    }
    // --- FIM DA ADIÇÃO ---


    LaunchedEffect(petIdInt) {
        if (petIdInt != null) {
            petViewModel.carregarPetPorId(petIdInt)
            eventoViewModel.carregarEventosDoPet(petIdInt)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(pet?.nome ?: "Detalhes do Pet") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    // --- BOTÃO DE RELATÓRIOS (EXISTENTE) ---
                    IconButton(onClick = {
                        if (petId != null) {
                            navController.navigate(ScreenRoutes.reports(petId))
                        }
                    }) {
                        Icon(Icons.Default.BarChart, contentDescription = "Relatórios do Pet")
                    }

                    // --- BOTÃO DE EXCLUIR (NOVO) ---
                    IconButton(onClick = {
                        showDeleteDialog = true // Abre o diálogo de confirmação
                    }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Excluir Pet",
                            tint = MaterialTheme.colorScheme.error // Dá destaque
                        )
                    }
                    // --- FIM DA ADIÇÃO ---
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddEventClick) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Evento")
            }
        }
    ) { padding ->
        // ... (O resto da tela (LazyColumn) permanece o mesmo) ...
        if (pet == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                item {
                    PetInfoCard(pet!!)
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "Próximos Eventos",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (eventos.isEmpty()) {
                    item {
                        Text(
                            "Nenhum evento cadastrado para este pet.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    items(eventos, key = { it.idEvento }) { evento ->
                        // ✅ CHAMADA DO EventoCard ATUALIZADA
                        EventoCard(
                            evento = evento,
                            onDeleteClick = {
                                showDeleteEventoDialog = evento // Define qual evento excluir
                            }
                        )
                    }
                }
            }
        }
    }
}

// --- DIÁLOGO DE CONFIRMAÇÃO (NOVO) ---
@Composable
private fun DeleteConfirmationDialog(
    petName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Excluir Pet") },
        text = { Text("Tem certeza que deseja excluir $petName? Esta ação não pode ser desfeita.") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Excluir")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
// --- FIM DA ADIÇÃO ---

// ✅ --- DIÁLOGO DE CONFIRMAÇÃO DE EVENTO (NOVO) ---
@Composable
private fun DeleteEventoConfirmationDialog(
    evento: Evento,
    onConfirm: (Evento) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Excluir Evento") },
        text = { Text("Tem certeza que deseja excluir o evento '${evento.tipoEvento}' de ${evento.dataEvento}?") },
        confirmButton = {
            Button(
                onClick = { onConfirm(evento) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Excluir")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
// --- FIM DA ADIÇÃO ---


// ... (PetInfoCard, InfoLinha permanecem os mesmos) ...
@Composable
private fun PetInfoCard(pet: Pet) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = pet.nome,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            InfoLinha(label = "Espécie:", valor = pet.especie)
            InfoLinha(label = "Raça:", valor = pet.raca)
            InfoLinha(label = "Idade:", valor = "${pet.idade} anos")
        }
    }
}

// ✅ --- EventoCard ATUALIZADO (com botão de excluir) ---
@Composable
private fun EventoCard(evento: Evento, onDeleteClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp) // Padding ajustado
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Coluna de Informações (ocupa a maior parte)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = evento.tipoEvento,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                if (evento.observacoes != null) {
                    Text(
                        text = evento.observacoes,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            // Data
            Text(
                text = evento.dataEvento,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            // Botão de Excluir
            IconButton(onClick = onDeleteClick) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Excluir Evento",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f) // Cor de erro
                )
            }
        }
    }
}
// --- FIM DA ATUALIZAÇÃO ---


@Composable
private fun InfoLinha(label: String, valor: String) {
    Row {
        Text(
            text = label,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.width(80.dp)
        )
        Text(text = valor)
    }
}