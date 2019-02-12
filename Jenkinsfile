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
        withCredentials([usernamePassword(credentialsId: 'docker-hub', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
          sh '''
            docker login -u $USERNAME -p $PASSWORD
            docker build -t lora-tb-connector:0.1 --build-arg VER=0.1 .
          '''
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
