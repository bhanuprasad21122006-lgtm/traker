package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TaskCategory

data class DayCompletionData(
  val dayLabel: String,
  val completedCount: Int,
  val targetCount: Int,
  val isToday: Boolean
)

@Composable
fun ProgressRing(
  progress: Float,
  modifier: Modifier = Modifier,
  size: Dp = 100.dp,
  strokeWidth: Dp = 10.dp,
  primaryColor: Color = MaterialTheme.colorScheme.primary,
  secondaryColor: Color = MaterialTheme.colorScheme.tertiary,
  backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
  centerContent: @Composable () -> Unit = {}
) {
  val animatedProgress by animateFloatAsState(
    targetValue = progress.coerceIn(0f, 1f),
    animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
    label = "progress_anim"
  )

  Box(
    modifier = modifier.size(size),
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val strokePx = strokeWidth.toPx()
      val arcSize = Size(this.size.width - strokePx, this.size.height - strokePx)
      val topLeft = Offset(strokePx / 2f, strokePx / 2f)

      // Background track
      drawArc(
        color = backgroundColor,
        startAngle = -90f,
        sweepAngle = 360f,
        useCenter = false,
        topLeft = topLeft,
        size = arcSize,
        style = Stroke(width = strokePx, cap = StrokeCap.Round)
      )

      // Active progress arc
      if (animatedProgress > 0f) {
        drawArc(
          brush = Brush.sweepGradient(
            listOf(primaryColor, secondaryColor, primaryColor)
          ),
          startAngle = -90f,
          sweepAngle = animatedProgress * 360f,
          useCenter = false,
          topLeft = topLeft,
          size = arcSize,
          style = Stroke(width = strokePx, cap = StrokeCap.Round)
        )
      }
    }
    centerContent()
  }
}

@Composable
fun WeeklyCompletionChart(
  weeklyData: List<DayCompletionData>,
  modifier: Modifier = Modifier
) {
  val maxCount = (weeklyData.maxOfOrNull { it.completedCount } ?: 5).coerceAtLeast(4)

  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(24.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    border = androidx.compose.foundation.BorderStroke(
      width = 1.dp,
      color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(18.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Weekly Activity",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "Tasks completed over the last 7 days",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
        val totalCompletedThisWeek = weeklyData.sumOf { it.completedCount }
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
          Text(
            text = "$totalCompletedThisWeek completed",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // 7-day Bar chart row
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .height(130.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
      ) {
        weeklyData.forEach { day ->
          val barRatio = if (maxCount > 0) (day.completedCount.toFloat() / maxCount.toFloat()).coerceIn(0.08f, 1f) else 0.08f
          val animatedHeightRatio by animateFloatAsState(
            targetValue = barRatio,
            animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
            label = "bar_anim_${day.dayLabel}"
          )

          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
          ) {
            // Count label above bar
            Text(
              text = if (day.completedCount > 0) "${day.completedCount}" else "-",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Medium,
              color = if (day.isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
              fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Bar column
            Box(
              modifier = Modifier
                .width(22.dp)
                .height(84.dp)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 4.dp, bottomEnd = 4.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
              contentAlignment = Alignment.BottomCenter
            ) {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .fillMaxHeight(animatedHeightRatio)
                  .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 4.dp, bottomEnd = 4.dp))
                  .background(
                    if (day.isToday) {
                      Brush.verticalGradient(
                        listOf(
                          MaterialTheme.colorScheme.primary,
                          MaterialTheme.colorScheme.tertiary
                        )
                      )
                    } else if (day.completedCount > 0) {
                      Brush.verticalGradient(
                        listOf(
                          MaterialTheme.colorScheme.secondary,
                          MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)
                        )
                      )
                    } else {
                      Brush.verticalGradient(
                        listOf(
                          MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                          MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                        )
                      )
                    }
                  )
              )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Day label
            Box(
              modifier = if (day.isToday) {
                Modifier
                  .clip(CircleShape)
                  .background(MaterialTheme.colorScheme.primary)
                  .padding(horizontal = 4.dp, vertical = 2.dp)
              } else {
                Modifier.padding(vertical = 2.dp)
              },
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = day.dayLabel,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Normal,
                color = if (day.isToday) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                textAlign = TextAlign.Center
              )
            }
          }
        }
      }
    }
  }
}

data class CategoryProgressItem(
  val category: TaskCategory,
  val total: Int,
  val completed: Int
) {
  val progress: Float
    get() = if (total > 0) completed.toFloat() / total.toFloat() else 0f
}

@Composable
fun CategoryProgressRow(
  item: CategoryProgressItem,
  modifier: Modifier = Modifier
) {
  val animatedProgress by animateFloatAsState(
    targetValue = item.progress,
    animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
    label = "cat_progress"
  )

  Column(modifier = modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Box(
          modifier = Modifier
            .size(28.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(item.category.color.copy(alpha = 0.15f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = item.category.icon,
            contentDescription = item.category.displayName,
            tint = item.category.color,
            modifier = Modifier.size(16.dp)
          )
        }
        Text(
          text = item.category.displayName,
          style = MaterialTheme.typography.bodyMedium,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onSurface
        )
      }

      Text(
        text = "${item.completed}/${item.total} (${(item.progress * 100).toInt()}%)",
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    Spacer(modifier = Modifier.height(6.dp))

    // Progress track
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(8.dp)
        .clip(RoundedCornerShape(4.dp))
        .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth(animatedProgress.coerceIn(0.02f, 1f))
          .fillMaxHeight()
          .clip(RoundedCornerShape(4.dp))
          .background(item.category.color)
      )
    }
  }
}
