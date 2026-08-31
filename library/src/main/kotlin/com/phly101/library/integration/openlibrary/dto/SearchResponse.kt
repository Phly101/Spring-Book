package com.phly101.library.integration.openlibrary.dto

data class SearchResponse(
    val docs: List<SearchDoc> = emptyList()
)