package com.deliveryhero.services.crs.version

import com.deliveryhero.services.crs.api.version.Version
import com.deliveryhero.services.crs.api.version.VersionController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping(VersionController.PATH)
class VersionControllerImpl : VersionController {

    override fun getVersion(): Version {
        return Version(
                ApplicationInfo.version,
                ApplicationInfo.scmRevision,
                Instant.parse(ApplicationInfo.buildTimestamp),
                ApplicationInfo.buildNumber)
    }
}