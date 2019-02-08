pipeline {
  agent any
  stages {
    stage('Build') {
      environment {
        CHAT_ID = credentials('chatid')
        TG_TOKEN = credentials('tg_token')
        TEST = credentials('test')
      }
      steps {
        //withCredentials([string(credentialsId: 'chat_id', variable: 'CHAT_ID'), string(credentialsId: 'tg_token', variable: 'TG_TOKEN'), string(credentialsId: 'test', variable: 'TEST')]){
            sh ('./build.sh --chat_id=$CHAT_ID --tg-token=$TG_TOKEN')
            sh '''
              set +x
              Msg="test esterno"
              URL="https://api.telegram.org/bot$TG_TOKEN/sendMessage"
              CHAT="chat_id=$CHAT_ID"
              curl -s -X POST $URL -d $CHAT -d "text=$Msg" -vvv
            '''
        //}
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
