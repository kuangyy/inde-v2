<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/jsp/commons/taglibs.jsp"%>
<!DOCTYPE HTML>
<html lang="en">
<head>

    <title>About - 狂or野 - 你喜欢哪一个我</title>
    <jsp:include page="/WEB-INF/jsp/commons/resources.jsp"/>

    <style>
        * {
            margin: 0;
            padding: 0;
        }
        body {
            font-weight: 300;
            font-style: normal;
            top: 0;
            right: 0;
            bottom: 0;
            left: 0;
            overflow: hidden !important;
            color: #000;
            background: #222;
            -webkit-font-smoothing: antialiased;
        }
        canvas {
            /*position: fixed !important;*/
            z-index: 0;
            width: auto;
            height: auto;
        }
        #canvas-container {
            position: fixed !important;
            z-index: 0;
            width: 100%;
            height: 100%;
            margin-top: -100px;
        }
        .about {
            text-align: center;
            margin-top: 100px;
            color: #fff;
            z-index: 100;
        }
        .about .title {
            font-size: 70px;
            color: #f35626;
            background-image: -webkit-linear-gradient(92deg,#f35626,#feab3a);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            -webkit-animation: dhue 40s infinite linear;
        }
        .about p {
            margin: 10px 0;
        }
        .about p:first-of-type {
          margin-top: 30px;
        }

    </style>
</head>
<body>

<div class="about">
    <h1 class="title"  >关于/About</h1>
    <p>这是kyy的个人网站。</p>
</div>


<div id="canvas-container" class="animated fadeInUp layer"  data-depth="0.1">
    <canvas id="gravity"></canvas>
</div>





<script src="//cdn.bootcss.com/parallax/2.1.3/jquery.parallax.min.js"></script>
<!-- canvas star in background -->
<script src="../resources/js/effect/star.js"></script>
<script>
    //PARALLAX
    $('body').parallax({
//        scalarX: 25,
//        scalarY: 15,
        frictionX: 0.1,
        frictionY: 0.1,
    });

    //shrotcut
    shortcut.add("Ctrl+C",function(){alert(1)})
</script>

</body>
</html>