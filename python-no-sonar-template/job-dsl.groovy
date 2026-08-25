pipelineJob('python-app-without-sonar') {

    description('''
        Production Python CI/CD Pipeline without SonarQube.

        Pipeline:

        GitHub
          -> Python environment
          -> JFrog python-virtual dependencies
          -> Tests
          -> Python package
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
                'python-no-sonar-template/Jenkinsfile'
            )

            lightweight(true)
        }
    }
}