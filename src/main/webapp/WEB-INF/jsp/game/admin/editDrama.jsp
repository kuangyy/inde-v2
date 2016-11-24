<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/jsp/commons/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>

    <jsp:include page="/WEB-INF/jsp/game/commons/resources.jsp"/>
    <link rel="stylesheet" href="${ctx}/resources/plugins/materialize/css/ghpages-materialize.css">


    <link rel="stylesheet" href="${ctx}/resources/plugins/gooflow/css/public.css"/>
    <link rel="stylesheet" href="${ctx}/resources/plugins/gooflow/css/style.css"/>
    <script src="${ctx}/resources/plugins/gooflow/js/json.js"></script>
    <script src="${ctx}/resources/plugins/gooflow/js/goofunc.js"></script>
    <script src="${ctx}/resources/plugins/gooflow/js/gooflow.js"></script>

    <title>逃离剧本-狂or野-你喜欢哪一个我</title>
    <meta name="description" content="ky's game page">
    <meta name="author" content="kuangye">

</head>
<body>

<header>
    <nav class="top-nav">
        <div class="container">
            <div class="nav-wrapper"><a class="page-title">剧本</a></div>
        </div>
    </nav>

    <jsp:include page="/WEB-INF/jsp/game/admin/leftMenu.jsp"></jsp:include>
    
</header>

<main>
    <div class="container">
    <div class="row">

        <div class="col s12 m12">

            <div class="section scrollspy">

                <div id="gf" ></div>

                <br/>

                <a class="waves-effect waves-light btn" onclick="Export()">导出结果</a>
                <a class="waves-effect waves-light btn" onclick="Import()">导入数据</a>


                <a class="waves-effect waves-light btn" onclick="Submit()">修改</a>

                <br/><br/>
                <textarea id="result" rows="50" cols="100">${drama.data}</textarea>



            </div>

        </div>
    </div>
</div>
</main>   


<jsp:include page="/WEB-INF/jsp/game/admin/footer.jsp"></jsp:include>

<!--  Scripts-->
<!-- 语法高亮 -->
<script src="${ctx}/resources/plugins/prism/prism.js"></script>
<!-- 搜索索引 -->
<script src="${ctx}/resources/plugins/jade/lunr.min.js"></script>
<script src="${ctx}/resources/plugins/jade/search.js"></script>

<script src="${ctx}/resources/js/game/init.js"></script>

<script>
    var property = {
        width: 800,
        height: 600,
        toolBtns: [
            {name:"start",icon :"fa-play"},
            {name:"end",icon :"fa-stop"},
            {name:"task",icon :"fa-film"},
            {name:"node",icon :"fa-list"}
        ],
        haveHead: true,
        headBtns: [
//          {name:"new",icon:"fa-rotate-left",event:"ico_new"},
//          {name:"open",icon:"fa-rotate-left",event:"ico_open"},
//          {name:"save",icon:"fa-rotate-left",event:"ico_save"},
            {name:"undo",icon:"fa-rotate-left",type:"ico_undo"},
            {name:"redo",icon:"fa-rotate-right",type:"ico_redo"},
            {name:"reload",icon:"fa-refresh",type:"ico_reload"}
        ],//如果haveHead=true，则定义HEAD区的按钮
        haveTool: true,
        haveGroup: false,
        useOperStack: true
    };
    var remark = {
        "cursor": "选择指针",
        "direct": "转换连线",
        "start": "开始结点",
        "end": "结束结点",
        "task": "场景结点",
        "node": "选项节点",
        "group": "组织划分框编辑开关",
        "new": "新建",
        "open": "打开"
    };
    var gf;
    $(document).ready(function () {
        gf = $.createGooFlow($("#gf"), property);
        gf.setNodeRemarks(remark);

        Import();
    });
    var out;
    function Export() {
        document.getElementById("result").value = JSON.stringify(gf.exportData());
    }
    function Import() {
        gf.clearData()
        gf.loadData(JSON.parse($("#result").val()));
    }
    function Submit(){
        $.ajax({
            url : $.baseData.basePath+"/game/setting/editDramaDo/${id}",
            type: "POST",
            cache : false,
            data :{data:document.getElementById("result").value = JSON.stringify(gf.exportData())},
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
    }

//    //点击文字编辑
//    $(document).on("click",".GooFlow_item.item_round",function(){
//        console.log(this);
//    })
</script>

</body>
</html>
