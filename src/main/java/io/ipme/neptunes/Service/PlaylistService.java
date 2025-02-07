package io.ipme.neptunes.Service;

import io.ipme.neptunes.Model.Playlist;
import io.ipme.neptunes.Model.Track;
import io.ipme.neptunes.Repository.PlaylistRepository;
import io.ipme.neptunes.Service.dto.PlaylistCreateUpdateDTO;
import io.ipme.neptunes.Service.dto.PlaylistDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PlaylistService {

    private PlaylistRepository playlistRepository;

    public PlaylistService(PlaylistRepository playlistRepository) {
        this.playlistRepository = playlistRepository;
    }

    public List<PlaylistDTO> findAll() {
        List<PlaylistDTO> playlistDtos = new ArrayList<>();
        for (Playlist playlist : playlistRepository.findAll()) {
            PlaylistDTO playlistDto = new PlaylistDTO();
            BeanUtils.copyProperties(playlist, playlistDto);
            playlistDtos.add(playlistDto);
        }
        return playlistDtos;
    }

    public PlaylistDTO findOne(Integer id) {
        PlaylistDTO playlistDto = new PlaylistDTO();
        BeanUtils.copyProperties(playlistRepository.findById(id).orElseThrow(), playlistDto);
        return playlistDto;
    }

    public PlaylistDTO save(PlaylistCreateUpdateDTO playlistCreateUpdateDTO) {
        /*Playlist creation*/
        Playlist playlist = new Playlist(playlistCreateUpdateDTO.getName(), playlistCreateUpdateDTO.getRandom());
        playlistRepository.save(playlist);
        /*DTO send back*/
        PlaylistDTO playlistDTO = new PlaylistDTO();
        BeanUtils.copyProperties(playlist, playlistDTO);
        return playlistDTO;
    }

    public void remove(Integer id) {
        // TODO : la suppression doit supprimer dans la table track_playlist
        playlistRepository.deleteById(id);
    }

    public PlaylistDTO update(Integer id, PlaylistCreateUpdateDTO playlistUpdateDTO) {
        //TODO : gestion des exceptions
        /*Playlist update*/
        Playlist playlist = playlistRepository.findById(id).orElseThrow();

        if (playlistUpdateDTO.getName() != null) playlist.setName(playlistUpdateDTO.getName());
        if (playlistUpdateDTO.getRandom() != null) playlist.setRandom(playlistUpdateDTO.getRandom());

        playlistRepository.save(playlist);
        /*DTO send back*/
        PlaylistDTO playlistDTO = new PlaylistDTO();
        BeanUtils.copyProperties(playlist, playlistDTO);
        return playlistDTO;
    }

    public PlaylistDTO addTracks(Integer id, List<Integer> tracksId) throws Exception {
        Playlist playlist = playlistRepository.findById(id).orElseThrow();

        List<Track> tracksToSave = new ArrayList<>();

        for (Integer trackId : tracksId) {
            if (!playlist.getTracks().contains(new Track(trackId))) {
                tracksToSave.add(new Track(trackId));
            }
        }
        if (tracksToSave.isEmpty()){
            throw new Exception("cette valeur existe déjà dans la playlist "+id);
        }

        playlist.getTracks().addAll(tracksToSave);
        playlistRepository.save(playlist);
        /*DTO send back*/
        PlaylistDTO playlistDTO = new PlaylistDTO();
        BeanUtils.copyProperties(playlist, playlistDTO);
        return playlistDTO;
    }

    public PlaylistDTO removeTracks(Integer id, List<Integer> tracksId) throws Exception {
        Playlist playlist = playlistRepository.findById(id).orElseThrow();

        List<Track> tracksToDelete = new ArrayList<>();

        for (Integer trackId : tracksId) {
            if (playlist.getTracks().contains(new Track(trackId))) {
                tracksToDelete.add(new Track(trackId));
            }
        }
        if (tracksToDelete.isEmpty()){
            throw new Exception("cette valeur n\'existe pas dans la playlist "+id);
        }

        playlist.getTracks().removeAll(tracksToDelete);
        playlistRepository.save(playlist);
        /*DTO send back*/
        PlaylistDTO playlistDTO = new PlaylistDTO();
        BeanUtils.copyProperties(playlist, playlistDTO);
        return playlistDTO;
    }

    public PlaylistDTO generateRandomPlaylist(Integer limit) throws Exception {

        if (limit > 50) {
            throw new Exception("une playlist aléatoire ne peut pas contenir plus de " + limit + " morceaux");
        }

        List<Integer> randomTracks = playlistRepository.getRandomTracks(limit);
        PlaylistCreateUpdateDTO playlistCreateUpdateDTO = new PlaylistCreateUpdateDTO();
        playlistCreateUpdateDTO.setRandom(true);
        playlistCreateUpdateDTO.setName("playlist-" + UUID.randomUUID().toString().substring(0, 6));

        PlaylistDTO randomPlaylistGenerated = this.save(playlistCreateUpdateDTO);

        return this.addTracks(randomPlaylistGenerated.getId(), randomTracks);
    }

}
