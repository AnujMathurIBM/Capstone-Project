package com.bookworm.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.bookworm.app.data.remote.dto.ProductSummaryDto
import com.bookworm.app.ui.theme.*

/**
 * Reusable book product card used in Landing, Catalog, and Related Reads.
 */
@Composable
fun ProductCard(
    product: ProductSummaryDto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(160.dp)
            .clickable(onClick = onClick)
            .background(CardDark, RoundedCornerShape(8.dp))
            .padding(10.dp)
    ) {
        // Cover image
        AsyncImage(
            model = product.coverImageUrl,
            contentDescription = product.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(SurfaceDark)
        )

        Spacer(Modifier.height(8.dp))

        // Title
        Text(
            text = product.title,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        // Author
        Text(
            text = "by ${product.authorName}",
            fontSize = 11.sp,
            color = TextLink,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(Modifier.height(4.dp))

        // Format
        Text(
            text = product.format.lowercase().replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted
        )

        // Tags
        if (product.tags.isNotEmpty()) {
            Text(
                text = product.tags.take(2).joinToString(", "),
                fontSize = 11.sp,
                color = AccentBlue,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(Modifier.height(4.dp))

        // Price
        Text(
            text = "₹${product.salePrice ?: product.price}",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = TextPrimary
        )

        // Delivery date
        product.deliveryDate?.let { date ->
            Text(
                text = "Delivery by $date",
                fontSize = 11.sp,
                color = TextMuted
            )
        }
    }
}
