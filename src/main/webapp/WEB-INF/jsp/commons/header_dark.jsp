<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/commons/taglibs.jsp"%>

<div class="bg"></div>

<nav class="navbar navbar-dark bg-inverse">
	<!-- <div class="clearfix"> -->
	<button class="navbar-toggler pull-xs-right hidden-sm-up" type="button" data-toggle="collapse" data-target="#bd-main-nav" aria-controls="bd-main-nav" aria-expanded="false" aria-label="Toggle navigation">
		&#9776;
	</button>
	<a class="navbar-brand" href="/"> KY </a>
	<!-- </div> -->
	<div class="navbar-toggleable-xs collapse" id="bd-main-nav" aria-expanded="true">
		<ul class="nav navbar-nav">
			<li class="nav-item active">
				<a class="nav-link" href="/">Home <span class="sr-only">(current)</span></a>
			</li>

			<li class="nav-item">
				<a class="nav-link" href="${ctx}/photos">Photos</a>
			</li>
			<li class="nav-item">
				<a class="nav-link" href="${ctx}/foods">Foods</a>
			</li>
			<li class="nav-item">
				<a class="nav-link" href="${ctx}/movies">Movies</a>
			</li>
			<li class="nav-item">
				<a class="nav-link" href="${ctx}/books">Books</a>
			</li>
			<li class="nav-item">
				<a class="nav-link" href="${ctx}/tags">Others</a>
			</li>
		</ul>
	</div>
	</div>
</nav>

<script type="text/javascript">

	$(function(){

		generateBG();

		$(window).resize(function(){
			generateBG();
		});

		// $(".jumbotron .row .div").animate({

		// },"normal");
	})

	function generateBG(){
		var colorFunc = function(x, y) {
			return 'hsl('+Math.floor(Math.abs(x*y)*360)+',80%,60%)';
		};
		//rainbow
		//var pattern = Trianglify({color_function: colorFunc})
		var pattern = Trianglify({
			width: window.innerWidth,
			height: window.innerHeight,
			cell_size: 40
		});
		$(".bg").html(pattern.canvas());

	}


	$('.carousel').carousel({
		interval: 2000
	})
</script>