package com.example.pint_gui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import  androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.*
import androidx.navigation.NavHostController
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.runtime.*
import androidx.compose.material3.Text
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.FontScaling
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

val file = "History.txt"
data class HistoryData(val time: String, val status: String)

@Composable
fun History_screen(navController: NavHostController) {
    val context = LocalContext.current
    val history = remember { mutableStateOf(listOf<HistoryData>()) }

    // Read and parse history only once when the Composable is first launched
    LaunchedEffect(true) {
        history.value = Read_History(context, file)

    }
    Column (modifier = Modifier
        .padding(20.dp)
        .fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally){
        Text(text = "History")
        // Add other items as needed


        // Button to navigate back to the first screen
        Button(onClick = { navController.popBackStack() }) {
           Icon(painter = painterResource(id = R.drawable.baseline_arrow_back_24), contentDescription =null )
        }

        Display_History(entries = history.value)
    }
}

@Composable
 fun Display_History(entries: List<HistoryData>){
     val colorGood = Color(android.graphics.Color.parseColor("#68ed6c"))
     val colorBad = Color(android.graphics.Color.parseColor("#ed6a68"))
     val colorVeryBad = Color(android.graphics.Color.parseColor("#a8a8a8"))


    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        items(entries) { entry ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                        .background(if (entry.status == "Good") colorGood else
                            if (entry.status == "Bad") colorBad
                            else  colorVeryBad).height(40.dp)
                ) {
                    Text(
                        text = entry.time.replace(":", "-"),
                        modifier = Modifier
                            .padding(start = 16.dp),
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "    " + entry.status,
                        modifier = Modifier
                            .padding(end = 16.dp),
                        color = Color.Black,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

    }
}
suspend fun Write_History(context: Context, fileName: String, content: String) {
    withContext(Dispatchers.IO) {
        try {
            val file = File(context.filesDir, fileName)
            FileOutputStream(file, true).use { output ->
                output.write(content.toByteArray())
                output.write("\n".toByteArray())
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

private suspend fun Read_History(context: Context, fileName: String): List<HistoryData> {
        return withContext(Dispatchers.IO) {
            try {
                val file = File(context.filesDir, fileName)
                if (!file.exists()) {
                    return@withContext emptyList<HistoryData>()
                }

                // Regular expression to match the new format
                val validPattern = Regex("""(\d{4}:\d{2}:\d{2}:\d{3}): (.*), (Good|Bad|Very Bad)""")

                file.bufferedReader().use { reader ->
                    reader.readLines().mapNotNull { line ->
                        val matchResult = validPattern.find(line)
                        if (matchResult != null) {
                            val (timestamp, _, status) = matchResult.destructured
                            HistoryData(time = timestamp, status = status)
                        } else {
                            null
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                emptyList()
            }
        }
    }

