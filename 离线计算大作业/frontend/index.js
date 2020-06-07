const http = require('http');
const url = require('url');
const path = require('path');
const fs = require('fs');

// 以上是系统命令，不需下载

// npm install mime 下载插件，解析文件类型，作用是防止低版本浏览器无法识别文件类型而导致的错误
const mime = require('mime');


// 创建服务器
const app = http.createServer();

// 服务器请求事件
app.on('request', (req, res) => {
        // 获取用户请求路径
        let pathname = url.parse(req.url).pathname;
        pathname = pathname == '/' ? '/login.html' : pathname;
        
        // 将用户请求的路径转换为服务器盘符实际路径 
        //如：c:\test\demo\index.html
        let realpath = path.join(__dirname, pathname);
        console.log(realpath);
        // 解析文件类型
        let type = mime.getType(realpath);
        // 读取文件
        fs.readFile(realpath, (error, result) => {
            if (error != null) {
                res.writeHead(400, {
                    'content-type': 'text/html;charset=utf8'
                })
                res.end('文件读取失败!')
                    // 退出
                return;
            }

            // 设置文件类型
            res.writeHead(200, {
                    'content-type': type
                })
                // 输出
            res.end(result)
        })
    })
    // 设置端口号8000并进行监听


    
app.listen(8000);
console.log("服务器启动成功！");
