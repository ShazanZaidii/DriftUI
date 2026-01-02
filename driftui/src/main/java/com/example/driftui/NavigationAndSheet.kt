package com.example.driftui
//This file is NavigationSheet.kt
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
// --- CUSTOM IMPORTS ---
import com.example.driftui.driftColors
import com.example.driftui.Text
import com.example.driftui.system
import com.example.driftui.bold
import com.example.driftui.semibold
import com.example.driftui.onTapGesture
import com.example.driftui.State
import com.example.driftui.ToolbarStyleModifier
import com.example.driftui.NavigationTitleModifier
import com.example.driftui.BackButtonHiddenModifier
import com.example.driftui.PreferredColorSchemeModifier
import com.example.driftui.DriftColorScheme
import com.example.driftui.SheetModifier


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

// toolbar {} block
@Composable
fun toolbar(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    // Extract ONLY styling values from modifier for locals
    var fg: Color? = null
    var bg: Color? = null
    var elev: Dp? = null
    var paddingDp: Dp? = null

    modifier.foldIn(Unit) { _, element ->
        if (element is ToolbarStyleModifier) {
            if (element.foreground != null) fg = element.foreground
            if (element.background != null) bg = element.background
            if (element.elevation != null) elev = element.elevation
            if (element.contentPadding != null) paddingDp = element.contentPadding
        }
        Unit
    }

    // Write the full layout modifier into the shared LayoutState
    val layoutState = LocalToolbarLayoutState.current
    DisposableEffect(layoutState, modifier) {
        val previous = layoutState?.value
        layoutState?.value = modifier
        onDispose {
            layoutState?.value = previous
        }
    }

    CompositionLocalProvider(
        LocalToolbarForeground provides fg,
        LocalToolbarBackground provides bg,
        LocalToolbarElevation provides elev,
        LocalToolbarContentPadding provides paddingDp
    ) {
        content()
    }
}

// ---------------------------
// Custom toolbar renderer
// ---------------------------
@Composable
private fun CustomToolbarSurface(
    modifier: Modifier = Modifier,
    navController: DriftNavController,
    toolbarItems: List<ToolbarEntry>,
    navTitle: String?,
    hideBackButton: Boolean
) {
    val fg = LocalToolbarForeground.current
    val bg = LocalToolbarBackground.current
    val elev = LocalToolbarElevation.current
    val paddingFromChild = LocalToolbarContentPadding.current

    Surface(
        modifier = modifier,
        color = bg ?: MaterialTheme.colorScheme.surface,
        tonalElevation = elev ?: 0.dp,
        shadowElevation = elev ?: 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (paddingFromChild != null)
                        Modifier.padding(horizontal = paddingFromChild)
                    else Modifier.padding(horizontal = 12.dp)
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // LEADING
            Box(
                modifier = Modifier.wrapContentWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                val leading = toolbarItems.firstOrNull { it.placement == ToolbarPlacement.Leading }

                if (leading != null) {
                    leading.content()
                } else if (navController.canPop() && !hideBackButton) {
                    Text(
                        "< Back",
                        Modifier
                            .padding(start = 8.dp)
                            .onTapGesture { navController.pop() }
                            .font(system(16, bold))
                            .foregroundStyle(fg ?: driftColors.text)
                    )
                }
            }

            // CENTER
            Box(
                modifier = Modifier
                    .weight(1f)
                    .wrapContentWidth(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                if (navTitle != null) {
                    Text(navTitle, Modifier.font(system(18, semibold)))
                } else {
                    toolbarItems.firstOrNull { it.placement == ToolbarPlacement.Center }
                        ?.content?.invoke()
                }
            }

            // TRAILING
            Row(
                modifier = Modifier.wrapContentWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val trailing = toolbarItems.filter { it.placement == ToolbarPlacement.Trailing }
                trailing.forEach { it.content() }
            }
        }

    }
}


// ---------------------------
// Navigation controller
// ---------------------------

// ✅ UPDATED CONTROLLER TO SUPPORT STACK ENTRY & REPLACEMENT
class DriftNavController(private val stack: MutableList<StackEntry>) {

    // Normal push
    fun push(screen: @Composable () -> Unit) {
        stack.add(StackEntry(isRoot = false, content = screen))
    }

    // ✅ NEW: Push Replacement (Clears history, sets new Root)
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

    // Helper to peek at full entry
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
            easing = androidx.compose.animation.core.FastOutSlowInEasing
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
    // ✅ CHANGED: Now using StackEntry instead of raw composable lambda
    val navStack = remember { mutableStateListOf<StackEntry>() }
    val navController = remember { DriftNavController(navStack) }

    val currentEntry = navController.currentEntry()
    val toolbarItems = remember { mutableStateListOf<ToolbarEntry>() }
    val toolbarLayoutState = remember { mutableStateOf<Modifier?>(null) }


    LaunchedEffect(currentEntry) {
        toolbarItems.clear()
    }


    // ✅ AUTO-HANDLE BACK BUTTON
    // If it's a root replacement -> Exit App. Else -> Go Back.
    val context = LocalContext.current
    BackHandler(enabled = navController.canPop()) {
        if (currentEntry?.isRoot == true) {
            (context as? Activity)?.finish()
        } else {
            navController.pop()
        }
    }

    var navTitle: String? = null
    modifier.foldIn(Unit) { _, el ->
        if (el is NavigationTitleModifier) navTitle = el.title
        Unit
    }

    var hideBackButton = false
    modifier.foldIn(Unit) { _, el ->
        if (el is BackButtonHiddenModifier) hideBackButton = el.hidden
        Unit
    }

    var overrideScheme: DriftColorScheme? = null
    modifier.foldIn(Unit) { _, el ->
        if (el is PreferredColorSchemeModifier) overrideScheme = el.scheme
        Unit
    }

    var sheetModifier: SheetModifier? = null
    modifier.foldIn(Unit) { _, el ->
        if (el is SheetModifier) sheetModifier = el
        Unit
    }

    var navToolbarFg: Color? = null
    var navToolbarBg: Color? = null
    var navToolbarElev: Dp? = null
    var navToolbarPadding: Dp? = null


    // Lifecycle variables to hold the actions
    var onAppearAction: (() -> Unit)? = null
    var onDisappearAction: (() -> Unit)? = null

    modifier.foldIn(Unit) { _, el ->
        if (el is NavigationTitleModifier) navTitle = el.title
        else if (el is BackButtonHiddenModifier) hideBackButton = el.hidden
        else if (el is PreferredColorSchemeModifier) overrideScheme = el.scheme
        else if (el is SheetModifier) sheetModifier = el
        else if (el is ToolbarStyleModifier) { /* ... */ }

        // NEW: Extract Lifecycle Actions
        else if (el is LifecycleAppearModifier) onAppearAction = el.action
        else if (el is LifecycleDisappearModifier) onDisappearAction = el.action
        Unit
    }


    modifier.foldIn(Unit) { _, el ->
        if (el is ToolbarStyleModifier) {
            if (el.foreground != null) navToolbarFg = el.foreground
            if (el.background != null) navToolbarBg = el.background
            if (el.elevation != null) navToolbarElev = el.elevation
            if (el.contentPadding != null) navToolbarPadding = el.contentPadding
        }
        Unit
    }

    val isDark = when (overrideScheme) {
        DriftColorScheme.Dark -> true
        DriftColorScheme.Light -> false
        null -> isSystemInDarkTheme()
    }

    val colors = if (isDark) {
        darkColorScheme(
            primary = Color(0xFF88B4B5),
            onPrimary = Color.Black,
            background = Color(0xFF121212),
            onBackground = Color.White,
            surface = Color(0xFF1E1E1E),
        )
    } else {
        lightColorScheme(
            primary = Color(0xFF567779),
            onPrimary = Color.White,
            background = Color(0xFFF7F3F5),
            onBackground = Color.Black,
            surface = Color.White,
        )
    }

    MaterialTheme(colorScheme = colors) {
        val toolbarItems = remember { mutableStateListOf<ToolbarEntry>() }
        val toolbarLayoutState = remember { mutableStateOf<Modifier?>(null) }

        // Create internal sheet state that syncs with external
        val internalSheetState = remember {
            SheetState(
                isPresented = mutableStateOf(false),
                content = mutableStateOf(null)
            )
        }

        // BIDIRECTIONAL SYNC LOGIC
        if (sheetModifier != null) {
            val externalState = sheetModifier.isPresented
            val externalSetter = externalState.getSetter()

            // --- 1. External → Internal (Sheet is opened/closed by developer action)
            LaunchedEffect(externalState.value) {
                internalSheetState.isPresented.value = externalState.value
                if (externalState.value) {
                    internalSheetState.content.value = sheetModifier.content
                }
            }

            // --- 2. Internal → External (Sheet is dismissed by user swipe/scrim tap)
            val internalIsPresented by internalSheetState.isPresented
            LaunchedEffect(internalIsPresented) {
                if (!internalIsPresented && externalState.value) {
                    externalSetter(false)
                }
            }
        }

        // 3. --- LIFECYCLE HOOKS ---
        // DisposableEffect runs cleanup (onDispose) before the content leaves the Composition
        DisposableEffect(currentEntry) {
            // 3a. Run onAppear when the screen *starts* composing
            onAppearAction?.invoke()

            // 3b. Run onDisappear when the screen *leaves* the Composition
            onDispose {
                onDisappearAction?.invoke()
            }
        }

        CompositionLocalProvider(
            LocalToolbarState provides toolbarItems,
            LocalNavController provides navController,
            LocalToolbarLayoutState provides toolbarLayoutState
        ) {
            CompositionLocalProvider(
                LocalToolbarForeground provides navToolbarFg,
                LocalToolbarBackground provides navToolbarBg,
                LocalToolbarElevation provides navToolbarElev,
                LocalToolbarContentPadding provides navToolbarPadding
            ) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets(0, 0, 0, 0))
                ) {
                    val shouldShowToolbar = toolbarItems.isNotEmpty() ||
                            (navController.canPop() && !hideBackButton)

                    if (shouldShowToolbar) {
                        val layoutModifier = toolbarLayoutState.value ?: Modifier.fillMaxWidth()
                        CustomToolbarSurface(
                            modifier = layoutModifier,
                            navController = navController,
                            toolbarItems = toolbarItems,
                            navTitle = navTitle,
                            hideBackButton = hideBackButton
                        )
                    }

                    Box(Modifier.fillMaxSize()) {
                        // ✅ RENDER LOGIC UPDATE: Use entry or content()
                        if (currentEntry == null) content()
                        else currentEntry.content.invoke()
                    }
                }
            }
        }

        // Render sheet if present
        SheetHost(internalSheetState, sheetModifier)

        DisposableEffect(Unit) {
            onDispose {
                toolbarItems.clear()
                navStack.clear()
            }
        }
    }
}

@Composable
fun useNav(): DriftNavController {
    return LocalNavController.current
}