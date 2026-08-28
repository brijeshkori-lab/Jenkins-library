pipelineJob('python-app-v2-without-sonar') {

    description('''
        Python V2 CI/CD Pipeline without SonarQube.

        Uses JFrog Cloud for dependency resolution,
        artifact publishing, Build Info and Xray.
    ''')

    parameters {

        stringParam(
            'PROJECT_URL',
            '',
            'Git URL of the Python application'
        )

        choiceParam(
            'ENVIRONMENT',
            [
                'DEV',
                'PROD'
            ],
            'Target environment'
        )

        choiceParam(
            'IS_FINAL_RUN',
            [
                'false',
                'true'
            ],
            'Set true only for final PROD release'
        )

        stringParam(
            'PRIMARY_APPROVER_EMAIL',
            '',
            'Required for PROD final run'
        )

        stringParam(
            'MANAGER_EMAIL',
            '',
            'Manager notification email'
        )

        stringParam(
            'TO_EMAIL',
            '',
            'Pipeline notification recipients'
        )

        stringParam(
            'CC_EMAIL',
            '',
            'CC notification recipients'
        )
    }

    definition {

        cpsScm {

            scm {

                git {

                    remote {
                        url(
                            'https://github.com/brijeshkori-lab/Jenkins-library.git'
                        )
                    }

                    branch('template')
                }
            }

            scriptPath(
                'python-no-sonar-template/Jenkinsfile'
            )

            lightweight(true)
        }
    }
}