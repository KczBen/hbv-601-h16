package `is`.hi.hbv601g.h16.recipehub.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `is`.hi.hbv601g.h16.recipehub.domain.service.AuthService
import `is`.hi.hbv601g.h16.recipehub.ui.theme.RecipeHubTheme
import kotlinx.coroutines.launch

class AuthActivity : ComponentActivity() {
    private val authService = AuthService();

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
                        authService = authService,
                        onLoginSuccess = {
                            context.startActivity(Intent(context, MainActivity::class.java))
                            finish()
                        },
                        onSignUpSuccess = {
                            context.startActivity(Intent(context, MainActivity::class.java))
                            finish()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CredentialsFields(
    modifier: Modifier = Modifier,
    authService: AuthService,
    onLoginSuccess: () -> Unit,
    onSignUpSuccess: () -> Unit) {
    var showSignUp by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    if (showSignUp) {
        SignUpScreen(
            modifier = modifier,
            authService = authService,
            onBack = { showSignUp = false },
            onSignUpSuccess = { username, email, password ->
                scope.launch {
                    if (authService.registerUser(username, email, password)) {
                        onSignUpSuccess()
                    } else {
                        Toast.makeText(context, "Sign up failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    } else {
        LoginScreen(
            modifier = modifier,
            onSignUpClick = { showSignUp = true },
            onLoginClick = { username, password ->
                scope.launch {
                    if (authService.login(username, password)) {
                        onLoginSuccess()
                    } else {
                        Toast.makeText(context, "Incorrect username or password", Toast.LENGTH_SHORT).show()
                    }
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
fun SignUpScreen(
    modifier: Modifier = Modifier, 
    authService: AuthService,
    onBack: () -> Unit, 
    onSignUpSuccess: (username: String, email: String, password: String) -> Unit) {
    val nameState = rememberTextFieldState()
    val emailState = rememberTextFieldState()
    val passwordState = rememberTextFieldState()

    val password = passwordState.text.toString()

    val issues = authService.validatePassword(password)

    // Show the user password criteria
    val criteria = listOf(
        "At least 8 characters" to !issues.contains("at least 8 characters"),
        "One or more uppercase letters" to !issues.contains("one or more uppercase letters"),
        "One or more lowercase letters" to !issues.contains("one or more lowercase letters"),
        "One or more special symbols" to !issues.contains("one or more special symbols"),
        "One or more digits" to !issues.contains("one or more digits")
    )

    val allMet = issues.isEmpty()

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
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
            label = { Text("Password") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(modifier = Modifier.height(16.dp))
        
        Column(
            modifier = Modifier.width(280.dp), // Match TextField width roughly
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            criteria.forEach { (label, isMet) ->
                ValidationItem(label = label, isMet = isMet)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        // Only enable the button once the criteria are fulfilled, otherwise we'll just get a 401 anyway
        Button(
            onClick = { onSignUpSuccess(nameState.text.toString(), emailState.text.toString(), passwordState.text.toString()) },
            enabled = allMet && nameState.text.isNotEmpty() && emailState.text.isNotEmpty()
        ) {
            Text("Sign up")
        }
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedButton(onClick = onBack) {
            Text("Back to log in")
        }
    }
}

@Composable
fun ValidationItem(label: String, isMet: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = if (isMet) Icons.Filled.CheckCircle else Icons.Outlined.Circle,
            contentDescription = null,
            tint = if (isMet) Color.Black else Color.Gray,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            color = if (isMet) Color.Black else Color.Gray,
            fontSize = 14.sp
        )
    }
}
