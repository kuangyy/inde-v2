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
    
    <jsp:include page="/WEB-INF/jsp/commons/header_dark.jsp"></jsp:include>

    <div class="content">

        <div class="bd-pageheader text-xs-center text-sm-left">
            <div class="container">
                <h1>Examples</h1>
                <p class="lead">
                    Quickly get a project started with any of our examples ranging from using parts of the framework to custom components and layouts.
                </p>

                <div id="carbonads">
			    	<span>
			    		<span class="carbon-wrap">
			    			<a href="/" class="carbon-img" target="_blank"><img src="a.png" alt="" border="0" height="100" width="130" style="max-width:130px;"></a><a href="" class="carbon-text" target="_blank">Do You Want a Transparent Cloud Environment? Our Experts Can Help!</a>
		    			</span>
		    			<a href="http://carbonads.net/" class="carbon-poweredby" target="_blank">ads via Carbon</a>
	    			</span>
                </div>
            </div>
        </div>


        <div class="container">

            <ol class="breadcrumb">
                <li class="breadcrumb-item"><a href="#">Home</a></li>
                <li class="breadcrumb-item"><a href="#">Library</a></li>
                <li class="breadcrumb-item active">Data</li>
            </ol>


            <div class="card-deck-wrapper">
                <div class="card-deck">

                    <div class="card card-inverse">
                        <img class="card-img" src="a.png" alt="Card image">
                        <div class="card-img-overlay">
                            <h4 class="card-title">Card title</h4>
                            <p class="card-text">This is a wider card with supporting text below as a natural lead-in to additional content. This content is a little bit longer.</p>
                            <p class="card-text"><small class="text-muted">Last updated 3 mins ago</small></p>
                        </div>
                    </div>


                </div>
            </div>


            <jsp:include page="/WEB-INF/jsp/commons/page.jsp"></jsp:include>

        </div>




    </div>


    <jsp:include page="/WEB-INF/jsp/commons/footer.jsp"></jsp:include>

</body>
</html>
