package com.example.myapplication


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import kotlin.math.absoluteValue
import com.example.driftui.core.*


@Composable
fun metro(){
    DriftView() {
        val viewModel: MetroViewModel = StateObject()
        var source = viewModel.source
        var destination = viewModel.destination


        NavigationStack(
            Modifier.toolbarStyle(foregroundColor = Color.white).frame(width = 450, height = 70)
                .navigationBarBackButtonHidden(true).preferredColorScheme(lightMode)
        ) {

            VStack() {

                ZStack() {

                    ZStack(Modifier.padding(trailing = 60)) {
                        TextField(
                            "Source",
                            value = source,
                            Modifier.clipShape(RoundedRectangle(radius = 20)).opacity(0.7)
                                .background(Color.shazan).foregroundStyle(Color.white)
                                .frame(width = 300, height = 45).offset(x = 10)
                        )
                        Image("magnifyingglass", Modifier.scaleEffect(3).padding(leading = 80))
                    }

                    ZStack(Modifier.padding(top = 125).padding(trailing = 60)) {
                        TextField(
                            "Destination",
                            value = destination,
                            Modifier.clipShape(RoundedRectangle(radius = 20)).opacity(0.7)
                                .background(Color.shazan).foregroundStyle(Color.white)
                                .frame(width = 300, height = 45).offset(x = 10)
                        )
                        Image("magnifyingglass", Modifier.scaleEffect(3).padding(leading = 80))

                    }

                    NavigationLink(destination = { dest() }) {
                        Rectangle(
                            300,
                            45,
                            Modifier.padding(top = 125).padding(trailing = 60).opacity(0.0)
                        )
                    }
                    NavigationLink(destination = { source() }) {
                        Rectangle(300, 50, Modifier.padding(trailing = 60).opacity(0.0))
                    }
                }
                Button(action = {
                    swap(destination, source)
                }, Modifier.offset(x = 155, y = -130).scaleEffect(0.4)) {
                    Image("img")

                }


                Text("Entered text is: ${source.value}")
                Text("Entered password is: ${destination.value}")

                Rectangle(
                    width = statusBarWidth.value.toInt(),
                    height = statusBarHeight.value.toInt() + 40.u.toInt(),
                    modifier = Modifier.foregroundStyle(
                        linearGradient(
                            listOf(
                                Color.green,
                                Color.blue
                            )
                        )
                    )
                )
            }
            toolbar(
                Modifier.frame(
                    width = statusBarWidth.value,
                    height = statusBarHeight.value + 40.u
                )
            ) {
                ToolbarItem(ToolbarPlacement.Center) {

                }
                ZStack(Modifier.padding(top = -statusBarHeight.value * 2 - 5.u)) {
                    Rectangle(
                        width = statusBarWidth.value.toInt() + 4.u.toInt(),
                        height = statusBarHeight.value.toInt() + 40.u.toInt(),
                        modifier = Modifier.foregroundStyle(
                            linearGradient(
                                listOf(
                                    Color.green,
                                    Color.blue
                                )
                            )
                        )
                    )


//                    ToolbarItem(placement = ToolbarPlacement.Leading) {
//                        Button(action = {
//                            showMenu.set(true)
//                        }) {
//                            Image("menu", Modifier.frame(width = 35.u).padding(top = 35.u))
//                        }
//                    }
//                    ToolbarItem(placement = ToolbarPlacement.Center) {
//                        Image("removed_logo", Modifier.frame(95.u, 120.u).offset(y = 19.u.toInt()))
//                    }
                }
            }
        }
    }
}

@Composable
fun source() {
    val viewModel: MetroViewModel = StateObject()

    DriftView {
        val source = viewModel.source
        val query = viewModel.query
        val linewidth = (15 * deviceWidth.value) / 16
        val Dismiss = Dismiss()

        val activeList = if (query.value.isNotEmpty()) {
            viewModel.allStations.fuzzySearch("name", query.value)
        } else {
            viewModel.sortedList
        }

        // FIX: Force VStack to take full height so it doesn't get centered when content is small
        VStack(
            alignment = Alignment.Top,
            modifier = Modifier
                .frame(height = deviceHeight.value)
                .padding(top = 5) // Safe top padding
        ) {

            HStack {
                TextField(
                    placeholder = "Type to filter station names",
                    value = query,
                    Modifier.font(system(size = 20)).frame(width = deviceWidth.value, height = 40)
                )
            }

            Divider(
                color = Color.Blue,
                thickness = 2,
                width = linewidth.absoluteValue,
                modifier = Modifier.padding(trailing = 2)
            )

            // LIST
            List(items = activeList, modifier = Modifier.padding(top = 3)) { station ->
                // Color Logic
                val distinctColors = mutableSetOf<String>()
                for (line in station["lines"]) {
                    val rawString = line.string
                    if (rawString.contains("_")) {
                        distinctColors.add(rawString.substringBefore("_"))
                    }
                }

                val finalColor = if (distinctColors.size > 1) {
                    Color.Black
                } else {
                    when (distinctColors.firstOrNull()?.uppercase()) {
                        "RED" -> Color.Red
                        "BLUE" -> Color.Blue
                        "YELLOW" -> hex("#FFCC00")
                        "GREEN" -> Color.Green
                        "VIOLET" -> Color(0xFF800080)
                        "PINK" -> Color(0xFFFFC0CB)
                        "MAGENTA" -> Color.Magenta
                        "ORANGE", "ORANGE/AIRPORT" -> Color(0xFFFFA500)
                        "AQUA" -> Color(0xFF00FFFF)
                        "RAPID" -> Color.Cyan
                        "GRAY" -> Color.Gray
                        else -> Color.Gray
                    }
                }

                Button(action = {
                    source.value = station["name"].string
                    Dismiss()
                }) {
                    HStack {
                        // Icon
                        if (distinctColors.size == 1) {
                            Rectangle(
                                width = 50,
                                height = 50,
                                Modifier.scaleEffect(0.8).clipShape(RoundedRectangle(8))
                                    .foregroundStyle(color = finalColor)
                            )
                        } else {
                            ZStack {
                                Triangle(
                                    36,
                                    36,
                                    modifier = Modifier.padding(leading = 8)
                                        .foregroundStyle(Color.Gray)
                                )
                                Arrow(
                                    38,
                                    8,
                                    modifier = Modifier.foregroundStyle(
                                        color = Color.Black.copy(alpha = 0.3f)
                                    ).rotationEffect(-45).offset(y = 4)
                                )
                            }
                        }

                        // Text
                        VStack {
                            Text(
                                station["name"].string,
                                Modifier.font(system(size = 20, weight = medium))
                                    .foregroundStyle(Color.Black.copy(alpha = 0.7f))
                                    .frame(width = deviceWidth.value, height = 50)
                            )

                            if (distinctColors.size > 1) {
                                Text(
                                    "Interchange",
                                    Modifier.foregroundStyle(Color.Red.copy(alpha = 0.5f))
                                        .padding(trailing = 5).offset(y = -20)
                                )
                            }
                        }
                    }
                }
            }
        }

        toolbar(Modifier.frame(width = statusBarWidth.value, height = statusBarHeight.value)) {
            ToolbarItem(placement = ToolbarPlacement.Leading) {
                HStack {}
            }
        }
    }
}

@Composable
fun dest(){
    DriftView() {
        val viewModel: MetroViewModel = StateObject()
        var destination = viewModel.destination
        val Dismiss = Dismiss()
        val linewidth = (15* deviceWidth.value)/16

        VStack() {
            HStack() {
                TextField(
                    placeholder = "Type to filter station names",
                    value = destination,
                    Modifier.font(system(size = 20)).frame(width = deviceWidth.value, height = 40)
                        .offset(x = 5, y = 15)
                )
            }
            Divider(
                color = Color.blue,
                thickness = 2,
                width = linewidth.absoluteValue,
                modifier = Modifier.padding(trailing = 2)
            )


            List(items = viewModel.sortedList) { station ->
                HStack() {
                    Rectangle(width = 50, height = 50, Modifier.scaleEffect(0.8))
                    Button(action = {
                        destination.value = station["name"].string
                        Dismiss()
                    }) {
                        Text(
                            station["name"].string,
                            Modifier.frame(width = deviceWidth.value, height = 50)
                        )

                    }

                }
            }

        }
        toolbar(Modifier.frame(width = deviceWidth.value, height = statusBarHeight.value)) {
            ToolbarItem(placement = ToolbarPlacement.Leading) {
                HStack() {
//                    Text("Destination", Modifier.foregroundStyle(Color.white).font(system(size = 22, weight = medium)).padding(top = 40))

                }

            }

        }
    }

}