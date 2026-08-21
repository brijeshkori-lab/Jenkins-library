def call() {

    final String jfrogUrl = 'https://trialn4vk2g.jfrog.io'
    final String jfrogRepo = 'python-local'

    stage('Checkout') {
        checkout scm
    }

    stage('Install Dependencies') {
        sh '''
            set -e

            python3 -m venv venv
            . venv/bin/activate

            python -m pip install --upgrade pip
            pip install -r requirements.txt
            pip install build twine
        '''
    }

    stage('Test') {
        sh '''
            set -e

            . venv/bin/activate
            PYTHONPATH=. pytest -v
        '''
    }

    stage('Build Package') {
        sh '''
            set -e

            . venv/bin/activate
            rm -rf dist build *.egg-info
            python -m build
        '''
    }

    stage('Publish to JFrog') {
        withCredentials([
            string(credentialsId: 'jfrog-user', variable: 'JFROG_USER'),
            string(credentialsId: 'jfrog-token', variable: 'JFROG_TOKEN')
        ]) {

            withEnv([
                "JFROG_URL=${jfrogUrl}",
                "JFROG_REPO=${jfrogRepo}"
            ]) {

                sh '''
                    set -e

                    . venv/bin/activate

                    python -m twine upload \
                        --repository-url "${JFROG_URL}/artifactory/api/pypi/${JFROG_REPO}" \
                        -u "$JFROG_USER" \
                        -p "$JFROG_TOKEN" \
                        dist/*
                '''
            }
        }
    }

    stage('Archive Artifacts') {
        archiveArtifacts(
            artifacts: 'dist/*',
            fingerprint: true
        )
    }
}