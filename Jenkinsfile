pipeline {
    agent any
    tools {
        // Assuming you have defined a gradle installation with the name 'gradle'
        gradle 'gradle'
    }
    environment {
        // Define environment variable from Jenkins credentials
        SONAR_TOKEN = credentials('sonarqube-token')
    }
    stages {
        stage('Checkout') {
            steps {
                // Checkout code from Git repository
                checkout([$class: 'GitSCM', branches: [[name: '*/main']],
                  userRemoteConfigs: [[url: 'https://github.com/vladyslav-kramarenko/GiftCertificatesSystem']]])
            }
        }
        stage('Copy File') {
            steps {
                script {
                    withCredentials([file(credentialsId: 'gift-certificate.properties', variable: 'PROP_FILE')]) {
                        bat 'xcopy %PROP_FILE% api\\src\\main\\resources\\application-dev.properties'
                    }
                }
            }
        }
        stage('Build') {
            steps {
                // Build the project with Gradle
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
