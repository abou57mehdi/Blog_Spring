pipeline {
    agent any

    tools {
        maven 'Maven 3.8.5'
        jdk 'Java 17'
    }

    stages {
        stage('Build') {
            steps {
                echo "Build started"
                bat 'mvn clean install'
            }
        }

        stage('Test') {
            steps {
                echo "Running tests"
                bat 'mvn test'
            }
        }

        stage('Package') {
            steps {
                echo "Packaging"
                bat 'mvn package'
            }
        }

        stage('Test Nexus Connection') {
            steps {
                bat '''
                    curl -u admin:admin -X GET http://localhost:8081/service/rest/v1/status
                    echo %ERRORLEVEL%
                '''
            }
        }

        stage('Deploy to Nexus') {
            steps {
                bat """
                    echo ^<settings^> > settings.xml
                    echo   ^<servers^> >> settings.xml
                    echo     ^<server^> >> settings.xml
                    echo       ^<id^>nexus-snapshots^</id^> >> settings.xml
                    echo       ^<username^>admin^</username^> >> settings.xml
                    echo       ^<password^>admin^</password^> >> settings.xml
                    echo     ^</server^> >> settings.xml
                    echo   ^</servers^> >> settings.xml
                    echo ^</settings^> >> settings.xml

                    mvn deploy -s settings.xml -DaltDeploymentRepository=nexus-snapshots::default::http://localhost:8081/repository/maven-snapshots/

                    del settings.xml
                """
            }
        }


        stage('Deploy to Docker') {
            when {
                branch 'main'
            }
            steps {
                echo "Docker build and push"
                bat 'docker build -t my-app .'
                bat 'docker tag my-app my-dockerhub-username/my-app:latest'
                bat 'docker push my-dockerhub-username/my-app:latest'
            }
        }
    }

    post {
        failure {
            mail to: 'aboumehdi57@gmail.com',
                 subject: "Échec du build #${env.BUILD_NUMBER}",
                 body: "Le build a échoué. Vérifiez Jenkins : ${env.BUILD_URL}"
        }
    }
}