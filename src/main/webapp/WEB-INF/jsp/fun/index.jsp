<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/jsp/commons/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>

    <jsp:include page="/WEB-INF/jsp/commons/resources.jsp"/>

    <title>picture wall-狂or野-你喜欢哪一个我</title>
    <meta name="description" content="ky's fun page">
    <meta name="author" content="kuangye">
</head>
<body>


<canvas id="canvas" style="border:0px solid #f00" width="1920" height="666"></canvas>

<div id="crt" style="width:100%; height:150px; border:0px solid #fff; background-color:#099; text-align:center;">
    <table cellspacing="0" style="width:100%;  height:100%; border:1px solid #fff;">
        <tbody>
        <tr>
            <td style="width:25%;">
                <table id="msclist" class="msclist" style="width:100%; height:100%; background-color:#000;">
                    <tbody>
                    <tr>
                        <td id="0" onclick="playa(this.id)" style="cursor: pointer; background-color: rgb(255, 0, 0);">
                            a
                        </td>
                    </tr>
                    </tbody>
                    <tbody>
                    <tr>
                        <td id="1" onclick="playa(this.id)" style="cursor: pointer; background-color: rgb(0, 0, 0);">
                            b
                        </td>
                    </tr>
                    </tbody>
                </table>
            </td>
            <td style="width:50%;">
                <div onclick="last()"
                     style="width:30%; cursor:pointer; line-height:130px; background-color:#63F; margin-left:3%; height:100%; float:left;">
                    上一曲

                </div>
                <div onclick="run()"
                     style="width:30%; cursor:pointer; background-color:#63F; margin-left:2%; height:100%; float:left;">
                    <img id="img" src="${ctx}/resources/img/HatsuhinodeOarai_ZH-CN9858647947_1920x1080.jpg"
                         style=" margin-top:10px; height:120px; width:auto; border-radius:100%;" class="img">

                </div>
                <div onclick="next()"
                     style="width:30%; cursor:pointer; line-height:130px; background-color:#63F; margin-left:2%; height:100%; float:left;">
                    下一曲
                </div>
            </td>
            <td style="width:25%;">
                <form oninput="cgcolor()">
                    <table style="width:100%; height:100%; ">
                        <tbody>
                        <tr>
                            <td style="cursor:pointer;  background-color:#09C;" onclick="canvascg(1)">球状</td>
                            <td style="cursor:pointer; background-color:#09C;" onclick="canvascg(2)">柱形</td>
                            <td style="cursor:pointer; background-color:#09C;" onclick="canvascg(3)">线条</td>
                        </tr>
                        <tr>
                            <td id="td1" style="cursor:pointer; font-size:18px; background-color:#09C;">开始<br/><input
                                    type="color" id='startc'/></td>
                            <td style="cursor:pointer; font-size:18px; background-color:#09C;">中点<br/><input
                                    type="color" id='contentc'/></td>
                            <td style="cursor:pointer; font-size:18px; background-color:#09C;">末端<br/><input
                                    type="color" id='endc'/></td>
                        </tr>
                        </tbody>
                    </table>
                </form>
            </td>
        </tr>
        </tbody>
    </table>
</div>
<audio id="audio" style=" " src="http://oooc.lol/mp3/a.mp3" crossorigin="anonymous" autobuffered >
    <source src="http://oooc.lol/mp3/a.mp3" type="audio/mpeg">
    Your browser does not support the audio tag.
</audio>
<script>

    window.AudioContext = window.AudioContext || window.webkitAudioContext || window.mozAudioContext;

    var msc = ['a', 'b'];
    var mscsrc = ['http://oooc.lol/mp3/a.mp3', 'http://oooc.lol/mp3/a.mp3'];
    var mscid = 0;
    function playa(id) {
        mscid = id;
        document.getElementById('audio').src = mscsrc[mscid];
        run();
        cgcss();
    }
    function next() {
        if (mscid > (msc.length - 2))
            mscid = 0;
        else
            mscid++;
        document.getElementById('audio').src = mscsrc[mscid];
        run();
        cgcss();
    }
    function last() {
        if (mscid < 1)
            mscid = msc.length - 1;
        else
            mscid--;
        document.getElementById('audio').src = mscsrc[mscid];
        run();
        cgcss();
    }
    function cgcss() {
        document.getElementById("1").style.backgroundColor = "#000";
        document.getElementById("0").style.backgroundColor = "#000";
        document.getElementById(mscid).style.backgroundColor = "#f00";
    }
    var cssn = 2;
    var voicec1 = ["#0f0", "#f00", "#f0f"];
    function cgcolor() {
        voicec1[0] = document.getElementById('startc').value;
        voicec1[1] = document.getElementById('startc').value;
        voicec1[2] = document.getElementById('startc').value;
    }
    window.onload = function play() {

        var $ = function (id) {
            return document.getElementById(id);
        }
        var canvas = $('canvas');
        var audio = $('audio');
        for (var a = 0; a < msc.length; a++) {
            var msclist = $('msclist').innerHTML;
            $('msclist').innerHTML = msclist + "<tr><td id='" + a + "' onclick='playa(this.id)' style='cursor:pointer;'>" + msc[a] + "</td></tr>";
        }
        cgcss();

        audio.src = mscsrc[mscid];
        var ctr = $('ctr');
        var ctx = canvas.getContext('2d');
        var actx = new AudioContext();

        color = ctx.createLinearGradient(canvas.width * .5, 0, canvas.width * .5, 300);
        color.addColorStop(0, voicec1[0]);
        color.addColorStop(.5, voicec1[1]);
        color.addColorStop(1, voicec1[2]);
        colort = ctx.createLinearGradient(canvas.width * .5, 300, canvas.width * .5, 600);
        colort.addColorStop(0, "rgba(125,225,133,0.7)");
        colort.addColorStop(.5, "rgba(225,225,0,0.1)");
        colort.addColorStop(1, "rgba(125,0,133,0)");
        canvas.width = window.innerWidth;
        canvas.height = window.innerHeight * .7;

        var analyser = actx.createAnalyser();
        var audioSrc = actx.createMediaElementSource(audio);
        audioSrc.connect(analyser);
        analyser.connect(actx.destination);
        var num = 80;

        function draw() {
            var voicehigh = new Uint8Array(analyser.frequencyBinCount);
            analyser.getByteFrequencyData(voicehigh);
            var step = Math.round(voicehigh.length / num);
            ctx.clearRect(0, 0, canvas.width, canvas.height);

            ctx.beginPath();

            for (var i = 1; i < num; i++) {
                var value = voicehigh[step * i];


                switch (cssn) {
                    case 1:
                        ctx.fillStyle = color;
                        ctx.beginPath();
                        ctx.arc(i * 50, canvas.height * .5, value * .3, 0, Math.PI * 2, true);
                        ctx.fill();
                        break;
                    case 2:
                        ctx.fillStyle = color;
                        ctx.fillRect(i * 10 + canvas.width * .5, 250, 7, -value + 1);
                        ctx.fillRect(canvas.width * .5 - (i - 1) * 10, 250, 7, -value + 1);
                        ctx.fill();
                        ctx.fillStyle = colort;
                        ctx.fillRect(i * 10 + canvas.width * .5, 250, 7, value + 1);
                        ctx.fillRect(canvas.width * .5 - (i - 1) * 10, 250, 7, value + 1);
                        break;
                    case 3:
                        moveTo(0, canvas.height * .5);
                        ctx.lineTo(i * 7 + canvas.width * .5, -value + canvas.height * .5);
                        ctx.strokeStyle = "#f00";
                        ctx.stroke();
                        break;

                }

                //ctx.stroke();
            }
            requestAnimationFrame(draw);

        }

        draw();
    }
    function canvascg(n) {
        cssn = n;
    }
    function run() {
        var audio = document.getElementById('audio');
        var imgcss = document.getElementById('img');
        if (audio.paused){
            audio.play();
            imgcss.className = 'img';
        } else {
            audio.pause();
            imgcss.className = '';
        }
    }
</script>
</body>
</html>
