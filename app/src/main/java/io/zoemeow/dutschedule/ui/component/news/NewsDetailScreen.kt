package io.zoemeow.dutschedule.ui.component.news

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import io.dutwrapper.dutwrapper.News
import io.dutwrapper.dutwrapper.News.NewsItem
import io.zoemeow.dutschedule.R
import io.zoemeow.dutschedule.utils.CustomDateUtil

@Composable
fun NewsDetailScreen(
    context: Context,
    newsItem: NewsItem,
    newsType: News.NewsType,
    padding: PaddingValues = PaddingValues(0.dp),
    linkClicked: ((String) -> Unit)? = null
) {
    when (newsType) {
        News.NewsType.Global -> {
            NewsDetailBody_NewsGlobal(
                context = context,
                padding = padding,
                newsItem = newsItem,
                linkClicked = linkClicked
            )
        }
        News.NewsType.Subject -> {
            NewsDetailBody_NewsSubject(
                context = context,
                padding = padding,
                newsItem = newsItem as News.NewsSubjectItem,
                linkClicked = linkClicked
            )
        }
    }
}

@Composable
private fun NewsDetailBody_NewsGlobal(
    context: Context,
    padding: PaddingValues,
    newsItem: NewsItem,
    linkClicked: ((String) -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .wrapContentSize()
            .padding(padding)
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.padding(10.dp)
        ) {
            Text(
                text = newsItem.title,
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = "⏱ ${
                    CustomDateUtil.dateUnixToString(
                        newsItem.date,
                        "dd/MM/yyyy",
                        "UTC"
                    )
                } (${CustomDateUtil.unixToDurationWithLocale(
                    context = context,
                    unix = newsItem.date
                )})",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 7.dp)
            )
            HorizontalDivider(
                thickness = 1.dp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            val annotatedString = buildAnnotatedString {
                if (newsItem.content != null) {
                    // Parse all string to annotated string.
                    append(newsItem.content)
                    // Adjust color for annotated string to follow system mode.
                    addStyle(
                        style = SpanStyle(color = MaterialTheme.colorScheme.inverseSurface),
                        start = 0,
                        end = newsItem.content.length
                    )
                    // Adjust for detected link.
                    newsItem.resources?.forEach {
                        addStringAnnotation(
                            tag = it.position!!.toString(),
                            annotation = it.content!!,
                            start = it.position,
                            end = it.position + it.text!!.length
                        )
                        addStyle(
                            style = SpanStyle(color = Color(0xff64B5F6)),
                            start = it.position,
                            end = it.position + it.text.length
                        )
                    }
                }
            }
            SelectionContainer {
                ClickableText(
                    text = annotatedString,
                    style = MaterialTheme.typography.bodyLarge,
                    onClick = {
                        try {
                            newsItem.resources?.forEach { item ->
                                annotatedString
                                    .getStringAnnotations(item.position!!.toString(), it, it)
                                    .firstOrNull()
                                    ?.let { url ->
                                        var urlTemp = url.item
                                        urlTemp =
                                            urlTemp.replace(
                                                "http://",
                                                "http://",
                                                ignoreCase = true
                                            )
                                        urlTemp = urlTemp.replace(
                                            "https://",
                                            "https://",
                                            ignoreCase = true
                                        )
                                        linkClicked?.let { it(urlTemp) }
                                    }
                            }
                        } catch (_: Exception) {
                            // TODO: Exception for can't open link here!
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun NewsDetailBody_NewsSubject(
    context: Context,
    padding: PaddingValues,
    newsItem: News.NewsSubjectItem,
    linkClicked: ((String) -> Unit)? = null
) {
    val optionsScrollState = rememberScrollState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(optionsScrollState)
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.padding(10.dp)
        ) {
            Text(
                text = context.getString(
                    R.string.news_detail_newssubject_subjecttitle,
                    newsItem.lecturerName
                ),
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "⏱ ${
                    CustomDateUtil.dateUnixToString(
                        newsItem.date,
                        "dd/MM/yyyy",
                        "UTC"
                    )
                } (${CustomDateUtil.unixToDurationWithLocale(
                    context = context,
                    unix = newsItem.date
                )})",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 7.dp)
            )
            HorizontalDivider(
                thickness = 1.dp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            // Affecting classrooms.
            var affectedClassrooms = ""
            newsItem.affectedClass.forEach { className ->
                if (affectedClassrooms.isEmpty()) {
                    affectedClassrooms = "\n- ${className.subjectName}"
                } else {
                    affectedClassrooms += "\n- ${className.subjectName}"
                }
                var first = true
                for (item in className.codeList) {
                    if (first) {
                        affectedClassrooms += " ["
                        first = false
                    } else {
                        affectedClassrooms += ", "
                    }
                    affectedClassrooms += "${item.studentYearId.lowercase()}.${item.classId.uppercase()}"
                }
                affectedClassrooms += "]"
            }
            Text(
                text = context.getString(
                    R.string.news_detail_newssubject_subjectaffected,
                    affectedClassrooms
                ),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            HorizontalDivider(
                thickness = 1.dp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            // Affecting lessons, hour, room.
            if (arrayListOf(
                    News.LessonStatus.Leaving,
                    News.LessonStatus.MakeUpLesson
                ).contains(newsItem.lessonStatus)
            ) {
                Text(
                    text = context.getString(
                        R.string.news_detail_newssubject_subjectstatus,
                        when (newsItem.lessonStatus) {
                            News.LessonStatus.Leaving -> context.getString(R.string.news_detail_newssubject_subjectstatus_leaving)
                            News.LessonStatus.MakeUpLesson -> context.getString(R.string.news_detail_newssubject_subjectstatus_makeup)
                            else -> context.getString(R.string.news_detail_newssubject_subjectstatus_unknown)
                        }
                    ),
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.size(5.dp))
                Text(
                    text = context.getString(
                        R.string.news_detail_newssubject_subjectlesson,
                        newsItem.affectedLesson
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Spacer(modifier = Modifier.size(5.dp))
                Text(
                    text = context.getString(
                        R.string.news_detail_newssubject_subjectdate,
                        CustomDateUtil.dateUnixToString(newsItem.affectedDate, "dd/MM/yyyy", "UTC")
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                )
                if (newsItem.lessonStatus == News.LessonStatus.MakeUpLesson) {
                    Spacer(modifier = Modifier.size(5.dp))
                    Text(
                        text = context.getString(
                            R.string.news_detail_newssubject_subjectroom,
                            newsItem.affectedRoom
                        ),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
                HorizontalDivider(
                    thickness = 1.dp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 7.dp, bottom = 10.dp)
                )
            }
            val annotatedString = buildAnnotatedString {
                if (newsItem.content != null) {
                    // Parse all string to annotated string.
                    append(newsItem.content)
                    // Adjust color for annotated string to follow system mode.
                    addStyle(
                        style = SpanStyle(color = MaterialTheme.colorScheme.inverseSurface),
                        start = 0,
                        end = newsItem.content.length
                    )
                    // Adjust for detected link.
                    newsItem.resources?.forEach {
                        addStringAnnotation(
                            tag = it.position!!.toString(),
                            annotation = it.content!!,
                            start = it.position,
                            end = it.position + it.text!!.length
                        )
                        addStyle(
                            style = SpanStyle(color = Color(0xff64B5F6)),
                            start = it.position,
                            end = it.position + it.text.length
                        )
                    }
                }
            }
            SelectionContainer {
                ClickableText(
                    text = annotatedString,
                    style = MaterialTheme.typography.bodyLarge,
                    onClick = {
                        try {
                            newsItem.resources?.forEach { item ->
                                annotatedString
                                    .getStringAnnotations(
                                        item.position!!.toString(),
                                        it,
                                        it
                                    )
                                    .firstOrNull()
                                    ?.let { url ->
                                        var urlTemp = url.toString()
                                        urlTemp =
                                            urlTemp.replace(
                                                "http://",
                                                "http://",
                                                ignoreCase = true
                                            )
                                        urlTemp = urlTemp.replace(
                                            "https://",
                                            "https://",
                                            ignoreCase = true
                                        )
                                        linkClicked?.let { it(urlTemp) }
                                    }
                            }
                        } catch (_: Exception) {
                            // TODO: Exception for can't open link here!
                        }
                    }
                )
            }
        }
    }
}