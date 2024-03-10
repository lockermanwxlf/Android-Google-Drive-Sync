package com.lockermanwxlf.drivesync.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.lockermanwxlf.drivesync.R
import com.lockermanwxlf.drivesync.utils.data.Sync

@Composable
fun SyncsView(
    syncs: List<Sync>,
    onCreateSyncPressed: () -> Unit,
    onDeleteSync: (Sync) -> Unit
) {

    var viewingSync: Sync? by rememberSaveable {
        mutableStateOf(null)
    }

    viewingSync?.let {
        Dialog(
            onDismissRequest = { viewingSync = null },
        ) {
            Surface(
                shadowElevation = 10.dp,
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(
                    modifier = Modifier.padding(15.dp)
                ) {
                    Text("Device URI: ${it.deviceUri}")
                    Divider()
                    Text("Device Name: ${it.deviceName}")
                    Divider()
                    Text("Drive ID: ${it.driveId}")
                    Divider()
                    Text("Drive Name: ${it.driveName}")
                    Divider()
                    Text("Sync Type: ${it.syncType.displayName}")
                    Divider()
                    Text("Checksum Mismatch Behavior: ${it.checksumMismatchBehavior.displayName}")
                    Divider()
                    Text("Delete On: ${it.deleteOn.displayName}")
                }
            }
        }
    }

    Surface(
        shadowElevation = 5.dp,
        modifier = Modifier.padding(30.dp, 10.dp),
        shape = RoundedCornerShape(5.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextButton(onClick = {
                onCreateSyncPressed()
            }) {
                Text("Create new sync")
            }
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                items(syncs) { sync ->
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp)
                            .clickable { viewingSync = sync }
                    ) {
                        Text(sync.deviceName)
                        IconButton(onClick = {
                            onDeleteSync(sync)
                        }) {
                            Icon(painter = painterResource(id = R.drawable.baseline_cancel_24), contentDescription = "Delete sync")
                        }
                    }
                    Divider()
                }
            }
        }
    }
}