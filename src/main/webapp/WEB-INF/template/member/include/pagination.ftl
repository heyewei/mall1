[#if totalPages > 1]
	<div class="pagination">
		[#if isFirst]
			<span class="firstPage">&nbsp;</span>
		[#else]
			<a href="[@pattern?replace("{pageNumber}", "${firstPageNumber}")?interpret /]" class="firstPage">&nbsp;</a>
		[/#if]
		[#if hasPrevious]
			<a href="[@pattern?replace("{pageNumber}", "${previousPageNumber}")?interpret /]" class="previousPage">&nbsp;</a>
		[#else]
			<span class="previousPage">&nbsp;</span>
		[/#if]
		[#list segment as segmentPageNumber]
			[#if segmentPageNumber_index == 0 && segmentPageNumber > firstPageNumber + 1]
				<span class="pageBreak">...</span>
			[/#if]
			[#if segmentPageNumber != pageNumber]
				<a href="[@pattern?replace("{pageNumber}", "${segmentPageNumber}")?interpret /]">${segmentPageNumber}</a>
			[#else]
				<span class="currentPage">${segmentPageNumber}</span>
			[/#if]
			[#if !segmentPageNumber_has_next && segmentPageNumber < lastPageNumber - 1]
				<span class="pageBreak">...</span>
			[/#if]
		[/#list]
		[#if hasNext]
			<a href="[@pattern?replace("{pageNumber}", "${nextPageNumber}")?interpret /]" class="nextPage">&nbsp;</a>
		[#else]
			<span class="nextPage">&nbsp;</span>
		[/#if]
		[#if isLast]
			<span class="lastPage">&nbsp;</span>
		[#else]
			<a href="[@pattern?replace("{pageNumber}", "${lastPageNumber}")?interpret /]" class="lastPage">&nbsp;</a>
		[/#if]
	</div>
[/#if]