/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.lunchtray

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.lunchtray.datasource.DataSource
import com.example.lunchtray.ui.AccompanimentMenuScreen
import com.example.lunchtray.ui.CheckoutScreen
import com.example.lunchtray.ui.EntreeMenuScreen
import com.example.lunchtray.ui.OrderViewModel
import com.example.lunchtray.ui.SideDishMenuScreen
import com.example.lunchtray.ui.StartOrderScreen

// TODO: Screen enum
// TODO: Start this task March 28 6:13pm
//       I remember the coding pattern from the cupcake app.
//       I will use this pattern as a guide.
//       The starter code provides the string for the titles.
enum class LunchTrayScreen(@StringRes val title: Int) {
    Start(title = R.string.start_order),
    Entree(title = R.string.choose_entree),
    SideDish(title = R.string.choose_side_dish),
    Accompaniment(title = R.string.choose_accompaniment),
    OrderCheckout(title = R.string.order_checkout)
}

// TODO: AppBar
//       Create a composable for the AppBar of the Scaffold composable. The AppBar should display
//       the title of the current screen, always. The appropriate backward-navigation button also
//       should appear on the screen if backward navigation is possible. Backward navigation should
//       not be available from the STart screen.
//       The parameter navigateUp is named as such since the calling composable extension function
//       delegates this action to navController.navigateUp().
//       The TopAppBar and TopAppBarDefaults are experimental in the Material3 API.
//       There are two ways to handle this.
//       Use the @OptIn(ExperimentalMaterial3Api::class) annotation on the composable function
//       that references TopAppBar and TopAppBarDefaults.
//       Alternatively, add a line to kotlinOptions in app/build.gradle.
//       freeCompilerArgs += "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api"
//       The Cupcake app chose the primaryContainer color for the top app bar.
//       This is a darker color than the background, provides more delineation.
//       The UX wireframes of the Lunch Tray app show a top app bar the same color as the background.
//       There is a theme color for that: background!
//       The UX wireframes of the Lunch Tray app show a top app bar title that is centered horizontally.
//       There is a composable function for that: CenterAlignedTopAppBar!
@Composable
fun LunchTrayAppBar(
    currentScreen: LunchTrayScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit, // what to actually do when the user navigates up
    modifier: Modifier = Modifier
) {
    // TODO: delegate to the Jetpack Compose Material3 TopAppBar composable function.
    CenterAlignedTopAppBar(
        title = { Text(stringResource(id = currentScreen.title)) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }

    )
}


/**
 * Note that the navigation controller has Navigator vararg values,
 * since there is no bottom tab backstack navigation to be tracked.
 *
 * Parameter:
 * navController The navigation controller for the destinations of screens.
 *     The rememberNavController() extension function is used to restore state
 *     after configuration changes; it delegates to rememberSaveable().
 *     It is an instance of NavHostController, that extends NavController.
 */
@Composable
fun LunchTrayApp(
    navController: NavHostController = rememberNavController()
) {

    // TODO: Check out my enum class!
    LunchTrayScreen.entries.forEach {
        Log.i(
            "LunchTrayApp",
            "lunch tray #${it.ordinal} screen title and name: ${stringResource(it.title)} and ${it.name}"
        )
    }

    // TODO: Create Controller and initialization
    // TODO: Start this task March 28 6:34pm
    //       I read the practice task description. Then had to start supper.
    //       Studying the cupcake app, the navigation controller is passed in as a parameter.
    //       This is done, mainly, to make the LunchTray testable.
    // TODO: As per the codelab, using the navController instance, initialize the backstack entry,
    //       and name the current screen. Notice that currentBackStackEntryAsState() is called.
    //       It collects the back stack entries flow as state. This facilitates (re)composition.
    // TODO: Returned to this task March 29 3:00pm to complete it.
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen =
        LunchTrayScreen.valueOf(backStackEntry?.destination?.route ?: LunchTrayScreen.Start.name)

    // Create ViewModel
    val viewModel: OrderViewModel = viewModel()

    Scaffold(
        topBar = {
            LunchTrayAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->

        Log.i("LunchTrayApp", "The inner padding is $innerPadding.")

        val uiState by viewModel.uiState.collectAsState()

        // TODO: Navigation host
        // TODO: Start this task March 29 5:11pm

        // Add all the destinations (screens/routes) to NavHost.
        NavHost(
            navController = navController,
            startDestination = LunchTrayScreen.Start.name,
            modifier = Modifier.padding(innerPadding)
        ) {

            // The start screen
            composable(route = LunchTrayScreen.Start.name) {
                StartOrderScreen(
                    onStartOrderButtonClicked = {
                        navController.navigate(LunchTrayScreen.Entree.name)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(dimensionResource(R.dimen.padding_medium))
                )
            }

            // The entree screen
            // I learned that the verticalScroll() of the Modifier must be configured.
            // The entree list height is greater than the screen height.
            // Without verticalScroll(), the buttons are compressed to fit the screen.
            // Then the button text is not visible!
            composable(route = LunchTrayScreen.Entree.name) {
                EntreeMenuScreen(
                    options = DataSource.entreeMenuItems,
                    onCancelButtonClicked = { cancelOrderAndNavigateToStart(navController) },
                    onNextButtonClicked = { navController.navigate(LunchTrayScreen.SideDish.name) },
                    onSelectionChanged = { viewModel.updateEntree(it) },
                    modifier = Modifier
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                )
            }

            // The side dish screen
            composable(route = LunchTrayScreen.SideDish.name) {
                SideDishMenuScreen(
                    options = DataSource.sideDishMenuItems,
                    onCancelButtonClicked = { cancelOrderAndNavigateToStart(navController) },
                    onNextButtonClicked = { navController.navigate(LunchTrayScreen.Accompaniment.name) },
                    onSelectionChanged = { viewModel.updateSideDish(it) },
                    modifier = Modifier
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                )
            }

            // The accompaniment screen
            composable(route = LunchTrayScreen.Accompaniment.name) {
                AccompanimentMenuScreen(
                    options = DataSource.accompanimentMenuItems,
                    onCancelButtonClicked = { cancelOrderAndNavigateToStart(navController) },
                    onNextButtonClicked = { navController.navigate(LunchTrayScreen.OrderCheckout.name) },
                    onSelectionChanged = { viewModel.updateAccompaniment(it) },
                    modifier = Modifier
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                )
            }

            // The order checkout screen
            composable(route = LunchTrayScreen.OrderCheckout.name) {
                CheckoutScreen(
                    orderUiState = uiState,
                    onCancelButtonClicked = { cancelOrderAndNavigateToStart(navController) },
                    onNextButtonClicked = {
                        cancelOrderAndNavigateToStart(navController) // pop to start like cancel
                        Log.i("LunchTrayApp", "TODO: submit order to the <checkout> service.")
                    },
                    modifier = Modifier
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                )
            }

        }

    }
}

private fun cancelOrderAndNavigateToStart(navController: NavHostController) {
    navController.popBackStack(LunchTrayScreen.Start.name, inclusive = false)
}
