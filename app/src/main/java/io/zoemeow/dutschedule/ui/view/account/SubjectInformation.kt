package io.zoemeow.dutschedule.ui.view.account

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.dutwrapper.dutwrapper.AccountInformation.SubjectInformation
import io.zoemeow.dutschedule.R
import io.zoemeow.dutschedule.model.AppearanceState
import io.zoemeow.dutschedule.model.ProcessState
import io.zoemeow.dutschedule.ui.view.account.controls.AccountSubjectMoreInformation
import io.zoemeow.dutschedule.ui.view.account.controls.SubjectInformation
import io.zoemeow.dutschedule.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Activity_Account_SubjectInformation(
    context: Context,
    snackBarHostState: SnackbarHostState,
    appearanceState: AppearanceState,
    mainViewModel: MainViewModel,
    onMessageReceived: (String, Boolean, String?, (() -> Unit)?) -> Unit, // (msg, forceDismissBefore, actionText, action)
    onBack: () -> Unit
) {
    val subjectScheduleItem: MutableState<SubjectInformation?> = remember { mutableStateOf(null) }
    val subjectDetailVisible = remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        containerColor = appearanceState.containerColor,
        contentColor = appearanceState.contentColor,
        topBar = {
            Box(
                contentAlignment = Alignment.BottomCenter,
                content = {
                    TopAppBar(
                        title = { Text(context.getString(R.string.account_subjectinfo_title)) },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    onBack()
                                },
                                content = {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        context.getString(R.string.action_back),
                                        modifier = Modifier.size(25.dp)
                                    )
                                }
                            )
                        }
                    )
                    if (mainViewModel.accountSession.subjectSchedule.processState.value == ProcessState.Running) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }
            )
        },
        floatingActionButton = {
            if (mainViewModel.accountSession.subjectSchedule.processState.value != ProcessState.Running) {
                FloatingActionButton(
                    onClick = {
                        mainViewModel.accountSession.fetchSubjectSchedule(force = true)
                    },
                    content = {
                        Icon(Icons.Default.Refresh, context.getString(R.string.action_refresh))
                    }
                )
            }
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                content = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp)
                            .padding(vertical = 2.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        content = {
                            Text(mainViewModel.appSettings.value.currentSchoolYear.composeToString())
                        }
                    )
                    if (mainViewModel.accountSession.subjectSchedule.data.size == 0 && mainViewModel.accountSession.subjectSchedule.processState.value != ProcessState.Running) {
                        Column(
                            modifier = Modifier.fillMaxSize()
                                .padding(horizontal = 15.dp)
                                .padding(vertical = 2.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            content = {
                                Text(
                                    context.getString(R.string.account_subjectinfo_summary_nosubjects),
                                    textAlign = TextAlign.Center
                                )
                            }
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 15.dp)
                                .padding(bottom = 7.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Top,
                            content = {
                                items(mainViewModel.accountSession.subjectSchedule.data) { item ->
                                    SubjectInformation(
                                        modifier = Modifier.padding(bottom = 7.dp),
                                        item = item,
                                        opacity = appearanceState.componentOpacity,
                                        onClick = {
                                            subjectScheduleItem.value = item
                                            subjectDetailVisible.value = true
                                        }
                                    )
                                }
                            }
                        )
                    }
                }
            )
        }
    )
    AccountSubjectMoreInformation(
        context = context,
        item = subjectScheduleItem.value,
        isVisible = subjectDetailVisible.value,
        dismissClicked = {
            subjectDetailVisible.value = false
        },
        onAddToFilterRequested = { item ->
            if (mainViewModel.appSettings.value.newsBackgroundFilterList.any { it.isEquals(item) }) {
                onMessageReceived(context.getString(R.string.account_subjectinfo_filter_alreadyadded), true, null, null)
            } else {
                mainViewModel.appSettings.value = mainViewModel.appSettings.value.clone(
                    newsFilterList = mainViewModel.appSettings.value.newsBackgroundFilterList.also {
                        it.add(item)
                    }
                )
                mainViewModel.saveApplicationSettings(saveUserSettings = true)
                onMessageReceived(context.getString(
                        R.string.account_subjectinfo_filter_added,
                        item
                    ), true, null, null)
            }
        }
    )

    val hasRun = remember { mutableStateOf(false) }
    run {
        if (!hasRun.value) {
            mainViewModel.accountSession.fetchSubjectSchedule()
            hasRun.value = true
        }
    }
}