$(".loginButton").click(function () {
    //判断checkbox是否勾选
    var isCookie = false;
    if(document.getElementById("isUseCookie").checked){
        isCookie = true;
    }
    if(isCookie==false){
        alert('同意我们的用户及隐私协议才能使用！');
        return;
    }
    var username=document.getElementById("inputEmail3").value
    var password=document.getElementById("inputPassword3").value;
    var sqlUrl=document.getElementById("inputUrl").value;
    var driver=document.getElementById("inputDriver").value;
    
    var connectUrl="http://127.0.0.1:8080/connect";
    alert(connectUrl)
    //单击登录按钮触发ajax事件
    $.ajax({
        url:connectUrl,
        type:"POST",
        data:{
        url:sqlUrl,
        user:username,
        password:password,
        driver:driver
        },
        dataType:"json",
        crossDomain: true, 
        success:function(result) {
            if(result=="1"){
                alert("返回结果"+result);
                
                window.location="./././index.html";
                var test=document.getElementById()
            }
            if(result == "0"){
                
                alert("用户名或密码错误，请重新输入");
                window.location="./././index.html";
            }
        },error: function(XMLHttpRequest,textStatus,errorThrown){
            // 状态码
            console.log(XMLHttpRequest.status);
            // 状态
            console.log(XMLHttpRequest.readyState);
            // 错误信息
            console.log(textStatus);
            __hideLoading();
            
            if(XMLHttpRequest.readyState==0){
                // 对应登录超时问题，直接跳到登录页面
                location.href='../Login.action';
            }else{
                $.messager.alert('提示','系统内部错误，请联系管理员处理！','info');
            }
 

        }
    });
});