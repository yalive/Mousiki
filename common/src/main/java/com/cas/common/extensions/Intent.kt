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
    val extrasData = StringBuilder("Extras:{")
    keySet?.forEach { key ->
        extrasData.append("$key= ${extras?.get(key)}, ")
    }
    extrasData.append("}")
    return "$categoryData, $actionData, $packageData, $typeData, $extrasData "
}

fun Intent.doOnExtrasTrue(booleanExtras: String, block: () -> Unit) {
    val isTrue = getBooleanExtra(booleanExtras, false)
    if (isTrue) {
        block()
    }
}

fun Intent?.bool(key: String): Boolean {
    return this?.getBooleanExtra(key, false) == true
}

fun Intent.fromDynamicLink(): Boolean {
    return hasExtra("com.google.firebase.dynamiclinks.DYNAMIC_LINK_DATA")
}