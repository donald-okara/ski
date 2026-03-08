package ke.don.gallery.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ke.don.gallery.domain.ComponentExample

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComponentDetail(component: ComponentExample, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(component.label) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Description", style = MaterialTheme.typography.titleMedium)
            Text(component.description, modifier = Modifier.padding(vertical = 8.dp))

            Text("Rendered", style = MaterialTheme.typography.titleMedium)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(Color.LightGray.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                component.rendered()
            }

            component.focusable?.let {
                Spacer(Modifier.height(12.dp))
                Text("Focusable Example", style = MaterialTheme.typography.titleMedium)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(Color.LightGray.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    it()
                }
            }

            Spacer(Modifier.height(12.dp))
            Text("Dos", style = MaterialTheme.typography.titleMedium)
            component.dos.forEach { doItem ->
                Text("• $doItem", modifier = Modifier.padding(start = 8.dp, top = 4.dp))
            }

            Spacer(Modifier.height(8.dp))
            Text("Don'ts", style = MaterialTheme.typography.titleMedium)
            component.donts.forEach { dontItem ->
                Text("• $dontItem", modifier = Modifier.padding(start = 8.dp, top = 4.dp))
            }
        }
    }
}