package `is`.hi.hbv601g.h16.recipehub.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import `is`.hi.hbv601g.h16.recipehub.ui.theme.RecipeHubTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import `is`.hi.hbv601g.h16.recipehub.domain.service.AuthService
import `is`.hi.hbv601g.h16.recipehub.domain.service.CategoryService
import `is`.hi.hbv601g.h16.recipehub.domain.service.RecipeService
import `is`.hi.hbv601g.h16.recipehub.model.Category
import `is`.hi.hbv601g.h16.recipehub.model.Recipe
import java.time.LocalDateTime
import java.util.UUID

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RecipeHubTheme {
                RecipeHubApp()
            }
        }
    }
}

@PreviewScreenSizes
@Composable
fun RecipeHubApp(mainViewModel: MainViewModel = viewModel()) {
    val navController = androidx.navigation.compose.rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val context = LocalContext.current
    var showLoginDialog by remember { mutableStateOf(false) }

    if (showLoginDialog) {
        AlertDialog(
            onDismissRequest = { showLoginDialog = false },
            title = { Text("Login Required") },
            text = { Text("Please log in to create a post.") },
            confirmButton = {
                Button(onClick = {
                    showLoginDialog = false
                    context.startActivity(Intent(context, AuthActivity::class.java))
                }) {
                    Text("Login")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLoginDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach { destination ->
                item(
                    icon = { Icon(destination.icon, contentDescription = destination.label) },
                    label = { Text(destination.label) },
                    selected = currentRoute == destination.name,
                    onClick = {
                        navController.navigate(destination.name) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                AppHeader(
                    onProfileClick = {
                        if (!AuthService().isLoggedIn()) {
                            context.startActivity(Intent(context, AuthActivity::class.java))
                        }
                    }
                )
            },
            floatingActionButton = {
                if (currentRoute != "CREATE_POST") {
                    FloatingActionButton(onClick = {
                        if (AuthService().isLoggedIn()) {
                            navController.navigate("CREATE_POST")
                        } else {
                            showLoginDialog = true
                        }
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Post Recipe")
                    }
                }
            }
        ) { innerPadding ->
            androidx.navigation.compose.NavHost(
                navController = navController,
                startDestination = AppDestinations.HOME.name,
                modifier = Modifier.padding(innerPadding)
            ) {
                // feed
                composable(AppDestinations.HOME.name) {
                   FeedScreen(modifier = Modifier, service = mainViewModel.recipeService)
                }

                // search
                composable(AppDestinations.SEARCH.name) {
                    SearchScreen(modifier = Modifier, recipeService = mainViewModel.recipeService, categoryService = mainViewModel.categoryService)
                }

                // recipe books
                composable(AppDestinations.RECIPE_BOOKS.name) {
                    Text("Recipe Books Screen")
                }

                // create post
                composable("CREATE_POST") {
                    CreatePostScreen(
                        recipeService = mainViewModel.recipeService,
                        categoryService = mainViewModel.categoryService,
                        onPostCreated = {
                            // go back after posting
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
) {
    HOME("Home", Icons.Default.Home),
    SEARCH("Search", Icons.Default.Search),
    RECIPE_BOOKS("Recipe Books", Icons.Default.Bookmarks),
}

@Composable
fun FeedScreen(
    modifier: Modifier = Modifier,
    service: RecipeService
) {
    val recipes = service.getAllRecipes(0, 10)

    LazyColumn(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(recipes) {
            r -> FeedCard(recipe = r, onLikeClick = {}, onCommentClick = {}, onShareClick = {})
        }
    }
}

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    recipeService: RecipeService,
    categoryService: CategoryService
) {
    var categoryQuery by rememberSaveable { mutableStateOf("") }
    val selectedCategories = remember { mutableStateListOf<Category>() }
    var expanded by remember { mutableStateOf(false) }

    val allCategories = categoryService.getAllCategories(0, 100).toList()
    val filteredCategories = allCategories.filter {
        it.name.contains(categoryQuery, ignoreCase = true) && it !in selectedCategories
    }

    val results = remember(selectedCategories.size) {
        recipeService.getRecipeByCategory(selectedCategories.toSet())
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Search by Category", style = MaterialTheme.typography.headlineSmall)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            selectedCategories.forEach { category ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.clickable { selectedCategories.remove(category) }
                ) {
                    Text(
                        text = category.name,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }

        Box {
            OutlinedTextField(
                value = categoryQuery,
                onValueChange = {
                    categoryQuery = it
                    expanded = it.isNotEmpty()
                },
                label = { Text("Category") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            DropdownMenu(
                expanded = expanded && filteredCategories.isNotEmpty(),
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                filteredCategories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category.name) },
                        onClick = {
                            selectedCategories.add(category)
                            categoryQuery = ""
                            expanded = false
                        }
                    )
                }
            }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(results) { r ->
                FeedCard(recipe = r, onLikeClick = {}, onCommentClick = {}, onShareClick = {})
            }
        }
    }
}

@Composable
fun CreatePostScreen(
    recipeService: RecipeService,
    categoryService: CategoryService,
    onPostCreated: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var textContent by remember { mutableStateOf("") }
    var categoryQuery by remember { mutableStateOf("") }
    val selectedCategories = remember { mutableStateListOf<Category>() }
    var expanded by remember { mutableStateOf(false) }

    val allCategories = categoryService.getAllCategories(0, 100).toList()
    val filteredCategories = allCategories.filter {
        it.name.contains(categoryQuery, ignoreCase = true) && it !in selectedCategories
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Create a New Recipe", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = textContent,
            onValueChange = { textContent = it },
            label = { Text("Recipe Body") },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )

        Column {
            Text("Categories", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                selectedCategories.forEach { category ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.clickable { selectedCategories.remove(category) }
                    ) {
                        Text(
                            text = category.name,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }

            Box {
                OutlinedTextField(
                    value = categoryQuery,
                    onValueChange = {
                        categoryQuery = it
                        expanded = it.isNotEmpty()
                    },
                    label = { Text("Add Category") },
                    modifier = Modifier.fillMaxWidth()
                )
                DropdownMenu(
                    expanded = expanded && filteredCategories.isNotEmpty(),
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    filteredCategories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name) },
                            onClick = {
                                selectedCategories.add(category)
                                categoryQuery = ""
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onPostCreated) {
                Text("Cancel")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    val user = AuthService.currentUser
                    if (user != null && title.isNotBlank() && textContent.isNotBlank()) {
                        val newRecipe = Recipe(
                            id = UUID.randomUUID(),
                            owner = user,
                            title = title,
                            textContent = textContent,
                            creationDate = LocalDateTime.now(),
                            editDate = LocalDateTime.now(),
                            rating = 0f,
                            ratingCount = 0,
                            categories = selectedCategories.toSet()
                        )
                        recipeService.createRecipe(newRecipe)
                        onPostCreated()
                    }
                },
                enabled = title.isNotBlank() && textContent.isNotBlank()
            ) {
                Text("Submit Post")
            }
        }
    }
}

@Composable
fun FeedCard(
    modifier: Modifier = Modifier,
    recipe: Recipe,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onShareClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // user info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
                Column {
                    Text(
                        text = recipe.owner.userName,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = recipe.creationDate.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // post date and tags
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                recipe.categories.forEach { category ->
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = category.name,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            // rating (if we keep those)
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(text = "${recipe.rating}/5.0", style = MaterialTheme.typography.labelSmall)
                Icon(imageVector = Icons.Filled.Star, contentDescription = "Rating", modifier = Modifier.size(12.dp))
            }

            // post body
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = recipe.textContent,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ActionButton(icon = Icons.Outlined.FavoriteBorder, label ="Like", onClick = onLikeClick)
                ActionButton(icon = Icons.Outlined.ChatBubbleOutline, label = "Comment", onClick = onCommentClick)
                ActionButton(icon = Icons.Outlined.BookmarkBorder, label = "Save", onClick = onShareClick)
            }
        }
    }
}

@Composable
private fun ActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    TextButton(onClick = onClick) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, style = MaterialTheme.typography.labelMedium)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppHeader(
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {},
        actions = {
            IconButton(onClick = onProfileClick) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profile"
                )
            }
        },
        modifier = modifier
    )
}
