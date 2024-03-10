package com.lockermanwxlf.drivesync.ui.home

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.lockermanwxlf.drivesync.MainActivity
import com.lockermanwxlf.drivesync.models.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val vm: HomeViewModel = hiltViewModel()
    val context = LocalContext.current
    var account: GoogleSignInAccount? by rememberSaveable {
        mutableStateOf(GoogleSignIn.getLastSignedInAccount(context))
    }
    vm.setAccount(account)
    val signInOptions = GoogleSignInOptions.Builder()
        .requestId()
        .apply { MainActivity.SCOPES.forEach { requestScopes(Scope(it)) } }
        .build()
    val signInClient = GoogleSignIn.getClient(context, signInOptions)


    val signInLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
        account = GoogleSignIn.getSignedInAccountFromIntent(it.data).result
    }



    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Drive Sync") })
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AccountStatus(
                account,
                signInLauncher,
                signInClient,
                onLogOut = { account = null }
            )
            val syncs by vm.syncs.collectAsState(initial = listOf())
            AnimatedVisibility(visible = account != null) {
                SyncsView(
                    syncs = syncs,
                    onCreateSyncPressed = {
                        navController.navigate("New Sync") {
                            launchSingleTop = true
                            popUpTo("Home")
                        }
                    },
                    onDeleteSync = {
                        vm.deleteSync(it)
                    }
                )
            }
        }
    }







}