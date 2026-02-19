package `is`.hi.hbv601g.h16.recipehub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `is`.hi.hbv601g.h16.recipehub.ui.theme.RecipeHubTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RecipeHubTheme {
                Scaffold( modifier = Modifier.fillMaxSize() ) { innerPadding ->
                    CredentialsFields(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun CredentialsFields(modifier: Modifier = Modifier) {
    var showSignUp by remember { mutableStateOf(false) }

    if (showSignUp) {
        SignUpScreen(modifier = modifier, onBack = { showSignUp = false })
    } else {
        LoginScreen(modifier = modifier, onSignUpClick = { showSignUp = true })
    }
}

@Composable
fun LoginScreen(modifier: Modifier = Modifier, onSignUpClick: () -> Unit) {
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
            label = { Text("Password") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {}) {
            Text("Log in")
        }
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedButton(onClick = onSignUpClick) {
            Text("...or sign up")
        }
    }
}

@Composable
fun SignUpScreen(modifier: Modifier = Modifier, onBack: () -> Unit) {
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
        Button(onClick = {}) {
            Text("Sign up")
        }
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedButton(onClick = onBack) {
            Text("Back to log in")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CredentialsFieldsPreview() {
    RecipeHubTheme {
        CredentialsFields()
    }
}