pipelineJob('python-app-without-sonar-v2') {

    description('''
        Python CI/CD Pipeline without SonarQube.

        Supports multiple Python projects through PROJECT_URL.

        Pipeline:

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

        // =========================================================
        // APPLICATION
        // =========================================================

        stringParam(
            'PROJECT_URL',
            '',
            'Git URL of the Python application'
        )


        // =========================================================
        // PYTHON VERSION
        // =========================================================

        choiceParam(
            'PYTHON_VERSION',
            [
                '3.13'
            ],
            'Python version to use for this build'
        )


        // =========================================================
        // ENVIRONMENT
        // =========================================================

        choiceParam(
            'ENVIRONMENT',
            [
                'DEV',
                'PROD'
            ],
            'Target deployment environment'
        )


        // =========================================================
        // FINAL RUN
        // =========================================================

        choiceParam(
            'IS_FINAL_RUN',
            [
                'false',
                'true'
            ],
            'Set true only for the final PROD release'
        )


        // =========================================================
        // PROD APPROVAL
        // =========================================================

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
                'python-no-sonar-template-v2/Jenkinsfile'
            )


            lightweight(true)
        }
    }
}