<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
	<meta name="format-detection" content="telephone=no">
	<meta name="author" content="SHOP++ Team">
	<meta name="copyright" content="SHOP++">
	<title>${message("member.index.title")}[#if showPowered] - Powered By SHOP++[/#if]</title>
	<link href="${base}/favicon.ico" rel="icon">
	<link href="${base}/resources/mobile/member/css/bootstrap.css" rel="stylesheet">
	<link href="${base}/resources/mobile/member/css/font-awesome.css" rel="stylesheet">
	<link href="${base}/resources/mobile/member/css/animate.css" rel="stylesheet">
	<link href="${base}/resources/mobile/member/css/common.css" rel="stylesheet">
	<link href="${base}/resources/mobile/member/css/profile.css" rel="stylesheet">
	<!--[if lt IE 9]>
		<script src="${base}/resources/mobile/member/js/html5shiv.js"></script>
		<script src="${base}/resources/mobile/member/js/respond.js"></script>
	<![endif]-->
	<script src="${base}/resources/mobile/member/js/jquery.js"></script>
	<script src="${base}/resources/mobile/member/js/bootstrap.js"></script>
	<script src="${base}/resources/mobile/member/js/underscore.js"></script>
	<script src="${base}/resources/mobile/member/js/common.js"></script>
</head>
<body class="profile">
	<header class="header-fixed">
		<a class="pull-left" href="javascript: history.back();">
			<span class="glyphicon glyphicon-menu-left"></span>
		</a>
		${message("member.index.title")}
	</header>
	<main>
		<div class="container-fluid">
			<div class="panel panel-flat">
				<div class="panel-heading">
					${message("member.navigation.welcome")}
					<span class="red">${currentUser.username}</span>
					<span class="pull-right">
						${message("member.navigation.memberRank")}:
						<span class="red">${currentUser.memberRank.name}</span>
					</span>
				</div>
				<div class="panel-body small">
					<div class="row">
						<div class="col-xs-3 text-center">
							<a class="icon" href="product_favorite/list">
								<span class="fa fa-heart-o gray"></span>
								<span class="badge">${productFavoriteCount}</span>
								${message("member.index.productFavoriteCount")}
							</a>
						</div>
						<div class="col-xs-3 text-center">
							<a class="icon" href="product_notify/list">
								<span class="fa fa-envelope-o gray"></span>
								<span class="badge">${productNotifyCount}</span>
								${message("member.index.productNotifyCount")}
							</a>
						</div>
						<div class="col-xs-3 text-center">
							<a class="icon" href="review/list">
								<span class="fa fa-comment-o gray"></span>
								<span class="badge">${reviewCount}</span>
								${message("member.index.reviewCount")}
							</a>
						</div>
						<div class="col-xs-3 text-center">
							<a class="icon" href="consultation/list">
								<span class="fa fa-question-circle-o gray"></span>
								<span class="badge">${consultationCount}</span>
								${message("member.index.consultationCount")}
							</a>
						</div>
					</div>
				</div>
			</div>
			<div class="panel panel-flat">
				<div class="panel-heading">
					${message("member.order.list")}
					<a class="pull-right gray-darker" href="order/list">
						${message("member.order.viewAll")}
						<span class="glyphicon glyphicon-menu-right"></span>
					</a>
				</div>
				<div class="panel-body small">
					<div class="row">
						<div class="col-xs-4 text-center">
							<a class="icon" href="order/list?status=pendingPayment&hasExpired=false">
								<span class="fa fa-credit-card gray"></span>
								<span class="badge">${pendingPaymentOrderCount}</span>
								${message("member.index.pendingPaymentOrderCount")}
							</a>
						</div>
						<div class="col-xs-4 text-center">
							<a class="icon" href="order/list?status=pendingShipment&hasExpired=false">
								<span class="fa fa-calendar-minus-o gray"></span>
								<span class="badge">${pendingShipmentOrderCount}</span>
								${message("member.index.pendingShipmentOrderCount")}
							</a>
						</div>
						<div class="col-xs-4 text-center">
							<a class="icon" href="order/list?status=shipped&hasExpired=false">
								<span class="fa fa-truck gray"></span>
								<span class="badge">${shippedOrderCount}</span>
								${message("member.index.shippedOrderCount")}
							</a>
						</div>
					</div>
				</div>
			</div>
			<div class="panel panel-flat">
				<div class="panel-heading">${message("member.message.messageBox")}</div>
				<div class="panel-body small">
					<div class="row">
						<div class="col-xs-4 text-center">
							<a class="icon" href="message/list">
								<span class="fa fa-commenting blue"></span>
								${message("member.message.list")}
							</a>
						</div>
						<div class="col-xs-4 text-center">
							<a class="icon" href="message/send">
								<span class="fa fa-send-o green"></span>
								${message("member.message.send")}
							</a>
						</div>
						<div class="col-xs-4 text-center">
							<a class="icon" href="message/draft">
								<span class="fa fa-sticky-note-o orange-light"></span>
								${message("member.message.draft")}
							</a>
						</div>
					</div>
				</div>
			</div>
			<div class="panel panel-flat">
				<div class="panel-heading">${message("member.navigation.essentialTools")}</div>
				<div class="panel-body small">
					<div class="list-group list-group-flat">
						<div class="list-group-item">
							<div class="row">
								<div class="col-xs-4 text-center">
									<a class="icon" href="coupon_code/list">
										<span class="fa fa-ticket orange-lighter"></span>
										${message("member.couponCode.list")}
									</a>
								</div>
								<div class="col-xs-4 text-center">
									<a class="icon" href="coupon_code/exchange">
										<span class="fa fa-exchange green-darker"></span>
										${message("member.couponCode.exchange")}
									</a>
								</div>
								<div class="col-xs-4 text-center">
									<a class="icon" href="point_log/list">
										<span class="fa fa-gift purple-lighter"></span>
										${message("member.pointLog.list")}
									</a>
								</div>
							</div>
						</div>
						<div class="list-group-item">
							<div class="row">
								<div class="col-xs-4 text-center">
									<a class="icon" href="deposit/recharge">
										<span class="fa fa-money magenta"></span>
										${message("member.deposit.recharge")}
									</a>
								</div>
								<div class="col-xs-4 text-center">
									<a class="icon" href="deposit/log">
										<span class="fa fa-rmb blue-dark"></span>
										${message("member.deposit.log")}
									</a>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="list-group list-group-flat">
				<div class="list-group-item">
					${message("member.navigation.settings")}
					<a class="pull-right gray-darker" href="receiver/list">
						${message("member.receiver.manage")}
						<span class="glyphicon glyphicon-menu-right"></span>
					</a>
				</div>
			</div>
			<div class="list-group list-group-flat">
				<div class="list-group-item">
					${message("member.profile.edit")}
					<a class="pull-right gray-darker" href="profile/edit">
						${message("member.profile.manage")}
						<span class="glyphicon glyphicon-menu-right"></span>
					</a>
				</div>
			</div>
			<div class="list-group list-group-flat">
				<div class="list-group-item">
					${message("member.password.edit")}
					<a class="pull-right gray-darker" href="password/edit">
						${message("member.password.edit")}
						<span class="glyphicon glyphicon-menu-right"></span>
					</a>
				</div>
			</div>
			<div class="list-group list-group-flat">
				<div class="list-group-item">
					${message("member.socialUser.list")}
					<a class="pull-right gray-darker" href="${base}/member/social_user/list">
						${message("member.socialUser.list")}
						<span class="glyphicon glyphicon-menu-right"></span>
					</a>
				</div>
			</div>
			<div class="list-group list-group-flat">
				<div class="list-group-item">
					<a class="btn btn-lg btn-primary btn-flat btn-block" href="${base}/member/logout">${message("member.index.logout")}</a>
				</div>
			</div>
		</div>
	</main>
	<footer class="footer-fixed">
		<div class="container-fluid">
			<div class="row">
				<div class="col-xs-3 text-center">
					<a href="${base}/">
						<span class="glyphicon glyphicon-home"></span>
						${message("member.index.index")}
					</a>
				</div>
				<div class="col-xs-3 text-center">
					<a href="${base}/product_category">
						<span class="glyphicon glyphicon-th-list"></span>
						${message("member.index.productCategory")}
					</a>
				</div>
				<div class="col-xs-3 text-center">
					<a href="${base}/cart/list">
						<span class="glyphicon glyphicon-shopping-cart"></span>
						${message("member.index.cart")}
					</a>
				</div>
				<div class="col-xs-3 text-center active">
					<a href="${base}/member/index">
						<span class="glyphicon glyphicon-user"></span>
						${message("member.index.member")}
					</a>
				</div>
			</div>
		</div>
	</footer>
</body>
</html>