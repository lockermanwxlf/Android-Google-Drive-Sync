package com.lockermanwxlf.drivesync.ui.selectdrivefolder

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.lockermanwxlf.drive.DriveFolder
import com.lockermanwxlf.drivesync.R
import com.lockermanwxlf.drivesync.models.SelectDriveFolderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectDriveFolderScreen(
    navController: NavController
) {
    val vm: SelectDriveFolderViewModel = hiltViewModel()
    val context = LocalContext.current
    val topFolders = vm.driveFolders
    val account by remember {
        mutableStateOf(GoogleSignIn.getLastSignedInAccount(context))
    }
    if (account == null) {
        Toast.makeText(context, "Google Account not valid.", Toast.LENGTH_SHORT).show()
        navController.popBackStack("Home", false)
        return
    } else {
        vm.setAccount(account)
    }
    var selectedFolder: DriveFolder? by rememberSaveable {
        mutableStateOf(null)
    }
    var viewingFolder: DriveFolder? by rememberSaveable {
        mutableStateOf(null)
    }
    BackHandler(enabled = viewingFolder != null) {
        viewingFolder = viewingFolder?.parent
    }

        Scaffold(
        topBar = {
            TopAppBar(title = { Text("Select Drive Folder") })
        },
        bottomBar = {
            AnimatedVisibility(visible = selectedFolder != null) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    FloatingActionButton(
                        onClick = {
                            if (selectedFolder != null) {
                                navController.previousBackStackEntry?.savedStateHandle?.set(
                                    "selected_folder",
                                    selectedFolder
                                )?.also {
                                    navController.popBackStack()
                                } ?: Toast.makeText(
                                    context,
                                    "Error selecting folder.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        modifier = Modifier.padding(10.dp)
                    ) {
                        Text(
                            text = "Select ${selectedFolder?.name}",
                            modifier = Modifier
                                .padding(10.dp, 5.dp, 10.dp, 5.dp)
                                .animateContentSize(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    ) {
        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .padding(it)) {
            items(viewingFolder?.subfolders?:topFolders) {
                val height by animateDpAsState(targetValue = if (selectedFolder == it) 10.dp else 2.dp,
                    label = "Selection Height Animation"
                )
                val padding by animateDpAsState(targetValue = if (selectedFolder == it) 12.dp else 3.dp,
                    label = "Selection Padding Animation"
                )
                Surface(
                    shadowElevation = height,
                    modifier = Modifier
                        .padding(padding)
                        .let { modifier ->
                            if (selectedFolder != it) {
                                modifier.clickable {
                                    selectedFolder = it
                                }
                            } else {
                                modifier
                            }
                        }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = it.name,
                            modifier = Modifier.padding(padding)
                        )

                        if (it.subfolders.isNotEmpty()) {
                            IconButton(onClick = { viewingFolder = it }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_arrow_upward_24),
                                    contentDescription = "View subfolders of ${it.name}"
                                )
                            }
                        }

                    }

                }
            }
        }
    }
}