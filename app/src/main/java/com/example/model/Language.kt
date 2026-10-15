package com.example.model

enum class Language(val code: String, val displayName: String, val flag: String) {
    BN("bn", "বাংলা", "🇧🇩"),
    EN("en", "English", "🌐");

    fun isBengali(): Boolean = this == BN
}

object LanguageHelper {
    private val bengaliDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')

    fun toBengaliNumerals(input: Any): String {
        val str = input.toString()
        val sb = StringBuilder()
        for (c in str) {
            if (c in '0'..'9') {
                sb.append(bengaliDigits[c - '0'])
            } else {
                sb.append(c)
            }
        }
        return sb.toString()
    }

    fun formatBdt(amount: Double, language: Language): String {
        val rounded = amount.toInt()
        val formattedNumber = "%,d".format(rounded)
        return if (language.isBengali()) {
            "৳ ${toBengaliNumerals(formattedNumber)}"
        } else {
            "৳ $formattedNumber"
        }
    }

    fun text(en: String, bn: String, lang: Language): String {
        return if (lang.isBengali()) bn else en
    }
}
