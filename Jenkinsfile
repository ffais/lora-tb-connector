pipeline {
  agent any

  stages {
    stage('Build') {
      steps {
        withCredentials(bindings: [sshUserPrivateKey(credentialsId: 'jk_dev', keyFileVariable: 'key')]) {
          withCredentials([
            string(credentialsId: 'chat_id', variable: 'ChatID'),
            string(credentialsId: 'tg_token', variable: 'Token')
          ]){
            sh ('./build.sh --ChatID= ${ChatID} --Token= {$Token}')
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
