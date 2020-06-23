$(document).ready(function () {

    $(document).keydown(function () {
        var e= event;

        if(e.keyCode==13||e.keyCode==32){

           send();

        }

    })
    /**
     * 检测是否为空的方法
     * @param element
     * @param mag
     * @param magname
     * @returns {boolean}
     */
    function cheak(element, mag, magname) {
        var username = element.val();
        if (username == null || username == "") {

            $("#" + magname + "").html(mag);
            element.css("background-color", "#f0f0f0");
            return false;

        }
        if (!(username == null) || !(username == "")) {

            $("#" + magname + "").html(null);
            element.css("background-color", "#AFEEEE");
            return true;

        }

    }

    /**
     * 检测
     */
    function sub() {

        var number = $("#number");
        if (cheak(number, "请填设备编号", "numbermsg")) {
            send();
        }

    }

    /**
     * 提交方法
     */
    function send() {

        var number = $("#number").val();

        $.get("/start", {"number": number,"type":1}, function (data) {

            alert(data.code);
        })


    }


    $("#submit").click(sub);


});
