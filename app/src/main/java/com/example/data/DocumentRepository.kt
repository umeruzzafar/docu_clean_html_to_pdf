package com.example.data

import kotlinx.coroutines.flow.Flow

class DocumentRepository(private val dao: CleanedDocumentDao) {
    val allDocuments: Flow<List<CleanedDocumentEntity>> = dao.getAllDocuments()

    suspend fun saveDocument(document: CleanedDocumentEntity): Long {
        return dao.insert(document)
    }

    suspend fun deleteDocument(id: Long) {
        dao.deleteById(id)
    }

    suspend fun clearHistory() {
        dao.clearAll()
    }
}
