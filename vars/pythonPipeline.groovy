def call(String jfrogRepo = 'python-local') {

    stage('Checkout') {
        checkout scm
    }

    stage('Install Dependencies') {
        sh '''
            python3 -m venv venv
            . venv/bin/activate

            pip install -r requirements.txt
            pip install build twine
        '''
    }

    stage('Test') {
        sh '''
            . venv/bin/activate
            PYTHONPATH=. pytest
        '''
    }

    stage('Build Package') {
        sh '''
            . venv/bin/activate
            python -m build
        '''
    }

    stage('Publish to JFrog') {
        withCredentials([
            string(credentialsId: 'jfrog-user', variable: 'JFROG_USER'),
            string(credentialsId: 'jfrog-token', variable: 'JFROG_TOKEN')
        ]) {
            sh """
                . venv/bin/activate

                python -m twine upload \
                  --repository-url "https://trialn4vk2g.jfrog.io/artifactory/api/pypi/${jfrogRepo}" \
                  -u "\$JFROG_USER" \
                  -p "\$JFROG_TOKEN" \
                  dist/*
            """
        }
    }
}