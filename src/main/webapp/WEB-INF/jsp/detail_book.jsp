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
                <h1>${posts.title}</h1>
                <p class="lead">
                    ${posts.summary}
                </p>
                <div class="pull-right">
                    <span> <i class="fa fa-male" aria-hidden="true"></i> [ ky ]</span>
                    <span> <i class="fa fa-clock-o" aria-hidden="true"></i> <fmt:formatDate type="both" value="${posts.publishTime}" pattern="yyyy-MM-dd HH:mm"></fmt:formatDate> </span>
                </div>
            </div>
        </div>


        <div class="container">
            <div class="d-content markdown">
               ${posts.contentModel.markdownContent}
            </div>
        </div>


    </div>


    <jsp:include page="/WEB-INF/jsp/commons/footer.jsp"/>

</body>
</html>
