package com.zhexu.cs677_lab2.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.zhexu.cs677_lab2.api.bean.basic.Product;


import java.io.IOException;

/**
 * @project: CS677_Lab1
 * @description:
 * @author: zhexu
 * @create: 10/29/22
 **/
public class ProductDeSerializer extends KeyDeserializer {
    @Override
    public Product deserializeKey(String s, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        return new Product(s);
    }
}
