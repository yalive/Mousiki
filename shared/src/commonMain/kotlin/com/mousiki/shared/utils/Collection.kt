package com.mousiki.shared.utils

fun List<String>.getOrEmpty(index: Int): String {
    return getOrElse(index) { "" }
}

fun <T> List<T>.swapped(position1: Int, position2: Int): List<T> {
    if (position1 > size || position2 >= size || position1 < 0 || position2 < 0) return this
    val mutableList = toMutableList()
    val item1 = mutableList[position1]
    val item2 = mutableList[position2]
    mutableList[position1] = item2
    mutableList[position2] = item1
    return mutableList
}