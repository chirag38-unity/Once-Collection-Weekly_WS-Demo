package com.cr.oncecollectionweekly

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.request.CachePolicy
import coil3.request.crossfade
import coil3.util.DebugLogger
import com.cr.oncecollectionweekly.di.appModule
import io.github.aakira.napier.BuildConfig
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class OnceCollectionWeekly : Application(), SingletonImageLoader.Factory {

    override fun newImageLoader(context: PlatformContext): ImageLoader = ImageLoader.Builder(this)
        .crossfade(true)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .memoryCache{
            MemoryCache.Builder()
                .maxSizePercent(context = this, percent = 0.05)
                .strongReferencesEnabled(true)
                .build()
        }
        .also {
            if (BuildConfig.DEBUG) {
                it.logger(DebugLogger())
            }
        }
        .build()

    override fun onCreate() {
        Napier.base(DebugAntilog())
        super.onCreate()

        startKoin {
            androidContext(this@OnceCollectionWeekly)
            modules(
                appModule
            )
        }

    }

}