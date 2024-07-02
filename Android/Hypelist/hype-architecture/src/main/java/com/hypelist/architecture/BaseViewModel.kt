package com.hypelist.architecture

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {

    /**
     * Holds the latest UI rendered state.
     */
    private val _state by lazy { MutableStateFlow<State?>(null) }
    val state: StateFlow<State?> get() = _state

    /**
     * Flow that will emit commands to the view.
     * A command is an action that ViewModel will send only once for the attached view.
     *
     * Example of commands:
     * - Open this dialog.
     * - Navigate to this screen.
     * - Navigate back from this screen.
     */
    private val _command by lazy { MutableSharedFlow<ViewCommand>() }
    val command: SharedFlow<ViewCommand> get() = _command

    /**
     * Control the view model initialization flow and avoid it to be called more than once.
     */
    private var isInitialized = false

    /**
     * Called by view to send 'an interaction' to the view model by using the view model scope.
     */
    open fun processAction(userAction: UserAction) = viewModelScope.launch {
        handleUserAction(userAction)
    }

    protected abstract suspend fun handleUserAction(userAction: UserAction)

    /**
     * Called by view to send a back state result to the view model.
     */
    @VisibleForTesting
    open fun handleBackStateResult(result: BackStateResult) = Unit

    /**
     * Override this function to initialize your component flow.
     * It will be called only once when the view model is created.
     *
     * It basically overrides the kotlin init {} function. But do we need this?
     * Using this instead of the init {} function makes it possible to mock things easier on our tests.
     */
    protected open suspend fun initializeComponents() = Unit

    /**
     * Call this function on `onCreate` of your Activity or Fragment to initialize the view model.
     */
    fun initialize() {
        if (isInitialized) {
            return
        }

        viewModelScope.launch {
            initializeComponents()
        }

        isInitialized = true
    }

    /**
     * Used to safely dispatch a new [State] for the client.
     */
    protected fun dispatchState(state: State) = viewModelScope.launch(Dispatchers.Main) {
        _state.value = state
    }

    /**
     * Used to safely dispatch a new [ViewCommand] for the client.
     */
    protected fun dispatchCommand(command: ViewCommand) = viewModelScope.launch(Dispatchers.Main) {
        _command.emit(command)
    }

    /**
     * Used to safely dispatch a new [BackStateResult] result for the view model.
     */
    fun dispatchResult(result: BackStateResult) = viewModelScope.launch(Dispatchers.Main) {
        handleBackStateResult(result)
    }

    /**
     * Returns the latest state.
     */
    protected fun <T> currentState() = requireNotNull(currentStateOrNull<T>())

    /**
     * Returns the latest state or null.
     */
    protected fun <T> currentStateOrNull() = _state.value?.let { it as? T }
}
