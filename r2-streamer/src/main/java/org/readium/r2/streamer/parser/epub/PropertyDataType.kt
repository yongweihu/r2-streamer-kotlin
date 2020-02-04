/*
 * Module: r2-streamer-kotlin
 * Developers: Quentin Gliosca
 *
 * Copyright (c) 2018. Readium Foundation. All rights reserved.
 * Use of this source code is governed by a BSD-style license which is detailed in the
 * LICENSE file present in the project repository where this source code is maintained.
 */


package org.readium.r2.streamer.parser.epub

internal val PACKAGE_RESERVED_PREFIXES = mapOf(
        "ally" to "http://www.idpf.org/epub/vocab/package/a11y/#",
        "dcterms" to "http://purl.org/dc/terms/",
        "marc" to "http://id.loc.gov/vocabulary/",
        "media" to "http://www.idpf.org/epub/vocab/overlays/#",
        "onix" to "http://www.editeur.org/ONIX/book/codelists/current.html#",
        "rendition" to "http://www.idpf.org/vocab/rendition/#",
        "schema" to "http://schema.org/",
        "xsd" to "http://www.w3.org/2001/XMLSchema#"
)

internal val CONTENT_RESERVED_PREFIXES = mapOf(
        "msv" to "http://www.idpf.org/epub/vocab/structure/magazine/#",
        "prism" to "http://www.prismstandard.org/specifications/3.0/PRISM_CV_Spec_3.0.htm#"
)

internal enum class DEFAULT_VOCAB(val iri: String) {
        META("http://idpf.org/epub/vocab/package/meta/#"),
        LINK("http://idpf.org/epub/vocab/package/meta/#"),
        ITEM("http://idpf.org/epub/vocab/package/item/#"),
        ITEMREF("http://idpf.org/epub/vocab/package/itemref/#"),
        TYPE("http://idpf.org/epub/vocab/structure/#") // this is a guessed value;
}


internal fun resolveProperty(property: String, prefixMap: Map<String, String>,
                    defaultVocab: DEFAULT_VOCAB? = null) : String? {
    val splitted = property.split(":", limit = 2)
    return if (splitted.size == 1) {
        defaultVocab?.iri?.let { it + splitted.first() }
    } else {
        prefixMap[splitted[0]]?.let { it + splitted[1] }
    }
}


internal fun parsePrefixes(prefixes: String): Map<String, String> {
    fun parsePrefixEntry(entry: String): Pair<String, String>? {
        val splitted = entry.split(":", limit = 2)
        return if (splitted.size == 1)
            null
        else {
            Pair(splitted[0].trim(), splitted[1].trim())
        }
    }

    return prefixes.split(" ").mapNotNull { parsePrefixEntry(it) }.toMap()
}