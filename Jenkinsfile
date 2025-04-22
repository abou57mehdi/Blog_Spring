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
            }
        }

        stage('Analyse du code') {
            parallel {
                stage('Checkstyle') {
                    steps {
                        echo "Running Checkstyle analysis"
                        bat 'mvn checkstyle:checkstyle'
                    }
                    post {
                        always {
                            publishHTML(target: [
                                allowMissing: false,
                                alwaysLinkToLastBuild: true,
                                keepAll: true,
                                reportDir: 'target/site',
                                reportFiles: 'checkstyle.html',
                                reportName: 'Checkstyle Report'
                            ])
                        }
                    }
                }
                
                stage('FindBugs') {
                    steps {
                        echo "Running SpotBugs analysis (FindBugs successor)"
                        bat 'mvn spotbugs:spotbugs'
                    }
                    post {
                        always {
                            publishHTML(target: [
                                allowMissing: false,
                                alwaysLinkToLastBuild: true,
                                keepAll: true,
                                reportDir: 'target/site',
                                reportFiles: 'spotbugs.html',
                                reportName: 'SpotBugs Report'
                            ])
                        }
                    }
                }
                
                stage('PMD') {
                    steps {
                        echo "Running PMD analysis"
                        bat 'mvn pmd:pmd'
                    }
                    post {
                        always {
                            publishHTML(target: [
                                allowMissing: false,
                                alwaysLinkToLastBuild: true,
                                keepAll: true,
                                reportDir: 'target/site',
                                reportFiles: 'pmd.html',
                                reportName: 'PMD Report'
                            ])
                        }
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

        stage('Docker Preconditions') {
            steps {
                echo "Checking Docker preconditions"
                script {
                    // Check if Docker daemon is running
                    try {
                        bat 'docker info'
                        echo "✅ Docker daemon is running"
                    } catch (Exception e) {
                        error "Docker daemon is not running. Please start Docker and retry."
                    }
                    
                    // Check if Dockerfile exists
                    if (!fileExists('Dockerfile')) {
                        error "Dockerfile not found in the workspace. Please create a valid Dockerfile."
                    } else {
                        echo "✅ Dockerfile exists"
                    }
                    
                    // Validate Dockerfile syntax
                    try {
                        bat 'docker run --rm -i hadolint/hadolint < Dockerfile || true'
                        echo "✅ Dockerfile validation completed"
                    } catch (Exception e) {
                        echo "⚠️ Dockerfile linting failed but continuing: ${e.message}"
                    }
                    
                    // Clean up old images to free up space
                    try {
                        bat 'docker images "aboum22/blog-spring" -q | findstr . && docker rmi $(docker images "aboum22/blog-spring" -q) || echo "No images to clean"'
                        echo "✅ Cleaned up old Docker images"
                    } catch (Exception e) {
                        echo "⚠️ Old image cleanup failed but continuing: ${e.message}"
                    }
                    
                    // Check available disk space
                    bat 'wmic logicaldisk get deviceid,freespace,size'
                    
                    // Ensure target directory for artifacts exists
                    bat 'mkdir -p target\\docker-image'
                }
            }
        }

        stage('Deploy to Docker') {
            steps {
                echo "Building and pushing Docker image"
                script {
                    // Build the Docker image locally first
                    bat 'docker build -t aboum22/blog-spring:latest .'
                    
                    // Then try to push with retries
                    retry(3) {
                        try {
                            timeout(time: 2, unit: 'MINUTES') {
                                withCredentials([usernamePassword(
                                    credentialsId: 'dockerhub-creds',
                                    usernameVariable: 'DOCKER_USERNAME',
                                    passwordVariable: 'DOCKER_PASSWORD'
                                )]) {
                                    // First log out to avoid any cached credentials issues
                                    bat 'docker logout'
                                    
                                    // Then login and push
                                    bat """
                                        docker login -u "%DOCKER_USERNAME%" -p "%DOCKER_PASSWORD%"
                                        docker push aboum22/blog-spring:latest
                                    """
                                }
                            }
                        } catch (Exception e) {
                            echo "Docker push attempt failed: ${e.message}"
                            echo "Waiting before retry..."
                            sleep(time: 10, unit: 'SECONDS')
                            error("Retrying push operation")
                        }
                    }
                    
                    // Even if pushing to Docker Hub fails, save the image locally
                    try {
                        bat '''
                            mkdir -p target/docker-image
                            docker save aboum22/blog-spring:latest -o target/docker-image/blog-spring.tar
                        '''
                        archiveArtifacts artifacts: 'target/docker-image/*.tar', fingerprint: true
                        echo "Docker image archived as a Jenkins artifact"
                    } catch (Exception e) {
                        echo "Failed to archive Docker image: ${e.message}"
                    }
                }
            }
            post {
                success {
                    echo "Docker image successfully built and pushed"
                }
                failure {
                    echo "Docker deployment failed, but pipeline continues"
                    // Set build status to unstable instead of failed
                    unstable(message: "Docker deployment failed but pipeline continues")
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
            cleanWs(cleanWhenNotBuilt: false,
                    deleteDirs: true,
                    disableDeferredWipeout: true,
                    notFailBuild: true,
                    patterns: [[pattern: '.gitignore', type: 'INCLUDE'],
                              [pattern: '.propsfile', type: 'EXCLUDE']])
        }
    }
}
