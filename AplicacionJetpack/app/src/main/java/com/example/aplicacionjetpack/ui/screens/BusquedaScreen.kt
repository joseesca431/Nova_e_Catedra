package com.example.aplicacionjetpack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyRow // <-- ¡Usamos LazyRow!
import androidx.compose.foundation.lazy.items // <-- ¡Necesario para LazyRow!
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.aplicacionjetpack.data.dto.ProductResponse
import com.example.aplicacionjetpack.ui.theme.OrangeAccent
import com.example.aplicacionjetpack.ui.theme.PurpleDark
import com.example.aplicacionjetpack.ui.viewmodel.SearchUiState
import com.example.aplicacionjetpack.ui.viewmodel.SearchViewModel
import com.example.aplicacionjetpack.ui.viewmodel.SortOption
import java.text.NumberFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusquedaScreen(
    navController: NavController,
    viewModel: SearchViewModel = hiltViewModel(),
    onProductClick: (ProductResponse) -> Unit
) {
    val uiState = viewModel.uiState

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buscar Productos", fontSize = 18.sp, color = PurpleDark, fontWeight = FontWeight.Medium) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, "Atrás", tint = PurpleDark)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            HomeBottomBar(
                selectedTab = "Home",
                onTabSelected = { /* No-op */ },
                navController = navController
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(paddingValues)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().background(Color.White).padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = viewModel::onSearchQueryChanged,
                    placeholder = { Text("Buscar productos...", color = Color.Gray, fontSize = 14.sp) },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurpleDark, unfocusedBorderColor = Color.LightGray,
                        focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                Button(
                    onClick = viewModel::onFilterButtonClick,
                    colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(56.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Filtros", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Icon(Icons.Default.FilterList, "Filtros", tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center).padding(top = 32.dp))
                } else if (uiState.error != null) {
                    Text(
                        uiState.error, color = Color.Red,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                } else if (uiState.searchResults.isEmpty()) {
                    val message = if (uiState.searchQuery.isNotBlank() || uiState.selectedCategories.isNotEmpty()) {
                        "No se encontraron resultados para los filtros aplicados."
                    } else {
                        "Escribe en la barra para buscar productos..."
                    }
                    Text(
                        message, color = Color.Gray,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(uiState.searchResults, key = { it.idProducto }) { product ->
                            ProductCard(product = product, onClick = { onProductClick(product) })
                        }
                    }
                }
            }
        }
    }

    if (uiState.showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = viewModel::onFilterSheetDismiss,
            sheetState = rememberModalBottomSheetState(),
            containerColor = Color.White
        ) {
            FilterSheetContent(
                uiState = uiState,
                onCategorySelected = viewModel::onCategorySelected,
                onPriceRangeChanged = viewModel::onPriceRangeChanged,
                onSortOptionSelected = viewModel::onSortOptionSelected,
                onApply = viewModel::applyFiltersFromSheet,
                onClear = viewModel::clearFilters
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterSheetContent(
    uiState: SearchUiState,
    onCategorySelected: (String) -> Unit,
    onPriceRangeChanged: (ClosedFloatingPointRange<Float>) -> Unit,
    onSortOptionSelected: (SortOption) -> Unit,
    onApply: () -> Unit,
    onClear: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Filtros de Búsqueda", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = PurpleDark)
        Spacer(Modifier.height(16.dp))

        Text("Categorías", style = MaterialTheme.typography.titleMedium, color = PurpleDark)
        Spacer(Modifier.height(8.dp))

        // --- 👇👇👇 ¡REEMPLAZO DE FlowRow POR LazyRow! 👇👇👇 ---
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(uiState.allCategories) { category ->
                FilterChip(
                    selected = uiState.selectedCategories.contains(category),
                    onClick = { onCategorySelected(category) },
                    label = { Text(category) },
                    leadingIcon = {
                        if (uiState.selectedCategories.contains(category)) {
                            Icon(Icons.Default.Check, "Selected", Modifier.size(FilterChipDefaults.IconSize))
                        }
                    }
                )
            }
        }
        // --- -------------------------------------------------- ---

        Spacer(Modifier.height(24.dp))

        Text("Rango de Precio", style = MaterialTheme.typography.titleMedium, color = PurpleDark)
        RangeSlider(
            value = uiState.currentPriceValues,
            onValueChange = onPriceRangeChanged,
            valueRange = uiState.priceRange,
            steps = 100,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
            val currencyFormat = NumberFormat.getCurrencyInstance()
            Text(currencyFormat.format(uiState.currentPriceValues.start), style = MaterialTheme.typography.bodySmall)
            Text(currencyFormat.format(uiState.currentPriceValues.endInclusive), style = MaterialTheme.typography.bodySmall)
        }

        Spacer(Modifier.height(24.dp))

        Text("Ordenar por", style = MaterialTheme.typography.titleMedium, color = PurpleDark)
        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = uiState.sortBy.displayName,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                SortOption.values().forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.displayName) },
                        onClick = {
                            onSortOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedButton(onClick = onClear, modifier = Modifier.weight(1f)) {
                Text("Limpiar Filtros")
            }
            Button(onClick = onApply, modifier = Modifier.weight(1f)) {
                Text("Aplicar")
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}
