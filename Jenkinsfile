pipeline {
    agent any

     triggers {
            pollSCM('* * * * *') //polling for changes, here once a minute
            }

    environment {
        SONAR_TOKEN = credentials('sonarqube-token')
        SONAR_PROPERTIES = '''
            sonar.projectKey=gift_certificates_system
            sonar.projectName=Gift Certificates System
            sonar.projectVersion=1.0
            sonar.sources=api/src,core/src
            sonar.login=${SONAR_TOKEN}
        '''
    }

    stages {
        stage('Checkout') {
                    steps {

                        checkout([$class: 'GitSCM',
                            branches: [[name: '*/main']],
                            doGenerateSubmoduleConfigurations: false,
                            extensions: [],
                            submoduleCfg: [],
                            userRemoteConfigs: [[url: 'https://github.com/vladyslav-kramarenko/GiftCertificatesSystem']]
                        ])
                    }
        }

        stage('Prepare') {
                    steps {
                        echo "Copying credential file to application-dev.properties"
                        withCredentials([file(credentialsId: 'GiftCertificatesSystemDevProperties', variable: 'PROPERTIES')]) {
                                    bat "copy /Y ${PROPERTIES} .\\api\\src\\main\\resources\\application-dev.properties"
                                }
                    }
                }

        stage('Build') {
            steps {
                dir('api') {
                    bat './gradlew :api:clean :api:build'
                }
            }
        }
    }
}
