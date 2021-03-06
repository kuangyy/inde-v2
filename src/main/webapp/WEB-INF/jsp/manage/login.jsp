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
                <h1>The Manage Center</h1>
                <p class="lead">
                    Easyer to manage the site.
                </p>
            </div>
        </div>


        <div class="container">

           <form action="${ctx}/manage/access" method="post">
               <div class="form-group">
                   <div class="input-group login-group">
                       <input class="form-control" name="password" placeholder="enter you token">
                       <span class="input-group-addon">remeber me</span>
                       <span class="input-group-addon">
                          <input type="checkbox" name="remember">
                       </span>
                       <span class="input-group-btn">
                           <button class="btn btn-info" >access</button>
                       </span>
                   </div>
               </div>
           </form>


            <div class="bd-callout bd-callout-warning">
                <h4 id="alternatives-to-hidden-labels">Warning</h4>
                <p> Just a small site don't hurt him please. <code class="highlighter-rouge">.sr-only</code> class. There are further alternative methods of providing a label for assistive technologies, such as the <code class="highlighter-rouge">aria-label</code>, <code class="highlighter-rouge">aria-labelledby</code> or <code class="highlighter-rouge">title</code> attribute. If none of these are present, assistive technologies may resort to using the <code class="highlighter-rouge">placeholder</code> attribute, if present, but note that use of <code class="highlighter-rouge">placeholder</code> as a replacement for other labelling methods is not advised.</p>
            </div>
        </div>


    </div>


    <jsp:include page="/WEB-INF/jsp/commons/footer.jsp"/>

</body>
</html>
