pipeline {
  agent any
  stages {
    stage('Build') {
      environment {
                CHAT_ID = '691082561'
                TG_TOKEN = '731643171:AAHpBHrEgs4Sfuq12uYvpq31DUtnEHdVlFU'
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
