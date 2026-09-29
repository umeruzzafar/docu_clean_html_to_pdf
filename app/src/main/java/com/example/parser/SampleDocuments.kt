package com.example.parser

import android.content.Context
import com.example.model.AttendanceRecord

data class PresetSample(
    val id: String,
    val title: String,
    val subtitle: String,
    val formatDescription: String,
    val sampleText: String
)

object SampleDocuments {

    val defaultAttendanceList: List<AttendanceRecord> = listOf(
        AttendanceRecord("BCIT-101", "Syed Muhammad Hamza", "CS-302", 42, 39, 92.8f, "ELIGIBLE"),
        AttendanceRecord("BCIT-102", "Ayesha Siddiqua", "CS-302", 42, 41, 97.6f, "ELIGIBLE"),
        AttendanceRecord("BCIT-105", "Bilal Ahmed Khan", "CS-302", 42, 35, 83.3f, "ELIGIBLE"),
        AttendanceRecord("BCIT-108", "Fatima Zehra", "CS-302", 42, 38, 90.5f, "ELIGIBLE"),
        AttendanceRecord("BCIT-112", "Mohammad Owais", "CS-302", 42, 29, 69.0f, "WARNING"),
        AttendanceRecord("BCIT-115", "Zainab Binte Tariq", "CS-302", 42, 40, 95.2f, "ELIGIBLE"),
        AttendanceRecord("BCIT-119", "Hamid Raza Qureshi", "CS-302", 42, 26, 61.9f, "DEBARRED"),
        AttendanceRecord("BCIT-122", "Maryam Noor", "CS-302", 42, 37, 88.1f, "ELIGIBLE"),
        AttendanceRecord("BCIT-126", "Daniyal Sheikh", "CS-302", 42, 34, 80.9f, "ELIGIBLE"),
        AttendanceRecord("BCIT-130", "Usman Ali Ghani", "CS-302", 42, 24, 57.1f, "DEBARRED"),
        AttendanceRecord("BCIT-134", "Khadija Tul Kubra", "CS-302", 42, 42, 100.0f, "ELIGIBLE"),
        AttendanceRecord("BCIT-137", "Zeeshan Haider", "CS-302", 42, 30, 71.4f, "WARNING"),
        AttendanceRecord("BCIT-140", "Sarah Mansoor", "CS-302", 42, 36, 85.7f, "ELIGIBLE"),
        AttendanceRecord("BCIT-144", "Saad Farooqi", "CS-302", 42, 33, 78.6f, "ELIGIBLE"),
        AttendanceRecord("BCIT-149", "Hira Tahir", "CS-302", 42, 39, 92.8f, "ELIGIBLE"),
        AttendanceRecord("BCIT-153", "Mustafa Kamal", "CS-302", 42, 28, 66.7f, "WARNING"),
        AttendanceRecord("BCIT-158", "Amna Javed", "CS-302", 42, 41, 97.6f, "ELIGIBLE"),
        AttendanceRecord("BCIT-162", "Taha Siddiqui", "CS-302", 42, 37, 88.1f, "ELIGIBLE")
    )

    fun getPresets(context: Context): List<PresetSample> {
        return listOf(
            PresetSample(
                id = "ned_attendance_corrupted",
                title = "NED Attendance (Corrupted .html with %PDF-1.4)",
                subtitle = "Oracle Reports misnamed stream containing embedded %PDF-1.4 stream",
                formatDescription = "Raw PDF stream wrapped in portal HTML tags",
                sampleText = """
                    <!-- ORACLE REPORTS WEB SERVER 12.2.1.4.0 -->
                    <!-- Content-Type: text/html; charset=UTF-8; name="Attendance_Report.html" -->
                    <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
                    <html>
                    <head><title>NED University of Eng. - Official Attendance Sheet</title></head>
                    <body>
                    <div id="portal-report-wrapper" style="font-family: monospace;">
                    %PDF-1.4
                    %
                    1 0 obj
                    << /Type /Catalog /Pages 2 0 R >>
                    endobj
                    2 0 obj
                    << /Type /Pages /Kids [3 0 R] /Count 1 >>
                    endobj
                    3 0 obj
                    << /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Contents 4 0 R >>
                    endobj
                    4 0 obj
                    << /Length 124 >>
                    stream
                    BT
                    /F1 16 Tf
                    50 800 Td
                    (NED UNIVERSITY OF ENGINEERING & TECHNOLOGY) Tj
                    0 -20 Td
                    /F1 11 Tf
                    (Department of Computer Science - Attendance & Eligibility) Tj
                    ET
                    endstream
                    endobj
                    xref
                    0 5
                    0000000000 65535 f 
                    0000000015 00000 n 
                    0000000068 00000 n 
                    0000000125 00000 n 
                    0000000212 00000 n 
                    trailer
                    << /Size 5 /Root 1 0 R >>
                    startxref
                    386
                    %%EOF
                    </div>
                    </body>
                    </html>
                """.trimIndent()
            ),
            PresetSample(
                id = "oracle_reports_stream",
                title = "Oracle Reports Portal Stream (HTTP + %PDF-1.5)",
                subtitle = "University intranet export with MIME HTTP headers & binary body",
                formatDescription = "Raw HTTP header stream with %PDF-1.5 binary stream",
                sampleText = """
                    HTTP/1.1 200 OK
                    Server: Oracle-HTTP-Server/12.2.1
                    Content-Type: application/octet-stream; charset=binary
                    Content-Disposition: inline; filename="SE302_ATTENDANCE_SUMMARY.pdf"
                    X-Oracle-Report-Id: RPT_ATT_2026_FALL
                    Cache-Control: private, max-age=0

                    %PDF-1.5
                    %NED-CS302-ATTENDANCE-RECORD
                    1 0 obj
                    << /Type /Catalog /Pages 2 0 R >>
                    endobj
                    2 0 obj
                    << /Type /Pages /Kids [3 0 R] /Count 1 >>
                    endobj
                    3 0 obj
                    << /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Contents 4 0 R >>
                    endobj
                    4 0 obj
                    << /Length 180 >>
                    stream
                    BT
                    /F1 14 Tf
                    40 800 Td
                    (NED UNIVERSITY OF ENGINEERING & TECHNOLOGY) Tj
                    0 -18 Td
                    /F1 10 Tf
                    (Department of Software Engineering - Section A) Tj
                    0 -15 Td
                    (Roll No: BCIT-101 | Hamza | Classes Held: 42 | Attended: 39 | 92.8%) Tj
                    ET
                    endstream
                    endobj
                    xref
                    0 5
                    0000000000 65535 f 
                    0000000015 00000 n 
                    0000000068 00000 n 
                    0000000125 00000 n 
                    0000000212 00000 n 
                    trailer
                    << /Size 5 /Root 1 0 R >>
                    startxref
                    442
                    %%EOF
                    <!-- END OF REPORT TRANSMISSION: CRC=0x9A4F -->
                """.trimIndent()
            ),
            PresetSample(
                id = "university_html_table",
                title = "University Portal HTML Attendance Table",
                subtitle = "Direct table export from student management system",
                formatDescription = "Clean HTML table with student roll numbers, classes & percentages",
                sampleText = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <title>NED University of Eng. - Attendance Summary Fall</title>
                        <style>
                            table { border-collapse: collapse; width: 100%; font-family: sans-serif; }
                            th, td { border: 1px solid #ddd; padding: 8px; font-size: 12px; }
                            th { background-color: #1e3a8a; color: white; }
                        </style>
                    </head>
                    <body>
                        <h2>NED UNIVERSITY OF ENGINEERING & TECHNOLOGY</h2>
                        <h3>Department of Computer Science & Software Engineering</h3>
                        <p>Course: <b>CS-302 Software Engineering (Theory)</b> | Total Lectures Held: <b>42</b></p>
                        <table>
                            <thead>
                                <tr>
                                    <th>#</th>
                                    <th>Roll No</th>
                                    <th>Student Name</th>
                                    <th>Course</th>
                                    <th>Held</th>
                                    <th>Attended</th>
                                    <th>Att %</th>
                                    <th>Eligibility</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr><td>1</td><td>BCIT-101</td><td>Syed Muhammad Hamza</td><td>CS-302</td><td>42</td><td>39</td><td>92.8%</td><td>ELIGIBLE</td></tr>
                                <tr><td>2</td><td>BCIT-102</td><td>Ayesha Siddiqua</td><td>CS-302</td><td>42</td><td>41</td><td>97.6%</td><td>ELIGIBLE</td></tr>
                                <tr><td>3</td><td>BCIT-105</td><td>Bilal Ahmed Khan</td><td>CS-302</td><td>42</td><td>35</td><td>83.3%</td><td>ELIGIBLE</td></tr>
                                <tr><td>4</td><td>BCIT-108</td><td>Fatima Zehra</td><td>CS-302</td><td>42</td><td>38</td><td>90.5%</td><td>ELIGIBLE</td></tr>
                                <tr><td>5</td><td>BCIT-112</td><td>Mohammad Owais</td><td>CS-302</td><td>42</td><td>29</td><td>69.0%</td><td>WARNING</td></tr>
                                <tr><td>6</td><td>BCIT-115</td><td>Zainab Binte Tariq</td><td>CS-302</td><td>42</td><td>40</td><td>95.2%</td><td>ELIGIBLE</td></tr>
                                <tr><td>7</td><td>BCIT-119</td><td>Hamid Raza Qureshi</td><td>CS-302</td><td>42</td><td>26</td><td>61.9%</td><td>DEBARRED</td></tr>
                                <tr><td>8</td><td>BCIT-122</td><td>Maryam Noor</td><td>CS-302</td><td>42</td><td>37</td><td>88.1%</td><td>ELIGIBLE</td></tr>
                                <tr><td>9</td><td>BCIT-126</td><td>Daniyal Sheikh</td><td>CS-302</td><td>42</td><td>34</td><td>80.9%</td><td>ELIGIBLE</td></tr>
                                <tr><td>10</td><td>BCIT-130</td><td>Usman Ali Ghani</td><td>CS-302</td><td>42</td><td>24</td><td>57.1%</td><td>DEBARRED</td></tr>
                            </tbody>
                        </table>
                    </body>
                    </html>
                """.trimIndent()
            ),
            PresetSample(
                id = "meeting_minutes_markdown",
                title = "Faculty Meeting & Attendance Record (Markdown)",
                subtitle = "Departmental board agenda, roll call & student clearance notes",
                formatDescription = "Markdown headers, checklists, quotes & attendance audit",
                sampleText = """
                    # NED UNIVERSITY OF ENGINEERING & TECHNOLOGY
                    ## Board of Studies & Attendance Review Committee

                    > Date: 28 September 2026 | Location: Syndicate Hall, Main Campus
                    > Presided By: Dean Faculty of Information Sciences & Technology

                    ### 1. Attendance & Eligibility Policy (75% Threshold)
                    In accordance with university regulations, students with less than 75% attendance are subject to examination debarment unless condoned by the Academic Council.

                    ### 2. High-Priority Case Review
                    - **BCIT-101**: 92.8% Attendance - Commended for outstanding consistency
                    - **BCIT-112**: 69.0% Attendance - Medical certificate verified; granted provisional clearance
                    - **BCIT-119**: 61.9% Attendance - Debarred from End-Semester Examination
                    - **BCIT-130**: 57.1% Attendance - Formal notice issued to guardian

                    ### 3. Summary of Recommendations
                    * All course instructors must submit final biometric logs by Friday.
                    * DocuClean PDF stream verification adopted for portal report exports.
                    * Re-check student portal uploads for corrupted MIME headers.
                """.trimIndent()
            )
        )
    }
}
