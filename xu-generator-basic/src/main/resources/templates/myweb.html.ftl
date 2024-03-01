<!DOCTYPE html>
<html>
    <head>
        <title>xu官网</title>
    </head>
    <body>
        <h1>欢迎来到xu官网</h1>
        <ul>
            <#-- 循环渲染导航条 -->
            <#list menuItems as item>
                <li><a href="${item.url}">${item.label}</a></li>
            </#list>
        </ul>

        <#-- 版权信息 -->
        <footer>
            ${currentYear} xu官网. All rights reserved.

        </footer>
    </body>
</html>