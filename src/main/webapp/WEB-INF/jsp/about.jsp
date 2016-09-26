<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/jsp/commons/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <jsp:include page="/WEB-INF/jsp/commons/resources.jsp"/>

    <title>About - 狂or野-你喜欢哪一个我</title>

    <link rel="stylesheet" type="text/css" href="${ctx}/resources/css/404.css">
    <script type="text/javascript" src="${ctx}/resources/js/wordsearch-resize.js"></script>

</head>
<body>

<div id="wrap">
    <div id="main-content">
        <h1>KYKYS.CN</h1>
        <p>
            Just a personal website for kuangye.
        </p>
        <p>
            All of the site contains his collection.Maybe something not abstract you now.Don't upset.I'll do better soon.
        </p>
        <p>
            books / foods / poems / photos .etc
        </p>
        <div id="search">
            <form action="${ctx}/s">
                <input type="text" placeholder="Search" name="s">
                <button type="submit" class="input-search"><i class="fa fa-search"></i>
                </button>
            </form>
        </div>
        <div id="navigation">
            <a class="navigation" href="${ctx}">Home</a>
            <%--<a class="navigation" href="${ctx}/about">About Us</a>--%>
            <!-- <a class="navigation" href="">Site Map</a>
            <a class="navigation" href="">Contact</a> -->
            <!-- <a class="navigation" href="http://twitter.com/SeptimusFossett"> <i class="fa fa-twitter"></i>
            </a> -->
            <a class="navigation" href="https://google.com">
                <i class="fa fa-google-plus"></i>
            </a>
        </div>
    </div>
</div>

</body>
</html>