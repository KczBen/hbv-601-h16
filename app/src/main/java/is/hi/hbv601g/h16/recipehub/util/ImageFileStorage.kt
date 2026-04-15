package `is`.hi.hbv601g.h16.recipehub.util

import `is`.hi.hbv601g.h16.recipehub.RecipeHubApplication
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object ImageFileStorage {
    private const val IMAGE_DIR = "stored_images"

    fun saveImage(data: ByteArray?): String? {
        if (data == null || data.isEmpty()) return null
        
        val context = RecipeHubApplication.getAppContext()
        val directory = File(context.filesDir, IMAGE_DIR)
        if (!directory.exists()) directory.mkdirs()

        val fileName = "img_${UUID.randomUUID()}.bin"
        val file = File(directory, fileName)
        
        return try {
            FileOutputStream(file).use { it.write(data) }
            file.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    fun loadImage(path: String?): ByteArray? {
        if (path == null) return null
        val file = File(path)
        return if (file.exists()) {
            try {
                file.readBytes()
            } catch (e: Exception) {
                null
            }
        } else null
    }
    
    fun deleteImage(path: String?) {
        if (path == null) return
        val file = File(path)
        if (file.exists()) file.delete()
    }
}
