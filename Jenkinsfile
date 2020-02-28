pipeline {
    agent any
    tools {
        maven 'M3'
    }
    stages {
        stage('Initialize') {
           steps {
               
           }
        }
        stage('Build') {
           steps {
               sh 'mvn clean compile package'
           }
        }
        stage('Test') {
           steps {
               sh 'mvn test'
           }
        }
    }
}