package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Cottage
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Language
import com.example.model.ProductCategory
import com.example.ui.components.ProductCard
import com.example.ui.theme.FastGreenPrimary
import com.example.ui.theme.FastOrangeAccent
import com.example.viewmodel.MarketplaceViewModel

@Composable
fun CategoriesScreen(
    viewModel: MarketplaceViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.currentLanguage.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val products by viewModel.filteredProducts.collectAsState()
    val isBn = language.isBengali()

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier
            .fillMaxSize()
            .testTag("categories_screen"),
        contentPadding = PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item(span = { GridItemSpan(2) }) {
            Column {
                Text(
                    text = if (isBn) "সকল পণ্য ক্যাটাগরি" else "Marketplace Categories",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (isBn) "আপনার প্রয়োজনীয় বিভাগ নির্বাচন করুন" else "Select a category to explore fresh collections",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Category Cards
        items(ProductCategory.values(), key = { it.name }) { category ->
            val isSelected = selectedCategory == category
            val icon = getCategoryIcon(category)
            val count = viewModel.products.value.count { category == ProductCategory.ALL || it.category == category }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.selectedCategory.value = category }
                    .testTag("category_card_${category.name}"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) FastGreenPrimary.copy(alpha = 0.12f) else Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(
                                if (isSelected) FastGreenPrimary else FastGreenPrimary.copy(alpha = 0.15f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = category.enName,
                            tint = if (isSelected) Color.White else FastGreenPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = category.localizedName(language),
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = if (isSelected) FastGreenPrimary else MaterialTheme.colorScheme.onSurface,
                            lineHeight = 16.sp
                        )
                        Text(
                            text = if (isBn) "${count}টি পণ্য" else "$count items",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        // Separator Header for Products in Selected Category
        item(span = { GridItemSpan(2) }) {
            Column(modifier = Modifier.padding(top = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${selectedCategory.localizedName(language)} (${products.size})",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = FastGreenPrimary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        // Products for this category
        items(products, key = { it.id }) { product ->
            ProductCard(
                product = product,
                language = language,
                onProductClick = { viewModel.openProductDetail(it) },
                onAddToCart = { viewModel.addToCart(it, 1) }
            )
        }
    }
}

fun getCategoryIcon(category: ProductCategory): ImageVector {
    return when (category) {
        ProductCategory.ALL -> Icons.Default.ShoppingBag
        ProductCategory.GROCERIES -> Icons.Default.ShoppingBasket
        ProductCategory.ELECTRONICS -> Icons.Default.Devices
        ProductCategory.FASHION -> Icons.Default.Checkroom
        ProductCategory.SNACKS_SWEETS -> Icons.Default.Restaurant
        ProductCategory.HEALTHCARE -> Icons.Default.HealthAndSafety
        ProductCategory.HOME_LIVING -> Icons.Default.Cottage
    }
}
