package com.deliveryhero.services.crs.version

import com.ninecookies.common.util.ExceptionFormatter
import com.ninecookies.common.util.Strings
import java.io.IOException
import java.util.*

object ApplicationInfo {

    private const val GIT_PROPERTIES = "git.properties"
    private const val BUILD_PROPERTIES = "build.properties"

    private const val SCM_PREFIX = "git."
    private const val BUILD_VERSION = SCM_PREFIX + "build.version"
    private const val BUILD_TIMESTAMP = SCM_PREFIX + "build.time"
    private const val SCM_REVISION = SCM_PREFIX + "commit.id.abbrev"

    private const val BUILD_NUMBER = "build.number"

    private val properties = load()

    private fun load(): Properties {
        val props = Properties()
        load(GIT_PROPERTIES, props)
        load(BUILD_PROPERTIES, props)
        return props
    }

    private fun load(filename: String, props: Properties) {
        try {
            ApplicationInfo::class.java.classLoader.getResourceAsStream(filename).use { inputStream ->
                if (inputStream == null) {
                    throw IllegalStateException(ExceptionFormatter
                            .format("can't locate build information file on classpath", "file",
                                    filename))
                }
                props.load(inputStream)
                props
            }
        } catch (ex: IOException) {
            throw IllegalStateException(ex)
        }
    }

    /**
     * Gets the application version.
     *
     * @return the application version.
     */
    val version: String
        get() = getRequiredProperty(BUILD_VERSION)

    /**
     * Gets the build time stamp in ISO-8601 UTC format.
     *
     * @return the build time stamp.
     */
    val buildTimestamp: String
        get() = getRequiredProperty(BUILD_TIMESTAMP)

    /**
     * Gets the SCM revision this application was built from.
     *
     * @return the SCM revision.
     */
    val scmRevision: String
        get() = getRequiredProperty(SCM_REVISION)

    /**
     * Gets the builder number - e.g. set by a CI system.
     *
     * @return the build number.
     */
    val buildNumber: String
        get() = getRequiredProperty(BUILD_NUMBER)


    private fun getRequiredProperty(property: String): String {
        val value = properties.getProperty(property)
        if (Strings.isNullOrEmpty(value)) {
            throw IllegalStateException(
                    ExceptionFormatter.format("can't find required property in build information file",
                            "file", BUILD_PROPERTIES, "property", property))
        }
        return value
    }
}
