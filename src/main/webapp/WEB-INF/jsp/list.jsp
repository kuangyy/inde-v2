<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/jsp/commons/taglibs.jsp"%>
<html>
<head>

   <jsp:include page="/WEB-INF/jsp/commons/resources.jsp"/>

    <title>狂or野-你喜欢哪一个我</title>
    <meta name="description" content="ky's home page">
    <meta name="author" content="kuangye">
</head>
<body>
    
    <jsp:include page="/WEB-INF/jsp/commons/header_dark.jsp"/>

    <div class="content">

        <div class="bd-pageheader text-xs-center text-sm-left">
            <div class="container">
                <h1>Posts</h1>
                <p class="lead">
                    夜不炳烛则昧,冬不御裘则寒,渡河而乘陆车者危,易证而尝旧方者死
                </p>
                <hr>
                <div id="carbonads">
                    <div class="row">
                        <div class="offset-xs-2 col-xs-8">
                            <form class="form-inline text-sx-center" action="${ctx}/s">
                                <div class="input-group">
                                    <input type="text" class="form-control" aria-label="Text input with segmented button dropdown" name="wd" value="${wd}"
                                           placeholder="Search">
                                    <div class="input-group-btn">
                                        <button class="btn btn-outline-inverse btn-outline-gold" type="submit">Search</button>
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>


        <div class="container">

            <ol class="breadcrumb">
                <li class="breadcrumb-item">Search results : (${pageWeb.count}) matches. </li>
            </ol>


            <div class="list">
                <div class="card-columns">

                    <c:forEach items="${postsModelList}" var="posts" varStatus="varStatus">
                        <%--<div class="card card-inverse">--%>
                            <%--<img class="card-img" width="100%" height="200px" src="<c:choose><c:when test="${posts.pic != null}">${posts.pic}</c:when><c:otherwise>${ctx}/resources/img/default.png</c:otherwise></c:choose>" alt="Card image">--%>
                            <%--<div class="card-img-overlay">--%>
                                <%--<h4 class="card-title">--%>
                                    <%--<c:choose>--%>
                                        <%--<c:when test="${fn:length(posts.title) > 12}">--%>
                                            <%--${fn:substring(posts.title,0,12)}...--%>
                                        <%--</c:when>--%>
                                        <%--<c:otherwise>--%>
                                            <%--${posts.title}--%>
                                        <%--</c:otherwise>--%>
                                    <%--</c:choose>--%>
                                <%--</h4>--%>

                                <%--<p class="card-text" title="${posts.summary}">--%>
                                <%--<c:choose>--%>
                                    <%--<c:when test="${fn:length(posts.summary) > 50}">--%>
                                        <%--${fn:substring(posts.summary,0,50)}...--%>
                                    <%--</c:when>--%>
                                    <%--<c:otherwise>--%>
                                        <%--${posts.summary}--%>
                                    <%--</c:otherwise>--%>
                                <%--</c:choose>--%>
                                <%--</p>--%>
                                <%--<p class="card-text">--%>
                                    <%--<small class="text-inverse pull-right"><fmt:formatDate value="${posts.publishTime}" pattern="yy-MM-dd HH:mm"></fmt:formatDate> </small>--%>
                                <%--</p>--%>
                            <%--</div>--%>
                        <%--</div>--%>

                        <c:choose>
                            <c:when test="${posts.pic != null}">
                                <a href="${ctx}/p/${posts.id}" target="_blank">
                                <div class="card card-inverse">
                                    <img class="card-img" width="100%" height="200px" src="${posts.pic}" alt="Card image">
                                    <div class="card-img-overlay">
                                        <h3 class="card-title" title="${posts.title}">
                                            <c:choose>
                                                <c:when test="${fn:length(posts.title) > 12}">
                                                    ${fn:substring(posts.title,0,12)}...
                                                </c:when>
                                                <c:otherwise>
                                                    ${posts.title}
                                                </c:otherwise>
                                            </c:choose>
                                        </h3>
                                        <p class="card-text" title="${posts.summary}">
                                            <c:choose>
                                                <c:when test="${fn:length(posts.summary) > 50}">
                                                    ${fn:substring(posts.summary,0,50)}...
                                                </c:when>
                                                <c:otherwise>
                                                    ${posts.summary}
                                                </c:otherwise>
                                            </c:choose>
                                        </p>
                                        <p class="card-text">
                                            <small class="text-inverse pull-right">
                                                <i class="fa fa-heart" aria-hidden="true"></i> ${posts.likeCount}
                                                <i class="fa fa-eye" aria-hidden="true"></i> ${posts.viewCount}
                                            </small>
                                        </p>
                                        <p class="card-text">
                                            <small class="text-inverse pull-right"> <i class="fa fa-clock-o" aria-hidden="true"></i> <fmt:formatDate value="${posts.publishTime}" pattern="yy-MM-dd HH:mm"/> </small>
                                        </p>
                                    </div>
                                </div>
                                </a>
                            </c:when>
                            <c:otherwise>
                                <div class="card card-inverse" style="background-color: #333; border-color: #333;">
                                    <div class="card-block">
                                        <h3 class="card-title" title="${posts.title}">
                                            <c:choose>
                                                <c:when test="${fn:length(posts.title) > 12}">
                                                    ${fn:substring(posts.title,0,12)}...
                                                </c:when>
                                                <c:otherwise>
                                                    ${posts.title}
                                                </c:otherwise>
                                            </c:choose>
                                        </h3>
                                        <p class="card-text" title="${posts.summary}">
                                            <c:choose>
                                                <c:when test="${fn:length(posts.summary) > 50}">
                                                    ${fn:substring(posts.summary,0,50)}...
                                                </c:when>
                                                <c:otherwise>
                                                    ${posts.summary}
                                                </c:otherwise>
                                            </c:choose>
                                        </p>
                                        <p class="card-text">
                                            <small class="text-inverse pull-right">
                                                <i class="fa fa-heart" aria-hidden="true"></i> ${posts.likeCount}
                                                <i class="fa fa-eye" aria-hidden="true"></i> ${posts.viewCount}
                                            </small>
                                        </p>
                                        <p class="card-text">
                                            <small class="text-inverse pull-right"> <i class="fa fa-clock-o" aria-hidden="true"></i> <fmt:formatDate value="${posts.publishTime}" pattern="yy-MM-dd HH:mm"/> </small>
                                        </p>
                                        <a href="${ctx}/p/${posts.id}" target="_blank" class="btn btn-primary">View</a>
                                    </div>
                                </div>
                            </c:otherwise>
                        </c:choose>

                    </c:forEach>

                </div>

                <jsp:include page="/WEB-INF/jsp/commons/page.jsp"/>

            </div>


        </div>




    </div>


    <jsp:include page="/WEB-INF/jsp/commons/footer.jsp"/>

</body>
</html>
