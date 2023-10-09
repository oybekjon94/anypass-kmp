package io.spherelabs.authpresentation.signup


import io.spherelabs.meteor.middleware.Middleware
import io.spherelabs.validation.EmailValidation
import io.spherelabs.validation.KeyPasswordValidation
import io.spherelabs.validation.NameValidation
import io.spherelabs.validation.PasswordValidation

typealias Validation = suspend (String) -> Boolean

class SignUpValidateMiddleware(
    private val validateName: NameValidation,
    private val validatePassword: PasswordValidation,
    private val validateEmail: EmailValidation,
    private val validateKeyPassword: KeyPasswordValidation,
) : Middleware<SignUpState, SignUpWish> {

    override suspend fun process(
        state: SignUpState,
        wish: SignUpWish,
        next: suspend (SignUpWish) -> Unit,
    ) {
        when (wish) {
            SignUpWish.OnSignUpClick -> {
                checkAndHandleEmail(state.email, next)
                checkAndHandleName(state.name, next)
                checkAndHandlePassword(state.password, next)
                checkAndHandleKeyPassword(state.keyPassword, next)
                checkAndHandleConfirmKeyPassword(state.confirmKeyPassword, next)
                hasKeyPasswordsSame(
                    keyPassword = state.keyPassword,
                    confirmKeyPassword = state.confirmKeyPassword,
                    next,
                )
                checkAllFieldsExist(
                    email = state.email,
                    password = state.password,
                    keyPassword = state.keyPassword,
                    confirmKeyPassword = state.confirmKeyPassword,
                    next,
                )
            }

            else -> {}
        }
    }

    private suspend fun checkAndHandleEmail(email: String, next: suspend (SignUpWish) -> Unit) {
        val isEmailNotValid = email.isNotEmpty() && !validateEmail.execute(email)
        if (isEmailNotValid) {
            next.invoke(SignUpWish.OnEmailFailed)
        } else {
            next.invoke(SignUpWish.OnLoadingChanged(false))
        }
    }

    private suspend fun checkAndHandleName(name: String, next: suspend (SignUpWish) -> Unit) {
        val isNameNotValid = name.isNotEmpty() && !validateName.execute(name)
        if (isNameNotValid) {
            next.invoke(SignUpWish.OnNameFailed)
        } else {
            next.invoke(SignUpWish.OnLoadingChanged(false))
        }
    }

    private suspend fun checkAndHandlePassword(
        password: String,
        next: suspend (SignUpWish) -> Unit,
    ) {
        val isPasswordNotValid =
            password.isNotEmpty() && !validatePassword.execute(password)
        if (isPasswordNotValid) {
            next.invoke(SignUpWish.OnPasswordFailed)
        } else {
            next.invoke(SignUpWish.OnLoadingChanged(false))
        }
    }

    private suspend fun checkAndHandleKeyPassword(
        keyPassword: String,
        next: suspend (SignUpWish) -> Unit,
    ) {
        val isKeyPasswordNotValid =
            keyPassword.isNotEmpty() && !validateKeyPassword.execute(keyPassword)
        if (isKeyPasswordNotValid) {
            next.invoke(SignUpWish.OnKeyPasswordFailed)
        } else {
            next.invoke(SignUpWish.OnLoadingChanged(false))
        }
    }

    private suspend fun checkAndHandleConfirmKeyPassword(
        confirmKeyPassword: String,
        next: suspend (SignUpWish) -> Unit,
    ) {
        val isConfirmKeyPasswordNotValid =
            confirmKeyPassword.isNotEmpty() && !validateKeyPassword.execute(confirmKeyPassword)
        if (isConfirmKeyPasswordNotValid) {
            next.invoke(SignUpWish.OnConfirmKeyPasswordFailed)
        } else {
            next.invoke(SignUpWish.OnLoadingChanged(false))
        }
    }

    private suspend fun hasKeyPasswordsSame(
        keyPassword: String,
        confirmKeyPassword: String,
        next: suspend (SignUpWish) -> Unit,
    ) {
        val isKeyPasswordSame =
            keyPassword == confirmKeyPassword

        if (!isKeyPasswordSame) {
            next.invoke(SignUpWish.OnKeyPasswordSameFailed)
        } else {
            next.invoke(SignUpWish.OnLoadingChanged(false))
        }
    }

    private suspend fun checkAllFieldsExist(
        email: String,
        password: String,
        keyPassword: String,
        confirmKeyPassword: String,
        next: suspend (SignUpWish) -> Unit,
    ) {
        suspend fun isNotEmptyAndValid(value: String, validation: Validation) =
            value.isNotEmpty() && validation(value)

        val isEmailValid = isNotEmptyAndValid(email, validateEmail::execute)
        val isPasswordValid = isNotEmptyAndValid(password, validatePassword::execute)
        val isKeyPasswordValid = isNotEmptyAndValid(keyPassword, validateKeyPassword::execute)
        val isKeyPasswordSame = keyPassword == confirmKeyPassword


        if (isEmailValid && isPasswordValid && isKeyPasswordValid && isKeyPasswordSame) {
            next.invoke(SignUpWish.SignUp)
        }
    }
}
