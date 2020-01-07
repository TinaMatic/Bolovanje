package com.example.bolovanje.ui.base

class BaseContract {

    interface BasePresenter<in T>{
        fun destroy()
        fun attach(view: T)
    }

    interface BaseView{

    }
}