package `is`.hi.hbv601g.h16.recipehub

import android.app.Application
import android.content.Context
import `is`.hi.hbv601g.h16.recipehub.persistence.PersistenceModule

class RecipeHubApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        PersistenceModule.initialize(this)
    }

    companion object {
        private var instance: RecipeHubApplication? = null

        fun getAppContext(): Context {
            return instance!!.applicationContext
        }
    }
}
