pipeline {
    agent any
    triggers {
        pollSCM('H * * * *') //polling for changes, here once an hour
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
                bat 'gradlew :api:clean :api:build'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withCredentials([string(credentialsId: 'sonarqube-token', variable: 'SONAR_TOKEN')]) {
                    withSonarQubeEnv('LocalSonar') {
                        bat '''
                        I:\\Sonarqube\\sonar-scanner-4.8.0.2856-windows\\bin\\sonar-scanner.bat ^
                        -Dsonar.projectKey=gift_certificates_system ^
                        -Dsonar.projectName="Gift Certificates System" ^
                        -Dsonar.projectVersion=1.0 ^
                        -Dsonar.sources=api/src,core/src ^
                        -Dsonar.login=%SONAR_TOKEN% ^
                        -Dsonar.java.binaries=api/build/classes/java/main,core/build/classes/java/main ^
                        -Dsonar.tests=core/build/classes/java/main ^
                        -Dsonar.junit.reportsPath=core/build/reports/tests/test ^
                        -Dsonar.coverage.jacoco.xmlReportPaths=build/jacoco/test.exec
                        '''
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'tomcat-manager-script', passwordVariable: 'TOMCAT_PASSWORD', usernameVariable: 'TOMCAT_USER')]) {
                    bat '''
                    curl --upload-file .\\api\\build\\libs\\api-1.war ^
                    "http://%TOMCAT_USER%:%TOMCAT_PASSWORD%@localhost:8080/manager/text/deploy?path=/api&update=true"
                    '''
                }
            }
        }
    }
}
