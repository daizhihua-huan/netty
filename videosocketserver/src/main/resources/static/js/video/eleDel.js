layui.extend({
    admin: '{/}../js/menu/admin'
});

$(function () {
    toPage(1);
})
var sum = 0;

/**
 * 切换页面方法
 * @param curr
 */
function toPage(curr) {

    $.get("/getClientForPage?PageNumber=" + curr, {"data": new Date().getTime()}, function (data) {
        var lis = "";
        var flag = false;
        for (var i = 0; i < (data.data).length; i++) {
            var item = sessionStorage.getItem((data.data)[i]);
            if (item == null) {
                item = "";
            }


            var checkde = "";
            if (item == "开启成功") {
                strarr.push((data.data)[i]);

                findlist();
                $("#allstart").prop("style", "display: none;");
                $("#allclose").prop("style", "display: block;");
                $("#onlinelook").prop("style", "display: block;");


                checkde += "            <td>\n" +
                    "                 <input type=\"checkbox\" class=\"videoche\" value=\"" + (data.data)[i] + "\" disabled=\"false\" checked=\"checked\" >\n" +
                    "            </td>\n"
                flag = true;

            } else if (item == "关闭成功") {
                $("#allstart").prop("style", "display: block;");
                $("#allclose").prop("style", "display: none;");
                $("#onlinelook").prop("style", "display: none;");

                checkde += "            <td>\n" +
                    "                 <input type=\"checkbox\" class=\"videoche\" value=\"" + (data.data)[i] + "\" >\n" +
                    "            </td>\n"

            } else {


                checkde += "            <td>\n" +
                    "                 <input type=\"checkbox\" class=\"videoche\" value=\"" + (data.data)[i] + "\" >\n" +
                    "            </td>\n"


            }

            var txt = " <tr >\n" +
                checkde +
                "            <td>" + (data.data)[i] + "</td>\n" +
                "            <td>" + (data.data)[i] + "</td>\n" +
                "            <td></td>\n" +
                "            <td></td>\n" +
                "            <td class=\"td-status\">\n" +
                "                <span class=\" layui-btn-xs msg\" style=\"color: red\" value=\"" + (data.data)[i] + "\">" + item + "</span></td>\n" +
                "        </tr>"

            lis += txt;
            sum = data.msg;

        }

        if (!flag) {
            $("#allstart").prop("style", "display: block;");
            $("#allclose").prop("style", "display: none;");
            $("#onlinelook").prop("style", "display: none;");

        }
        $("#tbodys").html(lis);

        $("#sum").text(sum);


    });


}

/**
 * 查找有无开启方法
 *
 */
function findlist() {

    $.get("/getUrlVideoList", {"data": new Date().getTime()}, function (data) {
        for (var key in data.data) {
            if (key == "rows") {

                if (data.data[key] == null || data.data[key] == "") {


                    $(".msg").html("");
                    $(".videoche").prop("checked", null);
                    $(".videoche").prop("disabled", null);
                    $("#allstart").prop("style", "display: block;");
                    $("#allclose").prop("style", "display: none;");
                    $("#onlinelook").prop("style", "display: none;");

                }

                if (data.data[key][0] != "") {

                    for (var key2 in data.data[key][0]) {
                        if (key2 == "path") {

                            var num = data.data[key][0][key2];
                            substrs = num.substr(1);

                        }
                        if (key2 == "source") {

                            source = data.data[key][0][key2];
                        }

                        map[substrs] = source;

                    }

                }


            }

        }


        if (map != null) {


            for (var k in map) {


                var arr = $(".videoche");
                var flag = false;
                for (var i = 0; i < arr.length; i++) {

                    if (arr[i].checked) {

                        if (k == arr[i].value) {

                            var check = $("input[value=" + k + "]");


                            for (var i = 0; i < check.length; i++) {

                                check[i].setAttribute("disabled", "false");
                                check[i].setAttribute("checked", "checked");
                                var span = $("span[value=" + k + "]")

                                sessionStorage.setItem(k + "start", key);
                                $(span).html("开启成功");
                            }


                            $("#onlinelook").prop("style", "display: block;");
                            $("#allstart").prop("style", "display: none;");
                            $("#allclose").prop("style", "display: block;");
                            flag = true;
                        }


                    }

                }

                /*  if(!flag){

                      var arr = $(".videoche");
                      for (var i = 0; i < arr.length; i++) {

                          if (arr[i].checked) {
                              arr[i].removeAttr("disabled")
                              arr[i].setAttribute("checked", "null");
                          }
                      }
                      $("#onlinelook").prop("style","display: none;");
                      $("#allstart").prop("style","display: block;");
                      $("#allclose").prop("style","display: none;");

                  }*/

            }


        }


    });


}

var map = {};
var substrs = "";
var source = "";
var strarr = new Array();

layui.use(['laydate', 'jquery', 'admin', 'laypage'], function () {


    var laydate = layui.laydate,
        $ = layui.jquery,
        admin = layui.admin,
        laypage = layui.laypage;


    findlist();


    laypage.render({
        elem: 'page'
        , count: sum
        , limit: 4
        , groups: 5
        , prev: "<<"
        , next: ">>"
        , first: "首页"
        , last: "尾页"

        , jump: function (obj, first) {

            if (!first) {
                toPage(obj.curr)
            }
        }

    });


    /**
     * 多选开启
     */


    function allstart() {
        strarr = [];
        var arr = $(".videoche");
        var numarr = new Array();
        for (var i = 0; i < arr.length; i++) {

            if (arr[i].checked) {

                var parents = arr[i].parentNode.parentNode;

                var childNodes = parents.childNodes;

                var number = arr[i].value;

                numarr.push(number)

            }
        }
        if (numarr == null) {
            return
        }

        var strify = JSON.stringify(numarr);


        $.get("/allStartController", {"numbers": strify, "type": 1, "data": new Date().getTime()}, function (data) {


            for (var key in data.data) {
                // alert(key)

                var span = $("span[value=" + key + "]")

                $(span).html(data.data[key]);
                sessionStorage.setItem(key, data.data[key]);


                if (data.data[key] == "开启成功") {

                    sessionStorage.setItem(key + "start", key);
                    $("#allstart").css("background", "#6c6c6c");
                    $("#allclose").css("background", "#FF5722");
                    $("#onlinelook").css("background", "#FF5722");
                    $("#onlinelook").prop("style", "display: block;");
                    $("#allstart").prop("style", "display: none;");
                    $("#allclose").prop("style", "display: block;");
                    var check = $("input[value=" + key + "]");


                    for (var i = 0; i < check.length; i++) {

                        check[i].setAttribute("disabled", "false");
                    }


                }

            }


        });

        findlist();


    }


    $(document).on("click", "#allstart", allstart);

    /**
     *
     * 多选关闭
     */


    function allstop() {
        var allstar = new Array();
        var arr = $(".videoche");
        for (var i = 0; i < arr.length; i++) {

                var item = sessionStorage.getItem(arr[i].value+"start");
                // alert(!$.isEmptyObject(item))
                if(!$.isEmptyObject(item)){

                    allstar.push(item)
                }
        }

        if (strarr == null) {
            return;
        }


        var strify = JSON.stringify(allstar);
        $.get("/allendController", {
            "numbers": strify,
            "type": 1,
            "data": new Date().getTime()
        }, function (data) {


            for (var key in data.data) {


                var span = $("span[value=" + key + "]")

                $(span).html(data.data[key]);
                sessionStorage.setItem(key, data.data[key]);

                if (data.data[key] == "关闭成功") {

                    sessionStorage.clear();
                    $("#allstart").css("background", "#FF5722");
                    $("#allclose").css("background", "#6c6c6c");
                    $("#onlinelook").css("background", "#6c6c6c");

                    $("#allstart").prop("style", "display: block;");
                    $("#allclose").prop("style", "display: none;");
                    $("#onlinelook").prop("style", "display: none;");
                    var check = $("input[value=" + key + "]");

                    for (var i = 0; i < check.length; i++) {

                        check[i].removeAttribute("disabled");

                    }




                }
            }


        });


        for (var i = 0; i < arr.length; i++) {

            var item = sessionStorage.getItem(arr[i].value+"start");
            // alert(!$.isEmptyObject(item))
            if(!$.isEmptyObject(item)){

              sessionStorage.removeItem(arr[i].value+"start");
            }
        }

        var videoon = $(".allv");

        for (var i = 0; i < videoon.length; i++) {
            videoon[i].setAttribute("style", "display:none");

        }

    }

    $(document).on("click", "#allclose", allstop);

    function onlineLook() {


        var videoon = $(".allv");


        var onli = new Array();
        var arr = $(".videoche");
        for (var i = 0; i < arr.length; i++) {

            var item = sessionStorage.getItem(arr[i].value+"start");

            if(!$.isEmptyObject(item)){

                onli.push(item)
            }
        }

        if (onli != null || onli.length != 0) {


            for (var i = 0; i < onli.length; i++) {


                for (var keys in map) {

                    if (keys == onli[i]) {

                        videoon[i].setAttribute("url", map[keys]);
                        videoon[i].setAttribute("style", "display:block;width: 640px; height: 480px;");

                    }

                }


            }
        }


    }

    $(document).on("click", "#onlinelook", onlineLook);
    /**
     * 点击全选
     */
    $(document).on("click", "#allsel", function () {
        var arr = $(".videoche");
        if (this.checked) {


            for (var i = 0; i < arr.length; i++) {

                if (arr[i].disabled == false) {
                    arr[i].checked = true;

                }
            }
        } else {

            for (var i = 0; i < arr.length; i++) {
                if (arr[i].disabled == false) {
                    arr[i].checked = false;
                }

            }
        }


    });


    //执行一个laydate实例
    laydate.render({
        elem: '#start' //指定元素
    });
    //执行一个laydate实例
    laydate.render({
        elem: '#end' //指定元素
    });
    /*用户-停用*/
    window.member_stop = function (obj, id) {
        layer.confirm('确认要停用吗？', function (index) {
            if ($(obj).attr('title') == '启用') {

                //发异步把用户状态进行更改
                $(obj).attr('title', '停用')
                $(obj).find('i').html('&#xe62f;');

                $(obj).parents("tr").find(".td-status").find('span').addClass('layui-btn-disabled').html('已停用');
                layer.msg('已停用!', {
                    icon: 5,
                    time: 1000
                });

            } else {
                $(obj).attr('title', '启用')
                $(obj).find('i').html('&#xe601;');

                $(obj).parents("tr").find(".td-status").find('span').removeClass('layui-btn-disabled').html('已启用');
                layer.msg('已启用!', {
                    icon: 5,
                    time: 1000
                });
            }
        });
    }

    /*用户-删除*/
    window.member_del = function (obj, id) {
        layer.confirm('确认要删除吗？', function (index) {
            //发异步删除数据
            $(obj).parents("tr").remove();
            layer.msg('已删除!', {
                icon: 1,
                time: 1000
            });
        });
    }

    window.delAll = function (argument) {
        var data = tableCheck.getData();
        layer.confirm('确认要删除吗？' + data, function (index) {
            //捉到所有被选中的，发异步进行删除
            layer.msg('删除成功', {
                icon: 1
            });
            $(".layui-form-checked").not('.header').parents('tr').remove();
        });
    }

});
