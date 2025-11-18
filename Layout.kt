package com.example.driftui

// ---------------------------------------------------------------------------------------------
// IMPORTS
// ---------------------------------------------------------------------------------------------

import androidx.compose.foundation.Image as ComposeImage
import androidx.compose.foundation.background as foundationBackground
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding as foundationPadding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider as MaterialDivider
import androidx.compose.material3.Text as MaterialText
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.LocalLifecycleOwner

import kotlin.reflect.KProperty
import androidx.compose.foundation.clickable

import androidx.lifecycle.ViewModelStoreOwner

// ---------------------------------------------------------------------------------------------
// COLORS (SwiftUI-style)
// ---------------------------------------------------------------------------------------------

private val Black     = Color(0xFF000000)
private val Blue      = Color(0xFF007AFF)
private val Cyan      = Color(0xFF00FFFF)
private val Gray      = Color(0xFF8E8E93)
private val Green     = Color(0xFF34C759)
private val LightGray = Color(0xFFD1D1D6)
private val Magenta   = Color(0xFFFF2D55)
private val Red       = Color(0xFFFF3B30)
private val White     = Color(0xFFFFFFFF)
private val Yellow    = Color(0xFFFFCC00)

val Color.Companion.black get() = Black
val Color.Companion.blue get() = Blue
val Color.Companion.cyan get() = Cyan
val Color.Companion.gray get() = Gray
val Color.Companion.green get() = Green
val Color.Companion.lightGray get() = LightGray
val Color.Companion.magenta get() = Magenta
val Color.Companion.red get() = Red
val Color.Companion.white get() = White
val Color.Companion.yellow get() = Yellow

val Color.Companion.pink get() = Color(0xFFFF2D55)
val Color.Companion.orange get() = Color(0xFFFF9500)
val Color.Companion.purple get() = Color(0xFFAF52DE)
val Color.Companion.brown get() = Color(0xFFA2845E)
val Color.Companion.mint get() = Color(0xFF00C7BE)
val Color.Companion.teal get() = Color(0xFF30B0C7)
val Color.Companion.indigo get() = Color(0xFF5856D6)



// ---------------------------------------------------------------------------------------------
// ALIGNMENTS
// ---------------------------------------------------------------------------------------------

val top: Alignment = Alignment.TopCenter
val center: Alignment = Alignment.Center
val bottom: Alignment = Alignment.BottomCenter
val leading: Alignment = Alignment.CenterStart
val trailing: Alignment = Alignment.CenterEnd
val topLeading: Alignment = Alignment.TopStart
val topTrailing: Alignment = Alignment.TopEnd
val bottomLeading: Alignment = Alignment.BottomStart
val bottomTrailing: Alignment = Alignment.BottomEnd



// ---------------------------------------------------------------------------------------------
// VSTACK / HSTACK / ZSTACK / DriftView
// ---------------------------------------------------------------------------------------------

@Composable
fun VStack(
    modifier: Modifier = Modifier,
    alignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    spacing: Int = 8,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = alignment,
        verticalArrangement = Arrangement.spacedBy(spacing.dp),
        content = content
    )
}

@Composable
fun HStack(
    modifier: Modifier = Modifier,
    alignment: Alignment.Vertical = Alignment.CenterVertically,
    spacing: Int = 8,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = alignment,
        horizontalArrangement = Arrangement.spacedBy(spacing.dp),
        content = content
    )
}

@Composable
fun ZStack(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier, contentAlignment = contentAlignment, content = content)
}

@Composable
fun DriftView(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center, content = content)
}



// ---------------------------------------------------------------------------------------------
// PADDING (fixed to avoid Modifier.then errors)
// ---------------------------------------------------------------------------------------------

fun Modifier.padding(all: Int): Modifier =
    this.then(Modifier.foundationPadding(all.dp))

fun Modifier.padding(horizontal: Int = 0, vertical: Int = 0): Modifier =
    this.then(Modifier.foundationPadding(horizontal = horizontal.dp, vertical = vertical.dp))

fun Modifier.padding(
    top: Int = 0,
    bottom: Int = 0,
    leading: Int = 0,
    trailing: Int = 0
): Modifier =
    this.then(
        Modifier.foundationPadding(
            PaddingValues(
                start = leading.dp,
                end = trailing.dp,
                top = top.dp,
                bottom = bottom.dp
            )
        )
    )



// ---------------------------------------------------------------------------------------------
// SPACER
// ---------------------------------------------------------------------------------------------

@Composable
fun ColumnScope.Spacer() = androidx.compose.foundation.layout.Spacer(Modifier.weight(1f))

@Composable
fun RowScope.Spacer() = androidx.compose.foundation.layout.Spacer(Modifier.weight(1f))

@Composable
fun Spacer(size: Int) = androidx.compose.foundation.layout.Spacer(Modifier.size(size.dp))



// ---------------------------------------------------------------------------------------------
// SHAPES
// ---------------------------------------------------------------------------------------------

fun Circle(): Shape = CircleShape
fun Capsule(): Shape = RoundedCornerShape(percent = 50)
fun RoundedRectangle(radius: Int): Shape = RoundedCornerShape(radius.dp)

fun Modifier.clipShape(shape: Shape): Modifier = this.clip(shape)
fun Modifier.cornerRadius(radius: Int): Modifier = this.clipShape(RoundedRectangle(radius))



// ---------------------------------------------------------------------------------------------
// BACKGROUND (fixed implementation)
// ---------------------------------------------------------------------------------------------

fun Modifier.background(color: Color): Modifier =
    this.then(Modifier.foundationBackground(color))



// ---------------------------------------------------------------------------------------------
// FRAME
// ---------------------------------------------------------------------------------------------

fun Modifier.frame(
    width: Int? = null,
    height: Int? = null,
    minWidth: Int = 0,
    maxWidth: Int? = null,
    minHeight: Int = 0,
    maxHeight: Int? = null
): Modifier {

    var m = this

    if (width != null) m = m.then(Modifier.width(width.dp))
    else m = m.then(Modifier.widthIn(
        min = if (minWidth > 0) minWidth.dp else Dp.Unspecified,
        max = if (maxWidth != null) maxWidth!!.dp else Dp.Unspecified
    ))

    if (height != null) m = m.then(Modifier.height(height.dp))
    else m = m.then(Modifier.heightIn(
        min = if (minHeight > 0) minHeight.dp else Dp.Unspecified,
        max = if (maxHeight != null) maxHeight!!.dp else Dp.Unspecified
    ))

    return m
}



// ---------------------------------------------------------------------------------------------
// FONT SYSTEM + TEXT
// ---------------------------------------------------------------------------------------------

val bold = FontWeight.Bold
val semibold = FontWeight.SemiBold
val medium = FontWeight.Medium
val regular = FontWeight.Normal
val light = FontWeight.Light
val thin = FontWeight.Thin
val ultralight = FontWeight.ExtraLight
val black = FontWeight.Black

data class SystemFont(val size: Int, val weight: FontWeight = regular)
fun system(size: Int, weight: FontWeight = regular) = SystemFont(size, weight)

private data class FontModifier(val font: SystemFont) : Modifier.Element
private data class ForegroundColorModifier(val color: Color) : Modifier.Element

fun Modifier.font(font: SystemFont) = this.then(FontModifier(font))
fun Modifier.foregroundStyle(color: Color) = this.then(ForegroundColorModifier(color))

@Composable
fun Text(text: String, modifier: Modifier = Modifier) {
    var chosenFont: SystemFont? = null
    var chosenColor = Color.Unspecified

    modifier.foldIn(Unit) { _, element ->
        when (element) {
            is FontModifier -> chosenFont = element.font
            is ForegroundColorModifier -> chosenColor = element.color
        }
        Unit
    }

    val style = TextStyle(
        fontSize = chosenFont?.size?.sp ?: TextStyle.Default.fontSize,
        fontWeight = chosenFont?.weight ?: TextStyle.Default.fontWeight,
        color = if (chosenColor != Color.Unspecified) chosenColor else TextStyle.Default.color
    )

    MaterialText(
        text = text,
        modifier = modifier,
        style = style
    )

}



// ---------------------------------------------------------------------------------------------
// IMAGE
// ---------------------------------------------------------------------------------------------

@Composable
fun Image(
    name: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit
) {
    val ctx = LocalContext.current
    val id = ctx.resources.getIdentifier(name, "drawable", ctx.packageName)

    ComposeImage(
        painter = painterResource(id),
        contentDescription = null,
        modifier = modifier,
        contentScale = contentScale
    )
}



// =============================================================================================
// STATE, PUBLISHED, TEXTFIELD, SECUREFIELD
// =============================================================================================

// -----------------------------------------
// STATE<T>
// -----------------------------------------

class Binding<T>(
    val value: T,
    val set: (T) -> Unit
)

class State<T>(initial: T) {
    private val s = mutableStateOf(initial)

    val value get() = s.value

    operator fun getValue(thisRef: Any?, property: KProperty<*>) = s.value
    operator fun setValue(thisRef: Any?, property: KProperty<*>, v: T) { s.value = v }

    fun set(v: T) { s.value = v }

    fun binding() = Binding(s.value) { s.value = it }
}

// -----------------------------------------
// Published<T>
// -----------------------------------------

typealias Published<T> = State<T>

// -----------------------------------------
// TEXTFIELD
// -----------------------------------------

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
        modifier = modifier,
        textStyle = TextStyle.Default.copy(fontSize = 16.sp),
        decorationBox = { inner ->
            Box(
                modifier = Modifier
                    .cornerRadius(5)
                    .padding(horizontal = 8, vertical = 8)
                    .background(Color.lightGray.copy(alpha = 0.3f))
            ) {
                if (text.isEmpty()) {
                    MaterialText(
                        text = placeholder,
                        style = TextStyle.Default.copy(fontSize = 16.sp, color = Color.Gray)
                    )
                }
                inner()
            }
        }
    )
}

@Composable
fun TextField(
    placeholder: String,
    value: State<String>,
    modifier: Modifier = Modifier
) {
    val b = value.binding()
    TextField(placeholder, b.value, b.set, modifier)
}



// -----------------------------------------
// SECUREFIELD
// -----------------------------------------

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
        decorationBox = { inner ->
            Box(
                modifier = Modifier
                    .cornerRadius(5)
                    .padding(horizontal = 8, vertical = 8)
                    .background(Color.lightGray.copy(alpha = 0.3f))
            ) {
                if (text.isEmpty()) {
                    MaterialText(
                        text = placeholder,
                        style = TextStyle.Default.copy(fontSize = 16.sp, color = Color.Gray)
                    )
                }
                inner()
            }
        }
    )
}

@Composable
fun SecureField(
    placeholder: String,
    value: State<String>,
    modifier: Modifier = Modifier
) {
    val b = value.binding()
    SecureField(placeholder, b.value, b.set, modifier)
}



// =============================================================================================
// BUTTON, DIVIDER, LIST
// =============================================================================================

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
            indication = null
        ),
        contentAlignment = Alignment.Center,
        content = label
    )
}

@Composable
fun Divider(
    modifier: Modifier = Modifier,
    color: Color = Color.gray,
    thickness: Int = 1
) {
    MaterialDivider(
        modifier = modifier,
        color = color,
        thickness = thickness.dp
    )
}

@Composable
fun List(
    alignment: Alignment = center,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(Modifier.fillMaxSize(), contentAlignment = alignment) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 8)
                .clip(RoundedRectangle(10))
                .background(Color.lightGray.copy(alpha = 0.2f)),
            content = content
        )
    }
}

@Composable
fun <T> List(
    items: List<T>,
    alignment: Alignment = center,
    modifier: Modifier = Modifier,
    rowContent: @Composable (T) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize().padding(horizontal = 8),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        itemsIndexed(items) { index, item ->
            rowContent(item)
            if (index < items.size - 1) Divider()
        }
    }
}


// =============================================================================================
// MVVM (SwiftUI-like)
// =============================================================================================


open class ObservableObject : ViewModel()

@Composable
inline fun <reified T : ObservableObject> StateObject(): T {
    return viewModel<T>()
}

@Composable
inline fun <reified T : ObservableObject> EnvironmentObject(): T {

    val lifecycleOwner = LocalLifecycleOwner.current
    val storeOwner = lifecycleOwner as ViewModelStoreOwner

    // NOTE: Your Compose version supports viewModel(owner)
    return viewModel<T>(storeOwner)
}

@Composable
inline fun <reified T : ObservableObject> ObservedObject(noinline factory: () -> T): T {
    return remember { factory() }
}
