package com.hypelist.presentation.extensions


import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.hypelist.architecture.BaseViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
inline fun <reified T : BaseViewModel> createViewModel(): T {
    val viewModel: T = koinViewModel()
    viewModel.initialize()
    return viewModel
}

@Composable
inline fun <reified T> BaseViewModel.collectState(): androidx.compose.runtime.State<T?> {
    return state.collectAsState() as androidx.compose.runtime.State<T?>
}