package `is`.hi.hbv601g.h16.recipehub

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `is`.hi.hbv601g.h16.recipehub.domain.controller.AuthController
import `is`.hi.hbv601g.h16.recipehub.ui.theme.RecipeHubTheme

class AuthActivity : ComponentActivity() {
    private val authController = AuthController();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RecipeHubTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // get the activity's context to launch a new activity
                    val context = LocalContext.current
                    CredentialsFields(
                        modifier = Modifier.padding(innerPadding),
                        authController = authController,
                        onLoginSuccess = {
                            context.startActivity(Intent(context, MainActivity::class.java))
                        },
                        onSignUpSuccess = {context.startActivity(Intent(context, MainActivity::class.java))}
                    )
                }
            }
        }
    }
}

@Composable
fun CredentialsFields(
    modifier: Modifier = Modifier,
    authController: AuthController, // Accept the controller
    onLoginSuccess: () -> Unit,
    onSignUpSuccess: () -> Unit) {
    var showSignUp by remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (showSignUp) {
        SignUpScreen(
            modifier = modifier,
            onBack = { showSignUp = false },
            onSignUpSuccess = onSignUpSuccess)
    } else {
        LoginScreen(
            modifier = modifier,
            onSignUpClick = { showSignUp = true },
            onLoginClick = { username, password ->
                if (authController.login(username, password)) {
                    onLoginSuccess()
                } else {
                    Toast.makeText(context, "Incorrect username or password", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
}

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onSignUpClick: () -> Unit,
    onLoginClick: (String, String) -> Unit
) {
    val nameState = rememberTextFieldState()
    val passwordState = rememberTextFieldState()

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Log in", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            state = nameState,
            label = { Text("Username") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            state = passwordState,
            label = { Text("Password") },
            outputTransformation = OutputTransformation {
                replace(0, length, "•".repeat(length))
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {onLoginClick(nameState.text.toString(), passwordState.text.toString())}) {
            Text("Log in")
        }
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedButton(onClick = onSignUpClick) {
            Text("...or sign up")
        }
    }
}

@Composable
fun SignUpScreen(modifier: Modifier = Modifier, onBack: () -> Unit, onSignUpSuccess: () -> Unit) {
    val nameState = rememberTextFieldState()
    val emailState = rememberTextFieldState()
    val passwordState = rememberTextFieldState()


    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Sign up", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            state = nameState,
            label = { Text("Username") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            state = emailState,
            label = { Text("Email") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            state = passwordState,
            label = { Text("Password") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {onSignUpSuccess()}) {
            Text("Sign up")
        }
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedButton(onClick = onBack) {
            Text("Back to log in")
        }
    }
}