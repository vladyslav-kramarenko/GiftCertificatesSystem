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

        stage('SonarQube Analysis') {
                    steps {
                        withSonarQubeEnv('Sonar') {
                            bat "sonar-scanner.bat ${SONAR_PROPERTIES}"
                        }
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
                bat 'gradlew :api:clean :api:build'
            }
        }

        stage('Deploy') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'tomcat-manager-script', passwordVariable: 'TOMCAT_PASSWORD', usernameVariable: 'TOMCAT_USER')]) {
                    bat '''
                    curl --upload-file .\\api\\build\\libs\\api-1.war "http://%TOMCAT_USER%:%TOMCAT_PASSWORD%@localhost:8080/manager/text/deploy?path=/api&update=true"
                    '''
                }
            }
        }
    }
}
