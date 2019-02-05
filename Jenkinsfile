pipeline {
  agent any
  stages {
    stage('Build') {
      environment {
            CHAT_ID = credentials('chat_id')
            TG_TOKEN = credentials('tg_token')
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
