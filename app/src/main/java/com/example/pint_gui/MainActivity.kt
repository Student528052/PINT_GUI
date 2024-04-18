package com.example.pint_gui

import android.annotation.SuppressLint
import android.content.res.Resources.Theme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.pint_gui.ui.theme.PINT_GUITheme
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.*

class MainActivity : ComponentActivity() {
    private var connectionJob: Job? = null;
    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            //controlling between the main screen and the history screen
            val navController = rememberNavController();

            var connectionJob = CoroutineScope(Dispatchers.Default).launch{
                while(isActive){
                    val ESP32_connection = connect_to_ESP32();
                    if(ESP32_connection)

                break;

                }
                delay(15000); //15 seconds
            }

            PINT_GUITheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.onSurface
                ) {
                    NavHost(navController = navController, startDestination = "mainscreen") {
                        composable("mainscreen"){
                            mainscreen(navController = navController)
                        }
                        composable("history_screen"){
                            History_screen(navController = navController)
                        }

                    }
                }
            }
        }
    }
}


 fun connect_to_ESP32(): Boolean{
    //TODO: figure out a way to connect to the ESP32, assuming were using a common hotspot
    return true;
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PINT_GUITheme {

    }
}