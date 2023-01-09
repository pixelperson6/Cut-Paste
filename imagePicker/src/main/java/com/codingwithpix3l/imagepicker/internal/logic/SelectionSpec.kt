package com.codingwithpix3l.imagepicker.internal.logic

import com.codingwithpix3l.imagepicker.Matisse


internal object SelectionSpec {

    private var matisseCache: Matisse? = null

    fun inject(matisse: Matisse) {
        matisseCache = matisse
    }

    fun getMatisse(): Matisse {
        return matisseCache ?: Matisse()
    }

}