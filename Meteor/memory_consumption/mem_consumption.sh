#!/bin/sh
help() {
    echo "[OVX_cpu.sh] - get cpu info using 'ps(1)'"
    echo "USAGE:"
    echo "\tsudo sh OVX_cpu.sh -[opt] [value]\n"
    echo "OPTIONS:"
    echo "\t-o [FILENAME]\t(flowdump log Output filename)"
    echo "\t-s [SLEEPTIME]\t(Time interval)"
    echo "\t-g [GREP]\t(GREP String)"
    echo "\t-h \t\t(Help Message)"
    exit 0
}
while getopts "o:s:g:h" opt
do
    case $opt in
        o) FILENAME=$OPTARG
          ;;
        s) SLEEPTIME=$OPTARG
          ;;
        g) GREP=$OPTARG
          ;;
        h) help ;;
        ?) help ;;
    esac
done

echo "\$SLEEPTIME:"$SLEEPTIME
echo "\$GREP:"$GREP
echo "\$FILENAME:"$FILENAME
while true
do
    date +%H:%M:%S.%N;
    ps -e -o user,pid,pcpu,rss,cmd | grep $GREP;
    # echo '\n';
    echo '';
    sleep $SLEEPTIME;
done | tee $FILENAME ;
