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

<h1 class="site_title" >狂or野 - 你喜欢哪一个我</h1>


<div class="list container col-sm-12">
    <ul>
        <%--<li><a href=""><img src="${ctx}/resources/img/test/red.png"></a></li>--%>
        <%--<li><a href=""><img src="${ctx}/resources/img/test/green.png"></a></li>--%>
        <%--<li><a href=""><img src="${ctx}/resources/img/test/red.png"></a></li>--%>
        <%--<li><a href=""><img src="${ctx}/resources/img/test/green.png"></a></li>--%>
        <li>
            <a href=""><div>博客</div></a>
        </li>
        <li>
            <a href=""><div>游戏</div></a>
        </li>
        <li>
            <a href=""><div>新奇</div></a>
        </li>
        <li>
            <a href=""><div>其他</div></a>
        </li>
    </ul>
</div>


<div class="motto">${motto.name}</div>

<div class="shadow-list"></div>




<footer><a href="${ctx}/about">About</a> </footer>



<script src="//cdn.bootcss.com/parallax/2.1.3/jquery.parallax.min.js"></script>
<!-- canvas star in background -->
<script src="../resources/js/effect/star.js"></script>
<script>
    //PARALLAX
    $('body').parallax({
//        scalarX: 25,
//        scalarY: 15,
        frictionX: 0.2,
        frictionY: 0.2,
    });

    //animate
    $.each($(".list ul li"),function(index,item){
        index%2==0?$(item).addClass("bounceInDown animated"):$(item).addClass("bounceInUp animated");
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