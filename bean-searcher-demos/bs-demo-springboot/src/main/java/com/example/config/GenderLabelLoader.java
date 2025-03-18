package com.example.config;

import cn.zhxu.bs.label.Label;
import cn.zhxu.bs.label.LabelLoader;
import com.example.enums.Gender;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GenderLabelLoader implements LabelLoader<Gender> {

    @Override
    public boolean supports(String key) {
        return "genderName".equals(key);
    }

    @Override
    public List<Label<Gender>> load(String key, List<Gender> genders) {
        return genders.stream()
                .map(gender -> new Label<>(gender, gender.getChinese()))
                .collect(Collectors.toList());
    }

}
