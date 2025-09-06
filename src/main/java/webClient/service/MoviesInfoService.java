package webClient.service;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import webClient.domain.MovieInfo;
import webClient.repository.MovieInfoRepository;

@Service
public class MoviesInfoService {

    private MovieInfoRepository movieInfoRepository;

    public MoviesInfoService(MovieInfoRepository movieInfoRepository){
        this.movieInfoRepository=movieInfoRepository;
    }

    public Mono<MovieInfo> addMovieInfo(MovieInfo movieInfo) {
        return movieInfoRepository.save(movieInfo);
    }

    public Flux<MovieInfo> getAllMovieInfos() {
       return movieInfoRepository.findAll();
    }

    public Mono<MovieInfo> getMovieInfoById(String id) {
        return movieInfoRepository.findById(id);
    }

    public Mono<MovieInfo> updateMovieInfo(String id, MovieInfo updatedMovieInfo) {
        return movieInfoRepository.findById(id)
                                  .flatMap(movieInfo ->{
                                    movieInfo.setCast(updatedMovieInfo.getCast());
                                    movieInfo.setName(updatedMovieInfo.getName());
                                    movieInfo.setRelease_Date(updatedMovieInfo.getRelease_Date());
                                    movieInfo.setYear(updatedMovieInfo.getYear());
                                    return movieInfoRepository.save(movieInfo);
                                  });
    }

    public Mono<Void> deleteMovieInfo(String id) {
        return movieInfoRepository.deleteById(id);
    }

    public Flux<MovieInfo> getMovieInfoFindByYear(Integer year) {
        return movieInfoRepository.findByYear(year);
    }

    public Flux<MovieInfo> getMovieInfoFindByName(String name) {
        System.out.println("In service layer");
        return movieInfoRepository.findByName(name);
    }
    
}
