#!/bin/bash
set +x
#ssh key
cat $key > sshkey
cat $env > lora-tb-connector.env
chmod 600 lora-tb-connector.env
chmod 600 sshkey
statusCode=1
APP="lora-tb-connector-prod"
TSTAMP=$(date +%Y.%m.%d-%H.%M.%S)
TSSRV="$TSTAMP $APP:"
RELEASE=$(sed -E -n '/<artifactId>(lora-tb-connector)<\/artifactId>.*/{n;p}' pom.xml | grep -Po '\d\.\d')
echo $RELEASE
Msg="$TSSRV Build in corso"
URL="https://api.telegram.org/bot${TG_TOKEN}/sendMessage"
CHAT="chat_id=${CHAT_ID}"
curl -s -X POST $URL -d $CHAT -d "text=$Msg"
#curl -s — max-time $TimeLim -d "chat_id=$CHAT_ID&disable_web_page_preview=1&text=$Msg" "https://api.telegram.org/bot$TG_TOKEN/sendMessage"
ssh -i sshkey -o "StrictHostKeyChecking no" $USR@$IP "sudo service lora-tb-conn stop"
docker-compose -f lora-tb-connector-test.yaml up -d
while [[ $(docker inspect lora-tb-connector --format='{{.State.Health.Status}}') == 'starting' ]]; do
  echo "starting in progress"
done
if [[ $(docker inspect lora-tb-connector --format='{{.State.Health.Status}}') == 'healthy' ]]
then
  echo "container started"
  appstate=$(curl localhost:5050/actuator/health | jq -r '.status')
  echo $appstate
  if [[ $appstate == 'UP' ]]; then
    echo "test ok"
    Msg="$TSSRV test ok"
    curl -s -X POST $URL -d $CHAT -d "text=$Msg"
#    docker-compose -f lora-tb-connector-test.yaml down
    statusCode=0
  else
    echo "test failed"
#    docker-compose -f lora-tb-connector-test.yaml down
    Msg="$TSSRV test failed"
    curl -s -X POST $URL -d $CHAT -d "text=$Msg"
    statusCode=1
  fi
else
  echo "starting containter failed"
#  docker-compose -f lora-tb-connector-test.yaml down
  Msg="$TSSRV starting containter failed"
  curl -s -X POST $URL -d $CHAT -d "text=$Msg"
fi
#docker-compose -f lora-tb-connector-test.yaml down
#docker system prune -f
#docker volume prune -f
#ssh -i sshkey -o "StrictHostKeyChecking no" $USR@$IP "sudo service lora-tb-conn start"
rm sshkey
rm lora-tb-connector.env
echo $statusCode
exit $statusCode
