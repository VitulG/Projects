package com.govt.irctc.elasticsearchrepository;

import com.govt.irctc.model.Train;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainSearchRepository extends ElasticsearchRepository<Train, Long> {
    public List<Train> searchTrainByTrainNameContaining(String trainName);
}
