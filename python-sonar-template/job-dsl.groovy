pipelineJob('python-app-with-sonar') {

    description('''
        Production Python CI/CD Pipeline with SonarQube and JFrog.

        This job is generated using Jenkins Job DSL.
        The pipeline implementation is maintained in GitHub.
    ''')

    // =========================================================
    // PARAMETERS
    // =========================================================

    parameters {

        // -----------------------------------------------------
        // GIT
        // -----------------------------------------------------

        stringParam(
            'GIT_URL',
            '',
            'Git URL of the Python application'
        )

        stringParam(
            'GIT_BRANCH',
            'main',
            'Git branch to build'
        )


        // -----------------------------------------------------
        // PYTHON
        // -----------------------------------------------------

        choiceParam(
            'PYTHON_VERSION',
            [
                'python3'
            ],
            'Python executable available on the Jenkins agent'
        )


        // -----------------------------------------------------
        // ENVIRONMENT
        // -----------------------------------------------------

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
            'Must be true for PROD execution'
        )


        // -----------------------------------------------------
        // PYTHON BUILD
        // -----------------------------------------------------

        stringParam(
            'REQUIREMENTS_FILE',
            'requirements.txt',
            'Python dependency file'
        )

        stringParam(
            'TEST_COMMAND',
            'pytest -v',
            'Python test command'
        )


        // -----------------------------------------------------
        // SONARQUBE
        // -----------------------------------------------------

        stringParam(
            'SONAR_PROJECT_KEY',
            'python-app',
            'SonarQube project key'
        )

        stringParam(
            'SONAR_PROJECT_NAME',
            'python-app',
            'SonarQube project name'
        )


        // -----------------------------------------------------
        // PROD APPROVAL
        // -----------------------------------------------------

        stringParam(
            'PRIMARY_APPROVER_EMAIL',
            '',
            'Email address of the PROD approver'
        )

        stringParam(
            'MANAGER_EMAIL',
            '',
            'Manager email for PROD notification'
        )
    }


    // =========================================================
    // PIPELINE DEFINITION
    // =========================================================

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