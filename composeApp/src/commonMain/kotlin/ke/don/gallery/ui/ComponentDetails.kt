package ke.don.gallery.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.dp
import ke.don.gallery.domain.ComponentExample

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComponentDetail(component: ComponentExample, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(component.label) }, navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            })
        },
    ) { padding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
            modifier = Modifier.fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(16.dp)
        ) {
            ShowcaseContainer(component)

            Spacer(Modifier.padding(8.dp))

            DescriptionSegment(component)


            DosComponent(component, isDos = true)

            Spacer(Modifier.height(8.dp))
            DosComponent(component, isDos = false)

            component.focusable?.let {
                Spacer(Modifier.height(12.dp))
                Text("Focusable Example", style = MaterialTheme.typography.titleMedium)
                Box(
                    modifier = Modifier.fillMaxWidth().height(80.dp)
                        .background(Color.LightGray.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    it()
                }
            }

        }
    }
}

@Composable
private fun DescriptionSegment(component: ComponentExample) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
    ){
        Text("Description", style = MaterialTheme.typography.headlineMedium)
        Text(component.description, modifier = Modifier.padding(vertical = 8.dp))
    }
}

@Composable
private fun DosComponent(
    component: ComponentExample,
    isDos: Boolean,
    hueColor: Color = if (isDos) Color.Green else MaterialTheme.colorScheme.error,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = hueColor.copy(alpha = 0.1f).compositeOver(
            MaterialTheme.colorScheme.surfaceVariant
        ),
        border = BorderStroke(
            width = 1.dp, color = hueColor.copy(alpha = 0.4f).compositeOver(
                MaterialTheme.colorScheme.outline
            )
        )
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy( 8.dp, Alignment.Top ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
            ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
                verticalAlignment = Alignment.CenterVertically
            ){
                Icon(
                    imageVector = if (isDos) Icons.Default.Check else Icons.Rounded.Close,
                    contentDescription =if (isDos)"Dos" else "Don'ts",
                    tint = hueColor
                )
                Text(
                    text = if (isDos) "Dos" else "Don'ts",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (isDos) {
                component.dos.forEach { doItem ->
                    Text("• $doItem", modifier = Modifier.padding(start = 8.dp, top = 4.dp))
                }
            } else {
                component.donts.forEach { dontItem ->
                    Text("• $dontItem", modifier = Modifier.padding(start = 8.dp, top = 4.dp))
                }
            }
        }
    }
}

@Composable
private fun ShowcaseContainer(
    component: ComponentExample, modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth().height(120.dp).background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                    )
                ), shape = RoundedCornerShape(16.dp)
            ), contentAlignment = Alignment.Center
    ) {
        component.rendered()
    }
}
