function initPageData(currentPage,totalPages) {
    var element = $('#pageButton');
    var options = {
        bootstrapMajorVersion : 3,
        currentPage : currentPage, // 当前页数
        numberOfPages : totalPages, // 显示按钮的数量
        totalPages : totalPages, // 总页数
        itemTexts : function(type, page, current) {

            switch (type) {
                case "first":
                    return "首页";
                case "prev":
                    return "上一页";
                case "next":
                    return "下一页";
                case "last":
                    return "末页";
                case "page":
                    return page;
            }
        },
        // 点击事件，用于通过Ajax来刷新整个list列表
        onPageClicked : function(event, originalEvent, type, page) {
            searchList(page);
        }
    };

    element.bootstrapPaginator(options);
}
