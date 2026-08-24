pipelineJob('python-app-with-sonar') {

    description('''
        Production Python CI/CD Pipeline with SonarQube and JFrog.

        This job is generated using Jenkins Job DSL.
        The pipeline implementation is maintained in GitHub.
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
                'python3.8',
                'python3.9',
                'python3.10',
                'python3.11',
                'python3.12'
            ],
            'Python version to use for the build'
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
            'Set true only for an approved PROD execution'
        )

        stringParam(
            'REQUIREMENTS_FILE',
            'requirements.txt',
            'Python dependency file'
        )

        stringParam(
            'TEST_COMMAND',
            'pytest',
            'Command used to execute Python tests'
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
            'Email address of the PROD approver'
        )

        stringParam(
            'MANAGER_EMAIL',
            '',
            'Manager email for PROD notification'
        )
    }

    definition {

        cpsScm {

            scm {

                git {

                    remote {
                        url('https://github.com/brijeshkori-lab/Jenkins-library.git')
                    }

                    branch('*/template')
                }
            }

            scriptPath('python-sonar-template/Jenkinsfile')

            lightweight(true)
        }
    }
}