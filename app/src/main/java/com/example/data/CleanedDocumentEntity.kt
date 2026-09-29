package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cleaned_documents")
data class CleanedDocumentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val format: String,
    val originalLength: Long,
    val pageCount: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val previewSnippet: String,
    val filePath: String
)
