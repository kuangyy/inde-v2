<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/jsp/commons/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>

    <jsp:include page="/WEB-INF/jsp/game/commons/resources.jsp"/>
    <link rel="stylesheet" href="${ctx}/resources/plugins/materialize/css/ghpages-materialize.css">

    <title>逃离剧本-狂or野-你喜欢哪一个我</title>
    <meta name="description" content="ky's game page">
    <meta name="author" content="kuangye">

</head>
<body>

<header>
    <nav class="top-nav">
        <div class="container">
            <div class="nav-wrapper"><a class="page-title">Chips</a></div>
        </div>
    </nav>

    <jsp:include page="/WEB-INF/jsp/game/admin/leftMenu.jsp"></jsp:include>
    
</header>

<main>
    <div class="container">
    <div class="row">

        <div class="col s12 m12">

            <div class="section scrollspy">

                <a class="waves-effect waves-light btn" href="${ctx}/addDrama">添加</a>
                <br>
                <h4>jQuery Plugin Options</h4>
                <table class="table highlight">
                    <thead>
                    <tr>
                        <th>Name</th>
                        <th>Description</th>
                        <th>Status</th>
                        <th>Time</th>
                        <th>Action</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="drama" items="${dramaModelList}">
                        <tr>
                            <td>${drama.name}</td>
                            <td>${drama.description}</td>
                            <td>${drama.status}</td>
                            <td>${drama.lastUpdateTime}</td>
                            <td>
                                <a class="waves-effect waves-light btn" href="${ctx}/game/setting/editDrama/${drama.id}">编辑</a>

                            </td>
                        </tr>
                    </c:forEach>

                        <tr></tr>
                    </tbody>
                </table>

                <script>var pageUrl = "/game/setting";</script>
                <jsp:include page="/WEB-INF/jsp/game/admin/page.jsp"></jsp:include>

            </div>

        </div>

    </div>
</div>
</main>   


<jsp:include page="/WEB-INF/jsp/game/admin/footer.jsp"></jsp:include>

<!--  Scripts-->
<!-- 语法高亮 -->
<script src="${ctx}/resources/plugins/prism/prism.js"></script>
<!-- 搜索索引 -->
<script src="${ctx}/resources/plugins/jade/lunr.min.js"></script>
<script src="${ctx}/resources/plugins/jade/search.js"></script>

<script src="${ctx}/resources/js/game/init.js"></script>

</body>
</html>
