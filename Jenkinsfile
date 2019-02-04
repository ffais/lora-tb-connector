pipeline {
  agent any
  environment {
        ChatID     = credentials('chat_id')
        Token = credentials('tg_token')
    }
  stages {
    stage('Build') {
      steps {
        withCredentials(bindings: [sshUserPrivateKey(credentialsId: 'jk_dev', \
                                                           keyFileVariable: 'key')]) {
          sh /* CORRECT */ '''
          set +x
          cat $key > sshkey
          chmod 600 sshkey
          #Telegram
          TimeLim=240
          APP="nb-tb-connector"
          TSTAMP=$(date +%Y.%m.%d-%H.%M.%S)
          TSSRV="$TSTAMP $APP:"
          RELEASE=$(sed -E -n '/<artifactId>(lora-tb-connector)<\/artifactId>.*/{n;p}' pom.xml | grep -Po '\d\.\d')
          echo $RELEASE
          Msg="$TSSRV Build in corso"
          curl -s — max-time $TimeLim -d "chat_id=${ChatID}&disable_web_page_preview=1&text=${Msg}" "https://api.telegram.org/bot${Token}/sendMessage" > /dev/null
          echo $Msg
          ssh -i sshkey -o "StrictHostKeyChecking no" dev@52.209.70.46 'service lora-tb-connector stop && cd /home/dev/sources/lora-tb-connector && git checkout dev && git pull origin dev && mvn install && service lora-tb-connector start '
          if [ $? -eq 0 ]
          then
            Msg="$TSSRV Aggiornamento completato"
            curl -s — max-time $TimeLim -d "chat_id=${ChatID}&disable_web_page_preview=1&text=${Msg}" "https://api.telegram.org/bot${Token}/sendMessage" > /dev/null
            echo $Msg
          else
            Msg="$TSSRV Aggiornamento non riuscito $?"
            curl -s — max-time $TimeLim -d "chat_id=${ChatID}&disable_web_page_preview=1&text=${Msg}" "https://api.telegram.org/bot${Token}/sendMessage" > /dev/null
            echo $Msg
          fi
          rm sshkey
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
