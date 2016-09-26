<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/commons/taglibs.jsp"%>

<nav aria-label="Page navigation" class="pull-right">
    <ul class="pagination">

        <c:forEach var="itemPage" begin="${pageWeb.startIndex}" end="${pageWeb.endIndex}" varStatus="status">
            <c:if test="${status.first && pageWeb.pageIndex > 1}">
                <li class="page-item">
                    <a class="page-link" href="javascript:goForm(${pageWeb.pageIndex-1})" aria-label="Previous">
                        <span aria-hidden="true">&laquo;</span>
                        <span class="sr-only">上一页(Previous)</span>
                    </a>
                </li>
            </c:if>
            <c:if test="${status.first && pageWeb.pageIndex == 1}">
                <li class="page-item disabled">
                    <a class="page-link" href="javascript:;" aria-label="Previous">
                        <span aria-hidden="true">&laquo;</span>
                        <span class="sr-only">上一页(Previous)</span>
                    </a>
                </li>
            </c:if>
            <c:if test="${status.first && pageWeb.startIndex > 1 && itemPage > 1}">
                <li class="page-item"><a class="page-link" href="javascript:goForm(1)">1</a></li>
            </c:if>
            <c:if test="${status.first && pageWeb.startIndex > 2 && itemPage > 2}">
                <li class="page-item"><a class="page-link">…</a></li>
            </c:if>
            <c:if test="${itemPage==pageWeb.pageIndex}">
                <li class="page-item active"><a class="page-link">${itemPage}</a></li>
            </c:if>
            <c:if test="${itemPage!=pageWeb.pageIndex}">
                <li class="page-item"><a class="page-link" href="javascript:goForm(${itemPage})">${itemPage}</a></li>
            </c:if>
            <c:if test="${status.last && itemPage < pageWeb.pageCount-1}">
                <li class="page-item"><a class="page-link">…</a></li>
            </c:if>
            <c:if test="${status.last && itemPage < pageWeb.pageCount}">
                <li class="page-item"><a class="page-link" href="javascript:goForm(${pageWeb.pageCount})">${pageWeb.pageCount}</a></li>
            </c:if>
            <c:if test="${status.last && pageWeb.pageIndex < pageWeb.pageCount}">
                <li class="page-item">
                    <a class="page-link" href="javascript:goForm(${pageWeb.pageIndex+1})" aria-label="Next">
                        <span aria-hidden="true">&raquo;</span>
                        <span class="sr-only">下一页(Next)</span>
                    </a>
                </li>
            </c:if>
            <c:if test="${status.last && pageWeb.pageIndex == pageWeb.pageCount}">
                <li class="page-item disabled">
                    <a class="page-link" href="javascript:;" aria-label="Next">
                        <span aria-hidden="true">&raquo;</span>
                        <span class="sr-only">下一页(Next)</span>
                    </a>
                </li>
            </c:if>
        </c:forEach>

    </ul>
</nav>


<script>
    /*************************分页*******************************/

    function goForm(page){
        postUrl($.baseData.basePath+pageUrl,{pageIndex:page})
    }
</script>