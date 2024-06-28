package com.example.pint_gui

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*

import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import androidx.lifecycle.viewmodel.compose.viewModel
data class ModelData(val name: String, val value: Int)



@Composable
fun BarChart( modifier: Modifier = Modifier, esp32result: MutableState<String>){
    //Filtering out the data using REGEX
    val regex = Regex("\\d+: (-?[\\d.]+)")
    val values = regex.findAll(esp32result.value).mapNotNull { it.groupValues[1].toFloatOrNull() }.toList()
    val status = esp32result.value.substringAfterLast(",").trim()

    Column(modifier = Modifier
        .padding(20.dp)
        ) {
        values.forEachIndexed { index, value ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                Text(text = "Sen ${index + 1}", modifier = Modifier.width(80.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .height(45.dp)
                            .width(value.dp * 3) // Scale factor to adjust bar width
                            .background(Color.DarkGray),
                        contentAlignment = Alignment.Center
                    ) {

                        if (value.dp * 2 > 50.dp) { // Adjust threshold as needed
                            Text(text = "$value", color = Color.White, style = TextStyle(fontSize = 25.sp), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center )
                        }
                    }
                    if (value.dp * 2 <= 50.dp) { // Adjust threshold as needed
                        Text(text = "$value", color = Color.White, style = TextStyle(fontSize = 25.sp), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center )

                    }
                }
            }
        }

    }
    Row(modifier = Modifier
        .background(
            if (status == "Good") Color.Green
            else if (status == "Bad") Color.Red
            else if (status == "Very Bad") Color.Black
            else Color.LightGray
        )
        .fillMaxWidth(), Arrangement.Center){
        Box(modifier = Modifier, contentAlignment = Alignment.Center)
        {
            Text(text = "${status}", color = Color.Gray, modifier = Modifier, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, style = TextStyle(fontSize = 30.sp))
        }
    }


}




/*
MAIN FUNCTION
 */
@Composable
fun mainscreen(navController: NavHostController, esp32values : MutableState<String>, viewModel: MainViewModel = viewModel()) {
    val scope = rememberCoroutineScope();
    val timer by viewModel.timer;
    val time = remember { mutableStateOf(0L) }
    val CalButton: () -> Unit = {
        scope.launch {
            fetchData("http://${ESP32_IP}/Startcal");

        }
        viewModel.startOrResetTimer()
        }






    // Load data asynchronously
    Column(modifier = Modifier
        .padding(10.dp)
        .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally){
        //timer
        //TODO: change this to start a function and start the timer
        Box(modifier = Modifier.padding(10.dp) ) {
            Text(text = formatTime(timer), style = TextStyle(fontSize = 40.sp), fontWeight = FontWeight.Bold )
        }

        Button(onClick = { navController.navigate("history_screen")}, modifier = Modifier.padding(10.dp)) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_history_24),
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )

        }
        Button(onClick = CalButton ) {

            Icon(
                painter = painterResource(id = R.drawable.baseline_build_24),
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
        }



        //assuming we get the data live(and not store it in a file), we can choose not to display anytning
        if(esp32_status != "") {
            BarChart( esp32result = esp32values)
            Box(modifier = Modifier
                .padding(100.dp)
                .background(color = Color(30, 200, 30))){
                Text(text = "Status: Connected", modifier = Modifier
                    .padding()
                    .background(color = Color(20, 200, 20)))
            }

        }

        else{
            Text(text="Sorry, nothing to show",modifier = Modifier.padding(150.dp))
        }
        Box( modifier = Modifier.padding(100.dp)){
            Text(text = "STATUS: GOOD(TEST TEST TEST)", modifier = Modifier.padding( ))
        }

}
}
private fun formatTime(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60
    return String.format("%02d:%02d:%02d", hours, minutes, secs)
}


class MainViewModel : ViewModel() {
    private var timerJob: Job? = null
    private val _timer = mutableStateOf(0L)
    val timer: State<Long> get() = _timer
    private var isTimerRunning = false

    fun startOrResetTimer() {
        if (!isTimerRunning) {
            isTimerRunning = true
            timerJob = viewModelScope.launch {
                while (isTimerRunning) {
                    _timer.value++
                    delay(1000L)
                }
            }
        } else {
            _timer.value = 0L // Reset the timer
        }
    }

    fun stopTimer() {
        isTimerRunning = false
        timerJob?.cancel()
    }
}