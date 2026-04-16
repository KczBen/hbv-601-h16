package `is`.hi.hbv601g.h16.recipehub.ui

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import `is`.hi.hbv601g.h16.recipehub.domain.service.AuthService
import `is`.hi.hbv601g.h16.recipehub.model.Comment
import `is`.hi.hbv601g.h16.recipehub.model.Recipe
import `is`.hi.hbv601g.h16.recipehub.model.User
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    recipe: Recipe,
    mainViewModel: MainViewModel,
    onBack: () -> Unit,
    onUserClick: (User) -> Unit = {}
) {
    val currentUser = AuthService.currentUser

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var commentText by remember { mutableStateOf("") }
    val selectedCommentImages = remember { mutableStateListOf<Pair<ByteArray, String>>() }

    val context = LocalContext.current
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }

    fun createImageUri(): Uri {
        val directory = File(context.cacheDir, "comment_images")
        if (!directory.exists()) directory.mkdirs()
        val file = File.createTempFile("comment_image_", ".jpg", directory)
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val inputStream = context.contentResolver.openInputStream(it)
            val bytes = inputStream?.readBytes()
            val type = context.contentResolver.getType(it) ?: "image/jpeg"
            if (bytes != null) {
                selectedCommentImages.add(bytes to type)
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tempImageUri?.let { uri ->
                val inputStream = context.contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes()
                if (bytes != null) {
                    selectedCommentImages.add(bytes to "image/jpeg")
                }
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            val uri = createImageUri()
            tempImageUri = uri
            cameraLauncher.launch(uri)
        }
    }

    // load comments when the screen first opens
    LaunchedEffect(recipe.id) {
        mainViewModel.fetchComments(recipe.id)
    }

    if (mainViewModel.showEditRecipeDialog) {
        EditRecipeDialog(
            recipe = recipe,
            onDismiss = { mainViewModel.showEditRecipeDialog = false },
            onConfirm = { updatedTitle, updatedText, updatedImages ->
                val updatedRecipe = recipe.copy(
                    title = updatedTitle,
                    textContent = updatedText,
                    images = updatedImages,
                    editDate = LocalDateTime.now()
                )
                mainViewModel.updateRecipe(updatedRecipe) { success ->
                    mainViewModel.showEditRecipeDialog = false
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            if (success) "Recipe updated!" else "Update failed, please try again"
                        )
                    }
                }
            }
        )
    }

    if (mainViewModel.showDeleteRecipeDialog) {
        AlertDialog(
            onDismissRequest = { mainViewModel.showDeleteRecipeDialog = false },
            title = { Text("Delete Recipe") },
            text = { Text("Are you sure you want to delete this recipe? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        mainViewModel.deleteRecipe(recipe.id) { success ->
                            mainViewModel.showDeleteRecipeDialog = false
                            if (success) {
                                onBack()
                            } else {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Failed to delete recipe")
                                }
                            }
                        }
                    },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { mainViewModel.showDeleteRecipeDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.clickable { onUserClick(recipe.owner) }
                ) {
                    ImageFromBytes(
                        data = recipe.owner.profilePictureData,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentDescription = "Profile picture"
                    )
                    Text(
                        text = "by ${recipe.owner.userName}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                if (recipe.images.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(recipe.images.toList()) { image ->
                            ImageFromBytes(
                                data = image.data,
                                modifier = Modifier
                                    .height(240.dp)
                                    .width(320.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentDescription = "Recipe image"
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

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
                        canDelete = currentUser?.id == comment.owner.id || currentUser?.isAdmin == true,
                        onEditConfirm = { newText, newImages ->
                            mainViewModel.updateComment(
                                recipeId = recipe.id,
                                commentId = comment.id!!,
                                newText = newText,
                                images = newImages
                            ) { success ->
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        if (success) "Comment updated!" else "Update failed, please try again"
                                    )
                                }
                            }
                        },
                        onDeleteConfirm = {
                            mainViewModel.deleteComment(
                                recipeId = recipe.id,
                                commentId = comment.id!!
                            ) { success ->
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        if (success) "Comment deleted!" else "Delete failed, please try again"
                                    )
                                }
                            }
                        },
                        onUserClick = onUserClick
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }

    if (currentUser != null) {
        Surface(
            modifier = Modifier.align(Alignment.BottomCenter),
            tonalElevation = 3.dp,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .imePadding()
            ) {
                if (selectedCommentImages.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(selectedCommentImages) { pair ->
                            val (data, _) = pair
                            Box {
                                ImageFromBytes(
                                    data = data,
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                )
                                IconButton(
                                    onClick = { selectedCommentImages.remove(pair) },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(20.dp)
                                        .background(
                                            MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                            CircleShape
                                        )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove",
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    var showImageSourceMenu by remember { mutableStateOf(false) }
                    Box {
                        IconButton(onClick = { showImageSourceMenu = true }) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = "Add image")
                        }
                        DropdownMenu(
                            expanded = showImageSourceMenu,
                            onDismissRequest = { showImageSourceMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Gallery") },
                                onClick = {
                                    showImageSourceMenu = false
                                    imagePickerLauncher.launch("image/*")
                                },
                                leadingIcon = { Icon(Icons.Default.AddPhotoAlternate, null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Camera") },
                                onClick = {
                                    showImageSourceMenu = false
                                    when (PackageManager.PERMISSION_GRANTED) {
                                        ContextCompat.checkSelfPermission(
                                            context,
                                            Manifest.permission.CAMERA
                                        ) -> {
                                            val uri = createImageUri()
                                            tempImageUri = uri
                                            cameraLauncher.launch(uri)
                                        }
                                        else -> {
                                            permissionLauncher.launch(Manifest.permission.CAMERA)
                                        }
                                    }
                                },
                                leadingIcon = { Icon(Icons.Default.CameraAlt, null) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        placeholder = { Text("Write a comment...") },
                        modifier = Modifier.weight(1f),
                        maxLines = 4,
                        shape = RoundedCornerShape(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (commentText.isNotBlank()) {
                                mainViewModel.createComment(
                                    recipe.id,
                                    commentText,
                                    selectedCommentImages.map { (data, type) ->
                                        Comment.CommentImage(data, type)
                                    }.toSet()
                                ) { success ->
                                    if (success) {
                                        commentText = ""
                                        selectedCommentImages.clear()
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Comment posted!")
                                        }
                                    } else {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Failed to post comment")
                                        }
                                    }
                                }
                            }
                        },
                        enabled = commentText.isNotBlank()
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                    }
                }
            }
        }
    }
    SnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier.align(Alignment.BottomCenter)
    )
}
}


@Composable
fun CommentItem(
    comment: Comment,
    canEdit: Boolean,
    canDelete: Boolean = false,
    onEditConfirm: (String, Set<Comment.CommentImage>) -> Unit,
    onDeleteConfirm: () -> Unit,
    onUserClick: (User) -> Unit = {}
) {
    var menuExpanded by remember { mutableStateOf(false) }

    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showEditDialog) {
        EditCommentDialog(
            currentText = comment.textContent,
            currentImages = comment.images,
            onDismiss = { showEditDialog = false },
            onConfirm = { newText, newImages ->
                showEditDialog = false
                onEditConfirm(newText, newImages)
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Comment") },
            text = { Text("Are you sure you want to delete this comment?") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteConfirm()
                    },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
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
            ImageFromBytes(
                data = comment.owner.profilePictureData,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentDescription = "Profile picture"
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = comment.owner.userName,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onUserClick(comment.owner) }
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = comment.textContent,
                    style = MaterialTheme.typography.bodyMedium
                )
                if (comment.images.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(comment.images.toList()) { image ->
                            ImageFromBytes(
                                data = image.data,
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentDescription = "Comment image"
                            )
                        }
                    }
                }
            }

            if (canEdit || canDelete) {
                Box {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Comment options")
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        if (canEdit) {
                            DropdownMenuItem(
                                text = { Text("Edit") },
                                onClick = {
                                    menuExpanded = false
                                    showEditDialog = true
                                }
                            )
                        }
                        if (canDelete) {
                            DropdownMenuItem(
                                text = { Text("Delete") },
                                onClick = {
                                    menuExpanded = false
                                    showDeleteDialog = true

                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EditCommentDialog(
    currentText: String,
    currentImages: Set<Comment.CommentImage>,
    onDismiss: () -> Unit,
    onConfirm: (String, Set<Comment.CommentImage>) -> Unit
) {
    var editedText by remember { mutableStateOf(currentText) }
    val editedImages = remember {
        mutableStateListOf<Pair<ByteArray, String>>().apply {
            addAll(currentImages.map { it.data to it.imageType })
        }
    }

    val context = LocalContext.current
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val bytes = context.contentResolver.openInputStream(it)?.readBytes()
            val type = context.contentResolver.getType(it) ?: "image/jpeg"
            if (bytes != null) {
                editedImages.add(bytes to type)
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tempImageUri?.let { uri ->
                val bytes = context.contentResolver.openInputStream(uri)?.readBytes()
                val type = context.contentResolver.getType(uri) ?: "image/jpeg"
                if (bytes != null) {
                    editedImages.add(bytes to type)
                }
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    fun createImageUri(): Uri {
        val file = File(context.cacheDir, "edit_comment_capture_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Comment") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = editedText,
                    onValueChange = { editedText = it },
                    label = { Text("Comment") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                if (editedImages.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(editedImages) { pair ->
                            val (data, _) = pair
                            Box {
                                ImageFromBytes(
                                    data = data,
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                )
                                IconButton(
                                    onClick = { editedImages.remove(pair) },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(20.dp)
                                        .background(
                                            MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                            CircleShape
                                        )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove",
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Row {
                    var showImageSourceMenu by remember { mutableStateOf(false) }
                    Box {
                        IconButton(onClick = { showImageSourceMenu = true }) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = "Add image")
                        }
                        DropdownMenu(
                            expanded = showImageSourceMenu,
                            onDismissRequest = { showImageSourceMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Gallery") },
                                onClick = {
                                    showImageSourceMenu = false
                                    imagePickerLauncher.launch("image/*")
                                },
                                leadingIcon = { Icon(Icons.Default.AddPhotoAlternate, null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Camera") },
                                onClick = {
                                    showImageSourceMenu = false
                                    when (PackageManager.PERMISSION_GRANTED) {
                                        ContextCompat.checkSelfPermission(
                                            context,
                                            Manifest.permission.CAMERA
                                        ) -> {
                                            val uri = createImageUri()
                                            tempImageUri = uri
                                            cameraLauncher.launch(uri)
                                        }
                                        else -> {
                                            permissionLauncher.launch(Manifest.permission.CAMERA)
                                        }
                                    }
                                },
                                leadingIcon = { Icon(Icons.Default.CameraAlt, null) }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        editedText,
                        editedImages.map { Comment.CommentImage(it.first, it.second) }.toSet()
                    )
                },
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
    onConfirm: (title: String, textContent: String, images: Set<Recipe.RecipeImage>) -> Unit
) {
    var editedTitle by remember { mutableStateOf(recipe.title) }
    var editedText by remember { mutableStateOf(recipe.textContent) }
    val editedImages = remember {
        mutableStateListOf<Recipe.RecipeImage>().apply { addAll(recipe.images) }
    }

    val context = LocalContext.current
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }

    fun createImageUri(): Uri {
        val directory = File(context.cacheDir, "recipe_images")
        if (!directory.exists()) directory.mkdirs()
        val file = File.createTempFile("recipe_image_", ".jpg", directory)
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val inputStream = context.contentResolver.openInputStream(it)
            val bytes = inputStream?.readBytes()
            val type = context.contentResolver.getType(it) ?: "image/jpeg"
            if (bytes != null) {
                editedImages.add(Recipe.RecipeImage(bytes, type))
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tempImageUri?.let { uri ->
                val inputStream = context.contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes()
                if (bytes != null) {
                    editedImages.add(Recipe.RecipeImage(bytes, "image/jpeg"))
                }
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            val uri = createImageUri()
            tempImageUri = uri
            cameraLauncher.launch(uri)
        }
    }

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

                Text("Images", style = MaterialTheme.typography.titleMedium)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(editedImages) { img ->
                        Box {
                            ImageFromBytes(
                                data = img.data,
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            IconButton(
                                onClick = { editedImages.remove(img) },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(24.dp)
                                    .background(
                                        MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                        CircleShape
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                    item {
                        var showImageSourceMenu by remember { mutableStateOf(false) }
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { showImageSourceMenu = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = "Add image"
                            )
                            DropdownMenu(
                                expanded = showImageSourceMenu,
                                onDismissRequest = { showImageSourceMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Gallery") },
                                    onClick = {
                                        showImageSourceMenu = false
                                        imagePickerLauncher.launch("image/*")
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.AddPhotoAlternate,
                                            null
                                        )
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Camera") },
                                    onClick = {
                                        showImageSourceMenu = false
                                        val permissionCheckResult = ContextCompat.checkSelfPermission(
                                            context,
                                            Manifest.permission.CAMERA
                                        )
                                        if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                                            val uri = createImageUri()
                                            tempImageUri = uri
                                            cameraLauncher.launch(uri)
                                        } else {
                                            permissionLauncher.launch(Manifest.permission.CAMERA)
                                        }
                                    },
                                    leadingIcon = { Icon(Icons.Default.CameraAlt, null) }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(editedTitle, editedText, editedImages.toSet()) },
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
