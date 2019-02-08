pipeline {
  agent any
  stages {
    stage('Build') {
      environment {
                CHAT_ID = credentials('chat_id')
                TG_TOKEN = credentials('tg_token')
            }
      steps {
        withCredentials([string(credentialsId: 'chat_id', variable: 'CHAT_ID'), string(credentialsId: 'tg_token', variable: 'TG_TOKEN'), string(credentialsId: 'test', variable: 'TEST')]){
            sh ("./build.sh --chat_id=${CHAT_ID} --TG_TOKEN=${TG_TOKEN}")
        }
      }
    }
    stage('Test') {
      steps {
        echo 'Testing..'
      }
    }
    stage('Deploy') {
      steps {
        echo 'Deploying....'
      }
    }
  }
}
