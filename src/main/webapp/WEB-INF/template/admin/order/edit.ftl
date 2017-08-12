<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html;charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("admin.order.edit")} - Powered By SHOP++</title>
<meta name="author" content="SHOP++ Team" />
<meta name="copyright" content="SHOP++" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.tools.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.lSelect.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/input.js"></script>
<script type="text/javascript">
$().ready(function() {

	var $inputForm = $("#inputForm");
	var $amount = $("#amount");
	var $freight = $("#freight");
	var $offsetAmount = $("#offsetAmount");
	var $isInvoice = $("#isInvoice");
	var $invoiceTitle = $("#invoiceTitle");
	var $tax = $("#tax");
	var $areaId = $("#areaId");
	var isLocked = false;
	var timeouts = {};
	
	[@flash_message /]
	
	// 地区选择
	$areaId.lSelect({
		url: "${base}/common/area"
	});
	
	// 获取订单锁
	function acquireLock() {
		$.ajax({
			url: "acquire_lock",
			type: "POST",
			data: {id: ${order.id}},
			dataType: "json",
			cache: false,
			success: function(data) {
				if (!data) {
					$.message("warn", "${message("admin.order.locked")}");
					$inputForm.find(":input:not(:button)").prop("disabled", true);
					isLocked = true;
				}
			}
		});
	}
	
	// 获取订单锁
	acquireLock();
	setInterval(function() {
		if (!isLocked) {
			acquireLock();
		}
	}, 50000);
	
	// 开具发票
	$isInvoice.click(function() {
		if ($(this).prop("checked")) {
			$invoiceTitle.prop("disabled", false);
			$tax.prop("disabled", false);
		} else {
			$invoiceTitle.prop("disabled", true);
			$tax.prop("disabled", true);
		}
	});
	
	// 计算
	$amount.add($freight).add($offsetAmount).add($isInvoice).add($invoiceTitle).add($tax).on("input propertychange change", function(event) {
		if (event.type != "propertychange" || event.originalEvent.propertyName == "value") {
			calculate($(this));
		}
	});
	
	// 计算
	function calculate($input) {
		var name = $input.attr("name");
		clearTimeout(timeouts[name]);
		timeouts[name] = setTimeout(function() {
			if ($inputForm.valid()) {
				$.ajax({
					url: "calculate",
					type: "POST",
					data: {
						id: ${order.id},
						freight: $freight.val(),
						offsetAmount: $offsetAmount.val(),
						tax: !$tax.prop("disabled") ? $tax.val() : 0
					},
					dataType: "json",
					cache: false,
					success: function(data) {
						if (data.message.type == "success") {
							$amount.text(currency(data.amount, true));
						} else {
							$.message(data.message);
						}
					}
				});
			}
		}, 500);
	}
	
	// 表单验证
	$inputForm.validate({
		rules: {
			freight: {
				required: true,
				min: 0,
				decimal: {
					integer: 12,
					fraction: ${setting.priceScale}
				}
			},
			offsetAmount: {
				required: true,
				number: true,
				decimal: {
					integer: 12,
					fraction: ${setting.priceScale}
				}
			},
			rewardPoint: {
				required: true,
				digits: true
			},
			invoiceTitle: "required",
			tax: {
				required: true,
				min: 0,
				decimal: {
					integer: 12,
					fraction: ${setting.priceScale}
				}
			},
			consignee: "required",
			areaId: "required",
			address: "required",
			zipCode: {
				required: true,
				pattern: /^\d{6}$/
			},
			phone: {
				required: true,
				pattern: /^\d{3,4}-?\d{7,9}$/
			}
		}
	});

});
</script>
</head>
<body>
	<div class="breadcrumb">
		${message("admin.order.edit")}
	</div>
	<ul id="tab" class="tab">
		<li>
			<input type="button" value="${message("admin.order.orderInfo")}" />
		</li>
		<li>
			<input type="button" value="${message("admin.order.productInfo")}" />
		</li>
	</ul>
	<form id="inputForm" action="update" method="post">
		<input type="hidden" name="id" value="${order.id}" />
		<table class="input tabContent">
			<tr>
				<th>
					${message("Order.sn")}:
				</th>
				<td width="360">
					${order.sn}
				</td>
				<th>
					${message("admin.common.createdDate")}:
				</th>
				<td>
					${order.createdDate?string("yyyy-MM-dd HH:mm:ss")}
				</td>
			</tr>
			<tr>
				<th>
					${message("Order.type")}:
				</th>
				<td>
					${message("Order.Type." + order.type)}
				</td>
				<th>
					${message("Order.status")}:
				</th>
				<td>
					${message("Order.Status." + order.status)}
					[#if order.hasExpired()]
						<span class="silver">(${message("admin.order.hasExpired")})</span>
					[#else]
						[#if order.expire??]
							<span class="silver">(${message("Order.expire")}: ${order.expire?string("yyyy-MM-dd HH:mm:ss")})</span>
						[/#if]
					[/#if]
				</td>
			</tr>
			<tr>
				<th>
					${message("Order.member")}:
				</th>
				<td>
					<a href="../member/view?id=${order.member.id}">${order.member.username}</a>
				</td>
				<th>
					${message("Member.memberRank")}:
				</th>
				<td>
					${order.member.memberRank.name}
				</td>
			</tr>
			<tr>
				<th>
					${message("Order.price")}:
				</th>
				<td>
					${currency(order.price, true)}
				</td>
				<th>
					${message("Order.exchangePoint")}:
				</th>
				<td>
					${order.exchangePoint}
				</td>
			</tr>
			<tr>
				<th>
					${message("Order.amount")}:
				</th>
				<td>
					<span id="amount" class="red">${currency(order.amount, true)}</span>
				</td>
				<th>
					${message("Order.amountPaid")}:
				</th>
				<td>
					${currency(order.amountPaid, true)}
					[#if order.amountPayable > 0]
						<span class="silver">(${message("Order.amountPayable")}: ${currency(order.amountPayable, true)})</span>
					[/#if]
				</td>
			</tr>
			<tr>
				<th>
					${message("Order.weight")}:
				</th>
				<td>
					${order.weight}
				</td>
				<th>
					${message("Order.quantity")}:
				</th>
				<td>
					${order.quantity}
				</td>
			</tr>
			<tr>
				<th>
					${message("admin.order.promotion")}:
				</th>
				<td>
					[#if order.promotionNames?has_content]
						${order.promotionNames?join(", ")}
					[#else]
						-
					[/#if]
				</td>
				<th>
					${message("Order.promotionDiscount")}:
				</th>
				<td>
					${currency(order.promotionDiscount, true)}
				</td>
			</tr>
			<tr>
				<th>
					${message("admin.order.coupon")}:
				</th>
				<td>
					${(order.couponCode.coupon.name)!"-"}
				</td>
				<th>
					${message("Order.couponDiscount")}:
				</th>
				<td>
					${currency(order.couponDiscount, true)}
				</td>
			</tr>
			<tr>
				<th>
					${message("Order.fee")}:
				</th>
				<td>
					${currency(order.fee, true)}
				</td>
				<th>
					${message("Order.freight")}:
				</th>
				<td>
					[#if order.isDelivery]
						<input type="text" id="freight" name="freight" class="text" value="${order.freight}" maxlength="16" />
					[#else]
						${currency(order.freight, true)}
					[/#if]
				</td>
			</tr>
			<tr>
				<th>
					${message("Order.offsetAmount")}:
				</th>
				<td>
					<input type="text" id="offsetAmount" name="offsetAmount" class="text" value="${order.offsetAmount}" maxlength="16" />
				</td>
				<th>
					${message("Order.rewardPoint")}:
				</th>
				<td>
					<input type="text" name="rewardPoint" class="text" value="${order.rewardPoint}" maxlength="9" />
				</td>
			</tr>
			<tr>
				<th>
					${message("Order.paymentMethod")}:
				</th>
				<td>
					<select name="paymentMethodId">
						<option value="">${message("admin.common.choose")}</option>
						[#list paymentMethods as paymentMethod]
							<option value="${paymentMethod.id}"[#if paymentMethod == order.paymentMethod] selected="selected"[/#if]>${paymentMethod.name}</option>
						[/#list]
					</select>
				</td>
				<th>
					${message("Order.shippingMethod")}:
				</th>
				<td>
					[#if order.isDelivery]
						<select name="shippingMethodId">
							<option value="">${message("admin.common.choose")}</option>
							[#list shippingMethods as shippingMethod]
								<option value="${shippingMethod.id}"[#if shippingMethod == order.shippingMethod] selected="selected"[/#if]>${shippingMethod.name}</option>
							[/#list]
						</select>
					[#else]
						-
					[/#if]
				</td>
			</tr>
			<tr>
				<th>
					${message("Invoice.title")}:
				</th>
				<td>
					<span class="fieldSet">
						<input type="text" id="invoiceTitle" name="invoiceTitle" class="text" value="${(order.invoice.title)!}" maxlength="200"[#if !order.invoice??] disabled="disabled"[/#if] />
						<label>
							<input type="checkbox" id="isInvoice" name="isInvoice" value="true"[#if order.invoice??] checked="checked"[/#if] />
						</label>
					</span>
				</td>
				<th>
					${message("Order.tax")}:
				</th>
				<td>
					<input type="text" id="tax" name="tax" class="text" value="${order.tax}" maxlength="16"[#if !order.invoice??] disabled="disabled"[/#if] />
				</td>
			</tr>
			[#if order.isDelivery]
				<tr>
					<th>
						${message("Order.consignee")}:
					</th>
					<td>
						<input type="text" name="consignee" class="text" value="${order.consignee}" maxlength="200" />
					</td>
					<th>
						${message("Order.area")}:
					</th>
					<td>
						<span class="fieldSet">
							<input type="hidden" id="areaId" name="areaId" value="${(order.area.id)!}" treePath="${(order.area.treePath)!}" />
						</span>
					</td>
				</tr>
				<tr>
					<th>
						${message("Order.address")}:
					</th>
					<td>
						<input type="text" name="address" class="text" value="${order.address}" maxlength="200" />
					</td>
					<th>
						${message("Order.zipCode")}:
					</th>
					<td>
						<input type="text" name="zipCode" class="text" value="${order.zipCode}" maxlength="200" />
					</td>
				</tr>
				<tr>
					<th>
						${message("Order.phone")}:
					</th>
					<td>
						<input type="text" name="phone" class="text" value="${order.phone}" maxlength="200" />
					</td>
					<th>
						${message("Order.memo")}:
					</th>
					<td>
						<input type="text" name="memo" class="text" value="${order.memo}" maxlength="200" />
					</td>
				</tr>
			[/#if]
		</table>
		<table class="item tabContent">
			<tr>
				<th>
					${message("OrderItem.sn")}
				</th>
				<th>
					${message("OrderItem.name")}
				</th>
				<th>
					${message("OrderItem.price")}
				</th>
				<th>
					${message("OrderItem.quantity")}
				</th>
				<th>
					${message("OrderItem.subtotal")}
				</th>
			</tr>
			[#list order.orderItems as orderItem]
				<tr>
					<td>
						${orderItem.sn}
					</td>
					<td width="400">
						[#if orderItem.sku??]
							<a href="${base}${orderItem.sku.path}" title="${orderItem.name}" target="_blank">${abbreviate(orderItem.name, 50, "...")}</a>
						[#else]
							<span title="${orderItem.name}">${abbreviate(orderItem.name, 50, "...")}</span>
						[/#if]
						[#if orderItem.specifications?has_content]
							<span class="silver">[${orderItem.specifications?join(", ")}]</span>
						[/#if]
						[#if orderItem.type != "general"]
							<span class="red">[${message("Product.Type." + orderItem.type)}]</span>
						[/#if]
					</td>
					<td>
						[#if orderItem.type == "general"]
							${currency(orderItem.price, true)}
						[#else]
							-
						[/#if]
					</td>
					<td>
						${orderItem.quantity}
					</td>
					<td>
						[#if orderItem.type == "general"]
							${currency(orderItem.subtotal, true)}
						[#else]
							-
						[/#if]
					</td>
				</tr>
			[/#list]
		</table>
		<table class="input">
			<tr>
				<th>
					&nbsp;
				</th>
				<td>
					<input type="submit" class="button" value="${message("admin.common.submit")}" />
					<input type="button" class="button" value="${message("admin.common.back")}" onclick="history.back(); return false;" />
				</td>
			</tr>
		</table>
	</form>
</body>
</html>