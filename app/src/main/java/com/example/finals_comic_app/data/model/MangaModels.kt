package com.example.finals_comic_app.data.model

import com.google.gson.annotations.SerializedName

data class MangaResponse(
    @SerializedName("data") val data: List<Manga>,
    @SerializedName("pagination") val pagination: Pagination
)

data class Manga(
    @SerializedName("mal_id") val malId: Int,
    @SerializedName("url") val url: String,
    @SerializedName("images") val images: Images,
    @SerializedName("title") val title: String,
    @SerializedName("title_english") val titleEnglish: String?,
    @SerializedName("chapters") val chapters: Int?,
    @SerializedName("volumes") val volumes: Int?,
    @SerializedName("status") val status: String,
    @SerializedName("synopsis") val synopsis: String?,
    @SerializedName("type") val type: String?,
    @SerializedName("score") val score: Double?,
    @SerializedName("genres") val genres: List<Genre> = emptyList(),
    @SerializedName("authors") val authors: List<Author> = emptyList(),
    @SerializedName("relations") val relations: List<Relation>? = emptyList()
)

data class Relation(
    @SerializedName("relation") val relation: String,
    @SerializedName("entry") val entry: List<RelatedEntry>
)

data class RelatedEntry(
    @SerializedName("mal_id") val malId: Int,
    @SerializedName("type") val type: String,
    @SerializedName("name") val name: String,
    @SerializedName("url") val url: String
)

data class Genre(
    @SerializedName("mal_id") val malId: Int,
    @SerializedName("type") val type: String,
    @SerializedName("name") val name: String,
    @SerializedName("url") val url: String
)

data class Author(
    @SerializedName("mal_id") val malId: Int,
    @SerializedName("type") val type: String,
    @SerializedName("name") val name: String,
    @SerializedName("url") val url: String
)

data class Images(
    @SerializedName("jpg") val jpg: ImageUrl
)

data class ImageUrl(
    @SerializedName("image_url") val imageUrl: String,
    @SerializedName("small_image_url") val smallImageUrl: String,
    @SerializedName("large_image_url") val largeImageUrl: String
)

data class Pagination(
    @SerializedName("last_visible_page") val lastVisiblePage: Int,
    @SerializedName("has_next_page") val hasNextPage: Boolean,
    @SerializedName("current_page") val currentPage: Int
)
