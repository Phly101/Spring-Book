package com.phly101.library.integration.openlibrary

import com.phly101.library.dto.book.CreateBookRequest
import com.phly101.library.integration.openlibrary.dto.OpenLibraryBookData
import org.springframework.stereotype.Component

@Component
class OpenLibraryMapper {

    fun toCreateBookRequests(data: Map<String, OpenLibraryBookData>): List<CreateBookRequest> {
        val dedupedEntries = data.entries.distinctBy { it.value.title.trim().lowercase() }

        return dedupedEntries.map { (key, value) ->
            CreateBookRequest(
                value.title,
                value.authors?.joinToString(", ") { it.name } ?: "Unknown Author",
                key.removePrefix("ISBN:"),
                PublishDateFormatter.parsePublishDate(value.publishDate),
                value.numberOfPages ?: 0,
                value.cover?.medium ?: value.cover?.small
            )
        }
    }
}