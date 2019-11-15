package com.example.bolovanje.model

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmployerRepository { // TODO: when have structure like this, create data class Employee in models with all this info and add inject it to repo

    private var firstName: String? = null
    private var lastName: String? = null
    private var doznaka: Boolean = false
    private var numOfDays: Int? = null


    @Inject
    constructor(firstName: String?, lastName: String?, doznaka: Boolean, numOfDays: Int?) {
        this.firstName = firstName
        this.lastName = lastName
        this.doznaka = doznaka
        this.numOfDays = numOfDays
    }
}