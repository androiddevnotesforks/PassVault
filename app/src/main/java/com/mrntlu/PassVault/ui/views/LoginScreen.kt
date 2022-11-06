package com.mrntlu.PassVault.ui.views

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.*
import com.mrntlu.PassVault.R
import com.mrntlu.PassVault.ui.theme.Yellow700
import com.mrntlu.PassVault.ui.widgets.ErrorDialog
import com.mrntlu.PassVault.ui.widgets.auth.ForgotPasswordSheet
import com.mrntlu.PassVault.utils.CheckLoggedIn
import com.mrntlu.PassVault.utils.setGradientBackground
import com.mrntlu.PassVault.viewmodels.auth.ParseAuthViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LoginScreen(
    navController: NavController,
    parseVM: ParseAuthViewModel
) {
    val isErrorOccured = parseVM.isErrorOccured.value
    val focusManager = LocalFocusManager.current

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.login))
    val progress by animateLottieCompositionAsState(composition = composition, iterations = LottieConstants.IterateForever)

    val usernameState = remember { mutableStateOf(TextFieldValue()) }
    val passwordState = remember { mutableStateOf(TextFieldValue()) }
    var passwordVisible by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmStateChange = {
            it != ModalBottomSheetValue.Expanded
        },
        skipHalfExpanded = true
    )

    CheckLoggedIn(navController = navController, parseVM = parseVM)

    BackHandler(modalSheetState.isVisible) {
        coroutineScope.launch { modalSheetState.hide() }
    }

    ModalBottomSheetLayout(
        sheetState = modalSheetState,
        sheetShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        sheetContent = {
            ForgotPasswordSheet(
                isSheetVisible = modalSheetState.isVisible,
                parseVM = parseVM,
                onCancel = {
                    coroutineScope.launch { modalSheetState.hide() }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .setGradientBackground()
                .verticalScroll(rememberScrollState())
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 48.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                LottieAnimation(
                    modifier = Modifier
                        .padding(vertical = 6.dp)
                        .height(185.dp),
                    composition = composition,
                    progress = { progress },
                )

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = usernameState.value,
                    onValueChange = { usernameState.value = it },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Email
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.moveFocus(
                            FocusDirection.Down
                        )
                    }),
                    label = {
                        Text(text = stringResource(id = R.string.username_mail))
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        backgroundColor = Color.White,
                    ),
                )

                OutlinedTextField(
                    modifier = Modifier
                        .padding(top = 3.dp)
                        .fillMaxWidth(),
                    value = passwordState.value,
                    onValueChange = { passwordState.value = it },
                    singleLine = true,
                    label = {
                        Text(text = stringResource(id = R.string.password))
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    trailingIcon = {
                        val image = if (passwordVisible)
                            Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff

                        val description = if (passwordVisible)
                            stringResource(R.string.hide_password)
                        else
                            stringResource(R.string.show_password)

                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, description)
                        }
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        backgroundColor = Color.White,
                    ),
                )

                Button(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth(),
                    onClick = {
                        focusManager.clearFocus(force = true)
                        parseVM.parseLogin(usernameState.value.text, passwordState.value.text)
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Yellow700,
                    ),
                ) {
                    Text(
                        text = stringResource(id = R.string.login),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                }

                TextButton(
                    onClick = { navController.navigate("register") },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Transparent,
                        contentColor = Color.White
                    )
                ) {
                    Text(text = stringResource(R.string.no_acc_register))
                }

                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            modalSheetState.animateTo(ModalBottomSheetValue.Expanded)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Transparent,
                        contentColor = Color.Black
                    ),
                    contentPadding = PaddingValues(3.dp),
                ) {
                    Text(
                        text = stringResource(R.string.forgot_password_),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal
                    )
                }

                if (isErrorOccured != null) {
                    var showDialog by remember { mutableStateOf(true) }

                    if (showDialog) {
                        ErrorDialog(error = isErrorOccured) {
                            showDialog = false
                            parseVM.isErrorOccured.value = null
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreen(rememberNavController(), viewModel())
}