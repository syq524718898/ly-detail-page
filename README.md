## 分发层nginx配置部分

nginx.conf 

http部分需要配置

```
lua_shared_dict my_cache 128m;
lua_package_path "/usr/servers/lualib/?.lua;;";
lua_package_cpath "/usr/servers/lualib/?.so;;";
include /usr/servers/mylua/distribute.conf;
```

distribute.conf配置

```
server {
    listen       80;
    server_name  _;
    
    location /item {
        default_type 'text/html';
        client_max_body_size 1000m;
        content_by_lua_file /usr/servers/mylua/itemdistribute.lua;
    }
}

```

分发层lua脚本：itemdistribute.lua

```
local uri_args = ngx.req.get_uri_args()
local productId = uri_args["productId"]
local hosts = {"192.168.130.43", "192.168.130.42"}

local cache_ngx = ngx.shared.my_cache
local hash = ngx.crc32_long(productId)
local index = (hash % 2) + 1
backend = "http://"..hosts[index]

local http = require("resty.http")
local httpc = http.new()

local resp, err = httpc:request_uri(backend, {
    method = "GET",
    path = "/item?productId="..productId,
    keepalive = false
})

if not resp then
    ngx.say("request error :", err)
    return
end

ngx.say(resp.body)
httpc:close()

```

## 应用层nginx配置部分

nginx.conf

http部分

```
lua_package_path "/usr/servers/lualib/?.lua;;";
lua_package_cpath "/usr/servers/lualib/?.so;;";
lua_shared_dict my_cache 128m;
include /usr/servers/mylua/app.conf;
```

app.conf

```
server {
    listen       80;
    server_name  _;
    set $template_location "/templates";
    set $template_root "/usr/servers/templates";

    location /item {
        default_type 'text/html';
        content_by_lua_file /usr/servers/mylua/itemdetail.lua;
    }

}

```

应用层lua脚本：itemdetail.lua

```
local uri_args = ngx.req.get_uri_args()
local productId = uri_args["productId"]
local cache_ngx = ngx.shared.my_cache
local http = require("resty.http")
local httpc = http.new()

local model = cache_ngx:get(productId)
if model == "" or model == nil then
        local resp, err = httpc:request_uri("http://192.168.1.103:8084",{
                method = "GET",
                path = "/item?productId="..productId,
                keepalive = false
        })
        model  = resp.body
        cache_ngx:set(productId,model,10 * 60)
end

local cjson = require("cjson")
local productCacheJSON = cjson.decode(model)

local context = {
        brand = productCacheJSON.brand,
        categories = productCacheJSON.categories,
        spu = productCacheJSON.spu,
        skus = cjson.encode(productCacheJSON.skus),
        detail = productCacheJSON.detail,
        specs = cjson.encode(productCacheJSON.specs)
}

```

## 商品详情页模板文件

```html
<!DOCTYPE html>

<head>
    <meta charset="utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7; IE=EDGE">
    <meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7"/>
    <title>乐优商城--商品详情页</title>
    <link rel="icon" href="/assets/img/favicon.ico">

    <link rel="stylesheet" type="text/css" href="/css/webbase.css"/>
    <link rel="stylesheet" type="text/css" href="/css/pages-item.css"/>
    <link rel="stylesheet" type="text/css" href="/css/pages-zoom.css"/>
    <link rel="stylesheet" type="text/css" href="/css/widget-cartPanelView.css"/>

    <style type="text/css">
        .goods-intro-list li {
            display: inline-block;
            width: 300px;
        }

        .Ptable {
            margin: 10px 0;
        }

        .Ptable-item {
            padding: 12px 0;
            line-height: 220%;
            color: #999;
            font-size: 12px;
            border-bottom: 1px solid #eee;
        }

        .Ptable-item h3 {
            width: 110px;
            text-align: right;
        }

        .Ptable-item h3, .package-list h3 {
            font-weight: 400;
            font-size: 12px;
            float: left;
        }

        h3 {
            display: block;
            font-size: 1.17em;
            -webkit-margin-before: 1em;
            -webkit-margin-after: 1em;
            -webkit-margin-start: 0px;
            -webkit-margin-end: 0px;
            font-weight: bold;
        }

        .Ptable-item dl {
            margin-left: 110px;
        }

        dl {
            display: block;
            -webkit-margin-before: 1em;
            -webkit-margin-after: 1em;
            -webkit-margin-start: 0px;
            -webkit-margin-end: 0px;
        }

        .Ptable-item dt {
            width: 160px;
            float: left;
            text-align: right;
            padding-right: 5px;
        }

        .Ptable-item dd {
            margin-left: 210px;
        }

        dd {
            display: block;
            -webkit-margin-start: 40px;
        }

        .package-list {
            padding: 12px 0;
            line-height: 220%;
            color: #999;
            font-size: 12px;
            margin-top: -1px;
        }

        .package-list h3 {
            width: 130px;
            text-align: right;
        }

        .package-list p {
            margin-left: 155px;
            padding-right: 50px;
        }
    </style>

</head>

<body>

<!-- 头部栏位 -->
<!--页面顶部，由js动态加载-->
<div id="itemApp">
    <div id="nav-bottom">
        <ly-top/>
    </div>
    <div class="py-container">
        <div id="item">
            <div class="crumb-wrap">
                <ul class="sui-breadcrumb">
                    {% for i, c in ipairs(categories) do %}
                    <li><a href="#">{{ c.name }}</a></li>
                    {% end %}
                    <li>
                        <a href="#">{{ brand.name }}</a>
                    </li>
                    <li class="active">{{ spu.title }}</li>
                </ul>
            </div>
            <!--product-info-->
            <div class="product-info">
                <div class="fl preview-wrap">
                    <!--放大镜效果-->
                    <div class="zoom">
                        <!--默认第一个预览-->
                        <div id="preview" class="spec-preview">
							<span class="jqzoom">
								<img :jqimg="images[0]" :src="images[0]" width="400px" height="400px"/>
							</span>
                        </div>
                        <!--下方的缩略图-->
                        <div class="spec-scroll">
                            <a class="prev">&lt;</a>
                            <!--左右按钮-->
                            <div class="items">
                                <ul>
                                    <li v-for="img in images">
                                        <img :src="img" :bimg="img" onmousemove="preview(this)"/>
                                    </li>
                                </ul>
                            </div>
                            <a class="next">&gt;</a>
                        </div>
                    </div>
                </div>
                <div class="fr itemInfo-wrap">
                    <div class="sku-name">
                        <h4 v-text="sku.title"></h4>
                    </div>
                    <div class="news"><span>{* spu.subTitle *}</span></div>
                    <div class="summary">
                        <div class="summary-wrap">
                            <div class="fl title"><i>价　　格</i></div>
                            <div class="fl price">
                                <i>¥</i><em v-text="ly.formatPrice(sku.price)"></em><span>降价通知</span>
                            </div>
                            <div class="fr remark"><i>累计评价</i><em>612188</em></div>
                        </div>
                        <div class="summary-wrap">
                            <div class="fl title">
                                <i>促　　销</i>
                            </div>
                            <div class="fl fix-width">
                                <i class="red-bg">加价购</i>
                                <em class="t-gray">满999.00另加20.00元，或满1999.00另加30.00元，或满2999.00另加40.00元，即可在购物车换
                                    购热销商品</em>
                            </div>
                        </div>
                    </div>
                    <div class="support">
                        <div class="summary-wrap">
                            <div class="fl title">
                                <i>支　　持</i>
                            </div>
                            <div class="fl fix-width">
                                <em class="t-gray">以旧换新，闲置手机回收 4G套餐超值抢 礼品购</em>
                            </div>
                        </div>
                        <div class="summary-wrap">
                            <div class="fl title">
                                <i>配 送 至</i>
                            </div>
                            <div class="fl fix-width">
                                <em class="t-gray">上海 <span v-text="sku.stock > 0 ? '有货' : '缺货'"></span></em>
                            </div>
                        </div>
                    </div>
                    <div class="clearfix choose">
                        <div id="specification" class="summary-wrap clearfix">
                            <dl v-for="(options,id) in specialSpec" :key="id">
                                <dt>
                                    <div class="fl title">
                                        <i v-text="params[id]"></i>
                                    </div>
                                </dt>
                                <dd v-for="(o,i) in options" :key="i" @click="selectSku(id,i)">
                                    <a href="javascript:;" :class="{selected:i === indexes[id], locked:locked(id, i)}">
                                        {-raw-}{{o}}{-raw-}<span title="点击取消选择">&nbsp;</span>
                                    </a>
                                </dd>
                            </dl>
                        </div>

                        <div class="summary-wrap">
                            <div class="fl title">
                                <div class="control-group">
                                    <div class="controls">
                                        <input autocomplete="off" type="text" disabled v-model="num" minnum="1"
                                               class="itxt"/>
                                        <a href="javascript:void(0)" class="increment plus" @click="increment">+</a>
                                        <a href="javascript:void(0)" class="increment mins" @click="decrement">-</a>
                                    </div>
                                </div>
                            </div>
                            <div class="fl">
                                <ul class="btn-choose unstyled">
                                    <li>
                                        <a href="#" @click.prevent="addCart" target="_blank"
                                           class="sui-btn  btn-danger addshopcar">加入购物车</a>
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <!--product-detail-->
            <div class="clearfix product-detail">
                <div class="fl aside">
                    <ul class="sui-nav nav-tabs tab-wraped">
                        <li class="active">
                            <a href="#index" data-toggle="tab">
                                <span>相关分类</span>
                            </a>
                        </li>
                        <li>
                            <a href="#profile" data-toggle="tab">
                                <span>推荐品牌</span>
                            </a>
                        </li>
                    </ul>
                    <div class="tab-content tab-wraped">
                        <div id="index" class="tab-pane active">
                            <ul class="part-list unstyled">
                                <li>手机</li>
                                <li>手机壳</li>
                                <li>内存卡</li>
                                <li>Iphone配件</li>
                                <li>贴膜</li>
                                <li>手机耳机</li>
                                <li>移动电源</li>
                                <li>平板电脑</li>
                            </ul>
                            <ul class="goods-list unstyled">
                                <li>
                                    <div class="list-wrap">
                                        <div class="p-img">
                                            <img src="/img/_/part01.png"/>
                                        </div>
                                        <div class="attr">
                                            <em>Apple苹果iPhone 6s (A1699)</em>
                                        </div>
                                        <div class="price">
                                            <strong>
                                                <em>¥</em>
                                                <i>6088.00</i>
                                            </strong>
                                        </div>
                                        <div class="operate">
                                            <a href="javascript:void(0);" class="sui-btn btn-bordered">加入购物车</a>
                                        </div>
                                    </div>
                                </li>
                                <li>
                                    <div class="list-wrap">
                                        <div class="p-img">
                                            <img src="/img/_/part02.png"/>
                                        </div>
                                        <div class="attr">
                                            <em>Apple苹果iPhone 6s (A1699)</em>
                                        </div>
                                        <div class="price">
                                            <strong>
                                                <em>¥</em>
                                                <i>6088.00</i>
                                            </strong>
                                        </div>
                                        <div class="operate">
                                            <a href="javascript:void(0);" class="sui-btn btn-bordered">加入购物车</a>
                                        </div>
                                    </div>
                                </li>
                                <li>
                                    <div class="list-wrap">
                                        <div class="p-img">
                                            <img src="/img/_/part03.png"/>
                                        </div>
                                        <div class="attr">
                                            <em>Apple苹果iPhone 6s (A1699)</em>
                                        </div>
                                        <div class="price">
                                            <strong>
                                                <em>¥</em>
                                                <i>6088.00</i>
                                            </strong>
                                        </div>
                                        <div class="operate">
                                            <a href="javascript:void(0);" class="sui-btn btn-bordered">加入购物车</a>
                                        </div>
                                    </div>
                                    <div class="list-wrap">
                                        <div class="p-img">
                                            <img src="/img/_/part02.png"/>
                                        </div>
                                        <div class="attr">
                                            <em>Apple苹果iPhone 6s (A1699)</em>
                                        </div>
                                        <div class="price">
                                            <strong>
                                                <em>¥</em>
                                                <i>6088.00</i>
                                            </strong>
                                        </div>
                                        <div class="operate">
                                            <a href="javascript:void(0);" class="sui-btn btn-bordered">加入购物车</a>
                                        </div>
                                    </div>
                                    <div class="list-wrap">
                                        <div class="p-img">
                                            <img src="/img/_/part03.png"/>
                                        </div>
                                        <div class="attr">
                                            <em>Apple苹果iPhone 6s (A1699)</em>
                                        </div>
                                        <div class="price">
                                            <strong>
                                                <em>¥</em>
                                                <i>6088.00</i>
                                            </strong>
                                        </div>
                                        <div class="operate">
                                            <a href="javascript:void(0);" class="sui-btn btn-bordered">加入购物车</a>
                                        </div>
                                    </div>
                                </li>
                            </ul>
                        </div>
                        <div id="profile" class="tab-pane">
                            <p>推荐品牌</p>
                        </div>
                    </div>
                </div>
                <div class="fr detail">
                    <div class="clearfix fitting">
                        <h4 class="kt">选择搭配</h4>
                        <div class="good-suits">
                            <div class="fl master">
                                <div class="list-wrap">
                                    <div class="p-img">
                                        <img src="/img/_/l-m01.png"/>
                                    </div>
                                    <em>￥5299</em>
                                    <i>+</i>
                                </div>
                            </div>
                            <div class="fl suits">
                                <ul class="suit-list">
                                    <li class="">
                                        <div id="e">
                                            <img src="/img/_/dp01.png"/>
                                        </div>
                                        <i>Feless费勒斯VR</i>
                                        <label data-toggle="checkbox" class="checkbox-pretty">
                                            <input type="checkbox"><span>39</span>
                                        </label>
                                    </li>
                                    <li class="">
                                        <div id=""><img src="/img/_/dp02.png"/></div>
                                        <i>Feless费勒斯VR</i>
                                        <label data-toggle="checkbox" class="checkbox-pretty">
                                            <input type="checkbox"><span>50</span>
                                        </label>
                                    </li>
                                    <li class="">
                                        <div id=""><img src="/img/_/dp03.png"/></div>
                                        <i>Feless费勒斯VR</i>
                                        <label data-toggle="checkbox" class="checkbox-pretty">
                                            <input type="checkbox"><span>59</span>
                                        </label>
                                    </li>
                                    <li class="">
                                        <div id=""><img src="/img/_/dp04.png"/></div>
                                        <i>Feless费勒斯VR</i>
                                        <label data-toggle="checkbox" class="checkbox-pretty">
                                            <input type="checkbox"><span>99</span>
                                        </label>
                                    </li>
                                </ul>
                            </div>
                            <div class="fr result">
                                <div class="num">已选购0件商品</div>
                                <div class="price-tit"><strong>套餐价</strong></div>
                                <div class="price">￥5299</div>
                                <button class="sui-btn  btn-danger addshopcar">加入购物车</button>
                            </div>
                        </div>
                    </div>
                    <div class="tab-main intro">
                        <ul class="sui-nav nav-tabs tab-wraped">
                            <li class="active">
                                <a href="#one" data-toggle="tab">
                                    <span>商品介绍</span>
                                </a>
                            </li>
                            <li>
                                <a href="#two" data-toggle="tab">
                                    <span>规格与包装</span>
                                </a>
                            </li>
                            <li>
                                <a href="#three" data-toggle="tab">
                                    <span>售后保障</span>
                                </a>
                            </li>
                            <li>
                                <a href="#four" data-toggle="tab">
                                    <span>商品评价</span>
                                </a>
                            </li>
                            <li>
                                <a href="#five" data-toggle="tab">
                                    <span>手机社区</span>
                                </a>
                            </li>
                        </ul>
                        <div class="clearfix"></div>
                        <div class="tab-content tab-wraped">
                            <div id="one" class="tab-pane active">
                                <ul class="goods-intro-list unstyled" style="list-style: none;" >
                                </ul>
                                <!--商品详情-->
                                <div class="intro-detail">
                                    <div>{* detail.description *}</div>
                                </div>
                            </div>
                            <div id="two" class="tab-pane">
                                <div class="Ptable">
                                    <div class="Ptable-item" v-for="group in specGroups" :key="group.id">
                                        <h3 v-text="group.name"></h3>
                                        <dl>
            <span v-for="param in group.params" :key="param.id">
				<dt v-text="param.name"></dt><dd v-text="param.value + (param.unit || '')"></dd>
			</span>
                                        </dl>
                                    </div>
                                </div>
                                <div class="package-list">
                                    <h3>包装清单</h3>
                                    <p>{{ detail.packingList }}</p>
                                </div>

                            </div>
                            <div id="three" class="tab-pane">
                                <p>{{ detail.afterService }}</p>
                            </div>
                            <div id="four" class="tab-pane">
                                <p>商品评价</p>
                            </div>
                            <div id="five" class="tab-pane">
                                <p>手机社区</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <!--like-->
            <div class="clearfix"></div>
            <div class="like">
                <h4 class="kt">猜你喜欢</h4>
                <div class="like-list">
                    <ul class="yui3-g">
                        <li class="yui3-u-1-6">
                            <div class="list-wrap">
                                <div class="p-img">
                                    <img src="/img/_/itemlike01.png"/>
                                </div>
                                <div class="attr">
                                    <em>DELL戴尔Ins 15MR-7528SS 15英寸 银色 笔记本</em>
                                </div>
                                <div class="price">
                                    <strong>
                                        <em>¥</em>
                                        <i>3699.00</i>
                                    </strong>
                                </div>
                                <div class="commit">
                                    <i class="command">已有6人评价</i>
                                </div>
                            </div>
                        </li>
                        <li class="yui3-u-1-6">
                            <div class="list-wrap">
                                <div class="p-img">
                                    <img src="/img/_/itemlike02.png"/>
                                </div>
                                <div class="attr">
                                    <em>Apple苹果iPhone 6s/6s Plus 16G 64G 128G</em>
                                </div>
                                <div class="price">
                                    <strong>
                                        <em>¥</em>
                                        <i>4388.00</i>
                                    </strong>
                                </div>
                                <div class="commit">
                                    <i class="command">已有700人评价</i>
                                </div>
                            </div>
                        </li>
                        <li class="yui3-u-1-6">
                            <div class="list-wrap">
                                <div class="p-img">
                                    <img src="/img/_/itemlike03.png"/>
                                </div>
                                <div class="attr">
                                    <em>DELL戴尔Ins 15MR-7528SS 15英寸 银色 笔记本</em>
                                </div>
                                <div class="price">
                                    <strong>
                                        <em>¥</em>
                                        <i>4088.00</i>
                                    </strong>
                                </div>
                                <div class="commit">
                                    <i class="command">已有700人评价</i>
                                </div>
                            </div>
                        </li>
                        <li class="yui3-u-1-6">
                            <div class="list-wrap">
                                <div class="p-img">
                                    <img src="/img/_/itemlike04.png"/>
                                </div>
                                <div class="attr">
                                    <em>DELL戴尔Ins 15MR-7528SS 15英寸 银色 笔记本</em>
                                </div>
                                <div class="price">
                                    <strong>
                                        <em>¥</em>
                                        <i>4088.00</i>
                                    </strong>
                                </div>
                                <div class="commit">
                                    <i class="command">已有700人评价</i>
                                </div>
                            </div>
                        </li>
                        <li class="yui3-u-1-6">
                            <div class="list-wrap">
                                <div class="p-img">
                                    <img src="/img/_/itemlike05.png"/>
                                </div>
                                <div class="attr">
                                    <em>DELL戴尔Ins 15MR-7528SS 15英寸 银色 笔记本</em>
                                </div>
                                <div class="price">
                                    <strong>
                                        <em>¥</em>
                                        <i>4088.00</i>
                                    </strong>
                                </div>
                                <div class="commit">
                                    <i class="command">已有700人评价</i>
                                </div>
                            </div>
                        </li>
                        <li class="yui3-u-1-6">
                            <div class="list-wrap">
                                <div class="p-img">
                                    <img src="/img/_/itemlike06.png"/>
                                </div>
                                <div class="attr">
                                    <em>DELL戴尔Ins 15MR-7528SS 15英寸 银色 笔记本</em>
                                </div>
                                <div class="price">
                                    <strong>
                                        <em>¥</em>
                                        <i>4088.00</i>
                                    </strong>
                                </div>
                                <div class="commit">
                                    <i class="command">已有700人评价</i>
                                </div>
                            </div>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
    </div>

</div>
<script src="/js/vue/vue.js"></script>
<script src="/js/axios.min.js"></script>
<script src="/js/common.js"></script>

<script>
    const specialSpec = {* detail.specialSpec *};
    const genericSpec = {* detail.genericSpec *};
    const skus = {* skus *};
    const specs = {* specs *};
    const params = {};
    specs.forEach(group => {
        group.params.forEach(param => {
            params[param.id] = param.name;
        })
    });
    // 初始化特有规格参数默认选中一个
    const indexes = {};
    const initIndex = skus[0].indexes.split("_");
    Object.keys(specialSpec).forEach((id, i) => {
        indexes[id] = parseInt(initIndex[i]);
    })
    const indexArr = skus.map(s => s.indexes);

</script>
<script>
    var itemVm = new Vue({
        el: "#itemApp",
        data: {
            ly,
            specialSpec,// 特有规格参数模板
            params,// 参数对象数组
            indexes,// 初始化被选中的参数
            num: 1,
        },
        methods: {
            decrement() {
                if (this.num > 1) {
                    this.num--;
                }
            },
            increment() {
                this.num++;
            },
            addCart() {
                // 判断是否登录
                ly.verifyUser().then(() => {
                    // 已登录
                    ly.http.post("/cart", {
                        skuId: this.sku.id,
                        title: this.sku.title,
                        image: this.images[0],
                        price: this.sku.price,
                        num: this.num,
                        ownSpec: JSON.stringify(this.ownSpec)
                    }).then(() => {
                        // 跳转到购物车列表页
                        window.location.href = "http://www.leyou.com/cart.html";
                    }).catch(() => {
                        alert("添加购物车失败，请重试！");
                    })
                }).catch(() => {
                    // 获取以前的购物车
                    const carts = ly.store.get("carts") || [];
                    // 获取与当前商品id一致的购物车数据
                    const cart = carts.find(c => c.skuId === this.sku.id);
                    if (cart) {
                        // 存在，修改数量
                        cart.num += this.num;
                    } else {
                        // 不存在，新增
                        carts.push({
                            skuId: this.sku.id,
                            title: this.sku.title,
                            image: this.images[0],
                            price: this.sku.price,
                            num: this.num,
                            ownSpec: JSON.stringify(this.ownSpec)
                        })
                    }
                    // 未登录
                    ly.store.set("carts", carts);
                    // 跳转到购物车列表页
                    window.location.href = "http://www.leyou.com/cart.html";
                })
            },
            locked(id, i) {
                // 如果只有一个可选项，永不锁定
                if(specialSpec[id].length === 1) return false;
                // 如果有其它项未选，不锁定
                let boo = true;
                Object.keys(this.indexes).forEach(key => {
                    if (key !== id && this.indexes[key] == null) {
                        boo = false;
                        return;
                    }
                });
                if (!boo) return false;
                // 如果当前项的组合不存在，锁定
                const {...o} = this.indexes;
                o[id] = i;
                const index = Object.values(o).join("_");
                return !indexArr.includes(index);
            },
            selectSku(id, i) {
                // 先判断当前选中的是否是锁定项
                const isLocked = this.locked(id, i);
                // 无论是否是锁定项，都允许修改
                this.indexes[id] = i;
                // 如果是锁定项，则需要调整其它项的选中状态
                if (isLocked) {
                    Object.keys(this.indexes).forEach(key => {
                        if (key !== id) {
                            const remainSpec = specialSpec[key].filter((e, j) => !this.locked(key, j));
                            this.indexes[key] = remainSpec.length === 1 ? specialSpec[key].findIndex(e => e === remainSpec[0]) : null;
                        }
                    })
                }

            }
        },
        computed: {
            sku() {
                if (Object.values(this.indexes).includes(null)) {
                    return skus[0];
                }
                // 获取选中的规格参数的索引
                const index = Object.values(this.indexes).join("_");
                // 去skus集合寻找与index一致的sku
                return skus.find(s => s.indexes === index);
            },
            images() {
                return this.sku.images ? this.sku.images.split(",") : [];
            },
            specGroups() {
                // 获取特有规格参数值
                const ownSpec = JSON.parse(this.sku.ownSpec);
                specs.forEach(group => {
                    group.params.forEach(param => {
                        if (param.generic) {
                            param.value = genericSpec[param.id];
                        } else {
                            param.value = ownSpec[param.id];
                        }
                    })
                })
                return specs;
            },
            ownSpec() {
                const ownSpec = JSON.parse(this.sku.ownSpec);
                const obj = {};
                Object.keys(ownSpec).forEach(id => {
                    obj[this.params[id]] = ownSpec[id];
                })
                return obj;
            }
        },
        components: {
            lyTop: () => import('/js/pages/top.js')
        }
    });
</script>

<script type="text/javascript" src="/js/plugins/jquery/jquery.min.js"></script>
<script type="text/javascript">
    $(function () {
        $("#service").hover(function () {
            $(".service").show();
        }, function () {
            $(".service").hide();
        });
        $("#shopcar").hover(function () {
            $("#shopcarlist").show();
        }, function () {
            $("#shopcarlist").hide();
        });

    })
</script>
<script type="text/javascript" src="/js/model/cartModel.js"></script>
<script type="text/javascript" src="/js/plugins/jquery.easing/jquery.easing.min.js"></script>
<script type="text/javascript" src="/js/plugins/sui/sui.min.js"></script>
<script type="text/javascript" src="/js/plugins/jquery.jqzoom/jquery.jqzoom.js"></script>
<script type="text/javascript" src="/js/plugins/jquery.jqzoom/zoom.js"></script>
<script type="text/javascript" src="index/index.js"></script>
</body>

</html>
```
