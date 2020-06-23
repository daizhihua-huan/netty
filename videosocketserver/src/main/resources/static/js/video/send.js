$(document).ready(function () {

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
        var startTime = $("#startTime");
        var endTime = $("#endTime");
        var number = $("#number");
        if (cheak(startTime, "请填写开始时间", "startmsg")
            && cheak(endTime, "请填写结束时间", "endmsg")
            && cheak(number, "请填写设备编号", "numbermsg")) {

            send();
        }

    }

    /**
     * 提交方法
     */
    function send() {


        var startTime = $("#startTime").val();
        var endTime = $("#endTime").val();
        var number = $("#number").val();


        var datestart = getDate(startTime);

        var datestart1 = new Date(datestart);

        var rightStartTime = datestart1.Format("yyyyMMdd hhmmss");

        var dateend = getDate(endTime);
        var dateend1 = new Date(dateend);
        var rightEndTime = dateend1.Format("yyyyMMdd hhmmss");


        $.get("/send", {"startTime": rightStartTime, "endTime": rightEndTime, "number": number}, function (data) {
            var videocode = data.code;

            switch (videocode) {

                case 200:
                    var date = new Date().getTime();
                    $("#video").attr("src", "/getVideos?number=" + number + "&date=" + date);
                    $("#erroemsg").text("")

                    break;
                case 201:
                    $("#video").attr("src", "");
                    $("#erroemsg").text("请输入设备编号");

                    break;
                case 202:
                    $("#video").attr("src", "");
                    $("#erroemsg").text("日期传入格式错误")
                    break;
                case 203:
                    $("#video").attr("src", "");
                    $("#erroemsg").text("服务器断开连接")
                    break;
                case 204:
                    $("#video").attr("src", "");
                    $("#erroemsg").text("开始时间大于结束时间")
                    break;
            }

        });


    }

    /**
     * 字符串转日期
     * @param strDate
     * @returns {any}
     */
    function getDate(strDate) {
        var date = eval('new Date(' + strDate.replace(/\d+(?=-[^-]+$)/,
            function (a) {
                return parseInt(a, 10) - 1;
            }).match(/\d+/g) + ')');
        return date;
    }

    /**
     * 日期转字符串
     * @param fmt
     * @returns {*}
     * @constructor
     */
    Date.prototype.Format = function (fmt) { //author: meizz

        var o = {

            "M+": this.getMonth() + 1, //月份

            "d+": this.getDate(), //日

            "h+": this.getHours(), //小时

            "m+": this.getMinutes(), //分

            "s+": this.getSeconds(), //秒

            "q+": Math.floor((this.getMonth() + 3) / 3), //季度

            "S": this.getMilliseconds() //毫秒

        };

        if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));

        for (var k in o)

            if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));

        return fmt;

    }


    $("#submit").click(sub);


});

layui.use(['laydate', 'layer'], function () {


    var laydate = layui.laydate
        , $ = layui.$;
    $(document).on('click', "#errorTest", errorTest);


    function errorTest() {


            layer.msg('正在检测......', {

                time: 2000 //2秒关闭（如果不配置，默认是3秒）
            });

        var flag = false;

        setInterval(function () {


            if (flag) {

                layer.msg('设备正常', {

                    time: 5000 //2秒关闭（如果不配置，默认是3秒）
                });
                flag = !flag;
                setTimeout(function () {

                }, 5000);
            } else {
                layer.msg('设备异常', {

                    time: 5000 //2秒关闭（如果不配置，默认是3秒）
                });

                flag = !flag;
                setTimeout(function () {

                }, 5000);
            }


        }, 1000);


    }


    laydate.render({
        elem: '#startTime'
        , type: 'datetime'
    });

    laydate.render({
        elem: '#endTime'
        , type: 'datetime'
    });

});
