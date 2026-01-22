package com.gokanaz.kanaznotes

import android.app.Application
import com.tencent.mmkv.MMKV

class KanazNotesApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
    }
}
