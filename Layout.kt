package com.example.driftui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

// For ColumnScope / RowScope / BoxScope
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.BoxScope

//MARK: - For HStack
import androidx.compose.ui.unit.dp

// For padding
import androidx.compose.foundation.layout.padding as foundationPadding
import androidx.compose.foundation.layout.PaddingValues

// For background & color
import androidx.compose.foundation.background as foundationBackground
import androidx.compose.ui.graphics.Color

// For Shapes
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.graphics.Shape

//ClipShape
import androidx.compose.ui.draw.clip

//Frame:
import androidx.compose.ui.unit.Dp

// Font

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.Text as MaterialText
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

// Images:

import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.Image as ComposeImage
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalContext


// TextField:

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import kotlin.reflect.KProperty // Required for Property Delegation

// Button
import androidx.compose.material3.Button as MaterialButton

// Preview
import androidx.compose.ui.tooling.preview.Preview

// ---------------------------------------------------------
// MARK: - SwiftUI-style Colors
// ---------------------------------------------------------

// Standard Colors (camelCase)
val Color.Companion.black: Color get() = Black
val Color.Companion.blue: Color get() = Blue
val Color.Companion.cyan: Color get() = Cyan
val Color.Companion.gray: Color get() = Gray
val Color.Companion.green: Color get() = Green
val Color.Companion.lightGray: Color get() = LightGray
val Color.Companion.magenta: Color get() = Magenta
val Color.Companion.red: Color get() = Red
val Color.Companion.white: Color get() = White
val Color.Companion.yellow: Color get() = Yellow

// New SwiftUI Colors
val Color.Companion.pink: Color get() = Color(0xFFFF2D55)
val Color.Companion.orange: Color get() = Color(0xFFFF9500)
val Color.Companion.purple: Color get() = Color(0xFFAF52DE)
val Color.Companion.brown: Color get() = Color(0xFFA2845E)
val Color.Companion.mint: Color get() = Color(0xFF00C7BE)
val Color.Companion.teal: Color get() = Color(0xFF30B0C7)
val Color.Companion.indigo: Color get() = Color(0xFF5856D6)


// ---------------------------------------------------------
// MARK: - VStack
// ---------------------------------------------------------

@Composable
fun VStack(
    modifier: Modifier = Modifier,
    alignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    spacing: Int = 8,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),   // only width, not height
        horizontalAlignment = alignment,
        verticalArrangement = Arrangement.spacedBy(spacing.dp),
        content = content
    )
}




// ---------------------------------------------------------
// MARK: - HStack
// ---------------------------------------------------------

@Composable
fun HStack(
    modifier: Modifier = Modifier,
    alignment: Alignment.Vertical = Alignment.CenterVertically,
    spacing: Int = 8,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier,  // <-- FIX: do NOT fillMaxWidth by default
        verticalAlignment = alignment,
        horizontalArrangement = Arrangement.spacedBy(spacing.dp),
        content = content
    )
}




// ---------------------------------------------------------
// MARK: - ZStack
// ---------------------------------------------------------

@Composable
fun ZStack(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier,   // DO NOT force fillMaxSize
        contentAlignment = contentAlignment,
        content = content
    )
}

//MAINNNNNNNNNN
@Composable
fun DriftView(
    modifier: Modifier = Modifier, 
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
        content = content
    )
}
/////////////

// ---------------------------------------------------------
// MARK: - SwiftUI-style Padding
// ---------------------------------------------------------

fun Modifier.padding(all: Int): Modifier =
    this.then(
        foundationPadding(all.dp)
    )

fun Modifier.padding(horizontal: Int = 0, vertical: Int = 0): Modifier =
    this.then(
        foundationPadding(
            PaddingValues(
                start = horizontal.dp,
                end = horizontal.dp,
                top = vertical.dp,
                bottom = vertical.dp
            )
        )
    )

fun Modifier.padding(
    top: Int = 0,
    bottom: Int = 0,
    leading: Int = 0,
    trailing: Int = 0
): Modifier =
    this.then(
        foundationPadding(
            PaddingValues(
                start = leading.dp,
                end = trailing.dp,
                top = top.dp,
                bottom = bottom.dp
            )
        )
    )

// ---------------------------------------------------------
// MARK: - Spacer (SwiftUI-style)
// ---------------------------------------------------------

@Composable
fun ColumnScope.Spacer() {
    androidx.compose.foundation.layout.Spacer(
        modifier = Modifier.weight(1f)
    )
}

@Composable
fun RowScope.Spacer() {
    androidx.compose.foundation.layout.Spacer(
        modifier = Modifier.weight(1f)
    )
}

@Composable
fun Spacer(size: Int) {
    // Fixed size Spacer
    androidx.compose.foundation.layout.Spacer(
        modifier = Modifier.size(size.dp)
    )
}

// ---------------------------------------------------------
// MARK: - SwiftUI Shape DSL
// ---------------------------------------------------------



fun Circle(): Shape = CircleShape

fun Capsule(): Shape = RoundedCornerShape(percent = 50)

fun RoundedRectangle(cornerRadius: Int): Shape =
    RoundedCornerShape(cornerRadius.dp)

//ClipShape:

fun Modifier.clipShape(shape: Shape): Modifier =
    this.then(clip(shape))
//Corner-Radius:
fun Modifier.cornerRadius(radius: Int): Modifier =
    this.clipShape(RoundedRectangle(radius))


// ---------------------------------------------------------
// MARK: - Background Color (SwiftUI-style)
// --------------------------------------------------------- 
fun Modifier.background(color: Color): Modifier =
    this.then(foundationBackground(color))

// ---------------------------------------------------------
// MARK: - Frame (SwiftUI-style)
// ---------------------------------------------------------



fun Modifier.frame(
    width: Int? = null,
    height: Int? = null,
    minWidth: Int = 0,
    maxWidth: Int? = null,
    minHeight: Int = 0,
    maxHeight: Int? = null
): Modifier {

    var m = this

    // Width (fixed or ranged)
    if (width != null) {
        m = m.then(Modifier.width(width.dp))
    } else {
        m = m.then(
            Modifier.widthIn(
                min = if (minWidth > 0) minWidth.dp else Dp.Unspecified,
                max = if (maxWidth != null) maxWidth.dp else Dp.Unspecified
            )
        )
    }

    // Height (fixed or ranged)
    if (height != null) {
        m = m.then(Modifier.height(height.dp))
    } else {
        m = m.then(
            Modifier.heightIn(
                min = if (minHeight > 0) minHeight.dp else Dp.Unspecified,
                max = if (maxHeight != null) maxHeight.dp else Dp.Unspecified
            )
        )
    }

    return m
}

// ---------------------------------------------------------
// MARK: - Font & Foreground (SwiftUI-style)
// ---------------------------------------------------------

// Font weights (DriftUI style, no dot syntax)
val bold = FontWeight.Bold
val semibold = FontWeight.SemiBold
val medium = FontWeight.Medium
val regular = FontWeight.Normal
val light = FontWeight.Light
val thin = FontWeight.Thin
val ultralight = FontWeight.ExtraLight
val black = FontWeight.Black

// DriftUI System font model and helper (usage: system(size = 18, weight = bold))
data class SystemFont(
    val size: Int,
    val weight: FontWeight = regular
)

fun system(size: Int, weight: FontWeight = regular): SystemFont =
    SystemFont(size, weight)


// store font metadata in Modifier chain
private data class FontModifier(val font: SystemFont) : Modifier.Element
private data class ForegroundColorModifier(val color: Color) : Modifier.Element

fun Modifier.font(font: SystemFont): Modifier =
    this.then(FontModifier(font))

fun Modifier.foregroundStyle(color: Color): Modifier =
    this.then(ForegroundColorModifier(color))




// DriftUI Text wrapper: it reads FontModifier from the provided modifier and applies style.
@Composable
fun Text(
    text: String,
    modifier: Modifier = Modifier
) {
    // Walk modifier chain to find FontModifier & Color (the last one wins if multiple)
    val textStyleData = remember(modifier) {
        var font: SystemFont? = null
        var color: Color = Color.Unspecified

        modifier.foldIn(Unit) { _, element ->
            when (element) {
                is FontModifier -> font = element.font
                is ForegroundColorModifier -> color = element.color
            }
            Unit
        }
        font to color // Using a Pair to hold both
    }

    val style = TextStyle(
        fontSize = textStyleData.first?.size?.sp ?: TextStyle.Default.fontSize,
        fontWeight = textStyleData.first?.weight ?: TextStyle.Default.fontWeight,
        color = if (textStyleData.second != Color.Unspecified) textStyleData.second else TextStyle.Default.color
    )

    // Delegate to Material Text with the computed style
    MaterialText(
        text = text,
        modifier = modifier,
        style = style
    )
}


//SwiftUI like Images

@Composable
fun Image(
    name: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit
) {
    val context = LocalContext.current
    val resId = context.resources.getIdentifier(name, "drawable", context.packageName)

    ComposeImage(
        painter = painterResource(id = resId),
        contentDescription = null,
        modifier = modifier,
        contentScale = contentScale
    )
}


// ---------------------------------------------------------
// MARK: - State Structure (SwiftUI @State equivalent)
// ---------------------------------------------------------

/**
 * A simple structure to represent the value and the setter function for two-way data flow.
 */
class Binding<T>(
    val value: T,
    val set: (T) -> Unit
)

/**
 * Property Delegate that mimics SwiftUI's @State.
 * Allows usage: var name by State("Initial")
 */
class State<T>(initialValue: T) {
    private val state = mutableStateOf(initialValue)

    // Public property to read the value, which was the missing piece
    val value: T
        get() = state.value

    // Kotlin's delegate getter: provides the value
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = state.value

    // Kotlin's delegate setter: updates the state
    operator fun setValue(thisRef: Any?, property: KProperty<*>, newValue: T) {
        state.value = newValue
    }

    // Provides the Binding object used internally by the TextField overload
    fun binding(): Binding<T> {
        return Binding(state.value, { state.value = it })
    }

    // The new, simple setter function
    fun set(newValue: T) {
        state.value = newValue
    }
}

// ---------------------------------------------------------
// MARK: - TextField (Primary Implementation)
// ---------------------------------------------------------

@Composable
fun TextField(
    placeholder: String,
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = text,
        onValueChange = onTextChange,
        modifier = modifier, // Pass modifier directly
        textStyle = TextStyle.Default.copy(fontSize = 16.sp),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .foundationBackground(color = Color.LightGray.copy(alpha = 0.3f), shape = RoundedRectangle(cornerRadius = 5))
                    .padding(horizontal = 8, vertical = 8)
            ) {
                if (text.isEmpty()) {
                    MaterialText(
                        text = placeholder,
                        style = TextStyle.Default.copy(fontSize = 16.sp, color = Color.Gray)
                    )
                }
                innerTextField()
            }
        }
    )
}

// ---------------------------------------------------------
// MARK: - TextField (SwiftUI-Style Overload)
// ---------------------------------------------------------

@Composable
fun TextField(
    placeholder: String,
    // Accepts the custom State object directly, using 'value' as the clean parameter name
    value: State<String>,
    modifier: Modifier = Modifier
) {
    // Delegates to the primary implementation using the State's binding function
    TextField(
        placeholder = placeholder,
        text = value.binding().value,
        onTextChange = value.binding().set, // This hides the complex lambda
        modifier = modifier
    )
}



@Preview(showBackground = true)
@Composable
fun DriftUITextFieldPreview() {
    val name = remember { State("John Doe") }
    DriftView {
        VStack(spacing = 16) {
            TextField("Enter Name", value = name)
            Text("Current Name: ${name.value}")
        }
    }
}

// ---------------------------------------------------------
// MARK: - SecureField (Primary Implementation)
// ---------------------------------------------------------

@Composable
fun SecureField(
    placeholder: String,
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = text,
        onValueChange = onTextChange,
        modifier = modifier,
        textStyle = TextStyle.Default.copy(fontSize = 16.sp),
        visualTransformation = PasswordVisualTransformation(),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .foundationBackground(color = Color.LightGray.copy(alpha = 0.3f), shape = RoundedRectangle(cornerRadius = 5))
                    .padding(horizontal = 8, vertical = 8)
            ) {
                if (text.isEmpty()) {
                    MaterialText(
                        text = placeholder,
                        style = TextStyle.Default.copy(fontSize = 16.sp, color = Color.Gray)
                    )
                }
                innerTextField()
            }
        }
    )
}

// ---------------------------------------------------------
// MARK: - SecureField (SwiftUI-Style Overload)
// ---------------------------------------------------------

@Composable
fun SecureField(
    placeholder: String,
    value: State<String>,
    modifier: Modifier = Modifier
) {
    SecureField(
        placeholder = placeholder,
        text = value.binding().value,
        onTextChange = value.binding().set,
        modifier = modifier
    )
}

// ---------------------------------------------------------
// MARK: - Button (SwiftUI-Style)
// ---------------------------------------------------------

@Composable
fun Button(
    action: () -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier.clickable(
            onClick = action,
            interactionSource = remember { MutableInteractionSource() },
            indication = null // No default ripple, allowing full custom styling
        ),
        contentAlignment = Alignment.Center
    ) {
        label()
    }
}
