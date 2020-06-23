/**
 * @author CHR
 * @version 1.0
 *
 */
$(document).ready(function () {
    $(document).keydown(function () {

        var e= event;

        if(e.keyCode==13||e.keyCode==32){

            checklogin();

        }

    })

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

        $.post("/login", {"username": username, "password": password, "code": checkcode}, userDatabcak);

        function userDatabcak(data) {
            var code = data.code;
            // alert(code)
            switch (code) {
                case 200:
                    window.location.href = "http://localhost:10003/toSuccess";
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
                    $("#codemsg").html("用户名或者密码错误");
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

            $("#usermsg").html(null);
            $("#inputEmail3").css("background-color", "#AFEEEE");
            return true;

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

        if (cheakuser() && checkpassword() && checkcode()) {

            sendData();
        }
        // $("#loginsubmit").click(sendData);
    }

    /**
     * 跳转注册
     *
     */
    function toreg(){

        window.location.href = "http://localhost:10003/toRegister";
    }



    $("#toreg").click(toreg);
    $("#inputEmail3").blur(cheakuser);
    $("#inputPassword3").blur(checkpassword);
    $("#yanzheng").blur(checkcode);
    $("#loginsubmit").click(checklogin);

    $("#img").click(codechange);



});


