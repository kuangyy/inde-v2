<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/jsp/commons/taglibs.jsp"%>

<!-- return top -->
<div id="topcontrol" class="fa fa-angle-up" title="返回顶部" style="bottom: 26px;"></div>
<script>
    $(document).ready(function() {
        // Code that uses jQuery's $ can follow here.
        var $topcontrol = $('#topcontrol');
        $(window).scroll(function() {
            $(window).scrollTop() > 100 ? $topcontrol.css({ bottom: "26px"}) : $topcontrol.css({bottom: "-100px"});
        });
        $topcontrol.click(function() {
            $('html, body').animate({scrollTop: '0px'}, 800);
            return false;
        });
    });
</script>
<!-- return top close -->