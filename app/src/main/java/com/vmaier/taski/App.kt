package com.vmaier.taski

import android.app.Application
import android.content.Context
import com.maltaisn.icondialog.pack.IconPack
import com.maltaisn.icondialog.pack.IconPackLoader
import com.maltaisn.iconpack.defaultpack.createDefaultIconPack
import timber.log.Timber
import timber.log.Timber.DebugTree
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Vladas Maier
 * on 13.05.2019
 * at 19:32
 */
class App : Application() {

    companion object {
        private const val DATE_FORMAT_PATTERN = "dd.MM.yyyy HH:mm"
        val dateFormat = SimpleDateFormat(DATE_FORMAT_PATTERN)

        lateinit var iconPack: IconPack

        fun loadIconPack(context: Context) {
            val loader = IconPackLoader(context)
            val iconPack = createDefaultIconPack(loader)
            iconPack.loadDrawables(loader.drawableLoader)
            this.iconPack = iconPack
        }
    }

    override fun onCreate() {
        super.onCreate()
        loadIconPack(this)
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }
}