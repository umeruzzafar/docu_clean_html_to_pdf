package com.example.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintManager
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.CleanedDocumentEntity
import com.example.data.DocumentRepository
import com.example.model.AppThemeMode
import com.example.model.DocumentFormat
import com.example.model.DocumentInspectionResult
import com.example.parser.DocuCleanEngine
import com.example.parser.PresetSample
import com.example.parser.SampleDocuments
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

data class DownloadedFileInfo(
    val fileName: String,
    val fileSizeBytes: Long,
    val localFile: File,
    val destinationUri: Uri?
)

class DocuCleanViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = DocumentRepository(
        AppDatabase.getInstance(application).cleanedDocumentDao()
    )

    val recentDocuments: StateFlow<List<CleanedDocumentEntity>> = repository.allDocuments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _detectedFormat = MutableStateFlow(DocumentFormat.RAW_PDF_STREAM)
    val detectedFormat: StateFlow<DocumentFormat> = _detectedFormat.asStateFlow()

    private val _inspectionResult = MutableStateFlow<DocumentInspectionResult?>(null)
    val inspectionResult: StateFlow<DocumentInspectionResult?> = _inspectionResult.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    private val _themeMode = MutableStateFlow(AppThemeMode.LIGHT_CLEAN)
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    private val _zoomPercent = MutableStateFlow(100)
    val zoomPercent: StateFlow<Int> = _zoomPercent.asStateFlow()

    private val _fitToWidth = MutableStateFlow(true)
    val fitToWidth: StateFlow<Boolean> = _fitToWidth.asStateFlow()

    private val _selectedPageIndex = MutableStateFlow(0)
    val selectedPageIndex: StateFlow<Int> = _selectedPageIndex.asStateFlow()

    private val _activeTab = MutableStateFlow(0) // 0: Input/Inspector, 1: Live Paper Canvas
    val activeTab: StateFlow<Int> = _activeTab.asStateFlow()

    private val _importedFileName = MutableStateFlow<String?>(null)
    val importedFileName: StateFlow<String?> = _importedFileName.asStateFlow()

    private val _recentlyDownloadedInfo = MutableStateFlow<DownloadedFileInfo?>(null)
    val recentlyDownloadedInfo: StateFlow<DownloadedFileInfo?> = _recentlyDownloadedInfo.asStateFlow()

    private val _showOpenDownloadDialog = MutableStateFlow(false)
    val showOpenDownloadDialog: StateFlow<Boolean> = _showOpenDownloadDialog.asStateFlow()

    private val _autoOpenEnabled = MutableStateFlow(true)
    val autoOpenEnabled: StateFlow<Boolean> = _autoOpenEnabled.asStateFlow()

    init {
        // Load initial realistic preset (NED University Attendance with Corrupted %PDF-1.4 stream)
        val defaultPreset = SampleDocuments.getPresets(application).first()
        loadPreset(defaultPreset)
    }

    fun onInputTextChanged(text: String) {
        _inputText.value = text
        _importedFileName.value = null
        _detectedFormat.value = DocuCleanEngine.detectFormat(text.toByteArray(), text)
    }

    fun loadPreset(preset: PresetSample) {
        _inputText.value = preset.sampleText
        _importedFileName.value = preset.title
        _detectedFormat.value = DocuCleanEngine.detectFormat(preset.sampleText.toByteArray(), preset.sampleText)
        processCurrentDocument()
    }

    fun clearInput() {
        _inputText.value = ""
        _importedFileName.value = null
        _inspectionResult.value = null
        _statusMessage.value = "Input workspace cleared."
    }

    fun setZoom(percent: Int) {
        _zoomPercent.value = percent.coerceIn(50, 200)
        _fitToWidth.value = false
    }

    fun zoomIn() {
        setZoom(_zoomPercent.value + 15)
    }

    fun zoomOut() {
        setZoom(_zoomPercent.value - 15)
    }

    fun resetZoom() {
        _zoomPercent.value = 100
        _fitToWidth.value = true
    }

    fun toggleFitToWidth() {
        _fitToWidth.value = !_fitToWidth.value
    }

    fun setTheme(theme: AppThemeMode) {
        _themeMode.value = theme
    }

    fun setActiveTab(tab: Int) {
        _activeTab.value = tab
    }

    fun setSelectedPage(index: Int) {
        val total = _inspectionResult.value?.metadata?.pageCount ?: 1
        _selectedPageIndex.value = index.coerceIn(0, total - 1)
    }

    fun processCurrentDocument(autoSwitchToPreview: Boolean = false) {
        val text = _inputText.value
        if (text.isBlank()) {
            _statusMessage.value = "Please paste or import a document stream."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _statusMessage.value = "Inspecting stream & analyzing headers..."

            try {
                val result = withContext(Dispatchers.Default) {
                    DocuCleanEngine.processDocument(
                        inputBytes = null,
                        inputText = text,
                        context = getApplication()
                    )
                }

                _inspectionResult.value = result
                _detectedFormat.value = result.metadata.detectedFormat
                _selectedPageIndex.value = 0

                // Save to Room history
                if (result.cleanPdfFile != null) {
                    val entity = CleanedDocumentEntity(
                        title = result.metadata.documentTitle,
                        format = result.metadata.detectedFormat.displayName,
                        originalLength = result.metadata.fileSizeBytes,
                        pageCount = result.metadata.pageCount,
                        previewSnippet = text.take(150),
                        filePath = result.cleanPdfFile.absolutePath
                    )
                    repository.saveDocument(entity)
                }

                _statusMessage.value = "Document verified: ${result.metadata.pageCount} page(s) ready."
                if (autoSwitchToPreview) {
                    _activeTab.value = 1
                }
            } catch (e: Exception) {
                _statusMessage.value = "Processing warning: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun handleFileUri(context: Context, uri: Uri) {
        viewModelScope.launch {
            _isLoading.value = true
            _statusMessage.value = "Reading imported stream..."

            try {
                var fileName = "Imported_Document"
                context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    val nameIdx = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (nameIdx >= 0 && cursor.moveToFirst()) {
                        fileName = cursor.getString(nameIdx)
                    }
                }
                _importedFileName.value = fileName

                val bytes = withContext(Dispatchers.IO) {
                    context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                }

                if (bytes != null && bytes.isNotEmpty()) {
                    val text = String(bytes, Charsets.UTF_8)
                    _inputText.value = text
                    _detectedFormat.value = DocuCleanEngine.detectFormat(bytes, text)

                    val result = withContext(Dispatchers.Default) {
                        DocuCleanEngine.processDocument(
                            inputBytes = bytes,
                            inputText = text,
                            context = getApplication()
                        )
                    }
                    _inspectionResult.value = result
                    _activeTab.value = 1 // Switch to live paper preview

                    if (result.cleanPdfFile != null) {
                        repository.saveDocument(
                            CleanedDocumentEntity(
                                title = fileName,
                                format = result.metadata.detectedFormat.displayName,
                                originalLength = bytes.size.toLong(),
                                pageCount = result.metadata.pageCount,
                                previewSnippet = text.take(150),
                                filePath = result.cleanPdfFile.absolutePath
                            )
                        )
                    }
                    _statusMessage.value = "Imported $fileName successfully."
                }
            } catch (e: Exception) {
                _statusMessage.value = "Import error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadHistoryDocument(entity: CleanedDocumentEntity) {
        val file = File(entity.filePath)
        if (file.exists()) {
            viewModelScope.launch {
                _isLoading.value = true
                _importedFileName.value = entity.title
                try {
                    val bytes = withContext(Dispatchers.IO) { file.readBytes() }
                    val text = String(bytes, Charsets.UTF_8)
                    _inputText.value = text
                    val result = withContext(Dispatchers.Default) {
                        DocuCleanEngine.processDocument(bytes, text, getApplication())
                    }
                    _inspectionResult.value = result
                    _activeTab.value = 1
                    _statusMessage.value = "Loaded ${entity.title}"
                } catch (e: Exception) {
                    _statusMessage.value = "Could not reload history: ${e.message}"
                } finally {
                    _isLoading.value = false
                }
            }
        } else {
            _statusMessage.value = "Saved file is no longer in cache."
        }
    }

    fun deleteHistory(id: Long) {
        viewModelScope.launch {
            repository.deleteDocument(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    fun exportPdfToUri(context: Context, destinationUri: Uri) {
        val cleanFile = _inspectionResult.value?.cleanPdfFile ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                context.contentResolver.openOutputStream(destinationUri)?.use { out ->
                    FileInputStream(cleanFile).use { input ->
                        input.copyTo(out)
                    }
                }

                val docTitle = _inspectionResult.value?.metadata?.documentTitle ?: "Cleaned_Report.pdf"
                val fileInfo = DownloadedFileInfo(
                    fileName = docTitle,
                    fileSizeBytes = cleanFile.length(),
                    localFile = cleanFile,
                    destinationUri = destinationUri
                )
                _recentlyDownloadedInfo.value = fileInfo
                _showOpenDownloadDialog.value = true
                _statusMessage.value = "PDF exported and saved successfully!"

                // Auto-open if enabled
                if (_autoOpenEnabled.value) {
                    withContext(Dispatchers.Main) {
                        openRecentlyDownloaded(context)
                    }
                }
            } catch (e: Exception) {
                _statusMessage.value = "Export failed: ${e.message}"
            }
        }
    }

    fun openRecentlyDownloaded(context: Context) {
        val fileInfo = _recentlyDownloadedInfo.value
        val cleanFile = fileInfo?.localFile ?: _inspectionResult.value?.cleanPdfFile ?: return

        try {
            val fileUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                cleanFile
            )

            val openIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(fileUri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(openIntent, "Open Cleaned PDF With"))
        } catch (e: Exception) {
            _statusMessage.value = "Could not launch PDF viewer: ${e.message}"
        }
    }

    fun dismissDownloadDialog() {
        _showOpenDownloadDialog.value = false
    }

    fun toggleAutoOpen() {
        _autoOpenEnabled.value = !_autoOpenEnabled.value
    }

    fun sharePdf(context: Context) {
        val cleanFile = _inspectionResult.value?.cleanPdfFile
        if (cleanFile == null || !cleanFile.exists()) {
            _statusMessage.value = "No clean PDF document to share."
            return
        }

        try {
            val fileUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                cleanFile
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, fileUri)
                putExtra(Intent.EXTRA_SUBJECT, _inspectionResult.value?.metadata?.documentTitle ?: "Cleaned PDF")
                putExtra(Intent.EXTRA_TEXT, "Here is the cleaned, verified official PDF document generated by DocuClean PDF.")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share Clean PDF"))
        } catch (e: Exception) {
            _statusMessage.value = "Sharing error: ${e.message}"
        }
    }

    fun printDocument(context: Context) {
        val cleanFile = _inspectionResult.value?.cleanPdfFile
        if (cleanFile == null || !cleanFile.exists()) {
            _statusMessage.value = "No clean PDF document to print."
            return
        }

        try {
            val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
            val jobName = _inspectionResult.value?.metadata?.documentTitle ?: "DocuClean_Document"

            val printAdapter = object : PrintDocumentAdapter() {
                override fun onLayout(
                    oldAttributes: PrintAttributes?,
                    newAttributes: PrintAttributes?,
                    cancellationSignal: android.os.CancellationSignal?,
                    callback: LayoutResultCallback?,
                    extras: android.os.Bundle?
                ) {
                    if (cancellationSignal?.isCanceled == true) {
                        callback?.onLayoutCancelled()
                        return
                    }
                    val info = android.print.PrintDocumentInfo.Builder("$jobName.pdf")
                        .setContentType(android.print.PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                        .setPageCount(_inspectionResult.value?.metadata?.pageCount ?: 1)
                        .build()
                    callback?.onLayoutFinished(info, true)
                }

                override fun onWrite(
                    pages: Array<out android.print.PageRange>?,
                    destination: android.os.ParcelFileDescriptor?,
                    cancellationSignal: android.os.CancellationSignal?,
                    callback: WriteResultCallback?
                ) {
                    if (destination == null) {
                        callback?.onWriteFailed("Output descriptor missing")
                        return
                    }
                    try {
                        FileInputStream(cleanFile).use { input ->
                            FileOutputStream(destination.fileDescriptor).use { output ->
                                input.copyTo(output)
                            }
                        }
                        callback?.onWriteFinished(arrayOf(android.print.PageRange.ALL_PAGES))
                    } catch (e: Exception) {
                        callback?.onWriteFailed(e.message)
                    }
                }
            }

            printManager?.print(jobName, printAdapter, PrintAttributes.Builder().build())
        } catch (e: Exception) {
            _statusMessage.value = "Print error: ${e.message}"
        }
    }

    fun dismissStatus() {
        _statusMessage.value = null
    }
}
