package com.xlk.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xlk.takeout.common.CustomException;
import com.xlk.takeout.dto.DishDto;
import com.xlk.takeout.entity.Dish;
import com.xlk.takeout.entity.DishFlavor;
import com.xlk.takeout.mapper.DishMapper;
import com.xlk.takeout.service.DishFlavorService;
import com.xlk.takeout.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品，同时保存对应的口味数据
     *
     * @param dishDto
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {

        this.save(dishDto);
        Long dishId = dishDto.getId();
        for (DishFlavor flavor : dishDto.getFlavors()) {
            flavor.setDishId(dishId);
        }
        dishFlavorService.saveBatch(dishDto.getFlavors());

    }

    @Override
    public DishDto getByIdWithFlavor(Long id) {

        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();

        BeanUtils.copyProperties(dish, dishDto);
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, id);
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);

        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {

        this.updateById(dishDto);
        Long dishId = dishDto.getId();
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishId);
        dishFlavorService.remove(queryWrapper);
        for (DishFlavor flavor : dishDto.getFlavors()) {
            flavor.setDishId(dishId);
        }
        dishFlavorService.saveBatch(dishDto.getFlavors());
    }

    @Override
    @Transactional
    public void deleteWithFlavor(Long id) {

        Dish dish = this.getById(id);
        int status = dish.getStatus();
        if (status == 1){
            throw new CustomException("菜品正在售卖中，不能删除");
        }

        this.removeById(id);
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, id);
        dishFlavorService.remove(queryWrapper);
    }

}
