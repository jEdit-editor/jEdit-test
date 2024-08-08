#!/usr/bin/env kotlin

@file:Repository("https://repo.maven.apache.org/maven2/")
@file:DependsOn("io.github.typesafegithub:github-workflows-kt:3.0.0")
//@file:DependsOn("io.github.typesafegithub:github-workflows-kt:3.0.1-SNAPSHOT")

@file:Repository("https://bindings.krzeminski.it/")
//@file:Repository("http://localhost:8080/")
//@file:Repository("http://localhost:8080/v2/")
//@file:Repository("http://localhost:8080/refresh/")

//@file:DependsOn("tecolicom:actions-use-homebrew-tools___major:[v1,v2)")
//@file:DependsOn("Wandalen:wretry.action__main___major:[v3,v4)")
//@file:DependsOn("actions:checkout___major:[v4,v5)")
//@file:DependsOn("actions:setup-java___major:[v4,v5)")
//@file:DependsOn("actions:setup-java:[v4,v5)")
//@file:DependsOn("DamianReeves:write-file-action___minor:[v1,v2)")
//@file:DependsOn("actions__types__66e2cf81-db71-4622-a4fb-3f611e2c9c8f:cache:v4") // boolean
//@file:DependsOn("actions__types__b694d967-f59a-41f9-b151-823585b51f72:cache:v4") // string
//@file:DependsOn("actions__types__599dcd1f-0f3f-4ea2-80af-ae0737b0a5d6:cache:v4") // untyped
//@file:DependsOn("actions:cache___major:[v4,v5)")
//@file:DependsOn("actions:cache__restore___major:[v4,v5)")
//@file:DependsOn("actions:cache__save___major:[v4,v5)")
//@file:DependsOn("actions:upload-artifact___major:[v4,v5)")

@file:DependsOn("tecolicom:actions-use-homebrew-tools:v1")
@file:DependsOn("Wandalen:wretry.action__main:v3")
@file:DependsOn("actions:checkout:v4")
@file:DependsOn("actions:setup-java:v4")
@file:DependsOn("DamianReeves:write-file-action:v1.3")
@file:DependsOn("actions:cache:v4")
@file:DependsOn("actions:cache__restore:v4")
@file:DependsOn("actions:cache__save:v4")
@file:DependsOn("actions:upload-artifact:v4")
//@file:DependsOn("mxschmitt:action-tmate:v3")

import io.github.typesafegithub.workflows.actions.actions.Cache
import io.github.typesafegithub.workflows.actions.actions.CacheRestore
import io.github.typesafegithub.workflows.actions.actions.CacheSave
import io.github.typesafegithub.workflows.actions.actions.Checkout
import io.github.typesafegithub.workflows.actions.actions.SetupJava
import io.github.typesafegithub.workflows.actions.actions.SetupJava.Distribution.Temurin
import io.github.typesafegithub.workflows.actions.actions.UploadArtifact
import io.github.typesafegithub.workflows.actions.actions.UploadArtifact.BehaviorIfNoFilesFound.Error
import io.github.typesafegithub.workflows.actions.actions.UploadArtifact.BehaviorIfNoFilesFound.Ignore
import io.github.typesafegithub.workflows.actions.actions.UploadArtifact.CompressionLevel.NoCompression
import io.github.typesafegithub.workflows.actions.damianreeves.WriteFileAction
//import io.github.typesafegithub.workflows.actions.mxschmitt.ActionTmate
import io.github.typesafegithub.workflows.actions.tecolicom.ActionsUseHomebrewTools
import io.github.typesafegithub.workflows.actions.wandalen.WretryActionMain
import io.github.typesafegithub.workflows.domain.AbstractResult.Status.Skipped
import io.github.typesafegithub.workflows.domain.AbstractResult.Status.Success
//import io.github.typesafegithub.workflows.domain.Expression
//import io.github.typesafegithub.workflows.domain.JobOutputs
import io.github.typesafegithub.workflows.domain.RunnerType
import io.github.typesafegithub.workflows.domain.RunnerType.UbuntuLatest
import io.github.typesafegithub.workflows.domain.Shell.Bash
import io.github.typesafegithub.workflows.domain.triggers.PullRequest
import io.github.typesafegithub.workflows.domain.triggers.Push
import io.github.typesafegithub.workflows.dsl.expressions.Contexts.always
import io.github.typesafegithub.workflows.dsl.expressions.Contexts.runner
import io.github.typesafegithub.workflows.dsl.expressions.expr
import io.github.typesafegithub.workflows.dsl.workflow

workflow(
    name = "Build",
    on = listOf(
        Push(),
        PullRequest()
    ),
    sourceFile = __FILE__
) {
    val allPossibleArtifacts = listOf(
        Artifact(
            name = "Manual in A4 Paper size",
            path = "dist/jedit*manual-a4.pdf"
        ),
        Artifact(
            name = "Manual in Letter Paper size",
            path = "dist/jedit*manual-letter.pdf"
        ),
        Artifact(
            name = "Source Package",
            path = "dist/jedit*source.tar.bz2"
        ),
        Artifact(
            name = "Java based Installer",
            path = "dist/jedit*install.jar"
        ),
        Artifact(
            name = "Slackware Installer",
            path = "dist/jedit-*-noarch-1sao.tgz"
        ),
        Artifact(
            name = "Debian Installer",
            path = "dist/jedit_*_all.deb"
        ),
        Artifact(
            name = "Windows Installer",
            path = "dist/jedit*install.exe"
        ),
        Artifact(
            name = "macOS Installer",
            path = "dist/jedit*install.dmg",
            condition = "${runner.os} == 'macOS'"
        ),
        Artifact(
            name = "macOS Intermediate Result",
            path = "dist/jedit*-dist-mac-finish.tar.bz2",
            condition = "${runner.os} != 'macOS'"
        ),
        Artifact(
            name = "Debian Repository Packages File",
            path = "dist/Packages",
            ignoreIfMissing = true
        ),
        Artifact(
            name = "Debian Repository Packages File (gz)",
            path = "dist/Packages.gz",
            ignoreIfMissing = true
        ),
        Artifact(
            name = "Debian Repository Packages File (bz2)",
            path = "dist/Packages.bz2",
            ignoreIfMissing = true
        ),
        Artifact(
            name = "Debian Repository Release File",
            path = "dist/Release",
            ignoreIfMissing = true
        ),
        Artifact(
            name = "Debian Repository Release File Signature",
            path = "dist/Release.gpg",
            ignoreIfMissing = true
        )
    )

    val build = job(
        id = "build",
        name = "Build on ${expr("matrix.os")}",
        runsOn = RunnerType.Custom(expr("matrix.os")),
//        outputs = object : JobOutputs() {
//            var booleanOutput by output<Boolean>()
//            var booleanOutput2 by output<Boolean>()
//            var stringOutput by output<String>()
//            var stringOutput2 by output<String>()
//            var anyOutput by output<Any>()
//            var anyOutput2 by output<Any>()
//        },
        _customArguments = mapOf(
            "strategy" to mapOf(
                "fail-fast" to false,
                "matrix" to mapOf(
                    "os" to listOf(
                        "ubuntu-latest",
                        "windows-latest",
                        "macos-latest"
                    )
                )
            )
        )
    ) {
//        uses(action = ActionTmate())

        run(
            name = "Install Wine on Linux",
            command = """
                sudo dpkg --add-architecture i386
                sudo wget -nc -O /etc/apt/keyrings/winehq-archive.key https://dl.winehq.org/wine-builds/winehq.key
                sudo wget -NP /etc/apt/sources.list.d/ "https://dl.winehq.org/wine-builds/ubuntu/dists/${'$'}(lsb_release -c | grep -o '\w*${'$'}')/winehq-${'$'}(lsb_release -c | grep -o '\w*${'$'}').sources"
                sudo apt update
                sudo apt install --yes --no-install-recommends winehq-stable
                winecfg /v win10
            """.trimIndent(),
            condition = "${runner.os} == 'Linux'"
        )

        uses(
            name = "Install Wine on macOS",
            action = ActionsUseHomebrewTools(
                tools = listOf("wine-stable"),
                path = listOf(
                    "/Applications",
                    "/Library"
                )
            ),
            condition = "${runner.os} == 'macOS'"
        )

        val innoSetupInstaller = "${expr { runner.temp }}/innosetup-6.3.3.exe"

        val cacheInnoSetupInstaller = uses(
            name = "Cache InnoSetup installer on Linux and macOS",
            action = Cache(
                path = listOf(innoSetupInstaller),
                key = innoSetupInstaller.substringAfterLast('/')
            ),
            condition = "(${runner.os} == 'Linux') || (${runner.os} == 'macOS')"
        )

//        // cacheHit boolean typed
//        jobOutputs.booleanOutput = cache.outputs.cacheHit
//        jobOutputs.booleanOutput2 = cache.outputs.cacheHit_Untyped
//        //jobOutputs.stringOutput = cache.outputs.cacheHit // does not work
//        jobOutputs.stringOutput = cache.outputs.cacheHit_Untyped // drop-in
//        jobOutputs.stringOutput2 = cache.outputs.cacheHit_Untyped
//        //jobOutputs.anyOutput = cache.outputs.cacheHit // does not work
//        jobOutputs.anyOutput = Expression(cache.outputs.cacheHit.expression) // drop-in
//        jobOutputs.anyOutput2 = cache.outputs.cacheHit_Untyped

//        // cacheHit string typed
//        //jobOutputs.booleanOutput = cache.outputs.cacheHit // does not work
//        jobOutputs.booleanOutput = cache.outputs.cacheHit_Untyped // drop-in
//        jobOutputs.booleanOutput2 = cache.outputs.cacheHit_Untyped
//        jobOutputs.stringOutput = cache.outputs.cacheHit
//        jobOutputs.stringOutput2 = cache.outputs.cacheHit_Untyped
//        //jobOutputs.anyOutput = cache.outputs.cacheHit // does not work
//        jobOutputs.anyOutput = Expression(cache.outputs.cacheHit.expression) // drop-in
//        jobOutputs.anyOutput2 = cache.outputs.cacheHit_Untyped

//        // cacheHit untyped
//        jobOutputs.booleanOutput = cache.outputs.cacheHit_Untyped
//        jobOutputs.booleanOutput2 = cache.outputs.cacheHit_Untyped
//        jobOutputs.stringOutput = cache.outputs.cacheHit_Untyped
//        jobOutputs.stringOutput2 = cache.outputs.cacheHit_Untyped
//        jobOutputs.anyOutput = cache.outputs.cacheHit_Untyped
//        jobOutputs.anyOutput2 = cache.outputs.cacheHit_Untyped

//        println("cache.outputs.cacheHit.expression = ${cache.outputs.cacheHit.expression}")
//        println("cache.outputs.cacheHit.expressionString = ${cache.outputs.cacheHit.expressionString}")
//        println("cache.outputs.cacheHit.expression = ${cache.outputs.cacheHit_Untyped.expression}")
//        println("cache.outputs.cacheHit.expressionString = ${cache.outputs.cacheHit_Untyped.expressionString}")
//        println("cache.outputs[\"cache-hit\"].expression = ${cache.outputs["cache-hit"].expression}")
//        println("cache.outputs[\"cache-hit\"].expressionString = ${cache.outputs["cache-hit"].expressionString}")
//
//        uses(
//            name = "Cache InnoSetup installer on Linux and macOS",
//            action = Cache(
//                path = listOf("${expr { runner.tool_cache }}/innosetup.exe/6.3.3"),
//                key = "innosetup.exe_6.3.3",
////                lookupOnlyExpression = cache.outputs.cacheHit // good type
////                lookupOnlyExpression = Expression(cache.outputs.cacheHit.expression) // bad type
////                lookupOnly_Untyped = cache.outputs.cacheHit.expressionString // bad type
//                lookupOnlyExpression = cache.outputs.cacheHit_Untyped // untyped
////                lookupOnlyExpression = cache.outputs["cache-hit"] // ad-hoc-output
//            ),
//            condition = "(${runner.os} == 'Linux') || (${runner.os} == 'macOS')"
//        )

        run(
            name = "Provision InnoSetup on Linux and macOS",
            command = "wget -O $innoSetupInstaller https://files.jrsoftware.org/is/6/innosetup-6.3.3.exe",
            condition = """
                ((${runner.os} == 'Linux') || (${runner.os} == 'macOS'))
                && (${cacheInnoSetupInstaller.outputs.cacheHit} == false)
            """.trimIndent()
        )

        run(
            name = "Start Xvfb on Linux",
            command = "Xvfb :0 -screen 0 1024x768x16 &",
            condition = "${runner.os} == 'Linux'"
        )

        uses(
            name = "Install InnoSetup on Linux",
            env = mapOf(
                "DISPLAY" to ":0.0"
            ),
            // part of work-around for Xvfb sometimes not starting fast enough
            action = WretryActionMain(
                command = "wine $innoSetupInstaller /SP- /VERYSILENT /SUPPRESSMSGBOXES /NORESTART",
                attemptLimit = 3,
                attemptDelay = 1000
            ),
            condition = "${runner.os} == 'Linux'"
        )

        run(
            name = "Install InnoSetup on macOS",
            command = "wine $innoSetupInstaller /SP- /VERYSILENT /SUPPRESSMSGBOXES /NORESTART",
            condition = "${runner.os} == 'macOS'"
        )

        run(
            name = "Configure Git",
            command = "git config --global core.autocrlf input"
        )

        uses(
            name = "Checkout",
            action = Checkout()
        )

        uses(
            name = "Setup Java 11",
            action = SetupJava(
                javaVersion = "11",
                distribution = Temurin
            )
        )

        uses(
            name = "Configure Build Properties for Linux",
            action = WriteFileAction(
                path = "build.properties",
                contents = """
                    wine.executable = wine
                    winepath.executable = winepath
                    innosetup.compiler.executable = /home/runner/.wine/drive_c/Program Files (x86)/Inno Setup 6/ISCC.exe
                    innosetup.via.wine = true
                """.trimIndent()
            ),
            condition = "${runner.os} == 'Linux'"
        )

        uses(
            name = "Configure Build Properties for Windows",
            action = WriteFileAction(
                path = "build.properties",
                contents = """
                    innosetup.compiler.executable = C:/Program Files (x86)/Inno Setup 6/ISCC.exe
                """.trimIndent()
            ),
            condition = "${runner.os} == 'Windows'"
        )

        uses(
            name = "Configure Build Properties for macOS",
            action = WriteFileAction(
                path = "build.properties",
                contents = """
                    wine.executable = wine
                    winepath.executable = winepath
                    innosetup.compiler.executable = /Users/runner/.wine/drive_c/Program Files (x86)/Inno Setup 6/ISCC.exe
                    innosetup.via.wine = true
                """.trimIndent()
            ),
            condition = "${runner.os} == 'macOS'"
        )

        val build = run(
            name = "Build",
            command = "ant -keep-going dist"
        )

        val buildSuccessful = """
            (${always()})
            && (${build.outcome eq Success})
        """.trimIndent()

        // Upload all result files from macOS build that can build all artifacts as one archive
        uses(
            name = "Upload All Result Files",
            action = UploadArtifact(
                name = "All Artifacts",
                path = allPossibleArtifacts.map { it.path },
                ifNoFilesFound = Error,
                compressionLevel = NoCompression
            ),
            condition = "($buildSuccessful) && (${runner.os} == 'macOS')"
        )

        allPossibleArtifacts.forEach { (name, path, ignoreIfMissing, condition) ->
            val verifyArtifactWasBuilt = run(
                name = "Verify $name was Built",
                command = if (ignoreIfMissing) {
                    """
                        [ -f $path ] && echo 'found=true' || true >>"${'$'}GITHUB_OUTPUT"
                    """.trimIndent()
                } else {
                    """
                        [ -f $path ] && echo 'found=true' >>"${'$'}GITHUB_OUTPUT"
                    """.trimIndent()
                },
                condition = condition?.let { "($buildSuccessful) && ($it)" } ?: buildSuccessful,
                shell = Bash
            )

            uses(
                name = "Save $name to Cache",
                action = CacheSave(
                    path = listOf(path),
                    key = "${expr { github.run_id }} - $name - ${expr { runner.os }}",
                    enableCrossOsArchive = true
                ),
                // work-around for https://github.com/typesafegithub/github-workflows-kt/issues/1587
                //    && (${verifyArtifactWasBuilt.outputs["found"]} == 'true')
                condition = """
                    ($buildSuccessful)
                    && (${verifyArtifactWasBuilt.outcome eq Success})
                    && (steps.${verifyArtifactWasBuilt.id}.outputs.found == 'true')
                """.trimIndent()
            )
        }

        val uploadAllUnexpectedResultFiles = uses(
            name = "Upload All Unexpected Result Files",
            action = UploadArtifact(
                name = "Unexpected Artifacts (${expr { runner.os }})",
                path = listOf("dist") + allPossibleArtifacts.map { "!${it.path}" },
                ifNoFilesFound = Ignore,
                compressionLevel = NoCompression
            ),
            condition = buildSuccessful
        )

        run(
            name = "Verify No Unexpected Result Files",
            command = "[ '${expr { uploadAllUnexpectedResultFiles.outputs.artifactId }}' == '' ]",
            condition = """
                (${always()})
                && (${uploadAllUnexpectedResultFiles.outcome eq Success})
            """.trimIndent(),
            shell = Bash
        )
    }

    job(
        id = "upload-artifacts",
        name = "Upload Individual Artifacts from one of the Jobs",
        runsOn = UbuntuLatest,
        needs = listOf(build),
        condition = """
            (${always()}
            && (${build.result neq Skipped}))
        """.trimIndent()
    ) {
        allPossibleArtifacts.forEach { (name, path) ->
            uses(
                name = "Restore $name from Cache",
                action = CacheRestore(
                    path = listOf(path),
//                    key = "${build.outputs.stringOutput.expressionString} - ${build.outputs.stringOutput2.expressionString} - ${build.outputs.booleanOutput.expressionString} - ${build.outputs.booleanOutput2.expressionString} - ${build.outputs.anyOutput.expressionString} - ${build.outputs.anyOutput2.expressionString} - ${expr { github.run_id }} - $name - macOS",
                    key = "${expr { github.run_id }} - $name - macOS",
                    restoreKeys = listOf("${expr { github.run_id }} - $name - ")
                )
            )

            uses(
                name = "Upload $name",
                action = UploadArtifact(
                    name = name,
                    path = listOf(path),
                    ifNoFilesFound = Ignore,
                    compressionLevel = NoCompression
                )
            )
        }
    }
}

data class Artifact(
    val name: String,
    val path: String,
    val ignoreIfMissing: Boolean = false,
    val condition: String? = null
)
