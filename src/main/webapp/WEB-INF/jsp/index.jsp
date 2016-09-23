<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/jsp/commons/taglibs.jsp" %>
<html>
<head>

    <jsp:include page="/WEB-INF/jsp/commons/resources.jsp"/>

    <title>狂or野-你喜欢哪一个我</title>
    <meta name="description" content="ky's home page">
    <meta name="author" content="kuangye">
</head>
<body>

<jsp:include page="/WEB-INF/jsp/commons/header.jsp"/>


<div class="container">

    <div class="jumbotron">
        <h1 class="display-3">Hello, world!</h1>
        <p class="lead">This is a simple hero unit, a simple jumbotron-style component for calling extra attention to
            featured content or information.</p>
        <hr class="m-y-2">
        <p>It uses utility classes for typography and spacing to space content out within the larger container.</p>

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


    <c:forEach var="tags" items="${hotTagList}" varStatus="varStatus">

        <ol class="breadcrumb">
            <li class="breadcrumb-item">${tags.name} </li><span class="pull-right"><a> more... </a></span>
        </ol>
        <div class="card-deck-wrapper">
            <div class="card-deck">

                <c:forEach var="posts" items="${tags.postsModelList}" varStatus="varStatus">

                    <div class="card">
                        <img class="card-img-top" src="${ctx}/resources/images/a.png" alt="Card image cap">
                        <div class="card-block">
                            <h4 class="card-title" title="${posts.title}">
                                <c:choose>
                                    <c:when test="${fn:length(posts.title) > 12}">
                                        ${fn:substring(posts.title,0,12)}...
                                    </c:when>
                                    <c:otherwise>
                                        ${posts.title}
                                    </c:otherwise>
                                </c:choose>
                            </h4>
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
                                <small class="text-inverse pull-right">
                                    <i class="fa fa-clock-o" aria-hidden="true"></i> <fmt:formatDate value="${posts.publishTime}" pattern="yy-MM-dd HH:mm"/>
                                </small>
                            </p>
                        </div>
                    </div>

                </c:forEach>

            </div>
        </div>

    </c:forEach>





</div>


<jsp:include page="/WEB-INF/jsp/commons/footer.jsp"/>

</body>
</html>
