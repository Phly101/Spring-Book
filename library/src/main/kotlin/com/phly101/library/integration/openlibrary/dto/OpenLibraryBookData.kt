package com.phly101.library.integration.openlibrary.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class OpenLibraryBookData(
    val title: String,
    val authors: List<OpenLibraryAuthor>? = null,
    val cover: OpenLibraryCover?,
    @JsonProperty("publish_date")
    val publishDate: String? = null,
    @JsonProperty("number_of_pages")
    val numberOfPages: Int? = null,


    )