<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html;charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("shop.cart.title")}[#if showPowered] - Powered By SHOP++[/#if]</title>
<meta name="author" content="SHOP++ Team" />
<meta name="copyright" content="SHOP++" />
<link href="${base}/resources/shop/css/animate.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/shop/css/common.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/shop/css/cart.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/shop/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/shop/js/common.js"></script>
<script type="text/javascript">
$().ready(function() {

	var $quantity = $("#cartTable input[name='quantity']");
	var $increase = $("#cartTable span.increase");
	var $decrease = $("#cartTable span.decrease");
	var $remove = $("#cartTable a.remove");
	var $gift = $("#gift");
	var $promotion = $("#promotion");
	var $effectiveRewardPoint = $("#effectiveRewardPoint");
	var $effectivePrice = $("#effectivePrice");
	var $clear = $("#clear");
	var $checkout = $("#checkout");
	var timeouts = {};
	
	// 初始数量
	$quantity.each(function() {
		var $this = $(this);
		$this.data("value", $this.val());
	});
	
	// 数量
	$quantity.keypress(function(event) {
		return (event.which >= 48 && event.which <= 57) || event.which == 8;
	});
	
	// 增加数量
	$increase.click(function() {
		var $quantity = $(this).parent().siblings("input");
		var quantity = $quantity.val();
		if (/^\d*[1-9]\d*$/.test(quantity)) {
			$quantity.val(parseInt(quantity) + 1);
		} else {
			$quantity.val(1);
		}
		modify($quantity);
	});
	
	// 减少数量
	$decrease.click(function() {
		var $quantity = $(this).parent().siblings("input");
		var quantity = $quantity.val();
		if (/^\d*[1-9]\d*$/.test(quantity) && parseInt(quantity) > 1) {
			$quantity.val(parseInt(quantity) - 1);
		} else {
			$quantity.val(1);
		}
		modify($quantity);
	});
	
	// 修改
	$quantity.on("input propertychange change", function(event) {
		if (event.type != "propertychange" || event.originalEvent.propertyName == "value") {
			modify($(this));
		}
	});
	
	// 修改
	function modify($quantity) {
		var quantity = $quantity.val();
		if (/^\d*[1-9]\d*$/.test(quantity)) {
			var $tr = $quantity.closest("tr");
			var skuId = $tr.find("input[name='skuId']").val();
			clearTimeout(timeouts[skuId]);
			timeouts[skuId] = setTimeout(function() {
				$.ajax({
					url: "modify",
					type: "POST",
					data: {skuId: skuId, quantity: quantity},
					dataType: "json",
					beforeSend: function() {
						$checkout.prop("disabled", true);
					},
					success: function(data) {
						$quantity.data("value", quantity);
						$tr.find("span.subtotal").text(currency(data.subtotal, true));
						if (data.giftNames != null && data.giftNames.length > 0) {
							$gift.html('<dt>${message("Cart.gifts")}:<\/dt>');
							$.each(data.giftNames, function(i, giftName) {
								$gift.append('<dd title="' + escapeHtml(giftName) + '">' + escapeHtml(abbreviate(giftName, 50)) + ' &times; 1<\/dd>');
							});
							"opacity" in document.documentElement.style ? $gift.fadeIn() : $gift.show();
						} else {
							"opacity" in document.documentElement.style ? $gift.fadeOut() : $gift.hide();
						}
						$promotion.text(data.promotionNames.join(", "));
						if (!data.isLowStock) {
							$tr.find("span.lowStock").remove();
						}
						$effectiveRewardPoint.text(data.effectiveRewardPoint);
						$effectivePrice.text(currency(data.effectivePrice, true, true));
					},
					error: function() {
						$quantity.val($quantity.data("value"));
					},
					complete: function() {
						$checkout.prop("disabled", false);
					}
				});
			}, 500);
		} else {
			$quantity.val($quantity.data("value"));
		}
	}
	
	// 移除
	$remove.click(function() {
		if (confirm("${message("shop.dialog.deleteConfirm")}")) {
			var $this = $(this);
			var $tr = $this.closest("tr");
			var skuId = $tr.find("input[name='skuId']").val();
			clearTimeout(timeouts[skuId]);
			$.ajax({
				url: "remove",
				type: "POST",
				data: {skuId: skuId},
				dataType: "json",
				beforeSend: function() {
					$checkout.prop("disabled", true);
				},
				success: function(data) {
					if (data.quantity > 0) {
						$tr.remove();
						if (data.giftNames != null && data.giftNames.length > 0) {
							$gift.html('<dt>${message("Cart.gifts")}:<\/dt>');
							$.each(data.giftNames, function(i, giftName) {
								$gift.append('<dd title="' + escapeHtml(giftName) + '">' + escapeHtml(abbreviate(giftName, 50)) + ' &times; 1<\/dd>');
							});
							"opacity" in document.documentElement.style ? $gift.fadeIn() : $gift.show();
						} else {
							"opacity" in document.documentElement.style ? $gift.fadeOut() : $gift.hide();
						}
						$promotion.text(data.promotionNames.join(", "));
						$effectiveRewardPoint.text(data.effectiveRewardPoint);
						$effectivePrice.text(currency(data.effectivePrice, true, true));
					} else {
						location.reload(true);
					}
				},
				complete: function() {
					$checkout.prop("disabled", false);
				}
			});
		}
		return false;
	});
	
	// 清空
	$clear.click(function() {
		if (confirm("${message("shop.dialog.clearConfirm")}")) {
			$.each(timeouts, function(i, timeout) {
				clearTimeout(timeout);
			});
			$.ajax({
				url: "clear",
				type: "POST",
				dataType: "json",
				success: function() {
					location.reload(true);
				}
			});
		}
		return false;
	});

});
</script>
</head>
<body>
	[#include "/shop/include/header.ftl" /]
	<div class="container cart">
		<div class="row">
			<div class="span12">
				<div class="step">
					<ul>
						<li class="current">${message("shop.cart.step1")}</li>
						<li>${message("shop.cart.step2")}</li>
						<li>${message("shop.cart.step3")}</li>
					</ul>
				</div>
				[#if currentCart?? && currentCart.cartItems?has_content]
					<table id="cartTable" class="cartTable">
						<tr>
							<th>${message("shop.cart.image")}</th>
							<th>${message("shop.cart.sku")}</th>
							<th>${message("shop.cart.price")}</th>
							<th>${message("shop.cart.quantity")}</th>
							<th>${message("shop.cart.subtotal")}</th>
							<th>${message("shop.cart.action")}</th>
						</tr>
						[#list currentCart.cartItems as cartItem]
							<tr>
								<td width="60">
									<input type="hidden" name="skuId" value="${cartItem.sku.id}" />
									<img src="${cartItem.sku.thumbnail!setting.defaultThumbnailProductImage}" alt="${cartItem.sku.name}" />
								</td>
								<td>
									<a href="${base}${cartItem.sku.path}" title="${cartItem.sku.name}" target="_blank">${abbreviate(cartItem.sku.name, 50, "...")}</a>
									[#if cartItem.sku.specifications?has_content]
										<span class="silver">[${cartItem.sku.specifications?join(", ")}]</span>
									[/#if]
									[#if !cartItem.isMarketable]
										<span class="red">[${message("shop.cart.notMarketable")}]</span>
									[/#if]
									[#if cartItem.isLowStock]
										<span class="red lowStock">[${message("shop.cart.lowStock")}]</span>
									[/#if]
								</td>
								<td>
									${currency(cartItem.price, true)}
								</td>
								<td class="quantity" width="60">
									<input type="text" name="quantity" value="${cartItem.quantity}" maxlength="4" onpaste="return false;" />
									<div>
										<span class="increase">&nbsp;</span>
										<span class="decrease">&nbsp;</span>
									</div>
								</td>
								<td width="140">
									<span class="subtotal">${currency(cartItem.subtotal, true)}</span>
								</td>
								<td>
									<a href="javascript:;" class="remove">${message("shop.cart.remove")}</a>
								</td>
							</tr>
						[/#list]
					</table>
				[#else]
					<p>
						<a href="${base}/">${message("shop.cart.empty")}</a>
					</p>
				[/#if]
			</div>
		</div>
		[#if currentCart?? && currentCart.cartItems?has_content]
			<div class="row">
				<div class="span6">
					<dl id="gift" class="gift clearfix[#if !currentCart.giftNames?has_content] hidden[/#if]">
						[#if currentCart.giftNames?has_content]
							<dt>${message("Cart.gifts")}:</dt>
							[#list currentCart.giftNames as giftName]
								<dd title="${giftName}">${abbreviate(giftName, 50)} &times; 1</dd>
							[/#list]
						[/#if]
					</dl>
				</div>
				<div class="span6">
					<div class="total">
						<em id="promotion">${currentCart.promotionNames?join(", ")}</em>
						[#if !currentUser??]
							<em>${message("shop.cart.promotionTips")}</em>
						[/#if]
						${message("shop.cart.effectiveRewardPoint")}: <em id="effectiveRewardPoint">${currentCart.effectiveRewardPoint}</em>
						${message("shop.cart.effectivePrice")}: <strong id="effectivePrice">${currency(currentCart.effectivePrice, true, true)}</strong>
					</div>
				</div>
			</div>
			<div class="row">
				<div class="span12">
					<div class="bottom">
						<a href="javascript:;" id="clear" class="clear">${message("shop.cart.clear")}</a>
						<a href="${base}/order/checkout" id="checkout" class="checkout">${message("shop.cart.checkout")}</a>
					</div>
				</div>
			</div>
		[/#if]
	</div>
	[#include "/shop/include/footer.ftl" /]
</body>
</html>