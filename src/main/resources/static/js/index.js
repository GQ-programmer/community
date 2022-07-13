$(function(){
	$("#publishBtn").click(publish);

});


// 点击发布按钮会执行该方法
function publish() {
	// 隐藏文本框
	$("#publishModal").modal("hide");

	// 获取标题和内容
	var title = $("#recipient-name").val();
	var content = $("#message-text").val();
	// 发送异步请求Post
	$.post(
		CONTEXT_PATH + "/discuss/add",
		{"title":title,"content":content},
		function (data) {
			data = $.parseJSON(data);

			//在提示框显示返回信息
			$("#hintBody").text(data.msg);

			// 显示提示框
			$("#hintModal").modal("show");

			// 2秒后消失
			setTimeout(function(){
				$("#hintModal").modal("hide");
				if (data.code == 0) {
					window.location.reload();
				}

			}, 2000);
		}
	);



}