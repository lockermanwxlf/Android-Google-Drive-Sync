package com.lockermanwxlf.drivesync.utils.data

enum class DeleteOn(val displayName: String) {
    NEVER("Never Delete"),
    ON_UPLOAD("Delete On Upload"),
    ON_PRESENT("Delete if in Drive")
}