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


3. By default In DriftView useSafeArea = true, you can set it to false to allow the elements/ Views to even fall out of the screen

4. This now works as expected just the fact that you have to use .align instead of .alignment- Using the material3 Text
   
```
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.driftui.core.DriftView
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment

@Composable
fun learning(){
    DriftView{
        Text("I am centered", modifier = Modifier.align(Alignment.BottomStart))
    }

}
```

5. Just use Row, Column and Box Instead of H,V, ZStacks to natively use Compose Elements.

6. option+cmd+L to format document (to improve view)
7. main Axis ka alignment hota hai cross axis ka arrangement hota hai.
8. Use \n for desired text wrapping, and """...""" instead of just double quotes to control how the text is explicitly laid out, then- [.trimMargin or .trimIndent, You can change the margin character if you want! trimMargin(">") would look for > instead of |]

```
Text("""
                    |      I am centered but unstyled
                    |                Hello
                    |                                       Wow
                """.trimMargin())
```

Removed:
Text
Padding
