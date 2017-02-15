var intimeType = function(style,styleBlock,textBlock){
    var openComment = false;

    var writeStyleChar = function (which) {
        //	        # begin wrapping open comments
        if (which == '/' && openComment == false) {
            openComment = true;
            styles = $('#style-text').html() + which;
        } else if (which == '/' && openComment == true) {
            openComment = false;
            styles = $('#style-text').html().replace(/(\/[^\/]*\*)$/, '<em class="comment">$1/</em>');
        }
        //            # wrap style declaration
        else if (which == ':') {
            styles = $('#style-text').html().replace(/([a-zA-Z- ^\n]*)$/, '<em class="key">$1</em>:');
        }
        //            # wrap style value
        else if (which == ';') {
            styles = $('#style-text').html().replace(/([^:]*)$/, '<em class="value">$1</em>;');
        }
        //            # wrap selector
        else if (which == '{') {
            styles = $('#style-text').html().replace(/(.*)$/, '<em class="selector">$1</em>{');
        } else {
            styles = $('#style-text').html() + which;
        }
        $('#style-text').html(styles);
        $('#style-tag').append(which);
    }

    var writeStyles = function (message, index, interval) {
        if (index < message.length) {
            pre = document.getElementById('style-text');
            pre.scrollTop = pre.scrollHeight;
            writeStyleChar(message[index++]);
            setTimeout("writeStyles(`" + message + "`," + index + "," + interval + ")", interval);
        }
    }


    $('body').append(`
            <style id="style-tag"></style>
                <span id="echo"></span>
                <span id="heart"><i></i></span>
                <pre id="style-text"></pre>
               `);

    //             faster typing in small iframe on codepen homepage
    var time = window.innerWidth <= 578 ? 1 : 8;

    //             starting it off
    writeStyles(styles, 0, time);
}

