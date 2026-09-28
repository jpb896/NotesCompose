package com.example.cahier.features.home

import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cahier.R
import com.example.cahier.core.data.NoteType
import com.example.cahier.core.navigation.BrushGraphDestination
import com.example.cahier.core.navigation.CahierNavHost
import com.example.cahier.core.navigation.DrawingCanvasDestination
import com.example.cahier.core.navigation.HomePagePreference
import com.example.cahier.core.navigation.TextCanvasDestination
import com.example.cahier.core.ui.CahierTextureBitmapStore
import com.example.cahier.features.home.viewmodel.SettingsViewModel

sealed class CahierTab(
    val route: String,
    val title: String,
    @DrawableRes val iconRes: Int
) {
    object PrimaryHome : CahierTab("primary_home", "Home", R.drawable.home_24px)
    object Insights : CahierTab("insights", "Insights", R.drawable.insights_24px)
    object Settings : CahierTab("settings", "Settings", R.drawable.settings_24px)

    // Keep Entries accessible as a route definition
    object Entries : CahierTab("entries", "Entries", R.drawable.format_list_bulleted_24px)
}

val bottomNavTabs = listOf(
    CahierTab.PrimaryHome,
    CahierTab.Insights,
    CahierTab.Settings
)

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun CahierApp(
    noteId: Long,
    noteType: NoteType?,
    textureStore: CahierTextureBitmapStore,
    modifier: Modifier = Modifier,
    navigateToBrushGraph: Boolean = false,
    onNavigateToBrushGraphHandled: () -> Unit = {},
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val homePagePreference by settingsViewModel.homePagePreference.collectAsStateWithLifecycle()

    // Determine target route mapped to the primary 'Home' tab
    val primaryHomeRoute = if (homePagePreference == HomePagePreference.HOME) {
        HomeDestination.route
    } else {
        CahierTab.Entries.route
    }

    LaunchedEffect(noteId, noteType) {
        if (noteId > 0) {
            val destination = when (noteType) {
                NoteType.Text -> "${TextCanvasDestination.route}/$noteId"
                NoteType.Drawing -> "${DrawingCanvasDestination.route}/$noteId"
                else -> null
            }
            destination?.let {
                navController.navigate(it)
            }
        }
    }

    LaunchedEffect(navigateToBrushGraph) {
        if (navigateToBrushGraph) {
            if (navController.currentDestination?.route != BrushGraphDestination.route) {
                navController.navigate(BrushGraphDestination.route)
            }
            onNavigateToBrushGraphHandled()
        }
    }

    // Hide bottom bar during active drawing/text canvas editing
    val showBottomBar = currentRoute in listOf(
        HomeDestination.route,
        CahierTab.Entries.route,
        CahierTab.Insights.route,
        CahierTab.Settings.route
    )

    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    tonalElevation = 3.dp
                ) {
                    bottomNavTabs.forEach { tab ->
                        val targetRoute = if (tab == CahierTab.PrimaryHome) primaryHomeRoute else tab.route
                        val selected = currentRoute == targetRoute

                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (currentRoute != targetRoute) {
                                    navController.navigate(targetRoute) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(tab.iconRes),
                                    contentDescription = tab.title
                                )
                            },
                            label = { Text(tab.title) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        CahierNavHost(
            navController = navController,
            textureStore = textureStore,
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
            settingsViewModel = settingsViewModel
        )
    }
}