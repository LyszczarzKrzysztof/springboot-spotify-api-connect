package com.example.springbootspotifyapiconnect.repository;

import com.example.springbootspotifyapiconnect.entity.Track;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackRepository extends MongoRepository<Track, String> {

}
