package com.lockermanwxlf.drive

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DriveFolder(
    val id: String,
    val name: String,
    val parent: DriveFolder?,
    val subfolders: List<DriveFolder>
): Parcelable
