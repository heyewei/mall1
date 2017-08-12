<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html;charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("member.receiver.list")}[#if showPowered] - Powered By SHOP++[/#if]</title>
<meta name="author" content="SHOP++ Team" />
<meta name="copyright" content="SHOP++" />
<link href="${base}/resources/member/css/animate.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/member/css/common.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/member/css/member.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/member/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/member/js/common.js"></script>
<script type="text/javascript">
$().ready(function() {

	var $delete = $("#listTable a.delete");
	
	[#if flashMessage?has_content]
		$.alert("${flashMessage}");
	[/#if]
	
	// 删除
	$delete.click(function() {
		if (confirm("${message("shop.dialog.deleteConfirm")}")) {
			var $element = $(this);
			var receiverId = $element.data("receiver-id");
			$.ajax({
				url: "delete",
				type: "POST",
				data: {receiverId: receiverId},
				dataType: "json",
				success: function() {
					var $item = $element.closest("tr");
					if ($item.siblings("tr").size() < 2) {
						setTimeout(function() {
							location.reload(true);
						}, 3000);
					}
					$item.remove();
				}
			});
		}
		return false;
	});

});
</script>
</head>
<body>
	[#assign current = "receiverList" /]
	[#include "/shop/include/header.ftl" /]
	<div class="container member">
		<div class="row">
			[#include "/member/include/navigation.ftl" /]
			<div class="span10">
				<div class="list">
					<div class="title">${message("member.receiver.list")}</div>
					<div class="bar">
						<a href="add" class="button">${message("member.receiver.add")}</a>
					</div>
					<table id="listTable" class="list">
						<tr>
							<th>
								${message("Receiver.consignee")}
							</th>
							<th>
								${message("Receiver.address")}
							</th>
							<th>
								${message("Receiver.isDefault")}
							</th>
							<th>
								${message("member.common.action")}
							</th>
						</tr>
						[#list page.content as receiver]
							<tr[#if !receiver_has_next] class="last"[/#if]>
								<td>
									${receiver.consignee}
								</td>
								<td>
									<span title="${receiver.areaName}${receiver.address}">${receiver.areaName}${abbreviate(receiver.address, 30, "...")}</span>
								</td>
								<td>
									${receiver.isDefault?string(message("member.common.true"), message("member.common.false"))}
								</td>
								<td>
									<a href="edit?receiverId=${receiver.id}">[${message("member.common.edit")}]</a>
									<a href="javascript:;" class="delete" data-receiver-id="${receiver.id}">[${message("member.common.delete")}]</a>
								</td>
							</tr>
						[/#list]
					</table>
					[#if !page.content?has_content]
						<p>${message("member.common.noResult")}</p>
					[/#if]
				</div>
				[@pagination pageNumber = page.pageNumber totalPages = page.totalPages pattern = "?pageNumber={pageNumber}"]
					[#include "/member/include/pagination.ftl"]
				[/@pagination]
			</div>
		</div>
	</div>
	[#include "/shop/include/footer.ftl" /]
</body>
</html>