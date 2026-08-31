package com.phly101.library.integration.openlibrary

import com.phly101.library.mapper.BookMapper
import com.phly101.library.service.BookService
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Profile("import")
@Component
class OpenLibraryImportRunner(
    private val client: OpenLibraryClient,
    private val mapper: OpenLibraryMapper,
    private val bookService: BookService
) : CommandLineRunner {
    override fun run(vararg args: String) {
        val query = "subject:fantasy"

        println("Searching Open Library for query: \"$query\" (limit 100)...")

        val isbns = client.fetchIsbnsForQuery(query)

        println("Discovered ${isbns.size} ISBNs")

        if (isbns.isEmpty()) {
            println("No ISBNs found for query. Aborting import.")
            return
        }

        val booksData = client.fetchByIsbns(isbns)

        println("Fetched ${booksData.size} of ${isbns.size} requested books from Open Library")

        val missingIsbns = isbns - booksData.keys.map { it.removePrefix("ISBN:") }.toSet()

        if (missingIsbns.isNotEmpty()) {
            println("Not found on Open Library: $missingIsbns")
        }

        val request = mapper.toCreateBookRequests(booksData)

        val books = BookMapper.toEntities(request)

        val saved = bookService.addBooks(books)

        println("Import complete. Saved ${saved.size} books.")

    }
}