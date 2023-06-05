pipeline {
    agent any

    parameters {
            string(credentialsId: 'sonarqube-token', variable: 'SONAR_TOKEN')
            file(credentialsId: 'application-dev.properties', variable: 'properties')
        }

    environment {
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
                        bat "copy /Y ${properties} .\\api\\src\\main\\resources\\application-dev.properties"
                    }
                }

        stage('Build') {
            steps {
                bat './gradlew :api:clean :api:build'
            }
        }

        stage('Deploy') {
            steps {
                deploy adapters: [tomcat9(credentialsId: 'tomcat-manager-script',
                  url: 'http://localhost:8080', war: '**/api-1.war', contextPath: 'api-1')]
            }
        }
    }
}
