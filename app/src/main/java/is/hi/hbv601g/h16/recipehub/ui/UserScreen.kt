package `is`.hi.hbv601g.h16.recipehub.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import `is`.hi.hbv601g.h16.recipehub.domain.service.AuthService
import `is`.hi.hbv601g.h16.recipehub.model.Recipe
import `is`.hi.hbv601g.h16.recipehub.model.User
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(
    profileUser: User,
    mainViewModel: MainViewModel,
    onBack: () -> Unit,
    onRecipeClick: (Recipe) -> Unit = {}
) {
    val currentUser = AuthService.currentUser
    val isOwnProfile = currentUser?.id == profileUser.id

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // which tab is selected, 0 = Recipes, 1 = Recipe Books
    var selectedTab by remember { mutableIntStateOf(0) }

    // follow state — based on whether current user already follows this person
    var isFollowing by remember {
        mutableStateOf(currentUser?.following?.any { it.id == profileUser.id } ?: false)
    }

    // controls the unfollow confirmation dialog
    var showUnfollowDialog by remember { mutableStateOf(false) }

    // controls the edit profile dialog - own profile
    var showEditProfileDialog by remember { mutableStateOf(false) }

    var displayedUser by remember { mutableStateOf(profileUser) }

    var userRecipes by remember { mutableStateOf<List<Recipe>>(emptyList()) }
    LaunchedEffect(profileUser.id) {
        profileUser.id.let { uid ->
            mainViewModel.fetchRecipeBooks(uid)
            // filter the global recipe list for this user's recipes
            userRecipes = mainViewModel.recipes.filter { it.owner.id == uid }
            if (userRecipes.isEmpty()) {
                // recipes not cached yet - fetch all and filter
                mainViewModel.fetchRecipes()
                userRecipes = mainViewModel.recipes.filter { it.owner.id == uid }
            }
        }
    }

    if (showUnfollowDialog) {
        AlertDialog(
            onDismissRequest = { showUnfollowDialog = false },
            title = { Text("Unfollow ${displayedUser.userName}?") },
            text = { Text("You will no longer see their recipes in your feed.") },
            confirmButton = {
                Button(
                    onClick = {
                        showUnfollowDialog = false
                        mainViewModel.unfollowUser(displayedUser.id) { updatedUser ->
                            if (updatedUser != null) {
                                displayedUser = updatedUser
                                isFollowing = false
                                scope.launch {
                                    snackbarHostState.showSnackbar("Unfollowed ${displayedUser.userName}")
                                }
                            } else {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Something went wrong, please try again")
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB00020))
                ) {
                    Text("Unfollow")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUnfollowDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showEditProfileDialog) {
        EditProfileDialog(
            currentBio = displayedUser.bio,
            currentPictureUrl = displayedUser.profilePictureURL,
            onDismiss = { showEditProfileDialog = false },
            onConfirm = { newBio, newPicUrl ->
                showEditProfileDialog = false
                scope.launch {
                    val updated = mainViewModel.userService.updateUser(
                        id = displayedUser.id,
                        bio = newBio,
                        profilePictureUrl = newPicUrl.ifBlank { null }
                    )
                    if (updated != null) {
                        displayedUser = updated   // update the UI
                        AuthService.currentUser = updated
                        snackbarHostState.showSnackbar("Profile updated!")
                    } else {
                        snackbarHostState.showSnackbar("Update failed, please try again")
                    }
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(if (isOwnProfile) "My Profile" else displayedUser.userName) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // edit button - only visible on own profile
                    if (isOwnProfile) {
                        IconButton(onClick = { showEditProfileDialog = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit profile")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            item {
                Spacer(modifier = Modifier.height(16.dp))

                // avatar
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profile picture",
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                // username
                Text(
                    text = displayedUser.userName,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                // bio - shown if not empty, otherwise a placeholder on own profile
                if (displayedUser.bio.isNotBlank()) {
                    Text(
                        text = displayedUser.bio,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                } else if (isOwnProfile) {
                    TextButton(onClick = { showEditProfileDialog = true }) {
                        Text("+ Add a bio")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // follower/following counts
                Row(
                    horizontalArrangement = Arrangement.spacedBy(40.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatColumn(label = "Recipes", count = userRecipes.size)
                    StatColumn(label = "Followers", count = displayedUser.followers.size)
                    StatColumn(label = "Following", count = displayedUser.following.size)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // follow/unfollow button - only on other people's profiles
                if (!isOwnProfile) {
                    if (isFollowing) {
                        OutlinedButton(
                            onClick = { showUnfollowDialog = true },
                            modifier = Modifier.width(160.dp)
                        ) {
                            Text("Unfollow")
                        }
                    } else {
                        Button(
                            onClick = {
                                mainViewModel.followUser(displayedUser.id) { updatedUser ->
                                    if (updatedUser != null) {
                                        displayedUser = updatedUser
                                        isFollowing = true
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Following ${displayedUser.userName}")
                                        }
                                    } else {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Something went wrong, please try again")
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.width(160.dp)
                        ) {
                            Text("Follow")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
            }

            item {
                TabRow(
                    selectedTabIndex = selectedTab
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Recipes") }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Recipe Books") }
                    )
                }
            }

            when (selectedTab) {

                // recipes tab
                0 -> {
                    if (userRecipes.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 48.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isOwnProfile) "You haven't posted any recipes yet."
                                    else "${displayedUser.userName} hasn't posted any recipes yet.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        items(userRecipes) { recipe ->
                            ProfileRecipeCard(
                                recipe = recipe,
                                onClick = { onRecipeClick(recipe) }
                            )
                        }
                    }
                }

                // recipe books tab
                1 -> {
                    val books = mainViewModel.recipeBooks.filter {
                        it.owner?.id == displayedUser.id
                    }
                    if (books.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 48.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isOwnProfile) "You have no recipe books yet."
                                    else "${displayedUser.userName} has no recipe books.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        items(books) { book ->
                            RecipeBookCard(
                                book = book,
                                // RecipeBookCard exists in MainActivity.kt
                                onClick = {}
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun StatColumn(label: String, count: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "$count",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ProfileRecipeCard(recipe: Recipe, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = recipe.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = recipe.textContent,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun EditProfileDialog(
    currentBio: String,
    currentPictureUrl: String,
    onDismiss: () -> Unit,
    onConfirm: (bio: String, pictureUrl: String) -> Unit
) {
    var bio by remember { mutableStateOf(currentBio) }
    var pictureUrl by remember { mutableStateOf(currentPictureUrl) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profile") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Bio") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    supportingText = { Text("Tell others a bit about yourself") }
                )
                // do we want to have a link or open photo gallery?
                OutlinedTextField(
                    value = pictureUrl,
                    onValueChange = { pictureUrl = it },
                    label = { Text("Profile picture URL") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    supportingText = { Text("Paste a link to an image") }
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(bio, pictureUrl) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
