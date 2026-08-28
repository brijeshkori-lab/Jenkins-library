pipelineJob('python-app-v2-without-sonar') {

    description('''
        Enterprise Python V2 CI/CD Pipeline without SonarQube.

        Pipeline flow:

        Application Git
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

        /*
         * Application repository.
         *
         * This is NOT the Jenkins-library repository.
         *
         * Example:
         * https://github.com/brijeshkori-lab/python-app-template
         */
        stringParam(
            'PROJECT_URL',
            '',
            'Git URL of the Python application'
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

                    /*
                     * This is the TEMPLATE repository branch.
                     *
                     * Keep this exactly as it is.
                     */
                    branch('*/template')
                }
            }

            /*
             * This is the V2 Jenkinsfile inside Jenkins-library.
             */
            scriptPath(
                'python-no-sonar-template-v2/Jenkinsfile'
            )

            lightweight(true)
        }
    }
}