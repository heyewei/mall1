<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
	<meta name="author" content="SHOP++ Team">
	<meta name="copyright" content="SHOP++">
	[@seo type = "productSearch"]
		<title>${seo.resolveTitle()}[#if showPowered] - Powered By SHOP++[/#if]</title>
		[#if seo.resolveKeywords()?has_content]
			<meta name="keywords" content="${seo.resolveKeywords()}">
		[/#if]
		[#if seo.resolveDescription()?has_content]
			<meta name="description" content="${seo.resolveDescription()}">
		[/#if]
		
	[/@seo]
	<link href="${base}/favicon.ico" rel="icon">
	<link href="${base}/resources/mobile/shop/css/bootstrap.css" rel="stylesheet">
	<link href="${base}/resources/mobile/shop/css/font-awesome.css" rel="stylesheet">
	<link href="${base}/resources/mobile/shop/css/animate.css" rel="stylesheet">
	<link href="${base}/resources/mobile/shop/css/common.css" rel="stylesheet">
	<link href="${base}/resources/mobile/shop/css/product.css" rel="stylesheet">
	<!--[if lt IE 9]>
		<script src="${base}/resources/mobile/shop/js/html5shiv.js"></script>
		<script src="${base}/resources/mobile/shop/js/respond.js"></script>
	<![endif]-->
	<script src="${base}/resources/mobile/shop/js/jquery.js"></script>
	<script src="${base}/resources/mobile/shop/js/bootstrap.js"></script>
	<script src="${base}/resources/mobile/shop/js/underscore.js"></script>
	<script src="${base}/resources/mobile/shop/js/common.js"></script>
	<script id="productTemplate" type="text/template">
		<%_.each(products, function(product, i) {%>
			<div class="col-xs-6">
				<div class="thumbnail thumbnail-flat thumbnail-condensed">
					<a href="${base}<%-product.path%>">
						<img class="img-responsive center-block" src="<%-product.thumbnail != null ? product.thumbnail : "${setting.defaultThumbnailProductImage}"%>" alt="<%-product.name%>">
						<h4 class="text-overflow"><%-product.name%></h4>
						<%if (product.type == "general") {%>
							<strong class="text-overflow red"><%-currency(product.price, true)%></strong>
						<%} else if (product.type == "exchange") {%>
							<span class="small">${message("Sku.exchangePoint")}:</span>
							<strong class="text-overflow red"><%-product.exchangePoint%></strong>
						<%}%>
					</a>
				</div>
			</div>
		<%})%>
	</script>
	<script type="text/javascript">
	$().ready(function() {
		
		var $searchForm = $("#searchForm");
		var $keyword = $("#keyword");
		var $order = $("#order");
		var $orderType = $("#order a[data-order-type]");
		var $productItems = $("#list div.row");
		var productTemplate = _.template($("#productTemplate").html());
		var data = {
			keyword: "${productKeyword?js_string}",
			orderType: null
		}
		
		// 搜索
		$searchForm.submit(function() {
			if ($.trim($keyword.val()) == "") {
				return false;
			}
			
			data.keyword = $keyword.val();
			$productItems.infiniteScroll("refresh");
			return false;
		});
		
		// 排序
		$orderType.click(function() {
			var $element = $(this);
			if ($element.hasClass("active")) {
				return;
			}
			
			$order.find(".active").removeClass("active");
			$element.addClass("active");
			var $dropdown = $element.closest(".dropdown");
			if ($dropdown.size() > 0) {
				$dropdown.find("a[data-toggle='dropdown'] strong").text($element.data("title")).addClass("active");
			}
			
			data.orderType = $element.data("order-type");
			$productItems.infiniteScroll("refresh");
		});
		
		// 无限滚动加载
		$productItems.infiniteScroll({
			url: function(pageNumber) {
				return "${base}/product/search?pageNumber=" + pageNumber;
			},
			data: data,
			pageSize: 20,
			template: function(pageNumber, data) {
				return productTemplate({
					products: data
				});
			}
		});
	
	});
	</script>
</head>
<body class="product-list">
	<header class="header-fixed">
		<div class="container-fluid">
			<div class="row">
				<div class="col-xs-2 text-center">
					<a href="javascript: history.back();">
						<span class="glyphicon glyphicon-menu-left"></span>
					</a>
				</div>
				<div class="col-xs-8">
					<form id="searchForm" action="${base}/product/search" method="get">
						<div class="input-group">
							<input id="keyword" name="keyword" class="form-control" type="text" value="${productKeyword}" placeholder="${message("shop.product.search")}">
							<span class="input-group-btn">
								<button class="btn btn-default" type="submit">
									<span class="glyphicon glyphicon-search"></span>
								</button>
							</span>
						</div>
					</form>
				</div>
				<div class="col-xs-2 text-center">
					<div class="menu dropdown">
						<a href="javascript:;" data-toggle="dropdown">
							<span class="glyphicon glyphicon-th-list"></span>
							<span class="caret"></span>
						</a>
						<ul class="dropdown-menu">
							<li>
								<a href="${base}/">
									<span class="glyphicon glyphicon-home"></span>
									${message("shop.product.home")}
								</a>
							</li>
							<li>
								<a href="${base}/cart/list">
									<span class="glyphicon glyphicon-shopping-cart"></span>
									${message("shop.product.cart")}
								</a>
							</li>
							<li>
								<a href="${base}/member/index">
									<span class="glyphicon glyphicon-user"></span>
									${message("shop.product.member")}
								</a>
							</li>
						</ul>
					</div>
				</div>
			</div>
			<div id="order" class="order">
				<div class="row">
					<div class="col-xs-3 text-center">
						<div class="dropdown">
							<a href="javascript:;" data-toggle="dropdown">
								<strong>${message("shop.product.defaultTitle")}</strong>
								<span class="caret"></span>
							</a>
							<ul class="dropdown-menu">
								<li>
									<a href="javascript:;" data-order-type="topDesc" data-title="${message("shop.product.defaultTitle")}">${message("shop.product.default")}</a>
								</li>
								<li>
									<a href="javascript:;" data-order-type="priceAsc" data-title="${message("shop.product.priceAscTitle")}">${message("shop.product.priceAsc")}</a>
								</li>
								<li>
									<a href="javascript:;" data-order-type="priceDesc" data-title="${message("shop.product.priceDescTitle")}">${message("shop.product.priceDesc")}</a>
								</li>
							</ul>
						</div>
					</div>
					<div class="col-xs-3 text-center">
						<a href="javascript:;" data-order-type="salesDesc">
							${message("shop.product.salesDescTitle")}
							<span class="fa fa-long-arrow-down"></span>
						</a>
					</div>
					<div class="col-xs-3 text-center">
						<a href="javascript:;" data-order-type="scoreDesc">
							${message("shop.product.scoreDescTitle")}
							<span class="fa fa-long-arrow-down"></span>
						</a>
					</div>
					<div class="col-xs-3 text-center">
						<a class="disabled" href="javascript:;">
							${message("shop.product.filterTitle")}
							<span class="glyphicon glyphicon-filter"></span>
						</a>
					</div>
				</div>
			</div>
		</div>
	</header>
	<main>
		<div class="container-fluid">
			<div id="list" class="list">
				<div class="row"></div>
			</div>
		</div>
	</main>
</body>
</html>