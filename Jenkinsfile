pipeline {
    agent any

    tools {
        maven 'Maven 3.8.5'  // ou le nom de ton Maven dans Jenkins
        jdk 'Java 17'        // ou Java installé dans Jenkins
    }

    stages {
        steps {
                        echo "Build started"
                        bat 'mvn clean install'  // Use bat for Windows systems
                    }

        stage('Test') {
            steps {
                echo "Running tests"
                sh 'mvn test'
            }
        }

        stage('Package') {
            steps {
                echo "Packaging"
                sh 'mvn package'
            }
        }

        stage('Deploy to Nexus') {
            steps {
                echo "Uploading to Nexus"
                sh 'mvn deploy'
            }
        }

        stage('Deploy to Docker') {
            when {
                branch 'main'
            }
            steps {
                echo "Docker build and push"
                sh 'docker build -t my-app .'
                sh 'docker tag my-app my-dockerhub-username/my-app:latest'
                sh 'docker push my-dockerhub-username/my-app:latest'
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
