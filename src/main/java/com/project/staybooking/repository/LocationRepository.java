package com.project.staybooking.repository;

import com.project.staybooking.model.Location;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

// Repository 最后会由spring boot来实现，所以都定义为 interface
// elastic search自带的增删改查
@Repository
public interface LocationRepository extends ElasticsearchRepository<Location, Long>, CustomLocationRepository {
                                            // 提供最基本功能，crud，get/ delete by id etc；custom，search by distance

}
