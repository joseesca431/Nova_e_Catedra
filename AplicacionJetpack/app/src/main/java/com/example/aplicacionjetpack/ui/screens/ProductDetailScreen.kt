package com.example.aplicacionjetpack.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.aplicacionjetpack.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(navController: NavController) {
    var quantity by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalle del producto",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Regresar",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Imagen del producto
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_producto),
                    contentDescription = "Producto",
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp))
                )
            }

            // Información del producto
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(24.dp)
            ) {
                // Nombre del producto
                Text(
                    text = "Nombre del producto",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Precio
                Text(
                    text = "$0.00",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = PurpleDark,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Título descripción
                Text(
                    text = "DESCRIPCIÓN",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Texto descripción
                Text(
                    text = "Breve descripción del producto, mostrando sus características y cualquier detalle más",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Controles de cantidad
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Botón disminuir
                    Button(
                        onClick = {
                            if (quantity > 0) quantity--
                        },
                        modifier = Modifier.size(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = OrangeAccent
                        ),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "−",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Botón aumentar
                    Button(
                        onClick = { quantity++ },
                        modifier = Modifier.size(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = OrangeAccent
                        ),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "+",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(24.dp))

                    // Cantidad
                    Text(
                        text = quantity.toString(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                // Botón AGREGAR
                Button(
                    onClick = {
                        // Navegar al carrito
                        navController.navigate("cart")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PurpleDark
                    ),
                    shape = RoundedCornerShape(8.dp),
                    enabled = quantity > 0 // Solo habilitar si hay cantidad
                ) {
                    Text(
                        text = "AGREGAR",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}