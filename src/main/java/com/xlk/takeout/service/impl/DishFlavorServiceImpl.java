package com.xlk.takeout.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xlk.takeout.entity.DishFlavor;
import com.xlk.takeout.mapper.DishFlavorMapper;
import com.xlk.takeout.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
