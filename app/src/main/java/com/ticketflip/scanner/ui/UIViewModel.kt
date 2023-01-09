package com.ticketflip.scanner.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UIViewModel : ViewModel() {

    private val _bottomNavIndex = MutableStateFlow(0)
    val bottomNavIndex = _bottomNavIndex.asStateFlow()

    private val _sharedFlow = MutableSharedFlow<UIEvents>()
    val sharedFlow = _sharedFlow.asSharedFlow()


    fun navigate(route: String) {
        viewModelScope.launch {
            _sharedFlow.emit(UIEvents.Navigate(route))
        }
    }

    fun goBack(activated: Boolean) {
        viewModelScope.launch {
            _sharedFlow.emit(UIEvents.GoBack(activated))
        }
    }


    fun showSnackbar(message: String) {
        viewModelScope.launch {
            _sharedFlow.emit(UIEvents.ShowSnackbar(message))
        }
    }

    fun ShowToast(message: String) {
        viewModelScope.launch {
            _sharedFlow.emit(UIEvents.ShowToast(message))
        }
    }


    fun clickBottomNavItem(index: Int) {
        viewModelScope.launch {
            _bottomNavIndex.value = UIEvents.ClickBottomNavItem(index).index

        }
    }

    sealed class UIEvents {
        data class ShowSnackbar(val message: String) : UIEvents()
        data class ShowToast(val message: String) : UIEvents()
        data class Navigate(val route: String) : UIEvents()
        data class GoBack(val activated: Boolean) : UIEvents()
        data class ClickBottomNavItem(val index: Int) : UIEvents()
    }
}