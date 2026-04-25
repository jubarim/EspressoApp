package org.juba.espressoapp.extensions

/** Normalizes decimal input by replacing commas with dots (e.g. "18,5" → "18.5"). */
fun String.normalizeDecimal(): String = replace(',', '.')
