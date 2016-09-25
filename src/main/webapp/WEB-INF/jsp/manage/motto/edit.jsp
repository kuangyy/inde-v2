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
    
    <jsp:include page="/WEB-INF/jsp/commons/header.jsp"/>


    <div class="col-md-3">
        <jsp:include page="/WEB-INF/jsp/manage/commons/leftmenu.jsp"/>
    </div>
    <div class="col-md-9">

        <div class="container">

            <div class="jumbotron">
                <%--<h1 class="display-3">Hello, world!</h1>--%>
                <%--<p class="lead">This is a simple hero unit, a simple jumbotron-style component for calling extra attention to featured content or information.</p>--%>
                <%--<hr class="m-y-2">--%>
                <%--<p>It uses utility classes for typography and spacing to space content out within the larger container.</p>--%>
                <%--<p class="lead">--%>

                <%--<div class="row">--%>
                    <%--<div class="offset-xs-2 col-xs-8">--%>
                        <%--<form class="form-inline text-sx-center">--%>
                            <%--<div class="input-group">--%>
                                <%--<input type="text" class="form-control" aria-label="Text input with segmented button dropdown" placeholder="Search">--%>
                                <%--<div class="input-group-btn">--%>
                                    <%--<button class="btn btn-outline-inverse btn-outline-gold" type="submit">Search</button>--%>
                                <%--</div>--%>
                            <%--</div>--%>
                        <%--</form>--%>
                    <%--</div>--%>
                <%--</div>--%>
            </div>

           <form id="submitForm">
               <c:if test="${motto != null}">
                   <input type="hidden" name="update" value="true">
                   <input type="hidden" name="id" value="${motto.id}">
               </c:if>

               <div class="form-group row">
                   <label class="col-xs-2 col-form-label">Motto</label>
                   <div class="col-xs-10">
                       <input class="form-control" type="text" name="name" value="${motto.name}" >
                   </div>
               </div>
               <div class="form-group row">
                   <label class="col-xs-2 col-form-label">Summary</label>
                   <div class="col-xs-10">
                       <input class="form-control" type="text" name="translate" value="${motto.translate}" >
                   </div>
               </div>
               </div>
               <div class="form-group row">
                   <div class="col-xs-5">
                       <button class="btn btn-info" type="button" id="commit" >Post</button>
                   </div>
               </div>
           </form>


        </div>
    </div>


    <jsp:include page="/WEB-INF/jsp/commons/footer.jsp"/>

    <script>
        $(function(){
            $("#commit").on("click",function(){
                $.ajax({
                    url : $.baseData.basePath+"/manage/motto/updateDo",
                    type: "POST",
                    cache : false,
                    data :$("#submitForm").serialize(),
                    dataType : "json",
                    success : function(data) {
                        if(data.status){
                            alertify.tips_success("添加成功");
                        }else{
                            alertify.tips_error("添加失败");
                        }
                    },
                    error : function(){
                        alertify.tips_error("服务器异常");
                    }
                });
            })
        })
    </script>

</body>
</html>
