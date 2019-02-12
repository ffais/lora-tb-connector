def skipRemainingStages = false
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
          rc = sh(script: build.sh, returnStatus: true)
          echo $rc
        }
      }
    }
    stage('Test') {
      when {
                expression { skipRemainingStages == 'false' }
            }
      steps {
        echo 'Testing..'
        sh '''
          docker run lora-tb-connector:0.1

        '''
      }
    }
    stage('Deploy') {
      steps {
        echo 'Deploying....'
      }
    }
  }
}
