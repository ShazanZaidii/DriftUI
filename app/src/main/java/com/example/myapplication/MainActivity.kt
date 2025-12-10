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

data class Model(
    var name: String,
    var age: Int?
)
@Composable
fun test3(){

    var users = DriftStore("user1", Model::class)





    val sortRule = Sort(Model::age, order = Order.Ascending)
    //Using SortRule seperately
//    val sorted = users.sort(sortRule)

    //Or: Using SortRule inside with age sorted Descendingly and name sorted Ascendingly
    val sorted = users.sort(Sort(Model::age, Order.Descending), Sort(Model::name))

//  Or: Both sorted Ascendingly
//    val sorted = users.sort(Sort(Model::age), Sort(Model::name))

//    **************************
    //EDIT:

//    // 1. Find the user (returns null if not found)
//    val sameer = users.items.find { it.name == "Umair" }
//
//// 2. Edit safely
//    if (sameer != null) {
//        users.edit(sameer) {
//            age = 53 // Update the specific field
//        }
//    }
//*********************


    DriftView() {
        if(users.items.isEmpty()) {
            Text("Add", Modifier.onTapGesture(action = {
                users.add(Model("Sameer", 12))
                users.add(Model("Akbar", 3))
                users.add(Model("Umair",33))
            }))
        }
        VStack() {
            Spacer(140)
            if(!users.items.isEmpty()){
                List(sorted){s->
                    Text("${s.age}.  ${s.name}")
                }


            }
        }
        Text("Nuke", Modifier.padding(top = 120).onTapGesture(action = {users.removeAll()}))


    }

}