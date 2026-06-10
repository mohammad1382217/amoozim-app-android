package com.amoozim.creator.ui.shell

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.amoozim.creator.core.designsystem.component.AmoozimTopBar
import com.amoozim.creator.core.designsystem.component.EmptyState

/**
 * Stand-in for tabs not yet ported in this foundation slice (Users, Publish, Wallet,
 * My Courses). Each is a real, navigable destination wired into the shell — the
 * screen body is the only thing left to implement.
 */
@Composable
fun PlaceholderScreen(title: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize()) {
        AmoozimTopBar(title = title)
        Box(modifier = Modifier.weight(1f)) {
            EmptyState(message = "این بخش به‌زودی در نسخه اندروید اضافه می‌شود.")
        }
    }
}
