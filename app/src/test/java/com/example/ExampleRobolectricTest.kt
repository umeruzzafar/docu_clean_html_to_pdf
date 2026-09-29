package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.DocumentFormat
import com.example.parser.DocuCleanEngine
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("DocuClean PDF", appName)
    }

    @Test
    fun `detect raw pdf stream in html wrapper`() {
        val htmlWithPdf = "<html><body>%PDF-1.4\nstream data\n%%EOF</body></html>"
        val format = DocuCleanEngine.detectFormat(htmlWithPdf.toByteArray(), htmlWithPdf)
        assertEquals(DocumentFormat.RAW_PDF_STREAM, format)
    }

    @Test
    fun `detect portal html table`() {
        val tableHtml = "<html><table><tr><th>Roll No</th></tr></table></html>"
        val format = DocuCleanEngine.detectFormat(tableHtml.toByteArray(), tableHtml)
        assertEquals(DocumentFormat.HTML_PORTAL_EXPORT, format)
    }
}
