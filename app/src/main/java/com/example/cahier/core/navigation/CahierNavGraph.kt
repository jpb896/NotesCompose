/*
 * Copyright 2025 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.cahier.core.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.cahier.core.ui.CahierTextureBitmapStore
import com.example.cahier.developer.brushgraph.ui.BrushGraphScreen
import com.example.cahier.features.drawing.DrawingCanvas
import com.example.cahier.features.home.CahierTab
import com.example.cahier.features.home.HomeDestination
import com.example.cahier.features.home.HomePane
import com.example.cahier.features.home.SettingsScreen
import com.example.cahier.features.text.TextNoteCanvasScreen

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@OptIn(ExperimentalComposeApi::class)
@Composable
fun CahierNavHost(
    navController: NavHostController,
    textureStore: CahierTextureBitmapStore,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        // Home Screen
        composable(HomeDestination.route) {
            HomePane(
                navigateToCanvas = { noteId ->
                    navController.navigate("${TextCanvasDestination.route}/$noteId")
                },
                navigateToDrawingCanvas = { noteId ->
                    navController.navigate("${DrawingCanvasDestination.route}/$noteId")
                },
                navigateUp = {
                    navController.navigateUp()
                },
                navigateToBrushGraph = {
                    navController.navigate(BrushGraphDestination.route)
                }
            )
        }

        // Bottom Bar Destination: Entries
        composable(CahierTab.Entries.route) {
            HomePane(
                navigateToCanvas = { noteId ->
                    navController.navigate("${TextCanvasDestination.route}/$noteId")
                },
                navigateToDrawingCanvas = { noteId ->
                    navController.navigate("${DrawingCanvasDestination.route}/$noteId")
                },
                navigateUp = {
                    navController.navigateUp()
                },
                navigateToBrushGraph = {
                    navController.navigate(BrushGraphDestination.route)
                }
            )
        }

        // Bottom Bar Destination: Insights
        composable(CahierTab.Insights.route) {
            HomePane(
                navigateToCanvas = { noteId ->
                    navController.navigate("${TextCanvasDestination.route}/$noteId")
                },
                navigateToDrawingCanvas = { noteId ->
                    navController.navigate("${DrawingCanvasDestination.route}/$noteId")
                },
                navigateUp = {
                    navController.navigateUp()
                },
                navigateToBrushGraph = {
                    navController.navigate(BrushGraphDestination.route)
                }
            )
        }

        // Bottom Bar Destination: Settings
        composable(CahierTab.Settings.route) {
            SettingsScreen(
                navigateToBrushGraph = {
                    navController.navigate(BrushGraphDestination.route)
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // Text Canvas Editor
        composable(
            route = TextCanvasDestination.routeWithArgs,
            arguments = listOf(navArgument(TextCanvasDestination.NOTE_ID_ARG) {
                type = NavType.LongType
            })
        ) {
            TextNoteCanvasScreen(
                onExit = { navController.navigateUp() }
            )
        }

        // Drawing Canvas Editor
        composable(
            route = DrawingCanvasDestination.routeWithArgs,
            arguments = listOf(navArgument(DrawingCanvasDestination.NOTE_ID_ARG) {
                type = NavType.LongType
            })
        ) {
            DrawingCanvas(
                navigateUp = { navController.navigateUp() },
                navigateToBrushGraph = { navController.navigate(BrushGraphDestination.route) }
            )
        }

        // Brush Graph Tool
        composable(route = BrushGraphDestination.route) {
            BrushGraphScreen(
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}

object TextCanvasDestination : NavigationDestination {
    override val route = "note_canvas"
    const val NOTE_ID_ARG = "noteId"
    val routeWithArgs = "$route/{$NOTE_ID_ARG}"
}

object DrawingCanvasDestination : NavigationDestination {
    override val route = "drawing_canvas"
    const val NOTE_ID_ARG = "noteId"
    val routeWithArgs = "$route/{$NOTE_ID_ARG}"
}

object BrushGraphDestination : NavigationDestination {
    override val route = "brush_graph"
}