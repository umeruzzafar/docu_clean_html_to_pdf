package com.example.ui

import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.DownloadBrush
import com.example.ui.theme.TopBarGradient
import com.example.ui.theme.UploadBrush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.DownloadedFilePopupDialog
import com.example.ui.components.HistoryModalSheet
import com.example.ui.components.InputWorkspacePanel
import com.example.ui.components.LivePaperCanvas
import com.example.ui.components.ThemeSelectorDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocuCleanScreen(
    viewModel: DocuCleanViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val inputText by viewModel.inputText.collectAsStateWithLifecycle()
    val detectedFormat by viewModel.detectedFormat.collectAsStateWithLifecycle()
    val inspectionResult by viewModel.inspectionResult.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val statusMessage by viewModel.statusMessage.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val zoomPercent by viewModel.zoomPercent.collectAsStateWithLifecycle()
    val fitToWidth by viewModel.fitToWidth.collectAsStateWithLifecycle()
    val selectedPageIndex by viewModel.selectedPageIndex.collectAsStateWithLifecycle()
    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
    val importedFileName by viewModel.importedFileName.collectAsStateWithLifecycle()
    val recentDocuments by viewModel.recentDocuments.collectAsStateWithLifecycle()
    val recentlyDownloadedInfo by viewModel.recentlyDownloadedInfo.collectAsStateWithLifecycle()
    val showOpenDownloadDialog by viewModel.showOpenDownloadDialog.collectAsStateWithLifecycle()
    val autoOpenEnabled by viewModel.autoOpenEnabled.collectAsStateWithLifecycle()

    var showThemeDialog by remember { mutableStateOf(false) }
    var showHistoryDialog by remember { mutableStateOf(false) }

    // File Open Launcher (.pdf, .html, .txt, or all)
    val openFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            viewModel.handleFileUri(context, uri)
        }
    }

    // Save Cleaned PDF Launcher
    val savePdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri ->
        if (uri != null) {
            viewModel.exportPdfToUri(context, uri)
        }
    }

    // Status snackbar observer
    LaunchedEffect(statusMessage) {
        statusMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.dismissStatus()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "DocuClean PDF",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.Transparent,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(UploadBrush)
                            ) {
                                Text(
                                    text = "INSPECTOR",
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                        }
                        Text(
                            text = "Intelligent Document & Attendance Inspector",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    // Theme Selector Action
                    IconButton(
                        onClick = { showThemeDialog = true },
                        modifier = Modifier.testTag("theme_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "Theme Selector",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // History Action with Badge
                    IconButton(
                        onClick = { showHistoryDialog = true },
                        modifier = Modifier.testTag("history_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (recentDocuments.isNotEmpty()) {
                                    Badge(
                                        containerColor = Color(0xFFEF4444)
                                    ) { Text("${recentDocuments.size}") }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "History",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // ULTRA-DISTINCT DOWNLOAD BUTTON IN TOPBAR (RADIANT EMERALD GRADIENT)
                    if (inspectionResult?.cleanPdfFile != null) {
                        Card(
                            onClick = {
                                val fileName = "Cleaned_${System.currentTimeMillis()}.pdf"
                                savePdfLauncher.launch(fileName)
                            },
                            shape = RoundedCornerShape(20.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .shadow(
                                    elevation = 6.dp,
                                    shape = RoundedCornerShape(20.dp),
                                    spotColor = Color(0xFF10B981).copy(alpha = 0.5f)
                                )
                                .testTag("topbar_download_button"),
                            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(DownloadBrush)
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Download,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = "Download PDF",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Subtle Vibrant Accent Stripe
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(TopBarGradient)
            )

            // Quick Access Bar for Recently Downloaded File
            if (recentlyDownloadedInfo != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("recently_downloaded_banner"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFECFDF5)
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.5f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFF10B981),
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.PictureAsPdf,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Cleaned PDF Ready: ${recentlyDownloadedInfo?.fileName}",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    color = Color(0xFF065F46)
                                )
                                Text(
                                    text = "Tap to open recently converted PDF",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 11.sp,
                                    color = Color(0xFF047857)
                                )
                            }
                        }

                        Card(
                            onClick = { viewModel.openRecentlyDownloaded(context) },
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.testTag("open_recent_pdf_banner_button"),
                            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(DownloadBrush)
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.OpenInNew,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Open PDF",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
            val isWideScreen = maxWidth >= 720.dp

            if (isWideScreen) {
                // Tablet / Desktop Split-Pane Layout
                Row(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .width(380.dp)
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        InputWorkspacePanel(
                            inputText = inputText,
                            detectedFormat = detectedFormat,
                            inspectionResult = inspectionResult,
                            isLoading = isLoading,
                            importedFileName = importedFileName,
                            onInputTextChanged = viewModel::onInputTextChanged,
                            onPasteFromClipboard = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                if (clipboard.hasPrimaryClip()) {
                                    val item = clipboard.primaryClip?.getItemAt(0)
                                    val text = item?.text?.toString() ?: ""
                                    if (text.isNotBlank()) {
                                        viewModel.onInputTextChanged(text)
                                        viewModel.processCurrentDocument()
                                    }
                                }
                            },
                            onOpenFilePicker = {
                                openFileLauncher.launch(arrayOf("*/*", "application/pdf", "text/html", "text/plain"))
                            },
                            onClear = viewModel::clearInput,
                            onProcess = { viewModel.processCurrentDocument(autoSwitchToPreview = false) },
                            onSelectPreset = viewModel::loadPreset
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                    ) {
                        LivePaperCanvas(
                            inspectionResult = inspectionResult,
                            zoomPercent = zoomPercent,
                            fitToWidth = fitToWidth,
                            selectedPageIndex = selectedPageIndex,
                            onZoomIn = viewModel::zoomIn,
                            onZoomOut = viewModel::zoomOut,
                            onResetZoom = viewModel::resetZoom,
                            onToggleFitToWidth = viewModel::toggleFitToWidth,
                            onSelectPage = viewModel::setSelectedPage,
                            onDownloadPdf = {
                                val fileName = "DocuClean_${System.currentTimeMillis()}.pdf"
                                savePdfLauncher.launch(fileName)
                            },
                            onPrintPdf = { viewModel.printDocument(context) },
                            onSharePdf = { viewModel.sharePdf(context) }
                        )
                    }
                }
            } else {
                // Mobile-First Tabbed Layout (Editor / Preview Canvas)
                Column(modifier = Modifier.fillMaxSize()) {
                    TabRow(
                        selectedTabIndex = activeTab,
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.testTag("mobile_tab_row")
                    ) {
                        Tab(
                            selected = activeTab == 0,
                            onClick = { viewModel.setActiveTab(0) },
                            text = { Text("Input & Stream") },
                            icon = { Icon(Icons.Default.Terminal, contentDescription = null, modifier = Modifier.size(18.dp)) },
                            modifier = Modifier.testTag("tab_input")
                        )
                        Tab(
                            selected = activeTab == 1,
                            onClick = { viewModel.setActiveTab(1) },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Live Paper Canvas")
                                    if ((inspectionResult?.pageBitmaps?.size ?: 0) > 0) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = MaterialTheme.colorScheme.primary
                                        ) {
                                            Text(
                                                text = "${inspectionResult?.pageBitmaps?.size}",
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp),
                                                fontSize = 10.sp,
                                                color = MaterialTheme.colorScheme.onPrimary,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            },
                            icon = { Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(18.dp)) },
                            modifier = Modifier.testTag("tab_preview")
                        )
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        when (activeTab) {
                            0 -> {
                                InputWorkspacePanel(
                                    inputText = inputText,
                                    detectedFormat = detectedFormat,
                                    inspectionResult = inspectionResult,
                                    isLoading = isLoading,
                                    importedFileName = importedFileName,
                                    onInputTextChanged = viewModel::onInputTextChanged,
                                    onPasteFromClipboard = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        if (clipboard.hasPrimaryClip()) {
                                            val item = clipboard.primaryClip?.getItemAt(0)
                                            val text = item?.text?.toString() ?: ""
                                            if (text.isNotBlank()) {
                                                viewModel.onInputTextChanged(text)
                                                viewModel.processCurrentDocument(autoSwitchToPreview = true)
                                            }
                                        }
                                    },
                                    onOpenFilePicker = {
                                        openFileLauncher.launch(arrayOf("*/*", "application/pdf", "text/html", "text/plain"))
                                    },
                                    onClear = viewModel::clearInput,
                                    onProcess = { viewModel.processCurrentDocument(autoSwitchToPreview = true) },
                                    onSelectPreset = viewModel::loadPreset
                                )
                            }
                            1 -> {
                                LivePaperCanvas(
                                    inspectionResult = inspectionResult,
                                    zoomPercent = zoomPercent,
                                    fitToWidth = fitToWidth,
                                    selectedPageIndex = selectedPageIndex,
                                    onZoomIn = viewModel::zoomIn,
                                    onZoomOut = viewModel::zoomOut,
                                    onResetZoom = viewModel::resetZoom,
                                    onToggleFitToWidth = viewModel::toggleFitToWidth,
                                    onSelectPage = viewModel::setSelectedPage,
                                    onDownloadPdf = {
                                        val fileName = "DocuClean_${System.currentTimeMillis()}.pdf"
                                        savePdfLauncher.launch(fileName)
                                    },
                                    onPrintPdf = { viewModel.printDocument(context) },
                                    onSharePdf = { viewModel.sharePdf(context) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

    // Dialogs
    if (showThemeDialog) {
        ThemeSelectorDialog(
            currentTheme = themeMode,
            onSelectTheme = viewModel::setTheme,
            onDismiss = { showThemeDialog = false }
        )
    }

    if (showHistoryDialog) {
        HistoryModalSheet(
            documents = recentDocuments,
            onSelectDocument = viewModel::loadHistoryDocument,
            onDeleteDocument = viewModel::deleteHistory,
            onClearAll = viewModel::clearAllHistory,
            onDismiss = { showHistoryDialog = false }
        )
    }

    if (showOpenDownloadDialog && recentlyDownloadedInfo != null) {
        DownloadedFilePopupDialog(
            fileInfo = recentlyDownloadedInfo!!,
            autoOpenEnabled = autoOpenEnabled,
            onToggleAutoOpen = viewModel::toggleAutoOpen,
            onOpenPdf = {
                viewModel.openRecentlyDownloaded(context)
                viewModel.dismissDownloadDialog()
            },
            onSharePdf = {
                viewModel.sharePdf(context)
            },
            onDismiss = viewModel::dismissDownloadDialog
        )
    }
}
