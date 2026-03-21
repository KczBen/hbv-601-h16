package `is`.hi.hbv601g.h16.recipehub.persistence

import `is`.hi.hbv601g.h16.recipehub.model.Category
import `is`.hi.hbv601g.h16.recipehub.model.Recipe
import `is`.hi.hbv601g.h16.recipehub.model.RecipeBook
import `is`.hi.hbv601g.h16.recipehub.model.User

fun UserEntity.toModel(): User {
    return User(
        id = id,
        userName = userName,
        email = email,
        passwordHash = passwordHash,
        profilePictureURL = profilePictureURL,
        bio = bio,
        isBanned = isBanned,
        isAdmin = isAdmin,
        isLoggedIn = isLoggedIn
    )
}

fun User.toEntity(): UserEntity {
    return UserEntity(
        id = id,
        userName = userName,
        email = email,
        passwordHash = passwordHash,
        profilePictureURL = profilePictureURL,
        bio = bio,
        isBanned = isBanned,
        isAdmin = isAdmin,
        isLoggedIn = isLoggedIn
    )
}

fun RecipeEntity.toModel(owner: User, categories: Set<Category>): Recipe {
    return Recipe(
        id = id,
        owner = owner,
        title = title,
        textContent = textContent,
        images = images,
        creationDate = creationDate,
        editDate = editDate,
        rating = rating,
        ratingCount = ratingCount,
        categories = categories
    )
}

fun Recipe.toEntity(): RecipeEntity {
    return RecipeEntity(
        id = id,
        ownerId = owner.id,
        title = title,
        textContent = textContent,
        images = images,
        creationDate = creationDate,
        editDate = editDate,
        rating = rating,
        ratingCount = ratingCount
    )
}

fun CategoryEntity.toModel(): Category {
    return Category(
        id = id,
        name = name
    )
}

fun Category.toEntity(): CategoryEntity {
    return CategoryEntity(
        id = id,
        name = name
    )
}

fun RecipeBookEntity.toModel(owner: User, recipes: Set<Recipe>): RecipeBook {
    return RecipeBook(
        id = id,
        owner = owner,
        name = name,
        recipes = recipes,
        isPublic = isPublic
    )
}

fun RecipeBook.toEntity(): RecipeBookEntity {
    return RecipeBookEntity(
        id = id,
        ownerId = owner.id,
        name = name,
        isPublic = isPublic
    )
}
