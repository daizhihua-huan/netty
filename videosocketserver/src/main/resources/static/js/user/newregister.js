/**
 * @author CHR
 * @version 1.0
 *
 */
$(document).ready(function () {


    //$("#loginsubmit").click(sendData());

    /**
     *验证码切换方法
     */
    function codechange() {

        //$("img").attr("src","/xxx/xxxServlet" + "?" + Math.random());
        var date = new Date().getTime();

        //  $("#img").src = "/code?date=" + date;
        $("#img").attr("src", "/code?date=" + date);


    }

    /**
     * 异步验证登陆方法
     */
    function sendData() {


        var username = $("#inputEmail3").val();
        var password = $("#inputPassword3").val();
        var checkcode = $("#yanzheng").val();

        $.get("register", {"username": username, "password": password, "code": checkcode}, userDatabcak);

        function userDatabcak(data) {
            var code = data.code;

            switch (code) {
                case 200:
                    window.location.href = "http://localhost:10003/toLogin";
                    break;
                case 201:
                    $("#usermsg").html("用户名不能为空");
                    break;
                case 202:
                    $("#passwordmsg").html("密码不能为空");
                    break;
                case 203:
                    $("#codemsg").html("验证码不能为空");
                    break;
                case 204:
                    $("#codemsg").html("注册失败");
                    codechange();
                    break;
                case 205:
                    $("#codemsg").html("验证码错误");
                    codechange();
                    break;

            }

        }

    }

    // function good(){
    //     //配置一个透明的询问框
    //     layer.msg('大部分参数都是可以公用的<br>合理搭配，展示不一样的风格', {
    //         time: 20000, //20s后自动关闭
    //         btn: ['明白了', '知道了', '哦']
    //     });
    // }


    /**
     *用户验证方法
     * @returns {boolean}
     *
     */
    function cheakuser() {

        var username = $("#inputEmail3").val();

        if (username == null || username == "") {

            $("#usermsg").html("用户名不能为空");
            $("#inputEmail3").css("background-color", "#f0f0f0");
            return false;

        }
        if (!(username == null) || !(username == "")) {
            var flag=false;
            $.get("checkUser",{"username":username},function (data) {

                var usercode=data.code;

                if(usercode==206){
                    $("#usermsg").html(null);
                    $("#inputEmail3").css("background-color", "#AFEEEE");
                   flag=true;
                }
                if(usercode==207){
                    $("#usermsg").html("用户名已存在");
                   flag=false;
                }

            })


                 return flag;

        }

    }

    /**
     * 密码验证方法
     * @returns {boolean}
     */
    function checkpassword() {
        var password = $("#inputPassword3").val();
        if (password == null || password == "") {
            $("#passwordmsg").html("密码不能为空");
            $("#inputPassword3").css("background-color", "#f0f0f0");
            return false;
        }


        if (!(password == null) || !(password == "")) {

            $("#passwordmsg").html(null);
            $("#inputPassword3").css("background-color", "#AFEEEE");
            return true;
        }

    }

    /**
     * 密码再次验证
     * @returns {boolean}
     */
    function checkpasswordagain() {


        var password = $("#inputPassword3again").val();

        if (password == null || password == "") {
            $("#passwordmsgagain").html("请再次输入密码");
            $("#inputPassword3again").css("background-color", "#f0f0f0");
            return false;
        }


        if (!(password == null) || !(password == "")) {

            $("#passwordmsgagain").html(null);
            $("#inputPassword3again").css("background-color", "#AFEEEE");

            var password1 = $("#inputPassword3").val();
            if (password != password1) {
                $("#passwordmsgagain").html("两次密码输入不一致");
                return false;
            }
            if (password == password1 && (password != null || password != "") && (password1 != null || password1 != "")) {
                $("#passwordmsgagain").html("");
                return true;
            }

            return true;
        }

    }




    /**
     * 验证码验证方法
     * @returns {boolean}
     */
    function checkcode() {

        var code = $("#yanzheng").val();

        if (code == null || code == "") {

            $("#codemsg").html("验证码不能为空");
            $("#yanzheng").css("background-color", "#f0f0f0");
            return false;

        }

        if (!(code == null) || !(code == "")) {
            $("#codemsg").html(null);
            $("#yanzheng").css("background-color", "#AFEEEE");
            return true;

        }

    }


    /**
     * 登陆验证方法
     */
    function checklogin() {
        //     alert(cheakuser())
        // alert(checkpassword())
        // alert(checkcode())
        // alert(checkpasswordagain())
        if (cheakuser() && checkpassword() && checkcode() && checkpasswordagain()) {
            alert("aaa")
            sendData();
        }
        // $("#loginsubmit").click(sendData);
    }

    /**
     * 跳转登录方法
     */
    function tologin(){
        window.location.href = "http://localhost:10003/toLogin";

    }

    $("#inputEmail3").blur(cheakuser);
    $("#inputPassword3").blur(checkpassword);
    $("#inputPassword3again").blur(checkpasswordagain);

    $("#yanzheng").blur(checkcode);
    $("#loginsubmit").click(checklogin);
    $("#img").click(codechange);
    $("#gologin").click(tologin)



});


