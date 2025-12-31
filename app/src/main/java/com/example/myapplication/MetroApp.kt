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
    DriftView() {
        var source = Storage(key = "source", defaultValue = "")
        var destination = Storage(key = "destination", defaultValue = "")
        //Graph:
        val graph = readJson("metro_graph_final_fixed.json")
        val allStations = graph["stations"]
        val mundkaExact = allStations.search("name", "Mundka")
        val sortedList = allStations.sort("name")
        val searchResults = allStations.fuzzySearch("name", "Mndka")



        NavigationStack(Modifier.toolbarStyle(backgroundColor = Color.shazan, foregroundColor = Color.white).frame(width = 450, height = 70).navigationBarBackButtonHidden(true)) {

            VStack() {

                ZStack() {

                    ZStack(Modifier.padding(trailing = 60)) {
                        TextField(
                            "Source",
                            value = source,
                            Modifier.clipShape(RoundedRectangle(radius = 20)).opacity(0.7)
                                .background(Color.shazan).foregroundStyle(Color.white)
                                .frame(width= 300, height = 45).offset(x = 10))
                        Image("magnifyingglass", Modifier.scaleEffect(3).padding(leading = 80))
                    }
                    Button(action = {
                        swap(destination, source)
                    }, Modifier.offset(x = 155, y=30).scaleEffect(0.4)) {
                        Image("img")

                    }
                    ZStack(Modifier.padding(top = 125).padding(trailing = 60)) {
                        TextField(
                            "Destination",
                            value = destination,
                            Modifier.clipShape(RoundedRectangle(radius = 20)).opacity(0.7)
                                .background(Color.shazan).foregroundStyle(Color.white)
                                .frame(width= 300, height = 45).offset(x = 10))
                        Image("magnifyingglass", Modifier.scaleEffect(3).padding(leading = 80))

                    }

                    NavigationLink(destination = {dest()} ) {
                        Rectangle(300, 45, Modifier.padding(top = 125).padding(trailing = 60).opacity(0.2))
                    }
                    NavigationLink(destination = {source()} ) {
                        Rectangle(300, 50, Modifier.padding(trailing = 60).opacity(0.2))
                    }
                }
//            List(items = sortedList) { station ->
//                // Access properties using ["key"].string
//                Text(station["name"].string)
//            }

                Text("Entered text is: ${source.value}")
                Text("Entered password is: ${destination.value}")
            }
            toolbar(Modifier.frame(width = 450, height = 95)) {

                ToolbarItem(placement = ToolbarPlacement.Leading) {
                    Text("Route", Modifier.foregroundStyle(Color.white).font(system(size = 22, weight = medium)).padding(top = 40))
                }
            }
        }
    }
}

@Composable
fun source(){
    DriftView() {
        toolbar(Modifier.frame(width = 450, height = 95)) {
            ToolbarItem(placement = ToolbarPlacement.Leading) {
                HStack() {
                    Text("Source", Modifier.foregroundStyle(Color.white).font(system(size = 22, weight = medium)).padding(top = 40))

                }

            }

        }
    }

}

@Composable
fun dest(){
    DriftView() {
        toolbar(Modifier.frame(width = 450, height = 95)) {
            ToolbarItem(placement = ToolbarPlacement.Leading) {
                HStack() {
                    Text("Destination", Modifier.foregroundStyle(Color.white).font(system(size = 22, weight = medium)).padding(top = 40))

                }

            }

        }
    }

}