#!/bin/bash

###############################################真正返回值设定####################################################
#真正返回值记录的变量
#若要编辑该脚本，请不要直接再脚本内直接使用exit退出
#出错请赋值该变量为1即可
realLastReturn=0 #如果想要暂时取消请不要在这里修改该变量！！！
#最好使用该方法对realLastReturn赋值，不然可能会覆盖别人的非零返回值
function setRealLastReturn() {
  if [[ $1 == 1 ]]; then
    realLastReturn=1
  fi
  #    如果git hook检查不再适用，且暂时没时间修改该脚本，请取消下面一行注释
      realLastReturn=0
}

###############################################Commit消息格式检查####################################################
commitMsg=$(cat "$1")

# 来源于该网址：https://gitmoji.dev/
declare -A commitMap=(
  ["art"]="改进代码的结构"
  ["zap"]="提高性能"
  ["fire"]="删除代码或文件"
  ["bug"]="修复bug"
  ["ambulance"]="关键的热修复补丁"
  ["sparkles"]="引入新功能"
  ["memo"]="添加或更新文档"
  ["rocket"]="部署内容"
  ["lipstick"]="添加或更新 UI 和样式文件"
  ["tada"]="开始一个项目"
  ["white_check_mark"]="添加、更新或通过测试"
  ["lock"]="修复安全问题"
  ["closed_lock_with_key"]="添加或更新机密"
  ["bookmark"]="发布新版的标签"
  ["rotating_light"]="修复编译器警告"
  ["construction"]="工作正在进行中"
  ["green_heart"]="修复 CI 构建"
  ["arrow_down"]="降级依赖版本"
  ["arrow_up"]="升级依赖版本"
  ["pushpin"]="将依赖项固定到特定版本"
  ["construction_worker"]="添加或更新 CI 构建"
  ["chart_with_upwards_trend"]="添加或更新对代码的分析"
  ["recycle"]="重构代码"
  ["heavy_plus_sign"]="添加依赖"
  ["heavy_minus_sign"]="移除依赖"
  ["wrench"]="添加或更新配置文件"
  ["hammer"]="添加或更新开发脚本"
  ["globe_with_meridians"]="本地化或国际化处理"
  ["pencil2"]="修复拼写错误"
  ["poop"]="重构屎山"
  ["rewind"]="还原更改"
  ["twisted_rightwards_arrows"]="合并分支"
  ["package"]="添加或更新已编译的文件或包"
  ["alien"]="由于外部 API 更改而更新代码"
  ["truck"]="移动或重命名资源"
  ["page_facing_up"]="添加或更新许可证"
  ["boom"]="引入重大更改"
  ["bento"]="添加或更新资产"
  ["wheelchair"]="提高可访问性"
  ["bulb"]="在源代码中添加或更新注释"
  ["beers"]="醉醺醺地编写代码"
  ["speech_balloon"]="添加或更新文本和文字"
  ["card_file_box"]="数据库相关的更改"
  ["loud_sound"]="添加或更新日志"
  ["mute"]="删除日志"
  ["busts_in_silhouette"]="添加或更新参与者"
  ["children_crossing"]="改善用户体验"
  ["building_construction"]="进行体系结构更改"
  ["iphone"]="完善响应式设计"
  ["clown_face"]="嘲讽某人代码"
  ["egg"]="添加或更新彩蛋"
  ["see_no_evil"]="添加或更新 .gitignore 文件"
  ["camera_flash"]="添加或更新快照"
  ["alembic"]="执行实验"
  ["mag"]="提升知名度"
  ["label"]="添加或更新类型"
  ["seedling"]="添加或更新种子文件"
  ["triangular_flag_on_post"]="添加、更新或删除功能标志"
  ["goal_net"]="捕获异常"
  ["dizzy"]="添加动画，优化过渡"
  ["wastebasket"]="删除废弃代码"
  ["passport_control"]="处理与授权、角色和权限相关的代码"
  ["adhesive_bandage"]="非关键问题的简单修复"
  ["monocle_face"]="数据探索/检查"
  ["coffin"]="删除废弃代码"
  ["test_tube"]="添加失败的测试"
  ["necktie"]="添加或更新业务逻辑"
  ["stethoscope"]="添加或更新测试"
  ["bricks"]="与基础结构相关的更改"
  ["technologist"]="改善开发人员体验"
  ["money_with_wings"]="添加赞助或与资金相关的基础设施"
)

merge=$(echo "$commitMsg" | grep "^Merge")

result=$(echo "$commitMsg" | grep ":[a-z_]\+: .\+")

if ! [ "$merge" == "" ]; then
  # 当得到的 merge 不为空的时候，说明是 git 合并生成的提交记录
  echo "检测到为自动merge提交信息，忽略提交格式检查"
elif [ "$result" == "" ]; then
  # 当得到的 result 为空的时候，说明没按正确格式提交
  blank=$(echo "$commitMsg" | grep ":[a-z_]\+:.\+") # 检查是否少打了空格
  if ! [ "$blank" == "" ]; then
    echo "『:title:』与『title』之间未空格！"
  else
    echo "提交格式有误，标准格式为:"
    echo ":type: title"
    echo "（注意：这一行是空行）"
    echo "description"
    echo "************************* type *******************************"
    echo "type 请使用该网址上的 commit 头 https://gitmoji.dev/ （看不懂英文就用网页翻译）"
    echo "AS 必须安装 Gitmoji Plus: Commit Button 插件！！！"
    echo "方便生成 commit，使用教程请查看飞书资料库"
  fi
  setRealLastReturn 1
else
  head=${commitMsg#*:}
  type=${head%%:*}
  value=${commitMap["$type"]}
  if [ "$value" == "" ]; then
    echo "type = $type"
    echo "未找到对应 type，请使用该网址上的 commit 头 https://gitmoji.dev/ "
    setRealLastReturn 1
  else
    echo "本次 commit 头为: $type 表达的意思为: $value"
  fi
fi

###############################################Drawable资源命名检查####################################################

#判断该文件是否为Drawable资源,接受一个参数，参数为文件路径，相对路径绝对路径都可
function isDrawable() {
  path=$(
    cd $(dirname $1)
    pwd
  )
  tag=$(echo "$path" | sed 's/.*\/\(.*\)/\1/g')
  if [[ ${tag%%-*} == "drawable" ]]; then
    return 1
  fi
  return 0
}

#获取xml最外层标签,获取一个参数，文件路径，相对和绝对都行
#由于不能返回字符串，所以请直接使用$(getXmlFirstTag 参数)
#切勿直接调用此函数
function getXmlFirstTag() {
  #获取最外层那个节点名
  startTag=$(cat $1 | sed -n "/<[^?].*/p" | sed -n 1p | sed "s/.*<\([^ ]*\).*/\1/g")
  #    endTag=$(cat $1|sed -n "/<\/.*>/p"|sed -n '$p'|sed "s/.*<\/[ ]*\([^ ]*\)[ ]*>.*/\1/g")
  #防止某些
  echo "$startTag"
}

drawableFirstTips="*******************以下资源文件有问题需要解决***********************"
function tipsEcho() {
  if ! [[ ${drawableFirstTips} == "" ]]; then
    echo ${drawableFirstTips}
    drawableFirstTips=""
  fi
  tipsName=$(basename $1)
  tipsModuleName=$(echo $(
    cd $(dirname $1)
    pwd
  ) | sed "s/.*\/\(.*\)\/src\/.*/\1/g")
  tipsModuleName=${tipsModuleName//"module_"/}
  tipsModuleName=${tipsModuleName//"lib_"/}
  tipsTagName=$2
  echo "[ $(
    cd $(dirname $1)
    pwd
  )/$tipsName 新增资源文件命名格式错误 ]"
  echo "[ ${tipsName} 文件前缀应修改为：${tipsModuleName}_${tipsTagName}_ ]"
}

#处理xml的Drawable,获取一个参数，文件路径，相对和绝对都行
function handDrawable() {
  returnValue=0
  name=$(basename $1)
  suffix=${name##*.}
  moduleNamePrefix=$(echo $(
    cd $(dirname $1)
    pwd
  ) | sed "s/.*\/\(.*\)\/src\/.*/\1/g")
  moduleNamePrefix=${moduleNamePrefix//"module_"/}
  moduleNamePrefix=${moduleNamePrefix//"lib_"/}
  if ! [[ $(echo ${name} | grep "ic_launcher") == "" ]]; then
    # 如果检测到是app图标，放行
    return ${returnValue}
  elif [[ ${suffix} == "xml" ]]; then
    firstTag=$(getXmlFirstTag $1)
    if [[ ${firstTag} == "vector" ]]; then
      isFit=$(echo ${name} | sed "s/${moduleNamePrefix}_ic_.*//g")
      if ! [[ "$isFit" == "" ]]; then
        tipsEcho $1 "ic"
        returnValue=1
      fi
    else
      realTag=${firstTag//-/_}
      isFit=$(echo ${name} | sed "s/${moduleNamePrefix}_${realTag}_.*//g")
      if ! [[ "$isFit" == "" ]]; then
        tipsEcho $1 ${realTag}
        returnValue=1
      fi
    fi
  elif [[ ${suffix} == "jpg" ]] ||
    [[ ${suffix} == "png" ]] ||
    [[ ${suffix} == "webp" ]] \
    ; then
    isFit=$(echo ${name} | sed "s/${moduleNamePrefix}_ic_.*//g")
    if ! [[ "$isFit" == "" ]]; then
      tipsEcho $1 "ic"
      returnValue=1
    fi
  fi
  return ${returnValue}
}

#判断drawable主要流程
changeFile=$(git status | grep "new file:")
array=(${changeFile//new file:/})
for file in "${array[@]}"; do
  #判断是不是drawable
  isDrawable ${file}
  if test $? -eq 1; then
    fileName=$(basename ${file})
    handDrawable ${file}
    setRealLastReturn $?
  fi
done

###############################################退出脚本,请确保脚本没有中途exit####################################################
if [[ $realLastReturn == 1 ]]; then
  echo ""
  echo "提交失败，请修复上述问题后再提交"
fi
exit ${realLastReturn}
