package com.xlk.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xlk.takeout.common.R;
import com.xlk.takeout.dto.DishDto;
import com.xlk.takeout.entity.Category;
import com.xlk.takeout.entity.Dish;
import com.xlk.takeout.service.CategoryService;
import com.xlk.takeout.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品
     *
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        String keys = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(keys);

        return R.success("新增菜品成功");
    }

    /**
     * 菜品信息分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {

        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Dish::getName, name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(pageInfo, queryWrapper);
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);
    }


    /**
     * 根据id查询菜品对应的口味信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id) {
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品
     *
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {

        dishService.updateWithFlavor(dishDto);
        //清理所有菜品缓存
//        Set keys = redisTemplate.keys("dish_*");
//        redisTemplate.delete(keys);
        //清理特定的菜品
        String keys = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(keys);

        return R.success("修改菜品信息成功");
    }

    /**
     * 修改菜品状态
     *
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> statue(@PathVariable Integer status, Long[] ids) {

        Dish dish = new Dish();
        for (Long id : ids) {
            dish.setId(id);
            dish.setStatus(status);
            dishService.updateById(dish);
        }
        return R.success("修改状态成功");
    }

    @DeleteMapping
    public R<String> delete(Long[] ids) {

        for (Long id : ids) {

            Dish dish = dishService.getById(id);
            Long categoryId = dish.getCategoryId();
            dishService.deleteWithFlavor(id);
            String keys = "dish_" + categoryId + "_1";
            redisTemplate.delete(keys);
        }

        return R.success("删除菜品成功");
    }

//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish) {
//
//        Long categoryId = dish.getCategoryId();
//
//        String name = dish.getName();
//
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//
//        queryWrapper.eq(categoryId != null, Dish::getCategoryId, categoryId);
//        queryWrapper.like(name != null, Dish::getName, name);
//
//        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//        List<Dish> list = dishService.list(queryWrapper);
//
//
//
//        return R.success(list);
//    }

    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {

        List<DishDto> dishDtos = null;
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();


        //先从redis中获取缓存数据
        dishDtos = (List<DishDto>) redisTemplate.opsForValue().get(key);


        if (dishDtos != null) {
            //如果存在，直接返回，无需查询数据库
            return R.success(dishDtos);
        }
        //如果不存在需要查询数据库


        Long categoryId = dish.getCategoryId();

        String name = dish.getName();

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(categoryId != null, Dish::getCategoryId, categoryId);
        queryWrapper.like(name != null, Dish::getName, name);

        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);

        dishDtos = new ArrayList<>();
        for (Dish dish1 : list) {
            DishDto dishDto = dishService.getByIdWithFlavor(dish1.getId());
            dishDtos.add(dishDto);
        }
        redisTemplate.opsForValue().set(key, dishDtos, 60, TimeUnit.MINUTES);

        return R.success(dishDtos);
    }

}
