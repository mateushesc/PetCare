package com.example.petcaresistemadecontroleerotinaparapets.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.petcaresistemadecontroleerotinaparapets.data.local.entities.Pet
import com.example.petcaresistemadecontroleerotinaparapets.viewmodel.AuthViewModel
import com.example.petcaresistemadecontroleerotinaparapets.viewmodel.PetViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPetScreen(
    petViewModel: PetViewModel,
    authViewModel: AuthViewModel,
    onPetSaved: () -> Unit,
    petId: String? = null
) {
    var nome by remember { mutableStateOf("") }
    var especie by remember { mutableStateOf("") }
    var raca by remember { mutableStateOf("") }
    var idade by remember { mutableStateOf("") }

    val context = LocalContext.current

    // ✅✅✅ CORREÇÃO AQUI (LINHA 35) ✅✅✅
    // Trocamos 'getCurrentUserId()' (que não existe)
    // por 'getCurrentUser()?.uid' (que pega o ID do usuário logado)
    val currentUserId = authViewModel.getCurrentUser()?.uid //
    // --- FIM DA CORREÇÃO ---

    val isEditMode = petId != null
    val petIdInt = petId?.toIntOrNull()
    var existingPet by remember { mutableStateOf<Pet?>(null) }

    // (Carrega os dados do pet se estiver no modo de edição)
    LaunchedEffect(petIdInt) {
        if (isEditMode && petIdInt != null) {
            val pet = petViewModel.getPetParaEdicao(petIdInt)
            if (pet != null) {
                existingPet = pet
                nome = pet.nome
                especie = pet.especie
                raca = pet.raca
                idade = pet.idade.toString()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Editar Pet" else "Adicionar Pet") },
                navigationIcon = {
                    IconButton(onClick = onPetSaved) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = especie,
                onValueChange = { especie = it },
                label = { Text("Espécie * (ex: Cachorro, Gato)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = raca,
                onValueChange = { raca = it },
                label = { Text("Raça * (ex: Poodle, Siamês)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = idade,
                onValueChange = { idade = it },
                label = { Text("Idade * (anos)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val idadeInt = idade.toIntOrNull()

                    if (nome.isBlank() || especie.isBlank() || raca.isBlank() || idadeInt == null) {
                        Toast.makeText(context, "Preencha todos os campos obrigatórios.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    // ✅ A verificação abaixo agora funciona, pois 'currentUserId' é um String?
                    if (currentUserId == null) {
                        Toast.makeText(context, "Erro: Usuário não autenticado.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (isEditMode && existingPet != null) {
                        // Modo Edição: Atualiza o pet existente
                        val petAtualizado = existingPet!!.copy(
                            nome = nome,
                            especie = especie,
                            raca = raca,
                            idade = idadeInt
                        )
                        petViewModel.updatePet(petAtualizado)
                        Toast.makeText(context, "Pet atualizado!", Toast.LENGTH_SHORT).show()
                    } else {
                        // Modo Adição: Cria um novo pet
                        val novoPet = Pet(
                            nome = nome,
                            especie = especie,
                            raca = raca,
                            idade = idadeInt,
                            // ✅ Erro 2 resolvido: 'currentUserId' agora é um 'String' (não-nulo)
                            // graças à verificação 'if (currentUserId == null)' acima
                            userId = currentUserId,
                            isSynced = false
                        )
                        petViewModel.addPet(novoPet)
                        Toast.makeText(context, "Pet salvo!", Toast.LENGTH_SHORT).show()
                    }

                    onPetSaved()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isEditMode) "Atualizar Pet" else "Salvar Pet")
            }
        }
    }
}