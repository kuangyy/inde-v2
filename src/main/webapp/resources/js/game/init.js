(function($){
    $(function(){


        // Plugin initialization
        $('.carousel.carousel-slider').carousel({full_width: true});
        $('.carousel').carousel();
        $('.slider').slider({full_width: true});
        $('.parallax').parallax();
        $('.modal').modal();
        $('.scrollspy').scrollSpy();
        $('.button-collapse').sideNav({'edge': 'left'});
        $('.datepicker').pickadate({selectYears: 20});
        $('select').not('.disabled').material_select();
        // $('input.autocomplete').autocomplete({
        //     data: {"Apple": null, "Microsoft": null, "Google": 'http://placehold.it/250x250'}
        // });


    }); // end of document ready
})(jQuery); // end of jQuery name space