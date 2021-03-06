/**
 * Copyright 2016 - 2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jetbrains.rsynk.files

import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import java.util.concurrent.TimeUnit


internal object FileBitmasks {
    val FileTypeBitMask = 61440

    val Socket = 49152
    val SymLink = 40960
    val RegularFile = 32768
    val BlockDevice = 24576
    val Directory = 16384
    val CharacterDevice = 8192
    val FIFO = 4096

    val Other = 53248
}

class FileInfoReader(private val fs: FileSystemInfo) {

    fun getFileInfo(file: Path): FileInfo {

        val attributes = Files.readAttributes(file, BasicFileAttributes::class.java, LinkOption.NOFOLLOW_LINKS)
        val mode = getFileMode(attributes)

        return FileInfo(
                file,
                mode,
                attributes.size(),
                attributes.lastModifiedTime().to(TimeUnit.SECONDS),
                fs.defaultUser,
                fs.defaultGroup
        )
    }

    private fun getFileMode(attributes: BasicFileAttributes): Int {
        val modeBits = when {
            attributes.isDirectory -> FileBitmasks.Directory
            attributes.isRegularFile -> FileBitmasks.RegularFile
            attributes.isSymbolicLink -> FileBitmasks.SymLink
            else -> FileBitmasks.Other
        }
        return modeBits or fs.defaultDirPermission
    }
}
