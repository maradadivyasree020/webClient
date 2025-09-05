package webClient.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import webClient.domain.MovieInfo; 

public interface MovieInfoRepository extends ReactiveMongoRepository<MovieInfo, String> {
    
}
