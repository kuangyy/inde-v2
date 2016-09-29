<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/jsp/commons/taglibs.jsp"%>
<html>
<head>

   <jsp:include page="/WEB-INF/jsp/commons/resources.jsp"/>

    <title>PHOTOS - 狂or野-你喜欢哪一个我</title>
    <meta name="description" content="ky's home page">
    <meta name="author" content="kuangye">
</head>
<body>
    
    <jsp:include page="/WEB-INF/jsp/commons/header_dark.jsp"/>

    <div class="content">

        <div class="bd-pageheader text-xs-center text-sm-left">
            <div class="container">
                <h1>Photos</h1>
                <%--<p class="lead">--%>
                    <%--夜不炳烛则昧,冬不御裘则寒,渡河而乘陆车者危,易证而尝旧方者死--%>
                <%--</p>--%>
                <hr>
            </div>
        </div>


        <div class="container">

            <ol class="breadcrumb">
                <li class="breadcrumb-item"> We have ${pageWeb.count} Photos. </li>
            </ol>


            <div class="list">
                <div class="card-columns">

                    <c:forEach items="${photoModelList}" var="photo" varStatus="varStatus">

                                <%--<a href="${ctx}/p/${photo.url}+ld.jpg" target="_blank">--%>
                                <div class="card card-inverse">
                                    <img class="card-img"  src="${photo.url}ld.jpg" alt="Card image">
                                </div>
                                </a>

                    </c:forEach>

                </div>

                <script>var pageUrl = "/photos";</script>
                <jsp:include page="/WEB-INF/jsp/commons/page.jsp"/>

            </div>


        </div>




    </div>


    <jsp:include page="/WEB-INF/jsp/commons/footer.jsp"/>


    <style>
        .lime-dialog {
            position: fixed;
            width: 232px;
            height: auto;
            max-width: 400px;
            top: 50%;
            left: 50%;
            background-color: #fff;
            z-index: 1002;
            -webkit-box-shadow: 0 0 8px 2px rgba(0,0,0,0.1);
            box-shadow: 0 0 8px 2px rgba(0,0,0,0.1);
            -webkit-transform: translate(-50%, -50%);
            transform: translate(-50%, -50%);
        }
        .lime-dialog-mask {
            position: fixed;
            left: 0;
            top: 0;
            z-index: 110;
            width: 100%;
            height: 100%;
            background-color: #afbbb1;
            overflow: hidden;
            overflow-y: auto;
            opacity: .5;
            filter: alpha(opacity=50);
        }
        .img-show-dialog {
            width: auto;
            max-width: 800px;
            background: transparent;
            box-shadow: none;
        }
        .img-show-mask {
            background-color: #060606;
        }
        .img-show-dialog img {
            max-width: 800px;
            max-height: 500px;
        }
        .card img {
            width: 100%;
        }
        .card img:hover {
            cursor: pointer;
        }
    </style>
    <script>
        $(".card img").on("click",function(){
            $("#img-mask").show();
            $("#img-dialog").show(20);
            var url = $(event.target).attr("src");
//            $("#img-dialog").find("img").attr("src",url.substring(0,url.length-6)+"hd2.jpg");
            $("#img-dialog").find("img").attr("src",url);
        })
        $(document).on("click","#img-mask",function(){
            $("#img-mask").hide();
            $("#img-dialog").hide();
        })
    </script>
    <div class="lime-dialog-mask img-show-mask" id="img-mask" style="display: none"></div>
    <div class="lime-dialog img-show-dialog" id="img-dialog" style="display: none">
        <img src="">
    </div>

</body>
</html>
