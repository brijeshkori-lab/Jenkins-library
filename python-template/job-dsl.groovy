pipelineJob('Python-App-With-Sonar') {

    description("""
        Reusable Python Application Pipeline Template

        Source:
        Jenkins-library/template

        Pipeline:
        python-template/Jenkinsfile
    """)

    logRotator {
        numToKeep(20)
        artifactNumToKeep(10)
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
                'python-template/Jenkinsfile'
            )

            lightweight(true)
        }
    }
}