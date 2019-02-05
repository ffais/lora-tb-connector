pipeline {
  agent any
  stages {
    stage('Build') {
      environment {
            CHAT_ID = credentials('tg_token')
            TG_TOKEN = credentials('chat_id')
      }
      steps {
        withCredentials(bindings: [sshUserPrivateKey(credentialsId: 'jk_dev', keyFileVariable: 'key')]) {
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
