package com.nowcoder.controller;

import com.nowcoder.aspect.LogAspect;
import com.nowcoder.model.User;
import com.nowcoder.service.ToutiaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
public class IndexController {
    private static final Logger logger=LoggerFactory.getLogger(LogAspect.class);
    @Autowired  //调用service，注解，直接用对象里的方法
    private ToutiaoService toutiaoService;
    @RequestMapping(path={"/","/index"})   //访问 首页 字符数组
    @ResponseBody   //返回的是一个String
    public String index(HttpSession seesion){
        logger.info("Visit Index");
        return "Hello NowCoder,"+seesion.getAttribute("msg")+"<br> Say:"+toutiaoService.say();

    }

    @RequestMapping(value = {"/profile/{groupId}/{userId}"}) //path
    @ResponseBody
    public String profile(@PathVariable("groupId") String groupId,
                          @PathVariable("userId") int userId,
                          @RequestParam(value="type",defaultValue = "1") int type,  //request参数
                          @RequestParam(value = "key",defaultValue = "nowcoder") String key){    //指定path里的变量
        //service 和dao层
        return String.format("GID{%s},UID{%d},TYPE{%d},KEY{%s}",groupId,userId,type,key);
    }

    @RequestMapping(value={"/vm"})
    public String news(Model model){ //数据存储模型
        model.addAttribute("value1","vv1");
        List<String> colors = Arrays.asList(new String[]{"RED","GREEN","BLUE"});
        Map<String,String > map = new HashMap<String,String>();
        for(int i=0;i<4;i++){
            map.put(String.valueOf(i),String .valueOf(i*i));
        }
        model.addAttribute("colors",colors);  //model上下文传递变量
        model.addAttribute("map",map);
        model.addAttribute("user",new User("Jim"));
        return "news";
    }
    @RequestMapping(value={"/request"})
    @ResponseBody
    public String request(HttpServletRequest request,
                          HttpServletResponse response,
                          HttpSession session){
        StringBuilder sb = new StringBuilder();
        Enumeration<String> headerNames = request.getHeaderNames();
        while(headerNames.hasMoreElements()){
            String name = headerNames.nextElement();
            sb.append(name+":"+request.getHeader(name)+"<br>");
        }
        for(Cookie cookie:request.getCookies()){
            sb.append("Cookie:");
            sb.append(cookie.getName());
            sb.append(":");
            sb.append(cookie.getValue());
            sb.append("<br>");
        }
        sb.append("getMethod:"+request.getMethod()+"<br>");
        sb.append("getPathInfo:"+request.getPathInfo()+"<br>");
        sb.append("getQueryString:"+request.getQueryString()+"<br>");
        sb.append("getRequestURI:"+request.getRequestURI()+"<br>");
        return sb.toString();
    }
    @RequestMapping(value={"/response"})
    @ResponseBody
    public String response(@CookieValue(value="nowcoderid",defaultValue = "a") String nowcodeId,
                           @RequestParam(value="key",defaultValue = "key") String key,
                           @RequestParam(value="value",defaultValue = "value") String value,
                           HttpServletResponse response){
        response.addCookie(new Cookie(key,value));
        response.addHeader(key,value);
        return "NowCoderId From Cookie:"+nowcodeId;
    }
    @RequestMapping("/redirect/{code}")
    public String redirect(@PathVariable("code") int code,
                           HttpSession session){
        /*
        RedirectView red = new RedirectView("/",true);  //跳转主页
        if(code==301){
            red.setStatusCode(HttpStatus.MOVED_PERMANENTLY);   //永久跳转
        }
        return red; */
        session.setAttribute("msg","Jump from redirect.");
        return "redirect:/";
    }
    @RequestMapping("/admin")
    @ResponseBody
    public String admin(@RequestParam(value = "key",required = false) String key){
        if("admin".equals(key)){
            return "hello admin";
        }
        throw new IllegalArgumentException("Key 错误");
    }
    @ExceptionHandler
    @ResponseBody
    public String error(Exception e){
        return "error:"+e.getMessage();
    }
}
