<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/jsp/commons/taglibs.jsp"%>
<html>
<head>

   <jsp:include page="/WEB-INF/jsp/commons/resources.jsp"/>

    <title><c:if test="${tag!=null}">${tag.name} - </c:if>狂or野-你喜欢哪一个我</title>
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
                            <form class="form-inline text-sx-center" action="${ctx}/books/s">
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
                <li class="breadcrumb-item"><c:if test="${tag!=null}">Tag : ${tag.name} - </c:if> Search results : (${pageWeb.count}) matches. </li>
            </ol>


            <div class="list">
                <div class="card-columns">

                    <c:forEach items="${bookModelList}" var="book" varStatus="varStatus">

                        <c:choose>
                            <c:when test="${book.pic != null}">
                                <a href="${ctx}/p/${book.id}" target="_blank">
                                <div class="card card-inverse">
                                    <img class="card-img" width="100%" height="200px" src="${book.pic}" alt="Card image">
                                    <div class="card-img-overlay">
                                        <h3 class="card-title" title="${book.name}">
                                            <c:choose>
                                                <c:when test="${fn:length(book.name) > 12}">
                                                    ${fn:substring(book.name,0,12)}...
                                                </c:when>
                                                <c:otherwise>
                                                    ${book.name}
                                                </c:otherwise>
                                            </c:choose>
                                        </h3>
                                        <p class="card-text" title="${book.summary}">
                                            <c:choose>
                                                <c:when test="${fn:length(book.summary) > 50}">
                                                    ${fn:substring(book.summary,0,50)}...
                                                </c:when>
                                                <c:otherwise>
                                                    ${book.summary}
                                                </c:otherwise>
                                            </c:choose>
                                        </p>
                                        <p class="card-text">
                                            <small class="text-inverse pull-right">
                                                <i class="fa fa-clock-o" aria-hidden="true"></i> <fmt:formatDate value="${book.updateTime}" pattern="yy-MM-dd HH:mm"/>
                                                <i class="fa fa-heart" aria-hidden="true"></i> ${book.likeCount}
                                                <i class="fa fa-eye" aria-hidden="true"></i> ${book.viewCount}
                                            </small>
                                        </p>
                                    </div>
                                </div>
                                </a>
                            </c:when>
                            <c:otherwise>
                                <div class="card card-inverse" style="background-color: #333; border-color: #333;">
                                    <div class="card-block">
                                        <h3 class="card-title" title="${book.name}">
                                            <%--<c:choose>--%>
                                                <%--<c:when test="${fn:length(book.name) > 12}">--%>
                                                    <%--${fn:substring(book.name,0,12)}...--%>
                                                <%--</c:when>--%>
                                                <%--<c:otherwise>--%>
                                                    ${book.name}
                                                <%--</c:otherwise>--%>
                                            <%--</c:choose>--%>
                                        </h3>
                                        <p class="card-text" title="${book.summary}">
                                            <c:choose>
                                                <c:when test="${fn:length(book.summary) > 50}">
                                                    ${fn:substring(book.summary,0,50)}...
                                                </c:when>
                                                <c:otherwise>
                                                    ${book.summary}
                                                </c:otherwise>
                                            </c:choose>
                                        </p>
                                        <p class="card-text">
                                            <small class="text-inverse pull-right">
                                                <i class="fa fa-clock-o" aria-hidden="true"></i> <fmt:formatDate value="${book.updateTime}" pattern="yy-MM-dd HH:mm"/>
                                                <i class="fa fa-heart" aria-hidden="true"></i> ${book.likeCount}
                                                <i class="fa fa-eye" aria-hidden="true"></i> ${book.viewCount}
                                            </small>
                                        </p>
                                        <a href="${ctx}/b/${book.id}" target="_blank" class="btn btn-primary">View</a>
                                    </div>
                                </div>
                            </c:otherwise>
                        </c:choose>

                    </c:forEach>

                </div>

                <script>var pageUrl = "/books/s";</script>
                <jsp:include page="/WEB-INF/jsp/commons/page.jsp"/>

            </div>


        </div>




    </div>


    <jsp:include page="/WEB-INF/jsp/commons/footer.jsp"/>

</body>
</html>
