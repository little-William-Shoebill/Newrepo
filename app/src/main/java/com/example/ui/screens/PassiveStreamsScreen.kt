package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.IncomeStreamEntity
import com.example.data.model.StreamCategory
import com.example.ui.components.CategoryBadge
import com.example.ui.components.PlatformBadge
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VaultBorder
import com.example.ui.theme.VaultCardDark
import com.example.ui.theme.VaultCardElevated
import com.example.ui.theme.VaultDarkNavy
import java.text.NumberFormat
import java.util.Locale

@Composable
fun PassiveStreamsScreen(
  streams: List<IncomeStreamEntity>,
  onHarvestStream: (IncomeStreamEntity) -> Unit,
  onToggleActive: (IncomeStreamEntity) -> Unit,
  onDeleteStream: (IncomeStreamEntity) -> Unit,
  onOpenAddStream: () -> Unit,
  modifier: Modifier = Modifier
) {
  val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)
  var selectedCategoryFilter by remember { mutableStateOf<StreamCategory?>(null) }

  val filteredStreams = remember(streams, selectedCategoryFilter) {
    if (selectedCategoryFilter == null) streams
    else streams.filter { it.category == selectedCategoryFilter }
  }

  val activeStreams = streams.filter { it.isActive }
  val totalDaily = activeStreams.sumOf { it.dailyYieldUsd }
  val totalMonthly = activeStreams.sumOf { it.monthlyYieldUsd }

  Box(modifier = modifier.fillMaxSize().background(VaultDarkNavy)) {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // 1. Header Summary Card
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(VaultCardDark)
            .border(1.dp, VaultBorder, RoundedCornerShape(20.dp))
            .padding(18.dp)
        ) {
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "PASSIVE INCOME STREAMS",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 1.1.sp
                ),
                color = CyanNeon
              )
              Surface(
                color = EmeraldNeon.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp)
              ) {
                Text(
                  text = "${activeStreams.size} Active",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = EmeraldNeon,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column {
                Text(
                  text = "Total Daily Flow",
                  style = MaterialTheme.typography.bodySmall,
                  color = TextSecondary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = "+${currencyFormat.format(totalDaily)}",
                  style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                  color = EmeraldNeon
                )
              }

              Column {
                Text(
                  text = "Total Monthly Flow",
                  style = MaterialTheme.typography.bodySmall,
                  color = TextSecondary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = "+${currencyFormat.format(totalMonthly)}",
                  style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                  color = CyanNeon
                )
              }

              Column {
                Text(
                  text = "Total Harvested",
                  style = MaterialTheme.typography.bodySmall,
                  color = TextSecondary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = currencyFormat.format(streams.sumOf { it.totalEarnedUsd }),
                  style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                  color = TextPrimary
                )
              }
            }
          }
        }
      }

      // 2. Category Filter Pills
      item {
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          contentPadding = PaddingValues(horizontal = 2.dp)
        ) {
          item {
            FilterChip(
              selected = selectedCategoryFilter == null,
              onClick = { selectedCategoryFilter = null },
              label = { Text("All (${streams.size})") },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = EmeraldNeon,
                selectedLabelColor = Color(0xFF022C22),
                containerColor = VaultCardDark,
                labelColor = TextSecondary
              )
            )
          }

          items(StreamCategory.values()) { category ->
            FilterChip(
              selected = selectedCategoryFilter == category,
              onClick = { selectedCategoryFilter = category },
              label = { Text(category.displayName.take(16)) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = Color(category.badgeColorHex),
                selectedLabelColor = Color.White,
                containerColor = VaultCardDark,
                labelColor = TextSecondary
              )
            )
          }
        }
      }

      // 3. Streams list
      if (filteredStreams.isEmpty()) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(16.dp))
              .background(VaultCardDark)
              .padding(32.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "No streams found in this category.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
              )
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "Tap the + button below to create one!",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
              )
            }
          }
        }
      } else {
        items(filteredStreams, key = { it.id }) { stream ->
          StreamCardItem(
            stream = stream,
            onHarvest = { onHarvestStream(stream) },
            onToggleActive = { onToggleActive(stream) },
            onDelete = { onDeleteStream(stream) }
          )
        }
      }

      // Extra space for FAB
      item {
        Spacer(modifier = Modifier.height(64.dp))
      }
    }

    // Floating Action Button to Add Stream
    FloatingActionButton(
      onClick = onOpenAddStream,
      containerColor = EmeraldNeon,
      contentColor = Color(0xFF022C22),
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(20.dp)
        .testTag("add_stream_fab")
    ) {
      Icon(
        imageVector = Icons.Default.Add,
        contentDescription = "Add New Passive Stream"
      )
    }
  }
}

@Composable
fun StreamCardItem(
  stream: IncomeStreamEntity,
  onHarvest: () -> Unit,
  onToggleActive: () -> Unit,
  onDelete: () -> Unit,
  modifier: Modifier = Modifier
) {
  val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)

  Surface(
    shape = RoundedCornerShape(18.dp),
    color = VaultCardDark,
    modifier = modifier
      .fillMaxWidth()
      .border(1.dp, VaultBorder, RoundedCornerShape(18.dp))
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = stream.name,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = if (stream.isActive) TextPrimary else TextMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Spacer(modifier = Modifier.height(4.dp))
          Row(verticalAlignment = Alignment.CenterVertically) {
            CategoryBadge(category = stream.category)
            Spacer(modifier = Modifier.width(6.dp))
            PlatformBadge(platform = stream.targetPlatform)
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Switch(
            checked = stream.isActive,
            onCheckedChange = { onToggleActive() },
            colors = SwitchDefaults.colors(
              checkedThumbColor = EmeraldNeon,
              checkedTrackColor = EmeraldDark,
              uncheckedThumbColor = TextMuted,
              uncheckedTrackColor = VaultCardElevated
            ),
            modifier = Modifier.testTag("stream_toggle_${stream.id}")
          )
          IconButton(onClick = onDelete) {
            Icon(
              imageVector = Icons.Default.DeleteOutline,
              contentDescription = "Delete stream",
              tint = TextMuted,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Performance stats
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(VaultCardElevated)
          .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column {
          Text(
            text = "Principal / Staked",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = currencyFormat.format(stream.principalAmountUsd),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = TextPrimary
          )
        }

        Column {
          Text(
            text = "Annual Yield",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "${stream.apyPercentage}% APY",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = EmeraldNeon
          )
        }

        Column {
          Text(
            text = "Daily Rate",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "+${currencyFormat.format(stream.dailyYieldUsd)}",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = CyanNeon
          )
        }
      }

      if (stream.destinationNote.isNotEmpty()) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "Routing: ${stream.destinationNote}",
          style = MaterialTheme.typography.labelSmall,
          color = TextMuted,
          fontSize = 11.sp,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Harvest action button
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Lifetime Earned: ${currencyFormat.format(stream.totalEarnedUsd)}",
          style = MaterialTheme.typography.bodySmall,
          color = TextSecondary
        )

        Button(
          onClick = onHarvest,
          enabled = stream.isActive,
          colors = ButtonDefaults.buttonColors(
            containerColor = EmeraldNeon.copy(alpha = 0.2f),
            contentColor = EmeraldNeon
          ),
          shape = RoundedCornerShape(10.dp),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
          modifier = Modifier.testTag("harvest_stream_${stream.id}")
        ) {
          Icon(
            imageVector = Icons.Default.ElectricBolt,
            contentDescription = null,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Harvest Yield",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
          )
        }
      }
    }
  }
}
