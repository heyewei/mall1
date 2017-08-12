<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html;charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("admin.stock.stockIn")} - Powered By SHOP++</title>
<meta name="author" content="SHOP++ Team" />
<meta name="copyright" content="SHOP++" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.tools.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.autocomplete.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/input.js"></script>
<script type="text/javascript">
$().ready(function() {

	var $inputForm = $("#inputForm");
	var $skuId = $("#skuId");
	var $skuSelect = $("#skuSelect");
	var $sn = $("#sn");
	var $name = $("#name");
	var $stock = $("#stock");
	var $allocatedStock = $("#allocatedStock");
	
	[@flash_message /]
	
	// SKU选择
	$skuSelect.autocomplete("sku_select", {
		dataType: "json",
		max: 20,
		width: 218,
		scrollHeight: 300,
		parse: function(data) {
			return $.map(data, function(item) {
				return {
					data: item,
					value: item.name
				}
			});
		},
		formatItem: function(item) {
			return '<span title="' + escapeHtml(item.name) + '">' + escapeHtml(abbreviate(item.name, 50, "...")) + '<\/span>' + (item.specifications.length > 0 ? ' <span class="silver">[' + escapeHtml(item.specifications.join(", ")) + ']<\/span>' : '');
		}
	}).result(function(event, item) {
		$skuId.val(item.id);
		$sn.text(item.sn).closest("tr").show();
		$name.html(escapeHtml(item.name) + (item.specifications.length > 0 ? ' <span class="silver">[' + escapeHtml(item.specifications.join(", ")) + ']<\/span>' : '')).closest("tr").show();
		$stock.text(item.stock).closest("tr").show();
		$allocatedStock.text(item.allocatedStock).closest("tr").show();
	});
	
	// 表单验证
	$inputForm.validate({
		rules: {
			quantity: {
				required: true,
				integer: true,
				min: 1
			}
		},
		submitHandler: function(form) {
			if ($skuId.val() == "") {
				$.message("warn", "${message("admin.stock.skuRequired")}");
				return false;
			}
			$(form).find("input:submit").prop("disabled", true);
			form.submit();
		}
	});

});
</script>
</head>
<body>
	<div class="breadcrumb">
		${message("admin.stock.stockIn")}
	</div>
	<form id="inputForm" action="stock_in" method="post">
		<input type="hidden" id="skuId" name="skuId" value="${(sku.id)!}" />
		<table class="input">
			<tr>
				<th>
					${message("admin.stock.skuSelect")}:
				</th>
				<td>
					<input type="text" id="skuSelect" name="skuSelect" class="text" maxlength="200" title="${message("admin.stock.skuSelectTitle")}" />
				</td>
			</tr>
			<tr[#if !sku??] class="hidden"[/#if]>
				<th>
					${message("Sku.sn")}:
				</th>
				<td id="sn">
					${(sku.sn)!}
				</td>
			</tr>
			<tr[#if !sku??] class="hidden"[/#if]>
				<th>
					${message("Sku.name")}:
				</th>
				<td id="name">
					${(sku.name)!}
					[#if sku?? && sku.specifications?has_content]
						<span class="silver">[${sku.specifications?join(", ")}]</span>
					[/#if]
				</td>
			</tr>
			<tr[#if !sku??] class="hidden"[/#if]>
				<th>
					${message("Sku.stock")}:
				</th>
				<td id="stock">
					${(sku.stock)!}
				</td>
			</tr>
			<tr[#if !sku??] class="hidden"[/#if]>
				<th>
					${message("Sku.allocatedStock")}:
				</th>
				<td id="allocatedStock">
					${(sku.allocatedStock)!}
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>${message("admin.stock.quantity")}:
				</th>
				<td>
					<input type="text" name="quantity" class="text" maxlength="16" />
				</td>
			</tr>
			<tr>
				<th>
					${message("admin.stock.memo")}:
				</th>
				<td>
					<input type="text" name="memo" class="text" maxlength="200" />
				</td>
			</tr>
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