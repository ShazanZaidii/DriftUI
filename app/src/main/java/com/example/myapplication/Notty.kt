package com.example.myapplication

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.driftui.core.DriftView
import com.example.driftui.core.Notification
import com.example.driftui.core.Button
import com.example.driftui.core.Spacer
import com.example.driftui.core.VStack
import com.example.driftui.core.bold
import com.example.driftui.core.font
import com.example.driftui.core.system

@Composable
fun notty() {
    DriftView() {

        VStack(spacing = 20) {
            Spacer()

            Text("Drift Notification Test", Modifier.font(system(22, bold)))

            // ---------------------------------------------------------
            // SIMPLE NOTIFICATION
            // ---------------------------------------------------------
            Button({
                Notification.send {
                    title = "Hello!"
                    body = "This is a Drift notification."
                }
            }) {
                Text("Send Simple Notification")
            }

            // ---------------------------------------------------------
            // ONGOING NOTIFICATION (cannot swipe away)
            // ---------------------------------------------------------
            Button({
                Notification.send {
                    title = "Uploading…"
                    body = "This notification cannot be dismissed."
                    isOngoing = true
                }
            }) {
                Text("Send Ongoing Notification")
            }

            // ---------------------------------------------------------
            // TAP ACTION NOTIFICATION
            // ---------------------------------------------------------
            Button({
                Notification.send {
                    title = "New Message"
                    body = "Tap to open ChatView()."
                    onTap = {
                    }
                }
            }) {
                Text("Send Tap Action Notification")
            }

            // ---------------------------------------------------------
            // SCHEDULED NOTIFICATION
            // ---------------------------------------------------------
            Button({
                Notification.schedule(5) {
                    title = "Reminder"
                    body = "This appeared 5 seconds later."
                }
            }) {
                Text("Schedule Notification (5 seconds)")
            }

            Spacer()
        }
    }
}