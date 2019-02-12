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
          sh ('./build.sh', returnStatus:true)
          
        }
      }
    }
    stage('Test') {
      when {
                expression { skipRemainingStages == 'false' }
            }
      steps {
        echo 'Testing..'
        withCredentials(bindings: [sshUserPrivateKey(credentialsId: 'jk_dev', keyFileVariable: 'key')]) {
          withCredentials([file(credentialsId: 'lora-tb-connector-env', variable: 'env')]){
            sh ('./test.sh')
          }
        }
      }
    }
    stage('Deploy') {
      steps {
        echo 'Deploying....'
      }
    }
  }
}
