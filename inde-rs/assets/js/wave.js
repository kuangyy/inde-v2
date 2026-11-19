var kyWave = function (id, width, height, size) {
       
    var RES_X = width||20;
    var RES_Y = height||20;
    var SIZE = size||22;

// super laggy on mobile devices so ease up the res
    if( /iphone|ipad|android/ig.test( navigator.userAgent ) ) {
        RES_X = RES_X / 2;
        RES_Y = RES_Y / 2;
        SIZE = SIZE + 3;
    }

    var entities = [];

    var wrapper = document.createElement( 'div' );
    wrapper.className = 'wrapper';
    wrapper.style.width = ( RES_X * SIZE ) + 'px';
    wrapper.style.height = ( RES_Y * SIZE ) + 'px';

    if(id){
        document.getElementById(id).appendChild( wrapper );
    }else{
        document.body.appendChild( wrapper );
    }

    for( var x = 0; x < RES_X; x++ ) {
        for( var y = 0; y < RES_Y; y++ ) {
            var el = document.createElement( 'div' );
            el.setAttribute( 'class', 'dot' );
            wrapper.appendChild( el );

            var entity = {
                element: el,
                x: x * SIZE,
                y: y * SIZE
            }

            el.style.left = entity.x + 'px';
            el.style.top = entity.y + 'px';
            el.addEventListener( 'click', toggle.bind( this, entity ) );

            entities.push( entity );
        }
    }

    function toggle( targetEntity ) {

        var checked = targetEntity.element.checked;

        entities.forEach( function( entity ) {

            var dx = targetEntity.x - entity.x;
            var dy = targetEntity.y - entity.y;
            var distance = Math.sqrt( dx * dx + dy * dy );

            setTimeout( function() {
                entity.element.checked = checked;

                // re-run the animation, reading offsetWidth forces reflow
                entity.element.className = '';
                entity.element.offsetWidth;

                var index = $(entity.element).index();
                var $entity = $(".wrapper").find(".dot").eq(index);
                entity.element.className = $entity.attr("class") + " grow";
            }, Math.round( distance * 1.8 ) );

        } );

    }

    setTimeout( function() {
        entities[ 0 ].element.checked = true;
        toggle( entities[ 0 ] );
    }, 500 );
}
function kyWaveText(id,color){

    function arrayToIndex(x,y,width,height){
        width=width||50;
        height=height||20;
        if(x<width&&y<height){
            return x*height+y+1;
        }
    }
    function ss(x,y){
        $("#"+id).find("div").eq(arrayToIndex(x,y)).addClass("text-color");
    }

    //H
    ss(5,4),ss(5,5),ss(5,6),ss(5,7),ss(5,8),ss(5,9),ss(5,10),ss(5,11),ss(5,12),ss(5,13);
    ss(6,8),ss(7,8),ss(8,8),ss(9,8);
    ss(6,9),ss(7,9),ss(8,9),ss(9,9);
    ss(10,4),ss(10,5),ss(10,6),ss(10,7),ss(10,8),ss(10,9),ss(10,10),ss(10,11),ss(10,12),ss(10,13);
    //O
    ss(14,4),ss(13,5),ss(12,6),ss(12,7),ss(12,8),ss(12,9),ss(12,10),ss(12,11),ss(13,12),ss(14,13);
    ss(14,5),ss(14,12);
    ss(15,4),ss(15,13),ss(15,5),ss(15,12);
    ss(16,5),ss(16,12);
    ss(16,4),ss(17,5),ss(18,6),ss(18,7),ss(18,8),ss(18,9),ss(18,10),ss(18,11),ss(17,12),ss(16,13);
    //L
    ss(20,4),ss(20,5),ss(20,6),ss(20,7),ss(20,8),ss(20,9),ss(20,10),ss(20,11),ss(20,12),ss(20,13);
    ss(21,12),ss(21,13);
    ss(22,12),ss(22,13);
    ss(23,12),ss(23,13);
    ss(24,12),ss(24,13);
    ss(25,12),ss(25,13);
    ss(26,12),ss(26,13);
    //A
    ss(30,13),ss(31,13),ss(32,12),ss(33,12),ss(34,11),ss(35,10);
    ss(32,10),ss(32,11);
    ss(33,10),ss(33,11);
    ss(34,10),ss(34,11);
    ss(36,13),ss(35,13),ss(34,12),ss(33,12),ss(34,11),ss(35,10);
}