package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PayoutPlatform
import com.example.data.model.StreamCategory
import com.example.ui.theme.CashAppGreen
import com.example.ui.theme.CryptoBtcGold
import com.example.ui.theme.CryptoEthBlue
import com.example.ui.theme.CryptoSolGreen
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.PayPalBlue
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VaultBorder
import com.example.ui.theme.VaultCardDark
import com.example.ui.theme.VaultCardElevated
import kotlinx.coroutines.delay

@Composable
fun StatMetricCard(
  title: String,
  value: String,
  subtitle: String,
  accentColor: Color = EmeraldNeon,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(16.dp))
      .background(VaultCardDark)
      .border(1.dp, VaultBorder, RoundedCornerShape(16.dp))
      .padding(14.dp)
  ) {
    Column {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(
          text = title,
          style = MaterialTheme.typography.labelMedium,
          color = TextSecondary
        )
        Box(
          modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(accentColor)
        )
      }
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = value,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = TextPrimary,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
      Spacer(modifier = Modifier.height(4.dp))
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.TrendingUp,
          contentDescription = null,
          tint = accentColor,
          modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
          text = subtitle,
          style = MaterialTheme.typography.bodySmall,
          color = accentColor,
          fontSize = 11.sp
        )
      }
    }
  }
}

@Composable
fun PlatformBadge(
  platform: PayoutPlatform,
  modifier: Modifier = Modifier
) {
  val (bgColor, textColor, label) = when (platform) {
    PayoutPlatform.CRYPTO_BTC -> Triple(Color(0x28F7931A), CryptoBtcGold, "BTC")
    PayoutPlatform.CRYPTO_ETH -> Triple(Color(0x28627EEA), CryptoEthBlue, "ETH")
    PayoutPlatform.CRYPTO_SOL -> Triple(Color(0x2814F195), CryptoSolGreen, "SOL")
    PayoutPlatform.PAYPAL -> Triple(Color(0x280070BA), PayPalBlue, "PayPal")
    PayoutPlatform.CASH_APP -> Triple(Color(0x2800D632), CashAppGreen, "Cash App")
  }

  Surface(
    color = bgColor,
    shape = RoundedCornerShape(8.dp),
    modifier = modifier
  ) {
    Text(
      text = label,
      color = textColor,
      fontWeight = FontWeight.SemiBold,
      fontSize = 11.sp,
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
    )
  }
}

@Composable
fun CategoryBadge(
  category: StreamCategory,
  modifier: Modifier = Modifier
) {
  Surface(
    color = Color(category.badgeColorHex).copy(alpha = 0.15f),
    shape = RoundedCornerShape(8.dp),
    modifier = modifier
  ) {
    Text(
      text = category.displayName,
      color = Color(category.badgeColorHex),
      fontWeight = FontWeight.Medium,
      fontSize = 11.sp,
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
    )
  }
}

@Composable
fun YieldGrowthCanvasChart(
  dataPoints: List<Float>,
  lineColor: Color = EmeraldNeon,
  modifier: Modifier = Modifier
) {
  Canvas(modifier = modifier) {
    if (dataPoints.size < 2) return@Canvas

    val width = size.width
    val height = size.height
    val minVal = dataPoints.minOrNull() ?: 0f
    val maxVal = dataPoints.maxOrNull() ?: 1f
    val range = if (maxVal - minVal == 0f) 1f else maxVal - minVal

    val path = Path()
    val fillPath = Path()

    val stepX = width / (dataPoints.size - 1)

    dataPoints.forEachIndexed { index, value ->
      val normY = 1f - ((value - minVal) / range)
      val x = index * stepX
      val y = normY * (height * 0.75f) + (height * 0.12f)

      if (index == 0) {
        path.moveTo(x, y)
        fillPath.moveTo(x, height)
        fillPath.lineTo(x, y)
      } else {
        path.lineTo(x, y)
        fillPath.lineTo(x, y)
      }
    }

    fillPath.lineTo(width, height)
    fillPath.close()

    // Draw background gradient fill under line
    drawPath(
      path = fillPath,
      brush = Brush.verticalGradient(
        colors = listOf(lineColor.copy(alpha = 0.35f), Color.Transparent),
        startY = 0f,
        endY = height
      )
    )

    // Draw smooth line
    drawPath(
      path = path,
      color = lineColor,
      style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
    )

    // Draw point circle at end
    val lastVal = dataPoints.last()
    val lastNormY = 1f - ((lastVal - minVal) / range)
    val lastX = width
    val lastY = lastNormY * (height * 0.75f) + (height * 0.12f)
    drawCircle(color = Color.White, radius = 4.dp.toPx(), center = Offset(lastX, lastY))
    drawCircle(color = lineColor, radius = 2.dp.toPx(), center = Offset(lastX, lastY))
  }
}

@Composable
fun CopyableAddressCard(
  label: String,
  address: String,
  platform: PayoutPlatform,
  modifier: Modifier = Modifier
) {
  val clipboardManager = LocalClipboardManager.current
  var copied by remember { mutableStateOf(false) }

  LaunchedEffect(copied) {
    if (copied) {
      delay(2000)
      copied = false
    }
  }

  Surface(
    shape = RoundedCornerShape(12.dp),
    color = VaultCardElevated,
    modifier = modifier
      .fillMaxWidth()
      .border(1.dp, VaultBorder, RoundedCornerShape(12.dp))
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          PlatformBadge(platform = platform)
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
          )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = address,
          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
          color = TextPrimary,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }

      IconButton(
        onClick = {
          clipboardManager.setText(AnnotatedString(address))
          copied = true
        },
        modifier = Modifier.testTag("copy_address_btn")
      ) {
        if (copied) {
          Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Copied",
            tint = EmeraldNeon
          )
        } else {
          Icon(
            imageVector = Icons.Default.ContentCopy,
            contentDescription = "Copy to clipboard",
            tint = TextSecondary
          )
        }
      }
    }
  }
}
