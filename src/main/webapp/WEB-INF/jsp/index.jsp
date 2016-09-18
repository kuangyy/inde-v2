<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/jsp/commons/taglibs.jsp"%>
<html>
<head>

   <jsp:include page="/WEB-INF/jsp/commons/resources.jsp"></jsp:include>

    <title>狂or野-你喜欢哪一个我</title>
    <meta name="description" content="ky's home page">
    <meta name="author" content="kuangye">
</head>
<body>
    
    <jsp:include page="/WEB-INF/jsp/commons/header.jsp"></jsp:include>


    <div class="container">

        <div class="jumbotron">
            <h1 class="display-3">Hello, world!</h1>
            <p class="lead">This is a simple hero unit, a simple jumbotron-style component for calling extra attention to featured content or information.</p>
            <hr class="m-y-2">
            <p>It uses utility classes for typography and spacing to space content out within the larger container.</p>
            <p class="lead">

            <div class="row">
                <div class="offset-xs-2 col-xs-8">
                    <form class="form-inline text-sx-center">
                        <div class="input-group">
                            <input type="text" class="form-control" aria-label="Text input with segmented button dropdown" placeholder="Search">
                            <div class="input-group-btn">
                                <button class="btn btn-outline-inverse btn-outline-gold" type="submit">Search</button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
            </p>
        </div>

        <div class="card-deck-wrapper">
            <div class="card-deck">
                <div class="card">
                    <img class="card-img-top" src="${ctx}/resources/images/a.png" alt="Card image cap">
                    <div class="card-block">
                        <h4 class="card-title">Card title</h4>
                        <p class="card-text">This is a longer card with supporting text below as a natural lead-in to additional content. This content is a little bit longer.</p>
                        <p class="card-text"><small class="text-muted">Last updated 3 mins ago</small></p>
                    </div>
                </div>
                <div class="card">
                    <img class="card-img-top" src="${ctx}/resources/images/a.png" alt="Card image cap">
                    <div class="card-block">
                        <h4 class="card-title">Card title</h4>
                        <p class="card-text">This card has supporting text below as a natural lead-in to additional content.</p>
                        <p class="card-text"><small class="text-muted">Last updated 3 mins ago</small></p>
                    </div>
                </div>
                <div class="card">
                    <img class="card-img-top" src="${ctx}/resources/images/a.png" alt="Card image cap">
                    <div class="card-block">
                        <h4 class="card-title">Card title</h4>
                        <p class="card-text">This is a wider card with supporting text below as a natural lead-in to additional content. This card has even longer content than the first to show that equal height action.</p>
                        <p class="card-text"><small class="text-muted">Last updated 3 mins ago</small></p>
                    </div>
                </div>
            </div>
        </div>



    </div>


    <jsp:include page="/WEB-INF/jsp/commons/footer.jsp"></jsp:include>

</body>
</html>
