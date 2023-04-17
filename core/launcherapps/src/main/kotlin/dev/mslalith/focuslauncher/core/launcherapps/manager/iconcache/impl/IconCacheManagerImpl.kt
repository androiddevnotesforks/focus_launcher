package dev.mslalith.focuslauncher.core.launcherapps.manager.iconcache.impl

import android.content.Context
import android.graphics.drawable.Drawable
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.mslalith.focuslauncher.core.launcherapps.manager.iconcache.IconCacheManager
import dev.mslalith.focuslauncher.core.launcherapps.parser.IconPackXmlParser
import dev.mslalith.focuslauncher.core.model.IconPackType
import javax.inject.Inject

internal class IconCacheManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : IconCacheManager {

    private val iconCache = hashMapOf<String, Drawable>()
    private val iconPackCache = hashMapOf<String, IconPackXmlParser>()

    override fun clearCache() {
        iconCache.clear()
    }

    override fun iconPackFor(packageName: String): IconPackXmlParser = iconPackCache.getOrPut(packageName) {
        IconPackXmlParser(context = context, iconPackPackageName = packageName)
    }

    override fun iconFor(packageName: String, iconPackType: IconPackType): Drawable = when (iconPackType) {
        is IconPackType.Custom -> getCustomTypeIcon(iconPackPackageName = iconPackType.packageName, packageName = packageName)
        IconPackType.System -> getSystemTypeIcon(packageName = packageName)
    }

    private fun getSystemTypeIcon(packageName: String): Drawable = iconCache.getOrPut(packageName) {
        context.packageManager.getApplicationIcon(packageName)
    }

    private fun getCustomTypeIcon(iconPackPackageName: String, packageName: String): Drawable = iconCache.getOrPut(packageName) {
        val componentName = context.packageManager.getLaunchIntentForPackage(packageName)?.component
        val key = componentName?.toString() ?: packageName
        val iconPack = iconPackFor(packageName = iconPackPackageName)
        iconPack.drawableFor(componentName = key) ?: getSystemTypeIcon(packageName)
    }
}
