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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import `is`.hi.hbv601g.h16.recipehub.domain.service.AuthService
import `is`.hi.hbv601g.h16.recipehub.domain.service.CategoryService
import `is`.hi.hbv601g.h16.recipehub.domain.service.RecipeService
import `is`.hi.hbv601g.h16.recipehub.model.Category
import `is`.hi.hbv601g.h16.recipehub.model.Recipe
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDateTime
import java.util.UUID

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
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val selectedImages = remember { mutableStateListOf<Pair<ByteArray, String>>() }
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }

    fun createImageUri(): Uri {
        val directory = File(context.cacheDir, "images")
        if (!directory.exists()) directory.mkdirs()
        val file = File.createTempFile("selected_image_", ".jpg", directory)
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
                selectedImages.add(bytes to type)
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
                    selectedImages.add(bytes to "image/jpeg")
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

    var allCategories by remember { mutableStateOf<List<Category>>(emptyList()) }
    LaunchedEffect(Unit) {
        allCategories = categoryService.getAllCategories(0, 100).toList()
    }

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
                .height(150.dp)
        )

        Column {
            Text("Images", style = MaterialTheme.typography.titleMedium)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(selectedImages) { pair ->
                    val (data, type) = pair
                    Box {
                        ImageFromBytes(
                            data = data,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        IconButton(
                            onClick = { selectedImages.remove(pair) },
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
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = "Add Image")

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
        }

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
                            categories = selectedCategories.toSet(),
                            images = selectedImages.map { (data, type) ->
                                Recipe.RecipeImage(data, type)
                            }.toSet()
                        )
                        scope.launch {
                            if (recipeService.createRecipe(newRecipe)) {
                                onPostCreated()
                            }
                        }
                    }
                },
                enabled = title.isNotBlank() && textContent.isNotBlank()
            ) {
                Text("Submit Post")
            }
        }
    }
}