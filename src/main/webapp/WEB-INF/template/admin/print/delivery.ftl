<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html;charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("admin.print.delivery")} - Powered By SHOP++</title>
<meta name="author" content="SHOP++ Team" />
<meta name="copyright" content="SHOP++" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<style type="text/css">
.bar {
	height: 30px;
	line-height: 30px;
	border-bottom: 1px solid #d7e8f8;
	background-color: #eff7ff;
}

.content {
	margin: 4px;
	position: relative;
	overflow: hidden;
	box-shadow: 0px 0px 6px rgba(0, 0, 0, 0.1);
	-moz-box-shadow: 0px 0px 6px rgba(0, 0, 0, 0.1);
	-webkit-box-shadow: 0px 0px 6px rgba(0, 0, 0, 0.1);
	border: 1px solid #dde9f5;
	[#if deliveryTemplate??]
		[#if deliveryTemplate.width??]
			width: ${deliveryTemplate.width}px;
		[/#if]
		[#if deliveryTemplate.height??]
			height: ${deliveryTemplate.height}px;
		[/#if]
		[#if deliveryTemplate.background??]
			background: url(${deliveryTemplate.background}) 0px 0px no-repeat;
		[/#if]
	[/#if]
}

.content .item {
	float: left;
	padding: 1px;
	position: absolute;
	overflow: hidden;
}

.content .text {
	width: 100%;
	_width: auto;
	height: 100%;
	line-height: 22px;
	_float: left;
	color: #000000;
	font-size: 12pt;
	word-break: break-all;
}
</style>
<style type="text/css" media="print">
.bar {
	display: none;
}

.content {
	margin: 0px;
	border: none;
	[#if deliveryTemplate??]
		[#if deliveryTemplate.offsetX??]
			margin-left: ${deliveryTemplate.offsetX}px;
		[/#if]
		[#if deliveryTemplate.offsetY??]
			margin-top: ${deliveryTemplate.offsetY}px;
		[/#if]
		[#if deliveryTemplate.background??]
			background: none;
		[/#if]
	[/#if]
}
</style>
<script type="text/javascript">
$().ready(function() {

	var $deliveryForm = $("#deliveryForm");
	var $deliveryTemplate = $("#deliveryTemplate");
	var $deliveryCenter = $("#deliveryCenter");
	var $print = $("#print");
	
	$deliveryTemplate.add($deliveryCenter).change(function() {
		if ($deliveryTemplate.val() != "" && $deliveryCenter.val() != "") {
			$deliveryForm.submit();
		}
	});
	
	$print.click(function() {
		if ($deliveryTemplate.val() == "") {
			$.message("warn", "${message("admin.print.deliveryTemplateRequired")}");
			return false;
		}
		if ($deliveryCenter.val() == "") {
			$.message("warn", "${message("admin.print.deliveryCenterRequired")}");
			return false;
		}
		window.print();
		return false;
	});

});
</script>
</head>
<body>
	<div class="bar">
		<form id="deliveryForm" action="delivery" method="get">
			<input type="hidden" name="orderId" value="${order.id}" />
			<a href="javascript:;" id="print" class="button">${message("admin.print.print")}</a>
			${message("admin.print.deliveryTemplate")}:
			<select id="deliveryTemplate" name="deliveryTemplateId">
				<option value="">${message("admin.common.choose")}</option>
				[#list deliveryTemplates as template]
					<option value="${template.id}"[#if template == deliveryTemplate] selected="selected"[/#if]>${template.name}</option>
				[/#list]
			</select>
			${message("admin.print.deliveryCenter")}:
			<select id="deliveryCenter" name="deliveryCenterId">
				<option value="">${message("admin.common.choose")}</option>
				[#list deliveryCenters as center]
					<option value="${center.id}"[#if center == deliveryCenter] selected="selected"[/#if]>${center.name}</option>
				[/#list]
			</select>
		</form>
	</div>
	[#if deliveryTemplate?? && deliveryCenter??]
		[#noautoesc]
			<div class="content">${deliveryTemplate.resolveContent()}</div>
		[/#noautoesc]
	[/#if]
</body>
</html>