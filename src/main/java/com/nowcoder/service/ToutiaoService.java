package com.nowcoder.service;

import org.springframework.stereotype.Service;

@Service //定义它为service
public class ToutiaoService {
    public String say(){
        return "This is from ToutiaoService";
    }
}
