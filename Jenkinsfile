pipeline {
  agent any
  stages {
    stage('Build') {
      environment {
                CHAT_ID = credentials('chat_id')
                TG_TOKEN = credentials('tg_token')
            }
      steps {
            sh ('./build.sh')
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
