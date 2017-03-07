<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/jsp/commons/taglibs.jsp"%>
<!DOCTYPE HTML>
<html lang="en">
<head>

    <title>狂or野 - 你喜欢哪一个我</title>
    <meta name="author" content="ky"/>

    <jsp:include page="/WEB-INF/jsp/commons/resources.jsp"/>

    <link rel="stylesheet" href="${ctx}/resources/css/index.css">

</head>
<body>

<div id="canvas-container" class="animated fadeInUp layer"  data-depth="0.1">
    <canvas id="gravity"></canvas>
</div>

<div class="site_title" >狂or野 - 你喜欢哪一个我</div>

<div class="info-list container col-sm-12">
    <ul>
        <li>
            <a href="${ctx}/blog" target="_blank" title="文字"><div>壹</div></a>
        </li>
        <li>
            <a href="${ctx}/game" target="_blank" title="游戏"><div>贰</div></a>
        </li>
        <li>
            <a href="${ctx}/research" target="_blank" title="研究"><div>叁</div></a>
        </li>
        <li>
            <a href="${ctx}/fun" target="_blank" title="趣"><div>肆</div></a>
        </li>
    </ul>
</div>


<div class="shadow-box"></div>


<div class="audiolist">
    <div class="audioplay">
        <audio class="audio1" style="width:200px; height:20px;" src="http://m2.music.126.net/DRohYj-4xOW8kOO7ERkSdg==/18824738580876363.mp3" loop autobuffer ></audio>
        <div class="audioplay1"><span>关于我爱你</span><br>-张悬</div>
        <div class="audioplay2"></div>
        <div class="audioplay3"><span id="audiospan">00:00/00:00</span></div>
    </div>
</div>

<footer>
    <span style="color: #999">[ky]</span> /
    <a href="${ctx}/about"> About</a>
    <div class="motto pull-right">${motto.name}</div>
</footer>

<script src="//cdn.bootcss.com/parallax/2.1.3/jquery.parallax.min.js"></script>
<!-- canvas star in background -->
<script src="${ctx}/resources/js/effect/star.js"></script>
<script>
    //PARALLAX
    $('body').parallax({
//        scalarX: 25,
//        scalarY: 15,
//        frictionX: 0.2,
//        frictionY: 0.2,
    });

    //animate
    $.each($(".info-list ul li"),function(index,item){
        index%4==0?$(item).addClass("slideInLeft animated"):index%4==1?$(item).addClass("slideInDown animated"):index%4==2?$(item).addClass("slideInUp animated"):$(item).addClass("slideInRight animated");
    });

    //shrotcut
    shortcut.add("Ctrl+C",function(){alert("你干嘛要复制我")})

    //clock
    setInterval(function(){
        $(".site_title").text(new Date());
    },1000)


    {
        //music
        var time = [];
        var audiostart = [];

        $(function () {
            $(".audioplay").click(function () {
                var i = $(this).index()
                var au = $(".audio1")[i];

                if (audiostart[i]) {
                    au.pause();
                    au.currentTime = time[i];
                    audiostart[i] = false;
                } else {
                    for (var j = 0; j < $(".audioplay").length; j++) {
                        audiostart[j] ? $(".audioplay")[j].click() : "";
                    }
                    au.play();
                    audiostart[i] = true;
                }
            });
            window.setInterval(function () {
                var audios = $(".audioplay audio");
                var audiopro = $(".audioplay2");

                for (var i = 0; i < audios.length; i++) {
                    var total = audios[i].duration;
                    var now = audios[i].currentTime;
                    time[i] = now;

                    var minuteTotal = Math.floor(total / 60) > 9 ? Math.floor(total / 60) : "0" + Math.floor(total / 60);
                    var secondTotal = Math.floor(total % 60) > 9 ? Math.floor(total % 60) : "0" + Math.floor(total % 60);

                    var minuteNow = Math.floor(now / 60) > 9 ? Math.floor(now / 60) : "0" + Math.floor(now / 60);
                    var secondNow = Math.floor(now % 60) > 9 ? Math.floor(now % 60) : "0" + Math.floor(now % 60);

                    var widthpx = $($(".audioplay")[i]).css("width");
                    var w = parseInt(widthpx);

                    $(audiopro[i]).animate({width: now / total * w});
                    $($(".audioplay3")[i]).html(minuteNow + ":" + secondNow + "/" + minuteTotal + ":" + secondTotal);
                }
            }, 1000)
        });
    }

</script>

</body>
</html>