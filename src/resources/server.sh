#!/bin/bash
GIT_URL="https://github.com/iSenninha/tank.git"
GIT_PATH="/home/senninha/server/tank/"
JAR_NAME="tankserver-0.0.1-SNAPSHOT.jar"
JAR_TARGET_PATH="${GIT_PATH}target/"

#help函数
help(){
  echo "用法:"
  echo "-a 更新项目重新生成并重启"
  echo "-p 拉下最新代码"
  echo "-g 重新打包"
  echo "-rs 启动或重启"
  echo "-stop 停止"
}


#拉取git代码
pull(){
git pull;

echo "拉取完毕git代码完毕"
}

#maven打包
package(){
cd $GIT_PATH
mvn package
echo "打包完毕"
}

#启动或者重启
restart(){
cd $JAR_TARGET_PATH
stop
if [ $? -eq 0 ] 
then
	echo "等待20s关服"
	sleep 20
fi

java -jar ${JAR_TARGET_PATH}${JAR_NAME} & >>/dev/null 2>&1
echo $! > SERVER_PID
}

#停止游戏服务
stop(){
       	cd $JAR_TARGET_PATH
	kill -15 $(cat SERVER_PID)
	if [ $? -eq 0 ]
	then
		echo "关服成功"
		return 0
	else
		echo "当前没有开启的服务器"
		return 1
	fi
}
            

#-----------------------------------------------------------------------------------
if [ $# -eq 0 ]
then
	help;
	exit;
fi

if [ ! -d "$GIT_PATH" ];then
	echo "clone项目"
	git clone ${GIT_URL}
fi

cd $GIT_PATH

#参数
a=0;
p=0;
g=0;
rs=0;
stop=0;

for tem in $@;
do
	case $tem in
		-a)
			a=1;;
		-p)
			p=1;;
		-g)
			g=1;;
		-rs)
			rs=1;;
		-stop)
			stop=1;;
	esac	
done

echo $a$p$g$rs

if [ $stop -eq 1 ]
then
	echo "尝试停止游戏服务..."
	stop;
	exit;
fi


if [ $a -eq 1 ]
then
	pull;
	package;
	restart;
	exit;
fi

if [ $p -eq 1 ]
then
	pull;
fi

if [ $g -eq 1 ]
then
	package;
fi

if [ $rs -eq 1 ]
then
	restart;
fi

