package com.lockermanwxlf.drivesync.ui.newsync

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import com.lockermanwxlf.drivesync.utils.data.DeleteOn
import com.lockermanwxlf.drivesync.utils.data.SyncType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteOnPicker(
    deleteOn: DeleteOn,
    syncType: SyncType,
    onChange: (DeleteOn) -> Unit
) {
    AnimatedVisibility(visible = syncType == SyncType.UPLOAD) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Delete from device")
            Row {
                DeleteOn.entries.forEach {
                    FilterChip(
                        selected = deleteOn == it,
                        onClick = { onChange(it) },
                        label = { Text(text = it.displayName) }
                    )
                }
            }
        }
    }
}