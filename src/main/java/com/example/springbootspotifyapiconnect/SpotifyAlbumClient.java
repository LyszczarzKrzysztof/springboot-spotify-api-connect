package com.example.springbootspotifyapiconnect;

import com.example.springbootspotifyapiconnect.dto.SpotifyAlbumDto;
import com.example.springbootspotifyapiconnect.entity.Track;
import com.example.springbootspotifyapiconnect.model.SpotifyAlbum;
import com.example.springbootspotifyapiconnect.repository.TrackRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

// albo @RestController albo @Controller+nad metodą @ResponseBody - inaczej nie mozna zwrocic zlozonego obiektu
@Controller
public class SpotifyAlbumClient {

    private TrackRepository trackRepository;

    //byl problem zeby zwrocic albumy z adresu "https://api.spotify.com/v1/search?q=taylor%20swift&type=track&market=US&limit=10&offset=5"
    //przy uzyciu adnotacji @Controller - ona nie ma @ResponseBody i dlatego nie mozna w niej zwracac zlozonych obiektow
    //trzeba albo dodac @ResponseBody tutaj ponizej (nad metoda) albo uzyc adnotacji @RestController bo ona ma wbudowanego @ResponseBody
    @GetMapping("/album/{authorName}")
    @ResponseBody
    public List<SpotifyAlbumDto> getAlbumsByAuthor(OAuth2Authentication details, @PathVariable String authorName){

        String jwt = ((OAuth2AuthenticationDetails)details.getDetails()).getTokenValue();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization","Bearer "+jwt);
        HttpEntity httpEntity = new HttpEntity(httpHeaders);


        ResponseEntity<SpotifyAlbum> exchange = restTemplate.exchange("https://api.spotify.com/v1/search?q="+authorName+"&type=track&market=US&limit=10&offset=5",
                HttpMethod.GET,
                httpEntity,
                SpotifyAlbum.class); // JsonNode formatuje odpowiedz do JSON w przegladarce

        List<SpotifyAlbumDto> spotifyAlbumDtos =
                exchange.getBody()
                .getTracks().getItems().stream()
                .map(item -> new SpotifyAlbumDto(item.getName(), item.getAlbum().getImages().get(0).getUrl()))
                .collect(Collectors.toList());

        return spotifyAlbumDtos;
    }

    @PostMapping("/add-track")
    public void addTrack(@RequestBody Track track){
        trackRepository.save(track);
    }
}
