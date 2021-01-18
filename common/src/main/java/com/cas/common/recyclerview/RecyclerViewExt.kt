package com.cas.common.recyclerview

import androidx.recyclerview.widget.RecyclerView

/**
 ***************************************
 * Created by Y.Abdelhadi on 5/23/20.
 ***************************************
 */

fun RecyclerView.itemsMarginDecorator(decorator: MarginItemDecoration) {
    for (i in 0 until itemDecorationCount) {
        val itemDecoration = getItemDecorationAt(0)
        if (itemDecoration is MarginItemDecoration) {
            removeItemDecoration(itemDecoration)
            break
        }
    }
    addItemDecoration(decorator)
}