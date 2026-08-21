def call() {

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

}