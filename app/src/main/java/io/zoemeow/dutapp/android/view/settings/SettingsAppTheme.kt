package io.zoemeow.dutapp.android.view.settings

import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import io.zoemeow.dutapp.android.R
import io.zoemeow.dutapp.android.model.enums.AppTheme
import io.zoemeow.dutapp.android.viewmodel.GlobalViewModel
import io.zoemeow.dutapp.android.viewmodel.UIStatus

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SettingsAppTheme(
    enabled: MutableState<Boolean>,
    globalViewModel: GlobalViewModel,
    uiStatus: UIStatus,
) {
    val themeList = listOf("Follow device theme", "Dark mode", "Light mode")
    val selectedThemeList = remember { mutableStateOf("") }
    val dynamicColorEnabled = remember { mutableStateOf(true) }

    LaunchedEffect(enabled.value) {
        selectedThemeList.value = themeList[globalViewModel.appTheme.value.ordinal]
        dynamicColorEnabled.value = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            globalViewModel.dynamicColorEnabled.value else false
    }

    fun commitChanges() {
        globalViewModel.appTheme.value =
            AppTheme.values()[themeList.indexOf(selectedThemeList.value)]
        globalViewModel.dynamicColorEnabled.value = dynamicColorEnabled.value
        globalViewModel.requestSaveSettings()

        uiStatus.updateComposeUI()
        enabled.value = false
    }

    if (enabled.value) {
        AlertDialog(
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            ),
            modifier = Modifier
                .padding(15.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            onDismissRequest = {
                enabled.value = false
            },
            title = {
                Text("Select app theme")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        commitChanges()
                    },
                    content = {
                        Text("OK")
                    }
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    themeList.forEach { option ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && option == themeList[0]) ||
                                        option != themeList[0]
                                    )
                                        selectedThemeList.value = option
                                }
                        ) {
                            RadioButton(
                                selected = (option == selectedThemeList.value),
                                onClick = {
                                    if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && option == themeList[0]) ||
                                        option != themeList[0]
                                    )
                                        selectedThemeList.value = option
                                }
                            )
                            Spacer(modifier = Modifier.size(5.dp))
                            Text(text = option)
                        }
                    }
                    Spacer(modifier = Modifier.size(5.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                                    dynamicColorEnabled.value = !dynamicColorEnabled.value
                            }
                    ) {
                        Checkbox(
                            checked = dynamicColorEnabled.value,
                            onCheckedChange = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                                    dynamicColorEnabled.value = it
                            },
                        )
                        Text(text = "Enable dynamic color")
                    }
                    Spacer(modifier = Modifier.size(15.dp))
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.wrapContentSize()
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_info_24),
                            contentDescription = "info_icon",
                            tint = if (uiStatus.mainActivityIsDarkTheme.value) Color.White else Color.Black
                        )
                        Text("Your OS needs at least:\n - Android 9 to follow device theme,\n - Android 12 to enable dynamic color.")
                    }
                }
            }
        )
    }
}