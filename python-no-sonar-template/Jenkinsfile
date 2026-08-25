pipeline {

    agent any

    parameters {

        // =====================================================
        // GIT
        // =====================================================

        string(
            name: 'GIT_URL',
            defaultValue: '',
            description: 'Git URL of the Python application'
        )

        string(
            name: 'GIT_BRANCH',
            defaultValue: 'main',
            description: 'Git branch to build'
        )


        // =====================================================
        // PYTHON
        // =====================================================

        choice(
            name: 'PYTHON_VERSION',
            choices: [
                'python3'
            ],
            description: 'Python executable available on Jenkins agent'
        )


        // =====================================================
        // ENVIRONMENT
        // =====================================================

        choice(
            name: 'ENVIRONMENT',
            choices: [
                'DEV',
                'PROD'
            ],
            description: 'Target environment'
        )

        choice(
            name: 'IS_FINAL_RUN',
            choices: [
                'false',
                'true'
            ],
            description: 'Must be true for PROD execution'
        )


        // =====================================================
        // PYTHON BUILD
        // =====================================================

        string(
            name: 'REQUIREMENTS_FILE',
            defaultValue: 'requirements.txt',
            description: 'Python dependency file'
        )

        string(
            name: 'TEST_COMMAND',
            defaultValue: 'pytest -v',
            description: 'Python test command'
        )


        // =====================================================
        // PROD APPROVAL
        // =====================================================

        string(
            name: 'PRIMARY_APPROVER_EMAIL',
            defaultValue: '',
            description: 'Email address of PROD approver'
        )

        string(
            name: 'MANAGER_EMAIL',
            defaultValue: '',
            description: 'Manager email'
        )
    }


    environment {

        // =====================================================
        // JFROG
        // =====================================================

        JFROG_URL = 'https://trialn4vk2g.jfrog.io'

        JFROG_VIRTUAL_REPO = 'python-virtual'

        JFROG_LOCAL_REPO = 'python-local'

        JFROG_SERVER = 'python-server'


        // =====================================================
        // JFROG BUILD INFO
        // =====================================================

        JFROG_CLI_BUILD_NAME = 'python-app-no-sonar'

        JFROG_CLI_BUILD_NUMBER = "${BUILD_NUMBER}"


        // =====================================================
        // PYTHON
        // =====================================================

        VENV_DIR = '.venv'

        PACKAGE_DIR = 'dist'
    }


    stages {


        // =====================================================
        // 1. VALIDATE PARAMETERS
        // =====================================================

        stage('Validate Parameters') {

            steps {

                script {

                    if (!params.GIT_URL?.trim()) {
                        error('GIT_URL is required.')
                    }

                    if (!params.GIT_BRANCH?.trim()) {
                        error('GIT_BRANCH is required.')
                    }


                    // -------------------------------------------------
                    // PROD safety
                    // -------------------------------------------------

                    if (
                        params.ENVIRONMENT == 'PROD' &&
                        params.IS_FINAL_RUN != 'true'
                    ) {

                        error("""
====================================================
PRODUCTION EXECUTION BLOCKED
====================================================

Environment : PROD
Final Run   : ${params.IS_FINAL_RUN}

PROD requires:

ENVIRONMENT = PROD
IS_FINAL_RUN = true

====================================================
""")
                    }


                    if (
                        params.IS_FINAL_RUN == 'true' &&
                        params.ENVIRONMENT != 'PROD'
                    ) {

                        error("""
====================================================
FINAL RUN BLOCKED
====================================================

IS_FINAL_RUN = true

But ENVIRONMENT is:

${params.ENVIRONMENT}

IS_FINAL_RUN=true is allowed only for PROD.

====================================================
""")
                    }


                    // -------------------------------------------------
                    // PROD approval validation
                    // -------------------------------------------------

                    if (params.ENVIRONMENT == 'PROD') {

                        if (!params.PRIMARY_APPROVER_EMAIL?.trim()) {

                            error(
                                'PRIMARY_APPROVER_EMAIL is required for PROD.'
                            )
                        }

                        if (!params.MANAGER_EMAIL?.trim()) {

                            error(
                                'MANAGER_EMAIL is required for PROD.'
                            )
                        }
                    }


                    echo """
====================================================
PYTHON + JFROG PIPELINE
====================================================

Git URL              : ${params.GIT_URL}
Git Branch           : ${params.GIT_BRANCH}

Python Version       : ${params.PYTHON_VERSION}

Environment          : ${params.ENVIRONMENT}
Final Run            : ${params.IS_FINAL_RUN}

Requirements File    : ${params.REQUIREMENTS_FILE}
Test Command         : ${params.TEST_COMMAND}

JFrog URL            : ${env.JFROG_URL}
JFrog Resolve Repo   : ${env.JFROG_VIRTUAL_REPO}
JFrog Deploy Repo    : ${env.JFROG_LOCAL_REPO}

JFrog Build Name     : ${env.JFROG_CLI_BUILD_NAME}
JFrog Build Number   : ${env.JFROG_CLI_BUILD_NUMBER}

SonarQube            : DISABLED

====================================================
"""
                }
            }
        }


        // =====================================================
        // 2. CHECK PYTHON
        // =====================================================

        stage('Check Python') {

            steps {

                sh '''
                    set -e

                    echo "======================================"
                    echo "CHECKING PYTHON"
                    echo "======================================"

                    command -v "${PYTHON_VERSION}"

                    "${PYTHON_VERSION}" --version

                    "${PYTHON_VERSION}" -m pip --version

                    echo "======================================"
                    echo "PYTHON CHECK PASSED"
                    echo "======================================"
                '''
            }
        }


        // =====================================================
        // 3. CONFIGURE JFROG
        // =====================================================

        stage('Configure JFrog') {

            steps {

                withCredentials([

                    string(
                        credentialsId: 'jfrog-user',
                        variable: 'JFROG_USER'
                    ),

                    string(
                        credentialsId: 'jfrog-token',
                        variable: 'JFROG_TOKEN'
                    )

                ]) {

                    sh '''
                        set -e

                        echo "======================================"
                        echo "CONFIGURING JFROG CLI"
                        echo "======================================"

                        jf config rm "${JFROG_SERVER}" --quiet || true

                        jf config add "${JFROG_SERVER}" \
                            --url="${JFROG_URL}" \
                            --user="${JFROG_USER}" \
                            --access-token="${JFROG_TOKEN}" \
                            --interactive=false \
                            --overwrite=true

                        echo "Testing JFrog connection..."

                        jf rt ping \
                            --server-id="${JFROG_SERVER}"

                        echo "JFrog connection successful."

                        echo "Resolve repository:"
                        echo "${JFROG_VIRTUAL_REPO}"

                        echo "Deploy repository:"
                        echo "${JFROG_LOCAL_REPO}"

                        echo "======================================"
                    '''
                }
            }
        }


        // =====================================================
        // 4. CHECKOUT
        // =====================================================

        stage('Checkout') {

            steps {

                deleteDir()

                git(
                    url: params.GIT_URL,
                    branch: params.GIT_BRANCH
                )
            }
        }


        // =====================================================
        // 5. CREATE VIRTUAL ENVIRONMENT
        // =====================================================

        stage('Create Virtual Environment') {

            steps {

                sh '''
                    set -e

                    echo "======================================"
                    echo "CREATING PYTHON VIRTUAL ENVIRONMENT"
                    echo "======================================"

                    rm -rf "${VENV_DIR}"

                    "${PYTHON_VERSION}" -m venv "${VENV_DIR}"

                    echo "Python:"
                    "${VENV_DIR}/bin/python" --version

                    echo "Pip:"
                    "${VENV_DIR}/bin/python" -m pip --version

                    echo "Installing build tooling..."

                    "${VENV_DIR}/bin/python" -m pip install \
                        --upgrade \
                        pip \
                        setuptools \
                        wheel \
                        build

                    echo "Installing pytest..."

                    "${VENV_DIR}/bin/python" -m pip install pytest

                    echo "pytest:"
                    "${VENV_DIR}/bin/python" -m pytest --version

                    echo "======================================"
                    echo "VIRTUAL ENVIRONMENT READY"
                    echo "======================================"
                '''
            }
        }


        // =====================================================
        // 6. INSTALL DEPENDENCIES FROM JFROG
        // =====================================================

        stage('Install Dependencies from JFrog') {

            steps {

                withCredentials([

                    string(
                        credentialsId: 'jfrog-user',
                        variable: 'JFROG_USER'
                    ),

                    string(
                        credentialsId: 'jfrog-token',
                        variable: 'JFROG_TOKEN'
                    )

                ]) {

                    sh '''
                        set -e

                        echo "======================================"
                        echo "INSTALLING DEPENDENCIES FROM JFROG"
                        echo "======================================"

                        PYTHON="${VENV_DIR}/bin/python"

                        if [ ! -f "${REQUIREMENTS_FILE}" ]; then

                            echo "No ${REQUIREMENTS_FILE} found."

                            exit 0

                        fi


                        PIP_CONFIG="${WORKSPACE}/${VENV_DIR}/pip.conf"

                        cleanup() {
                            rm -f "${PIP_CONFIG}"
                        }

                        trap cleanup EXIT


                        cat > "${PIP_CONFIG}" <<EOF
[global]
index-url = https://${JFROG_USER}:${JFROG_TOKEN}@${JFROG_URL}/artifactory/${JFROG_VIRTUAL_REPO}/simple
EOF

                        export PIP_CONFIG_FILE="${PIP_CONFIG}"


                        echo "Python:"
                        "${PYTHON}" --version

                        echo "Pip:"
                        "${PYTHON}" -m pip --version

                        echo "Installing dependencies..."

                        "${PYTHON}" -m pip install \
                            -r "${REQUIREMENTS_FILE}"


                        echo "======================================"
                        echo "DEPENDENCIES INSTALLED SUCCESSFULLY"
                        echo "======================================"
                    '''
                }
            }
        }


        // =====================================================
        // 7. TEST
        // =====================================================

        stage('Test') {

            steps {

                sh '''
                    set -e

                    echo "======================================"
                    echo "RUNNING PYTHON TESTS"
                    echo "======================================"

                    PYTHON="${VENV_DIR}/bin/python"

                    export PATH="${VENV_DIR}/bin:${PATH}"

                    export PYTHONPATH="${WORKSPACE}:${PYTHONPATH:-}"

                    echo "Python:"
                    "${PYTHON}" --version

                    echo "Pytest:"
                    "${PYTHON}" -m pytest --version

                    echo "PYTHONPATH:"
                    echo "${PYTHONPATH}"

                    echo "Test command:"
                    echo "${TEST_COMMAND}"

                    ${TEST_COMMAND}

                    echo "======================================"
                    echo "PYTHON TESTS PASSED"
                    echo "======================================"
                '''
            }
        }


        // =====================================================
        // 8. BUILD PYTHON PACKAGE
        // =====================================================

        stage('Build Python Package') {

            steps {

                sh '''
                    set -e

                    echo "======================================"
                    echo "BUILDING PYTHON PACKAGE"
                    echo "======================================"

                    PYTHON="${VENV_DIR}/bin/python"

                    rm -rf "${PACKAGE_DIR}"

                    "${PYTHON}" -m build

                    echo "======================================"
                    echo "BUILD OUTPUT"
                    echo "======================================"

                    ls -lah "${PACKAGE_DIR}"

                    echo "======================================"
                    echo "PACKAGE BUILD COMPLETED"
                    echo "======================================"
                '''
            }
        }


        // =====================================================
        // 9. READ PACKAGE VERSION
        // =====================================================

        stage('Read Package Version') {

            steps {

                script {

                    def wheelFile = sh(

                        script: """
                            ls ${PACKAGE_DIR}/*.whl
                        """,

                        returnStdout: true
                    ).trim()


                    if (!wheelFile) {

                        error(
                            'No Python wheel found in dist/.'
                        )
                    }


                    env.PYTHON_PACKAGE_FILE = wheelFile


                    echo """
====================================================
PYTHON PACKAGE
====================================================

${wheelFile}

====================================================
"""
                }
            }
        }


        // =====================================================
        // 10. PROD APPROVAL
        // =====================================================

        stage('PROD Approval') {

            when {

                expression {

                    params.ENVIRONMENT == 'PROD'
                }
            }


            steps {

                script {

                    echo """
====================================================
PRODUCTION APPROVAL REQUIRED
====================================================

Project       : ${params.GIT_URL}
Build         : ${BUILD_NUMBER}
Environment   : PROD

Package       : ${env.PYTHON_PACKAGE_FILE}

Testing       : PASSED

The package will NOT be published until
production approval is granted.

====================================================
"""


                    // -------------------------------------------------
                    // Approval email
                    // -------------------------------------------------

                    mail(

                        to: params.PRIMARY_APPROVER_EMAIL,

                        subject:
                            "PROD APPROVAL REQUIRED - Python #${BUILD_NUMBER}",

                        mimeType: 'text/html',

                        body: """

                            <h2>Production Approval Required</h2>

                            <p>
                                <b>Application:</b>
                                ${params.GIT_URL}
                            </p>

                            <p>
                                <b>Build:</b>
                                ${BUILD_NUMBER}
                            </p>

                            <p>
                                <b>Environment:</b>
                                PROD
                            </p>

                            <p>
                                <b>Package:</b>
                                ${env.PYTHON_PACKAGE_FILE}
                            </p>

                            <p>
                                <b>Tests:</b>
                                PASSED
                            </p>

                            <p>
                                <b>Jenkins Build:</b>
                                <a href="${BUILD_URL}">
                                    ${BUILD_URL}
                                </a>
                            </p>

                            <p>
                                Please review and approve the
                                production release in Jenkins.
                            </p>
                        """
                    )


                    // -------------------------------------------------
                    // Jenkins approval
                    // -------------------------------------------------

                    def approver = input(

                        message: """

====================================================
PRODUCTION RELEASE APPROVAL
====================================================

Application:
${params.GIT_URL}

Build:
${BUILD_NUMBER}

Environment:
PROD

Package:
${env.PYTHON_PACKAGE_FILE}

Tests:
PASSED

The next stage will publish this package
to JFrog python-local.

====================================================

""",

                        ok: 'APPROVE PROD RELEASE',

                        submitter:
                            'admin',

                        submitterParameter:
                            'APPROVER'
                    )


                    env.APPROVED_BY = approver


                    echo """
====================================================
PRODUCTION RELEASE APPROVED
====================================================

Approved By : ${approver}
Build       : ${BUILD_NUMBER}

====================================================
"""


                    // -------------------------------------------------
                    // Manager notification
                    // -------------------------------------------------

                    mail(

                        to: params.MANAGER_EMAIL,

                        subject:
                            "PROD RELEASE APPROVED - Python #${BUILD_NUMBER}",

                        mimeType: 'text/html',

                        body: """

                            <h2>Production Release Approved</h2>

                            <p>
                                <b>Application:</b>
                                ${params.GIT_URL}
                            </p>

                            <p>
                                <b>Build:</b>
                                ${BUILD_NUMBER}
                            </p>

                            <p>
                                <b>Approved By:</b>
                                ${approver}
                            </p>

                            <p>
                                <b>Package:</b>
                                ${env.PYTHON_PACKAGE_FILE}
                            </p>

                            <p>
                                <b>Jenkins Build:</b>
                                <a href="${BUILD_URL}">
                                    ${BUILD_URL}
                                </a>
                            </p>

                        """
                    )
                }
            }
        }


        // =====================================================
        // 11. PUBLISH TO JFROG
        // =====================================================

        stage('Publish Package to JFrog') {

            when {

                expression {

                    params.ENVIRONMENT == 'DEV' ||
                    params.ENVIRONMENT == 'PROD'
                }
            }


            steps {

                withCredentials([

                    string(
                        credentialsId: 'jfrog-user',
                        variable: 'JFROG_USER'
                    ),

                    string(
                        credentialsId: 'jfrog-token',
                        variable: 'JFROG_TOKEN'
                    )

                ]) {

                    sh '''
                        set -e

                        echo "======================================"
                        echo "PUBLISHING PYTHON PACKAGE TO JFROG"
                        echo "======================================"

                        echo "Environment:"
                        echo "${ENVIRONMENT}"

                        echo "Repository:"
                        echo "${JFROG_LOCAL_REPO}"

                        echo "Package:"
                        echo "${PYTHON_PACKAGE_FILE}"


                        # -------------------------------------------------
                        # Configure JFrog
                        # -------------------------------------------------

                        jf config rm "${JFROG_SERVER}" --quiet || true

                        jf config add "${JFROG_SERVER}" \
                            --url="${JFROG_URL}" \
                            --user="${JFROG_USER}" \
                            --access-token="${JFROG_TOKEN}" \
                            --interactive=false \
                            --overwrite=true


                        # -------------------------------------------------
                        # Verify JFrog connection
                        # -------------------------------------------------

                        echo "======================================"
                        echo "VERIFYING JFROG CONNECTION"
                        echo "======================================"

                        jf rt ping \
                            --server-id="${JFROG_SERVER}"


                        # -------------------------------------------------
                        # Package files
                        # -------------------------------------------------

                        echo "======================================"
                        echo "PACKAGE FILES"
                        echo "======================================"

                        ls -lah "${PACKAGE_DIR}"


                        # -------------------------------------------------
                        # Upload wheel
                        # -------------------------------------------------

                        echo "======================================"
                        echo "UPLOADING WHEEL"
                        echo "======================================"

                        jf rt upload \
                            "${PACKAGE_DIR}/*.whl" \
                            "${JFROG_LOCAL_REPO}/" \
                            --server-id="${JFROG_SERVER}" \
                            --flat=true


                        # -------------------------------------------------
                        # Upload source distribution
                        # -------------------------------------------------

                        echo "======================================"
                        echo "UPLOADING SOURCE DISTRIBUTION"
                        echo "======================================"

                        if ls "${PACKAGE_DIR}"/*.tar.gz >/dev/null 2>&1; then

                            jf rt upload \
                                "${PACKAGE_DIR}/*.tar.gz" \
                                "${JFROG_LOCAL_REPO}/" \
                                --server-id="${JFROG_SERVER}" \
                                --flat=true

                        else

                            echo "No .tar.gz file found."

                        fi


                        # -------------------------------------------------
                        # Publish Build Info
                        # -------------------------------------------------

                        echo "======================================"
                        echo "PUBLISHING JFROG BUILD INFO"
                        echo "======================================"

                        jf rt build-publish \
                            "${JFROG_CLI_BUILD_NAME}" \
                            "${JFROG_CLI_BUILD_NUMBER}" \
                            --server-id="${JFROG_SERVER}"


                        echo "======================================"
                        echo "JFROG PUBLISH COMPLETED"
                        echo "======================================"

                        echo "Build Name:"
                        echo "${JFROG_CLI_BUILD_NAME}"

                        echo "Build Number:"
                        echo "${JFROG_CLI_BUILD_NUMBER}"

                        echo "Repository:"
                        echo "${JFROG_LOCAL_REPO}"

                        echo "======================================"
                    '''
                }
            }
        }
    }


    post {

        success {

            echo """
====================================================
PYTHON + JFROG PIPELINE SUCCESS
====================================================

Environment : ${params.ENVIRONMENT}
Build       : ${BUILD_NUMBER}

JFrog Build : ${env.JFROG_CLI_BUILD_NAME}
JFrog Number: ${env.JFROG_CLI_BUILD_NUMBER}

SonarQube   : NOT CONFIGURED

====================================================
"""
        }


        failure {

            echo """
====================================================
PYTHON + JFROG PIPELINE FAILED
====================================================

Environment : ${params.ENVIRONMENT}
Build       : ${BUILD_NUMBER}

Please check the failed stage.

====================================================
"""
        }


        always {

            sh '''
                rm -rf .venv || true
                rm -rf __pycache__ || true
                rm -rf .pytest_cache || true
            '''
        }
    }
}