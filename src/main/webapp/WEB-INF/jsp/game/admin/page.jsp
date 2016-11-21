<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/commons/taglibs.jsp"%>

<ul class="pagination">
    <c:forEach var="itemPage" begin="${pageWeb.startIndex}" end="${pageWeb.endIndex}" varStatus="status">
        <c:if test="${status.first && pageWeb.pageIndex > 1}">
            <li class="waves-effect"><a href="javascript:goForm(${pageWeb.pageIndex-1})" ><i class="fa fa-chevron-left" aria-hidden="true"></i></a></li>
        </c:if>
        <c:if test="${status.first && pageWeb.pageIndex == 1}">
            <li class="disabled"><a><i class="fa fa-chevron-left" aria-hidden="true"></i></a></li>
        </c:if>
        <c:if test="${status.first && pageWeb.startIndex > 1 && itemPage > 1}">
            <li class="waves-effect"><a href="javascript:goForm(1)">1</a></li>
        </c:if>
        <c:if test="${status.first && pageWeb.startIndex > 2 && itemPage > 2}">
            <li class="waves-effect"><a>…</a></li>
        </c:if>
        <c:if test="${itemPage==pageWeb.pageIndex}">
            <li class="waves-effect active"><a>${itemPage}</a></li>
        </c:if>
        <c:if test="${itemPage!=pageWeb.pageIndex}">
            <li class="waves-effect "><a href="javascript:goForm(${itemPage})">${itemPage}</a></li>
        </c:if>
        <c:if test="${status.last && itemPage < pageWeb.pageCount-1}">
            <li class="waves-effect"><a>…</a></li>
        </c:if>
        <c:if test="${status.last && itemPage < pageWeb.pageCount}">
            <li class="waves-effect"><a href="javascript:goForm(${pageWeb.pageCount})">${pageWeb.pageCount}</a></li>
        </c:if>
        <c:if test="${status.last && pageWeb.pageIndex < pageWeb.pageCount}">
            <li class="waves-effect"><a href="javascript:goForm(${pageWeb.pageIndex+1})"><i class="fa fa-chevron-right" aria-hidden="true"></i></a></li>
        </c:if>
        <c:if test="${status.last && pageWeb.pageIndex == pageWeb.pageCount}">
            <li class="disabled"><a><i class="fa fa-chevron-right" aria-hidden="true"></i></a></li>
        </c:if>
    </c:forEach>
</ul>

<script>
    /*************************分页*******************************/

    function goForm(page){
        postUrl($.baseData.basePath+pageUrl,{pageIndex:page})
    }
</script>