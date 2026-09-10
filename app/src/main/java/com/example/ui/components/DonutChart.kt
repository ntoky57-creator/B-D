package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.MoraStrings
import kotlin.math.atan2

data class DonutSlice(
    val categoryId: Long,
    val name: String,
    val icon: String,
    val amount: Double,
    val color: Color,
    val percentage: Float
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DonutChart(
    slices: List<DonutSlice>,
    totalAmount: Double,
    strings: MoraStrings,
    modifier: Modifier = Modifier
) {
    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(slices) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(1f, animationSpec = tween(durationMillis = 650))
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("donut_chart_section"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(230.dp)
                .padding(8.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .size(210.dp)
                    .pointerInput(slices) {
                        detectTapGestures { tapOffset ->
                            if (slices.isEmpty()) return@detectTapGestures
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val dx = tapOffset.x - center.x
                            val dy = tapOffset.y - center.y
                            val dist = kotlin.math.sqrt(dx * dx + dy * dy)
                            val innerR = (size.width / 2f) * 0.55f
                            val outerR = (size.width / 2f) * 1.05f

                            if (dist in innerR..outerR) {
                                var angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
                                if (angle < 0) angle += 360f
                                // canvas startAngle is -90f
                                val normalizedAngle = (angle + 90f) % 360f

                                var currentAngle = 0f
                                var hitIndex: Int? = null
                                for (i in slices.indices) {
                                    val sweep = (slices[i].percentage / 100f) * 360f
                                    if (normalizedAngle >= currentAngle && normalizedAngle < currentAngle + sweep) {
                                        hitIndex = i
                                        break
                                    }
                                    currentAngle += sweep
                                }
                                selectedIndex = if (selectedIndex == hitIndex) null else hitIndex
                            } else {
                                selectedIndex = null
                            }
                        }
                    }
            ) {
                val strokeWidth = 26.dp.toPx()
                val selectedStrokeWidth = 32.dp.toPx()
                val diameter = size.minDimension - selectedStrokeWidth
                val topLeft = Offset(
                    (size.width - diameter) / 2f,
                    (size.height - diameter) / 2f
                )
                val arcSize = Size(diameter, diameter)

                if (slices.isEmpty()) {
                    // Empty state circle
                    drawArc(
                        color = Color.LightGray.copy(alpha = 0.3f),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                } else {
                    var startAngle = -90f
                    val animatedSweep = animationProgress.value

                    slices.forEachIndexed { index, slice ->
                        val isSelected = selectedIndex == index
                        val sweepAngle = (slice.percentage / 100f) * 360f * animatedSweep
                        val currentStroke = if (isSelected) selectedStrokeWidth else strokeWidth

                        drawArc(
                            color = slice.color,
                            startAngle = startAngle,
                            sweepAngle = sweepAngle.coerceAtLeast(1f),
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = currentStroke, cap = StrokeCap.Butt)
                        )
                        startAngle += sweepAngle
                    }
                }
            }

            // Center Display Content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .clickable { selectedIndex = null }
            ) {
                val activeSlice = selectedIndex?.let { slices.getOrNull(it) }
                if (activeSlice != null) {
                    Text(
                        text = "${activeSlice.icon} ${activeSlice.name}",
                        style = MaterialTheme.typography.labelMedium,
                        color = activeSlice.color,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = MoraStrings.formatAriary(activeSlice.amount),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "${"%.1f".format(activeSlice.percentage)}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(
                        text = strings.totalSpent,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = MoraStrings.formatAriary(totalAmount),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    if (slices.isNotEmpty()) {
                        Text(
                            text = "${slices.size} ${strings.allCategories.lowercase()}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Legend chips
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            slices.forEachIndexed { index, slice ->
                val isSelected = selectedIndex == index
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) slice.color.copy(alpha = 0.22f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (isSelected) 1.5.dp else 0.5.dp,
                        color = if (isSelected) slice.color else Color.Transparent
                    ),
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .clickable {
                            selectedIndex = if (selectedIndex == index) null else index
                        }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = slice.color,
                            modifier = Modifier.size(8.dp)
                        ) {}
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "${slice.icon} ${slice.name}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${"%.0f".format(slice.percentage)}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}
