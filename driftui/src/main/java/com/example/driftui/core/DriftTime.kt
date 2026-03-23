package com.example.driftui.core

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object Time {
    val now: DriftDate get() = DriftDate(System.currentTimeMillis())
    fun from(millis: Long): DriftDate = DriftDate(millis)
}

class DriftDate(val millis: Long) {
    private val calendar = Calendar.getInstance().apply { timeInMillis = millis }
    private val date = Date(millis)

    // raw numbers
    val year: Int get() = calendar.get(Calendar.YEAR)
    val month: Int get() = calendar.get(Calendar.MONTH) + 1 // one to twelve
    val day: Int get() = calendar.get(Calendar.DAY_OF_MONTH)
    val hour12: Int get() = calendar.get(Calendar.HOUR).let { if (it == 0) 12 else it }
    val hour24: Int get() = calendar.get(Calendar.HOUR_OF_DAY)
    val minute: Int get() = calendar.get(Calendar.MINUTE)

    // text representations
    val dayName: String get() = format("EEEE")
    val dayNameShort: String get() = format("EEE")
    val monthName: String get() = format("MMMM")
    val monthNameShort: String get() = format("MMM")
    val amPm: String get() = format("a")

    // object conversion engine
    inner class TimeStringProvider {
        // invoked as function
        operator fun invoke(): String = format("hh:mm a")
        // accessed as property
        val to24HourFormat: String get() = format("HH:mm")
    }
    val timeString = TimeStringProvider()

    val dateString: String get() = format("dd MMM yyyy")

    inner class FullStringProvider {
        operator fun invoke(): String = format("EEEE, dd MMM yyyy • hh:mm a")
        val to24HourFormat: String get() = format("EEEE, dd MMM yyyy • HH:mm")
    }
    val fullString = FullStringProvider()

    // smart checks
    val isToday: Boolean get() = isSameDay(System.currentTimeMillis())
    val isYesterday: Boolean get() = isSameDay(System.currentTimeMillis() - 86_400_000L)

    // custom formatter
    fun format(pattern: String): String = SimpleDateFormat(pattern, Locale.getDefault()).format(date)

    private fun isSameDay(otherMillis: Long): Boolean {
        val other = Calendar.getInstance().apply { timeInMillis = otherMillis }
        return calendar.get(Calendar.YEAR) == other.get(Calendar.YEAR) &&
                calendar.get(Calendar.DAY_OF_YEAR) == other.get(Calendar.DAY_OF_YEAR)
    }
}