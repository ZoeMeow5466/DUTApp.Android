package io.zoemeow.dutschedule.ui.view.main.controls

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.zoemeow.dutschedule.GlobalVariables
import io.zoemeow.dutschedule.R
import io.zoemeow.dutschedule.model.NotificationHistory
import io.zoemeow.dutschedule.utils.CustomDateUtils
import io.zoemeow.dutschedule.utils.ExtensionUtils.Companion.getRandomString

@Composable
fun NotificationItem(
    context: Context,
    modifier: Modifier = Modifier,
    item: NotificationHistory,
    showDate: Boolean = false,
    isVisible: Boolean = true,
    onClick: (() -> Unit)? = null,
    onClear: (() -> Unit)? = null,
    opacity: Float = 1f
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(400)),
        exit = fadeOut(animationSpec = tween(400)),
    ) {
        Surface(
            modifier = modifier.clickable { onClick?.let { it() } },
            shape = RoundedCornerShape(GlobalVariables.ROUNDED_CORNER_SHAPE_SIZE),
            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = opacity),
            content = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    content = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp)
                                .weight(0.9f),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.Start
                        ) {
                            if (showDate) {
                                Text(
                                    CustomDateUtils.unixToDurationWithLocale(
                                        context = context,
                                        unix = item.timestamp
                                    ),
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(bottom = 5.dp)
                                )
                            }
                            Text(
                                item.title,
                                style = MaterialTheme.typography.titleMedium,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 2
                            )
                            Spacer(modifier = Modifier.size(3.dp))
                            Text(
                                item.description,
                                style = MaterialTheme.typography.bodyMedium,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 3
                            )
                        }
                        Column(
                            modifier = Modifier.weight(0.10f),
                            content = {
                                IconButton(
                                    modifier = Modifier.size(36.dp),
                                    onClick = { onClear?.let { it() } }
                                ) {
                                    Icon(Icons.Default.Clear, context.getString(R.string.action_delete))
                                }
                            }
                        )
                    }
                )
            }
        )
    }

}

@Preview
@Composable
private fun Preview1() {
    val notificationHistory = NotificationHistory(
        id = getRandomString(32),
        title = "News global",
        description = "V/v Xét giao Đồ án tốt nghiệp học kỳ 2/23-24",
        tag = 1,
        timestamp = 1708534800000,
        parameters = mapOf(),
        isRead = false
    )
    NotificationItem(
        context = LocalContext.current,
        item = notificationHistory
    )
}

@Preview
@Composable
private fun Preview2() {
    val notificationHistory = NotificationHistory(
        id = getRandomString(32),
        title = "Thầy Lê Kim Hùng thông báo đến lớp: Phương pháp luận nghiên cứu khoa học [20.Nh29]",
        description = "Chiều mai (thứ sáu, 23/2) thầy Hùng bận việc từ 16.00 nên ngày mai ta nghỉ tiết 9-10 (HP PPNCKH). Ta còn nhiều tuần để bù (báo các em biết).",
        tag = 1,
        timestamp = 1708534800000,
        parameters = mapOf(),
        isRead = false
    )
    NotificationItem(
        context = LocalContext.current,
        item = notificationHistory
    )
}