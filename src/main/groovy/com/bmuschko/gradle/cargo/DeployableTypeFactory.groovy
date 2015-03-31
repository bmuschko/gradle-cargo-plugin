package com.bmuschko.gradle.cargo

import com.bmuschko.gradle.cargo.convention.Deployable
import com.bmuschko.gradle.cargo.util.DefaultFileUtil
import com.bmuschko.gradle.cargo.util.FileUtil

@Singleton
class DeployableTypeFactory {
    FileUtil fileUtil = new DefaultFileUtil()

    DeployableType getType(Deployable deployable) {
        String filenameExtension = fileUtil.getExtension(deployable.file)

        switch(filenameExtension) {
            case 'war': return DeployableType.WAR
            case 'ear': return DeployableType.EAR
            case '': return DeployableType.EXPLODED
            default: throw new IllegalArgumentException("Unknown deployable type for file extension '$filenameExtension'")
        }
    }
}
