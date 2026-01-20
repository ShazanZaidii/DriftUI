<h1>Till now it supports: </h1>
*NOTE: Do not forget to wrap everything in DriftView{} it acts as an initialiser and manager for other blocks. Also by default In driftView useSafeArea = true, you can set it to false to allow the elements/ Views to even fall out of the screen.

<h2>1. H,V and ZStacks - Support alignment now!</h2>
Example:

```
@Composable
fun Test(){
    DriftView() {
        VStack(modifier = Modifier.fillMaxSize().alignment(Alignment.Center)) {
            Text("Hello")
            Text("Hey")
        }
    }
}
```
<h2>2. Paddings Example- Text("hello", Modifier.padding(leading = 20) </br>
<h2>3. Background & Color</h2>
<h2>4. Shapes, ClipShape - Remember clipShape should be used before giving a background color</h2>
<h2>5. Frame</h2>
<h2>6. font modifier - To use this we need to remove default "import androidx.compose.material3.Text" and add "import com.example.driftui.Text" (if you dont modify)</h2>
<h1>7. Images (the file shoud be saved in - app -> src -> main -> res -> drawable -> Your image )</h1>

```
Image("//Image name goes here")

```
<h1>8. TextField & SecureField(for passwords)</h1> [Use align to adjust to hard left, right or center using -1, 0 and 1. To adjust precise position of text wrt frame use .offset]

```
// Defining variables
    var source = State("")
    var destination = State("")

// Using in TextField and SecureField
    TextField(
                "Enter Source",
                value = source,
                Modifier.clipShape(RoundedRectangle(radius = 20)).opacity(0.7)
                    .background(Color.shazan).foregroundStyle(Color.white)
                    .frame(width= 370, height = 45).align(x = -1, y = 0)
            )

SecureField(
                "Enter Destination",
                value = destination,
                Modifier.clipShape(RoundedRectangle(radius = 20)).opacity(0.7)
                    .background(Color.shazan).foregroundStyle(Color.white)
                    .frame(width= 370, height = 45).align(x = -1, y = 0)
            )

// Accessing Variables values:
    Text("Entered text is: ${source.value}")
    Text("Entered password is: ${destination.value}")


```   
<h1>9. Buttons</h1>

```
Button(
                        action = {
                            username.set("")
                            password.set("")
                        },
                        Modifier
                            .frame(width = 280, height = 50)
                            .clipShape(Capsule()) // Clip before background
                            .background(Color(0xFF567779)) // Using the new Swifty color
                    ) {
                        Text("Clear",
                            Modifier
                                .foregroundStyle(Color.white) // Using the new Swifty color
                                .font(system(size = 26, weight = bold))
                        )
                    }
```




<h1>10. Divider()</h1>

```
Divider(color = Color.white, thickness = 5)
```

<h1>11. Lists </h1>

```
// Simple Implementation:
List(alignment = center, modifier = Modifier.padding(top = 50)){
    Text("Hello World")
    Divider()
    Text("banana")
     }

// Using Arrays:
val food = listOf("Apple", "Banana", "Orange", "Grape")
List(food, alignment = top, modifier = Modifier.padding(top = 50)) { item ->
     Text(item, modifier = Modifier.padding(16))
     }

Or you can also say items = array name as:
List(items = food, alignment = top, modifier = Modifier.padding(top = 50)) { item ->
     Text(item, modifier = Modifier.padding(16))
     }

```

<h1>12. MVVM (For sample refer - https://github.com/ShazanZaidii/DriftUI/tree/main/MVVM%20Implementation )</h1>

<h1>13. NavigationStack and Toolbar</h1>

Implementation:
<img width="1728" height="1117" alt="Screenshot 2025-11-18 at 10 14 22 PM" src="https://github.com/user-attachments/assets/ed1cbe48-a8b2-4415-b0d6-5cd4fa2f3eac" />

<h1>14. preferredColorScheme:</h1>

```
NavigationStack(Modifier.preferredColorScheme(lightMode // or DarkMode)){
//Content here
}
```

<h1>15. Dismis()-</h1>

```
val dismiss = Dismiss()
Button(dismiss) { Text("Dismiss" , Modifier.font(system(size = 28, weight = bold)))}
```

<h1>16. Toolbar with modifiers -       </h1>

<img width="1728" height="1117" alt="Screenshot 2025-11-20 at 1 44 09 PM" src="https://github.com/user-attachments/assets/0dbb3a48-4e5a-44e0-a403-e0474cee3b9c" />


```
//**Inside OnCreate**- 
//Set WindowCompat.setDecorFitsSystemWindows(window, true) to make toolbar be pushed below the camera punch and
// either WindowCompat.setDecorFitsSystemWindows(window, false)  or just remove this line to make status bAr overlay on Top of Toolbar


```

```
NavigationStack(Modifier.toolbarStyle(foregroundColor = Color.shazan, backgroundColor = Color.DarkGray)) {
        DriftView {
            toolbar(
                Modifier.toolbarStyle(backgroundColor = Color.yellow)
                    .frame(width = 450, height = 100)
                    .clipShape(RoundedRectangle(68))
            ) {
                ToolbarItem(placement = ToolbarPlacement.Center) {
                        Text(
                            "I am a Toolbar!", Modifier.frame(width = 290)
                                .padding(top = 38).padding(leading = 55).foregroundStyle(Color.white)
                                .font(system(size = 26, weight = bold))
                        )
                }
            }


        }

    }
```

<h1>17. Toggle: [Can accept modifiers- onColor, offColor, & thumbColor] </h1>

```
// Type 1:
val wifi = State(true)
Toggle("Simple", value = wifi)

//Type 2:
val wifi = State(true)
Toggle(value = wifi, Modifier.padding(top = 5)){
                    Text("Switch", Modifier.font(system(size =18, weight = bold)))
                }

// Type 3: (Simple with custom Toggle color Modifiers) :
Toggle("Hello", value = wifi, Modifier.toggleStyle(onColor = Color.Magenta, offColor = Color.Transparent, thumbColor = Color.Green) )

// Type 4: (Advanced with custom Toggle color Modifiers)
Toggle(value = wifi, Modifier.toggleStyle(onColor = Color.teal, offColor = Color.gray, thumbColor = Color.DarkGray).padding(bottom = 120)){
                    Text("Switch", Modifier.foregroundStyle(Color.white))
                }


//                       To access the underlying value of the variable wifi-
                         Text("Current value is- ${wifi.value}")


```

<h1>18. ScaleEffect, RotationEffect & Offsets: (Yes, offsets can be negative too, Below is an example of how to use offsets and scaleEffect inside a Toggle):</h1>

```
 Toggle("Hello", value = wifi, Modifier
.padding(top= 220)
.toggleStyle(onColor = Color.Magenta, offColor = Color.Transparent, thumbColor = Color.Green)
.rotationEffect(degrees = 180)
.scaleEffect(3))

```

<h1>19. Opacity:</h1>
    
```
Text("Shazan", Modifier.foregroundStyle(Color.white).font(system(size = 37)).zIndex(1f).padding(top = 90).opacity(0.5))

```
<h1>20. Sliders:</h1>

```
Type1: Simple (no modifiers)
var size = State(10)
Slider(value = size)

Type2: [Advanced]
var volume = State(50)
Slider(
       value = volume,
       range = 0..200,
       step = 10,
       Modifier.sliderStyle(
       activeTrackColor = Color.shazan,
       thumbColor = Color.teal,
       stepColor = Color.white,
       stepOpacity = 0.05)
       .frame(width = 250, height = 30)
        )

```
<h1>21. onDoubleTap, onTripleTap, onHold and untilHold (which accepts parameters) -</h1>

```
@Composable
fun LoginScreenView2() {
    var isHighlighted = State(false)
    NavigationStack(Modifier.toolbarStyle(foregroundColor = Color.shazan, backgroundColor = Color.shazan).preferredColorScheme(lightMode)) {
        DriftView {
            var value = State(0)

                VStack {
                   Text("Hold Me", Modifier.untilHold(onPress = { isHighlighted.set(true) }, onRelease = {isHighlighted.set(false)}).font(system(size = 48, weight = bold)).foregroundStyle(Color.shazan))
                    Text("${isHighlighted.value}", Modifier.padding(top = 50))
                    Slider(value = value, range = 0..100, step = 12,  modifier = Modifier.sliderStyle(stepOpacity = 0.1).rotationEffect(-90).offset(x= 120,y = 180))
                    Button(action = {isHighlighted.toggle()}) {
                        Text("Touch Me")
                    }

                    toolbar(Modifier.frame(height = 110)) {
                        ToolbarItem(placement = ToolbarPlacement.Center) {
                            Text("Gesture Control", Modifier.foregroundStyle(Color.white).font(system(size = 28, weight = bold)).padding(top = 55))
                        }
                    }
                }



        }

    }
}

```
<h1>22. Sheets (To control them use a variable toggle dont use dismiss or app will crash)</h1>

```
@Composable
fun MyScreen() {
    val showSheet = State(false)

    NavigationStack(
        Modifier.sheet(
            isPresented = showSheet,
            detents = listOf(0.2, 0.5, 0.95),
            initialDetent = 0.2,
            showGrabber = true,
            cornerRadius = 20,
            allowDismiss = true
        ) {
            // Sheet content
            VStack(spacing = 20) {
                Text("Sheet Content", Modifier.font(system(size = 24, weight = bold)))
                Text("Current state: ${showSheet.value}")

                Button(action = { showSheet.set(false) }, Modifier.offset(x = 160, y = -100)) {
                    Text("CLOSE X", Modifier.font(system(size = 18)))
                }

                Text("Swipe down or tap outside to dismiss")
            }
        }
    ) {
        // Main screen content
        DriftView {
            VStack(spacing = 20) {
                Button(action = { showSheet.toggle() }) {
                    Text("Show Sheet", Modifier.font(system(size = 28, weight = bold)))
                }

                if (showSheet.value) {
                    Text("Sheet is open!", Modifier.padding(top = 20))
                }

                Text("State: ${showSheet.value}")
            }
        }
    }
}

```

<h1>23. Border & Shadow Modifiers: [shazan is my custom color] </h1>

RoundedRectangle(width = 50, height = 50, cornerRadius = 8, Modifier.foregroundStyle(color).border(color = Color.shazan, width = 2).shadow(radius = 18, color = Color.yellow))

<h1>24. PenTool 🎉</h1>

```
  PenTool(
                color = Color.Magenta,
                width = 18f,
                smooth = false,
                modifier = Modifier
                    .frame(width = 330, height = 500)
                    .background(Color.White)
                    .border(width = 3, color = Color.Black)
            )
```

<h1>25. EraserTool (2 types):</h1>

```
Type 1: [Erases strokewise]
EraserTool(
                        path = pathState,
                        radius = 30f,
                        type = EraserType.Line,   //difference
                        modifier = Modifier
                            .frame(300, 400)
                    )

Type 2: [Like Real Eraser]
EraserTool(
                        path = pathState,
                        radius = 30f,
                        type = EraserType.Area, //difference
                        modifier = Modifier
                            .frame(300, 400)
                    )
```
<h1>26. ColorPicker, Undo/Redo, SaveToGallery: [Complete Code]</h1>

```

@Composable
fun DrawingScreen2() {

    // 1. INITIALIZATION: One line. No 'remember', no 'Context'.
    val controller = DrawController()

    DriftView {
        VStack(spacing = 20) {

            // ---------Top Bar
            HStack(Modifier.padding(horizontal = 20, vertical = 120)) {
                Text("Simple Draw", Modifier.font(system(24, bold)))
                Spacer()

                // Human-readable actions
                Button(action = { controller.undo() }) {
                    Text("Undo", Modifier.foregroundStyle(Color.blue))
                }

                Button(action = { controller.redo() }) {
                    Text("Redo", Modifier.foregroundStyle(Color.blue))
                }

                // "save()" handles permissions, bitmap creation, and context automatically
                Button(action = { controller.save() }) {
                    Text("Save", Modifier.foregroundStyle(Color.green))
                }
            }

            // ----------CANVAS:
            // The controller handles the Pen/Eraser layering logic internally.
            DriftCanvas(
                controller = controller,
                Modifier
                    .weight(1f)
                    .frame(450, height = 800)
                    .padding(bottom = 120)
                    .background(Color.white)
                    .cornerRadius(20)
                    .shadow(2)
            )

            //-------- Controls:
            HStack(spacing = 20, modifier = Modifier.padding(bottom = 30)) {

                // 1. Color Picker (Visible when drawing)
                if (!controller.eraser.value) {
                    ColorPicker(selectedColor = controller.color)
                } else {
                    // 2. Eraser Type Selector (Visible ONLY when erasing)
                    // This is where you use the Types!
                    HStack(spacing = 10) {
                        Button(action = { controller.eraserType.set(EraserType.Area) }) {
                            Text(
                                "Real Eraser",
                                // Highlight if selected
                                Modifier.foregroundStyle(if(controller.eraserType.value == EraserType.Area) Color.black else Color.gray)
                            )
                        }

                        // Divider
                        Capsule(width = 1, height = 20, Modifier.background(Color.lightGray))

                        Button(action = { controller.eraserType.set(EraserType.Line) }) {
                            Text(
                                "Line Eraser",
                                // Highlight if selected
                                Modifier.foregroundStyle(if(controller.eraserType.value == EraserType.Line) Color.black else Color.gray)
                            )}}}

                Spacer()

                // 3. Main Toggle (The On/Off Switch)
                Button(action = { controller.toggleEraser() }) {
                    VStack {
                        Capsule(width = 40, height = 4,
                            Modifier.foregroundStyle(if (controller.eraser.value) Color.red else Color.gray))
                        Text("Eraser", Modifier.foregroundStyle(Color.gray))
                    }}}}}}




```
<h1>27. SOUND ENGINE (Advanced!):</h1>

```
//Simple:
   playSound(file = "chaloo.mp3")

//Advanced: (By passing parameters):
playSound(file = "lofi.mp3", pitch = 1.0, speed = 1.2, pan = 1.0, panEnd = -1.0,backgroundPlay = false, loop = true, stopSystemAudio = true)

List of Acceptable Paramters:
        file: String,
        volume: Double, // To set volume
        pitch: Double,
        speed: Double,
        pan: Double,  // pan = 1 sets the audio to hard right, while -1 sets it to hard left and 0 is the middle.
        panEnd: Double?,   // If pan is set to -1 and panEnd is set to 1 then sound will start from left and will go into the right ear. 
        startTime: Int, //In milliseconds
        endTime: Int, //In milliseconds
        fadeIn: Int,
        backgroundPlay: Boolean, //To allow app to keep playing music when the user has minimised it.
        stopSystemAudio: Boolean, //To stop all other system sounds to play current sound
        override: Boolean, //Set to true if you dont want the sound to start echoing if user spam presses a button
        loop: Boolean // To loop the audio

```
<h1>28. Haptics:</h1>

```
//[You can use as action in buttons or however you like, You may feel selection is stronger than success so use it as per your desire.]

//Haptic1:
haptic(Haptic.Selection)

//Haptic2:
haptic(Haptic.Light)

//Haptic3
haptic(Haptic.Medium)           

//Haptic4
haptic(Haptic.Heavy)
           
//Haptic5
haptic(Haptic.Success)

//Haptic6
haptic(Haptic.Warning)

//Haptic7
haptic(Haptic.Error)
            
```

<h1>29. Storage Persistence Type1 (For light data like @AppStorage) [To use inside classes add -   "DriftStorage.initialize(applicationContext)" before "setContent" line in MainActivity.]
]:</h1>

```
var username = Storage(key = "username", defaultValue = "")

    DriftView {
        VStack(spacing = 20) {
            TextField(placeholder = "Username",value = username)
            Text("Name is ${username.value}")
        }
    }
``` 

<h1>30. Advanced Data Persistence -- DriftStore:</h1>


## How To use:

### 1\. Define your Data Model

Just a standard Kotlin data class. No annotations required.

```kotlin
data class User(
    val name: String,
    val age: Int,
    val isAdmin: Boolean = false
)
```

### 2\. Create a Store

Initialize the store inside any Composable. It survives recompositions automatically.

```kotlin
@Composable
fun UserListScreen() {
    // Creates or opens "users_db"
    val users = DriftStore("users_db", User::class)

    DriftView {
        // Your UI code...
    }
}
```

-----

## CRUD Operations

### Add Data

```kotlin
Button(onClick = {
    users.add(User("Alice", 25))
    users.add(User("Bob", 30))
}) { Text("Add User") }
```

### Add Data [Safely as seed it will be ready on app's first launch]

```kotlin
//data class:
data class User(
    val username: String,
    val password: String
)

//Using Seed to plant database:
//Automatically creates table, checks for duplicates by username, and inserts if missing.
        users.seed(
            User("admin", "admin"),
            User("user1", "user1")
        ) {
            //To check duplicacy based on username
            it.username
        }
```
### Edit Data (Reactive)

Find an item and modify it in place. The UI will refresh instantly.

```kotlin
val bob = users.items.find { it.name == "Bob" }

// Safe edit (handles nulls automatically)
users.edit(bob) {
    age = 31
    isAdmin = true
}
```

### Remove Data

```kotlin
// Remove specific item
users.remove(bob)

// Remove by condition
users.removeBy(User::name, "Alice")

// Nuke table
users.removeAll()
```

-----

## Type-Safe Filtering

Stop writing SQL strings. Use pure Kotlin syntax for fast, memory-safe filtering.

### Basic Filters

```kotlin
// Exact match
val admins = users.filter { it.isAdmin }

// Ranges
val adults = users.filter { it.age >= 18 }
```

### String Matching

Supports all standard String operations.

```kotlin
// Starts/Ends with
val aNames = users.filter { it.name.startsWith("A") }

// Contains (Case Insensitive)
val searchResults = users.filter { 
    it.name.contains("bob", ignoreCase = true) 
}
```

### Complex Logic

Combine conditions freely.

```kotlin
val targetUsers = users.filter { 
    (it.age > 20 && it.name.startsWith("J")) || it.isAdmin 
}
```

-----

## Sorting & Querying

### Sorting

Use the `Sort` helper for clean syntax.

```kotlin
// Simple sort
val byAge = users.sort(Sort(User::age))

// Reverse sort
val byNameDesc = users.sort(Sort(User::name, Order.Descending))

// Compound sort (Sort by Age, then Name)
val complexSort = users.sort(
    Sort(User::age, Order.Descending),
    Sort(User::name)
)
```


<h3>2. Sample Code (To CRUD, Search, Sort And Query/Filter):</h3>

```

//Data Model
data class Model(
    var name: String,
    var age: Int
)

@Composable
fun test4(){
    //Creating a Store
    val users = DriftStore("database", Model::class)

    //Editing by first finding the id then passing it to edit.
    val umair = users.items.find { it.name == "Umair" }
    users.edit(umair){
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
        if(users.items.isEmpty()) {
            Text("Add", Modifier.onTapGesture(action = {
                users.add(Model("Sameer", 12))
                users.add(Model("Akbar", 7))
                users.add(Model("Akmala", 43))
                users.add(Model("Akshara", 21))
                users.add(Model("Umair",33))
            }))
        }

        VStack {

            if(!users.items.isEmpty()){
                Text("Original Data:", Modifier.font(system(18, light)))
                List(users.items){s->
                    Text("${s.age}.  ${s.name}")
                }

                //Original List:

                Spacer(13)
                Text("Sorted Data:", Modifier.font(system(18, light)))
                List(sorted) { s ->
                    Text("${s.age}.  ${s.name}")
                }
                Spacer(13)
                List(selected2){s->
                    Text("${s.age}.  ${s.name}")
                }

            }

            Text("Nuke", Modifier.padding(top = 70).onTapGesture(action = {users.removeAll()}))
        }





    }


```
<h1>31. Toasts: [Note to modify background color use toastColor Modifier and not .background]</h1>

```
//1.Simple Toast:
Toast("Hey, this is Shazan")

//2. With Some action once the toast ends:
Toast("Hey, this is Shazan", Modifier.onEnd { current.set(0) })

//3. Advanced Toast as a view:
Toast(
                    Modifier.clipShape(RoundedRectangle(radius = 12))
                        .toastColor(Color.yellow.copy(alpha = 0.2f))
                        .onEnd{ current.set(0)}
                        .duration(2.0)
                ) {
                    HStack() {
                        Image("pip_swap", Modifier.scaleEffect(2))
                        Text("This is an advanced toast")
                    }
                }

```

<h1>32. JSON Reader:</h1>

```
val graph = readJson("metro_graph_final_fixed.json") // Telling the name of the file stored in assets folder in this case- metro_graph_final_fixed.json

        val allStations = graph["stations"] //traversing 
        val mundkaExact = allStations.search("name", "Mundka") //Searching
        val sortedList = allStations.sort("name") //Sorting
        val searchResults = allStations.fuzzySearch("name", "Mndka") //Fuzzy Searching to be able to get the resuts even for incorrect spellings if words match (kindof)

//Showing the stored values as list:
List(items = sortedList) { station ->
                Text(station["name"].string)
            }
```

<h1>33. Add Ons:</h1>
* Device Specific Variables- statusBarHeight & width N deviceHeight & width, -> To access values: statusBarHeight.value, statusBarWidth.value, deviceHeight.value, deviceWidth.value </br>
* Triangle - Shape, </br>
* Darker - color Modifier ->  .background(Color.yellow.darker(0.8f)) </br>
* Rgb, rgba, hex - color formats. </br>
* useNav and useNavigationAction </br>
* pushReplacement for navStacks </br>
* Side Menu </br>
* .seed in Advanced persistence [We do this to seed the data values so they are ready on app's first launch too "smoothly"] - </br>

// Inside MainActivity.onCreate before setContent{} </br>
DriftStorage.initialize(applicationContext)  // Starts SharedPreferences - "Storage" you need this to use Storage variables inside viewModels or classes </br>
DriftRegistry.initialize(applicationContext) // Starts Advance Persistence Engine (Add this!) </br>

<h2> Push Replacement: [To use when you need to control navigation Stack while being outside of it.. Say from SideMenu ]</h2>

```
//Define a variable
val logoutAction = useNavigationAction()

// Inside Navigation Stack
NavigationStack() {
            val nav = useNav()

            logoutAction.set {
                nav.pushReplacement { Login() }
            }
}

//Controlling from Side Menu:
SideMenu(
            isOpen = showMenu.value,
            modifier = Modifier.frame(width = 320.wu).background(Color.white),
            onDismiss = { showMenu.set(false) }
        ) {
MenuItem(MenuPlacement.Bottom) {
                Button(
                    action = {
                        logoutAction()     //******HERE- You have to call it like a function
                        viewModel.isLoggedIn.value = false
                        showMenu.set(false)
                    },
                    modifier = Modifier.padding(all = 20.u).padding(bottom = 20.hu)
                ) {
                    HStack() {
                        Image("logout_img", Modifier.frame(width = 25.wu, height = 25.hu))
                        Text("Logout", Modifier.font(system(18, medium)).foregroundStyle(Color.black).padding(leading = 5.wu))
                    }

                }
            }
}
```

<h2> Seeding: </h2>
    
```
//Defining the class
data class User(
    val username: String,
    val password: String
)

//Connecting it with DriftStorage and assigning it a file name.
val users = DriftStore("users_db", User::class)

//Seeding: 
users.seed(
            User("admin", "admin"),
            User("user1", "user1")
        ) {
            //To check duplicacy based on username
            it.username
        }

```

<h1> 34. Added linearGradient and .u, .hu, .wu </h1>
   <h3> .u [short for unit]- for unit scaling across x-y direction for example in Text size, cornerRadius, </h3>
   <h3> .hu [short for heightUnit]- for unit scaling across y direction for example in padding/ offsets/ frames/ Shapes-height in y directional parameters  </h3>
   <h3> .wu [short for WidthUnit]- for unit scaling across x direction for example in padding/ offsets/ frames/ Shapes-width in x directional parameters  </h3>

```
Modifier.frame(width = 320.wu, height = 100.hu)

or on Text:

Text("Hello", Modifier.font(system(size = 28.u, weight = medium).foregroundStyle(linearGradient(listOf(Color.red, Color.black, Color.purple))) //There is no limit on number of colors used for creating a Gradient

```

<h1> 35. ScrollView (now supports - onTop & onScroll parameters) </h1>
<h3> Use onTop to do something when user hasnt scrolled yet Like showing a floating button in the bottom end corner & onScroll to do any task when user has scrolled (this is computed only once, but i am actively thinking of adding onContinuousScroll too to do something as long as user is scrolling.)</h3>

Code:

```
 ScrollView(modifier = Modifier.background(Color.white), onTop = {isAtTop.set(true)}, onScroll = {isAtTop.set(false)}

                ) {
                    VStack(
                        spacing = 20,
                        modifier = Modifier
                            .frame(
                                width = (deviceWidth.value),
                                height = (deviceHeight.value + 100.hu )
                            )
                            .alignment(Alignment.Center).padding(top = -45.hu)
                    ) {
//                        Divider(color = Color.red, width = 500, modifier = Modifier.padding(top = -70))


                        // ================= LEADS TAB =================
                        ZStack {
                            RoundedRectangle(
                                (deviceWidth.value) - 10.wu,
                                140.hu,
                                cornerRadius = 12.u,
                                Modifier.foregroundStyle(Color.white)
                                    .shadow(Color.gray, 46.u, 6.u, cornerRadius = 128).border(color = Color.black.copy(alpha = 0.2f), width = 1.u, cornerRadius = 16)
                            )

                            Text(
                                "Leads",
                                Modifier.font(system(22.u, bold))
                                    .foregroundStyle(Color.black.copy(alpha = 0.67f))
                                    .padding(top = -100.hu, leading = -285.wu)
                            )

                            HStack(
                                spacing = 10.wu.toInt(),
                                modifier = Modifier
                                    .frame(
                                        width = (deviceWidth.value * 1).wu - 30.wu,
                                        height = 140.hu
                                    )
                                    .alignment(Alignment.Center)
                                    .padding(top = 29.hu)
                            ) {
                                VStack(Modifier.frame(width = 80.wu).padding(top = 1.hu)) {
                                    Image("leads_img", Modifier.frame(width = 55.wu))
                                    Text("Qualified")
                                }
                               
                            }
                        }
}

```
<h1> 36. +FIREBASE Direct Support! [Supports CRUD operations for now in DB with? 99% DriftStore like syntax!! except you have to use copy keyword before editing something.]</h1>

```
data class Account(
    val id: String,
    val role: String
)

@Composable
fun FirebaseStoreTest() {
    var tap = 1
    // Firebase-backed store
    val accounts = FirebaseStore("accounts_test", Account::class)
    accounts.Bind()


    DriftView {

        VStack(spacing = 20.u.toInt()) {

            Text("FirebaseStore Test", Modifier.font(system(62, bold)))

            Button(action = {

                // Idempotent seed
                accounts.seed(
                    Account("root", "admin"),
                    Account("guest", "viewer")
                ) {
                    it.id
                }

            }) {
                Text("Seed Accounts")
            }

            Button(action = {
                accounts.add(Account("alice", "editor"))
            }) {
                Text("Add Alice")
            }

            Button(action = {

                tap++

                val guest = accounts.items.find { it.id == "guest" }
                if(tap%2 == 0){

                    accounts.edit(guest) {
                        copy(role = "viewer")
                    }
                }
                else {
                    accounts.edit(guest) {
                        copy(role = "shazan")
                    }
                }
            }) {
                Text("Edit Guest")
            }

            Button(action = {
                accounts.removeBy(Account::id, "alice")
            }) {
                Text("Remove Alice")
            }

            Divider()

            // Reactive view
            VStack {
                Text("Accounts in DB:", Modifier.font(system(18.u, medium)))

                accounts.items.forEach {
                    Text("${it.id} → ${it.role}")
                }
            }
        }
    }
}


```

<h1> 37. xMax, xMin, yMax and yMin:</h1>
    <h3> Use these variables with offset to "align" items relatively to the screen, Don't worry your UI will look the same on all screens because in the backend these variabels are computed based onn the device being used, be it a foldable, a tab, a flip phone or whatever. For example "Modifier.offset(y = yMax)" pushes the view it is applied on to Top-Center and "Modifier.offset(x = xMax)" shifts the view to far right.  </h3>
   
<h1> 38. Gauges (LinearGauge, CircularGauge, AccessoryGauge):</h1>
    <h3> They support the parameters:
    value, range, radius, thickness, fillColor, tracker . 
    To adjust size use frame around it (for example if you need to adjust length of LinearGauge.)  </h3>
<h2>CODE EXAMPLE: [AccessoryCircularGauge] </h2>

```
AccessoryCircularGauge(
                                value = 20,
                                range = 0..40,
                                radius = 33.8.u,
                                thickness = 7.u,

                                // Very light track
                                trackColor = gaugeColor(
                                    Color.white.copy(alpha = 0.80f)
                                ),
                                fillColor = gaugeColor(
                                    linearGradient(
                                        listOf(
                                            hex("6A89A7"),
                                            hex("88BDF2")
                                        )
                                    )
                                ),
                                tracker = {

                                    Circle(
                                        3,
                                        Modifier.foregroundStyle(
                                            Color.white.copy(alpha = 0.9f)
                                        )
                                    )
                                },


                            ) {
                                VStack {

                                    Text(
                                        "20",
                                        Modifier
                                            .font(system(30.u, bold))
                                            .foregroundStyle(Color.white)
                                            .offset(y = 8.hu.toInt())
                                    )

                                    Image(
                                        "flame_fill2",
                                        Modifier
                                            .frame(13.wu, 13.hu)
                                            .offset(y = 10.9.hu.toInt())
                                    )
                                }
                            }
``` 

<h1> 39. Subtract Modifier, FourPointStar Shape, Screen Sizes based ui designing - eg- if(Screen.Tablet), Group{} (Same implementation as in swiftui except you cannot apply modifiers to it for now in future updates i will add that too) </h1>

<h3>Applied a Rectangle Cutout to Circle: </h3>

```
Circle(81.u.toInt(), Modifier.foregroundStyle(Color.gray.copy(0.3f))
                                        .offset(x= 72.wu.toInt(), y= -40.hu.toInt()).subtract(
                                            shape = RectangleShape,
                                            width = 220.wu,
                                            height = 66.hu,
                                            x = -5.wu,
                                            y = -21.hu
                                        ))

```

<h3> FourPointStar: </h3>

```
FourPointStar(45,45, Modifier.foregroundStyle(Color.cyan))

```

<h3> Screen Sizes Based Logic: </h3>

```
//In the file you want to design UI differently for different devices define this first-
        val screen = getScreenSizes()   // getScreenSizes is defined in dsl

   //Then you can design deviceSpecificUI by doing:

if(screen == Screen.Phone){   // Screen.SmallPhone, or Screen.Tablet or Screen.Desktop

    //Specific UI design elements go here

}


```

<h1> 40. onActiveSession- Better version of LaunchedEffect: You can pass delays, to Always do something whenever app is launched regardless or with regard to the parameter by changing the alwaysLaunchAtLogin optional parameter's value- defaulted to true to behave like simple launched effect. </h1>

Example: (You can set the delay by doing Int.minutes/ .seconds / .milliseconds / .hours/ even .days ,, where int is any integer value.)
```
//In this example the action happens only when 5 minutes are passed, irrespective if the app is relaunched, with the key - foundUser.username meaning  after 5 minutes if it changes the action is performed 
onActiveSession(delay = 5.minutes, key = foundUser?.username, alwaysLaunchAtLogin = false) {
                    if(foundUser != null){
                        viewModel.users.edit(foundUser){
                            copy(lastSeenAt = System.currentTimeMillis())
                        }
                    }
                }
```

**********</br>

Steps to Use DriftUI:
1. Clone this git repo and open the project in android studio.
2. Publish driftui to Maven Local on your Machine
3. Add the dependency - implementation("com.example:driftui:0.1.78") or whatever the current version is when you are cloning
   NOTE: (You can check the version at driftui/build.gradle.kts by looking into the version section at the end of the file)
4. Import com.example.driftui.core.* in the files you want to use driftui in.
5. Voila! Happy Journey! 

Project Structure:
![ss](https://github.com/user-attachments/assets/ce719ab5-02a8-407b-98e2-5cc479a90a9f)


Checkout Screenhot:
![Screenshot 2025-11-18 at 12 43 29 PM](https://github.com/user-attachments/assets/5139df8a-706d-4fc8-af20-3d3cecf37ee5)

