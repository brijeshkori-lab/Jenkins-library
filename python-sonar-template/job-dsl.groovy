pipelineJob('python-app-with-sonar') {

    description('''
        Production Python CI Pipeline with SonarQube.

        This job is generated using Jenkins Job DSL.
        The pipeline implementation is maintained in GitHub.
    ''')

    parameters {

        stringParam(
            'PROJECT_URL',
            '',
            'Git URL of the Python application'
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

        stringParam(
            'SONAR_PROJECT_KEY',
            'python-app',
            'SonarQube project key'
        )

        stringParam(
            'SONAR_PROJECT_NAME',
            'python-app ',
            'SonarQube project name'
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