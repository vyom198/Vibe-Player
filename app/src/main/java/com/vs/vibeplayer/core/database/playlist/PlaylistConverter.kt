package com.vs.vibeplayer.core.database.playlist

import androidx.room.TypeConverter


class LongListConverter {

    @TypeConverter
    fun listToString(values: List<Long>): String {
        return values.joinToString(",")
    }

    @TypeConverter
    fun stringToList(value: String): List<Long> {
        return value.split(",").map { it.toLong() }
    }

}