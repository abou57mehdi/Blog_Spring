pipeline {
    agent any
    stages {
        stage('Source SCM') {
            steps {
                git branch: 'abdelah', url: 'https://github.com/abous7me7di/Blog_Spring.git'
            }
        }
        stage('Build') {
            steps {
                sh 'mvn clean install -DskipTests'
            }
        }
        stage('Test') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }
        stage('Analyze Code') {
            steps {
                sh 'mvn checkstyle:checkstyle'
                sh 'mvn spotbugs:spotbugs'
                sh 'mvn pmd:pmd'
            }
            post {
                always {
                    recordIssues enabledForFailure: true, tool: checkStyle(pattern: 'target/checkstyle-result.xml')
                    recordIssues enabledForFailure: true, tool: spotBugs(pattern: 'target/spotbugsXml.xml')
                    recordIssues enabledForFailure: true, tool: pmd(pattern: 'target/pmd.xml')
                }
            }
        }
        stage('JavaDoc') {
            steps {
                sh 'mvn javadoc:javadoc'
            }
        }
        stage('Package') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }
    }
    post {
        failure {
            mail to: 'your-email@example.com',
                 subject: "Build Failed: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                 body: "The build failed. Check the Jenkins console output at ${env.BUILD_URL}"
        }
    }
}
