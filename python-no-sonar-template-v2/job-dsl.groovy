pipelineJob('python-app-v2-with-sonar') {

    description('''
        Python V2 Enterprise CI/CD Pipeline.

        Pipeline:

        GitHub
          -> Python Environment
          -> JFrog Dependency Resolution
          -> Unit Tests + Coverage
          -> Python Package Build
          -> SonarQube
          -> Quality Gate
          -> Artifactory Project / Repository
          -> JFrog Build Info
          -> Artifact Publication
          -> Xray
          -> SBOM
          -> DEV / PROD Release
    ''')


    parameters {

        stringParam(
            'PROJECT_URL',
            '',
            'GitHub repository URL of the Python application'
        )

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

        stringParam(
            'TO_EMAIL',
            '',
            'Pipeline notification recipients'
        )

        stringParam(
            'CC_EMAIL',
            '',
            'Pipeline notification CC recipients'
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
                'python-v2-template/Jenkinsfile'
            )


            lightweight(true)
        }
    }
}