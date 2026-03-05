package `is`.hi.hbv601g.h16.recipehub.domain.service

import `is`.hi.hbv601g.h16.recipehub.domain.repository.MockRepository
import `is`.hi.hbv601g.h16.recipehub.model.Like
import `is`.hi.hbv601g.h16.recipehub.model.Recipe
import `is`.hi.hbv601g.h16.recipehub.model.User
import java.util.UUID

class LikeService {



    fun getLikesForRecipe(user: User, recipe: Recipe): Boolean{
        return MockRepository.likes.any {it.owner.id == user.id && it.recipe.id == recipe.id}
    }

    fun likeRecipe(user: User, recipe: Recipe): Boolean {
        val existing = MockRepository.likes.firstOrNull {
            it.owner.id == user.id && it.recipe.id == recipe.id
        }

        return if (existing != null) {
            MockRepository.likes.remove(existing)
            false // unlike
        } else {
            MockRepository.likes.add(
                Like(
                    id = UUID.randomUUID(),
                    owner = user,
                    recipe = recipe
                )
            )
            true // like
        }
    }

    fun unlikeRecipe(){

    }

    fun getLikeById(user: User): Set<UUID?> {
        return MockRepository.likes
            .filter { it.owner.id == user.id }
            .map { it.recipe.id }
            .toSet()
    }
}