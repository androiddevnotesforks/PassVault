package com.mrntlu.PassVault.ui

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.mrntlu.PassVault.R
import com.mrntlu.PassVault.models.BottomNavItem
import com.mrntlu.PassVault.ui.theme.PassVaultTheme
import com.mrntlu.PassVault.ui.widgets.*
import com.mrntlu.PassVault.utils.PreferenceStore
import com.mrntlu.PassVault.utils.SearchWidgetState
import com.mrntlu.PassVault.utils.loadInterstitial
import com.mrntlu.PassVault.utils.removeInterstitial
import com.mrntlu.PassVault.viewmodels.auth.ParseAuthViewModel
import com.mrntlu.PassVault.viewmodels.offline.OfflineViewModel
import com.mrntlu.PassVault.viewmodels.online.HomeViewModel
import com.mrntlu.PassVault.viewmodels.shared.BillingViewModel
import com.mrntlu.PassVault.viewmodels.shared.MainActivitySharedViewModel
import com.mrntlu.PassVault.viewmodels.shared.OnlinePasswordViewModel
import com.mrntlu.PassVault.viewmodels.shared.ThemeViewModel
import com.parse.ParseUser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var navController: NavHostController
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private val themeViewModel: ThemeViewModel by viewModels()
    private val sharedViewModel: MainActivitySharedViewModel by viewModels()
    private var preferenceStore: PreferenceStore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAnalytics = Firebase.analytics
        preferenceStore = PreferenceStore(applicationContext)

        val systemTheme = when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> { true }
            Configuration.UI_MODE_NIGHT_NO -> { false }
            else -> { false }
        }

        setContent {
            val theme = preferenceStore?.getTheme(systemTheme)?.collectAsState(initial = systemTheme)

            PassVaultTheme(
                darkTheme = theme?.value ?: false,
            ) {
                navController = rememberNavController()
                window.statusBarColor = MaterialTheme.colorScheme.primary.toArgb()

                MainScreen(
                    themeViewModel = themeViewModel,
                    navController = navController,
                    sharedViewModel = sharedViewModel,
                )
            }
        }

        // Interstitial Init & Callbacks
        loadInterstitial(this)
    }

    override fun onDestroy() {
        preferenceStore = null
        removeInterstitial()
        super.onDestroy()
    }
}

@Composable
fun MainScreen(
    themeViewModel: ThemeViewModel,
    sharedViewModel: MainActivitySharedViewModel,
    navController: NavHostController,
) {
    val focusManager = LocalFocusManager.current
    val preferenceStore = PreferenceStore(LocalContext.current)

    val parseViewModel = hiltViewModel<ParseAuthViewModel>()
    val homeViewModel = hiltViewModel<HomeViewModel>()
    val offlineViewModel = hiltViewModel<OfflineViewModel>()
    val onlinePasswordViewModel = hiltViewModel<OnlinePasswordViewModel>()
    val billingViewModel = hiltViewModel<BillingViewModel>()

    val bottomBarItems = listOf(
        BottomNavItem(
            name = stringResource(R.string.bottom_nav_online),
            route = "home",
            icon = Icons.Rounded.Cloud,
        ),
        BottomNavItem(
            name = stringResource(R.string.bottom_nav_offline),
            route = "offline",
            icon = ImageVector.vectorResource(id = R.drawable.ic_database)
        ),
        BottomNavItem(
            name = stringResource(R.string.bottom_nav_settings),
            route = "settings",
            icon = Icons.Rounded.Settings,
        )
    )
    val showBottomBar = navController.currentBackStackEntryAsState().value?.destination?.route in bottomBarItems.map { it.route }
    val isCurrentScreenHome = navController.currentBackStackEntry?.destination?.route == "home"
    val isCurrentScreenOffline = navController.currentBackStackEntry?.destination?.route == "offline"
    val isAuthLoading = parseViewModel.isLoading.value

    val coroutineScope = rememberCoroutineScope()
    val isAdsDialogDismissed = preferenceStore.getAdsDialog().collectAsState(initial = false).value

    val isUserLoggedIn by remember { parseViewModel.isSignedIn }
    var showDialog by remember { mutableStateOf(false) }
    var searchWidgetState by remember{ mutableStateOf(SearchWidgetState.CLOSED) }
    var searchTextState by remember { mutableStateOf("") }
    val showAdsDialog by remember { sharedViewModel.shouldShowRemoveAdsDialog }

    LaunchedEffect(key1 = isCurrentScreenHome) {
        if (!isCurrentScreenHome && searchWidgetState == SearchWidgetState.OPENED) {
            searchWidgetState = SearchWidgetState.CLOSED
            searchTextState = ""
        }
    }

    LaunchedEffect(key1 = isCurrentScreenOffline) {
        if (!isCurrentScreenOffline && searchWidgetState == SearchWidgetState.OPENED) {
            searchWidgetState = SearchWidgetState.CLOSED
            searchTextState = ""
        }
    }

    LaunchedEffect(key1 = isUserLoggedIn) {
        if (isUserLoggedIn) {
            billingViewModel.loginUser(ParseUser.getCurrentUser().objectId)
        } else {
            billingViewModel.logoutUser()
        }
    }

    Scaffold(
        topBar = {
            if (
                (isCurrentScreenOffline && searchWidgetState == SearchWidgetState.OPENED) ||
                (isCurrentScreenHome && isUserLoggedIn && searchWidgetState == SearchWidgetState.OPENED)
            ) {
                SearchAppBar(
                    text = searchTextState,
                    onResetSearch = {
                        if (isCurrentScreenHome)
                            homeViewModel.resetPassword()
                        else
                            offlineViewModel.resetPassword()
                    },
                    onTextChange = { searchTextState = it },
                    onCloseClicked = { searchWidgetState = SearchWidgetState.CLOSED },
                    onSearchClicked = {
                        if (isCurrentScreenHome)
                            homeViewModel.searchPassword(it)
                        else
                            offlineViewModel.searchPassword(it)
                        focusManager.clearFocus(force = true)
                    }
                )
            } else if (navController.currentBackStackEntry?.destination?.route != "online") {
                DefaultAppBar(
                    navController = navController,
                    isAuthLoading = isAuthLoading,
                    isUserLoggedIn = isUserLoggedIn,
                    showBottomBar = showBottomBar,
                    isCurrentScreenHome = isCurrentScreenHome,
                    isCurrentScreenOffline = isCurrentScreenOffline,
                    onSearchClicked = { searchWidgetState = SearchWidgetState.OPENED },
                    onLogOutClicked = {
                        showDialog = true
                    }
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(
                    items = bottomBarItems,
                    navController = navController,
                    onItemClick = {
                        navController.navigate(it.route)
                    },
                )
            }
        }
    ) {
        NavigationComposable(
            navController = navController,
            padding = it,
            parseViewModel = parseViewModel,
            homeViewModel = homeViewModel,
            offlineViewModel = offlineViewModel,
            onlinePasswordViewModel = onlinePasswordViewModel,
            themeViewModel = themeViewModel,
            billingViewModel = billingViewModel,
            sharedViewModel = sharedViewModel,
        )

        if (showDialog) {
            CautionDialog(
                text = stringResource(R.string.ays_logout),
                onConfirmClicked = {
                    showDialog = false
                    parseViewModel.parseSignout()
                }
            ) {
                showDialog = false
            }
        }

        if (showAdsDialog && !isAdsDialogDismissed) {
            CustomDialog(
                title = stringResource(R.string.info),
                text = {
                    Column {
                        Text(
                            text = stringResource(R.string.ads_dialog_info),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth(),
                            contentAlignment = Alignment.BottomEnd,
                        ) {
                            TextButton(
                                onClick = {
                                    coroutineScope.launch {
                                        sharedViewModel.shouldShowDialog(false)
                                        preferenceStore.saveAdsDialog()
                                    }
                                },
                            ) {
                                Text(
                                    text = stringResource(R.string.dont_show_again_),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }
                        }
                    }
                },
                onConfirmClicked = {
                    navController.navigate("settings")
                },
                onDismissClicked = {
                    sharedViewModel.shouldShowDialog(false)
                },
            )
        }

        if (isAuthLoading) {
            LoadingView()
        }
    }
}