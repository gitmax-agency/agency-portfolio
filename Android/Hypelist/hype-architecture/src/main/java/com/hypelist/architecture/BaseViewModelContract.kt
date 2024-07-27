package com.hypelist.architecture

import java.io.Serializable

/**
 * Represents the UI state of your component.
 */
open class State

/**
 * Represents an action performed by the user on the UI.
 * For example: button click, page scroll, gesture movement, etc.
 */
open class UserAction

/**
 * Represents an instruction sent from the view model to the view.
 * For example: navigate to another screen, show a dialog, show a toast message, etc.
 *
 * A command will be consumed only once by the view.
 */
open class ViewCommand

/**
 * Represents an back state result sent from the view to the view model.
 * For example: popping from a fragment back to another fragment on the backstack with a result
 */
open class BackStateResult : Serializable
