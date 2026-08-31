package com.phly101.library.integration.openlibrary

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

class PublishDateFormatter {
    companion object {
        private val fullDateFormatters: List<DateTimeFormatter> = listOf(
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH)
        )
        private val yearMonthFormatters: List<DateTimeFormatter> = listOf(
            DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("yyyy-MM")
        )

        fun parsePublishDate(raw: String?): LocalDateTime {
            if (raw.isNullOrBlank()) return LocalDateTime.of(1, 1, 1, 0, 0)

            for (formatter in fullDateFormatters) {
                try {
                    return LocalDate.parse(raw, formatter).atStartOfDay()
                } catch (_: DateTimeParseException) {
                }
            }

            for (formatter in yearMonthFormatters) {
                try {
                    return YearMonth.parse(raw, formatter).atDay(1).atStartOfDay()
                } catch (_: DateTimeParseException) {
                }
            }

            val yearMatch = Regex("""\b(1[5-9]\d{2}|20\d{2})\b""").find(raw)
            if (yearMatch != null) {
                val year = yearMatch.value.toInt()
                return LocalDateTime.of(year, 1, 1, 0, 0)
            }

            return LocalDateTime.of(1, 1, 1, 0, 0)
        }
    }

}