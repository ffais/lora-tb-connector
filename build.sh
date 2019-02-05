set +x
for i in "$@"
do
case $i in
    --ChatID=*)
    ChatID="${i#*=}"
    shift # past argument=value
    ;;
    --Token=*)
    Token="${i#*=}"
    shift # past argument=value
    ;;
esac
done
cat $key > sshkey
chmod 600 sshkey
TimeLim=240
APP="nb-tb-connector-dev"
TSTAMP=$(date +%Y.%m.%d-%H.%M.%S)
TSSRV="$TSTAMP $APP:"
RELEASE=$(sed -E -n '/<artifactId>(lora-tb-connector)<\/artifactId>.*/{n;p}' pom.xml | grep -Po '\d\.\d')
echo $RELEASE
Msg="$TSSRV Build in corso"
echo $c
echo $t
curl -s — max-time $TimeLim -d 'chat_id=$ChatID&disable_web_page_preview=1&text=$Msg" "https://api.telegram.org/bot$Token/sendMessage' > /dev/null
echo $Msg
ssh -i sshkey -o "StrictHostKeyChecking no" dev@52.209.70.46 'sudo service lora-tb-conn stop && /home/dev/sources/deploy-lora-tb-conn.sh && echo "VER='${RELEASE}'" > /home/dev/lora-tb-connector-env && sudo service lora-tb-conn start '
if [ $? -eq 0 ]
then
  Msg="$TSSRV Aggiornamento completato"
  curl -s — max-time $TimeLim -d 'chat_id=$ChatID&disable_web_page_preview=1&text=$Msg" "https://api.telegram.org/bot$Token/sendMessage' > /dev/null
  echo $Msg
else
  Msg="$TSSRV Aggiornamento non riuscito $?"
  curl -s — max-time $TimeLim -d 'chat_id=$ChatID&disable_web_page_preview=1&text=$Msg" "https://api.telegram.org/bot$Token/sendMessage' > /dev/null
  echo $Msg
fi
rm sshkey
