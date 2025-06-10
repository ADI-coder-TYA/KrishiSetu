package com.cyberlabs.krishisetu.states

import com.amplifyframework.datastore.generated.model.UserRole

data class UserState(
    var id: String = "",
    var name: String = "",
    var role: UserRole = UserRole.FARMER,
    var phone: String = "",
    var email: String = ""
)