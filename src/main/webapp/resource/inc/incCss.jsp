<%@ page import="com.ts.cpfr.utils.CommConst" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<title><%=CommConst.WEB_TITLE%></title>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=no">
<!-- 上述3个meta标签*必须*放在最前面，任何其他内容都*必须*跟随其后！ -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/resource/css/base.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/resource/component/element-ui/element-ui.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/resource/component/cropper/cropper.min.css">

<style type="text/css">
    /*修复vue加载时显示{{}}问题 */
    [v-cloak] {
        display: none
    }
</style>
