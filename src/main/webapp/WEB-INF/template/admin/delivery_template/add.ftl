<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html;charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("admin.deliveryTemplate.add")} - Powered By SHOP++</title>
<meta name="author" content="SHOP++ Team" />
<meta name="copyright" content="SHOP++" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/webuploader.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/input.js"></script>
<style type="text/css">
.dropdownMenu {
	position: relative;
	z-index: 1000000;
}

.dropdownMenu ul {
	width: 140px;
	height: 300px;
	overflow-x: hidden;
	overflow-y: auto;
}

.container {
	width: 1000px;
	height: 400px;
	position: relative;
	overflow: hidden;
	border: 1px solid #dde9f5;
}

.container .item {
	float: left;
	position: absolute;
	top: 0px;
	left: 0px;
	filter: alpha(opacity = 80);
	opacity: 0.8;
	-webkit-user-select: none;
	-moz-user-select: none;
	-ms-user-select: none;
	-o-user-select: none;
	user-select: none;
	cursor: move;
	overflow: hidden;
	border: 1px solid #dddddd;
	background-color: #ffffff;
}

.container .selected {
	filter: alpha(opacity = 100);
	opacity: 1;
	-webkit-box-shadow: 0px 0px 10px #dddddd;
	-moz-box-shadow: 0px 0px 10px #dddddd;
	box-shadow: 0px 0px 10px #dddddd;
	border-color: #cccccc;
}

.container .editable {
	-webkit-user-select: text;
	-moz-user-select: text;
	-ms-user-select: text;
	-o-user-select: text;
	user-select: text;
	cursor: text;
	border-color: #7ecbfe;
}

.container .text {
	width: 100%;
	_width: auto;
	height: 100%;
	line-height: 22px;
	_float: left;
	color: #666666;
	font-size: 12pt;
	word-break: break-all;
	outline: none;
}

.container .resize {
	width: 6px;
	height: 6px;
	display: none;
	position: absolute;
	bottom: 0px;
	_bottom: -1px;
	right: 0px;
	_right: -1px;
	overflow: hidden;
	cursor: nw-resize;
	background-color: #cccccc;
}

.container .selected .resize {
	display: block;
}
</style>
<script type="text/javascript">
$().ready(function() {

	var $inputForm = $("#inputForm");
	var $content = $("#content");
	var $tagMenu = $("#tagMenu");
	var $tagMenuItem = $("#tagMenu li");
	var $deleteTag = $("#deleteTag");
	var $container = $("#container");
	var $filePicker = $("#filePicker");
	var $background = $("#background");
	var $width = $("#width");
	var $height = $("#height");
	var zIndex = 1;
	
	[@flash_message /]
	
	$tagMenu.hover(
		function() {
			$(this).children("ul").show();
		}, function() {
			$(this).children("ul").hide();
		}
	);
	
	$tagMenuItem.click(function() {
		var value = $(this).attr("val");
		if (value != "") {
			var $item = $('<div class="item"><div class="text">' + escapeHtml(value) + '<\/div><div class="resize"><\/div><\/div>').appendTo($container);
			var $text = $item.children("div.text");
			var $resize = $item.children("div.resize");
			var dragStart = {};
			var resizeStart = {};
			var dragging = false;
			var resizing = false;
			
			$text.mousedown(function(event) {
				if ($text.attr("contenteditable") == "true") {
					return true;
				}
				$item.css({"z-index": zIndex ++});
				var position = $item.position();
				dragStart.pageX = event.pageX;
				dragStart.pageY = event.pageY;
				dragStart.left = position.left;
				dragStart.top = position.top;
				dragging = true;
				return false;
			}).mouseup(function() {
				dragging = false;
			}).click(function() {
				$item.addClass("selected").siblings().removeClass("selected");
			}).dblclick(function() {
				$item.addClass("editable")
				$text.attr("contenteditable", "true");
			}).focusout(function() {
				$item.removeClass("editable")
				$text.attr("contenteditable", "false");
			});
			
			$resize.mousedown(function(event) {
				resizeStart.pageX = event.pageX;
				resizeStart.pageY = event.pageY;
				resizeStart.width = $item.width();
				resizeStart.height = $item.height();
				resizing = true;
				return false;
			}).mouseup(function() {
				resizing = false;
			});
			
			$(document).mousemove(function(event) {
				if (dragging) {
					$item.css({"left": dragStart.left + event.pageX - dragStart.pageX, "top": dragStart.top + event.pageY - dragStart.pageY});
					return false;
				}
				if (resizing) {
					$item.css({"width": resizeStart.width + event.pageX - resizeStart.pageX, "height": resizeStart.height + event.pageY - resizeStart.pageY});
					return false;
				}
			}).mouseup(function() {
				dragging = false;
				resizing = false;
			});
		}
		$tagMenu.children("ul").hide();
	});
	
	// 删除标签
	$deleteTag.click(function() {
		$container.find("div.selected").remove();
		return false;
	});
	
	// 背景图
	$filePicker.uploader({
		complete: function(file, data) {
			$container.css({
				background: "url(" + data.url + ") 0px 0px no-repeat"
			});
		}
	});
	
	$background.on("input propertychange change", function() {
		$container.css({
			background: "url(" + $background.val() + ") 0px 0px no-repeat"
		});
	});
	
	// 宽度
	$width.on("input propertychange change", function() {
		$container.width($width.val());
	});
	
	// 高度
	$height.on("input propertychange change", function() {
		$container.height($height.val());
	});
	
	// 表单验证
	$inputForm.validate({
		rules: {
			name: "required",
			background: {
				pattern: /^(http:\/\/|https:\/\/|\/).*$/i
			},
			width: {
				required: true,
				integer: true,
				min: 1
			},
			height: {
				required: true,
				integer: true,
				min: 1
			},
			offsetX: {
				required: true,
				integer: true
			},
			offsetY: {
				required: true,
				integer: true
			}
		},
		submitHandler: function(form) {
			if ($.trim($container.html()) == "") {
				$.message("warn", "${message("admin.deliveryTemplate.emptyNotAllow")}");
				return false;
			}
			$container.find("div.item").removeClass("selected editable").find("div.text").removeAttr("contenteditable");
			$content.val($container.html());
			$(form).find("input:submit").prop("disabled", true);
			form.submit();
		}
	});

});
</script>
</head>
<body>
	<div class="breadcrumb">
		${message("admin.deliveryTemplate.add")}
	</div>
	<form id="inputForm" action="save" method="post">
		<input type="hidden" id="content" name="content" />
		<table class="input">
			<tr>
				<th>
					<span class="requiredField">*</span>${message("DeliveryTemplate.name")}:
				</th>
				<td>
					<input type="text" name="name" class="text" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					${message("admin.common.action")}:
				</th>
				<td>
					<div id="tagMenu" class="dropdownMenu">
						<a href="javascript:;" class="button">${message("admin.deliveryTemplate.addTags")}</a>
						<ul>
							<li val="[#noparse]${deliveryCenter.name}[/#noparse]">${message("admin.deliveryTemplate.deliveryCenterName")}</li>
							<li val="[#noparse]${deliveryCenter.contact}[/#noparse]">${message("admin.deliveryTemplate.deliveryCenterContact")}</li>
							<li val="[#noparse]${deliveryCenter.areaName}[/#noparse]">${message("admin.deliveryTemplate.deliveryCenterArea")}</li>
							<li val="[#noparse]${deliveryCenter.address}[/#noparse]">${message("admin.deliveryTemplate.deliveryCenterAddress")}</li>
							<li val="[#noparse]${deliveryCenter.zipCode}[/#noparse]">${message("admin.deliveryTemplate.deliveryCenterZipCode")}</li>
							<li val="[#noparse]${deliveryCenter.phone}[/#noparse]">${message("admin.deliveryTemplate.deliveryCenterPhone")}</li>
							<li val="[#noparse]${deliveryCenter.mobile}[/#noparse]">${message("admin.deliveryTemplate.deliveryCenterMobile")}</li>
							<li val="[#noparse]${order.consignee}[/#noparse]">${message("admin.deliveryTemplate.orderConsignee")}</li>
							<li val="[#noparse]${order.areaName}[/#noparse]">${message("admin.deliveryTemplate.orderAreaName")}</li>
							<li val="[#noparse]${order.address}[/#noparse]">${message("admin.deliveryTemplate.orderAddress")}</li>
							<li val="[#noparse]${order.zipCode}[/#noparse]">${message("admin.deliveryTemplate.orderZipCode")}</li>
							<li val="[#noparse]${order.phone}[/#noparse]">${message("admin.deliveryTemplate.orderPhone")}</li>
							<li val="[#noparse]${order.sn}[/#noparse]">${message("admin.deliveryTemplate.orderSn")}</li>
							<li val="[#noparse]${order.freight}[/#noparse]">${message("admin.deliveryTemplate.orderFreight")}</li>
							<li val="[#noparse]${order.fee}[/#noparse]">${message("admin.deliveryTemplate.orderFee")}</li>
							<li val="[#noparse]${order.amountPaid}[/#noparse]">${message("admin.deliveryTemplate.orderAmountPaid")}</li>
							<li val="[#noparse]${order.weight}[/#noparse]">${message("admin.deliveryTemplate.orderWeight")}</li>
							<li val="[#noparse]${order.quantity}[/#noparse]">${message("admin.deliveryTemplate.orderQuantity")}</li>
							<li val="[#noparse]${currency(order.amount, true)}[/#noparse]">${message("admin.deliveryTemplate.orderAmount")}</li>
							<li val="[#noparse]${order.memo}[/#noparse]">${message("admin.deliveryTemplate.orderMemo")}</li>
							<li val="[#noparse]${setting.siteName}[/#noparse]">${message("admin.deliveryTemplate.siteName")}</li>
							<li val="[#noparse]${setting.siteUrl}[/#noparse]">${message("admin.deliveryTemplate.siteUrl")}</li>
							<li val="[#noparse]${setting.address}[/#noparse]">${message("admin.deliveryTemplate.siteAddress")}</li>
							<li val="[#noparse]${setting.phone}[/#noparse]">${message("admin.deliveryTemplate.sitePhone")}</li>
							<li val="[#noparse]${setting.zipCode}[/#noparse]">${message("admin.deliveryTemplate.siteZipCode")}</li>
							<li val="[#noparse]${setting.email}[/#noparse]">${message("admin.deliveryTemplate.siteEmail")}</li>
							<li val="[#noparse]${.now?string('yyyy-MM-dd')}[/#noparse]">${message("admin.deliveryTemplate.now")}</li>
						</ul>
					</div>
					<a href="javascript:;" id="deleteTag" class="button">${message("admin.deliveryTemplate.deleteTags")}</a>
				</td>
			</tr>
			<tr>
				<th>
					${message("DeliveryTemplate.content")}:
				</th>
				<td>
					<div id="container" class="container"></div>
				</td>
			</tr>
			<tr>
				<th>
					${message("DeliveryTemplate.background")}:
				</th>
				<td>
					<span class="fieldSet">
						<input type="text" id="background" name="background" class="text" maxlength="200" />
						<a href="javascript:;" id="filePicker" class="button">${message("admin.upload.filePicker")}</a>
					</span>
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>${message("DeliveryTemplate.width")}:
				</th>
				<td>
					<input type="text" id="width" name="width" class="text" value="1000" maxlength="9" />
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>${message("DeliveryTemplate.height")}:
				</th>
				<td>
					<input type="text" id="height" name="height" class="text" value="400" maxlength="9" />
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>${message("DeliveryTemplate.offsetX")}:
				</th>
				<td>
					<input type="text" name="offsetX" class="text" value="0" maxlength="9" />
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>${message("DeliveryTemplate.offsetY")}:
				</th>
				<td>
					<input type="text" name="offsetY" class="text" value="0" maxlength="9" />
				</td>
			</tr>
			<tr>
				<th>
					${message("DeliveryTemplate.isDefault")}:
				</th>
				<td>
					<input type="checkbox" name="isDefault" />
					<input type="hidden" name="_isDefault" value="false" />
				</td>
			</tr>
			<tr>
				<th>
					${message("DeliveryTemplate.memo")}
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