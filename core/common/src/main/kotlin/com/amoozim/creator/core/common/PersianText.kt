package com.amoozim.creator.core.common

private val LATIN_TO_PERSIAN_DIGITS = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')

/**
 * Converts ASCII digits to Persian digits. The web app gets this "for free" from the
 * IRANYekanX **FaNum** font (which substitutes glyphs); since we render with the
 * system font here, we convert explicitly so numbers display as ۱۲۳ like the web UI.
 */
fun String.toPersianDigits(): String = buildString(length) {
    for (ch in this@toPersianDigits) {
        append(if (ch in '0'..'9') LATIN_TO_PERSIAN_DIGITS[ch - '0'] else ch)
    }
}

/** Groups a number with thousands separators and Persian digits (e.g. 1234567 -> "۱٬۲۳۴٬۵۶۷"). */
fun formatNumber(value: Long): String {
    val grouped = buildString {
        val raw = kotlin.math.abs(value).toString()
        for ((index, ch) in raw.withIndex()) {
            if (index > 0 && (raw.length - index) % 3 == 0) append('٬')
            append(ch)
        }
    }
    val signed = if (value < 0) "-$grouped" else grouped
    return signed.toPersianDigits()
}

fun formatNumber(value: Int): String = formatNumber(value.toLong())
