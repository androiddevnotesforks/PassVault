package com.mrntlu.PassVault.ui.widgets

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.mrntlu.PassVault.R

@Composable
fun DefaultAppBar(
    navController: NavController,
    isUserLoggedIn: Boolean,
    isAuthLoading: Boolean,
    showBottomBar: Boolean,
    isCurrentScreenHome: Boolean,
    isCurrentScreenOffline: Boolean,
    onSearchClicked: () -> Unit,
    onLogOutClicked: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(
                text = if (navController.currentDestination?.route == "register") {
                    stringResource(id = R.string.register)
                } else if (navController.currentDestination?.route == "home" && !isUserLoggedIn) {
                    stringResource(id = R.string.login)
                } else {
                    ""
                },
                color = Color.White,
            )
        },
        navigationIcon = {
            if (!showBottomBar && navController.previousBackStackEntry != null && !isAuthLoading) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.cd_back),
                        tint = Color.White,
                    )
                }
            }

            if (isCurrentScreenOffline || (isCurrentScreenHome && isUserLoggedIn)) {
                IconButton(onClick = onSearchClicked) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = stringResource(R.string.cd_search),
                        tint = Color.White,
                    )
                }
            }
        },
        actions = {
            if (isCurrentScreenHome && isUserLoggedIn) {
                IconButton(
                    onClick = onLogOutClicked
                ){
                    Icon(
                        imageVector = Icons.Rounded.Logout,
                        contentDescription = stringResource(R.string.cd_log_out),
                        tint = Color.White,
                    )
                }
            }
        },
        elevation = 8.dp,
        backgroundColor = MaterialTheme.colorScheme.primary,
    )
}

@Preview
@Composable
fun DefaultAppBarPreview() {
    DefaultAppBar(navController = rememberNavController(), isUserLoggedIn = true, isAuthLoading = false, showBottomBar = true, isCurrentScreenHome = true, isCurrentScreenOffline = false, onLogOutClicked = {}, onSearchClicked = {})
}