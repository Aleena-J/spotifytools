import '../css/UserRepeats.css';
import Cookies from 'js-cookie';
import { useState, useEffect } from "react"

function UserRepeats() {
    const [isLoggedIn, setLogIn] = useState(false);
    const [loading, setLoading] = useState(true);
    const [repeatedSongs, setRepeatedSongs] = useState([])
    const [selectedTime, setSelectedTime] = useState("today")

    useEffect(() => {
        if (!loading && !isLoggedIn) {
            window.location.replace("/");
        }
    }, [loading, isLoggedIn]);

        
    useEffect(() => {
        const login = Cookies.get('userId');
        if(login === "noID" || login == undefined){
            setLogIn(false)
            setLoading(false);
        }else{
            setLogIn(true)
            setLoading(false);

            const loadUserRepeats = async () => {
                setLoading(true)
                try{
                    const res = await fetch("http://127.0.0.1:8080/repeats", {
                        credentials: "include"
                    });
                    const data = await res.json();
                    console.log(data);
                    setRepeatedSongs(data.sort((a, b) => b.repeats - a.repeats));

                }finally{
                    setLoading(false)
                }
            }
            loadUserRepeats()
        }
    }, []);

    return (
        <div className="repeat-body">
        {loading ? (
            <h1>Loading...</h1>
        ) : (
            <>
                <h1 className='repeatHeading'>Your repeats</h1>
                

                <button className={`tabs${selectedTime === "today" ? "-activeTab" : ""}`} onClick={() => setSelectedTime("today")}>Today</button>
                <button className={`tabs${selectedTime === "week" ? "-activeTab" : ""}`} onClick={() => setSelectedTime("week")}>This Week</button>
                <button className={`tabs${selectedTime === "month" ? "-activeTab" : ""}`} onClick={() => setSelectedTime("month")}>This Month</button>
                <button className={`tabs${selectedTime === "lastmonth" ? "-activeTab" : ""}`} onClick={() => setSelectedTime("lastmonth")}>Last Month</button>



                <ol className={`repeats${selectedTime === "today" ? "-active" : "" }`} >
                    {repeatedSongs
                    .filter((song) => song.dateType === "today")
                    .map((song, index) => (
                        <li className="repeatedSong" key={index}>
                            <img className="song-image" src={song.imageUrl}/>
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

                <ol className={`repeats${selectedTime === "week" ? "-active" : "" }`}>
                    {repeatedSongs
                    .filter((song) => song.dateType === "week")
                    .map((song, index) => (
                        <li className="repeatedSong" key={index}>
                            <img className="song-image" src={song.imageUrl}/>
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

                <ol className={`repeats${selectedTime === "month" ? "-active" : "" }`}>
                    {repeatedSongs
                    .filter((song) => song.dateType === "month")
                    .map((song, index) => (
                        <li className="repeatedSong" key={index}>
                            <img className="song-image" src={song.imageUrl}/>
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

                <ol className={`repeats${selectedTime === "lastmonth" ? "-active" : "" }`}>
                    {repeatedSongs
                    .filter((song) => song.dateType === "lastmonth")
                    .map((song, index) => (
                        <li className="repeatedSong" key={index}>
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
        )}
    </div>
    );
}


export default UserRepeats
