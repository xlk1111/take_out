package com.xlk.takeout.dto;

import com.xlk.takeout.entity.Setmeal;
import com.xlk.takeout.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
