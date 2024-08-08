#!/usr/bin/env kotlin

@file:Repository("https://repo.maven.apache.org/maven2/")
@file:DependsOn("io.github.typesafegithub:github-workflows-kt:2.3.0")

@file:Repository("https://bindings.krzeminski.it/")
@file:DependsOn("actions:checkout:v4")
@file:DependsOn("actions:setup-java:v4")
@file:DependsOn("actions:upload-artifact:v4")
@file:DependsOn("mxschmitt:action-tmate:v3")

import io.github.typesafegithub.workflows.actions.actions.Checkout
import io.github.typesafegithub.workflows.actions.actions.SetupJava
import io.github.typesafegithub.workflows.actions.actions.SetupJava.Distribution.Temurin
import io.github.typesafegithub.workflows.actions.actions.UploadArtifact
import io.github.typesafegithub.workflows.actions.actions.UploadArtifact.BehaviorIfNoFilesFound.Error
import io.github.typesafegithub.workflows.actions.actions.UploadArtifact.BehaviorIfNoFilesFound.Ignore
import io.github.typesafegithub.workflows.actions.actions.UploadArtifact.CompressionLevel.NoCompression
import io.github.typesafegithub.workflows.domain.AbstractResult.Status.Success
import io.github.typesafegithub.workflows.domain.RunnerType
import io.github.typesafegithub.workflows.domain.Shell
import io.github.typesafegithub.workflows.domain.triggers.PullRequest
import io.github.typesafegithub.workflows.domain.triggers.Push
import io.github.typesafegithub.workflows.dsl.expressions.Contexts.always
import io.github.typesafegithub.workflows.dsl.expressions.Contexts.runner
import io.github.typesafegithub.workflows.dsl.expressions.expr
import io.github.typesafegithub.workflows.dsl.workflow
import io.github.typesafegithub.workflows.yaml.ConsistencyCheckJobConfig.Disabled

workflow(
    name = "Build",
    on = listOf(
        Push(),
        PullRequest()
    ),
    sourceFile = __FILE__,
    //TODO
    consistencyCheckJobConfig = Disabled
) {
    //TODO
    //job(
    //    id = "check_all_workflow_yaml_consistency",
    //    name = "Check all Workflow YAML consistency",
    //    runsOn = UbuntuLatest
    //) {
    //    uses(
    //        name = "Checkout",
    //        action = Checkout()
    //    )
    //    run(
    //        name = "Regenerate all workflow YAMLs and check for modifications",
    //        command = """find .github/workflows -mindepth 1 -maxdepth 1 -name "*.main.kts" | xargs -ri sh -c '{} && git diff --exit-code'"""
    //    )
    //}

    job(
        id = "build",
        name = "Build on ${expr("matrix.os")}",
        runsOn = RunnerType.Custom(expr("matrix.os")),
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

        val build = run(
            name = "Build",
            command = "ant -keep-going dist"
        )

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
                ifNoFilesFound = Ignore
            ),
            Artifact(
                name = "Debian Repository Packages File (gz)",
                path = "dist/Packages.gz",
                ifNoFilesFound = Ignore
            ),
            Artifact(
                name = "Debian Repository Packages File (bz2)",
                path = "dist/Packages.bz2",
                ifNoFilesFound = Ignore
            ),
            Artifact(
                name = "Debian Repository Release File",
                path = "dist/Release",
                ifNoFilesFound = Ignore
            ),
            Artifact(
                name = "Debian Repository Release File Signature",
                path = "dist/Release.gpg",
                ifNoFilesFound = Ignore
            )
        )

        val buildSuccessful = "(${always()}) && (${build.outcome eq Success})"

        uses(
            name = "Upload All Result Files",
            action = UploadArtifact(
                name = "All Artifacts",
                path = allPossibleArtifacts.map { it.path },
                ifNoFilesFound = Error,
                compressionLevel = NoCompression
            ),
            condition = buildSuccessful
        )

        allPossibleArtifacts.forEach { (name, path, ifNoFilesFound, condition) ->
            uses(
                name = "Upload $name",
                action = UploadArtifact(
                    name = name,
                    path = listOf(path),
                    ifNoFilesFound = ifNoFilesFound,
                    compressionLevel = NoCompression
                ),
                condition = condition?.let { "($buildSuccessful) && ($condition)" } ?: buildSuccessful
            )
        }

        val uploadAllUnexpectedResultFiles = uses(
            name = "Upload All Unexpected Result Files",
            action = UploadArtifact(
                name = "Unexpected Artifacts",
                path = allPossibleArtifacts.map { "!${it.path}" } + "dist",
                ifNoFilesFound = Ignore,
                compressionLevel = NoCompression
            ),
            condition = buildSuccessful
        )

        run(
            name = "Verify No Unexpected Result Files",
            command = "[ '${expr { uploadAllUnexpectedResultFiles.outputs.artifactId }}' == '' ]",
            condition = uploadAllUnexpectedResultFiles.outcome eq Success,
            shell = Shell.Bash
        )
    }
}

data class Artifact(
    val name: String,
    val path: String,
    val ifNoFilesFound: UploadArtifact.BehaviorIfNoFilesFound = Error,
    val condition: String? = null
)
