<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html;charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("admin.productNotify.list")} - Powered By SHOP++</title>
<meta name="author" content="SHOP++ Team" />
<meta name="copyright" content="SHOP++" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/list.js"></script>
<script type="text/javascript">
$().ready(function() {

	var $listForm = $("#listForm");
	var $selectAll = $("#selectAll");
	var $ids = $("#listTable input[name='ids']");
	var $sendButton = $("#sendButton");
	var $filterMenu = $("#filterMenu");
	var $filterMenuItem = $("#filterMenu li");
	
	[@flash_message /]
	
	// 发送到货通知
	$sendButton.click(function() {
		if ($sendButton.hasClass("disabled")) {
			return false;
		}
		var $checkedIds = $ids.filter(":enabled:checked");
		$.dialog({
			type: "warn",
			content: "${message("admin.productNotify.sendConfirm")}",
			ok: "${message("admin.dialog.ok")}",
			cancel: "${message("admin.dialog.cancel")}",
			onOk: function() {
				$.ajax({
					url: "send",
					type: "POST",
					data: $checkedIds.serialize(),
					dataType: "json",
					cache: false,
					success: function(message) {
						$.message(message);
						if (message.type == "success") {
							$checkedIds.closest("td").siblings(".hasSent").html('<span title="${message("admin.productNotify.hasSent")}" class="trueIcon">&nbsp;<\/span>');
						}
					}
				});
			}
		});
	});
	
	// 全选
	$selectAll.click(function() {
		var $this = $(this);
		var $enabledIds = $ids.filter(":enabled");
		if ($this.prop("checked")) {
			if ($enabledIds.filter(":checked").size() > 0) {
				$sendButton.removeClass("disabled");
			} else {
				$sendButton.addClass("disabled");
			}
		} else {
			$sendButton.addClass("disabled");
		}
	});
	
	// 选择
	$ids.click(function() {
		var $this = $(this);
		if ($this.prop("checked")) {
			$sendButton.removeClass("disabled");
		} else {
			if ($ids.filter(":enabled:checked").size() > 0) {
				$sendButton.removeClass("disabled");
			} else {
				$sendButton.addClass("disabled");
			}
		}
	});
	
	// 筛选菜单
	$filterMenu.hover(
		function() {
			$(this).children("ul").show();
		}, function() {
			$(this).children("ul").hide();
		}
	);
	
	// 筛选
	$filterMenuItem.click(function() {
		var $this = $(this);
		var $dest = $("#" + $this.attr("name"));
		if ($this.hasClass("checked")) {
			$dest.val("");
		} else {
			$dest.val($this.attr("val"));
		}
		$listForm.submit();
	});

});
</script>
</head>
<body>
	<div class="breadcrumb">
		${message("admin.productNotify.list")} <span>(${message("admin.page.total", page.total)})</span>
	</div>
	<form id="listForm" action="list" method="get">
		<input type="hidden" id="isMarketable" name="isMarketable" value="[#if isMarketable??]${isMarketable?string("true", "false")}[/#if]" />
		<input type="hidden" id="isOutOfStock" name="isOutOfStock" value="[#if isOutOfStock??]${isOutOfStock?string("true", "false")}[/#if]" />
		<input type="hidden" id="hasSent" name="hasSent" value="[#if hasSent??]${hasSent?string("true", "false")}[/#if]" />
		<div class="bar">
			<div class="buttonGroup">
				<a href="javascript:;" id="sendButton" class="button disabled">
					${message("admin.productNotify.send")}
				</a>
				<a href="javascript:;" id="deleteButton" class="iconButton disabled">
					<span class="deleteIcon">&nbsp;</span>${message("admin.common.delete")}
				</a>
				<a href="javascript:;" id="refreshButton" class="iconButton">
					<span class="refreshIcon">&nbsp;</span>${message("admin.common.refresh")}
				</a>
				<div id="filterMenu" class="dropdownMenu">
					<a href="javascript:;" class="button">
						${message("admin.productNotify.filter")}<span class="arrow">&nbsp;</span>
					</a>
					<ul class="check">
						<li name="isMarketable"[#if isMarketable] class="checked"[/#if] val="true">${message("admin.productNotify.marketable")}</li>
						<li name="isMarketable"[#if isMarketable?? && !isMarketable] class="checked"[/#if] val="false">${message("admin.productNotify.notMarketable")}</li>
						<li class="divider">&nbsp;</li>
						<li name="isOutOfStock"[#if isOutOfStock] class="checked"[/#if] val="true">${message("admin.productNotify.outOfStock")}</li>
						<li name="isOutOfStock"[#if isOutOfStock?? && !isOutOfStock] class="checked"[/#if] val="false">${message("admin.productNotify.inStock")}</li>
						<li class="divider">&nbsp;</li>
						<li name="hasSent"[#if hasSent] class="checked"[/#if] val="true">${message("admin.productNotify.hasSent")}</li>
						<li name="hasSent"[#if hasSent?? && !hasSent] class="checked"[/#if] val="false">${message("admin.productNotify.hasNotSent")}</li>
					</ul>
				</div>
				<div id="pageSizeMenu" class="dropdownMenu">
					<a href="javascript:;" class="button">
						${message("admin.page.pageSize")}<span class="arrow">&nbsp;</span>
					</a>
					<ul>
						<li[#if page.pageSize == 10] class="current"[/#if] val="10">10</li>
						<li[#if page.pageSize == 20] class="current"[/#if] val="20">20</li>
						<li[#if page.pageSize == 50] class="current"[/#if] val="50">50</li>
						<li[#if page.pageSize == 100] class="current"[/#if] val="100">100</li>
					</ul>
				</div>
			</div>
			<div id="searchPropertyMenu" class="dropdownMenu">
				<div class="search">
					<span class="arrow">&nbsp;</span>
					<input type="text" id="searchValue" name="searchValue" value="${page.searchValue}" maxlength="200" />
					<button type="submit">&nbsp;</button>
				</div>
				<ul>
					<li[#if page.searchProperty == "email"] class="current"[/#if] val="email">${message("ProductNotify.email")}</li>
				</ul>
			</div>
		</div>
		<table id="listTable" class="list">
			<tr>
				<th class="check">
					<input type="checkbox" id="selectAll" />
				</th>
				<th>
					<span>${message("admin.productNotify.skuName")}</span>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="member">${message("ProductNotify.member")}</a>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="email">${message("ProductNotify.email")}</a>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="createdDate">${message("admin.productNotify.createdDate")}</a>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="lastModifiedDate">${message("admin.productNotify.notifyDate")}</a>
				</th>
				<th>
					<span>${message("admin.productNotify.status")}</span>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="hasSent">${message("admin.productNotify.hasSent")}</a>
				</th>
			</tr>
			[#list page.content as productNotify]
				<tr>
					<td>
						<input type="checkbox" name="ids" value="${productNotify.id}" />
					</td>
					<td>
						<a href="${base}${productNotify.sku.path}" title="${productNotify.sku.name}" target="_blank">${abbreviate(productNotify.sku.name, 50, "...")}</a>
						[#if productNotify.sku.specifications?has_content]
							<span class="silver">[${productNotify.sku.specifications?join(", ")}]</span>
						[/#if]
					</td>
					<td>
						[#if productNotify.member??]
							<a href="../member/view?id=${productNotify.member.id}">${productNotify.member.username}</a>
						[#else]
							-
						[/#if]
					</td>
					<td>
						${productNotify.email}
					</td>
					<td>
						<span title="${productNotify.createdDate?string("yyyy-MM-dd HH:mm:ss")}">${productNotify.createdDate}</span>
					</td>
					<td>
						[#if productNotify.hasSent]
							<span title="${productNotify.lastModifiedDate?string("yyyy-MM-dd HH:mm:ss")}">${productNotify.lastModifiedDate}</span>
						[#else]
							-
						[/#if]
					</td>
					<td>
						[#if productNotify.sku.isMarketable]
							<span class="green">${message("admin.productNotify.marketable")}</span>
						[#else]
							${message("admin.productNotify.notMarketable")}
						[/#if]
						[#if productNotify.sku.isOutOfStock]
							${message("admin.productNotify.outOfStock")}
						[#else]
							<span class="green">${message("admin.productNotify.inStock")}</span>
						[/#if]
					</td>
					<td class="hasSent">
						<span title="${productNotify.hasSent?string(message("admin.productNotify.hasSent"), message("admin.productNotify.hasNotSent"))}" class="${productNotify.hasSent?string("true", "false")}Icon">&nbsp;</span>
					</td>
				</tr>
			[/#list]
		</table>
		[@pagination pageNumber = page.pageNumber totalPages = page.totalPages]
			[#include "/admin/include/pagination.ftl"]
		[/@pagination]
	</form>
</body>
</html>