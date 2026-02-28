package `is`.hi.hbv601g.h16.recipehub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import `is`.hi.hbv601g.h16.recipehub.domain.controller.SearchController
import `is`.hi.hbv601g.h16.recipehub.ui.theme.RecipeHubTheme

class SearchActivity : ComponentActivity() {
    private val searchController = SearchController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RecipeHubTheme {
                Scaffold { innerPadding ->
                    SearchScreen(
                        modifier = Modifier.padding(innerPadding),
                        controller = searchController
                    )
                }
            }
        }
    }
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



