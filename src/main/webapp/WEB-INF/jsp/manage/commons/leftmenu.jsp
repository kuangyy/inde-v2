<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/jsp/commons/taglibs.jsp"%>

<div class="ge aom">
    <nav class="aot">
        <div class="aon">
            <button class="amy amz aoo" type="button" data-toggle="collapse" data-target="#nav-toggleable-sm">
                <span class="ct">Toggle nav</span>
            </button>
            <a class="aop cn" href="../index.html">
                <span class="bv act aoq"></span>
            </a>
        </div>

        <div class="" id="nav-toggleable-sm">
            <form class="aor">
                <div class="input-group">
                    <input class="form-control" type="text" placeholder="Search...">
                    <span class="input-group-btn">
                        <button class="btn btn-default" type="submit" >
                            <i class="fa fa-search" aria-hidden="true"></i>
                        </button>
                    </span>

                </div>
            </form>
            <ul class="nav of nav-stacked">
                <li class="tq">Dashboards</li>
                <li>
                    <a href="${ctx}/manage/index">Overview</a>
                </li>
                <li>
                    <a href="${ctx}/manage/posts">Posts</a>
                </li>
                <li>
                    <a href="../fluid/index.html">Fluid layout</a>
                </li>
                <li>
                    <a href="../icon-nav/index.html">Icon nav</a>
                </li>

                <li class="tq">More</li>
                <li>
                    <a href="../docs/index.html">
                        Toolkit docs
                    </a>
                </li>
                <li>
                    <a href="http://getbootstrap.com" target="blank">
                        Bootstrap docs
                    </a>
                </li>
                <li class="active">
                    <a href="../light/index.html">Light UI</a>
                </li>
                <li>
                    <a href="#docsModal" data-toggle="modal">
                        Example modal
                    </a>
                </li>
            </ul>
            <hr class="rw aky">
        </div>
    </nav>
</div>
