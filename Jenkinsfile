pipeline {
  agent any
  stages {
    stage('Build') {
      environment {
                CHAT_ID = credentials('chat_id')
                TG_TOKEN = credentials('tg_token')
            }
      steps {
        withcredentials([string(credentialsId: 'chat_id', variable: 'CHAT_ID'), string(credentialsId: 'tg_token', variable: 'TG_TOKEN')]){
            sh ('./build.sh')
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
