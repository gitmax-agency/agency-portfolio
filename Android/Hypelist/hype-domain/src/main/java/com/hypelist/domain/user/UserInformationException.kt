package com.hypelist.domain.user

sealed class UserInformationException: RuntimeException() {
    object NotFoundUserException: UserInformationException()
    object GenericUserException: UserInformationException()
    object UsernameNotAvailable: UserInformationException()
}