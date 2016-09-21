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


    <div class="row">
        <div class="col-md-3">
            <jsp:include page="/WEB-INF/jsp/manage/commons/leftmenu.jsp"></jsp:include>
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
                   <c:if test="${posts != null}">
                       <input type="hidden" name="update" value="true">
                       <input type="hidden" name="id" value="${posts.id}">
                   </c:if>

                   <div class="form-group row">
                       <label class="col-xs-2 col-form-label">Title</label>
                       <div class="col-xs-10">
                           <input class="form-control" type="text" name="title" value="${posts.title}" >
                       </div>
                   </div>
                   <div class="form-group row">
                       <label class="col-xs-2 col-form-label">Summary</label>
                       <div class="col-xs-5">
                           <textarea class="form-control" name="summary" >${posts.summary}</textarea>
                       </div>
                   </div>
                   <div class="form-group row">
                       <label class="col-xs-2 col-form-label">PublishTime</label>
                       <div class="col-xs-5">
                           <input class="form-control" type="datetime-local" name="publishTimeString" placeholder="default publish now"
                                  value="<fmt:formatDate value="${posts.publishTime}" pattern="yyyy/MM/dd hh:mm:ss"></fmt:formatDate>">
                       </div>
                   </div>
                   <div class="form-group row">
                       <label class="col-xs-2 col-form-label">Thumbnail</label>
                       <div class="col-xs-5">
                           <input class="form-control" type="file" name="pic" value="${posts.pic}" >
                       </div>
                       <div class="col-xs-5">
                           <button class="btn btn-info" type="button" id="commit" >Post</button>
                       </div>
                   </div>
                   <div class="row">
                       <div class="col-xs-12">
                           <label class="label">Content</label>
                       </div>
                       <div class="col-md-5">
                        <textarea id="text-input" oninput="this.editor.update()" name="originContent"
                                  rows="10" cols="60">${posts.contentModel.originContent}</textarea>
                       </div>
                       <div class="col-md-5">
                           <div id="preview"></div>
                       </div>
                   </div>
               </form>


            </div>

        </div>

    </div>


    <jsp:include page="/WEB-INF/jsp/commons/footer.jsp"></jsp:include>

    <script>
        var exports = window;
    </script>
    <script src="${ctx}/resources/js/markdown.js"></script>
    <script>
        function Editor(input, preview) {
            this.update = function () {
                preview.innerHTML = Markdown.toHTML(input.value);
            };
            input.editor = this;
            this.update();
        }
        var $$ = function (id) { return document.getElementById(id); };
        new Editor($$("text-input"), $$("preview"));
    </script>
    <script>
        $(function(){
            $("#commit").on("click",function(){
                $.ajax({
                    url : $.baseData.basePath+"/manage/posts/updateDo",
                    type: "POST",
                    cache : false,
                    data :$("#submitForm").serialize()+"&markdownContent="+$("#preview").html()+"",
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
