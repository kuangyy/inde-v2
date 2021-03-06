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


<%--<div class="container">--%>
    <div class="col-md-3">
        <jsp:include page="/WEB-INF/jsp/manage/commons/leftmenu.jsp"/>
    </div>
    <div class="col-md-9">

        <div class="container">

            <div class="jumbotron">
                <%--<h1 class="display-3">Hello, world!</h1>--%>
                <%--<p class="lead">This is a simple hero unit, a simple jumbotron-style component for calling extra--%>
                    <%--attention to featured content or information.</p>--%>
                <%--<hr class="m-y-2">--%>
                <%--<p>It uses utility classes for typography and spacing to space content out within the larger--%>
                    <%--container.</p>--%>
                <%--<p class="lead">--%>

                <%--<div class="row">--%>
                    <%--<div class="offset-xs-2 col-xs-8">--%>
                        <%--<form class="form-inline text-sx-center">--%>
                            <%--<div class="input-group">--%>
                                <%--<input type="text" class="form-control"--%>
                                       <%--aria-label="Text input with segmented button dropdown" placeholder="Search">--%>
                                <%--<div class="input-group-btn">--%>
                                    <%--<button class="btn btn-outline-inverse btn-outline-gold" type="submit">Search--%>
                                    <%--</button>--%>
                                <%--</div>--%>
                            <%--</div>--%>
                        <%--</form>--%>
                    <%--</div>--%>
                <%--</div>--%>
            </div>

                <div class="container">
                    <div class="col-xs-12">
                        <div class="pull-right">
                            <a class="btn btn-success" href="${ctx}/manage/motto/add">Add</a>
                        </div>
                    </div>
                    <div class="col-xs-12">
                        <table class="col-xs-12">
                            <thead>
                            <tr>
                                <th>ID</th>
                                <th>Motto</th>
                                <th>Translate</th>
                                <th>Options</th>
                            </tr>
                            </thead>
                            <tbody>
                            <%--<td>--%>
                            <%--<p><code class="highlighter-rouge">.form-group</code></p>--%>
                            <%--</td>--%>
                            <%--<span class="text-muted">N/A</span>--%>

                            <c:forEach var="motto" items="${mottoModelList}" varStatus="varStatus">
                                <tr>
                                    <td>${motto.id}</td>
                                    <td><span title="${motto.name}" style="max-width: 5em">${motto.name}</span></td>
                                    <td>${motto.translate}</td>
                                    <td> <a class="btn btn-success" href="${ctx}/manage/motto/edit?id=${motto.id}">Edit</a></td>
                                </tr>
                            </c:forEach>

                            </tbody>
                        </table>

                        <script>var pageUrl = "/manage/motto";</script>
                        <jsp:include page="/WEB-INF/jsp/commons/page.jsp"/>
                    </div>
                </div>


        </div>

    </div>

<%--</div>--%>


<jsp:include page="/WEB-INF/jsp/manage/commons/footer.jsp"/>

</body>
</html>