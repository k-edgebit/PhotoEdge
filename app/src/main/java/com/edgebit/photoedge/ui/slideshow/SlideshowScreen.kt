package com.edgebit.photoedge.ui.slideshow

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.edgebit.photoedge.data.model.MediaType
import com.edgebit.photoedge.data.model.TransitionEffect
import com.edgebit.photoedge.ui.components.ExoPlayerView
import com.edgebit.photoedge.ui.theme.PrimaryIndigo
import java.io.File

@Composable
fun SlideshowScreen(
    onNavigateToServer: () -> Unit,
    onNavigateToClock: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: SlideshowViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                viewModel.toggleControls()
            }
    ) {
        if (uiState.mediaList.isEmpty()) {
            EmptyMediaGuide(
                onNavigateToServer = onNavigateToServer
            )
        } else {
            val currentItem = uiState.currentItem
            if (currentItem != null) {
                AnimatedContent(
                    targetState = currentItem,
                    transitionSpec = {
                        when (uiState.transitionEffect) {
                            TransitionEffect.SLIDE -> {
                                slideInHorizontally(
                                    initialOffsetX = { it },
                                    animationSpec = tween(600)
                                ) togetherWith slideOutHorizontally(
                                    targetOffsetX = { -it },
                                    animationSpec = tween(600)
                                )
                            }
                            TransitionEffect.NONE -> {
                                fadeIn(animationSpec = tween(0)) togetherWith fadeOut(animationSpec = tween(0))
                            }
                            else -> {
                                fadeIn(animationSpec = tween(800)) togetherWith fadeOut(animationSpec = tween(800))
                            }
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                    label = "SlideshowMediaTransition"
                ) { targetMedia ->
                    if (targetMedia.mediaType == MediaType.VIDEO) {
                        ExoPlayerView(
                            videoFilePath = targetMedia.filePath,
                            isMuted = uiState.isMuted,
                            onVideoEnded = { viewModel.onVideoFinished() }
                        )
                    } else {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(File(targetMedia.filePath))
                                .crossfade(true)
                                .build(),
                            contentDescription = targetMedia.fileName,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }

        // Overlay Controls
        AnimatedVisibility(
            visible = uiState.isControlsVisible,
            enter = fadeIn(tween(250)),
            exit = fadeOut(tween(250)),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f))
            ) {
                // Top Action Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 20.dp)
                        .align(Alignment.TopCenter),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PhotoEdge",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = onNavigateToClock,
                            modifier = Modifier.background(Color.White.copy(alpha = 0.15f), CircleShape)
                        ) {
                            Icon(Icons.Default.Schedule, contentDescription = "시계 모드", tint = Color.White)
                        }
                        IconButton(
                            onClick = onNavigateToServer,
                            modifier = Modifier.background(Color.White.copy(alpha = 0.15f), CircleShape)
                        ) {
                            Icon(Icons.Default.Wifi, contentDescription = "PC 업로드", tint = Color.White)
                        }
                        IconButton(
                            onClick = onNavigateToSettings,
                            modifier = Modifier.background(Color.White.copy(alpha = 0.15f), CircleShape)
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = "설정", tint = Color.White)
                        }
                    }
                }

                // Center Playback Controls (if media exists)
                if (uiState.mediaList.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(32.dp))
                            .padding(horizontal = 20.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        IconButton(onClick = { viewModel.previous() }) {
                            Icon(Icons.Default.NavigateBefore, contentDescription = "이전", tint = Color.White, modifier = Modifier.size(36.dp))
                        }

                        IconButton(onClick = { viewModel.togglePlay() }) {
                            Icon(
                                if (uiState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (uiState.isPlaying) "일시정지" else "재생",
                                tint = Color.White,
                                modifier = Modifier.size(42.dp)
                            )
                        }

                        IconButton(onClick = { viewModel.next() }) {
                            Icon(Icons.Default.NavigateNext, contentDescription = "다음", tint = Color.White, modifier = Modifier.size(36.dp))
                        }
                    }

                    // Bottom info label (e.g. 3 / 12)
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 32.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.Black.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = "${uiState.currentIndex + 1} / ${uiState.mediaList.size}",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyMediaGuide(
    onNavigateToServer: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = CircleShape,
            color = PrimaryIndigo.copy(alpha = 0.15f),
            modifier = Modifier.size(80.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.AddPhotoAlternate,
                    contentDescription = null,
                    tint = PrimaryIndigo,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "표시할 사진이 없습니다",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "같은 Wi-Fi에 연결된 PC 브라우저에서\n사진과 동영상을 전송해보세요.",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onNavigateToServer,
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Wifi, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.size(8.dp))
            Text("PC 업로드 연결 안내 보기")
        }
    }
}
