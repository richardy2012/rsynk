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

import java.io.File
import java.nio.file.Path

data class User(
        val name: String,
        val uid: Int
) {
    val isRoot: Boolean = uid == 0
}

data class Group(
        val name: String,
        val gid: Int
) {
    val isRoot: Boolean = gid == 0
}

data class FileInfo(
        val path: Path,
        val mode: Int,
        val size: Long,
        val lastModified: Long,
        val user: User,
        val group: Group
) : Comparable<FileInfo> {

    private infix fun Byte.compare(other: Byte): Int {
        return (0xFF and this.toInt()) - (0xFF and other.toInt())
    }

    override fun compareTo(other: FileInfo): Int {
        when {
            this.isDotDir && other.isDotDir -> return 0
            this.isDotDir && other.isNotDotDir -> return -1
            this.isNotDotDir && other.isDotDir -> return 1
        }

        when {
            this.isDirectory && !other.isDirectory -> return 1
            !this.isDirectory && other.isDirectory -> return -1
        }

        val thisBytes = this.path.toUri().path.toByteArray()
        val otherBytes = other.path.toUri().path.toByteArray()
        (thisBytes zip otherBytes).forEach { (a, b) ->
            val result = a compare b
            if (result != 0) {
                return result
            }
        }

        if (thisBytes.size == otherBytes.size) {
            return 0
        }

        val callerPathIsShorter = thisBytes.size < otherBytes.size
        if (this.isDirectory && other.isDirectory) {
            if (callerPathIsShorter) {
                return '/'.toByte() compare otherBytes[thisBytes.size]
            } else {
                return thisBytes[otherBytes.size] compare '/'.toByte()
            }
        }

        if (callerPathIsShorter) {
            return -1
        }
        return 1
    }


    val isDirectory: Boolean
        get() = (mode and FileBitmasks.FileTypeBitMask) == FileBitmasks.Directory
    val isSocket: Boolean
        get() = (mode and FileBitmasks.FileTypeBitMask) == FileBitmasks.Socket
    val isSymlink: Boolean
        get() = (mode and FileBitmasks.FileTypeBitMask) == FileBitmasks.SymLink
    val isBlockDevice: Boolean
        get() = (mode and FileBitmasks.FileTypeBitMask) == FileBitmasks.BlockDevice
    val isCharacterDevice: Boolean
        get() = (mode and FileBitmasks.FileTypeBitMask) == FileBitmasks.CharacterDevice
    val isFIFO: Boolean
        get() = (mode and FileBitmasks.FileTypeBitMask) == FileBitmasks.FIFO
    val isReqularFile: Boolean
        get() = (mode and FileBitmasks.FileTypeBitMask) == FileBitmasks.RegularFile

    val isDotDir = path.nameCount == 1 && path.endsWith(".")
    val isNotDotDir = !isDotDir
}

data class RsynkFileBoundaries(val offset: Long,
                               val length: Long)

data class RsynkFile(val file: File,
                     val getBoundariesCallback: () -> RsynkFileBoundaries)

data class RsynkFileInfoWithBoundaries(val rsynkFile: RsynkFile,
                                       val info: FileInfo): Comparable<RsynkFileInfoWithBoundaries> {
    override fun compareTo(other: RsynkFileInfoWithBoundaries): Int {
        return info.compareTo(other.info)
    }
}


