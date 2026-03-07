import '../css/PlaylistSort.css';
import Cookies from 'js-cookie';
import { useState, useEffect, useRef } from "react"


function PlaylistSort(){
    const [isLoggedIn, setLogIn] = useState(false);
    const [loading, setLoading] = useState(true);
    const [playlists, setPlaylists] = useState([]);
    const [selectedMethods, setSelectedMethods] = useState({});
    const [selectedAscDesc, setSelectedAscDesc] = useState({});
    const [isSorting, setIsSorting] = useState(false)
    const [sortingPlaylist, setSortingPlaylist] = useState(null);
    
    useEffect(() => {
        if (!loading && !isLoggedIn) {
            window.location.replace("/");
        }
    }, [loading, isLoggedIn]);

    useEffect(() =>{
        const login = localStorage.getItem('userId');
        if (login === "noID" || login == undefined) {
            setLogIn(false);
            setLoading(false);
        } else {
            setLogIn(true);
        

            const loadUserPlaylists = async () => {
                try {
                    const res = await fetch("https://spotifytools.onrender.com/users-playlists", {
                        credentials: "include",
                        headers: {
                            "userId": localStorage.getItem("userId")
                        }
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

    const handleSelectChange = (playlistID, value) => {
        setSelectedMethods(prev => ({
            ...prev,
            [playlistID]: value
        }));
    };

    const handleRadioChange = (playlistID, value) => {
        setSelectedAscDesc(prev => ({
            ...prev,
            [playlistID]: value
        }));
    };

    const handleSort = async (playlistID) => {
        setIsSorting(true);
        setSortingPlaylist(playlistID);
        try {
            const method = selectedMethods[playlistID];
            const order = selectedAscDesc[playlistID];
            const res = await fetch("https://spotifytools.onrender.com/sort-playlist-popularity", {
                        method: "POST",
                        credentials: "include",
                        headers: {
                            "Content-Type": "application/json",
                            "userId": localStorage.getItem("userId")
                        },
                        body: JSON.stringify({
                        playlistLink: playlistID,
                        method: method,
                        order: order
                        })
                    });
                    const data = await res.text();
                    if(data === "Sorting unsuccessful"){
                        alert("Sorting unsuccessful, make sure to use New if sorting someone else's playlist!")
                    }else if(data === "Sorting successful"){
                        alert("Sorting successful!")
                    }else{
                        alert("Unknown error, unable to sort")
                    }
                    setSortingPlaylist(null);
        } catch (e) {
            console.error("Failed to sort playlist: ", e);
        } finally {
            setIsSorting(false); 
        }
    };

    return(
        <>
            <h1 className='main-title'>Playlist Sorter</h1>
            <br/>
            <p className='info-text'>
            Sort playlists by popularity! <br/>
            Preserve: Selecting preserve will keep metadata such as the date the song was originally added. This method will take longer especially for large playlists.<br/>
            Overwrite: Selecting overwrite will sort the original playlist. It will write over metadata such as the date originally added.<br/>
            New: Selecting new will create a new playlist that is a copy but with the songs sorted.<br/>
            </p>
            <p className='note-text'>Note: Preserve and Overwrite will only work with your own playlists, use New to sort others' playlists!</p>
            <br/>
            <ol>
                {playlists
                .map((playlist) => (
                    <li className='playlist' key={playlist.playlistID}>
                        <div className='playlist-info'>
                            <img className='playlist-image' src={playlist.imageUrl}/>
                            <div className='playlist-text'>
                                < div className="truncate">Name: {playlist.playlistName}</div>
                                {playlist.owner && <div className="truncate">Owner: {playlist.owner}</div>}
                                <div className="truncate">Num tracks: {playlist.numTracks}</div>
                            </div>
                                <a
                                    className="spotify-redirect"
                                    target="_blank"
                                    rel="noopener noreferrer"
                                    href={playlist.playlistUrl}
                                    >
                                    Spotify
                                </a>
                                <br/>
                                <div className='playlist-actions'>
                                    <select
                                        className='method-select-menu' 
                                        id={`method-selection-${playlist.playlistID}`}
                                        value={selectedMethods[playlist.playlistID] || "0"}
                                        onChange={(e) => handleSelectChange(playlist.playlistID, e.target.value)}
                                        disabled={isSorting} 
                                    >
                                        <option value="0">Select a method</option>
                                        <option value="preserve">Preserve</option>
                                        <option value="overwrite">Overwrite</option>
                                        <option value="new">New</option>
                                    </select>
                                    <div className='sort-order-container'>
                                        <label>
                                            <input
                                            type="radio"
                                            name={`sort-order-${playlist.playlistID}`}
                                            checked={selectedAscDesc[playlist.playlistID] === "ascending"}
                                            onChange={(e) => handleRadioChange(playlist.playlistID, e.target.value)}
                                            value="ascending"
                                            disabled={isSorting}
                                            />
                                            Ascending
                                        </label>
                                        <label>
                                            <input
                                            type="radio"
                                            name={`sort-order-${playlist.playlistID}`}
                                            checked={selectedAscDesc[playlist.playlistID] === "descending"}
                                            onChange={(e) => handleRadioChange(playlist.playlistID, e.target.value)}
                                            value="descending"
                                            disabled={isSorting}
                                            />
                                            Descending
                                        </label>
                                    </div>
                                </div>
                                <button
                                    className='sort-button' 
                                    id={`sort-button-${playlist.playlistID}`}
                                    disabled={
                                        isSorting || 
                                        selectedMethods[playlist.playlistID] === "0" || 
                                        !selectedMethods[playlist.playlistID] ||
                                        !selectedAscDesc[playlist.playlistID]
                                    }
                                    onClick={() => handleSort(playlist.playlistID)}
                                >
                                    {sortingPlaylist === playlist.playlistID ? "Sorting..." : "Sort"}
                                </button>
                        </div>
                    </li>
                ))}
            </ol>
        </>

    )
}

export default PlaylistSort