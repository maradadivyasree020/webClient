package webClient.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import reactor.core.publisher.Flux;
import webClient.domain.MovieInfo;
 

public interface MovieInfoRepository extends ReactiveMongoRepository<MovieInfo, String> {
    
    Flux<MovieInfo> findByYear(Integer year);
    Flux<MovieInfo> findByName(String name);
}
