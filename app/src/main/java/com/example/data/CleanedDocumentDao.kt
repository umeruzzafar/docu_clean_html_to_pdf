package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CleanedDocumentDao {
    @Query("SELECT * FROM cleaned_documents ORDER BY timestamp DESC")
    fun getAllDocuments(): Flow<List<CleanedDocumentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(document: CleanedDocumentEntity): Long

    @Query("DELETE FROM cleaned_documents WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM cleaned_documents")
    suspend fun clearAll()
}
