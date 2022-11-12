package com.xlk.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xlk.takeout.common.R;
import com.xlk.takeout.dto.SetmealDishDto;
import com.xlk.takeout.dto.SetmealDto;
import com.itheima.reggie.entity.*;
import com.xlk.takeout.service.CategoryService;
import com.xlk.takeout.service.DishService;
import com.xlk.takeout.service.SetmealDishService;
import com.xlk.takeout.service.SetmealService;
import com.xlk.takeout.entity.Category;
import com.xlk.takeout.entity.Setmeal;
import com.xlk.takeout.entity.SetmealDish;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐管理
 */

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐成功
     *
     * @param setmealDto
     * @return
     */
    @PostMapping
    @CacheEvict(value = "setmealCache" , allEntries = true)
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }


    /**
     * 套餐信息分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        //构造分页构造器
        Page pageInfo = new Page(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();
        //构造条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.like(StringUtils.isNotEmpty(name), Setmeal::getName, name);
        //添加排序条件
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        //执行查询
        setmealService.page(pageInfo, queryWrapper);


        BeanUtils.copyProperties(pageInfo, setmealDtoPage, "records");
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);

            if (category != null) {
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());
        setmealDtoPage.setRecords(list);

        return R.success(setmealDtoPage);
    }


    /**
     * 根据id查询套餐对应的菜品信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> get(@PathVariable Long id) {
        SetmealDto setmealDto = setmealService.getByIdWithDish(id);
        return R.success(setmealDto);
    }

    /**
     * 修改套餐
     *
     * @param setmealDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto) {

        setmealService.updateWithDish(setmealDto);
        return R.success("修改菜品信息成功");
    }

    /**
     * 修改状态成功
     *
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> statue(@PathVariable Integer status, Long[] ids) {


        Setmeal setmeal = new Setmeal();
        for (Long id : ids) {
            setmealService.updateWithDish(status, id);
        }
        return R.success("修改状态成功");
    }

    @DeleteMapping
    @CacheEvict(value = "setmealCache" , allEntries = true)
    public R<String> delete(Long[] ids) {

        for (Long id : ids) {
            setmealService.deleteWithDish(id);
        }
        return R.success("删除菜品成功");
    }

    @GetMapping("/list")
    @Cacheable(value = "setmealCache", key = "#setmeal.categoryId + '_' + #setmeal.status")
    public R<List<Setmeal>> list(Setmeal setmeal) {
        Long categoryId = setmeal.getCategoryId();

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(categoryId != null, Setmeal::getCategoryId, categoryId);

        queryWrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(queryWrapper);

        return R.success(list);
    }

    @GetMapping("/dish/{setmealId}")
    public R<List<SetmealDishDto>> list2(SetmealDish setmealDish) {


        Long setmealId = setmealDish.getSetmealId();
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(setmealId != null, SetmealDish::getSetmealId, setmealId);
        List<SetmealDish> list = setmealDishService.list(queryWrapper);

        ArrayList<SetmealDishDto> setmealDishDtos = new ArrayList<>();

        for (SetmealDish setmealDish1 : list) {
            Long id = setmealDish1.getId();
            SetmealDishDto setmealDishDto = setmealDishService.getByIdWithDish(id);
            setmealDishDtos.add(setmealDishDto);
        }

        return R.success(setmealDishDtos);
    }


}
