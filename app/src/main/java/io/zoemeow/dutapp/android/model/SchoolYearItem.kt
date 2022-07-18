package io.zoemeow.dutapp.android.model

import java.io.Serializable

data class SchoolYearItem(
    // School year (ex. 21 is for 2021-2022).
    var year: Int = 21,
    // School semester (in range 1-3, ex. 1 for semester 1, 3 for semester in summer).
    var semester: Int = 3
): Serializable
