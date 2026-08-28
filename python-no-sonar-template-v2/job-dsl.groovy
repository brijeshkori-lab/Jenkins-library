pipelineJob('python-app-v2-without-sonar') {

    description('''
        Enterprise Python V2 CI/CD Pipeline without SonarQube.

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

                    // KEEP THIS EXACTLY AS THE WORKING VERSION
                    branch('*/template')
                }
            }

            // ONLY THIS PATH CHANGES FOR V2
            scriptPath(
                'python-no-sonar-template-v2/Jenkinsfile'
            )

            lightweight(true)
        }
    }
}