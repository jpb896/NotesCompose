/*
 * Copyright 2025 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.cahier.features.insights

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cahier.R
import com.example.cahier.core.data.Note
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// Feature flag to control visibility of topics until implemented
private const val SHOW_TOPICS_FEATURE = false

enum class InsightsTimeframe {
    WEEK, MONTH
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen(
    notes: List<Note> = emptyList(),
    modifier: Modifier = Modifier,
    onSearchClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    var timeframe by rememberSaveable { mutableStateOf(InsightsTimeframe.WEEK) }
    var calendarState by remember { mutableStateOf(Calendar.getInstance()) }

    // Dynamic Start/End of Current Week (Monday to Sunday)
    val (weekStartCal, weekEndCal) = remember(calendarState.timeInMillis) {
        val start = (calendarState.clone() as Calendar).apply {
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val end = (start.clone() as Calendar).apply {
            add(Calendar.DAY_OF_MONTH, 6)
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        Pair(start, end)
    }

    // Dynamic Header Title Calculation
    val headerText = remember(calendarState.timeInMillis, timeframe) {
        if (timeframe == InsightsTimeframe.WEEK) {
            val sameMonth = weekStartCal.get(Calendar.MONTH) == weekEndCal.get(Calendar.MONTH)
            val sameYear = weekStartCal.get(Calendar.YEAR) == weekEndCal.get(Calendar.YEAR)

            val startDay = weekStartCal.get(Calendar.DAY_OF_MONTH)
            val endDay = weekEndCal.get(Calendar.DAY_OF_MONTH)

            val startMonth = SimpleDateFormat("MMM", Locale.getDefault()).format(weekStartCal.time)
            val endMonth = SimpleDateFormat("MMM", Locale.getDefault()).format(weekEndCal.time)
            val year = SimpleDateFormat("yyyy", Locale.getDefault()).format(weekEndCal.time)

            when {
                sameMonth && sameYear -> "$startDay - $endDay $startMonth $year"
                !sameMonth && sameYear -> "$startDay $startMonth - $endDay $endMonth $year"
                else -> "$startDay $startMonth ${weekStartCal.get(Calendar.YEAR)} - $endDay $endMonth $year"
            }
        } else {
            SimpleDateFormat("MMMM", Locale.getDefault()).format(calendarState.time)
        }
    }

    // Filter notes relevant to the selected timeframe
    val filteredNotes = remember(notes, calendarState.timeInMillis, timeframe) {
        notes.filter { note ->
            if (timeframe == InsightsTimeframe.WEEK) {
                note.dateCreated in weekStartCal.timeInMillis..weekEndCal.timeInMillis
            } else {
                val cal = Calendar.getInstance().apply { timeInMillis = note.dateCreated }
                cal.get(Calendar.MONTH) == calendarState.get(Calendar.MONTH) &&
                        cal.get(Calendar.YEAR) == calendarState.get(Calendar.YEAR)
            }
        }
    }

    // Dynamic Stats Calculations for the active timeframe
    val totalEntries = filteredNotes.size

    val (mostActiveTime, writingStreak) = remember(filteredNotes, calendarState.timeInMillis, timeframe) {
        if (filteredNotes.isEmpty()) {
            Pair("-", 0)
        } else {
            val hourCounts = mutableMapOf<Int, Int>()
            val uniqueDays = mutableSetOf<Int>()

            filteredNotes.forEach { note ->
                val cal = Calendar.getInstance().apply { timeInMillis = note.dateCreated }
                val hour = cal.get(Calendar.HOUR_OF_DAY)
                hourCounts[hour] = (hourCounts[hour] ?: 0) + 1

                uniqueDays.add(cal.get(Calendar.DAY_OF_YEAR))
            }

            val peakHour = hourCounts.maxByOrNull { it.value }?.key ?: 12
            val formattedHour = if (peakHour == 0) "12 AM" else if (peakHour == 12) "12 PM" else if (peakHour > 12) "${peakHour - 12} PM" else "$peakHour AM"

            val sortedDays = uniqueDays.sorted()
            var maxStreak = 0
            var currentStreak = 0
            var previousDay = -1

            for (day in sortedDays) {
                if (previousDay == -1 || day == previousDay + 1) {
                    currentStreak++
                } else {
                    currentStreak = 1
                }
                if (currentStreak > maxStreak) {
                    maxStreak = currentStreak
                }
                previousDay = day
            }

            Pair(formattedHour, maxStreak)
        }
    }

    // Days in current selected period that contain notes (for calendar icons)
    val entryDays = remember(filteredNotes) {
        filteredNotes.map { note ->
            val cal = Calendar.getInstance().apply { timeInMillis = note.dateCreated }
            cal.get(Calendar.DAY_OF_MONTH)
        }.toSet()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Journal",
                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp),
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onSearchClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.search_24px),
                            contentDescription = "Search"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onProfileClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.account_circle_24px),
                            contentDescription = "Profile",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Timeframe Segmented Switcher
            item {
                TimeframeSegmentedControl(
                    selectedTimeframe = timeframe,
                    onTimeframeSelected = { timeframe = it }
                )
            }

            // Month / Week Navigation Header
            item {
                MonthNavigationHeader(
                    displayText = headerText,
                    onPreviousClick = {
                        calendarState = (calendarState.clone() as Calendar).apply {
                            if (timeframe == InsightsTimeframe.WEEK) {
                                add(Calendar.WEEK_OF_YEAR, -1)
                            } else {
                                add(Calendar.MONTH, -1)
                            }
                        }
                    },
                    onNextClick = {
                        calendarState = (calendarState.clone() as Calendar).apply {
                            if (timeframe == InsightsTimeframe.WEEK) {
                                add(Calendar.WEEK_OF_YEAR, 1)
                            } else {
                                add(Calendar.MONTH, 1)
                            }
                        }
                    },
                    onCalendarClick = {
                        calendarState = Calendar.getInstance()
                    }
                )
            }

            // Main Stat Summary Card
            item {
                JournalStatsCard(
                    mostActiveTime = mostActiveTime,
                    totalEntries = totalEntries,
                    writingStreakDays = writingStreak,
                    topTopic = if (SHOW_TOPICS_FEATURE) "Travel" else null
                )
            }

            // Your Moods / Calendar Card
            item {
                YourMoodsCard(
                    timeframe = timeframe,
                    entryDays = entryDays,
                    weekStartCal = weekStartCal,
                    calendarState = calendarState
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun TimeframeSegmentedControl(
    selectedTimeframe: InsightsTimeframe,
    onTimeframeSelected: (InsightsTimeframe) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val activeContainerColor = MaterialTheme.colorScheme.primaryContainer
        val activeContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        val inactiveContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.4f)
        val inactiveContentColor = MaterialTheme.colorScheme.onSurfaceVariant

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (selectedTimeframe == InsightsTimeframe.WEEK) activeContainerColor else inactiveContainerColor,
            contentColor = if (selectedTimeframe == InsightsTimeframe.WEEK) activeContentColor else inactiveContentColor,
            modifier = Modifier.clickable { onTimeframeSelected(InsightsTimeframe.WEEK) }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.view_week_24px),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Week",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (selectedTimeframe == InsightsTimeframe.MONTH) activeContainerColor else inactiveContainerColor,
            contentColor = if (selectedTimeframe == InsightsTimeframe.MONTH) activeContentColor else inactiveContentColor,
            modifier = Modifier.clickable { onTimeframeSelected(InsightsTimeframe.MONTH) }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.calendar_view_month_24px),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Month",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun MonthNavigationHeader(
    displayText: String,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    onCalendarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier
                .size(28.dp)
                .clickable { onPreviousClick() }
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(id = R.drawable.chevron_left_24px),
                    contentDescription = "Previous",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(20.dp))

        Text(
            text = displayText,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.width(20.dp))

        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier
                .size(28.dp)
                .clickable { onNextClick() }
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(id = R.drawable.chevron_right_24px),
                    contentDescription = "Next",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        IconButton(onClick = onCalendarClick, modifier = Modifier.size(28.dp)) {
            Icon(
                painter = painterResource(id = R.drawable.calendar_month_24px),
                contentDescription = "Reset to Today",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
private fun JournalStatsCard(
    mostActiveTime: String,
    totalEntries: Int,
    writingStreakDays: Int,
    topTopic: String?,
    modifier: Modifier = Modifier
) {
    val streakText = if (writingStreakDays == 1) "1 day" else "$writingStreakDays days"

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StatColumn(value = mostActiveTime, label = "Most active\ntime")
                StatColumn(value = totalEntries.toString(), label = "Total entries")
                StatColumn(value = streakText, label = "Writing\nstreak")
            }

            if (topTopic != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                painter = painterResource(id = R.drawable.explore_24px),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = topTopic,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Top journal topic",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatColumn(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 16.sp
        )
    }
}

@Composable
private fun YourMoodsCard(
    timeframe: InsightsTimeframe,
    entryDays: Set<Int>,
    weekStartCal: Calendar,
    calendarState: Calendar,
    modifier: Modifier = Modifier
) {
    val todayCalendar = remember { Calendar.getInstance() }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Your statistics",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            val daysOfWeek = listOf("M", "T", "W", "T", "F", "S", "S")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                daysOfWeek.forEach { day ->
                    Text(
                        text = day,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.width(36.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (timeframe == InsightsTimeframe.WEEK) {
                val weekCalendars = (0..6).map { dayOffset ->
                    (weekStartCal.clone() as Calendar).apply {
                        add(Calendar.DAY_OF_MONTH, dayOffset)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    weekCalendars.forEach { cellCal ->
                        val date = cellCal.get(Calendar.DAY_OF_MONTH)
                        val isToday = cellCal.get(Calendar.DAY_OF_MONTH) == todayCalendar.get(Calendar.DAY_OF_MONTH) &&
                                cellCal.get(Calendar.MONTH) == todayCalendar.get(Calendar.MONTH) &&
                                cellCal.get(Calendar.YEAR) == todayCalendar.get(Calendar.YEAR)

                        CalendarDayCell(
                            date = date,
                            isCurrentDay = isToday,
                            hasJournalEntry = entryDays.contains(date)
                        )
                    }
                }
            } else {
                val monthCal = (calendarState.clone() as Calendar).apply {
                    set(Calendar.DAY_OF_MONTH, 1)
                }
                val daysInMonth = monthCal.getActualMaximum(Calendar.DAY_OF_MONTH)

                val dayOfWeek = monthCal.get(Calendar.DAY_OF_WEEK)
                val startOffset = if (dayOfWeek == Calendar.SUNDAY) 6 else dayOfWeek - 2

                val monthGrid = remember(calendarState.timeInMillis) {
                    val grid = mutableListOf<List<Int?>>()
                    var currentDayCounter = 1
                    var currentWeek = mutableListOf<Int?>()

                    repeat(startOffset) {
                        currentWeek.add(null)
                    }

                    while (currentDayCounter <= daysInMonth) {
                        currentWeek.add(currentDayCounter)
                        currentDayCounter++

                        if (currentWeek.size == 7) {
                            grid.add(currentWeek)
                            currentWeek = mutableListOf()
                        }
                    }

                    if (currentWeek.isNotEmpty()) {
                        while (currentWeek.size < 7) {
                            currentWeek.add(null)
                        }
                        grid.add(currentWeek)
                    }

                    grid
                }

                val isViewingCurrentMonth = calendarState.get(Calendar.MONTH) == todayCalendar.get(Calendar.MONTH) &&
                        calendarState.get(Calendar.YEAR) == todayCalendar.get(Calendar.YEAR)

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    monthGrid.forEach { week ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            week.forEach { date ->
                                if (date != null) {
                                    val isToday = isViewingCurrentMonth && date == todayCalendar.get(Calendar.DAY_OF_MONTH)

                                    CalendarDayCell(
                                        date = date,
                                        isCurrentDay = isToday,
                                        hasJournalEntry = entryDays.contains(date)
                                    )
                                } else {
                                    Spacer(modifier = Modifier.width(36.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarDayCell(
    date: Int,
    isCurrentDay: Boolean,
    hasJournalEntry: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(36.dp)
            .then(
                if (isCurrentDay) {
                    Modifier.background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape
                    )
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        if (hasJournalEntry) {
            Icon(
                painter = painterResource(id = R.drawable.menu_book_24px),
                contentDescription = "Journal Entry",
                tint = Color(0xFFFFC107),
                modifier = Modifier.size(22.dp)
            )
        } else {
            Text(
                text = date.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isCurrentDay) FontWeight.Bold else FontWeight.Normal,
                color = if (isCurrentDay) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}