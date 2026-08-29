package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.ui.TaskTrackerApp
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.TaskTrackerViewModel

class MainActivity : ComponentActivity() {
  private val viewModel: TaskTrackerViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
          TaskTrackerApp(viewModel = viewModel)
        }
      }
    }
  }
}
