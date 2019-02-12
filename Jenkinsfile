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
          script {
            rc = sh(script: "./build.sh", returnStatus: true)
            // check exit code
            sh "echo \"exit code is : ${rc}\""

            if (rc != 0)
            {
                sh "echo 'exit code is NOT zero'"
                skipRemainingStages = true
            }
            else
            {
                sh "echo 'exit code is zero'"
            }
          }
        }
      }
    }
    stage('Test') {
      when {
                expression { skipRemainingStages == 'true' }
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
