<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html;charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("member.index.title")}[#if showPowered] - Powered By SHOP++[/#if]</title>
<meta name="author" content="SHOP++ Team" />
<meta name="copyright" content="SHOP++" />
<link href="${base}/resources/member/css/animate.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/member/css/common.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/member/css/member.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/member/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/member/js/common.js"></script>
</head>
<body>
	[#include "/shop/include/header.ftl" /]
	<div class="container member">
		<div class="row">
			[#include "/member/include/navigation.ftl" /]
			<div class="span10">
				<div class="index">
					<div class="top clearfix">
						<div>
							<ul>
								<li>
									${message("member.index.memberRank")}: ${currentUser.memberRank.name}
								</li>
								<li>
									${message("member.index.balance")}:
									<strong>${currency(currentUser.balance, true, true)}</strong>
								</li>
								<li>
									${message("member.index.amount")}:
									<strong>${currency(currentUser.amount, true, true)}</strong>
								</li>
								<li>
									${message("member.index.point")}:
									<em>${currentUser.point}</em>
									<a href="coupon_code/exchange" class="silver">${message("member.index.exchange")}</a>
								</li>
							</ul>
							<ul>
								<li>
									<a href="order/list?status=pendingPayment&hasExpired=false">${message("member.index.pendingPaymentOrderCount")}(<em>${pendingPaymentOrderCount}</em>)</a>
									<a href="order/list?status=pendingShipment&hasExpired=false">${message("member.index.pendingShipmentOrderCount")}(<em>${pendingShipmentOrderCount}</em>)</a>
								</li>
								<li>
									<a href="message/list">${message("member.index.messageCount")}(<em>${messageCount}</em>)</a>
									<a href="coupon_code/list">${message("member.index.couponCodeCount")}(<em>${couponCodeCount}</em>)</a>
								</li>
								<li>
									<a href="product_favorite/list">${message("member.index.productFavoriteCount")}(<em>${productFavoriteCount}</em>)</a>
									<a href="product_notify/list">${message("member.index.productNotifyCount")}(<em>${productNotifyCount}</em>)</a>
								</li>
								<li>
									<a href="review/list">${message("member.index.reviewCount")}(<em>${reviewCount}</em>)</a>
									<a href="consultation/list">${message("member.index.consultationCount")}(<em>${consultationCount}</em>)</a>
								</li>
							</ul>
						</div>
					</div>
					<div class="list">
						<div class="title">${message("member.index.newOrder")}</div>
						<table class="list">
							<tr>
								<th>
									${message("Order.sn")}
								</th>
								<th>
									${message("member.orderItem.sku")}
								</th>
								<th>
									${message("Order.consignee")}
								</th>
								<th>
									${message("Order.amount")}
								</th>
								<th>
									${message("Order.status")}
								</th>
								<th>
									${message("shop.common.createdDate")}
								</th>
								<th>
									${message("member.common.action")}
								</th>
							</tr>
							[#list newOrders as order]
								<tr[#if !order_has_next] class="last"[/#if]>
									<td>
										${order.sn}
									</td>
									<td>
										[#list order.orderItems as orderItem]
											<img src="${orderItem.thumbnail!setting.defaultThumbnailProductImage}" class="thumbnail" alt="${orderItem.name}" />
											[#if orderItem_index == 2]
												[#break /]
											[/#if]
										[/#list]
									</td>
									<td>
										${order.consignee!"-"}
									</td>
									<td>
										${currency(order.amount, true)}
									</td>
									<td>
										[#if order.hasExpired()]
											${message("member.order.hasExpired")}
										[#else]
											${message("Order.Status." + order.status)}
										[/#if]
									</td>
									<td>
										<span title="${order.createdDate?string("yyyy-MM-dd HH:mm:ss")}">${order.createdDate}</span>
									</td>
									<td>
										<a href="order/view?orderSn=${order.sn}">[${message("member.common.view")}]</a>
									</td>
								</tr>
							[/#list]
						</table>
						[#if !newOrders?has_content]
							<p>${message("member.common.noResult")}</p>
						[/#if]
					</div>
				</div>
			</div>
		</div>
		[#include "/shop/include/footer.ftl" /]
	</div>
</body>
</html>