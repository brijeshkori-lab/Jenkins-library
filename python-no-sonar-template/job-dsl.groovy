pipelineJob('python-app-without-sonar') {

    description('''
        Enterprise Python CI/CD Pipeline without SonarQube.

        Pipeline flow:

        GitHub
          -> Python environment
          -> JFrog dependency resolution
          -> Unit tests + coverage
          -> Python package build
          -> JFrog Build Info
          -> JFrog publish
          -> Xray
          -> SBOM
          -> DEV / PROD release
    ''')

    parameters {

        choiceParam(
            'ENVIRONMENT',
            [
                'DEV',
                'PROD'
            ],
            'Target deployment environment'
        )

        choiceParam(
            'IS_FINAL_RUN',
            [
                'false',
                'true'
            ],
            'Select true only for the final PROD release'
        )

        stringParam(
            'PRIMARY_APPROVER_EMAIL',
            '',
            'Primary PROD approval email address'
        )

        stringParam(
            'MANAGER_EMAIL',
            '',
            'Manager notification email address'
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

                    branch('*/template')
                }
            }

            scriptPath(
                'python-no-sonar-template/Jenkinsfile'
            )

            lightweight(true)
        }
    }
}