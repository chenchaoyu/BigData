

$(document).ready(function ()
{

    //要执行的js代码段
    console.log("我在这-2")
    var Url = "http://127.0.0.1:8080/databases";
    $.ajax({
        url: Url,
        type: "post",
        dataType: "json",
        crossDomain: true,
        async: false,
        success: function (data) {
            databases(data)
        }
    });
    


});


function databases(result){

        var html="";
        for(var i=0;i<result.length;i++){    //遍历result数组
            var db_name = result[i];
            html +="<li id='"+db_name+"'><a class=\"nav-link\" href=\"#\">"+db_name+"</a></li>";
            
        }
        
        $("#chushihua").html(html); //在html页面id=chushihua的标签里显示html内容
        listenDbOnClick(result)
}

/**
 * 为每个数据库名注册点击函数
 * @param {*}
 */

function listenDbOnClick(){
        //console.log("listenOnClickFunction")
        var list=document.getElementById("chushihua").children
        for(var i=0;i<list.length;i++){
            
            list[i].setAttribute('flag',0)
            list[i].onclick=function(){
                var db_name=this.id
                console.log("点击了"+db_name)
                //alert("点击了"+db_name)

                //若是未展开状态则展开
                if(this.getAttribute('flag')==0){
                    //请求改数据库下的表名列表
                    var Url="http://127.0.0.1:8080/tables"
                    console.log(db_name)
                    $.ajax({
                        url: Url,
                        type: "post",
                        data: {
                            database: db_name
                        },
                        dataType: "json",
                        crossDomain: true,
                        async: false,
                        success: function (data) {
                            //alert(data)
                            loadTable(db_name,data)
                            }
                    })
                    this.setAttribute("flag",1)
                }//若是已展开状态则关闭
                else{
                    var string="#"+db_name
                    $(string).html("<a class=\"nav-link\" href=\"#\">"+db_name+"</a>")
                    this.setAttribute("flag",0)
                }
                
               
            }
        }
        
}




/**
 * 为某个数据库下的表名注册点击函数
 * @param {*} db_name 
 */
function listenTableOnClick(db_name){
    var list=document.getElementById(db_name).children
    //console.log(list)
    for(var i=1;i<list.length;i++){
        //console.log(list[i].id)
        list[i].onclick=function(e){
            e.stopPropagation()
            var db_name=this.getAttribute('db')
            var table_name=this.getAttribute('tb')
            //console.log(db_name)
            //console.log(table_name)
            useDataBase(db_name)
            var sql="select * from "+table_name
            query(sql)
        }
    }

}


/**
 * 将表名列表显示出来
 * @param {*} db_name 数据库名
 * @param {*} tableList 数据库下表名列表
 */
function loadTable(db_name,tableList){
    console.log("loadTableFunction")
    var dblist=document.getElementById("chushihua").children
    for(var i=0;i<dblist.length;i++){
        
        if(dblist[i].id==db_name){
            //console.log(id)
            var html="";
            html+="<a class=\"nav-link\" id='"+"a"+db_name+"' href=\"#\">"+db_name+"</a>";
           /*  list[i].html(html);
            var a=document.getElementById("a"+db_name)
            var insideHtml=""; */
            for(var j=0;j<tableList.length;j++){    //遍历result数组
                var table_name = tableList[j];
                html +="<li style='margin-left: 20px' db='"+db_name+"' tb='"+table_name+"'><a class=\"nav-link\" href=\"#\">"+table_name+"</a></li>";
                
            }
            var string="#"+db_name
            console.log(string)
            $(string).html(html)
           /*  list[i].html(html); */ //在html页面id=chushihua的标签里显示html内容
           console.log(db_name)      
           break;
        }    
    }
    listenTableOnClick(db_name)
    
}


$(".query").click(function () {
    var script = $("#sql").val();
    script = script.replace(";", "");
    query(script)
});

/**
 * use 数据库语句
 * @param {*} db_name 
 */
function useDataBase(db_name){
    var sql="use "+db_name
    console.log(sql)
    $.ajax({
        url: "http://127.0.0.1:8080/query",
        type: "POST",
        dataType: "json",
        data: {
            sql: sql
        },
        async: true,
        beforeSend: function () {
            var msg = ["use database"+db_name];
            display(msg)
        },
        success: function (result) {
            console.log(result.length)
        }, error: function (XMLHttpRequest, textStatus, errorThrown) {
            // 状态码
            console.log(XMLHttpRequest.status);
            // 状态
            console.log(XMLHttpRequest.readyState);
            // 错误信息
            console.log(textStatus);
            var msg = [
                "Error: " + XMLHttpRequest.status
                + " " + XMLHttpRequest.readyState + " " + textStatus
            ];
            display(msg)
        }
    })
}

function query(sql){
    console.log(sql)
    $.ajax({
        url: "http://127.0.0.1:8080/query",
        type: "POST",
        dataType: "json",
        data: {
            sql: sql
        },
        async: true,
        beforeSend: function () {
            var msg = ["加载中......"];
            display(msg)
        },
        success: function (result) {
            console.log(result.length)
            display(result)
        }, error: function (XMLHttpRequest, textStatus, errorThrown) {
            // 状态码
            console.log(XMLHttpRequest.status);
            // 状态
            console.log(XMLHttpRequest.readyState);
            // 错误信息
            console.log(textStatus);
            var msg = [
                "Error: " + XMLHttpRequest.status
                + " " + XMLHttpRequest.readyState + " " + textStatus
            ];
            display(msg)
        }
    })
}

function display(array) {
    
    var str = "";
    var i = 0, j = 0;
    str += "<table id='results' border='solid'>";
    if (array.length === 1) {
        str += "<b style='color: red'>";
        for (i = 0, len = array.length; i < len; i++) {
            str += array[i] + " "
        }
        str += "</b>"
    } else {
        var head = true;
        for (i = 0, n_rows = array.length; i < n_rows; i++) {
            str += "<tr>";
            for (j = 0, n_cols = array[i].length; j < n_cols; j++) {
                if (head) {
                    str += "<th style='text-align: center'>" + array[i][j] + "</th>"
                } else {
                    str += "<td style='text-align: center'>" + array[i][j] + "</td>"
                }
            }
            if (head) {
                head = false
            }
            str += "</tr>"
        }
        str += "</table>"
    }

    document.getElementById("iframewrapper").innerHTML = str
}

