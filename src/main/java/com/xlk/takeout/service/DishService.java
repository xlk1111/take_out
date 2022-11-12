package com.xlk.takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xlk.takeout.dto.DishDto;
import com.xlk.takeout.entity.Dish;

public interface DishService extends IService<Dish> {

    //新增菜品，同时插入菜品对应的口味数据，需要操作两张表：dish dish_flavor
    public void saveWithFlavor(DishDto dishDto);

    //根据id查询菜品信息和口味信息
    public DishDto getByIdWithFlavor(Long id);

    public void updateWithFlavor(DishDto dishDto);

    public void deleteWithFlavor(Long id);


}
