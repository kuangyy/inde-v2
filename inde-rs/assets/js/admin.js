(function () {
    "use strict";

    var meta = document.querySelector('meta[name="csrf"]');
    var csrf = meta ? meta.getAttribute("content") : "";

    function notify(message, isError) {
        var box = document.createElement("p");
        box.className = "alert " + (isError ? "error" : "notice");
        box.textContent = message;
        var head = document.querySelector(".admin-head") || document.querySelector(".admin-main");
        if (head) {
            head.parentNode.insertBefore(box, head.nextSibling);
        } else {
            document.body.insertBefore(box, document.body.firstChild);
        }
        window.setTimeout(function () {
            box.parentNode && box.parentNode.removeChild(box);
        }, 8000);
    }

    function upload(file) {
        var data = new FormData();
        data.append("csrf", csrf);
        data.append("file", file);
        return fetch("/manage/upload", { method: "POST", body: data, credentials: "same-origin" })
            .then(function (response) {
                return response.json().catch(function () { return {}; }).then(function (body) {
                    if (!response.ok || !body.url) {
                        throw new Error(body.error || "上传失败");
                    }
                    return body.url;
                });
            });
    }

    function insertAtCursor(textarea, text) {
        var start = textarea.selectionStart || 0;
        var end = textarea.selectionEnd || 0;
        var value = textarea.value;
        textarea.value = value.slice(0, start) + text + value.slice(end);
        var pos = start + text.length;
        textarea.selectionStart = pos;
        textarea.selectionEnd = pos;
        textarea.focus();
    }

    document.addEventListener("click", function (event) {
        var target = event.target;

        var copyBtn = target.closest("[data-copy]");
        if (copyBtn) {
            var value = copyBtn.getAttribute("data-copy");
            if (navigator.clipboard) {
                navigator.clipboard.writeText(value).then(function () {
                    notify("已复制：" + value, false);
                });
            } else {
                window.prompt("复制地址", value);
            }
            return;
        }

        var uploadBtn = target.closest("[data-upload-target]");
        if (uploadBtn) {
            var input = document.createElement("input");
            input.type = "file";
            input.accept = "image/png,image/jpeg,image/webp,image/gif";
            input.onchange = function () {
                if (!input.files || !input.files[0]) { return; }
                notify("正在上传…", false);
                upload(input.files[0]).then(function (url) {
                    var field = document.querySelector(uploadBtn.getAttribute("data-upload-target"));
                    if (field) { field.value = url; }
                    notify("上传成功：" + url, false);
                }).catch(function (error) {
                    notify(error.message, true);
                });
            };
            input.click();
            return;
        }
    });

    var editorUpload = document.getElementById("editor-upload-btn");
    var editorFile = document.getElementById("editor-file");
    if (editorUpload && editorFile) {
        editorUpload.addEventListener("click", function () {
            editorFile.click();
        });
        editorFile.addEventListener("change", function () {
            if (!editorFile.files || !editorFile.files[0]) { return; }
            var file = editorFile.files[0];
            var status = document.getElementById("editor-status");
            if (status) { status.textContent = "正在上传…"; }
            upload(file).then(function (url) {
                var textarea = document.getElementById("body_md");
                var name = file.name.replace(/\.[^.]+$/, "");
                insertAtCursor(textarea, "\n![" + name + "](" + url + ")\n");
                if (status) { status.textContent = "已插入图片"; }
            }).catch(function (error) {
                if (status) { status.textContent = ""; }
                notify(error.message, true);
            });
            editorFile.value = "";
        });
    }

    var previewBtn = document.getElementById("preview-btn");
    if (previewBtn) {
        previewBtn.addEventListener("click", function () {
            var textarea = document.getElementById("body_md");
            var preview = document.getElementById("preview");
            var data = new FormData();
            data.append("csrf", csrf);
            data.append("body_md", textarea ? textarea.value : "");
            fetch("/manage/preview", { method: "POST", body: data, credentials: "same-origin" })
                .then(function (response) {
                    return response.json().catch(function () { return {}; }).then(function (body) {
                        if (!response.ok) { throw new Error(body.error || "预览失败"); }
                        return body.html;
                    });
                })
                .then(function (html) {
                    if (preview) {
                        preview.hidden = false;
                        preview.innerHTML = html;
                        preview.scrollIntoView({ behavior: "smooth", block: "start" });
                    }
                })
                .catch(function (error) {
                    notify(error.message, true);
                });
        });
    }
})();
