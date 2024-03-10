package com.lockermanwxlf.drivesync.ui.newsync

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lockermanwxlf.drivesync.utils.data.SyncType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyncTypePicker(
    syncType: SyncType,
    onSyncTypeChanged: (SyncType)->Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp, 0.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Sync Type")
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SyncType.entries.forEach {
                FilterChip(
                    selected = it == syncType,
                    onClick = { onSyncTypeChanged(it) },
                    label = { Text(text = it.displayName) }
                )
            }
        }
    }
}