#!/usr/bin/env kotlin

@file:DependsOn("io.github.typesafegithub:github-workflows-kt:2.3.0")

import io.github.typesafegithub.workflows.domain.RunnerType.UbuntuLatest
import io.github.typesafegithub.workflows.domain.triggers.Cron
import io.github.typesafegithub.workflows.domain.triggers.Push
import io.github.typesafegithub.workflows.domain.triggers.RepositoryDispatch
import io.github.typesafegithub.workflows.domain.triggers.Schedule
import io.github.typesafegithub.workflows.domain.triggers.WorkflowDispatch
import io.github.typesafegithub.workflows.dsl.expressions.Contexts.secrets
import io.github.typesafegithub.workflows.dsl.expressions.expr
import io.github.typesafegithub.workflows.dsl.workflow
import io.github.typesafegithub.workflows.yaml.ConsistencyCheckJobConfig.Disabled

workflow(
    name = "Mirror Canonical Repository",
    on = listOf(
        Push(),
        RepositoryDispatch(),
        //TODO
        //Schedule(triggers = listOf(Cron(minute = "44"))),
        WorkflowDispatch()
    ),
    sourceFile = __FILE__,
    //TODO
    consistencyCheckJobConfig = Disabled
) {
    job(
        id = "mirror_repository",
        name = "Mirror Repository",
        runsOn = UbuntuLatest
    ) {
        run(
            name = "Clone Canonical Repository",
            command = "git clone --bare git://git.code.sf.net/p/jedit/jEdit.bak ."
        )

        val MIRROR_TOKEN by secrets
        run(
            name = "Push To Mirror Repository",
            command = "git push --mirror https://x:${expr(MIRROR_TOKEN)}@github.com/${expr { github.repository }}"
        )
    }
}
