package com.emendo.expensestracker.core.app.base.shared.navtype

import com.ramcosta.composedestinations.navargs.primitives.DestinationsEnumNavType
import com.ramcosta.composedestinations.navargs.primitives.array.DestinationsEnumArrayNavType
import com.ramcosta.composedestinations.navargs.primitives.arraylist.DestinationsEnumArrayListNavType
import com.ramcosta.composedestinations.navargs.primitives.valueOfIgnoreCase
import com.emendo.expensestracker.core.app.resources.models.ColorModel

public val colorModelEnumNavType: DestinationsEnumNavType<ColorModel> = DestinationsEnumNavType(ColorModel::class.java)