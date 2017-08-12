<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html;charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("admin.freightConfig.edit")} - Powered By SHOP++</title>
<meta name="author" content="SHOP++ Team" />
<meta name="copyright" content="SHOP++" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.lSelect.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/input.js"></script>
<script type="text/javascript">
$().ready(function() {

	var $inputForm = $("#inputForm");
	var $areaId = $("#areaId");
	
	[@flash_message /]
	
	$areaId.lSelect({
		url: "${base}/common/area"
	});
	
	// 表单验证
	$inputForm.validate({
		rules: {
			areaId: {
				required: true,
				remote: {
					url: "check_area?id=${freightConfig.id}&shippingMethodId=${freightConfig.shippingMethod.id}",
					cache: false
				}
			},
			firstPrice: {
				required: true,
				min: 0,
				decimal: {
					integer: 12,
					fraction: ${setting.priceScale}
				}
			},
			continuePrice: {
				required: true,
				min: 0,
				decimal: {
					integer: 12,
					fraction: ${setting.priceScale}
				}
			}
		},
		messages: {
			areaId: {
				remote: "${message("admin.freightConfig.areaExists")}"
			}
		}
	});

});
</script>
</head>
<body>
	<div class="breadcrumb">
		${message("admin.freightConfig.edit")}
	</div>
	<form id="inputForm" action="update" method="post">
		<input type="hidden" name="id" value="${freightConfig.id}" />
		<table class="input">
			<tr>
				<th>
					${message("FreightConfig.shippingMethod")}:
				</th>
				<td>
					${freightConfig.shippingMethod.name}
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>${message("FreightConfig.area")}:
				</th>
				<td>
					<span class="fieldSet">
						<input type="hidden" id="areaId" name="areaId" value="${freightConfig.area.id}" treePath="${freightConfig.area.treePath}" />
					</span>
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>${message("FreightConfig.firstPrice")}:
				</th>
				<td>
					<input type="text" name="firstPrice" class="text" value="${freightConfig.firstPrice}" maxlength="16" />
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>${message("FreightConfig.continuePrice")}:
				</th>
				<td>
					<input type="text" name="continuePrice" class="text" value="${freightConfig.continuePrice}" maxlength="16" />
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