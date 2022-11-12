package com.xlk.takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xlk.takeout.dto.SetmealDishDto;
import com.xlk.takeout.entity.SetmealDish;

public interface SetmealDishService extends IService<SetmealDish> {

    public SetmealDishDto getByIdWithDish(Long id);
}
