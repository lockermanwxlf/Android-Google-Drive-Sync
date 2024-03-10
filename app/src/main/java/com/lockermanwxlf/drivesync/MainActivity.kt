package com.lockermanwxlf.drivesync

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.core.content.PackageManagerCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.common.Scopes
import com.lockermanwxlf.drivesync.service.SyncService
import com.lockermanwxlf.drivesync.ui.home.HomeScreen
import com.lockermanwxlf.drivesync.ui.newsync.NewSyncScreen
import com.lockermanwxlf.drivesync.ui.selectdrivefolder.SelectDriveFolderScreen
import com.lockermanwxlf.drivesync.ui.theme.DriveSyncTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    companion object {
        val SCOPES = listOf(
            Scopes.DRIVE_FULL
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startForegroundService(Intent(applicationContext, SyncService::class.java))



        setContent {
            DriveSyncTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {

                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        LaunchedEffect(Unit) {
                            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }


                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "Home") {
                        composable("Home") {
                            HomeScreen(navController = navController)
                        }
                        composable("New Sync") {
                            NewSyncScreen(navController = navController)
                        }
                        composable("Select Drive Folder") {
                            SelectDriveFolderScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }
}
