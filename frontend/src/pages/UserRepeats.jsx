import '../css/UserRepeats.css';
import Cookies from 'js-cookie';
import { useState, useEffect } from "react"

function UserRepeats() {
    const [isLoggedIn, setLogIn] = useState(false);
    const [loading, setLoading] = useState(true);
    const [repeatedSongs, setRepeatedSongs] = useState([])

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
                <h2>Your repeats</h2>
                
                <ul>
                    {repeatedSongs.map((song, index) => (
                        <li key={index}>{song.songName} — {song.artists} — {song.repeats} Repeats</li>
                    ))}
                </ul>
            </>
        )}
    </div>
    );
}


export default UserRepeats
