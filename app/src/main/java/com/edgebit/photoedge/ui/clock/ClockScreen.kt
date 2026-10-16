package com.edgebit.photoedge.ui.clock

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.edgebit.photoedge.ui.theme.PrimaryIndigo

@Composable
fun ClockScreen(
    onBackToSlideshow: () -> Unit,
    viewModel: ClockViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val bgColor by animateColorAsState(
        targetValue = if (uiState.isSleepModeActive) Color(0xFF050505) else Color(0xFF0F172A),
        animationSpec = tween(600),
        label = "clockBgColor"
    )

    val textColor by animateColorAsState(
        targetValue = if (uiState.isSleepModeActive) Color(0xFF475569) else Color(0xFFF8FAFC),
        animationSpec = tween(600),
        label = "clockTextColor"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        // Back Button
        IconButton(
            onClick = onBackToSlideshow,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(20.dp)
        ) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = "액자로 돌아가기",
                tint = textColor.copy(alpha = 0.7f)
            )
        }

        // Night Sleep Mode Toggle
        IconButton(
            onClick = { viewModel.toggleSleepMode() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(20.dp)
        ) {
            Icon(
                Icons.Default.Bedtime,
                contentDescription = "취침 절전 모드 토글",
                tint = if (uiState.isSleepModeActive) PrimaryIndigo else textColor.copy(alpha = 0.7f)
            )
        }

        // Clock Display
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .clickable { viewModel.toggleSleepMode() },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = uiState.currentTimeText.ifEmpty { "00:00" },
                    fontSize = if (uiState.isSleepModeActive) 72.sp else 96.sp,
                    fontWeight = FontWeight.ExtraLight,
                    color = textColor,
                    letterSpacing = (-2).sp
                )

                if (!uiState.isSleepModeActive) {
                    Text(
                        text = " ${uiState.currentSecondText}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Light,
                        color = textColor.copy(alpha = 0.5f),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${uiState.currentDateText} ${uiState.currentDayOfWeekText}",
                fontSize = if (uiState.isSleepModeActive) 14.sp else 18.sp,
                fontWeight = FontWeight.Normal,
                color = textColor.copy(alpha = 0.75f)
            )

            if (uiState.isSleepModeActive) {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.05f)
                ) {
                    Text(
                        text = "취침 모드 실행 중 (화면 탭 시 복귀)",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}
