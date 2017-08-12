<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html;charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("admin.orderReturns.view")} - Powered By SHOP++</title>
<meta name="author" content="SHOP++ Team" />
<meta name="copyright" content="SHOP++" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.tools.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/input.js"></script>
<script type="text/javascript">
$().ready(function() {

	[@flash_message /]

});
</script>
</head>
<body>
	<div class="breadcrumb">
		${message("admin.orderReturns.view")}
	</div>
	<ul id="tab" class="tab">
		<li>
			<input type="button" value="${message("admin.orderReturns.base")}" />
		</li>
		<li>
			<input type="button" value="${message("admin.orderReturns.orderReturnsItem")}" />
		</li>
	</ul>
	<table class="input tabContent">
		<tr>
			<th>
				${message("OrderReturns.sn")}:
			</th>
			<td>
				${orderReturns.sn}
			</td>
			<th>
				${message("admin.common.createdDate")}:
			</th>
			<td>
				${orderReturns.createdDate?string("yyyy-MM-dd HH:mm:ss")}
			</td>
		</tr>
		<tr>
			<th>
				${message("OrderReturns.shippingMethod")}:
			</th>
			<td>
				${orderReturns.shippingMethod!"-"}
			</td>
			<th>
				${message("OrderReturns.deliveryCorp")}:
			</th>
			<td>
				${orderReturns.deliveryCorp!"-"}
			</td>
		</tr>
		<tr>
			<th>
				${message("OrderReturns.trackingNo")}:
			</th>
			<td>
				${orderReturns.trackingNo!"-"}
			</td>
			<th>
				${message("OrderReturns.freight")}:
			</th>
			<td>
				${currency(orderReturns.freight, true)!"-"}
			</td>
		</tr>
		<tr>
			<th>
				${message("OrderReturns.shipper")}:
			</th>
			<td>
				${orderReturns.shipper!"-"}
			</td>
			<th>
				${message("OrderReturns.phone")}:
			</th>
			<td>
				${orderReturns.phone!"-"}
			</td>
		</tr>
		<tr>
			<th>
				${message("OrderReturns.area")}:
			</th>
			<td>
				${orderReturns.area!"-"}
			</td>
			<th>
				${message("OrderReturns.address")}:
			</th>
			<td>
				${orderReturns.address!"-"}
			</td>
		</tr>
		<tr>
			<th>
				${message("OrderReturns.zipCode")}:
			</th>
			<td>
				${orderReturns.zipCode!"-"}
			</td>
			<th>
				${message("OrderReturns.order")}:
			</th>
			<td>
				${orderReturns.order.sn}
			</td>
		</tr>
		<tr>
			<th>
				${message("OrderReturns.memo")}:
			</th>
			<td colspan="3">
				${orderReturns.memo!"-"}
			</td>
		</tr>
	</table>
	<table class="item tabContent">
		<tr>
			<th>
				${message("OrderReturnsItem.sn")}
			</th>
			<th>
				${message("OrderReturnsItem.name")}
			</th>
			<th>
				${message("OrderReturnsItem.quantity")}
			</th>
		</tr>
		[#list orderReturns.orderReturnsItems as orderReturnsItem]
			<tr>
				<td>
					${orderReturnsItem.sn}
				</td>
				<td>
					<span title="${orderReturnsItem.name}">${abbreviate(orderReturnsItem.name, 50, "...")}</span>
				</td>
				<td>
					${orderReturnsItem.quantity}
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
				<input type="button" class="button" value="${message("admin.common.back")}" onclick="history.back(); return false;" />
			</td>
		</tr>
	</table>
</body>
</html>