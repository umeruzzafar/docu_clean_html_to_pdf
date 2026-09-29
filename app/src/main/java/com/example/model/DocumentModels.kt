package com.example.model

import android.graphics.Bitmap
import java.io.File

enum class DocumentFormat(val displayName: String, val badgeColorHex: Long) {
    RAW_PDF_STREAM("Raw PDF Stream (%PDF)", 0xFFDC2626),
    HTML_PORTAL_EXPORT("HTML / Portal Export", 0xFF2563EB),
    MARKDOWN_TEXT("Markdown / Structured Text", 0xFF059669),
    BASE64_PDF("Base64 Encoded Stream", 0xFF7C3AED),
    UNKNOWN("Unknown / Corrupted Data", 0xFF64748B)
}

enum class AppThemeMode(val title: String, val description: String) {
    LIGHT_CLEAN("Light Clean", "Crisp paper & high contrast reading"),
    MODERN_INDIGO("Modern Indigo", "Sleek professional palette with vibrant accents"),
    EXECUTIVE_SLATE("Executive Slate", "Deep dark workspace for nighttime document audit")
}

data class AttendanceRecord(
    val rollNo: String,
    val studentName: String,
    val courseCode: String,
    val totalClasses: Int,
    val attendedClasses: Int,
    val percentage: Float,
    val status: String
)

data class DocumentMetadata(
    val detectedFormat: DocumentFormat,
    val documentTitle: String,
    val universityOrOrg: String,
    val reportDate: String,
    val department: String,
    val pageCount: Int,
    val fileSizeBytes: Long,
    val pdfVersion: String? = null,
    val isRepaired: Boolean = false,
    val repairLog: List<String> = emptyList(),
    val diagnosticDetails: Map<String, String> = emptyMap()
)

data class DocumentInspectionResult(
    val metadata: DocumentMetadata,
    val cleanPdfFile: File? = null,
    val pageBitmaps: List<Bitmap> = emptyList(),
    val attendanceRecords: List<AttendanceRecord> = emptyList(),
    val rawCleanText: String = "",
    val htmlParsedPreview: String = "",
    val error: String? = null
)
