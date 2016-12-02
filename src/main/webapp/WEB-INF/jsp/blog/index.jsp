<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/jsp/commons/taglibs.jsp" %>
<!DOCTYPE html>
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
        <p>${motto.name}</p>

        <div class="row">
            <div class="offset-xs-5 col-xs-5">
                <form class="form-inline text-sx-center" action="${ctx}/blog/s">
                    <div class="input-group">
                        <input style="display: none" type="text" class="form-control" aria-label="Text input with segmented button dropdown" name="wd" value="${wd}"
                               placeholder="Search">
                        <div class="input-group-btn">
                            <button class="btn btn-outline-inverse btn-outline-gold" type="submit">Search</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
        <script>
            $(function() {
                $('form').on('mouseover', function() {
                    $(this).parent()
                            .removeClass("offset-xs-5").removeClass("col-xs-5")
                            .addClass("offset-xs-2").addClass("col-xs-9");
                    $(this).find("input").show();

                });

            });
        </script>
    </div>


    <c:forEach var="tags" items="${hotTagList}" varStatus="varStatus">

        <ol class="breadcrumb">
            <li class="breadcrumb-item">${tags.name} </li><span class="pull-right"><a href="${ctx}/tag/${tags.id}"> more... </a></span>
        </ol>
        <div class="card-deck-wrapper">
            <div class="card-deck">

                <c:forEach var="posts" items="${tags.postsModelList}" varStatus="varStatus">

                    <div class="card">
                        <c:set var="defaultImg" value="${ctx}/resources/images/a.png"/>
                        <c:set var="img" value="${posts.pic==null?defaultImg:posts.pic}"/>
                        <img class="card-img-top" src="${img}" alt="${posts.title}">
                        <div class="card-block">
                            <a href="${ctx}/blog/p/${posts.id}" target="_blank">
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
                            </a>
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
                                    <i class="fa fa-clock-o" aria-hidden="true"></i> <fmt:formatDate value="${posts.publishTime}" pattern="yy-MM-dd HH:mm"/>
                                    <i class="fa fa-heart" aria-hidden="true"></i> ${posts.likeCount}
                                    <i class="fa fa-eye" aria-hidden="true"></i> ${posts.viewCount}
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
