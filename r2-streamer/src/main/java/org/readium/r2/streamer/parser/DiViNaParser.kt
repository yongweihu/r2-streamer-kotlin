/*
 * Module: r2-streamer-kotlin
 * Developers: Aferdita Muriqi
 *
 * Copyright (c) 2018. Readium Foundation. All rights reserved.
 * Use of this source code is governed by a BSD-style license which is detailed in the
 * LICENSE file present in the project repository where this source code is maintained.
 */

package org.readium.r2.streamer.parser

import android.util.Log
import org.json.JSONObject
import org.readium.r2.shared.Publication
import org.readium.r2.shared.parsePublication
import org.readium.r2.streamer.container.ContainerDiViNa
import timber.log.Timber
import java.io.File
import java.nio.charset.Charset


/**
 *      DiViNaParser : Handle any DiViNa file. Opening, listing files
 *                  get name of the resource, creating the Publication
 *                  for rendering
 */

class DiViNaParser : PublicationParser {

    companion object {
        // Some constants useful to parse an DiViNa document
        const val mimetypeDiViNa = "application/divina+json"
        const val manifestPath = "manifest.json"
    }

    /**
     * Check if path exist, generate a container for DiViNa file
     *                   then check if creation was a success
     */
    private fun generateContainerFrom(path: String): ContainerDiViNa {
        val container: ContainerDiViNa?

        if (!File(path).exists())
            throw Exception("Missing File")
        container = ContainerDiViNa(path)
        if (!container.successCreated)
            throw Exception("Missing File")
        return container
    }

    /**
     *
     */
    override fun parse(fileAtPath: String, title: String): PubBox? {

        val container = try {
            generateContainerFrom(fileAtPath)
        } catch (e: Exception) {
            Log.e("Error", "Could not generate container", e)
            return null
        }

        val data = try {
            container.data(manifestPath)
        } catch (e: Exception) {
            Timber.e(e, "Missing File : $manifestPath")
            return null
        }

        //Building publication object from manifest.json
        //Getting manifest.json
        val stringManifest = data.toString(Charset.defaultCharset())
        val json = JSONObject(stringManifest)

        //Parsing manifest.json & building publication object
        val publication = parsePublication(json)
        publication.type = Publication.TYPE.DiViNa

        // Add href as title if title is missing (this is used to display the TOC)
        for ((index, link) in publication.readingOrder.withIndex()) {
            if (link.title == null || link.title!!.isEmpty()) {
                link.title = link.href
            }
        }

        return PubBox(publication, container)
    }

}