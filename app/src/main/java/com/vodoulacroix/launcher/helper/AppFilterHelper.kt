package com.vodoulacroix.launcher.helper

import com.vodoulacroix.launcher.data.AppModel

interface AppFilterHelper {
    fun onAppFiltered(items:List<AppModel>)
}