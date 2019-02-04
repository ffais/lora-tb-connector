pipeline {
  agent any
  environment {
        ChatID = credentials('chat_id')
        Token = credentials('tg_token')
    }
  stages {
    stage('Build') {
      steps {
        withCredentials(bindings: [
          sshUserPrivateKey(credentialsId: 'jk_dev', keyFileVariable: 'key')
          string(credentialsId: 'chat_id', variable: 'ChatID')
          string(credentialsId: 'tg_token', variable: 'Token')
        ]) {
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
