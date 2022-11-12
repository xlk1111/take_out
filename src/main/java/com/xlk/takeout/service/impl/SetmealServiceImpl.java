package com.xlk.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xlk.takeout.common.CustomException;
import com.xlk.takeout.dto.SetmealDto;
import com.xlk.takeout.entity.Dish;
import com.xlk.takeout.entity.Setmeal;
import com.xlk.takeout.entity.SetmealDish;
import com.xlk.takeout.mapper.SetmealMapper;
import com.xlk.takeout.service.DishService;
import com.xlk.takeout.service.SetmealDishService;
import com.xlk.takeout.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {


    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private DishService dishService;

    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {

        this.save(setmealDto);
        Long setmealId = setmealDto.getId();
        for (SetmealDish setmealDish : setmealDto.getSetmealDishes()) {
            setmealDish.setSetmealId(setmealId);
        }
        setmealDishService.saveBatch(setmealDto.getSetmealDishes());

    }

    @Override
    public SetmealDto getByIdWithDish(Long id) {
        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal, setmealDto);
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, id);
        List<SetmealDish> list = setmealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(list);

        return setmealDto;
    }

    @Override
    public void updateWithDish(SetmealDto setmealDto) {
        this.updateById(setmealDto);
        Long setmealId = setmealDto.getId();
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, setmealId);
        setmealDishService.remove(queryWrapper);
        for (SetmealDish setmealDish : setmealDto.getSetmealDishes()) {
            setmealDish.setSetmealId(setmealId);
        }
        setmealDishService.saveBatch(setmealDto.getSetmealDishes());

    }

    @Override
    @Transactional
    public void deleteWithDish(Long id) {

        Setmeal setmeal = this.getById(id);
        int status = setmeal.getStatus();
        if (status == 1) {
            throw new CustomException("套餐正在售卖中，不能删除");
        }

        this.removeById(id);
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, id);
        setmealDishService.remove(queryWrapper);
    }

    @Override
    public void updateWithDish(Integer status, Long id) {
        Setmeal setmeal = new Setmeal();

        if (status == 1){
            LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();

            queryWrapper.eq(SetmealDish::getSetmealId, id);

            List<SetmealDish> list = setmealDishService.list(queryWrapper);

            for (SetmealDish setmealDish : list) {
                Dish dish = dishService.getById(setmealDish.getDishId());
                if (dish.getStatus() == 0) {
                    throw new CustomException("该套餐中有菜品已停售,无法起售");
                }
            }
        }

        setmeal.setId(id);
        setmeal.setStatus(status);
        this.updateById(setmeal);

    }
}
