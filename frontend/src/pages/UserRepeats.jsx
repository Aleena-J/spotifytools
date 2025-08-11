import '../css/UserRepeats.css';
import Cookies from 'js-cookie';
import { useState, useEffect, useRef } from "react"


function SongList({ period, repeatedSongs, filterNum, input, setInput, handleSubmit, createRepeatPlaylist, selectedTime, isCreating }) {
    const songs = repeatedSongs.filter(song => song.dateType === period);
    const filteredSongs = songs.filter(song => song.repeats >= filterNum);

    if (songs.length === 0) {
        return <p className='noRepeats-text'>No repeats for this period!</p>;
    }

    return (
        <>
            <button 
                className='createPlaylist'
                onClick={() => createRepeatPlaylist(selectedTime, filteredSongs)}
                disabled={isCreating || filteredSongs.length === 0}
            >
                Create Playlist
            </button>

            <form className="repeat-limit" onSubmit={handleSubmit}>
                <label>Minimum number of repeats:   </label>
                <input 
                    type="number"
                    value={input}
                    onChange={(e) => setInput(e.target.value)}
                />
                <button className="limit-apply" type="submit">Apply</button>
            </form>

            <ol className="repeats-active">
                {filteredSongs.map(song => (
                    <li className="repeatedSong" key={song.songUrl}>
                        <img className="song-image" src={song.imageUrl} alt={song.songName} />
                        <div className="song-info">
                            <div className="song-text">
                                <div className="truncate">Title: {song.songName}</div>
                                <div className="truncate">Artist(s): {song.artists}</div>
                                <div className="truncate">Album: {song.album}</div>
                                <div>Repeats: {song.repeats}</div>
                            </div>
                            <a
                                className="spotifyRedirect"
                                target="_blank"
                                rel="noopener noreferrer"
                                href={song.songUrl}
                            >
                                Spotify
                            </a>
                        </div>
                    </li>
                ))}
            </ol>
        </>
    );
}

function UserRepeats() {
    const [isLoggedIn, setLogIn] = useState(false);
    const [loading, setLoading] = useState(true);
    const [repeatedSongs, setRepeatedSongs] = useState([]);
    const [selectedTime, setSelectedTime] = useState("today");
    const [isCreating, setIsCreating] = useState(false);
    const [filterNum, setFilterNum] = useState(0);
    const [input, setInput] = useState(0);
    const repeatedSongsRef = useRef([]);

    const timePeriods = [
        { key: "today", label: "Today" },
        { key: "week", label: "This Week" },
        { key: "month", label: "This Month" },
        { key: "lastmonth", label: "Last Month" }
    ];

    useEffect(() => {
        if (!loading && !isLoggedIn) {
            window.location.replace("/");
        }
    }, [loading, isLoggedIn]);

    useEffect(() => {
        const login = Cookies.get('userId');
        if (login === "noID" || login == undefined) {
            setLogIn(false);
            setLoading(false);
        } else {
            setLogIn(true);
        

            const loadUserRepeats = async () => {
                try {
                    const res = await fetch("http://127.0.0.1:8080/repeats", {
                        credentials: "include"
                    });
                    const data = await res.json();
                    const sortedNewData = [...data].sort((a, b) => b.repeats - a.repeats);

                    const oldData = repeatedSongsRef.current;
                    const isDifferent =
                        oldData.length !== sortedNewData.length ||
                        oldData.some((song, i) =>
                            JSON.stringify(song) !== JSON.stringify(sortedNewData[i])
                        );

                    if (isDifferent) {
                        repeatedSongsRef.current = sortedNewData;
                        setRepeatedSongs(sortedNewData);
                    }
                } catch (e) {
                    console.error("Failed to load repeats: ", e);
                } finally {
                    setLoading(false);
                }
            };

            loadUserRepeats();

            const checkInterval = setInterval(loadUserRepeats, 180000);
            return () => clearInterval(checkInterval);
        }
    }, []);

    const createRepeatPlaylist = async (period, songs) => {
        setIsCreating(true);
        const res = await fetch("http://127.0.0.1:8080/create-repeat-playlist", {
            method: "POST",
            credentials: "include",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                period: period,
                songs: songs
                })
        });
        const data = await res.text();
        if(data === "Creation unsuccessful"){
            alert("Creation unsuccessful");
        }else if(data === "Creation successful"){
            alert("Creation successful!");
        }else{
            alert("Unknown error, unable to create");
        }
        setIsCreating(false);
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        setFilterNum(input);
    };

    

    return (
        <div className="repeat-body">
            {loading ? (
                <h1>Loading...</h1>
            ) : (
                <>
                    <h1 className='repeatHeading'>Your repeats</h1>

                    {timePeriods.map(period => (
                            <button
                                key={period.key}
                                className={`tabs${selectedTime === period.key ? "-activeTab" : ""}`}
                                onClick={() => setSelectedTime(period.key)}
                            >
                                {period.label}
                            </button>
                    ))}

                    <SongList 
                        period={selectedTime}
                        repeatedSongs={repeatedSongs}
                        filterNum={filterNum}
                        input={input}
                        setInput={setInput}
                        handleSubmit={handleSubmit}
                        createRepeatPlaylist={createRepeatPlaylist}
                        selectedTime={selectedTime}
                        isCreating={isCreating}
                    />
                </>
            )}
        </div>
    );
}

export default UserRepeats;
