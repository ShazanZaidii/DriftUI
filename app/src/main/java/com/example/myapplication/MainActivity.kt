package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposableOpenTarget
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment.Companion.Rectangle
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.zIndex
import androidx.core.view.WindowCompat
import com.example.driftui.DriftView
import com.example.driftui.NavigationStack
import com.example.driftui.VStack
import com.example.driftui.darkMode
import com.example.driftui.lightMode
import com.example.driftui.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
//        WindowCompat.setDecorFitsSystemWindows(window, true)
        super.onCreate(savedInstanceState)
        setContent {

            test3()

        }
    }
}

data class User(
    var name: String,
    var age: Int?
)
@Composable
fun test3(){

    var user1 = DriftStore("user1", User("John", 30))
    var nametext = remember {State("")}
    var agetext = remember {State("")}

    DriftView() {
        VStack {
            Text("Values in Local database are:", Modifier.font(system(size = 23, weight = light)))
            Text("Name : ${user1.value.name}", Modifier.font(system(size = 18, weight = light)))

            Text("Age : ${user1.value.age?.toString()?:""}", Modifier.font(system(size = 18, weight = light)) )

            Text("Edit:",
                Modifier.foregroundStyle(Color.shazan).padding(trailing = 300)
                    .font(system(size = 32, weight = bold)))

            TextField("Enter name",value = nametext,
                Modifier.frame(width= 370, height = 40).clipShape(RoundedRectangle(12))
                    .background(Color.lightGray).foregroundStyle(Color.purple))

            TextField("Enter Age",value = agetext,
                Modifier.frame(width= 370, height = 40).clipShape(RoundedRectangle(12))
                    .background(Color.lightGray).foregroundStyle(Color.purple))

            Button(action = {
                user1.edit{
                name = nametext.value
                age = agetext.value.toIntOrNull()
            }}, Modifier.padding(top = 20)) {
                Text("Save",
                    Modifier.foregroundStyle(Color.white).frame(width = 130, height = 35)
                        .clipShape(RoundedRectangle(12)).background(Color.shazan)
                        .font(system(size = 18, weight = bold)))
            }

            Button(action = {
                user1.edit{
                    name = ""
                    age = null
                }}) {
                Text("Nuke", Modifier.foregroundStyle(Color.white).frame(width = 130, height = 35)
                    .clipShape(RoundedRectangle(12)).background(Color.gray)
                    .font(system(size = 18, weight = bold)))
            }
        }


    }
}