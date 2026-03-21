package `is`.hi.hbv601g.h16.recipehub

import android.app.Application
import `is`.hi.hbv601g.h16.recipehub.persistence.PersistenceModule

class RecipeHubApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        PersistenceModule.initialize(this)
    }
}
