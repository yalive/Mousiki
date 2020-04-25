package com.cas.common.extensions

import android.content.Intent

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/24/20.
 ***************************************
 */
fun Intent.dumpData(): String {
    val categoryData = "Category:$categories"
    val actionData = "Action:$action"
    val packageData = "Package:$`package`"
    val typeData = "Type:$type"
    val keySet = extras?.keySet()
    val extrasData = StringBuilder("Extras:\n")
    keySet?.forEach { key ->
        extrasData.append("- $key=${extras?.get(key)}")
    }
    return "$categoryData\n$actionData$packageData\n$typeData\n$extrasData"
}