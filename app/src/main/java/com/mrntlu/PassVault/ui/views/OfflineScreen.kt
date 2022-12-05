@file:OptIn(ExperimentalMaterialApi::class)

package com.mrntlu.PassVault.ui.views

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mrntlu.PassVault.R
import com.mrntlu.PassVault.models.OfflinePassword
import com.mrntlu.PassVault.ui.widgets.BannerAdView
import com.mrntlu.PassVault.ui.widgets.CautionDialog
import com.mrntlu.PassVault.ui.widgets.OfflinePasswordBottomSheet
import com.mrntlu.PassVault.ui.widgets.OfflinePasswordList
import com.mrntlu.PassVault.utils.*
import com.mrntlu.PassVault.viewmodels.offline.OfflineViewModel
import com.mrntlu.PassVault.viewmodels.shared.BillingViewModel
import com.mrntlu.PassVault.viewmodels.shared.MainActivitySharedViewModel
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun OfflineScreen(
    offlineViewModel: OfflineViewModel,
    billingViewModel: BillingViewModel,
    sharedViewModel: MainActivitySharedViewModel,
) {
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        offlineViewModel.getOfflinePasswords()
    }

    var showDialog by remember { mutableStateOf(false) }
    var deleteIndex by remember { mutableStateOf(-1) }
    val isPurchased by remember { billingViewModel.isPurchased }

    var uiState by remember { mutableStateOf<UIState<OfflinePassword>>(UIState.AddItem) }

    val coroutineScope = rememberCoroutineScope()
    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmStateChange = { false },
        skipHalfExpanded = true
    )

    BackHandler(modalSheetState.isVisible) {
        coroutineScope.launch { modalSheetState.hide() }
    }

    fun interstitialAdsHandler() {
        if (!isPurchased) {
            if (adCount % 3 == 0) {
                showInterstitial(context) {
                    sharedViewModel.shouldShowDialog(true)
                }
            }
            adCount++
        }
    }

    ModalBottomSheetLayout(
        sheetState = modalSheetState,
        sheetShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        sheetContent = {
            OfflinePasswordBottomSheet(
                offlineVM = offlineViewModel,
                uiState = uiState,
                isSheetVisible = modalSheetState.isVisible,
                onEditClicked = {
                    uiState = UIState.EditItem(uiState.getItem()!!, uiState.getPosition()!!)
                },
                onCancel = {
                    coroutineScope.launch { modalSheetState.hide() }
                }
            )
        }
    ) {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        interstitialAdsHandler()

                        coroutineScope.launch {
                            uiState = UIState.AddItem

                            if (modalSheetState.isVisible)
                                modalSheetState.hide()
                            else
                                modalSheetState.animateTo(ModalBottomSheetValue.Expanded)
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.onBackground,
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Icon(
                        modifier = Modifier.size(28.dp),
                        imageVector = Icons.Rounded.Add,
                        contentDescription = stringResource(R.string.cd_add),
                        tint = MaterialTheme.colorScheme.background,
                    )
                }
            },
            floatingActionButtonPosition = FabPosition.End,
            isFloatingActionButtonDocked = false,
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = MaterialTheme.colorScheme.background),
                ) {
                    if (context.isNetworkConnectionAvailable() && !isPurchased) {
                        BannerAdView()
                    }

                    val passwords by offlineViewModel.password

                    OfflinePasswordList(
                        passwords = passwords,
                        onEditClicked = { index ->
                            passwords?.let { list ->
                                interstitialAdsHandler()

                                uiState = UIState.EditItem(list[index], index)

                                coroutineScope.launch {
                                    if (modalSheetState.isVisible)
                                        modalSheetState.hide()
                                    else
                                        modalSheetState.animateTo(ModalBottomSheetValue.Expanded)
                                }
                            }
                        },
                        onDeleteClicked = { index ->
                            showDialog = true
                            deleteIndex = index
                        },
                        onDescriptionClicked = { index ->
                            passwords?.let { list ->
                                interstitialAdsHandler()

                                uiState = UIState.ViewItem(list[index], index)

                                coroutineScope.launch {
                                    if (modalSheetState.isVisible)
                                        modalSheetState.hide()
                                    else
                                        modalSheetState.animateTo(ModalBottomSheetValue.Expanded)
                                }
                            }
                        }
                    )

                    if (showDialog) {
                        CautionDialog(
                            text = stringResource(id = R.string.ays_delete),
                            onConfirmClicked = {
                                showDialog = false
                                offlineViewModel.deletePassword(deleteIndex)
                                deleteIndex = -1
                            }
                        ) {
                            showDialog = false
                        }
                    }
                }
            }
        )
    }
}

@Preview
@Composable
fun OfflineScreenPreview() {
    OfflineScreen(viewModel(), viewModel(), viewModel())
}