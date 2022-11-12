package com.xlk.takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xlk.takeout.dto.SetmealDto;
import com.xlk.takeout.entity.Setmeal;

public interface SetmealService extends IService<Setmeal> {

    public void saveWithDish(SetmealDto setmealDto);

    public SetmealDto getByIdWithDish(Long id);

    public void updateWithDish(SetmealDto setmealDto);

    public void deleteWithDish(Long id);

    public void updateWithDish(Integer status, Long id);
}
