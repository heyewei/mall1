<div class="span2">
	<div class="info">
		<div class="top"></div>
		<div class="content">
			<p>
				${message("member.navigation.welcome")}
				<strong>${currentUser.username}</strong>
			</p>
			<p>
				${message("member.navigation.memberRank")}:
				<span class="red">${currentUser.memberRank.name}</span>
			</p>
		</div>
		<div class="bottom"></div>
	</div>
	<div class="menu">
		<div class="title">
			<a href="${base}/member/index">${message("member.navigation.title")}</a>
		</div>
		<div class="content">
			<dl>
				<dt>${message("member.navigation.order")}</dt>
				<dd>
					<a href="${base}/member/order/list"[#if current == "orderList"] class="current"[/#if]>${message("member.order.list")}</a>
				</dd>
				<dd>
					<a href="${base}/member/coupon_code/list"[#if current == "couponCodeList"] class="current"[/#if]>${message("member.couponCode.list")}</a>
				</dd>
				<dd>
					<a href="${base}/member/coupon_code/exchange"[#if current == "couponCodeExchange"] class="current"[/#if]>${message("member.couponCode.exchange")}</a>
				</dd>
				<dd>
					<a href="${base}/member/point_log/list"[#if current == "pointLogList"] class="current"[/#if]>${message("member.pointLog.list")}</a>
				</dd>
			</dl>
			<dl>
				<dt>${message("member.navigation.productFavorite")}</dt>
				<dd>
					<a href="${base}/member/product_favorite/list"[#if current == "productFavoriteList"] class="current"[/#if]>${message("member.productFavorite.list")}</a>
				</dd>
				<dd>
					<a href="${base}/member/product_notify/list"[#if current == "productNotifyList"] class="current"[/#if]>${message("member.productNotify.list")}</a>
				</dd>
				<dd>
					<a href="${base}/member/review/list"[#if current == "reviewList"] class="current"[/#if]>${message("member.review.list")}</a>
				</dd>
				<dd>
					<a href="${base}/member/consultation/list"[#if current == "consultationList"] class="current"[/#if]>${message("member.consultation.list")}</a>
				</dd>
			</dl>
			<dl>
				<dt>${message("member.navigation.message")}</dt>
				<dd>
					<a href="${base}/member/message/send"[#if current == "messageSend"] class="current"[/#if]>${message("member.message.send")}</a>
				</dd>
				<dd>
					<a href="${base}/member/message/list"[#if current == "messageList"] class="current"[/#if]>${message("member.message.list")}</a>
				</dd>
				<dd>
					<a href="${base}/member/message/draft"[#if current == "messageDraft"] class="current"[/#if]>${message("member.message.draft")}</a>
				</dd>
			</dl>
			<dl>
				<dt>${message("member.navigation.profile")}</dt>
				<dd>
					<a href="${base}/member/profile/edit"[#if current == "profileEdit"] class="current"[/#if]>${message("member.profile.edit")}</a>
				</dd>
				<dd>
					<a href="${base}/member/password/edit"[#if current == "passwordEdit"] class="current"[/#if]>${message("member.password.edit")}</a>
				</dd>
				<dd>
					<a href="${base}/member/receiver/list"[#if current == "receiverList"] class="current"[/#if]>${message("member.receiver.list")}</a>
				</dd>
				<dd>
					<a href="${base}/member/social_user/list"[#if current == "socialUserList"] class="current"[/#if]>${message("member.socialUser.list")}</a>
				</dd>
			</dl>
			<dl>
				<dt>${message("member.navigation.deposit")}</dt>
				<dd>
					<a href="${base}/member/deposit/recharge"[#if current == "depositRecharge"] class="current"[/#if]>${message("member.deposit.recharge")}</a>
				</dd>
				<dd>
					<a href="${base}/member/deposit/log"[#if current == "depositLog"] class="current"[/#if]>${message("member.deposit.log")}</a>
				</dd>
			</dl>
		</div>
		<div class="bottom"></div>
	</div>
</div>