pipeline {
  agent any
  stages {
    stage('Build') {
      steps {
        withCredentials([string(credentialsId: 'chat_id', variable: 'CHAT_ID'), string(credentialsId: 'tg_token', variable: 'TG_TOKEN'), string(credentialsId: 'test', variable: 'TEST')]){
            sh ('./build.sh --chat_id=$CHAT_ID --tg-token=$TG_TOKEN')
            sh /* CORRECT */ '''
              set +x
              TimeLim=240
              Msg="test esterno"
              URL="https://api.telegram.org/bot$TG_TOKEN/sendMessage"
              CHAT="chat_id=${CHAT_ID}"
              curl -s -X POST $URL -d $CHAT -d "text=$Msg" > /dev/null
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
