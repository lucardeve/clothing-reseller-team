package com.cegep.reseller

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cegep.reseller.ui.AppViewModel
import com.cegep.reseller.ui.AppViewModelFactory
import com.cegep.reseller.ui.ResellerAppUi
import com.cegep.reseller.ui.theme.ResellerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ResellerTheme {
                val app = applicationContext as ResellerApp
                val viewModel: AppViewModel = viewModel(factory = AppViewModelFactory(app.container))
                ResellerAppUi(viewModel)
            }
        }
    }
}
