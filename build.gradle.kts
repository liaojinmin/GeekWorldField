
val taboolibVersion: String by project

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.7.20"
    id("io.izzel.taboolib") version "1.56"
}

taboolib {
    install(
        "common",
        "common-5",
        "platform-bukkit",
        "module-configuration",
        "module-chat",
        "module-lang",
        "module-nms",
        "module-nms-util",
    )
    description {
        contributors {
            name("HSDLao_liao")
        }
        dependencies {
            bukkitApi("1.13")
            name("Vault").optional(true)
        }
    }

    relocate("me.geek.world", "me.geek.world")
    relocate("com.zaxxer.hikari", "com.zaxxer.hikari_4_0_3_world")
    classifier = null
    version = taboolibVersion
}

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://repo.tabooproject.org/repository/releases")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.codemc.org/repository/maven-public")
    maven("https://repo.rosewooddev.io/repository/public/")
    maven("https://repo.opencollab.dev/maven-snapshots/")
    maven("https://jitpack.io")
    maven("https://maven.pkg.github.com/LoneDev6/API-ItemsAdder")
}


dependencies {

    compileOnly(kotlin("stdlib"))
    // Server Core
    compileOnly("ink.ptms.core:v11604:11604")

    // Hook Plugins
    compileOnly("com.zaxxer:HikariCP:4.0.3")
    compileOnly("cn.hutool:hutool-all:5.4.3")
    compileOnly("org.xerial.snappy:snappy-java:1.1.8.4")
    compileOnly("me.clip:placeholderapi:2.10.9") { isTransitive = false }
    compileOnly("com.github.MilkBowl:VaultAPI:-SNAPSHOT") { isTransitive = false }
    compileOnly("org.black_ixx:playerpoints:3.1.1") { isTransitive = false }
    compileOnly("com.github.LoneDev6:api-itemsadder:3.2.3c")

    // Libraries
    compileOnly(fileTree("libs"))
}

