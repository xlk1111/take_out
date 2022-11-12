package com.xlk.takeout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xlk.takeout.dto.SetmealDishDto;
import com.xlk.takeout.entity.Dish;
import com.xlk.takeout.entity.SetmealDish;
import com.xlk.takeout.mapper.SetmealDishMapper;
import com.xlk.takeout.service.DishService;
import com.xlk.takeout.service.SetmealDishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper, SetmealDish> implements SetmealDishService {

    @Autowired
    private DishService dishService;

    @Override
    public SetmealDishDto getByIdWithDish(Long id) {

        SetmealDish setmealDish = this.getById(id);
        SetmealDishDto setmealDishDto = new SetmealDishDto();

        BeanUtils.copyProperties(setmealDish, setmealDishDto);

        Dish dish = dishService.getById(setmealDish.getDishId());

        setmealDishDto.setImage(dish.getImage());
        return setmealDishDto;
    }
}
