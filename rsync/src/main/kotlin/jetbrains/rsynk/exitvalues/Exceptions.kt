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
package jetbrains.rsynk.exitvalues

import jetbrains.rsynk.exitvalues.RsyncExitCodes.ProtocolIncompatibility
import jetbrains.rsynk.exitvalues.RsyncExitCodes.RequestActionNotSupported
import jetbrains.rsynk.exitvalues.RsyncExitCodes.RsyncProtocolDataStreamError
import jetbrains.rsynk.exitvalues.RsyncExitCodes.SelectInputFilesError

open class RsynkException(message: String, val exitCode: Int) : RuntimeException(message)

class ProtocolException(message: String, exitCode: Int = RsyncProtocolDataStreamError) :
        RsynkException(message, exitCode)

class ArgsParingException(message: String, exitCode: Int = RsyncProtocolDataStreamError) :
        RsynkException(message, exitCode)

class UnsupportedProtocolException(message: String) :
        RsynkException(message, ProtocolIncompatibility)

class ModuleNotFoundException(message: String) :
        RsynkException(message, SelectInputFilesError)

class InvalidFileException(message: String) :
        RsynkException(message, SelectInputFilesError)

class NotSupportedException(message: String) :
        RsynkException(message, RequestActionNotSupported)
