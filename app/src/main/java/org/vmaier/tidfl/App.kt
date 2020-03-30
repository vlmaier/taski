package org.vmaier.tidfl

import android.app.Application
import android.content.Context
import com.maltaisn.icondialog.pack.IconPack
import com.maltaisn.icondialog.pack.IconPackLoader
import com.maltaisn.iconpack.defaultpack.createDefaultIconPack


/**
 * Created by Vladas Maier
 * on 13.05.2019
 * at 19:32
 */
class App : Application() {

    companion object {
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
    }
}