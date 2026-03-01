package `is`.hi.hbv601g.h16.recipehub.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import `is`.hi.hbv601g.h16.recipehub.ui.theme.RecipeHubTheme
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import `is`.hi.hbv601g.h16.recipehub.domain.controller.SearchController

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
                    onProfileClick = {},
                    onSettingsClick = {}
                )
            },
            floatingActionButton = {
                if (currentRoute != "CREATE_POST") {
                    FloatingActionButton(onClick = {
                        navController.navigate("CREATE_POST")
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
                    FeedCard(
                        username = "Bobby Tables",
                        rating = 4.5f,
                        avatarUrl = "unused",
                        publishDate = "Today",
                        tag = "Dessert",
                        title = "Wonderful Chocolate Cake",
                        excerpt = "Here is my favourite chocolate cake recipe...",
                        onLikeClick = {},
                        onCommentClick = {},
                        onShareClick = {}
                    )
                }

                // search
                composable(AppDestinations.SEARCH.name) {
                    SearchScreen(modifier = Modifier.padding(innerPadding), controller = mainViewModel.searchController)
                }

                // recipe books
                composable(AppDestinations.RECIPE_BOOKS.name) {
                    Text("Recipe Books Screen")
                }

                // create post
                composable("CREATE_POST") {
                    CreatePostScreen(onPostCreated = {
                        // go back after posting
                        navController.popBackStack()
                    })
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
private fun SearchScreen(
    modifier: Modifier = Modifier,
    controller: SearchController
) {
    var query by rememberSaveable { mutableStateOf("") }
    var results by remember { mutableStateOf(controller.search("")) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Search", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                results = controller.search(query)
            },
            label = { Text("Recipe") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(results) { r ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(r.title, style = MaterialTheme.typography.titleMedium)
                        Text("by ${r.owner}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
fun CreatePostScreen(onPostCreated: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Create a New Recipe", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.size(16.dp))

        // placeholder stuff
        TextButton(onClick = onPostCreated) {
            Text("Submit Post")
        }

        TextButton(onClick = onPostCreated) {
            Text("Cancel")
        }
    }
}

@Composable
fun FeedCard(
    modifier: Modifier = Modifier,
    username: String,
    rating: Float,
    avatarUrl: String,
    publishDate: String,
    tag: String,
    title: String,
    excerpt: String,
    imageUrl: String? = null,
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
                        text = username,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = publishDate,
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
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = tag,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // rating (if we keep those)
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(text = "$rating/5", style = MaterialTheme.typography.labelSmall)
                Icon(imageVector = Icons.Filled.Star, contentDescription = "Rating", modifier = Modifier.size(12.dp))
            }

            // post body
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = excerpt,
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
                ActionButton(icon = Icons.Outlined.FavoriteBorder, label = "Like", onClick = onLikeClick)
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
    onSettingsClick: () -> Unit,
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
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings"
                )
            }
        },
        modifier = modifier
    )
}