package com.example.expensetracker.android.feature.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.expensetracker.android.R
import com.example.expensetracker.android.base.NavigationEvent
import com.example.expensetracker.android.ui.theme.Zinc
import com.example.expensetracker.android.ui.theme.Typography
import com.example.expensetracker.android.widget.ExpenseTextView

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var isRegisterMode by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is NavigationEvent.NavigateToHome -> {
                    navController.navigate("/home") {
                        popUpTo("/login") { inclusive = true }
                    }
                }
                else -> {}
            }
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (topBar, title, form) = createRefs()

            // Reuse same topbar image from existing app
            Image(
                painter = painterResource(id = R.drawable.ic_topbar),
                contentDescription = null,
                modifier = Modifier.constrainAs(topBar) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )

            // Title area
            Column(
                modifier = Modifier
                    .padding(top = 64.dp, start = 16.dp, end = 16.dp)
                    .constrainAs(title) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            ) {
                ExpenseTextView(
                    text = if (isRegisterMode) "Create Account" else "Welcome Back",
                    style = Typography.titleLarge,
                    color = Color.White
                )
                ExpenseTextView(
                    text = if (isRegisterMode) "Track your expenses smarter" else "Sign in to continue",
                    style = Typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }

            // Form card
            LoginForm(
                modifier = Modifier.constrainAs(form) {
                    top.linkTo(title.bottom, margin = 24.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
                isRegisterMode = isRegisterMode,
                uiState = uiState,
                onLogin = { email, password ->
                    viewModel.onEvent(LoginUiEvent.OnLoginClicked(email, password))
                },
                onRegister = { name, email, password ->
                    viewModel.onEvent(LoginUiEvent.OnRegisterClicked(name, email, password))
                },
                onToggleMode = { isRegisterMode = !isRegisterMode }
            )
        }
    }
}

@Composable
fun LoginForm(
    modifier: Modifier,
    isRegisterMode: Boolean,
    uiState: LoginUiState,
    onLogin: (String, String) -> Unit,
    onRegister: (String, String, String) -> Unit,
    onToggleMode: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(20.dp)
    ) {
        // Name field (register only)
        if (isRegisterMode) {
            FormLabel("Name")
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { ExpenseTextView(text = "Your full name") },
                singleLine = true,
                colors = fieldColors()
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        FormLabel("Email")
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { ExpenseTextView(text = "you@email.com") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            colors = fieldColors()
        )
        Spacer(modifier = Modifier.height(16.dp))

        FormLabel("Password")
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { ExpenseTextView(text = "Min. 6 characters") },
            visualTransformation = if (passwordVisible) VisualTransformation.None
            else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            trailingIcon = {
                Text(
                    text = if (passwordVisible) "Hide" else "Show",
                    color = Zinc,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clickable { passwordVisible = !passwordVisible }
                )
            },
            colors = fieldColors()
        )

        // Error message
        if (uiState.error != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = uiState.error,
                color = Color.Red,
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Submit button
        Button(
            onClick = {
                if (isRegisterMode) onRegister(name, email, password)
                else onLogin(email, password)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Zinc),
            enabled = !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                ExpenseTextView(
                    text = if (isRegisterMode) "Create Account" else "Sign In",
                    fontSize = 14.sp,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Toggle login/register
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            ExpenseTextView(
                text = if (isRegisterMode) "Already have an account? " else "Don't have an account? ",
                fontSize = 13.sp,
                color = Color.Gray
            )
            ExpenseTextView(
                text = if (isRegisterMode) "Sign In" else "Register",
                fontSize = 13.sp,
                color = Zinc,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { onToggleMode() }
            )
        }
    }
}

@Composable
private fun FormLabel(text: String) {
    ExpenseTextView(
        text = text.uppercase(),
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        color = Color.Gray
    )
    Spacer(modifier = Modifier.height(6.dp))
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Zinc,
    unfocusedBorderColor = Color.LightGray,
    focusedTextColor = Color.Black,
    unfocusedTextColor = Color.Black,
    cursorColor = Zinc
)