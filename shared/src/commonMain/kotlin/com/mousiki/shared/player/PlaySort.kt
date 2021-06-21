package com.mousiki.shared.player

enum class PlaySort(val iconName: String) {
    RANDOM("ic_random"),
    LOOP_ONE("ic_repeat_one"),
    LOOP_ALL("ic_repeat"),
    SEQUENCE("ic_arrow_alt_to_right");

    fun next(): PlaySort = when {
        this == RANDOM -> LOOP_ONE
        this == LOOP_ONE -> LOOP_ALL
        this == LOOP_ALL -> SEQUENCE
        else -> RANDOM
    }

    companion object {
        fun toEnum(enumString: String): PlaySort {
            return try {
                valueOf(enumString)
            } catch (ex: Exception) {
                // For error cases
                LOOP_ALL
            }
        }
    }
}

