package com.example.insertcode.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.insertcode.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenMediana() {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "Mi App Kotlin (Mediana)") })
        }
    ) { innerPadding ->
        Row(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo App",
                modifier = Modifier
                    .weight(1f)
                    .height(200.dp),
                contentScale = ContentScale.Fit
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "¡Bienvenido - Vista Mediana!",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.headlineSmall
                )
                Button(onClick = { /* Acción futura */ }, modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Presióname")
                }
            }
        }
    }
}

@Preview(name = "Medium", widthDp = 700, heightDp = 800, showBackground = true)
@Composable
fun PreviewMedium() {
    HomeScreenMediana()
}