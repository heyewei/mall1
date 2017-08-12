<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html;charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("admin.orderRefunds.view")} - Powered By SHOP++</title>
<meta name="author" content="SHOP++ Team" />
<meta name="copyright" content="SHOP++" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
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
		${message("admin.orderRefunds.view")}
	</div>
	<table class="input">
		<tr>
			<th>
				${message("OrderRefunds.sn")}:
			</th>
			<td>
				${orderRefunds.sn}
			</td>
			<th>
				${message("admin.common.createdDate")}:
			</th>
			<td>
				${orderRefunds.createdDate?string("yyyy-MM-dd HH:mm:ss")}
			</td>
		</tr>
		<tr>
			<th>
				${message("OrderRefunds.method")}:
			</th>
			<td>
				${message("OrderRefunds.Method." + orderRefunds.method)}
			</td>
			<th>
				${message("OrderRefunds.paymentMethod")}:
			</th>
			<td>
				${orderRefunds.paymentMethod!"-"}
			</td>
		</tr>
		<tr>
			<th>
				${message("OrderRefunds.bank")}:
			</th>
			<td>
				${orderRefunds.bank!"-"}
			</td>
			<th>
				${message("OrderRefunds.account")}:
			</th>
			<td>
				${orderRefunds.account!"-"}
			</td>
		</tr>
		<tr>
			<th>
				${message("OrderRefunds.amount")}:
			</th>
			<td>
				${currency(orderRefunds.amount, true)}
			</td>
			<th>
				${message("OrderRefunds.payee")}:
			</th>
			<td>
				${orderRefunds.payee!"-"}
			</td>
		</tr>
		<tr>
			<th>
				${message("OrderRefunds.order")}:
			</th>
			<td>
				${orderRefunds.order.sn}
			</td>
			<th>
				${message("OrderRefunds.memo")}:
			</th>
			<td>
				${orderRefunds.memo!"-"}
			</td>
		</tr>
		<tr>
			<th>
				&nbsp;
			</th>
			<td colspan="3">
				<input type="button" class="button" value="${message("admin.common.back")}" onclick="history.back(); return false;" />
			</td>
		</tr>
	</table>
</body>
</html>