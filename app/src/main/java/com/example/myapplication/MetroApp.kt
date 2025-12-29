package com.example.myapplication


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.driftui.DriftView
import com.example.driftui.Notification
import com.example.driftui.onTapGesture
import com.example.driftui.*

@Composable
fun metro(){
    var source = State("")
    var destination = State("")
    DriftView() {
        VStack() {
//            Divider(color = Color.shazan)

            TextField("Enter Source",value = source, Modifier.clipShape(RoundedRectangle(radius = 20)).opacity(0.7).background(Color.shazan).foregroundStyle(Color.white).frame(width= 370, height = 45).align(x = -1, y = 0))
            SecureField("Enter Destination",value = destination, Modifier.clipShape(RoundedRectangle(radius = 20)).opacity(0.7).background(Color.shazan).foregroundStyle(Color.white).frame(width= 370, height = 45).align(x = -1, y = 0))
            Text(source.value)
        }
    }
}