package `is`.hi.hbv601g.h16.recipehub.domain.repository

import `is`.hi.hbv601g.h16.recipehub.model.*
import java.time.LocalDateTime
import java.util.UUID

object MockRepository {
    val users = mutableSetOf<User>()
    val recipes = mutableSetOf<Recipe>()
    val recipeBooks = mutableSetOf<RecipeBook>()
    val likes = mutableSetOf<Like>()
    val ratings = mutableSetOf<Rating>()
    val comments = mutableSetOf<Comment>()
    val categories = mutableSetOf<Category>()

    init {
        val user1 = User(
            id = UUID.randomUUID(),
            userName = "Alice",
            email = "alice@example.com",
            passwordHash = "password123",
            bio = "I love cooking!"
        )
        val user2 = User(
            id = UUID.randomUUID(),
            userName = "Bob",
            email = "bob@example.com",
            passwordHash = "securePass",
            bio = "Baking is my passion."
        )

        users.add(user1)
        users.add(user2)

        val cat1 = Category(id = UUID.randomUUID(), name = "Breakfast")
        val cat2 = Category(id = UUID.randomUUID(), name = "Dessert")
        categories.add(cat1)
        categories.add(cat2)

        val recipe1 = Recipe(
            id = UUID.randomUUID(),
            owner = user1,
            title = "Pancakes",
            textContent = "Fluffy pancakes recipe...",
            creationDate = LocalDateTime.now(),
            editDate = LocalDateTime.now(),
            rating = 4.5f,
            ratingCount = 10L,
            categories = setOf(cat1)
        )
        val recipe2 = Recipe(
            id = UUID.randomUUID(),
            owner = user2,
            title = "Chocolate Cake",
            textContent = "Rich chocolate cake recipe...",
            creationDate = LocalDateTime.now(),
            editDate = LocalDateTime.now(),
            rating = 5.0f,
            ratingCount = 5L,
            categories = setOf(cat2)
        )

        recipes.add(recipe1)
        recipes.add(recipe2)

        val book1 = RecipeBook(
            id = UUID.randomUUID(),
            owner = user1,
            name = "My Favorites",
            recipes = setOf(recipe1, recipe2),
            isPublic = true
        )
        recipeBooks.add(book1)

        val like1 = Like(id = UUID.randomUUID(), owner = user2, recipe = recipe1)
        likes.add(like1)

        val rating1 = Rating(id = UUID.randomUUID(), owner = user2, recipe = recipe1, value = 5)
        ratings.add(rating1)

        val comment1 = Comment(
            id = UUID.randomUUID(),
            owner = user2,
            recipe = recipe1,
            creationDate = LocalDateTime.now(),
            editDate = LocalDateTime.now(),
            textContent = "Great recipe!",
            images = emptySet()
        )
        comments.add(comment1)
    }
}
