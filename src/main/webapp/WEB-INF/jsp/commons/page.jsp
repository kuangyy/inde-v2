<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/v3/commons/taglibs.jsp"%>

<nav class="text-center">
	<ul class="pagination">

        <c:forEach var="itemPage" begin="${pageWeb.startIndex}" end="${pageWeb.endIndex}" varStatus="status">
            <c:if test="${status.first && pageWeb.pageIndex > 1}">
                <li>
                    <a href="javascript:goForm(${pageWeb.pageIndex-1})">
                        上一页
                    </a>
                </li>
            </c:if>
            <c:if test="${status.first && pageWeb.pageIndex == 1}">
                <li class="disabled">
                    <a href="javascript:void(0);">
                        上一页
                    </a>
                </li>
            </c:if>
            <c:if test="${status.first && pageWeb.startIndex > 1 && itemPage > 1}">
                <li><a href="javascript:goForm(1)">1</a></li>
            </c:if>
            <c:if test="${status.first && pageWeb.startIndex > 2 && itemPage > 2}">
                <li class="pg-ellipsis"><a>…</a></li>
            </c:if>
            <c:if test="${itemPage==pageWeb.pageIndex}">
                <li class="active"><a href="javascript:void(0)">${itemPage}</a></li>
            </c:if>
            <c:if test="${itemPage!=pageWeb.pageIndex}">
                <li><a href="javascript:goForm(${itemPage})">${itemPage}</a></li>
            </c:if>
            <c:if test="${status.last && itemPage < pageWeb.pageCount-1}">
                <li class="pg-ellipsis"><a>…</a></li>
            </c:if>
            <c:if test="${status.last && itemPage < pageWeb.pageCount}">
                <li><a href="javascript:goForm(${pageWeb.pageCount})">${pageWeb.pageCount}</a></li>
            </c:if>
            <c:if test="${status.last && pageWeb.pageIndex < pageWeb.pageCount}">
                <li>
                    <a href="javascript:goForm(${pageWeb.pageIndex+1})">
                        下一页
                    </a>
                </li>
            </c:if>
            <c:if test="${status.last && pageWeb.pageIndex == pageWeb.pageCount}">
                <li class="disabled">
                    <a href="javascript:void(0);">
                        下一页
                    </a>
                </li>
            </c:if>
        </c:forEach>

	</ul>
</nav>