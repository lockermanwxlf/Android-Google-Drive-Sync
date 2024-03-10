package com.lockermanwxlf.drivesync.ui.home

import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient

@Composable
fun AccountStatus(
    account: GoogleSignInAccount?,
    signInLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    signInClient: GoogleSignInClient,
    onLogOut: ()->Unit
) {
    Surface(
        shadowElevation = 5.dp,
        modifier = Modifier.padding(5.dp),
        shape = RoundedCornerShape(5.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(10.dp)
        ) {
            Text(
                text = account?.let { "Logged in as ${it.email?:it.displayName?:"`nodisplay`"}" }?:"Not logged in.",
                modifier = Modifier.animateContentSize(),
                textAlign = TextAlign.Center
            )
            if (account == null) {
                TextButton(onClick = {
                    signInLauncher.launch(signInClient.signInIntent.putExtra("input", 1))
                }) {
                    Text("Sign in")
                }
            } else {
                TextButton(onClick = {
                    signInClient.signOut()
                        .addOnSuccessListener {
                            onLogOut()
                        }
                }) {
                    Text("Log out")
                }
            }
        }
    }

}