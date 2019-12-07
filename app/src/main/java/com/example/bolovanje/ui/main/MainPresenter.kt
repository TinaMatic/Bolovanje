package com.example.bolovanje.ui.main

class MainPresenter: MainContract.Presenter {
    private lateinit var view: MainContract.View

    override fun destroy() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun attach(view: MainContract.View) {
        this.view = view
//        view.showHomeFragment()
    }
}