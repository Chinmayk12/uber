package com.chinuthon.project.uber.uber.configs;

import com.chinuthon.project.uber.uber.dto.PointDto;
import com.chinuthon.project.uber.uber.utils.GeometryUtils;
import org.locationtech.jts.geom.Point;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

    @Bean
    public ModelMapper getModelMapper() {

        ModelMapper modelMapper = new ModelMapper();

        // For to convert the PointDto to Point entity
        modelMapper.typeMap(PointDto.class, Point.class).setConverter(context->{
           PointDto pointDto = context.getSource();
           return GeometryUtils.createPoint(pointDto);
        });

        // For to convert the Point entity to PointDto
        modelMapper.typeMap(Point.class,PointDto.class).setConverter(context ->{
            Point point = context.getSource();
            double[] pointCoordinates = new double[]{
                    point.getY(),
                    point.getX()
            };

            return new PointDto(pointCoordinates);
        });

        return modelMapper;
    }

}
