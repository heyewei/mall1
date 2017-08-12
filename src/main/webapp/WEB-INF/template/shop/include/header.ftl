<script type="text/javascript">
$().ready(function() {

	var $window = $(window);
	var $headerName = $("#headerName");
	var $headerLogin = $("#headerLogin");
	var $headerRegister = $("#headerRegister");
	var $headerLogout = $("#headerLogout");
	var $productSearchForm = $("#productSearchForm");
	var $keyword = $("#productSearchForm input");
	var $headerCartQuantity = $("#headerCart em");
	var currentMemberUsername = getCookie("currentMemberUsername");
	var defaultKeyword = "${message("shop.header.keyword")}";
	
	if ($.trim(currentMemberUsername) != "") {
		$headerName.text(currentMemberUsername).show();
		$headerLogout.show();
	} else {
		$headerLogin.show();
		$headerRegister.show();
	}
	
	$keyword.focus(function() {
		if ($.trim($keyword.val()) == defaultKeyword) {
			$keyword.val("");
		}
	});
	
	$keyword.blur(function() {
		if ($.trim($keyword.val()) == "") {
			$keyword.val(defaultKeyword);
		}
	});
	
	$productSearchForm.submit(function() {
		if ($.trim($keyword.val()) == "" || $keyword.val() == defaultKeyword) {
			return false;
		}
	});
	
	// 购物车信息
	$window.on("cartInfoLoad", function(event, cartInfo) {
		var productQuantity = cartInfo != null && cartInfo.productQuantity != null ? cartInfo.productQuantity : 0;
		if ($headerCartQuantity.text() != productQuantity && "opacity" in document.documentElement.style) {
			$headerCartQuantity.fadeOut(function() {
				$headerCartQuantity.text(productQuantity).fadeIn();
			});
		} else {
			$headerCartQuantity.text(productQuantity);
		}
	});

});
</script>
<div class="header">
	<div class="top">
		<div class="topNav">
			<ul class="left">
				<li>
					<span>${message("shop.header.welcome", setting.siteName)}</span>
					<span id="headerName" class="headerName">&nbsp;</span>
				</li>
				<li id="headerLogin" class="headerLogin">
					<a href="${base}/member/login">${message("shop.header.login")}</a>|
				</li>
				<li id="headerRegister" class="headerRegister">
					<a href="${base}/member/register">${message("shop.header.register")}</a>
				</li>
				<li id="headerLogout" class="headerLogout">
					<a href="${base}/member/logout">[${message("shop.header.logout")}]</a>
				</li>
			</ul>
			<ul class="right">
				[@navigation_list position = "top"]
					[#list navigations as navigation]
						<li>
							<a href="${navigation.url}"[#if navigation.isBlankTarget] target="_blank"[/#if]>${navigation.name}</a>|
						</li>
					[/#list]
				[/@navigation_list]
				<li id="headerCart" class="headerCart">
					<a href="${base}/cart/list">${message("shop.header.cart")}</a>
					(<em></em>)
				</li>
			</ul>
		</div>
	</div>
	<div class="container">
		<div class="row">
			<div class="span3">
				<a href="${base}/">
					<img src="${setting.logo}" alt="${setting.siteName}" />
				</a>
			</div>
			<div class="span6">
				<div class="search">
					<form id="productSearchForm" action="${base}/product/search" method="get">
						<input name="keyword" class="keyword" value="${productKeyword!message("shop.header.keyword")}" autocomplete="off" x-webkit-speech="x-webkit-speech" x-webkit-grammar="builtin:search" maxlength="30" />
						<button type="submit">&nbsp;</button>
					</form>
				</div>
				<div class="hotSearch">
					[#if setting.hotSearches?has_content]
						${message("shop.header.hotSearch")}:
						[#list setting.hotSearches as hotSearch]
							<a href="${base}/product/search?keyword=${hotSearch?url}">${hotSearch}</a>
						[/#list]
					[/#if]
				</div>
			</div>
			<div class="span3">
				<div class="phone">
					<em>${message("shop.header.phone")}</em>
					${setting.phone}
				</div>
			</div>
		</div>
		<div class="row">
			<div class="span12">
				<dl class="mainNav">
					<dt>
						<a href="${base}/product_category">${message("shop.header.allProductCategory")}</a>
					</dt>
					[@navigation_list position = "middle"]
						[#list navigations as navigation]
							<dd[#if navigation.url = url] class="current"[/#if]>
								<a href="${navigation.url}"[#if navigation.isBlankTarget] target="_blank"[/#if]>${navigation.name}</a>
							</dd>
						[/#list]
					[/@navigation_list]
				</dl>
			</div>
		</div>
	</div>
</div>