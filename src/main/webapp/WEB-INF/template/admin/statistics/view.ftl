<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html;charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("admin.statistics.view")} - Powered By SHOP++</title>
<meta name="author" content="SHOP++ Team" />
<meta name="copyright" content="SHOP++" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/input.js"></script>
<style type="text/css">
*{
	font: 12px tahoma, Arial, Verdana, sans-serif;
}
html, body {
	width: 100%;
	height: 100%;
	overflow: hidden;
}
</style>
<script type="text/javascript">
$().ready(function() {

	[#if !cnzzSiteId?has_content || !cnzzPassword?has_content]
		$.message("warn", "${message("admin.statistics.disabled")}");
	[/#if]

});
</script>
</head>
<body>
	<div class="breadcrumb">
		${message("admin.statistics.view")}
	</div>
	[#if cnzzSiteId?has_content && cnzzPassword?has_content]
		<iframe frameborder="0" width="100%" height="100%" src="http://intf.cnzz.com/user/companion/shopxx_login.php?site_id=${cnzzSiteId?url}&password=${cnzzPassword?url}"></iframe>
	[/#if]
</body>
</html>