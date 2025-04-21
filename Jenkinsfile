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
                 withCredentials([usernamePassword(
                     credentialsId: 'nexus-creds',
                     usernameVariable: 'NEXUS_USERNAME',
                     passwordVariable: 'NEXUS_PASSWORD'
                 )]) {
                     // Maven will use your existing settings.xml in .m2
                     bat "set NEXUS_USERNAME=admin && set NEXUS_PASSWORD=admin && mvn clean deploy -DskipTests"
                 }
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