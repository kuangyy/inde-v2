<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/jsp/commons/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>

    <jsp:include page="/WEB-INF/jsp/game/commons/resources.jsp"/>

    <title>逃离剧本-狂or野-你喜欢哪一个我</title>
    <meta name="description" content="ky's game page">
    <meta name="author" content="kuangye">
</head>
<body>

<nav class="white" role="navigation">
    <div class="nav-wrapper container">
        <a id="logo-container" href="#" class="brand-logo">OOOC</a>
        <ul class="right hide-on-med-and-down">
            <li><a href="#">来玩啊，快活啊 (ಥ_ಥ) </a></li>
        </ul>

        <ul id="nav-mobile" class="side-nav">
            <li><a href="#">·</a></li>
        </ul>
        <a href="#" data-activates="nav-mobile" class="button-collapse"><i class="material-icons"><i class="fa fa-navicon" aria-hidden="true"></i></i></a>
    </div>
</nav>

<div id="index-banner" class="parallax-container">
    <div class="section no-pad-bot">
        <div class="container">
            <br><br>
            <h1 class="header center teal-text text-darken-4" style="color: #81ffff !important;">逃离剧本</h1>
            <div class="row center">
                <h5 class="header col s12 light">
                    <p>一种进入文字世界的方法<br></p>
                    <hr>
                    <p>仿佛潜入海底，沉浸其中</p>
                </h5>
            </div>
            <div class="row center">
                <a href="#start" id="download-button" class="btn-large waves-effect waves-light teal lighten-2">开始游戏</a>
            </div>
            <br><br>

        </div>
    </div>
    <div class="parallax"><img src="${ctx}/resources/img/game/bg1.jpg" alt="Unsplashed background img 1"></div>
</div>
<div class="container">
    <div class="section">

        <!--   Icon Section   -->
        <div class="row">
            <div class="col s12 m4">
                <div class="icon-block">
                    <h2 class="center brown-text"><i class="fa fa-smile-o" aria-hidden="true"></i></h2>
                    <h5 class="center">其乐无穷</h5>
                    <hr>
                    <p class="light center">玩，万千剧本</p>
                </div>
            </div>

            <div class="col s12 m4">
                <div class="icon-block">
                    <h2 class="center brown-text"><i class="fa fa-share-alt" aria-hidden="true"></i></h2>
                    <h5 class="center">连锁反应</h5>
                    <hr>
                    <p class="light center">环环相扣，因果相依</p>
                </div>
            </div>

            <div class="col s12 m4">
                <div class="icon-block">
                    <h2 class="center brown-text"><i class="fa fa-paper-plane" aria-hidden="true"></i></h2>
                    <h5 class="center">随时随地</h5>
                    <hr>
                    <p class="light center">即时进入，随时退出</p>
                </div>
            </div>
        </div>

    </div>
    <div class="row">
        <div class="col s12 m4">
            <div class="icon-block">
                <h2 class="center brown-text"><i class="fa fa-object-group" aria-hidden="true"></i></h2>
                <h5 class="center">无限可能</h5>
                <hr>
                <p class="light center">随机事件</p>
            </div>
        </div>

        <div class="col s12 m4">
            <div class="icon-block">
                <h2 class="center brown-text"><i class="fa fa-puzzle-piece" aria-hidden="true"></i></h2>
                <h5 class="center">自定义</h5>
                <hr>
                <p class="light center">个性化编辑剧本，世界由你而创</p>
            </div>
        </div>

        <div class="col s12 m4">
            <div class="icon-block">
                <h2 class="center brown-text"><i class="fa fa-resistance" aria-hidden="true"></i></h2>
                <h5 class="center">抵制常规</h5>
                <hr>
                <p class="light center">如你所愿，由你选择</p>
            </div>
        </div>
    </div>
</div>
<div class="parallax-container valign-wrapper">
    <div class="section no-pad-bot">
        <div class="container">
            <div class="row center">
                <h5 class="header col s12 light">享受生活、融情于字</h5>
            </div>
        </div>
    </div>
    <div class="parallax"><img src="${ctx}/resources/img/game/bg2.jpg" alt="Unsplashed background img 2"></div>
</div>
<div class="container">
    <div class="section">

        <div class="row">
            <div class="col s12 center">
                <h3><i class="mdi-content-send brown-text"></i></h3>
                <a id="start"></a>
                <h4>介绍</h4>
                <p class="left-align light">
                    这是一种文字游戏。<br>
                    需要自己编写或者使用他人编好的剧本，生成游戏，剧本的好坏一定程度上决定了游戏的质量。<br>
                    剧本分为：简介、场景和选项。<br>
                    简介：介绍剧本，让玩家快速进入状态，了解故事背景。<br>
                    场景：从一开始起，玩家即进入某一场景，在此场景、此情形下，通过不同选项，决定游戏进行方向。<br>
                    选项：每一场景皆有无数选项（由剧本作者指定，但不直接决定最终选项），直至结束。
                </p>
                <hr>
                <p class="left-align light">
                    <b>如何开始游戏？关注微信订阅号 [ kys ] 即刻享受</b>
                </p>
            </div>
        </div>

    </div>
</div>
<div class="parallax-container valign-wrapper">
    <div class="section no-pad-bot">
        <div class="container">
            <div class="row center">
                <h5 class="header col s12 light">似梦、似幻、似真</h5>
            </div>
        </div>
    </div>
    <div class="parallax"><img src="${ctx}/resources/img/game/bg3.jpg" alt="Unsplashed background img 3"></div>
</div>

<footer class="page-footer teal">
    <div class="container">
        <div class="row">
            <div class="col l6 s12">
                <h5 class="white-text">关于我</h5>
                <p class="grey-text text-lighten-4">
                    就我一个人，就是想玩游戏！
                </p>
            </div>
            <div class="col l3 s12">
                <h5 class="white-text">相关</h5>
                <ul>
                    <li><a class="white-text" href="#!">橙光游戏</a></li>
                    <li><a class="white-text" href="#!">微信订阅号</a></li>
                    <li><a class="white-text" href="#!">游戏</a></li>
                </ul>
            </div>
            <div class="col l3 s12">
                <h5 class="white-text">联系我们</h5>
                <ul>
                    <li><a class="white-text" href="#">QQ:495621336</a></li>
                    <li><a class="white-text" href="#">Email:ky.kys.cn@hotmail.com</a></li>
                </ul>
            </div>
        </div>
    </div>
    <div class="footer-copyright">
        <div class="container">
            Made by <a class="brown-text text-lighten-3" >KY</a> / Thanks <a class="brown-text text-lighten-3" href="http://materializecss.com">Materialize</a>
        </div>
    </div>
</footer>

<script>
    (function($){
        $(function(){
            $('.button-collapse').sideNav();
            $('.parallax').parallax();
        }); // end of document ready
    })(jQuery); // end of jQuery name space
</script>

</body>
</html>
