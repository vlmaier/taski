package org.vmaier.tidfl

import timber.log.Timber
import android.app.Application



/**
 * Created by Vladas Maier
 * on 13.05.2019
 * at 19:32
 */
class ApplicationController : Application() {

    override fun onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree());
        }
    }
}