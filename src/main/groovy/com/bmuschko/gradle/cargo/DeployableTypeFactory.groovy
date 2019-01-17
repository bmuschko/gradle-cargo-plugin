package com.bmuschko.gradle.cargo

import com.bmuschko.gradle.cargo.util.DefaultFileUtil
import com.bmuschko.gradle.cargo.util.FileUtil

@Singleton
class DeployableTypeFactory {
    FileUtil fileUtil = new DefaultFileUtil()

    DeployableType getType(File file) {
        String filenameExtension = fileUtil.getExtension(file)

        switch(filenameExtension) {
            case 'war': return DeployableType.WAR
            case 'ear': return DeployableType.EAR
            case '': return DeployableType.EXPLODED
            default: throw new IllegalArgumentException("Unknown deployable type for file extension '$filenameExtension'")
        }
    }
}
