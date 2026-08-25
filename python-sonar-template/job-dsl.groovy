pipelineJob('python-app-with-sonar') {

    description('''
        Enterprise Python CI/CD Pipeline with SonarQube.

        Pipeline flow:

        GitHub
          -> Python environment
          -> JFrog dependency resolution
          -> Unit tests + coverage
          -> SonarQube
          -> SonarQube Quality Gate
          -> Python package build
          -> JFrog Build Info
          -> Xray
          -> SBOM
          -> DEV publish / PROD approval
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
                'python-sonar-template/Jenkinsfile'
            )

            lightweight(true)
        }
    }
}