<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/jsp/commons/taglibs.jsp"%>
<html>
<head>

   <jsp:include page="/WEB-INF/jsp/commons/resources.jsp"/>

    <title>${book.name}-狂or野-你喜欢哪一个我</title>
    <meta name="description" content="${book.summary}">
    <meta name="author" content="kuangye">
</head>
<body>
    
    <jsp:include page="/WEB-INF/jsp/commons/header_dark.jsp"/>

    <div class="content">

        <div class="bd-pageheader text-xs-center text-sm-left">
            <div class="container">
                <h1>${book.name}</h1>
                <p class="lead">
                    ${book.summary}
                </p>
                <p class="lead">
                    发送邮件与我联系（ky.kys.cn@hotmail.com）即可获取本书~
                </p>

                <div class="pull-right">
                    <span> <i class="fa fa-male" aria-hidden="true"></i> [ ${book.author} ]</span>
                    <span> <i class="fa fa-clock-o" aria-hidden="true"></i> <fmt:formatDate type="both" value="${book.updateTime}" pattern="yyyy-MM-dd HH:mm"/> </span>
                </div>
                <hr>
                <div class="pull-left ">
                    <span>
                        <i class="fa fa-first-order" aria-hidden="true"></i>
                        [
                            <c:forEach var="tag" items="${book.tagsModelList}" varStatus="varStatus">
                                <a href="${ctx}/tag/${tag.name}" target="_blank"><span class="">${tag.name}</span></a>
                                <c:if test="${!varStatus.last}"> | </c:if>
                            </c:forEach>
                         ]
                    </span>
                </div>
            </div>
        </div>


        <div class="container">
            <div class="d-content markdown">
               ${book.content}
            </div>
        </div>


    </div>


    <jsp:include page="/WEB-INF/jsp/commons/footer.jsp"/>

</body>
</html>
