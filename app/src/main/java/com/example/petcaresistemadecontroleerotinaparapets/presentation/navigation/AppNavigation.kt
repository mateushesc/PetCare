package com.example.petcaresistemadecontroleerotinaparapets.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.petcaresistemadecontroleerotinaparapets.presentation.screens.*
import com.example.petcaresistemadecontroleerotinaparapets.viewmodel.AuthViewModel
import com.example.petcaresistemadecontroleerotinaparapets.viewmodel.EventoViewModel
import com.example.petcaresistemadecontroleerotinaparapets.viewmodel.PetViewModel


@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val petViewModel: PetViewModel = hiltViewModel()
    val eventoViewModel: EventoViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = ScreenRoutes.Login.route) {

        // --- Tela de Login ---
        composable(ScreenRoutes.Login.route) {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(ScreenRoutes.MyPets.route) {
                        popUpTo(ScreenRoutes.Login.route) { inclusive = true }
                    }
                },
                onSignUpClick = {
                    navController.navigate(ScreenRoutes.SignUp.route)
                }
            )
        }

        // --- TELA DE CADASTRO (NOVA) ---
        composable(ScreenRoutes.SignUp.route) {
            SignUpScreen(
                authViewModel = authViewModel,
                onSignUpSuccess = {
                    navController.navigate(ScreenRoutes.MyPets.route) {
                        popUpTo(ScreenRoutes.Login.route) { inclusive = true }
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // --- Tela Meus Pets ---
        composable(ScreenRoutes.MyPets.route) {
            MyPetsScreen(
                petViewModel = petViewModel,
                authViewModel = authViewModel,
                onPetClick = { petId ->
                    navController.navigate(ScreenRoutes.petDetail(petId))
                },
                onAddPetClick = {
                    navController.navigate(ScreenRoutes.AddPet.route)
                },
                onEditPetClick = { petId ->
                    navController.navigate(ScreenRoutes.editPet(petId))
                },
                onSettingsClick = {
                    navController.navigate(ScreenRoutes.Settings.route)
                }
            )
        }

        // --- Tela Adicionar Pet ---
        composable(ScreenRoutes.AddPet.route) {
            AddPetScreen(
                petViewModel = petViewModel,
                authViewModel = authViewModel,
                onPetSaved = {
                    navController.popBackStack()
                },
                petId = null // MODO DE ADIÇÃO
            )
        }

        // --- TELA EDITAR PET (Linha ~83) ---
        composable(
            route = ScreenRoutes.EditPet.route, // Esta linha lê do objeto ScreenRoutes
            arguments = listOf(navArgument("petId") { type = NavType.StringType })
        ) { backStackEntry ->
            AddPetScreen(
                petViewModel = petViewModel,
                authViewModel = authViewModel,
                onPetSaved = {
                    navController.popBackStack()
                },
                petId = backStackEntry.arguments?.getString("petId") // MODO DE EDIÇÃO
            )
        }


        // --- Tela Detalhes do Pet (Linha ~98) ---
        composable(
            route = ScreenRoutes.PetDetail.route, // Esta linha lê do objeto ScreenRoutes
            arguments = listOf(navArgument("petId") { type = NavType.StringType })
        ) { backStackEntry ->
            PetDetailScreen(
                petId = backStackEntry.arguments?.getString("petId"),
                navController = navController,
                petViewModel = petViewModel,
                eventoViewModel = eventoViewModel,
                onAddEventClick = {
                    val petId = backStackEntry.arguments?.getString("petId")
                    if (petId != null) {
                        navController.navigate(ScreenRoutes.addEvent(petId))
                    }
                }
            )
        }

        // --- Tela Adicionar Evento ---
        composable(
            route = ScreenRoutes.AddEvent.route, // Esta linha lê do objeto ScreenRoutes
            arguments = listOf(navArgument("petId") { type = NavType.StringType })
        ) { backStackEntry ->
            AddEventScreen(
                petId = backStackEntry.arguments?.getString("petId"),
                eventoViewModel = eventoViewModel,
                authViewModel = authViewModel,
                onEventSaved = {
                    navController.popBackStack()
                }
            )
        }

        // --- Tela de Lembretes ---
        composable(ScreenRoutes.Reminders.route) {
            RemindersScreen(
                navController = navController
            )
        }

        // --- Tela de Configurações ---
        composable(ScreenRoutes.Settings.route) {
            SettingsScreen(
                navController = navController,
                authViewModel = authViewModel,
                onLogout = {
                    navController.navigate(ScreenRoutes.Login.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
            )
        }

        // (Composable de Reports)
        /*composable(
            route = ScreenRoutes.Reports.route,
            arguments = listOf(navArgument("petId") { type = NavType.StringType })
        ) { backStackEntry ->
            ReportsScreen(
                petId = backStackEntry.arguments?.getString("petId"),
                navController = navController,
                petViewModel = petViewModel,
                eventoViewModel = eventoViewModel
            )
        }*/
    }
}

// ✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅
//
// O ERRO ESTÁ SENDO CAUSADO POR ESTE OBJETO ABAIXO
// O SEU ARQUIVO LOCAL DEVE ESTAR COM UMA VERSÃO ANTIGA DELE
// SUBSTITUA O ARQUIVO INTEIRO PARA GARANTIR QUE ESTA VERSÃO CORRETA SEJA USADA
//
// ✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅
object ScreenRoutes {
    object Login { val route = "login_screen" }
    object SignUp { val route = "signup_screen" }
    object MyPets { val route = "my_pets_screen" }
    object AddPet { val route = "add_pet_screen" }
    object EditPet { val route = "edit_pet_screen/{petId}" } // ✅ DEVE TER O {petId}
    object PetDetail { val route = "pet_detail_screen/{petId}" } // ✅ DEVE TER O {petId}
    object AddEvent { val route = "add_event_screen/{petId}" } // ✅ DEVE TER O {petId}
    object Reminders { val route = "reminders_screen" }
    object Settings { val route = "settings_screen" }
    object Reports { val route = "reports_screen/{petId}" }

    // Funções auxiliares
    fun petDetail(petId: String) = "pet_detail_screen/$petId"
    fun editPet(petId: String) = "edit_pet_screen/$petId"
    fun addEvent(petId: String) = "add_event_screen/$petId"
    fun reports(petId: String) = "reports_screen/$petId"
}