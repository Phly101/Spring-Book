package com.phly101.library.integration.openlibrary.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class SearchDoc(
    @JsonProperty("isbn")
    val isbn: List<String>? = emptyList()
)