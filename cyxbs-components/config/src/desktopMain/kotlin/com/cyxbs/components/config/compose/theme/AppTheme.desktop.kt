package com.cyxbs.components.config.compose.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.LocalSystemTheme
import androidx.compose.ui.SystemTheme
import androidx.compose.ui.text.font.FontFamily

@OptIn(InternalComposeUiApi::class)
@Composable
internal actual fun ConfigAppThemeBefore(
  content: @Composable () -> Unit
) {
  CompositionLocalProvider(
    LocalAppDark provides false, // 桌面端暂时不开启黑夜模式
    LocalSystemTheme provides SystemTheme.Light,
  ) {
    content()
  }
}

@Composable
internal actual fun ConfigAppThemeAfter(
  content: @Composable () -> Unit
) {
  content()
}

@Composable
internal actual fun getFontFamily(): FontFamily {
  return FontFamily.Default
}
