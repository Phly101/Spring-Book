package com.phly101.library.integration.openlibrary

import com.phly101.library.integration.openlibrary.dto.OpenLibraryBookData
import com.phly101.library.integration.openlibrary.dto.SearchResponse
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class OpenLibraryClient(
    builder: RestClient.Builder
) {
    private val restClient =
        builder.baseUrl("https://openlibrary.org")
            .defaultHeader("User-Agent", "Spring-Book/1.0 (github.com/Phly101)")
            .build()

    companion object {
        private const val CHUNK_SIZE = 20
    }

    fun fetchIsbnsForQuery(query: String, limit: Int = 100): List<String> {
        val response = restClient.get()
            .uri("/search.json?q={query}&fields=isbn&limit={limit}", query, limit)
            .retrieve()
            .body(SearchResponse::class.java)

        return response?.docs
            ?.flatMap { it.isbn ?: emptyList() }
            ?.distinct()
            ?.take(limit) ?: emptyList()
    }


    fun fetchByIsbns(isbns: List<String>): Map<String, OpenLibraryBookData> {
        val result = mutableMapOf<String, OpenLibraryBookData>()
        isbns.chunked(CHUNK_SIZE).forEach { chunk ->
            val bibkeys = chunk.joinToString(",") { "ISBN:$it" }
            val response = restClient.get()
                .uri("/api/books?bibkeys={bibkeys}&format=json&jscmd=data", bibkeys)
                .retrieve().body(object : ParameterizedTypeReference<Map<String, OpenLibraryBookData>>() {})
            result.putAll(response ?: emptyMap())
        }
        return result
    }
}