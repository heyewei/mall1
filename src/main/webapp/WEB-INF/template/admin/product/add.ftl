<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html;charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("admin.product.add")} - Powered By SHOP++</title>
<meta name="author" content="SHOP++ Team" />
<meta name="copyright" content="SHOP++" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.tools.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/webuploader.js"></script>
<script type="text/javascript" src="${base}/resources/admin/ueditor/ueditor.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/input.js"></script>
<style type="text/css">
	.parameterTable table th {
		width: 146px;
	}
	
	.specificationTable span {
		padding: 10px;
	}
	
	.skuTable td {
		border: 1px solid #dde9f5;
	}
</style>
<script type="text/javascript">
$().ready(function() {

	var $inputForm = $("#inputForm");
	var $isDefault = $("#isDefault");
	var $productCategoryId = $("#productCategoryId");
	var $type = $("#type");
	var $price = $("#price");
	var $cost = $("#cost");
	var $marketPrice = $("#marketPrice");
	var $filePicker = $("#filePicker");
	var $rewardPoint = $("#rewardPoint");
	var $exchangePoint = $("#exchangePoint");
	var $stock = $("#stock");
	var $promotionIds = $("input[name='promotionIds']");
	var $introduction = $("#introduction");
	var $productImageTable = $("#productImageTable");
	var $addProductImage = $("#addProductImage");
	var $parameterTable = $("#parameterTable");
	var $addParameter = $("#addParameter");
	var $resetParameter = $("#resetParameter");
	var $attributeTable = $("#attributeTable");
	var $specificationTable = $("#specificationTable");
	var $resetSpecification = $("#resetSpecification");
	var $skuTable = $("#skuTable");
	var productImageIndex = 0;
	var parameterIndex = 0;
	var specificationItemEntryId = 0;
	var hasSpecification = false;
	
	[@flash_message /]
	
	var previousProductCategoryId = getCookie("previousProductCategoryId");
	previousProductCategoryId != null ? $productCategoryId.val(previousProductCategoryId) : previousProductCategoryId = $productCategoryId.val();
	
	loadParameter();
	loadAttribute();
	loadSpecification();
	
	$filePicker.uploader();
	
	$introduction.editor();
	
	// 商品分类
	$productCategoryId.change(function() {
		if ($attributeTable.find("select[value!='']").size() > 0) {
			$.dialog({
				type: "warn",
				content: "${message("admin.product.productCategoryChangeConfirm")}",
				width: 450,
				onOk: function() {
					if ($parameterTable.find("input.parameterEntryValue[value!='']").size() == 0) {
						loadParameter();
					}
					loadAttribute();
					if ($skuTable.find("input:text[value!='']").size() == 0) {
						loadSpecification();
					}
					previousProductCategoryId = $productCategoryId.val();
				},
				onCancel: function() {
					$productCategoryId.val(previousProductCategoryId);
				}
			});
		} else {
			if ($parameterTable.find("input.parameterEntryValue[value!='']").size() == 0) {
				loadParameter();
			}
			loadAttribute();
			if ($skuTable.find("input:text[value!='']").size() == 0) {
				loadSpecification();
			}
			previousProductCategoryId = $productCategoryId.val();
		}
	});
	
	// 类型
	$type.change(function() {
		changeView();
		buildSkuTable();
	});
	
	// 修改视图
	function changeView() {
		if (hasSpecification) {
			$isDefault.prop("disabled", true);
			$price.add($cost).add($marketPrice).add($rewardPoint).add($exchangePoint).add($stock).prop("disabled", true).closest("tr").hide();
			switch ($type.val()) {
				case "general":
					$promotionIds.prop("disabled", false).closest("tr").show();
					break;
				case "exchange":
					$promotionIds.prop("disabled", true).closest("tr").hide();
					break;
				case "gift":
					$promotionIds.prop("disabled", true).closest("tr").hide();
					break;
			}
		} else {
			$isDefault.prop("disabled", false);
			$cost.add($marketPrice).add($stock).prop("disabled", false).closest("tr").show();
			switch ($type.val()) {
				case "general":
					$price.add($rewardPoint).add($promotionIds).prop("disabled", false).closest("tr").show();
					$exchangePoint.prop("disabled", true).closest("tr").hide();
					break;
				case "exchange":
					$price.add($rewardPoint).add($promotionIds).prop("disabled", true).closest("tr").hide();
					$exchangePoint.prop("disabled", false).closest("tr").show();
					break;
				case "gift":
					$price.add($rewardPoint).add($exchangePoint).add($promotionIds).prop("disabled", true).closest("tr").hide();
					break;
			}
		}
	}
	
	// 增加商品图片
	$addProductImage.click(function() {
		$productImageTable.append(
			[@compress single_line = true]
				'<tr>
					<td>
						<input type="file" name="productImages[' + productImageIndex + '].file" class="productImageFile" \/>
					<\/td>
					<td>
						<input type="text" name="productImages[' + productImageIndex + '].title" class="text" maxlength="200" \/>
					<\/td>
					<td>
						<input type="text" name="productImages[' + productImageIndex + '].order" class="text productImageOrder" maxlength="9" style="width: 50px;" \/>
					<\/td>
					<td>
						<a href="javascript:;" class="remove">[${message("admin.common.remove")}]<\/a>
					<\/td>
				<\/tr>'
			[/@compress]
		);
		productImageIndex ++;
	});
	
	// 删除商品图片
	$productImageTable.on("click", "a.remove", function() {
		$(this).closest("tr").remove();
	});
	
	// 增加参数
	$addParameter.click(function() {
		$(
			[@compress single_line = true]
				'<tr>
					<td colspan="2">
						<table>
							<tr>
								<th>
									${message("Parameter.group")}:
								<\/th>
								<td>
									<input type="text" name="parameterValues[' + parameterIndex + '].group" class="text parameterGroup" maxlength="200" \/>
								<\/td>
								<td>
									<a href="javascript:;" class="remove group">[${message("admin.common.remove")}]<\/a>
									<a href="javascript:;" class="add">[${message("admin.common.add")}]<\/a>
								<\/td>
							<\/tr>
							<tr>
								<th>
									<input type="text" name="parameterValues[' + parameterIndex + '].entries[0].name" class="text parameterEntryName" maxlength="200" style="width: 50px;" \/>
								<\/th>
								<td>
									<input type="text" name="parameterValues[' + parameterIndex + '].entries[0].value" class="text parameterEntryValue" maxlength="200" \/>
								<\/td>
								<td>
									<a href="javascript:;" class="remove">[${message("admin.common.remove")}]<\/a>
								<\/td>
							<\/tr>
						<\/table>
					<\/td>
				<\/tr>'
			[/@compress]
		).appendTo($parameterTable).find("table").data("parameterIndex", parameterIndex).data("parameterEntryIndex", 1);
		parameterIndex ++;
	});
	
	// 重置参数
	$resetParameter.click(function() {
		$.dialog({
			type: "warn",
			content: "${message("admin.product.resetParameterConfirm")}",
			width: 450,
			onOk: function() {
				loadParameter();
			}
		});
	});
	
	// 删除参数
	$parameterTable.on("click", "a.remove", function() {
		var $this = $(this);
		if ($this.hasClass("group")) {
			$this.closest("table").closest("tr").remove();
		} else {
			if ($this.closest("table").find("tr").size() <= 2) {
				$.message("warn", "${message("admin.common.deleteAllNotAllowed")}");
				return false;
			}
			$this.closest("tr").remove();
		}
	});
	
	// 增加参数
	$parameterTable.on("click", "a.add", function() {
		var $table = $(this).closest("table");
		var parameterIndex = $table.data("parameterIndex");
		var parameterEntryIndex = $table.data("parameterEntryIndex");
		$table.append(
			[@compress single_line = true]
				'<tr>
					<th>
						<input type="text" name="parameterValues[' + parameterIndex + '].entries[' + parameterEntryIndex + '].name" class="text parameterEntryName" maxlength="200" style="width: 50px;" \/>
					<\/th>
					<td>
						<input type="text" name="parameterValues[' + parameterIndex + '].entries[' + parameterEntryIndex + '].value" class="text parameterEntryValue" maxlength="200" \/>
					<\/td>
					<td>
						<a href="javascript:;" class="remove">[${message("admin.common.remove")}]<\/a>
					<\/td>
				<\/tr>'
			[/@compress]
		);
		$table.data("parameterEntryIndex", parameterEntryIndex + 1);
	});
	
	// 加载参数
	function loadParameter() {
		$.ajax({
			url: "parameters",
			type: "GET",
			data: {productCategoryId: $productCategoryId.val()},
			dataType: "json",
			success: function(data) {
				parameterIndex = 0;
				$parameterTable.find("tr:gt(0)").remove();
				$.each(data, function(i, parameter) {
					var $parameterGroupTable = $(
						[@compress single_line = true]
							'<tr>
								<td colspan="2">
									<table>
										<tr>
											<th>
												${message("Parameter.group")}:
											<\/th>
											<td>
												<input type="text" name="parameterValues[' + parameterIndex + '].group" class="text parameterGroup" value="' + escapeHtml(parameter.group) + '" maxlength="200" \/>
											<\/td>
											<td>
												<a href="javascript:;" class="remove group">[${message("admin.common.remove")}]<\/a>
												<a href="javascript:;" class="add">[${message("admin.common.add")}]<\/a>
											<\/td>
										<\/tr>
									<\/table>
								<\/td>
							<\/tr>'
						[/@compress]
					).appendTo($parameterTable).find("table").data("parameterIndex", parameterIndex);
					
					var parameterEntryIndex = 0;
					$.each(parameter.names, function(i, name) {
						$parameterGroupTable.append(
							[@compress single_line = true]
								'<tr>
									<th>
										<input type="text" name="parameterValues[' + parameterIndex + '].entries[' + parameterEntryIndex + '].name" class="text parameterEntryName" value="' + escapeHtml(name) + '" maxlength="200" style="width: 50px;" \/>
									<\/th>
									<td>
										<input type="text" name="parameterValues[' + parameterIndex + '].entries[' + parameterEntryIndex + '].value" class="text parameterEntryValue" maxlength="200" \/>
									<\/td>
									<td>
										<a href="javascript:;" class="remove">[${message("admin.common.remove")}]<\/a>
									<\/td>
								<\/tr>'
							[/@compress]
						);
						parameterEntryIndex ++;
					});
					$parameterGroupTable.data("parameterEntryIndex", parameterEntryIndex);
					parameterIndex ++;
				});
			}
		});
	}
	
	// 加载属性
	function loadAttribute() {
		$.ajax({
			url: "attributes",
			type: "GET",
			data: {productCategoryId: $productCategoryId.val()},
			dataType: "json",
			success: function(data) {
				$attributeTable.empty();
				$.each(data, function(i, attribute) {
					var $select = $(
						[@compress single_line = true]
							'<tr>
								<th>' + escapeHtml(attribute.name) + ':<\/th>
								<td>
									<select name="attribute_' + attribute.id + '">
										<option value="">${message("admin.common.choose")}<\/option>
									<\/select>
								<\/td>
							<\/tr>'
						[/@compress]
					).appendTo($attributeTable).find("select");
					$.each(attribute.options, function(j, option) {
						$select.append('<option value="' + escapeHtml(option) + '">' + escapeHtml(option) + '<\/option>');
					});
				});
			}
		});
	}
	
	// 重置规格
	$resetSpecification.click(function() {
		$.dialog({
			type: "warn",
			content: "${message("admin.product.resetSpecificationConfirm")}",
			width: 450,
			onOk: function() {
				hasSpecification = false;
				changeView();
				loadSpecification();
			}
		});
	});
	
	// 选择规格
	$specificationTable.on("change", "input:checkbox", function() {
		if ($specificationTable.find("input:checkbox:checked").size() > 0) {
			hasSpecification = true;
		} else {
			hasSpecification = false;
		}
		changeView();
		buildSkuTable();
	});
	
	// 规格
	$specificationTable.on("change", "input:text", function() {
		var $this = $(this);
		var value = $.trim($this.val());
		if (value == "") {
			$this.val($this.data("value"));
			return false;
		}
		if ($this.hasClass("specificationItemEntryValue")) {
			var values = $this.closest("tr").find("input.specificationItemEntryValue").not($this).map(function() {
				return $.trim($(this).val());
			}).get();
			if ($.inArray(value, values) >= 0) {
				$.message("warn", "${message("admin.product.specificationItemEntryValueRepeated")}");
				$this.val($this.data("value"));
				return false;
			}
		}
		$this.data("value", value);
		buildSkuTable();
	});
	
	// 是否默认
	$skuTable.on("change", "input.isDefault", function() {
		var $this = $(this);
		if ($this.prop("checked")) {
			$skuTable.find("input.isDefault").not($this).prop("checked", false);
		} else {
			$this.prop("checked", true);
		}
	});
	
	// 是否启用
	$skuTable.on("change", "input.isEnabled", function() {
		var $this = $(this);
		if ($this.prop("checked")) {
			$this.closest("tr").find("input:not(.isEnabled)").prop("disabled", false);
		} else {
			$this.closest("tr").find("input:not(.isEnabled)").prop("disabled", true).end().find("input.isDefault").prop("checked", false);
		}
		if ($skuTable.find("input.isDefault:not(:disabled):checked").size() == 0) {
			$skuTable.find("input.isDefault:not(:disabled):first").prop("checked", true);
		}
	});
	
	// 生成SKU表
	function buildSkuTable() {
		var type = $type.val();
		var skuValues = {};
		var specificationItems = [];
		if (!hasSpecification) {
			$skuTable.empty()
			return false;
		}
		$specificationTable.find("tr:gt(0)").each(function() {
			var $this = $(this);
			var $checked = $this.find("input:checkbox:checked");
			if ($checked.size() > 0) {
				var specificationItem = {};
				specificationItem.name = $this.find("input.specificationItemName").val();
				specificationItem.entries = $checked.map(function() {
					return {
						id: $(this).siblings("input.specificationItemEntryId").val(),
						value: $(this).siblings("input.specificationItemEntryValue").val()
					};
				}).get();
				specificationItems.push(specificationItem);
			}
		});
		var skus = cartesianProductOf($.map(specificationItems, function(specificationItem) {
			return [specificationItem.entries];
		}));
		$skuTable.find("tr:gt(0)").each(function() {
			var $this = $(this);
			skuValues[$this.data("ids")] = {
				price: $this.find("input.price").val(),
				cost: $this.find("input.cost").val(),
				marketPrice: $this.find("input.marketPrice").val(),
				rewardPoint: $this.find("input.rewardPoint").val(),
				exchangePoint: $this.find("input.exchangePoint").val(),
				stock: $this.find("input.stock").val(),
				isDefault: $this.find("input.isDefault").prop("checked"),
				isEnabled: $this.find("input.isEnabled").prop("checked")
			};
		});
		$titleTr = $('<tr><\/tr>').appendTo($skuTable.empty());
		$.each(specificationItems, function(i, specificationItem) {
			$titleTr.append('<th>' + escapeHtml(specificationItem.name) + '<\/th>');
		});
		$titleTr.append(
			[@compress single_line = true]
				(type == "general" ? '<th>${message("Sku.price")}<\/th>' : '') + '
				<th>
					${message("Sku.cost")}
				<\/th>
				<th>
					${message("Sku.marketPrice")}
				<\/th>
				' + (type == "general" ? '<th>${message("Sku.rewardPoint")}<\/th>' : '') + 
				(type == "exchange" ? '<th>${message("Sku.exchangePoint")}<\/th>' : '') + '
				<th>
					${message("Sku.stock")}
				<\/th>
				<th>
					${message("Sku.isDefault")}
				<\/th>
				<th>
					${message("admin.product.isEnabled")}
				<\/th>'
			[/@compress]
		);
		$.each(skus, function(i, entries) {
			var ids = [];
			$skuTr = $('<tr><\/tr>').appendTo($skuTable);
			$.each(entries, function(j, entry) {
				$skuTr.append(
					[@compress single_line = true]
						'<td>
							' + escapeHtml(entry.value) + '
							<input type="hidden" name="skuList[' + i + '].specificationValues[' + j + '].id" value="' + entry.id + '" \/>
							<input type="hidden" name="skuList[' + i + '].specificationValues[' + j + '].value" value="' + escapeHtml(entry.value) + '" \/>
						<\/td>'
					[/@compress]
				);
				ids.push(entry.id);
			});
			var skuValue = skuValues[ids.join(",")];
			var price = skuValue != null && skuValue.price != null ? skuValue.price : "";
			var cost = skuValue != null && skuValue.cost != null ? skuValue.cost : "";
			var marketPrice = skuValue != null && skuValue.marketPrice != null ? skuValue.marketPrice : "";
			var rewardPoint = skuValue != null && skuValue.rewardPoint != null ? skuValue.rewardPoint : "";
			var exchangePoint = skuValue != null && skuValue.exchangePoint != null ? skuValue.exchangePoint : "";
			var stock = skuValue != null && skuValue.stock != null ? skuValue.stock : "";
			var isDefault = skuValue != null && skuValue.isDefault != null ? skuValue.isDefault : false;
			var isEnabled = skuValue != null && skuValue.isEnabled != null ? skuValue.isEnabled : false;
			$skuTr.append(
				[@compress single_line = true]
					(type == "general" ? '<td><input type="text" name="skuList[' + i + '].price" class="text price" value="' + price + '" maxlength="16" style="width: 50px;" \/><\/td>' : '') + '
					<td>
						<input type="text" name="skuList[' + i + '].cost" class="text cost" value="' + cost + '" maxlength="16" style="width: 50px;" \/>
					<\/td>
					<td>
						<input type="text" name="skuList[' + i + '].marketPrice" class="text marketPrice" value="' + marketPrice + '" maxlength="16" style="width: 50px;" \/>
					<\/td>
					' + (type == "general" ? '<td><input type="text" name="skuList[' + i + '].rewardPoint" class="text rewardPoint" value="' + rewardPoint + '" maxlength="9" style="width: 50px;" \/><\/td>' : '') + 
					(type == "exchange" ? '<td><input type="text" name="skuList[' + i + '].exchangePoint" class="text exchangePoint" value="' + exchangePoint + '" maxlength="9" style="width: 50px;" \/><\/td>' : '') + '
					<td>
						<input type="text" name="skuList[' + i + '].stock" class="text stock" value="' + stock + '" maxlength="9" style="width: 50px;" \/>
					<\/td>
					<td>
						<input type="checkbox" name="skuList[' + i + '].isDefault" class="isDefault" value="true"' + (isDefault ? ' checked="checked"' : '') + ' \/>
						<input type="hidden" name="_skuList[' + i + '].isDefault" value="false" \/>
					<\/td>
					<td>
						<input type="checkbox" name="isEnabled" class="isEnabled" value="true"' + (isEnabled ? ' checked="checked"' : '') + ' \/>
					<\/td>'
				[/@compress]
			).data("ids", ids.join(","));
			if (!isEnabled) {
				$skuTr.find(":input:not(.isEnabled)").prop("disabled", true);
			}
		});
		if ($skuTable.find("input.isDefault:not(:disabled):checked").size() == 0) {
			$skuTable.find("input.isDefault:not(:disabled):first").prop("checked", true);
		}
	}
	
	// 笛卡尔积
	function cartesianProductOf(array) {
		function addTo(current, args) {
			var i, copy;
			var rest = args.slice(1);
			var isLast = !rest.length;
			var result = [];
			for (i = 0; i < args[0].length; i++) {
				copy = current.slice();
				copy.push(args[0][i]);
				if (isLast) {
					result.push(copy);
				} else {
					result = result.concat(addTo(copy, rest));
				}
			}
			return result;
		}
		return addTo([], array);
	}
	
	// 加载规格
	function loadSpecification() {
		$.ajax({
			url: "specifications",
			type: "GET",
			data: {productCategoryId: $productCategoryId.val()},
			dataType: "json",
			success: function(data) {
				$specificationTable.find("tr:gt(0)").remove();
				$skuTable.empty();
				$.each(data, function(i, specification) {
					var $td = $(
						[@compress single_line = true]
							'<tr>
								<th>
									<input type="text" name="specificationItems[' + i + '].name" class="text specificationItemName" value="' + escapeHtml(specification.name) + '" style="width: 50px;" \/>
								<\/th>
								<td><\/td>
							<\/tr>'
						[/@compress]
					).appendTo($specificationTable).find("input").data("value", specification.name).end().find("td");
					$.each(specification.options, function(j, option) {
						$(
							[@compress single_line = true]
								'<span>
									<input type="checkbox" name="specificationItems[' + i + '].entries[' + j + '].isSelected" value="true" \/>
									<input type="hidden" name="_specificationItems[' + i + '].entries[' + j + '].isSelected" value="false" \/>
									<input type="hidden" name="specificationItems[' + i + '].entries[' + j + '].id" class="text specificationItemEntryId" value="' + specificationItemEntryId + '" \/>
									<input type="text" name="specificationItems[' + i + '].entries[' + j + '].value" class="text specificationItemEntryValue" value="' + escapeHtml(option) + '" style="width: 50px;" \/>
								<\/span>'
							[/@compress]
						).appendTo($td).find("input.specificationItemEntryValue").data("value", option);
						specificationItemEntryId ++;
					});
				});
			}
		});
	}
	
	$.validator.addClassRules({
		productImageFile: {
			required: true,
			extension: "${setting.uploadImageExtension}"
		},
		productImageOrder: {
			digits: true
		},
		parameterGroup: {
			required: true
		},
		price: {
			required: true,
			min: 0,
			decimal: {
				integer: 12,
				fraction: ${setting.priceScale}
			}
		},
		cost: {
			min: 0,
			decimal: {
				integer: 12,
				fraction: ${setting.priceScale}
			}
		},
		marketPrice: {
			min: 0,
			decimal: {
				integer: 12,
				fraction: ${setting.priceScale}
			}
		},
		rewardPoint: {
			digits: true
		},
		exchangePoint: {
			required: true,
			digits: true
		},
		stock: {
			required: true,
			digits: true
		}
	});
	
	// 表单验证
	$inputForm.validate({
		rules: {
			productCategoryId: "required",
			sn: {
				pattern: /^[0-9a-zA-Z_-]+$/,
				remote: {
					url: "check_sn",
					cache: false
				}
			},
			name: "required",
			"sku.price": {
				required: true,
				min: 0,
				decimal: {
					integer: 12,
					fraction: ${setting.priceScale}
				}
			},
			"sku.cost": {
				min: 0,
				decimal: {
					integer: 12,
					fraction: ${setting.priceScale}
				}
			},
			"sku.marketPrice": {
				min: 0,
				decimal: {
					integer: 12,
					fraction: ${setting.priceScale}
				}
			},
			image: {
				pattern: /^(http:\/\/|https:\/\/|\/).*$/i
			},
			weight: "digits",
			"sku.rewardPoint": "digits",
			"sku.exchangePoint": {
				digits: true,
				required: true
			},
			"sku.stock": {
				required: true,
				digits: true
			}
		},
		messages: {
			sn: {
				pattern: "${message("common.validate.illegal")}",
				remote: "${message("common.validate.exist")}"
			}
		},
		submitHandler: function(form) {
			if (hasSpecification && $skuTable.find("input.isEnabled:checked").size() == 0) {
				$.message("warn", "${message("admin.product.specificationSkuRequired")}");
				return false;
			}
			addCookie("previousProductCategoryId", $productCategoryId.val(), {expires: 24 * 60 * 60});
			$(form).find("input:submit").prop("disabled", true);
			form.submit();
		}
	});

});
</script>
</head>
<body>
	<div class="breadcrumb">
		${message("admin.product.add")}
	</div>
	<form id="inputForm" action="save" method="post" enctype="multipart/form-data">
		<input type="hidden" id="isDefault" name="sku.isDefault" value="true" />
		<ul id="tab" class="tab">
			<li>
				<input type="button" value="${message("admin.product.base")}" />
			</li>
			<li>
				<input type="button" value="${message("admin.product.introduction")}" />
			</li>
			<li>
				<input type="button" value="${message("admin.product.productImage")}" />
			</li>
			<li>
				<input type="button" value="${message("admin.product.parameter")}" />
			</li>
			<li>
				<input type="button" value="${message("admin.product.attribute")}" />
			</li>
			<li>
				<input type="button" value="${message("admin.product.specification")}" />
			</li>
		</ul>
		<table class="input tabContent">
			<tr>
				<th>
					${message("Product.productCategory")}:
				</th>
				<td>
					<select id="productCategoryId" name="productCategoryId">
						[#list productCategoryTree as productCategory]
							<option value="${productCategory.id}">
								[#if productCategory.grade != 0]
									[#list 1..productCategory.grade as i]
										&nbsp;&nbsp;
									[/#list]
								[/#if]
								${productCategory.name}
							</option>
						[/#list]
					</select>
				</td>
			</tr>
			<tr>
				<th>
					${message("Product.type")}:
				</th>
				<td>
					<select id="type" name="type">
						[#list types as type]
							<option value="${type}">${message("Product.Type." + type)}</option>
						[/#list]
					</select>
				</td>
			</tr>
			<tr>
				<th>
					${message("Product.sn")}:
				</th>
				<td>
					<input type="text" name="sn" class="text" maxlength="100" title="${message("admin.product.snTitle")}" />
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>${message("Product.name")}:
				</th>
				<td>
					<input type="text" name="name" class="text" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					${message("Product.caption")}:
				</th>
				<td>
					<input type="text" name="caption" class="text" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>${message("Sku.price")}:
				</th>
				<td>
					<input type="text" id="price" name="sku.price" class="text" maxlength="16" />
				</td>
			</tr>
			<tr>
				<th>
					${message("Sku.cost")}:
				</th>
				<td>
					<input type="text" id="cost" name="sku.cost" class="text" maxlength="16" title="${message("admin.product.costTitle")}" />
				</td>
			</tr>
			<tr>
				<th>
					${message("Sku.marketPrice")}:
				</th>
				<td>
					<input type="text" id="marketPrice" name="sku.marketPrice" class="text" maxlength="16" title="${message("admin.product.marketPriceTitle")}" />
				</td>
			</tr>
			<tr>
				<th>
					${message("Product.image")}:
				</th>
				<td>
					<span class="fieldSet">
						<input type="text" name="image" class="text" maxlength="200" title="${message("admin.product.imageTitle")}" />
						<a href="javascript:;" id="filePicker" class="button">${message("admin.upload.filePicker")}</a>
					</span>
				</td>
			</tr>
			<tr>
				<th>
					${message("Product.unit")}:
				</th>
				<td>
					<input type="text" name="unit" class="text" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					${message("Product.weight")}:
				</th>
				<td>
					<input type="text" name="weight" class="text" maxlength="9" title="${message("admin.product.weightTitle")}" />
				</td>
			</tr>
			<tr>
				<th>
					${message("Sku.rewardPoint")}:
				</th>
				<td>
					<input type="text" id="rewardPoint" name="sku.rewardPoint" class="text" maxlength="9" title="${message("admin.product.rewardPointTitle")}" />
				</td>
			</tr>
			<tr class="hidden">
				<th>
					<span class="requiredField">*</span>${message("Sku.exchangePoint")}:
				</th>
				<td>
					<input type="text" id="exchangePoint" name="sku.exchangePoint" class="text" maxlength="9" disabled="disabled" />
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>${message("Sku.stock")}:
				</th>
				<td>
					<input type="text" id="stock" name="sku.stock" class="text" value="1" maxlength="9" />
				</td>
			</tr>
			<tr>
				<th>
					${message("Product.brand")}:
				</th>
				<td>
					<select name="brandId">
						<option value="">${message("admin.common.choose")}</option>
						[#list brands as brand]
							<option value="${brand.id}">
								${brand.name}
							</option>
						[/#list]
					</select>
				</td>
			</tr>
			[#if promotions?has_content]
				<tr>
					<th>
						${message("Product.promotions")}:
					</th>
					<td>
						[#list promotions as promotion]
							<label title="${promotion.title}">
								<input type="checkbox" name="promotionIds" value="${promotion.id}" />${promotion.name}
							</label>
						[/#list]
					</td>
				</tr>
			[/#if]
			[#if productTags?has_content]
				<tr>
					<th>
						${message("Product.productTags")}:
					</th>
					<td>
						[#list productTags as productTag]
							<label>
								<input type="checkbox" name="productTagIds" value="${productTag.id}" />${productTag.name}
							</label>
						[/#list]
					</td>
				</tr>
			[/#if]
			<tr>
				<th>
					${message("admin.common.setting")}:
				</th>
				<td>
					<label>
						<input type="checkbox" name="isMarketable" value="true" checked="checked" />${message("Product.isMarketable")}
						<input type="hidden" name="_isMarketable" value="false" />
					</label>
					<label>
						<input type="checkbox" name="isList" value="true" checked="checked" />${message("Product.isList")}
						<input type="hidden" name="_isList" value="false" />
					</label>
					<label>
						<input type="checkbox" name="isTop" value="true" />${message("Product.isTop")}
						<input type="hidden" name="_isTop" value="false" />
					</label>
					<label>
						<input type="checkbox" name="isDelivery" value="true" checked="checked" />${message("Product.isDelivery")}
						<input type="hidden" name="_isDelivery" value="false" />
					</label>
				</td>
			</tr>
			<tr>
				<th>
					${message("Product.memo")}:
				</th>
				<td>
					<input type="text" name="memo" class="text" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					${message("Product.keyword")}:
				</th>
				<td>
					<input type="text" name="keyword" class="text" maxlength="200" title="${message("admin.product.keywordTitle")}" />
				</td>
			</tr>
			<tr>
				<th>
					${message("Product.seoTitle")}:
				</th>
				<td>
					<input type="text" name="seoTitle" class="text" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					${message("Product.seoKeywords")}:
				</th>
				<td>
					<input type="text" name="seoKeywords" class="text" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					${message("Product.seoDescription")}:
				</th>
				<td>
					<input type="text" name="seoDescription" class="text" maxlength="200" />
				</td>
			</tr>
		</table>
		<table class="input tabContent">
			<tr>
				<td>
					<textarea id="introduction" name="introduction" class="editor" style="width: 100%;"></textarea>
				</td>
			</tr>
		</table>
		<table id="productImageTable" class="item tabContent">
			<tr>
				<td colspan="4">
					<a href="javascript:;" id="addProductImage" class="button">${message("admin.product.addProductImage")}</a>
				</td>
			</tr>
			<tr>
				<th>
					${message("ProductImage.file")}
				</th>
				<th>
					${message("ProductImage.title")}
				</th>
				<th>
					${message("admin.common.order")}
				</th>
				<th>
					${message("admin.common.action")}
				</th>
			</tr>
		</table>
		<table id="parameterTable" class="parameterTable input tabContent">
			<tr>
				<th>
					&nbsp;
				</th>
				<td>
					<a href="javascript:;" id="addParameter" class="button">${message("admin.product.addParameter")}</a>
					<a href="javascript:;" id="resetParameter" class="button">${message("admin.product.resetParameter")}</a>
				</td>
			</tr>
		</table>
		<table id="attributeTable" class="input tabContent"></table>
		<div class="tabContent">
			<table id="specificationTable" class="specificationTable input">
				<tr>
					<th>
						&nbsp;
					</th>
					<td>
						<a href="javascript:;" id="resetSpecification" class="button">${message("admin.product.resetSpecification")}</a>
					</td>
				</tr>
			</table>
			<table id="skuTable" class="skuTable item"></table>
		</div>
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