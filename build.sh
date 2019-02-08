#!/bin/sh
set +x
for i in "$@"
do
case $i in
    --chat_id=*)
    CHAT_ID="${i#*=}"
    shift # past argument=value
    ;;
    --tg_token=*)
    TG_TOKEN="${i#*=}"
    shift # past argument=value
    ;;
    --default)
    DEFAULT=YES
    shift # past argument with no value
    ;;
    *)
          # unknown option
    ;;
esac
done
if [ $TEST = "test" ]
then
  echo "secret ok"
else
  echo "secret ko"
fi
cat $key > sshkey
chmod 600 sshkey
TimeLim=240
APP="nb-tb-connector-dev"
TSTAMP=$(date +%Y.%m.%d-%H.%M.%S)
TSSRV="$TSTAMP $APP:"
RELEASE=$(sed -E -n '/<artifactId>(lora-tb-connector)<\/artifactId>.*/{n;p}' pom.xml | grep -Po '\d\.\d')
echo $RELEASE
Msg="$TSSRV Build in corso"
URL="https://api.telegram.org/bot$TG_TOKEN/sendMessage"
curl -s -X POST $URL -d "chat_id=$CHAT_ID" -d "text=$Msg" 
curl -s — max-time $TimeLim -d "chat_id=$CHAT_ID&disable_web_page_preview=1&text=$Msg" "https://api.telegram.org/bot$TG_TOKEN/sendMessage"
echo $Msg
#ssh -i sshkey -o "StrictHostKeyChecking no" dev@52.209.70.46 'sudo service lora-tb-conn stop && /home/dev/sources/deploy-lora-tb-conn.sh && echo "VER='${RELEASE}'" > /home/dev/lora-tb-connector-env && sudo service lora-tb-conn start '
#echo $?
#if [ $? -eq 0 ]
#then
#  Msg="$TSSRV Aggiornamento completato"
#  curl -s — max-time $TimeLim -d "chat_id=${CHAT_ID}&disable_web_page_preview=1&text=$Msg" "https://api.telegram.org/bot${TG_TOKEN}/sendMessage"
#  echo $Msg
#else
#  Msg="$TSSRV Aggiornamento non riuscito $?"
#  curl -s — max-time $TimeLim -d "chat_id=${CHAT_ID}&disable_web_page_preview=1&text=$Msg" "https://api.telegram.org/bot${TG_TOKEN}/sendMessage"
#  echo $Msg
#fi
#rm sshkey
