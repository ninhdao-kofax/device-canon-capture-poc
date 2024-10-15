#!groovy    
@Library('jenkins-shared-lib')_

import net.printix.pipelines.Stage
import java.text.SimpleDateFormat

def getCurrentDate() {
    def date = new Date()
    def sdf = new SimpleDateFormat("yyyyMMdd")
    return sdf.format(date)
}
def currentQuarter() {
    def date = new Date()
    int quarter = (date.getMonth() / 3) + 1;
    return quarter
}
def isValidParam = true
def isFileCopied = false;
def JENKINS_WORKSPACE = "C:\\jenkins\\workspace"
def DEVICE_CANON_INSTALLER_DIR = "device-canon-installer"

//define a variable to get build number from device-canon-meap-login job
def MEAP_LOGIN_BUILD_NUMBER = ""

//define a package name contains the zip file of global region and china region
def PRINTIX_PRINT_PACKAGE_NAME = ""

pipeline {
    agent {
        node {
            label 'devjk-msvm-1'
        }
    }
    parameters {
        choice(
            name: 'GA_YEAR_RELEASE',
            choices: [ '2025', '2026', '2027', '2028', '2029', '2030', '2031', '2032', '2033', '2034', '2035' ],
            description: 'GA Year Release:'
        )
        choice(
            name: 'QUARTER_RELEASE',
            choices: ['1', '2', '3', '4'],
            description: 'Quarter Release:'
        )
        string defaultValue: '0', description: 'Update:', name: 'UPDATE', trim: true
        string defaultValue: '0', description: 'Fix (eg. FIX12345):', name: 'FIX_NUMBER', trim: true
    }

    environment {
        BUILD_NUMBER_REV = currentBuild.getNumber()
        BUILD_URL_REV = currentBuild.getAbsoluteUrl()
        BUILD_JOB_REV = currentBuild.getProjectName()
        REQUESTER = requestor()
        GITHUB_CREDENTIALS = credentials('printix-automation-github-cli-personal-access-token')
        MEND_PRODUCT_NAME = calculateMendProductName(branch: env.GIT_BRANCH)
        PACKAGE_NAME = "device-canon-meap-print-unsigned.${params.GA_YEAR_RELEASE}.${params.QUARTER_RELEASE}.${params.UPDATE}.${params.FIX_NUMBER}.${env.BUILD_NUMBER_REV}.zip"
        CANON_MEAP_PRINT_FILE = "device-canon-meap-print-${MEAP_LOGIN_BUILD_NUMBER}"
    }
    stages {
        stage('Prepare') {
            steps {
                cleanWs()
                buildDescription("""branch: ${env.GIT_BRANCH}""")
            }

        }
        stage('Download Unified Agent') {
            steps {
                getMendAgent('maven', true)
            }
        }
        stage('read meap-login build version') {
           steps {
               script {
                   MEAP_LOGIN_BUILD_NUMBER = readFile(file: "${JENKINS_WORKSPACE}\\${DEVICE_CANON_INSTALLER_DIR}\\build_version.properties")
                   println(MEAP_LOGIN_BUILD_NUMBER)
               }
           }
       }
        stage ('save meap-login build version') {
            steps {
                script {
                    if (params.MEAPLOGIN_TRIGGER_FLAG == null || params.MEAPLOGIN_TRIGGER_FLAG.isEmpty() || params.MEAPLOGIN_TRIGGER_FLAG.toInteger() != 1) {
                        MEAP_LOGIN_BUILD_NUMBER = (MEAP_LOGIN_BUILD_NUMBER.toInteger() + 1).toString()
                        writeFile(file: "${JENKINS_WORKSPACE}\\${DEVICE_CANON_INSTALLER_DIR}\\build_version.properties", text: MEAP_LOGIN_BUILD_NUMBER)
                        sh "ls -l"
                    }
                }
            }
        }
        stage('verify parameters') {
            steps {
                script {
                    if (params.UPDATE.isEmpty() || params.UPDATE.toInteger() < 0 || params.UPDATE.toInteger() >99){
                        echo "The update number is not empty, less than 0, or greater than 99"
                        isValidParam = false
                    }

                    if (MEAP_LOGIN_BUILD_NUMBER != null) {
                        PRINTIX_PRINT_PACKAGE_NAME = "device-canon-meap-print-${params.GA_YEAR_RELEASE}.${params.QUARTER_RELEASE}.${params.UPDATE}.${params.FIX_NUMBER}.${MEAP_LOGIN_BUILD_NUMBER}_unsigned.zip"
                    } else {
                        PRINTIX_PRINT_PACKAGE_NAME = "device-canon-meap-print-${params.GA_YEAR_RELEASE}.${params.QUARTER_RELEASE}.${params.UPDATE}.${params.FIX_NUMBER}.${env.BUILD_NUMBER_REV}_unsigned.zip"
                    }

                    echo "The GA year release is ${params.GA_YEAR_RELEASE}"
                    echo "The quarter is ${params.QUARTER_RELEASE}"
                    echo "The update number is ${params.UPDATE}"
                    echo "The OOS ID is ${params.FIX_NUMBER}"
                    echo "The build number  ${env.BUILD_NUMBER_REV}"
                    echo "The MEAP_LOGIN_BUILD_NUMBER  ${MEAP_LOGIN_BUILD_NUMBER}"
                    echo "The params.MEAPLOGIN_TRIGGER_FLAG  ${params.MEAPLOGIN_TRIGGER_FLAG}"
                    echo "The PRINTIX_PRINT_PACKAGE_NAME is ${PRINTIX_PRINT_PACKAGE_NAME}"
                }
            }
        }
        stage('checkout') {
            when {
                expression {
                    return isValidParam && shouldExecuteStage(Stage.CHECKOUT, env.GIT_BRANCH)
                }
            }
            steps {
                checkout scm
                script {
                    GIT_COMMIT_REV = sh (script: "git log -n 1 --pretty=format:'%h'", returnStdout: true).trim()
                }
            }
        }
        stage('build project') {
            when {
                expression {
                    return isValidParam && shouldExecuteStage(Stage.BUILD, env.GIT_BRANCH)
                }
            }
            steps {
                withCredentials([ string(credentialsId: 'jenkins-azure-user-maven-token', variable: 'SECRET')]) {
                    writeFile(file: 'gradle.properties', text: """
                        org.gradle.jvmargs=-Xmx2048M
                        azureArtifactsUser=printix
                        azureArtifactsToken=${SECRET}
                    """.stripIndent().trim())
                }
                script {
                    echo "****call gradlew clean assemble test -Pbuild_version=${params.GA_YEAR_RELEASE}.${params.QUARTER_RELEASE}.${params.UPDATE}.${params.FIX_NUMBER}.${MEAP_LOGIN_BUILD_NUMBER}"
                }

                shellex(script:""" 
                    call gradlew clean assemble test -Pbuild_version=${params.GA_YEAR_RELEASE}.${params.QUARTER_RELEASE}.${params.UPDATE}.${params.FIX_NUMBER}.${MEAP_LOGIN_BUILD_NUMBER}
                """)
            }
            post {
                    always {
                        jacoco(
                            execPattern: '**/build/jacoco/*.exec',
                            classPattern: '**/build/classes/java/main',
                            sourcePattern: '**/src/main'
                        )
                    }
            }
        }

        //this stage to remove old version of device-canon-meap-login before do copy new version
        stage('remove old jar version') {
            when {
                expression {
                    return isValidParam && shouldExecuteStage(Stage.PACKAGE, env.GIT_BRANCH)
                }
            }
            steps {
                script {
                    dir("${JENKINS_WORKSPACE}\\${DEVICE_CANON_INSTALLER_DIR}") {
                        fileOperations([fileDeleteOperation(includes: "device-canon-meap-print-*.jar", excludes: "*.zip")])
                    }
                }
            }
        }

        stage('copy jar') {
            when {
                expression {
                    return isValidParam && shouldExecuteStage(Stage.PACKAGE, env.GIT_BRANCH)
                }

            }
            steps {
                echo "Being copy file ${env.CANON_MEAP_PRINT_FILE} into the folder ${JENKINS_WORKSPACE}\\${DEVICE_CANON_INSTALLER_DIR}"
                script {
                    dir('dist') {
                        /* To copy file with file name contains a string such as "CCHN_unsigned" please use this pattern below:
                           fileOperations([fileCopyOperation(includes: "*CCHN_unsigned*.jar", targetLocation: "${JENKINS_WORKSPACE}\\${DEVICE_CANON_INSTALLER_DIR}")])
                        */

                        //Do clone jar files for all regions (except CHINA region) from GLOBAL region file:
                        fileOperations([fileCopyOperation(includes: "*GLOBAL_unsigned*.jar", targetLocation: "dist", flattenFiles: true, renameFiles: true, sourceCaptureExpression: "(GLOBAL_unsigned)?.jar", targetNameExpression: "CAUS_unsigned.jar")])
                        fileOperations([fileCopyOperation(includes: "*GLOBAL_unsigned*.jar", targetLocation: "dist", flattenFiles: true, renameFiles: true, sourceCaptureExpression: "(GLOBAL_unsigned)?.jar", targetNameExpression: "CJPN_unsigned.jar")])
                        fileOperations([fileCopyOperation(includes: "*GLOBAL_unsigned*.jar", targetLocation: "dist", flattenFiles: true, renameFiles: true, sourceCaptureExpression: "(GLOBAL_unsigned)?.jar", targetNameExpression: "CKOR_unsigned.jar")])
                        fileOperations([fileCopyOperation(includes: "*GLOBAL_unsigned*.jar", targetLocation: "dist", flattenFiles: true, renameFiles: true, sourceCaptureExpression: "(GLOBAL_unsigned)?.jar", targetNameExpression: "CUSA_unsigned.jar")])
                        fileOperations([fileCopyOperation(includes: "*GLOBAL_unsigned*.jar", targetLocation: "dist", flattenFiles: true, renameFiles: true, sourceCaptureExpression: "(GLOBAL_unsigned)?.jar", targetNameExpression: "CEL_unsigned.jar")])
                        fileOperations([fileCopyOperation(includes: "*GLOBAL_unsigned*.jar", targetLocation: "dist", flattenFiles: true, renameFiles: true, sourceCaptureExpression: "(GLOBAL_unsigned)?.jar", targetNameExpression: "CSING_unsigned.jar")])
                        echo "the files in dist is:"
                        bat """
                        dir
                        """
                        echo "end of list file in directory"

                        //Do copy to C:\\jenkins\\workspace\\device-canon-installer
                        fileOperations([fileCopyOperation(includes: "*.jar", excludes: "*GLOBAL_unsigned*.jar", targetLocation: "${JENKINS_WORKSPACE}\\${DEVICE_CANON_INSTALLER_DIR}")])
                        isFileCopied = true;
                    }
                }
                echo "The copy file ${env.CANON_MEAP_PRINT_FILE} into the folder ${JENKINS_WORKSPACE}\\${DEVICE_CANON_INSTALLER_DIR} has been completed."
            }
        }
        stage('package') {
            when {
                expression {
                    return isValidParam && shouldExecuteStage(Stage.PACKAGE, env.GIT_BRANCH)
                }
            }
            steps {
                script {
                    dir('dist') {
                        zip archive: true, dir: '', glob: '*.jar', zipFile: "${PRINTIX_PRINT_PACKAGE_NAME}"
                        bat """
                        dir
                        """
                    }
                }
            }
        }
        stage('list file') {
            when {
                expression {
                    return isValidParam && shouldExecuteStage(Stage.PACKAGE, env.GIT_BRANCH)
                }
            }
            steps {
                script {
                    dir("${JENKINS_WORKSPACE}\\${DEVICE_CANON_INSTALLER_DIR}") {
                        echo "the files in ${JENKINS_WORKSPACE}\\${DEVICE_CANON_INSTALLER_DIR} is:"
                        bat """
                        dir
                        """
                        echo "end of list file in directory"
                    }
                }
            }
        }
        stage('package printix-go-canon') {
            when {
                expression {
                    return isValidParam && isFileCopied && shouldExecuteStage(Stage.RELEASE, env.GIT_BRANCH)
                }
            }
            steps {
                // This child-job will package zip the build to the correct version.
                //you can change to any job of device-canon-mp
                script {
                    if (params.MEAPLOGIN_TRIGGER_FLAG == null) {
                        build(job: 'device-canon-meap-login-mp//main',
                            parameters: [
                            string(name: 'GA_YEAR_RELEASE', value: params.GA_YEAR_RELEASE),
                            string(name: 'QUARTER_RELEASE', value: params.QUARTER_RELEASE),
                            string(name: 'UPDATE', value: params.UPDATE),
                            string(name: 'FIX_NUMBER', value: params.FIX_NUMBER),
                            string(name: 'MEAPPRINT_TRIGGER_FLAG', value: "1")
                            ]
                        )
                    }
                }
            }
        }
        stage('Run Unified Agent') {
            when {
                expression {
                    return env.GIT_BRANCH ==~ /(origin\/)?(master|main|release.*|develop.*)/
                }
            }
            steps {
                runMendAgent('maven',  env.MEND_PRODUCT_NAME, env.JOB_NAME)
            }
        }
    }

    post {
        failure {
            emailext body: "${BUILD_URL_REV}",
            recipientProviders: [developers(), requestor()],
            subject: "device-canon-meap-print Build ${BUILD_NUMBER_REV} of ${BUILD_JOB_REV} failed"
        }
    }

}