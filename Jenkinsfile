pipeline {
  agent any
  stages {
    stage('Build') {
      environment {
        CHAT_ID = credentials('chatid')
        TG_TOKEN = credentials('tg_token')
        IP = credentials('platform-test-ip')
        USR = credentials('platform-test-user')
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
        sh ('docker build --build-arg VER=0.1 -t test .')
      }
    }
    stage('Deploy') {
      steps {
        echo 'Deploying....'
      }
    }
  }
}
