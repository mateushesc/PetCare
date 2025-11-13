package com.example.petcaresistemadecontroleerotinaparapets.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.petcaresistemadecontroleerotinaparapets.data.local.entities.Pet
import com.example.petcaresistemadecontroleerotinaparapets.viewmodel.AuthViewModel
import com.example.petcaresistemadecontroleerotinaparapets.viewmodel.PetViewModel
import com.example.petcaresistemadecontroleerotinaparapets.viewmodel.PetUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPetsScreen(
    petViewModel: PetViewModel,
    authViewModel: AuthViewModel,
    onPetClick: (String) -> Unit,
    onAddPetClick: () -> Unit,
    onEditPetClick: (String) -> Unit,
    onSettingsClick: () -> Unit
) {
    val pets by petViewModel.pets.collectAsState()
    val uiState by petViewModel.uiState.collectAsState()
    // ✅ CORREÇÃO: Removida a val 'userEmail' que causava erro

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meus Pets") },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Configurações")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddPetClick) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Pet")
            }
        }
    ) { padding ->
        MyPetsContent(
            modifier = Modifier.padding(padding),
            uiState = uiState,
            pets = pets,
            onPetClick = onPetClick,
            onEditPetClick = onEditPetClick
        )
    }
}

@Composable
private fun MyPetsContent(
    modifier: Modifier = Modifier,
    uiState: PetUiState,
    pets: List<Pet>,
    onPetClick: (String) -> Unit,
    onEditPetClick: (String) -> Unit
) {
    Column(modifier = modifier.padding(16.dp)) {
        // ✅ CORREÇÃO: Removido o Text("Olá, $userEmail!...")

        Spacer(modifier = Modifier.height(16.dp)) // Espaçamento ajustado

        when (uiState) {
            is PetUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is PetUiState.Success -> {
                if (pets.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Você ainda não adicionou nenhum pet.")
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(pets, key = { it.petId }) { pet ->
                            PetCard(
                                pet = pet,
                                onClick = { onPetClick(pet.petId.toString()) },
                                onEditClick = { onEditPetClick(pet.petId.toString()) }
                            )
                        }
                    }
                }
            }
            is PetUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Erro ao carregar pets: ${uiState.message}")
                }
            }
            is PetUiState.Idle -> {
                // Estado inicial
            }
        }
    }
}

// ✅ --- PETCARD TOTALMENTE CORRIGIDO ---
// (Corrige o erro 'it' e o problema de cliques sobrepostos)
@Composable
private fun PetCard(
    pet: Pet,
    onClick: () -> Unit,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        // NÃO definimos o onClick no Card principal
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp), // Padding apenas no início da Row
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Esta Row interna é a área clicável para "detalhes"
            Row(
                modifier = Modifier
                    .weight(1f) // Ocupa o espaço disponível
                    .clickable(onClick = onClick) // Ação de clique
                    .padding(vertical = 16.dp), // Padding interno para altura
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Pets,
                    contentDescription = "Ícone de Pet",
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = pet.nome,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Text(
                        text = "${pet.especie} - ${pet.raca}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // O IconButton fica fora da área clicável de "detalhes"
            IconButton(
                onClick = onEditClick // Ação de clique separada
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar Pet",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}