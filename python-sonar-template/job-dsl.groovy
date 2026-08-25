pipelineJob('python-app-with-sonar') {

    description('''
        Production Python CI/CD Pipeline with SonarQube and JFrog.

        Pipeline:

        GitHub
          -> Python environment
          -> JFrog python-virtual dependencies
          -> Tests
          -> SonarQube
          -> Quality Gate
          -> Package
          -> DEV: automatic JFrog publish
          -> PROD: approval -> JFrog publish
          -> JFrog Build Info
    ''')

    parameters {

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

        choiceParam(
            'PYTHON_VERSION',
            [
                'python3'
            ],
            'Python executable available on Jenkins agent'
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
            'Must be true for PROD execution'
        )

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