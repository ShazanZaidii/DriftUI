package com.example.driftui.core

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.abs
import androidx.compose.material3.ExperimentalMaterial3Api
import android.app.Activity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.ui.graphics.Brush

// ---------------------------
// Toolbar model + registration
// ---------------------------
enum class ToolbarPlacement { Leading, Center, Trailing }

data class ToolbarEntry(
    val placement: ToolbarPlacement,
    val content: @Composable () -> Unit
)

// ✅ DATA MODEL FOR NAVIGATION ENTRIES
data class StackEntry(
    val isRoot: Boolean = false,
    val content: @Composable () -> Unit
)

val LocalToolbarState = compositionLocalOf<MutableList<ToolbarEntry>?> { null }
val LocalToolbarLayoutState = compositionLocalOf<MutableState<Modifier?>?> { null }

@Composable
fun ToolbarItem(
    placement: ToolbarPlacement,
    content: @Composable () -> Unit
) {
    val state = LocalToolbarState.current ?: return
    val entry = remember { ToolbarEntry(placement, content) }
    DisposableEffect(state, entry) {
        state.add(entry)
        onDispose { state.remove(entry) }
    }
}

// ---------------------------
// Toolbar style locals + modifier
// ---------------------------

val LocalToolbarForeground = compositionLocalOf<Color?> { null }
val LocalToolbarBackground = compositionLocalOf<Color?> { null }
val LocalToolbarElevation = compositionLocalOf<Dp?> { null }
val LocalToolbarContentPadding = compositionLocalOf<Dp?> { null }

@Composable
fun toolbar(
    modifier: Modifier = Modifier,
    foreground: Color? = null,
    background: Color? = null,
    elevation: Dp? = null,
    contentPadding: Dp? = null,
    content: @Composable () -> Unit
) {
    // 1. Create a style modifier from the arguments
    var styleMod = modifier

    if (foreground != null || background != null || elevation != null || contentPadding != null) {
        styleMod = styleMod.then(
            Modifier.toolbarStyle(
                foregroundColor = foreground,
                backgroundColor = background,
                elevation = elevation,
                contentPadding = contentPadding
            )
        )
    }

    // 2. Extract values to provide to children (CompositionLocal)
    var finalFg: Color? = foreground
    var finalBg: Color? = background
    var finalElev: Dp? = elevation
    var finalPad: Dp? = contentPadding

    styleMod.foldIn(Unit) { _, element ->
        if (element is ToolbarStyleModifier) {
            if (element.foreground != null) finalFg = element.foreground
            if (element.background != null) finalBg = element.background
            if (element.elevation != null) finalElev = element.elevation
            if (element.contentPadding != null) finalPad = element.contentPadding
        }
        Unit
    }

    // 3. Push the FULL modifier up to NavigationStack
    val layoutState = LocalToolbarLayoutState.current
    DisposableEffect(layoutState, styleMod) {
        val previous = layoutState?.value
        layoutState?.value = styleMod
        onDispose {
            layoutState?.value = previous
        }
    }

    // 4. Provide locals to children inside the toolbar content
    CompositionLocalProvider(
        LocalToolbarForeground provides finalFg,
        LocalToolbarBackground provides finalBg,
        LocalToolbarElevation provides finalElev,
        LocalToolbarContentPadding provides finalPad
    ) {
        content()
    }
}

// ---------------------------
// ✅ THE NEW "RESPECTFUL" RENDERER
// ---------------------------
@Composable
private fun CustomToolbarSurface(
    modifier: Modifier = Modifier,
    navController: DriftNavController,
    toolbarItems: List<ToolbarEntry>,
    navTitle: String?,
    hideBackButton: Boolean
) {
    // 1. Resolve Styles
    var styleFg: Color? = null
    var styleBg: Color? = null
    var styleGradient: GradientColor? = null
    var styleElev: Dp? = null
    var stylePad: Dp? = null

    modifier.foldIn(Unit) { _, element ->
        if (element is ToolbarStyleModifier) {
            styleFg = element.foreground
            styleBg = element.background
            styleElev = element.elevation
            stylePad = element.contentPadding
        }
        if (element is ToolbarGradientModifier) {
            styleGradient = element.gradient
        }
        Unit
    }

    val finalFg = styleFg ?: LocalToolbarForeground.current ?: driftColors.text
    val finalBg = styleBg ?: LocalToolbarBackground.current ?: MaterialTheme.colorScheme.surface
    val finalElev = styleElev ?: 0.dp
    val finalPad = stylePad ?: 12.dp

    // 2. Container (Surface handles Elevation)
    Surface(
        modifier = modifier, // Applies user's height/width (e.g. 70dp)
        // If gradient exists, Surface must be transparent
        color = if (styleGradient != null) Color.Transparent else finalBg,
        tonalElevation = finalElev,
        shadowElevation = finalElev
    ) {
        // 3. Gradient Layer (Fills the Surface edge-to-edge)
        Box(
            modifier = if (styleGradient != null)
                Modifier.fillMaxSize().background(Brush.linearGradient(styleGradient!!.colors))
            else Modifier.fillMaxSize()
        ) {
            // 4. Layout Layer (Safe Content)
            // 🚀 MAGIC FIX: statusBarsPadding() pushes CONTENT down, but keeps background up.
            Box(
                modifier = Modifier
                    .fillMaxSize() // Fill the 70dp height
                    .statusBarsPadding() // Duck under the notch!
                    .padding(horizontal = finalPad),
                contentAlignment = Alignment.Center
            ) {

                // --- LEADING SLOT ---
                Box(Modifier.align(Alignment.CenterStart)) {
                    val leading = toolbarItems.firstOrNull { it.placement == ToolbarPlacement.Leading }

                    if (leading != null) {
                        leading.content()
                    } else if (navController.canPop() && !hideBackButton) {
                        Text(
                            "< Back",
                            Modifier
                                .padding(end = 12.dp)
                                .onTapGesture { navController.pop() }
                                .font(system(16, medium))
                                .foregroundStyle(finalFg)
                        )
                    }
                }

                // --- CENTER SLOT ---
                Box(Modifier.align(Alignment.Center)) {
                    if (navTitle != null) {
                        Text(navTitle, Modifier.font(system(18, semibold)).foregroundStyle(finalFg))
                    } else {
                        toolbarItems.firstOrNull { it.placement == ToolbarPlacement.Center }?.content?.invoke()
                    }
                }

                // --- TRAILING SLOT ---
                Row(
                    Modifier.align(Alignment.CenterEnd),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val trailing = toolbarItems.filter { it.placement == ToolbarPlacement.Trailing }
                    trailing.forEach { it.content() }
                }
            }
        }
    }
}


// ---------------------------
// Navigation controller
// ---------------------------

class DriftNavController(private val stack: MutableList<StackEntry>) {

    // Normal push
    fun push(screen: @Composable () -> Unit) {
        stack.add(StackEntry(isRoot = false, content = screen))
    }

    // Push Replacement
    fun pushReplacement(screen: @Composable () -> Unit) {
        stack.clear()
        stack.add(StackEntry(isRoot = true, content = screen))
    }

    fun pop() {
        if (stack.isNotEmpty()) {
            stack.removeAt(stack.lastIndex)
        }
    }

    fun clear() {
        stack.clear()
    }

    fun dismiss() = pop()

    fun currentEntry(): StackEntry? = stack.lastOrNull()

    // Compatibility helper
    fun current(): (@Composable () -> Unit)? = stack.lastOrNull()?.content

    fun canPop() = stack.isNotEmpty()
}

val LocalNavController = compositionLocalOf<DriftNavController> {
    error("NavigationStack not found in composition.")
}

@Composable
fun Dismiss(): () -> Unit {
    val nav = LocalNavController.current
    return remember {
        {nav.dismiss()}
    }
}

// ---------------------------
// NavigationLink variants
// ---------------------------
@Composable
fun NavigationLink(
    title: String,
    destination: @Composable () -> Unit
) {
    val nav = LocalNavController.current
    Text(
        title,
        Modifier.onTapGesture {
            nav.push(destination)
        }
    )
}

@Composable
fun NavigationLink(
    destination: @Composable () -> Unit,
    label: @Composable () -> Unit
) {
    val nav = LocalNavController.current
    Box(
        Modifier.onTapGesture {
            nav.push(destination)
        }
    ) {
        label()
    }
}


// ---------------------------------------------------------
// SHEET SYSTEM
// ---------------------------------------------------------

data class SheetState(
    var isPresented: MutableState<Boolean>,
    val content: MutableState<(@Composable () -> Unit)?>
)

// Sheet detent model
sealed class SheetDetent {
    data class Fraction(val fraction: Float) : SheetDetent()
    object Large : SheetDetent()
    companion object
}

// Detent extensions
val Double.detent get() = SheetDetent.Fraction(this.toFloat())
val Float.detent get() = SheetDetent.Fraction(this)
val Int.percentDetent get() = SheetDetent.Fraction(this / 100f)
val SheetDetent.Companion.large get() = SheetDetent.Large
fun SheetDetent.Companion.fraction(value: Double) = SheetDetent.Fraction(value.toFloat())


@Composable
fun SheetHost(sheetState: SheetState, sheetModifier: SheetModifier?) {
    val isPresented by sheetState.isPresented
    val sheetContent by sheetState.content

    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(isPresented) {
        if (isPresented) {
            isVisible = true
        } else {
            delay(300)
            isVisible = false
        }
    }

    if (!isVisible || sheetContent == null || sheetModifier == null) {
        return
    }

    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }

    val detentFractions = remember(sheetModifier.detents) {
        sheetModifier.detents.map { detent ->
            when (detent) {
                is SheetDetent.Fraction -> detent.fraction
                SheetDetent.Large -> 0.95f
            }
        }.sorted()
    }

    val initialFraction = remember(sheetModifier.initialDetent) {
        when (val initial = sheetModifier.initialDetent) {
            is SheetDetent.Fraction -> initial.fraction
            SheetDetent.Large -> 0.95f
        }
    }

    var targetFraction by remember { mutableStateOf(initialFraction) }
    var dragOffsetPx by remember { mutableStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    LaunchedEffect(isPresented) {
        if (isPresented) {
            targetFraction = initialFraction
            dragOffsetPx = 0f
        } else {
            targetFraction = 0f
        }
    }

    val animatedFraction by animateFloatAsState(
        targetValue = targetFraction,
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        ),
        label = "sheet_fraction"
    )

    fun snapToNearestDetent() {
        isDragging = false
        val dragFraction = dragOffsetPx / screenHeightPx
        val currentFraction = (targetFraction + dragFraction).coerceIn(0f, 1f)

        val nearest = detentFractions.minByOrNull {
            abs(it - currentFraction)
        } ?: detentFractions.first()

        if (currentFraction < detentFractions.first() * 0.6f && sheetModifier.allowDismiss) {
            sheetState.isPresented.value = false
        } else {
            targetFraction = nearest
        }

        dragOffsetPx = 0f
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Scrim
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    if (sheetModifier.allowDismiss) {
                        sheetState.isPresented.value = false
                    }
                }
        )

        val displayFraction = if (isDragging) {
            (animatedFraction + (dragOffsetPx / screenHeightPx)).coerceIn(0f, 1f)
        } else {
            animatedFraction.coerceIn(0f, 1f)
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(displayFraction)
                .align(Alignment.BottomCenter)
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onDragStart = {
                            isDragging = true
                        },
                        onDragEnd = {
                            snapToNearestDetent()
                        },
                        onDragCancel = {
                            isDragging = false
                            dragOffsetPx = 0f
                        },
                        onVerticalDrag = { _, dragAmount ->
                            dragOffsetPx = (dragOffsetPx - dragAmount).coerceIn(
                                -screenHeightPx * 0.5f,
                                screenHeightPx * 0.5f
                            )
                        }
                    )
                },
            shape = RoundedCornerShape(
                topStart = sheetModifier.cornerRadius.dp,
                topEnd = sheetModifier.cornerRadius.dp
            ),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            shadowElevation = 16.dp
        ) {
            Column(Modifier.fillMaxWidth()) {
                // Grabber
                if (sheetModifier.showGrabber) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .width(36.dp)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color.Gray.copy(alpha = 0.4f))
                        )
                    }
                }

                // Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    sheetContent?.invoke()
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationStack(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val navStack = remember { mutableStateListOf<StackEntry>() }
    val navController = remember { DriftNavController(navStack) }
    val currentEntry = navController.currentEntry()

    // Lifecycle & Defaults
    var onAppearAction: (() -> Unit)? = null
    var onDisappearAction: (() -> Unit)? = null
    var navTitle: String? = null
    var hideBackButton = true
    var overrideScheme: DriftColorScheme? = null
    var sheetModifier: SheetModifier? = null
    var navToolbarFg: Color? = null
    var navToolbarBg: Color? = null
    var navToolbarElev: Dp? = null
    var navToolbarPadding: Dp? = null

    modifier.foldIn(Unit) { _, el ->
        if (el is NavigationTitleModifier) navTitle = el.title
        else if (el is BackButtonHiddenModifier) hideBackButton = el.hidden
        else if (el is PreferredColorSchemeModifier) overrideScheme = el.scheme
        else if (el is SheetModifier) sheetModifier = el
        else if (el is LifecycleAppearModifier) onAppearAction = el.action
        else if (el is LifecycleDisappearModifier) onDisappearAction = el.action
        else if (el is ToolbarStyleModifier) {
            if (el.foreground != null) navToolbarFg = el.foreground
            if (el.background != null) navToolbarBg = el.background
            if (el.elevation != null) navToolbarElev = el.elevation
            if (el.contentPadding != null) navToolbarPadding = el.contentPadding
        }
        Unit
    }

    val context = LocalContext.current
    BackHandler(enabled = navController.canPop()) {
        if (currentEntry?.isRoot == true) (context as? Activity)?.finish() else navController.pop()
    }

    val isDark = when (overrideScheme) {
        DriftColorScheme.Dark -> true
        DriftColorScheme.Light -> false
        null -> isSystemInDarkTheme()
    }

    val colors = if (isDark) darkColorScheme(surface = Color(0xFF1E1E1E)) else lightColorScheme(surface = Color.White)

    MaterialTheme(colorScheme = colors) {
        val toolbarItems = remember { mutableStateListOf<ToolbarEntry>() }
        val toolbarLayoutState = remember { mutableStateOf<Modifier?>(null) }

        LaunchedEffect(currentEntry) { toolbarItems.clear() }

        // Sheet Logic
        val internalSheetState = remember { SheetState(mutableStateOf(false), mutableStateOf(null)) }
        if (sheetModifier != null) {
            val external = sheetModifier.isPresented
            val extSet = external.getSetter()
            LaunchedEffect(external.value) {
                internalSheetState.isPresented.value = external.value
                if(external.value) internalSheetState.content.value = sheetModifier.content
            }
            val internalVal by internalSheetState.isPresented
            LaunchedEffect(internalVal) { if(!internalVal && external.value) extSet(false) }
        }

        DisposableEffect(currentEntry) {
            onAppearAction?.invoke()
            onDispose { onDisappearAction?.invoke() }
        }

        CompositionLocalProvider(
            LocalToolbarState provides toolbarItems,
            LocalNavController provides navController,
            LocalToolbarLayoutState provides toolbarLayoutState,
            LocalToolbarForeground provides navToolbarFg,
            LocalToolbarBackground provides navToolbarBg,
            LocalToolbarElevation provides navToolbarElev,
            LocalToolbarContentPadding provides navToolbarPadding
        ) {
            Column(
                Modifier.fillMaxSize().windowInsetsPadding(WindowInsets(0,0,0,0))
            ) {
                // RENDER TOOLBAR
                // Shows if items exist, OR if a title exists, OR if Navigation exists, OR if user called toolbar() explicitly
                val shouldShowToolbar = toolbarItems.isNotEmpty() ||
                        (navTitle != null) ||
                        (navController.canPop() && !hideBackButton) ||
                        toolbarLayoutState.value != null

                if (shouldShowToolbar) {
                    val userMod = toolbarLayoutState.value ?: Modifier

                    // Force width, but obey user height
                    val finalMod = userMod.fillMaxWidth()

                    CustomToolbarSurface(
                        modifier = finalMod,
                        navController = navController,
                        toolbarItems = toolbarItems,
                        navTitle = navTitle,
                        hideBackButton = hideBackButton
                    )
                }

                Box(Modifier.fillMaxSize()) {
                    if (currentEntry == null) content() else currentEntry.content.invoke()
                }
            }
        }
        SheetHost(internalSheetState, sheetModifier)
        DisposableEffect(Unit) { onDispose { toolbarItems.clear(); navStack.clear() } }
    }
}

@Composable
fun useNav() = LocalNavController.current

class NavigationAction(private val state: State<(() -> Unit)?>) {
    fun set(action: () -> Unit) { state.set(action) }
    operator fun invoke() { state.value?.invoke() }
}

@Composable
fun useNavigationAction(): NavigationAction {
    val state = remember { State<(() -> Unit)?>(null) }
    return NavigationAction(state)
}