<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/jsp/commons/taglibs.jsp"%>
<html>
<head>

   <jsp:include page="/WEB-INF/jsp/commons/resources.jsp"/>

    <title> Tags - 狂or野-你喜欢哪一个我</title>
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
                            <form class="form-inline text-sx-center" action="${ctx}/blog/s">
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
                <li class="breadcrumb-item"> ${fn:length(tagsModelList)} tags. order by posts count. </li>
            </ol>


            <div class="taglist">

                    <c:forEach items="${tagsModelList}" var="tags" varStatus="varStatus">

                        <a class="d-inline-block bg-primary" href="${ctx}/tag/${tags.name}" target="_blank" ><span class="">${tags.name}</span></a>

                    </c:forEach>


                <%--<script>var pageUrl = "/list";</script>--%>
                <%--<jsp:include page="/WEB-INF/jsp/commons/page.jsp"/>--%>

            </div>


        </div>

    </div>

    <jsp:include page="/WEB-INF/jsp/commons/footer.jsp"/>

</body>
</html>
