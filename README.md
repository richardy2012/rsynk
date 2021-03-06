# Rsynk #

[![GitHub license](https://img.shields.io/hexpm/l/plug.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)
![Repository Size](https://reposs.herokuapp.com/?path=JetBrains/rsynk)
![Pure Kotlin](https://img.shields.io/badge/100%25-kotlin-orange.svg)

## SSH server that accepts rsync read request ##  

### What it is ###
In your application running in a JVM, it works like an [rsync](https://rsync.samba.org) daemon on a remote
computer: it allows you to connect to remote machine and download files. Hovewer it works via ssh, not via rsync protocol.

### What it is not ###
This is not a jvm rsync re-implementation. You cannot use it as jvm rsync client, only as a server

**Rsynk** lets you to choose tracked files dynamically, providing an API to your application:

```kotlin
val rsynk = Rsynk.newBuilder().apply {
                  port = 22
                  idleConnectionTimeout = 30000
                  nioWorkersNumber = 1
                  commandWorkersNumber = 1
                  }.build()
                  
rsynk.addTrackingFiles(getNewlyCreatedUsersDataFiles())
...
rsync.setTrackingFiles(emptyList()) //delete all previously added files without having server downtime
...
rsynk.addTrackingFile(getLastUserDataFile()) // track a new file, 0 downtime
```                

Also **Rsynk** makes it possible to track only a part (currently continious) of a file (i.e. a consistent part which is correct in terms of your application current state)

```kotlin
val logData = File("app/data/logs/users.log")
val rsynkFile = RsynkFile(logData, RsynkFileBoundaries {
  // this callback will be invoked for each request 
  // asking for users.log file
  // so you can define correct boundaries from your application
  val lowerBound = 0                                   
  val upperBound = getLastWritePosition(logData) // an example of method you can provide         
  RsynkFilesBoundaries(lowerBound, upperBound)
})
rsynk.addTrackingFile(rsynkFile)
```

Consider work in progress

### Building from Source
[Gradle](http://www.gradle.org) is used to build and test. JDK 1.8 and [Kotlin](http://kotlinlang.org)
1.1.1 are required. To build the project, run:

    ./gradlew
