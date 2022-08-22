package io.zoemeow.dutapp.android.ui.theme

import android.app.Activity
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat
import io.zoemeow.dutapp.android.model.enums.AppTheme
import io.zoemeow.dutapp.android.ui.custom.BackgroundImage
import io.zoemeow.dutapp.android.viewmodel.MainViewModel

val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun MainActivityTheme(
    // Set app mode layout
    darkMode: AppTheme = AppTheme.FollowSystem,
    // Set app black background (for AMOLED)
    blackTheme: Boolean = false,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val mainViewModel = MainViewModel.getInstance()

    val darkTheme: Boolean = when (darkMode) {
        AppTheme.FollowSystem -> isSystemInDarkTheme()
        AppTheme.DarkMode -> true
        AppTheme.LightMode -> false
    }
    var colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current

            if (darkTheme) dynamicDarkColorScheme(context)
                else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    // Set black background for AMOLED.
    if (darkTheme && blackTheme) {
        colorScheme = colorScheme.copy(background = Color.Black)
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            (view.context as Activity).window.statusBarColor = colorScheme.primary.toArgb()
            ViewCompat.getWindowInsetsController(view)?.isAppearanceLightStatusBars = darkTheme
        }
    }

    // Trigger for dark mode detection.
    mainViewModel.uiStatus.mainActivityIsDarkTheme.value = darkTheme

    // Load background image if needed
    BackgroundImage(
        drawable = if (mainViewModel.uiStatus.mainActivityBackgroundDrawable.value != null)
            mainViewModel.uiStatus.mainActivityBackgroundDrawable.value
        else ColorDrawable(colorScheme.background.hashCode())
    )

    // Start compose UI
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
