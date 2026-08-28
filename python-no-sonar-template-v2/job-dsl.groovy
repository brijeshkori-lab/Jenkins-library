pipelineJob('python-app-v2-with-sonar') {

    description('''
        Enterprise Python V2 CI/CD Pipeline with SonarQube.

        Pipeline:

        GitHub
          -> Python Environment
          -> JFrog Dependency Resolution
          -> Unit Tests + Coverage
          -> Python Package Build
          -> SonarQube
          -> Quality Gate
          -> JFrog Build Information
          -> DEV / PROD Repository Publication
          -> Xray
          -> SBOM
          -> PROD Approval / Release
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