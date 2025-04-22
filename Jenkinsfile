pipeline {
    agent any

    tools {
        maven 'Maven 3.8.5'
        jdk 'Java 17'
    }

    stages {
        stage('SCM Checkout') {
            steps {
                echo "Checking out source code from repository"
                checkout scm
            }
        }

        stage('Build') {
            parallel {
                stage('Maven Build') {
                    steps {
                        echo "Building with Maven"
                        bat 'mvn clean compile'
                    }
                }
            }
        }

        stage('Test') {
            parallel {
                stage('Unit Tests') {
                    steps {
                        echo "Running unit tests"
                        bat 'mvn test'
                    }
                    post {
                        always {
                            junit '**/target/surefire-reports/*.xml'
                        }
                    }
                }
                
                stage('Code Analysis') {
                    steps {
                        echo "Running code analysis"
                        bat 'mvn checkstyle:checkstyle pmd:pmd'
                    }
                }
            }
        }

        stage('JavaDoc') {
            steps {
                echo "Generating JavaDoc"
                bat 'mvn javadoc:javadoc'
            }
        }

        stage('Package') {
            steps {
                echo "Packaging application"
                bat 'mvn package -DskipTests'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('Deploy to Nexus') {
            steps {
                echo "Deploying to Nexus repository"
                configFileProvider([configFile(
                    fileId: 'bee134d9-45fb-4afe-80f2-83712d8c1156',
                    variable: 'MAVEN_SETTINGS'
                )]) {
                    bat """
                        mvn -s %MAVEN_SETTINGS% deploy \
                        -DaltDeploymentRepository=nexus-snapshots::default::http://localhost:8081/repository/maven-snapshots/ \
                        -DskipTests
                    """
                }
            }
        }

        stage('Deploy to Docker') {
            steps {
                echo "Building and pushing Docker image"
                script {
                    try {
                        withCredentials([usernamePassword(
                            credentialsId: 'dockerhub-creds',
                            usernameVariable: 'DOCKER_USERNAME',
                            passwordVariable: 'DOCKER_PASSWORD'
                        )]) {
                            bat """
                                docker login -u "%DOCKER_USERNAME%" -p "%DOCKER_PASSWORD%"
                                docker build -t aboum22/blog-spring:latest .
                                docker push aboum22/blog-spring:latest
                            """
                        }
                    } catch (Exception e) {
                        echo "Docker deployment failed: ${e.message}"
                        currentBuild.result = 'UNSTABLE'
                        // Continue pipeline even if Docker step fails
                    }
                }
            }
        }
    }

    post {
        success {
            echo "Pipeline completed successfully!"
        }
        failure {
            mail to: 'aboumehdi57@gmail.com',
                 subject: "Échec du build #${env.BUILD_NUMBER}",
                 body: "Le build a échoué. Vérifiez Jenkins : ${env.BUILD_URL}"
        }
        always {
            echo "Pipeline execution completed."
        }
    }
}
