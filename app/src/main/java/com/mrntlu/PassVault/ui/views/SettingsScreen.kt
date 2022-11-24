package com.mrntlu.PassVault.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.mrntlu.PassVault.ui.widgets.settings.FutureSettings

@Composable
fun SettingsScreen(
    navController: NavController
) {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize(),
        content = {
            FutureSettings(
                navController = navController,
            )
        }
    )

    /*Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        TextButton(
            onClick = { *//**//*TODO Implement delete account*//**//* }
        ) {
            Text(
                text = "Delete Account",
                fontSize = 10.sp,
                color = BlueMidnight,
                fontWeight = FontWeight.Bold,
            )
        }
    }*/
}



@Preview
@Composable
fun SettingsScreenPreview() {
    SettingsScreen(rememberNavController())
}