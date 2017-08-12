<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
	<meta name="format-detection" content="telephone=no">
	<meta name="author" content="SHOP++ Team">
	<meta name="copyright" content="SHOP++">
	<title>${message("shop.order.payment")}[#if showPowered] - Powered By SHOP++[/#if]</title>
	<link href="${base}/favicon.ico" rel="icon">
	<link href="${base}/resources/mobile/shop/css/bootstrap.css" rel="stylesheet">
	<link href="${base}/resources/mobile/shop/css/font-awesome.css" rel="stylesheet">
	<link href="${base}/resources/mobile/shop/css/animate.css" rel="stylesheet">
	<link href="${base}/resources/mobile/shop/css/common.css" rel="stylesheet">
	<link href="${base}/resources/mobile/shop/css/order.css" rel="stylesheet">
	<!--[if lt IE 9]>
		<script src="${base}/resources/mobile/shop/js/html5shiv.js"></script>
		<script src="${base}/resources/mobile/shop/js/respond.js"></script>
	<![endif]-->
	<script src="${base}/resources/mobile/shop/js/jquery.js"></script>
	<script src="${base}/resources/mobile/shop/js/bootstrap.js"></script>
	<script src="${base}/resources/mobile/shop/js/velocity.js"></script>
	<script src="${base}/resources/mobile/shop/js/velocity.ui.js"></script>
	<script src="${base}/resources/mobile/shop/js/underscore.js"></script>
	<script src="${base}/resources/mobile/shop/js/common.js"></script>
	<script type="text/javascript">
	$().ready(function() {
		
		var $paymentModal = $("#paymentModal");
		var $amountPayable = $("#amountPayable");
		var $feeItem = $("#feeItem");
		var $fee = $("#fee");
		var $paymentForm = $("#paymentForm");
		var $paymentPluginId = $("#paymentPluginId");
		var $paymentPluginItem = $("#paymentPlugin div.list-group-item");
		var $paymentButton = $("#paymentButton");
		
		[#if order.paymentMethod.method == "online"]
			// 获取订单锁
			function acquireLock() {
				$.ajax({
					url: "acquire_lock",
					type: "POST",
					data: {
						sn: ${order.sn}
					},
					dataType: "json"
				});
			}
			
			// 获取订单锁
			acquireLock();
			setInterval(function() {
				acquireLock();
			}, 50000);
			
			// 支付插件项
			$paymentPluginItem.click(function() {
				var $element = $(this);
				$element.addClass("active").siblings().removeClass("active");
				var paymentPluginId = $element.data("payment-plugin-id");
				$paymentPluginId.val(paymentPluginId);
				calculateAmount();
			});
			
			calculateAmount();
			
			// 计算金额
			function calculateAmount() {
				$.ajax({
					url: "calculate_amount",
					type: "GET",
					data: {
						sn: "${order.sn}",
						paymentPluginId: $paymentPluginId.val()
					},
					dataType: "json",
					success: function(data) {
						$amountPayable.text(currency(data.amount, true));
						if (data.fee > 0) {
							$fee.text(currency(data.fee, true));
							if ($feeItem.is(":hidden")) {
								$feeItem.velocity("slideDown");
							}
						} else {
							if ($feeItem.is(":visible")) {
								$feeItem.velocity("slideUp");
							}
						}
					}
				});
			}
		[/#if]
	
	});
	</script>
</head>
<body class="order-payment">
	<header class="header-fixed">
		<a class="pull-left" href="javascript: history.back();">
			<span class="glyphicon glyphicon-menu-left"></span>
		</a>
		${message("shop.order.payment")}
	</header>
	<main>
		<div class="container-fluid">
			<div class="list-group list-group-flat">
				<div class="list-group-item small">
					${message("Order.sn")}: ${order.sn}
					<a class="pull-right gray-darker" href="${base}/member/order/view?orderSn=${order.sn}">[${message("shop.order.view")}]</a>
				</div>
			</div>
			<div class="list-group list-group-flat">
				<div class="list-group-item small">
					[#if order.status == "pendingPayment"]
						${message("shop.order.pendingPayment")}
					[#elseif order.status == "pendingReview"]
						${message("shop.order.pendingReview")}
					[#else]
						${message("shop.order.pending")}
					[/#if]
				</div>
				[#if order.expire?? && !order.hasExpired()]
					<div class="list-group-item small">
						<strong class="orange">${message("Order.expire")}: ${order.expire?string("yyyy-MM-dd HH:mm:ss")}</strong>
					</div>
				[/#if]
			</div>
			<div class="list-group list-group-flat">
				[#if order.paymentMethod.method == "online"]
					<div class="list-group-item">
						${message("Order.amountPayable")}
						<strong id="amountPayable" class="pull-right red"></strong>
					</div>
					<div id="feeItem" class="fee-item list-group-item">
						${message("Order.fee")}
						<strong id="fee" class="pull-right red"></strong>
					</div>
				[#else]
					<div class="list-group-item">
						${message("Order.amountPayable")}
						<strong id="amountPayable" class="pull-right">${currency(order.amountPayable, true)}</strong>
					</div>
				[/#if]
				<div class="list-group-item">
					${message("Order.shippingMethod")}
					<span class="pull-right">${order.shippingMethodName!"-"}</span>
				</div>
				<div class="list-group-item">
					${message("Order.paymentMethod")}
					<span class="pull-right">${order.paymentMethodName!"-"}</span>
				</div>
			</div>
			[#if order.paymentMethod.method == "online"]
				[#if paymentPlugins?has_content]
					<form id="paymentForm" action="${base}/payment" method="post">
						<input name="type" type="hidden" value="ORDER_PAYMENT">
						<input name="orderSn" type="hidden" value="${order.sn}">
						<input id="paymentPluginId" name="paymentPluginId" type="hidden" value="${defaultPaymentPlugin.id}">
						<div class="panel panel-flat">
							<div class="panel-heading">${message("Order.paymentMethod")}</div>
							<div class="panel-body">
								<div id="paymentPlugin" class="list-group list-group-flat">
									[#list paymentPlugins as paymentPlugin]
										<div class="[#if paymentPlugin == defaultPaymentPlugin]active [/#if]list-group-item" data-payment-plugin-id="${paymentPlugin.id}">
											<div class="media">
												<div class="media-left media-middle">
													<div class="media-object">
														[#if paymentPlugin.logo?has_content]
															<img src="${paymentPlugin.logo}" alt="${paymentPlugin.paymentName}">
														[#else]
															${paymentPlugin.paymentName}
														[/#if]
													</div>
												</div>
												<div class="media-body media-middle">
													<span class="small gray-darker">${abbreviate(paymentPlugin.description, 100, "...")}</span>
												</div>
												<div class="media-right media-middle">
													<span class="glyphicon glyphicon-ok-circle"></span>
												</div>
											</div>
										</div>
									[/#list]
								</div>
							</div>
							<div class="panel-footer">
								<button id="paymentButton" class="btn btn-lg btn-red btn-flat btn-block" type="submit">${message("shop.order.payNow")}</button>
							</div>
						</div>
					</form>
				[/#if]
			[#else]
				<div class="panel panel-flat">
					[#noautoesc]
						${order.paymentMethod.content}
					[/#noautoesc]
				</div>
			[/#if]
		</div>
	</main>
</body>
</html>