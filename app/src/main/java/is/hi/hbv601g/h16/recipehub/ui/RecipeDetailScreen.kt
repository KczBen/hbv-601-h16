package `is`.hi.hbv601g.h16.recipehub.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import `is`.hi.hbv601g.h16.recipehub.domain.service.AuthService
import `is`.hi.hbv601g.h16.recipehub.model.Comment
import `is`.hi.hbv601g.h16.recipehub.model.Recipe
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    recipe: Recipe,
    mainViewModel: MainViewModel,
    onBack: () -> Unit
) {
    val currentUser = AuthService.currentUser
    val isOwner = currentUser?.id == recipe.owner.id

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showEditRecipeDialog by remember { mutableStateOf(false) }

    // load comments when the screen first opens
    LaunchedEffect(recipe.id) {
        recipe.id?.let { mainViewModel.fetchComments(it) }
    }

    if (showEditRecipeDialog) {
        EditRecipeDialog(
            recipe = recipe,
            onDismiss = { showEditRecipeDialog = false },
            onConfirm = { updatedTitle, updatedText ->
                val updatedRecipe = recipe.copy(
                    title = updatedTitle,
                    textContent = updatedText,
                    editDate = LocalDateTime.now()
                )
                mainViewModel.updateRecipe(updatedRecipe) { success ->
                    showEditRecipeDialog = false
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            if (success) "Recipe updated!" else "Update failed, please try again"
                        )
                    }
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(recipe.title, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (isOwner) {
                        IconButton(onClick = { showEditRecipeDialog = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit recipe")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "by ${recipe.owner.userName}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = recipe.textContent,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Comments",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            if (mainViewModel.comments.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No comments yet.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(mainViewModel.comments) { comment ->
                    CommentItem(
                        comment = comment,
                        // shows edit option only on the user's own comments
                        canEdit = currentUser?.id == comment.owner.id,
                        onEditConfirm = { newText ->
                            mainViewModel.updateComment(
                                recipeId = recipe.id!!,
                                commentId = comment.id!!,
                                newText = newText
                            ) { success ->
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        if (success) "Comment updated!" else "Update failed, please try again"
                                    )
                                }
                            }
                        }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun CommentItem(
    comment: Comment,
    canEdit: Boolean,
    onEditConfirm: (String) -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    var showEditDialog by remember { mutableStateOf(false) }

    if (showEditDialog) {
        EditCommentDialog(
            currentText = comment.textContent,
            onDismiss = { showEditDialog = false },
            onConfirm = { newText ->
                showEditDialog = false
                onEditConfirm(newText)
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = comment.owner.userName,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = comment.textContent,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (canEdit) {
                Box {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Comment options")
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            onClick = {
                                menuExpanded = false
                                showEditDialog = true
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EditCommentDialog(
    currentText: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var editedText by remember { mutableStateOf(currentText) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Comment") },
        text = {
            OutlinedTextField(
                value = editedText,
                onValueChange = { editedText = it },
                label = { Text("Comment") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(editedText) },
                enabled = editedText.isNotBlank()
            ) {
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

@Composable
fun EditRecipeDialog(
    recipe: Recipe,
    onDismiss: () -> Unit,
    onConfirm: (title: String, textContent: String) -> Unit
) {
    var editedTitle by remember { mutableStateOf(recipe.title) }
    var editedText by remember { mutableStateOf(recipe.textContent) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Recipe") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = editedTitle,
                    onValueChange = { editedTitle = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = { Text("${editedTitle.length}/${Recipe.MAX_TITLE_LENGTH}") }
                )
                OutlinedTextField(
                    value = editedText,
                    onValueChange = { editedText = it },
                    label = { Text("Content") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4,
                    supportingText = { Text("${editedText.length}/${Recipe.MAX_TEXT_LENGTH}") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(editedTitle, editedText) },
                enabled = editedTitle.isNotBlank()
                        && editedText.isNotBlank()
                        && editedTitle.length <= Recipe.MAX_TITLE_LENGTH
                        && editedText.length <= Recipe.MAX_TEXT_LENGTH
            ) {
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