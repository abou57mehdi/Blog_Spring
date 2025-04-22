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

        stage('Deploy to Nexus') {
            steps {
                configFileProvider([configFile(
                    fileId: 'bee134d9-45fb-4afe-80f2-83712d8c1156',
                    variable: 'MAVEN_SETTINGS'
                )]) {
                    bat """
                        mvn -s %MAVEN_SETTINGS% deploy \
                        -DaltDeploymentRepository=nexus-snapshots::default::http://localhost:8081/repository/maven-snapshots/
                    """
                }
            }
        }

        stage('Deploy to Docker') {
            when {
                branch 'master'
            }
            steps {
                echo "Docker build and push"
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-creds',
                    usernameVariable: 'DOCKER_USERNAME',
                    passwordVariable: 'DOCKER_PASSWORD'
                )]) {
                    bat """
                        docker login -u "%DOCKER_USERNAME%" -p "%DOCKER_PASSWORD%"
                        docker build -t aboum22/my-app .
                        docker push aboum22/my-app:latest
                    """
                }
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
