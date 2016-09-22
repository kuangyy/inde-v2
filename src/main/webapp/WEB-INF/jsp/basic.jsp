<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/jsp/commons/taglibs.jsp"%>
<!DOCTYPE HTML>
<html lang="en">
<head>

    <jsp:include page="/WEB-INF/jsp/commons/resources.jsp"></jsp:include>

<!-- Generic page styles -->
<link rel="stylesheet" href="${ctx}/resources/component/css/style.css">
<!-- CSS to style the file input field as button and adjust the Bootstrap progress bars -->
<link rel="stylesheet" href="${ctx}/resources/component/css/jquery.fileupload.css">
</head>
<body>

<div class="container">
    <h1>jQuery File Upload Demo</h1>
    <h2 class="lead">Basic version</h2>
  
    <br>
    <blockquote>
        <p>File Upload widget with multiple file selection, drag&amp;drop support and progress bar for jQuery.<br>
        Supports cross-domain, chunked and resumable file uploads.<br>
        Works with any server-side platform (PHP, Python, Ruby on Rails, Java, Node.js, Go etc.) that supports standard HTML form file uploads.</p>
    </blockquote>
    <br>
    <!-- The fileinput-button span is used to style the file input field as button -->
    <span class="btn btn-success fileinput-button">
        <i class="glyphicon glyphicon-plus"></i>
        <span>Select files...</span>
        <!-- The file input field used as target for the file upload widget -->
        <input id="fileupload" type="file" name="files" multiple>
    </span>
    <br>
    <br>
    <!-- The global progress bar -->
    <div id="progress" class="progress">
        <div class="progress-bar progress-bar-success"></div>
    </div>
    <!-- The container for the uploaded files -->
    <div id="files" class="files"></div>
    <br>
    <p id="tt"></p>
    <br>
    <div class="panel panel-default">
        <div class="panel-heading">
            <h3 class="panel-title">Demo Notes</h3>
        </div>
        <div class="panel-body">
            <ul>
                <li>The maximum file size for uploads in this demo is <strong>999 KB</strong> (default file size is unlimited).</li>
                <li>Only image files (<strong>JPG, GIF, PNG</strong>) are allowed in this demo (by default there is no file type restriction).</li>
                <li>Uploaded files will be deleted automatically after <strong>5 minutes or less</strong> (demo files are stored in memory).</li>
                <li>You can <strong>drag &amp; drop</strong> files from your desktop on this webpage (see <a href="https://github.com/blueimp/jQuery-File-Upload/wiki/Browser-support">Browser support</a>).</li>
                <li>Please refer to the <a href="https://github.com/blueimp/jQuery-File-Upload">project website</a> and <a href="https://github.com/blueimp/jQuery-File-Upload/wiki">documentation</a> for more information.</li>
                <li>Built with the <a href="http://getbootstrap.com/">Bootstrap</a> CSS framework and Icons from <a href="http://glyphicons.com/">Glyphicons</a>.</li>
            </ul>
        </div>
    </div>
</div>

<!-- The jQuery UI widget factory, can be omitted if jQuery UI is already included -->
<script src="${ctx}/resources/component/js/vendor/jquery.ui.widget.js"></script>
<!-- The Iframe Transport is required for browsers without support for XHR file uploads -->
<script src="${ctx}/resources/component/js/jquery.iframe-transport.js"></script>
<!-- The basic File Upload plugin -->
<script src="${ctx}/resources/component/js/jquery.fileupload.js"></script>
<!-- Bootstrap JS is not required, but included for the responsive demo navigation -->
<!-- <script src="//netdna.bootstrapcdn.com/bootstrap/3.2.0/js/bootstrap.min.js"></script> -->
<script>
/*jslint unparam: true */
/*global window, $ */
$(function () {
    'use strict';
    // Change this to the location of your server-side upload handler:
    $('#fileupload').fileupload({
        url: "${ctx}/file/fileuploads",//server
        type: "POST",//type default post
        dataType: 'json',
        autoUpload: false,
        singleFileUploads: false,
        progressall: function (e, data) {
            var progress = parseInt(data.loaded / data.total * 100, 10);
            $('#progress .progress-bar').css(
                'width',
                progress + '%'
            );
        },
//        add: function (e, data) {
//            data.context = $('<button/>').text('Upload')
//                    .appendTo(document.body)
//                    .click(function () {
//                        data.context = $('<p/>').text('Uploading...').replaceAll($(this));
//                        data.submit();
//                    });
//            data.submit();
//        },
        done: function (e, data) {
            $.each(data.result.urls, function (index, file) {
                $('<p/>').text(file.name).appendTo('#files');
            });
        },
        fail:  function (e, data) {
            alertify.tips_error("上传失败");
        },

    }).prop('disabled', !$.support.fileInput)
        .parent().addClass($.support.fileInput ? undefined : 'disabled');
});
</script>
</body>
</html>
