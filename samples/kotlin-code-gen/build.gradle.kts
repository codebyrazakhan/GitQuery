import com.google.protobuf.gradle.builtins
import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.ofSourceSet
import com.google.protobuf.gradle.plugins
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc
import com.google.protobuf.gradle.remove

plugins {
    application
    id("com.google.protobuf") version "0.8.12"
    id("com.tinder.gitquery") version "2.0.1"
    java
}

val protoDir = "src/main/proto"

gitQuery {
    configFile = "gitquery.yml"
    outputDir = protoDir
    repoDir = "tmp/.gitquery"
    cleanOutput = true
}

sourceSets {
    main {
        proto.srcDirs(protoDir)
    }
}

val protobufVersion by extra("3.13.0")

repositories {
    jcenter()
    if (System.getenv("CI") == "true") {
        mavenLocal()
    }
    maven("https://jitpack.io")
}

application {
    mainClassName = "com.examples.addressbook.MainKt"
    applicationName = "addressbook"
}

dependencies {
    implementation("com.google.protobuf:protobuf-java:3.13.0")
}

protobuf {
    generatedFilesBaseDir = "$projectDir/src"
    protoc {
        artifact = "com.google.protobuf:protoc:$protobufVersion"
    }
    generateProtoTasks {
        ofSourceSet("main").forEach { task ->
            if (task.name == "generateProto") {
                task.dependsOn(tasks.getByName("gitQuery"))
            }
        }
    }
}
