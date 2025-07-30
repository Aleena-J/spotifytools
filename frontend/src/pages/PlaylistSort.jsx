import '../css/PlaylistSort.css';
import Cookies from 'js-cookie';
import { useState, useEffect, useRef } from "react"


function PlaylistSort(){
    const [isLoggedIn, setLogIn] = useState(false);
    const [loading, setLoading] = useState(true);
    const [playlists, setPlaylists] = useState([]);

    useEffect(() => {
        if (!loading && !isLoggedIn) {
            window.location.replace("/");
        }
    }, [loading, isLoggedIn]);

    useEffect(() =>{
        const login = Cookies.get('userId');
        if (login === "noID" || login == undefined) {
            setLogIn(false);
            setLoading(false);
        } else {
            setLogIn(true);
        

            const loadUserPlaylists = async () => {
                try {
                    const res = await fetch("http://127.0.0.1:8080/users-playlists", {
                        credentials: "include"
                    });
                    const data = await res.json();
                    setPlaylists(data);
                } catch (e) {
                    console.error("Failed to load playlists: ", e);
                } finally {
                    setLoading(false);
                }
            };

            loadUserPlaylists();
        }
    }, []);

    return(
        <>
            <h1>Playlist Sorter</h1>
            <p>
            Preserve: Selecting preserve will keep metadata such as the date the song was originally added. This method will take longer especially for large playlists.<br/>
            Overwrite: Selecting overwrite will sort the original playlist. It will write over metadata such as the date originally added.<br/>
            New: Selecting new will create a new playlist that is a copy but with the songs sorted.<br/>
            </p>
            <p>Note: Preserve and Overwrite will only work with your own playlists, use New to sort others' playlists!</p>
            <ol>
                {playlists
                .map((playlist) => (
                    <li key={playlist.playlistID}>
                        <img src={playlist.imageUrl}/>
                        <div >
                            <div >
                                <div>Name: {playlist.playlistName}</div>
                                <div>Num tracks: {playlist.numTracks}</div>
                            </div>
                        </div>
                    </li>
                ))}
            </ol>
        </>

    )
}

export default PlaylistSort