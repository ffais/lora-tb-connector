pipeline {
  agent any
  stages {
    stage('Build') {
      steps {
        withCredentials([string(credentialsId: 'chat_id', variable: 'CHAT_ID'), string(credentialsId: 'tg_token', variable: 'TG_TOKEN')]){
          withCredentials(bindings: [sshUserPrivateKey(credentialsId: 'jk_dev', keyFileVariable: 'key')]) {
            sh ('./build.sh')
          }
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
