package com.example.notasymedia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notasymedia.R
import com.example.notasymedia.ui.theme.NotasYMediaTheme
import com.example.notasymedia.viewmodel.NotaViewModel

/**
 * Estado para el panel de detalle.
 */
sealed class DetailState {
    object Placeholder : DetailState()
    data class ItemDetail(val id: Int) : DetailState()
    object NewForm : DetailState()
    data class EditForm(val id: Int) : DetailState()
}

/**
 * Layout Master/Detail para tablets. Maneja internamente la navegación a formulario en el panel derecho.
 */
@Composable
fun MasterDetailLayout(
    modifier: Modifier = Modifier,
    onNavigateToEdit: (Int) -> Unit = {}
) {
    val viewModel: NotaViewModel = viewModel()

    var detailState by remember { mutableStateOf<DetailState>(DetailState.Placeholder) }

    Row(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.weight(0.4f).fillMaxHeight()) {
            MainScreen(
                modifier = Modifier.fillMaxSize(),
                onNavigateToForm = { id ->
                    detailState = if (id == -1) DetailState.NewForm else DetailState.EditForm(id)
                },
                onNavigateToDetail = { id ->
                    detailState = DetailState.ItemDetail(id)
                },
                viewModel = viewModel
            )
        }

        Spacer(modifier = Modifier.width(1.dp).fillMaxHeight().background(MaterialTheme.colorScheme.primary))

        Column(modifier = Modifier.weight(0.6f).fillMaxHeight()) {
            when (val current = detailState) {
                is DetailState.Placeholder -> PlaceholderDetailScreen()
                is DetailState.ItemDetail -> {
                    DetailScreen(
                        itemId = current.id,
                        onNavigateToEdit = { detailState = DetailState.EditForm(current.id) },
                        onNavigateBack = { detailState = DetailState.Placeholder },
                        viewModel = viewModel
                    )
                }
                is DetailState.NewForm, is DetailState.EditForm -> {
                    val formId = if (current is DetailState.NewForm) -1 else (current as DetailState.EditForm).id
                    EntryFormScreen(
                        itemId = formId,
                        onNavigateBack = { detailState = DetailState.Placeholder },
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

/**
 * Marcador de posición para el panel de detalle cuando no hay selección.
 */
@Composable
fun PlaceholderDetailScreen() {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surfaceVariant) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                stringResource(R.string.placeholder_detail_title),
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                stringResource(R.string.placeholder_detail_subtitle)
            )
        }
    }
}

@Preview(
    widthDp = 1024,
    heightDp = 720,
    showBackground = true
)
@Composable
fun PreviewMasterDetailLayout() {
    NotasYMediaTheme {
        MasterDetailLayout()
    }
}