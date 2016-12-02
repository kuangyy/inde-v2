<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/jsp/commons/taglibs.jsp"%>
<!DOCTYPE HTML>
<html lang="en">
<head>

    <title>狂or野 - 你喜欢哪一个我</title>
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
    shortcut.add("Ctrl+C",function(){alert(1)})

    //clock
    setInterval(function(){
        $(".site_title").text(new Date());
    },1000)
</script>

</body>
</html>