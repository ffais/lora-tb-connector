pipeline {
  agent any
  stages {
    stage('Build') {
      steps {
        withCredentials([string(credentialsId: 'chat_id', variable: 'CHAT_ID'), string(credentialsId: 'tg_token', variable: 'TG_TOKEN'), string(credentialsId: 'test', variable: 'TEST')]){
            sh ("./build.sh --chat_id=${CHAT_ID} --TG_TOKEN=${TG_TOKEN}")
            sh /* CORRECT */ '''
              set +x
              TimeLim=240
              Msg="test esterno"
              curl -s -d "chat_id=${CHAT_ID}&disable_web_page_preview=1&text=$Msg" "https://api.telegram.org/bot${TG_TOKEN}/sendMessage"
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
