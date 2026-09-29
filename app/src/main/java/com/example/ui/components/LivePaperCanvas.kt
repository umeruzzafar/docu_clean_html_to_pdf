package com.example.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FitScreen
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.DownloadBrush
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AttendanceRecord
import com.example.model.DocumentInspectionResult
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LivePaperCanvas(
    inspectionResult: DocumentInspectionResult?,
    zoomPercent: Int,
    fitToWidth: Boolean,
    selectedPageIndex: Int,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onResetZoom: () -> Unit,
    onToggleFitToWidth: () -> Unit,
    onSelectPage: (Int) -> Unit,
    onDownloadPdf: () -> Unit,
    onPrintPdf: () -> Unit,
    onSharePdf: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalPages = inspectionResult?.metadata?.pageCount ?: 0
    val pageBitmaps = inspectionResult?.pageBitmaps ?: emptyList()
    val attendance = inspectionResult?.attendanceRecords ?: emptyList()

    var showAttendanceTableSheet by remember { mutableStateOf(false) }
    var tableFilterText by remember { mutableStateOf("") }
    var selectedFilterStatus by remember { mutableStateOf("ALL") }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(selectedPageIndex) {
        if (selectedPageIndex in pageBitmaps.indices) {
            listState.animateScrollToItem(selectedPageIndex)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Toolbar: Zoom & Export Controls
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Zoom Controls
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(
                            onClick = onZoomOut,
                            enabled = zoomPercent > 50,
                            modifier = Modifier.size(36.dp).testTag("zoom_out_button")
                        ) {
                            Icon(Icons.Default.ZoomOut, contentDescription = "Zoom Out", modifier = Modifier.size(20.dp))
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.testTag("zoom_indicator")
                        ) {
                            Text(
                                text = "$zoomPercent%",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        IconButton(
                            onClick = onZoomIn,
                            enabled = zoomPercent < 200,
                            modifier = Modifier.size(36.dp).testTag("zoom_in_button")
                        ) {
                            Icon(Icons.Default.ZoomIn, contentDescription = "Zoom In", modifier = Modifier.size(20.dp))
                        }

                        IconButton(
                            onClick = onToggleFitToWidth,
                            colors = if (fitToWidth) IconButtonDefaults.filledIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ) else IconButtonDefaults.iconButtonColors(),
                            modifier = Modifier.size(36.dp).testTag("fit_width_button")
                        ) {
                            Icon(Icons.Default.FitScreen, contentDescription = "Fit to Width", modifier = Modifier.size(18.dp))
                        }
                    }

                    // Export & Print Actions
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        IconButton(
                            onClick = onPrintPdf,
                            modifier = Modifier.size(38.dp).testTag("print_button")
                        ) {
                            Icon(Icons.Default.Print, contentDescription = "Print PDF", tint = MaterialTheme.colorScheme.primary)
                        }

                        IconButton(
                            onClick = onSharePdf,
                            modifier = Modifier.size(38.dp).testTag("share_button")
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Share PDF", tint = MaterialTheme.colorScheme.secondary)
                        }

                        // ULTRA-DISTINCT DOWNLOAD BUTTON (EMERALD-TO-MINT GLOWING PILL)
                        Card(
                            onClick = onDownloadPdf,
                            shape = RoundedCornerShape(20.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            modifier = Modifier
                                .shadow(
                                    elevation = 8.dp,
                                    shape = RoundedCornerShape(20.dp),
                                    spotColor = Color(0xFF10B981).copy(alpha = 0.5f)
                                )
                                .testTag("download_pdf_button"),
                            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(DownloadBrush)
                                    .padding(horizontal = 14.dp, vertical = 9.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color.White.copy(alpha = 0.25f),
                                        modifier = Modifier.size(20.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.Download,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(13.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "DOWNLOAD PDF",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 0.5.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                // Page Navigation & Inspection Status Bar
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (totalPages > 1) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { onSelectPage((selectedPageIndex - 1).coerceAtLeast(0)) },
                                enabled = selectedPageIndex > 0,
                                modifier = Modifier.size(32.dp).testTag("prev_page_button")
                            ) {
                                Icon(Icons.Default.NavigateBefore, contentDescription = "Previous Page")
                            }

                            Text(
                                text = "Page ${selectedPageIndex + 1} of $totalPages",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold
                            )

                            IconButton(
                                onClick = { onSelectPage((selectedPageIndex + 1).coerceAtMost(totalPages - 1)) },
                                enabled = selectedPageIndex < totalPages - 1,
                                modifier = Modifier.size(32.dp).testTag("next_page_button")
                            ) {
                                Icon(Icons.Default.NavigateNext, contentDescription = "Next Page")
                            }
                        }
                    } else {
                        Text(
                            text = if (totalPages > 0) "1 Clean Document Page" else "No document loaded",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Attendance inspection quick pill
                    if (attendance.isNotEmpty()) {
                        FilledTonalButton(
                            onClick = { showAttendanceTableSheet = !showAttendanceTableSheet },
                            modifier = Modifier.height(28.dp).testTag("toggle_attendance_table_button"),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${attendance.size} Students (${if (showAttendanceTableSheet) "Hide Grid" else "Show Grid"})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // Attendance Table View (Expandable Drawer / Overlay)
        if (showAttendanceTableSheet && attendance.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Parsed Student Attendance Records",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf("ALL", "ELIGIBLE", "WARNING", "DEBARRED").forEach { st ->
                                FilterChip(
                                    selected = selectedFilterStatus == st,
                                    onClick = { selectedFilterStatus = st },
                                    label = { Text(st, fontSize = 10.sp) },
                                    modifier = Modifier.height(26.dp)
                                )
                            }
                        }
                    }

                    // Filter list
                    val filtered = attendance.filter {
                        (selectedFilterStatus == "ALL" || it.status == selectedFilterStatus) &&
                                (tableFilterText.isBlank() || it.rollNo.contains(tableFilterText, true) || it.studentName.contains(tableFilterText, true))
                    }

                    Box(modifier = Modifier.height(180.dp)) {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            itemsIndexed(filtered) { _, item ->
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(item.rollNo, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(item.studentName, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                                            }
                                            // Colorful mini-progress bar
                                            Spacer(modifier = Modifier.height(3.dp))
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth(0.85f)
                                                    .height(4.dp)
                                                    .clip(RoundedCornerShape(2.dp))
                                                    .background(Color(0xFFE2E8F0))
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth(item.percentage / 100f)
                                                        .height(4.dp)
                                                        .background(
                                                            when {
                                                                item.percentage >= 75f -> Color(0xFF10B981)
                                                                item.percentage >= 65f -> Color(0xFFF59E0B)
                                                                else -> Color(0xFFEF4444)
                                                            }
                                                        )
                                                )
                                            }
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Text(
                                                "${item.attendedClasses}/${item.totalClasses} (${String.format("%.1f", item.percentage)}%)",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = when {
                                                    item.percentage >= 75f -> Color(0xFF047857)
                                                    item.percentage >= 65f -> Color(0xFFB45309)
                                                    else -> Color(0xFFB91C1C)
                                                }
                                            )
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = when (item.status) {
                                                    "ELIGIBLE" -> Color(0xFFECFDF5)
                                                    "WARNING" -> Color(0xFFFFFBEB)
                                                    else -> Color(0xFFFEF2F2)
                                                },
                                                border = androidx.compose.foundation.BorderStroke(
                                                    1.dp,
                                                    when (item.status) {
                                                        "ELIGIBLE" -> Color(0xFF10B981).copy(alpha = 0.5f)
                                                        "WARNING" -> Color(0xFFF59E0B).copy(alpha = 0.5f)
                                                        else -> Color(0xFFEF4444).copy(alpha = 0.5f)
                                                    }
                                                )
                                            ) {
                                                Text(
                                                    text = item.status,
                                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.5.dp),
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = when (item.status) {
                                                        "ELIGIBLE" -> Color(0xFF059669)
                                                        "WARNING" -> Color(0xFFD97706)
                                                        else -> Color(0xFFDC2626)
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Live Paper Sheet Canvas Area
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (MaterialTheme.colorScheme.surface.hashCode() > 0) Color(0xFFE2E8F0).copy(alpha = 0.6f)
                    else Color(0xFF0B132B)
                )
                .padding(vertical = 12.dp, horizontal = 8.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            val availableWidth = maxWidth

            if (pageBitmaps.isEmpty()) {
                // Empty state / Processing placeholder
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FitScreen,
                        contentDescription = null,
                        modifier = Modifier.size(54.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
                    Text(
                        text = "Ready to inspect document stream",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Paste raw %PDF-1.4 data, Oracle Reports, or HTML to view virtual paper sheets.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            } else {
                // Dynamic page width based on zoom and fit-to-width
                val sheetWidth = if (fitToWidth) {
                    availableWidth.coerceAtMost(620.dp)
                } else {
                    (availableWidth.value * (zoomPercent / 100f)).dp.coerceAtLeast(300.dp)
                }

                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize().testTag("live_paper_sheet_list"),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    itemsIndexed(pageBitmaps) { index, bitmap ->
                        VirtualPaperSheet(
                            pageIndex = index,
                            totalPages = pageBitmaps.size,
                            bitmap = bitmap,
                            width = sheetWidth,
                            isSelected = index == selectedPageIndex
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VirtualPaperSheet(
    pageIndex: Int,
    totalPages: Int,
    bitmap: Bitmap,
    width: androidx.compose.ui.unit.Dp,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.testTag("virtual_paper_page_$pageIndex")
    ) {
        // Page Sheet Container with Realistic Drop Shadow
        Card(
            modifier = Modifier
                .width(width)
                .aspectRatio(1f / 1.414f) // Standard A4 Aspect Ratio
                .shadow(
                    elevation = if (isSelected) 12.dp else 6.dp,
                    shape = RoundedCornerShape(4.dp),
                    spotColor = Color.Black.copy(alpha = 0.35f)
                ),
            shape = RoundedCornerShape(4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(
                0.5.dp,
                if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFFCBD5E1)
            )
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Page ${pageIndex + 1}",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Page ${pageIndex + 1} of $totalPages",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
