package io.zoemeow.dutschedule.activity

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import io.dutwrapperlib.dutwrapper.objects.accounts.SubjectScheduleItem
import io.zoemeow.dutschedule.model.ProcessState
import io.zoemeow.dutschedule.model.account.AccountAuth
import io.zoemeow.dutschedule.ui.component.account.AccountInfoBanner
import io.zoemeow.dutschedule.ui.component.account.LoginBannerNotLoggedIn
import io.zoemeow.dutschedule.ui.component.account.LoginDialog
import io.zoemeow.dutschedule.ui.component.account.LogoutDialog
import io.zoemeow.dutschedule.ui.component.account.subjectitem.SubjectDetailItem
import io.zoemeow.dutschedule.ui.component.account.subjectitem.SubjectSummaryItem
import io.zoemeow.dutschedule.ui.component.base.ButtonBase
import io.zoemeow.dutschedule.ui.component.base.OutlinedTextBox
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AccountActivity: BaseActivity() {
    @Composable
    override fun OnPreloadOnce() {

    }

    @Composable
    override fun OnMainView(padding: PaddingValues) {
        when (intent.action) {
            "subject_schedule" -> {
                AccountSubjectScheduleView()
            }
            "subject_fee" -> {
                AccountSubjectFeeView()
            }
            "acc_info" -> {
                AccountInformationView()
            }
            else -> {
                MainView()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun AccountSubjectScheduleView() {
        val subjectScheduleItem: MutableState<SubjectScheduleItem?> = remember { mutableStateOf(null) }
        val subjectDetailVisible = remember { mutableStateOf(false) }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Subject schedule") },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                setResult(RESULT_OK)
                                finish()
                            },
                            content = {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    "",
                                    modifier = Modifier.size(25.dp)
                                )
                            }
                        )
                    },
                )
            },
            content = { padding ->
                when (getMainViewModel().subjectSchedule.value.processState) {
                    ProcessState.NotRunYet,
                    ProcessState.Failed -> {
                        val p1 = padding
                    }
                    ProcessState.Running -> {
                        Surface(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(padding),
                            color = Color.Transparent,
                            content = {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    content = {
                                        CircularProgressIndicator()
                                    }
                                )
                            }
                        )
                    }
                    ProcessState.Successful -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(padding)
                                .padding(horizontal = 15.dp)
                                .padding(bottom = 7.dp)
                                .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Top,
                            content = {
                                getMainViewModel().subjectSchedule.value.data?.forEach { item ->
                                    SubjectSummaryItem(
                                        title = item.name,
                                        content = item.lecturer,
                                        clicked = {
                                            subjectScheduleItem.value = item
                                            subjectDetailVisible.value = true
                                        }
                                    )
                                }
                            }
                        )
                    }
                }
            }
        )
        SubjectDetailItem(
            item = subjectScheduleItem.value,
            isVisible = subjectDetailVisible.value,
            dismissClicked = {
                subjectDetailVisible.value = false
            }
        )

        val hasRun = remember { mutableStateOf(false) }
        run {
            if (!hasRun.value) {
                CoroutineScope(Dispatchers.IO).launch {
                    getMainViewModel().accountLogin(
                        after = {
                            if (it) {
                                getMainViewModel().accountGetSubjectSchedule()
                            }
                        }
                    )
                }
                hasRun.value = true
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun AccountSubjectFeeView() {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Subject fee") },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                setResult(RESULT_OK)
                                finish()
                            },
                            content = {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    "",
                                    modifier = Modifier.size(25.dp)
                                )
                            }
                        )
                    },
                )
            },
            content = { padding ->
                when (getMainViewModel().subjectFee.value.processState) {
                    ProcessState.NotRunYet,
                    ProcessState.Failed -> {
                        val p1 = padding
                    }
                    ProcessState.Running -> {
                        Surface(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(padding),
                            color = Color.Transparent,
                            content = {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    content = {
                                        CircularProgressIndicator()
                                    }
                                )
                            }
                        )
                    }
                    ProcessState.Successful -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(padding)
                                .padding(horizontal = 15.dp)
                                .padding(bottom = 7.dp)
                                .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Top,
                            content = {
                                getMainViewModel().subjectFee.value.data?.forEach { item ->
                                    SubjectSummaryItem(
                                        title = item.name,
                                        content = "${item.credit} credit(s), ${item.price} VND (${if (item.debt) "not completed yet" else "completed"})"
                                    )
                                }
                            }
                        )
                    }
                }
            }
        )

        val hasRun = remember { mutableStateOf(false) }
        run {
            if (!hasRun.value) {
                CoroutineScope(Dispatchers.IO).launch {
                    getMainViewModel().accountLogin(
                        after = {
                            if (it) {
                                getMainViewModel().accountGetSubjectFee()
                            }
                        }
                    )
                }
                hasRun.value = true
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun AccountInformationView() {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Basic Information") },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                setResult(RESULT_OK)
                                finish()
                            },
                            content = {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    "",
                                    modifier = Modifier.size(25.dp)
                                )
                            }
                        )
                    },
                )
            },
            content = { padding ->
                val data = getMainViewModel().accountInformation.value.data
                val mapPersonalInfo = mapOf(
                    "Name" to (data?.name ?: "(unknown)"),
                    "Date of birth" to (data?.dateOfBirth ?: "(unknown)"),
                    "Place of birth" to (data?.birthPlace ?: "(unknown)"),
                    "Gender" to (data?.gender ?: "(unknown)"),
                    "National ID card" to (data?.nationalIdCard ?: "(unknown)"),
                    "National card issue place and date" to ("${data?.nationalIdCardIssuePlace ?: "(unknown)"} on ${data?.nationalIdCardIssueDate ?: "(unknown)"}"),
                    "Citizen card date" to (data?.citizenIdCardIssueDate ?: "(unknown)"),
                    "Citizen ID card" to (data?.citizenIdCard ?: "(unknown)"),
                    "Bank card ID" to ("${data?.accountBankId ?: "(unknown)"} (${data?.accountBankName ?: "(unknown)"})"),
                    "Personal email" to (data?.personalEmail ?: "(unknown)"),
                    "Phone number" to (data?.phoneNumber ?: "(unknown)"),
                    "Class" to (data?.schoolClass ?: "(unknown)"),
                    "Specialization" to (data?.specialization ?: "(unknown)"),
                    "Training program plan" to (data?.trainingProgramPlan ?: "(unknown)"),
                    "School email" to (data?.schoolEmail ?: "(unknown)"),
                )

                when (getMainViewModel().accountInformation.value.processState) {
                    ProcessState.NotRunYet,
                    ProcessState.Failed -> {
                        val p1 = padding
                    }

                    ProcessState.Running -> {
                        Surface(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(padding),
                            color = Color.Transparent,
                            content = {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    content = {
                                        CircularProgressIndicator()
                                    }
                                )
                            }
                        )
                    }

                    ProcessState.Successful -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(padding)
                                .padding(horizontal = 15.dp)
                                .padding(bottom = 7.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Top,
                            content = {
                                Text("Click and hold a text field to show option to copy it.")
                                Spacer(modifier = Modifier.size(5.dp))
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .verticalScroll(rememberScrollState()),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Top
                                ) {
                                    mapPersonalInfo.keys.forEach { title ->
                                        OutlinedTextBox(
                                            title = title,
                                            value = mapPersonalInfo[title] ?: "(unknown)",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(bottom = 5.dp)
                                        )
                                    }
                                }
                            }
                        )
                    }
                }
            }
        )

        val hasRun = remember { mutableStateOf(false) }
        run {
            if (!hasRun.value) {
                CoroutineScope(Dispatchers.IO).launch {
                    getMainViewModel().accountLogin(
                        after = {
                            if (it) {
                                getMainViewModel().accountGetInformation()
                            }
                        }
                    )
                }
                hasRun.value = true
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun MainView() {
        val loginDialogVisible = remember { mutableStateOf(false) }
        val loginDialogEnabled = remember { mutableStateOf(true) }
        val logoutDialogVisible = remember { mutableStateOf(false) }
        val context = LocalContext.current

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Account") },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                setResult(RESULT_OK)
                                finish()
                            },
                            content = {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    "",
                                    modifier = Modifier.size(25.dp)
                                )
                            }
                        )
                    },
                )
            },
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                        .verticalScroll(rememberScrollState()),
                    content = {
                        when (getMainViewModel().accountSession.value.processState) {
                            ProcessState.NotRunYet,
                            ProcessState.Failed -> {
                                LoginBannerNotLoggedIn(
                                    padding = PaddingValues(10.dp),
                                    clicked = {
                                        loginDialogVisible.value = true
                                    },
                                )
                            }
                            ProcessState.Running -> {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                    content = {
                                        CircularProgressIndicator()
                                    }
                                )
                            }
                            ProcessState.Successful -> {
                                AccountInfoBanner(
                                    padding = PaddingValues(10.dp),
                                    isLoading = getMainViewModel().accountInformation.value.processState == ProcessState.Running,
                                    username = getMainViewModel().accountInformation.value.data?.studentId ?: "(unknown)",
                                    schoolClass = getMainViewModel().accountInformation.value.data?.schoolClass ?: "(unknown)",
                                    trainingProgramPlan = getMainViewModel().accountInformation.value.data?.trainingProgramPlan ?: "(unknown)"
                                )
                                ButtonBase(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 5.dp),
                                    modifierInside = Modifier.padding(vertical = 7.dp),
                                    content = { Text("Subject schedule") },
                                    horizontalArrangement = Arrangement.Start,
                                    isOutlinedButton = true,
                                    clicked = {
                                        val intent = Intent(context, AccountActivity::class.java)
                                        intent.action = "subject_schedule"
                                        context.startActivity(intent)
                                    }
                                )
                                ButtonBase(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 5.dp),
                                    modifierInside = Modifier.padding(vertical = 7.dp),
                                    content = { Text("Subject fee") },
                                    horizontalArrangement = Arrangement.Start,
                                    isOutlinedButton = true,
                                    clicked = {
                                        val intent = Intent(context, AccountActivity::class.java)
                                        intent.action = "subject_fee"
                                        context.startActivity(intent)
                                    }
                                )
                                ButtonBase(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 5.dp),
                                    modifierInside = Modifier.padding(vertical = 7.dp),
                                    content = { Text("Account information") },
                                    horizontalArrangement = Arrangement.Start,
                                    isOutlinedButton = true,
                                    clicked = {
                                        val intent = Intent(context, AccountActivity::class.java)
                                        intent.action = "acc_info"
                                        context.startActivity(intent)
                                    }
                                )
                                ButtonBase(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 5.dp),
                                    modifierInside = Modifier.padding(vertical = 7.dp),
                                    content = { Text("Logout") },
                                    horizontalArrangement = Arrangement.Start,
                                    isOutlinedButton = true,
                                    clicked = {
                                        logoutDialogVisible.value = true
                                    }
                                )
                            }
                        }
                    }
                )
            }
        )
        LoginDialog(
            isVisible = loginDialogVisible.value,
            controlEnabled = loginDialogEnabled.value,
            loginClicked = { username, password, rememberLogin ->
                run {
                    CoroutineScope(Dispatchers.IO).launch {
                        getMainViewModel().accountLogin(
                            data = AccountAuth(
                                username = username,
                                password = password,
                                rememberLogin = rememberLogin
                            ),
                            before = {
                                loginDialogEnabled.value = false
                                showSnackBar(
                                    text = "Logging you in...",
                                    clearPrevious = true,
                                )
                            },
                            after = {
                                when (it) {
                                    true -> {
                                        loginDialogEnabled.value = true
                                        loginDialogVisible.value = false
                                        getMainViewModel().accountGetInformation()
                                        showSnackBar(
                                            text = "Successfully logged in!",
                                            clearPrevious = true,
                                        )
                                    }
                                    false -> {
                                        loginDialogEnabled.value = true
                                        showSnackBar(
                                            text = "Login failed! Please check your login information and try again.",
                                            clearPrevious = true,
                                        )
                                    }
                                }
                            }
                        )
                    }
                }
            },
            cancelRequested = {
                loginDialogVisible.value = false
            },
            canDismiss = false,
            dismissClicked = {
                if (loginDialogEnabled.value) {
                    loginDialogVisible.value = false
                }
            }
        )
        LogoutDialog(
            isVisible = logoutDialogVisible.value,
            canDismiss = true,
            logoutClicked = {
                run {
                    getMainViewModel().accountLogout(
                        after = {
                            logoutDialogVisible.value = false
                            showSnackBar(
                                text = "Successfully logout!",
                                clearPrevious = true,
                            )
                        }
                    )
                }
            },
            dismissClicked = {
                logoutDialogVisible.value = false
            }
        )
        BackHandler(
            enabled = loginDialogVisible.value || logoutDialogVisible.value,
            onBack = {
                if (loginDialogVisible.value) {
                    loginDialogVisible.value = false
                }
                if (logoutDialogVisible.value) {
                    logoutDialogVisible.value = false
                }
            }
        )

        val hasRun = remember { mutableStateOf(false) }
        run {
            if (!hasRun.value) {
                CoroutineScope(Dispatchers.IO).launch {
                    getMainViewModel().accountLogin(
                        after = {
                            if (it) {
                                getMainViewModel().accountGetInformation()
                            }
                        }
                    )
                }
                hasRun.value = true
            }
        }
    }
}