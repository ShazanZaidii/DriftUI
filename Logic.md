<h2> 1. Variables can be used as: </h2>
   
```
   var name = remember{State("")}

   // To modify
    name.set("Shazan")

    // To access:
    name.value
```

<h2> 2. To only enable landscape mode for tablet users do: </h2>
<h3>In main Activity after super.onCreate-- </h3>

```
    val metrics = resources.displayMetrics
    val widthDp = metrics.widthPixels / metrics.density

    // 2. Logic: If it's a phone (< 600dp), LOCK it. If Tablet, UNLOCK it.
    if (widthDp < 600) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    } else {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

```

  <h2>  Sample: </h2>

   ```
   class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        val metrics = resources.displayMetrics
        val widthDp = metrics.widthPixels / metrics.density

        // 2. Logic: If it's a phone (< 600dp), LOCK it. If Tablet, UNLOCK it.
        if (widthDp < 600) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
        enableEdgeToEdge()
        DriftStorage.initialize(applicationContext)
        val checkSession = Storage("is_logged_in", false)
        val checkUser = Storage("who_logged_in", "none")
        val checkError = Storage("error_occurred",false)



        setContent {
            if(checkSession.value){
                if(checkUser.value == "admin"){
                    AdminHome()
                }
                else if(checkUser.value != "admin" && checkUser.value != "none"){
                    AssociateHome()
                }

            }
            else {
                Login()
            }



        }
    }
   }

   ```


3. By default In driftView useSafeArea = true, you can set it to false to allow the elements/ Views to even fall out of the screen
