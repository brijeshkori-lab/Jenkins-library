pipelineJob('python-app-with-sonar') {

    description('''
        Production Python CI/CD Pipeline with SonarQube and JFrog.

        Pipeline flow:

        GitHub
          -> Python Environment
          -> JFrog Remote/Virtual Dependencies
          -> Tests
          -> SonarQube
          -> Quality Gate
          -> Python Package
          -> DEV JFrog Publish

        PROD approval and publishing will be enabled
        after DEV publishing is validated.
    ''')


    parameters {

        // =====================================================
        // GIT
        // =====================================================

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


        // =====================================================
        // PYTHON
        // =====================================================

        choiceParam(
            'PYTHON_VERSION',
            [
                'python3'
            ],
            'Python executable available on Jenkins agent'
        )


        // =====================================================
        // ENVIRONMENT
        // =====================================================

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


        // =====================================================
        // PYTHON BUILD
        // =====================================================

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


        // =====================================================
        // SONARQUBE
        // =====================================================

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


        // =====================================================
        // PROD APPROVAL
        // =====================================================

        stringParam(
            'PRIMARY_APPROVER_EMAIL',
            '',
            'Email address of PROD approver'
        )

        stringParam(
            'MANAGER_EMAIL',
            '',
            'Manager email'
        )
    }


    // =========================================================
    // PIPELINE
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