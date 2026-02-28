package `is`.hi.hbv601g.h16.recipehub.domain.controller

import `is`.hi.hbv601g.h16.recipehub.model.User
import java.util.UUID


class RecipeBookController {

    // dummmy data

    private val currentUser = User(
        id = UUID.randomUUID(),
        userName = "Bobby Tables",
        email = "bobby@example.com",
        passwordHash = "password123"
    )




}