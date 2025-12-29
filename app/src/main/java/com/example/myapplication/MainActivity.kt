package com.example.myapplication

import android.app.Notification
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposableOpenTarget
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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
                metro()
        }
    }


    //Data Model
    data class Model(
        var id: Long? = null,
        var name: String,
        var age: Int
    )

    @Composable
    fun test4() {
        //Creating a Store
        val users = DriftStore("database", Model::class)

        //Editing by first finding the id then passing it to edit.
        val umair = users.items.find { it.name == "Umair" }
        users.edit(umair) {
            age = 6
        }

        val selected2 = users.filter { it.name.startsWith("Ak") && it.name.endsWith("a") }

        // Sorting users:
        val sorted = users.sort(Sort(Model::name, Order.Descending)) //Using Descending Order
        //CODE: val sorted = users.sort(Sort(Model::name)) //Using Ascending Order - No need to explicitly mention. It will be Ascending by default.

        //For Compound Sorting-
        // Code: val sorted = users.sort(Sort(Model::name, Order.Descending), Sort(Model::age, Order.Descending)) //Using Descending Order

        DriftView() {
            //Create
            if (users.items.isEmpty()) {

                Text("Add", Modifier.onTapGesture(action = {
                    users.add(Model(1, "Sameer", 12))
                    users.add(Model(2, "Akbar", 7))
                    users.add(Model(3, "Akmala", 43))
                    users.add(Model(4, "Akshar", 21))
                    users.add(Model(5, "Umair", 33))
                }))
            }

            VStack {

                if (!users.items.isEmpty()) {
                    Text("Original Data:", Modifier.font(system(18, light)))
                    List(users.items) { s ->
                        Text("${s.age}.  ${s.name}")
                    }

                    //Original List:

                    Spacer(13)
                    Text("Sorted Data:", Modifier.font(system(18, light)))
                    List(sorted) { s ->
                        Text("${s.age}.  ${s.name}")
                    }
                    Spacer(13)
                    List(selected2) { s ->
                        Text("${s.age}.  ${s.name}")
                    }

                }

                Text(
                    "Nuke",
                    Modifier.padding(top = 70).onTapGesture(action = { users.removeAll() })
                )
            }
        }
    }

}