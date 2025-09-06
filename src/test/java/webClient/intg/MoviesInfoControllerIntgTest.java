package webClient.intg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;

import webClient.domain.MovieInfo;
import webClient.repository.MovieInfoRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
public class MoviesInfoControllerIntgTest {

    @Autowired
    MovieInfoRepository movieInfoRepository;

    @Autowired
    WebTestClient webTestClient;

    static String MOVIEs_INFO_URL="/v1/movieinfos";

    @BeforeEach
    void setUp() {

        var movieinfos = List.of(new MovieInfo(null, "Batman Begins",
                        2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo(null, "The Dark Knight",
                        2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
                new MovieInfo("abc", "Dark Knight Rises",
                        2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));

        movieInfoRepository.saveAll(movieinfos)
                .blockLast();
    }

    @AfterEach
    void tearDown(){
        movieInfoRepository.deleteAll().block();
    }

    @Test
    void addMovieInfo(){
        var movieInfo=  new MovieInfo(null, "Batman Begins1",
                        2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        webTestClient.post()
                     .uri(MOVIEs_INFO_URL)
                     .bodyValue(movieInfo)
                     .exchange()
                     .expectStatus()
                     .isCreated()
                     .expectBody(MovieInfo.class)
                     .consumeWith(movieInfoEntityExchangeResult ->{
                        var savedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                        assert savedMovieInfo != null;
                        assert savedMovieInfo.getMovieInfoId() != null;
                     });

    }

    @Test
    void getAllMovieInfos(){

        webTestClient.get()
                     .uri(MOVIEs_INFO_URL)
                     .exchange()
                     .expectStatus()
                     .is2xxSuccessful()
                     .expectBodyList(MovieInfo.class)
                     .hasSize(3);
    }

    @Test
    void getMovieInfoById(){
        var movieInfoId="abc";
        webTestClient.get()
                     .uri(MOVIEs_INFO_URL+"/{id}",movieInfoId)
                     .exchange()
                     .expectStatus()
                     .is2xxSuccessful()
                     .expectBody()
                     .jsonPath("$.name").isEqualTo("Dark Knight Rises");
                    //  .expectBody(MovieInfo.class)
                    //  .consumeWith(movieInfoEntityExchangeResult ->{
                    //     var movieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    //     assertNotNull(movieInfo);
                    //  });
    }

    @Test
    void getMovieInfoById_notFound(){
        var movieInfoId="def";
        webTestClient.get()
                     .uri(MOVIEs_INFO_URL+"/{id}",movieInfoId)
                     .exchange()
                     .expectStatus()
                     .isNotFound();
    }
   
    @Test
    void getMovieInfoByYear(){
        var uri = UriComponentsBuilder.fromUriString(MOVIEs_INFO_URL)
                                      .queryParam("year", 2005)
                                      .buildAndExpand()
                                      .toUri();
        webTestClient.get()
                     .uri(uri)
                     .exchange()
                     .expectStatus()
                     .is2xxSuccessful()
                     .expectBodyList(MovieInfo.class)
                     .hasSize(1);
    }

    @Test
    void getMovieInfoByName(){
        var uri = UriComponentsBuilder.fromUriString(MOVIEs_INFO_URL)
                                      .queryParam("name", "Batman Begins")
                                      .buildAndExpand()
                                      .toUri();
        webTestClient.get()
                     .uri(uri)
                     .exchange()
                     .expectStatus()
                     .is2xxSuccessful()
                     .expectBodyList(MovieInfo.class)
                     .hasSize(1);

    }

    @Test
    void updatedMovieInfo(){
        var movieInfoId="abc";
        
        var movieInfo=  new MovieInfo(null, "Dark Knight Rises1",
                        2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        webTestClient.put()
                     .uri(MOVIEs_INFO_URL+"/{id}",movieInfoId)
                     .bodyValue(movieInfo)
                     .exchange()
                     .expectStatus()
                     .isOk()
                     .expectBody(MovieInfo.class)
                     .consumeWith(movieInfoEntityExchangeResult ->{
                        var updatedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                        assert updatedMovieInfo != null;
                        assert updatedMovieInfo.getMovieInfoId() != null;
                        assertEquals("Dark Knight Rises1", updatedMovieInfo.getName());
                     });
    }

    @Test
    void updatedMovieInfo_notFound(){
        var movieInfoId="def";
        
        var movieInfo=  new MovieInfo(null, "Dark Knight Rises1",
                        2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        webTestClient.put()
                     .uri(MOVIEs_INFO_URL+"/{id}",movieInfoId)
                     .bodyValue(movieInfo)
                     .exchange()
                     .expectStatus()
                     .isNotFound();
                    //  .isOk()
                    //  .expectBody(MovieInfo.class)
                    //  .consumeWith(movieInfoEntityExchangeResult ->{
                    //     var updatedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    //     assert updatedMovieInfo != null;
                    //     assert updatedMovieInfo.getMovieInfoId() != null;
                    //     assertEquals("Dark Knight Rises1", updatedMovieInfo.getName());
                    //  });
    }

    

    @Test
    void deleteMovieInfo(){
        var movieInfoId="abc";

        webTestClient.delete()
                     .uri(MOVIEs_INFO_URL+"/{id}",movieInfoId)
                     .exchange()
                     .expectStatus()
                     .isNoContent();
    }

}
