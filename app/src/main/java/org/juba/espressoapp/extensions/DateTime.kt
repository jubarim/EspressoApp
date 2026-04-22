package org.juba.espressoapp.extensions

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

const val UTC_TIME_ZONE = "UTC"

/**
 * Adjust a timestamp to the local device's timezone.
 *
 * @return The timestamp adjusted to the local timezone, in milliseconds.
 */
fun Long.epochToLocalTimeZoneConvertor(): Long {
    val epochCalendar = Calendar.getInstance()
    epochCalendar.timeZone = TimeZone.getTimeZone(UTC_TIME_ZONE)
    epochCalendar.timeInMillis = this

    val converterCalendar = Calendar.getInstance()
    converterCalendar.set(
        epochCalendar.get(Calendar.YEAR),
        epochCalendar.get(Calendar.MONTH),
        epochCalendar.get(Calendar.DATE),
        epochCalendar.get(Calendar.HOUR_OF_DAY),
        epochCalendar.get(Calendar.MINUTE),
    )

    converterCalendar.timeZone = TimeZone.getDefault()

    return converterCalendar.timeInMillis
}

/**
 * Converts a Unix timestamp in milliseconds to a localized date string using the device's locale.
 *
 * @param formatStyle The style of the output format (e.g., SHORT, MEDIUM, LONG). Defaults to [FormatStyle.MEDIUM].
 * @return A localized date string in the specified format.
 */
fun Long.toDeviceLocaleFormattedDate(
    formatStyle: FormatStyle = FormatStyle.MEDIUM,
): String {
    val date = Instant.ofEpochMilli(this)
        .atZone(ZoneId.of(UTC_TIME_ZONE))
        .toLocalDate()

    return date.format(
        DateTimeFormatter
            .ofLocalizedDate(formatStyle)
            .withLocale(Locale.getDefault())
    )
}
